/*
 * UsuariosControladorUrlEnum
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
public enum UsuariosControladorUrlEnum {

    URL17601("USUARIOSCONTROLADORURL17601", "248003"),

    URL17602("USUARIOSCONTROLADORURL17602", "289018"),

    URL14820("USUARIOSCONTROLADORURL14820", "310005"),

    URL13755("USUARIOSCONTROLADORURL13755", "242005"),

    URL14237("USUARIOSCONTROLADORURL14237", "310005"),

    URL19629("USUARIOSCONTROLADORURL19629", "319001"),

    URL13373("USUARIOSCONTROLADORURL13373", "107014"),

    URL18121("USUARIOSCONTROLADORURL18121", "338001"),

    URL12606("USUARIOSCONTROLADORURL12606", "209001"),

    URL18565("USUARIOSCONTROLADORURL18565", "269002"),

    URL15434("USUARIOSCONTROLADORURL15434", "310005"),

    URL15435("USUARIOSCONTROLADORURL15435", "213203"),

    URL15436("USUARIOSCONTROLADORURL15436", "213204");

    private final String key;
    private final String value;

    private UsuariosControladorUrlEnum(String key, String value) {
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
