/*
 * FrmPolitaCuipoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.cgr.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados
 * en el refactoring y asociados al codigo legacy obtenido con patrones de
 * busqueda.
 */
public enum FrmPolitaCuipoControladorUrlEnum {

    URL4647("FRMPOLITACUIPOCONTROLADORURL4647", "4001");

    private final String key;
    private final String value;

    private FrmPolitaCuipoControladorUrlEnum(String key, String value) {
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
