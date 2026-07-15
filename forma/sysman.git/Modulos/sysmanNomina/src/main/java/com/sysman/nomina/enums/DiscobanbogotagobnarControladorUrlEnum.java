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
public enum DiscobanbogotagobnarControladorUrlEnum {
	/**
	 * 471025 getPeriodosCodigoNombrePorAnoYMesQuery
	 */
	URL5969("DISCOBANBOGOTAGOBNARCONTROLADORURL5969", "471025"),
	/**
	 * 7024 getMesesPorPeriodosYCompaniaQuery
	 */
	URL5500("DISCOBANBOGOTAGOBNARCONTROLADORURL5969", "7024"),
	/**
	 * 459001 getBancosnominaPagTodosPorBancoQuery
	 */
	URL6365("DISCOBANBOGOTAGOBNARCONTROLADORURL5969", "459001"),
	/**
	 * 471002 getPeriodosAniosPorCompaniaQuery
	 */
	URL5135("DISCOBANBOGOTAGOBNARCONTROLADORURL5969", "471002"),
	/**
	 * 459008 getBancosnominaBancopagobancolombiaQuery
	 */
	URL5136("DISCOBANBOGOTAGOBNARCONTROLADORURL5969", "459008");

	private final String key;
	private final String value;

	private DiscobanbogotagobnarControladorUrlEnum(String key, String value) {
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
