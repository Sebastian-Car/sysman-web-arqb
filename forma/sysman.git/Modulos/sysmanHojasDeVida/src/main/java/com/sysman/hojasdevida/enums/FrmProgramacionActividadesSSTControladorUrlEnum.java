/*
 * FrmProgramacionActividadesSSTControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmProgramacionActividadesSSTControladorUrlEnum {

    URL0001("FRMPROGRAMACIONACTIVIDADESSSTCONTROLADORURL0001", "954001"),

    URL7194("FRMPROGRAMACIONACTIVIDADESSSTCONTROLADORURL7194", "749001"),

    URL5825("FRMPROGRAMACIONACTIVIDADESSSTCONTROLADORURL5825", "685050"),

    URL5494("FRMPROGRAMACIONACTIVIDADESSSTCONTROLADORURL5494", "651001"),

    URL6603("FRMPROGRAMACIONACTIVIDADESSSTCONTROLADORURL6603", "209002"),

    URL0002("FRMPROGRAMACIONACTIVIDADESSSTCONTROLADORURL0002", "14001"),
    // Personal
    URL0003("FRMPROGRAMACIONACTIVIDADESSSTCONTROLADORURL0003", "210124"),

    URL0004("FRMPROGRAMACIONACTIVIDADESSSTCONTROLADORURL0004", "210133"),
    // TERCERO
    URL0005("FRMPROGRAMACIONACTIVIDADESSSTCONTROLADORURL0004", "14160");

    private final String key;
    private final String value;

    private FrmProgramacionActividadesSSTControladorUrlEnum(String key,
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
