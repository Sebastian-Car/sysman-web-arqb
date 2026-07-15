package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Source;

/**
 * <p>
 * Clase Java para infoEstadosFicheroYNomina complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="infoEstadosFicheroYNomina">
 *   &lt;complexContent>
 *     &lt;extension base="{http://impl.consultaestados.ws.saaf.delogica.es/}infoEstadosNomina">
 *       &lt;sequence>
 *         &lt;element name="ficheroXML" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="attachedDocumento" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="nombreAttached" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nombreXML" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infoEstadosFicheroYNomina", propOrder = { "ficheroXML", "attachedDocumento", "nombreAttached",
		"nombreXML" })
public class InfoEstadosFicheroYNomina extends InfoEstadosNomina {

	@XmlMimeType("text/xml")
	protected Source ficheroXML;
	@XmlMimeType("text/xml")
	protected Source attachedDocumento;
	protected String nombreAttached;
	protected String nombreXML;

	/**
	 * Obtiene el valor de la propiedad ficheroXML.
	 * 
	 * @return possible object is {@link Source }
	 * 
	 */
	public Source getFicheroXML() {
		return ficheroXML;
	}

	/**
	 * Define el valor de la propiedad ficheroXML.
	 * 
	 * @param value allowed object is {@link Source }
	 * 
	 */
	public void setFicheroXML(Source value) {
		this.ficheroXML = value;
	}

	/**
	 * Obtiene el valor de la propiedad attachedDocumento.
	 * 
	 * @return possible object is {@link Source }
	 * 
	 */
	public Source getAttachedDocumento() {
		return attachedDocumento;
	}

	/**
	 * Define el valor de la propiedad attachedDocumento.
	 * 
	 * @param value allowed object is {@link Source }
	 * 
	 */
	public void setAttachedDocumento(Source value) {
		this.attachedDocumento = value;
	}

	/**
	 * Obtiene el valor de la propiedad nombreAttached.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNombreAttached() {
		return nombreAttached;
	}

	/**
	 * Define el valor de la propiedad nombreAttached.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setNombreAttached(String value) {
		this.nombreAttached = value;
	}

	/**
	 * Obtiene el valor de la propiedad nombreXML.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNombreXML() {
		return nombreXML;
	}

	/**
	 * Define el valor de la propiedad nombreXML.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setNombreXML(String value) {
		this.nombreXML = value;
	}

}
