/*
 * FrmEliminarCptoNominaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;
 
/**
 * @version 1.0, 24/05/2023
 *
 * @author jmillan
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */

public enum FrmEliminarCptoNominaControladorUrlEnum {

    URL4058("FRMELIMINARCPTONOMINACONTROLADORRURL4058", "537004"),

    URL4735("FRMELIMINARCPTONOMINACONTROLADORRURL4735", "471008"),

    URL7274("FRMELIMINARCPTONOMINACONTROLADORRURL7274", "471050"),

    URL5723("FRMELIMINARCPTONOMINACONTROLADORRURL5723", "471049"),
	
    URL6834("FRMELIMINARCPTONOMINACONTROLADORRURL6834", "620021"),
	
	URL7945("FRMELIMINARCPTONOMINACONTROLADORRURL6834", "210158"),
	
	URL7956("FRMELIMINARCPTONOMINACONTROLADORRURL7956", "51005");
	
	

    private final String key;
    private final String value;

    private FrmEliminarCptoNominaControladorUrlEnum(String key, String value)
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
