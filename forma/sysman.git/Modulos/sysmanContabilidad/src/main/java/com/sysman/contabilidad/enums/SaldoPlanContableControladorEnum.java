/*-
 * SaldoPlanContableControladorEnum.java
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
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 10/04/2017
 * @author jguerrero
 *
 */
public enum SaldoPlanContableControladorEnum {

    PARAM0("PLAN_CONTABLE");

    private final String value;

    private SaldoPlanContableControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
