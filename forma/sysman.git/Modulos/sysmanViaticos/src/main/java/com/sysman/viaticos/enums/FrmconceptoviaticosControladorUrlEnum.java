/*-
 * FrmconceptoviaticosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos.enums;

/**
 * Este enumerado me relaciona los servicios necesarios para el
 * controlador FrmconceptoviaticosControlador
 * 
 * @version 1.0, 18/01/2018
 * @author mvenegas
 *
 */
public enum FrmconceptoviaticosControladorUrlEnum {

    URL100("FRMCONCEPTOVIATICOSCONTROLADORURL100", "471060"),

    URL101("FRMCONCEPTOVIATICOSCONTROLADORURL101", "29134"),

    URL102("FRMCONCEPTOVIATICOSCONTROLADORURL102", "25038"),

    URL103("FRMCONCEPTOVIATICOSCONTROLADORURL103", "75037"),

    URL104("FRMCONCEPTOVIATICOSCONTROLADORURL104", "38037"),

    URL105("FRMCONCEPTOVIATICOSCONTROLADORURL105", "15058"),

    URL106("FRMCONCEPTOVIATICOSCONTROLADORURL106", "15012"),

    URL107("FRMCONCEPTOVIATICOSCONTROLADORURL107", "20013"),

    URL108("FRMCONCEPTOVIATICOSCONTROLADORURL108", "23001"),

    URL109("FRMCONCEPTOVIATICOSCONTROLADORURL109", "34001"),

    URL110("FRMCONCEPTOVIATICOSCONTROLADORURL110", "13001"),

    URL111("FRMCONCEPTOVIATICOSCONTROLADORURL111", "771001"),

    URL112("FRMCONCEPTOVIATICOSCONTROLADORURL112", "29136");

    private final String key;
    private final String value;

    private FrmconceptoviaticosControladorUrlEnum(String key, String value) {
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
