/*
 * MantenimientoVehiculosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.mantenimientoactivos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 *          Enumeración que permite clasificar cada uno de los parámetros
 *          identificados en el refactoring, para ser convertidos Map
 *          <String,String> y disponibles en dicha enumeración.
 */
public enum MantenimientoVehiculosControladorEnum {

	compania("compania"), fechaInicial("fechaInicial"), fechaFinal("fechaFinal"), elementoInicial(
			"elementoInicial"), elementoFinal("elementoFinal"), serieInicial("serieInicial"), serieFinal("serieFinal"),

	NOMBRELARGO("NOMBRELARGO"), CODIGOELEMENTO("CODIGOELEMENTO"), CODIGO("CODIGO"), SERIEINICIAL("SERIEINICIAL");

	private final String value;

	private MantenimientoVehiculosControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
