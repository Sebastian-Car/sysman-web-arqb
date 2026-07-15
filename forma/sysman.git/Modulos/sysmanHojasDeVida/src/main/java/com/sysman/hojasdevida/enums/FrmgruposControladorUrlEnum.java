/*-
 * frmgruposControladorUrlEnum.java
 *
 * 1.0
 * 
 * 30/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * 
 * @version 1.0, 30/01/2018
 * @author fperez
 *
 */
public enum FrmgruposControladorUrlEnum {

    URL239("FRMEVGRUPOSCONTROLADORURL101",
                    "945001"), URL345("FRMEVGRUPOSCONTROLADORURL345", "938002");

    private final String key;
    private final String value;

    private FrmgruposControladorUrlEnum(String key, String value) {
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
