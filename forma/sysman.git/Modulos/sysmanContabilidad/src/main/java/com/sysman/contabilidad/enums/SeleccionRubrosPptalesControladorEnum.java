/*-
 * SeleccionRubrosPptalesControladorEnum.java
 *
 * 1.0
 * 
 * 25/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 25/05/2017
 * @author jrodrigueza
 *
 */
public enum SeleccionRubrosPptalesControladorEnum {
    /**
     * Par&aacute;metro RUBRO_PPTAL.
     */
    RUBRO_PPTAL("RUBRO_PPTAL"),
    /**
     * Par&aacute;metro CUENTAPPTAL
     */
    CUENTAPPTAL("CUENTAPPTAL");

    private final String value;

    private SeleccionRubrosPptalesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
