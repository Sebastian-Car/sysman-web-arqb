/*
* FrmselecciondetalledisposicionsControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrmselecciondetalledisposicionsControladorUrlEnum {
   
           	URL7660("FRMSELECCIONDETALLEDISPOSICIONSCONTROLADORURL7660","227002"),  
             	URL6681("FRMSELECCIONDETALLEDISPOSICIONSCONTROLADORURL6681","214053"),  
             	URL9191("FRMSELECCIONDETALLEDISPOSICIONSCONTROLADORURL9191","213119"),  
             	URL8266("FRMSELECCIONDETALLEDISPOSICIONSCONTROLADORURL8266","213117"),  
             	URL7113("FRMSELECCIONDETALLEDISPOSICIONSCONTROLADORURL7113","227001");
        	
	private final String key;
	private final String value;
	
	private  FrmselecciondetalledisposicionsControladorUrlEnum(String key, String value) {
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
