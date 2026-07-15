/*
 * EntidadesCapacitacionControladorEnum
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
public enum FrmEvaluacionesDetControladorEnum {

    CEDULA_EVALUADO("CEDULA_EVALUADO"),
    CEDULA_EVALUADOR("CEDULA_EVALUADOR"),
    CARGO_EVALUADO("CARGO_EVALUADO"),
    CARGO_EVALUADOR("CARGO_EVALUADOR"),
    SUCURSAL_EVALUADO("SUCURSAL_EVALUADO"),
    SUCURSAL_EVALUADOR("SUCURSAL_EVALUADOR"),
    CODIGO_EMPLEADO_EVALUADO("CODIGO_EMPLEADO_EVALUADO"),
    CODIGO_EMPLEADO_EVALUADOR("CODIGO_EMPLEADO_EVALUADOR"),
    ESCALAFON("ESCALAFON"),
    ESCALAFON_EVALUADO("ESCALAFON_EVALUADO"),
    ESCALAFON_EVALUADOR("ESCALAFON_EVALUADOR"),
    ID_DE_CARGO("ID_DE_CARGO"),
    TIPO("TIPO"),
    FECHACONSULTA("FECHACONSULTA");

    private final String value;

    private FrmEvaluacionesDetControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
