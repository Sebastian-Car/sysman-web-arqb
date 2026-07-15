/*-
 * FrmInformesSireciControladorUrlEnum.java
 *
 * 1.0
 * 
 * 15/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * Enum necesario para traer datos de los combos utilizando el dss
 * correspondientes
 * 
 * @version 1.0, 15/03/2018
 * @author crodriguez
 *
 */
public enum FrmInformesSireciControladorUrlEnum {
    URL_224("FRMINFORMESSIRECICONTROLADOR224", "32045"),

    URL_247("FRMINFORMESSIRECICONTROLADOR247", "32046"),

    URL_300("FRMINFORMESSIRECICONTROLADOR300", "433005");

    private final String key;
    private final String value;

    private FrmInformesSireciControladorUrlEnum(String key, String value)
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
