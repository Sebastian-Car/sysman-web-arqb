/*
* RecibosexcedentesControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum RecibosexcedentesControladorUrlEnum {
   
             	URL6394("RECIBOSEXCEDENTESCONTROLADORURL6394","386010"),  
             	URL7764("RECIBOSEXCEDENTESCONTROLADORURL7764","413003"),  
             	URL7269("RECIBOSEXCEDENTESCONTROLADORURL7269","375004"),
             	URL7080("RECIBOSEXCEDENTESCONTROLADORURL7080","374029"),
             	URL6452("RECIBOSEXCEDENTESCONTROLADORURL6452","413006"),
             	URL2345("RECIBOSEXCEDENTESCONTROLADORURL2345","367188"),
             	URL1047("RECIBOSEXCEDENTESCONTROLADORURL1047","413007"),
             	URL3056("RECIBOSEXCEDENTESCONTROLADORURL3056","386012"),
             	URL4587("RECIBOSEXCEDENTESCONTROLADORURL4587","413008"),
             	URL1752("RECIBOSEXCEDENTESCONTROLADORURL1752","374031"),
             	URL8754("RECIBOSEXCEDENTESCONTROLADORURL8754","410006"),
             	URL4928("RECIBOSEXCEDENTESCONTROLADORURL4928","413012"),
             	URL9863("RECIBOSEXCEDENTESCONTROLADORURL9863","413011"),
             	URL1765("RECIBOSEXCEDENTESCONTROLADORURL1765","413013"),
             	URL4571("RECIBOSEXCEDENTESCONTROLADORURL4571","367189"),
             	URL10874("RECIBOSEXCEDENTESCONTROLADORURL10874","413005"),  
             	URL5517("RECIBOSEXCEDENTESCONTROLADORURL5517","413001");
        	
	private final String key;
	private final String value;
	
	private  RecibosexcedentesControladorUrlEnum(String key, String value) {
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
