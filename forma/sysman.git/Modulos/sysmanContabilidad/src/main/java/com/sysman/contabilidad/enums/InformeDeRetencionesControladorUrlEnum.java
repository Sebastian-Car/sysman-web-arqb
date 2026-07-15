/*
* InformeDeRetencionesControladorUrlEnum
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
public enum InformeDeRetencionesControladorUrlEnum {
   
    URL4368("INFORMEDERETENCIONESCONTROLADORURL4368","16092"),  
    
    URL3314("INFORMEDERETENCIONESCONTROLADORURL3314","16090"),
    
    URL13028("INFORMEDERETENCIONESCONTROLADORURL13028", "13028"),
    
    URL13030("INFORMEDERETENCIONESCONTROLADORURL13030","13030"),
    
    URL29045("INFORMEDERETENCIONESCONTROLADORURL29045","29045"),
    
    URL29047("INFORMEDERETENCIONESCONTROLADORURL29047","29047"),
    
    URL14001("AUXILIARPPTALTERCEROSCONTROLADORURL3640", "14001"),

    URL14026("AUXILIARPPTALTERCEROSCONTROLADORURL4377", "14026")
    
    ;
        	
	private final String key;
	private final String value;
	
	private  InformeDeRetencionesControladorUrlEnum(String key, String value) {
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
