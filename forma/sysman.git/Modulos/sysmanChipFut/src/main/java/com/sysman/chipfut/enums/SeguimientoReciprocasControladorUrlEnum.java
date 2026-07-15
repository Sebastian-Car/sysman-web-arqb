/*-
 * SeguimientoReciprocasControladorUrlEnum.java
 *
 * 1.0
 * 
 * 17/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.chipfut.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 17/07/2018
 * @author lbotia
 *
 */
public enum SeguimientoReciprocasControladorUrlEnum {
    // Lista Año
	
	   URL16164("SEGUIMIENTORECIPROCASCONTROLADORUR16164", "16164"),//código Plan Contrable
    URL1742001("SEGUIMIENTORECIPROCASCONTROLADORURL1742001", "4001"),//vigencia
	URL4001("SEGUIMIENTORECIPROCASCONTROLADORURL4001","4001"), //Ano
	URL14178("SEGUIMIENTORECIPROCASCONTROLADORURL14178","14178"),
	URL7044("SEGUIMIENTORECIPROCASCONTROLADORURL7044","7044"); //Mes
 
	

    private final String key;
    private final String value;

    private SeguimientoReciprocasControladorUrlEnum(String key, String value) {
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
