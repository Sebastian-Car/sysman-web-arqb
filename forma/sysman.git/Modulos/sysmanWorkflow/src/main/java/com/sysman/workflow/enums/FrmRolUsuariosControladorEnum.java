/*-
 * FrmRolUsuariosControladorEnum.java
 *
 * 1.0
 * 
 * 25/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 25/04/2018
 * @author lbotia
 *
 */
public enum FrmRolUsuariosControladorEnum {

    CODIGO_ROL("CODIGO_ROL"),

    CODIGO("CODIGO"),

    NOMBRE("NOMBRE"),

    USUARIO("USUARIO"),

    NOMBRE_DEPENDENCIA("NOMBRE_DEPENDENCIA"),

    DEPENDENCIA("DEPENDENCIA"),

    PR_CODIGO_ROL("PR_CODIGO_ROL");

    private final String value;

    private FrmRolUsuariosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
