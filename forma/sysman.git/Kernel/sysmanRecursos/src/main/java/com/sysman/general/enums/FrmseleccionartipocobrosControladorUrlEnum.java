/*
 * AuxiliaresControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmseleccionartipocobrosControladorUrlEnum {

    URL3211("FRMSELECCIONARTIPOCOBROSCONTROLADORURL3211", "665001"),

    URL3566("FRMSELECCIONARTIPOCOBROSCONTROLADORURL3566",
                    "665006"), /*-Facturacion general */

    URL0001("FRMSELECCIONARTIPOCOBROSCONTROLADORURL0001",
                    "665008"); /*-Contabilidad */

    private final String key;
    private final String value;

    private FrmseleccionartipocobrosControladorUrlEnum(String key,
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
