/*
 * ResolucionessubsControladorEnum
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
public enum ResolucionessubsControladorEnum {
    DEPARTAMENTO("DEPARTAMENTO"),

    MUNICIPIO("MUNICIPIO"),

    ANIO("ANIO"),

    CODIGO_ANT("CODIGO_ANT"),

    AVALUO_ANT("AVALUO_ANT"),

    AVALUO_ANO("AVALUO_ANO"),

    AREA_M2("AREA_M2"),

    AREA_HA("AREA_HA"),

    AREA_CONSTRUIDA("AREA_CONSTRUIDA"),

    FECHAINGRESOSISTEMA("FECHAINGRESOSISTEMA"),

    DESTINO_ECONOMICO("DESTINO_ECONOMICO"),

    CANCELAINSCRIBE("CANCELAINSCRIBE"),

    PAGO_ANO("PAGO_ANO"),

    PAG_VAL("PAG_VAL"),

    TARIFA("TARIFA"),

    ULTIMA_TARIFA("ULTIMA_TARIFA"),

    ULTIMO_ANO("ULTIMO_ANO"),

    NUMEROORDEN("NUMEROORDEN"),

    REGISTRADOS("REGISTRADOS"),

    NUM_COM("NUM_COM"),

    DESTINOECONOMICO("DESTINOECONOMICO"),

    AREATERENOM2("AREATERENOM2"),

    AREAHECTARES("AREAHECTARES"),

    AREACONSTRUIDA("AREACONSTRUIDA"),

    ANO_CONSTRUCCION("ANO_CONSTRUCCION"),

    CODIGO_PADRE("CODIGO_PADRE"),

    NUMERODOCUMENTO("NUMERODOCUMENTO"),

    TRPCOD("TRPCOD"),

    TRPRAN("TRPRAN"),

    REGISTRADO("REGISTRADO"),

    ULTIMO_ANIO("ULTIMO_ANIO"),

    ULTIMO_PAGO("ULTIMO_PAGO"),

    PAIS("PAIS");

    private final String value;

    private ResolucionessubsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
