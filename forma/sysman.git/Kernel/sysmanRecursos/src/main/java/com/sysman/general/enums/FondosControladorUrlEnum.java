/*
 * FondosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FondosControladorUrlEnum {

    URL14375("FONDOSCONTROLADORURL14375", "1001"),

    URL16911("FONDOSCONTROLADORURL16911", "5001"),

    URL91001("FONDOSCONTROLADORURL91001", "461006"),

    URL71121("FONDOSCONTROLADORURL71121", "2001"),

    URL16613("FONDOSCONTROLADORURL16613", "5001"),

    URL49751("FONDOSCONTROLADORURL49751", "36001"),

    URL16091("FONDOSCONTROLADORURL16091", "475001"),

    URL17546("FONDOSCONTROLADORURL17546", "14142"),

    URL63461("FONDOSCONTROLADORURL63461", "2005"),

    URL13371("FONDOSCONTROLADORURL13371", "461007"),

    URL14769("FONDOSCONTROLADORURL14769", "461008"),

    URL14770("FONDOSCONTROLADORURL14770", "61700G"),

    URL14771("FONDOSCONTROLADORURL14771", "61700C"),

    URL14773("FONDOSCONTROLADORURL14773", "61700D"),

    URL14772("FONDOSCONTROLADORURL14772", "61700R"),

    URL14774("FONDOSCONTROLADORURL14774", "61700U"),

    URL14775("FONDOSCONTROLADORURL14775", "617001"),

    URL15368("FONDOSCONTROLADORURL15368", "461009"),

    URL15376("FONDOSCONTROLADORURL15376", "45048"),

    URL15377("FONDOSCONTROLADORURL15377", "209008");

    private final String key;
    private final String value;

    private FondosControladorUrlEnum(String key, String value) {
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
