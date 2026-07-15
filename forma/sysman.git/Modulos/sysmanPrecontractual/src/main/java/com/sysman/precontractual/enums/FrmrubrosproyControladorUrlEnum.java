/*
 * FrmrubrosproyControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmrubrosproyControladorUrlEnum {

    URL9609("FRMRUBROSPROYCONTROLADORURL9609", "474001"),

    URL9625("FRMRUBROSPROYCONTROLADORURL9625", "605001"),

    URL6650("FRMRUBROSPROYCONTROLADORURL6650", "34024"),

    URL7170("FRMRUBROSPROYCONTROLADORURL7170", "94107"),

    URL6374("FRMRUBROSPROYCONTROLADORURL6374", "4001"),

    URL6331("FRMRUBROSPROYCONTROLADORURL6331", "509004");
    private final String key;
    private final String value;

    private FrmrubrosproyControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
