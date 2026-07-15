/*
 * ActualizarPagosControladorEnum
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
public enum AdicionespcontratosControladorEnum {

    FILTRARLM5("PF('LM5s').filter()"),

    NUMERACIONUNICA("NUMERACIONUNICA"),

    PLAZOENTREGA("PLAZOENTREGA"),

    FECHAFINALIZACION("FECHAFINALIZACION"),

    DIGITOSREDONDEAR("DIGITOSREDONDEAR"),

    ORDENADORMODIF("ORDENADORMODIF"),

    TIPOCONTRATO("TIPOCONTRATO"),

    SUCURSALCESION("SUCURSALCESION"),

    NITCESION("NITCESION"),

    NUMEROCONTRATO("NUMEROCONTRATO"),

    CEDULA("CEDULA"),

    NIT("NIT"),

    NUMEROAFECTADO("NUMEROAFECTADO"),

    TIPOPPTO("TIPOPPTO"),

    FECHAORDEN("FECHAORDEN"),

    TIPOAFECTADO("TIPOAFECTADO"),

    TIPO("TIPO");

    private final String value;

    private AdicionespcontratosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
