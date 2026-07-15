/*
 * SolicitudserviciosControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum SolicitudserviciosControladorEnum {

    TIPO("TIPO"),

    PAIS("PAIS"),

    DEPARTAMENTO("DEPARTAMENTO"),

    USO("USO"),

    CLASESOLICITUD("CLASESOLICITUD"),

    SOLICITUD("SOLICITUD"),

    MSM_REGISTRO_ELIMINADO("MSM_REGISTRO_ELIMINADO"),

    MSM_REGISTRO_MODIFICADO("MSM_REGISTRO_MODIFICADO"),

    SOLICITUDSERVICIO("SOLICITUDSERVICIO"),

    BARRIO("BARRIO"),

    ANOINICIAL("ANOINICIAL"),

    NOMBREPERIODOINICIAL("NOMBREPERIODOINICIAL"),

    AREATOTAL("AREATOTAL"),

    AREACONSTRUIDARED("AREACONSTRUIDARED"),

    LONGFRENTEPREDIO("LONGFRENTEPREDIO"),

    LONGFONDOPREDIO("LONGFONDOPREDIO"),

    CUOTAS("CUOTAS"),

    INICIAL("INICIAL"),

    VALORCUOTA("VALORCUOTA"),

    VALORUNITARIO("VALORUNITARIO"),

    TEXTOTOTAL("TEXTOTOTAL"),

    CODIGODANE("CODIGODANE"),

    TIPOVIVIENDA("TIPOVIVIENDA"),

    TIPODEPREDIO("TIPODEPREDIO"),

    NOMBRECONCEPTO("NOMBRECONCEPTO"),;

    private final String value;

    private SolicitudserviciosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
