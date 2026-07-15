/*-
 * FrmmanteliminarcerosnitControladorUrlEnum.java
 *
 * 1.0
 * 
 * 5/07/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.predial.enums;

/**
 * 
 * @version 1.0, 5/07/2017
 * @author jcrodriguez
 *
 */
public enum FrmmanteliminarcerosnitControladorUrlEnum {

    URL5323("FRMMANTELIMINARCEROSNITCONTROLADORURL5323", "367091");
    private final String key;
    private final String value;

    private FrmmanteliminarcerosnitControladorUrlEnum(String key, String value)
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
