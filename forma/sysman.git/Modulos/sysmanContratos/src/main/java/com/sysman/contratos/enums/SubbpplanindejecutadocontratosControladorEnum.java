/*
 * SubbpplanindejecutadocontratosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubbpplanindejecutadocontratosControladorEnum {

    PARAM19("EXPR1"),

    PARAM18("ID_PLAN_N"),

    PARAM17("ID_PLAN_P"),

    PARAM16("NOMBREPROYECTO"),

    PARAM15("VIGENCIA_META_N"),

    PARAM14("VIGENCIA_META_P"),

    PARAM13("VIGENCIA_PLAN_P"),

    PARAM9("VIGENCIA_PLAN_N"),

    PARAM12("COMPONENTE"),

    PARAM23("CLASEORDEN_N"),

    PARAM11("NOVEDAD_N"),

    PARAM22("ORDENDECOMPRA_N"),

    PARAM10("CLASET_N"),

    PARAM21("TIPOT_N"),

    PARAM6("FECHA_N"),

    PARAM20("PORCENTAJE_EJECUCION"),

    PARAM5("PROYECTO"),

    PARAM8("CODIGOPROYECTO"),

    PARAM7("CONSECUTIVO"),

    PARAM2("1"),

    PARAM1("claseOrden"),

    PARAM4("numero"),

    PARAM3("novedad"),

    PARAM24("tipot"),

    PARAM25("claset"),

    PARAM26("fechan"),

    PARAM27("pejecucion"),;

    private final String value;

    private SubbpplanindejecutadocontratosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
