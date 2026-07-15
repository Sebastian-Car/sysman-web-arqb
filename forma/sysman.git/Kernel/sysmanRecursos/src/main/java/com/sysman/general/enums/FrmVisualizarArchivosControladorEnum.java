/*-
 * FrmVisualizarArchivosControladorEnum.java
 *
 * 1.0
 * 
 * 18 jun. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * Enumerado que permite centralizar y clasificar las claves de los
 * parametros utilizados en el controlador:
 * {@link com.sysman.general.FrmVisualizarArchivosControlador}.
 * 
 * @version 1.0, 18 jun. 2018
 * @author pespitia
 *
 */
public enum FrmVisualizarArchivosControladorEnum {

    PR_RUTA("PR_RUTA"),

    PR_TIPO_ARCHIVO("PR_TIPO_ARCHIVO"),

    PR_TITULO("PR_TITULO");

    private final String value;

    private FrmVisualizarArchivosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
