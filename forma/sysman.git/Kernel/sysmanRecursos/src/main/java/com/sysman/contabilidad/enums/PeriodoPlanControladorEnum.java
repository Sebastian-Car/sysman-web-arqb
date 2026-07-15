/*
 * PeriodoPlanControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

import com.sysman.enums.GeneralParameterEnum;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum PeriodoPlanControladorEnum {

    PARAM0(GeneralParameterEnum.COMPANIA.getName());

    private final String value;

    private PeriodoPlanControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
