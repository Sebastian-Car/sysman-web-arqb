/*-
 * ParametrosCargos.java
 *
 * 1.0
 * 
 * 4/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * Clase que administra los parametros del servicio de convertir word
 * a pdf
 * 
 * @version 1.0, 11/02/2021
 * @author eamaya
 *
 */
public class ParametrosConvertirPdf {

    private String nombreArchivo;

    private String archivoBase64;

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getArchivoBase64() {
        return archivoBase64;
    }

    public void setArchivoBase64(String archivoBase64) {
        this.archivoBase64 = archivoBase64;
    }

}
