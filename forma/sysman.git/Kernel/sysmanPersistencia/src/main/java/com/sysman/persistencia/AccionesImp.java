/*-
 * Acciones.java
 *
 * 1.0
 * 
 * 24/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.persistencia;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.sqlserver.UtilitarioMsSqlServer;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Acciones ejecutadas a la base de datos.
 * 
 * @version 1.0, 24/03/2017
 * @author cmanrique
 * @author jrodrigueza
 *
 */
public class AccionesImp {

    private static final Log logger = LogFactory.getLog(AccionesImp.class);
    /**
     * Identificador para bases de datos Microsoft SQL Server.
     */
    public static final String MICROSOFT = "MICROSOFT";
    /**
     * Identificador para bases de datos Oracle.
     */
    public static final String ORACLE = "ORACLE";

    /**
     * Constructor privado para especificar que es una clase
     * utilitaria.
     */
    private AccionesImp() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Ejecucion de procedimientos en la base de datos.
     * 
     * @param nombreConexion
     * @param procedimiento
     * @param parametros
     * @throws SystemException
     * @throws NamingException
     * @throws SQLException
     */
    public static void ejecutarProcedimiento(String nombreConexion,
        String procedimiento, String parametros)
                    throws SystemException {
        PreparedStatement cstmt = null;
        ConectorPool cp = null;
        String sql = null;
        try {
            try {
                cp = new ConectorPool();
                cp.conectar(nombreConexion);
                String tipoBaseDeDatos = traerNombreProductoBD(
                                cp.getConection());
                if (MICROSOFT.equals(tipoBaseDeDatos)) {
                    sql = UtilitarioMsSqlServer.traducirParametrosATransactSQL(
                                    procedimiento, parametros,
                                    cp.getConection());
                    List<Object> objects = capturarFechas(sql);

                    if (objects.isEmpty()) {
                        logger.info(sql);
                        cstmt = cp.getConection().prepareStatement(sql);
                        cstmt.execute();
                    }
                    else {
                        sql = (String) objects.get(0);
                        cstmt = cp.getConection().prepareStatement(sql);
                        @SuppressWarnings("unchecked")
                        List<String> fechasCadena = (List<String>) objects
                                        .get(1);
                        Timestamp[] datetimeObjects = prepararFechas(
                                        fechasCadena);
                        int contador = 1;
                        // Asignar fechas a parametros ?
                        for (Timestamp timestamp : datetimeObjects) {
                            cstmt.setTimestamp(contador, timestamp);
                            contador++;
                        }
                        logger.info(sql);
                        cstmt.execute();
                    }
                }
                else if (ORACLE.equals(tipoBaseDeDatos)) {
                    sql = "BEGIN " + procedimiento + "(" + parametros
                        + "); END; ";
                    logger.info(sql);
                    cstmt = cp.getConection().prepareCall(sql);
                    cstmt.executeUpdate(sql);
                }
                else {
                    throw new SystemException(tipoBaseDeDatos);
                }
            }
            finally {
                if (cstmt != null) {
                    cstmt.close();
                }
                if (cp != null) {
                    cp.getConection().close();
                }
            }
        }
        catch (NamingException | SQLException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /**
     * Ejecucion de funciones en la base de datos.
     * 
     * @param nombreConexion
     * @param funcion
     * @param parametros
     * @param sqlType
     * @return
     * @throws SystemException
     */
    public static Object ejecutarFuncion(String nombreConexion, String funcion,
        String parametros, int sqlType)
                    throws SystemException {
        ConectorPool cp = null;
        Object rta = null;
        CallableStatement cstmt = null;
        String sql = null;
        boolean retornaBooleano = false;

        try {
            try {
                cp = new ConectorPool();
                cp.conectar(nombreConexion);
                String tipoBaseDeDatos = traerNombreProductoBD(
                                cp.getConection());
                if (MICROSOFT.equals(tipoBaseDeDatos)) {
                    sql = UtilitarioMsSqlServer.traducirParametrosATransactSQL(
                                    funcion, parametros, cp.getConection());
                    List<Object> objects = capturarFechas(sql);

                    retornaBooleano = sqlType == Types.TINYINT;

                    // Funciones o procedimientos sin parametros de
                    // tipo fecha
                    if (objects.isEmpty()) {
                        cstmt = cp.getConection().prepareCall(sql);
                        cstmt.registerOutParameter(1,
                                        sqlType == Types.TINYINT ? Types.NUMERIC
                                            : sqlType);
                    }
                    // Funciones o procedimientos con parametros de
                    // tipo fecha
                    else {
                        sql = (String) objects.get(0);

                        cstmt = cp.getConection().prepareCall(sql);

                        @SuppressWarnings("unchecked")
                        List<String> fechasCadena = (List<String>) objects
                                        .get(1);

                        Timestamp[] datetimeObjects = prepararFechas(
                                        fechasCadena);

                        boolean esFuncion = sql.replaceAll("\\s+", "")
                                        .startsWith("{?=call");

                        int contador = esFuncion ? 2 : 1;

                        // Asignar fechas a parametros ? de sentencia
                        for (Timestamp timestamp : datetimeObjects) {
                            cstmt.setTimestamp(contador, timestamp);
                            contador++;
                        }

                        cstmt.registerOutParameter(esFuncion ? 1 : contador,
                                        sqlType);

                        logger.info(sql);
                        cstmt.execute();

                        rta = cstmt.getObject(esFuncion ? 1 : contador);

                        if (retornaBooleano
                            && MICROSOFT.equals(tipoBaseDeDatos)) {
                            String aux = rta.toString();
                            byte b = (byte) (aux.equals("0") ? 0 : 1);
                            rta = b;
                        }

                        return rta;
                    }
                    logger.info(sql);
                    cstmt.execute();
                }
                else if (ORACLE.equals(tipoBaseDeDatos)) {
                    sql = ConstatesPersistencia.ACME_LLAMADA + funcion + " ("
                        + parametros + ")}";
                    cstmt = cp.getConection().prepareCall(sql);
                    cstmt.registerOutParameter(1, sqlType);
                    logger.info(sql);
                    cstmt.executeQuery();
                }
                else {
                    throw new SystemException(tipoBaseDeDatos);
                }
                rta = cstmt.getObject(1);
                if (retornaBooleano && MICROSOFT.equals(tipoBaseDeDatos)) {
                    int aux = ((BigDecimal) rta).toBigInteger().intValue();
                    byte b = (byte) (aux != 0 ? 1 : 0);
                    rta = b;
                }
            }
            finally {
                if (cstmt != null) {
                    cstmt.close();
                }
                if ((cp != null) && (cp.getConection() != null)) {
                    cp.getConection().close();
                }
            }
        }
        catch (NamingException | SQLException e) {
            throw new SystemException(e.getMessage(), e);
        }
        return rta;
    }
    
    
    /**
     * Ejecucion de funciones en la base de datos.
     * JM CC 3425
     * la diferencia con la de arriba es que aca se pasa el objeto directo sin concatenar 
     * (cuando es muy grande se rompe ) 
     * muchas pruebas, lagrimas y gemini dieron esta version "final"
     * quizas en algun punto se va a tener que modificar y usarla en otros lados
     * pero mi yo del futuro se preocupara cuando llegue el momento 
     * y si no soy yo, suerte compañero 
     */
    
    public static Object ejecutarFuncionConClob(String nombreConexion, String funcion, 
    	    Object parametroClob, int sqlType) throws SystemException {
    	    
    	    ConectorPool cp = null;
    	    Object rta = null;
    	    CallableStatement cstmt = null;
    	    String sql = null;

    	    try {
    	        cp = new ConectorPool();
    	        cp.conectar(nombreConexion);
    	        
    	        // Usamos ? para el retorno y ? para el parámetro CLOB
    	        // Esto evita la concatenación de strings largos
    	        sql = "{ ? = call " + funcion + " (UN_JSON => ?) }";
    	        
    	        logger.info("SQL FINAL: " + sql);
    	        
    	        cstmt = cp.getConection().prepareCall(sql);
    	        
    	        // Registro del parámetro de salida (el retorno de la función)
    	        cstmt.registerOutParameter(1, sqlType);
    	        
    	     // Seteo del parámetro de entrada usando un Stream para evitar errores de tipo
    	        if (parametroClob != null && parametroClob instanceof String) {
    	            String contenido = (String) parametroClob;
    	            // Creamos un StringReader para que el driver lea el JSON como un flujo
    	            java.io.StringReader reader = new java.io.StringReader(contenido);
    	            cstmt.setCharacterStream(2, reader, contenido.length());
    	        } else if (parametroClob == null) {
    	            cstmt.setNull(2, java.sql.Types.CLOB);
    	        } else {
    	            // Para otros tipos de objetos (poco probable en este caso)
    	            cstmt.setObject(2, parametroClob);
    	        }

    	        logger.info("Ejecutando función con CLOB: " + funcion);
    	        cstmt.execute();
    	        
    	        rta = cstmt.getObject(1);

    	    } catch (NamingException | SQLException e) {
    	        throw new SystemException(e.getMessage(), e);
    	    } finally {
    	        try {
    	            if (cstmt != null) cstmt.close();
    	            if (cp != null && cp.getConection() != null) cp.getConection().close();
    	        } catch (SQLException e) {
    	            logger.error("Error cerrando conexiones", e);
    	        }
    	    }
    	    return rta;
    	}

    /**
     * Convierte las fechas enviadas por parametros y las convierte a
     * objetos java.sql.Timestamp que se pueda procesar por JDBC.
     * 
     * @param fechas
     * @return arreglo de timestamps.
     * @throws SystemException
     */
    private static Timestamp[] prepararFechas(List<String> fechas)
                    throws SystemException {
        Timestamp[] dateTimeParameters = new Timestamp[fechas.size()];
        for (int i = 0; i < fechas.size(); i++) {
            String fecha = fechas.get(i);
            Date date = null;
            try {
                date = SysmanFunciones.convertirAFecha(fecha);
            }
            catch (ParseException e) {
                throw new SystemException(e);
            }
            dateTimeParameters[i] = new Timestamp(date.getTime());
        }
        return dateTimeParameters;
    }

    /**
     * Identifica el nombre de producto de base de datos al que se
     * est&aacute; conectando.
     * 
     * @param conexion
     * @return
     */
    private static String traerNombreProductoBD(Connection conexion) {
        String strDb = ORACLE;
        DatabaseMetaData dbm;
        try {
            dbm = conexion.getMetaData();
            strDb = dbm.getDatabaseProductName();
            if (strDb.toUpperCase().contains(MICROSOFT)) {
                strDb = MICROSOFT;
            }
            if (strDb.toUpperCase().contains(ORACLE)) {
                strDb = ORACLE;
            }
        }
        catch (Exception ex) {
            strDb = "TIPO DE BASE DE DATOS NO DEFINIDA " + ex.toString();
        }
        return strDb;
    }

    /**
     * Identifica el nombre de producto de base de datos al que se
     * est&aacute; conectando.
     * 
     * @param nombreConexion
     * nombre de la conexion
     * @return nombre del producto de base de datos
     * @throws NamingException
     * @throws SQLException
     */
    public static String traerNombreProductoBD(String nombreConexion)
                    throws NamingException, SQLException {
        String databaseProduct = null;
        ConectorPool conectorPool = new ConectorPool();
        try {
            conectorPool.conectar(nombreConexion);
            databaseProduct = traerNombreProductoBD(
                            conectorPool.getConection());
        }
        finally {
            conectorPool.getConection().close();
        }
        return databaseProduct;
    }

    /**
     * Captura las fechas definidas como cadena entre numerales. Ej.
     * <dt>21/01/2019
     * <dt>o
     * <dt>21/01/2019 08:19:00
     * <dt>
     * 
     * @param callableStatement
     * sentencia que se va a ejecutar por jTDS
     * @return arreglo con dos elemenots: la sentencia final y las
     * fechas identificadas, si aplican.
     */
    private static List<Object> capturarFechas(String callableStatement) {
        String regex = "<dt>(.*?)<dt>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(callableStatement);

        if (!matcher.find()) {
            return new ArrayList<>();
        }

        List<Object> parametros = new ArrayList<>();
        List<String> fechas = new ArrayList<>();
        String sqlFinal = callableStatement;

        Matcher matcher2 = pattern.matcher(sqlFinal);
        /*-Busca fechas declaradas como <dt>15/09/2018<dt> o <dt>15/09/2018 14:30:56<dt>*/
        while (matcher2.find()) {
            String variable = matcher2.group(0);
            sqlFinal = sqlFinal.replace(variable, "?");
            fechas.add(variable.replace("<dt>", ""));
        }
        parametros.add(sqlFinal);
        parametros.add(fechas);
        return parametros;
    }

}
