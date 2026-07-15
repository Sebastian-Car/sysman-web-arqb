/*-
 * FrmseriedocumentalsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 11/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 11/04/2018
 * @author lbotia
 *
 */
public enum FrmseriedocumentalsControladorUrlEnum {

    URL3347("FRMSERIEDOCUMENTALSCONTORLADORURL3347", "1036001"),

    URL3348("FRMSERIEDOCUMENTALSCONTORLADORURL3348", "1032003"),
	
	URL3349("FRMSERIEDOCUMENTALSCONTORLADORURL3349", "4001");

    private final String key;
    private final String value;

    private FrmseriedocumentalsControladorUrlEnum(String key, String value) {
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
