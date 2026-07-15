
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para documento complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="documento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="numeroDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="uuidDocumento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="subtipoDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipoOperacion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="divisa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fechaDocumento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="refPedido" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unidadOrganizativa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaVencimiento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="direccionFactura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="distritoFactura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ciudadFactura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="departamentoFactura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoPostalFactura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paisFactura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="incoterm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="motivoRect" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaIniFacturacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaFinFacturacion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentosReferenciados" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="documentoReferenciado" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="numDocumentoRef" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="uuidDocumentoRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="fechaDocumentoRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="documentosAdjuntos" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="documentoAdjunto" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="nombreFichero" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="contenidoFichero" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="indPdfPrincipal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="proveedor">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="idProveedor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="cliente">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="idCliente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="tipoDocumentoIdCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="regimenCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="razonSocialCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="nombreCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="apellido1Cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="apellido2Cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="tipoPersonaCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="direccionCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="distritoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="ciudadCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="departamentoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="codigoPostalCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="paisCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="telefonoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="emailCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="matriculaCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="responsabilidadesRutCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="tributosCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="emailsEnvio" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="impuestos" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="impuesto" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="baseImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="porcImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="valorImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="codImpuesto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="retenciones" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="retencion" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="baseRetencion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="porcRetencion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="valorRetencion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="codRetencion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="anticipos" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="anticipo" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="valorAnticipo" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="fechaRecepcionAnticipo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="fechaRealizacionAnticipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="instruccionesAnticipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="datosTotales">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="subtotal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="porcDescuentoFinal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="descuentoFinal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="totalBase" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="totalImpuestos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="totalGastos" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="totalDocumento" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="totalRetenciones" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="totalAnticipos" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                   &lt;element name="aPagar" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="totalesCop">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="fctConvCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="monedaCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="subtotalCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="descuentoDetalleCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cargoDetalleCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalBrutoCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalIvaCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalIncCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalBolsasCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalOtroImpCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="montoImpuestosCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalNetoCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="montoDctoCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="montoCargoCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="valorPagarCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="reteFuenteCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="reteIvaCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="reteIcaCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalAnticiposCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="condicionesPago">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="condicionPago" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="formaPago" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="medioPago" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="entidadBancaria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="numeroCuenta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="beneficiario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="fechaPago" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="tipoCambio" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="divisaOrigen" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="divisaDestino" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="tipoCambio" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="fechaTipoCambio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="datosAdicionales" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="datoAdicional" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="numDato" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *                             &lt;element name="valorDato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="lineas">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="linea" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="numLinea" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *                             &lt;element name="idEstandarReferencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="referenciaItem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="descripcionItem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="unidadMedida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="unidadesLinea" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="precioUnidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="subtotalLinea" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="porcDescuentoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="descuentoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="totalLinea" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                             &lt;element name="codImpuestoLinea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="porcImpuestoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="valorImpuestoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                             &lt;element name="idMandante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="datosAdicionales" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="datoAdicional" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="numDato" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *                                                 &lt;element name="valorDato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="extensionBolsa" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="consecutivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="fechaCumplimiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="cantidadNominal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="precioRegistro" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="montoRendimientos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                   &lt;element name="porcentajeComision" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="extensionPOS" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="beneficiosComprador" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="codigoComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="nombreComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="puntosAcumuladosComprador" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="puntoDeVenta" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="placaCaja" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="ubicacionAlmacen" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="nombreVendedor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="tipoCaja" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="codigoVenta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="subtotalVenta" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="extensionSPD" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="numLinea" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   &lt;element name="servicioFacturado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="empresa" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="motivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="numeroContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="fechaContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="informacionContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="subscriptor" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="direccionPostal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="direccionEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="ciudad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="departamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="pais" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="tipoEstrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="valorFacturado" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ciclo" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *                             &lt;element name="tipoPeriodicidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="infoAdicional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="producto" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="totalUnidades" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="unidadMedidaTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="consumoTotal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="unidadMedidaConsumida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="valorConsumoParcial" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="valorUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="cantidadAplicaPrecio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="unidadMedida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="descuentos" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="razonDescuento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="valorDto" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="cargos" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="razonCargo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="valorCargo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="lecturaContador" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="datosMedidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="fechaLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="unidadesLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="unidadMedidaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="fechaLecturaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="unidadesLecturaActual" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="unidadMedidaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="metodoLectura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="duracionServicio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="unidadMedidaServicio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="historicoConsumos" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="pagoAnterior" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="consumoMensual" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="diasFacturados" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="unidadMedidaDiasFacturados" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="valorAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="divisa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="consumoPromedio" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="mediaConsumo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="unidadMedidaMediaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="historicoResiduos" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="residuos" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="unidadMedidaTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="totalNoAprovechable" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="totalLimpiezaBarrido" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="totalLimpiezaUrbana" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                                 &lt;element name="totalInutil" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="acuerdosPago" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="numeroContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="nombreProducto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="descripcionProducto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                       &lt;element name="cuotasAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="cuotasPagadas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="porcentajeInteres" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="saldoAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="cuotasACancelar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="valorCuotaPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="totalCargos" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                       &lt;element name="totalDescuento" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "documento")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documento", propOrder = {
    "numeroDocumento",
    "uuidDocumento",
    "tipoDocumento",
    "subtipoDocumento",
    "tipoOperacion",
    "divisa",
    "fechaDocumento",
    "refPedido",
    "unidadOrganizativa",
    "fechaVencimiento",
    "direccionFactura",
    "distritoFactura",
    "ciudadFactura",
    "departamentoFactura",
    "codigoPostalFactura",
    "paisFactura",
    "incoterm",
    "motivoRect",
    "fechaIniFacturacion",
    "fechaFinFacturacion",
    "documentosReferenciados",
    "documentosAdjuntos",
    "proveedor",
    "cliente",
    "emailsEnvio",
    "impuestos",
    "retenciones",
    "anticipos",
    "datosTotales",
    "totalesCop",
    "condicionesPago",
    "tipoCambio",
    "datosAdicionales",
    "lineas",
    "extensionBolsa",
    "extensionPOS",
    "extensionSPD"
})
public class Documento {

    @XmlElement(required = true)
    protected String numeroDocumento;
    protected String uuidDocumento;
    @XmlElement(required = true)
    protected String tipoDocumento;
    @XmlElement(required = true)
    protected String subtipoDocumento;
    @XmlElement(required = true)
    protected String tipoOperacion;
    @XmlElement(required = true)
    protected String divisa;
    @XmlElement(required = true)
    protected String fechaDocumento;
    protected String refPedido;
    protected String unidadOrganizativa;
    protected String fechaVencimiento;
    protected String direccionFactura;
    protected String distritoFactura;
    protected String ciudadFactura;
    protected String departamentoFactura;
    protected String codigoPostalFactura;
    protected String paisFactura;
    protected String incoterm;
    protected String motivoRect;
    protected String fechaIniFacturacion;
    protected String fechaFinFacturacion;
    protected Documento.DocumentosReferenciados documentosReferenciados;
    protected Documento.DocumentosAdjuntos documentosAdjuntos;
    @XmlElement(required = true)
    protected Documento.Proveedor proveedor;
    @XmlElement(required = true)
    protected Documento.Cliente cliente;
    protected Documento.EmailsEnvio emailsEnvio;
    protected Documento.Impuestos impuestos;
    protected Documento.Retenciones retenciones;
    protected Documento.Anticipos anticipos;
    @XmlElement(required = true)
    protected Documento.DatosTotales datosTotales;
    @XmlElement(required = true)
    protected Documento.TotalesCop totalesCop;
    @XmlElement(required = true)
    protected Documento.CondicionesPago condicionesPago;
    protected Documento.TipoCambio tipoCambio;
    protected Documento.DatosAdicionales datosAdicionales;
    @XmlElement(required = true)
    protected Documento.Lineas lineas;
    protected Documento.ExtensionBolsa extensionBolsa;
    protected Documento.ExtensionPOS extensionPOS;
    protected List<Documento.ExtensionSPD> extensionSPD;

    /**
     * Obtiene el valor de la propiedad numeroDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * Define el valor de la propiedad numeroDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroDocumento(String value) {
        this.numeroDocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad uuidDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUuidDocumento() {
        return uuidDocumento;
    }

    /**
     * Define el valor de la propiedad uuidDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUuidDocumento(String value) {
        this.uuidDocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Define el valor de la propiedad tipoDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDocumento(String value) {
        this.tipoDocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad subtipoDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtipoDocumento() {
        return subtipoDocumento;
    }

    /**
     * Define el valor de la propiedad subtipoDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtipoDocumento(String value) {
        this.subtipoDocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoOperacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * Define el valor de la propiedad tipoOperacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoOperacion(String value) {
        this.tipoOperacion = value;
    }

    /**
     * Obtiene el valor de la propiedad divisa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDivisa() {
        return divisa;
    }

    /**
     * Define el valor de la propiedad divisa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDivisa(String value) {
        this.divisa = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaDocumento() {
        return fechaDocumento;
    }

    /**
     * Define el valor de la propiedad fechaDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaDocumento(String value) {
        this.fechaDocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad refPedido.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefPedido() {
        return refPedido;
    }

    /**
     * Define el valor de la propiedad refPedido.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefPedido(String value) {
        this.refPedido = value;
    }

    /**
     * Obtiene el valor de la propiedad unidadOrganizativa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnidadOrganizativa() {
        return unidadOrganizativa;
    }

    /**
     * Define el valor de la propiedad unidadOrganizativa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnidadOrganizativa(String value) {
        this.unidadOrganizativa = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaVencimiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * Define el valor de la propiedad fechaVencimiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaVencimiento(String value) {
        this.fechaVencimiento = value;
    }

    /**
     * Obtiene el valor de la propiedad direccionFactura.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDireccionFactura() {
        return direccionFactura;
    }

    /**
     * Define el valor de la propiedad direccionFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDireccionFactura(String value) {
        this.direccionFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad distritoFactura.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistritoFactura() {
        return distritoFactura;
    }

    /**
     * Define el valor de la propiedad distritoFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistritoFactura(String value) {
        this.distritoFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad ciudadFactura.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCiudadFactura() {
        return ciudadFactura;
    }

    /**
     * Define el valor de la propiedad ciudadFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCiudadFactura(String value) {
        this.ciudadFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad departamentoFactura.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartamentoFactura() {
        return departamentoFactura;
    }

    /**
     * Define el valor de la propiedad departamentoFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartamentoFactura(String value) {
        this.departamentoFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoPostalFactura.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoPostalFactura() {
        return codigoPostalFactura;
    }

    /**
     * Define el valor de la propiedad codigoPostalFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoPostalFactura(String value) {
        this.codigoPostalFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad paisFactura.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaisFactura() {
        return paisFactura;
    }

    /**
     * Define el valor de la propiedad paisFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaisFactura(String value) {
        this.paisFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad incoterm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncoterm() {
        return incoterm;
    }

    /**
     * Define el valor de la propiedad incoterm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncoterm(String value) {
        this.incoterm = value;
    }

    /**
     * Obtiene el valor de la propiedad motivoRect.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotivoRect() {
        return motivoRect;
    }

    /**
     * Define el valor de la propiedad motivoRect.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotivoRect(String value) {
        this.motivoRect = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaIniFacturacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaIniFacturacion() {
        return fechaIniFacturacion;
    }

    /**
     * Define el valor de la propiedad fechaIniFacturacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaIniFacturacion(String value) {
        this.fechaIniFacturacion = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaFinFacturacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaFinFacturacion() {
        return fechaFinFacturacion;
    }

    /**
     * Define el valor de la propiedad fechaFinFacturacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaFinFacturacion(String value) {
        this.fechaFinFacturacion = value;
    }

    /**
     * Obtiene el valor de la propiedad documentosReferenciados.
     * 
     * @return
     *     possible object is
     *     {@link Documento.DocumentosReferenciados }
     *     
     */
    public Documento.DocumentosReferenciados getDocumentosReferenciados() {
        return documentosReferenciados;
    }

    /**
     * Define el valor de la propiedad documentosReferenciados.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.DocumentosReferenciados }
     *     
     */
    public void setDocumentosReferenciados(Documento.DocumentosReferenciados value) {
        this.documentosReferenciados = value;
    }

    /**
     * Obtiene el valor de la propiedad documentosAdjuntos.
     * 
     * @return
     *     possible object is
     *     {@link Documento.DocumentosAdjuntos }
     *     
     */
    public Documento.DocumentosAdjuntos getDocumentosAdjuntos() {
        return documentosAdjuntos;
    }

    /**
     * Define el valor de la propiedad documentosAdjuntos.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.DocumentosAdjuntos }
     *     
     */
    public void setDocumentosAdjuntos(Documento.DocumentosAdjuntos value) {
        this.documentosAdjuntos = value;
    }

    /**
     * Obtiene el valor de la propiedad proveedor.
     * 
     * @return
     *     possible object is
     *     {@link Documento.Proveedor }
     *     
     */
    public Documento.Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Define el valor de la propiedad proveedor.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.Proveedor }
     *     
     */
    public void setProveedor(Documento.Proveedor value) {
        this.proveedor = value;
    }

    /**
     * Obtiene el valor de la propiedad cliente.
     * 
     * @return
     *     possible object is
     *     {@link Documento.Cliente }
     *     
     */
    public Documento.Cliente getCliente() {
        return cliente;
    }

    /**
     * Define el valor de la propiedad cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.Cliente }
     *     
     */
    public void setCliente(Documento.Cliente value) {
        this.cliente = value;
    }

    /**
     * Obtiene el valor de la propiedad emailsEnvio.
     * 
     * @return
     *     possible object is
     *     {@link Documento.EmailsEnvio }
     *     
     */
    public Documento.EmailsEnvio getEmailsEnvio() {
        return emailsEnvio;
    }

    /**
     * Define el valor de la propiedad emailsEnvio.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.EmailsEnvio }
     *     
     */
    public void setEmailsEnvio(Documento.EmailsEnvio value) {
        this.emailsEnvio = value;
    }

    /**
     * Obtiene el valor de la propiedad impuestos.
     * 
     * @return
     *     possible object is
     *     {@link Documento.Impuestos }
     *     
     */
    public Documento.Impuestos getImpuestos() {
        return impuestos;
    }

    /**
     * Define el valor de la propiedad impuestos.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.Impuestos }
     *     
     */
    public void setImpuestos(Documento.Impuestos value) {
        this.impuestos = value;
    }

    /**
     * Obtiene el valor de la propiedad retenciones.
     * 
     * @return
     *     possible object is
     *     {@link Documento.Retenciones }
     *     
     */
    public Documento.Retenciones getRetenciones() {
        return retenciones;
    }

    /**
     * Define el valor de la propiedad retenciones.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.Retenciones }
     *     
     */
    public void setRetenciones(Documento.Retenciones value) {
        this.retenciones = value;
    }

    /**
     * Obtiene el valor de la propiedad anticipos.
     * 
     * @return
     *     possible object is
     *     {@link Documento.Anticipos }
     *     
     */
    public Documento.Anticipos getAnticipos() {
        return anticipos;
    }

    /**
     * Define el valor de la propiedad anticipos.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.Anticipos }
     *     
     */
    public void setAnticipos(Documento.Anticipos value) {
        this.anticipos = value;
    }

    /**
     * Obtiene el valor de la propiedad datosTotales.
     * 
     * @return
     *     possible object is
     *     {@link Documento.DatosTotales }
     *     
     */
    public Documento.DatosTotales getDatosTotales() {
        return datosTotales;
    }

    /**
     * Define el valor de la propiedad datosTotales.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.DatosTotales }
     *     
     */
    public void setDatosTotales(Documento.DatosTotales value) {
        this.datosTotales = value;
    }

    /**
     * Obtiene el valor de la propiedad totalesCop.
     * 
     * @return
     *     possible object is
     *     {@link Documento.TotalesCop }
     *     
     */
    public Documento.TotalesCop getTotalesCop() {
        return totalesCop;
    }

    /**
     * Define el valor de la propiedad totalesCop.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.TotalesCop }
     *     
     */
    public void setTotalesCop(Documento.TotalesCop value) {
        this.totalesCop = value;
    }

    /**
     * Obtiene el valor de la propiedad condicionesPago.
     * 
     * @return
     *     possible object is
     *     {@link Documento.CondicionesPago }
     *     
     */
    public Documento.CondicionesPago getCondicionesPago() {
        return condicionesPago;
    }

    /**
     * Define el valor de la propiedad condicionesPago.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.CondicionesPago }
     *     
     */
    public void setCondicionesPago(Documento.CondicionesPago value) {
        this.condicionesPago = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoCambio.
     * 
     * @return
     *     possible object is
     *     {@link Documento.TipoCambio }
     *     
     */
    public Documento.TipoCambio getTipoCambio() {
        return tipoCambio;
    }

    /**
     * Define el valor de la propiedad tipoCambio.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.TipoCambio }
     *     
     */
    public void setTipoCambio(Documento.TipoCambio value) {
        this.tipoCambio = value;
    }

    /**
     * Obtiene el valor de la propiedad datosAdicionales.
     * 
     * @return
     *     possible object is
     *     {@link Documento.DatosAdicionales }
     *     
     */
    public Documento.DatosAdicionales getDatosAdicionales() {
        return datosAdicionales;
    }

    /**
     * Define el valor de la propiedad datosAdicionales.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.DatosAdicionales }
     *     
     */
    public void setDatosAdicionales(Documento.DatosAdicionales value) {
        this.datosAdicionales = value;
    }

    /**
     * Obtiene el valor de la propiedad lineas.
     * 
     * @return
     *     possible object is
     *     {@link Documento.Lineas }
     *     
     */
    public Documento.Lineas getLineas() {
        return lineas;
    }

    /**
     * Define el valor de la propiedad lineas.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.Lineas }
     *     
     */
    public void setLineas(Documento.Lineas value) {
        this.lineas = value;
    }

    /**
     * Obtiene el valor de la propiedad extensionBolsa.
     * 
     * @return
     *     possible object is
     *     {@link Documento.ExtensionBolsa }
     *     
     */
    public Documento.ExtensionBolsa getExtensionBolsa() {
        return extensionBolsa;
    }

    /**
     * Define el valor de la propiedad extensionBolsa.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.ExtensionBolsa }
     *     
     */
    public void setExtensionBolsa(Documento.ExtensionBolsa value) {
        this.extensionBolsa = value;
    }

    /**
     * Obtiene el valor de la propiedad extensionPOS.
     * 
     * @return
     *     possible object is
     *     {@link Documento.ExtensionPOS }
     *     
     */
    public Documento.ExtensionPOS getExtensionPOS() {
        return extensionPOS;
    }

    /**
     * Define el valor de la propiedad extensionPOS.
     * 
     * @param value
     *     allowed object is
     *     {@link Documento.ExtensionPOS }
     *     
     */
    public void setExtensionPOS(Documento.ExtensionPOS value) {
        this.extensionPOS = value;
    }

    /**
     * Gets the value of the extensionSPD property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extensionSPD property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtensionSPD().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Documento.ExtensionSPD }
     * 
     * 
     */
    public List<Documento.ExtensionSPD> getExtensionSPD() {
        if (extensionSPD == null) {
            extensionSPD = new ArrayList<Documento.ExtensionSPD>();
        }
        return this.extensionSPD;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="anticipo" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="valorAnticipo" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="fechaRecepcionAnticipo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="fechaRealizacionAnticipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="instruccionesAnticipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "anticipo"
    })
    public static class Anticipos {

        @XmlElement(required = true)
        protected List<Documento.Anticipos.Anticipo> anticipo;

        /**
         * Gets the value of the anticipo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the anticipo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAnticipo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.Anticipos.Anticipo }
         * 
         * 
         */
        public List<Documento.Anticipos.Anticipo> getAnticipo() {
            if (anticipo == null) {
                anticipo = new ArrayList<Documento.Anticipos.Anticipo>();
            }
            return this.anticipo;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="valorAnticipo" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="fechaRecepcionAnticipo" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="fechaRealizacionAnticipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="instruccionesAnticipo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "valorAnticipo",
            "fechaRecepcionAnticipo",
            "fechaRealizacionAnticipo",
            "instruccionesAnticipo"
        })
        public static class Anticipo {

            @XmlElement(required = true)
            protected BigDecimal valorAnticipo;
            @XmlElement(required = true)
            protected String fechaRecepcionAnticipo;
            protected String fechaRealizacionAnticipo;
            protected String instruccionesAnticipo;

            /**
             * Obtiene el valor de la propiedad valorAnticipo.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getValorAnticipo() {
                return valorAnticipo;
            }

            /**
             * Define el valor de la propiedad valorAnticipo.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setValorAnticipo(BigDecimal value) {
                this.valorAnticipo = value;
            }

            /**
             * Obtiene el valor de la propiedad fechaRecepcionAnticipo.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFechaRecepcionAnticipo() {
                return fechaRecepcionAnticipo;
            }

            /**
             * Define el valor de la propiedad fechaRecepcionAnticipo.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFechaRecepcionAnticipo(String value) {
                this.fechaRecepcionAnticipo = value;
            }

            /**
             * Obtiene el valor de la propiedad fechaRealizacionAnticipo.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFechaRealizacionAnticipo() {
                return fechaRealizacionAnticipo;
            }

            /**
             * Define el valor de la propiedad fechaRealizacionAnticipo.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFechaRealizacionAnticipo(String value) {
                this.fechaRealizacionAnticipo = value;
            }

            /**
             * Obtiene el valor de la propiedad instruccionesAnticipo.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInstruccionesAnticipo() {
                return instruccionesAnticipo;
            }

            /**
             * Define el valor de la propiedad instruccionesAnticipo.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInstruccionesAnticipo(String value) {
                this.instruccionesAnticipo = value;
            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="idCliente" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="tipoDocumentoIdCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="regimenCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="razonSocialCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="nombreCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="apellido1Cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="apellido2Cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="tipoPersonaCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="direccionCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="distritoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="ciudadCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="departamentoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="codigoPostalCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="paisCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="telefonoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="emailCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="matriculaCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="responsabilidadesRutCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="tributosCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "idCliente",
        "tipoDocumentoIdCliente",
        "regimenCliente",
        "razonSocialCliente",
        "nombreCliente",
        "apellido1Cliente",
        "apellido2Cliente",
        "tipoPersonaCliente",
        "direccionCliente",
        "distritoCliente",
        "ciudadCliente",
        "departamentoCliente",
        "codigoPostalCliente",
        "paisCliente",
        "telefonoCliente",
        "emailCliente",
        "matriculaCliente",
        "responsabilidadesRutCliente",
        "tributosCliente"
    })
    public static class Cliente {

        @XmlElement(required = true)
        protected String idCliente;
        protected String tipoDocumentoIdCliente;
        protected String regimenCliente;
        protected String razonSocialCliente;
        protected String nombreCliente;
        protected String apellido1Cliente;
        protected String apellido2Cliente;
        protected String tipoPersonaCliente;
        protected String direccionCliente;
        protected String distritoCliente;
        protected String ciudadCliente;
        protected String departamentoCliente;
        protected String codigoPostalCliente;
        protected String paisCliente;
        protected String telefonoCliente;
        protected String emailCliente;
        protected String matriculaCliente;
        protected String responsabilidadesRutCliente;
        protected String tributosCliente;

        /**
         * Obtiene el valor de la propiedad idCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdCliente() {
            return idCliente;
        }

        /**
         * Define el valor de la propiedad idCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdCliente(String value) {
            this.idCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad tipoDocumentoIdCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTipoDocumentoIdCliente() {
            return tipoDocumentoIdCliente;
        }

        /**
         * Define el valor de la propiedad tipoDocumentoIdCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTipoDocumentoIdCliente(String value) {
            this.tipoDocumentoIdCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad regimenCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRegimenCliente() {
            return regimenCliente;
        }

        /**
         * Define el valor de la propiedad regimenCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRegimenCliente(String value) {
            this.regimenCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad razonSocialCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRazonSocialCliente() {
            return razonSocialCliente;
        }

        /**
         * Define el valor de la propiedad razonSocialCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRazonSocialCliente(String value) {
            this.razonSocialCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad nombreCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNombreCliente() {
            return nombreCliente;
        }

        /**
         * Define el valor de la propiedad nombreCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNombreCliente(String value) {
            this.nombreCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad apellido1Cliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getApellido1Cliente() {
            return apellido1Cliente;
        }

        /**
         * Define el valor de la propiedad apellido1Cliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setApellido1Cliente(String value) {
            this.apellido1Cliente = value;
        }

        /**
         * Obtiene el valor de la propiedad apellido2Cliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getApellido2Cliente() {
            return apellido2Cliente;
        }

        /**
         * Define el valor de la propiedad apellido2Cliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setApellido2Cliente(String value) {
            this.apellido2Cliente = value;
        }

        /**
         * Obtiene el valor de la propiedad tipoPersonaCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTipoPersonaCliente() {
            return tipoPersonaCliente;
        }

        /**
         * Define el valor de la propiedad tipoPersonaCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTipoPersonaCliente(String value) {
            this.tipoPersonaCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad direccionCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDireccionCliente() {
            return direccionCliente;
        }

        /**
         * Define el valor de la propiedad direccionCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDireccionCliente(String value) {
            this.direccionCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad distritoCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDistritoCliente() {
            return distritoCliente;
        }

        /**
         * Define el valor de la propiedad distritoCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDistritoCliente(String value) {
            this.distritoCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad ciudadCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCiudadCliente() {
            return ciudadCliente;
        }

        /**
         * Define el valor de la propiedad ciudadCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCiudadCliente(String value) {
            this.ciudadCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad departamentoCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDepartamentoCliente() {
            return departamentoCliente;
        }

        /**
         * Define el valor de la propiedad departamentoCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDepartamentoCliente(String value) {
            this.departamentoCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad codigoPostalCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCodigoPostalCliente() {
            return codigoPostalCliente;
        }

        /**
         * Define el valor de la propiedad codigoPostalCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCodigoPostalCliente(String value) {
            this.codigoPostalCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad paisCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPaisCliente() {
            return paisCliente;
        }

        /**
         * Define el valor de la propiedad paisCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPaisCliente(String value) {
            this.paisCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad telefonoCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTelefonoCliente() {
            return telefonoCliente;
        }

        /**
         * Define el valor de la propiedad telefonoCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTelefonoCliente(String value) {
            this.telefonoCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad emailCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEmailCliente() {
            return emailCliente;
        }

        /**
         * Define el valor de la propiedad emailCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEmailCliente(String value) {
            this.emailCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad matriculaCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMatriculaCliente() {
            return matriculaCliente;
        }

        /**
         * Define el valor de la propiedad matriculaCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMatriculaCliente(String value) {
            this.matriculaCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad responsabilidadesRutCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getResponsabilidadesRutCliente() {
            return responsabilidadesRutCliente;
        }

        /**
         * Define el valor de la propiedad responsabilidadesRutCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setResponsabilidadesRutCliente(String value) {
            this.responsabilidadesRutCliente = value;
        }

        /**
         * Obtiene el valor de la propiedad tributosCliente.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTributosCliente() {
            return tributosCliente;
        }

        /**
         * Define el valor de la propiedad tributosCliente.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTributosCliente(String value) {
            this.tributosCliente = value;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="condicionPago" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="formaPago" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="medioPago" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="entidadBancaria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="numeroCuenta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="beneficiario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="fechaPago" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "condicionPago"
    })
    public static class CondicionesPago {

        @XmlElement(required = true)
        protected List<Documento.CondicionesPago.CondicionPago> condicionPago;

        /**
         * Gets the value of the condicionPago property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the condicionPago property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCondicionPago().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.CondicionesPago.CondicionPago }
         * 
         * 
         */
        public List<Documento.CondicionesPago.CondicionPago> getCondicionPago() {
            if (condicionPago == null) {
                condicionPago = new ArrayList<Documento.CondicionesPago.CondicionPago>();
            }
            return this.condicionPago;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="formaPago" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="medioPago" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="entidadBancaria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="numeroCuenta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="beneficiario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="fechaPago" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "formaPago",
            "medioPago",
            "entidadBancaria",
            "numeroCuenta",
            "beneficiario",
            "fechaPago"
        })
        public static class CondicionPago {

            @XmlElement(required = true)
            protected String formaPago;
            @XmlElement(required = true)
            protected String medioPago;
            protected String entidadBancaria;
            protected String numeroCuenta;
            protected String beneficiario;
            protected String fechaPago;

            /**
             * Obtiene el valor de la propiedad formaPago.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFormaPago() {
                return formaPago;
            }

            /**
             * Define el valor de la propiedad formaPago.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFormaPago(String value) {
                this.formaPago = value;
            }

            /**
             * Obtiene el valor de la propiedad medioPago.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMedioPago() {
                return medioPago;
            }

            /**
             * Define el valor de la propiedad medioPago.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMedioPago(String value) {
                this.medioPago = value;
            }

            /**
             * Obtiene el valor de la propiedad entidadBancaria.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEntidadBancaria() {
                return entidadBancaria;
            }

            /**
             * Define el valor de la propiedad entidadBancaria.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEntidadBancaria(String value) {
                this.entidadBancaria = value;
            }

            /**
             * Obtiene el valor de la propiedad numeroCuenta.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNumeroCuenta() {
                return numeroCuenta;
            }

            /**
             * Define el valor de la propiedad numeroCuenta.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNumeroCuenta(String value) {
                this.numeroCuenta = value;
            }

            /**
             * Obtiene el valor de la propiedad beneficiario.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBeneficiario() {
                return beneficiario;
            }

            /**
             * Define el valor de la propiedad beneficiario.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBeneficiario(String value) {
                this.beneficiario = value;
            }

            /**
             * Obtiene el valor de la propiedad fechaPago.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFechaPago() {
                return fechaPago;
            }

            /**
             * Define el valor de la propiedad fechaPago.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFechaPago(String value) {
                this.fechaPago = value;
            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="datoAdicional" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="numDato" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
     *                   &lt;element name="valorDato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "datoAdicional"
    })
    public static class DatosAdicionales {

        @XmlElement(nillable = true)
        protected List<Documento.DatosAdicionales.DatoAdicional> datoAdicional;

        /**
         * Gets the value of the datoAdicional property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the datoAdicional property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDatoAdicional().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.DatosAdicionales.DatoAdicional }
         * 
         * 
         */
        public List<Documento.DatosAdicionales.DatoAdicional> getDatoAdicional() {
            if (datoAdicional == null) {
                datoAdicional = new ArrayList<Documento.DatosAdicionales.DatoAdicional>();
            }
            return this.datoAdicional;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="numDato" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
         *         &lt;element name="valorDato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "numDato",
            "valorDato"
        })
        public static class DatoAdicional {

            @XmlSchemaType(name = "unsignedInt")
            protected long numDato;
            protected String valorDato;

            /**
             * Obtiene el valor de la propiedad numDato.
             * 
             */
            public long getNumDato() {
                return numDato;
            }

            /**
             * Define el valor de la propiedad numDato.
             * 
             */
            public void setNumDato(long value) {
                this.numDato = value;
            }

            /**
             * Obtiene el valor de la propiedad valorDato.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValorDato() {
                return valorDato;
            }

            /**
             * Define el valor de la propiedad valorDato.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValorDato(String value) {
                this.valorDato = value;
            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="subtotal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="porcDescuentoFinal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="descuentoFinal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="totalBase" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="totalImpuestos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="totalGastos" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="totalDocumento" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="totalRetenciones" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="totalAnticipos" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *         &lt;element name="aPagar" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "subtotal",
        "porcDescuentoFinal",
        "descuentoFinal",
        "totalBase",
        "totalImpuestos",
        "totalGastos",
        "totalDocumento",
        "totalRetenciones",
        "totalAnticipos",
        "aPagar"
    })
    public static class DatosTotales {

        @XmlElement(required = true)
        protected BigDecimal subtotal;
        protected BigDecimal porcDescuentoFinal;
        protected BigDecimal descuentoFinal;
        @XmlElement(required = true)
        protected BigDecimal totalBase;
        @XmlElement(required = true)
        protected BigDecimal totalImpuestos;
        protected BigDecimal totalGastos;
        @XmlElement(required = true)
        protected BigDecimal totalDocumento;
        protected BigDecimal totalRetenciones;
        protected BigDecimal totalAnticipos;
        @XmlElement(required = true)
        protected BigDecimal aPagar;

        /**
         * Obtiene el valor de la propiedad subtotal.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getSubtotal() {
            return subtotal;
        }

        /**
         * Define el valor de la propiedad subtotal.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setSubtotal(BigDecimal value) {
            this.subtotal = value;
        }

        /**
         * Obtiene el valor de la propiedad porcDescuentoFinal.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getPorcDescuentoFinal() {
            return porcDescuentoFinal;
        }

        /**
         * Define el valor de la propiedad porcDescuentoFinal.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setPorcDescuentoFinal(BigDecimal value) {
            this.porcDescuentoFinal = value;
        }

        /**
         * Obtiene el valor de la propiedad descuentoFinal.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getDescuentoFinal() {
            return descuentoFinal;
        }

        /**
         * Define el valor de la propiedad descuentoFinal.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setDescuentoFinal(BigDecimal value) {
            this.descuentoFinal = value;
        }

        /**
         * Obtiene el valor de la propiedad totalBase.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTotalBase() {
            return totalBase;
        }

        /**
         * Define el valor de la propiedad totalBase.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTotalBase(BigDecimal value) {
            this.totalBase = value;
        }

        /**
         * Obtiene el valor de la propiedad totalImpuestos.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTotalImpuestos() {
            return totalImpuestos;
        }

        /**
         * Define el valor de la propiedad totalImpuestos.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTotalImpuestos(BigDecimal value) {
            this.totalImpuestos = value;
        }

        /**
         * Obtiene el valor de la propiedad totalGastos.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTotalGastos() {
            return totalGastos;
        }

        /**
         * Define el valor de la propiedad totalGastos.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTotalGastos(BigDecimal value) {
            this.totalGastos = value;
        }

        /**
         * Obtiene el valor de la propiedad totalDocumento.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTotalDocumento() {
            return totalDocumento;
        }

        /**
         * Define el valor de la propiedad totalDocumento.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTotalDocumento(BigDecimal value) {
            this.totalDocumento = value;
        }

        /**
         * Obtiene el valor de la propiedad totalRetenciones.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTotalRetenciones() {
            return totalRetenciones;
        }

        /**
         * Define el valor de la propiedad totalRetenciones.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTotalRetenciones(BigDecimal value) {
            this.totalRetenciones = value;
        }

        /**
         * Obtiene el valor de la propiedad totalAnticipos.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTotalAnticipos() {
            return totalAnticipos;
        }

        /**
         * Define el valor de la propiedad totalAnticipos.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTotalAnticipos(BigDecimal value) {
            this.totalAnticipos = value;
        }

        /**
         * Obtiene el valor de la propiedad aPagar.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getAPagar() {
            return aPagar;
        }

        /**
         * Define el valor de la propiedad aPagar.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setAPagar(BigDecimal value) {
            this.aPagar = value;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="documentoAdjunto" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="nombreFichero" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="contenidoFichero" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="indPdfPrincipal" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "documentoAdjunto"
    })
    public static class DocumentosAdjuntos {

        @XmlElement(required = true)
        protected List<Documento.DocumentosAdjuntos.DocumentoAdjunto> documentoAdjunto;

        /**
         * Gets the value of the documentoAdjunto property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the documentoAdjunto property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDocumentoAdjunto().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.DocumentosAdjuntos.DocumentoAdjunto }
         * 
         * 
         */
        public List<Documento.DocumentosAdjuntos.DocumentoAdjunto> getDocumentoAdjunto() {
            if (documentoAdjunto == null) {
                documentoAdjunto = new ArrayList<Documento.DocumentosAdjuntos.DocumentoAdjunto>();
            }
            return this.documentoAdjunto;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="nombreFichero" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="contenidoFichero" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="indPdfPrincipal" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "nombreFichero",
            "contenidoFichero",
            "indPdfPrincipal"
        })
        public static class DocumentoAdjunto {

            @XmlElement(required = true)
            protected String nombreFichero;
            @XmlElement(required = true)
            protected String contenidoFichero;
            @XmlElement(required = true)
            protected String indPdfPrincipal;

            /**
             * Obtiene el valor de la propiedad nombreFichero.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNombreFichero() {
                return nombreFichero;
            }

            /**
             * Define el valor de la propiedad nombreFichero.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNombreFichero(String value) {
                this.nombreFichero = value;
            }

            /**
             * Obtiene el valor de la propiedad contenidoFichero.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getContenidoFichero() {
                return contenidoFichero;
            }

            /**
             * Define el valor de la propiedad contenidoFichero.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setContenidoFichero(String value) {
                this.contenidoFichero = value;
            }

            /**
             * Obtiene el valor de la propiedad indPdfPrincipal.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIndPdfPrincipal() {
                return indPdfPrincipal;
            }

            /**
             * Define el valor de la propiedad indPdfPrincipal.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIndPdfPrincipal(String value) {
                this.indPdfPrincipal = value;
            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="documentoReferenciado" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="numDocumentoRef" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="uuidDocumentoRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="fechaDocumentoRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "documentoReferenciado"
    })
    public static class DocumentosReferenciados {

        @XmlElement(required = true)
        protected List<Documento.DocumentosReferenciados.DocumentoReferenciado> documentoReferenciado;

        /**
         * Gets the value of the documentoReferenciado property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the documentoReferenciado property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDocumentoReferenciado().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.DocumentosReferenciados.DocumentoReferenciado }
         * 
         * 
         */
        public List<Documento.DocumentosReferenciados.DocumentoReferenciado> getDocumentoReferenciado() {
            if (documentoReferenciado == null) {
                documentoReferenciado = new ArrayList<Documento.DocumentosReferenciados.DocumentoReferenciado>();
            }
            return this.documentoReferenciado;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="numDocumentoRef" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="uuidDocumentoRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="fechaDocumentoRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "numDocumentoRef",
            "uuidDocumentoRef",
            "fechaDocumentoRef"
        })
        public static class DocumentoReferenciado {

            @XmlElement(required = true)
            protected String numDocumentoRef;
            protected String uuidDocumentoRef;
            protected String fechaDocumentoRef;

            /**
             * Obtiene el valor de la propiedad numDocumentoRef.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNumDocumentoRef() {
                return numDocumentoRef;
            }

            /**
             * Define el valor de la propiedad numDocumentoRef.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNumDocumentoRef(String value) {
                this.numDocumentoRef = value;
            }

            /**
             * Obtiene el valor de la propiedad uuidDocumentoRef.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUuidDocumentoRef() {
                return uuidDocumentoRef;
            }

            /**
             * Define el valor de la propiedad uuidDocumentoRef.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUuidDocumentoRef(String value) {
                this.uuidDocumentoRef = value;
            }

            /**
             * Obtiene el valor de la propiedad fechaDocumentoRef.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFechaDocumentoRef() {
                return fechaDocumentoRef;
            }

            /**
             * Define el valor de la propiedad fechaDocumentoRef.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFechaDocumentoRef(String value) {
                this.fechaDocumentoRef = value;
            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "email"
    })
    public static class EmailsEnvio {

        @XmlElement(required = true)
        protected List<String> email;

        /**
         * Gets the value of the email property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the email property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEmail().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getEmail() {
            if (email == null) {
                email = new ArrayList<String>();
            }
            return this.email;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="consecutivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="fechaCumplimiento" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="cantidadNominal" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="precioRegistro" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="montoRendimientos" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="porcentajeComision" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "consecutivo",
        "fechaCumplimiento",
        "cantidadNominal",
        "precioRegistro",
        "montoRendimientos",
        "valor",
        "porcentajeComision"
    })
    public static class ExtensionBolsa {

        @XmlElement(required = true)
        protected String consecutivo;
        @XmlElement(required = true)
        protected String fechaCumplimiento;
        @XmlElement(required = true)
        protected BigDecimal cantidadNominal;
        @XmlElement(required = true)
        protected BigDecimal precioRegistro;
        @XmlElement(required = true)
        protected BigDecimal montoRendimientos;
        @XmlElement(required = true)
        protected BigDecimal valor;
        @XmlElement(required = true)
        protected BigDecimal porcentajeComision;

        /**
         * Obtiene el valor de la propiedad consecutivo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getConsecutivo() {
            return consecutivo;
        }

        /**
         * Define el valor de la propiedad consecutivo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setConsecutivo(String value) {
            this.consecutivo = value;
        }

        /**
         * Obtiene el valor de la propiedad fechaCumplimiento.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFechaCumplimiento() {
            return fechaCumplimiento;
        }

        /**
         * Define el valor de la propiedad fechaCumplimiento.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFechaCumplimiento(String value) {
            this.fechaCumplimiento = value;
        }

        /**
         * Obtiene el valor de la propiedad cantidadNominal.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getCantidadNominal() {
            return cantidadNominal;
        }

        /**
         * Define el valor de la propiedad cantidadNominal.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setCantidadNominal(BigDecimal value) {
            this.cantidadNominal = value;
        }

        /**
         * Obtiene el valor de la propiedad precioRegistro.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getPrecioRegistro() {
            return precioRegistro;
        }

        /**
         * Define el valor de la propiedad precioRegistro.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setPrecioRegistro(BigDecimal value) {
            this.precioRegistro = value;
        }

        /**
         * Obtiene el valor de la propiedad montoRendimientos.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getMontoRendimientos() {
            return montoRendimientos;
        }

        /**
         * Define el valor de la propiedad montoRendimientos.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setMontoRendimientos(BigDecimal value) {
            this.montoRendimientos = value;
        }

        /**
         * Obtiene el valor de la propiedad valor.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getValor() {
            return valor;
        }

        /**
         * Define el valor de la propiedad valor.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setValor(BigDecimal value) {
            this.valor = value;
        }

        /**
         * Obtiene el valor de la propiedad porcentajeComision.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getPorcentajeComision() {
            return porcentajeComision;
        }

        /**
         * Define el valor de la propiedad porcentajeComision.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setPorcentajeComision(BigDecimal value) {
            this.porcentajeComision = value;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="beneficiosComprador" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="codigoComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="nombreComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="puntosAcumuladosComprador" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="puntoDeVenta" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="placaCaja" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="ubicacionAlmacen" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="nombreVendedor" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="tipoCaja" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="codigoVenta" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="subtotalVenta" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "beneficiosComprador",
        "puntoDeVenta"
    })
    public static class ExtensionPOS {

        protected Documento.ExtensionPOS.BeneficiosComprador beneficiosComprador;
        protected Documento.ExtensionPOS.PuntoDeVenta puntoDeVenta;

        /**
         * Obtiene el valor de la propiedad beneficiosComprador.
         * 
         * @return
         *     possible object is
         *     {@link Documento.ExtensionPOS.BeneficiosComprador }
         *     
         */
        public Documento.ExtensionPOS.BeneficiosComprador getBeneficiosComprador() {
            return beneficiosComprador;
        }

        /**
         * Define el valor de la propiedad beneficiosComprador.
         * 
         * @param value
         *     allowed object is
         *     {@link Documento.ExtensionPOS.BeneficiosComprador }
         *     
         */
        public void setBeneficiosComprador(Documento.ExtensionPOS.BeneficiosComprador value) {
            this.beneficiosComprador = value;
        }

        /**
         * Obtiene el valor de la propiedad puntoDeVenta.
         * 
         * @return
         *     possible object is
         *     {@link Documento.ExtensionPOS.PuntoDeVenta }
         *     
         */
        public Documento.ExtensionPOS.PuntoDeVenta getPuntoDeVenta() {
            return puntoDeVenta;
        }

        /**
         * Define el valor de la propiedad puntoDeVenta.
         * 
         * @param value
         *     allowed object is
         *     {@link Documento.ExtensionPOS.PuntoDeVenta }
         *     
         */
        public void setPuntoDeVenta(Documento.ExtensionPOS.PuntoDeVenta value) {
            this.puntoDeVenta = value;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="codigoComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="nombreComprador" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="puntosAcumuladosComprador" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "codigoComprador",
            "nombreComprador",
            "puntosAcumuladosComprador"
        })
        public static class BeneficiosComprador {

            @XmlElement(required = true)
            protected String codigoComprador;
            @XmlElement(required = true)
            protected String nombreComprador;
            @XmlElement(required = true)
            protected BigDecimal puntosAcumuladosComprador;

            /**
             * Obtiene el valor de la propiedad codigoComprador.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodigoComprador() {
                return codigoComprador;
            }

            /**
             * Define el valor de la propiedad codigoComprador.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodigoComprador(String value) {
                this.codigoComprador = value;
            }

            /**
             * Obtiene el valor de la propiedad nombreComprador.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNombreComprador() {
                return nombreComprador;
            }

            /**
             * Define el valor de la propiedad nombreComprador.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNombreComprador(String value) {
                this.nombreComprador = value;
            }

            /**
             * Obtiene el valor de la propiedad puntosAcumuladosComprador.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPuntosAcumuladosComprador() {
                return puntosAcumuladosComprador;
            }

            /**
             * Define el valor de la propiedad puntosAcumuladosComprador.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPuntosAcumuladosComprador(BigDecimal value) {
                this.puntosAcumuladosComprador = value;
            }

        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="placaCaja" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="ubicacionAlmacen" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="nombreVendedor" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="tipoCaja" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="codigoVenta" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="subtotalVenta" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "placaCaja",
            "ubicacionAlmacen",
            "nombreVendedor",
            "tipoCaja",
            "codigoVenta",
            "subtotalVenta"
        })
        public static class PuntoDeVenta {

            @XmlElement(required = true)
            protected String placaCaja;
            @XmlElement(required = true)
            protected String ubicacionAlmacen;
            @XmlElement(required = true)
            protected String nombreVendedor;
            @XmlElement(required = true)
            protected String tipoCaja;
            @XmlElement(required = true)
            protected String codigoVenta;
            @XmlElement(required = true)
            protected BigDecimal subtotalVenta;

            /**
             * Obtiene el valor de la propiedad placaCaja.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPlacaCaja() {
                return placaCaja;
            }

            /**
             * Define el valor de la propiedad placaCaja.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPlacaCaja(String value) {
                this.placaCaja = value;
            }

            /**
             * Obtiene el valor de la propiedad ubicacionAlmacen.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUbicacionAlmacen() {
                return ubicacionAlmacen;
            }

            /**
             * Define el valor de la propiedad ubicacionAlmacen.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUbicacionAlmacen(String value) {
                this.ubicacionAlmacen = value;
            }

            /**
             * Obtiene el valor de la propiedad nombreVendedor.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNombreVendedor() {
                return nombreVendedor;
            }

            /**
             * Define el valor de la propiedad nombreVendedor.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNombreVendedor(String value) {
                this.nombreVendedor = value;
            }

            /**
             * Obtiene el valor de la propiedad tipoCaja.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTipoCaja() {
                return tipoCaja;
            }

            /**
             * Define el valor de la propiedad tipoCaja.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTipoCaja(String value) {
                this.tipoCaja = value;
            }

            /**
             * Obtiene el valor de la propiedad codigoVenta.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodigoVenta() {
                return codigoVenta;
            }

            /**
             * Define el valor de la propiedad codigoVenta.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodigoVenta(String value) {
                this.codigoVenta = value;
            }

            /**
             * Obtiene el valor de la propiedad subtotalVenta.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSubtotalVenta() {
                return subtotalVenta;
            }

            /**
             * Define el valor de la propiedad subtotalVenta.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSubtotalVenta(BigDecimal value) {
                this.subtotalVenta = value;
            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="numLinea" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         &lt;element name="servicioFacturado" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="empresa" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="motivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="numeroContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="fechaContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="informacionContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="subscriptor" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="direccionPostal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="direccionEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="ciudad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="departamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="pais" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="tipoEstrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="valorFacturado" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ciclo" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
     *                   &lt;element name="tipoPeriodicidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="infoAdicional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="producto" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="totalUnidades" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="unidadMedidaTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="consumoTotal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="unidadMedidaConsumida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="valorConsumoParcial" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="valorUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="cantidadAplicaPrecio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="unidadMedida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="descuentos" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="razonDescuento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="valorDto" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="cargos" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="razonCargo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="valorCargo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="lecturaContador" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="datosMedidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="fechaLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="unidadesLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="unidadMedidaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="fechaLecturaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="unidadesLecturaActual" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="unidadMedidaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="metodoLectura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="duracionServicio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="unidadMedidaServicio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="historicoConsumos" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="pagoAnterior" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="consumoMensual" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="diasFacturados" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="unidadMedidaDiasFacturados" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="valorAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="divisa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="consumoPromedio" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="mediaConsumo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="unidadMedidaMediaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="historicoResiduos" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="residuos" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="unidadMedidaTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="totalNoAprovechable" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="totalLimpiezaBarrido" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="totalLimpiezaUrbana" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                       &lt;element name="totalInutil" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="acuerdosPago" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="numeroContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="nombreProducto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="descripcionProducto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                             &lt;element name="cuotasAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="cuotasPagadas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="porcentajeInteres" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="saldoAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="cuotasACancelar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="valorCuotaPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="totalCargos" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                             &lt;element name="totalDescuento" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "numLinea",
        "servicioFacturado",
        "empresa",
        "motivo",
        "numeroContrato",
        "fechaContrato",
        "informacionContrato",
        "subscriptor",
        "valorFacturado"
    })
    public static class ExtensionSPD {

        @XmlElement(required = true)
        protected BigInteger numLinea;
        @XmlElement(required = true)
        protected String servicioFacturado;
        @XmlElement(required = true)
        protected String empresa;
        @XmlElement(required = true)
        protected String motivo;
        protected String numeroContrato;
        protected String fechaContrato;
        protected String informacionContrato;
        protected List<Documento.ExtensionSPD.Subscriptor> subscriptor;
        protected List<Documento.ExtensionSPD.ValorFacturado> valorFacturado;

        /**
         * Obtiene el valor de la propiedad numLinea.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getNumLinea() {
            return numLinea;
        }

        /**
         * Define el valor de la propiedad numLinea.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setNumLinea(BigInteger value) {
            this.numLinea = value;
        }

        /**
         * Obtiene el valor de la propiedad servicioFacturado.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getServicioFacturado() {
            return servicioFacturado;
        }

        /**
         * Define el valor de la propiedad servicioFacturado.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setServicioFacturado(String value) {
            this.servicioFacturado = value;
        }

        /**
         * Obtiene el valor de la propiedad empresa.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEmpresa() {
            return empresa;
        }

        /**
         * Define el valor de la propiedad empresa.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEmpresa(String value) {
            this.empresa = value;
        }

        /**
         * Obtiene el valor de la propiedad motivo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMotivo() {
            return motivo;
        }

        /**
         * Define el valor de la propiedad motivo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMotivo(String value) {
            this.motivo = value;
        }

        /**
         * Obtiene el valor de la propiedad numeroContrato.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNumeroContrato() {
            return numeroContrato;
        }

        /**
         * Define el valor de la propiedad numeroContrato.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNumeroContrato(String value) {
            this.numeroContrato = value;
        }

        /**
         * Obtiene el valor de la propiedad fechaContrato.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFechaContrato() {
            return fechaContrato;
        }

        /**
         * Define el valor de la propiedad fechaContrato.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFechaContrato(String value) {
            this.fechaContrato = value;
        }

        /**
         * Obtiene el valor de la propiedad informacionContrato.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInformacionContrato() {
            return informacionContrato;
        }

        /**
         * Define el valor de la propiedad informacionContrato.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInformacionContrato(String value) {
            this.informacionContrato = value;
        }

        /**
         * Gets the value of the subscriptor property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the subscriptor property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSubscriptor().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.ExtensionSPD.Subscriptor }
         * 
         * 
         */
        public List<Documento.ExtensionSPD.Subscriptor> getSubscriptor() {
            if (subscriptor == null) {
                subscriptor = new ArrayList<Documento.ExtensionSPD.Subscriptor>();
            }
            return this.subscriptor;
        }

        /**
         * Gets the value of the valorFacturado property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the valorFacturado property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getValorFacturado().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.ExtensionSPD.ValorFacturado }
         * 
         * 
         */
        public List<Documento.ExtensionSPD.ValorFacturado> getValorFacturado() {
            if (valorFacturado == null) {
                valorFacturado = new ArrayList<Documento.ExtensionSPD.ValorFacturado>();
            }
            return this.valorFacturado;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="direccionPostal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="direccionEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="ciudad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="departamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="pais" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="tipoEstrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "nombre",
            "direccionPostal",
            "direccionEntrega",
            "ciudad",
            "departamento",
            "pais",
            "tipoEstrato",
            "email"
        })
        public static class Subscriptor {

            protected String nombre;
            protected String direccionPostal;
            protected String direccionEntrega;
            protected String ciudad;
            protected String departamento;
            protected String pais;
            protected String tipoEstrato;
            protected String email;

            /**
             * Obtiene el valor de la propiedad nombre.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNombre() {
                return nombre;
            }

            /**
             * Define el valor de la propiedad nombre.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNombre(String value) {
                this.nombre = value;
            }

            /**
             * Obtiene el valor de la propiedad direccionPostal.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDireccionPostal() {
                return direccionPostal;
            }

            /**
             * Define el valor de la propiedad direccionPostal.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDireccionPostal(String value) {
                this.direccionPostal = value;
            }

            /**
             * Obtiene el valor de la propiedad direccionEntrega.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDireccionEntrega() {
                return direccionEntrega;
            }

            /**
             * Define el valor de la propiedad direccionEntrega.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDireccionEntrega(String value) {
                this.direccionEntrega = value;
            }

            /**
             * Obtiene el valor de la propiedad ciudad.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCiudad() {
                return ciudad;
            }

            /**
             * Define el valor de la propiedad ciudad.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCiudad(String value) {
                this.ciudad = value;
            }

            /**
             * Obtiene el valor de la propiedad departamento.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDepartamento() {
                return departamento;
            }

            /**
             * Define el valor de la propiedad departamento.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDepartamento(String value) {
                this.departamento = value;
            }

            /**
             * Obtiene el valor de la propiedad pais.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPais() {
                return pais;
            }

            /**
             * Define el valor de la propiedad pais.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPais(String value) {
                this.pais = value;
            }

            /**
             * Obtiene el valor de la propiedad tipoEstrato.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTipoEstrato() {
                return tipoEstrato;
            }

            /**
             * Define el valor de la propiedad tipoEstrato.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTipoEstrato(String value) {
                this.tipoEstrato = value;
            }

            /**
             * Obtiene el valor de la propiedad email.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEmail() {
                return email;
            }

            /**
             * Define el valor de la propiedad email.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEmail(String value) {
                this.email = value;
            }

        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="ciclo" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
         *         &lt;element name="tipoPeriodicidad" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="infoAdicional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="producto" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="totalUnidades" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="unidadMedidaTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="consumoTotal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="unidadMedidaConsumida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="valorConsumoParcial" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="valorUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="cantidadAplicaPrecio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="unidadMedida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="descuentos" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="razonDescuento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="valorDto" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="cargos" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="razonCargo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="valorCargo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="lecturaContador" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="datosMedidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="fechaLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="unidadesLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="unidadMedidaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="fechaLecturaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="unidadesLecturaActual" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="unidadMedidaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="metodoLectura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="duracionServicio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="unidadMedidaServicio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="historicoConsumos" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="pagoAnterior" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="consumoMensual" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="diasFacturados" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="unidadMedidaDiasFacturados" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="valorAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="divisa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="consumoPromedio" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="mediaConsumo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="unidadMedidaMediaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="historicoResiduos" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="residuos" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="unidadMedidaTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="totalNoAprovechable" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="totalLimpiezaBarrido" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="totalLimpiezaUrbana" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                             &lt;element name="totalInutil" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="acuerdosPago" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="numeroContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="nombreProducto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="descripcionProducto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                   &lt;element name="cuotasAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="cuotasPagadas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="porcentajeInteres" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="saldoAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="cuotasACancelar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="valorCuotaPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="totalCargos" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                   &lt;element name="totalDescuento" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "ciclo",
            "tipoPeriodicidad",
            "infoAdicional",
            "producto",
            "historicoConsumos",
            "historicoResiduos",
            "acuerdosPago"
        })
        public static class ValorFacturado {

            protected BigInteger ciclo;
            protected String tipoPeriodicidad;
            protected String infoAdicional;
            protected List<Documento.ExtensionSPD.ValorFacturado.Producto> producto;
            protected Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos historicoConsumos;
            protected Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos historicoResiduos;
            protected List<Documento.ExtensionSPD.ValorFacturado.AcuerdosPago> acuerdosPago;

            /**
             * Obtiene el valor de la propiedad ciclo.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getCiclo() {
                return ciclo;
            }

            /**
             * Define el valor de la propiedad ciclo.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setCiclo(BigInteger value) {
                this.ciclo = value;
            }

            /**
             * Obtiene el valor de la propiedad tipoPeriodicidad.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTipoPeriodicidad() {
                return tipoPeriodicidad;
            }

            /**
             * Define el valor de la propiedad tipoPeriodicidad.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTipoPeriodicidad(String value) {
                this.tipoPeriodicidad = value;
            }

            /**
             * Obtiene el valor de la propiedad infoAdicional.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInfoAdicional() {
                return infoAdicional;
            }

            /**
             * Define el valor de la propiedad infoAdicional.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInfoAdicional(String value) {
                this.infoAdicional = value;
            }

            /**
             * Gets the value of the producto property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the producto property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getProducto().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Documento.ExtensionSPD.ValorFacturado.Producto }
             * 
             * 
             */
            public List<Documento.ExtensionSPD.ValorFacturado.Producto> getProducto() {
                if (producto == null) {
                    producto = new ArrayList<Documento.ExtensionSPD.ValorFacturado.Producto>();
                }
                return this.producto;
            }

            /**
             * Obtiene el valor de la propiedad historicoConsumos.
             * 
             * @return
             *     possible object is
             *     {@link Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos }
             *     
             */
            public Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos getHistoricoConsumos() {
                return historicoConsumos;
            }

            /**
             * Define el valor de la propiedad historicoConsumos.
             * 
             * @param value
             *     allowed object is
             *     {@link Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos }
             *     
             */
            public void setHistoricoConsumos(Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos value) {
                this.historicoConsumos = value;
            }

            /**
             * Obtiene el valor de la propiedad historicoResiduos.
             * 
             * @return
             *     possible object is
             *     {@link Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos }
             *     
             */
            public Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos getHistoricoResiduos() {
                return historicoResiduos;
            }

            /**
             * Define el valor de la propiedad historicoResiduos.
             * 
             * @param value
             *     allowed object is
             *     {@link Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos }
             *     
             */
            public void setHistoricoResiduos(Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos value) {
                this.historicoResiduos = value;
            }

            /**
             * Gets the value of the acuerdosPago property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the acuerdosPago property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAcuerdosPago().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Documento.ExtensionSPD.ValorFacturado.AcuerdosPago }
             * 
             * 
             */
            public List<Documento.ExtensionSPD.ValorFacturado.AcuerdosPago> getAcuerdosPago() {
                if (acuerdosPago == null) {
                    acuerdosPago = new ArrayList<Documento.ExtensionSPD.ValorFacturado.AcuerdosPago>();
                }
                return this.acuerdosPago;
            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="numeroContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="nombreProducto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="descripcionProducto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="cuotasAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="cuotasPagadas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="porcentajeInteres" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="saldoAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="cuotasACancelar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="valorCuotaPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="totalCargos" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="totalDescuento" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "numeroContrato",
                "nombreProducto",
                "descripcionProducto",
                "cuotasAPagar",
                "cuotasPagadas",
                "porcentajeInteres",
                "saldoAPagar",
                "cuotasACancelar",
                "valorCuotaPagar",
                "totalCargos",
                "totalDescuento"
            })
            public static class AcuerdosPago {

                protected String numeroContrato;
                protected String nombreProducto;
                protected String descripcionProducto;
                protected BigDecimal cuotasAPagar;
                protected BigDecimal cuotasPagadas;
                protected BigDecimal porcentajeInteres;
                protected BigDecimal saldoAPagar;
                protected BigDecimal cuotasACancelar;
                protected BigDecimal valorCuotaPagar;
                protected BigDecimal totalCargos;
                protected BigDecimal totalDescuento;

                /**
                 * Obtiene el valor de la propiedad numeroContrato.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getNumeroContrato() {
                    return numeroContrato;
                }

                /**
                 * Define el valor de la propiedad numeroContrato.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setNumeroContrato(String value) {
                    this.numeroContrato = value;
                }

                /**
                 * Obtiene el valor de la propiedad nombreProducto.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getNombreProducto() {
                    return nombreProducto;
                }

                /**
                 * Define el valor de la propiedad nombreProducto.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setNombreProducto(String value) {
                    this.nombreProducto = value;
                }

                /**
                 * Obtiene el valor de la propiedad descripcionProducto.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getDescripcionProducto() {
                    return descripcionProducto;
                }

                /**
                 * Define el valor de la propiedad descripcionProducto.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setDescripcionProducto(String value) {
                    this.descripcionProducto = value;
                }

                /**
                 * Obtiene el valor de la propiedad cuotasAPagar.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getCuotasAPagar() {
                    return cuotasAPagar;
                }

                /**
                 * Define el valor de la propiedad cuotasAPagar.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setCuotasAPagar(BigDecimal value) {
                    this.cuotasAPagar = value;
                }

                /**
                 * Obtiene el valor de la propiedad cuotasPagadas.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getCuotasPagadas() {
                    return cuotasPagadas;
                }

                /**
                 * Define el valor de la propiedad cuotasPagadas.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setCuotasPagadas(BigDecimal value) {
                    this.cuotasPagadas = value;
                }

                /**
                 * Obtiene el valor de la propiedad porcentajeInteres.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getPorcentajeInteres() {
                    return porcentajeInteres;
                }

                /**
                 * Define el valor de la propiedad porcentajeInteres.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setPorcentajeInteres(BigDecimal value) {
                    this.porcentajeInteres = value;
                }

                /**
                 * Obtiene el valor de la propiedad saldoAPagar.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getSaldoAPagar() {
                    return saldoAPagar;
                }

                /**
                 * Define el valor de la propiedad saldoAPagar.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setSaldoAPagar(BigDecimal value) {
                    this.saldoAPagar = value;
                }

                /**
                 * Obtiene el valor de la propiedad cuotasACancelar.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getCuotasACancelar() {
                    return cuotasACancelar;
                }

                /**
                 * Define el valor de la propiedad cuotasACancelar.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setCuotasACancelar(BigDecimal value) {
                    this.cuotasACancelar = value;
                }

                /**
                 * Obtiene el valor de la propiedad valorCuotaPagar.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getValorCuotaPagar() {
                    return valorCuotaPagar;
                }

                /**
                 * Define el valor de la propiedad valorCuotaPagar.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setValorCuotaPagar(BigDecimal value) {
                    this.valorCuotaPagar = value;
                }

                /**
                 * Obtiene el valor de la propiedad totalCargos.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getTotalCargos() {
                    return totalCargos;
                }

                /**
                 * Define el valor de la propiedad totalCargos.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setTotalCargos(BigDecimal value) {
                    this.totalCargos = value;
                }

                /**
                 * Obtiene el valor de la propiedad totalDescuento.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getTotalDescuento() {
                    return totalDescuento;
                }

                /**
                 * Define el valor de la propiedad totalDescuento.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setTotalDescuento(BigDecimal value) {
                    this.totalDescuento = value;
                }

            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="pagoAnterior" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="consumoMensual" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="diasFacturados" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="unidadMedidaDiasFacturados" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="valorAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="divisa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="consumoPromedio" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="mediaConsumo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="unidadMedidaMediaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "pagoAnterior",
                "consumoMensual",
                "consumoPromedio"
            })
            public static class HistoricoConsumos {

                protected BigDecimal pagoAnterior;
                protected List<Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoMensual> consumoMensual;
                protected List<Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoPromedio> consumoPromedio;

                /**
                 * Obtiene el valor de la propiedad pagoAnterior.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getPagoAnterior() {
                    return pagoAnterior;
                }

                /**
                 * Define el valor de la propiedad pagoAnterior.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setPagoAnterior(BigDecimal value) {
                    this.pagoAnterior = value;
                }

                /**
                 * Gets the value of the consumoMensual property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the consumoMensual property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getConsumoMensual().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoMensual }
                 * 
                 * 
                 */
                public List<Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoMensual> getConsumoMensual() {
                    if (consumoMensual == null) {
                        consumoMensual = new ArrayList<Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoMensual>();
                    }
                    return this.consumoMensual;
                }

                /**
                 * Gets the value of the consumoPromedio property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the consumoPromedio property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getConsumoPromedio().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoPromedio }
                 * 
                 * 
                 */
                public List<Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoPromedio> getConsumoPromedio() {
                    if (consumoPromedio == null) {
                        consumoPromedio = new ArrayList<Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoPromedio>();
                    }
                    return this.consumoPromedio;
                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="diasFacturados" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="unidadMedidaDiasFacturados" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="valorAPagar" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="divisa" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *       &lt;/sequence>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "unidadesConsumidas",
                    "unidadMedidaConsumo",
                    "fechaInicioPeriodo",
                    "fechaFinPeriodo",
                    "diasFacturados",
                    "unidadMedidaDiasFacturados",
                    "valorAPagar",
                    "divisa"
                })
                public static class ConsumoMensual {

                    protected BigDecimal unidadesConsumidas;
                    protected String unidadMedidaConsumo;
                    protected String fechaInicioPeriodo;
                    protected String fechaFinPeriodo;
                    protected BigDecimal diasFacturados;
                    protected String unidadMedidaDiasFacturados;
                    protected BigDecimal valorAPagar;
                    protected String divisa;

                    /**
                     * Obtiene el valor de la propiedad unidadesConsumidas.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getUnidadesConsumidas() {
                        return unidadesConsumidas;
                    }

                    /**
                     * Define el valor de la propiedad unidadesConsumidas.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setUnidadesConsumidas(BigDecimal value) {
                        this.unidadesConsumidas = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadMedidaConsumo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getUnidadMedidaConsumo() {
                        return unidadMedidaConsumo;
                    }

                    /**
                     * Define el valor de la propiedad unidadMedidaConsumo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setUnidadMedidaConsumo(String value) {
                        this.unidadMedidaConsumo = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad fechaInicioPeriodo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getFechaInicioPeriodo() {
                        return fechaInicioPeriodo;
                    }

                    /**
                     * Define el valor de la propiedad fechaInicioPeriodo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setFechaInicioPeriodo(String value) {
                        this.fechaInicioPeriodo = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad fechaFinPeriodo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getFechaFinPeriodo() {
                        return fechaFinPeriodo;
                    }

                    /**
                     * Define el valor de la propiedad fechaFinPeriodo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setFechaFinPeriodo(String value) {
                        this.fechaFinPeriodo = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad diasFacturados.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getDiasFacturados() {
                        return diasFacturados;
                    }

                    /**
                     * Define el valor de la propiedad diasFacturados.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setDiasFacturados(BigDecimal value) {
                        this.diasFacturados = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadMedidaDiasFacturados.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getUnidadMedidaDiasFacturados() {
                        return unidadMedidaDiasFacturados;
                    }

                    /**
                     * Define el valor de la propiedad unidadMedidaDiasFacturados.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setUnidadMedidaDiasFacturados(String value) {
                        this.unidadMedidaDiasFacturados = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad valorAPagar.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getValorAPagar() {
                        return valorAPagar;
                    }

                    /**
                     * Define el valor de la propiedad valorAPagar.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setValorAPagar(BigDecimal value) {
                        this.valorAPagar = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad divisa.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getDivisa() {
                        return divisa;
                    }

                    /**
                     * Define el valor de la propiedad divisa.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setDivisa(String value) {
                        this.divisa = value;
                    }

                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="unidadMedidaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="mediaConsumo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="unidadMedidaMediaConsumo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *       &lt;/sequence>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "unidadesConsumidas",
                    "unidadMedidaConsumo",
                    "mediaConsumo",
                    "unidadMedidaMediaConsumo"
                })
                public static class ConsumoPromedio {

                    protected BigDecimal unidadesConsumidas;
                    protected String unidadMedidaConsumo;
                    protected BigDecimal mediaConsumo;
                    protected String unidadMedidaMediaConsumo;

                    /**
                     * Obtiene el valor de la propiedad unidadesConsumidas.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getUnidadesConsumidas() {
                        return unidadesConsumidas;
                    }

                    /**
                     * Define el valor de la propiedad unidadesConsumidas.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setUnidadesConsumidas(BigDecimal value) {
                        this.unidadesConsumidas = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadMedidaConsumo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getUnidadMedidaConsumo() {
                        return unidadMedidaConsumo;
                    }

                    /**
                     * Define el valor de la propiedad unidadMedidaConsumo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setUnidadMedidaConsumo(String value) {
                        this.unidadMedidaConsumo = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad mediaConsumo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getMediaConsumo() {
                        return mediaConsumo;
                    }

                    /**
                     * Define el valor de la propiedad mediaConsumo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setMediaConsumo(BigDecimal value) {
                        this.mediaConsumo = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadMedidaMediaConsumo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getUnidadMedidaMediaConsumo() {
                        return unidadMedidaMediaConsumo;
                    }

                    /**
                     * Define el valor de la propiedad unidadMedidaMediaConsumo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setUnidadMedidaMediaConsumo(String value) {
                        this.unidadMedidaMediaConsumo = value;
                    }

                }

            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="residuos" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="unidadMedidaTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="totalNoAprovechable" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="totalLimpiezaBarrido" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="totalLimpiezaUrbana" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="totalInutil" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "residuos"
            })
            public static class HistoricoResiduos {

                protected List<Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos.Residuos> residuos;

                /**
                 * Gets the value of the residuos property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the residuos property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getResiduos().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos.Residuos }
                 * 
                 * 
                 */
                public List<Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos.Residuos> getResiduos() {
                    if (residuos == null) {
                        residuos = new ArrayList<Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos.Residuos>();
                    }
                    return this.residuos;
                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="unidadMedidaTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="fechaInicioPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="fechaFinPeriodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="totalNoAprovechable" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="totalLimpiezaBarrido" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="totalLimpiezaUrbana" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="totalInutil" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *       &lt;/sequence>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "total",
                    "unidadMedidaTotal",
                    "fechaInicioPeriodo",
                    "fechaFinPeriodo",
                    "totalNoAprovechable",
                    "totalLimpiezaBarrido",
                    "totalLimpiezaUrbana",
                    "totalInutil"
                })
                public static class Residuos {

                    protected BigDecimal total;
                    protected String unidadMedidaTotal;
                    protected String fechaInicioPeriodo;
                    protected String fechaFinPeriodo;
                    protected BigDecimal totalNoAprovechable;
                    protected BigDecimal totalLimpiezaBarrido;
                    protected BigDecimal totalLimpiezaUrbana;
                    protected BigDecimal totalInutil;

                    /**
                     * Obtiene el valor de la propiedad total.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getTotal() {
                        return total;
                    }

                    /**
                     * Define el valor de la propiedad total.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setTotal(BigDecimal value) {
                        this.total = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadMedidaTotal.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getUnidadMedidaTotal() {
                        return unidadMedidaTotal;
                    }

                    /**
                     * Define el valor de la propiedad unidadMedidaTotal.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setUnidadMedidaTotal(String value) {
                        this.unidadMedidaTotal = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad fechaInicioPeriodo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getFechaInicioPeriodo() {
                        return fechaInicioPeriodo;
                    }

                    /**
                     * Define el valor de la propiedad fechaInicioPeriodo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setFechaInicioPeriodo(String value) {
                        this.fechaInicioPeriodo = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad fechaFinPeriodo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getFechaFinPeriodo() {
                        return fechaFinPeriodo;
                    }

                    /**
                     * Define el valor de la propiedad fechaFinPeriodo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setFechaFinPeriodo(String value) {
                        this.fechaFinPeriodo = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad totalNoAprovechable.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getTotalNoAprovechable() {
                        return totalNoAprovechable;
                    }

                    /**
                     * Define el valor de la propiedad totalNoAprovechable.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setTotalNoAprovechable(BigDecimal value) {
                        this.totalNoAprovechable = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad totalLimpiezaBarrido.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getTotalLimpiezaBarrido() {
                        return totalLimpiezaBarrido;
                    }

                    /**
                     * Define el valor de la propiedad totalLimpiezaBarrido.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setTotalLimpiezaBarrido(BigDecimal value) {
                        this.totalLimpiezaBarrido = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad totalLimpiezaUrbana.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getTotalLimpiezaUrbana() {
                        return totalLimpiezaUrbana;
                    }

                    /**
                     * Define el valor de la propiedad totalLimpiezaUrbana.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setTotalLimpiezaUrbana(BigDecimal value) {
                        this.totalLimpiezaUrbana = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad totalInutil.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getTotalInutil() {
                        return totalInutil;
                    }

                    /**
                     * Define el valor de la propiedad totalInutil.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setTotalInutil(BigDecimal value) {
                        this.totalInutil = value;
                    }

                }

            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="totalUnidades" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="unidadMedidaTotal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="consumoTotal" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="unidadesConsumidas" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="unidadMedidaConsumida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="valorConsumoParcial" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="valorUnitario" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="cantidadAplicaPrecio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *         &lt;element name="unidadMedida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *         &lt;element name="descuentos" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="razonDescuento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="valorDto" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="cargos" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="razonCargo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="valorCargo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="lecturaContador" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="datosMedidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="fechaLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="unidadesLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="unidadMedidaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="fechaLecturaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="unidadesLecturaActual" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="unidadMedidaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="metodoLectura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="duracionServicio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
             *                   &lt;element name="unidadMedidaServicio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "totalUnidades",
                "unidadMedidaTotal",
                "consumoTotal",
                "unidadesConsumidas",
                "unidadMedidaConsumida",
                "valorConsumoParcial",
                "valorUnitario",
                "cantidadAplicaPrecio",
                "unidadMedida",
                "descuentos",
                "cargos",
                "lecturaContador"
            })
            public static class Producto {

                protected BigDecimal totalUnidades;
                protected String unidadMedidaTotal;
                protected BigDecimal consumoTotal;
                protected BigDecimal unidadesConsumidas;
                protected String unidadMedidaConsumida;
                protected BigDecimal valorConsumoParcial;
                protected BigDecimal valorUnitario;
                protected BigDecimal cantidadAplicaPrecio;
                protected String unidadMedida;
                protected List<Documento.ExtensionSPD.ValorFacturado.Producto.Descuentos> descuentos;
                protected List<Documento.ExtensionSPD.ValorFacturado.Producto.Cargos> cargos;
                protected List<Documento.ExtensionSPD.ValorFacturado.Producto.LecturaContador> lecturaContador;

                /**
                 * Obtiene el valor de la propiedad totalUnidades.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getTotalUnidades() {
                    return totalUnidades;
                }

                /**
                 * Define el valor de la propiedad totalUnidades.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setTotalUnidades(BigDecimal value) {
                    this.totalUnidades = value;
                }

                /**
                 * Obtiene el valor de la propiedad unidadMedidaTotal.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getUnidadMedidaTotal() {
                    return unidadMedidaTotal;
                }

                /**
                 * Define el valor de la propiedad unidadMedidaTotal.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setUnidadMedidaTotal(String value) {
                    this.unidadMedidaTotal = value;
                }

                /**
                 * Obtiene el valor de la propiedad consumoTotal.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getConsumoTotal() {
                    return consumoTotal;
                }

                /**
                 * Define el valor de la propiedad consumoTotal.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setConsumoTotal(BigDecimal value) {
                    this.consumoTotal = value;
                }

                /**
                 * Obtiene el valor de la propiedad unidadesConsumidas.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getUnidadesConsumidas() {
                    return unidadesConsumidas;
                }

                /**
                 * Define el valor de la propiedad unidadesConsumidas.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setUnidadesConsumidas(BigDecimal value) {
                    this.unidadesConsumidas = value;
                }

                /**
                 * Obtiene el valor de la propiedad unidadMedidaConsumida.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getUnidadMedidaConsumida() {
                    return unidadMedidaConsumida;
                }

                /**
                 * Define el valor de la propiedad unidadMedidaConsumida.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setUnidadMedidaConsumida(String value) {
                    this.unidadMedidaConsumida = value;
                }

                /**
                 * Obtiene el valor de la propiedad valorConsumoParcial.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getValorConsumoParcial() {
                    return valorConsumoParcial;
                }

                /**
                 * Define el valor de la propiedad valorConsumoParcial.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setValorConsumoParcial(BigDecimal value) {
                    this.valorConsumoParcial = value;
                }

                /**
                 * Obtiene el valor de la propiedad valorUnitario.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getValorUnitario() {
                    return valorUnitario;
                }

                /**
                 * Define el valor de la propiedad valorUnitario.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setValorUnitario(BigDecimal value) {
                    this.valorUnitario = value;
                }

                /**
                 * Obtiene el valor de la propiedad cantidadAplicaPrecio.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getCantidadAplicaPrecio() {
                    return cantidadAplicaPrecio;
                }

                /**
                 * Define el valor de la propiedad cantidadAplicaPrecio.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setCantidadAplicaPrecio(BigDecimal value) {
                    this.cantidadAplicaPrecio = value;
                }

                /**
                 * Obtiene el valor de la propiedad unidadMedida.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getUnidadMedida() {
                    return unidadMedida;
                }

                /**
                 * Define el valor de la propiedad unidadMedida.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setUnidadMedida(String value) {
                    this.unidadMedida = value;
                }

                /**
                 * Gets the value of the descuentos property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the descuentos property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getDescuentos().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Documento.ExtensionSPD.ValorFacturado.Producto.Descuentos }
                 * 
                 * 
                 */
                public List<Documento.ExtensionSPD.ValorFacturado.Producto.Descuentos> getDescuentos() {
                    if (descuentos == null) {
                        descuentos = new ArrayList<Documento.ExtensionSPD.ValorFacturado.Producto.Descuentos>();
                    }
                    return this.descuentos;
                }

                /**
                 * Gets the value of the cargos property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the cargos property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getCargos().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Documento.ExtensionSPD.ValorFacturado.Producto.Cargos }
                 * 
                 * 
                 */
                public List<Documento.ExtensionSPD.ValorFacturado.Producto.Cargos> getCargos() {
                    if (cargos == null) {
                        cargos = new ArrayList<Documento.ExtensionSPD.ValorFacturado.Producto.Cargos>();
                    }
                    return this.cargos;
                }

                /**
                 * Gets the value of the lecturaContador property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the lecturaContador property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getLecturaContador().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Documento.ExtensionSPD.ValorFacturado.Producto.LecturaContador }
                 * 
                 * 
                 */
                public List<Documento.ExtensionSPD.ValorFacturado.Producto.LecturaContador> getLecturaContador() {
                    if (lecturaContador == null) {
                        lecturaContador = new ArrayList<Documento.ExtensionSPD.ValorFacturado.Producto.LecturaContador>();
                    }
                    return this.lecturaContador;
                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="razonCargo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="valorCargo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *       &lt;/sequence>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "razonCargo",
                    "valorCargo"
                })
                public static class Cargos {

                    protected String razonCargo;
                    protected BigDecimal valorCargo;

                    /**
                     * Obtiene el valor de la propiedad razonCargo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getRazonCargo() {
                        return razonCargo;
                    }

                    /**
                     * Define el valor de la propiedad razonCargo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setRazonCargo(String value) {
                        this.razonCargo = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad valorCargo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getValorCargo() {
                        return valorCargo;
                    }

                    /**
                     * Define el valor de la propiedad valorCargo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setValorCargo(BigDecimal value) {
                        this.valorCargo = value;
                    }

                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="razonDescuento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="valorDto" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *       &lt;/sequence>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "razonDescuento",
                    "valorDto"
                })
                public static class Descuentos {

                    protected String razonDescuento;
                    protected BigDecimal valorDto;

                    /**
                     * Obtiene el valor de la propiedad razonDescuento.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getRazonDescuento() {
                        return razonDescuento;
                    }

                    /**
                     * Define el valor de la propiedad razonDescuento.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setRazonDescuento(String value) {
                        this.razonDescuento = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad valorDto.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getValorDto() {
                        return valorDto;
                    }

                    /**
                     * Define el valor de la propiedad valorDto.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setValorDto(BigDecimal value) {
                        this.valorDto = value;
                    }

                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="datosMedidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="fechaLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="unidadesLecturaAnterior" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="unidadMedidaAnterior" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="fechaLecturaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="unidadesLecturaActual" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="unidadMedidaActual" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="metodoLectura" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="duracionServicio" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
                 *         &lt;element name="unidadMedidaServicio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *       &lt;/sequence>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "datosMedidor",
                    "fechaLecturaAnterior",
                    "unidadesLecturaAnterior",
                    "unidadMedidaAnterior",
                    "fechaLecturaActual",
                    "unidadesLecturaActual",
                    "unidadMedidaActual",
                    "metodoLectura",
                    "duracionServicio",
                    "unidadMedidaServicio"
                })
                public static class LecturaContador {

                    protected String datosMedidor;
                    protected String fechaLecturaAnterior;
                    protected BigDecimal unidadesLecturaAnterior;
                    protected String unidadMedidaAnterior;
                    protected String fechaLecturaActual;
                    protected BigDecimal unidadesLecturaActual;
                    protected String unidadMedidaActual;
                    protected String metodoLectura;
                    protected BigDecimal duracionServicio;
                    protected String unidadMedidaServicio;

                    /**
                     * Obtiene el valor de la propiedad datosMedidor.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getDatosMedidor() {
                        return datosMedidor;
                    }

                    /**
                     * Define el valor de la propiedad datosMedidor.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setDatosMedidor(String value) {
                        this.datosMedidor = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad fechaLecturaAnterior.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getFechaLecturaAnterior() {
                        return fechaLecturaAnterior;
                    }

                    /**
                     * Define el valor de la propiedad fechaLecturaAnterior.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setFechaLecturaAnterior(String value) {
                        this.fechaLecturaAnterior = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadesLecturaAnterior.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getUnidadesLecturaAnterior() {
                        return unidadesLecturaAnterior;
                    }

                    /**
                     * Define el valor de la propiedad unidadesLecturaAnterior.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setUnidadesLecturaAnterior(BigDecimal value) {
                        this.unidadesLecturaAnterior = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadMedidaAnterior.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getUnidadMedidaAnterior() {
                        return unidadMedidaAnterior;
                    }

                    /**
                     * Define el valor de la propiedad unidadMedidaAnterior.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setUnidadMedidaAnterior(String value) {
                        this.unidadMedidaAnterior = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad fechaLecturaActual.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getFechaLecturaActual() {
                        return fechaLecturaActual;
                    }

                    /**
                     * Define el valor de la propiedad fechaLecturaActual.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setFechaLecturaActual(String value) {
                        this.fechaLecturaActual = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadesLecturaActual.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getUnidadesLecturaActual() {
                        return unidadesLecturaActual;
                    }

                    /**
                     * Define el valor de la propiedad unidadesLecturaActual.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setUnidadesLecturaActual(BigDecimal value) {
                        this.unidadesLecturaActual = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadMedidaActual.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getUnidadMedidaActual() {
                        return unidadMedidaActual;
                    }

                    /**
                     * Define el valor de la propiedad unidadMedidaActual.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setUnidadMedidaActual(String value) {
                        this.unidadMedidaActual = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad metodoLectura.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getMetodoLectura() {
                        return metodoLectura;
                    }

                    /**
                     * Define el valor de la propiedad metodoLectura.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setMetodoLectura(String value) {
                        this.metodoLectura = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad duracionServicio.
                     * 
                     * @return
                     *     possible object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public BigDecimal getDuracionServicio() {
                        return duracionServicio;
                    }

                    /**
                     * Define el valor de la propiedad duracionServicio.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link BigDecimal }
                     *     
                     */
                    public void setDuracionServicio(BigDecimal value) {
                        this.duracionServicio = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad unidadMedidaServicio.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getUnidadMedidaServicio() {
                        return unidadMedidaServicio;
                    }

                    /**
                     * Define el valor de la propiedad unidadMedidaServicio.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setUnidadMedidaServicio(String value) {
                        this.unidadMedidaServicio = value;
                    }

                }

            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="impuesto" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="baseImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="porcImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="valorImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="codImpuesto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "impuesto"
    })
    public static class Impuestos {

        @XmlElement(required = true)
        protected List<Documento.Impuestos.Impuesto> impuesto;

        /**
         * Gets the value of the impuesto property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the impuesto property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getImpuesto().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.Impuestos.Impuesto }
         * 
         * 
         */
        public List<Documento.Impuestos.Impuesto> getImpuesto() {
            if (impuesto == null) {
                impuesto = new ArrayList<Documento.Impuestos.Impuesto>();
            }
            return this.impuesto;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="baseImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="porcImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="valorImpuesto" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="codImpuesto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "baseImpuesto",
            "porcImpuesto",
            "valorImpuesto",
            "codImpuesto"
        })
        public static class Impuesto {

            @XmlElement(required = true)
            protected BigDecimal baseImpuesto;
            @XmlElement(required = true)
            protected BigDecimal porcImpuesto;
            @XmlElement(required = true)
            protected BigDecimal valorImpuesto;
            protected String codImpuesto;

            /**
             * Obtiene el valor de la propiedad baseImpuesto.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getBaseImpuesto() {
                return baseImpuesto;
            }

            /**
             * Define el valor de la propiedad baseImpuesto.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setBaseImpuesto(BigDecimal value) {
                this.baseImpuesto = value;
            }

            /**
             * Obtiene el valor de la propiedad porcImpuesto.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPorcImpuesto() {
                return porcImpuesto;
            }

            /**
             * Define el valor de la propiedad porcImpuesto.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPorcImpuesto(BigDecimal value) {
                this.porcImpuesto = value;
            }

            /**
             * Obtiene el valor de la propiedad valorImpuesto.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getValorImpuesto() {
                return valorImpuesto;
            }

            /**
             * Define el valor de la propiedad valorImpuesto.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setValorImpuesto(BigDecimal value) {
                this.valorImpuesto = value;
            }

            /**
             * Obtiene el valor de la propiedad codImpuesto.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodImpuesto() {
                return codImpuesto;
            }

            /**
             * Define el valor de la propiedad codImpuesto.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodImpuesto(String value) {
                this.codImpuesto = value;
            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="linea" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="numLinea" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
     *                   &lt;element name="idEstandarReferencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="referenciaItem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="descripcionItem" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="unidadMedida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="unidadesLinea" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="precioUnidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="subtotalLinea" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="porcDescuentoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="descuentoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="totalLinea" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="codImpuestoLinea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="porcImpuestoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="valorImpuestoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
     *                   &lt;element name="idMandante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="datosAdicionales" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="datoAdicional" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="numDato" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
     *                                       &lt;element name="valorDato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "linea"
    })
    public static class Lineas {

        @XmlElement(required = true)
        protected List<Documento.Lineas.Linea> linea;

        /**
         * Gets the value of the linea property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the linea property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLinea().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.Lineas.Linea }
         * 
         * 
         */
        public List<Documento.Lineas.Linea> getLinea() {
            if (linea == null) {
                linea = new ArrayList<Documento.Lineas.Linea>();
            }
            return this.linea;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="numLinea" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
         *         &lt;element name="idEstandarReferencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="referenciaItem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="descripcionItem" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="unidadMedida" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="unidadesLinea" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="precioUnidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="subtotalLinea" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="porcDescuentoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="descuentoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="totalLinea" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="codImpuestoLinea" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="porcImpuestoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="valorImpuestoLinea" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
         *         &lt;element name="idMandante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="datosAdicionales" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="datoAdicional" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="numDato" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
         *                             &lt;element name="valorDato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                           &lt;/sequence>
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "numLinea",
            "idEstandarReferencia",
            "referenciaItem",
            "descripcionItem",
            "unidadMedida",
            "unidadesLinea",
            "precioUnidad",
            "subtotalLinea",
            "porcDescuentoLinea",
            "descuentoLinea",
            "totalLinea",
            "codImpuestoLinea",
            "porcImpuestoLinea",
            "valorImpuestoLinea",
            "idMandante",
            "datosAdicionales"
        })
        public static class Linea {

            @XmlSchemaType(name = "unsignedInt")
            protected long numLinea;
            protected String idEstandarReferencia;
            protected String referenciaItem;
            @XmlElement(required = true)
            protected String descripcionItem;
            protected String unidadMedida;
            @XmlElement(required = true)
            protected BigDecimal unidadesLinea;
            @XmlElement(required = true)
            protected BigDecimal precioUnidad;
            @XmlElement(required = true)
            protected BigDecimal subtotalLinea;
            protected BigDecimal porcDescuentoLinea;
            protected BigDecimal descuentoLinea;
            @XmlElement(required = true)
            protected BigDecimal totalLinea;
            protected String codImpuestoLinea;
            protected BigDecimal porcImpuestoLinea;
            protected BigDecimal valorImpuestoLinea;
            protected String idMandante;
            protected Documento.Lineas.Linea.DatosAdicionales datosAdicionales;

            /**
             * Obtiene el valor de la propiedad numLinea.
             * 
             */
            public long getNumLinea() {
                return numLinea;
            }

            /**
             * Define el valor de la propiedad numLinea.
             * 
             */
            public void setNumLinea(long value) {
                this.numLinea = value;
            }

            /**
             * Obtiene el valor de la propiedad idEstandarReferencia.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIdEstandarReferencia() {
                return idEstandarReferencia;
            }

            /**
             * Define el valor de la propiedad idEstandarReferencia.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIdEstandarReferencia(String value) {
                this.idEstandarReferencia = value;
            }

            /**
             * Obtiene el valor de la propiedad referenciaItem.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getReferenciaItem() {
                return referenciaItem;
            }

            /**
             * Define el valor de la propiedad referenciaItem.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setReferenciaItem(String value) {
                this.referenciaItem = value;
            }

            /**
             * Obtiene el valor de la propiedad descripcionItem.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDescripcionItem() {
                return descripcionItem;
            }

            /**
             * Define el valor de la propiedad descripcionItem.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDescripcionItem(String value) {
                this.descripcionItem = value;
            }

            /**
             * Obtiene el valor de la propiedad unidadMedida.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUnidadMedida() {
                return unidadMedida;
            }

            /**
             * Define el valor de la propiedad unidadMedida.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUnidadMedida(String value) {
                this.unidadMedida = value;
            }

            /**
             * Obtiene el valor de la propiedad unidadesLinea.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getUnidadesLinea() {
                return unidadesLinea;
            }

            /**
             * Define el valor de la propiedad unidadesLinea.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setUnidadesLinea(BigDecimal value) {
                this.unidadesLinea = value;
            }

            /**
             * Obtiene el valor de la propiedad precioUnidad.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPrecioUnidad() {
                return precioUnidad;
            }

            /**
             * Define el valor de la propiedad precioUnidad.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPrecioUnidad(BigDecimal value) {
                this.precioUnidad = value;
            }

            /**
             * Obtiene el valor de la propiedad subtotalLinea.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getSubtotalLinea() {
                return subtotalLinea;
            }

            /**
             * Define el valor de la propiedad subtotalLinea.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setSubtotalLinea(BigDecimal value) {
                this.subtotalLinea = value;
            }

            /**
             * Obtiene el valor de la propiedad porcDescuentoLinea.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPorcDescuentoLinea() {
                return porcDescuentoLinea;
            }

            /**
             * Define el valor de la propiedad porcDescuentoLinea.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPorcDescuentoLinea(BigDecimal value) {
                this.porcDescuentoLinea = value;
            }

            /**
             * Obtiene el valor de la propiedad descuentoLinea.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getDescuentoLinea() {
                return descuentoLinea;
            }

            /**
             * Define el valor de la propiedad descuentoLinea.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setDescuentoLinea(BigDecimal value) {
                this.descuentoLinea = value;
            }

            /**
             * Obtiene el valor de la propiedad totalLinea.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getTotalLinea() {
                return totalLinea;
            }

            /**
             * Define el valor de la propiedad totalLinea.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setTotalLinea(BigDecimal value) {
                this.totalLinea = value;
            }

            /**
             * Obtiene el valor de la propiedad codImpuestoLinea.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodImpuestoLinea() {
                return codImpuestoLinea;
            }

            /**
             * Define el valor de la propiedad codImpuestoLinea.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodImpuestoLinea(String value) {
                this.codImpuestoLinea = value;
            }

            /**
             * Obtiene el valor de la propiedad porcImpuestoLinea.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPorcImpuestoLinea() {
                return porcImpuestoLinea;
            }

            /**
             * Define el valor de la propiedad porcImpuestoLinea.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPorcImpuestoLinea(BigDecimal value) {
                this.porcImpuestoLinea = value;
            }

            /**
             * Obtiene el valor de la propiedad valorImpuestoLinea.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getValorImpuestoLinea() {
                return valorImpuestoLinea;
            }

            /**
             * Define el valor de la propiedad valorImpuestoLinea.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setValorImpuestoLinea(BigDecimal value) {
                this.valorImpuestoLinea = value;
            }

            /**
             * Obtiene el valor de la propiedad idMandante.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIdMandante() {
                return idMandante;
            }

            /**
             * Define el valor de la propiedad idMandante.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIdMandante(String value) {
                this.idMandante = value;
            }

            /**
             * Obtiene el valor de la propiedad datosAdicionales.
             * 
             * @return
             *     possible object is
             *     {@link Documento.Lineas.Linea.DatosAdicionales }
             *     
             */
            public Documento.Lineas.Linea.DatosAdicionales getDatosAdicionales() {
                return datosAdicionales;
            }

            /**
             * Define el valor de la propiedad datosAdicionales.
             * 
             * @param value
             *     allowed object is
             *     {@link Documento.Lineas.Linea.DatosAdicionales }
             *     
             */
            public void setDatosAdicionales(Documento.Lineas.Linea.DatosAdicionales value) {
                this.datosAdicionales = value;
            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="datoAdicional" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="numDato" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
             *                   &lt;element name="valorDato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                 &lt;/sequence>
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "datoAdicional"
            })
            public static class DatosAdicionales {

                @XmlElement(required = true)
                protected List<Documento.Lineas.Linea.DatosAdicionales.DatoAdicional> datoAdicional;

                /**
                 * Gets the value of the datoAdicional property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the datoAdicional property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getDatoAdicional().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Documento.Lineas.Linea.DatosAdicionales.DatoAdicional }
                 * 
                 * 
                 */
                public List<Documento.Lineas.Linea.DatosAdicionales.DatoAdicional> getDatoAdicional() {
                    if (datoAdicional == null) {
                        datoAdicional = new ArrayList<Documento.Lineas.Linea.DatosAdicionales.DatoAdicional>();
                    }
                    return this.datoAdicional;
                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="numDato" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
                 *         &lt;element name="valorDato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *       &lt;/sequence>
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "numDato",
                    "valorDato"
                })
                public static class DatoAdicional {

                    @XmlSchemaType(name = "unsignedInt")
                    protected long numDato;
                    protected String valorDato;

                    /**
                     * Obtiene el valor de la propiedad numDato.
                     * 
                     */
                    public long getNumDato() {
                        return numDato;
                    }

                    /**
                     * Define el valor de la propiedad numDato.
                     * 
                     */
                    public void setNumDato(long value) {
                        this.numDato = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad valorDato.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getValorDato() {
                        return valorDato;
                    }

                    /**
                     * Define el valor de la propiedad valorDato.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setValorDato(String value) {
                        this.valorDato = value;
                    }

                }

            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="idProveedor" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "idProveedor"
    })
    public static class Proveedor {

        @XmlElement(required = true)
        protected String idProveedor;

        /**
         * Obtiene el valor de la propiedad idProveedor.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdProveedor() {
            return idProveedor;
        }

        /**
         * Define el valor de la propiedad idProveedor.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdProveedor(String value) {
            this.idProveedor = value;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="retencion" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="baseRetencion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="porcRetencion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="valorRetencion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *                   &lt;element name="codRetencion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "retencion"
    })
    public static class Retenciones {

        @XmlElement(required = true)
        protected List<Documento.Retenciones.Retencion> retencion;

        /**
         * Gets the value of the retencion property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the retencion property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRetencion().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Documento.Retenciones.Retencion }
         * 
         * 
         */
        public List<Documento.Retenciones.Retencion> getRetencion() {
            if (retencion == null) {
                retencion = new ArrayList<Documento.Retenciones.Retencion>();
            }
            return this.retencion;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="baseRetencion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="porcRetencion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="valorRetencion" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
         *         &lt;element name="codRetencion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "baseRetencion",
            "porcRetencion",
            "valorRetencion",
            "codRetencion"
        })
        public static class Retencion {

            @XmlElement(required = true)
            protected BigDecimal baseRetencion;
            @XmlElement(required = true)
            protected BigDecimal porcRetencion;
            @XmlElement(required = true)
            protected BigDecimal valorRetencion;
            protected String codRetencion;

            /**
             * Obtiene el valor de la propiedad baseRetencion.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getBaseRetencion() {
                return baseRetencion;
            }

            /**
             * Define el valor de la propiedad baseRetencion.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setBaseRetencion(BigDecimal value) {
                this.baseRetencion = value;
            }

            /**
             * Obtiene el valor de la propiedad porcRetencion.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getPorcRetencion() {
                return porcRetencion;
            }

            /**
             * Define el valor de la propiedad porcRetencion.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setPorcRetencion(BigDecimal value) {
                this.porcRetencion = value;
            }

            /**
             * Obtiene el valor de la propiedad valorRetencion.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getValorRetencion() {
                return valorRetencion;
            }

            /**
             * Define el valor de la propiedad valorRetencion.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setValorRetencion(BigDecimal value) {
                this.valorRetencion = value;
            }

            /**
             * Obtiene el valor de la propiedad codRetencion.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCodRetencion() {
                return codRetencion;
            }

            /**
             * Define el valor de la propiedad codRetencion.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCodRetencion(String value) {
                this.codRetencion = value;
            }

        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="divisaOrigen" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="divisaDestino" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="tipoCambio" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
     *         &lt;element name="fechaTipoCambio" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "divisaOrigen",
        "divisaDestino",
        "tipoCambio",
        "fechaTipoCambio"
    })
    public static class TipoCambio {

        @XmlElement(required = true)
        protected String divisaOrigen;
        @XmlElement(required = true)
        protected String divisaDestino;
        @XmlElement(required = true)
        protected BigDecimal tipoCambio;
        @XmlElement(required = true)
        protected String fechaTipoCambio;

        /**
         * Obtiene el valor de la propiedad divisaOrigen.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDivisaOrigen() {
            return divisaOrigen;
        }

        /**
         * Define el valor de la propiedad divisaOrigen.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDivisaOrigen(String value) {
            this.divisaOrigen = value;
        }

        /**
         * Obtiene el valor de la propiedad divisaDestino.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDivisaDestino() {
            return divisaDestino;
        }

        /**
         * Define el valor de la propiedad divisaDestino.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDivisaDestino(String value) {
            this.divisaDestino = value;
        }

        /**
         * Obtiene el valor de la propiedad tipoCambio.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getTipoCambio() {
            return tipoCambio;
        }

        /**
         * Define el valor de la propiedad tipoCambio.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setTipoCambio(BigDecimal value) {
            this.tipoCambio = value;
        }

        /**
         * Obtiene el valor de la propiedad fechaTipoCambio.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFechaTipoCambio() {
            return fechaTipoCambio;
        }

        /**
         * Define el valor de la propiedad fechaTipoCambio.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFechaTipoCambio(String value) {
            this.fechaTipoCambio = value;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="fctConvCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="monedaCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="subtotalCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="descuentoDetalleCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="cargoDetalleCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalBrutoCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalIvaCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalIncCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalBolsasCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalOtroImpCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="montoImpuestosCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalNetoCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="montoDctoCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="montoCargoCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="valorPagarCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="reteFuenteCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="reteIvaCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="reteIcaCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalAnticiposCop" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "fctConvCop",
        "monedaCop",
        "subtotalCop",
        "descuentoDetalleCop",
        "cargoDetalleCop",
        "totalBrutoCop",
        "totalIvaCop",
        "totalIncCop",
        "totalBolsasCop",
        "totalOtroImpCop",
        "montoImpuestosCop",
        "totalNetoCop",
        "montoDctoCop",
        "montoCargoCop",
        "valorPagarCop",
        "reteFuenteCop",
        "reteIvaCop",
        "reteIcaCop",
        "totalAnticiposCop"
    })
    public static class TotalesCop {

        @XmlElement(required = true)
        protected String fctConvCop;
        @XmlElement(required = true)
        protected String monedaCop;
        @XmlElement(required = true)
        protected String subtotalCop;
        @XmlElement(required = true)
        protected String descuentoDetalleCop;
        @XmlElement(required = true)
        protected String cargoDetalleCop;
        @XmlElement(required = true)
        protected String totalBrutoCop;
        @XmlElement(required = true)
        protected String totalIvaCop;
        @XmlElement(required = true)
        protected String totalIncCop;
        @XmlElement(required = true)
        protected String totalBolsasCop;
        @XmlElement(required = true)
        protected String totalOtroImpCop;
        @XmlElement(required = true)
        protected String montoImpuestosCop;
        @XmlElement(required = true)
        protected String totalNetoCop;
        @XmlElement(required = true)
        protected String montoDctoCop;
        @XmlElement(required = true)
        protected String montoCargoCop;
        @XmlElement(required = true)
        protected String valorPagarCop;
        @XmlElement(required = true)
        protected String reteFuenteCop;
        @XmlElement(required = true)
        protected String reteIvaCop;
        @XmlElement(required = true)
        protected String reteIcaCop;
        @XmlElement(required = true)
        protected String totalAnticiposCop;

        /**
         * Obtiene el valor de la propiedad fctConvCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFctConvCop() {
            return fctConvCop;
        }

        /**
         * Define el valor de la propiedad fctConvCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFctConvCop(String value) {
            this.fctConvCop = value;
        }

        /**
         * Obtiene el valor de la propiedad monedaCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMonedaCop() {
            return monedaCop;
        }

        /**
         * Define el valor de la propiedad monedaCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMonedaCop(String value) {
            this.monedaCop = value;
        }

        /**
         * Obtiene el valor de la propiedad subtotalCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSubtotalCop() {
            return subtotalCop;
        }

        /**
         * Define el valor de la propiedad subtotalCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSubtotalCop(String value) {
            this.subtotalCop = value;
        }

        /**
         * Obtiene el valor de la propiedad descuentoDetalleCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDescuentoDetalleCop() {
            return descuentoDetalleCop;
        }

        /**
         * Define el valor de la propiedad descuentoDetalleCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDescuentoDetalleCop(String value) {
            this.descuentoDetalleCop = value;
        }

        /**
         * Obtiene el valor de la propiedad cargoDetalleCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCargoDetalleCop() {
            return cargoDetalleCop;
        }

        /**
         * Define el valor de la propiedad cargoDetalleCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCargoDetalleCop(String value) {
            this.cargoDetalleCop = value;
        }

        /**
         * Obtiene el valor de la propiedad totalBrutoCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotalBrutoCop() {
            return totalBrutoCop;
        }

        /**
         * Define el valor de la propiedad totalBrutoCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotalBrutoCop(String value) {
            this.totalBrutoCop = value;
        }

        /**
         * Obtiene el valor de la propiedad totalIvaCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotalIvaCop() {
            return totalIvaCop;
        }

        /**
         * Define el valor de la propiedad totalIvaCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotalIvaCop(String value) {
            this.totalIvaCop = value;
        }

        /**
         * Obtiene el valor de la propiedad totalIncCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotalIncCop() {
            return totalIncCop;
        }

        /**
         * Define el valor de la propiedad totalIncCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotalIncCop(String value) {
            this.totalIncCop = value;
        }

        /**
         * Obtiene el valor de la propiedad totalBolsasCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotalBolsasCop() {
            return totalBolsasCop;
        }

        /**
         * Define el valor de la propiedad totalBolsasCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotalBolsasCop(String value) {
            this.totalBolsasCop = value;
        }

        /**
         * Obtiene el valor de la propiedad totalOtroImpCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotalOtroImpCop() {
            return totalOtroImpCop;
        }

        /**
         * Define el valor de la propiedad totalOtroImpCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotalOtroImpCop(String value) {
            this.totalOtroImpCop = value;
        }

        /**
         * Obtiene el valor de la propiedad montoImpuestosCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMontoImpuestosCop() {
            return montoImpuestosCop;
        }

        /**
         * Define el valor de la propiedad montoImpuestosCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMontoImpuestosCop(String value) {
            this.montoImpuestosCop = value;
        }

        /**
         * Obtiene el valor de la propiedad totalNetoCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotalNetoCop() {
            return totalNetoCop;
        }

        /**
         * Define el valor de la propiedad totalNetoCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotalNetoCop(String value) {
            this.totalNetoCop = value;
        }

        /**
         * Obtiene el valor de la propiedad montoDctoCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMontoDctoCop() {
            return montoDctoCop;
        }

        /**
         * Define el valor de la propiedad montoDctoCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMontoDctoCop(String value) {
            this.montoDctoCop = value;
        }

        /**
         * Obtiene el valor de la propiedad montoCargoCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMontoCargoCop() {
            return montoCargoCop;
        }

        /**
         * Define el valor de la propiedad montoCargoCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMontoCargoCop(String value) {
            this.montoCargoCop = value;
        }

        /**
         * Obtiene el valor de la propiedad valorPagarCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValorPagarCop() {
            return valorPagarCop;
        }

        /**
         * Define el valor de la propiedad valorPagarCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValorPagarCop(String value) {
            this.valorPagarCop = value;
        }

        /**
         * Obtiene el valor de la propiedad reteFuenteCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReteFuenteCop() {
            return reteFuenteCop;
        }

        /**
         * Define el valor de la propiedad reteFuenteCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReteFuenteCop(String value) {
            this.reteFuenteCop = value;
        }

        /**
         * Obtiene el valor de la propiedad reteIvaCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReteIvaCop() {
            return reteIvaCop;
        }

        /**
         * Define el valor de la propiedad reteIvaCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReteIvaCop(String value) {
            this.reteIvaCop = value;
        }

        /**
         * Obtiene el valor de la propiedad reteIcaCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReteIcaCop() {
            return reteIcaCop;
        }

        /**
         * Define el valor de la propiedad reteIcaCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReteIcaCop(String value) {
            this.reteIcaCop = value;
        }

        /**
         * Obtiene el valor de la propiedad totalAnticiposCop.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotalAnticiposCop() {
            return totalAnticiposCop;
        }

        /**
         * Define el valor de la propiedad totalAnticiposCop.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotalAnticiposCop(String value) {
            this.totalAnticiposCop = value;
        }

    }

}
