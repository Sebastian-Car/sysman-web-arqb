/*-
 * PeriodosspsControladorUrlEnum.java
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
public enum PeriodosspsControladorUrlEnum {
    URL4104("PERIODOSSPSCONTROLADORURL4104", "4019");

    private final String key;
    private final String value;

    private PeriodosspsControladorUrlEnum(String key, String value) {
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
