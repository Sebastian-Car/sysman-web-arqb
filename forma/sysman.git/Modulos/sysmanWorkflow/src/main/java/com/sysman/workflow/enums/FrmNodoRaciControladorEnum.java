/*-
 * FrmNodosRaciControladorEnum.java
 *
 * 1.0
 * 
 * 20/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 20/04/2018
 * @author lbotia
 *
 */
public enum FrmNodoRaciControladorEnum {

    CATEGORIA("CATEGORIA"),

    PR_CODIGO_PROCESO("PR_CODIGO_PROCESO"),

    PR_CODIGO_NODO("PR_CODIGO_NODO"),

    CODIGO_PROCESO("CODIGO_PROCESO"),

    CODIGO_NODO("CODIGO_NODO");

    private final String value;

    private FrmNodoRaciControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
