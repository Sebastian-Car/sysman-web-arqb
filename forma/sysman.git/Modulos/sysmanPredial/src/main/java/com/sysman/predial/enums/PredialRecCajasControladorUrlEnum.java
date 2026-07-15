/*
 * PredialRecCajasControladorUrlEnum
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
public enum PredialRecCajasControladorUrlEnum {
    URL9523("PREDIALRECCAJASCONTROLADORURL9523",
                    "400005"), URL9503("PREDIALRECCAJASCONTROLADORURL9503",
                                    "375008"), URL8404(
                                                    "PREDIALRECCAJASCONTROLADORURL8404",
                                                    "385016"), URL10110(
                                                                    "PREDIALRECCAJASCONTROLADORURL10110", "367147");
    private final String key;
    private final String value;

    private PredialRecCajasControladorUrlEnum(String key, String value)
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
