/*
 * FrmCronogramasStsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmCronogramasStsControladorUrlEnum {

    URL5529("FRMCRONOGRAMASSTSCONTROLADORURL5529", "210060"),

    URL6050("FRMCRONOGRAMASSTSCONTROLADORURL6050", ""),

    URL4803("FRMCRONOGRAMASSTSCONTROLADORURL4803", "726001");

    private final String key;
    private final String value;

    private FrmCronogramasStsControladorUrlEnum(String key, String value)
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
