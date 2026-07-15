/*
 * FrmfichatecnicaproyectosControladorUrlEnum
 *
 * 1.0
 *
 * spin 14/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmfichatecnicaproyectosControladorUrlEnum {

    URL2390("FRMFICHATECNICAPROYECTOSCONTROLADORURL2390", "56300G"),

    URL2391("FRMFICHATECNICAPROYECTOSCONTROLADORURL2391", "564001"),

    URL2392("FRMFICHATECNICAPROYECTOSCONTROLADORURL2392", "565001"),

    URL2393("FRMFICHATECNICAPROYECTOSCONTROLADORURL2393", "567001"),

    URL2394("FRMFICHATECNICAPROYECTOSCONTROLADORURL2394", "567002"),

    URL2395("FRMFICHATECNICAPROYECTOSCONTROLADORURL2395", "567003"),

    URL2397("FRMFICHATECNICAPROYECTOSCONTROLADORURL2397", "567004"),

    URL2398("FRMFICHATECNICAPROYECTOSCONTROLADORURL2398", "56300U")

    ;

    private final String key;
    private final String value;

    private FrmfichatecnicaproyectosControladorUrlEnum(String key, String value)
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
