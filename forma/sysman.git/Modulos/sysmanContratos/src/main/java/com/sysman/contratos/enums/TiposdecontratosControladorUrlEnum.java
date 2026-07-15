/*
 * TiposdecontratosControladorUrlEnum
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
public enum TiposdecontratosControladorUrlEnum {

    URL7445("TIPOSDECONTRATOSCONTROLADORURL7445",
                    "439001"),

    URL8723("TIPOSDECONTRATOSCONTROLADORURL8723",
                    "426001"),

    URL7542("TIPOSDECONTRATOSCONTROLADORURL7542",
                    "435001"),

    URL6878("TIPOSDECONTRATOSCONTROLADORURL6878",
                    "438001"),

    URL6502("TIPOSDECONTRATOSCONTROLADORURL6502",
                    "428001"),

    URL001("TIPOSDECONTRATOSCONTROLADORURL001", "73039"),

    URL002("TIPOSDECONTRATOSCONTROLADORURL002",
                    "73041"),

    URL003("TIPOSDECONTRATOSCONTROLADORURL003",
                    "73042"),

    URL004("TIPOSDECONTRATOSCONTROLADORURL004",
                    "73043"),

    URL005("TIPOSDECONTRATOSCONTROLADORURL005",
                    "7300D"),

    URL7854("TIPOSDECONTRATOSCONTROLADORURL7854",
                    "434001"),
    
    URL1928("TIPOSDECONTRATOSCONTROLADORURL1928001",
            "1928001");

    private final String key;
    private final String value;

    private TiposdecontratosControladorUrlEnum(String key, String value) {
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
