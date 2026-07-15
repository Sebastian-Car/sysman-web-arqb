/*-
 * FrmvariablesdetramitesControladorEnum.java
 *
 * 1.0
 *
 * 19/04/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 *
 * @version 1.0, 19/04/2018
 * @author spina
 *
 */
public enum FrmvariablesdetramitesControladorEnum {

    CODIGO_PROCESO("CODIGO_PROCESO"),

    ESTADO("ESTADO"),

    PROCESOS("PROCESOS");

    private final String value;

    private FrmvariablesdetramitesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
