/*
 * RelacionPagoDescuentosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RelacionPagoDescuentosControladorUrlEnum {

    URL4391("RELACIONPAGODESCUENTOSCONTROLADORURL4391", "4001"),

    URL4748("RELACIONPAGODESCUENTOSCONTROLADORURL4748", "14036"),

    URL3960("RELACIONPAGODESCUENTOSCONTROLADORURL3960", "7013"),

    URL5344("RELACIONPAGODESCUENTOSCONTROLADORURL5344", "14038");

    private final String key;
    private final String value;

    private RelacionPagoDescuentosControladorUrlEnum(String key, String value) {
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
