/*
 * FrmCodificacionGlosasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.sysmanauditoriacuentasmedicas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmCodificacionGlosasControladorUrlEnum {

    URL4864("FRMCODIFICACIONGLOSASCONTROLADORURL4864",
                    "1833001"),

    URL5641("FRMCODIFICACIONGLOSASCONTROLADORURL5641",
                    "1834001"),

    URL5254("FRMCODIFICACIONGLOSASCONTROLADORURL5254",
                    "1834001"),

    URL4476("FRMCODIFICACIONGLOSASCONTROLADORURL4476",
                    "1833001");

    private final String key;
    private final String value;

    private FrmCodificacionGlosasControladorUrlEnum(String key, String value) {
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
