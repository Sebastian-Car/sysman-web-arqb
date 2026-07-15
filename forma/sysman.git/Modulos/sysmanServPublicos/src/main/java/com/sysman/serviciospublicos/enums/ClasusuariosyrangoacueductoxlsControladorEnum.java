/*
 * ClasusuariosyrangoacueductoxlsControladorEnum
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
public enum ClasusuariosyrangoacueductoxlsControladorEnum {

    PARAM0("ANOFINAL"),

    PARAM1("PERIODOINICIAL"),

    PARAM2("ANOINICIAL"),

    PARAM3("PERIODOFINAL");

    private final String value;

    private ClasusuariosyrangoacueductoxlsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
