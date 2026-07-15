/*-
 * FrmevcompetenciasempleadosControladorUrlEmun.java
 *
 * 1.0
 * 
 * 29/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumerado que me permite acceder a los servicios necesarios para el
 * funcionamiento de los combos del formulario
 * FRM_EV_COMPETENCIAS_EMPLEADO
 * 
 * @version 1.0, 29/01/2018
 * @author mvenegas
 *
 */
public enum FrmevcompetenciasempleadosControladorUrlEmun {

    URL100("FRMEVCOMPETENCIASEMPLEADOS100", "753010"),

    URL101("FRMEVCOMPETENCIASEMPLEADOS101", "773001"),

    URL102("FRMEVCOMPETENCIASEMPLEADOS102", "210099"),

    URL103("FRMEVCOMPETENCIASEMPLEADOS103", "62077"),

    URL104("FRMEVCOMPETENCIASEMPLEADOS104", "773003"),

    ;

    private final String key;
    private final String value;

    private FrmevcompetenciasempleadosControladorUrlEmun(String key,
        String value) {
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
