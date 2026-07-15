/*-
 * AuxiliaresOblgFechaPagoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 30/01/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 30/01/2019
 * @author bcardenas
 *
 */
public enum AuxiliaresOblgFechaPagoControladorUrlEnum {

    URL5222("AUXILIARESOBLGFECHAPAGOCONTROLADORURL5222", "16008"),

    URL5971("AUXILIARESOBLGFECHAPAGOCONTROLADORURL5971", "16010"),

    URL4272("AUXILIARESOBLGFECHAPAGOCONTROLADORURL4272", "15003"),

    URL3488("AUXILIARESOBLGFECHAPAGOCONTROLADORURL3488", "15005"),

    URL7965("AUXILIARESOBLGFECHAPAGOCONTROLADORURL7965", "20019"),

    URL7434("AUXILIARESOBLGFECHAPAGOCONTROLADORURL7434", "20017"),

    URL6436("AUXILIARESOBLGFECHAPAGOCONTROLADORURL6436", "14001"),

    URL6904("AUXILIARESOBLGFECHAPAGOCONTROLADORURL6904", "14031");

    private final String key;
    private final String value;

    private AuxiliaresOblgFechaPagoControladorUrlEnum(String key,
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