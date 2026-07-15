/*
 * SaldoinicialsControladorUrlEnum
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
public enum SaldoinicialsControladorUrlEnum {

    URL9247("SALDOINICIALSCONTROLADORURL9247", "20001"),

    URL10139("SALDOINICIALSCONTROLADORURL10139", "23012"),

    URL18164("SALDOINICIALSCONTROLADORURL18164", "34001"),

    URL16448("SALDOINICIALSCONTROLADORURL16448", "13001"),

    URL7635("SALDOINICIALSCONTROLADORURL7635", "14040"),

    URL11923("SALDOINICIALSCONTROLADORURL11923", "16045"),

    URL6922("SALDOINICIALSCONTROLADORURL6922", "14040"),

    URL8352("SALDOINICIALSCONTROLADORURL8352", "20001"),

    URL15627("SALDOINICIALSCONTROLADORURL15627", "13001"),

    URL13773("SALDOINICIALSCONTROLADORURL13773", "16045"),

    URL11032("SALDOINICIALSCONTROLADORURL11032", "23012"),

    URL17272("SALDOINICIALSCONTROLADORURL17272", "34001");

    private final String key;
    private final String value;

    private SaldoinicialsControladorUrlEnum(String key, String value) {
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
