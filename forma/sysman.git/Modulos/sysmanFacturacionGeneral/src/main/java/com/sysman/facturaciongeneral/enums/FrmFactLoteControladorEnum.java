package com.sysman.facturaciongeneral.enums;

public enum FrmFactLoteControladorEnum {
	
	BASEGRAVAVLEDETALLE("\"baseGravable\":0.0,"),
	
	IMPUESTOIVA("\"porcentajeIva\":0.0,"),
	
	VALORIMPUESTOIVA("\"valorIva\":0.0,"),
	
	IMPUESTOICA("\"porcentajeIca\":0.0,"),
	
	VALORIMPUESTOICA("\"valorIca\":0.0,"),
	
    IMPUESTOIMPOCONSUMO("\"porcentajeImpConsumo\":0.0,"),
	
	VALORIMPUESTOIMPOCONSUMO("\"valorImpConsumo\":0.0,"),
	
    // parametros para el consumo de ws de invoway
    URL_SERVICIO_SOAP( "URL SERVICIO SOAP"),
    MANEJA_FACTURACION_ELECTRONICA_EXTERNA("MANEJA FACTURACION ELECTRONICA EXTERNA"),
    USUARIO_FACT_ELECTRONICA_EXTERNA("USUARIO FACT ELECTRONICA EXTERNA"),
    CLAVE_FACT_ELECTRONICA_EXTERNA("CLAVE FACT ELECTRONICA EXTERNA");

	private final String value;

    private FrmFactLoteControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
