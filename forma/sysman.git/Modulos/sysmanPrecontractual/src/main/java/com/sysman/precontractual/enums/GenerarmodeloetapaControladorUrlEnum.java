/*
 * GenerarmodeloetapaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum GenerarmodeloetapaControladorUrlEnum {

    URL9309("GENERARMODELOETAPACONTROLADORURL9309", "188009"),

    URL10244("GENERARMODELOETAPACONTROLADORURL10244", "523001"),

    URL6552("GENERARMODELOETAPACONTROLADORURL6552", "184003"),

    URL8124("GENERARMODELOETAPACONTROLADORURL8124", "520003"),

    URL12013("GENERARMODELOETAPACONTROLADORURL12013", "104043"),

    URL6913("GENERARMODELOETAPACONTROLADORURL6913", "520001"),

    URL6914("GENERARMODELOETAPACONTROLADORURL6914", "490003");

    private final String key;
    private final String value;

    private GenerarmodeloetapaControladorUrlEnum(String key, String value) {
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
