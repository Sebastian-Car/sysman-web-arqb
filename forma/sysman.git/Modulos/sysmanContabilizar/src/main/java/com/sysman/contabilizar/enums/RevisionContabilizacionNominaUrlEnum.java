/*
 * RevisionContabilizacionNominaUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilizar.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RevisionContabilizacionNominaUrlEnum {

    URL5505("REVISIONCONTABILIZACIONNOMINAURL5505",
                    "59003"),
    
    URL5506("REVISIONCONTABILIZACIONNOMINAURL55056",
                    "537004"),
    
    URL6844("REVISIONCONTABILIZACIONNOMINAURL6844",
                    "471003"),

    URL6393("REVISIONCONTABILIZACIONNOMINAURL6393",
                    "4001"),

    URL5965("REVISIONCONTABILIZACIONNOMINAURL5965",
                    "7001");

    private final String key;
    private final String value;

    private RevisionContabilizacionNominaUrlEnum(String key, String value) {
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
