/*
* DiscoBancolombiaControladorUrlEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 *          Enumeracion que permite clasificar cada uno de los identificadores
 *          geenerados en el refactoring y asociados al codigo legacy obtenido
 *          con patrones de busqueda.
 */
public enum DiscoBancolombiaGnControladorUrlEnum {
	/**
	 * 471025 getPeriodosCodigoNombrePorAnoYMesQuery
	 */
	URL5969("DISCOBANCOLOMBIACONTROLADORURL5969", "471025"),
	/**
	 * 7024 getMesesPorPeriodosYCompaniaQuery
	 */
	URL5500("DISCOBANCOLOMBIACONTROLADORURL5500", "7024"),
	/**
	 * 459001 getBancosnominaPagTodosPorBancoQuery
	 */
	URL6365("DISCOBANCOLOMBIACONTROLADORURL6365", "459001"),
	/**
	 * 471002 getPeriodosAniosPorCompaniaQuery
	 */
	URL5135("DISCOBANCOLOMBIACONTROLADORURL5135", "471002"),

	/**
	 * 459008 getBancosnominaBancopagobancolombiaQuery
	 */
	URL5136("DISCOBANCOLOMBIACONTROLADORURL5136", "459008");

	private final String key;
	private final String value;

	private DiscoBancolombiaGnControladorUrlEnum(String key, String value) {
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
