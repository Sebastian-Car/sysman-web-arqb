/*-
 * AccionesCContratoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 2/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.enums;

/**
 * 
 * @version 1.0, 2/08/2017
 * @author pespitia
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CumplimientoactividadesControladorUrlEnum {

    URL001("CUMPLIMIENTOACTIVIDADESCONTROLADORURL001", "1726001"),

    URL002("CUMPLIMIENTOACTIVIDADESCONTROLADORURL002", "1726002");

    private final String key;
    private final String value;

    private CumplimientoactividadesControladorUrlEnum(String key,
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
