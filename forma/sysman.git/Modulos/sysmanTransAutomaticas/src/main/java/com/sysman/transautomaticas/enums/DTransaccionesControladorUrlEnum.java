/*-
 * DatosTransaccionControlador.java
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

public enum DTransaccionesControladorUrlEnum {

    URL001("TRANSACCIONMODELOSCONTROLADORURL001","1723001"), //validar comprobante y tipo_cpte
    
    URL002("TRANSACCIONMODELOSCONTROLADORURL002","1723003")//consultar comprobante
    ;
            
    private final String key;
    private final String value;
    
    private  DTransaccionesControladorUrlEnum(String key, String value) {
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
