/*
 * UsuariospredialsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum UsuariospredialsControladorEnum {

    TRPDES("TRPDES"),

    CODIGO_ESTRATO("CODIGO_ESTRATO"),

    PARAM17("TB_TB225"),

    PARAM16("INDICADOR_RESERVA"),

    PARAM15("indReserva"),

    PARAM14("NUMERO_PROCESO"),

    PARAM13("nomPropietario"),

    PARAM12("INDBORRADO"),

    PARAM11("ESTRATO_SOCIOECONOMICO"),

    PARAM10("direccionPredio"),

    PARAM9("CLASE_PREDIO"),

    PARAM8("retornoFormulario"),

    PARAM7("retorna"),

    PARAM6("nroOrden"),

    PARAM5("codigoPredio"),

    PARAM4("accion"),

    PARAM3("TIPO_MUTACION"),

    PARAM2("PAIS"),

    PARAM1("TRPRAN"),

    PARAM0("TRPCOD");

    private final String value;

    private UsuariospredialsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
