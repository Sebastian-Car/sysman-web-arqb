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
 * @version 1.0, 15/11/2018
 * @author ybecerra
 *
 */
public enum ProgramacionSectorControladorUrlEnum {

    URL126("PROGRAMACIONSECTORCONTROLADOR126", "4001");

    private final String key;
    private final String value;

    private ProgramacionSectorControladorUrlEnum(String key, String value) {
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
