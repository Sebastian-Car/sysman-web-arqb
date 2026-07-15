/*
 * ContratodependenciaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
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
public enum ContratodependenciaControladorUrlEnum {

    URL3308("CONTRATODEPENDENCIACONTROLADORURL3308", "62002"),

    URL4028("CONTRATODEPENDENCIACONTROLADORURL4028", "62019"),

    URL4795("CONTRATODEPENDENCIACONTROLADORURL4795", "73012"),

    URL5401("CONTRATODEPENDENCIACONTROLADORURL5401", "73014");

    private final String key;
    private final String value;

    private ContratodependenciaControladorUrlEnum(String key, String value) {
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
