/*
 * NovedadesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SindicatosControladorEnum {

    KEY_COMPANIA("KEY_COMPANIA"),

    NIT("NIT"),

    NOMBRE_FONDO("NOMBRE_FONDO"),

    ID_DE_FONDO("ID_DE_FONDO"),

    VALORCUOTA("VALORCUOTAADICIONAL"),

    CONCEPTOCUOTAADICIONAL("CONCEPTOCUOTAADICIONAL"),

    DIASPRIMASERVICIO("DIASPRIMASERVICIO")

    ;

    private final String value;

    private SindicatosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
