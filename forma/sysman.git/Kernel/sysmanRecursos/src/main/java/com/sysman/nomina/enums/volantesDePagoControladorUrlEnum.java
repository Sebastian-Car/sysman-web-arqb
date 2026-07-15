/*-
 * ActualizaparametrosretroactivosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 15/01/2018
 * @author vmolano
 *
 */
public enum volantesDePagoControladorUrlEnum {

    URL0001("LISTADOPROCESOSNOMINA", "537004"),

    URL0002("LISTADOANOS", "471002"),

    URL0003("LISTADOMESES", "471058"),

    URL0004("LISTADOPERIODOS", "471020"),

    URL0005("LISTADOEMPLEADOS", "210074"),

    URL0006("LISTADOCENTROSCOSTO", "20051"),
    
    URL0007("LISTADOCORREOS", "210123"),
    
    URL0008("LISTADOCORREOS", "1033001");

    private final String key;
    private final String value;

    private volantesDePagoControladorUrlEnum(String key,
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
