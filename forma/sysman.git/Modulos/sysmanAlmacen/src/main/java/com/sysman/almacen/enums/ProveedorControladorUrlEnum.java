/*
* ProveedorControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum ProveedorControladorUrlEnum {
   
                URL15084("PROVEEDORCONTROLADORURL15084","163001"),
           	URL5313("PROVEEDORCONTROLADORURL5313","14081"),  
             	URL7309("PROVEEDORCONTROLADORURL7309","112032");  
        	
	private final String key;
	private final String value;
	
	private  ProveedorControladorUrlEnum(String key, String value) {
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
