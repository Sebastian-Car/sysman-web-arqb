/*
* LisboletinmensualcajaControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum LisboletinmensualcajaControladorUrlEnum {
   
           	URL3518("LISBOLETINMENSUALCAJACONTROLADORURL3518","4013"),  
             	URL3817("LISBOLETINMENSUALCAJACONTROLADORURL3817","16023");
        	
	private final String key;
	private final String value;
	
	private  LisboletinmensualcajaControladorUrlEnum(String key, String value) {
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
