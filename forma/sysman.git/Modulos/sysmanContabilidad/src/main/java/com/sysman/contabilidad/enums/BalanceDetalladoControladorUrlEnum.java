/*
 * BalanceDetalladoControladorUrlEnum
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
public enum BalanceDetalladoControladorUrlEnum {

    URL6322("BALANCEDETALLADOCONTROLADORURL6322", "14026"), URL4779(
                    "BALANCEDETALLADOCONTROLADORURL4779",
                    "16003"), URL5556(
                                    "BALANCEDETALLADOCONTROLADORURL5556",
                                    "14001"), URL4131(
                                                    "BALANCEDETALLADOCONTROLADORURL4131",
                                                    "16005"), URL3710(
                                                                    "BALANCEDETALLADOCONTROLADORURL3710",
                                                                    "4001");

    private final String key;
    private final String value;

    private BalanceDetalladoControladorUrlEnum(String key, String value) {
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
