/*
 * EpitemsestproysControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum EpitemsestproysControladorEnum {

    ADMINISTRACION("ADMINISTRACION"),

    IMPREVISTOS("IMPREVISTOS"),

    UTILIDADES("UTILIDADES"),

    ES_DITEM_E("ES_DITEM_E"),

    COD_ITEM("COD_ITEM"),

    VLRTOTAL("VLRTOTAL"),

    SUBTOTAL("SUBTOTAL"),

    CODESTUDIO("CODESTUDIO"),

    CODIGOITEM("CODIGOITEM"),

    COD_DITEM("COD_DITEM"),

    CREATED_BY("CREATED_BY"),

    DATE_CREATED("DATE_CREATED"),

    COD_STUDIOLOWER("codEstudio"),

    COD_ITEMLOWER("cod_Item"),

    ELEMENTOLOWER("elemento"),

    RIDLOWER("rid"),

    RIDUPPER("RID"),

    NOMBRELARGO("NOMBRELARGO"),

    VLRUNITARIOPROM("VLRUNITARIOPROM"),

    SUCURSAL_RESPONSABLE("SUCURSAL_RESPONSABLE"),

    PORCIVAGLOBAL("PORCIVAGLOBAL"),

    PORCDESCGLOBAL("PORCDESCGLOBAL"),

    A_GLOBAL("A_GLOBAL"),

    I_GLOBAL("I_GLOBAL"),

    U_GLOBAL("U_GLOBAL"),

    TXT_COD_ESTUDIOLOWER("txtCodEstudio"),

    VIGENCIA_PERIODOLOWER("vigenciaPeriodo"),

    ES_CREADORLOWER("esCreador"),

    CODIGOESTUDIO("CODIGOESTUDIO")

    ;

    private final String value;

    private EpitemsestproysControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
