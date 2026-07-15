/*
 * FcregistroaprcompagControladorUrlEnum
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
public enum FcregistroaprcompagControladorUrlEnum {

    URL6852("FCREGISTROAPRCOMPAGCONTROLADORURL6852", "45018"),

    URL6247("FCREGISTROAPRCOMPAGCONTROLADORURL6247", "7012"),

    URL11226("FCREGISTROAPRCOMPAGCONTROLADORURL11226", "34003"),

    URL9791("FCREGISTROAPRCOMPAGCONTROLADORURL9791", "20015"),

    URL7857("FCREGISTROAPRCOMPAGCONTROLADORURL7857", "45020"),

    URL5147("FCREGISTROAPRCOMPAGCONTROLADORURL5147", "4007"),

    URL9093("FCREGISTROAPRCOMPAGCONTROLADORURL9093", "20013"),

    URL10563("FCREGISTROAPRCOMPAGCONTROLADORURL10563", "34001"),

    URL5648("FCREGISTROAPRCOMPAGCONTROLADORURL5648", "7007");

    private final String key;
    private final String value;

    private FcregistroaprcompagControladorUrlEnum(String key, String value) {
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
