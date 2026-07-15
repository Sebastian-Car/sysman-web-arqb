package com.sysman.rest.logica;

import static com.sysman.rest.EnumRole.RECEIVER;

import java.io.Serializable;
import java.util.Date;

import com.sysman.rest.Ejecutor;


/**
 * Pojo requerido para obtener los datos requeridos del Pqr
 * 
 * @version 1.0, 04/03/2019
 * @author  mochoa
 */
@Ejecutor(tipo = RECEIVER)
public class ConsultaPqr implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Constructor vacio
     */
    public ConsultaPqr() {

    }
   
    public ConsultaPqr(String numRadicado, String etapa, String dependencia) {
        this.setNumRadicado(numRadicado);
        this.setEtapa(etapa);
        this.setDependencia(dependencia);
        

    }
    
    

	/**
     * Parametro que almacena num de radicado para una consulta
     */
    private String numRadicado;
	/**
     * Parámetro que establecera el tipo de proceso que se va ha realizar en el caso va a ser siempre pqr
     */
    private String etapa;
    /**
     * Parámetro que establecera el tipo de proceso que se va ha realizar en el caso va a ser siempre pqr
     */
    private String dependencia;
    private String nodofinal;
    private String mensajeParametro;
    
    /**
	 * @return the numRadicado
	 */
	public String getNumRadicado() {
		return numRadicado;
	}

	/**
	 * @param numRadicado the numRadicado to set
	 */
	public void setNumRadicado(String numRadicado) {
		this.numRadicado = numRadicado;
	}

	/**
	 * @return the etapa
	 */
	public String getEtapa() {
		return etapa;
	}

	/**
	 * @param etapa the etapa to set
	 */
	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}

	/**
	 * @return the dependencia
	 */
	public String getDependencia() {
		return dependencia;
	}

	/**
	 * @param dependencia the dependencia to set
	 */
	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}
	
	public void setNodoFinal(String nodofinal) {
		this.nodofinal = nodofinal;
	}
	
	public String getNodoFinal() {
		return nodofinal;
	}

	/**
	 * @return the mensajeParametro
	 */
	public String getMensajeParametro() {
		return mensajeParametro;
	}

	/**
	 * @param mensajeParametro the mensajeParametro to set
	 */
	public void setMensajeParametro(String mensajeParametro) {
		this.mensajeParametro = mensajeParametro;
	}
	
	

}
    
