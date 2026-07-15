/*
 * NatSubnombramientoCarControladorUrlEnum
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
public enum NatSubnombramientoCarControladorUrlEnum {

    URL10414("NATSUBNOMBRAMIENTOCARCONTROLADORURL10414",
                    "700002"),

    URL11432("NATSUBNOMBRAMIENTOCARCONTROLADORURL11432",
                    "463017"),

    URL11988("NATSUBNOMBRAMIENTOCARCONTROLADORURL11988",
                    "62002"),

    URL10891("NATSUBNOMBRAMIENTOCARCONTROLADORURL10891",
                    "463015"),

    URL12527("NATSUBNOMBRAMIENTOCARCONTROLADORURL12527",
                    "62002"),

    URL9454("NATSUBNOMBRAMIENTOCARCONTROLADORURL9454",
                    "618006"),

    URL9999("NATSUBNOMBRAMIENTOCARCONTROLADORURL9999",
                    "697009"),

    URL8888("NATSUBNOMBRAMIENTOCARCONTROLADORURL9999",
                    "697012"),

    URL2525("NATSUBNOMBRAMIENTOCARCONTROLADORURL2525",
                    "697011"),

    URL4242("NATSUBNOMBRAMIENTOCARCONTROLADORURL4242",
                    "697010"),

    URL6464("NATSUBNOMBRAMIENTOCARCONTROLADORURL6464",
                    "69700D"),

    URL5757("NATSUBNOMBRAMIENTOCARCONTROLADORURL5757",
                    "697008"),

    URL5858("NATSUBNOMBRAMIENTOCARCONTROLADORURL5858",
                    "697013")

    ;

    private final String key;
    private final String value;

    private NatSubnombramientoCarControladorUrlEnum(String key, String value) {
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
