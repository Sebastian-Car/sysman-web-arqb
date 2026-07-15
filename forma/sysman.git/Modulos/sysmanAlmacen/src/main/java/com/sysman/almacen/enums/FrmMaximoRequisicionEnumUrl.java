/*
 * FrmeditarespecplacasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmMaximoRequisicionEnumUrl {

    URL0001("FRMMAXIMOREQUISICIONENUMURL01", "62002"), // DEPENDENCIA

    URL0002("FRMMAXIMOREQUISICIONENUMURL02", "14001"), // RESPONSABLE

    URL0003("FRMMAXIMOREQUISICIONENUMURL03", "4001"), // AÑOS

    URL0004("FRMMAXIMOREQUISICIONENUMURL04", "1060001"), // VALOR_ACUMULADO

    URL0005("FRMMAXIMOREQUISICIONENUMURL05", "1060002"); // VALOR_MAX

    private final String key;
    private final String value;

    private FrmMaximoRequisicionEnumUrl(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
