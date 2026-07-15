/*
 * PredialcertcatastralControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PredialcertcatastralControladorUrlEnum {

    URL9817("PREDIALCERTCATASTRALCONTROLADORURL9817", "104008"),

    URL8986("PREDIALCERTCATASTRALCONTROLADORURL8986", "367129"),

    URL7969("PREDIALCERTCATASTRALCONTROLADORURL7969", "367126"),

    URL14665("PREDIALCERTCATASTRALCONTROLADORURL14665", "400003"),

    URL14666("PREDIALCERTCATASTRALCONTROLADORURL14665", "372003"),

    URL14667("PREDIALCERTCATASTRALCONTROLADORURL14665", "400004");

    private final String key;
    private final String value;

    private PredialcertcatastralControladorUrlEnum(String key, String value) {
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
