/*
 * FrmTipoCobroSfControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmTipoCobroSfControladorUrlEnum {
    URL5127("FRMTIPOCOBROSFCONTROLADORURL5127", "4001"),

    URL6724("FRMTIPOCOBROSFCONTROLADORURL6724", "15055"),

    URL3030("FRMTIPOCOBROSFCONTROLADORURL3030", "63003"),

    URL5970("FRMTIPOCOBROSFCONTROLADORURL5970", "15054"),

    URL4980("FRMTIPOCOBROSFCONTROLADORURL4980", "15074"),
    
    URL4981("FRMTIPOCOBROSFCONTROLADORURL4980", "104011"),
    
    URL3784("FRMTIPOCOBROSFCONTROLADORURL3784", "1848001"),
    
    URL8894("FRMTIPOCOBROSFCONTROLADORURL8894", "1853001"),
    
    URL15011("FRMTIPOCOBROSFCONTROLADORURL15011", "15011")
    
    ;

    private final String key;
    private final String value;

    private FrmTipoCobroSfControladorUrlEnum(String key, String value) {
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
