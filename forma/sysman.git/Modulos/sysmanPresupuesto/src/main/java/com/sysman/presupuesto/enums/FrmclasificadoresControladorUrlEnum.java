/*
 * FrmclasificadoresControladorUrlEnum
 *
 * 1.0
 *
 * 06/01/2022
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmclasificadoresControladorUrlEnum {

    URL4425("FRMCLASIFICADORESCONTROLADORURL4425", "4014"),

    URL4190("FRMCLASIFICADORESCONTROLADORURL4190", "20001"),

    URL4663("FRMCLASIFICADORESCONTROLADORURL4663", "12003"),

    URL8298("FRMCLASIFICADORESCONTROLADORURL8298", "1883003"),

    URL3567("FRMCLASIFICADORESCONTROLADORURL3567", "4001"),

    URL5125("FRMCLASIFICADORESCONTROLADORURL5125", "8001"),

    URL3679("FRMCLASIFICADORESCONTROLADORURL3679", "16074"),

    URL3947("FRMCLASIFICADORESCONTROLADORURL3947", "20023"),

    URL3948("FRMCLASIFICADORESCONTROLADORURL3948", "29060"),

    URL3949("FRMCLASIFICADORESCONTROLADORURL3949", "29061"),

    URL3950("FRMCLASIFICADORESCONTROLADORURL3950", "23005"),

    URL4899("FRMCLASIFICADORESCONTROLADORURL4899", "23010");

    private final String key;
    private final String value;

    private FrmclasificadoresControladorUrlEnum(String key, String value) {
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
