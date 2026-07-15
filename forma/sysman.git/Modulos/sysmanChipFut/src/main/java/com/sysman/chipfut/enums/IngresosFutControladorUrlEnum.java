/*-
 * IngresosFutControladorUrlEnum.java
 *
 * 1.0
 * 
 * 17/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.chipfut.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 17/07/2018
 * @author lbotia
 *
 */
public enum IngresosFutControladorUrlEnum {
    // Lista Año
    URL0001("INGRESOSFUTCONTROLADORURL0001", "4001"),

    URL0002("INGRESOSFUTCONTROLADORURL0002", "7001")

    ;

    private final String key;
    private final String value;

    private IngresosFutControladorUrlEnum(String key, String value) {
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
