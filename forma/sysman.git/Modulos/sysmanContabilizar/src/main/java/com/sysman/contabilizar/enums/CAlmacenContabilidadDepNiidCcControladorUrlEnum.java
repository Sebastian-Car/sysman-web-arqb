/*-
 * CAlmacenContabilidadDepNiidCcControladorUrlEnum.java
 *
 * 1.0
 * 
 * 2/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 2/10/2018
 * @author lbotia
 *
 */
public enum CAlmacenContabilidadDepNiidCcControladorUrlEnum {

    URL0001("CALMACENCONTABILIDADTRASCONTROLADORURL0001", "1730001"),

    URL0002("CALMACENCONTABILIDADTRASCONTROLADORURL0002", "171004"),

    URL0003("CALMACENCONTABILIDADTRASCONTROLADORURL0003", "16118"),

    URL0004("CALMACENCONTABILIDADTRASCONTROLADORURL0004", "20040"),
    
    URL0005("CALMACENCONTABILIDADTRASCONTROLADORURL0005", "135007")

    ;

    private final String key;
    private final String value;

    private CAlmacenContabilidadDepNiidCcControladorUrlEnum(String key,
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
