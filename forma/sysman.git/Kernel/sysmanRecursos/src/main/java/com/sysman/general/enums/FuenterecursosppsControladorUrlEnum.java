/*
 * FuenterecursosppsControladorUrlEnum
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
public enum FuenterecursosppsControladorUrlEnum {

    URL199("FUENTERECURSOSPPSCONTROLADORURL199", "34035"),

    URL128("FUENTERECURSOSPPSCONTROLADORURL128", "34011"),

    URL4989("FUENTERECURSOSPPSCONTROLADORURL4989", "4001"),

    URL4660("FUENTERECURSOSPPSCONTROLADORURL4660", "126001"),

    URL5987("FUENTERECURSOSPPSCONTROLADORURL5987", "4016"),

    URL119("FUENTERECURSOSPPSCONTROLADORURL119", "34013"),

    URL122("FUENTERECURSOSPPSCONTROLADORURL122", "34014"),
    
    URL3400C("FUENTERECURSOSPPSCONTROLADORURL3400C", "3400C"),

    URL126("FUENTERECURSOSPPSCONTROLADORURL126", "3400D"),

    URL001("FUENTERECURSOSPPSCONTROLADORURL001", "1697001"),

    URL271("FUENTERECURSOSPPSCONTROLADORURL271", "1751001"),

    URL282("FUENTERECURSOSPPSCONTROLADORURL282", "1752001"),
    
	URL341("FUENTERECURSOSPPSCONTROLADORURL341", "34061"),
	
	URL1929001("FUENTERECURSOSPPSCONTROLADORURL1929001","1929001"),
	
	URL34074("FUENTERECURSOSPPSCONTROLADORURL341", "34074");

    private final String key;
    private final String value;

    private FuenterecursosppsControladorUrlEnum(String key, String value) {
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
