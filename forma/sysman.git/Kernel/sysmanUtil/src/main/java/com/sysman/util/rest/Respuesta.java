/*-
 * Respuesta.java
 *
 * 1.0
 * 
 * 26/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Modelo para la respuesta que genera el autoservicio.
 * 
 * @version 1.0, 25/05/2018
 * @author jgomez
 *
 */
@XmlRootElement
public class Respuesta {

    private Long consecutivo;
    /**
     * Formato del archivo
     */
    private String formato;
    /**
     * Nombre de la ruta abstracta en donde queda alojado el archivo.
     */
    private String archivo;

    /**
     * @return the consecutivo
     */
    public Long getConsecutivo() {
        return consecutivo;
    }

    /**
     * @param consecutivo
     * the consecutivo to set
     */
    public void setConsecutivo(Long consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * @return the formato
     */
    public String getFormato() {
        return formato;
    }

    /**
     * @param formato
     * the formato to set
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /**
     * Obtiene la ruta abstracta al archivo generado.
     * 
     * @return ruta relativa del archivo.
     */
    public String getArchivo() {
        return archivo;
    }

    /**
     * Configura la ruta abstracta al archivo generado.
     * 
     * @param ruta
     * relativa del archivo
     */
    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

}
