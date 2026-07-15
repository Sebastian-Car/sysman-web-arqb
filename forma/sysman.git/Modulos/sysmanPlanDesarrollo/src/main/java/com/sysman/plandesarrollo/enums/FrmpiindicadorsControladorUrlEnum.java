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

package com.sysman.plandesarrollo.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 27/02/2018
 * @author jhernandez
 *
 */
public enum FrmpiindicadorsControladorUrlEnum {

    URL0001("LISTUNIDADPROYECTOSURL0001", "553004"),

    URL0002("LISTAIDPLANCIAVIGMETAURL0002", "552033"),

    URL0003("LISTASECTORESCODIGODESCRIPURL0003", "203004"),

    URL0004("LISTASECTORESCODIGODESCRIPURL0004", "1831001")

    ;

    private final String key;
    private final String value;

    private FrmpiindicadorsControladorUrlEnum(String key,
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
