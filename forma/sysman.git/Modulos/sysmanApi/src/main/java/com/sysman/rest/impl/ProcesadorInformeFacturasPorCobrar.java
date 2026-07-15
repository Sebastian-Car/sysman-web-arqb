package com.sysman.rest.impl;

import static com.sysman.rest.EnumRole.COMPONENTE_PROCESSOR;

import com.sysman.rest.Ejecutor;
import com.sysman.rest.Procesador;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.logica.ParametrosInformeFacturasPorCobrar;
import com.sysman.rest.negocio.GestionAutoservicio;
import com.sysman.util.rest.RespuestaApi;

@Ejecutor(tipo = COMPONENTE_PROCESSOR)
public class ProcesadorInformeFacturasPorCobrar extends Procesador<ParametrosInformeFacturasPorCobrar, RespuestaApi> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8616094561587934272L;

	 /**
     * Para implementar hilos con la clase <code>Runnable</code>,
     */
    @Override
    public void run()
    {
        // Sin implementar
    }

    /**
     * Acciones que se ejecutan antes de procesar una petición.
     */
    @Override
    protected void preProcesar()
    {
        if (contexto != null)
        {
            esValido = true;
        }
    }

    /**
     * Acciones que se ejecutan despues de procesar una petición.
     */
    @Override
    protected void posProcesar()
    {
        LOG.info("Pos-proceso <<{}>> con resultado ->> {}", this.getClass(),
                        resultado);
    }

    /**
     * Acciones que ejecuta el procesador o comando concreto.
     */
    @Override
    public void ejecutar() throws NegocioExcepcion
    {
        GestionAutoservicio autoservicio = new GestionAutoservicio();
        resultado = autoservicio.generarInffacturasPorCobrar(contexto);
    }

    /**
     * Obtiene el ejecutable o worker.
     */
    @Override
    public Runnable getEjecutable()
    {
        return this;
    }

    /**
     * Retorna resultado esperado posejecución
     */
    @Override
    public RespuestaApi getResultado()
    {
        return resultado;
    }
}
