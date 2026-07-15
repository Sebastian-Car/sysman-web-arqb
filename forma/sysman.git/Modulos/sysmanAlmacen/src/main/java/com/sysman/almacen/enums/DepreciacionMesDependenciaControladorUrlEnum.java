/*
 * DepreciacionMesDependenciaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DepreciacionMesDependenciaControladorUrlEnum {

    URL8625("DEPRECIACIONMESDEPENDENCIACONTROLADORURL8625", "7001"),

    URL11410("DEPRECIACIONMESDEPENDENCIACONTROLADORURL11410", "20042"),

    URL10812("DEPRECIACIONMESDEPENDENCIACONTROLADORURL10812", "20040"),

    URL9133("DEPRECIACIONMESDEPENDENCIACONTROLADORURL9133", "4002"),

    URL9459("DEPRECIACIONMESDEPENDENCIACONTROLADORURL9459", "112002"),

    URL10101("DEPRECIACIONMESDEPENDENCIACONTROLADORURL10101", "112004"),
    
    URL179005("DEPRECIACIONMESDEPENDENCIACONTROLADORURL179005", "179005"),
    
    URL112198("DEPRECIACIONMESDEPENDENCIACONTROLADORURL10101", "112198");

    private final String key;
    private final String value;

    private DepreciacionMesDependenciaControladorUrlEnum(String key,
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
