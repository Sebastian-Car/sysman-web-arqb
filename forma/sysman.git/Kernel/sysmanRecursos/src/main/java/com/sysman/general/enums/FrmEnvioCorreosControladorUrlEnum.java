/*
 * ApropiacioninicialanoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmEnvioCorreosControladorUrlEnum {

    URL001("ENVIOCORREOS001", "1663001"), // COMBO CORREO

    URL002("ENVIOCORREOS002", "1664001"); // LISTA MULTIPLE

    private final String key;
    private final String value;

    private FrmEnvioCorreosControladorUrlEnum(String key, String value) {
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
