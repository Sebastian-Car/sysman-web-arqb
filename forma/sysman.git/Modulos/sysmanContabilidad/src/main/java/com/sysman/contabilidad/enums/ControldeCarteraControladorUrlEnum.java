/*-
 * ControldeCarteraControladorUrlEnum.java
 *
 * 1.0
 * 
 * 15/04/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 15/04/2019
 * @author bcardenas
 *
 */
public enum ControldeCarteraControladorUrlEnum {

    URL001("CONTROLDECARTERACONTROLADORURL001", "16008"), // PLAN_CONTABLE

    URL002("CONTROLDECARTERACONTROLADORURL002", "58001"), // APLICACIONES

    URL003("CONTROLDECARTERACONTROLADORURL003", "15007"), // TIPO_COMPROBANTE
	
	URL004("CONTROLDECARTERACONTROLADORURL003", "4002");

    private final String key;
    private final String value;

    private ControldeCarteraControladorUrlEnum(String key, String value) {
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
