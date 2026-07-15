/*
 * FrmgenerarrecpysControladorUrlEnum
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
public enum FrmgenerarrecpysControladorUrlEnum {

    URL4372("FRMGENERARRECPYSCONTROLADORURL4372", "367068"),

    URL222("FRMGENERARRECPYSCONTROLADORURL222", "367070"),

    URL254("FRMGENERARRECPYSCONTROLADORURL254", "367071"),

    URL366("FRMGENERARRECPYSCONTROLADORURL366", "394003"),

    URL380("FRMGENERARRECPYSCONTROLADORURL380", "394004"),

    URL448("FRMGENERARRECPYSCONTROLADORURL448", "400001"),

    URL538("FRMGENERARRECPYSCONTROLADORURL538", "367077"),

    URL611("FRMGENERARRECPYSCONTROLADORURL611", "385012"),

    URL653("FRMGENERARRECPYSCONTROLADORURL653", "386002"),

    URL700("FRMGENERARRECPYSCONTROLADORURL700", "385013"),

    URL737("FRMGENERARRECPYSCONTROLADORURL737", "386003"),

    URL718("FRMGENERARRECPYSCONTROLADORURL718", "385014"),

    URL770("FRMGENERARRECPYSCONTROLADORURL770", "400002");

    private final String key;
    private final String value;

    private FrmgenerarrecpysControladorUrlEnum(String key, String value)
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
