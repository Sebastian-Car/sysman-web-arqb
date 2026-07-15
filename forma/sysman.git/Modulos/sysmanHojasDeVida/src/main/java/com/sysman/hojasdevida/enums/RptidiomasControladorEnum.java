/**
 * Clase: RptidiomasControladorEnum.java
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados
 * en el refactoring, para ser convertidos Map <String,String> y disponibles en
 * dicha enumeración.
 *
 */

public enum RptidiomasControladorEnum {

	COMPANIA("COMPANIA"), PERSONA("PERSONA"), NUMEROCARPETA("NUMEROCARPETA"), EMPLEADOINICIAL("EMPLEADOINICIAL");

	private final String value;

	private RptidiomasControladorEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
