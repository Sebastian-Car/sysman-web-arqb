package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para GeneralResponseNomina complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="GeneralResponseNomina">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoRespuesta" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="mensajeRespuesta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeneralResponseNomina", propOrder = { "codigoRespuesta", "mensajeRespuesta" })
@XmlSeeAlso({ ConsultaEstadosNominasResponse.class, ConsultaEstadosFicherosYNominasResponse.class })
public class GeneralResponseNomina {

	protected Integer codigoRespuesta;
	protected String mensajeRespuesta;

	/**
	 * Obtiene el valor de la propiedad codigoRespuesta.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public Integer getCodigoRespuesta() {
		return codigoRespuesta;
	}

	/**
	 * Define el valor de la propiedad codigoRespuesta.
	 * 
	 * @param value allowed object is {@link Integer }
	 * 
	 */
	public void setCodigoRespuesta(Integer value) {
		this.codigoRespuesta = value;
	}

	/**
	 * Obtiene el valor de la propiedad mensajeRespuesta.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMensajeRespuesta() {
		return mensajeRespuesta;
	}

	/**
	 * Define el valor de la propiedad mensajeRespuesta.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setMensajeRespuesta(String value) {
		this.mensajeRespuesta = value;
	}

}
