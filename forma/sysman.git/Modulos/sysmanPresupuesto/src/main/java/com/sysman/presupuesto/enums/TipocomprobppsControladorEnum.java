/*
 * TipocomprobppsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum TipocomprobppsControladorEnum {

    PARAM2("KEY_TIPOCOMPROBANTE"), PARAM1("CONSECUTIVOTCP"), PARAM0(
                    "TIPOCOMPROBANTE");

    private final String value;

    private TipocomprobppsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
