/*
* SeleccionarPlacasControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum SeleccionarPlacasControladorUrlEnum {
   
           	URL3676("SELECCIONARPLACASCONTROLADORURL3676","141087"),
           	URL3677("SELECCIONARPLACASCONTROLADORURL3677","141156"),
           	URL3678("SELECCIONARPLACASCONTROLADORURL3678","139011"),
                URL2174("SELECCIONARPLACASCONTROLADORURL2174","119015"),
                URL3854("SELECCIONARPLACASCONTROLADORURL3874","119016"),
                URL2649("SELECCIONARPLACASCONTROLADORURL2649","119017"),
                URL4896("SELECCIONARPLACASCONTROLADORURL4896","119018");
    
        	
	private final String key;
	private final String value;
	
	private  SeleccionarPlacasControladorUrlEnum(String key, String value) {
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
