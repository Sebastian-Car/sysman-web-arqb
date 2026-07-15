/*
 * SubbpplanindicativometasControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubbpplanindicativometasControladorEnum {

    VIGENCIAMETA("VIGENCIAMETA"),

    ANIOFINAL("ANIOFINAL"),

    ANOINICIAL("ANOINICIAL"),

    CANTIDAD_PROGRAMADA("CANTIDAD_PROGRAMADA"),

    PONDERACION_META("PONDERACION_META"),

    VIGENCIA_META("VIGENCIA_META"),

    META_BRUTA("META_BRUTA"),

    DESCRIPCION_TEXTO_BP("DESCRIPCION_TEXTO_BP"),

    TIPO_META("TIPO_META"),

    META("META"),

    LB("LB"),

    CANT_PROG("CANT_PROG"),

    DESCRIPCION_INDICADOR("DESCRIPCION_INDICADOR"),

    VIGENCIA_FINAL("VIGENCIA_FINAL"),

    CANTIDAD_EJECUTADA("CANTIDAD_EJECUTADA"),

    VALOR_EJECUTADO_META("VALOR_EJECUTADO_META"),

    VIGENCIA_PLAN("VIGENCIA_PLAN"),

    ID_PLAN("ID_PLAN"),

    DESCRIPCION2("DESCRIPCION2"),

    TIPO_META_INDICADOR("TIPO_META_INDICADOR");

    private final String value;

    private SubbpplanindicativometasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
