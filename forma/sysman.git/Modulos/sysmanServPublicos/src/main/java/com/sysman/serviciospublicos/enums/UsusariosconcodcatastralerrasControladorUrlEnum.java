/*
 * UsuariosConsumoPromedioControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author jguerrero
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al código legacy obtenido con patrones de búsqueda.
 */
public enum UsusariosconcodcatastralerrasControladorUrlEnum {

    URL7563("USUSARIOSCONCODCATASTRALERRASCONTROLADORURL7563","213200"),

    URL5748("USUSARIOSCONCODCATASTRALERRASCONTROLADORURL5748","213198"),

    URL8348("USUSARIOSCONCODCATASTRALERRASCONTROLADORURL8348","213199");

    private final String key;
    private final String value;

    private UsusariosconcodcatastralerrasControladorUrlEnum(String key, String value)
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
