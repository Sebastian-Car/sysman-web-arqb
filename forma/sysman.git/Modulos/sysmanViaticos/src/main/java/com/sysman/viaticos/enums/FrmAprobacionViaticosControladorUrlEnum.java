/*
* FrmAprobacionViaticosControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum FrmAprobacionViaticosControladorUrlEnum {
   
	URL15084("FRMAPROBACIONVIATICOSCONTROLADORURL3528","761003"),       	
	URL3528("FRMAPROBACIONVIATICOSCONTROLADORURL3528","761005"),
	URL0001("FRMAPROBACIONVIATICOSCONTROLADORURL0001", "104050");;
        	
	private final String key;
	private final String value;
	
	private  FrmAprobacionViaticosControladorUrlEnum(String key, String value) {
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
