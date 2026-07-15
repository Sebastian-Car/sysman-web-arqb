/**
 * Clase: MonedasControladorEnums.java
 */

package com.sysman.general.enums;

/**
 * @version 1.0, 18/01/2018
 * @author fperez
 *
 * Enumeraci�n que permite clasificar cada uno de los par�metros identificados
 * en el refactoring, para ser convertidos Map <String,String> y disponibles en
 * dicha enumeraci�n.
 */

public enum MonedasControladorEnum {

	CODIGO("CODIGO"), NOMBRE("NOMBRE"), COMPANIA("COMPANIA"), MONEDA("MONEDA");

	private final String value;

	private MonedasControladorEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

}
