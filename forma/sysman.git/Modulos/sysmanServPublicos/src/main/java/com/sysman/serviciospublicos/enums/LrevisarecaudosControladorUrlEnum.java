/*
 * LrevisarecaudosControladorUrlEnum
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
public enum LrevisarecaudosControladorUrlEnum {

    URL4164("LREVISARECAUDOSCONTROLADORURL4164",
                    "214071"),

    URL8648("LREVISARECAUDOSCONTROLADORURL8648",
                    " fechaPreparacion = service.buscarEnListaObj(ciclo, \"NUMERO\", \"FECHA_PREPARACION\", listaCiclo) == null ? null : (Date) service.buscarEnListaObj(ciclo, \"NUMERO\", \"FECHA_PREPARACION\",");

    private final String key;
    private final String value;

    private LrevisarecaudosControladorUrlEnum(String key, String value) {
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
