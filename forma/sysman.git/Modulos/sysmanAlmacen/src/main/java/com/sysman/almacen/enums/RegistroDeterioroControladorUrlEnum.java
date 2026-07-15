/*
 * RelacionPrestamoBienesControladorUrlEnum
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
public enum RegistroDeterioroControladorUrlEnum {

    URL112154("REGISTRODETERIOROCONTROLADORURL112154", "112154"),

    URL112156("REGISTRODETERIOROCONTROLADORURL112156", "112156"),

    URL369("REGISTRODETERIOROCONTROLADORURL369", "1749004"),
    
    URL1749005("REGISTRODETERIOROCONTROLADORURL1749005", "1749005"),
	
	URL1749006("REGISTRODETERIOROCONTROLADORURL1749006", "1749006");

    private final String key;
    private final String value;

    private RegistroDeterioroControladorUrlEnum(String key, String value) {
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
