/*
 * PagosdoblesdosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum PagosdoblesdosControladorUrlEnum {

    URL4857("PAGOSDOBLESDOSCONTROLADORURL4857", "375004"), URL15185("PAGOSDOBLESDOSCONTROLADORURL15185",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"IP_PAGO_BANCOSCAB\","), URL19269(
                                    "PAGOSDOBLESDOSCONTROLADORURL19269",
                                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"IP_PAGOSDOBLES\","), URL18849(
                                                    "PAGOSDOBLESDOSCONTROLADORURL18849",
                                                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"IP_RECIBOS_DE_PAGO\",");

    private final String key;
    private final String value;

    private PagosdoblesdosControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
