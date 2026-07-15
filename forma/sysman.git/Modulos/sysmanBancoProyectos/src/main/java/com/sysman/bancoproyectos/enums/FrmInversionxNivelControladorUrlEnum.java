/*
 * FrmInversionxNivelControladorUrlEnum
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
public enum FrmInversionxNivelControladorUrlEnum {

    URL5239("FRMINVERSIONXNIVELCONTROLADORURL5239", "576002"),

    URL3606("FRMINVERSIONXNIVELCONTROLADORURL3606", "4001"),

    URL10490("FRMINVERSIONXNIVELCONTROLADORURL10490", ""),

    URL4368("FRMINVERSIONXNIVELCONTROLADORURL4368", "576001"),

    URL3986("FRMINVERSIONXNIVELCONTROLADORURL3986", "");
    private final String key;
    private final String value;

    private FrmInversionxNivelControladorUrlEnum(String key, String value)
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
