/*
 * FmractualizadesviosignificativosControladorUrlEnum
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
public enum FmractualizadesviosignificativosControladorUrlEnum {

    URL10228("FMRACTUALIZADESVIOSIGNIFICATIVOSCONTROLADORURL10228", "214045"),

    URL145("FMRACTUALIZADESVIOSIGNIFICATIVOSCONTROLADORURL145", "213018"),

    URL178("FMRACTUALIZADESVIOSIGNIFICATIVOSCONTROLADORURL178", "213015");

    private final String key;
    private final String value;

    private FmractualizadesviosignificativosControladorUrlEnum(String key,
        String value)
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
