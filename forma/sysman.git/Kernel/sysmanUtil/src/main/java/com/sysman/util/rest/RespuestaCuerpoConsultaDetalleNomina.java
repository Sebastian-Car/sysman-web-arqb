/**
 * 
 */
package com.sysman.util.rest;

import java.util.List;

/**
 * @author avega
 *Se adiciona el atributo XmlDocumentKey, que representa el codigo
 * CUNE
 */
public class RespuestaCuerpoConsultaDetalleNomina {

	 	private String Observaciones;

	    private String Estado;

		public String getObservaciones() {
			return Observaciones;
		}

		public void setObservaciones(String observaciones) {
			Observaciones = observaciones;
		}

		public String getEstado() {
			return Estado;
		}

		public void setEstado(String estado) {
			Estado = estado;
		}

	    

}
