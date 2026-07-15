/*
 * FrmviasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmviasControladorUrlEnum {

    URL21167("FRMVIASCONTROLADORURL21167", "4001"),

    URL0004("FRMVIASCONTROLADORURL0004", "112027"),

    URL0005("FRMVIASCONTROLADORURL0005", "136018"),

    URL0008("FRMVIASCONTROLADORURL0008", "136019"),

    URL0007("FRMVIASCONTROLADORURL0007", "137018"),

    URL0006("FRMVIASCONTROLADORURL0006", "141041"),

    URL0009("FRMVIASCONTROLADORURL0009", "141042"),

    URL19805("FRMVIASCONTROLADORURL19805", "141043"),

    URL20665("FRMVIASCONTROLADORURL20665", "141045"),

    URL22409("FRMVIASCONTROLADORURL22409", "141048"),

    URL0001("FRMVIASCONTROLADORURL0001", "148001"),

    URL19388("FRMVIASCONTROLADORURL19388", "172001"),

    URL25884("FRMVIASCONTROLADORURL25884", "174002"),

    URL0002("FRMVIASCONTROLADORURL0001", "175001"),

    URL31286("FRMVIASCONTROLADORURL31286", "175002"),

    URL0003("FRMVIASCONTROLADORURL0003", "176001"),

    URL18996("FRMVIASCONTROLADORURL18996", "178001"),

    URL0010("FRMVIASCONTROLADORURL0010", "179001"),

    URL18628("FRMVIASCONTROLADORURL18628", "180001");

    private final String key;
    private final String value;

    private FrmviasControladorUrlEnum(String key, String value) {
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
