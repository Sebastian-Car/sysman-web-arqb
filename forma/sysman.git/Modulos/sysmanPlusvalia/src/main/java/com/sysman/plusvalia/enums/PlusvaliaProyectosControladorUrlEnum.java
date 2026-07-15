/*-
 * PlusvaliaProyectosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 31/01/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 31/01/2019
 * @author bcardenas
 *
 */
public enum PlusvaliaProyectosControladorUrlEnum {

    URL1032("PLUSVALIAPROYECTOSCONTROLADORURL1032", "1032005"),

    URL558("PLUSVALIAPROYECTOSCONTROLADORURL558", "558005"),

    URL1022("PLUSVALIAPROYECTOSCONTROLADORURL1022", "15007"),

    URL00C("PLUSVALIAPROYECTOSCONTROLADORURL00C", "1767005"),

    URL00G("PLUSVALIAPROYECTOSCONTROLADORURL00G", "1767001"),

    URL00U("PLUSVALIAPROYECTOSCONTROLADORURL00U", "176700U"),

    URL00R("PLUSVALIAPROYECTOSCONTROLADORURL00R", "176700R"),

    URL00D("PLUSVALIAPROYECTOSCONTROLADORURL00D", "176700D"),

    URL0001("PLUSVALIAPROYECTOSCONTROLADORURL0001", "4001"),
    
    URL0002("PLUSVALIAPROYECTOSCONTROLADORURL0002", "7007"),
    
    ;

    private final String key;
    private final String value;

    private PlusvaliaProyectosControladorUrlEnum(String key, String value) {
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
