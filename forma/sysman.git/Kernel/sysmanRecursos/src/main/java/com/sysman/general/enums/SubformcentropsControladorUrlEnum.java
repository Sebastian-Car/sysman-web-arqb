/*-
 * SubformcentropsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 6/04/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 6/04/2017
 * @author jguerrero
 *
 */

public enum SubformcentropsControladorUrlEnum {
    URL0001("SUBFORMCENTROPSCONTROLADORURL0001", "38001"),

    URL0002("SUBFORMCENTROPSCONTROLADORURL0002", "38003"),

    URL0003("SUBFORMCENTROPSCONTROLADORURL0003", "38005"),

    URL0004("SUBFORMCENTROPSCONTROLADORURL0003", "38007");

    private final String key;
    private final String value;

    private SubformcentropsControladorUrlEnum(String key, String value) {
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
