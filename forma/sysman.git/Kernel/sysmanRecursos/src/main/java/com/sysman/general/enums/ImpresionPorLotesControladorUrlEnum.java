/*
 * ImpresionPorLotesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ImpresionPorLotesControladorUrlEnum {

    URL7218("IMPRESIONPORLOTESCONTROLADORURL7218", "4002"),

    URL6255("IMPRESIONPORLOTESCONTROLADORURL6255", "15007"),

    URL7022("IMPRESIONPORLOTESCONTROLADORURL7022", "25001"),

    URL0003("IMPRESIONPORLOTESCONTROLADORURL0003", "39001"),

    URL0004("IMPRESIONPORLOTESCONTROLADORURL0004", "39002"),

    URL0001("IMPRESIONPORLOTESCONTROLADORURL0001", "45001"),

    URL36874("IMPRESIONPORLOTESCONTROLADORURL36874", "72001"),

    URL0002("IMPRESIONPORLOTESCONTROLADORURL0002", "72006"),

    URL10386("IMPRESIONPORLOTESCONTROLADORURL10386", "72004"),

    URL0005("IMPRESIONPORLOTESCONTROLADORURL0005", "72007"),

    URL8338("IMPRESIONPORLOTESCONTROLADORURL8338", "72002"),

    URL9218("IMPRESIONPORLOTESCONTROLADORURL9218", "75001"),

    URL11250("IMPRESIONPORLOTESCONTROLADORURL11250", "75003"),
    
    URL190801("IMPRESIONPORLOTESCONTROLADORURL190801", "72124"),
    
    URL190802("IMPRESIONPORLOTESCONTROLADORURL190802", "72126"),
     
    URL190803("IMPRESIONPORLOTESCONTROLADORURL190803", "75063"),
    
    URL190804("IMPRESIONPORLOTESCONTROLADORURL190804", "75065");

    private final String key;
    private final String value;

    private ImpresionPorLotesControladorUrlEnum(String key, String value) {
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
