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
public enum UsuariosinmedidorControladorUrlEnum {
   
           	URL5182("USUARIOSINMEDIDORCONTROLADORURL5182","213190"),  
             	URL5832("USUARIOSINMEDIDORCONTROLADORURL5832","290004"),
             	URL9545("USUARIOSINMEDIDORCONTROLADORURL5832","213194"),
             	URL9321("USUARIOSINMEDIDORCONTROLADORURL9321","289013"),
             	URL9045("USUARIOSINMEDIDORCONTROLADORURL9045","289014"),
             	URL15847("USUARIOSINMEDIDORCONTROLADORURL15847","289017");
        	
	private final String key;
	private final String value;
	
	private  UsuariosinmedidorControladorUrlEnum(String key, String value) {
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
