package com.sysman.workflow.enums;

public enum CorrespondenciaRecibidaControladorUrlEnum {

    URL001("CORRESPONDENCIARECIBIDACONTROLADORURL001","1042009"),
    
    URL002("CORRESPONDENCIARECIBIDACONTROLADORURL002","1042010"),
    
    URL003("CORRESPONDENCIARECIBIDACONTROLADORURL003","1032005")
    ;
    private final String key;
    private final String value;

    private CorrespondenciaRecibidaControladorUrlEnum(String key, String value) {
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
