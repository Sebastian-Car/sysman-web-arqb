/*-
 * FamiliaresControladorEnum.java
 *
 * 1.0
 *
 * 28/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 *
 * @version 1.0, 25/06/2018
 * @author jromero
 *
 */
public enum FalertasControladorEnum {
    ID_DE_CARGO("ID_DE_CARGO"),

    NOMBRE_DEL_CARGO("NOMBRE_DEL_CARGO");

    private final String value;

    private FalertasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return null;
    }
}
