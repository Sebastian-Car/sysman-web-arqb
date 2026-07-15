/*
 * BalanceaperturaniifControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 *          Enumeracion que permite clasificar cada uno de los identificadores
 *          geenerados en el refactoring y asociados al codigo legacy obtenido
 *          con patrones de busqueda.
 */
public enum BalanceaperturaniifControladorUrlEnum {

	// URL10013("BALANCEAPERTURANIIFCONTROLADORURL10013",
	// " List<Registro> regAux = service.getListado(con, sql);"),

	URL4326("BALANCEAPERTURANIIFCONTROLADORURL4326", "29003"),

	URL5411("BALANCEAPERTURANIIFCONTROLADORURL5411", "29005"),

	URL3815("BALANCEAPERTURANIIFCONTROLADORURL3815", "7008"),

	URL3309("BALANCEAPERTURANIIFCONTROLADORURL3309", "4001");

	private final String key;
	private final String value;

	private BalanceaperturaniifControladorUrlEnum(String key, String value) {
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
