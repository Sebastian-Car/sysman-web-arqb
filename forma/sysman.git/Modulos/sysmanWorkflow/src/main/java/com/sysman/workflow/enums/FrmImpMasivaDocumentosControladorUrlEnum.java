package com.sysman.workflow.enums;

public enum FrmImpMasivaDocumentosControladorUrlEnum {
	
	 /**
     * Permite consultar el codigo y nombre de proceso persuasivo y coactivo
     */
    URL0001("FRMIMPMASIVADOCUMENTOSCONTROLADORURL988","988008"),
    
    URL0002("FRMIMPMASIVADOCUMENTOSCONTROLADORURL1035","1035008"),
    
    URL0003("FRMIMPMASIVADOCUMENTOSCONTROLADORURL1035","104072"),
    
    URL0004("FRMIMPMASIVADOCUMENTOSCONTROLADORURL1035","1042012"),
    
    URL0005("FRMIMPMASIVADOCUMENTOSCONTROLADORURL1035","1042013"),
    
	URL1035006("PCONTRATOSCONTROLADORURL1979", "104070"),
    
    ;
	
    private final String key;
    private final String value;

    private FrmImpMasivaDocumentosControladorUrlEnum(String key, String value) {
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
