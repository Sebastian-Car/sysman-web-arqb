/*
 * FrmmantenimientoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmmantenimientoControladorUrlEnum {

    URL3648("FRMMANTENIMIENTOCONTROLADORURL3648", "214054"),

    URL3649("FRMMANTENIMIENTOCONTROLADORURL3648", "276005");

    private final String key;
    private final String value;

    private FrmmantenimientoControladorUrlEnum(String key, String value) {
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
