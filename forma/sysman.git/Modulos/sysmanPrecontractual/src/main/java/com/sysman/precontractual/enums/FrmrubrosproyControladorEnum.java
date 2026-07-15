/*
 * FrmrubrosproyControladorEnum
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
public enum FrmrubrosproyControladorEnum {
    VLR_SOLICITADO("VLR_SOLICITADO"),

    SUMAVLRSOLICITADO("SUMAVLRSOLICITADO"),

    DESTINO("DESTINO"),

    VALORTOTAL("VALORTOTAL"),

    COD_ESTUDIO("COD_ESTUDIO"),

    PARAMETRO_COMILLAS("PARAMETRO_COMILLAS"),

    FUENTE_RECURSOS_AUX("FUENTE_RECURSOS_AUX"),

    FUENTE_RECURSOS("FUENTE_RECURSOS"),

    NOMBRE_FUENTE("NOMBRE_FUENTE"),

    COD_PROYECTO("COD_PROYECTO"),

    ID_RUBRO("ID_RUBRO"),

    ID("ID"),

    MODULO("MODULO");

    private final String value;

    private FrmrubrosproyControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
