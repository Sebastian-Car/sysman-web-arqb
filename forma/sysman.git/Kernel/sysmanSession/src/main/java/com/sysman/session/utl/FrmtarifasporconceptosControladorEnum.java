/*-
 * FrmtarifasporconceptosControladorEnum.java
 *
 * 1.0
 * 
 * 8/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.session.utl;

/**
 * Enumeracion que permite clasificar cada uno de los parametros de
 * sesion utilizados en el modulo de <code>FACTURACION GENERAL</code>.
 * 
 * @version 1.0, 8/11/2017
 * @author pespitia
 *
 */
public enum FrmtarifasporconceptosControladorEnum {

    ANIO("anio"),

    TIPOCOBRO("tipoCobro"),

    NOMBRETIPOCOBRO("nombreTipoCobro");

    private final String value;

    private FrmtarifasporconceptosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
