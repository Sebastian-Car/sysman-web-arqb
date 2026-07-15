/*-
 * TipoincapacidadsControladorEnum.java
 *
 * 1.0
 * 
 * 30/10/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * 
 * @version 1.0, 30/10/2017
 * @author jcrodriguez
 *
 */
public enum TipoincapacidadsControladorEnum {
    NOMBRE_CONCEPTO("NOMBRE_CONCEPTO"),

    NOMBRECONCEPTO("NOMBRECONCEPTO"),

    ID_DE_CONCEPTO("ID_DE_CONCEPTO"),

    MENUNIE("60602"),

    DESCNIE126("DESCNIE126");

    private final String value;

    private TipoincapacidadsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
