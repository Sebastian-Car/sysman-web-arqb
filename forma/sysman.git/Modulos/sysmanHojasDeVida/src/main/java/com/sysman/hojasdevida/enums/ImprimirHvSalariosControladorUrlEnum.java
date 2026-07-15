/*-
 * ImprimirHvSalariosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 13/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @version 1.0, 13/12/2017
 * @author jcrodriguez
 *
 */
public enum ImprimirHvSalariosControladorUrlEnum {
    URL3323("IMPRIMIRHVSALARIOSCONTROLADORURLENUM3323", "685001"),

    URL3325("IMPRIMIRHVSALARIOSCONTROLADORURLENUM3325", "685003");

    private final String key;
    private final String value;

    private ImprimirHvSalariosControladorUrlEnum(String key, String value) {
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
