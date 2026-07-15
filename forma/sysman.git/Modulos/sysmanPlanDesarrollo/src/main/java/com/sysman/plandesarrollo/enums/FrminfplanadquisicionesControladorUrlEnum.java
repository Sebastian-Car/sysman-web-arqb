/*-
 * FrminfplanadquisicionesControladorUrlEnum.java
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 * 
 * @version 1.0, 05/03/2018
 * @author jhernandez
 *
 */
public enum FrminfplanadquisicionesControladorUrlEnum {

    URL132("FRMINFPLANADQUISICIONESCONTROLADORURL132", "4001");//dss de vigencias 1021001

    private final String key;
    private final String value;

    private FrminfplanadquisicionesControladorUrlEnum(String key,
        String value) {
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
