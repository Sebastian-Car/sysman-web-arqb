/*-
 * frmtipopremiosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 31/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase reservada para implementar recursos DSS para el controlador
 * Frmtipopremios.
 * 
 * @version 1.0, 31/01/2018
 * @author dnino
 *
 */
public enum FrmtipopremiosControladorUrlEnum {
    URL44("FRMTIPOPREMIOSCONTROLADORURL44",
                    "FRMTIPOPREMIOSCONTROLADORURL44");

    private final String key;
    private final String value;

    private FrmtipopremiosControladorUrlEnum(String key, String value) {
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
