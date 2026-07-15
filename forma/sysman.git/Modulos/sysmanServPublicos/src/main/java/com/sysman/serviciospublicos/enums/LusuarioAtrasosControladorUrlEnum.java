/*
* LusuarioAtrasosControladorUrlEnum
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
public enum LusuarioAtrasosControladorUrlEnum {
   
           	URL14021("LUSUARIOATRASOSCONTROLADORURL14021","107012"),  
             	URL9799("LUSUARIOATRASOSCONTROLADORURL9799","214080"),  
             	URL12473("LUSUARIOATRASOSCONTROLADORURL12473","310013"),  
             	URL9209("LUSUARIOATRASOSCONTROLADORURL9209","214079"),  
             	URL10399("LUSUARIOATRASOSCONTROLADORURL10399","242001"),  
             	URL11759("LUSUARIOATRASOSCONTROLADORURL11759","242003"),  
             	URL13412("LUSUARIOATRASOSCONTROLADORURL13412","107010"),  
             	URL11001("LUSUARIOATRASOSCONTROLADORURL11001","310011");
        	
	private final String key;
	private final String value;
	
	private  LusuarioAtrasosControladorUrlEnum(String key, String value) {
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
