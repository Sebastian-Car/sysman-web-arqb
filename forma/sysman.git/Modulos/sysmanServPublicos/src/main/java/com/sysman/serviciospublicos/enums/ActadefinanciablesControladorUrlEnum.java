/*
 * ActadefinanciablesControladorUrlEnum
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
public enum ActadefinanciablesControladorUrlEnum {

    URL0001("ACTADEFINANCIABLESCONTROLADORURL0001", "342001"),

    URL0002("ACTADEFINANCIABLESCONTROLADORURL0002", "342004"),

    URL0003("ACTADEFINANCIABLESCONTROLADORURL0003", "342006"),

    URL0004("ACTADEFINANCIABLESCONTROLADORURL0004", "342005"),

    URL0005("ACTADEFINANCIABLESCONTROLADORURL0005", "342007"),

    URL13509("ACTADEFINANCIABLESCONTROLADORURL13509", "307001"),

    URL12157("ACTADEFINANCIABLESCONTROLADORURL12157", "214018"),

    URL15514("ACTADEFINANCIABLESCONTROLADORURL15514", "104006"),

    URL12619("ACTADEFINANCIABLESCONTROLADORURL12619", "213013"),

    URL14472("ACTADEFINANCIABLESCONTROLADORURL14472", "307003");

    private final String key;
    private final String value;

    private ActadefinanciablesControladorUrlEnum(String key, String value) {
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
