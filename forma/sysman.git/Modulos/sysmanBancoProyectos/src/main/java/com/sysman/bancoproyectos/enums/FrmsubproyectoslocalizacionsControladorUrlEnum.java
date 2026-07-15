/*
 * FrmsubproyectoslocalizacionsControladorUrlEnum
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
public enum FrmsubproyectoslocalizacionsControladorUrlEnum {

    URL5340("FRMSUBPROYECTOSLOCALIZACIONSCONTROLADORURL5340", "107018"),

    URL4174("FRMSUBPROYECTOSLOCALIZACIONSCONTROLADORURL4174", "2005"),

    URL4829("FRMSUBPROYECTOSLOCALIZACIONSCONTROLADORURL4829", "5001"),

    URL3679("FRMSUBPROYECTOSLOCALIZACIONSCONTROLADORURL3679", "1001");
    private final String key;
    private final String value;

    private FrmsubproyectoslocalizacionsControladorUrlEnum(String key, String value)
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
