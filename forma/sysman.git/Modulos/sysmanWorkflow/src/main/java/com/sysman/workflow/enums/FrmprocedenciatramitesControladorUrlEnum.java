/*-
 * FrmprocedenciatramitesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 17/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 17/04/2018
 * @author lbotia
 *
 */
public enum FrmprocedenciatramitesControladorUrlEnum {

    URL3348("FRMPROCEDENCIATRAMITECONTORLADORURL3348", "14001"),
    
    URL3349("FRMPROCEDENCIATRAMITECONTORLADORURL3348", "1040007");

    private final String key;
    private final String value;

    private FrmprocedenciatramitesControladorUrlEnum(String key, String value) {
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
