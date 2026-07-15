package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para getEstadosNominasConFicherosResponse complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="getEstadosNominasConFicherosResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://impl.consultaestados.ws.saaf.delogica.es/}consultaEstadosFicherosYNominasResponse" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getEstadosNominasConFicherosResponseType", propOrder = { "_return" })
public class GetEstadosNominasConFicherosResponse {

	@XmlElement(name = "return")
	protected ConsultaEstadosFicherosYNominasResponse _return;

	/**
	 * Obtiene el valor de la propiedad return.
	 * 
	 * @return possible object is {@link ConsultaEstadosFicherosYNominasResponse }
	 * 
	 */
	public ConsultaEstadosFicherosYNominasResponse getReturn() {
		return _return;
	}

	/**
	 * Define el valor de la propiedad return.
	 * 
	 * @param value allowed object is {@link ConsultaEstadosFicherosYNominasResponse
	 *              }
	 * 
	 */
	public void setReturn(ConsultaEstadosFicherosYNominasResponse value) {
		this._return = value;
	}

}
