package com.sysman.contabilidad.enums;

public enum GenerarcptepptalCausacionControladorEnum {
	
	DETALLE_COMPROBANTE_PPTAL("DETALLE_COMPROBANTE_PPTAL"), 

    COMPANIA("COMPANIA"),
    
    ANO("ANO"),
 
    TERCERO("TERCERO"),
    
    KEY_COMPANIA("KEY_COMPANIA"),
    
    KEY_ANO("KEY_ANO"),
    
    SALDO("SALDO"), 
    
    VALOR_A_PAGAR("VALOR_A_PAGAR"), 
    
    COMPROBANTE("COMPROBANTE"),
    
    FECHAINI("FECHAINI");

    private final String value;

    private GenerarcptepptalCausacionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
