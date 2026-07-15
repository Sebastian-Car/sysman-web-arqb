/**
 * Clase: RptImprimirAdmitidosControladorEnum.java
 * 
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */

package com.sysman.hojasdevida.enums;

/**
 * @version 1.0, 11/01/2018
 * @author fperez
 *
 *         Enumeración que permite clasificar cada uno de los parámetros
 *         identificados en el refactoring, para ser convertidos Map
 *         <String,String> y disponibles en dicha enumeración.
 */

public enum RptImprimirAdmitidosControladorEnum {

	COMPANIA("COMPANIA"),

	CONVOCATORIA("CONVOCATORIA"), NRO_CONVOCATORIA("NRO_CONVOCATORIA"), PRUEBA("PRUEBA"), CONSECUTIVO(
			"CONSECUTIVO"), FECHA_CONVOCATORIA("FECHA_CONVOCATORIA"), DESCRIPCION("DESCRIPCION");

	private final String value;

	private RptImprimirAdmitidosControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
