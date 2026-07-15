/*
 * FrmsubproyectoslocalizacionsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmsubproyectoslocalizacionsControladorEnum {

    CODIGOPROYECTO("CODIGOPROYECTO"),

    NOMBREBAR("NOMBREBAR"),

    NOMBRECIU("NOMBRECIU"),

    NOMBREDEP("NOMBREDEP"),

    NOMBREPAIS("NOMBREPAIS"),

    PAIS("PAIS"),

    CODIGO_PROY("CODIGO_PROY"),

    MENU52020102("52020102"),

    MENU52020402("52020402");

    private final String value;

    private FrmsubproyectoslocalizacionsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
