/*
 * FrmSeleccionarTramitesControladorUrlEnum
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
public enum FrmSeleccionarTramitesControladorUrlEnum {

    URL002("FRMSELECCIONARTRAMITESCONTROLADORURL002", "1048001"),

    URL005("FRMSELECCIONARTRAMITESCONTROLADORURL005", "1042008"),

    URL004("FRMSELECCIONARTRAMITESCONTROLADORURL004", "1042007"),

    URL001("FRMSELECCIONARTRAMITESCONTROLADORURL001", "1042005"),

    URL5085("FRMSELECCIONARTRAMITESCONTROLADORURL5085", "1042003"),

    URL003("FRMSELECCIONARTRAMITESCONTROLADORURL003", "1037004"),

    URL0001("FRMSELECCIONARTRAMITESCONTROLADORURL0001", "997001"),

    URL0002("FRMSELECCIONARTRAMITESCONTROLADORURL0002", "988005");

    private final String key;
    private final String value;

    private FrmSeleccionarTramitesControladorUrlEnum(String key, String value) {
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
