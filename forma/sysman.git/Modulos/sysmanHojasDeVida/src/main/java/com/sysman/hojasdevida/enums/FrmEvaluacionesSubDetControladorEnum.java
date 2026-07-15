/*-
 * TipoClaseEventoSstsControladorEnum.java
 *
 * 1.0
 * 
 * 29 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
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
public enum FrmEvaluacionesSubDetControladorEnum {

    TABLA("EV_SUBDETALLE_EVALUACION"),
    EVALUACION("EVALUACION"),
    CEDULA_EVALUADO("CEDULA_EVALUADO"),
    CEDULA_EVALUADOR("CEDULA_EVALUADOR"),
    TIPO("TIPO"),
    ESCALAFON("ESCALAFON"),
    CRITERIO("CRITERIO"),
    CODIGO_EMPLEADO_EVALUADO("CODIGO_EMPLEADO_EVALUADO"),
    CODIGO_EMPLEADO_EVALUADOR("CODIGO_EMPLEADO_EVALUADOR"),
    ESCALAFON_EVALUADO("ESCALAFON_EVALUADO"),
    ESCALAFON_EVALUADOR("ESCALAFON_EVALUADOR"),
    ID_DE_CARGO("ID_DE_CARGO");

    private final String value;

    private FrmEvaluacionesSubDetControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
