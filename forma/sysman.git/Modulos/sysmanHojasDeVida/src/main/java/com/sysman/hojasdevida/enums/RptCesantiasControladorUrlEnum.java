/*-
 * RptcesantiasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 11/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 11/01/2018
 * @author dnino
 *
 */
public enum RptCesantiasControladorUrlEnum {

    URL4170("RPTCESANTIASCONTROLADORURLENUM4170", "685001"),

    URL1704("RPTCESANTIASCONTROLADORURLENUM1704", "685003");

    private final String key;
    private final String value;

    private RptCesantiasControladorUrlEnum(String key, String value) {
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