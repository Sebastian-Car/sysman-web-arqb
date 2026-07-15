/*
 * FrmcuotasacuerdosControladorUrlEnum
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
public enum FrmcuotasacuerdosControladorUrlEnum {

    URL6362("FRMCUOTASACUERDOSCONTROLADORURL6362", "371008"),

    URL7946("FRMCUOTASACUERDOSCONTROLADORURL7946", "367064"),

    URL4397("FRMCUOTASACUERDOSCONTROLADORURL4397", "375004"),

    URL5637("FRMCUOTASACUERDOSCONTROLADORURL5637", "371006"),

    URL4969("FRMCUOTASACUERDOSCONTROLADORURL4969", "375006"),

    URL7207("FRMCUOTASACUERDOSCONTROLADORURL7207", "367062");

    private final String key;
    private final String value;

    private FrmcuotasacuerdosControladorUrlEnum(String key, String value)
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
