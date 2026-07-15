/*
 * FrmcompromisoslaboralesControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum FrmcompromisoslaboralesControladorEnum {

    NUMEROEVALUACION("NUMEROEVALUACION"),

    CLASEEVALUACION("CLASEEVALUACION"),

    TIPOEVALUACION("TIPOEVALUACION"),

    CLASE_EVALUACION("CLASE_EVALUACION"),

    NUMERO_EVALUACION("NUMERO_EVALUACION"),

    TIPO_EVALUACION("TIPO_EVALUACION"),

    CEDULAEVALUADO("CEDULAEVALUADO"),

    SUCURSALEVALUADO("SUCURSALEVALUADO"),

    CEDULAEVALUADOR("CEDULAEVALUADOR"),

    SUCURSALEVALUADOR("SUCURSALEVALUADOR"),

    CEDULA_EVALUADO("CEDULA_EVALUADO"),

    SUCURSAL_EVALUADO("SUCURSAL_EVALUADO"),

    CEDULA_EVALUADOR("CEDULA_EVALUADOR"),

    CODIGO_META("CODIGO_META"),

    SUCURSAL_EVALUADOR("SUCURSAL_EVALUADOR"),

    NOMBREMETA("NOMBREMETA"),

    META("META"),

    TIPO_COMPETENCIA("TIPO_COMPETENCIA"),

    CONSECUTIVO_COMPETENCIA("CONSECUTIVO_COMPETENCIA");

    private final String value;

    private FrmcompromisoslaboralesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
