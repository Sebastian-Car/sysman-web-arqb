/*
 * EntidadesCapacitacionControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmauxiliospersonalControladorUrlEnum {

    URL100("FRMAUXILIOSPERSONALCONTROLADORURLENUM100", "69500C"), // CREAR

    URL101("FRMAUXILIOSPERSONALCONTROLADORURLENUM101", "69500D"), // ELIMINAR

    URL102("FRMAUXILIOSPERSONALCONTROLADORURLENUM102", "69500R"), // SELECT

    URL103("FRMAUXILIOSPERSONALCONTROLADORURLENUM103", "695001"), // GRILLA

    URL104("FRMAUXILIOSPERSONALCONTROLADORURLENUM104", "695005"), // UPDATE

    URL105("FRMAUXILIOSPERSONALCONTROLADORURLENUM105", "210109"), // COMBO
                                                                  // BUSCAR
    URL106("FRMAUXILIOSPERSONALCONTROLADORURLENUM106", "685054")

    ;

    private final String key;
    private final String value;

    private FrmauxiliospersonalControladorUrlEnum(String key,
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
