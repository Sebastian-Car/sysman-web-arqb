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

package com.sysman.nomina.enums;

/**
 * Enumerado utilizado para centralizar las cadenas y parametros
 * utilizados en el controlador:
 * {@link com.sysman.workflow.FrmMonitorTramitesControlador}
 * 
 * @version 1.0, 02/05/2018
 * @author jmalaver
 *
 */
public enum SubformdetallevacacionesControladorEnum {

    KEY_PROCESOS("KEY_PROCESOS"),

    FECHA_FINAL("FECHA_FINAL"),

    FECHA_INICIO_DISFRUTE("FECHA_INICIO_DISFRUTE"),

    FECHA_ESTIMADA_REGRESO("FECHA_ESTIMADA_REGRESO"),

    DIAS_ESTIMADOS_DISFRUTE("DIAS_ESTIMADOS_DISFRUTE");

    private final String value;

    private SubformdetallevacacionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
