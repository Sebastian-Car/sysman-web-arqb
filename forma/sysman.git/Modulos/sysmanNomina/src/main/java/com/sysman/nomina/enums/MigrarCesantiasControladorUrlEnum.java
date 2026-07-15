/*
 * MigrarCesantiasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum MigrarCesantiasControladorUrlEnum {

	URL10243("MIGRARCESANTIASCONTROLADORURL10243","151001"),  
	URL7338("MIGRARCESANTIASCONTROLADORURL7338","471002"),  
	URL7680("MIGRARCESANTIASCONTROLADORURL7680","471004"),  
	URL8022("MIGRARCESANTIASCONTROLADORURL8022","7024"),  
	URL9754("MIGRARCESANTIASCONTROLADORURL9754","537001"),  
	URL9136("MIGRARCESANTIASCONTROLADORURL9136","471003"); 

	private final String key;
	private final String value;

	private  MigrarCesantiasControladorUrlEnum(String key, String value) {
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
