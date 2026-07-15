package com.sysman.rest.impl;

import static com.sysman.rest.EnumRole.COMPONENTE_PROCESSOR;

import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.rest.Ejecutor;
import com.sysman.rest.Procesador;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.logica.ParametrosEntradaSolicitudes;
import com.sysman.rest.negocio.GestionAutoservicio;
import com.sysman.util.rest.RespuestaApi;

/**
 * Procesador de solicitudes/consultas de Autoservicio.
 * 
 * @version 1.0, 16 jul. 2018
 * @author jrodrigueza
 */
@Ejecutor(tipo = COMPONENTE_PROCESSOR)
public class ProcesaSolicitudesAutoservicioPropio
                extends Procesador<ParametrosEntradaSolicitudes, RespuestaApi> {

    /**
     * Propiedad para objeto serializable serialVersionUID
     */
    private static final long serialVersionUID = 1L;

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
    public void ejecutar() throws NegocioExcepcion {
        GestionAutoservicio autoservicio = new GestionAutoservicio();
        try {
			resultado = autoservicio.generarSolicitudPropia(contexto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
