/*
 * PeriodoContratosControladorUrlEnum
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
public enum PeriodoContratosControladorUrlEnum {

    URL6199("PERIODOCONTRATOSCONTROLADORURL6199", "73003"),

    URL2957("PERIODOCONTRATOSCONTROLADORURL2957",
                    "73001"),

    URL2987("PERIODOCONTRATOSCONTROLADORURL2957",
                    "73002"), URL29151("PERIODOCONTRATOSCONTROLADORURL2957",
                                    "73004"),

    URL3110("PERIODOCONTRATOSCONTROLADORURL3110", "4001"),
	
	URL3111("PERIODOCONTRATOSCONTROLADORURL3111", "73055"), 
	
	URL73058("PERIODOCONTRATOSCONTROLADORURL73058", "73058");

    private final String key;
    private final String value;

    private PeriodoContratosControladorUrlEnum(String key, String value) {
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
