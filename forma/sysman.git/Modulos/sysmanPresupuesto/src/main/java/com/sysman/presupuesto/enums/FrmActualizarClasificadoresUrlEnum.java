/*
 * RevisarafectacionesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
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
 */
public enum FrmActualizarClasificadoresUrlEnum {

    URL3043("FrmActualizarClasificadoresUrlEnum3043", "4001"),
    URL0001("FrmActualizarClasificadoresUrlEnum001", "38059"),
	URL0002("FrmActualizarClasificadoresUrlEnum002", "38060"),
	URL0003("FrmActualizarClasificadoresUrlEnum003", "38061"),
	URL0004("FrmActualizarClasificadoresUrlEnum004", "4"),
	URL0005("FrmActualizarClasificadoresUrlEnum005", "5"),
	URL0006("FrmActualizarClasificadoresUrlEnum006", "6"),
	URL0007("FrmActualizarClasificadoresUrlEnum007", "38062"),
	URL0008("FrmActualizarClasificadoresUrlEnum008", "8"),
	URL0009("FrmActualizarClasificadoresUrlEnum009", "38063");
	

    private final String key;
    private final String value;

    private FrmActualizarClasificadoresUrlEnum(String key, String value) {
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
