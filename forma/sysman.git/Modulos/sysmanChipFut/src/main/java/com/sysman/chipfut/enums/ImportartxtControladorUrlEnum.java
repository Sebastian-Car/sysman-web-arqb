/*
 * ImportartxtControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ImportartxtControladorUrlEnum {

    URL14603("IMPORTARTXTCONTROLADORURL14603",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, codigoDetalle,"),

    URL13188("IMPORTARTXTCONTROLADORURL13188",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, codigoDetalle,"),

    URL7781("IMPORTARTXTCONTROLADORURL7781",
                    "4001");

    private final String key;
    private final String value;

    private ImportartxtControladorUrlEnum(String key, String value) {
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
