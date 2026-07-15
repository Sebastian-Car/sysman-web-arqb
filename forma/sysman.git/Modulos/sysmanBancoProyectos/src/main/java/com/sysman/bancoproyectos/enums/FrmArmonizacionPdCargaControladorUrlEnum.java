/*
 * FrmArmonizacionPdCargaControladorUrlEnum
 *
 * 1.0
 *
 * 23/02/2026
 *
 * Copyright Sysman
 */
package com.sysman.bancoproyectos.enums;

public enum FrmArmonizacionPdCargaControladorUrlEnum {
	
	URL433020("FRMARMONIZACIONPDCARGACONTROLADORURL433020", "433020"),
	
	URL4310("FRMARMONIZACIONPDCARGACONTROLADORURL4310", "4001");


    private final String key;
    private final String value;

    private FrmArmonizacionPdCargaControladorUrlEnum(String key, String value)
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
