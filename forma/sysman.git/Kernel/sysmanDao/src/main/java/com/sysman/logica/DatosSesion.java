/*-
 * DatosSesion.java
 *
 * 1.0
 * 
 * 24/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.logica;

/**
 * POJO que contiene los datos que se cargan al inicio de
 * sesi&oacute;n, esto para simular la creaci&oacute;n de variables de
 * sesi&oacute;n.
 *
 * @version 1.0, 24/05/2018
 * @author jrodrigueza
 *
 */
public class DatosSesion {
    /**
     * guarda el codigo del usuario
     */
    private String usuario;
    /**
     * guarda el código de la compania
     */
    private String compania;
    /**
     * guarda los datos de la compania con la que se ingresa
     */
    private Compania companiaIngreso;
    /**
     * atributo que guarda el modulo sobre el cual se trabaja
     */
    private String modulo;
    /**
     * Objeto que contiene los datos del usuario
     */
    private Usuario user;

    /**
     * Identifica si los reportes exportados a excel salen desde la
     * consulta y o desde el reporte
     */
    private String excelPlano;

    /**
     * Constructor sin parametros
     */
    public DatosSesion() {
        // constructor vacio
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario
     * the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }

    /**
     * @param compania
     * the compania to set
     */
    public void setCompania(String compania) {
        this.compania = compania;
    }

    /**
     * @return the companiaIngreso
     */
    public Compania getCompaniaIngreso() {
        return companiaIngreso;
    }

    /**
     * @param companiaIngreso
     * the companiaIngreso to set
     */
    public void setCompaniaIngreso(Compania companiaIngreso) {
        this.companiaIngreso = companiaIngreso;
    }

    /**
     * 
     * @return modulo
     */
    public String getModulo() {
        return modulo;
    }

    /**
     * 
     * @param modulo
     */
    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    /**
     * 
     * @return el objeto usuario
     */
    public Usuario getUser() {
        return user;
    }

    /**
     * inicializa el objeto usuario
     * 
     * @param user
     */
    public void setUser(Usuario user) {
        this.user = user;
    }

    /**
     * 
     * @return el parametro que indica si los reportes con formato
     * excel salen desde el reporte o son planos
     */
    public String getExcelPlano() {
        return excelPlano;
    }

    /**
     * Inicializar con el parametro
     * 
     * @param excelPlano
     */
    public void setExcelPlano(String excelPlano) {
        this.excelPlano = excelPlano;
    }

}
