/*
 * SdentdevolutivoactivosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum SdentdevolutivoactivosControladorEnum {
    TB_TB1930("TB_TB1930"),
    TB_TB1928("TB_TB1928"),
    TB_TB1929("TB_TB1929"),
    HORA("HORA"),
    TIPOACTIVO_ANT("TIPOACTIVO_ANT"),
    NOMBRETIPOACTIVOFIN("NOMBRETIPOACTIVOFIN"),
    CODIGO_TIPOACTIVO("CODIGO_TIPOACTIVO"),
    CONSECUTIVOCTA("CONSECUTIVOCTA"),
    INDREG("IND_REG"),
    TIPOACTIVO("NIIF_TIPO_ACTIVO"),
    VALORBASE("NIIF_VALOR_BASE"),
    VALORTOTAL("NIIF_VALOR_TOTAL"),
    NOMBRELARGO("NOMBRELARGO"),
    NOMBRETIPOACT("NOMBRETIPOACTIVO"),
    NOMBRE_TIPOACTIVO("NOMBRE_TIPOACTIVO"),
    VALORBASED("VALORBASED"),
    VALORTOTALD("VALORTOTALD"),
    PARAMETROTIPOMOV("PARAMETROTIPOMOV"),
    TIPOMOVIMIENTOCTA("TIPOMOVIMIENTOCTA");

    private final String value;

    private  SdentdevolutivoactivosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
