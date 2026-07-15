/*-
 * FrmMonitorHistorialControladorUrlEnum.java
 *
 * 1.0
 * 
 * 17/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumeracion que permite clasificar cada unos de los DSS utilizados
 * en el controlador
 * {@link com.sysman.workflow.FrmMonitorHistorialControlador}.
 * 
 * @version 1.0, 17/05/2018
 * @author pespitia
 *
 */
public enum FrmMonitorHistorialControladorUrlEnum {

    URL0001("FRMMONITORHISTORIALCONTROLADORURL0001", "1045002");

    private final String key;
    private final String value;

    private FrmMonitorHistorialControladorUrlEnum(String key, String value) {
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
