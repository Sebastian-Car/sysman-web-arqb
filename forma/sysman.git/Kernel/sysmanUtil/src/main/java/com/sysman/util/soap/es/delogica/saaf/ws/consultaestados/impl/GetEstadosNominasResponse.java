package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para getEstadosNominasResponse complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="getEstadosNominasResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://impl.consultaestados.ws.saaf.delogica.es/}consultaEstadosNominasResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getEstadosNominasResponseType", propOrder = { "_return" })
public class GetEstadosNominasResponse {

	@XmlElement(name = "return")
	protected ConsultaEstadosNominasResponse _return;

	/**
	 * Obtiene el valor de la propiedad return.
	 * 
	 * @return possible object is {@link ConsultaEstadosNominasResponse }
	 * 
	 */
	public ConsultaEstadosNominasResponse getReturn() {
		return _return;
	}

	/**
	 * Define el valor de la propiedad return.
	 * 
	 * @param value allowed object is {@link ConsultaEstadosNominasResponse }
	 * 
	 */
	public void setReturn(ConsultaEstadosNominasResponse value) {
		this._return = value;
	}

}
