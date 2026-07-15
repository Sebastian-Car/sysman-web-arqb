package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RipsPrincipal {
    @JsonProperty("NUMDOCUMENTOIDOBLIGADO")
    private String numDocumentoIdObligado;

    @JsonProperty("NUMFACTURA")
    private String numFactura;

    @JsonProperty("TIPONOTA")
    private String tipoNota; 

    @JsonProperty("NUMNOTA")
    private String numNota;

    @JsonProperty("USUARIOS")
    private List<Usuario> usuarios;
    
	public RipsPrincipal() {
	}

	/**
	 * @return the numDocumentoIdObligado
	 */
	public String getNumDocumentoIdObligado() {
		return numDocumentoIdObligado;
	}

	/**
	 * @param numDocumentoIdObligado the numDocumentoIdObligado to set
	 */
	public void setNumDocumentoIdObligado(String numDocumentoIdObligado) {
		this.numDocumentoIdObligado = numDocumentoIdObligado;
	}

	/**
	 * @return the numFactura
	 */
	public String getNumFactura() {
		return numFactura;
	}

	/**
	 * @param numFactura the numFactura to set
	 */
	public void setNumFactura(String numFactura) {
		this.numFactura = numFactura;
	}

	/**
	 * @return the tipoNota
	 */
	public String getTipoNota() {
		return tipoNota;
	}

	/**
	 * @param tipoNota the tipoNota to set
	 */
	public void setTipoNota(String tipoNota) {
		this.tipoNota = tipoNota;
	}

	/**
	 * @return the numNota
	 */
	public String getNumNota() {
		return numNota;
	}

	/**
	 * @param numNota the numNota to set
	 */
	public void setNumNota(String numNota) {
		this.numNota = numNota;
	}

	/**
	 * @return the usuarios
	 */
	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	/**
	 * @param usuarios the usuarios to set
	 */
	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	
}
