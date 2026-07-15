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
 * 
 * @version 1.0, 29/10/2018
 * @author ybecerra
 *
 */
public enum InformesFormulariosControladorEnum {

    SUBTIPO("SUBTIPO"),

    CONSULTA("CONSULTA"),

    ENCABEZADO("ENCABEZADO");

    private final String value;

    private InformesFormulariosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
