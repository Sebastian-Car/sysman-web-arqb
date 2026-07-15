/*
 * FrmevrequisitosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmevrequisitosControladorEnum {

    EV_REQUISITOS("EV_REQUISITOS"),

    NUMERO_MANUAL("NUMERO_MANUAL"),

    ALTERNATIVA("ALTERNATIVA"),

    TIPO_REQUISITO("TIPO_REQUISITO"),

    VERSION("VERSION"),

    NOMBRE_MANUAL("NOMBRE_MANUAL"),

    EV_MANUAL("EV_MANUAL");

    private final String value;

    private FrmevrequisitosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
