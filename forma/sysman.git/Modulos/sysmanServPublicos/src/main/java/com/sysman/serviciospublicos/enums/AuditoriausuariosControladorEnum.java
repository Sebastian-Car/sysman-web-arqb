/*-
 * AuditoriausuariosControladorEnum.java
 *
 * 1.0
 *
 * 15/05/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 *
 * @version 1.0, 15/05/2017
 * @author lcortes
 *
 */
public enum AuditoriausuariosControladorEnum {

    PARAM0("SP_AUDITORIA_USUARIO");

    private final String value;

    private AuditoriausuariosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
