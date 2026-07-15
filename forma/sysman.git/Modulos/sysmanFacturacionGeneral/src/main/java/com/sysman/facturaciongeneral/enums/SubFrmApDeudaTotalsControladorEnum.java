/*-
 * SubFrmApDeudaTotalsControladorEnum.java
 *
 * 1.0
 * 
 * 17/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * utilizados en el refactoring de sentencias SQL del controlador:
 * {@link com.sysman.facturaciongeneral.SubFrmApDeudaTotalsControlador}
 * 
 * @version 1.0, 17/11/2017
 * @author pespitia
 *
 */
public enum SubFrmApDeudaTotalsControladorEnum {

    SELECCIONADOS("SELECCIONADOS"),

    TIPOCOBRO("TIPOCOBRO");

    private final String value;

    private SubFrmApDeudaTotalsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
