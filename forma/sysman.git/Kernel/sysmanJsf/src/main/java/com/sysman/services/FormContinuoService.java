/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.services;

import com.sysman.dao.Registro;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

/**
 *
 * @author cmanrique
 */
public class FormContinuoService implements Serializable {

    private static FormContinuoService instance;

    private static final String SERVICIO_CONTEO_CONSULTA = "-3001";

    /**
     * Creates a new instance of FormContinuoService
     */
    private FormContinuoService() {
        //
    }

    public static FormContinuoService getInstance() {
        if (instance == null) {
            instance = new FormContinuoService();

        }
        return instance;
    }

    /**
     * Consume un servicio de utilidad que realiza el conteo de
     * registros que trae una consulta ya armada
     * 
     * @param sql
     * @return
     * @throws SystemException
     */
    public long getConteoConsulta(String sql) throws SystemException {
        RequestManager rq = new RequestManager();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("SQL", sql);
        return Long.parseLong(rq.get(UrlServiceUtil
                        .getUrlBeanById(SERVICIO_CONTEO_CONSULTA)
                        .getUrl(),
                        parametros).getFields().get("TOTAL").toString());
    }

    public List<Registro> getListado(String conexion, String sql) {
        List<Registro> lista = new ArrayList<>();
        ConectorPool c = new ConectorPool();
        Statement st = null;
        int indice = 0;
        try {
            if (lista.isEmpty()) {

                c.conectar(conexion);

                st = c.getConection().createStatement();
                try (ResultSet rs = st.executeQuery(sql)) {
                    int col = rs.getMetaData().getColumnCount();

                    HashMap<String, Object> aux;
                    while (rs.next()) {
                        aux = new HashMap<>();
                        for (int i = 1; i <= col; i++) {
                            if (rs.getMetaData().getColumnName(i)
                                            .equals("ROWID")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getString(i));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("NUMBER")
                                && (rs.getMetaData().getPrecision(i) == 1)
                                && (rs.getMetaData().getScale(i) == 0)) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                !rs.getString(i).equals("0"));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("CLOB")) {
                                //aux.put(rs.getMetaData().getColumnName(i),
                                //                Acciones
                                //                                .clobToStringSalto(
                                //                                                rs.getClob(i)));
                                
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getString(i));
                                
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("DATE")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getDate(i));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .startsWith("TIMESTAMP")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getTimestamp(i));
                            }

                            else {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getObject(i));
                            }
                        }

                        lista.add(new Registro(indice, aux));
                        indice++;

                    }
                }
            }
        }
        catch (SQLException | NamingException ex) {
            Logger.getLogger(FormContinuoService.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                c.getConection().close();
                if (st != null) {
                    st.close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(FormContinuoService.class.getName())
                                .log(Level.SEVERE, null, ex);

            }
        }
        return lista;
    }

    public List<Registro> getListado(String conexion, String sql,
        String[] llave) {
        List<Registro> lista = new ArrayList<>();
        ConectorPool c = new ConectorPool();
        Statement st = null;
        int indice = 0;
        try {
            if (lista.isEmpty()) {

                c.conectar(conexion);

                st = c.getConection().createStatement();
                try (ResultSet rs = st.executeQuery(sql)) {
                    int col = rs.getMetaData().getColumnCount();
                    Registro tempReg;
                    HashMap<String, Object> aux;
                    while (rs.next()) {
                        aux = new HashMap<>();
                        for (int i = 1; i <= col; i++) {
                            if (rs.getMetaData().getColumnName(i)
                                            .equals("ROWID")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getString(i));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("NUMBER")
                                && (rs.getMetaData().getPrecision(i) == 1)
                                && (rs.getMetaData().getScale(i) == 0)) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                !rs.getString(i).equals("0"));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("CLOB")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                Acciones
                                                                .clobToStringSalto(
                                                                                rs.getClob(i)));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .startsWith("TIMESTAMP")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getTimestamp(i));
                            }
                            else {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getObject(i));
                            }
                        }

                        tempReg = new Registro(indice, aux);
                        tempReg.asignarLlaveOLD(llave);
                        lista.add(tempReg);
                        indice++;

                    }
                }
            }
        }
        catch (SQLException | NamingException | IOException ex) {
            Logger.getLogger(FormContinuoService.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                c.getConection().close();
                if (st != null) {
                    st.close();
                }

            }
            catch (SQLException ex) {
                Logger.getLogger(FormContinuoService.class.getName())
                                .log(Level.SEVERE, null, ex);

            }
        }
        return lista;
    }

    public List<Registro> getListadoCadena(String conexion, String sql) {
        List<Registro> lista = new ArrayList<>();
        ConectorPool c = new ConectorPool();
        Statement st = null;
        int indice = 0;
        try {
            if (lista.isEmpty()) {

                c.conectar(conexion);

                st = c.getConection().createStatement();
                try (ResultSet rs = st.executeQuery(sql)) {
                    int col = rs.getMetaData().getColumnCount();

                    HashMap<String, Object> aux;
                    while (rs.next()) {
                        aux = new HashMap<>();
                        for (int i = 1; i <= col; i++) {
                            if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("CLOB")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                Acciones
                                                                .clobToString(rs.getClob(
                                                                                i)));
                            }
                            else {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getString(i));
                            }
                        }

                        lista.add(new Registro(indice, aux));
                        indice++;

                    }
                }
            }
        }
        catch (SQLException | NamingException | IOException ex) {
            Logger.getLogger(FormContinuoService.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                c.getConection().close();
                if (st != null) {
                    st.close();
                }

            }
            catch (SQLException ex) {
                Logger.getLogger(FormContinuoService.class.getName())
                                .log(Level.SEVERE, null, ex);

            }
        }
        return lista;
    }

    public List<String> getCamposListado(String conexion, String sql) {
        List<String> lista = new ArrayList<>();
        ConectorPool c = new ConectorPool();
        Statement st = null;
        try {
            c.conectar(conexion);
            st = c.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(sql)) {
                int col = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= col; i++) {
                    lista.add(rs.getMetaData().getColumnName(i));
                }

            }
        }
        catch (SQLException | NamingException ex) {
            Logger.getLogger(FormContinuoService.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                c.getConection().close();
                if (st != null) {
                    st.close();
                }

            }
            catch (SQLException ex) {
                Logger.getLogger(FormContinuoService.class.getName())
                                .log(Level.SEVERE, null, ex);

            }
        }
        return lista;
    }

    public List<Registro> getListado(ConectorPool c, String sql) {
        List<Registro> lista = new ArrayList<>();
        Statement st = null;
        int indice = 0;
        try {
            if (lista.isEmpty()) {
                st = c.getConection().createStatement();
                try (ResultSet rs = st.executeQuery(sql)) {
                    int col = rs.getMetaData().getColumnCount();

                    HashMap<String, Object> aux;
                    while (rs.next()) {
                        aux = new HashMap<>();
                        for (int i = 1; i <= col; i++) {
                            if (rs.getMetaData().getColumnName(i)
                                            .equals("ROWID")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getString(i));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("NUMBER")
                                && (rs.getMetaData().getPrecision(i) == 1)
                                && (rs.getMetaData().getScale(i) == 0)) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                !rs.getString(i).equals("0"));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("CLOB")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                Acciones
                                                                .clobToString(rs.getClob(
                                                                                i)));
                            }
                            else {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getObject(i));
                            }
                        }

                        lista.add(new Registro(indice, aux));
                        indice++;
                    }
                }
            }
        }
        catch (SQLException | IOException ex) {
            Logger.getLogger(FormContinuoService.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                if (st != null) {
                    st.close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(FormContinuoService.class.getName())
                                .log(Level.SEVERE, null, ex);

            }
        }
        return lista;
    }

    public Registro getRegistro(String conexion, String sql) {
        ConectorPool con = new ConectorPool();
        Registro registro = null;
        Statement st = null;
        String valor = "";
        try {
            con.conectar(conexion);
            st = con.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    int numCol = rs.getMetaData().getColumnCount();
                    registro = new Registro();
                    HashMap<String, Object> campos = new HashMap<>();
                    for (int i = 1; i <= numCol; i++) {
                        if (rs.getMetaData().getColumnTypeName(i)
                                        .equals("DATE")) {
                            campos.put(rs.getMetaData().getColumnName(i),
                                            rs.getDate(i));
                        }
                        else if (rs.getMetaData().getColumnTypeName(i)
                                        .startsWith("TIMESTAMP")) {
                            campos.put(rs.getMetaData().getColumnName(i),
                                            rs.getTimestamp(i));
                        }

                        else if (rs.getMetaData().getColumnTypeName(i)
                                        .equals("CLOB")) {
                            campos.put(rs.getMetaData().getColumnName(i),
                                            Acciones
                                                            .clobToStringSalto(
                                                                            rs.getClob(i)));
                        }
                        else {
                            valor = rs.getString(i);
                            if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("NUMBER")
                                && (valor != null)
                                && (valor.length() > 15)) {
                                campos.put(rs.getMetaData().getColumnName(i),
                                                new BigDecimal(valor));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("NUMBER")
                                && (valor != null)
                                && valor.startsWith(".")) {
                                campos.put(rs.getMetaData().getColumnName(i),
                                                String.valueOf(Double
                                                                .parseDouble(
                                                                                valor)));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("NUMBER")
                                && (valor != null)
                                && (rs.getMetaData().getPrecision(i) == 1)
                                && (rs.getMetaData().getScale(i) == 0)) {
                                campos.put(rs.getMetaData().getColumnName(i),
                                                !valor.equals("0"));
                            }
                            else {
                                campos.put(rs.getMetaData().getColumnName(i),
                                                valor);
                            }
                        }
                    }
                    registro.setCampos(campos);
                }
            }
        }
        catch (SQLException | NamingException | IOException ex) {
            Logger.getLogger(FormContinuoService.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                con.getConection().close();
                if (st != null) {
                    st.close();
                }
            }
            catch (SQLException ex) {
                Logger.getLogger(FormContinuoService.class.getName())
                                .log(Level.SEVERE, null, ex);

            }
        }
        return registro;
    }

    public Registro getRegistro(ConectorPool con, String sql) {

        Registro registro = null;
        Statement st = null;
        String valor = "";
        try {
            st = con.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(sql)) {
                int numCol = rs.getMetaData().getColumnCount();
                if (rs.next()) {
                    registro = new Registro();
                    HashMap<String, Object> campos = new HashMap<>();
                    for (int i = 1; i <= numCol; i++) {
                        if (rs.getMetaData().getColumnTypeName(i)
                                        .equals("DATE")) {
                            campos.put(rs.getMetaData().getColumnName(i),
                                            rs.getDate(i));
                        }
                        else if (rs.getMetaData().getColumnTypeName(i)
                                        .startsWith("TIMESTAMP")) {
                            campos.put(rs.getMetaData().getColumnName(i),
                                            rs.getTimestamp(i));
                        }
                        else if (rs.getMetaData().getColumnTypeName(i)
                                        .equals("CLOB")) {
                            campos.put(rs.getMetaData().getColumnName(i),
                                            Acciones
                                                            .clobToStringSalto(
                                                                            rs.getClob(i)));
                        }
                        else {
                            valor = rs.getString(i);
                            if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("NUMBER")
                                && (valor != null)
                                && (valor.length() > 15)) {
                                campos.put(rs.getMetaData().getColumnName(i),
                                                new BigDecimal(valor));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("NUMBER")
                                && (valor != null)
                                && valor.startsWith(".")) {
                                campos.put(rs.getMetaData().getColumnName(i),
                                                String.valueOf(Double
                                                                .parseDouble(
                                                                                valor)));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("NUMBER")
                                && (valor != null)
                                && (rs.getMetaData().getPrecision(i) == 1)
                                && (rs.getMetaData().getScale(i) == 0)) {
                                campos.put(rs.getMetaData().getColumnName(i),
                                                !valor.equals("0"));
                            }
                            else {
                                campos.put(rs.getMetaData().getColumnName(i),
                                                valor);
                            }
                        }
                    }
                    registro.setCampos(campos);
                }
            }
        }
        catch (SQLException | IOException ex) {
            Logger.getLogger(FormContinuoService.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {

                if (st != null) {
                    st.close();
                }

            }
            catch (SQLException ex) {
                Logger.getLogger(FormContinuoService.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
        return registro;
    }

    public Registro getRegistro(ConectorPool con, String sql, String tabla,
        Map<String, Object> llave) {

        Registro registro = null;
        Statement st = null;
        StringBuilder valor = null;
        try {
            String condicion = Acciones.generarNombresCampos(llave,
                            " AND " + tabla + ".");
            st = con.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(
                            sql + " WHERE  " + tabla + "." + condicion)) {
                int numCol = rs.getMetaData().getColumnCount();
                if (rs.next()) {
                    registro = new Registro();
                    Map<String, Object> campos = new HashMap<>();
                    llenarCampos(numCol, valor, campos, rs);
                    registro.setCampos(campos);
                    registro.setLlave(llave);
                }
            }
        }
        catch (SQLException | IOException ex) {
            Logger.getLogger(FormContinuoService.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {

                if (st != null) {
                    st.close();
                }

            }
            catch (SQLException ex) {
                Logger.getLogger(FormContinuoService.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
        return registro;
    }

    public Registro getRegistro(String conexion, String sql, String tabla,
        Map<String, Object> llave) {

        Registro registro = null;
        Statement st = null;
        StringBuilder valor = null;
        ConectorPool con = null;
        try {
            con = new ConectorPool();
            String condicion = Acciones.generarNombresCampos(llave,
                            " AND " + tabla + ".");
            con.conectar(conexion);
            st = con.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(
                            sql + " WHERE  " + tabla + "." + condicion)) {
                int numCol = rs.getMetaData().getColumnCount();
                if (rs.next()) {
                    registro = new Registro();
                    Map<String, Object> campos = new HashMap<>();
                    llenarCampos(numCol, valor, campos, rs);
                    registro.setCampos(campos);
                    registro.setLlave(llave);

                }
            }
        }
        catch (SQLException | IOException | NamingException ex) {
            Logger.getLogger(FormContinuoService.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                if (con != null) {
                    con.getConection().close();
                }

                if (st != null) {
                    st.close();
                }

            }
            catch (SQLException ex) {
                Logger.getLogger(FormContinuoService.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
        return registro;
    }

    private void llenarCampos(int numCol, StringBuilder valor,
        Map<String, Object> campos, ResultSet rs)
                    throws SQLException,
                    IOException {
        for (int i = 1; i <= numCol; i++) {
            if (rs.getMetaData().getColumnTypeName(i).equals("DATE")) {
                campos.put(rs.getMetaData().getColumnName(i), rs.getDate(i));
            }
            else if (rs.getMetaData().getColumnTypeName(i)
                            .startsWith("TIMESTAMP")) {
                campos.put(rs.getMetaData().getColumnName(i),
                                rs.getTimestamp(i));
            }
            else if (rs.getMetaData().getColumnTypeName(i).equals("CLOB")) {
                campos.put(rs.getMetaData().getColumnName(i),
                                Acciones.clobToStringSalto(rs.getClob(i)));
            }
            else {
                if (rs.getString(i) != null) {
                    valor = new StringBuilder(rs.getString(i));
                    if (rs.getMetaData().getColumnTypeName(i).equals("NUMBER")
                        && (valor.length() > 15)) {
                        campos.put(rs.getMetaData().getColumnName(i),
                                        new BigDecimal(valor.toString()));
                    }
                    else if (rs.getMetaData().getColumnTypeName(i)
                                    .equals("NUMBER")
                        && (valor.indexOf(".") == 0)) {
                        campos.put(rs.getMetaData().getColumnName(i),
                                        String.valueOf(Double.parseDouble(
                                                        valor.toString())));
                    }
                    else if (rs.getMetaData().getColumnTypeName(i)
                                    .equals("NUMBER")
                        && (rs.getMetaData().getPrecision(i) == 1)
                        && (rs.getMetaData().getScale(i) == 0)) {
                        campos.put(rs.getMetaData().getColumnName(i),
                                        !valor.toString().equals("0"));
                    }
                    else {
                        campos.put(rs.getMetaData().getColumnName(i),
                                        valor.toString());
                    }
                }
                else {
                    campos.put(rs.getMetaData().getColumnName(i), null);
                }
            }
        }
    }

    public String buscarEnLista(String id, String key, String value,
        List<Registro> lista) {
        String rta = null;
        for (Registro reg : lista) {
            if (reg.getCampos().get(key).toString().equals(id)) {
                if (reg.getCampos().get(value) == null) {
                    return null;
                }
                rta = reg.getCampos().get(value).toString();
                break;
            }
        }
        return rta;
    }

    public Object buscarEnListaObj(String id, String key, String value,
        List<Registro> lista) {
        Object rta = null;
        for (Registro reg : lista) {
            if (reg.getCampos().get(key).toString().equals(id)) {
                if (reg.getCampos().get(value) == null) {
                    return null;
                }
                rta = reg.getCampos().get(value);
                break;
            }
        }
        return rta;
    }

}
