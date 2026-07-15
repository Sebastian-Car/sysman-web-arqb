/*
 * BalanceVerificacionControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum MovimientosCuentasControladorUrlEnum {

    URL3728("MOVIMIENTOSCUENTASCONTROLADORURL3728", "39015"),

    URL3194("MOVIMIENTOSCUENTASCONTROLADORURL3194", "29083"),

    URL3198("MOVIMIENTOSCUENTASCONTROLADORURL3198", "39067"),

    URL3195("MOVIMIENTOSCUENTASCONTROLADORURL3195", "39048");

    private final String key;
    private final String value;

    private MovimientosCuentasControladorUrlEnum(String key, String value) {
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
