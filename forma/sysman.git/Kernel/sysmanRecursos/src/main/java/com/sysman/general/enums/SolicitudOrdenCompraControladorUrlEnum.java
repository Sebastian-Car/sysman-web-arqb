/*
 * SolicitudOrdenCompraControladorUrlEnum
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
public enum SolicitudOrdenCompraControladorUrlEnum {

    URL7206("SOLICITUDORDENCOMPRACONTROLADORURL7206",
                    "189003"), URL2601(
                                    "SOLICITUDORDENCOMPRACONTROLADORURL2601",
                                    "130001"), URL170(
                                                    "SOLICITUDORDENCOMPRACONTROLADORURL170",
                                                    "131001"), URL101(
                                                                    "SOLICITUDORDENCOMPRACONTROLADORURL101",
                                                                    "130003"), URL202(
                                                                                    "SOLICITUDORDENCOMPRACONTROLADORURL202",
                                                                                    "189004");

    private final String key;
    private final String value;

    private SolicitudOrdenCompraControladorUrlEnum(String key, String value) {
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
