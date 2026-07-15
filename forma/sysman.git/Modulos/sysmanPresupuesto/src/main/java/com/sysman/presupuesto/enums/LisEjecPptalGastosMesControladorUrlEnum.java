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
public enum LisEjecPptalGastosMesControladorUrlEnum {
   
	URL11959("LISEJECPPTALGASTOSMESCONTROLADORURL11959","94062"),
	
	URL12000("LISEJECPPTALGASTOSMESCONTROLADORURL12000","94064"),
	
	URL12003("LISEJECPPTALGASTOSMESCONTROLADORURL12003","4001"),
	
	URL12012("LISEJECPPTALGASTOSMESCONTROLADORURL12012","");
	
	private final String key;
	private final String value;
	
	private  LisEjecPptalGastosMesControladorUrlEnum(String key, String value) {
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