/*
 * InfResolucionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InfResolucionesControladorUrlEnum {

    URL7836("INFRESOLUCIONESCONTROLADORURL7836", "104050"),

    URL4541("INFRESOLUCIONESCONTROLADORURL4541", "471039"),

    URL7027("INFRESOLUCIONESCONTROLADORURL7027", "210045"),

    URL3927("INFRESOLUCIONESCONTROLADORURL3927", "471032"),

    URL3928("INFRESOLUCIONESCONTROLADORURL3928", "471026"),

    URL3929("INFRESOLUCIONESCONTROLADORURL3929", "537008");

    private final String key;
    private final String value;

    private InfResolucionesControladorUrlEnum(String key, String value)
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
