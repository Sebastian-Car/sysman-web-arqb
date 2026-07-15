/*-
 * PeriodosspsControladorEnum.java
 *
 * 1.0
 * 
 * 14/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * 
 * @version 1.0, 14/06/2017
 * @author jeguerrero
 *
 */
public enum PeriodosspsControladorEnum {

    PARAM0("SP_PERIODO");

    private final String value;

    private PeriodosspsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}