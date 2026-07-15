/*-
 * FrmVisualizarAcuerdosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 20/05/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * 
 * @version 1.0, 20/05/2019
 * @author bcardenas
 *
 */
public enum FrmVisualizarAcuerdosControladorUrlEnum {

    URL282("FRMVISUALIZARACUERDOSCONTROLADORURL", "1797001"),

    URL1796("FRMVISUALIZARACUERDOSCONTROLADORURL", "1796003"),

    URL1798("FRMVISUALIZARACUERDOSCONTROLADORURL", "1798001");

    private final String key;
    private final String value;

    private FrmVisualizarAcuerdosControladorUrlEnum(String key, String value) {
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