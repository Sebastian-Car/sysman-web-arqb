package com.sysman.kernel.api.clientwso2.dbs;

import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.converters.JsonConverter;
import com.sysman.kernel.api.commons.util.enums.JsonEnum;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton que centraliza la migracion local/WSO2 por CODIGO (el
 * mismo idioma que ClientConfig/UrlServiceUtil ya usan en este
 * paquete). Punto unico de entrada para:
 *
 * - UrlServiceUtil.getUrlServiceByUrlByEnumID: decide si un CODIGO
 *   debe resolverse local (sin ir nunca a buscar su UrlBean en WSO2).
 * - RequestManager: ejecuta localmente cuando la URL recibida trae el
 *   PREFIJO_LOCAL en vez de una URL real.
 *
 * Se activa definiendo la system property "sysman.dbs.dir" con la
 * ruta a un directorio externo del filesystem donde viven los .dbs
 * migrados. Si no esta definida, esLocal(...) siempre devuelve false
 * y todo sigue yendo por WSO2 como hasta ahora.
 */
public class DbsDispatcherConfig {

    private static final Logger LOG = Logger.getLogger(DbsDispatcherConfig.class.getName());

    /** Prefijo artificial que jamas puede colisionar con una URL real de WSO2. */
    public static final String PREFIJO_LOCAL = "dbs-local://";

    public static final String PROP_DIR_EXTERNO = "sysman.dbs.dir";

    private static final DbsDispatcherConfig instance = new DbsDispatcherConfig();

    private final boolean activo;
    private final DbsRegistryHolder registry;
    private final DbsSwitch dbsSwitch;
    private final DbsQueryExecutor executor;

    private DbsDispatcherConfig() {
        String dir = System.getProperty(PROP_DIR_EXTERNO);
        if (dir == null || dir.trim().isEmpty()) {
            LOG.info("system property '" + PROP_DIR_EXTERNO
                + "' no definida: el dispatcher local queda inactivo, todo va por WSO2.");
            activo = false;
            registry = null;
            dbsSwitch = null;
            executor = null;
            return;
        }

        Path directorio = Paths.get(dir.trim());
        registry = new DbsRegistryHolder(directorio);
        registry.iniciarVigilancia();

        Path archivoSwitch = directorio.resolve("dbs-switch.properties");
        dbsSwitch = DbsSwitch.cargarDesdeArchivo(archivoSwitch);

        executor = new DbsQueryExecutor(registry, new DataSourceResolver());
        activo = true;
        LOG.info("Dispatcher local activo, leyendo .dbs desde " + directorio);
    }

    public static DbsDispatcherConfig getInstance() {
        return instance;
    }

    /** Detiene la vigilancia del directorio; llamar al apagar el contexto de la aplicacion. */
    public void detener() {
        if (activo) {
            registry.detener();
        }
    }

    public boolean esLocal(String codigo) {
        return activo && codigo != null && dbsSwitch.esLocal(codigo);
    }

    /** Ejecuta local un GET/POST/PUT/DELETE de un solo registro, devolviendo el JSON crudo. */
    public String ejecutarJson(String codigo, Map<String, Object> params) throws SystemException {
        try {
            DbsResource recurso = obtenerRecurso(codigo);
            return executor.execute(recurso, params);
        }
        catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /**
     * Ejecuta local un recurso de tipo grilla/combo (gridKey),
     * trayendo filas + total en la misma llamada, sin necesitar una
     * segunda consulta por urlConteo.
     */
    public ListaConTotal ejecutarListaConConteo(String codigo, Map<String, Object> params)
                    throws SystemException {
        try {
            DbsResource recurso = obtenerRecurso(codigo);
            String jsonFilas = executor.execute(recurso, params);
            List<Parameter> filas = JsonConverter.toRegistroList(jsonFilas, JsonEnum.DEFAULT);

            int total = recurso.getQueryIdConteo() != null
                ? executor.ejecutarConteo(recurso, params)
                : filas.size();

            return new ListaConTotal(filas, total);
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /** Ejecuta solo el conteo de un recurso local (usado cuando se necesita el total antes de pedir la lista). */
    public int ejecutarConteo(String codigo, Map<String, Object> params) throws SystemException {
        try {
            DbsResource recurso = obtenerRecurso(codigo);
            if (recurso.getQueryIdConteo() == null) {
                throw new IllegalStateException(
                    "El recurso '" + codigo + "' no declara <call-query-conteo> en su .dbs.");
            }
            return executor.ejecutarConteo(recurso, params);
        }
        catch (SystemException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    private DbsResource obtenerRecurso(String codigo) throws SystemException {
        DbsResource recurso = registry.getResource(codigo);
        if (recurso == null) {
            throw new SystemException("El CODIGO '" + codigo
                + "' esta marcado como local en dbs-switch.properties, pero no hay ningun "
                + ".dbs que declare <resource codigo=\"" + codigo + "\">.");
        }
        return recurso;
    }
}
