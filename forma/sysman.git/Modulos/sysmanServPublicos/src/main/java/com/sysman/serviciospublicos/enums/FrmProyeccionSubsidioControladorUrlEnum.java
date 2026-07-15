/*
 * FrmProyeccionSubsidioControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmProyeccionSubsidioControladorUrlEnum {

    URL8839("FRMPROYECCIONSUBSIDIOCONTROLADORURL8839", "214029"),

    URL10675("FRMPROYECCIONSUBSIDIOCONTROLADORURL10675", "227030"),

    URL9335("FRMPROYECCIONSUBSIDIOCONTROLADORURL9335", "227029"),

    URL11365("FRMPROYECCIONSUBSIDIOCONTROLADORURL11365", "227032"),

    URL9962("FRMPROYECCIONSUBSIDIOCONTROLADORURL9962", "227031");

    private final String key;
    private final String value;

    private FrmProyeccionSubsidioControladorUrlEnum(String key, String value) {
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
