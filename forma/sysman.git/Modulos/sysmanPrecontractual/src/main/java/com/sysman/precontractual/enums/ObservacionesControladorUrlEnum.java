/*
* ObservacionesControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido 
 * con patrones de busqueda.
 */ 
public enum ObservacionesControladorUrlEnum {
   
           	URL5890("OBSERVACIONESCONTROLADORURL5890"," listaPROPONENTE = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT \" + \" OBSERVACIONES.PROPONENTE, \" + \" TERCERO.NOMBRE \" + \" FROM \" + \" OBSERVACIONES INNER JOIN \" + \" PROPONENTE INNER JOIN TERCERO \" + \" ON PROPONENTE.COMPANIA = TERCERO.COMPANIA \" + \" AND PROPONENTE.PROPONENTE = TERCERO.NIT \" + \" AND PROPONENTE.SUCURSAL = TERCERO.SUCURSAL \" + \" ON OBSERVACIONES.COMPANIA = PROPONENTE.COMPANIA \" + \" AND OBSERVACIONES.TIPOCONTRATO = PROPONENTE.TIPOCONTRATO \" + \" AND OBSERVACIONES.TRANSACCION = PROPONENTE.TRANSACCION \" + \" AND OBSERVACIONES.CONSECUTIVODETALLE = PROPONENTE.CONSECUTIVODETALLE \" + \" AND OBSERVACIONES.PROPONENTE = PROPONENTE.PROPONENTE \" + \" AND OBSERVACIONES.SUCURSAL = PROPONENTE.SUCURSAL \" + \" WHERE OBSERVACIONES.COMPANIA = '\" + compania + \"' \" + \" AND OBSERVACIONES.TIPOCONTRATO = '\" + tipoContrato + \"' \" + \" AND OBSERVACIONES.TRANSACCION = '\" + transaccion + \"' \" + \" AND OBSERVACIONES.CONSECUTIVODETALLE = '\" + consecutivo + \"' \");");
        	
	private final String key;
	private final String value;
	
	private  ObservacionesControladorUrlEnum(String key, String value) {
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
