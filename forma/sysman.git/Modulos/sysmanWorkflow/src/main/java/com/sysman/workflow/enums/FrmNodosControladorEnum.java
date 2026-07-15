/*-
 * FrmNodosControladorEnum.java
 *
 * 1.0
 * 
 * 10/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado que permite controlar las cadenas que generan alertas en
 * SonarLint y utilizadas como clave en los parametros.
 * 
 * @version 1.0, 10/04/2018
 * @author pespitia
 *
 */
public enum FrmNodosControladorEnum {

    PR_PROCESO("PR_PROCESO"),

    PR_RID("PR_RID"),

    APLICACION("APLICACION"),

    CATEGORIA("CATEGORIA"),

    CODIGO_PROCESO("CODIGO_PROCESO"),

    MENU("MENU"),

    NODO("NODO"),

    PROCESO("PROCESO"),

    PROCESO_NOM("PROCESO_NOM"),

    S_DOC_ESTANDAR("S_DOC_ESTANDAR"),

    TIPO("TIPO"),

    VARIABLE("VARIABLE");

    private final String value;

    private FrmNodosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
