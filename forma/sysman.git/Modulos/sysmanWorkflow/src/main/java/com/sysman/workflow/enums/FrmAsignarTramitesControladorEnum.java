/*-
 * FrmAsignarTramitesControladorEnum.java
 *
 * 1.0
 * 
 * 6/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado utilizado para clasificar las claves de los parametros
 * utilizados en las colecciones del controlador
 * {@link com.sysman.workflow.FrmAsignarTramitesControlador}.
 * 
 * @version 1.0, 6/06/2018
 * @author pespitia
 *
 */
public enum FrmAsignarTramitesControladorEnum {

    PR_NODO_ACTUAL("PR_NODO_ACTUAL"),

    PR_PROCESO("PR_PROCESO"),

    PR_PROCESO_NOM("PR_PROCESO_NOM"),

    PR_TIPO_TRAMITE("PR_TIPO_TRAMITE"),

    PR_TIPO_TRAMITE_NOM("PR_TIPO_TRAMITE_NOM"),

    PR_TRAMITE("PR_TRAMITE"),

    PR_D_TRAMITE("PR_D_TRAMITE"),

    PR_USUARIO_INT_TRAMITE("PR_USUARIO_INT_TRAMITE");

    private final String value;

    private FrmAsignarTramitesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
