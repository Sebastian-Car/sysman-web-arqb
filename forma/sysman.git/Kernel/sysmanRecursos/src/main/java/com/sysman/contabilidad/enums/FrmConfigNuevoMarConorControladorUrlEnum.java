/*-
 * FrmConfigNuevoMarConorControladorUrlEnum.java
 *
 * 1.0
 * 
 * 7/02/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmConfigNuevoMarConorControladorUrlEnum {

    URL3655("FRMCONFIGNUEVOMARCONORCONTROLADORURL3655",
                    "4001");

    private final String key;
    private final String value;

    private FrmConfigNuevoMarConorControladorUrlEnum(String key, String value) {
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
