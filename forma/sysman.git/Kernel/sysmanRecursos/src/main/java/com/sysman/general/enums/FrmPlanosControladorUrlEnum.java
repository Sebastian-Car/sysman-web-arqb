/*
 * FrmPlanosControladorUrlEnum
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
public enum FrmPlanosControladorUrlEnum {

    URL4270("FRMPLANOSCONTROLADORURL4270",
                    "673001"),

    URL4620("FRMPLANOSCONTROLADORURL4620",
                    " listagrupoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1470:TBCB4828\", \"SELECT GRUPO, DESCRIPCION FROM GRUPO_PLANOS\",");

    private final String key;
    private final String value;

    private FrmPlanosControladorUrlEnum(String key, String value) {
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
