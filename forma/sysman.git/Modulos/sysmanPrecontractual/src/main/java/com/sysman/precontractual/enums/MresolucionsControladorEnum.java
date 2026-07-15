/*
 * CerrarprocesoControladorEnum
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
public enum MresolucionsControladorEnum {

    MODULO("MODULO"),

    FORM_ORIGENLOWER("formOrigen"),

    MRESOLUCIONLOWER("mresolucion"),

    COD_FORM_ORIGENLOWER("codFormOrigen"),

    PARAMETROS_PLANT_WLOWER("parametrosPlantW"),

    CODCONTRATONOMBRE("CODCONTRATONOMBRE"),

    TIPO_ESTUDIO2("TIPO_ESTUDIO2")

    ;

    private final String value;

    private MresolucionsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
