/*-
 * FrmWFProcesosControladorEnum.java
 *
 * 1.0
 * 
 * 23/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado que permite clasificar la clave de las colecciones
 * utilizadas en el controlador
 * {@link com.sysman.workflow.FrmWFProcesosControlador}
 * 
 * @version 1.0, 23/05/2018
 * @author pespitia
 *
 */
public enum FrmWFProcesosControladorEnum {

    PR_RID("PR_RID"),

    FECHA_CREACION("FECHA_CREACION"),

    RUTA_IMG_WF("RUTA_IMG_WF"),
    
    APLICACION("APLICACION"),
    
    URL_PROJUD("URL_PROJUD");

    private final String value;

    private FrmWFProcesosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
