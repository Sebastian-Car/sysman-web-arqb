/*
 * ProponenteiteminventariosControladorEnum
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
public enum ProponenteiteminventariosControladorEnum {

    TIPOCONTRATO("TIPOCONTRATO"),

    TRANSACCION("TRANSACCION"),

    PROPONENTE("PROPONENTE"),

    IDELEMENTO("IDELEMENTO"),

    VALORUNITARIO("VALORUNITARIO"),

    PORCDESC("PORCDESC"),

    PORCIVA("PORCIVA"),

    VALORUNITARIODI("VALORUNITARIODI"),

    VLRIVA("VLRIVA"),

    VLRDESCUENTO("VLRDESCUENTO"),

    VLRTOTAL("VLRTOTAL"),

    CONSECUTIVODETALLE("CONSECUTIVODETALLE"),

    VALTOTAL("VALTOTAL"),

    VALDESCTOTAL("VALDESCTOTAL"),

    VALIVATOTAL("VALIVATOTAL"),

    VALUNITARIODITOTAL("VALUNITARIODITOTAL"),

    PROPONENTE_ITEMINVENTARIO("PROPONENTE_ITEMINVENTARIO");

    private final String value;

    private ProponenteiteminventariosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
