/*-
 * ReporteEvaluacionesControladorEnum.java
 *
 * 1.0
 * 
 * 31/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros identificados
 * en el refactoring, para ser convertidos Map <String,String> y disponibles en
 * dicha enumeración.
 *
 * 
 * @version 1.0, 31/01/2018
 * @author crodriguez
 *
 */
public enum ReporteEvaluacionesControladorEnum {

	CLASE_EVALUACION("CLASEEVALUACION"),

	CONSECUTIVO("CONSECUTIVO"),

	ID_DE_CARGO("ID_DE_CARGO"),

	CODIGO_INICIAL("CODIGOINICIAL"),

	FECHA("FECHA"),

	NOMBRE("NOMBRE"),

	NOMBRE_DEL_CARGO("NOMBRE_DEL_CARGO"),

	CARGO_EVALUADOR("CARGOEVALUADOR"), COMPANIA("COMPANIA"), CODIGO("CODIGO");

	private final String value;

	private ReporteEvaluacionesControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
