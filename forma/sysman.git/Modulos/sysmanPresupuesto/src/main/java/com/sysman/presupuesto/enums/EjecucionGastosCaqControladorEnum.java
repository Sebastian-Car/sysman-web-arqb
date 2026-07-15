/*-
 * AcumuladopptalingControladorEnum.java
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
 * 
 * 
 * @version 1.0, 17/04/2017
 * @author jguerrero
 *
 */
public enum EjecucionGastosCaqControladorEnum {

    SECTOR("SECTOR"),

    CUENTAINICIAL("CUENTAINICIAL"),

    CENTRO_COSTO("CENTRO_COSTO"),

    NIT("NIT"),

    NITINICIAL("NITINICIAL"),

    FUENTEINICIAL("FUENTEINICIAL"),

    ANIO("ANIO"),

    CODIGOFINAL("CODIGOFINAL"),

    REFERENCIAINICIAL("REFERENCIAINICIAL");

    private final String value;

    private EjecucionGastosCaqControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
