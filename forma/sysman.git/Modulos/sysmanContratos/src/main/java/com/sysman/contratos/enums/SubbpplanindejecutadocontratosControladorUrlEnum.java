/*
 * SubbpplanindejecutadocontratosControladorUrlEnum
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
public enum SubbpplanindejecutadocontratosControladorUrlEnum {

    URL19159("SUBBPPLANINDEJECUTADOCONTRATOSCONTROLADORURL19159", ""),

    URL8521("SUBBPPLANINDEJECUTADOCONTRATOSCONTROLADORURL8521", "433001"),

    URL11621("SUBBPPLANINDEJECUTADOCONTRATOSCONTROLADORURL11621", ""),

    URL14724("SUBBPPLANINDEJECUTADOCONTRATOSCONTROLADORURL14724", "206002"),

    URL25398("SUBBPPLANINDEJECUTADOCONTRATOSCONTROLADORURL25398", "23026"),

    URL27326("SUBBPPLANINDEJECUTADOCONTRATOSCONTROLADORURL27326", ""),

    URL16940("SUBBPPLANINDEJECUTADOCONTRATOSCONTROLADORURL16940", ""),

    URL22281("SUBBPPLANINDEJECUTADOCONTRATOSCONTROLADORURL22281", "");

    private final String key;
    private final String value;

    private SubbpplanindejecutadocontratosControladorUrlEnum(String key,
        String value) {
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
