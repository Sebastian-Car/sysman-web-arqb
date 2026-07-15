/*
* DistribucionctauxiliaresControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum DistribucionctauxiliaresControladorUrlEnum {
   
           	URL10212("DISTRIBUCIONCTAUXILIARESCONTROLADORURL10212"," List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);"),  
             	URL8251("DISTRIBUCIONCTAUXILIARESCONTROLADORURL8251","16047"),  
             	URL8790("DISTRIBUCIONCTAUXILIARESCONTROLADORURL8790","23014"),  
             	URL9246("DISTRIBUCIONCTAUXILIARESCONTROLADORURL9246","20024"),  
             	URL2779("DISTRIBUCIONCTAUXILIARESCONTROLADORURL2779"," String esta = service.buscarEnLista(cuentaActual, \"CODIGO\", \"CODIGO\","),
             	URL2728("DISTRIBUCIONCTAUXILIARESCONTROLADORURL2728","43002"),
             	URL2729("DISTRIBUCIONCTAUXILIARESCONTROLADORURL2729","43001");
	private final String key;
	private final String value;
	
	private  DistribucionctauxiliaresControladorUrlEnum(String key, String value) {
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
