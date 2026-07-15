/*
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FinanciablespsControladorUrlEnum {

    URL001("FINANCIABLESPSCONTROLADORURL001", "227003"),

    URL18392("FINANCIABLESPSCONTROLADORURL18392", "227011"),

    URL13921("FINANCIABLESPSCONTROLADORURL13921", "215005"),

    URL13922("FINANCIABLESPSCONTROLADORURL13922", "215007"),

    URL13923("FINANCIABLESPSCONTROLADORURL13923", "215009"),

    URL13924("FINANCIABLESPSCONTROLADORURL13924", "215011"),

    URL5783("FINANCIABLESPSCONTROLADORURL5783", "213079"),

    URL1749("FINANCIABLESPSCONTROLADORURL1749", "214044"),

    URL1069("FINANCIABLESPSCONTROLADORURL1069", "307011"),

    URL8596("FINANCIABLESPSCONTROLADORURL8596", "307012"),

    URL7581("FINANCIABLESPSCONTROLADORURL7581", "227012"),

    URL9712("FINANCIABLESPSCONTROLADORURL9712", "227013"),

    URL19055("FINANCIABLESPSCONTROLADORURL19055", "213076");

    private final String key;
    private final String value;

    private FinanciablespsControladorUrlEnum(String key, String value) {
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
