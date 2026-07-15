/*
 * FormularioCorreccionCriticaEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum FormularioCorreccionCriticaEnum {

    SP_USUARIO("SP_USUARIO"),

    SP_USUARIO_PROBLEMA("SP_USUARIO_PROBLEMA"),

    LECTURAAFORO("LECTURAAFORO"),

    DESVIACIONAFORO("DESVIACIONAFORO"),

    FRM_CORRECCIONCRITICA_LIS("FRM_CORRECCIONCRITICA_LIS"),

    FRM_CORRECCIONCRITICA_PROB("FRM_CORRECCIONCRITICA_PROB"),

    LECTURA("LECTURA"),

    KEY_CODIGORUTA("KEY_CODIGORUTA");

    private final String value;

    private FormularioCorreccionCriticaEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
