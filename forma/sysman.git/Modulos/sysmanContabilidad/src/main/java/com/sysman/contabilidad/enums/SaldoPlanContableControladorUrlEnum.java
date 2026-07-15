/*-
 * SaldoPlanContableControlador.java
 *
 * 1.0
 * 
 * 10/04/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * 
 * 
 * @version 1.0, 10/04/2017
 * @author jguerrero
 *
 */
public enum SaldoPlanContableControladorUrlEnum {

    URL2789("SALDOPLANCONTABLECONTROLADORURL2789", "29055");

    private final String key;
    private final String value;

    private SaldoPlanContableControladorUrlEnum(String key, String value) {
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
