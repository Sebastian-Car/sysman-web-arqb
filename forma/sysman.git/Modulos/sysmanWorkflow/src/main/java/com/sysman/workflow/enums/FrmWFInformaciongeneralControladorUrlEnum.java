/*-
 * DTramiteVariablesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 10/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado que contiene los codigos de los DSS utilizados en el
 * controlador
 * {@link com.sysman.workflow.FrmWFInformaciongeneralControlador}.
 * 
 * @version 1.0, 17/03/2021
 * @author jacevedo
 *
 */
public enum FrmWFInformaciongeneralControladorUrlEnum {


    
    URL001("FRMWFINFORMACIONGENERALENUM001", "1847004");

    private final String key;
    private final String value;

    private FrmWFInformaciongeneralControladorUrlEnum(String key, String value) {
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
