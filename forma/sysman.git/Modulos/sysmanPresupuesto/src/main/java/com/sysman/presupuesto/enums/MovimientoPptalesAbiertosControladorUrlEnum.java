/*
 * AcumuladopptalingControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum MovimientoPptalesAbiertosControladorUrlEnum {
	
	URL000("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "4013"),//ano
	
    URL001("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL001", "25008"), 

    URL002("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "25012"),// Comprobante
    
    URL003("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "13001"), 
    
    URL004("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "13035"),//referencia
    
    URL005("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "20013"),
    
    URL006("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "20015"),//centro costo
    
    URL007("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "14001"),
    
    URL008("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "14048"), //tercero
    
    URL009("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "23006"),
    
    URL010("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "23008"), //Auxiliar
    
    URL011("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "34001"),
    
    URL012("MOVIMIENTOPRESUPUESTOABIERTOCONTROLADORURL002", "34003") //fuente recurso
    ;

    private final String key;
    private final String value;

    private MovimientoPptalesAbiertosControladorUrlEnum(String key, String value) {
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
