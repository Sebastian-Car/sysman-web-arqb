/*-
 * ResuelveConsultas.java
 *
 * 1.0
 * 
 * 14 feb. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.negocio;

import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.negocio.enums.ResuelveConsultaEnum;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.persistencia.ConectorPool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Patr&oacute;n comando g&eacute;neral. Recibidor de Servicios para
 * el comando ejecutado.
 * 
 * @version 1.0, 14 feb. 2019
 * @author jrodrigueza
 *
 */
public class ResuelveConsultas {

    /**
     * Objeto para el registro de eventos.
     */
    protected final Log logger = LogFactory.getLog(this.getClass());
    /**
     * C&oacute;digo de la compa&ntilde;&iacute;a.
     */
    private String compania;
    /**
     * C&oacute;digo que identifica el reporte.
     */
    private String idReporte;
    /**
     * C&oacute;digo que identifica la consulta.
     */
    private String idConsulta;
    /**
     * Indica si la consulta tiene par&aacute;metros.
     */
    private boolean tieneParametrosConsulta;

    /**
     * Resuelve la consulta y trae el resultado de ejecutar la
     * consulta.
     * 
     * @param compania
     * @param idReporte
     * c&oacute;digo de la consulta
     * @param parametros
     * filtros de consutla
     * @return datos resultado de resolver la consulta
     * @throws NegocioExcepcion
     */
    public List<Map<String, Object>> traerDatos(String compania,
        String idReporte, Map<String, Object> parametros)
                    throws NegocioExcepcion {
        this.compania = compania;
        this.idReporte = idReporte;
        List<Map<String, Object>> resultado = null;
        String consultaFinal = armarConsulta(parametros);
        resultado = resolverConsulta(ConectorPool.ESQUEMA_SYSMAN, consultaFinal,
                        parametros);
        return resultado;
    }

    /**
     * Verifica que se hayan ingresado los par&aacute;metros definidos
     * en la consulta y reporte del generador de reportes.
     * Tambi&eacute;n elimina los par&aacute;metros que no solicita la
     * consulta final.
     * 
     * @param filtros
     * que pide la consulta final
     * @throws NegocioExcepcion
     */
    private void rectificarParametros(Map<String, Object> filtros)
                    throws NegocioExcepcion {
        /*
         * Trae los parametros configurados para la consulta y el
         * reporte
         */
        List<Registro> parametros = new ArrayList<>();
        // parametros de consulta
        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.CODIGO.getName(), idConsulta);
        try {
            parametros = ejecutarSelectMultiRegistro(
                            ResuelveConsultaEnum.DSS_1670002.getValue(),
                            params);
            tieneParametrosConsulta = parametros != null
                && !parametros.isEmpty();
        }
        catch (SystemException e) {
            String mensaje = "Error al consultar los parámetros de la consulta: "
                + idConsulta + ".";
            NegocioExcepcion error = new NegocioExcepcion(mensaje);
            error.initCause(new Exception(mensaje));
            throw error;
        }
        // parametros de reporte
        Map<String, Object> paramsRep = new HashMap<>();
        paramsRep.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        paramsRep.put(GeneralParameterEnum.CODIGO.getName(), idReporte);
        try {
            List<Registro> parametrosReporte = ejecutarSelectMultiRegistro(
                            ResuelveConsultaEnum.DSS_1058004.getValue(),
                            paramsRep);
            if (parametros != null) {
                parametros.addAll(parametrosReporte);
            }
        }
        catch (SystemException e) {
            String mensaje = "Error al consultar los parámetros del reporte: "
                + idReporte + ".";
            NegocioExcepcion error = new NegocioExcepcion(mensaje);
            error.initCause(new Exception(mensaje));
            throw error;
        }

        /*
         * Valida que los parametros configurados en el reporteador
         * hayan sido ingresados por el usuario
         */
        List<String> etiquetas = new ArrayList<>();
        for (Registro registro : parametros) {
            Map<String, Object> campos = registro.getCampos();
            String etiqueta = SysmanFunciones
                            .toString(campos.get("ETIQUETA_PARAMETRO"));
            etiqueta = etiqueta.toUpperCase();
            etiquetas.add(etiqueta);
            if (!filtros.containsKey(etiqueta)) {
                String mensaje = "Se esperaba recibir el parámetro " + etiqueta
                    + ", necesario para resolver la consulta.";
                NegocioExcepcion error = new NegocioExcepcion(mensaje);
                error.initCause(new Exception(mensaje));
                throw error;
            }
        }
        // Eliminacion de parametros no declarados en la consulta
        for (Map.Entry<String, Object> entry : filtros.entrySet()) {
            String clave = entry.getKey();
            clave = clave.toUpperCase();
            if (!etiquetas.contains(clave)) {
                filtros.remove(clave);
            }
        }
    }

    /**
     * Arma la consulta resultante seg&uacute;n la condici&oacute;n
     * definida en el generador de reportes.
     * 
     * @param parametros
     * filtros que pide la consulta
     * 
     * @return consulta SQL v&aacute;lida
     * @throws NegocioExcepcion
     */
    private String armarConsulta(Map<String, Object> parametros)
                    throws NegocioExcepcion {
        Map<String, Object> campos = null;

        Map<String, Object> parametrosReporte = new TreeMap<>();
        parametrosReporte.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosReporte.put("REPORTE", idReporte);
        try {
            campos = ejecutarSelectConRegistroUnico(
                            ResuelveConsultaEnum.DSS_1055006.getValue(),
                            parametrosReporte);
        }
        catch (SystemException e) {
            String mensaje = "Error al recuperar los datos del reporte. Causa: "
                + e.getMessage();
            NegocioExcepcion error = new NegocioExcepcion(mensaje);
            error.initCause(new Exception(mensaje));
            throw error;
        }
        String filtro = SysmanFunciones.toString(campos.get("CONDICION"));
        this.idConsulta = SysmanFunciones
                        .toString(campos.get("CODIGO_CONSULTA"));

        Map<String, Object> parametrosConsulta = new TreeMap<>();
        parametrosConsulta.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosConsulta.put(GeneralParameterEnum.CODIGO.getName(),
                        idConsulta);
        try {
            campos = ejecutarSelectConRegistroUnico(
                            ResuelveConsultaEnum.DSS_1054003.getValue(),
                            parametrosConsulta);
        }
        catch (SystemException e) {
            String mensaje = "Error al recuperar la consulta SQL base. Causa: "
                + e.getMessage();
            NegocioExcepcion error = new NegocioExcepcion(mensaje);
            error.initCause(new Exception(mensaje));
            throw error;
        }

        rectificarParametros(parametros);

        String consultaBase = SysmanFunciones.toString(campos.get("SQL"));
        consultaBase = consultaBase.toUpperCase();

        if (filtro != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(consultaBase);
            sb.append((char) 32);
            sb.append(tieneParametrosConsulta ? "AND" : "WHERE");
            sb.append((char) 32);
            sb.append(filtro);
            return sb.toString().toUpperCase();
        }
        else {
            return consultaBase;
        }
    }

    /**
     * Ejecuta un servicio DSS que corresponde a una consulta que
     * devuelve un &uacute;nico registro.
     * 
     * @param idServicio
     * identificador del servicio
     * @param params
     * parametros para resolver la consulta
     * @return mapa de datos con los campos devueltos por la consulta
     * @throws SystemException
     */
    private Map<String, Object> ejecutarSelectConRegistroUnico(
        String idServicio,
        Map<String, Object> params) throws SystemException {
        UrlServiceUtil urlService = UrlServiceUtil.getInstance();
        UrlBean urlBean = urlService.getUrlServiceByUrlByEnumID(idServicio);
        RequestManager requestManager = new RequestManager();
        String url = urlBean.getUrl();
        Parameter parameter = requestManager.get(url, params);
        return parameter.getFields();
    }

    /**
     * Realiza el reemplazo de parametros en la consulta y trae el
     * resultado como una lista de mapas de datos.
     * 
     * @param conexion
     * conexi&oacute;n a la base de datos
     * @param consulta
     * sentencia SQL, que puede contener variables de enlace
     * @param parametros
     * filtros de la consulta
     * @return resultado de la consulta
     * @throws NegocioExcepcion
     * @throws NamingException
     * en caso de que se presenten problemas al identificar el
     * datasource que apunta a la base de datos
     * @throws SQLException
     * en caso de que se presenten problemas al establecer
     * conexi&oacute;n a la base de datos o ejecutar la sentencia SQL
     * @throws SystemException
     */
    private List<Map<String, Object>> resolverConsulta(String conexion,
        String consulta, Map<String, Object> parametros)
                    throws NegocioExcepcion {
        List<Map<String, Object>> resultado = new ArrayList<>();
        ConectorPool conectorPool = new ConectorPool();
        try {
            conectorPool.conectar(conexion);
        }
        catch (NamingException | SQLException e) {
            String mensaje = "Error al establecer la conexión a la base de datos "
                + conexion + ". Causa: " + e.getMessage();
            NegocioExcepcion error = new NegocioExcepcion(mensaje);
            error.initCause(new Exception(mensaje));
            throw error;
        }
        Connection con = conectorPool.getConection();
        String sqlParametrosIndexados = reemplazarParametrosNombradosPorIndexados(
                        consulta, parametros);
        try (CallableStatement statement = con
                        .prepareCall(sqlParametrosIndexados)) {
            prepararSentenciaSQL(statement, consulta, parametros);
            resultado = traerListado(statement);
        }
        catch (SQLException e) {
            String mensaje = "Error al momento de preparar el llamado a la sentencia SQL. Causa: "
                + e.getMessage();
            NegocioExcepcion error = new NegocioExcepcion(mensaje);
            error.initCause(new Exception(mensaje));
            throw error;
        }
        finally {
            try {
                con.close();
            }
            catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return resultado;
    }

    /**
     * Reemplazo de cada par&aacute;metro nombrado existente en la
     * sentencia SQL por un par&aacute;metro indexado representado por
     * el marcador de posición '?'.
     * 
     * @param consulta
     * sentencia SQL
     * @param parametros
     * mapa con los par&aacute;metros que recibe la consulta
     * @return consulta con parametros indexados
     */
    private String reemplazarParametrosNombradosPorIndexados(String consulta,
        Map<String, Object> parametros) {
        String sqlFinal = consulta;
        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            String clave = entry.getKey();
            clave = clave.toUpperCase();
            String namedParameter = ":" + clave;
            sqlFinal = sqlFinal.replace(namedParameter, "?");
        }
        return sqlFinal;
    }

    /**
     * Establece los filtros que recibe la consulta seg&uacute;n el
     * &iacute;ndice que tenga asociado el par&Aacute;metro.
     * 
     * @param statement
     * objeto <code>Statement</code>
     * @param consulta
     * sentencia SQL
     * @param parametros
     * filtros para resolver la consulta
     * @throws NegocioExcepcion
     */
    private void prepararSentenciaSQL(CallableStatement statement,
        String consulta, Map<String, Object> parametros)
                    throws NegocioExcepcion {
        // Sorted Map
        Map<Integer, String> parametrosOrdenados = new TreeMap<>();
        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            String clave = entry.getKey();
            clave = clave.toUpperCase();
            String namedParameter = ":" + clave;
            int posicion = consulta.indexOf(namedParameter);
            parametrosOrdenados.put(posicion, clave);
        }

        try {
            int i = 1;
            for (Map.Entry<Integer, String> entry : parametrosOrdenados
                            .entrySet()) {
                String nombreParametro = entry.getValue();
                nombreParametro = nombreParametro.toUpperCase();
                Object valor = parametros.get(nombreParametro);
                statement.setObject(i, valor);
                i++;
            }
        }
        catch (SQLException e) {
            String mensaje = "Error al configurar los filtros de la consulta en la sentencia. Causa: "
                + e.getMessage();
            NegocioExcepcion error = new NegocioExcepcion(mensaje);
            error.initCause(new Exception(mensaje));
            throw error;
        }
    }

    /**
     * Ejecuta un servicio DSS que corresponde a una consulta que
     * devuelve multiples registros.
     * 
     * @param idServicio
     * identificador del servicio
     * @param params
     * parametros para resolver la consulta
     * @return lista de registros devueltos por la consulta
     * @throws SystemException
     * en caso de que se presenten problemas al ejecutar el servicio
     * DSS
     */
    private List<Registro> ejecutarSelectMultiRegistro(String idServicio,
        Map<String, Object> params) throws SystemException {
        UrlServiceUtil urlservice = UrlServiceUtil.getInstance();
        String url = urlservice.getUrlServiceByUrlByEnumID(idServicio).getUrl();
        RequestManager requestManager = new RequestManager();
        List<Parameter> parameters = requestManager.getList(url, params);
        return RegistroConverter.toListRegistro(parameters);
    }

    /**
     * Trae el conjunto de datos devuelto por la consulta ejecutada.
     * 
     * @param statement
     * objeto que representa la sentencia SQL precompilada
     * @return listado de datos devueltos por la consulta
     * @throws NegocioExcepcion
     * @throws SQLException
     * en caso de que se presenten problemas al ejecutar la consulta
     */
    private List<Map<String, Object>> traerListado(
        PreparedStatement statement) throws NegocioExcepcion {
        List<Map<String, Object>> resultado = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int numeroColumnas = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> registro = new HashMap<>(
                                numeroColumnas);
                for (int i = 1; i <= numeroColumnas; i++) {
                    Object object = rs.getObject(i);
                    if (SysmanFunciones.esFecha(object)) {
                        Timestamp timestamp = rs.getTimestamp(i);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timestamp.getTime());
                        object = SysmanFunciones
                                        .convertirAIso8601(calendar.getTime());
                    }
                    registro.put(metaData.getColumnName(i), object);
                }
                resultado.add(registro);
            }
        }
        catch (SQLException e) {
            String mensaje = "Error en la base de datos al ejecutar la consulta. Causa: "
                + e.getMessage();
            NegocioExcepcion error = new NegocioExcepcion(mensaje);
            error.initCause(new Exception(mensaje));
            throw error;
        }
        return resultado;
    }

}
