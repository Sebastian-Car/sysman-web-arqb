/*
* InfDependTipoGastoModSeleccControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum InfDependTipoGastoModSeleccControladorUrlEnum {
   
           	URL5597("INFDEPENDTIPOGASTOMODSELECCCONTROLADORURL5597","108001"),  
             	URL6538("INFDEPENDTIPOGASTOMODSELECCCONTROLADORURL6538","4032"),  
             	URL6139("INFDEPENDTIPOGASTOMODSELECCCONTROLADORURL6139","4001"),  
             	URL7041("INFDEPENDTIPOGASTOMODSELECCCONTROLADORURL7041","62002");
        	
	private final String key;
	private final String value;
	
	private  InfDependTipoGastoModSeleccControladorUrlEnum(String key, String value) {
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
