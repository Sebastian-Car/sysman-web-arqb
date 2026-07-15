/*
 * FrmseleccionarprocesosjudicialesControladorUrlEnum
 *
 * 1.0
 *
 * 04/07/2024
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmseleccionarprocesosjudicialesControladorUrlEnum {

    URL1935005("FRMSELECCIONARPROCESOSJUDICIALESCONTROLADORURL1935005", "1935005");

    private final String key;
    private final String value;

    private FrmseleccionarprocesosjudicialesControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
