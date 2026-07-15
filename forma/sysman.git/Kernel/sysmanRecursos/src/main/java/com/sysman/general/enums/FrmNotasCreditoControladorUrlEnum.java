package com.sysman.general.enums;

public enum FrmNotasCreditoControladorUrlEnum {

	URL001("FRM_NOTAS_CREDITO_CONTROLADORURL", "1934001"),
	
	URL002("FRM_NOTAS_CREDITO_CONTROLADORURL", "82126"),
	
	URL003("FRM_NOTAS_CREDITO_CONTROLADORURL", "1934002");

    private final String key;
    private final String value;

    private FrmNotasCreditoControladorUrlEnum(String key, String value) {
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
