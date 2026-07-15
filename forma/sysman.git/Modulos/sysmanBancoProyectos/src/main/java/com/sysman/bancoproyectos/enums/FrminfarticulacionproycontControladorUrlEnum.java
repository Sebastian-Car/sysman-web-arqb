/*
 * FrminfarticulacionproycontControladorUrlEnum
 *
 * 1.0
 *
 * 20/01/2025
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
public enum FrminfarticulacionproycontControladorUrlEnum {

	URL4043("FRMINFARTICULACIONPROYCONTCONTROLADORURL4043","4043"),
	
	URL34001("FRMINFARTICULACIONPROYCONTCONTROLADORURL34001", "34001"),
	
	URL34003("FRMINFARTICULACIONPROYCONTCONTROLADORURL34001", "34003"),
	
	URL32003("FRMINFARTICULACIONPROYCONTCONTROLADORURL32003","32003"),
	
	URL32013("FRMINFARTICULACIONPROYCONTCONTROLADORURL32013","32013");    

    private final String key;
    private final String value;

    private  FrminfarticulacionproycontControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
