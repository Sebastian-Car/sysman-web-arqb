/*
 * FormularioCorreccionCriticaUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FormularioCorreccionCriticaUrlEnum {

    URL20478("FORMULARIOCORRECCIONCRITICAURL20478", "234021"),

    URL20479("FORMULARIOCORRECCIONCRITICAURL20479", "234022"),

    URL18575("FORMULARIOCORRECCIONCRITICAURL18575", "213068"),

    URL19287("FORMULARIOCORRECCIONCRITICAURL19287", "238001"),

    URL19288("FORMULARIOCORRECCIONCRITICAURL19288", "238007"),

    URL19289("FORMULARIOCORRECCIONCRITICAURL19289", "238008"),

    URL19290("FORMULARIOCORRECCIONCRITICAURL19290", "238009"),

    URL19291("FORMULARIOCORRECCIONCRITICAURL19291", "366023");

    private final String key;
    private final String value;

    private FormularioCorreccionCriticaUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
