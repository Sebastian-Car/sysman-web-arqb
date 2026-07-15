/*
 * FrmcomponentesactividadesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmcomponentesactividadesControladorUrlEnum {

    URL10583("FRMCOMPONENTESACTIVIDADESCONTROLADORURL10583", "558001"),

    URL11376("FRMCOMPONENTESACTIVIDADESCONTROLADORURL11376", "206005"),

    URL16291("FRMCOMPONENTESACTIVIDADESCONTROLADORURL16291", "32024"),

    URL40381("FRMCOMPONENTESACTIVIDADESCONTROLADORURL40381", "571001"),

    URL12234("FRMCOMPONENTESACTIVIDADESCONTROLADORURL12234", "558003"),

    URL418("FRMCOMPONENTESACTIVIDADESCONTROLADORURL418", "513003"),

    URL738("FRMCOMPONENTESACTIVIDADESCONTROLADORURL738", "558004"),

    URL751("FRMCOMPONENTESACTIVIDADESCONTROLADORURL738", "513004"),

    URL32744("FRMCOMPONENTESACTIVIDADESCONTROLADORURL32744", "513005");

    private final String key;
    private final String value;

    private FrmcomponentesactividadesControladorUrlEnum(String key,
        String value)
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
