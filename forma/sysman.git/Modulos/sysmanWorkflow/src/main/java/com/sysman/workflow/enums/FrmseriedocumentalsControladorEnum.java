/*-
 * FrmseriedocumentalsControladorEnum.java
 *
 * 1.0
 * 
 * 11/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 11/04/2018
 * @author lbotia
 *
 */
public enum FrmseriedocumentalsControladorEnum {

    CATEGORIA("CATEGORIA"),
    COMPANIA("COMPANIA");

    private final String value;

    private FrmseriedocumentalsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
