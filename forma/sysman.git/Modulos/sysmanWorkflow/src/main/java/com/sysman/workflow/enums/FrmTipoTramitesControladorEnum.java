/*-
 * FrmTipoTramitesControladorEnum.java
 *
 * 1.0
 * 
 * 19/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado que contiene las claves de los parametros enviados al
 * llamar los DSS.
 * 
 * @version 1.0, 19/04/2018
 * @author pespitia
 *
 */
public enum FrmTipoTramitesControladorEnum {

    PROCESOS("PROCESOS"),

    TIPOTRAMITE("TIPOTRAMITE");

    private final String value;

    private FrmTipoTramitesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
