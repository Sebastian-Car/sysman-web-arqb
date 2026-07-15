/*-
 * FrmNodoVariablesControladorEnum.java
 *
 * 1.0
 * 
 * 11/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado utilizado para nombrar los parametros que se reciben al
 * abrir el formulario y utilizados en los llamados a DSS.
 * 
 * @version 1.0, 11/04/2018
 * @author pespitia
 *
 */
public enum FrmNodoVariablesControladorEnum {

    PR_CODIGO_NODO("PR_CODIGO_NODO"),

    PR_CODIGO_PROCESO("PR_CODIGO_PROCESO"),

    PR_RUTA("PR_RUTA"),

    COD_REG_TABLA("COD_REG_TABLA"),

    PR_NODO_NOM("NODO_NOM"),

    PR_PROCESO_NOM("PROCESO_NOM"),

    ADJUNTO("ADJUNTO"),

    CATEGORIA("CATEGORIA"),

    CODIGO_NODO("CODIGO_NODO"),

    CODIGO_PROCESO("CODIGO_PROCESO"),

    MANEJA_ADJUNTO("MANEJA_ADJUNTO"),

    NODO("NODO"),

    NOMBRELARGO("NOMBRELARGO"),

    NOM_REG_TABLA("NOM_REG_TABLA"),

    PROCESO("PROCESO");

    private final String value;

    private FrmNodoVariablesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
