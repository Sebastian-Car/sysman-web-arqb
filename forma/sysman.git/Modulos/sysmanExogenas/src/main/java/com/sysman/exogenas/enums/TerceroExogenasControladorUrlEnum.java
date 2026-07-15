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

package com.sysman.exogenas.enums;

/**
 * 
 * @version 1.0, 3 dic. 2018
 * @author ybecerra
 *
 */
public enum TerceroExogenasControladorUrlEnum {

    URL131("TERCEROEXOGENASCONTROLADORURL169", "14182"),
    
    URL137("TERCEROEXOGENASCONTROLADORURL169", "14184");

    private final String key;
    private final String value;

    private TerceroExogenasControladorUrlEnum(String key,
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
