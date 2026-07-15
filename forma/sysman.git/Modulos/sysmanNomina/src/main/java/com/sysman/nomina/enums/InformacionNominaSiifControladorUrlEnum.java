/*
 * InformacionNominaSiifControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum InformacionNominaSiifControladorUrlEnum {

    URL7227("INFORMACIONNOMINASIIFCONTROLADORURL7227","7028"),  
    URL8815("INFORMACIONNOMINASIIFCONTROLADORURL8815","471019"),  
    URL5999("INFORMACIONNOMINASIIFCONTROLADORURL5999","471002");  

    private final String key;
    private final String value;

    private  InformacionNominaSiifControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
