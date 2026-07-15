/*
 * CorreccioncriticasControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CorreccioncriticasControladorUrlEnum {

    URL8923("CORRECCIONCRITICASCONTROLADORURL8923", "366024"),

    URL8925("CORRECCIONCRITICASCONTROLADORURL8925", "214005"),

    URL8959("CORRECCIONCRITICASCONTROLADORURL8959", "362010"),

    URL8929("CORRECCIONCRITICASCONTROLADORURL8929", "278002");

    private final String key;
    private final String value;

    private CorreccioncriticasControladorUrlEnum(String key, String value)
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
