/*-
 * NatsubpensionsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 24/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Se encarga de almacenar los enumerados de los DSS necesarios para
 * el funcionamiento del controlador NatsubpensionsControlador
 * 
 * @version 1.0, 24/01/2018
 * @author mvenegas
 *
 */
public enum NatsubquinqueniosControladorUrlEnum {

    URL100("NatsubquinqueniosControladorUrl100", "210047");

    private final String key;
    private final String value;

    private NatsubquinqueniosControladorUrlEnum(String key, String value)
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
