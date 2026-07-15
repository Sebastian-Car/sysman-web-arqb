/*-
 * SubFrmApDeudaTotalsControladorUrlEnum.java
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * utilizados en el refactoring de sentencias SQL del controlador:
 * {@link com.sysman.facturaciongeneral.SubFrmApDeudaTotalsControlador}
 * 
 * @version 1.0, 17/11/2017
 * @author pespitia
 *
 */
public enum SubFrmApDeudaTotalsControladorUrlEnum {

    URL0001("SUBFRMAPDEUDATOTALSCONTROLADORURL0001", "669001"),

    URL0002("SUBFRMAPDEUDATOTALSCONTROLADORURL0002", "669003");

    private final String key;
    private final String value;

    private SubFrmApDeudaTotalsControladorUrlEnum(String key, String value) {
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
