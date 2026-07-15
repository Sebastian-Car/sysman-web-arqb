/*
 * FacturaConveniosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum FacturaConveniosControladorEnum {

    /**
     * NOMBREPERIODO
     */
    PARAM0("NOMBREPERIODO"),
    /**
     * NOMBREBANCO
     */
    PARAM1("NOMBREBANCO"),
    /**
     * ID_EMPRESA
     */
    PARAM2("ID_EMPRESA");

    private final String value;

    private FacturaConveniosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
