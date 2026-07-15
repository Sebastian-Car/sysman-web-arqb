/*-
 * FrmcotizacionesproysControladorEnum.java
 *
 * 1.0
 * 
 * 25/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.precontractual.enums;

/**
 * 
 * @version 1.0, 25/08/2017
 * @author jcrodriguez
 *
 */
public enum FrmcotizacionesproysControladorEnum {
    ES_COTIZACIONES("ES_COTIZACIONES"),

    COD_ESTUDIO("COD_ESTUDIO"),

    COD_ELEMENTO("COD_ELEMENTO"),

    NOMBRELARGO("NOMBRELARGO"),

    RID("RID"),

    COD_ITEM("COD_ITEM"),

    CODESTUDIO("CODESTUDIO");

    private final String value;

    private FrmcotizacionesproysControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
