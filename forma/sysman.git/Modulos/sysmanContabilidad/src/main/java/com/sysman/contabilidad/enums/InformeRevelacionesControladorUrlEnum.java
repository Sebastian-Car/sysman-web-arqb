/*
 * ActualizaConfiguracionControladorUrlEnum
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
public enum InformeRevelacionesControladorUrlEnum {

    URL4621("INFORMEREVELACIONESCONTROLADORURL4621","4001"),
    URL7451("INFORMEREVELACIONESCONTROLADORURL7451","7001"),
    URL9682("INFORMEREVELACIONESCONTROLADORURL9682","4027"),
    URL3521("INFORMEREVELACIONESCONTROLADORURL3521","7012"),
    URL6428("INFORMEREVELACIONESCONTROLADORURL6428","16008"),
    URL7684("INFORMEREVELACIONESCONTROLADORURL7684","16010"),
    URL3785("INFORMEREVELACIONESCONTROLADORURL3785","104056");

    private final String key;
    private final String value;

    private InformeRevelacionesControladorUrlEnum(String key, String value) {
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
