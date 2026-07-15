/*
 * ConfigurarPlanPptalChipsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConfigurarPlanPptalChipsControladorUrlEnum {

    URL15296("CONFIGURARPLANPPTALCHIPSCONTROLADORURL15296",
                    "34038"),

    URL19540("CONFIGURARPLANPPTALCHIPSCONTROLADORURL19540",
                    "1693001"),

    URL20639("CONFIGURARPLANPPTALCHIPSCONTROLADORURL20639",
                    "1693001"),

    URL21771("CONFIGURARPLANPPTALCHIPSCONTROLADORURL21771",
                    "1694001"),

    URL16738("CONFIGURARPLANPPTALCHIPSCONTROLADORURL16738",
                    "1691001"),

    URL12901("CONFIGURARPLANPPTALCHIPSCONTROLADORURL12901",
                    "1690001"),

    URL18489("CONFIGURARPLANPPTALCHIPSCONTROLADORURL18489",
                    "1031031"),

    URL13612("CONFIGURARPLANPPTALCHIPSCONTROLADORURL13612",
                    "1690003"),

    URL17870("CONFIGURARPLANPPTALCHIPSCONTROLADORURL17870",
                    "1692003"),

    URL22879("CONFIGURARPLANPPTALCHIPSCONTROLADORURL22879",
                    "1694001"),

    URL18636("CONFIGURARPLANPPTALCHIPSCONTROLADORURL18636",
                    "4001"),

    URL14513("CONFIGURARPLANPPTALCHIPSCONTROLADORURL14513",
                    "34038"),

    URL20888("CONFIGURARPLANPPTALCHIPSCONTROLADORURL20888",
                    "1031010"),

    URL9938("CONFIGURARPLANPPTALCHIPSCONTROLADORURL9938",
                    "1690005"),

    URL9940("CONFIGURARPLANPPTALCHIPSCONTROLADORURL99140",
                    "1692001");

    private final String key;
    private final String value;

    private ConfigurarPlanPptalChipsControladorUrlEnum(String key,
        String value) {
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
