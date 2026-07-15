/*
 * BalanceaperturaesfaControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum BalanceaperturaesfaControladorUrlEnum {

    URL4873("BALANCEAPERTURAESFACONTROLADORURL4873", "7010"),

    URL3137("BALANCEAPERTURAESFACONTROLADORURL3137", "29003"),

    URL4380("BALANCEAPERTURAESFACONTROLADORURL4380", "4001");

    private final String key;
    private final String value;

    private BalanceaperturaesfaControladorUrlEnum(String key, String value) {
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
