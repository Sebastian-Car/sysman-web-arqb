/*
 * FactoresRiesgoControladorUrlEnum
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
public enum FrmAnexosControladorUrlEnum {

    URL7559("FRMANEXOSCONTROLADORURLURL7559", "1808001"),

    URL7859("FRMANEXOSCONTROLADORURLURL7859", "1808003"),

    URL8858("FRMANEXOSCONTROLADORURLURL8858", "1808004"),
    
    URL1983001("FRMANEXOSCONTROLADORURLURL1983001", "1983001");

    private final String key;
    private final String value;

    private FrmAnexosControladorUrlEnum(String key, String value) {
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
