/*
 * NivelplanindsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum NivelplanindsControladorUrlEnum {

    URL4542("NIVELPLANINDSCONTROLADORURL4542", "4001"),

    URL4543("NIVELPLANINDSCONTROLADORURL4543", "554015"),

    URL4544("NIVELPLANINDSCONTROLADORURL4544", "554016"),

    URL4545("NIVELPLANINDSCONTROLADORURL4545", "433013"),

    URL4546("NIVELPLANINDSCONTROLADORURL4546", "552028"),;

    private final String key;
    private final String value;

    private NivelplanindsControladorUrlEnum(String key, String value) {
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
