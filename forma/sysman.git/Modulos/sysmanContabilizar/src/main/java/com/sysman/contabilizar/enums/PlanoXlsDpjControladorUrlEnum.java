package com.sysman.contabilizar.enums;

public enum PlanoXlsDpjControladorUrlEnum {
	
	URL0001("FRMPLANOXLSCONTROLADORURLENUM","15007"),
	
	URL0002("FRMPLANOXLSCONTROLADORURLENUM","23015"),
	
	;

	  private final String key;
    private final String value;

    private PlanoXlsDpjControladorUrlEnum(String key, String value) {
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
