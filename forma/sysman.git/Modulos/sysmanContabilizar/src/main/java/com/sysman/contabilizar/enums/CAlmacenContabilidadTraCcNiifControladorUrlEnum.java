/*-
 * CAlmacenContabilidadTraCcNiifControladorUrlEnum.java
 *
 * 1.0
 * 
 * 12/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 12/09/2018
 * @author lbotia
 *
 */
public enum CAlmacenContabilidadTraCcNiifControladorUrlEnum {

    URL0001("CALMACENCONTABILIDADTRASCONTROLADORURL0001", "139012"),

    URL0002("CALMACENCONTABILIDADTRASCONTROLADORURL0002", "745003"),

    URL0003("CALMACENCONTABILIDADTRASCONTROLADORURL0003", "20020"),

    URL0004("CALMACENCONTABILIDADTRASCONTROLADORURL0004", "16118"),

    URL0005("CALMACENCONTABILIDADTRASCONTROLADORURL0005", "20040"),
    
    URL0006("CALMACENCONTABILIDADTRASCONTROLADORURL0006", "745005")
    
    ;

    private final String key;
    private final String value;

    private CAlmacenContabilidadTraCcNiifControladorUrlEnum(String key,
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