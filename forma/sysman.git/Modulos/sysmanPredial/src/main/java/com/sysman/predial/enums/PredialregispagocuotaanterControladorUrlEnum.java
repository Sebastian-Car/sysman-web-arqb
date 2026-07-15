/*
 * PredialregispagocuotaanterControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PredialregispagocuotaanterControladorUrlEnum {

    URL4841("PREDIALREGISPAGOCUOTAANTERCONTROLADORURL4841",
                    "368011"),

    URL5858("PREDIALREGISPAGOCUOTAANTERCONTROLADORURL5858",
                    "367153"),

    URL4256("PREDIALREGISPAGOCUOTAANTERCONTROLADORURL4256",
                    "368010"),

    URL5381("PREDIALREGISPAGOCUOTAANTERCONTROLADORURL5381",
                    "375004"),

    URL1345("PREDIALREGISPAGOCUOTAANTERCONTROLADORURL1345",
                    "371015"),

    URL1346("PREDIALREGISPAGOCUOTAANTERCONTROLADORURL1346",
                    "368013");

    private final String key;
    private final String value;

    private PredialregispagocuotaanterControladorUrlEnum(String key,
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
