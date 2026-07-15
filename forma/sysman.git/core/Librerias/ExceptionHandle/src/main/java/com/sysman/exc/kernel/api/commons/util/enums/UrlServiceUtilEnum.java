package com.sysman.exc.kernel.api.commons.util.enums;

public enum UrlServiceUtilEnum {

    URLGET("getReg", "http://192.168.1.191:9763/services/recursos/codigo"), URLUPDATE(
                    "updtReg",
                    "application/json"), URLID("URLWSO2", "URLWSO2"), URLDELET(
                                    "deleteReg", "application/json");

    private final String key;
    private final String value;

    private UrlServiceUtilEnum(String key, String value) {
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
