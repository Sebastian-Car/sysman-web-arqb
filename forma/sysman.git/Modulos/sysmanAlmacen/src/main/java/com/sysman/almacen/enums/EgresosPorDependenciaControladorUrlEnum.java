/*
* AfectacioncontratosControladorUrlEnum
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
public enum EgresosPorDependenciaControladorUrlEnum {
   
	URL11959("EGRESOSPORDEPENDENCIACONTROLADORURL11959","112158"),
	
	URL12000("EGRESOSPORDEPENDENCIACONTROLADORURL12000","112160"),
	
	URL12012("EGRESOSPORDEPENDENCIACONTROLADORURL12012","62007"),
	
	URL1812("EGRESOSPORDEPENDENCIACONTROLADORURL1812","62011");
        	
	private final String key;
	private final String value;
	
	private  EgresosPorDependenciaControladorUrlEnum(String key, String value) {
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
