/**
 * Clase: MonedasControladorUrlEnum.java
 */

package com.sysman.general.enums;

/**
 * @version 1.0, 18/01/2018
 * @author fperez
 *
 * Enumeraci�n que permite clasificar cada uno de los identificadores generados
 * en el refactoring y asociados al c�digo legacy obtenido con patrones de
 * b�squeda.
 */

public enum MonedasControladorUrlEnum {

	URL130("MONEDASCONTROLADORURL130", "767001"),
	URL9281("MONEDASCONTROLADORURL9281", "767002");

	private final String key;
	private final String value;

	private MonedasControladorUrlEnum(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public String getKey()
	{
		return key;
	}

	public String getValue()
	{
		return value;
	}

}
