package com.sysman.kernel.api.clientwso2.dbs;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ejecuta localmente (sin WSO2 DSS) la misma consulta declarada en un
 * .dbs, y devuelve el resultado en el mismo contrato JSON que ya
 * espera el resto de la aplicacion:
 *
 * {"elementos":{"elemento":[ {campo: valor, ...}, ... ]}}
 *
 * Esto es lo unico que le importa a JsonConverter en el proyecto
 * original: no hace falta reproducir el motor de plantillas de WSO2.
 */
public class DbsQueryExecutor {

    private static final Pattern PARAM_PATTERN = Pattern.compile(":([A-Za-z_][A-Za-z0-9_]*)");

    private final DbsRegistryProvider registry;
    private final DataSourceResolver dataSourceResolver;

    public DbsQueryExecutor(DbsRegistryProvider registry, DataSourceResolver dataSourceResolver) {
        this.registry = registry;
        this.dataSourceResolver = dataSourceResolver;
    }

    public String execute(DbsResource resource, Map<String, Object> params) throws Exception {
        return ejecutarQueryComoJson(resource.getQueryId(), params);
    }

    /**
     * Ejecuta la query de conteo asociada al recurso (resource.getQueryIdConteo())
     * y devuelve el TOTAL como int, sin pasar por JSON ni por una
     * segunda llamada de red: es una sola consulta SQL mas, en el
     * mismo proceso.
     *
     * El SQL de esa query debe devolver una unica fila con una unica
     * columna numerica (tipicamente "SELECT COUNT(*) ... "). Si el
     * recurso no declaro queryIdConteo, lanza IllegalStateException:
     * quien llama debe revisar antes con resource.getQueryIdConteo() != null.
     */
    public int ejecutarConteo(DbsResource resource, Map<String, Object> params) throws Exception {
        if (resource.getQueryIdConteo() == null) {
            throw new IllegalStateException(
                "El recurso '" + resource.getCodigo() + "' no declara <call-query-conteo>.");
        }
        DbsQuery query = registry.getQuery(resource.getQueryIdConteo());
        if (query == null) {
            throw new IllegalStateException(
                "No se encontro la query de conteo '" + resource.getQueryIdConteo()
                    + "' para el recurso " + resource.getCodigo());
        }

        try (ConexionYSentencia cys = prepararSentencia(query, params)) {
            try (ResultSet rs = cys.ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    private String ejecutarQueryComoJson(String queryId, Map<String, Object> params) throws Exception {
        DbsQuery query = registry.getQuery(queryId);
        if (query == null) {
            throw new IllegalStateException("No se encontro la query '" + queryId + "'");
        }

        try (ConexionYSentencia cys = prepararSentencia(query, params)) {
            boolean esSelect = query.getSql().trim().toUpperCase().startsWith("SELECT");
            if (esSelect) {
                try (ResultSet rs = cys.ps.executeQuery()) {
                    return resultSetAJson(rs);
                }
            }
            else {
                int filas = cys.ps.executeUpdate();
                return totalAJson(filas);
            }
        }
    }

    /** Agrupa Connection + PreparedStatement ya con los parametros bindeados, cerrando ambos juntos. */
    private ConexionYSentencia prepararSentencia(DbsQuery query, Map<String, Object> params) throws Exception {
        // Traduce ":NOMBRE" -> "?" preservando el orden de aparicion
        List<String> nombresEnOrden = new ArrayList<>();
        Matcher m = PARAM_PATTERN.matcher(query.getSql());
        StringBuffer jdbcSql = new StringBuffer();
        while (m.find()) {
            nombresEnOrden.add(m.group(1));
            m.appendReplacement(jdbcSql, "?");
        }
        m.appendTail(jdbcSql);

        DataSource ds = dataSourceResolver.resolve(query.getDataSourceConfigId());
        Connection conn = ds.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(jdbcSql.toString());
            for (int i = 0; i < nombresEnOrden.size(); i++) {
                String nombre = nombresEnOrden.get(i);
                Object valor = params == null ? null : params.get(nombre);
                bindParam(ps, i + 1, valor);
            }
            return new ConexionYSentencia(conn, ps);
        }
        catch (Exception e) {
            conn.close();
            throw e;
        }
    }

    private static final class ConexionYSentencia implements AutoCloseable {
        final Connection conn;
        final PreparedStatement ps;

        ConexionYSentencia(Connection conn, PreparedStatement ps) {
            this.conn = conn;
            this.ps = ps;
        }

        @Override
        public void close() throws Exception {
            try {
                ps.close();
            }
            finally {
                conn.close();
            }
        }
    }

    private void bindParam(PreparedStatement ps, int index, Object valor) throws Exception {
        if (valor == null) {
            ps.setNull(index, Types.VARCHAR);
        }
        else if (valor instanceof Integer) {
            ps.setInt(index, (Integer) valor);
        }
        else if (valor instanceof Long) {
            ps.setLong(index, (Long) valor);
        }
        else if (valor instanceof Double || valor instanceof Float) {
            ps.setDouble(index, ((Number) valor).doubleValue());
        }
        else if (valor instanceof java.util.Date) {
            ps.setTimestamp(index, new java.sql.Timestamp(((java.util.Date) valor).getTime()));
        }
        else {
            ps.setString(index, String.valueOf(valor));
        }
    }

    private String resultSetAJson(ResultSet rs) throws Exception {
        ResultSetMetaData meta = rs.getMetaData();
        int columnas = meta.getColumnCount();

        JsonArrayBuilder elementos = Json.createArrayBuilder();
        while (rs.next()) {
            JsonObjectBuilder fila = Json.createObjectBuilder();
            for (int c = 1; c <= columnas; c++) {
                String nombreColumna = meta.getColumnLabel(c);
                Object valor = rs.getObject(c);
                agregarCampo(fila, nombreColumna, valor);
            }
            elementos.add(fila);
        }

        return Json.createObjectBuilder()
            .add("elementos", Json.createObjectBuilder().add("elemento", elementos))
            .build()
            .toString();
    }

    private String totalAJson(int total) {
        return Json.createObjectBuilder()
            .add("elementos", Json.createObjectBuilder().add("elemento",
                Json.createArrayBuilder().add(Json.createObjectBuilder().add("TOTAL", total))))
            .build()
            .toString();
    }

    private void agregarCampo(JsonObjectBuilder builder, String nombre, Object valor) {
        if (valor == null) {
            builder.addNull(nombre);
        }
        else if (valor instanceof Integer) {
            builder.add(nombre, (Integer) valor);
        }
        else if (valor instanceof Long) {
            builder.add(nombre, (Long) valor);
        }
        else if (valor instanceof Double || valor instanceof Float
            || valor instanceof java.math.BigDecimal) {
            builder.add(nombre, ((Number) valor).doubleValue());
        }
        else {
            builder.add(nombre, String.valueOf(valor));
        }
    }
}
