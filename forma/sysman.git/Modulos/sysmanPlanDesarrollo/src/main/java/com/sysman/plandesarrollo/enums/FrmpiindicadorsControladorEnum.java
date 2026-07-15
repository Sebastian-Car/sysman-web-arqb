/*-
 * ActualizaparametrosretroactivosControladorEnum.java
 *
 * 1.0
 * 
 * 18/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 27/02/2018
 * @author jhernandez
 *
 */
public enum FrmpiindicadorsControladorEnum {

    GETMPRO("GETM_PRO"),

    GETMRES("GETM_RES"),

    ID_PLAN("ID_PLAN"),

    CODIGO("CODIGO"),

    ODS("ODS");

    private final String value;

    private FrmpiindicadorsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
