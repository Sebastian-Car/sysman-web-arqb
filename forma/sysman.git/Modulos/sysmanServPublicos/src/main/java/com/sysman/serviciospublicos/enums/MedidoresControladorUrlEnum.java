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
public enum MedidoresControladorUrlEnum {
   
           	URL14021("MEDIDORESCONTROLADORURL14021","290003"),
           	
             	URL9799("MEDIDORESCONTROLADORURL9799","289008"), 
             	
             	URL12473("MEDIDORESCONTROLADORURL12473","227037"),  
             	
             	URL9209("MEDIDORESCONTROLADORURL9209","227039"),
             	
             	URL10399("MEDIDORESCONTROLADORURL10399","227041"),
             	
             	URL11759("MEDIDORESCONTROLADORURL11759","227043"), 
             	
             	URL13412("MEDIDORESCONTROLADORURL13412","213177"),
             	
             	URL11001("MEDIDORESCONTROLADORURL11001","289010"),
             	
             	URL76235("MEDIDORESCONTROLADORURL76235","213223"),
             	
             	URL1154("MEDIDORESCONTROLADORURL1154","213179"),
             	
             	URL4578("MEDIDORESCONTROLADORURL4578","213180"),
             	
             	URL9045("MEDIDORESCONTROLADORURL4578","213181"),
             	
             	URL7249("MEDIDORESCONTROLADORURL7249","289011"),
             	
             	URL8521("MEDIDORESCONTROLADORURL8521","213222"),
             	
             	URL5427("MEDIDORESCONTROLADORURL5427","289012");
    
        	
	private final String key;
	private final String value;
	
	private  MedidoresControladorUrlEnum(String key, String value) {
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
