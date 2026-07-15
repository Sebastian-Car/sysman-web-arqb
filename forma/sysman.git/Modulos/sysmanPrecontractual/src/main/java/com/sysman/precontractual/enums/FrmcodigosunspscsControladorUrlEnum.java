/*-
 * FrmcodigosunspscsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 25/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.precontractual.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring.
 * 
 * @version 1.0, 25/08/2017
 * @author pespitia
 *
 */
public enum FrmcodigosunspscsControladorUrlEnum {

    URL0001("FRMCODIGOSUNSPSCSCONTROLADORURL0001", "118011");

    private final String key;
    private final String value;

    private FrmcodigosunspscsControladorUrlEnum(String key, String value) {
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
