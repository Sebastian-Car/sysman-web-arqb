/*-
 * FrmRolesControladorEnum.java
 *
 * 1.0
 * 
 * 16/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * 
 * 
 * @version 1.0, 16/04/2018
 * @author lbotia
 *
 */
public enum FrmRolesControladorEnum {

    CODIGO_ROL("CODIGO_ROL");

    private final String value;

    private FrmRolesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
