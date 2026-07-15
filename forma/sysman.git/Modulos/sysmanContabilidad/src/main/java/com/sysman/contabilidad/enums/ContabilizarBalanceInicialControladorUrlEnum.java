/*
 * ContabilizarBalanceInicialControladorUrlEnum
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
public enum ContabilizarBalanceInicialControladorUrlEnum {

    URL2491("CONTABILIZARBALANCEINICIALCONTROLADORURL2491", "4002"),

    URL4160("CONTABILIZARBALANCEINICIALCONTROLADORURL4160", "42003"),

    URL111("CONTABILIZARBALANCEINICIALCONTROLADORURL111", "42001"),

    URL146("CONTABILIZARBALANCEINICIALCONTROLADORURL146", "42002");

    private final String key;
    private final String value;

    private ContabilizarBalanceInicialControladorUrlEnum(String key,
        String value) {
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
