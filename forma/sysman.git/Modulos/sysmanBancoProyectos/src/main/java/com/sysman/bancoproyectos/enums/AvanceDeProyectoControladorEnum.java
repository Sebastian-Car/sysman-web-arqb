/*
 * ParametroAdicionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/14/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum AvanceDeProyectoControladorEnum {

    VIGENCIA("VIGENCIA"),

    PARAM1("NUMERO");

    private final String value;

    private AvanceDeProyectoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
