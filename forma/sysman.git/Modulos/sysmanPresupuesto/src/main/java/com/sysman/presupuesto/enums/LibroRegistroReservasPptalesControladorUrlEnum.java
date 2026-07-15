/*
 * LibroRegistroReservasPptalesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LibroRegistroReservasPptalesControladorUrlEnum {

    URL4492("LIBROREGISTRORESERVASPPTALESCONTROLADORURL4492", "7007"),

    URL9507("LIBROREGISTRORESERVASPPTALESCONTROLADORURL9507", "23010"),

    URL10142("LIBROREGISTRORESERVASPPTALESCONTROLADORURL10142", "23019"),

    URL6845("LIBROREGISTRORESERVASPPTALESCONTROLADORURL6845", "45026"),

    URL5783("LIBROREGISTRORESERVASPPTALESCONTROLADORURL5783", "45024"),

    URL8043("LIBROREGISTRORESERVASPPTALESCONTROLADORURL8043", "20013"),

    URL4924("LIBROREGISTRORESERVASPPTALESCONTROLADORURL4924", "7012"),

    URL8771("LIBROREGISTRORESERVASPPTALESCONTROLADORURL8771", "20015"),

    URL5425("LIBROREGISTRORESERVASPPTALESCONTROLADORURL5425", "4001");

    private final String key;
    private final String value;

    private LibroRegistroReservasPptalesControladorUrlEnum(String key,
        String value) {
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
