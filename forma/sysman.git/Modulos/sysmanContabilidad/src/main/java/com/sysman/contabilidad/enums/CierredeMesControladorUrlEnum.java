/*
 * CierredeMesControladorUrlEnum
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
public enum CierredeMesControladorUrlEnum {

    URL5332("CIERREDEMESCONTROLADORURL5332", "20025"),

    URL6002("CIERREDEMESCONTROLADORURL6002", "4017"),

    URL6003("CIERREDEMESCONTROLADORURL6002", "4018"),

    URL17823("CIERREDEMESCONTROLADORURL17823", "72039"),

    URL16332("CIERREDEMESCONTROLADORURL16332", "116010"),

    URL4471("CIERREDEMESCONTROLADORURL4471", "4001"),

    URL4846("CIERREDEMESCONTROLADORURL4846", "7007"),

    URL4848("CIERREDEMESCONTROLADORURL4848", "15022"),

    URL4847("CIERREDEMESCONTROLADORURL4847", "4011")

    ;

    private final String key;
    private final String value;

    private CierredeMesControladorUrlEnum(String key, String value) {
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
