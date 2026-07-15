/*-
 * TiporespuestaspqrsControladorEnum.java
 *
 * 1.0
 * 
 * 20/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * 
 * @version 1.0, 20/06/2017
 * @author jeguerrero
 *
 */
public enum TiporespuestaspqrsControladorEnum {

    PARAM3("PARAM3"),

    PARAM1("PARAM1"),

    PARAM2("TIPO"),

    PARAM0("SP_TIPORESPUESTA_PQR");

    private final String value;

    private TiporespuestaspqrsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
