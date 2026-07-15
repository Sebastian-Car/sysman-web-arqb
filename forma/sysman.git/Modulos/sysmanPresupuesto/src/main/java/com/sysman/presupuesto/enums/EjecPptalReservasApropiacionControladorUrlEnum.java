/*
* EjecPptalReservasApropiacionControladorUrlEnum
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
public enum EjecPptalReservasApropiacionControladorUrlEnum {
   
           	URL9128("EJECPPTALRESERVASAPROPIACIONCONTROLADORURL9128","20030"),  
             	URL11317("EJECPPTALRESERVASAPROPIACIONCONTROLADORURL11317","23019"),  
             	URL10701("EJECPPTALRESERVASAPROPIACIONCONTROLADORURL10701","23010"),  
             	URL4975("EJECPPTALRESERVASAPROPIACIONCONTROLADORURL4975","7013"),  
             	URL7595("EJECPPTALRESERVASAPROPIACIONCONTROLADORURL7595","94026"),  
             	URL9982("EJECPPTALRESERVASAPROPIACIONCONTROLADORURL9982","20015"),  
             	URL6191("EJECPPTALRESERVASAPROPIACIONCONTROLADORURL6191","94020"),  
             	URL5852("EJECPPTALRESERVASAPROPIACIONCONTROLADORURL5852","4001");
        	
	private final String key;
	private final String value;
	
	private  EjecPptalReservasApropiacionControladorUrlEnum(String key, String value) {
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
