/*
 * InformeIngresosCarUnificadoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InformeIngresosCarUnificadoControladorUrlEnum {

    URL5070("INFORMEINGRESOSCARUNIFICADOCONTROLADORURL5070", "375004"), URL5654("INFORMEINGRESOSCARUNIFICADOCONTROLADORURL5654", "375006");
    private final String key;
    private final String value;

    private InformeIngresosCarUnificadoControladorUrlEnum(String key, String value)
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
