/*
* DiscoBancoPopularControladorUrlEnum
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
public enum DiscoBancoPopularControladorUrlEnum {

	/**
	 * 471002 getPeriodosAniosPorCompaniaQuery
	 */
	URL4522("DISCOBANCOPOPULARCONTROLADORURL4522", "471002"),
	/**
	 * 459001 getBancosnominaPagTodosPorBancoQuery
	 */
	URL6961("DISCOBANCOPOPULARCONTROLADORURL6961", "459001"),
	/**
	 * 7024 getMesesPorPeriodosYCompaniaQuery
	 */
	URL4996("DISCOBANCOPOPULARCONTROLADORURL4996", "7024"),
	/**
	 * 537002 getProcesosdenominaPorCompaniaDifCeroQuery
	 */
	URL6203("DISCOBANCOPOPULARCONTROLADORURL6203", "537002"),

	/**
	 * 471025 getPeriodosCodigoNombrePorAnoYMesQuery
	 */
	URL6204("DISCOBANCOPOPULARCONTROLADORURL6203", "471025"),
			
	/**
	 * 459014 getBancosnominaPagBancopopularpagoQuery
	 */
	URL459014("DISCOBANCOPOPULARCONTROLADORURL459014", "459014");

	private final String key;
	private final String value;

	private DiscoBancoPopularControladorUrlEnum(String key, String value) {
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
