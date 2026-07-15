/*-
 * SolicitudRapidaControladorEnum.java
 *
 * 1.0
 * 
 * 16/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 16/10/2018
 * @author jgomez
 *
 */
public enum SolicitudRapidaControladorEnum {
    TB4226("TB_TB4226"),

    TB4227("TB_TB4227"),

    TB4228("TB_TB4228"),

    TB4229("TB_TB4229"),

    TB4230("TB_TB4230"),

    TB4231("TB_TB4231"),

    TB4232("TB_TB4232"),

    REQUIERE_PERIODO("REQUIERE_PERIODO"),

    ;

    private final String value;

    private SolicitudRapidaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
