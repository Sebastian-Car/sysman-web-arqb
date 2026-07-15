
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para getEstadosFacturas complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="getEstadosFacturas">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="request" type="{http://impl.consultaestados.ws.saaf.delogica.es/}consultaEstadosFacturasRequest" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getEstadosFacturasType", propOrder = {
    "request"
})
public class GetEstadosFacturas {

    @XmlElement(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/")
    protected ConsultaEstadosFacturasRequest request;

    /**
     * Obtiene el valor de la propiedad request.
     * 
     * @return
     *     possible object is
     *     {@link ConsultaEstadosFacturasRequest }
     *     
     */
    public ConsultaEstadosFacturasRequest getRequest() {
        return request;
    }

    /**
     * Define el valor de la propiedad request.
     * 
     * @param value
     *     allowed object is
     *     {@link ConsultaEstadosFacturasRequest }
     *     
     */
    public void setRequest(ConsultaEstadosFacturasRequest value) {
        this.request = value;
    }

}
