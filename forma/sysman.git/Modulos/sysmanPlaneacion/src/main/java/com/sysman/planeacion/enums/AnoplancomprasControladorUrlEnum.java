/*-
 * AnoplancomprasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 6/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.planeacion.enums;

/**
 * 
 * @version 1.0, 6/09/2017
 * @author jcrodriguez
 *
 */
public enum AnoplancomprasControladorUrlEnum {

    URL2124("ANOPLANCOMPRASCONTROLADORURL2124", "4033"),

    URL2126("ANOPLANCOMPRASCONTROLADORURL2126", "4035"),

    URL2127("ANOPLANCOMPRASCONTROLADORURL2127", "4036"),

    URL2129("ANOPLANCOMPRASCONTROLADORURL2129", "4028");
    private final String key;
    private final String value;

    private AnoplancomprasControladorUrlEnum(String key, String value)
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
