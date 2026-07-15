/*-
 * FrmDiferirFacturaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 9/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * 
 * @version 1.0, 9/11/2017
 * @author jcrodriguez
 *
 */
public enum FrmDiferirFacturaControladorUrlEnum {
    URL6322("FRMINFCONTABILIDADCONTROLADORURL6322", "665011"),

    URL6394("FRMINFCONTABILIDADCONTROLADORURL6394", "661011"),

    URL6325("FRMINFCONTABILIDADCONTROLADORURL6325", "665004"),

    URL6327("FRMINFCONTABILIDADCONTROLADORURL6327", "665011"),

    URL6330("FRMINFCONTABILIDADCONTROLADORURL6330", "665012"),

    URL6332("FRMINFCONTABILIDADCONTROLADORURL6332", "665010");

    private final String key;
    private final String value;

    private FrmDiferirFacturaControladorUrlEnum(String key, String value) {
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
