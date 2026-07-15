/*
 * FrmactualizachapetasControladorUrlEnum
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
public enum FrmactualizachapetasControladorUrlEnum {

    URL2900("FRMACTUALIZACHAPETASCONTROLADORURL2900",
                    "214022"),

    URL10228("FRMACTUALIZACHAPETASCONTROLADORURL10228", "214045"),

    URL145("FRMACTUALIZACHAPETASCONTROLADORURL145", "213018"),

    URL178("FRMACTUALIZACHAPETASCONTROLADORURL178", "213015");

    private final String key;
    private final String value;

    private FrmactualizachapetasControladorUrlEnum(String key, String value)
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
