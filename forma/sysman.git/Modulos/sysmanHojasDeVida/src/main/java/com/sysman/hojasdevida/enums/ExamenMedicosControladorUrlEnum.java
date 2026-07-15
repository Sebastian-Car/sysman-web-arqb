/*
 * ExamenMedicosControladorUrlEnum
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
public enum ExamenMedicosControladorUrlEnum {

    URL8944("EXAMENMEDICOSCONTROLADORURL8944", "623001"),

    URL8407("EXAMENMEDICOSCONTROLADORURL8407", "734001"),

    URL9544("EXAMENMEDICOSCONTROLADORURL9544", "210068");

    private final String key;
    private final String value;

    private ExamenMedicosControladorUrlEnum(String key, String value) {
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
