/**
 * 
 */
package com.sysman.circularunica.enums;

/**
 * @author User
 *
 */
public enum GenerarProcesosControladorEnum {


    NOMBRE_ARCHIVO("NOMBRE_ARCHIVO"),
    
    COLUMNAS("COLUMNAS"),
    
    SEPARADOR("SEPARADOR");

    private final String value;

    private GenerarProcesosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
