/*
 * PlanContableControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */
public enum PlanContableControladorEnum {

    FRMATO("FORMATOFL"),
    ANOACT("ANOACTUAL"),
    SYSDAT("SYSDATE"),
    ANO("ANIO"),
    COD("CODIGOF"),
    BANCO("BANCO"),
    BLOCIND("BLOQUEAR_INDICADORES"),
    CLASCUENTA("CLASECUENTA"),
    MANAUXFUE("MAN_AUX_FUE"),
    MANAUXGEN("MAN_AUX_GEN"),
    MANAUXREF("MAN_AUX_REF"),
    MANAUXTER("MAN_AUX_TER"),
    MANAUXCTO("MAN_CEN_CTO"),
    CANTCOD("CANTCODIGO"),
    LONGCOD("LONGITUDCOD"),
    MOV("MOVIMIENTO"),
    MANCENTRO("MAN_CEN_CTO")


    ;

	private final String value;

	private  PlanContableControladorEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
