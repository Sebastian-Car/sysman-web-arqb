
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para consultaEstadosFicherosYNominasResponse complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultaEstadosFicherosYNominasResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://impl.consultaestados.ws.saaf.delogica.es/}GeneralResponseNomina">
 *       &lt;sequence>
 *         &lt;element name="infoEstadosNominas" type="{http://impl.consultaestados.ws.saaf.delogica.es/}infoEstadosFicheroYNomina" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaEstadosFicherosYNominasResponse", propOrder = { "infoEstadosNominas" })
public class ConsultaEstadosFicherosYNominasResponse extends GeneralResponseNomina {

	@XmlElement(nillable = true)
	protected List<InfoEstadosFicheroYNomina> infoEstadosNominas;

	/**
	 * Gets the value of the infoEstadosNominas property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the infoEstadosNominas property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getInfoEstadosNominas().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link InfoEstadosFicheroYNomina }
	 * 
	 * 
	 */
	public List<InfoEstadosFicheroYNomina> getInfoEstadosNominas() {
		if (infoEstadosNominas == null) {
			infoEstadosNominas = new ArrayList<InfoEstadosFicheroYNomina>();
		}
		return this.infoEstadosNominas;
	}

}
