/*-
 * FrmpolizasproysControladorEnum.java
 *
 * 1.0
 * 
 * 29/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.precontractual.enums;

/**
 * 
 * @version 1.0, 29/08/2017
 * @author jcrodriguez
 *
 */
public enum FrmpolizasproysControladorEnum {
    ES_POLIZA_EST("ES_POLIZA_EST"),

    COD_POLIZA("COD_POLIZA"),

    TIPOPOLIZA("TIPOPOLIZA");

    private final String value;

    private FrmpolizasproysControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
