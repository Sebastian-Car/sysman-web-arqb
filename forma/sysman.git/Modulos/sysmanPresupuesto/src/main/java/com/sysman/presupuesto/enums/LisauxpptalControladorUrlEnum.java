/*
* LisauxpptalControladorUrlEnum
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
public enum LisauxpptalControladorUrlEnum {
   
           	URL5537("LISAUXPPTALCONTROLADORURL5537","45014"),  
             	URL6261("LISAUXPPTALCONTROLADORURL6261","45016"),  
             	URL7790("LISAUXPPTALCONTROLADORURL7790","20015"),  
             	URL7104("LISAUXPPTALCONTROLADORURL7104","20013"),  
             	URL4827("LISAUXPPTALCONTROLADORURL4827","25012"),  
             	URL4238("LISAUXPPTALCONTROLADORURL4238","25008"),  
             	URL8589("LISAUXPPTALCONTROLADORURL8589","23010"),  
             	URL9185("LISAUXPPTALCONTROLADORURL9185","23019");
        	
	private final String key;
	private final String value;
	
	private  LisauxpptalControladorUrlEnum(String key, String value) {
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
