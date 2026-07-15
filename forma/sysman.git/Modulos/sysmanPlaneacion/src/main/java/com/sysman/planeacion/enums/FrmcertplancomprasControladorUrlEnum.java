/*
 * FrmcertplancomprasControladorUrlEnum
 *
 * 1.0
 *
 * 07/09/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmcertplancomprasControladorUrlEnum {

    URL3320("FRMCERTPLANCOMPRASCONTROLADORURL3320", "62001"),

    URL3321("FRMCERTPLANCOMPRASCONTROLADORURL3321", "71013"),

    URL3322("FRMCERTPLANCOMPRASCONTROLADORURL3322", "112107"),

    URL3324("FRMCERTPLANCOMPRASCONTROLADORURL3324", "62043"),

    URL3325("FRMCERTPLANCOMPRASCONTROLADORURL3325", "71016"),

    URL3326("FRMCERTPLANCOMPRASCONTROLADORURL3326", "4001"),

    URL3327("FRMCERTPLANCOMPRASCONTROLADORURL3327", "541001"),

    URL3500("FRMCERTPLANCOMPRASCONTROLADORURL3500", "542006"),
    
    URL114010("FRMCERTPLANCOMPRASCONTROLADORURL114010", "114010");

    private final String key;
    private final String value;

    private FrmcertplancomprasControladorUrlEnum(String key, String value) {
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
