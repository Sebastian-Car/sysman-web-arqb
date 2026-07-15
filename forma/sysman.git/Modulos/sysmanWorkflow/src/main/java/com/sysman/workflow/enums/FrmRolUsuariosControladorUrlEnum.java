/*-
 * FrmRolUsuariosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 23/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 23/04/2018
 * @author dnino
 *
 */
public enum FrmRolUsuariosControladorUrlEnum {
    // Dependencia
    URL255("FRMROLESUSUARIOSCONTROLADOR255", "62007"),

    // Usuario
    URL350("FRMROLESUSUARIOSCONTROLADOR", "47019"),
    // usuario_dependencia
    URL351("FRMROLESUSUARIOSCONTROLADOR", "52004"),

    URL352("FRMROLESUSUARIOSCONTROLADOR", "62089");

    private final String key;
    private final String value;

    private FrmRolUsuariosControladorUrlEnum(String key, String value) {
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
