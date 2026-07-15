/*
* ReporteConsumoAcueductoUrlEnum
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
public enum ReporteConsumoAcueductoUrlEnum {
   
           	URL7404("REPORTECONSUMOACUEDUCTOURL7404","227029"),  
             	URL10334("REPORTECONSUMOACUEDUCTOURL10334","118007"),  
             	URL8802("REPORTECONSUMOACUEDUCTOURL8802","227030"),  
             	URL9418("REPORTECONSUMOACUEDUCTOURL9418","227004"),  
             	URL14562("REPORTECONSUMOACUEDUCTOURL14562","317001"),  
             	URL8027("REPORTECONSUMOACUEDUCTOURL8027","227017");
        	
	private final String key;
	private final String value;
	
	private  ReporteConsumoAcueductoUrlEnum(String key, String value) {
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
