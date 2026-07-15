/*
 * PredialregispagbansControladorUrlEnum
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
public enum PredialregispagbansControladorUrlEnum {

    URL26049("PREDIALREGISPAGBANSCONTROLADORURL26049",
                    "410007"),

    URL56094("PREDIALREGISPAGBANSCONTROLADORURL56094",
                    "4013"),

    URL62135("PREDIALREGISPAGBANSCONTROLADORURL62135",
                    "367160"),

    URL66778("PREDIALREGISPAGBANSCONTROLADORURL66778",
                    "375010"),

    URL59618("PREDIALREGISPAGBANSCONTROLADORURL59618",
                    "374025"),

    URL64770("PREDIALREGISPAGBANSCONTROLADORURL64770",
                    "374026"),

    URL24814("PREDIALREGISPAGBANSCONTROLADORURL24814",
                    "367170"),

    URL24815("PREDIALREGISPAGBANSCONTROLADORURL24815",
                    "374027"),

    URL24816("PREDIALREGISPAGBANSCONTROLADORURL24816",
                    "375009"),

    URL24817("PREDIALREGISPAGBANSCONTROLADORURL24817",
                    "374028"),

    URL24818("PREDIALREGISPAGBANSCONTROLADORURL24818",
                    "374036"),

    URL24819("PREDIALREGISPAGBANSCONTROLADORURL24819",
                    "374037"),

    URL24820("PREDIALREGISPAGBANSCONTROLADORURL24820",
                    "410008"),

    URL24821("PREDIALREGISPAGBANSCONTROLADORURL24821",
                    "410009");

    private final String key;
    private final String value;

    private PredialregispagbansControladorUrlEnum(String key, String value) {
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
