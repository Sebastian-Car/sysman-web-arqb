/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.persistencia;

import com.sysman.util.SysmanFunciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author cmanrique
 */
public class Acciones {

    protected static final Log logger = LogFactory.getLog(Acciones.class);

    private Acciones() {
    }

    public static int insertar(String nombreConexion, String tabla,
        Map<String, Object> campos) throws IllegalAccessException,
                        InstantiationException, ClassNotFoundException,
                        SQLException, NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            cp = new ConectorPool();
            Iterator<Entry<String, Object>> it = campos.entrySet().iterator();
            StringBuilder valor = new StringBuilder("");
            StringBuilder nombreCampos = new StringBuilder("");
            String nombreCamposCadena;
            String valorCadena;
            SimpleDateFormat sdf = new SimpleDateFormat(
                            ConstatesPersistencia.ACME_FORMATO_FECHA_DEFAULT);
            String cadFinal;
            while (it.hasNext()) {
                Map.Entry e = it.next();
                if ((e.getValue() != null) && !("" + e.getValue()).isEmpty()) {
                    nombreCampos.append(e.getKey() + ",");
                    if (e.getValue() instanceof Date) {
                        valor.append(ConstatesPersistencia.ACME_OPERADOR_TO_DATE
                            + sdf.format(e.getValue())
                            + ConstatesPersistencia.ACME_FORMATO_FECHA_CONCATENADO);
                    }
                    else {
                        cadFinal = e.getValue().toString();
                        if (cadFinal.contains("'")) {
                            cadFinal = cadFinal.replace("'", "''");
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }
                        else {
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }

                    }
                }
            }

            nombreCamposCadena = nombreCampos.toString().substring(0,
                            nombreCampos.length() - 1);
            valorCadena = valor.toString();
            valorCadena = valorCadena.substring(0, valor.length() - 1);
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_TRUE,
                            "-1");
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_FALSE,
                            "0");

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_INSERTAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_UN_VALORES_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_INSERTAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp.conectar(nombreConexion);

            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, nombreCamposCadena);
            cstmt.setString(3, valorCadena);
            cstmt.registerOutParameter(4, Types.INTEGER);
            cstmt.executeQuery();
            rta = cstmt.getInt(4);

        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }

        }

        return rta;
    }

    public static int insertar(ConectorPool cp, String tabla,
        Map<String, Object> campos) throws IllegalAccessException,
                        InstantiationException, ClassNotFoundException,
                        SQLException, NamingException {
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            Iterator<Entry<String, Object>> it = campos.entrySet().iterator();
            StringBuilder valor = new StringBuilder("");
            StringBuilder nombreCampos = new StringBuilder("");
            String nombreCamposCadena;
            String valorCadena;
            SimpleDateFormat sdf = new SimpleDateFormat(
                            ConstatesPersistencia.ACME_FORMATO_FECHA_DEFAULT);
            String cadFinal;
            while (it.hasNext()) {
                Map.Entry e = it.next();
                if ((e.getValue() != null) && !("" + e.getValue()).isEmpty()) {
                    nombreCampos.append(e.getKey() + ",");
                    if (e.getValue() instanceof Date) {
                        valor.append(ConstatesPersistencia.ACME_OPERADOR_TO_DATE
                            + sdf.format(e.getValue())
                            + ConstatesPersistencia.ACME_FORMATO_FECHA_CONCATENADO);
                    }
                    else {
                        cadFinal = e.getValue().toString();
                        if (cadFinal.contains("'")) {
                            cadFinal = cadFinal.replace("'", "''");
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }
                        else {
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }

                    }
                }
            }

            nombreCamposCadena = nombreCampos.toString().substring(0,
                            nombreCampos.length() - 1);
            valorCadena = valor.toString().substring(0, valor.length() - 1);
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_TRUE,
                            "-1");
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_FALSE,
                            "0");

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_INSERTAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_UN_VALORES_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_INSERTAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, nombreCamposCadena);
            cstmt.setString(3, valorCadena);
            cstmt.registerOutParameter(4, Types.INTEGER);
            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + nombreCampos + ConstatesPersistencia.ACME_ASIGNACION_VALOR_N
                + valor
                + "\nUN_LLAVE=>"
                + valor + ")");
            cstmt.executeQuery();
            rta = cstmt.getInt(4);

        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }

        return rta;
    }

    public static Map<String, Object> insertar(ConectorPool cp, String tabla,
        Map<String, Object> campos, String[] llave)
                        throws IllegalAccessException,
                        InstantiationException, ClassNotFoundException,
                        SQLException, NamingException,
                        IOException {
        CallableStatement cstmt = null;
        Map<String, Object> rta = null;
        try {
            Iterator it = campos.entrySet().iterator();
            StringBuilder nombreCampos = new StringBuilder("");
            StringBuilder valor = new StringBuilder("");
            String valorCadena = "";
            String nombreCamposCadena;
            String estructuraLlave = "";
            SimpleDateFormat sdf = new SimpleDateFormat(
                            ConstatesPersistencia.ACME_FORMATO_FECHA_DEFAULT);
            String cadFinal;
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                if ((e.getValue() != null) && !("" + e.getValue()).isEmpty()) {
                    nombreCampos.append(e.getKey() + ",");
                    if (e.getValue() instanceof Date) {
                        valor.append(ConstatesPersistencia.ACME_OPERADOR_TO_DATE
                            + sdf.format(e.getValue())
                            + ConstatesPersistencia.ACME_FORMATO_FECHA_CONCATENADO);
                    }
                    else {
                        cadFinal = e.getValue().toString();
                        if (cadFinal.contains("'")) {
                            cadFinal = cadFinal.replace("'", "''");
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }
                        else {
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }

                    }
                }
            }

            nombreCamposCadena = nombreCampos.toString().substring(0,
                            nombreCampos.length() - 1);
            valorCadena = valor.toString().substring(0, valor.length() - 1);
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_TRUE,
                            "-1");
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_FALSE,
                            "0");

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_INSERTAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + "     UN_VALORES => ?,"
                + "      UN_LLAVE =>?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_INSERTAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            logger.info(sql);

            estructuraLlave = getEstructuraLlaves(llave);

            cstmt = cp.getConection().prepareCall(sql);

            cstmt.setString(1, tabla);
            cstmt.setString(6, tabla);
            cstmt.setString(2, nombreCamposCadena);
            cstmt.setString(3, valorCadena);
            cstmt.setString(4, estructuraLlave);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + nombreCampos + ConstatesPersistencia.ACME_ASIGNACION_VALOR_N
                + valor
                + "\nUN_LLAVE=>"
                + estructuraLlave + ")");

            cstmt.registerOutParameter(5, Types.VARCHAR);

            cstmt.executeQuery();
            rta = SysmanFunciones.jsonToMap(cstmt.getString(5));
            logger.info(rta);

        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }

        return rta;
    }

    private static String getEstructuraLlaves(String[] llavesVec) {
        StringBuilder rta = new StringBuilder("'{");
        for (int i = 0; i < llavesVec.length; i++) {
            rta.append("\"" + llavesVec[i] + "\":\"'||" + llavesVec[i]
                + "||'\",");
        }
        rta.deleteCharAt(rta.length() - 1);
        rta.append("}'");

        return rta.toString();

    }

    public static int insertar(String nombreConexion, String tabla,
        String campos, String valores)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = -1;
        try {

            cp = new ConectorPool();

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + "     UN_ACCION => 'IS',\n"
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_UN_VALORES_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_INSERTAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp.conectar(nombreConexion);
            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);

            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, campos);
            cstmt.setString(3, valores);
            cstmt.registerOutParameter(4, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + campos + ConstatesPersistencia.ACME_ASIGNACION_VALOR_N
                + valores + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(4);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
        return rta;
    }

    public static int insertar(ConectorPool cp, String tabla, String campos,
        String valores)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + "     UN_ACCION => 'IS',\n"
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_UN_VALORES_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_INSERTAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            logger.info(sql);

            cstmt = cp.getConection().prepareCall(sql);

            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, campos);
            cstmt.setString(3, valores);
            cstmt.registerOutParameter(4, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + campos + ConstatesPersistencia.ACME_ASIGNACION_VALOR_N
                + valores + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(4);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }
        return rta;
    }

    public static int insertarRegistro(String nombreConexion, String tabla,
        String campos, String valores)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            cp = new ConectorPool();

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_INSERTAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_UN_VALORES_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_INSERTAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";
            cp.conectar(nombreConexion);

            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, campos);
            cstmt.setString(3, valores);
            cstmt.registerOutParameter(4, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + campos + ConstatesPersistencia.ACME_ASIGNACION_VALOR_N
                + valores + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(4);

        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
        return rta;
    }

    public static int insertarRegistro(ConectorPool cp, String tabla,
        String campos, String valores)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {

        CallableStatement cstmt = null;
        int rta = -1;
        try {
            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_INSERTAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_UN_VALORES_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_INSERTAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, campos);
            cstmt.setString(3, valores);
            cstmt.registerOutParameter(4, Types.VARCHAR);
            cstmt.executeQuery();

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + campos + ConstatesPersistencia.ACME_ASIGNACION_VALOR_N
                + valores + ")");
            rta = cstmt.getInt(4);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }
        return rta;
    }

    public static int actualizar(String nombreConexion, String tabla,
        String campos, String condicion)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = 0;
        try {

            cp = new ConectorPool();

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_MODIFICAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_ELIMINAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp.conectar(nombreConexion);
            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, campos);
            cstmt.setString(3, condicion);
            cstmt.registerOutParameter(4, Types.NUMERIC);
            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + campos + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_N
                + condicion + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(4);

        }
        finally {
            if (cstmt != null) {
                cstmt.close();

            }
            if (cp != null) {
                cp.getConection().close();
            }
        }

        return rta;
    }

    public static int actualizar(String nombreConexion, String tabla,
        String campos, Map<String, Object> llave)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = 0;
        try {
            cp = new ConectorPool();
            String condicion = generarNombresCampos(llave,
                            ConstatesPersistencia.ACME_OPERADOR_AND);

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_MODIFICAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_ELIMINAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp.conectar(nombreConexion);
            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, campos);
            cstmt.setString(3, condicion);
            cstmt.registerOutParameter(4, Types.NUMERIC);
            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + campos + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_N
                + condicion + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(4);

        }
        finally {
            if (cstmt != null) {
                cstmt.close();

            }
            if (cp != null) {
                cp.getConection().close();
            }
        }

        return rta;
    }

    public static int actualizar(String nombreConexion, String tabla,
        Map<String, Object> campos, Map<String, Object> llave)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;

        String nombreCampos = generarNombresCampos(campos, ",");
        String condicion = generarNombresCampos(llave,
                        ConstatesPersistencia.ACME_OPERADOR_AND);

        int rta = -1;

        try {

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_MODIFICAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_ELIMINAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp = new ConectorPool();
            cp.conectar(nombreConexion);
            logger.info(sql);

            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, nombreCampos);
            cstmt.setString(3, condicion);
            cstmt.registerOutParameter(4, Types.INTEGER);
            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + campos + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_N
                + condicion + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(4);

        }
        finally

        {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }

        return rta;

    }

    public static int actualizar(ConectorPool cp, String tabla,
        Map<String, Object> campos, Map<String, Object> llave)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        CallableStatement cstmt = null;

        String nombreCampos = generarNombresCampos(campos, ",");
        String condicion = generarNombresCampos(llave,
                        ConstatesPersistencia.ACME_OPERADOR_AND);

        int rta = -1;

        try {
            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_MODIFICAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_ELIMINAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);

            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, nombreCampos);
            cstmt.setString(3, condicion);
            cstmt.registerOutParameter(4, Types.INTEGER);
            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS_N
                + nombreCampos
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_N + condicion
                + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(4);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }

        }

        return rta;
    }

    public static String generarNombresCampos(Map<String, Object> campos,
        String union) {
        StringBuilder nombreCampos = new StringBuilder("");
        String nombreCamposCadena;
        Iterator it = campos.entrySet().iterator();
        SimpleDateFormat sdf = new SimpleDateFormat(
                        ConstatesPersistencia.ACME_FORMATO_FECHA_DEFAULT);
        String aux;

        while (it.hasNext()) {

            Map.Entry e = (Map.Entry) it.next();
            if (!ConstatesPersistencia.ACME_CAMPO_ROWID.equals(e.getKey())) {
                if (e.getValue() instanceof Date) {
                    nombreCampos.append(e.getKey()
                        + ConstatesPersistencia.ACME_OPERADOR_TO_DATE_IGUAL
                        + sdf.format(e.getValue())
                        + "','DD/MM/YYYY HH24:mi:ss')" + union);
                }
                else if ((campos.get(e.getKey()) != null)
                    && !campos.get(e.getKey()).toString().isEmpty()
                    && campos.get(e.getKey()).toString()
                                    .contains("'")) {
                    aux = campos.get(e.getKey()).toString().replace("'", "''");
                    aux = e.getValue().toString().length() > 3500
                        ? getClobConcatenado(aux)
                        : "'" + aux + "'" + union;
                    nombreCampos.append(e.getKey() + "= " + aux);
                }
                else {
                    if (e.getValue() != null) {
                        aux = e.getValue().toString().length() > 3500
                            ? getClobConcatenado(
                                            e.getValue().toString())
                            : "'" + e.getValue().toString() + "'"
                                + union;
                    }
                    else {
                        aux = "'" + e.getValue() + "'" + union;
                    }

                    nombreCampos.append(e.getKey() + "= " + aux);
                }
            }
        }
        nombreCamposCadena = nombreCampos.toString().substring(0,
                        nombreCampos.length() - union.length());
        nombreCamposCadena = nombreCamposCadena.replace(
                        ConstatesPersistencia.ACME_VARIABLE_NULL, "null");
        nombreCamposCadena = nombreCamposCadena.replace(
                        ConstatesPersistencia.ACME_VARIABLE_NULL_MAYUS, "null");
        nombreCamposCadena = nombreCamposCadena.replace(
                        ConstatesPersistencia.ACME_VARIABLE_TRUE, "-1");
        nombreCamposCadena = nombreCamposCadena.replace(
                        ConstatesPersistencia.ACME_VARIABLE_FALSE, "0");
        return nombreCamposCadena;
    }

    public static String generarNombreCampos(Map<String, Object> campos,
        Map<String, Object> ini) {
        StringBuilder nombreCampos = new StringBuilder("");
        String aux;
        SimpleDateFormat sdf = new SimpleDateFormat(
                        ConstatesPersistencia.ACME_FORMATO_FECHA_DEFAULT);
        Iterator it = campos.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            if (!ConstatesPersistencia.ACME_CAMPO_ROWID.equals(e.getKey())) {
                if ((campos.get(e.getKey()) != null)
                    && !(e.getValue() instanceof Date)
                    && campos.get(e.getKey()).toString()
                                    .contains("'")) {
                    campos.put((String) e.getKey(), campos.get(e.getKey())
                                    .toString().replace("'", "''"));
                }
                if (((!SysmanFunciones.validarCampoVacio(campos,
                                (String) e.getKey()))
                    && (ini.get(e.getKey()) == null))
                    || ((campos.get(e.getKey()) == null)
                        && (ini.get(e.getKey()) != null))) {
                    if (e.getValue() instanceof Date) {
                        nombreCampos.append(e.getKey()
                            + ConstatesPersistencia.ACME_OPERADOR_TO_DATE_IGUAL
                            + sdf.format(e.getValue())
                            + ConstatesPersistencia.ACME_FORMATO_FECHA_CONCATENADO);
                    }
                    else {
                        if (e.getValue() != null) {
                            aux = e.getValue().toString().length() > 3500
                                ? getClobConcatenado(e.getValue()
                                                .toString())
                                : "'" + e.getValue().toString()
                                    + "',";
                        }
                        else {
                            aux = "'" + e.getValue() + "',";
                        }
                        nombreCampos = nombreCampos
                                        .append(e.getKey() + "= " + aux);
                    }
                }
                else if (!(e.getValue() instanceof Date)
                    && ((campos.get(e.getKey()) != null)
                        && (ini.get(e.getKey()) != null))
                    && (!campos.get(e.getKey()).toString()
                                    .equals(ini.get(e.getKey())
                                                    .toString()))) {
                    if (e.getValue() != null) {
                        aux = e.getValue().toString().length() > 3500
                            ? getClobConcatenado(
                                            e.getValue().toString())
                            : "'" + e.getValue().toString() + "',";
                    }
                    else {
                        aux = "'" + e.getValue() + "',";
                    }
                    nombreCampos.append(e.getKey() + "= " + aux);
                }
                else if ((e.getValue() instanceof Date)
                    && !campos.get(e.getKey())
                                    .equals(ini.get(e.getKey()))) {
                    nombreCampos.append(e.getKey()
                        + ConstatesPersistencia.ACME_OPERADOR_TO_DATE_IGUAL
                        + sdf.format(e.getValue())
                        + ConstatesPersistencia.ACME_FORMATO_FECHA_CONCATENADO);
                }
            }

        }
        return nombreCampos.toString();

    }

    public static int actualizar(ConectorPool cp, String tabla, String campos,
        String condicion)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        int rta = 0;
        CallableStatement cstmt = null;
        try {
            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_MODIFICAR
                + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_ELIMINAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cstmt = cp.getConection().prepareCall(sql);

            cstmt.setString(1, tabla);
            cstmt.setString(5, tabla);
            cstmt.setString(2, campos);
            cstmt.setString(3, condicion);
            cstmt.registerOutParameter(4, Types.NUMERIC);
            cstmt.executeQuery();
            rta = cstmt.getInt(4);
            logger.info(sql);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }
        return rta;
    }

    public static int actualizar(ConectorPool cp, String tabla,
        Map<String, Object> campos, Map<String, Object> ini,
        Map<String, Object> llave)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        CallableStatement cstmt = null;
        campos = new HashMap<>(campos);
        int rta = -1;
        String nombreCampos = generarNombreCampos(campos, ini);
        String condicion = generarNombresCampos(llave,
                        ConstatesPersistencia.ACME_OPERADOR_AND);

        if (!nombreCampos.isEmpty()) {
            try {
                nombreCampos = nombreCampos.substring(0,
                                nombreCampos.length() - 1);
                nombreCampos = nombreCampos.replace(
                                ConstatesPersistencia.ACME_VARIABLE_NULL,
                                "null");
                nombreCampos = nombreCampos.replace(
                                ConstatesPersistencia.ACME_VARIABLE_NULL_MAYUS,
                                "null");
                nombreCampos = nombreCampos.replace(
                                ConstatesPersistencia.ACME_VARIABLE_TRUE, "-1");
                nombreCampos = nombreCampos.replace(
                                ConstatesPersistencia.ACME_VARIABLE_FALSE, "0");

                String sql = "DECLARE\n"
                    + "    MI_RTA VARCHAR2(4000);\n "
                    + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                    + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                    + ConstatesPersistencia.ACME_ACCION_MODIFICAR
                    + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                    + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST
                    + "?);\n"
                    + "     ?" + ConstatesPersistencia.ACME_RTA
                    + ConstatesPersistencia.ACME_EXCEPCION_ELIMINAR
                    + "          PCK_ERR_MSG.RAISE_WITH_MSG("
                    + "                  UN_EXC_COD =>SQLCODE,"
                    + "                  UN_TABLAERROR =>?);"
                    + "END;";

                logger.info(sql);
                cstmt = cp.getConection().prepareCall(sql);

                cstmt.setString(1, tabla);
                cstmt.setString(5, tabla);
                cstmt.setString(2, nombreCampos);
                cstmt.setString(3, condicion);
                cstmt.registerOutParameter(4, Types.INTEGER);

                logger.info(ConstatesPersistencia.ACME_ASIGNACION_TABLA
                    + tabla + ",\n"
                    + ConstatesPersistencia.ACME_ACCION_MODIFICAR
                    + "     UN_CAMPOS => " + nombreCampos + ",\n"
                    + ConstatesPersistencia.ACME_ASIGNACION_CONDICION
                    + condicion);

                cstmt.executeQuery();
                rta = cstmt.getInt(4);
            }
            finally {
                campos = null;
                if (cstmt != null) {
                    cstmt.close();
                }
            }
        }
        return rta;
    }

    public static int actualizar(String nombreConexion, String tabla,
        Map<String, Object> campos, Map<String, Object> ini,
        Map<String, Object> llave)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        campos = new HashMap<>(campos);
        int rta = -1;
        String nombreCampos = generarNombreCampos(campos, ini);
        String condicion = generarNombresCampos(llave,
                        ConstatesPersistencia.ACME_OPERADOR_AND);

        if (!nombreCampos.isEmpty()) {
            try {
                nombreCampos = nombreCampos.substring(0,
                                nombreCampos.length() - 1);
                nombreCampos = nombreCampos.replace(
                                ConstatesPersistencia.ACME_VARIABLE_NULL,
                                "null");
                nombreCampos = nombreCampos.replace(
                                ConstatesPersistencia.ACME_VARIABLE_NULL_MAYUS,
                                "null");
                nombreCampos = nombreCampos.replace(
                                ConstatesPersistencia.ACME_VARIABLE_TRUE, "-1");
                nombreCampos = nombreCampos.replace(
                                ConstatesPersistencia.ACME_VARIABLE_FALSE, "0");

                String sql = "DECLARE\n"
                    + "    MI_RTA VARCHAR2(4000);\n "
                    + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                    + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                    + ConstatesPersistencia.ACME_ACCION_MODIFICAR
                    + ConstatesPersistencia.ACME_ASIGNACION_CAMPOS + " ?,\n"
                    + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST
                    + "?);\n"
                    + "     ?" + ConstatesPersistencia.ACME_RTA
                    + ConstatesPersistencia.ACME_EXCEPCION_ELIMINAR
                    + "          PCK_ERR_MSG.RAISE_WITH_MSG("
                    + "                  UN_EXC_COD =>SQLCODE,"
                    + "                  UN_TABLAERROR =>?);"
                    + "END;";

                cp = new ConectorPool();
                cp.conectar(nombreConexion);
                logger.info(sql);

                cstmt = cp.getConection().prepareCall(sql);
                cstmt.setString(1, tabla);
                cstmt.setString(5, tabla);
                cstmt.setString(2, nombreCampos);
                cstmt.setString(3, condicion);
                cstmt.registerOutParameter(4, Types.INTEGER);
                logger.info(ConstatesPersistencia.ACME_ASIGNACION_TABLA
                    + tabla + ",\n"
                    + ConstatesPersistencia.ACME_ACCION_MODIFICAR
                    + "     UN_CAMPOS => " + nombreCampos + ",\n"
                    + ConstatesPersistencia.ACME_ASIGNACION_CONDICION
                    + condicion);
                cstmt.executeQuery();
                rta = cstmt.getInt(4);
            }
            finally {
                campos = null;
                if (cstmt != null) {
                    cstmt.close();
                }
                if (cp != null) {
                    cp.getConection().close();
                }
            }
        }
        return rta;
    }

    public static int eliminar(String nombreConexion, String tabla,
        Map<String, Object> llave) throws IllegalAccessException,
                        InstantiationException, ClassNotFoundException,
                        SQLException, NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = -1;
        String condicion = generarNombresCampos(llave,
                        ConstatesPersistencia.ACME_OPERADOR_AND);
        try {
            cp = new ConectorPool();

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_ELIMINAR
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_ACTUALIZAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp.conectar(nombreConexion);
            cstmt = cp.getConection().prepareCall(sql);

            cstmt.setString(1, tabla);
            cstmt.setString(4, tabla);
            cstmt.setString(2, condicion);

            cstmt.registerOutParameter(3, Types.INTEGER);
            logger.info(ConstatesPersistencia.ACME_ASIGNACION_TABLA
                + tabla + ",\n"
                + ConstatesPersistencia.ACME_ACCION_ELIMINAR
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION + condicion);

            cstmt.executeQuery();

            rta = cstmt.getInt(3);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
        return rta;
    }

    public static int eliminar(ConectorPool cp, String tabla,
        Map<String, Object> llave) throws IllegalAccessException,
                        InstantiationException, ClassNotFoundException,
                        SQLException, NamingException {
        CallableStatement cstmt = null;
        int rta = -1;
        String condicion = generarNombresCampos(llave,
                        ConstatesPersistencia.ACME_OPERADOR_AND);
        try {

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_ELIMINAR
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_ACTUALIZAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);

            cstmt.setString(1, tabla);
            cstmt.setString(4, tabla);
            cstmt.setString(2, condicion);

            cstmt.registerOutParameter(3, Types.INTEGER);
            logger.info(ConstatesPersistencia.ACME_ASIGNACION_TABLA
                + tabla + ",\n"
                + ConstatesPersistencia.ACME_ACCION_ELIMINAR
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION + condicion);
            cstmt.executeQuery();
            rta = cstmt.getInt(3);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }

        }
        return rta;
    }

    public static String getParametro(String nombreConexion, String compania,
        String nombre, String modulo,
        String fecha) throws NamingException, SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        ConectorPool cp = null;
        String parametro = null;
        try {
            cp = new ConectorPool();

            String sql = ConstatesPersistencia.ACME_LLAMADO_PAR
                + " UN_COMPANIA=>?,\n"
                + " UN_NOMBRE=>?,\n"
                + " UN_MODULO=>?,\n"
                + " UN_FECHA_PAR=>" + fecha + "\n"// Esta fecha queda
                                                  // concatenada ya
                                                  // que
                                                  // viene con formato
                                                  // de oracle por lo
                                                  // que el
                                                  // PreparedStatement
                                                  // no la puede
                                                  // interpretar
                + ")  PARAMETRO FROM DUAL ";

            cp.conectar(nombreConexion);
            st = cp.getConection().prepareStatement(sql);

            st.setString(1, compania);
            st.setString(2, nombre);
            st.setInt(3, Integer.parseInt(modulo));

            rs = st.executeQuery();

            parametro = rs.next() ? rs.getString(
                            ConstatesPersistencia.ACME_CAMPO_PARAMETRO)
                : parametro;
            if (parametro != null && ("SI".equalsIgnoreCase(parametro)
                || "NO".equalsIgnoreCase(parametro))) {
                parametro = parametro.toUpperCase();
            }

        }
        finally

        {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
        return parametro;

    }

    public static String getParametro(ConectorPool cp, String compania,
        String nombre, String modulo, String fecha)
                        throws NamingException, SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        String parametro = null;
        try {
            String sql = ConstatesPersistencia.ACME_LLAMADO_PAR
                + "     UN_COMPANIA=>?,\n"
                + "     UN_NOMBRE=>?,\n"
                + "     UN_MODULO=>?,\n"
                + "     UN_FECHA_PAR=>" + fecha + "\n"// Esta fecha
                                                      // queda
            // concatenada ya que
            // viene con formato
            // de oracle por lo
            // que el
            // PreparedStatement
            // no la puede
            // interpretar
                + ") PARAMETRO FROM DUAL ";

            st = cp.getConection().prepareStatement(sql);

            st.setString(1, compania);
            st.setString(2, nombre);
            st.setInt(3, Integer.parseInt(modulo));

            rs = st.executeQuery();

            parametro = rs.next() ? rs.getString(
                            ConstatesPersistencia.ACME_CAMPO_PARAMETRO)
                : parametro;
            if (parametro != null && ("SI".equalsIgnoreCase(parametro)
                || "NO".equalsIgnoreCase(parametro))) {
                parametro = parametro.toUpperCase();
            }
        }
        finally {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return parametro;
    }

    public static String getParametroOriginal(String nombreConexion,
        String compania, String nombre, String modulo,
        String fecha) throws NamingException, SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        ConectorPool cp = null;
        String parametro = null;
        try {
            cp = new ConectorPool();
            String sql = ConstatesPersistencia.ACME_LLAMADO_PAR
                + "UN_COMPANIA=>?,\n"
                + "UN_NOMBRE=>?,\n"
                + "UN_MODULO=>?,\n"
                + "UN_FECHA_PAR=>" + fecha + ",\n"// Esta fecha queda
                                                  // concatenada ya
                                                  // que
                                                  // viene con formato
                                                  // de oracle por lo
                                                  // que el
                                                  // PreparedStatement
                                                  // no la puede
                                                  // interpretar
                + "UN_IND_MAYUS=>0"
                + ") PARAMETRO FROM DUAL";

            cp.conectar(nombreConexion);
            st = cp.getConection().prepareStatement(sql);

            st.setString(1, compania);
            st.setString(2, nombre);
            st.setInt(3, Integer.parseInt(modulo));

            rs = st.executeQuery();

            parametro = rs.next() ? rs.getString(
                            ConstatesPersistencia.ACME_CAMPO_PARAMETRO)
                : parametro;
            if (parametro != null && ("SI".equalsIgnoreCase(parametro)
                || "NO".equalsIgnoreCase(parametro))) {
                parametro = parametro.toUpperCase();
            }
        }
        finally {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
        return parametro;
    }

    public static String getParametroOriginal(ConectorPool cp, String compania,
        String nombre, String modulo, String fecha)
                        throws NamingException, SQLException {
        PreparedStatement st = null;
        ResultSet rs = null;
        String parametro = null;
        try {
            String sql = ConstatesPersistencia.ACME_LLAMADO_PAR
                + "UN_COMPANIA=>?,\n"
                + "UN_NOMBRE=>?,\n"
                + "UN_MODULO=>?,\n"
                + "UN_FECHA_PAR=>" + fecha + ",\n"// Esta fecha queda
                                                  // concatenada ya
                                                  // que
                                                  // viene con formato
                                                  // de oracle por lo
                                                  // que el
                                                  // PreparedStatement
                                                  // no la puede
                                                  // interpretar
                + "UN_IND_MAYUS=>0"
                + ") PARAMETRO FROM DUAL";

            st = cp.getConection().prepareStatement(sql);

            st.setString(1, compania);
            st.setString(2, nombre);
            st.setInt(3, Integer.parseInt(modulo));

            rs = st.executeQuery();

            parametro = rs.next() ? rs.getString(
                            ConstatesPersistencia.ACME_CAMPO_PARAMETRO)
                : parametro;
            if (parametro != null && ("SI".equalsIgnoreCase(parametro)
                || "NO".equalsIgnoreCase(parametro))) {
                parametro = parametro.toUpperCase();
            }
        }
        finally {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }

        }
        return parametro;
    }

    public static Object ejecutarFuncion(String nombreConexion, String funcion,
        String parametros, int sqlType)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        ConectorPool cp = null;
        Object rta = null;
        CallableStatement cstmt = null;
        String sql = ConstatesPersistencia.ACME_LLAMADA + funcion + " ("
            + parametros + ")}";
        logger.info(sql);
        try {
            cp = new ConectorPool();
            cp.conectar(nombreConexion);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.registerOutParameter(1, sqlType);
            cstmt.executeQuery();
            rta = cstmt.getObject(1);

        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if ((cp != null) && (cp.getConection() != null)) {
                cp.getConection().close();
            }
        }
        return rta;
    }

    public static Object ejecutarFuncion(Connection con, String funcion,
        String parametros, int sqlType)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {

        Object rta = null;
        CallableStatement cstmt = null;
        String sql = ConstatesPersistencia.ACME_LLAMADA + funcion + " ("
            + parametros + ")}";
        try {

            cstmt = con.prepareCall(sql);
            cstmt.registerOutParameter(1, sqlType);

            cstmt.executeQuery();
            rta = cstmt.getObject(1);
            logger.info(sql);

        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }

        }
        return rta;
    }

    public static void ejecutarProcedimiento(String nombreConexion,
        String procedimiento, String parametros)
                        throws NamingException, SQLException {
        Statement st = null;
        ConectorPool cp = null;
        try {
            cp = new ConectorPool();
            String sql = "BEGIN " + procedimiento + "(" + parametros
                + "); END; ";
            logger.info(sql);
            cp.conectar(nombreConexion);
            st = cp.getConection().createStatement();
            st.executeUpdate(sql);
        }
        finally {
            if (st != null) {
                st.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }

    }

    public static void ejecutarProcedimiento(Connection con,
        String procedimiento, String parametros)
                        throws NamingException, SQLException {
        Statement st = null;
        try {
            String sql = "BEGIN " + procedimiento + "(" + parametros
                + "); END; ";
            logger.info(sql);
            st = con.createStatement();
            st.executeUpdate(sql);
        }
        finally {
            if (st != null) {
                st.close();
            }

        }
    }

    public static Date getSysDate(String nombreConexion) {
        Statement st = null;
        Date rta = null;
        ConectorPool cp = null;
        try {
            cp = new ConectorPool();
            rta = null;

            cp.conectar(nombreConexion);
            st = cp.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(
                            "SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:mi:ss') FROM DUAL")) {
                String aux = rs.next() ? rs.getString(1) : null;
                rta = SysmanFunciones.convertirAFechaHora(aux);
            }

        }
        catch (NamingException | SQLException | ParseException ex) {
            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null,
                            ex);
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (cp != null) {
                    cp.getConection().close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE,
                                null, ex);
            }
        }
        return rta;
    }

    public static Date getSysDate(ConectorPool cp) {
        Statement st = null;
        Date rta = null;
        try {
            rta = null;
            st = cp.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(
                            "SELECT TO_CHAR(SYSDATE,'DD/MM/YYYY HH24:mi:ss') FROM DUAL")) {
                String aux = rs.next() ? rs.getString(1) : null;
                rta = SysmanFunciones.convertirAFechaHora(aux);
            }

        }
        catch (SQLException | ParseException ex) {
            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null,
                            ex);
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE,
                                null, ex);
            }
        }
        return rta;
    }

    public static Date getSysHora(String nombreConexion) {
        Statement st = null;
        Date rta = null;
        ConectorPool cp = null;
        try {
            cp = new ConectorPool();
            rta = null;

            cp.conectar(nombreConexion);
            st = cp.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(
                            "SELECT '30/12/1899 ' || TO_CHAR(SYSDATE,'HH24:mi:ss') FROM DUAL")) {
                String aux = rs.next() ? rs.getString(1) : null;
                rta = SysmanFunciones.convertirAFechaHora(aux);
            }
        }
        catch (NamingException | SQLException | ParseException ex) {
            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null,
                            ex);
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
                if (cp != null) {
                    cp.getConection().close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE,
                                null, ex);
            }
        }
        return rta;
    }

    public static Date getSysHora(ConectorPool cp) {
        Statement st = null;
        Date rta = null;
        try {
            rta = null;
            st = cp.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(
                            "SELECT  '30/12/1899 '|| TO_CHAR(SYSDATE, 'HH24:mi:ss') FROM DUAL")) {

                String aux = rs.next() ? rs.getString(1) : null;
                rta = SysmanFunciones.convertirAHora(aux);
            }

        }
        catch (SQLException | ParseException ex) {
            Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE, null,
                            ex);
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(Acciones.class.getName()).log(Level.SEVERE,
                                null, ex);
            }
        }
        return rta;
    }

    public static String clobToString(Clob cl)
                    throws IOException, SQLException {
        if (cl == null) {
            return "";
        }
        StringBuilder strOut = new StringBuilder();
        String aux;
        BufferedReader br = new BufferedReader(cl.getCharacterStream());
        while ((aux = br.readLine()) != null) {
            strOut.append(aux);
        }
        return strOut.toString();
    }

    public static String clobToStringEspacio(Clob cl)
                    throws IOException, SQLException {
        if (cl == null) {
            return "";
        }
        StringBuilder strOut = new StringBuilder();
        String aux;
        BufferedReader br = new BufferedReader(cl.getCharacterStream());
        while ((aux = br.readLine()) != null) {
            strOut.append(aux + " ");
        }
        return strOut.toString();
    }

    public static String clobToStringSalto(Clob cl)
                    throws IOException, SQLException {
        if (cl == null) {
            return "";
        }
        StringBuilder strOut = new StringBuilder();
        String aux;
        BufferedReader br = new BufferedReader(cl.getCharacterStream());
        while ((aux = br.readLine()) != null) {
            strOut.append(aux + "\r\n");
        }
        strOut.delete(strOut.lastIndexOf("\r"), strOut.length());
        return new String(strOut.toString().getBytes(),StandardCharsets.UTF_8);
    }

    public static int eliminarRegistro(String nombreConexion, String tabla,
        String condicion)
                        throws SQLException, NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            cp = new ConectorPool();

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_ELIMINAR
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_ACTUALIZAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp.conectar(nombreConexion);
            logger.info(sql);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(4, tabla);
            cstmt.setString(2, condicion);
            cstmt.registerOutParameter(3, Types.INTEGER);
            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_N
                + condicion + ")");
            cstmt.executeQuery();
            rta = cstmt.getInt(3);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
        return rta;
    }

    public static int eliminarRegistro(ConectorPool cp, String tabla,
        String condicion)
                        throws SQLException, NamingException {

        CallableStatement cstmt = null;
        int rta = -1;
        try {

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_ELIMINAR
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_ST + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_ACTUALIZAR
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cstmt = cp.getConection().prepareCall(sql);
            logger.info(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(4, tabla);
            cstmt.setString(2, condicion);
            cstmt.registerOutParameter(3, Types.INTEGER);
            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ASIGNACION_CONDICION_N
                + condicion + ")");
            cstmt.executeQuery();
            rta = cstmt.getInt(3);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }

        }
        return rta;
    }

    public static int mergeActualiza(String nombreConexion, String tabla,
        String mergeUsing, String mergeEnlace,
        String mergeExiste) throws IllegalAccessException,
                        InstantiationException, ClassNotFoundException,
                        SQLException, NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            cp = new ConectorPool();

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + "     UN_ACCION => 'MM',\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_MERGE
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp.conectar(nombreConexion);

            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(6, tabla);
            cstmt.setString(2, mergeUsing);
            cstmt.setString(3, mergeEnlace);
            cstmt.setString(4, mergeExiste);
            cstmt.registerOutParameter(5, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + "\nUN_ACCION =>'MM'"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_USING_N
                + mergeUsing
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE_N
                + mergeEnlace
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_N
                + mergeExiste + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(5);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }

        return rta;
    }

    public static int mergeActualiza(ConectorPool cp, String tabla,
        String mergeUsing, String mergeEnlace,
        String mergeExiste) throws IllegalAccessException,
                        InstantiationException, ClassNotFoundException,
                        SQLException, NamingException {
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + "     UN_ACCION => 'MM',\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_MERGE
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(6, tabla);
            cstmt.setString(2, mergeUsing);
            cstmt.setString(3, mergeEnlace);
            cstmt.setString(4, mergeExiste);
            cstmt.registerOutParameter(5, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + "\nUN_ACCION =>'MM'"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_USING_N
                + mergeUsing
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE_N
                + mergeEnlace
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_N
                + mergeExiste + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(5);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }
        return rta;
    }

    public static int mergeActuaInserta(String nombreConexion, String tabla,
        String mergeUsing, String mergeEnlace,
        String mergeExiste, String mergeNoExiste)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException, NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            cp = new ConectorPool();

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_INSERTAR_MERGE
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_MERGE
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp.conectar(nombreConexion);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(7, tabla);
            cstmt.setString(2, mergeUsing);
            cstmt.setString(3, mergeEnlace);
            cstmt.setString(4, mergeExiste);
            cstmt.setString(5, mergeNoExiste);
            cstmt.registerOutParameter(6, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ACCION_INSERTAR_MERGE_N
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_USING_N
                + mergeUsing
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE_N
                + mergeEnlace
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_N
                + mergeExiste + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(6);

        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }

        return rta;

    }

    public static int mergeActuaInserta(ConectorPool cp, String tabla,
        String mergeUsing, String mergeEnlace,
        String mergeExiste, String mergeNoExiste)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException, NamingException {
        CallableStatement cstmt = null;
        int rta = -1;
        try {

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_INSERTAR_MERGE
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_MERGE
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(7, tabla);
            cstmt.setString(2, mergeUsing);
            cstmt.setString(3, mergeEnlace);
            cstmt.setString(4, mergeExiste);
            cstmt.setString(5, mergeNoExiste);
            cstmt.registerOutParameter(6, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ACCION_INSERTAR_MERGE_N
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_USING_N
                + mergeUsing
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE_N
                + mergeEnlace
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_N
                + mergeExiste + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(6);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }
        return rta;
    }

    public static int mergeElimina(String nombreConexion, String tabla,
        String mergeUsing, String mergeEnlace,
        String mergeExiste) throws IllegalAccessException,
                        InstantiationException, ClassNotFoundException,
                        SQLException, NamingException {
        ConectorPool cp = null;
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            cp = new ConectorPool();

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + "     UN_ACCION => 'EM',\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_MERGE
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cp.conectar(nombreConexion);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(6, tabla);
            cstmt.setString(2, mergeUsing);
            cstmt.setString(3, mergeEnlace);
            cstmt.setString(4, mergeExiste);
            cstmt.registerOutParameter(5, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + "\nUN_ACCION =>'EM'"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_USING_N
                + mergeUsing
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE_N
                + mergeEnlace
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_N
                + mergeExiste + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(5);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }

        return rta;
    }

    public static int mergeElimina(ConectorPool cp, String tabla,
        String mergeUsing, String mergeEnlace,
        String mergeExiste) throws IllegalAccessException,
                        InstantiationException, ClassNotFoundException,
                        SQLException, NamingException {
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + "     UN_ACCION => 'EM',\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_MERGE
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(6, tabla);
            cstmt.setString(2, mergeUsing);
            cstmt.setString(3, mergeEnlace);
            cstmt.setString(4, mergeExiste);
            cstmt.registerOutParameter(5, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + "\nUN_ACCION =>'EM'"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_USING_N
                + mergeUsing
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE_N
                + mergeEnlace
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_N
                + mergeExiste + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(5);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }

        }
        return rta;
    }

    /**
     * Equivalente a la funcion genConsecutivo en Access, genera un
     * consecutivo de un campo dentro de una tabla bajo una condici�n.
     *
     * @author dmaldonado
     * @param nombreConexion
     * Nombre de la conexion a la base de datos
     * @param tabla
     * Tabla donde se va a generar el consecutivo.
     * @param condicion
     * Condici�n para generar el consecutivo
     * @param campo
     * Campo al cual se le generar� el consecutivo
     * @param consecutivoInicial
     * (Opcional) En caso de que no exista ning�n registro, se tomar�
     * este consecutivo para iniciar la secuencia.
     * @return Numero consecutivo del parametro campo.
     * @throws SQLException
     * @throws NamingException
     */
    public static Long genConsecutivo(String nombreConexion, String tabla,
        String condicion, String campo,
        String consecutivoInicial)
                        throws SQLException, NamingException {
        ConectorPool cp = null;
        PreparedStatement cstmt = null;
        ResultSet rs = null;
        Long rta = -1L;
        try {
            cp = new ConectorPool();
            String condConsecutivo = "";
            if (consecutivoInicial != null) {
                condConsecutivo = ",\n UN_INICIAL => ?";
            }
            String sql = "SELECT PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA =>?, \n"
                + " UN_CRITERIO => ?, \n"
                + " UN_CAMPO => ?"
                + condConsecutivo + ") CONS\n"
                + "FROM DUAL";

            cp.conectar(nombreConexion);
            cstmt = cp.getConection().prepareStatement(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(2, condicion);
            cstmt.setString(3, campo);

            if (consecutivoInicial != null) {
                cstmt.setString(4, consecutivoInicial);
            }

            rs = cstmt.executeQuery();
            rta = rs.next() ? rs.getLong(1) : rta;
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
        return rta;
    }

    /**
     * Equivalente a la funci�n genConsecutivo en Access, genera un
     * consecutivo de un campo dentro de una tabla bajo una condici�n.
     *
     * @author dmaldonado
     * @param cp
     * Conexi�n a la base de datos
     * @param tabla
     * Tabla donde se va a generar el consecutivo.
     * @param condicion
     * Condici�n para generar el consecutivo
     * @param campo
     * Campo al cual se le generar� el consecutivo
     * @param consecutivoInicial
     * (Opcional) En caso de que no exista ning�n registro, se tomar�
     * este consecutivo para iniciar la secuencia.
     * @return Numero consecutivo del parametro campo.
     * @throws SQLException
     * @throws NamingException
     */
    public static Long genConsecutivo(ConectorPool cp, String tabla,
        String condicion, String campo,
        String consecutivoInicial)
                        throws SQLException, NamingException {
        PreparedStatement cstmt = null;
        ResultSet rs = null;
        Long rta = -1L;
        try {
            String condConsecutivo = "";
            if (consecutivoInicial != null) {
                condConsecutivo = ",\n UN_INICIAL => ?";
            }
            String sql = "SELECT PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA =>?, \n"
                + " UN_CRITERIO => ?, \n"
                + " UN_CAMPO => ?"
                + condConsecutivo + ") CONS\n"
                + "FROM DUAL";

            cstmt = cp.getConection().prepareStatement(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(2, condicion);
            cstmt.setString(3, campo);

            if (consecutivoInicial != null) {
                cstmt.setString(4, consecutivoInicial);
            }

            rs = cstmt.executeQuery();
            rta = rs.next() ? rs.getLong(1) : rta;
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (rs != null) {
                rs.close();
            }

        }
        return rta;
    }

    public static String getClobConcatenado(String cadena) {
        if (cadena.length() < 3500) {
            return "'" + cadena + "' ";
        }
        double temp = (cadena.length() / 3000) + 1d;
        int indiadorInicial = 0;
        int longitud = 3500;
        int indicadorFinal = longitud;
        StringBuilder nuevo = new StringBuilder("");
        for (int i = 0; i < temp; i++) {
            nuevo.append("TO_CLOB('"
                + cadena.substring(indiadorInicial, indicadorFinal)
                + "')||");
            indiadorInicial = indicadorFinal;
            indicadorFinal = (indiadorInicial + longitud) > cadena.length()
                ? cadena.length()
                : indiadorInicial + longitud;
        }
        return nuevo.toString().substring(0, nuevo.length() - 2) + ",";
    }

    public static int mergeActuaInserta(ConectorPool cp, String tabla,
        String mergeUsing, String mergeEnlace, Map campos,
        String excluciones)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        CallableStatement cstmt = null;
        int rta = -1;
        try {
            Iterator it = campos.entrySet().iterator();
            StringBuilder nombreCampos = new StringBuilder("");
            StringBuilder nombreCamposUpdate = new StringBuilder("");
            StringBuilder valor = new StringBuilder("");
            String aux = "";
            String nombreCamposCadena;
            String valorCadena;
            String nombreCamposUpdateCadema;
            SimpleDateFormat sdf = new SimpleDateFormat(
                            ConstatesPersistencia.ACME_FORMATO_FECHA_DEFAULT);
            String cadFinal;

            List<String> camposExcluidos = Arrays
                            .asList(excluciones.split(","));
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                if ((e.getValue() != null) && !("" + e.getValue()).isEmpty()) {
                    nombreCampos.append(ConstatesPersistencia.ACME_CAMPO_TABLA
                        + e.getKey() + ",");
                    if (e.getValue() instanceof Date) {
                        valor.append(ConstatesPersistencia.ACME_OPERADOR_TO_DATE
                            + sdf.format(e.getValue())
                            + ConstatesPersistencia.ACME_FORMATO_FECHA_CONCATENADO);
                    }
                    else {
                        cadFinal = e.getValue().toString();
                        if (cadFinal.contains("'")) {
                            cadFinal = cadFinal.replace("'", "''");
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }
                        else {
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }

                    }
                }
                if (!e.getKey().equals(ConstatesPersistencia.ACME_CAMPO_ROWID)
                    && !camposExcluidos.contains(e.getKey())) {
                    if (e.getValue() instanceof Date) {
                        nombreCamposUpdate.append(
                                        ConstatesPersistencia.ACME_CAMPO_TABLA
                                            + e.getKey()
                                            + ConstatesPersistencia.ACME_OPERADOR_TO_DATE_IGUAL
                                            + sdf.format(e.getValue())
                                            + ConstatesPersistencia.ACME_FORMATO_FECHA_CONCATENADO);
                    }
                    else if ((campos.get(e.getKey()) != null)
                        && !campos.get(e.getKey()).toString()
                                        .isEmpty()
                        && campos.get(e.getKey()).toString()
                                        .contains("'")) {
                        aux = campos.get(e.getKey()).toString().replace("'",
                                        "''");
                        aux = aux.length() > 3500 ? getClobConcatenado(aux)
                            : "'" + aux + "',";
                        nombreCamposUpdate.append(
                                        ConstatesPersistencia.ACME_CAMPO_TABLA
                                            + e.getKey() + "= "
                                            + aux);

                    }
                    else {
                        if (e.getValue() != null) {
                            aux = e.getValue().toString().length() > 3500
                                ? getClobConcatenado(e.getValue()
                                                .toString())
                                : "'" + e.getValue() + "',";
                        }
                        else {
                            aux = "'" + e.getValue() + "',";
                        }
                        nombreCamposUpdate.append(
                                        ConstatesPersistencia.ACME_CAMPO_TABLA
                                            + e.getKey() + "= "
                                            + aux);
                    }
                }
            }

            nombreCamposUpdateCadema = nombreCamposUpdate.toString().substring(
                            0,
                            nombreCamposUpdate.length() - 1);
            nombreCamposUpdateCadema = nombreCamposUpdateCadema.replace(
                            ConstatesPersistencia.ACME_VARIABLE_NULL, "null");
            nombreCamposUpdateCadema = nombreCamposUpdateCadema.replace(
                            ConstatesPersistencia.ACME_VARIABLE_NULL_MAYUS,
                            "null");
            nombreCamposUpdateCadema = nombreCamposUpdateCadema.replace(
                            ConstatesPersistencia.ACME_VARIABLE_TRUE, "-1");
            nombreCamposUpdateCadema = nombreCamposUpdateCadema.replace(
                            ConstatesPersistencia.ACME_VARIABLE_FALSE, "0");

            nombreCamposCadena = nombreCampos.toString().substring(0,
                            nombreCampos.length() - 1);
            valorCadena = valor.toString().substring(0, valor.length() - 1);
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_TRUE,
                            "-1");
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_FALSE,
                            "0");

            String mergeNoExiste = "INSERT(" + nombreCamposCadena + ") VALUES ("
                + valorCadena + ")";
            String mergeExiste = "UPDATE SET " + nombreCamposCadena;

            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_INSERTAR_MERGE
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_MERGE
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";

            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(7, tabla);
            cstmt.setString(2, mergeUsing);
            cstmt.setString(3, mergeEnlace);
            cstmt.setString(4, mergeExiste);
            cstmt.setString(5, mergeNoExiste);
            cstmt.registerOutParameter(6, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ACCION_INSERTAR_MERGE_N
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_USING_N
                + mergeUsing
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE_N
                + mergeEnlace
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_N
                + mergeExiste + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(6);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }
        return rta;
    }

    public static int mergeActuaInserta(String nombreConexion, String tabla,
        String mergeUsing, String mergeEnlace, Map campos,
        String excluciones)
                        throws IllegalAccessException, InstantiationException,
                        ClassNotFoundException, SQLException,
                        NamingException {
        CallableStatement cstmt = null;
        ConectorPool cp = null;
        int rta = -1;
        try {
            cp = new ConectorPool();
            Iterator it = campos.entrySet().iterator();
            StringBuilder nombreCampos = new StringBuilder("");
            String nombreCamposCadena;

            StringBuilder nombreCamposUpdate = new StringBuilder("");
            String nombreCamposUpdateCadena;

            StringBuilder valor = new StringBuilder("");
            String valorCadena;
            String aux = "";
            SimpleDateFormat sdf = new SimpleDateFormat(
                            ConstatesPersistencia.ACME_FORMATO_FECHA_DEFAULT);
            String cadFinal;
            List<String> camposExcluidos = Arrays
                            .asList(excluciones.split(","));
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry) it.next();
                if ((e.getValue() != null) && !("" + e.getValue()).isEmpty()) {
                    nombreCampos.append(ConstatesPersistencia.ACME_CAMPO_TABLA
                        + e.getKey() + ",");
                    if (e.getValue() instanceof Date) {
                        valor.append(ConstatesPersistencia.ACME_OPERADOR_TO_DATE
                            + sdf.format(e.getValue())
                            + ConstatesPersistencia.ACME_FORMATO_FECHA_CONCATENADO);
                    }
                    else {
                        cadFinal = e.getValue().toString();
                        if (cadFinal.contains("'")) {
                            cadFinal = cadFinal.replace("'", "''");
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }
                        else {
                            valor.append(cadFinal.length() > 3500
                                ? getClobConcatenado(cadFinal)
                                : "'" + cadFinal + "',");
                        }

                    }
                }
                if (!e.getKey().equals(ConstatesPersistencia.ACME_CAMPO_ROWID)
                    && !camposExcluidos.contains(e.getKey())) {
                    if (e.getValue() instanceof Date) {
                        nombreCamposUpdate.append(
                                        ConstatesPersistencia.ACME_CAMPO_TABLA
                                            + e.getKey()
                                            + ConstatesPersistencia.ACME_OPERADOR_TO_DATE_IGUAL
                                            + sdf.format(e.getValue())
                                            + ConstatesPersistencia.ACME_FORMATO_FECHA_CONCATENADO);
                    }
                    else if ((campos.get(e.getKey()) != null)
                        && !campos.get(e.getKey()).toString()
                                        .isEmpty()
                        && campos.get(e.getKey()).toString()
                                        .contains("'")) {
                        aux = campos.get(e.getKey()).toString().replace("'",
                                        "''");
                        aux = aux.length() > 3500 ? getClobConcatenado(aux)
                            : "'" + aux + "',";
                        nombreCamposUpdate.append(
                                        ConstatesPersistencia.ACME_CAMPO_TABLA
                                            + e.getKey() + "= "
                                            + aux);

                    }
                    else {
                        if (e.getValue() != null) {
                            aux = e.getValue().toString().length() > 3500
                                ? getClobConcatenado(e.getValue()
                                                .toString())
                                : "'" + e.getValue() + "',";
                        }
                        else {
                            aux = "'" + e.getValue() + "',";
                        }
                        nombreCamposUpdate.append(
                                        ConstatesPersistencia.ACME_CAMPO_TABLA
                                            + e.getKey() + "= "
                                            + aux);
                    }
                }
            }

            nombreCamposUpdateCadena = nombreCamposUpdate.toString().substring(
                            0,
                            nombreCamposUpdate.length() - 1);
            nombreCamposUpdateCadena = nombreCamposUpdateCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_NULL, "null");
            nombreCamposUpdateCadena = nombreCamposUpdateCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_NULL_MAYUS,
                            "null");
            nombreCamposUpdateCadena = nombreCamposUpdateCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_TRUE, "-1");
            nombreCamposUpdateCadena = nombreCamposUpdateCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_FALSE, "0");

            nombreCamposCadena = nombreCampos.toString().substring(0,
                            nombreCampos.length() - 1);
            valorCadena = valor.toString().substring(0, valor.length() - 1);
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_TRUE,
                            "-1");
            valorCadena = valorCadena.replace(
                            ConstatesPersistencia.ACME_VARIABLE_FALSE,
                            "0");

            String mergeNoExiste = "INSERT(" + nombreCamposCadena + ") VALUES ("
                + valorCadena + ")";
            String mergeExiste = "UPDATE SET " + nombreCamposUpdateCadena;
            String sql = ConstatesPersistencia.ACME_DECLARE
                + ConstatesPersistencia.ACME_OPERADOR_BEGIN
                + ConstatesPersistencia.ACME_ASIGNACION_RTA + " ?,\n"
                + ConstatesPersistencia.ACME_ACCION_INSERTAR_MERGE
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE + " ?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE + "?,\n"
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_NO_EXISTE_CIERRE
                + "?);\n"
                + "     ?" + ConstatesPersistencia.ACME_RTA
                + ConstatesPersistencia.ACME_EXCEPCION_MERGE
                + ConstatesPersistencia.RAISE_WITH_MSG + "?);\n"
                + "END;";
            cp.conectar(nombreConexion);
            cstmt = cp.getConection().prepareCall(sql);
            cstmt.setString(1, tabla);
            cstmt.setString(7, tabla);
            cstmt.setString(2, mergeUsing);
            cstmt.setString(3, mergeEnlace);
            cstmt.setString(4, mergeExiste);
            cstmt.setString(5, mergeNoExiste);
            cstmt.registerOutParameter(6, Types.INTEGER);

            logger.info(ConstatesPersistencia.ACME_LLAMADO_ACME + tabla
                + ConstatesPersistencia.ACME_ACCION_INSERTAR_MERGE_N
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_USING_N
                + mergeUsing
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_ENLACE_N
                + mergeEnlace
                + ConstatesPersistencia.ACME_ASIGNACION_MERGE_EXISTE_N
                + mergeExiste + ")");

            cstmt.executeQuery();
            rta = cstmt.getInt(6);
        }
        finally {
            if (cstmt != null) {
                cstmt.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
        return rta;
    }

    public static String getLlave(String conexion, String tabla)
                    throws NamingException, SQLException {
        ConectorPool cp = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        String rta = null;
        try {
            cp = new ConectorPool();
            cp.conectar(conexion);
            String sql = "SELECT LISTAGG(UCC.COLUMN_NAME ,',') WITHIN GROUP (ORDER BY POSITION) LLAVE\n"
                + " FROM\n"
                + "USER_CONSTRAINTS UC INNER JOIN USER_CONS_COLUMNS UCC \n"
                + "ON(UC.CONSTRAINT_NAME=UCC.CONSTRAINT_NAME)\n"
                + "WHERE CONSTRAINT_TYPE='P' \n" +
                "AND UC.TABLE_NAME=?";
            st = cp.getConection().prepareStatement(sql);
            st.setString(1, tabla);
            rs = st.executeQuery();
            if (rs.next()) {
                rta = rs.getString(ConstatesPersistencia.ACME_CAMPO_LLAVE);
            }

        }
        finally {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
        return rta;
    }

    public static void getLlave(String conexion, String tabla,
        Map<String, String[]> llaves)
                        throws NamingException, SQLException {
        ConectorPool cp = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            cp = new ConectorPool();
            cp.conectar(conexion);
            String sql = "SELECT LISTAGG(UCC.COLUMN_NAME ,',') WITHIN GROUP (ORDER BY POSITION) LLAVE\n"
                +
                "FROM \n" +
                "USER_CONSTRAINTS UC INNER JOIN USER_CONS_COLUMNS UCC\n"
                +
                "ON(UC.CONSTRAINT_NAME=UCC.CONSTRAINT_NAME) \n"
                +
                "WHERE CONSTRAINT_TYPE='P'\n" +
                "AND UC.TABLE_NAME=?";

            st = cp.getConection().prepareStatement(sql);
            st.setString(1, tabla);
            rs = st.executeQuery();
            if (rs.next()) {
                llaves.put(conexion + "." + tabla,
                                rs.getString(ConstatesPersistencia.ACME_CAMPO_LLAVE)
                                                .split(","));
            }

        }
        finally {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
    }

    public static void getLlaves(String conexion, Map<String, String> llaves)
                    throws NamingException, SQLException {
        ConectorPool cp = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            cp = new ConectorPool();
            cp.conectar(conexion);
            st = cp.getConection().createStatement();
            rs = st.executeQuery(
                            "SELECT UC.TABLE_NAME TABLA , LISTAGG(UCC.COLUMN_NAME ,',') WITHIN GROUP (ORDER BY POSITION) LLAVE\n"
                                +
                                "FROM \n" +
                                "USER_CONSTRAINTS UC INNER JOIN USER_CONS_COLUMNS UCC\n"
                                +
                                "ON(UC.CONSTRAINT_NAME=UCC.CONSTRAINT_NAME) \n"
                                +
                                "WHERE CONSTRAINT_TYPE='P'\n" +
                                "GROUP BY UC.TABLE_NAME");
            while (rs.next()) {
                llaves.put(conexion + "." + rs.getString("TABLA"),
                                rs.getString(ConstatesPersistencia.ACME_CAMPO_LLAVE));
            }

        }
        finally {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (cp != null) {
                cp.getConection().close();
            }
        }
    }

}
