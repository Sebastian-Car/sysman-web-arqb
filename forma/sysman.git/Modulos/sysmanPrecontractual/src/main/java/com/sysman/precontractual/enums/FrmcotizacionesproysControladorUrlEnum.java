/*-
 * FrmcotizacionesproysControladorUrlEnum.java
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
public enum FrmcotizacionesproysControladorUrlEnum {
    URL2222("FRMCOTIZACIONESPROYSCONTROLADORURL2222", "112092"),

    URL2224("FRMCOTIZACIONESPROYSCONTROLADORURL2224", "494001");
    private final String key;
    private final String value;

    private FrmcotizacionesproysControladorUrlEnum(String key, String value)
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
