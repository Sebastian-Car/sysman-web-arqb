package com.sysman.contabilidad.enums;

public enum FrmdocsoploteControladorEnum {
    BASEGRAVAVLEDETALLE("\"baseGravable\":0.0,"),
    
    IMPUESTOIVA("\"porcentajeIva\":0.0,"),
    
    VALORIMPUESTOIVA("\"valorIva\":0.0,"),
    
    IMPUESTOICA("\"porcentajeIca\":0.0,"),
    
    VALORIMPUESTOICA("\"valorIca\":0.0,"),
    
    IMPUESTOIMPOCONSUMO("\"porcentajeImpConsumo\":0.0,"),
    
    VALORIMPUESTOIMPOCONSUMO("\"valorImpConsumo\":0.0,");
    
    private final String value;

    private FrmdocsoploteControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
