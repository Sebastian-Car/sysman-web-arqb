/*
 * FcregistroejecucingresosControladorUrlEnum
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
public enum FcregistroejecucingresosControladorUrlEnum {

    URL4579("FCREGISTROEJECUCINGRESOSCONTROLADORURL4579", "4002"),

    URL6238("FCREGISTROEJECUCINGRESOSCONTROLADORURL6238", "20015"),

    URL8291("FCREGISTROEJECUCINGRESOSCONTROLADORURL8291", "94044"),

    URL5574("FCREGISTROEJECUCINGRESOSCONTROLADORURL5574", "20013"),

    URL6981("FCREGISTROEJECUCINGRESOSCONTROLADORURL6981", "23010"),

    URL9038("FCREGISTROEJECUCINGRESOSCONTROLADORURL9038", "94046"),

    URL7598("FCREGISTROEJECUCINGRESOSCONTROLADORURL7598", "23019"),

    URL5098("FCREGISTROEJECUCINGRESOSCONTROLADORURL5098", "7007");

    private final String key;
    private final String value;

    private FcregistroejecucingresosControladorUrlEnum(String key,
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
