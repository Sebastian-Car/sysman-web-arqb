/*-
 * FrmInformedeProyeccionesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 18/08/2018
 * @author bcardenas
 *
 */
public enum FrmInformedeProyeccionesControladorUrlEnum {
    // tipocomprobante

    URL0001("FRMINFORMEDEPROYECCIONESCONTROLADORURL0001", "471002"),

    URL0002("FRMINFORMEDEPROYECCIONESCONTROLADORURL0002", "7024"),

    URL0003("FRMINFORMEDEPROYECCIONESCONTROLADORURL0003", "471003");

    private final String key;
    private final String value;

    private FrmInformedeProyeccionesControladorUrlEnum(String key,
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
