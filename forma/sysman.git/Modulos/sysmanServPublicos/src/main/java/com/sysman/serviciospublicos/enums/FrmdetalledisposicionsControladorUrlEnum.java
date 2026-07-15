/*
 * FrmdetalledisposicionsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmdetalledisposicionsControladorUrlEnum {

    URL11862("FRMDETALLEDISPOSICIONSCONTROLADORURL11862", "366004"),

    URL10995("FRMDETALLEDISPOSICIONSCONTROLADORURL10995", "227017"),

    URL12695("FRMDETALLEDISPOSICIONSCONTROLADORURL12695", "227012"),

    URL13214("FRMDETALLEDISPOSICIONSCONTROLADORURL13214", "2001"),

    URL10197("FRMDETALLEDISPOSICIONSCONTROLADORURL10197", "5001"),

    URL9658("FRMDETALLEDISPOSICIONSCONTROLADORURL9658", "1001");

    private final String key;
    private final String value;

    private FrmdetalledisposicionsControladorUrlEnum(String key, String value) {
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
