/*
 * DeudoresPredialControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum DeudoresPredialControladorUrlEnum {

    URL9496("DEUDORESPREDIALCONTROLADORURL9496","367029"),  
    URL8623("DEUDORESPREDIALCONTROLADORURL8623","367027"),  
    URL10491("DEUDORESPREDIALCONTROLADORURL10491","367031"),  
    URL11075("DEUDORESPREDIALCONTROLADORURL11075","118009"),  
    URL11609("DEUDORESPREDIALCONTROLADORURL11609","367033"),  
    URL12554("DEUDORESPREDIALCONTROLADORURL12554","367035"),  
    URL6661("DEUDORESPREDIALCONTROLADORURL6661","4001"),  
    URL7053("DEUDORESPREDIALCONTROLADORURL7053","4027"),  
    URL7920("DEUDORESPREDIALCONTROLADORURL7920","367025");  

    private final String key;
    private final String value;

    private  DeudoresPredialControladorUrlEnum(String key, String value) {
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
