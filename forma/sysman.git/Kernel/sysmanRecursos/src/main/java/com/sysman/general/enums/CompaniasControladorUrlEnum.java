/*
* CompaniasControladorUrlEnum
*
* 1.0
*
* 09/09/2016
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
public enum CompaniasControladorUrlEnum {
   
           	
           	
             	URL8520("COMPANIASCONTROLADORURL8520","59001"),
             	
             	URL6080("COMPANIASCONTROLADORURL6080","64001"),  
             	
             	URL9281("COMPANIASCONTROLADORURL9281","59001"),  
             	
             	URL9517("COMPANIASCONTROLADORURL9517","66001"),  
             	
             	URL7080("COMPANIASCONTROLADORURL7080","1001"),  
             	
             	URL8002("COMPANIASCONTROLADORURL8002","5001"),  
             	
             	URL7434("COMPANIASCONTROLADORURL7434","2001"),
             	
             	URL8543("COMPANIASCONTROLADORURL8543","586001"),
             	
             	URL1032("COMPANIASCONTROLADORURL1032","1032010"),
             	
             	URL22001("COMPANIASCONTROLADORURL22001","22001");
        	
	private final String key;
	private final String value;
	
	private  CompaniasControladorUrlEnum(String key, String value) {
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
