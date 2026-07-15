/*
 * NatAccidentesTrabajosControladorUrlEnum
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
public enum NatAccidentesTrabajosControladorUrlEnum {

    URL6289("NATACCIDENTESTRABAJOSCONTROLADORURL6289",
                    "732003"),

    URL7111("NATACCIDENTESTRABAJOSCONTROLADORURL7111",
                    "210012"),

    URL6784("NATACCIDENTESTRABAJOSCONTROLADORURL6784",
                    "733003"),

    URL8585("NATACCIDENTESTRABAJOSCONTROLADORURL8585",
                    "735003"),

    URL7878("NATACCIDENTESTRABAJOSCONTROLADORURL7878", "735004"); // "735001"

    private final String key;
    private final String value;

    private NatAccidentesTrabajosControladorUrlEnum(String key, String value) {
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
