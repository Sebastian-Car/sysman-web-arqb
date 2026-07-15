/*
* DiscoBancoAgrarioDjControladorUrlEnum
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
public enum DiscoBancoAgrarioDjControladorUrlEnum {
   
    /**
     * 471025 getPeriodosCodigoNombrePorAnoYMesQuery
     */
   	URL6985("DISCOBANCOAGRARIODJCONTROLADORURL6985","471025"),  
   	
    /**
     * 471002 getPeriodosAniosPorCompaniaQuery
     */   	
 	URL5916("DISCOBANCOAGRARIODJCONTROLADORURL5916","471002"),  
 	
    /**
     * 537002 getProcesosdenominaPorCompaniaDifCeroQuery
     */ 	
 	URL7480("DISCOBANCOAGRARIODJCONTROLADORURL7480","537002"), 
 	
    /**
     * 7024 getMesesPorPeriodosYCompaniaQuery
     */    
 	URL6375("DISCOBANCOAGRARIODJCONTROLADORURL6375","7024"),  
 	 	
    /**
     * 459001 getBancosnominaPagTodosPorBancoQuery
     */ 	
 	URL7820("DISCOBANCOAGRARIODJCONTROLADORURL7820","459001");

	private final String key;
	private final String value;
	
	private  DiscoBancoAgrarioDjControladorUrlEnum(String key, String value) {
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
