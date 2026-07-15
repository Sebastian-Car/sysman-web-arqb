/*
 * ParametroAdicionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmprogramacionactividadesControladorUrlEnum {

    URL0001("FRMPROGRAMACIONACTIVIDADESCONTROLADORURL0001", "32003"),

    URL0002("FRMPROGRAMACIONACTIVIDADESCONTROLADORURL0002", "218003"),

    URL0003("FRMPROGRAMACIONACTIVIDADESCONTROLADORURL0003", "554005"),

    URL0010("FRMPROGRAMACIONACTIVIDADESCONTROLADORURL0010", "513010"),

    URL0011("FRMPROGRAMACIONACTIVIDADESCONTROLADORURL0011", "571002");

    private final String key;
    private final String value;

    private FrmprogramacionactividadesControladorUrlEnum(String key,
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
