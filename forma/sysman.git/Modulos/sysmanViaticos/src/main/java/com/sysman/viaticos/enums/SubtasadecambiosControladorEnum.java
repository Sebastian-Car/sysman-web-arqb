/**
 * Clase: SubtasadecambiosControladorEnum.java
 */

package com.sysman.viaticos.enums;

/**
 * @version 1.0, 19/01/2018
 * @author fperez
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados
 * en el refactoring, para ser convertidos Map <String,String> y disponibles en
 * dicha enumeración.
 */

public enum SubtasadecambiosControladorEnum {

	CODIGO("CODIGO");

	private final String value;

	private SubtasadecambiosControladorEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

}
