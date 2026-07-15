/*
* AfectacioncontratosControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum DevolutivosRecibidosEnComodatoControladorURLEnum {
   
	URL2474("ESTADODEVOLYBIENESFECHACONTROLADORURL2472", "112133"),
	URL2475("ESTADODEVOLYBIENESFECHACONTROLADORURL2472", "112135");

	private final String key;
	private final String value;

	private DevolutivosRecibidosEnComodatoControladorURLEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
