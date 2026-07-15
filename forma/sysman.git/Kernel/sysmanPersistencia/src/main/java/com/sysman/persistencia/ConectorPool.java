/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.persistencia;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author cmanrique
 */
public class ConectorPool {

    public static final String ESQUEMA_SYSMAN = "migradasource";
    public static final String ESQUEMA_SYSMANK = "sysmanksource";
    public static final String ESQUEMA_SYSMANAUDITORIA = "sysmanauditoria";
    public static final String ESQUEMA_INTERFAZBUCA = "interfazbuca";
    public static final String ESQUEMA_MGA = "mgasource";

    private Connection conection;

    private DataSource getDasource(String nombre) throws NamingException {
        Context c = new InitialContext();
        return (DataSource) c.lookup("java:/env/" + nombre);
    }

    public void conectar(String nombre) throws NamingException, SQLException {
        if (conection == null || conection.isClosed()) {
            conection = getDasource(nombre).getConnection();
            inicializar();
        }

    }

    public Connection getConection() {
        return conection;
    }

    private void inicializar() throws SQLException {
        if (("ORACLE").equalsIgnoreCase(
                        conection.getMetaData().getDatabaseProductName())) {
            Statement st = conection.createStatement();
            st.executeUpdate("ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '.,'");
            st.executeUpdate(
                            "ALTER SESSION SET NLS_DATE_FORMAT = 'DD/MM/YYYY'");
            st.close();
        }
    }

}
