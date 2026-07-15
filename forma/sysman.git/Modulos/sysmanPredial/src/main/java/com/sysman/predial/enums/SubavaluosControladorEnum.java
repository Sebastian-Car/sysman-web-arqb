/*
 * SubavaluosControladorEnum
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
public enum SubavaluosControladorEnum {
    PREIND("PREIND"),

    PREINDT("PREINDT"),

    PREFEC("PREFEC"),

    INDHIP("INDHIP"),

    ANOTARIFA("ANOTARIFA"),

    TRPRAN("TRPRAN"),

    TRPPOR("TRPPOR"),

    TRPCOD("TRPCOD"),

    PREANO("PREANO"),

    CODIGOPREDIO("CODIGOPREDIO");

    private final String value;

    private SubavaluosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
