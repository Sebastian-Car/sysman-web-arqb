/*
 * ConfigurarFuenteFutsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConfigurarFuenteFutsControladorUrlEnum {

    URL10568("CONFIGURARFUENTEFUTSCONTROLADORURL10568",
                    "1772003"),

    URL9585("CONFIGURARFUENTEFUTSCONTROLADORURL9585",
                    "1772003"),

    URL7768("CONFIGURARFUENTEFUTSCONTROLADORURL7768",
                    "4001"),

    URL8601("CONFIGURARFUENTEFUTSCONTROLADORURL8601",
                    "1772003"),

    URL8185("CONFIGURARFUENTEFUTSCONTROLADORURL8185",
                    "4001"),

    URL11541("CONFIGURARFUENTEFUTSCONTROLADORURL11541",
                    "1772003"),

    URL18944("CONFIGURARFUENTEFUTSCONTROLADORURL18944",
                    "34050"),

    URL11542("CONFIGURARFUENTEFUTSCONTROLADORURL11542",
                    "34055"),

    URL11543("CONFIGURARFUENTEFUTSCONTROLADORURL11543",
                    "34057"),

    URL11544("CONFIGURARFUENTEFUTSCONTROLADORURL11544",
                    "34058"),

    URL11545("CONFIGURARFUENTEFUTSCONTROLADORURL11545",
                    "3400D");

    private final String key;
    private final String value;

    private ConfigurarFuenteFutsControladorUrlEnum(String key, String value) {
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
