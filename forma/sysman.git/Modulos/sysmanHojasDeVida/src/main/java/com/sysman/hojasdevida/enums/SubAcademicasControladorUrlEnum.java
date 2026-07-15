/*
 * SubAcademicasControladorUrlEnum
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
public enum SubAcademicasControladorUrlEnum {

    URL19754("SUBACADEMICASCONTROLADORURL19754", "716001"),

    URL21409("SUBACADEMICASCONTROLADORURL21409", "7001"),

    URL13976("SUBACADEMICASCONTROLADORURL13976", "704001"),

    URL255("SUBACADEMICASCONTROLADORURL255", "698001"),

    URL415("SUBACADEMICASCONTROLADORURL415", "1001"),

    URL439("SUBACADEMICASCONTROLADORURL439", "2001"),

    URL466("SUBACADEMICASCONTROLADORURL466", "5001"),

    URL470("SUBACADEMICASCONTROLADORURL470", "4001"),

    URL493("SUBACADEMICASCONTROLADORURL493", "709001"),

    URL1070("SUBACADEMICASCONTROLADORURL1070", "709002"),

    URL767("SUBACADEMICASCONTROLADORURL767", "639001"),

    URL809("SUBACADEMICASCONTROLADORURL809", "14132"),
    
    URL9090("SUBACADEMICASCONTROLADORURL809", "1682001"),
    
    URL9091("SUBACADEMICASCONTROLADORURL9091", "118034"),
    
    URL9092("SUBACADEMICASCONTROLADORURL9092", "1007"),
    
    URL9093("SUBACADEMICASCONTROLADORURL9093", "2011"),
    
    URL9094("SUBACADEMICASCONTROLADORURL9094", "5011"),
    
    URL9095("SUBACADEMICASCONTROLADORURL9095", "1682002");
    
    private final String key;
    private final String value;

    private SubAcademicasControladorUrlEnum(String key, String value) {
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
