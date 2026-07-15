package com.sysman.contabilizar.enums;

public enum CAlmacenContabilidadBajaNiifControladorEnum {
	
	TIPO("TIPO");

    private final String value;

    private CAlmacenContabilidadBajaNiifControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
