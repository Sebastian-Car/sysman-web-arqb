/*-
 * InvocadorClaseEnum.java
 *
 * 1.0
 * 
 * 7/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 7/06/2018
 * @author jgomez
 *
 */
public enum InvocadorClaseEnum {

    /**
     * Consulta. Certificado Laboral.
     */
    CERTIFICADO_LABORAL(10),
    /**
     * Consulta. Volante de Pago
     */
    VOLANTE_PAGO(11),

    CERTIFICADO_RETENCION(14),
    /**
     * Solicitud. Permisos.
     */
    PERMISOS(1),
    /**
     * Solicitud. Vacaciones.
     */
    VACACIONES(2),
    /**
     * Consulta. Fondos de afiliacion.
     */
    FONDOS(16),
    /**
     * Consulta. Cesantías pendientes.
     */
    CESANTIAS_PENDIENTES(12),
    /**
     * Consulta. Vacaciones pendientes.
     */
    VACACIONES_PENDIENTES(20),
    /**
     * Consulta. Certificado laboral con funciones.
     */
    CERTIFICADO_LABORAL_CON_FUNCIONES(21),
    /**
     * Consulta. Manual de funciones.
     */
    MANUAL_FUNCIONES(15),
    /**
     * Solicitud de actualizacion de datos personales.
     */
    ACTUALIZACION_DATOS_PERSONALES(0),
    /**
     * Solicitud de actualizacion de datos familiares.
     */
    ACTUALIZACION_DATOS_FAMILIARES(0),

	/**
     * Consulta. Certificado laboral sin asignacion salarial.
     */
	CERTIFICADO_LABORAL_SIN_ASIGNACION_SALARIAL(23);
	
	
    private final int clase;

    private InvocadorClaseEnum(int clase) {
        this.clase = clase;
    }

    public int getClase() {
        return clase;
    }

}
