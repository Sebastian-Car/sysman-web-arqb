/*
 * FrmriesgosproysControladorEnum
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
public enum FrmriesgosproysControladorEnum {

    SUCURSAL_RESP_RECIBIDO("SUCURSAL_RESP_RECIBIDO"),

    DETALLE("DETALLE"),

    COD_RIESGO("COD_RIESGO"),

    COD_T_RIESGO("COD_T_RIESGO"),

    RESP_RECIBIDO("RESP_RECIBIDO"),

    CEDULA("CEDULA"),

    NOMRESPONSABLE("NOMRESPONSABLE"),

    IMPACTO("IMPACTO"),

    FUENTE("FUENTE"),

    ETAPA("ETAPA"),

    PROBABILIDAD("PROBABILIDAD"),

    VALORACION("VALORACION"),

    NOMBRE_IMPACTO("NOMBRE_IMPACTO"),

    CATEGORIA("CATEGORIA"),

    CATEGORIA_DESPUES("CATEGORIA_DESPUES"),

    IMPACTO_DESPUES("IMPACTO_DESPUES"),

    ANTES("ANTES"),

    DESPUES("DESPUES"),

    NOMBRE_FUENTE("NOMBRE_FUENTE"),

    NOMBRE_IMPACTO_DESPUES("NOMBRE_IMPACTO_DESPUES"),

    NOMBRE_PROBABILIDAD("NOMBRE_PROBABILIDAD"),

    NOMBRE_PROBABILIDAD_DESPUES("NOMBRE_PROBABILIDAD_DESPUES"),

    PROBABILIDAD_DESPUES("PROBABILIDAD_DESPUES"),

    NOMBRE_TIPO("NOMBRE_TIPO"),

    NOMBRE_ETAPA("NOMBRE_ETAPA")

    ;

    private final String value;

    private FrmriesgosproysControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
