/*-
 * AuditoriausuariosControladorUrlEnum.java
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
public enum AuditoriausuariosControladorUrlEnum {

    URL00001("AUDITORIAUSUARIOSCONTROLADORURL00001", "348001");

    private final String key;
    private final String value;

    private AuditoriausuariosControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
