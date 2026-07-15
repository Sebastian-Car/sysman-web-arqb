/*
 * FrmesmetasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmesmetasControladorEnum {

    VALOR_PROGRAMADO_META("VALOR_PROGRAMADO_META"),

    VALOR_EJECUTADO_META("VALOR_EJECUTADO_META"),

    CANTIDAD_EJECUTADA_P("CANTIDAD_EJECUTADA_P"),

    CANTIDAD_META_EJECUTAR("CANTIDAD_META_EJECUTAR"),

    ES_PROY_METAS("ES_PROY_METAS"),

    BP_PLAN_INDICATIVO_METAS("BP_PLAN_INDICATIVO_METAS"),

    DESCRIPCION_META("DESCRIPCION_META"),

    VIGENCIA_PLAN_P("VIGENCIA_PLAN_P"),

    VIGENCIA_META_P("VIGENCIA_META_P"),

    TIPOCOMPONENTE("TIPOCOMPONENTE"),

    COMPONENTE("COMPONENTE"),

    ID_PLAN_P("ID_PLAN_P");

    private final String value;

    private FrmesmetasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
