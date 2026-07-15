/*-
 * ConfigurarPlanContableExsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 3 dic. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.cgr.enums;

/**
 * 
 * @version 1.0, 3 dic. 2018
 * @author ybecerra
 *
 */
public enum CodigosfutsControladorUrlEnum {

    URL162("CODIGOSFUTSCONTROLADORURLENUM162", "1031030");

    private final String key;
    private final String value;

    private CodigosfutsControladorUrlEnum(String key,
        String value) {
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
