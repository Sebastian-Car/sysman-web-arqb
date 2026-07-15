/*-
 * ProcesaActualizacionDatosFamiliares.java
 *
 * 1.0
 * 
 * 23 jul. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.impl;

import com.sysman.rest.Procesador;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.negocio.ResuelveConsultas;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;

/**
 * Procesador para ejecuci&oacute;n de consultas del generador de
 * reportes.
 * 
 * @version 1.0, 14 feb. 2019
 * @author jrodrigueza
 *
 */
public class ProcesaResuelveConsultas extends
                Procesador<Map<String, Object>, List<Map<String, Object>>> {

    /**
     * Propiedad para objeto serializable serialVersionUID
     */
    private static final long serialVersionUID = -5506906234178114024L;

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
        ResuelveConsultas resuelveConsultas = new ResuelveConsultas();
        String compania = SysmanFunciones.toString(contexto.get("compania"));
        String idReporte = SysmanFunciones.toString(contexto.get("consulta"));

        Map<String, Object> parametros = contexto;
        parametros.remove("entidad");
        parametros.remove("consulta");
        resultado = resuelveConsultas.traerDatos(compania, idReporte,
                        parametros);
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
    public List<Map<String, Object>> getResultado() {
        return resultado;
    }

}
