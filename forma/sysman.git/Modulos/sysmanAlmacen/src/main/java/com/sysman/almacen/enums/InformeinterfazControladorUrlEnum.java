/*
 * InformeinterfazControladorUrlEnum
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
public enum InformeinterfazControladorUrlEnum {

    URL4299("INFORMEINTERFAZCONTROLADORURL4299", "112034"),

    URL2774("INFORMEINTERFAZCONTROLADORURL2774", "4001"),

    URL3456("INFORMEINTERFAZCONTROLADORURL3456", "112032");

    private final String key;
    private final String value;

    private InformeinterfazControladorUrlEnum(String key, String value) {
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
