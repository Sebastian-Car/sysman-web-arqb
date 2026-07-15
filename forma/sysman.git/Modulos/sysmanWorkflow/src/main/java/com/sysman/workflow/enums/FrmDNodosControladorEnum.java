/*-
 * FrmDNodosControladorEnum.java
 *
 * 1.0
 * 
 * 13/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado utilizado para centralizar las cadenas y parametros
 * utilizados en el controlador:
 * {@link com.sysman.workflow.FrmDNodosControlador}
 * 
 * @version 1.0, 13/04/2018
 * @author pespitia
 *
 */
public enum FrmDNodosControladorEnum {

    PR_RID("PR_RID"),

    PR_PROCESO("PR_PROCESO"),

    PROCESO("PROCESO");

    private final String value;

    private FrmDNodosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
