package com.sysman.contabilidad.enums;

public enum FrmComprobanteCntAfecAnticipoControladorUrlEnum {

	  URL001("FRMCOMPROBANTECNTAFECANTICIPOCONTROLADORURL", "72131"),
	  
	  URL002("FRMCOMPROBANTECNTAFECANTICIPOCONTROLADORURL", "72133");

    private final String key;
    private final String value;

    private FrmComprobanteCntAfecAnticipoControladorUrlEnum(String key, String value) {
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
