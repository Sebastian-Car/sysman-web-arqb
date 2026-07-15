/*
 * GenerarStickerControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum GenerarStickerControladorUrlEnum {

    URL4980("GENERARSTICKERCONTROLADORURL4980", "62019"),

    URL7958("GENERARSTICKERCONTROLADORURL7958", "61012"),

    URL7075("GENERARSTICKERCONTROLADORURL7075", "61014"),

    URL4372("GENERARSTICKERCONTROLADORURL4372", "62017"),

    URL5492("GENERARSTICKERCONTROLADORURL5492", "41008"),

    URL54922("GENERARSTICKERCONTROLADORURL5492", "41009"),

    URL54923("GENERARSTICKERCONTROLADORURL5492", "141029"),

    URL54924("GENERARSTICKERCONTROLADORURL5492", "141031"),

    URL54925("GENERARSTICKERCONTROLADORURL5492", "141035"),

    URL54926("GENERARSTICKERCONTROLADORURL5492", "141037"),

    URL54927("GENERARSTICKERCONTROLADORURL5492", "");

    private final String key;
    private final String value;

    private GenerarStickerControladorUrlEnum(String key, String value) {
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
