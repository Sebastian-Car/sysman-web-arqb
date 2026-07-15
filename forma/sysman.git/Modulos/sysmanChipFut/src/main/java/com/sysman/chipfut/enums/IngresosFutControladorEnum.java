/*-
 * IngresosFutControladorEnum.java
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
public enum IngresosFutControladorEnum {

    CODIGO_INICIAL("CODIGOINICIAL");

    private final String value;

    private IngresosFutControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
