/*-
 * FrmmodalidadprecontratosControladorEnum.java
 *
 * 1.0
 * 
 * 28/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.precontractual.enums;

/**
 * 
 * @version 1.0, 28/08/2017
 * @author jcrodriguez
 *
 */
public enum FrmmodalidadprecontratosControladorEnum {
    MODALIDAD("MODALIDAD");
    private final String value;

    private FrmmodalidadprecontratosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
