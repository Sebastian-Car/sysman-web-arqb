/*-
 * CargarPresupuestoVigenciaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 22/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 22/11/2018
 * @author bcardenas
 *
 */
public enum CargarPresupuestoVigenciaControladorUrlEnum {
    URL0001("CARGARPRESUPUESTOVIGENCIACONTROLADORURLENUMURL0001", "4001"); // trae
                                                                           // el
                                                                           // año

    private final String key;
    private final String value;

    private CargarPresupuestoVigenciaControladorUrlEnum(String key,
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
