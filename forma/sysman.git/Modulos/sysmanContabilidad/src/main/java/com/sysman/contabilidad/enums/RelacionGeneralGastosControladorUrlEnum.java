/*
 * RelacionGeneralGastosControladorUrlEnum
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
public enum RelacionGeneralGastosControladorUrlEnum {

    URL3980("RELACIONGENERALGASTOSCONTROLADORURL3980", "14036"),

    URL5345("RELACIONGENERALGASTOSCONTROLADORURL5345", "16026"),

    URL4639("RELACIONGENERALGASTOSCONTROLADORURL4639", "14038"),

    URL6490("RELACIONGENERALGASTOSCONTROLADORURL6490", "16028");

    private final String key;
    private final String value;

    private RelacionGeneralGastosControladorUrlEnum(String key, String value) {
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
