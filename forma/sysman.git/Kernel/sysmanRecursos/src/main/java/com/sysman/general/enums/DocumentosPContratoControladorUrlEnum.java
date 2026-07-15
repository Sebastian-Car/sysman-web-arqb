/*
 * DocumentosPContratoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DocumentosPContratoControladorUrlEnum {

    URL13888("DOCUMENTOSPCONTRATOCONTROLADORURL13888", "190001"),

    URL15939("DOCUMENTOSPCONTRATOCONTROLADORURL15939", "190002"),

    URL27661("DOCUMENTOSPCONTRATOCONTROLADORURL27661", "190003"),

    URL27630("DOCUMENTOSPCONTRATOCONTROLADORURL27630", "191001"),

    URL27631("DOCUMENTOSPCONTRATOCONTROLADORURL27631", "190006");
    private final String key;
    private final String value;

    private DocumentosPContratoControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
