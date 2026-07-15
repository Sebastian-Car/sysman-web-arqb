/*
 * AcummensualControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InformesFormulariosControladorUrlEnum {

    URL189("INFORMESFUTCONTROLADORURL189", "4001"),

    URL214("INFORMESFUTCONTROLADORURL214", "1750001"),

    URL251("INFORMESFUTCONTROLADORURL251", "118033");

    private final String key;
    private final String value;

    private InformesFormulariosControladorUrlEnum(String key,
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
