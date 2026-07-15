package com.sysman.enums;

public enum GeneralParametrosEnum {
	
	MANEJA_PROCESO_AUDITORIA("MANEJA PROCESO DE AUDITORIA"),
	
	URL_REGISTRO_AUDITORIA("URL REGISTRO AUDITORIA"),
	
	URL_REPORTE_AUDITORIA("URL REPORTE AUDITORIA")
	
	;
	private final String name;

    private GeneralParametrosEnum(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}
