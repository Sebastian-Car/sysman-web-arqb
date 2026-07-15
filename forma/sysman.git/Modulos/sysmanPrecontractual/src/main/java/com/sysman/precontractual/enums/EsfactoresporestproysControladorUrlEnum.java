/*-
 * EsfactoresporestproysControladorUrlEnum.java
 *
 * 1.0
 * 
 * 24/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.precontractual.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * del refactoring.
 * 
 * @version 1.0, 24/08/2017
 * @author pespitia
 *
 */
public enum EsfactoresporestproysControladorUrlEnum {

    URL0001("ESFACTORESPORESTPROYSCONTROLADORURL0001", "489001");

    private final String key;
    private final String value;

    private EsfactoresporestproysControladorUrlEnum(String key, String value) {
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
