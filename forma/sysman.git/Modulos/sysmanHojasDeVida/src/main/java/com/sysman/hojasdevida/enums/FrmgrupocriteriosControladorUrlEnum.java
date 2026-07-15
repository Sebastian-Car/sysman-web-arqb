/*-
 * frmgrupocriteriosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 29/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * 
 * @version 1.0, 29/01/2018
 * @author fperez
 *
 */
public enum FrmgrupocriteriosControladorUrlEnum {

    URL143("FRMEVGRUPOCRITERIOSCONTROLADORURL143", "752008"),

    URL4444("FRMEVGRUPOCRITERIOSCONTROLADORURL143", "752007");

    private final String key;
    private final String value;

    private FrmgrupocriteriosControladorUrlEnum(String key, String value) {
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
