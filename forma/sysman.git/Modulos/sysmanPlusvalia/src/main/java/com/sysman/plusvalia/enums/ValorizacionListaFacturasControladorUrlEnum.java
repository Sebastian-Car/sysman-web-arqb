/*-
 * ValorizacionListaFacturasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 21/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * 
 * @version 1.0, 21/03/2019
 * @author bcardenas
 *
 */
public enum ValorizacionListaFacturasControladorUrlEnum {

    URL1781("VALORIZACION_LISTA_FACTURAS_CONTROLADORURL1781", "1773003"),

    URL1767("VALORIZACION_LISTA_FACTURAS_CONTROLADORURL1781", "1767001"),

    URL1795("VALORIZACION_LISTA_FACTURAS_CONTROLADORURL1781", "1795001"),

    URL0001("VALORIZACION_LISTA_FACTURAS_CONTROLADORURL1781", "1767008");

    private final String key;
    private final String value;

    private ValorizacionListaFacturasControladorUrlEnum(String key,
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
