package com.sysman.workflow.enums;

public enum FrmMonitortramitesadminsControladorUrlEnum {
    
    URL001("FRMMONITORTRAMITESADMINSCONTROLADORURL", "1042018"),
    
    URL002("FRMMONITORTRAMITESADMINSCONTROLADORURL", "1042020"),
    
    URL003("FRMMONITORTRAMITESADMINSCONTROLADORURL", "71047"),
    
    URL004("FRMMONITORTRAMITESCONTROLADORURL001", "1737003");

    private final String key;
    private final String value;

    private FrmMonitortramitesadminsControladorUrlEnum(String key, String value) {
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
