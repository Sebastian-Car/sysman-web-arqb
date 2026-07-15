/*
 * TarifasfgControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ConceptosdependientesControladorEnum {

    KEY_CONCEPTO_DEPENDIENTE("KEY_CONCEPTO_DEPENDIENTE"),

    KEY_CONCEPTO_INDEPENDIENTE("KEY_CONCEPTO_INDEPENDIENTE"),

    CONCEPTO_DEPENDIENTE("CONCEPTO_DEPENDIENTE"),

    CONCEPTO_INDEPENDIENTE("CONCEPTO_INDEPENDIENTE"),

    TIPOCOBRO("TIPOCOBRO");

    private final String value;

    private ConceptosdependientesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
