/*
 * InfTransaccionesSstControladorEnum
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
public enum InfTransaccionesSstControladorEnum {

    TIPO_TRANSACCION("TIPO_TRANSACCION"),

    TIPOINICIAL("TIPOINICIAL"),

    CEDULA("CEDULA"),

    TERCEROINICIAL("TERCEROINICIAL"),

    CLASEEVENTOINICIAL("CLASEEVENTOINICIAL"),

    ACTIVIDAD("ACTIVIDAD"),

    ACTIVIDADINICIAL("ACTIVIDADINICIAL"),

    AGENTEINICIAL("AGENTEINICIAL"),

    TIPOFINAL("TIPOFINAL");

    private final String value;

    private InfTransaccionesSstControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
