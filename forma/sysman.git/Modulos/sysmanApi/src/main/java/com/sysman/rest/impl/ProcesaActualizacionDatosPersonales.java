/*-
 * ProcesaActualizacionDatosPersonales.java
 *
 * 1.0
 * 
 * 16 jul. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.impl;

import com.sysman.rest.Procesador;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.logica.ParametrosEntradaDatosPersonales;
import com.sysman.rest.negocio.GestionAutoservicio;
import com.sysman.util.rest.Respuesta;

/**
 * Procesador de solicitudes de Actualización de datos personales.
 * 
 * @version 1.0, 16 jul. 2018
 * @author jrodrigueza
 */
public class ProcesaActualizacionDatosPersonales
                extends
                Procesador<ParametrosEntradaDatosPersonales, Respuesta> {

    /**
     * Propiedad para objeto serializable serialVersionUID
     */
    private static final long serialVersionUID = -9075048713006849786L;

    /**
     * Para implementar hilos con la clase <code>Runnable</code>,
     */
    @Override
    public void run() {
        // Sin implementar
    }

    /**
     * Acciones que se ejecutan antes de procesar una petición.
     */
    @Override
    protected void preProcesar() {
        if (contexto != null) {
            esValido = true;
        }
    }

    /**
     * Acciones que se ejecutan despues de procesar una petición.
     */
    @Override
    protected void posProcesar() {
        LOG.info("Pos-proceso <<{}>> con resultado ->> {}", this.getClass(),
                        resultado);
    }

    /**
     * Acciones que ejecuta el procesador o comando concreto.
     */
    @Override
    protected void ejecutar() throws NegocioExcepcion {
        GestionAutoservicio autoservicio = new GestionAutoservicio();
        resultado = autoservicio
                        .solicitarActualizacionDatosPersonales(contexto);
    }

    /**
     * Obtiene el ejecutable o worker.
     */
    @Override
    public Runnable getEjecutable() {
        return this;
    }

    /**
     * Retorna resultado esperado posejecución
     */
    @Override
    public Respuesta getResultado() {
        return resultado;
    }

}
