/*-
 * ActualizaparametrosretroactivosControladorUrlEnum.java
 *
 * 1.0
 *
 * 18/08/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores generados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 *
 * @version 1.0, 28/03/2019
 * @author mzanguna
 *
 */
public enum AsignacionpagosespecialesControladorUrlEnum {

    URL0001("DETALLEPAGOS", "1782006"),

    URL0002("PAGOESPECIALPERSONAL", "1788001"),

    URL0003("PAGOESPECIALPERSONALPOST", "1788003"),

    URL0004("PAGOESPECIALPERSONALPUT", "1788004"),

    URL0005("PAGOESPECIALPERSONALDELETE", "1788005"),

    URL0006("PAGOESPECIALCARGOPOST", "1789003"),

    URL0007("PAGOESPECIALCARGOGET", "1789001"),

    URL0008("PAGOESPECIALCARGOPUT", "1789004"),

    URL0009("PAGOESPECIALCARGODELETE", "1789005"),

    URL0010("PAGOESPECIALCATEGORIAGET", "1790001"),

    URL0011("PAGOESPECIALCATEGORIAPUT", "1790003"),

    URL0012("PAGOESPECIALCATEGORIAPOST", "1790004"),

    URL0013("PAGOESPECIALCATEGORIADELETE", "1790005"),

    URL0014("CARGOSGET", "463042"),

    URL0015("PERSONALGET", "210141"),

    URL0016("CATEGORIAGET", "607020"),

    URL0017("PAGOESPECIALGRILLA", "1781001"),

    URL0018("PAGOESPECIALGRILLA", "1781003");

    private final String key;
    private final String value;

    private AsignacionpagosespecialesControladorUrlEnum(String key,
        String value)
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
