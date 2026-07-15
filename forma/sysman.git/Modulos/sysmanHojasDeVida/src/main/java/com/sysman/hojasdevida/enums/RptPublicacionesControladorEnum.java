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
 * @version 1.0, 14/12/2017
 * @author asana
 */

public enum RptPublicacionesControladorEnum {

    PARAM0("NUMEROCARPETA"),

    PARAM1("");

    private final String value;

    private RptPublicacionesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
