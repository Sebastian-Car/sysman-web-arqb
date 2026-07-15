/*-
 * FrmevevidenciasControladorEnum.java
 *
 * 1.0
 *
 * 08/02/2018
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum FrmevevidenciasControladorEnum {

    NUMEROEVALUACION("NUMEROEVALUACION"),

    CLASEEVALUACION("CLASEEVALUACION"),

    TIPOEVALUACION("TIPOEVALUACION"),

    CEDULAEVALUADO("CEDULAEVALUADO"),

    CEDULAEVALUADOR("CEDULAEVALUADOR"),

    SUCURSALEVALUADO("SUCURSALEVALUADO"),

    SUCURSALEVALUADOR("SUCURSALEVALUADOR"),

    EV_COMPETENCIAS("EV_COMPETENCIAS"),

    NUMERO_MANUAL("NUMERO_MANUAL"),

    VERSION("VERSION"),

    CONSECUTIVO("CONSECUTIVO"),

    TIPO_COMPETENCIA("TIPO_COMPETENCIA"),

    ID_COMPETENCIA("ID_COMPETENCIA"),

    NUMERO_EVALUACION("NUMERO_EVALUACION"),

    TIPO_EVALUACION("TIPO_EVALUACION"),

    CLASE_EVALUACION("CLASE_EVALUACION"),

    CEDULA_EVALUADO("CEDULA_EVALUADO"),

    CEDULA_EVALUADOR("CEDULA_EVALUADOR"),

    SUCURSAL_EVALUADO("SUCURSAL_EVALUADO"),

    SUCURSAL_EVALUADOR("SUCURSAL_EVALUADOR");

    private final String value;

    private FrmevevidenciasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
