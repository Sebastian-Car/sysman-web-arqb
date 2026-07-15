/*
 * TarifasControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map<String,String> y disponibles en dicha enumeración.
 */
public enum TarifasControladorEnum {

    MESESAMNISTIA_PREDIAL("MESESAMNISTIA_PREDIAL"),

    TRPRAN1("TRPRAN1"),

    TRPRAN2("TRPRAN2"),

    TRPPOR("TRPPOR"),

    PORCBOMB("PORCBOMB"),

    TRPALUMBRADO("TRPALUMBRADO"),

    TRPINC("TRPINC"),

    TRPINT("TRPINT"),

    TRPCAR("TRPCAR"),

    TRPANO("TRPANO");

    private final String value;

    private TarifasControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
