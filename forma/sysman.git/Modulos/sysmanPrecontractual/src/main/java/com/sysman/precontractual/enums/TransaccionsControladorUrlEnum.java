/*
 * TransaccionsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum TransaccionsControladorUrlEnum {

    URL001("TRANSACCIONSCONTROLADORURL001", "533007"),

    URL002("TRANSACCIONSCONTROLADORURL002", "535001"),

    URL003("TRANSACCIONSCONTROLADORURL003", "548002"),

    URL004("TRANSACCIONSCONTROLADORURL004", "548003"),

    URL005("TRANSACCIONSCONTROLADORURL005", "523003"),

    URL006("TRANSACCIONSCONTROLADORURL006", "477003"),

    URL007("TRANSACCIONSCONTROLADORURL007", "523004"),

    URL11581("TRANSACCIONSCONTROLADORURL11581", "548001"),

    URL13155("TRANSACCIONSCONTROLADORURL13155", "497008"),

    URL12382("TRANSACCIONSCONTROLADORURL12382", "497008"),

    URL16900("TRANSACCIONSCONTROLADORURL16900", "111015"),

    URL16901("TRANSACCIONSCONTROLADORURL16900", "111017"),

    URL14665("TRANSACCIONSCONTROLADORURL14665", "112103"),

    URL13927("TRANSACCIONSCONTROLADORURL13927", "112103");

    private final String key;
    private final String value;

    private TransaccionsControladorUrlEnum(String key, String value) {
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
