/*
 * PactesoreriacntsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PactesoreriacntsControladorUrlEnum {

    URL6439("PACTESORERIACNTSCONTROLADORURL6439", "94001"),

    URL5403("PACTESORERIACNTSCONTROLADORURL5403", "94001"),

    URL397("PACTESORERIACNTSCONTROLADORURL397", "94106");

    private final String key;
    private final String value;

    private PactesoreriacntsControladorUrlEnum(String key, String value)
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
