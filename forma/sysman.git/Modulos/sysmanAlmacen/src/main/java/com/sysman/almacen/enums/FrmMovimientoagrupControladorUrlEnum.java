/*-
 * FrmMovimientoagrupControladorUrlEnum.java
 *
 * 1.0
 * 
 * 30/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * 
 * @version 1.0, 30/07/2018
 * @author bcardenas
 *
 */
public enum FrmMovimientoagrupControladorUrlEnum {

    URL0001("FRMMOVIMIENTOAGRUPCONTROLADORURL0001", "112040"),

    URL0002("FRMMOVIMIENTOAGRUPCONTROLADORURL0002", "112042");

    private final String key;
    private final String value;

    private FrmMovimientoagrupControladorUrlEnum(String key, String value) {
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
