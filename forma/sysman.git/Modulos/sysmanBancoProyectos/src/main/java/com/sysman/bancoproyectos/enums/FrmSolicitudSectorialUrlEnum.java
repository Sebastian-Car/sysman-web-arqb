package com.sysman.bancoproyectos.enums;

public enum FrmSolicitudSectorialUrlEnum {
	
	URL32003("LISTADOPORCODIGOYOBJETO","32003"),
	URL218001("LISTADOTIPOTYNOMBRE218", "218001")
	;
    private final String key;
    private final String value;

    private FrmSolicitudSectorialUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

}
