/*-
 * ImprimirHvPrestacionesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 14 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author amonroy
 * 
 * @version 1.0, 14 de dic. de 2017
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ImprimirHvPrestacionesControladorUrlEnum {

    URL001("RPTNOMBRAMIENTOCONTROLADORURL001", "685001"),

    URL002("RPTNOMBRAMIENTOCONTROLADORURL002", "685003");

    private final String key;
    private final String value;

    private ImprimirHvPrestacionesControladorUrlEnum(String key, String value) {
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
