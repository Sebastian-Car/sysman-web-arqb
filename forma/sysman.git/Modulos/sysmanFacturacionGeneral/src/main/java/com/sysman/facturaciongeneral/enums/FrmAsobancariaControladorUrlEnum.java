/*-
 * FrmlistadoRecaudoDifUrlEnum.java
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
public enum FrmAsobancariaControladorUrlEnum {

    URL0001("ACCIONESCCONTRATOCONTROLADORURL0001", "16115");

    private final String key;
    private final String value;

    private FrmAsobancariaControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
