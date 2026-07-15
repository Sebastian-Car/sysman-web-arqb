/*-
 * ImprimirHojasDeVidaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 13/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * 
 * @version 1.0, 14/12/2017
 * @author asana
 *
 */
public enum OtrosItemsControladorUrlEnum {

    URL0002("IMPRIMIRHOJASDEVIDACONTROLADORURL0002", "685025"),

    URL0001("IMPRIMIRHOJASDEVIDACONTROLADORURL0001", "685019");

    private final String key;
    private final String value;

    private OtrosItemsControladorUrlEnum(String key,
        String value)
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
