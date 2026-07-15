/*-
 * BancosControladorTUrlEnum.java
 *
 * 1.0
 * 
 * 19/07/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 19/07/2019
 * @author bcardenas
 *
 */
public enum BancosControladorTUrlEnum {

	URL0001("BANCOSCONTROLADORTURL0001", "1805001"),
	
	URL16229("BANCOSCONTROLADORTURL16229", "16229"),
    
    URL4001("BANCOSCONTROLADORTURL4001", "4001");

    private final String key;
    private final String value;

    private BancosControladorTUrlEnum(String key, String value) {
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
