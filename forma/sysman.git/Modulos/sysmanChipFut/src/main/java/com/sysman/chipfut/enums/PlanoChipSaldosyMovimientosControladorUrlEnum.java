/*
 * AcummensualControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PlanoChipSaldosyMovimientosControladorUrlEnum {

    URL4198("PLANOCHIPSALDOSYMOVIMIENTOSCONTROLADORURL4198", "4001"),
    URL0002("LANOCHIPSALDOSYMOVIMIENTOSCONTROLADORURL", "7001");

    private final String key;
    private final String value;

    private PlanoChipSaldosyMovimientosControladorUrlEnum(String key,
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
