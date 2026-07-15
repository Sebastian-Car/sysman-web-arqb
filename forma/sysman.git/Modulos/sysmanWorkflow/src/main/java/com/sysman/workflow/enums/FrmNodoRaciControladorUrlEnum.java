/*-
 * FrmNodosRaciControladorUrlEnum.java
 *
 * 1.0
 * 
 * 20/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 20/04/2018
 * @author lbotia
 *
 */
public enum FrmNodoRaciControladorUrlEnum {

    URL3348("FRMNODORACICONTORLADORURL3348", "777001"),
    
    
    URL3349("FRMNODORACICONTORLADORURL3349", "1032003");

    private final String key;
    private final String value;

    private FrmNodoRaciControladorUrlEnum(String key, String value) {
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
