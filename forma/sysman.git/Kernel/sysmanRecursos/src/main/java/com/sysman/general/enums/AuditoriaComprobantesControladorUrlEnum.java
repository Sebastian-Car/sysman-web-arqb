/*
 * AUDITORIACOMPROBANTESControladorUrlEnum
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
public enum AuditoriaComprobantesControladorUrlEnum {
    URL002("AUDITORIACOMPROBANTESCONTROLADORURL8796", "4001"), // Ano
                                                               // Inicial

    URL2288("AUDITORIACOMPROBANTESCONTROLADORURL2288", "4027"), // Ano
                                                                // Final

    URL144("AUDITORIACOMPROBANTESCONTROLADORURL144", "25008"), // Tipos
                                                               // comprobantes
                                                               // Pptal
                                                               // Inicial

    URL147("AUDITORIACOMPROBANTESCONTROLADORURL147", "25012"), // Tipos
                                                               // comprobantes
                                                               // Pptal
                                                               // Final

    URL143("AUDITORIACOMPROBANTESCONTROLADORURL143", "463023"),

    URL146("AUDITORIACOMPROBANTESCONTROLADORURL146", "15005"), // Comprobantes
                                                               // Presupuestal
                                                               // Inicial

    URL145("AUDITORIACOMPROBANTESCONTROLADORURL145", "15003"), // Comprobantes
                                                               // Presupuestal
                                                               // Final

    URL148("AUDITORIACOMPROBANTESCONTROLADORURL148", "25047"),

    URL149("AUDITORIACOMPROBANTESCONTROLADORURL149", "25049"),

    URL152("AUDITORIACOMPROBANTESCONTROLADORURL152", "75051"), // Comprobantes
                                                               // Contables
                                                               // Inicial

    URL153("AUDITORIACOMPROBANTESCONTROLADORURL153", "75053"), // Comprobantes
                                                               // Contables
                                                               // Final

    URL150("AUDITORIACOMPROBANTESCONTROLADORURL148", "75055"), // Usuario
                                                               // Inicial
                                                               // Presupuesto

    URL151("AUDITORIACOMPROBANTESCONTROLADORURL149", "75057"), // Usuario
                                                               // Final
                                                               // Presupuesto

    URL154("AUDITORIACOMPROBANTESCONTROLADORURL154", "15070"), // Comprobantes
                                                               // listado
                                                               // Inicial

    URL155("AUDITORIACOMPROBANTESCONTROLADORURL155", "15072"), // Comprobante
                                                               // listado
                                                               // Final

    URL156("AUDITORIACOMPROBANTESCONTROLADORURL156", "72089"), // Usuario
                                                               // Contable
                                                               // Inicial

    URL157("AUDITORIACOMPROBANTESCONTROLADORURL157", "72091") // Usuario
                                                              // Contable
                                                              // Final

    ;
    private final String key;
    private final String value;

    private AuditoriaComprobantesControladorUrlEnum(String key, String value) {
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
