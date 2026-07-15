/*-
 * FrmconceptoviaticosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Este enumerado me relaciona los servicios necesarios para el
 * controlador PlaneacionDeActividadesControladorUrlEnum
 * 
 * @version 1.0, 18/01/2018
 * @author mvenegas
 *
 */
public enum PlaneacionDeActividadesControladorUrlEnum {

    URL100("FRMCONCEPTOVIATICOSCONTROLADORURL100", "471060"),

    URL101("FRMCONCEPTOVIATICOSCONTROLADORURL101", "954001"),

    URL102("FRMCONCEPTOVIATICOSCONTROLADORURL102", "954007")

    ;

    private final String key;
    private final String value;

    private PlaneacionDeActividadesControladorUrlEnum(String key,
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
