/*-
 * FrmPiTiposControladorUrlEnum.java
 *
 * 1.0
 * 
 * 27/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.planDesarrollo.enums;

/**
 * 
 * @version 1.0, 06/03/2018
 * @author ybecerra
 *
 */
public enum FrmPiTiposControladorUrlEnum {

    URL145("FRMPITIPOSCONTROLADORURL145", "552029"),

    URL168("FRMPITIPOSCONTROLADORURL168", "1015001");

    private final String key;
    private final String value;

    private FrmPiTiposControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
