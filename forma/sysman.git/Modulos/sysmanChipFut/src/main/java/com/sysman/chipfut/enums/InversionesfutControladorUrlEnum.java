/*
 * ConfiguracionFuncionamientosControladorUrlEnum
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
public enum InversionesfutControladorUrlEnum {

    URL4612("INVERSIONESFUTCONTROLADORURL4612",
                    "7001"),

    URL5457("INVERSIONESFUTCONTROLADORURL5457"
                    ,"1031016"),
    
    URL9897("INVERSIONESFUTCONTROLADORURL9897"
                    ,"1031018");

    private final String key;
    private final String value;

    private InversionesfutControladorUrlEnum(String key,
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
