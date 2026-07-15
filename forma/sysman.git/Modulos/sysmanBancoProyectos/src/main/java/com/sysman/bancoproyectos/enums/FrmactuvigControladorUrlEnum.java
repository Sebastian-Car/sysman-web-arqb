/*
 * FrmactuvigControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmactuvigControladorUrlEnum {

    URL2395("FRMACTUVIGCONTROLADORURL2395", "556001"),

    URL2971("FRMACTUVIGCONTROLADORURL2971", "554004"),

    URL4746("FRMACTUVIGCONTROLADORURL4746", "4041"),

    URL3552("FRMACTUVIGCONTROLADORURL3552", "4040");

    private final String key;
    private final String value;

    private FrmactuvigControladorUrlEnum(String key, String value)
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
