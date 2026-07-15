/*-
 * FrmlistadoRecaudoDifEnum.java
 *
 * 1.0
 * 
 * 8/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * 
 * @version 1.0, 8/11/2017
 * @author jcrodriguez
 *
 */
public enum FrmAsobancariaControladorEnum {

    PARAM0("ANOCOBRO"),

    PARAM1("BANCOINICIAL"),

    PARAM2("CODIGOINICIAL"),

    PARAM3("ANIO"),

    PARAM4("CONCEPTOINICIAL"),

    PARAM5("TIPOCOBRO"),

    PARAM6("NITINICIAL");

    private final String value;

    private FrmAsobancariaControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
