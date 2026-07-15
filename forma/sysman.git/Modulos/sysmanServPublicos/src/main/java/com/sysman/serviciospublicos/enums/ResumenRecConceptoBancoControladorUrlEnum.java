/*
 * ResumenRecConceptoBancoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ResumenRecConceptoBancoControladorUrlEnum {

    URL5310("RESUMENRECCONCEPTOBANCOCONTROLADORURL5310",
                    "36007"),

    URL5867("RESUMENRECCONCEPTOBANCOCONTROLADORURL5867",
                    "36008");

    private final String key;
    private final String value;

    private ResumenRecConceptoBancoControladorUrlEnum(String key,
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
