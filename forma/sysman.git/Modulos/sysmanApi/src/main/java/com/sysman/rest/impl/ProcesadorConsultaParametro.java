package com.sysman.rest.impl;

import com.sysman.util.rest.RespuestaApi;

import static com.sysman.rest.EnumRole.COMPONENTE_PROCESSOR;

import javax.inject.Named;

import com.sysman.rest.Ejecutor;
import com.sysman.rest.Procesador;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.negocio.GestionAutoservicio;
import com.sysman.util.rest.Parametro;

@Named("procesadorConsultaParametro")
@Ejecutor(tipo = COMPONENTE_PROCESSOR)
public class ProcesadorConsultaParametro extends Procesador<Parametro, RespuestaApi> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		//METODO_NO_IMPLEMENTADO
		
	}

	@Override
	protected void preProcesar() throws NegocioExcepcion {
		if (contexto != null) {
			esValido = true;
		}
	}

	@Override
	protected void posProcesar() {
		LOG.info("Pos-proceso <<{}>> con resultado ->> {}", this.getClass(),
        resultado);
		
	}

	@Override
	protected void ejecutar() throws NegocioExcepcion {
		GestionAutoservicio autoservicio = new GestionAutoservicio();
        resultado = autoservicio.consultarParam(contexto);
		
	}

	@Override
	public Runnable getEjecutable() {
		return this;
	}

	@Override
	public RespuestaApi getResultado() {
		return resultado;
	}


}
