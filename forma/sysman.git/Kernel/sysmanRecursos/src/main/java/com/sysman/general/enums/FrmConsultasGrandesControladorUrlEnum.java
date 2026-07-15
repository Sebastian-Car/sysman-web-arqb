/*
 * FrmConsultasGrandesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;
 
/**
 * @version 1.0, 31/07/2025
 *
 * @author jmillan
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */

public enum FrmConsultasGrandesControladorUrlEnum {

    URL4058("FRMCONSULTASGRANDESCONTROLADORRURL4058", "58009"),

    URL4735("FRMCONSULTASGRANDESCONTROLADORRURL4735", "404004"),
	
	URL7945("FRMCONSULTASGRANDESCONTROLADORRURL6834", "1975001"),
	
	URL4579("FRMCONSULTASGRANDESCONTROLADORRURL6834", "1975002"),
	
	URL5840("FRMCONSULTASGRANDESCONTROLADORRURL6834", "1975003");
	
	

    private final String key;
    private final String value;

    private FrmConsultasGrandesControladorUrlEnum(String key, String value)
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
