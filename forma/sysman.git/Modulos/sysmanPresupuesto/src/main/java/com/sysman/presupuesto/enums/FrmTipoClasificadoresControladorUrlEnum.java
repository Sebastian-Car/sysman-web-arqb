/*
 * FrmTipoClasificadoresControladorUrlEnum
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
public enum FrmTipoClasificadoresControladorUrlEnum {

    URL4425("FRMTIPOCLASIFICADORESCONTROLADORURL4425", "4014"),

    URL4190("FRMTIPOCLASIFICADORESCONTROLADORURL4190", "20001"),

    URL4663("FRMTIPOCLASIFICADORESCONTROLADORURL4663", "12003"),

    URL8298("FRMTIPOCLASIFICADORESCONTROLADORURL8298", "1884001"),

    URL3567("FRMTIPOCLASIFICADORESCONTROLADORURL3567", "4001"),
    
    URL3568("FRMTIPOCLASIFICADORESCONTROLADORURL3568", "1883005"),

    URL5125("FRMTIPOCLASIFICADORESCONTROLADORURL5125", "8001"),

    URL3679("FRMTIPOCLASIFICADORESCONTROLADORURL3679", "16074"),

    URL3947("FRMTIPOCLASIFICADORESCONTROLADORURL3947", "20023"),

    URL3948("FRMTIPOCLASIFICADORESCONTROLADORURL3948", "29060"),

    URL3949("FRMTIPOCLASIFICADORESCONTROLADORURL3949", "29061"),

    URL3950("FRMTIPOCLASIFICADORESCONTROLADORURL3950", "23005"),

    URL4899("FRMTIPOCLASIFICADORESCONTROLADORURL4899", "23010"),
    
    URL1884018("FRMTIPOCLASIFICADORESCONTROLADORURL4899","1884018"),
    
    URL1889001("FRMTIPOCLASIFICADORESCONTROLADORURL4899","1889001"),
    
	URL1889003("FRMTIPOCLASIFICADORESCONTROLADORURL1889003","1889003"),
	
	URL1889013("FRMTIPOCLASIFICADORESCONTROLADORURL1889013","1889013");

    private final String key;
    private final String value;

    private FrmTipoClasificadoresControladorUrlEnum(String key, String value) {
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
