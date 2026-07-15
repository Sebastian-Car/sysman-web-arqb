/*
 * SubresponsablesproyectosControladorUrlEnum
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
public enum FrmImportarMgaControladorUrlEnum {

    URL7704("FRMIMPORTARMGACONTROLADORURL7704", "4001"),
    URL4576("FRMIMPORTARMGACONTROLADORURL4576", "62073"),
    URL9660("FRMIMPORTARMGACONTROLADORURL9660", "4027");

    private final String key;
    private final String value;

    private FrmImportarMgaControladorUrlEnum(String key, String value)
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
