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
public enum NatsubpensionsControladorUrlEnum {

    URL100("NatsubpensionsControladorUrl100", "710004"),

    URL101("NatsubpensionsControladorUrl01", "71000C"),

    URL102("NatsubpensionsControladorUrl102", "71000U"),

    URL103("NatsubpensionsControladorUrl103", "71000D"),

    URL104("NatsubpensionsControladorUrl104", "640002"),

    URL105("NatsubpensionsControladorUrl105", "1024001");

    private final String key;
    private final String value;

    private NatsubpensionsControladorUrlEnum(String key, String value)
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
