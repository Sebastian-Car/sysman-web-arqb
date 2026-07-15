/*-
 * FrmcodigosfutsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 14/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 14/09/2017
 * @author pespitia
 *
 */
public enum FrmcodigosfutsControladorUrlEnum {

    URL0001("FRMCODIGOSFUTSCONTROLADORURL0001", "4001");

    private final String key;
    private final String value;

    private FrmcodigosfutsControladorUrlEnum(String key, String value) {
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
