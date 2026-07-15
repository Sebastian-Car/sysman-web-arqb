/*-
 * FrmtipotransaccionsstsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 28/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * 
 * @version 1.0, 28/12/2017
 * @author jcrodriguez
 *
 */
public enum FrmtipotransaccionsstsControladorUrlEnum {
    URL001("FRMTIPOTRANSACCIONSSTSCONTROLADORURL003", "104008"),

    URL002("FRMTIPOTRANSACCIONSSTSCONTROLADORURL004", "104014");

    private final String key;
    private final String value;

    private FrmtipotransaccionsstsControladorUrlEnum(String key, String value) {
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
