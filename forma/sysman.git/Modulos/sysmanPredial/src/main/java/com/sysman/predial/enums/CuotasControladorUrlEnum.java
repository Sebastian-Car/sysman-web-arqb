/*
 * CuotasControladorUrlEnum
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
public enum CuotasControladorUrlEnum {
    URL9722("CUOTASCONTROLADORURL9722", "390001"), URL9734("CUOTASCONTROLADORURL9734", ""), URL4896(
                    "CUOTASCONTROLADORURL4896", "227001"), URL4824(
                                    "CUOTASCONTROLADORURL4824", "390002"), URL4825(
                                                    "CUOTASCONTROLADORURL4825", "390003"), URL4826(
                                                                    "CUOTASCONTROLADORURL4826", "390004"), URL4827(
                                                                                    "CUOTASCONTROLADORURL4827", "390005");
    private final String key;
    private final String value;

    private CuotasControladorUrlEnum(String key, String value)
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
