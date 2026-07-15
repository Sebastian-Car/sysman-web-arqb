/*
 * FrmFormuladorNominaControladorUrlEnum
 *
 * 1.0
 *
 * 05/02/2026
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmFormuladorNominaControladorUrlEnum {

    URL1995001("FRMFORMULADORNOMINACONTROLADORURL1995001", "1995001"), 
    
    URL151001("FRMFORMULADORNOMINACONTROLADORURL151001", "151001"),
    
	URL210019("FRMFORMULADORNOMINACONTROLADORURL210019", "210019"), 
	
	URL1999001("FRMFORMULADORNOMINACONTROLADORURL1999001", "1999001"),
	
	URL2000001("FRMFORMULADORNOMINACONTROLADORURL2000001", "2000001"),

	;
    
    private final String key;
    private final String value;

    private FrmFormuladorNominaControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
