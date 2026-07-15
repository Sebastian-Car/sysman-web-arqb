/*
* FcejecprescuentasporpagarsControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FcejecprescuentasporpagarsControladorUrlEnum {
   
           	URL5476("FCEJECPRESCUENTASPORPAGARSCONTROLADORURL5476","7012"),  
             	URL8703("FCEJECPRESCUENTASPORPAGARSCONTROLADORURL8703","20013"),  
             	URL4939("FCEJECPRESCUENTASPORPAGARSCONTROLADORURL4939","7001"),  
             	URL7465("FCEJECPRESCUENTASPORPAGARSCONTROLADORURL7465","94042"),  
             	URL9366("FCEJECPRESCUENTASPORPAGARSCONTROLADORURL9366","20015"),  
             	URL10863("FCEJECPRESCUENTASPORPAGARSCONTROLADORURL10863","23019"),  
             	URL6515("FCEJECPRESCUENTASPORPAGARSCONTROLADORURL6515","94040"),  
             	URL6096("FCEJECPRESCUENTASPORPAGARSCONTROLADORURL6096","4002"),  
             	URL10104("FCEJECPRESCUENTASPORPAGARSCONTROLADORURL10104","23010");
        	
	private final String key;
	private final String value;
	
	private  FcejecprescuentasporpagarsControladorUrlEnum(String key, String value) {
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
