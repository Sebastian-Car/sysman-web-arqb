/*
 * InfClaseContratoTipoGastoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum InfClaseContratoTipoGastoControladorUrlEnum {

    URL5439("INFCLASECONTRATOTIPOGASTOCONTROLADORURL5439", "4001"),

    URL5867("INFCLASECONTRATOTIPOGASTOCONTROLADORURL5867", "4016"),

    URL4962("INFCLASECONTRATOTIPOGASTOCONTROLADORURL4962", "73028");

    private final String key;
    private final String value;

    private InfClaseContratoTipoGastoControladorUrlEnum(String key,
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
