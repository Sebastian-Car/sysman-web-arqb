/*-
 * SeleccionRubrosPptalesControladorEnum.java
 *
 * 1.0
 * 
 * 23/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 23/05/2017
 * @author jrodrigueza
 *
 */
public enum SeleccionRubrosPptalesControladorUrlEnum {
    /**
     * 50001 getPlanespptalcuentacntPagCuentasPptalQuery
     */
    URL1879("SELECCIONRUBROSPPTALESCONTROLADOR1879", "50001"),
    /**
     * 39070 updateDetallescomprobantecntEquivalenciaPptal
     */
    URL33613("SELECCIONRUBROSPPTALESCONTROLADOR33613", "39070");

    private final String key;
    private final String value;

    private SeleccionRubrosPptalesControladorUrlEnum(String key, String value) {
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
