/*
* InventariosControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum InventariosControladorUrlEnum {
   
             	URL33831("INVENTARIOSCONTROLADORURL33831","119011"),  
             	URL36478("INVENTARIOSCONTROLADORURL36478","119012"),  
             	URL10280("INVENTARIOSCONTROLADORURL10280","155001"),  
             	URL11306("INVENTARIOSCONTROLADORURL11306","16113"),  
             	URL7379("INVENTARIOSCONTROLADORURL7379","16096"), 
             	URL7270("INVENTARIOSCONTROLADORURL7270","159001"),  
             	URL53870("INVENTARIOSCONTROLADORURL53870","112053"),  
             	URL27976("INVENTARIOSCONTROLADORURL27976","112052"),  
             	URL10745("INVENTARIOSCONTROLADORURL10745","158001"),  
             	URL9245("INVENTARIOSCONTROLADORURL9245","4001"),  
             	URL9712("INVENTARIOSCONTROLADORURL9712","154001"),  
                URL9708("INVENTARIOSCONTROLADORURL9708","112054"),  
                URL9208("INVENTARIOSCONTROLADORURL9208","112056"),
                URL9910("INVENTARIOSCONTROLADORURL9910","141060"),
                URL1099("INVENTARIOSCONTROLADORURL1099","119013"),
                URL1368("INVENTARIOSCONTROLADORURL1368","183001"),
                URL2496("INVENTARIOSCONTROLADORURL2496","183002"),
                URL9766("INVENTARIOSCONTROLADORURL9766","159002"),
                URL9767("INVENTARIOSCONTROLADORURL9766","143001"),
                URL112202("INVENTARIOSCONTROLADORURL112202","112202"); 
        	
	private final String key;
	private final String value;
	
	private  InventariosControladorUrlEnum(String key, String value) {
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
