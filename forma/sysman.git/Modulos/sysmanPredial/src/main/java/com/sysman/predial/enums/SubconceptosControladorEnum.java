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
public enum SubconceptosControladorEnum {
    C1("C1"),

    C2("C2"),

    C3("C3"),

    C4("C4"),

    C12("C12"),

    C13("C13"),

    C14("C14"),

    C15("C15"),

    C16("C16"),

    C17("C17"),

    C18("C18"),

    C19("C19"),

    C20("C20"),

    ADMINISTRADOR_INCREMENTOS_LEY("ADMINISTRADOR INCREMENTOS DE LEY"),

    PERMITE_MANIPULAR_FACTURADOS("PERMITE MANIPULAR FACTURADOS"),

    PREANO("PREANO"),

    CODIGOPREDIO("CODIGOPREDIO");

    private final String value;

    private SubconceptosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
