
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.delogica.saaf.ws.consultaestados.impl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetEstadosFacturasConFicheros_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "getEstadosFacturasConFicheros");
    private final static QName _SAAFException_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "SAAFException");
    private final static QName _GetEstadosFacturas_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "getEstadosFacturas");
    private final static QName _ConsultaFacturasModificadasResponse_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "consultaFacturasModificadasResponse");
    private final static QName _EntregaFacturaResponse_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "entregaFacturaResponse");
    private final static QName _ConsultaFacturasModificadas_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "consultaFacturasModificadas");
    private final static QName _GetEstadosFacturasResponse_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "getEstadosFacturasResponse");
    private final static QName _GetEstadosFacturasConFicherosResponse_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "getEstadosFacturasConFicherosResponse");
    private final static QName _EntregaFactura_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "entregaFactura");
    private final static QName _GetEstadosNominas_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "getEstadosNominas");
    private final static QName _GetEstadosNominasResponse_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "getEstadosNominasResponse");
    private final static QName _GetEstadosNominasConFicheros_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "getEstadosNominasConFicheros");
    private final static QName _GetEstadosNominasConFicherosResponse_QNAME = new QName("http://impl.consultaestados.ws.saaf.delogica.es/", "getEstadosNominasConFicherosResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.delogica.saaf.ws.consultaestados.impl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Documento }
     * 
     */
    public Documento createDocumento() {
        return new Documento();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD }
     * 
     */
    public Documento.ExtensionSPD createDocumentoExtensionSPD() {
        return new Documento.ExtensionSPD();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado createDocumentoExtensionSPDValorFacturado() {
        return new Documento.ExtensionSPD.ValorFacturado();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos createDocumentoExtensionSPDValorFacturadoHistoricoResiduos() {
        return new Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos createDocumentoExtensionSPDValorFacturadoHistoricoConsumos() {
        return new Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.Producto }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.Producto createDocumentoExtensionSPDValorFacturadoProducto() {
        return new Documento.ExtensionSPD.ValorFacturado.Producto();
    }

    /**
     * Create an instance of {@link Documento.ExtensionPOS }
     * 
     */
    public Documento.ExtensionPOS createDocumentoExtensionPOS() {
        return new Documento.ExtensionPOS();
    }

    /**
     * Create an instance of {@link Documento.Lineas }
     * 
     */
    public Documento.Lineas createDocumentoLineas() {
        return new Documento.Lineas();
    }

    /**
     * Create an instance of {@link Documento.Lineas.Linea }
     * 
     */
    public Documento.Lineas.Linea createDocumentoLineasLinea() {
        return new Documento.Lineas.Linea();
    }

    /**
     * Create an instance of {@link Documento.Lineas.Linea.DatosAdicionales }
     * 
     */
    public Documento.Lineas.Linea.DatosAdicionales createDocumentoLineasLineaDatosAdicionales() {
        return new Documento.Lineas.Linea.DatosAdicionales();
    }

    /**
     * Create an instance of {@link Documento.DatosAdicionales }
     * 
     */
    public Documento.DatosAdicionales createDocumentoDatosAdicionales() {
        return new Documento.DatosAdicionales();
    }

    /**
     * Create an instance of {@link Documento.CondicionesPago }
     * 
     */
    public Documento.CondicionesPago createDocumentoCondicionesPago() {
        return new Documento.CondicionesPago();
    }

    /**
     * Create an instance of {@link Documento.Anticipos }
     * 
     */
    public Documento.Anticipos createDocumentoAnticipos() {
        return new Documento.Anticipos();
    }

    /**
     * Create an instance of {@link Documento.Retenciones }
     * 
     */
    public Documento.Retenciones createDocumentoRetenciones() {
        return new Documento.Retenciones();
    }

    /**
     * Create an instance of {@link Documento.Impuestos }
     * 
     */
    public Documento.Impuestos createDocumentoImpuestos() {
        return new Documento.Impuestos();
    }

    /**
     * Create an instance of {@link Documento.DocumentosAdjuntos }
     * 
     */
    public Documento.DocumentosAdjuntos createDocumentoDocumentosAdjuntos() {
        return new Documento.DocumentosAdjuntos();
    }

    /**
     * Create an instance of {@link Documento.DocumentosReferenciados }
     * 
     */
    public Documento.DocumentosReferenciados createDocumentoDocumentosReferenciados() {
        return new Documento.DocumentosReferenciados();
    }

    /**
     * Create an instance of {@link GetEstadosFacturasConFicheros }
     * 
     */
    public GetEstadosFacturasConFicheros createGetEstadosFacturasConFicheros() {
        return new GetEstadosFacturasConFicheros();
    }

    /**
     * Create an instance of {@link SAAFException }
     * 
     */
    public SAAFException createSAAFException() {
        return new SAAFException();
    }

    /**
     * Create an instance of {@link GetEstadosFacturas }
     * 
     */
    public GetEstadosFacturas createGetEstadosFacturas() {
        return new GetEstadosFacturas();
    }

    /**
     * Create an instance of {@link ConsultaFacturasModificadasResponse }
     * 
     */
    public ConsultaFacturasModificadasResponse createConsultaFacturasModificadasResponse() {
        return new ConsultaFacturasModificadasResponse();
    }

    /**
     * Create an instance of {@link ConsultaFacturasModificadas }
     * 
     */
    public ConsultaFacturasModificadas createConsultaFacturasModificadas() {
        return new ConsultaFacturasModificadas();
    }

    /**
     * Create an instance of {@link EntregaFacturaResponse }
     * 
     */
    public EntregaFacturaResponse createEntregaFacturaResponse() {
        return new EntregaFacturaResponse();
    }

    /**
     * Create an instance of {@link GetEstadosFacturasResponse }
     * 
     */
    public GetEstadosFacturasResponse createGetEstadosFacturasResponse() {
        return new GetEstadosFacturasResponse();
    }

    /**
     * Create an instance of {@link EntregaFactura }
     * 
     */
    public EntregaFactura createEntregaFactura() {
        return new EntregaFactura();
    }

    /**
     * Create an instance of {@link GetEstadosFacturasConFicherosResponse }
     * 
     */
    public GetEstadosFacturasConFicherosResponse createGetEstadosFacturasConFicherosResponse() {
        return new GetEstadosFacturasConFicherosResponse();
    }

    /**
     * Create an instance of {@link IdentificadorFactura }
     * 
     */
    public IdentificadorFactura createIdentificadorFactura() {
        return new IdentificadorFactura();
    }

    /**
     * Create an instance of {@link EntregaFacturaRequest }
     * 
     */
    public EntregaFacturaRequest createEntregaFacturaRequest() {
        return new EntregaFacturaRequest();
    }

    /**
     * Create an instance of {@link ConsultaFacturasModificadasRequest }
     * 
     */
    public ConsultaFacturasModificadasRequest createConsultaFacturasModificadasRequest() {
        return new ConsultaFacturasModificadasRequest();
    }

    /**
     * Create an instance of {@link ConsultaEstadosFacturasResponse }
     * 
     */
    public ConsultaEstadosFacturasResponse createConsultaEstadosFacturasResponse() {
        return new ConsultaEstadosFacturasResponse();
    }

    /**
     * Create an instance of {@link FacturaResult }
     * 
     */
    public FacturaResult createFacturaResult() {
        return new FacturaResult();
    }

    /**
     * Create an instance of {@link GeneralResponse }
     * 
     */
    public GeneralResponse createGeneralResponse() {
        return new GeneralResponse();
    }

    /**
     * Create an instance of {@link ConsultaEstadosFacturasRequest }
     * 
     */
    public ConsultaEstadosFacturasRequest createConsultaEstadosFacturasRequest() {
        return new ConsultaEstadosFacturasRequest();
    }

    /**
     * Create an instance of {@link InfoEstadosFicheroYFactura }
     * 
     */
    public InfoEstadosFicheroYFactura createInfoEstadosFicheroYFactura() {
        return new InfoEstadosFicheroYFactura();
    }

    /**
     * Create an instance of {@link ConsultaEstadosFicherosYFacturasResponse }
     * 
     */
    public ConsultaEstadosFicherosYFacturasResponse createConsultaEstadosFicherosYFacturasResponse() {
        return new ConsultaEstadosFicherosYFacturasResponse();
    }

    /**
     * Create an instance of {@link InfoEstadosFactura }
     * 
     */
    public InfoEstadosFactura createInfoEstadosFactura() {
        return new InfoEstadosFactura();
    }

    /**
     * Create an instance of {@link Documento.Proveedor }
     * 
     */
    public Documento.Proveedor createDocumentoProveedor() {
        return new Documento.Proveedor();
    }

    /**
     * Create an instance of {@link Documento.Cliente }
     * 
     */
    public Documento.Cliente createDocumentoCliente() {
        return new Documento.Cliente();
    }

    /**
     * Create an instance of {@link Documento.EmailsEnvio }
     * 
     */
    public Documento.EmailsEnvio createDocumentoEmailsEnvio() {
        return new Documento.EmailsEnvio();
    }

    /**
     * Create an instance of {@link Documento.DatosTotales }
     * 
     */
    public Documento.DatosTotales createDocumentoDatosTotales() {
        return new Documento.DatosTotales();
    }

    /**
     * Create an instance of {@link Documento.TotalesCop }
     * 
     */
    public Documento.TotalesCop createDocumentoTotalesCop() {
        return new Documento.TotalesCop();
    }

    /**
     * Create an instance of {@link Documento.TipoCambio }
     * 
     */
    public Documento.TipoCambio createDocumentoTipoCambio() {
        return new Documento.TipoCambio();
    }

    /**
     * Create an instance of {@link Documento.ExtensionBolsa }
     * 
     */
    public Documento.ExtensionBolsa createDocumentoExtensionBolsa() {
        return new Documento.ExtensionBolsa();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.Subscriptor }
     * 
     */
    public Documento.ExtensionSPD.Subscriptor createDocumentoExtensionSPDSubscriptor() {
        return new Documento.ExtensionSPD.Subscriptor();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.AcuerdosPago }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.AcuerdosPago createDocumentoExtensionSPDValorFacturadoAcuerdosPago() {
        return new Documento.ExtensionSPD.ValorFacturado.AcuerdosPago();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos.Residuos }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos.Residuos createDocumentoExtensionSPDValorFacturadoHistoricoResiduosResiduos() {
        return new Documento.ExtensionSPD.ValorFacturado.HistoricoResiduos.Residuos();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoMensual }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoMensual createDocumentoExtensionSPDValorFacturadoHistoricoConsumosConsumoMensual() {
        return new Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoMensual();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoPromedio }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoPromedio createDocumentoExtensionSPDValorFacturadoHistoricoConsumosConsumoPromedio() {
        return new Documento.ExtensionSPD.ValorFacturado.HistoricoConsumos.ConsumoPromedio();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.Producto.Descuentos }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.Producto.Descuentos createDocumentoExtensionSPDValorFacturadoProductoDescuentos() {
        return new Documento.ExtensionSPD.ValorFacturado.Producto.Descuentos();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.Producto.Cargos }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.Producto.Cargos createDocumentoExtensionSPDValorFacturadoProductoCargos() {
        return new Documento.ExtensionSPD.ValorFacturado.Producto.Cargos();
    }

    /**
     * Create an instance of {@link Documento.ExtensionSPD.ValorFacturado.Producto.LecturaContador }
     * 
     */
    public Documento.ExtensionSPD.ValorFacturado.Producto.LecturaContador createDocumentoExtensionSPDValorFacturadoProductoLecturaContador() {
        return new Documento.ExtensionSPD.ValorFacturado.Producto.LecturaContador();
    }

    /**
     * Create an instance of {@link Documento.ExtensionPOS.BeneficiosComprador }
     * 
     */
    public Documento.ExtensionPOS.BeneficiosComprador createDocumentoExtensionPOSBeneficiosComprador() {
        return new Documento.ExtensionPOS.BeneficiosComprador();
    }

    /**
     * Create an instance of {@link Documento.ExtensionPOS.PuntoDeVenta }
     * 
     */
    public Documento.ExtensionPOS.PuntoDeVenta createDocumentoExtensionPOSPuntoDeVenta() {
        return new Documento.ExtensionPOS.PuntoDeVenta();
    }

    /**
     * Create an instance of {@link Documento.Lineas.Linea.DatosAdicionales.DatoAdicional }
     * 
     */
    public Documento.Lineas.Linea.DatosAdicionales.DatoAdicional createDocumentoLineasLineaDatosAdicionalesDatoAdicional() {
        return new Documento.Lineas.Linea.DatosAdicionales.DatoAdicional();
    }

    /**
     * Create an instance of {@link Documento.DatosAdicionales.DatoAdicional }
     * 
     */
    public Documento.DatosAdicionales.DatoAdicional createDocumentoDatosAdicionalesDatoAdicional() {
        return new Documento.DatosAdicionales.DatoAdicional();
    }

    /**
     * Create an instance of {@link Documento.CondicionesPago.CondicionPago }
     * 
     */
    public Documento.CondicionesPago.CondicionPago createDocumentoCondicionesPagoCondicionPago() {
        return new Documento.CondicionesPago.CondicionPago();
    }

    /**
     * Create an instance of {@link Documento.Anticipos.Anticipo }
     * 
     */
    public Documento.Anticipos.Anticipo createDocumentoAnticiposAnticipo() {
        return new Documento.Anticipos.Anticipo();
    }

    /**
     * Create an instance of {@link Documento.Retenciones.Retencion }
     * 
     */
    public Documento.Retenciones.Retencion createDocumentoRetencionesRetencion() {
        return new Documento.Retenciones.Retencion();
    }

    /**
     * Create an instance of {@link Documento.Impuestos.Impuesto }
     * 
     */
    public Documento.Impuestos.Impuesto createDocumentoImpuestosImpuesto() {
        return new Documento.Impuestos.Impuesto();
    }

    /**
     * Create an instance of {@link Documento.DocumentosAdjuntos.DocumentoAdjunto }
     * 
     */
    public Documento.DocumentosAdjuntos.DocumentoAdjunto createDocumentoDocumentosAdjuntosDocumentoAdjunto() {
        return new Documento.DocumentosAdjuntos.DocumentoAdjunto();
    }

    /**
     * Create an instance of {@link Documento.DocumentosReferenciados.DocumentoReferenciado }
     * 
     */
    public Documento.DocumentosReferenciados.DocumentoReferenciado createDocumentoDocumentosReferenciadosDocumentoReferenciado() {
        return new Documento.DocumentosReferenciados.DocumentoReferenciado();
    }
    
    /**
     * Create an instance of {@link GetEstadosNominas }
     * 
     */
    public GetEstadosNominas createGetEstadosNominas() {
    	return new GetEstadosNominas();
    }
    
    /**
     * Create an instance of {@link GetEstadosNominasResponse }
     * 
     */
    public GetEstadosNominasResponse createGetEstadosNominasResponse() {
    	return new GetEstadosNominasResponse();
    }
    
    /**
     * Create an instance of {@link GetEstadosNominasConFicheros }
     * 
     */
    public GetEstadosNominasConFicheros createGetEstadosNominasConFicheros() {
    	return new GetEstadosNominasConFicheros();
    }
    
    /**
     * Create an instance of {@link GetEstadosNominasConFicherosResponse }
     * 
     */
    public GetEstadosNominasConFicherosResponse createGetEstadosNominasConFicherosResponse() {
    	return new GetEstadosNominasConFicherosResponse();
    }
    
    /**
     * Create an instance of {@link ConsultaEstadosNominasResponse }
     * 
     */
    public ConsultaEstadosNominasResponse createConsultaEstadosNominasResponse() {
    	return new ConsultaEstadosNominasResponse();
    }
    
    /**
     * Create an instance of {@link ConsultaEstadosNominasRequest }
     * 
     */
    public ConsultaEstadosNominasRequest createConsultaEstadosNominasRequest() {
    	return new ConsultaEstadosNominasRequest();
    }
    
    /**
     * Create an instance of {@link IdentificadorNomina }
     * 
     */
    public IdentificadorNomina createIdentificadorNomina() {
    	return new IdentificadorNomina();
    }
    
    /**
     * Create an instance of {@link InfoEstadosNomina }
     * 
     */
    public InfoEstadosNomina createInfoEstadosNomina() {
    	return new InfoEstadosNomina();
    }
    
    /**
     * Create an instance of {@link ConsultaEstadosFicherosYNominasResponse }
     * 
     */
    public ConsultaEstadosFicherosYNominasResponse createConsultaEstadosFicherosYNominasResponse() {
    	return new ConsultaEstadosFicherosYNominasResponse();
    }
    
    /**
     * Create an instance of {@link InfoEstadosFicheroYNomina }
     * 
     */
    public InfoEstadosFicheroYNomina createInfoEstadosFicheroYNomina() {
    	return new InfoEstadosFicheroYNomina();
    }
    
    /**
     * Create an instance of {@link GeneralResponseNomina }
     * 
     */
    public GeneralResponseNomina createGeneralResponseNomina() {
    	return new GeneralResponseNomina();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEstadosFacturasConFicheros }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "getEstadosFacturasConFicheros")
    public JAXBElement<GetEstadosFacturasConFicheros> createGetEstadosFacturasConFicheros(GetEstadosFacturasConFicheros value) {
        return new JAXBElement<GetEstadosFacturasConFicheros>(_GetEstadosFacturasConFicheros_QNAME, GetEstadosFacturasConFicheros.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SAAFException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "SAAFException")
    public JAXBElement<SAAFException> createSAAFException(SAAFException value) {
        return new JAXBElement<SAAFException>(_SAAFException_QNAME, SAAFException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEstadosFacturas }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "getEstadosFacturas")
    public JAXBElement<GetEstadosFacturas> createGetEstadosFacturas(GetEstadosFacturas value) {
        return new JAXBElement<GetEstadosFacturas>(_GetEstadosFacturas_QNAME, GetEstadosFacturas.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaFacturasModificadasResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "consultaFacturasModificadasResponse")
    public JAXBElement<ConsultaFacturasModificadasResponse> createConsultaFacturasModificadasResponse(ConsultaFacturasModificadasResponse value) {
        return new JAXBElement<ConsultaFacturasModificadasResponse>(_ConsultaFacturasModificadasResponse_QNAME, ConsultaFacturasModificadasResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntregaFacturaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "entregaFacturaResponse")
    public JAXBElement<EntregaFacturaResponse> createEntregaFacturaResponse(EntregaFacturaResponse value) {
        return new JAXBElement<EntregaFacturaResponse>(_EntregaFacturaResponse_QNAME, EntregaFacturaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaFacturasModificadas }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "consultaFacturasModificadas")
    public JAXBElement<ConsultaFacturasModificadas> createConsultaFacturasModificadas(ConsultaFacturasModificadas value) {
        return new JAXBElement<ConsultaFacturasModificadas>(_ConsultaFacturasModificadas_QNAME, ConsultaFacturasModificadas.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEstadosFacturasResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "getEstadosFacturasResponse")
    public JAXBElement<GetEstadosFacturasResponse> createGetEstadosFacturasResponse(GetEstadosFacturasResponse value) {
        return new JAXBElement<GetEstadosFacturasResponse>(_GetEstadosFacturasResponse_QNAME, GetEstadosFacturasResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEstadosFacturasConFicherosResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "getEstadosFacturasConFicherosResponse")
    public JAXBElement<GetEstadosFacturasConFicherosResponse> createGetEstadosFacturasConFicherosResponse(GetEstadosFacturasConFicherosResponse value) {
        return new JAXBElement<GetEstadosFacturasConFicherosResponse>(_GetEstadosFacturasConFicherosResponse_QNAME, GetEstadosFacturasConFicherosResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntregaFactura }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "entregaFactura")
    public JAXBElement<EntregaFactura> createEntregaFactura(EntregaFactura value) {
        return new JAXBElement<EntregaFactura>(_EntregaFactura_QNAME, EntregaFactura.class, null, value);
    }
    

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEstadosNominas }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "getEstadosNominas")
    public JAXBElement<GetEstadosNominas> createGetEstadosNominas(GetEstadosNominas value) {
        return new JAXBElement<GetEstadosNominas>(_GetEstadosNominas_QNAME, GetEstadosNominas.class, null, value);
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEstadosNominasResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "getEstadosNominasResponse")
    public JAXBElement<GetEstadosNominasResponse> createGetEstadosNominasResponse(GetEstadosNominasResponse value) {
        return new JAXBElement<GetEstadosNominasResponse>(_GetEstadosNominasResponse_QNAME, GetEstadosNominasResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEstadosNominasConFicheros }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "getEstadosNominasConFicheros")
    public JAXBElement<GetEstadosNominasConFicheros> createGetEstadosNominasConFicheros(GetEstadosNominasConFicheros value) {
        return new JAXBElement<GetEstadosNominasConFicheros>(_GetEstadosNominasConFicheros_QNAME, GetEstadosNominasConFicheros.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEstadosNominasConFicherosResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://impl.consultaestados.ws.saaf.delogica.es/", name = "getEstadosNominasConFicherosResponse")
    public JAXBElement<GetEstadosNominasConFicherosResponse> createGetEstadosNominasConFicherosResponse(GetEstadosNominasConFicherosResponse value) {
        return new JAXBElement<GetEstadosNominasConFicherosResponse>(_GetEstadosNominasConFicherosResponse_QNAME, GetEstadosNominasConFicherosResponse.class, null, value);
    }


}
