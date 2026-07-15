/*
 * FrmpersuasivosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmpersuasivosControladorUrlEnum {

    URL33363("FRMPERSUASIVOSCONTROLADORURL33363", "104026"),

    URL13248("FRMPERSUASIVOSCONTROLADORURL13248", "213112"),

    URL10443("FRMPERSUASIVOSCONTROLADORURL10443", "213112"),

    URL11262("FRMPERSUASIVOSCONTROLADORURL11262", "366008"),

    URL12076("FRMPERSUASIVOSCONTROLADORURL12076", "104019"),

    URL12674("FRMPERSUASIVOSCONTROLADORURL12674", "311004"),

    URL12675("FRMPERSUASIVOSCONTROLADORURL12675", "311005"),

    URL14078("FRMPERSUASIVOSCONTROLADORURL14078", "339003"),

    URL14079("FRMPERSUASIVOSCONTROLADORURL14079", "339001"),

    URL14080("FRMPERSUASIVOSCONTROLADORURL14080", "339003"),

    URL14081("FRMPERSUASIVOSCONTROLADORURL14081", "366009");

    private final String key;
    private final String value;

    private FrmpersuasivosControladorUrlEnum(String key, String value) {
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
