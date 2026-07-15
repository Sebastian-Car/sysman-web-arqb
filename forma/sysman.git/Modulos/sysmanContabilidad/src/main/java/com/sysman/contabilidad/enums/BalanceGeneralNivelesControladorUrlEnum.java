/*
 * BalancegeneralControladorUrlEnum
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
public enum BalanceGeneralNivelesControladorUrlEnum {

    URL4737("BALANCEGENERALCONTROLADORURL4737",
                    "16003"), URL5748(
                                    "BALANCEGENERALCONTROLADORURL5748",
                                    "20019"), URL4143(
                                                    "BALANCEGENERALCONTROLADORURL4143",
                                                    "16005"), URL5361(
                                                                    "BALANCEGENERALCONTROLADORURL5361",
                                                                    "20017"), URL3837(
                                                                                    "BALANCEGENERALCONTROLADORURL3837",
                                                                                    "4001"), URL5417(
                                                                                                    "BALANCEGENERALCONTROLADORURL5417",
                                                                                                    "16184");

    private final String key;
    private final String value;

    private BalanceGeneralNivelesControladorUrlEnum(String key, String value) {
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
