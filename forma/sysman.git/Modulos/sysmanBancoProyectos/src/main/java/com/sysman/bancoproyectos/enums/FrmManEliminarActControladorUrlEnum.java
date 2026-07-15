/*
 * FrmManEliminarActControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmManEliminarActControladorUrlEnum {

    URL4779("FRMMANELIMINARACTCONTROLADORURL4779",
                    "32003"),

    URL5792("FRMMANELIMINARACTCONTROLADORURL5792",
                    "513012");

    private final String key;
    private final String value;

    private FrmManEliminarActControladorUrlEnum(String key, String value) {
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
