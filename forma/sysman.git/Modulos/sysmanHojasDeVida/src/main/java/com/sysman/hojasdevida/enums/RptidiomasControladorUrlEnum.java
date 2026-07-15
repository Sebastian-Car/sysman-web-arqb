/*
 * TarifasfgControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los identificadores generados
 * en el refactoring y asociados al código legacy obtenido con patrones de
 * búsqueda.
 */
public enum RptidiomasControladorUrlEnum {

	URL142("RPTIDIOMASCONTROLADORURL142", "685029"), URL198("RPTIDIOMASCONTROLADORURL198", "685037");

	private final String key;
	private final String value;

	private RptidiomasControladorUrlEnum(String key, String value)
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
