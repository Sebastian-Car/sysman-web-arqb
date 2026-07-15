/*
 * DsupervisoresControladorUrlEnum
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
public enum DsupervisoresControladorUrlEnum {

    URL7544("DSUPERVISORESCONTROLADORURL7544", "14016"), URL8428(
                    "DSUPERVISORESCONTROLADORURL8428",
                    " listacedulaE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR353:TBCB1333\", \"SELECT \" + \" TERCERO.NIT, \" + \" SUBSTR(TERCERO.NOMBRE, 1, 100) NOMBRE,\" + \" TERCERO.SUCURSAL, \" + \" TERCERO.CARGO,\" + \" PROFESION \" + \" FROM TERCERO \" + \" WHERE TERCERO.IND_INTERVENTOR <> 0 \" + \" AND TERCERO.COMPANIA = '\" + compania + \"'\" + \" ORDER BY \" + \" TERCERO.NIT, \" + \" SUBSTR(TERCERO.NOMBRE, 1, 100)\",");

    private final String key;
    private final String value;

    private DsupervisoresControladorUrlEnum(String key, String value) {
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
