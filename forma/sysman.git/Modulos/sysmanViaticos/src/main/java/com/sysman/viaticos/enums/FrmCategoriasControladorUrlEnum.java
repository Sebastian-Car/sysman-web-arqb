/*
 * FrmCategoriasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmCategoriasControladorUrlEnum {

    URL5717("FRMCATEGORIASCONTROLADORURL5717",
                    "471002"),

    URL6507("FRMCATEGORIASCONTROLADORURL6507",
                    "462005"),

    URL3238("FRMCATEGORIASCONTROLADORURL3238",
                    "607012"),

    URL5555("FRMCATEGORIASCONTROLADORURL5555",
                    "607015"),

    URL7777("FRMCATEGORIASCONTROLADORURL7777",
                    "607014"),

    URL9999("FRMCATEGORIASCONTROLADORURL9999",
                    "607007"),

    URL7238("FRMCATEGORIASCONTROLADORURL7238",
                    " listaEscalafonE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1632:TBCB5463\", \"SELECT \" + \" ESCALAFON.CODIGO, \" + \" ESCALAFON.NOMBRE \" + \" FROM \" + \" ESCALAFON \" + \" WHERE \" + \" (((ESCALAFON.COMPANIA) = '\" + compania + \"')) \" + \" \",");

    private final String key;
    private final String value;

    private FrmCategoriasControladorUrlEnum(String key, String value) {
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
