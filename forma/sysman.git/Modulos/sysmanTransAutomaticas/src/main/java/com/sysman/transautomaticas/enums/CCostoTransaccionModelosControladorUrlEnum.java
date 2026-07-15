/*-
 * TransaccionModelosControlador.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */     

package com.sysman.transautomaticas.enums;

 /**
  * TODO Ingrese una descripcion para la clase.
  * 
  * @version 1.0, 18/09/2018
  * @author asana
  *
  */
public enum CCostoTransaccionModelosControladorUrlEnum {

    
    URL1812("CCOSTOTRANSACCIONMODELOSCONTROLADORURL1812","20003"), // Centro Costo
    
    ; //
            
    private final String key;
    private final String value;
    
    private  CCostoTransaccionModelosControladorUrlEnum(String key, String value) {
        this.key   = key; 
            this.value = value;
    }
    
    public String getKey() {
            return key;
    }
    
    public String getValue() {
            return value;
    }
}
