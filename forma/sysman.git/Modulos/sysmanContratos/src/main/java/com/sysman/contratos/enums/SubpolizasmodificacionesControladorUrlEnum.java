/*-
 * SubpolizasmodificacionesControladorEnumUrlEnum.java
 *
 * 1.0
 * 
 * 15/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SubpolizasmodificacionesControladorUrlEnum {

    URL001("SUBPOLIZASMODIFICACIONESCONTROLADORURL001", "195008"),

    URL002("SUBPOLIZASMODIFICACIONESCONTROLADORURL002", "195009"),

    URL003("SUBPOLIZASMODIFICACIONESCONTROLADORURL003", "195010"),

    URL004("SUBPOLIZASMODIFICACIONESCONTROLADORURL004", "195006"),

    URL005("SUBPOLIZASMODIFICACIONESCONTROLADORURL005", "195004"),

    URL006("SUBPOLIZASMODIFICACIONESCONTROLADORURL006", "195005"),

    URL007("SUBPOLIZASMODIFICACIONESCONTROLADORURL007", "195002"),

    URL244("SUBPOLIZASMODIFICACIONESCONTROLADORURL244", "195012"),

    URL254("SUBPOLIZASMODIFICACIONESCONTROLADORURL254", "195014"),

    URL008("SUBPOLIZASMODIFICACIONESCONTROLADORURL008", "195002"),

    URL8145("SUBPOLIZASMODIFICACIONESCONTROLADORURL8145", "95001"),

    URL7842("SUBPOLIZASMODIFICACIONESCONTROLADORURL7842", "198001");

    private final String key;
    private final String value;

    private SubpolizasmodificacionesControladorUrlEnum(String key,
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
