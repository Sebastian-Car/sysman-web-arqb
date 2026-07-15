/*
* LfinanciablesusuarioControladorUrlEnum
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
public enum LfinanciablesusuarioControladorUrlEnum {
   
           	URL6878("LFINANCIABLESUSUARIOCONTROLADORURL6878","227029"), 
           	URL6880("LFINANCIABLESUSUARIOCONTROLADORURL6880","213139"), 
             	URL8740("LFINANCIABLESUSUARIOCONTROLADORURL8740","227034"),  
             	URL7483("LFINANCIABLESUSUARIOCONTROLADORURL7483","227033"),  
             	URL10356("LFINANCIABLESUSUARIOCONTROLADORURL10356","213085"),  
             	URL12924("LFINANCIABLESUSUARIOCONTROLADORURL12924","215029"),  
             	URL11251("LFINANCIABLESUSUARIOCONTROLADORURL11251","214063"),  
             	URL8144("LFINANCIABLESUSUARIOCONTROLADORURL8144","227021"),  
             	URL12268("LFINANCIABLESUSUARIOCONTROLADORURL12268","215015"),  
             	URL9398("LFINANCIABLESUSUARIOCONTROLADORURL9398","213083");
        	
	private final String key;
	private final String value;
	
	private  LfinanciablesusuarioControladorUrlEnum(String key, String value) {
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
