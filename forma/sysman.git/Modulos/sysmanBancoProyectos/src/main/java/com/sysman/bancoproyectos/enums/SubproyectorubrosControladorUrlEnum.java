/*
 * SubproyectorubrosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SubproyectorubrosControladorUrlEnum {

    URL8880("SUBPROYECTORUBROSCONTROLADORURL8880", "430029"),

    URL12639("SUBPROYECTORUBROSCONTROLADORURL12638", "430031"),

    URL10414("SUBPROYECTORUBROSCONTROLADORURL10414", "430005"),

    URL11957("SUBPROYECTORUBROSCONTROLADORURL11957", "34001"),

    URL12638("SUBPROYECTORUBROSCONTROLADORURL9649", "430047"),

    URL8386("SUBPROYECTORUBROSCONTROLADORURL8386", "4001");

    private final String key;
    private final String value;

    private SubproyectorubrosControladorUrlEnum(String key, String value) {
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
