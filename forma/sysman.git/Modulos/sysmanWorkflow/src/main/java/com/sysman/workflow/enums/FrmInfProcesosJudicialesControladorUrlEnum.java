package com.sysman.workflow.enums;

public enum FrmInfProcesosJudicialesControladorUrlEnum {

	  URL001("FRMTRAMITESCONTROLADORURL01847","988008"),
	  
	  URL002("FRMTRAMITESCONTROLADORURL01847","1847002");
		
	    private final String key;
	    private final String value;

	    private FrmInfProcesosJudicialesControladorUrlEnum(String key, String value) {
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
