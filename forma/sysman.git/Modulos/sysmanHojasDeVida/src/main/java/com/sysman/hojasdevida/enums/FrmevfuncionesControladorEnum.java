/*-
 * FrmevfuncionesControladorEnum.java
 *
 * 1.0
 *
 * 16/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 */
public enum FrmevfuncionesControladorEnum {

    NUMERO_MANUAL("NUMERO_MANUAL"),

    VERSION("VERSION"),

    TIPO_FUNCION("TIPO_FUNCION"),

    EV_FUNCIONES("EV_FUNCIONES"),

    EV_MANUAL("EV_MANUAL"),

    NOMBRE_MANUAL("NOMBRE_MANUAL"),
    
	NOMBRE_CARGO("NOMBRE_DEL_CARGO"), 
	
	ID_DE_CARGO("ID_DE_CARGO");
	
    private final String value;

    private FrmevfuncionesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
