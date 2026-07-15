/*
 * FormularioPedirCicloCriticaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author pespitia
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FormularioPedirCicloCriticaControladorUrlEnum {

    URL0002("FORMULARIOPEDIRCICLOCRITICACONTROLADORURL0002", "213004"),

    URL0003("FORMULARIOPEDIRCICLOCRITICACONTROLADORURL0003", "213006"),

    URL0001("FORMULARIOPEDIRCICLOCRITICACONTROLADORURL0001", "214008");

    private final String key;
    private final String value;

    private FormularioPedirCicloCriticaControladorUrlEnum(String key,
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
