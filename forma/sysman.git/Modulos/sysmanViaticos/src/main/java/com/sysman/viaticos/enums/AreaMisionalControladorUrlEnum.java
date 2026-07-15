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

package com.sysman.viaticos.enums;

/**
 *
 * @version 1.0, 19/01/2018
 * @author asana
 *
 */
public enum AreaMisionalControladorUrlEnum {

    URL184("LEGALIZACIONVIATICOSCONTROLADORURL184", "764010"),

    URL213("LEGALIZACIONVIATICOSCONTROLADORURL213", "764011"),

    URL235("LEGALIZACIONVIATICOSCONTROLADORURL235", "")

    ;

    private final String key;
    private final String value;

    private AreaMisionalControladorUrlEnum(String key, String value)
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
