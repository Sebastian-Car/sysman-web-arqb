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
public enum FrmrubroinversionsControladorUrlEnum {

    URL0001("FRMRUBROINVERSIONSCONTROLADORURL0001", "4001"),

    URL0010("FRMRUBROINVERSIONSCONTROLADORURL0010", "45042"),

    URL0013("FRMRUBROINVERSIONSCONTROLADORURL0013", "430033"),

    URL0006("FRMRUBROINVERSIONSCONTROLADORURL0006", "576007"),

    URL0004("FRMRUBROINVERSIONSCONTROLADORURL0004", "576008"),

    URL0011("FRMRUBROINVERSIONSCONTROLADORURL0011", "576010"),

    URL0012("FRMRUBROINVERSIONSCONTROLADORURL0012", "576011");

    private final String key;
    private final String value;

    private FrmrubroinversionsControladorUrlEnum(String key, String value) {
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
