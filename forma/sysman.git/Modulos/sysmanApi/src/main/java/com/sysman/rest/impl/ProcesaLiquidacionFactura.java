/*-
 * ProcesaLiquidacionFactura.java
 * 
 * @version 1.0, 09/12/2022
 * @author cperez
 */

package com.sysman.rest.impl;

import com.sysman.rest.Procesador;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.logica.ParametrosLiquidacionFactura;
import com.sysman.rest.negocio.GestionAutoservicio;
import com.sysman.util.rest.RespuestaApi;

import javax.inject.Named;

/**
 * Procesador para pagar factura general
 * 
 * @version 1.0, 09/12/2022
 * @author cperez
 *
 */
@Named("procesaLiquidacionFactura")
public class ProcesaLiquidacionFactura extends
                Procesador<ParametrosLiquidacionFactura, RespuestaApi> {

    /**
     * Propiedad para objeto serializable serialVersionUID
     */
    private static final long serialVersionUID = 1319637949857402202L;

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
        resultado = autoservicio.pagarLiquidacionFactura(contexto);
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
    public RespuestaApi getResultado() {
        return resultado;
    }

}
