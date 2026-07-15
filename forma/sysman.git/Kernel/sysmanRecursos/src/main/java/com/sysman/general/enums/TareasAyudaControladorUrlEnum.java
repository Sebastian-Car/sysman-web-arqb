/*-
 * TareasAyudaControladorUrlEnum.java
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
public enum TareasAyudaControladorUrlEnum {
    
URL1908("PROCESOSAYUDACONTROLADORURL1908001", "1908001"),
    
    URL1909("PROCESOSAYUDACONTROLADORURL190800C", "190800C"),

    URL1910("PROCESOSAYUDACONTROLADORURL190800D", "190800D"),
    
    URL1911("PROCESOSAYUDACONTROLADORURL190800G", "190800G"),
    
    URL1912("PROCESOSAYUDACONTROLADORURL190800U", "190800U"),
    
    URL1913("PROCESOSAYUDACONTROLADORURL190800U", "1908005")
    ;

    private final String key;
    private final String value;

    private TareasAyudaControladorUrlEnum(String key, String value) {
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
