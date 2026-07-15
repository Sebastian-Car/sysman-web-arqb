/*-
* FrmProyeccionesTramitesControladorEnum.java
*
* 1.0
* 
* 25/07/2018
* 
* Copyright (c) 2016 Stefanini Sysman.
* Paipa, Boyaca.
* All rights reserved.
*/
package com.sysman.workflow.enums;

/**
 * Enumerado que permite clasificar las claves utilizadas en el
 * controlador:
 * {@link com.sysman.workflow.FrmProyeccionesTramitesControlador}
 * 
 * @version 1.0, 25/07/2018
 * @author pespitia
 *
 */

public enum FrmProyeccionesTramitesControladorEnum {

    PR_PROCESO("PR_PROCESO"),

    PR_TIPO_TRAMITE("PR_TIPO_TRAMITE"),

    PR_TRAMITE("PR_TRAMITE"),

    ESTIMADO_DIAS("ESTIMADO_DIAS"),

    ESTIMADO_FECHA("ESTIMADO_FECHA"),

    NODO("NODO"),

    NODO_NOM("NODO_NOM"),

    PROCESO("PROCESO"),

    REAL_DIAS("REAL_DIAS"),

    REAL_FECHA("REAL_FECHA"),

    TRAMITE("TRAMITE");

    private final String value;

    private FrmProyeccionesTramitesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
