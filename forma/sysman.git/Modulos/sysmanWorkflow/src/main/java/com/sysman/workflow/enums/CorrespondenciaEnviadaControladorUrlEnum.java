package com.sysman.workflow.enums;

public enum CorrespondenciaEnviadaControladorUrlEnum {
	  
    URL001("FRMTRAMITESCONTROLADORURL10093", "1829001"),
    ;

    private final String key;
    private final String value;

    private CorrespondenciaEnviadaControladorUrlEnum(String key, String value) {
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
