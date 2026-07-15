/*
 * FrmevmanualsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmevmanualsControladorUrlEnum {

    URL383("FRMEVMANUALSCONTROLADORURL383", "607016"), // 210112

    URL7043("FRMEVMANUALSCONTROLADORURL7043", "463026"), // 463019

    URL7047("FRMEVMANUALSCONTROLADORURL7047", "463024"), // 463021

    URL7045("FRMEVMANUALSCONTROLADORURL7045", "462007"),

    URL7050("FRMEVMANUALSCONTROLADORURL7050", "210084"),

    URL7049("FRMEVMANUALSCONTROLADORURL7049", "210083"),

    URL7046("FRMEVMANUALSCONTROLADORURL7046", "210081"),

    URL389("FRMEVMANUALSCONTROLADORURL389", "104008"),

    URL7048("FRMEVMANUALSCONTROLADORURL7048", "62075");

    private final String key;
    private final String value;

    private FrmevmanualsControladorUrlEnum(String key, String value) {
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
