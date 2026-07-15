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
public enum InformeGeneralControladorUrlEnum {

    URL184("LEGALIZACIONVIATICOSCONTROLADORURL184", "4001"),

    URL213("LEGALIZACIONVIATICOSCONTROLADORURL213", "761024"),
    
    URL758("LEGALIZACIONVIATICOSCONTROLADORURL758", "7001"),
    
    URL624("LEGALIZACIONVIATICOSCONTROLADORURL624", "7020")

    ;

    private final String key;
    private final String value;

    private InformeGeneralControladorUrlEnum(String key, String value)
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
