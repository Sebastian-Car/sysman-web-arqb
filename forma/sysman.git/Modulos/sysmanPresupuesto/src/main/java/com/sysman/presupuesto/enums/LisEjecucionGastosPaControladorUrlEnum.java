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
public enum LisEjecucionGastosPaControladorUrlEnum {
   
	URL11959("LISEJECUCIONGASTOSCONTROLADORURL11959","94030"),
	
	URL12000("LISEJECUCIONGASTOSCONTROLADORURL12000","94032"),
	
	URL12003("LISEJECUCIONGASTOSPACONTROLADORURL12003","4001"),
	
	URL12012("LISEJECUCIONGASTOSCONTROLADORURL12012","37006");
	
	private final String key;
	private final String value;
	
	private  LisEjecucionGastosPaControladorUrlEnum(String key, String value) {
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