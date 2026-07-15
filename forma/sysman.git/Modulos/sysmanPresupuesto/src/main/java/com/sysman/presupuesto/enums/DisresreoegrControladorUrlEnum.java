/*
 * DisresreoegrControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DisresreoegrControladorUrlEnum {

    URL3460("DISRESREOEGRCONTROLADORURL3460",
                    "75016"),

    URL5517("DISRESREOEGRCONTROLADORURL5517",
                    "25014"),

    URL4302("DISRESREOEGRCONTROLADORURL4302",
                    "75017"),

    URL5089("DISRESREOEGRCONTROLADORURL5089",
                    "4001"),
    
    URL20013("DISRESREOEGRCONTROLADORURL20013",
            "20013"),
    
    URL20015("DISRESREOEGRCONTROLADORURL20015",
            "20015"), 
    
    URL23006("DISRESREOEGRCONTROLADORURL23006",
            "23006"),
    
    URL23008("DISRESREOEGRCONTROLADORURL23008",
            "23008"),
    
    URL13001("DISRESREOEGRCONTROLADORURL13001",
            "13001"),
    
    URL13035("DISRESREOEGRCONTROLADORURL13035",
            "13035"),
    
    URL34001("DISRESREOEGRCONTROLADORURL34001",
            "34001"),
    
    URL34003("DISRESREOEGRCONTROLADORURL34003",
            "34003");

    private final String key;
    private final String value;

    private DisresreoegrControladorUrlEnum(String key, String value) {
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
