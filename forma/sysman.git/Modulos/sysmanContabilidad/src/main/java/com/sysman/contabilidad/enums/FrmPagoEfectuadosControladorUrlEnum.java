/*-
 * FrmPagoEfectuadosControladoUrlEnum.java
 *
 * 1.0
 * 
 * 5/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 5/07/2018
 * @author lbotia
 *
 */
public enum FrmPagoEfectuadosControladorUrlEnum {

    // Tercero Inicial
    URL0001("FRMPAGOEFECTUADOSCONTROLADORURL0001", "14036"),
    // Tercero Final
    URL0002("FRMPAGOEFECTUADOSCONTROLADORURL0002", "14038"),
    // Tipo Inicial
    URL0003("FRMPAGOEFECTUADOSCONTROLADORURL0003", "15063")

    ;

    private final String key;
    private final String value;

    private FrmPagoEfectuadosControladorUrlEnum(String key, String value) {
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