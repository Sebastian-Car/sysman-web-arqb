/*
 * FrmautorizaabonosControladorUrlEnum
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
public enum FrmautorizaabonosControladorUrlEnum {

    URL1234("FRMAUTORIZAABONOSCONTROLADORURL1234",
                    "233002"),
    
    URL2857("FRMAUTORIZAABONOSCONTROLADORURL2857",
                    "213012"),

    URL5166("FRMAUTORIZAABONOSCONTROLADORURL5166",
                    "233001"),

    URL6969("FRMAUTORIZAABONOSCONTROLADORURL6969",
                    "232001"),

    URL6581("FRMAUTORIZAABONOSCONTROLADORURL6581",
                    "231001"),

    URL3844("FRMAUTORIZAABONOSCONTROLADORURL3844",
                    "213008"),

    URL4702("FRMAUTORIZAABONOSCONTROLADORURL4702",
                    "213010"),

    URL3372("FRMAUTORIZAABONOSCONTROLADORURL3372",
                    "214010");

    private final String key;
    private final String value;

    private FrmautorizaabonosControladorUrlEnum(String key, String value) {
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
