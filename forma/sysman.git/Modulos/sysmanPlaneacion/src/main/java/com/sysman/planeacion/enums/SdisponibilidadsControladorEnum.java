/*
 * SdisponibilidadsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SdisponibilidadsControladorEnum {

    ORDENDESUMINISTRO("ORDENDESUMINISTRO"),

    NSOLICITUDDISPON("NSOLICITUDDISPON"),

    PARAMETRO_NUMEROSOLDIS("NUMERO SOLICITUD DISPONIBILIDAD"),

    FORMATO_SOLDIS("FORMATO SOLICITUD DISPONIBILIDAD"),

    SDP("SDP"),

    NUMERO_LOWER("numero"),

    VALORESTIMADO_LOWER("valorEstimado"),

    NSOLICITUD_LOWER("nsolicitud"),

    NUMERODEPENDENCIANOMBRE_LOWER("numeroDependenciaNombre"),

    CONCEPTO_LOWER("concepto"),

    REPORTE446("000446SDP2"),

    PR_STRSQL("PR_STRSQL"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),

    NOMBRERESPONSABLE("NOMBRERESPONSABLE"),

    VALORESTIMADO("VALORESTIMADO"),

    CONCEPTO("CONCEPTO"),
    
    DEPENDENCIANOMBRE("DEPENDENCIANOMBRE")

    ;

    private final String value;

    private SdisponibilidadsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
