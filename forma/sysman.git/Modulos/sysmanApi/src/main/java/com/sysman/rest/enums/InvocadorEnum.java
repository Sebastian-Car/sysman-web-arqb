/*-
 * InvocadorEnum.java
 *
 * 1.0
 * 
 * 19/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 19/10/2018
 * @author jgomez
 *
 */
public enum InvocadorEnum {

    MSG_INVOCADOR_NOAUTORIZA("MSG_INVOCADOR_NOAUTORIZA"),

    MSG_INVOCADOR_TOKENINVALIDO("MSG_INVOCADOR_TOKENINVALIDO"),

    MSG_INVOCADOR_TOKENINVALIDO_COM("MSG_INVOCADOR_TOKENINVALIDO_COM"),

    MSG_INVOCADOR_TOKENINVALIDO_COM_CAUSA(
                    "MSG_INVOCADOR_TOKENINVALIDO_COM_CAUSA"),

    MSG_INVOCADOR_URL_ERRADA("MSG_INVOCADOR_URL_ERRADA"),

    ;

    private final String value;

    private InvocadorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
