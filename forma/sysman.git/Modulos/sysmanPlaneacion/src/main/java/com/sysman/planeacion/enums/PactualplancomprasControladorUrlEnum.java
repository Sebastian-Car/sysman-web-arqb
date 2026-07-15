/*
 * PactualplancomprasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum PactualplancomprasControladorUrlEnum {

    URL7839("PACTUALPLANCOMPRASCONTROLADORURL7839","546001"),  
    URL10385("PACTUALPLANCOMPRASCONTROLADORURL10385","545001"),  
    URL7138("PACTUALPLANCOMPRASCONTROLADORURL7138","542001"),  
    URL9961("PACTUALPLANCOMPRASCONTROLADORURL9961","62045"),  
    URL8489("PACTUALPLANCOMPRASCONTROLADORURL8489","112101"),  
    URL11032("PACTUALPLANCOMPRASCONTROLADORURL11032","4001"),  
    URL11340("PACTUALPLANCOMPRASCONTROLADORURL11340","114005");

    private final String key;
    private final String value;

    private  PactualplancomprasControladorUrlEnum(String key, String value) {
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
