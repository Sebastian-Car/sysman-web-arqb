/*
 * CertificadoEstratoControladorEnum
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
public enum CertificadoEstratoControladorEnum {
    DIRECCIONPREDIO("DIRECCIONPREDIO"), ESTRATOPREDIO("ESTRATOPREDIO"), FECHA_IMPRESO("FECHA_IMPRESO"), HORA_IMPRESO(
                    "HORA_IMPRESO"), SUCURSALPROPIETARIO("SUCURSALPROPIETARIO"), NITPROPIETARIO("NITPROPIETARIO"), CODIGOPREDIO(
                                    "CODIGOPREDIO");

    private final String value;

    private CertificadoEstratoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
