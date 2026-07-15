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
public enum VacacionesControladorUrlEnum {
    URL3323("TIPOLICENCIASCONTROLADORURL3323", "625002"),

    URL3322("TIPOLICENCIASCONTROLADORURL3322", "210058"),

    URL3326("TIPOLICENCIASCONTROLADORURL3326", "620002"),

    URL3328("TIPOLICENCIASCONTROLADORURL3328", "471051"),
	
	URL625009("VACACIONESCONTROLADORURL625009", "625009");

    private final String key;
    private final String value;

    private VacacionesControladorUrlEnum(String key, String value)
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
