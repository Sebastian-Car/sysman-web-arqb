/*-
 * FrmRecaudoControladorEnum.java
 *
 * 1.0
 * 
 * 27/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * 
 * @version 1.0, 27/07/2017
 * @author jcrodriguez
 *
 */
public enum FrmRecaudoControladorEnum {
    FORMATO_FECHA("dd/MM/yyyy"),

    REPORTE(""),

    SI("SI"),

    PARAM0("CODIGORUTAINI");

    private final String value;

    private FrmRecaudoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
