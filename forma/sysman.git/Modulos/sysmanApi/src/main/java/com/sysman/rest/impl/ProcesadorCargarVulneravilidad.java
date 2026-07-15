/**
 * 
 */
package com.sysman.rest.impl;

import static com.sysman.rest.EnumRole.COMPONENTE_PROCESSOR;

import java.util.Map;

import javax.inject.Named;

import com.sysman.rest.Ejecutor;
import com.sysman.rest.Procesador;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.negocio.GestionAutoservicio;
import com.sysman.util.rest.RespuestaApi;

/**
 * @author USUARIO
 *
 */

@Named("procesaCargarVulneravilidad")
@Ejecutor(tipo = COMPONENTE_PROCESSOR)
public class ProcesadorCargarVulneravilidad extends Procesador<Map<String, Object>, RespuestaApi> {
	
	/**
     * Propiedad para objeto serializable serialVersionUID
     */
    private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void preProcesar() throws NegocioExcepcion {
esValido = true;		
	}

	@Override
	protected void posProcesar() {
		LOG.info("Pos-proceso <<{}>> con resultado ->> {}", this.getClass(),
                resultado);		
	}

	@Override
	protected void ejecutar() throws NegocioExcepcion {
		 GestionAutoservicio autoservicio = new GestionAutoservicio();
		 resultado = autoservicio.cargarVulnerabilidad(contexto);		
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
