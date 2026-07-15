
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para consultaEstadosFacturasRequest complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultaEstadosFacturasRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificadoresFacturas" type="{http://impl.consultaestados.ws.saaf.delogica.es/}identificadorFactura" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaEstadosFacturasRequest", propOrder = {
    "identificadoresFacturas"
})
public class ConsultaEstadosFacturasRequest {

    @XmlElement(nillable = true)
    protected List<IdentificadorFactura> identificadoresFacturas;

    /**
     * Gets the value of the identificadoresFacturas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the identificadoresFacturas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdentificadoresFacturas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdentificadorFactura }
     * 
     * 
     */
    public List<IdentificadorFactura> getIdentificadoresFacturas() {
        if (identificadoresFacturas == null) {
            identificadoresFacturas = new ArrayList<IdentificadorFactura>();
        }
        return this.identificadoresFacturas;
    }

}
