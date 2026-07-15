/*-
 * FrmListadoFacturacionControladorUrlEnum.java
 *
 * 1.0
 * 
 * 8/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * 
 * @version 1.0, 8/11/2017
 * @author jcrodriguez
 *
 */
public enum FrmListadoFacturacionControladorUrlEnum {

    URL6416("FRMINFGRUPOSCONCEPTOSCONTROLADORURL6416", "14006"),

    URL5690("FRMINFGRUPOSCONCEPTOSCONTROLADORURL5690", "14010"),

    URL5627("FRMINFGRUPOSCONCEPTOSCONTROLADORURL5627", "663001"),

    URL5629("FRMINFGRUPOSCONCEPTOSCONTROLADORURL5629", "663003");

    private final String key;
    private final String value;

    private FrmListadoFacturacionControladorUrlEnum(String key, String value) {
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
