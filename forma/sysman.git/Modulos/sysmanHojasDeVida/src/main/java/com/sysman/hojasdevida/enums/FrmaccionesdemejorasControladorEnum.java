/*-
 * FamiliaresControladorEnum.java
 *
 * 1.0
 *
 * 28/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 *
 * @version 1.0, 28/12/2017
 * @author spina
 *
 */
public enum FrmaccionesdemejorasControladorEnum {
    NUMEROEVALUACION("NUMEROEVALUACION"),

    CLASEEVALUACION("CLASEEVALUACION"),

    TIPOEVALUACION("TIPOEVALUACION"),

    CEDULAEVALUADOR("CEDULAEVALUADOR"),

    SUCURSALEVALUADOR("SUCURSALEVALUADOR"),

    CEDULAEVALUADO("CEDULAEVALUADO"),

    SUCURSALEVALUADO("SUCURSALEVALUADO"),

    EV_COMPETENCIAS("EV_COMPETENCIAS"),

    CONSECUTIVO("CONSECUTIVO"),

    CONSECUTIVO_COMPETENCIA("CONSECUTIVO_COMPETENCIA"),

    NUMERO_EVALUACION("NUMERO_EVALUACION"),

    CLASE_EVALUACION("CLASE_EVALUACION"),

    TIPO_COMPETENCIA("TIPO_COMPETENCIA"),

    NUMERO_MANUAL("NUMERO_MANUAL"),

    VERSION("VERSION"),

    ID_COMPETENCIA("ID_COMPETENCIA");

    private final String value;

    private FrmaccionesdemejorasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
