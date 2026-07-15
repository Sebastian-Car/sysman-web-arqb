/*
 * FinanciablesfacturasControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FinanciablesfacturasControladorEnum {

    PARAM19("USUARIOBLOQUEO"),

    PARAM18("HORABLOQUEO"),

    PARAM17("periodosNoCobradosFac"),

    PARAM16("periodosNoCobroFin"),

    PARAM15("lectura"),

    PARAM14("numeroFactura"),

    PARAM13("txtFimm"),

    PARAM9("BLOQUEADOHASTAPERIODO"),

    PARAM12("codigoInterno"),

    PARAM11("codigoRuta"),

    PARAM10("FECHACREACION"),

    PARAM21("PARAM21"),

    PARAM6("BLOQUEADOHASTAANO"),

    PARAM20("FECHABLOQUEADO"),

    VALORCUOTA("VALORCUOTA"),

    SALDOFINANCIABLE("SALDOFINANCIABLE"),

    NUMEROCUOTAS("NUMEROCUOTAS"),

    PARAM2("NROCUOTA"),

    PARAM1("MONTOFINANCIAR"),

    BLOQUEADO("BLOQUEADO"),

    PARAM3("CODIGOALMACEN"),

    PARAM0("ANOBLOQUEADO");

    private final String value;

    private FinanciablesfacturasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
