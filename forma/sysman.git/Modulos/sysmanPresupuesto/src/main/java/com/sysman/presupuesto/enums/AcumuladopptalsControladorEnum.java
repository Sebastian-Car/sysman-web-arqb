/*-
 * AcumuladopptalsControladorEnum.java
 *
 * 1.0
 * 
 * 17/04/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 17/04/2017
 * @author jguerrero
 *
 */
public enum AcumuladopptalsControladorEnum {
    PARAM2("V_PLAN_PRESUPUESTAL");

    private final String value;

    private AcumuladopptalsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
