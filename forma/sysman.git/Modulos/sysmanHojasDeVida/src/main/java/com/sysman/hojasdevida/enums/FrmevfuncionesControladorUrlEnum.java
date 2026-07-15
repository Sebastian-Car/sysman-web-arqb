/*
 * FrmevfuncionesControladorUrlEnum
 *
 * 1.0
 *
 * 16/01/2018
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmevfuncionesControladorUrlEnum {

    URL4131("FRMEVFUNCIONESCONTROLADORURL4131", "756001"),

    URL4132("FRMEVFUNCIONESCONTROLADORURL4132", "753002"), 
    
    URL463003("FRMEVFUNCIONESCONTROLADORURL4132", "463003")    
    
    ;

    private final String key;
    private final String value;

    private FrmevfuncionesControladorUrlEnum(String key, String value)
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
