/*
 * ActualizarSaldosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmCrearProgaProyectosControladorUrlEnum {

    URL3222("FRMCREARPROGAPROYECTOSCONTROLADORURL3222", "32043"),

    URL0001("FRMCREARPROGAPROYECTOSCONTROLADORURL0001", "32049"),

    URL17434("FRMCREARPROGAPROYECTOSCONTROLADORURL17434", "4056");

    private final String key;
    private final String value;

    private FrmCrearProgaProyectosControladorUrlEnum(String key, String value) {
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
