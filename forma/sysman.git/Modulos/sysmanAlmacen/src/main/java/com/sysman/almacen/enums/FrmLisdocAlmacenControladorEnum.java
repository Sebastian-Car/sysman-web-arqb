/*-
 * FrmLisdocAlmacenControladorEnum.java
 *
 * 1.0
 * 
 * 19/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 19/07/2018
 * @author bcardenas
 *
 */
public enum FrmLisdocAlmacenControladorEnum {
    PR_FECHA_INICIAL("PR_FORMS_LISDOCALMACEN_FECHAINICIAL"),

    PR_FECHA_FINAL("PR_FORMS_LISDOCALMACEN_FECHAFINAL"),

    PR_STRSQL_SUBC_LISDOC("PR_STRSQL_SECUNDARIO123"),

    PR_STRSQL("PR_STRSQL"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA");

    private final String value;

    private FrmLisdocAlmacenControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
