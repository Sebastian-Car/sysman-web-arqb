/*
 * LisReoAbiertosCtasControladorUrlEnum
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
public enum LisReoAbiertosCtasControladorUrlEnum {

    URL6460("LISREOABIERTOSCTASCONTROLADORURL6460", "14033"),

    URL5758("LISREOABIERTOSCTASCONTROLADORURL5758", "14067"),

    URL4700("LISREOABIERTOSCTASCONTROLADORURL4700", "45020"),

    URL3766("LISREOABIERTOSCTASCONTROLADORURL3766", "45018");

    private final String key;
    private final String value;

    private LisReoAbiertosCtasControladorUrlEnum(String key, String value) {
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
