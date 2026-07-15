/*-
 * FrmlistadoRecaudoDifUrlEnum.java
 *
 * 1.0
 * 
 * 8/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * 
 * @version 1.0, 19/03/2021
 * @author eamaya
 *
 */
public enum FrmEliminarFaeUrlEnum {

    URL2564("FRMELIMINARFAEURL2564", "1862001"),

    URL4589("FRMELIMINARFAEURL4589", "186200C"),;

    private final String key;
    private final String value;

    private FrmEliminarFaeUrlEnum(String key, String value) {
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
