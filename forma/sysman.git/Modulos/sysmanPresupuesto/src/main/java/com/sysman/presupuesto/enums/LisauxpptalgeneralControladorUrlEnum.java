/*
* LisauxpptalgeneralControladorUrlEnum
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
public enum LisauxpptalgeneralControladorUrlEnum {
   
           	URL9010("LISAUXPPTALGENERALCONTROLADORURL9010","122002"), 
           	URL3510("LISAUXPPTALGENERALCONTROLADORURL3510","122004"), 
             	URL10192("LISAUXPPTALGENERALCONTROLADORURL10192","121002"),
             	URL10352("LISAUXPPTALGENERALCONTROLADORURL10352","121004"),
             	URL4763("LISAUXPPTALGENERALCONTROLADORURL4763","25008"),  
             	URL6441("LISAUXPPTALGENERALCONTROLADORURL6441","45016"),  
             	URL5830("LISAUXPPTALGENERALCONTROLADORURL5830","45014"),  
             	URL7142("LISAUXPPTALGENERALCONTROLADORURL7142","14067"),  
             	URL7597("LISAUXPPTALGENERALCONTROLADORURL7597","14048"),  
             	URL5254("LISAUXPPTALGENERALCONTROLADORURL5254","25012"),
             	URL8426("LISAUXPPTALGENERALCONTROLADORURL8426","123002"),
             	URL8125("LISAUXPPTALGENERALCONTROLADORURL8125","12300G");
        	
	private final String key;
	private final String value;
	
	private  LisauxpptalgeneralControladorUrlEnum(String key, String value) {
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
