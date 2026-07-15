package com.sysman.contabilidad.enums;

public enum FrmrecursoactividadsControladorUrlEnum {
	
	URL1941001("FRMRECURSOACTIVIDADSCONTROLADORURL1941001","1941001"),
	
	URL1941003("FRMRECURSOACTIVIDADSCONTROLADORURL1941003","1941003")
	;

    private final String key;
    private final String value;

    private FrmrecursoactividadsControladorUrlEnum(String key, String value) {
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
