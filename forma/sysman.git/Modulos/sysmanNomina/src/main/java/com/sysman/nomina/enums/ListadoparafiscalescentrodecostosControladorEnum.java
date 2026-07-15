/*
 * ListadoparafiscalescentrodecostosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ListadoparafiscalescentrodecostosControladorEnum {

    PARAM3("PARAM3"),

    PARAM4("PROCESO"),

    PARAM1("ID_DE_PROCESO"),

    PARAM2("ANO"),

    PARAM0("PARAM0"),

    PARAM5("PARAM5"),
	
	PARAM6("000452ListadoParafiscalesCentrosdeCostoSTR");

    private final String value;

    private ListadoparafiscalescentrodecostosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
