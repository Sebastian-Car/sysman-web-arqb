/*
 * SubpactesoreriasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubpactesoreriasControladorEnum {

    PARAM5("KEY_MES"), PARAM4("MES"), PARAM3("MES"), PARAM2(
                    "PACTESORERIA"), PARAM1(
                                    "$ #,##0.00"), PARAM0(
                                                    "SALDO_PLAN_PPTAL");

    private final String value;

    private SubpactesoreriasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
