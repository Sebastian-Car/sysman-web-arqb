/*-
 * FrmmodalidadprecontratosControladorUrlEnum.java
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
public enum FrmmodalidadprecontratosControladorUrlEnum {
    URL162323("FRMMODALIDADPRECONTRATOSCONTROLADORURLL162323", "111006"),

    URL162324("FRMMODALIDADPRECONTRATOSCONTROLADORURLL162324", "111008");
    private final String key;
    private final String value;

    private FrmmodalidadprecontratosControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
