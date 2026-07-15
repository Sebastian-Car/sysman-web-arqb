/*-
 * FrmobligacionesypagosporproyectoEnum.java
 *
 * 1.0
 * 
 * 24/07/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * 
 * 
 * @version 1.0, 17/04/2017
 * @author jguerrero
 *
 */
public enum FrmobligacionesypagosporproyectoEnum {



    REFERENCIAINICIAL("REFERENCIAINICIAL");

    private final String value;

    private FrmobligacionesypagosporproyectoEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
