/*-
 * TiposretencionesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 30/10/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * @version 1.0, 30/10/2017
 * @author jcrodriguez
 *
 */
public enum TiposretencionesControladorUrlEnum {

    URL3322("TIPOLICENCIASCONTROLADORURL3322", "16048");

    private final String key;
    private final String value;

    private TiposretencionesControladorUrlEnum(String key, String value)
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
