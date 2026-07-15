/*
 * FrmModificacionEstratosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmModificacionEstratosControladorUrlEnum {

    URL5277("FRMMODIFICACIONESTRATOSCONTROLADORURL5277", "367015"),

    URL4455("FRMMODIFICACIONESTRATOSCONTROLADORURL4455", "367013");

    private final String key;
    private final String value;

    private FrmModificacionEstratosControladorUrlEnum(String key,
        String value) {
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
