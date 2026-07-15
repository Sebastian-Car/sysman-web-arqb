/*
 * FrmAprobacionComisionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmAprobacionComisionControladorUrlEnum {

    URL24545("FRMAPROBACIONCOMISIONCONTROLADORURL24545",
                    "761008"),

    URL28997("FRMAPROBACIONCOMISIONCONTROLADORURL28997",
                    " listaBanco = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1926_nuevo:TBCB6430\", \"SELECT BANCO, NOMBREBANCO FROM BANCO WHERE COMPANIA = :COMPANIA ORDER BY BANCO, COMPANIA\","),

    URL27671("FRMAPROBACIONCOMISIONCONTROLADORURL27671",
                    "62099"),

    URL28547("FRMAPROBACIONCOMISIONCONTROLADORURL28547",
                    "52002"),

    URL26942("FRMAPROBACIONCOMISIONCONTROLADORURL26942",
                    "768001"),

    URL12823("FRMAPROBACIONCOMISIONCONTROLADORURL12823",
                    "1001"),

    URL272824("FRMAPROBACIONCOMISIONCONTROLADORURL272824",
                    "76100U"),

    URL2852("FRMAPROBACIONCOMISIONCONTROLADORURL12823",
                    "76100R"),
    
    URL4147("FRMAPROBACIONCOMISIONCONTROLADORURL4147",
                    "2001"),
    
    URL5852("FRMAPROBACIONCOMISIONCONTROLADORURL5852",
                    "5002")
    ;

    private final String key;
    private final String value;

    private FrmAprobacionComisionControladorUrlEnum(String key, String value) {
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
