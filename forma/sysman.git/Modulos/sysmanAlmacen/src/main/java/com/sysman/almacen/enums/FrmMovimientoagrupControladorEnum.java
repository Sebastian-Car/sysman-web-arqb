/*-
 * FrmMovimientoagrupControladorEnum.java
 *
 * 1.0
 * 
 * 30/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * 
 * @version 1.0, 30/07/2018
 * @author bcardenas
 *
 */
public enum FrmMovimientoagrupControladorEnum {
    CODIGOELEMENTO("CODIGOELEMENTO");

    private final String value;

    private FrmMovimientoagrupControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
