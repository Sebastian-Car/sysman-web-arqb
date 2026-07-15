/*-
 * ImprimirayudaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 8/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */	

package com.sysman.general.enums;

 /**
  * TODO Ingrese una descripcion para la clase.
  * 
  * @version 1.0, 8/06/2023
  * @author grojas
  *
  */
public enum ImprimirayudaControladorUrlEnum {
    
    URL1906("IMPRIMIRAYUDACONTROLADORURL1906003", "1906003"),
    
    URL1907("IMPRIMIRAYUDACONTROLADORURL1906005", "1906005")

    ;

    private final String key;
    private final String value;

    private ImprimirayudaControladorUrlEnum(String key, String value) {
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
