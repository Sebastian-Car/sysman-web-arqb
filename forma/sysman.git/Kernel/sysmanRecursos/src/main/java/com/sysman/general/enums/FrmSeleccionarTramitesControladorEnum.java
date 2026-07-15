/*
 * FrmSeleccionarTramitesControladorEnum
 *
 * 1.0
 *
 * 18/07/2018
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @version 1.0, 18 jul. 2018
 * @author pespitia
 *
 */

public enum FrmSeleccionarTramitesControladorEnum {

    PR_D_TRAMITE("PR_D_TRAMITE"),

    PR_NODO_ACTUAL("PR_NODO_ACTUAL"),

    PR_PROCESO("PR_PROCESO"),

    PR_RUTA("PR_RUTA"),

    PR_TIPO_TRAMITE("PR_TIPO_TRAMITE"),

    PR_TRAMITE("PR_TRAMITE"),

    KEY_CODIGO_NODO_VARIABLE("KEY_CODIGO_NODO_VARIABLE"),

    KEY_CONSECUTIVO_TRAMITE("KEY_CONSECUTIVO_TRAMITE"),

    KEY_NAME_FILE("KEY_NAME_FILE"),

    KEY_NUMERO_TRAMITE("KEY_NUMERO_TRAMITE"),

    KEY_TIPO_TRAMITE("KEY_TIPO_TRAMITE"),

    ADJUNTO("ADJUNTO"),

    D_TRAMITE("D_TRAMITE"),

    DETALLE("DETALLE"),

    NODO("NODO"),

    NODO_ACTUAL("NODO_ACTUAL"),

    PROCESO("PROCESO"),

    TRAMITE("TRAMITE"),

    VARIABLE("VARIABLE");

    private final String value;

    private FrmSeleccionarTramitesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
