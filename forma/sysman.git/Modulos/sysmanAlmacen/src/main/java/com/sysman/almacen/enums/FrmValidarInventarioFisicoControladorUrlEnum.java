/*-
 * FrmValidarInventarioFisicoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 13/06/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 13/06/2019
 * @author bcardenas
 *
 */
public enum FrmValidarInventarioFisicoControladorUrlEnum {

    URL1795("FRMVALIDARINVENTARIOFISICOCONTROLADORURL", "62002"),

    URL1796("FRMVALIDARINVENTARIOFISICOCONTROLADORURL", "71006"),

    URL1800("FRMVALIDARINVENTARIOFISICOCONTROLADORURL", "1800001");

    private final String key;
    private final String value;

    private FrmValidarInventarioFisicoControladorUrlEnum(String key,
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
