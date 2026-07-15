/*
 * LibroRegistroVigFuturasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LibroRegistroVigFuturasControladorUrlEnum {

    URL5634("LIBROREGISTROVIGFUTURASCONTROLADORURL5634", "4001"),

    URL4590("LIBROREGISTROVIGFUTURASCONTROLADORURL4590", "7007"),

    URL5063("LIBROREGISTROVIGFUTURASCONTROLADORURL5063", "7018"),

    URL7967("LIBROREGISTROVIGFUTURASCONTROLADORURL7967", "20013"),

    URL8674("LIBROREGISTROVIGFUTURASCONTROLADORURL8674", "20015"),

    URL9424("LIBROREGISTROVIGFUTURASCONTROLADORURL9424", "23010"),

    URL10068("LIBROREGISTROVIGFUTURASCONTROLADORURL10068", "23019"),

    URL6002("LIBROREGISTROVIGFUTURASCONTROLADORURL6002", "45014"),

    URL6901("LIBROREGISTROVIGFUTURASCONTROLADORURL6901", "45016");

    private final String key;
    private final String value;

    private LibroRegistroVigFuturasControladorUrlEnum(String key,
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
