/*
 * NatSubEvaluacionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum NatSubEvaluacionControladorUrlEnum {

    URL8913("NATSUBEVALUACIONCONTROLADORURL8913",
                    "694001"),

    URL9498("NATSUBEVALUACIONCONTROLADORURL9498",
                    "1001001"),

    URL7854("NATSUBEVALUACIONCONTROLADORURL7854",
                    "685057"),

    URL5555("NATSUBEVALUACIONCONTROLADORURL5555",
                    "998001"),

    URL6666("NATSUBEVALUACIONCONTROLADORURL6666",
                    "998005"),

    URL7777("NATSUBEVALUACIONCONTROLADORURL7777",
                    "998004"),

    URL8888("NATSUBEVALUACIONCONTROLADORURL8888",
                    "998003"),

    URL9999("NATSUBEVALUACIONCONTROLADORURL9999",
                    "99800D");

    private final String key;
    private final String value;

    private NatSubEvaluacionControladorUrlEnum(String key, String value) {
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
