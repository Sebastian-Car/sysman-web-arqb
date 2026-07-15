/**
 * Clase : GeneralAutoservicioEnum.java
 *
 * Descripción TODO
 */
package com.sysman.enums;

/**
 * @version1.0, 21/05/2018
 *
 * @author sdaza
 */
public enum GeneralAutoservicioEnum {
    VOLANTE_PAGO("11"),

    CERTIFICADO_RETENCIONES("14"),
    
    CATEGORIA("CATEGORIA"),

    ;

    private final String value;

    private GeneralAutoservicioEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
