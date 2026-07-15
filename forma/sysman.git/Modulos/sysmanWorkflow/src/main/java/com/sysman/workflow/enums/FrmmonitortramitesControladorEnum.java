/*-
 * FrmDTramitesControladorEnum.java
 *
 * 1.0
 * 
 * 24/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado utilizado para centralizar las cadenas y parametros
 * utilizados en el controlador:
 * {@link com.sysman.workflow.FrmMonitorTramitesControlador}
 * 
 * @version 1.0, 02/05/2018
 * @author jmalaver
 *
 */
public enum FrmmonitortramitesControladorEnum {

    KEY_COMPANIA("KEY_COMPANIA"),

    KEY_PROCESOS("KEY_PROCESOS"),

    KEY_TIPO_TRAMITE("KEY_TIPO_TRAMITE"),

    KEY_NUMERO("KEY_NUMERO"),

    USUARIO_INTERNO("USUARIO_INTERNO"),

    PROCESOS("PROCESOS");

    private final String value;

    private FrmmonitortramitesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
