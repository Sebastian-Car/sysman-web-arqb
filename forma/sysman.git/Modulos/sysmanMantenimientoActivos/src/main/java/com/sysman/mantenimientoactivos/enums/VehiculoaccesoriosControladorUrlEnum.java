/*-
 * VehiculoaccesoriosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.mantenimientoactivos.enums;

/**
 * 
 * @version 1.0, 18/08/2017
 * @author jcrodriguez
 *
 */
public enum VehiculoaccesoriosControladorUrlEnum {
    URL17912("VEHICULOACCESORIOSCONTROLADORURL7912",
                    "442001"),

    URL17925("VEHICULOACCESORIOSCONTROLADORURL7925", "458001");

    private final String key;
    private final String value;

    private VehiculoaccesoriosControladorUrlEnum(String key, String value)
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
