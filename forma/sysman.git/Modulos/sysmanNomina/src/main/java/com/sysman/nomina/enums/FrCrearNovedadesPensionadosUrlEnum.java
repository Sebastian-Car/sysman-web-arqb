/*-
 * VacacionesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 31/10/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * 
 * @version 1.0, 31/10/2017
 * @author jcrodriguez
 *
 */
public enum FrCrearNovedadesPensionadosUrlEnum {
    URL001("FrCrearNovedadesPensionadosUrlEnum", "210156");

    private final String key;
    private final String value;

    private FrCrearNovedadesPensionadosUrlEnum(String key, String value)
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
