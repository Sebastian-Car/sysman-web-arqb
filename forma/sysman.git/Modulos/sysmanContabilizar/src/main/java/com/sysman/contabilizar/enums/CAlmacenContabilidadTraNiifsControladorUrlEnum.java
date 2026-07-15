/*-
 * CAlmacenContabilidadTraNiifsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 11/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 11/09/2018
 * @author lbotia
 *
 */
public enum CAlmacenContabilidadTraNiifsControladorUrlEnum {

    URL0001("CALMACENCONTABILIDADTRASCONTROLADORURL0001", "183017"),

    URL0002("CALMACENCONTABILIDADTRASCONTROLADORURL0002", "183014"),

    URL0003("CALMACENCONTABILIDADTRASCONTROLADORURL0003", "183015"),

    URL0004("CALMACENCONTABILIDADTRASCONTROLADORURL0004", "18300D"),

    URL0005("CALMACENCONTABILIDADTRASCONTROLADORURL0005", "139012"),

    URL0006("CALMACENCONTABILIDADTRASCONTROLADORURL0006", "16118"),

    URL0007("CALMACENCONTABILIDADTRASCONTROLADORURL0007", "16057")

    // URL0008("CALMACENCONTABILIDADTRASCONTROLADORURL0008", "16118")

    ;

    private final String key;
    private final String value;

    private CAlmacenContabilidadTraNiifsControladorUrlEnum(String key,
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