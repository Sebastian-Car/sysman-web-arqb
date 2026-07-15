/*
 *  InventarioFisicoPorDependenciaPlacaAnControladorEnum
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
public enum InventarioFisicoPorDependenciaPlacaAnControladorEnum  {
	codigoinicial("codigoInicial"),
	codigofinal("codigoFinal"),
    REPORTE001873("001873CInvIndivDevoluDepenPlacaAn"),
    REPORTE001875("001875CInvIndivDevoluDepenEspecPlaca"),
	
	DIGITOSAGRUPACIONINVENTARIO("DIGITOSAGRUPACIONINVENTARIO");
	
        	
	private final String value;
	
	private   InventarioFisicoPorDependenciaPlacaAnControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
