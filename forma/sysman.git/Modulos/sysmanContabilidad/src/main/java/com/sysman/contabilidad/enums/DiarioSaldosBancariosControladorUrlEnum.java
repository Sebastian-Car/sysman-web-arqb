/*
 * DiarioSaldosBancariosControladorUrlEnum
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
public enum DiarioSaldosBancariosControladorUrlEnum {

    URL3746("DIARIOSALDOSBANCARIOSCONTROLADORURL3746", "16016"), URL4720(
                    "DIARIOSALDOSBANCARIOSCONTROLADORURL4720", "16018");

    private final String key;
    private final String value;

    private DiarioSaldosBancariosControladorUrlEnum(String key, String value) {
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
