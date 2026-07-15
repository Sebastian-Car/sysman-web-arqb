/*
 * RelcontratosvencerControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RelcontratosvencerControladorUrlEnum {

    URL5039("RELCONTRATOSVENCERCONTROLADORURL5039", "14036"),

    URL5681("RELCONTRATOSVENCERCONTROLADORURL5681", "14038"),

    URL7438("RELCONTRATOSVENCERCONTROLADORURL7438", ""),

    URL5686("RELCONTRATOSVENCERCONTROLADORURL5686", ""),

    URL4051("RELCONTRATOSVENCERCONTROLADORURL4051", "73035"),

    URL4052("RELCONTRATOSVENCERCONTROLADORURL4052", "73033"),

    URL4053("RELCONTRATOSVENCERCONTROLADORURL4052", "67003"),;

    private final String key;
    private final String value;

    private RelcontratosvencerControladorUrlEnum(String key, String value) {
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
