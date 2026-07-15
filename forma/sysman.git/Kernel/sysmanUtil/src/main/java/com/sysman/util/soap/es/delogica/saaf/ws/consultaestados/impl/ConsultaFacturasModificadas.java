
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para consultaFacturasModificadas complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultaFacturasModificadas">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="request" type="{http://impl.consultaestados.ws.saaf.delogica.es/}consultaFacturasModificadasRequest" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaFacturasModificadasType", propOrder = {
    "request"
})
public class ConsultaFacturasModificadas {

    @XmlElement(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/")
    protected ConsultaFacturasModificadasRequest request;

    /**
     * Obtiene el valor de la propiedad request.
     * 
     * @return
     *     possible object is
     *     {@link ConsultaFacturasModificadasRequest }
     *     
     */
    public ConsultaFacturasModificadasRequest getRequest() {
        return request;
    }

    /**
     * Define el valor de la propiedad request.
     * 
     * @param value
     *     allowed object is
     *     {@link ConsultaFacturasModificadasRequest }
     *     
     */
    public void setRequest(ConsultaFacturasModificadasRequest value) {
        this.request = value;
    }

}
