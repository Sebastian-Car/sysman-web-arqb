/*-
 * FrmequivalenciasControladorUrlEnums.java
 *
 * 1.0
 * 
 * 27/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.enums;

/**
 * 
 * @version 1.0, 27/02/2018
 * @author lbotia
 *
 */
public enum PorSectoresControladorUrlEnum {

    URL144("PORSECTORESCONTROLADORURL144", "4001"),

    URL186("PORSECTORESCONTROLADORURL186", "203005"),

    URL208("PORSECTORESCONTROLADORURL208", "62097"),

    URL232("PORSECTORESCONTROLADORURL232", "104028"),

    URL271("PORSECTORESCONTROLADORURL271", "104014");

    private final String key;
    private final String value;

    private PorSectoresControladorUrlEnum(String key, String value) {
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
