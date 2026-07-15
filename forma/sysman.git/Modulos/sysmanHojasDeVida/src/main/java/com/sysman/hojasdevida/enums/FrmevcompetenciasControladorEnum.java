/**
 * Clase: FrmevcompetenciasControladorEnum.java
 *
 */

package com.sysman.hojasdevida.enums;

/**
 * @version 1.0, 23/01/2018
 * @author fperez
 *
 */

public enum FrmevcompetenciasControladorEnum {

	NUMERO_MANUAL("NUMERO_MANUAL"),

	NUM_MANUAL("NUM_MANUAL"),

	NOMBRE_MANUAL("NOMBRE_MANUAL"),

	NOMBRE_COMPETENCIA("NOMBRE_COMPETENCIA"),

	VERSION("VERSION"),

	EV_COMPETENCIAS("EV_COMPETENCIAS"),

	CONSECUTIVO("CONSECUTIVO"),

	TIPO_COMPETENCIA("TIPO_COMPETENCIA"),

	EV_MANUAL("EV_MANUAL");

	private final String value;

	private FrmevcompetenciasControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
