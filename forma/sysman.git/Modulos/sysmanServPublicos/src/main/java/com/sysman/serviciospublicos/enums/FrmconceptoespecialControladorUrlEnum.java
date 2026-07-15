/*
 * FrmconceptoespecialControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmconceptoespecialControladorUrlEnum {

    URL9720("FRMCONCEPTOESPECIALCONTROLADORURL9720",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, tabla,"),

    URL3466("FRMCONCEPTOESPECIALCONTROLADORURL3466", "213083"),

    URL6356("FRMCONCEPTOESPECIALCONTROLADORURL6356", "242006"),

    URL4914("FRMCONCEPTOESPECIALCONTROLADORURL4914", "214047"),

    URL6978("FRMCONCEPTOESPECIALCONTROLADORURL6978", "310006"),

    URL4207("FRMCONCEPTOESPECIALCONTROLADORURL4207", "213085"),

    URL5718("FRMCONCEPTOESPECIALCONTROLADORURL5718", "215017");

    private final String key;
    private final String value;

    private FrmconceptoespecialControladorUrlEnum(String key, String value) {
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
