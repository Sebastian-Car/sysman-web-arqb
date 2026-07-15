/*-
 * FrmlistadoRecaudoDifUrlEnum.java
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
public enum FrmRecaudoFacturacionControladorUrlEnum {

    URL0001("ACCIONESCCONTRATOCONTROLADORURL0001", "29123"),

    URL0002("ACCIONESCCONTRATOCONTROLADORURL0002", "29125"),

    URL0003("ACCIONESCCONTRATOCONTROLADORURL0003", "663011"),

    URL0004("ACCIONESCCONTRATOCONTROLADORURL0004", "663012"),

    URL0005("ACCIONESCCONTRATOCONTROLADORURL0005", "14001"),

    URL0006("ACCIONESCCONTRATOCONTROLADORURL0006", "14026");

    private final String key;
    private final String value;

    private FrmRecaudoFacturacionControladorUrlEnum(String key, String value) {
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
