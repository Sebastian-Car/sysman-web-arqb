/*
 * OrdentrabajosControladorEnum
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
public enum OrdentrabajosControladorEnum {

    PARAM26("true"),

    PARAM25("false"),

    PARAM24("AFORADOR"),

    PARAM23("ANOUSUARIO"),

    PARAM22("PQR DESPUES DE RES 20101300048765 DEL 14-12-2010"),

    PARAM21("INDFAVOREMPRESA"),

    PARAM20("SOLUCION"),

    PARAM19("FECHATRASLADO_SSPD"),

    PARAM18("TIPONOTIFICACION"),

    PARAM17("FECHASOLUCION"),

    PARAM16("FECHANOTIFICACION"),

    PARAM15("CLASENOMBRE"),

    PARAM14("SERVNORECLAMADO"),

    PARAM13("PROBLEMANOMBRE"),

    PARAM12("PROBLEMA"),

    PARAM11("PERIODOUSUARIO"),

    PARAM10("NUMORDENT"),

    PARAM9("DOCUMENTONOMBRE"),

    PARAM8("FECHASOLICITUD"),

    PARAM7("CODIGOINTERNO"),

    PARAM6("CARTERA"),

    PARAM5("KEY_CLASEDOC"),

    PARAM4("CLASEPROBLEMA"),

    PARAM3("TIPOREQUERIMIENTO"),

    PARAM2("NUMORDEN"),

    PARAM1("ORDENTRABAJO"),

    PARAM0("CLASEDOC");

    private final String value;

    private OrdentrabajosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
