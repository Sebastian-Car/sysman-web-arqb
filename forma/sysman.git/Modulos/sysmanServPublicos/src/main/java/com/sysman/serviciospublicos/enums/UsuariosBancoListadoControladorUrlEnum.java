/*
* UsuariosBancoListadoControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum UsuariosBancoListadoControladorUrlEnum {
   
           	URL5182("USUARIOSBANCOLISTADOCONTROLADORURL5182","228001"),  
             	URL6944("USUARIOSBANCOLISTADOCONTROLADORURL6944","228005"),  
             	URL5802("USUARIOSBANCOLISTADOCONTROLADORURL5802","214032"),
             	URL5832("USUARIOSBANCOLISTADOCONTROLADORURL5832","214086");
        	
	private final String key;
	private final String value;
	
	private  UsuariosBancoListadoControladorUrlEnum(String key, String value) {
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
