/*
* InformeUsuariosMedidoresUrlEnum
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
public enum InformeUsuariosMedidoresUrlEnum {
   
           	URL7839("INFORMEUSUARIOSMEDIDORESURL7839","227027"),
           	URL7840("INFORMEUSUARIOSMEDIDORESURL7840","227028"),
             	URL7140("INFORMEUSUARIOSMEDIDORESURL7140","214031");
        	
	private final String key;
	private final String value;
	
	private  InformeUsuariosMedidoresUrlEnum(String key, String value) {
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
