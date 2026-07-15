/*
 * PredialFacturaLotesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PredialFacturaLotesControladorUrlEnum {

    URL0001("PREDIALFACTURALOTESCONTROLADORURL0001", "67003"),

    URL9507("PREDIALFACTURALOTESCONTROLADORURL9507", "367158"),

    URL11843("PREDIALFACTURALOTESCONTROLADORURL11843", "367143"),

    URL8726("PREDIALFACTURALOTESCONTROLADORURL8726", "367156"),

    URL11080("PREDIALFACTURALOTESCONTROLADORURL11080", "367011"),

    URL10368("PREDIALFACTURALOTESCONTROLADORURL10368", "367137"),

    URL7617("PREDIALFACTURALOTESCONTROLADORURL7617", "367088"),

    URL12509("PREDIALFACTURALOTESCONTROLADORURL12509", "367145"),

    URL6045("PREDIALFACTURALOTESCONTROLADORURL6045", "4001"),

    URL6587("PREDIALFACTURALOTESCONTROLADORURL6587", "367086");

    private final String key;
    private final String value;

    private PredialFacturaLotesControladorUrlEnum(String key, String value) {
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
