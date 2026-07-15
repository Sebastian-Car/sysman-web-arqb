/*-
 * FrmMonitorHistorialControladorEnum.java
 *
 * 1.0
 * 
 * 8/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado que clasifica las cadenas utilizadas en el controlador:
 * {@link com.sysman.workflow.FrmMonitorHistorialControlador}.
 * 
 * @version 1.0, 8/05/2018
 * @author pespitia
 *
 */
public enum FrmMonitorHistorialControladorEnum {

    PR_PROCESO("PR_PROCESO"),

    PR_TIPO_TRAMITE("PR_TIPO_TRAMITE"),

    PR_TRAMITE("PR_TRAMITE");

    private final String value;

    private FrmMonitorHistorialControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
