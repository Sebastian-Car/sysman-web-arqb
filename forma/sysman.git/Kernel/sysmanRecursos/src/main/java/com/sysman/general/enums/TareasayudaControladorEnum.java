/*-
 * TareasayudaControladorEnum.java
 *
 * 1.0
 * 
 * 13/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */	

package com.sysman.general.enums;

 /**
  * TODO Ingrese una descripcion para la clase.
  * 
  * @version 1.0, 13/06/2023
  * @author grojas
  *
  */
public enum TareasayudaControladorEnum {
    
    PARAM2("PARAM2"),

    PARAM1("PARAM1"),

    PARAM4("PARAM4"),

    PARAM3("PARAM3"),

    PARAM0("ID_PROCESO");

    private final String value;

    private TareasayudaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
