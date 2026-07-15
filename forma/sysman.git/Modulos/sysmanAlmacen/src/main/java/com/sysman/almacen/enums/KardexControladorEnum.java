/*
 * KardexControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum KardexControladorEnum {

    CONSULTA800110("800110MovAlmacenSerieFechaHora"),

    ULTIMOSERIE("baseUltimoSerie"),

    FORMATOFECHA("dd/MM/yyyy HH:mm:ss"),

    FECHAHASTA("fechaHasta"),

    TIPOELEMENTO("TIPOELEMENTO"),

    ELEMENTODESDE("ELEMENTODESDE");

    private final String value;

    private KardexControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
