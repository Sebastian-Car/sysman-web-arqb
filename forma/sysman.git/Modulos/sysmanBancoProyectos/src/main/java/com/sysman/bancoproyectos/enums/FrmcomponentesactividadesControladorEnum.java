/*
 * FrmcomponentesactividadesControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmcomponentesactividadesControladorEnum {

    RIDCOMPONENTE("ridComponente"),

    KEY_CODIGO("KEY_CODIGO"),

    VALOR_SOLICITADO_ACTIVIDAD("VALOR_SOLICITADO_ACTIVIDAD"),

    CANTIDAD_EJE("CANTIDAD_EJE"),

    VALOR_DISMINUIDO("VALOR_DISMINUIDO"),

    PORCEJECUTADO("PORCEJECUTADO"),

    VALORPROGRAMADO("VALORPROGRAMADO"),

    VALOREJECUTADO("VALOREJECUTADO"),

    COSTOTOTAL("COSTOTOTAL"),

    COSTOUNITARIO("COSTOUNITARIO"),

    TOTALCOMPONENTE("TOTALCOMPONENTE"),

    CANTIDADCOMPONENTE("CANTIDADCOMPONENTE"),

    UNITARIOCOMPONENTE("UNITARIOCOMPONENTE"),

    NOMBREACTIVIDAD("NOMBREACTIVIDAD"),

    VIGENCIACOMPONENTE("VIGENCIACOMPONENTE"),

    NOMBRECOMPONENTE("NOMBRECOMPONENTE"),

    TIPOCOMPONENTE("TIPOCOMPONENTE"),

    CODIGOPROYECTO("CODIGOPROYECTO"),

    COMPONENTE("COMPONENTE");

    private final String value;

    private FrmcomponentesactividadesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
