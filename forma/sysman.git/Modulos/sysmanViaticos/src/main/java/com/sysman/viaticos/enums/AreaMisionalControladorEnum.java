/*-
 * FamiliaresControladorEnum.java
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
 * @version 1.0, 17/01/2018
 * @author ybecerra
 *
 */
public enum AreaMisionalControladorEnum {

    PARAM0("CODIGOINICIAL"),

    PARAM1("");

    private final String value;

    private AreaMisionalControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
