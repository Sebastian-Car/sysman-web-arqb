/*
 * Frminformenormativofac120ControladorUrlEnum
 *
 * 1.0
 *
 * 06/04/2026
 *
 * Copyright  Sysman
 */
package com.sysman.sysmanauditoriacuentasmedicas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum Frminformenormativofac120ControladorUrlEnum {
	URL4391("FRMINFORMENORMATIVOFAC120CONTROLADORURL4391",
			"14001"),
	URL4392("FRMINFORMENORMATIVOFAC120CONTROLADORURL4392",
			"1823001"),
	URL4393("FRMINFORMENORMATIVOFAC120CONTROLADORURL4393",
			"1823002"),
	URL4394("FRMINFORMENORMATIVOFAC120CONTROLADORURL4394",
			"1886003"),
	URL4395("FRMINFORMENORMATIVOFAC120CONTROLADORURL4395",
			"1885001"),
	URL4396("FRMINFORMENORMATIVOFAC120CONTROLADORURL001",
			"1823006");

	private final String key;
	private final String value;

	private Frminformenormativofac120ControladorUrlEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
