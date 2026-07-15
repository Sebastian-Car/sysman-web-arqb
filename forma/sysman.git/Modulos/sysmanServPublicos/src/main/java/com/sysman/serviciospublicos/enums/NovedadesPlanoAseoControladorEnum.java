/*
 * NovedadesPlanoAseoControladorEnum
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
public enum NovedadesPlanoAseoControladorEnum {

    PARAM0("FIMM"),

    PARAM1("ID"),

    PARAM2("NIT"),

    PARAM3("EMPRESA"),

    PARAM4("ID_EMPRESA"),

    PARAM5("VALOR_COB"),

    PARAM6("APLICA RESOLUCION CRA 720"),

    PARAM7("FACTURACION EN SITIO"),

    PARAM8("CARGA UNIDADES NO RESIDENCIAL EN ASEO CONJUNTO"),

    ;

    private final String value;

    private NovedadesPlanoAseoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
