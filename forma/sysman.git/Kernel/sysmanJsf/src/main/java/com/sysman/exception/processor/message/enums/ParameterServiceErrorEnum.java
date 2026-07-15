/*-
 * ParameterServiceErrorEnum.java
 *
 * 1.0
 * 
 * 1/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.exception.processor.message.enums;

import com.sysman.exception.processor.message.CrudMessageProcessorOra;

/**
 * Enumrado con valores de constantes para
 * {@code CrudMessageProcessorOra}
 * 
 * @see CrudMessageProcessorOra
 * 
 * @version 1.0, 1/08/2017
 * @author cmanrique
 *
 */
public enum ParameterServiceErrorEnum {

    CODERROR("CODERROR"),

    LOG_USUARIO("LOG_USUARIO"),

    CREATED_BY("CREATED_BY"),

    LOG_TABLA_ORIGEN("LOG_TABLA_ORIGEN"),

    MSG_CAPTURADO("MSG_CAPTURADO"),

    DESC_SQL("DESC_SQL"),

    ID_FORM_MENU("ID_FORM_MENU"),

    LOG_FECHA("LOG_FECHA"),

    DATE_CREATED("DATE_CREATED"),

    KEY_LOG_IDENT("KEY_LOG_IDENT"),

    MSG_INTERFAZ("MSG_INTERFAZ");

    private final String value;

    private ParameterServiceErrorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
