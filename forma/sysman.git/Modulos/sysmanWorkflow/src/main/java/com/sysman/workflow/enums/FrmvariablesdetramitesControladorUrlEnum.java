/*-
 * FrmvariablesdetramitesControladorUrlEnum.java
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
public enum FrmvariablesdetramitesControladorUrlEnum {

    URL3340("FRMVARIABLESDETRAMITESCONTROLADORURL3340", "1037001"),

    URL3341("FRMVARIABLESDETRAMITESCONTROLADORURL3341", "");

    private final String key;
    private final String value;

    private FrmvariablesdetramitesControladorUrlEnum(String key, String value)
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
