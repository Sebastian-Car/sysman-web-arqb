/*-
 * ProcesosayudaControladorUrlEnum.java
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
public enum ProcesosayudaControladorUrlEnum {
    
    URL1906("PROCESOSAYUDACONTROLADORURL1906001", "1906001")
    ;

    private final String key;
    private final String value;

    private ProcesosayudaControladorUrlEnum(String key, String value) {
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
