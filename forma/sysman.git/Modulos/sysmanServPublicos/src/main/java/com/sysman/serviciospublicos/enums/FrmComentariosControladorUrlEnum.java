/*-
 * FrmComentariosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 30/05/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmComentariosControladorUrlEnum {

    URL001("FRMCOMENTARIOSCONTROLADORURL001", "213095");

    private final String key;
    private final String value;

    private FrmComentariosControladorUrlEnum(String key,
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
