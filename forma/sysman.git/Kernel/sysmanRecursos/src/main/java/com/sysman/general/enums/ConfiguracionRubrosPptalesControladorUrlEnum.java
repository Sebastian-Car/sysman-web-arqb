/*
 * EntidadesCapacitacionControladorUrlEnum
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
public enum ConfiguracionRubrosPptalesControladorUrlEnum {

    URL001("CONFIGURACIONRUBROSPPTALESCONTROLADORURLENUM001", "1031003"),

    URL002("CONFIGURACIONRUBROSPPTALESCONTROLADORURLENUM002", "1031005"),

    URL003("CONFIGURACIONRUBROSPPTALESCONTROLADORURLENUM003", "1031009"),

    URL004("CONFIGURACIONRUBROSPPTALESCONTROLADORURLENUM004", "1031019"),

    URL005("CONFIGURACIONRUBROSPPTALESCONTROLADORURLENUM005", "1031021"),

    URL006("CONFIGURACIONRUBROSPPTALESCONTROLADORURLENUM006", "1684002"),

    URL007("CONFIGURACIONRUBROSPPTALESCONTROLADORURLENUM007", "1700004");

    private final String key;
    private final String value;

    private ConfiguracionRubrosPptalesControladorUrlEnum(String key,
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
