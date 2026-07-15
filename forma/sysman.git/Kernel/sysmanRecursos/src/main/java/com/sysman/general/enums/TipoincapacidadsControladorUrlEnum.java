/*-
 * TipoincapacidadsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 30/10/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * 
 * @version 1.0, 30/10/2017
 * @author jcrodriguez
 *
 */
public enum TipoincapacidadsControladorUrlEnum {

    URL4923("REVISIONINCAPACIDADESCONTROLADORURL4923", "151028");

    private final String key;
    private final String value;

    private TipoincapacidadsControladorUrlEnum(String key, String value)
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
