/*
 * CorreccioncriticasControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum CorreccioncriticasControladorEnum {
    ACCION("ACCION"),

    TB_TB3365("TB_TB3365"),

    TB_TB3364("TB_TB3364"),

    TB_TB3366("TB_TB3366"),

    USO("USO"),

    SP_ORDENTRABAJO("SP_ORDENTRABAJO"),

    LECTURA("LECTURA"),

    LECTURAAFORO("LECTURAAFORO"),

    FECHALECTURAAFORO("FECHALECTURAAFORO"),

    NOMBREAFORADOR("NOMBREAFORADOR"),

    CODIGOINTERNO("CODIGOINTERNO");

    private final String value;

    private CorreccioncriticasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
