/*-
 * RegistrosaldopptalsControlador.java
 *
 * 1.0
 *
 * 20/04/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 *
 * @version 1.0, 20/04/2017
 * @author lcortes
 *
 */
public enum RegistrosaldopptalsControladorUrlEnum {

    URL119("REGISTROSALDOPPTALSCONTROLADORURL119", "94096");

    private final String key;
    private final String value;

    private RegistrosaldopptalsControladorUrlEnum(String key, String value) {
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
