/*-
 * SubformcentropsControladorEnum.java
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
public enum SubformcentropsControladorEnum {

    PARAM2("DETALLE_COMPROBANTE_PPTAL"), PARAM1("MESFINAL"), PARAM0(
                    "MESINICIAL");

    private final String value;

    private SubformcentropsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
