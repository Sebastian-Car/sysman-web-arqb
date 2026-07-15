/*-
 * MovimientosPptalesAuxiliarControladorUrlEnum.java
 *
 * 1.0
 * 
 * 22/01/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 22/01/2019
 * @author bcardenas
 *
 */
public enum MovimientosPptalesAuxiliarControladorUrlEnum {

    URL000("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL000", "4013"), // anio

    URL001("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL001", "25008"),

    URL002("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "25012"),

    URL003("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL003", "20013"),

    URL004("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL004", "20015"),

    URL005("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL005", "14001"),

    URL006("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL006", "14048"), // tercero

    URL007("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL007", "23006"),

    URL008("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL008", "23008"), // Auxiliar

    URL009("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL009", "13001"),

    URL010("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL010", "13035"), // referencia

    URL011("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL011", "34001"),

    URL012("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL012", "34003") // fuente
                                                                     // recurso

    ;
    private final String key;
    private final String value;

    private MovimientosPptalesAuxiliarControladorUrlEnum(String key,
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
