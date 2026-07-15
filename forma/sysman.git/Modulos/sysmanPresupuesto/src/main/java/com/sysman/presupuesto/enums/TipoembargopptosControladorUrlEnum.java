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
public enum TipoembargopptosControladorUrlEnum {
   
	URL11959("APROPIACIONESINICIALESCONTROLADORURL11959","37002"),
	
	URL12000("APROPIACIONESINICIALESCONTROLADORURL12000","37005"),
	
	URL12003("APROPIACIONESINICIALESCONTROLADORURL12003","37004"),
	
	URL12012("APROPIACIONESINICIALESCONTROLADORURL12012","37006");
	
	private final String key;
	private final String value;
	
	private  TipoembargopptosControladorUrlEnum(String key, String value) {
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