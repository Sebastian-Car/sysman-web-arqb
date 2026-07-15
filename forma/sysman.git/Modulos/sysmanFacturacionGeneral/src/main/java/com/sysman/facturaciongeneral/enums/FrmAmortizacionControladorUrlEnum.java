/*
* AnoFacturacionControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrmAmortizacionControladorUrlEnum {
   
           	URL4247("ANOFACTURACIONCONTROLADORURL4247","670005"),
           	URL5471("ANOFACTURACIONCONTROLADORURL5471","1017004");
        	
	private final String key;
	private final String value;
	
	private  FrmAmortizacionControladorUrlEnum(String key, String value) {
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
