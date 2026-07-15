/*
 * FrmAudCertValoriControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmAudCertValoriControladorUrlEnum {

    URL5528("FRMAUDCERTVALORICONTROLADORURL5528", "393003"),

    URL4892("FRMAUDCERTVALORICONTROLADORURL4892", "393001");

    private final String key;
    private final String value;

    private FrmAudCertValoriControladorUrlEnum(String key, String value) {
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
