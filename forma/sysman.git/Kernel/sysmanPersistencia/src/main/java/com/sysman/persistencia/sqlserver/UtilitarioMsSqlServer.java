/*-
 * UtilitarioMsSqlServer.java
 *
 * 1.0
 * 
 * 13 sept. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.persistencia.sqlserver;

import com.sysman.exception.SystemException;
import com.sysman.util.SysmanFunciones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Funcionalidades para trabajar con bases de datos Microsoft SQL
 * Server.
 * 
 * @version 2.1, 12 oct. 2018
 * @author jrodrigueza
 *
 */
public class UtilitarioMsSqlServer {

    /**
     * Constructor privado para declarar la clase como una de tipo
     * utilitaria.
     */
    private UtilitarioMsSqlServer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Traduce los parametros de PL/SQL a sintáxis válida para
     * ejecutar en Transact SQL.
     * 
     * @param rutina
     * nombre del procedimiento/funci&oacute;n
     * @param parametros
     * parametros usados en Oracle
     * @return parametros separados por coma para ejecutar en MS SQL
     * Server.
     * @throws SQLException
     * en caso de que no se pueda liberar el ResultSet a la base de
     * datos.
     * @throws SystemException
     * en caso de que se presente alg&uacute;n problema al realizar la
     * traducci&oacute;n
     */
    public static String traducirParametrosATransactSQL(String rutina,
        String parametros, Connection conexionBaseDatos)
                    throws SystemException, SQLException {
        String strCadena = "";
        boolean hasOutParameter = false;
        rutina = SysmanUtl.strTraductorOnLine(rutina);
        rutina = rutina.replace(".", "_");
        try {
            String tipoRutina = identificarTipoRutina(rutina,
                            conexionBaseDatos);

            if ("FN".equals(tipoRutina) || "AF".equals(tipoRutina)) {
                strCadena = "".concat("{?=call ").concat(rutina).concat(" (");
            }
            else if ("P".equals(tipoRutina)) {
                hasOutParameter = tieneParametroDeSalida(rutina,
                                conexionBaseDatos);
                strCadena = "".concat("{call ").concat(rutina).concat(" (");
            }
            parametros = ajustarPorRegExp(parametros);
            parametros = SysmanUtl.strTraductorOnLine(parametros);
            parametros = parametros.replace("CONVERT(DATETIME,'", "<dt>");
            parametros = parametros.replace("CONVERT(DATE,'", "<dt>");
            parametros = parametros.replace("',103)", "<dt>");
            if (parametros.contains("dbo.TO_CHAR_DATE(")) {
                parametros = parametros.replace("dbo.TO_CHAR_DATE('",
                                "<dt>");
                parametros = parametros.replace(
                                "','DD/MM/YYYY HH24:MI:SS')",
                                "<dt>");
                parametros = parametros.replace(
                                "','dd/MM/YYYY HH24:MI:SS')",
                                "<dt>");
            }
            if (parametros.contains("dbo.TO_DATE(")) {
                parametros = parametros.replace("dbo.TO_DATE('", "<dt>");
            }
            String[] strPartes;
            String[] strSubPartes;
            if (parametros.contains("=>")) {
                strPartes = SysmanUtl.strSplit(parametros);
                parametros = "";
                for (int intL = 0; intL < strPartes.length; intL++) {
                    strSubPartes = strPartes[intL].split("=>");
                    parametros = parametros.concat(strSubPartes[1])
                                    .concat(
                                                    (intL < strPartes.length - 1
                                                        ? " , "
                                                        : ""));
                }
            }
            strCadena = strCadena.concat(parametros);

            String parametroSalida = (parametros == null
                || parametros.isEmpty()) ? "?" : ", ?";

            strCadena = strCadena
                            .concat((hasOutParameter ? parametroSalida : ""))
                            .concat(")}");
        }
        catch (Exception ex) {
            throw new SystemException(ex);
        }
        return strCadena;
    }

    /**
     * Reemplazos con expresiones regulares.
     * 
     * @param input
     * @return
     */
    private static String ajustarPorRegExp(String input) {
        String output = input;
        // extraccion de año a partir de una fecha
        // TO_CHAR(FECHA, 'YYYY') a YEAR(FECHA)
        Pattern p = Pattern.compile(
                        "(TO_CHAR)(\\()(\\w+)(,\\s?)('?'YYYY'?')(\\))");
        Matcher m = p.matcher(input);
        if (m.find()) {
            output = m.replaceFirst("YEAR($3)");
        }
        return output;
    }

    /**
     * Identifica si el procedimiento tiene parametros de salida.
     * 
     * @param rutina
     * nombre de la rutina
     * @param conexionBaseDatos
     * conexion a la base de datos
     * @return verdadero si teiene parametros de salida
     * @throws SQLException
     * en caso de que se presenten problemas al consultar la base de
     * datos
     */
    private static boolean tieneParametroDeSalida(String rutina,
        Connection conexionBaseDatos) throws SQLException {
        /*-String sql = "".concat("SELECT ")
                        .concat("P.is_output AS IsOutPutParameter ")
                        .concat("FROM ").concat("sys.objects AS SO ")
                        .concat("INNER JOIN sys.parameters AS P ON SO.OBJECT_ID = P.OBJECT_ID ")
                        .concat("WHERE ").concat("P.is_output NOT IN (0) ")
                        .concat("AND SO.name = ? ");*/
        // pespitia:20181209
        String sql = SysmanFunciones.concatenar(
                        "SELECT COUNT(PARAMETER_MODE) IsOutPutParameter ",
                        "FROM INFORMATION_SCHEMA.ROUTINES R ",
                        "INNER JOIN INFORMATION_SCHEMA.PARAMETERS P ",
                        "  ON R.SPECIFIC_CATALOG = P.SPECIFIC_CATALOG ",
                        " AND R.SPECIFIC_SCHEMA  = P.SPECIFIC_SCHEMA ",
                        " AND R.SPECIFIC_NAME    = P.SPECIFIC_NAME ",
                        "WHERE ROUTINE_NAME IN(?) ",
                        "  AND PARAMETER_MODE IN('INOUT') ");

        ResultSet resultSet = null;
        try (PreparedStatement statement = conexionBaseDatos
                        .prepareStatement(sql);) {
            statement.setString(1, rutina);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean(1);
        }
        finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    /**
     * Identifica de que tipo es una rutina.
     * 
     * @param rutina
     * nombre de la rutina
     * @param conexionBaseDatos
     * conexion a la base de datos
     * @return tipo de rutina segun MS SQL Server ya sea funcion (FN,
     * AF) o procedimiento (P)
     * @throws SystemException
     * en caso de que no exista la funcion consultada
     * @throws SQLException
     * en caso de que no se pueda cerrar el ResultSet
     */
    private static String identificarTipoRutina(String rutina,
        Connection conexionBaseDatos) throws SystemException, SQLException {
        String sql = " SELECT TYPE FROM sys.sysobjects WHERE NAME = ? ";
        ResultSet resultSet = null;
        try (PreparedStatement statement = conexionBaseDatos
                        .prepareStatement(sql)) {
            statement.setString(1, rutina);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getObject(1).toString().trim().toUpperCase();
        }
        catch (SQLException e) {
            throw new SystemException("La función/procedimiento "
                + rutina + " no existe en en la base de datos.");
        }
        finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    /**
     * Metodo para pruebas.
     * 
     * @param args
     */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        String compania = "001";
        String terceroNombreIni = "AAA";
        String terceroNombreFin = "zzz";
        String terceroNitInicial = "000";
        String terceroNitFinal = "zzz";
        String sqlOracle = true ? "WHERE TERCERO.COMPANIA = '"
            + compania + "' AND TRIM(UPPER(TERCERO.NOMBRE)) BETWEEN '"
            + terceroNombreIni + "' AND '" + terceroNombreFin
            + "' \n"
            + " ORDER BY TERCERO.NOMBRE"
            : "WHERE  TERCERO.COMPANIA = '" + compania
                + "' AND  TERCERO.NIT BETWEEN '"
                + terceroNitInicial + "' AND '"
                + terceroNitFinal + "' \n"
                + " ORDER BY TRIM(UPPER(TERCERO.NIT))";
        String sqlFinal = SysmanUtl.strTraductorSql(sqlOracle);
        sqlFinal = SysmanUtl.strTraductorOnLine(sqlFinal);

        System.out.println(sqlFinal);
    }
}
