package com.sysman.contabilidad.enums;

public enum FrmConfPlanCntFlujoCgnControladorUrlEnum {
	
	 URL0001("FRMCONFPLANCNTFLUJOCGNCONTROLADORURL001", "1840001") ,
	    
	 URL0002("FRMCONFPLANCNTFLUJOCGNCONTROLADORURL002", "16200"),
	 
	 URL0003("FRMCONFPLANCNTFLUJOCGNCONTROLADORURL002", "16202")
	    ;

	    private final String key;
	    private final String value;

	    private FrmConfPlanCntFlujoCgnControladorUrlEnum(String key, String value) {
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