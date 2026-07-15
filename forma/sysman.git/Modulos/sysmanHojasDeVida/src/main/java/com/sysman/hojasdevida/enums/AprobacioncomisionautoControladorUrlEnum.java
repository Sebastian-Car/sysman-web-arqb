/*-
 * FamiliaresControladorUrlEnum.java
 *
 * 1.0
 *
 * 28/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 *
 * @version 1.0, 19/01/2018
 * @author asana
 *
 */
public enum AprobacioncomisionautoControladorUrlEnum {

    URL24544("FRMAPROBACIONCOMISIONAUTOCONTROLADORURL24545", "761026"), 
    URL24545("FRMAPROBACIONCOMISIONAUTOCONTROLADORURL24545", "761028"),
    URL28547("FRMAPROBACIONCOMISIONCONTROLADORURL28547","52002"),

    ;

    private final String key;
    private final String value;

    private AprobacioncomisionautoControladorUrlEnum(String key, String value)
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
