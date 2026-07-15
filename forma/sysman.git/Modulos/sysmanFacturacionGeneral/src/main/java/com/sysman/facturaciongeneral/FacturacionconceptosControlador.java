/*-
 * FacturacionconceptosControlador.java
 *
 * 1.0
 *
 * 10/11/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralDosRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralTresRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralUnoRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.facturaciongeneral.enums.ConceptosNoFacturadosControladorEnum;
import com.sysman.facturaciongeneral.enums.ConceptosNoFacturadosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorEnum;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FacturarLotesControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmDocDianControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmFactLoteControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmRangoProduccionDianUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbCodigoBarrasRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.enums.ConstanteArchivo;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.APISIGEC;
import com.sysman.util.rest.ParamItem;
import com.sysman.util.rest.ParamItems;
import com.sysman.util.rest.ParametroCuerpoEjecucionReporte;
import com.sysman.util.rest.ParametroCuerpoEnvioFactura;
import com.sysman.util.rest.ParametroDeleteEnvioFactura;
import com.sysman.util.rest.ParametroEjecucionApiReporte;
import com.sysman.util.rest.ParametrosCargos;
import com.sysman.util.rest.ParametrosDescuentos;
import com.sysman.util.rest.ParametrosEnvioFactura;
import com.sysman.util.rest.ParametrosReenviarCorreoFactura;
import com.sysman.util.rest.ParametrosSIGEC;
import com.sysman.util.rest.ParametrosEnvioFacturaFiltros;
import com.sysman.util.rest.ParametrosFormato;
import com.sysman.util.rest.ParametrosImpuestos;
import com.sysman.util.rest.ParametrosItems;
import com.sysman.util.rest.ParametrosItemsImpuestos;
import com.sysman.util.rest.ParametrosLegalizarFactura;
import com.sysman.util.rest.ParametrosTercero;
import com.sysman.util.rest.ParametrosTerceroLote;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaApiSigec;
import com.sysman.util.rest.RespuestaCuerpoRangoFacturacion;
import com.sysman.util.rest.RespuestaEnvioFactura;
import com.sysman.util.rest.RespuestaFridaLegalizar;
import com.sysman.util.rest.RespuestaRangoFacturacion;
import com.sysman.util.rest.enums.APIAutoServicioEnum;
import com.sysman.util.soap.ApiInvoway;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Cliente;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.CondicionesPago;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.CondicionesPago.CondicionPago;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.DatosTotales;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Impuestos;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Impuestos.Impuesto;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Lineas;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Lineas.Linea;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Proveedor;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Retenciones;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Retenciones.Retencion;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.TotalesCop;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.InfoEstadosFactura;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.w3c.dom.Node;

import net.sf.jasperreports.engine.JRException;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.tomcat.util.buf.TimeStamp;

/**
 * Clase encargada de la facturacion de los conceptos,de genenar la
 * factura dependiendo de la entidad.
 *
 * @version 1.0, 10/11/2017
 * @author jeguerrero
 *
 * @version 2.0, 01/09/2018
 * @author eamaya
 *
 */
@ManagedBean
@ViewScoped

public class FacturacionconceptosControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String nitCompania;
    
     private BigDecimal consecutivoobjetocobro;

  

	// <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a los grupos de conceptos
     */
    private List<Registro> listaGrupoConceptos;

    /**
     * Lista que carga los tipos de medio de pago
     */
    private List<Registro> listaTipoMedioPago;
    /**
	 * Lista que carga las cuentas de pago
	 */
	private List<Registro> listaCuentaPago;

    /**
     * Variable encargada de almacenar temporalemtne lo seleccionado
     * en el combo concepto de la grilla del subformulario.
     */
    private String auxiliar;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a los estratos del subformulario
     */
    private RegistroDataModelImpl listaEstratoSf178;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a los estratos de la grilla del subformulario
     */
    private RegistroDataModelImpl listaEstratoSf178E;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a auxiliar del subformulario
     */
    private RegistroDataModelImpl listaAuxiliarSf178;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a auxiliar del subformulario
     */
    private RegistroDataModelImpl listaAuxiliarSf178E;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a centro costo del subformulario
     */
    private RegistroDataModelImpl listaCentroCostoSf178;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al centro costo de la grilla del subformulario
     */
    private RegistroDataModelImpl listaCentroCostoSf178E;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a los tercero del subformulario
     */
    private RegistroDataModelImpl listaTerceroSf178;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a los tercero de la grilla del subformulario
     */
    private RegistroDataModelImpl listaTerceroSf178E;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a la tarifa de la grilla del subformulario
     */
    private RegistroDataModelImpl listaTarifaSf178;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a la tarifa del subformulario
     */
    private RegistroDataModelImpl listaTarifaSf178E;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al concepto del subformulario
     */
    private RegistroDataModelImpl listaConceptoSf178;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al concepto de la grilla del subformulario
     */
    private RegistroDataModelImpl listaConceptoSf178E;

    /**
     * Lista que carga las referencias para los conceptos
     */
    private RegistroDataModelImpl listaReferencia;
    /**
     * Lista que carga las referencias para los conceptos en la grilla
     */
    private RegistroDataModelImpl listaReferenciaE;
    /**
     * Lista que carga las fuentes de recurso para los conceptos
     */
    private RegistroDataModelImpl listaFuenteRecurso;
    /**
     * Lista que carga las fuentes de recurso para los conceptos en la
     * grilla
     */
    private RegistroDataModelImpl listaFuenteRecursoE;
    
    /**
     *Lista que carga NUMEROLISTA para los conceptos
     */
 private RegistroDataModelImpl listanumerolista;
    /**
     * Lista que carga las NUMEROLISTA para los conceptos en la
     * grilla
     */
 private RegistroDataModelImpl listanumerolistaE;

    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al nit
     */
    private RegistroDataModelImpl listaNit;

    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al afectar contrato
     */
    private RegistroDataModelImpl listaAfectarContrato;

    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a las descripciones estandarizadas
     */

    private RegistroDataModelImpl listaCodDescripcion;

    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde a los codigos de tarifas base
     */
    private RegistroDataModelImpl listaCodTarifaBase;

    /**
     * Lista que carga las fuentes de recurso para el objeto cobro
     */
    private RegistroDataModelImpl listaFuentesRecurso;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al centro costo
     */
    private RegistroDataModelImpl listaCentroCosto;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al auxiliar
     */
    private RegistroDataModelImpl listaAuxiliar;
    
    
    private Registro regNumeroLista;

	private RegistroDataModelImpl listaTipoContrato;
	private RegistroDataModelImpl listaNroContrato;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>

    /**
     * Lista encargada de almacenar los datos del subformulario
     */
    private List<Registro> listaSubfacturacionconceptosbg;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>

    /**
     * Atributo de referencia para el subformulario
     * SubFacturacionConceptosBG
     */
    private Registro registroSub;
    /**
     * Variable encargada de almacenar temporalmente el tipo de cobro
     * de ingreso al modulo
     */

    private String tipoCobro;
    /**
     * Variable encargada de almacenar temporalmente si aplica
     * preliquidacion
     */
    private boolean indPreliquidacion;
    /**
     * Variable encargada de almacenar temporalmente el ano de cobro
     * de ingreso al modulo
     */
    private String ano;
    /**
     * Variable encargada de mostrar o no el dialogo de confirmacion.
     */
    private boolean dialogoVisible;

    /**
     * Variable encargada de mostrar o no el dialogo de confirmacion
     * de recaudo.
     */
    private boolean dialogoVisibleRecaudar;
    /**
     * Variable encargada de almancenar temporal y globalmente lo
     * seleccionado del contrato.
     */
    private Registro registroAuxiliarContrato;
    /**
     * Variable encargada de almancenar temporalmente un booleano que
     * bloquea o no el campo tercero Varios
     */
    private boolean bloqueaTercVarios;
    /**
     * Variable encargada de almancenar temporalmente el parametro SF
     * OBLIGA BASE GRAVABLE el cual muestra o no el campo base
     * Gravable en el subformulario
     */
    private boolean obligaBaseGravable;
    /**
     * Variable encargada de almancenar temporalmente el parametro SF
     * OBLIGA BASE GRAVABLE el cual muestra o no el campo base
     * Gravable en el subformulario
     */
    private boolean nobligaBaseGravable;
    /**
     * Variable encargada de almancenar temporalmente para saber cual
     * consulta debe tomar el combo tarifas
     */
    private boolean tarifaConcepRela;
    /**
     * Variable encargada de almancenar temporalmente para saber cual
     * consulta debe tomar el combo Estratos
     */
    private boolean estratoConcepRela;
    /**
     * Variable encargada de almancenar temporalmente un booleano que
     * bloquea o no el campo valor unitario
     */
    private boolean bloqueaValorUnitario;
    /**
     * Variable encargada de almancenar temporalmente un booleano que
     * bloquea o no el campo cantidad
     */
    private boolean bloqueaCantidad;
    /**
     * Variable encargada de almacenar temporalmente el booleano en el
     * cual especifica si el concepto aplica tarifa para el calculo
     */

    private boolean aplicaTarifa;

    /**
     * Atributo que indica si se esta cambiando la base fija en la
     * grilla
     */
    private boolean cambioBaseFija;

    /**
     * Atributo que indica si se esta cambiando el estrato
     */
    private boolean cambioEstrato;

    /**
     * Atributo que indica si se esta cambiando la tarifa
     */
    private boolean cambioTarifa;

    /**
     * Atributo que indica si se esta cambiando el concepto
     */
    private boolean cambioConcepto;

    /**
     * Variable encargada de almacenar temporalmente el porcentaje de
     * la tarifa del concepto
     */
    private double porcentajeTarifa;
    /**
     * Variable encargada de almacenar temporalmente el valorUnidad de
     * la tarifa del concepto
     */
    private String valorUnidad;

    /**
     * Variable encargada de almacenar temporalmente la deuda anterior
     * del concepto
     */
    private String deudaAnterior;
    /**
     * Variable encargada de almacenar temporalmente el valor
     * Auxiliarde la tarifa del concepto
     */
    private double valorUnidadAux;
    /**
     * Variable encargada de almacenar temporalmente el valor de la
     * tarifa del concepto
     */
    private String valor;
    /**
     * Variable que almacena el valor de la tarifa seleccionada en el
     * detalle de cada concepto
     */
    private double vlrTarifa;

    /**
     * Variable que almacena el valor del estrato seleccionado en el
     * detalle de cada concepto
     */
    private double vlrEstrato;
    /**
     * Variable encargada de almacenar temporalmente el iva de la
     * tarifa del concepto
     */
    private String iva;
    /**
     * Variable encargada de almacenar temporalmente el retefuente de
     * la tarifa del concepto
     */
    private String reteFuente;
    /**
     * Variable encargada de almacenar temporalmente el retefuente de
     * la tarifa del concepto
     */
    private String descuento;
    /**
     * Variable encargada de almacenar temporalmente el ica de la
     * tarifa del concepto
     */
    private String ica;
    /**
     * Variable encargada de almacenar temporalmente el valorNeto que
     * sera mostrado en el pie de pagina
     */
    private String valorNeto;
    /**
     * Variable encargada de almacenar temporalmente el valorNeto que
     * sera mostrado en el pie de pagina
     */
    private double valorNetoValidacion;

    /**
     * Variable encargada de almacenar temporalmente el valorNeto que
     * sera mostrado en el pie de pagina
     */
    private String valorImpoconsumo;
    /**
     * Variable encargada de almacenar temporalmente el booleano que
     * muestra la fecha de vencimientomanual o no
     */
    private boolean visibleFechaVencManual;
    /**
     * Variable encargada de almacenar temporalmente el booleano que
     * muestra la fecha de preliquidacion o no
     */
    private boolean visibleFechaPreliqui;
    /**
     * Variable encargada de almacenar temporalmente el indice al
     * activar edicion
     */
    private int indiceSubfacturacionconceptosbg;
    /**
     * Variable encargada de almacenar temporalmente el indice al
     * activar edicion
     */
    private String conceptoGrillaAux;
    /**
     * Variable encargada de almacenar temporalmente el booleano que
     * muestra la fecha de preliquidacion o no
     */
    private boolean cambioValorUnitario;

    /**
     * Constante encargada de almacenar el String CODIGO_COBRO
     */

    private final String codigoCobroCons;

    /**
     * Constante encargada de almacenar el String CANTIDAD
     */
    private final String cantidadCons;
    /**
     * Constante encargada de almacenar el String TARIFA
     */
    private final String tarifaCons;
    /**
     * Constante encargada de almacenar el String VALOR_IVA
     */
    private final String valorIvaCons;
    /**
     * Constante encargada de almacenar el String VALOR_RETEFUENTE
     */
    private final String valorRetefuenteCons;
    /**
     * Constante encargada de almacenar el String VALOR_DESCUENTO
     */
    private final String valorDescuentoCons;
    
    /**
     * Constante encargada de almacenar el String VALOR_RETEIVA
     */
    private final String valorReteIva;
    
    /**
     * Constante encargada de almacenar el String NUMEROLISTA
     */
    private final String numeroLista;
    
    private String codigoConcepto;
    
    /**
     * Constante encargada de almacenar el String VALOR_ICA
     */
    private final String valoricaCons;
    /**
     * Constante encargada de almacenar el String EXISTENCIA
     */
    private final String existenciaCons;

    /**
     * Constante encargada de almacenar el String APLICAICA
     */
    private final String aplicaIcaCons;
    /**
     * Constante encargada de almacenar el String PLICARETEFUENTE
     */
    private final String aplicaReteFuenteCons;
    /**
     * Constante encargada de almacenar el String APLICADESCUENTO
     */
    private final String aplicaDescuentoCons;
    /**
     * Constante encargada de almacenar el String OBJETO_CONTRATO
     */
    private final String objetoContratoCons;
    /**
     * Constante encargada de almacenar el String CANTIDAD_GRUPO
     */
    private final String cantidadGrupoCons;

    /**
     * Constante encargada de almacenar el String
     * NOMBRE_FUENTE_RECURSO
     */
    private final String nombreFuenteCons;

    /**
     * Constante encargada de almacenar el String NRO_FACTURA
     */
    private final String nroFacturaCons;
    /**
     * Constante encargada de almacenar el String TELEFONOS
     */
    private final String telefonosCons;
    /**
     * Constante encargada de almacenar el String TIPOCOBRO
     */
    private final String tipoCobroCons;
    /**
     * Constante encargada de almacenar el String CONCEPTO
     */
    private final String conceptoCons;

    /**
     * Constante encargada de almacenar el String
     */
    private final String valorCompraCons;

    /**
     * Constante encargada de almacenar el String
     */
    private final String nombreTerceroCons;
    /**
     * Constante encargada de almacenar el String
     */
    private final String direccionCons;
    /**
     * Constante encargada de almacenar el String
     */
    private final String codigoPostalCons;
    /**
     * Constante encargada de almacenar el String
     */
    private final String aplicaIvaCons;
    /**
     * Constante encargada de almacenar el String
     */
    private final String nombreConceptoCons;
    /**
     * Constante encargada de almacenar el String
     */
    private final String nombreAuxiliarCons;
    /**
     * Constante encargada de almacenar el String
     */
    private final String nombreCentroCostoCons;
    /**
     * Constante encargada de almacenar el String
     */
    private final String formatoMonedaCons;
    /**
     * Constante encargada de almacenar el String
     */
    private final String nitTerceroFacturadoCons;
    /**
     * Constante encargada de almacenar el String
     */
    private final String impresoCons;
    /**
     * Constante encargada de almacenar el String TB_TB3826
     */

    private final String mensajeProperties;

    /**
     * Constante encargadad de almacenar el String TOMAR_VLR
     */
    private final String tomatVlrCons;

    /**
     * Indica que prefijo tiene el tipo de cobro
     */
    private String prefijo;

    /**
     * Indica la referencia de tipo de factura de recaudo
     */
    private String tipoFactRecaudo;
    /**
     * Variable encargada de almacenar temporalemte la sucursal del
     * tercero de la grilla
     */
    private String sucursalTerceroGrilla;
    /**
     * Variable encargada de almacenar temporalemte el titulo Cantidad
     * o Area que sera mostrado en la grilla
     */
    private String tituloCantidad;
    /**
     * Variable encargada de almacenar temporalemte el titulo Estrato
     * o Base que sera mostrado en la grilla
     */
    private String tituloEstrato;
    /**
     * Variable encargada de almacenar temporalemte el titulo Taria o
     * Uso que sera mostrado en la grilla
     */
    private String tituloTarifa;
    /**
     * Variable encargada de almacenar temporalemte el titulo Valor o
     * Sub Total que sera mostrado en la grilla
     */
    private String tituloValor;
    /**
     * Constante encargada de almancenar el string FORMULA
     */
    private final String formulaCons;

    /**
     * Atributo que almacena el Codigo Ean del tipo de cobro
     */
    private String codigoEan;

    /**
     * Variable que almacena el nombre del tipo de cobro seleccionado
     */
    private String nombreTipoCobro;
    
    /**
     * Variable encargada de almacenar temporalmente el valorTotalDolares que
     * sera mostrado en la factura
     */
    private String valorDolares;
    
    /**
     * Variable encargada de almacenar temporalmente el saldoActualConcepto para validar el 
     * valor ingresado en cada concepto
     */
    private String saldoActualConcepto;
    
    /**
     * Variable encargada de almacenar temporalmente el valor actual neto  con el que 
     * se crea elconcepto 
     */
    private String valorActualConcepto;

    /**
     * Indicador que administra la visibilidad del combo codigo de
     * descripcon estandar
     */
    private boolean manejaDescripcion;

    /**
     * Indicador que administra la visibilidad del combo de tarifa
     * base
     */
    private boolean manejaTarifaBase;
    
    private boolean manejaResFacturacion;

    /**
     * Variable que indica si el cobro ya fue facturado para habilitar
     * edici�n de formulario
     */
    private boolean facturado;

    /**
     * Variable que indica si la factura se encuentra anulada para
     * mostrar la etiqueta 'Factura Anulada'
     **/
    private boolean anulada;
    /**
     * Variable que sirve para cargar el boton de crear tercero a
     * partir del parametro SF CREAR TERCERO DESDE LA FACTURACION DE
     * CONCEPTOS
     */
    private boolean cargarBotonTercero;

    /**
     * Atributo que indica si el tipo de cobro seleccionado maneja
     * causacion en el recaudo
     */
    private boolean causarFacRecaudo;

    /**
     * Atributo que indica si el tipo de cobro seleccionado maneja
     * inventario
     */
    private boolean indManejaInventario;

    /**
     * Atributo que indica si se debe cargar el boton Cambiar Tipo de
     * Cobro
     */
    private boolean visibleCambioTipo;

    /**
     * Variable para habilitar la visibilidad del boton de imprimir
     * factura electronica
     */
    private boolean visibleImpresionFacturaElectronica;

    /**
     * Atributo que almacena el nurvo valor de la base fija en la
     * grill
     */
    private double auxiliarBaseFija;

    private double totalFactura = 0;
    
    /**
	 * Variable para habilitar la visibilidad del combo cuentas de pago
	 */
	private boolean visibleCuentaPago;
	
	/**
	 * Variable para habilitar la visibilidad de los campos de facturaci�n en dolares
	 */
	private boolean visibleFacturaDolares;
	
	/**
	 * Variable para habilitar la visibilidad de los campos de relacion de contrato
	 */
	private boolean visibleRelacionContrato;
	
	/**
	 * Variable para desbloquear los campos de facturaci�n en dolares
	 */
	private boolean aplicaFacturaDolares;
	/**
         * Variable para determinar si se cargan las tarifas base
         */
        private boolean visibleTarifaBase;
	
	//7713469 FACTGENERAL(10/08/2022 mrosero)
		private boolean visibleFacturarA;
	//7713469 FACTGENERAL(10/08/2022 mrosero)
		
	/**
	* Variable encargada de almacenar temporalmente el tipo de cobro
	* de ingreso al modulo
	*/
	private String contrato;
	
	/**
	* Variable encargada de almacenar temporalmente la descripcion del tipo de pago
	* de ingreso al modulo
	*/
	private String tipoPagoDesc;
	
	/**
	* Variable encargada de almacenar temporalmente la descripcion del contrato
	* de ingreso al modulo
	*/
	private String objetoContrato;
	/**
     * Variable que indica si el campo "Email" esta bloqueado o no
     */
    private boolean bloqEmail;
    /**
     * Valida la visibilidad del valor a pagar
     * pagos parciales
     */
    private boolean manPagoParcial;
    
    long consecutivo = 0;
    
    private boolean dialogoConsecutivo = false;



	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFactGenCero;
    @EJB
    private EjbFacturacionGeneralUnoRemote ejbFactGenUno;
    @EJB
    private EjbFacturacionGeneralDosRemote ejbFactGenDos;
    @EJB
    private EjbFacturacionGeneralTresRemote ejbFactGenTres;
    @EJB
    private EjbCodigoBarrasRemote ejbCodigoBarras;
    @EJB
    private EjbFacturacionGeneralCuatroRemote ejbFactGeneralCuatro;

    private String tipoFactura;
    private String numFactura;
    private String numFacturaObjCobro;
    private String tituloTarifaBase;
    private boolean visibleTarifaTercero;
    private boolean visiblePrefijoContrato;
    private static final String CONTRA_CERTIFICADO = "CONTRA_CERTIFICADO";
    private static final String URL_SERVICIO_REST= "URL SERVICIO REST";
    private static final String URL_SERVICIO_SOAP= "URL SERVICIO SOAP";
    private static final String MANEJA_FACTURACION_ELECTRONICA_EXTERNA = "MANEJA FACTURACION ELECTRONICA EXTERNA";
    private static final String USUARIO_FACT_ELECTRONICA_EXTERNA = "USUARIO FACT ELECTRONICA EXTERNA";
    private static final String CLAVE_FACT_ELECTRONICA_EXTERNA = "CLAVE FACT ELECTRONICA EXTERNA";
	private String modulo;
	private String consConsecitivoDetalleCobro;
	private static final String MANEJA_REPORTE_FACT_ELECTRO_ORIGINAL_Y_COPIA = "MANEJA REPORTE FACT ELECTRO ORIGINAL Y COPIA";
	private boolean visibleSigec;

	private boolean visibleCotizaContrato;
	
	private String tituloGrillaPrincipal;
	
	private List<Registro> listaCONTRATOSTERCERO;
	
	private Date fechaVencimientoDcto;
	
	private boolean checkConDscto;
	
	private boolean sujetoareportar = true;
	
	private String textoPieDeFactura;
	
	private String actividadesEconomicas;
	
	private double dctoContrato;
	
	private boolean permiteAjusteDecimales = false;
	
	private boolean bloqueaAjusteDecimal;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FacturacionconceptosControlador
     */
    public FacturacionconceptosControlador()
    {
        super();
        codigoCobroCons = "CODIGO_COBRO";
        consConsecitivoDetalleCobro = "CONSECUTIVO";
        
        cantidadCons = "CANTIDAD";
        numeroLista = "NUMEROLISTA";
        tarifaCons = "TARIFA";
        valorIvaCons = "VALOR_IVA";
        valorRetefuenteCons = "VALOR_RETEFUENTE";
        valorDescuentoCons = "VALOR_DESCUENTO";
        valorReteIva = "VALOR_RETEIVA";
        valoricaCons = "VALOR_ICA";
        existenciaCons = "EXISTENCIA";
        aplicaIcaCons = "APLICAICA";
        aplicaReteFuenteCons = "APLICARETEFUENTE";
        aplicaDescuentoCons = "APLICADESCUENTO";
        objetoContratoCons = "OBJETO_CONTRATO";
        cantidadGrupoCons = "CANTIDAD_GRUPO";
        nroFacturaCons = "NRO_FACTURA";
        telefonosCons = "TELEFONOS";
        tipoCobroCons = "TIPOCOBRO";
        conceptoCons = "CONCEPTO";
        valorCompraCons = "VALOR_COMPRA";
        nombreTerceroCons = "NOMBRETERCERO";
        direccionCons = "DIRECCION";
        codigoPostalCons = "CODIGOPOSTAL";
        aplicaIvaCons = "APLICAIVA";
        nombreFuenteCons = "NOMBRE_FUENTE_RECURSO";
        nombreConceptoCons = "NOMBRECONCEPTO";
        nombreAuxiliarCons = "NOMBREAUXILIAR";
        nombreCentroCostoCons = "NOMBRE_CENTRO_COSTO";
        formatoMonedaCons = "$ #,##0.00";
        nitTerceroFacturadoCons = "NIT_TERCEROFACTURADO";
        impresoCons = "IMPRESO";
        formulaCons = "FORMULA";
        mensajeProperties = "TB_TB3826";
        tomatVlrCons = "TOMAR_VLR";
        cambioEstrato = false;
        cambioConcepto = false;
        vlrTarifa = 1;

        ano = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());

        indPreliquidacion = (boolean) SessionUtil
                        .getSessionVar(ConstantesFacturacionGenEnum.INDPRELIQUIDACION
                                        .getValue());

        nombreTipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                                        .getValue());

        causarFacRecaudo = (boolean) SessionUtil
                        .getSessionVar(ConstantesFacturacionGenEnum.INTERFAZ_RECAUDO
                                        .getValue());

        indManejaInventario = (boolean) SessionUtil
                        .getSessionVar(ConstantesFacturacionGenEnum.MANEJA_INVENTARIO
                                        .getValue());

        compania = SessionUtil.getCompania();
        
        modulo = SessionUtil.getModulo();

        nitCompania = SessionUtil.getCompaniaIngreso().getNit();

        manejaDescripcion = true;
        cambioBaseFija = false;
        facturado = false;        
        anulada = false;
        cargarBotonTercero = false;

        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null)
        {
            tipoCobro = (String) parametros.get("tipoCobro");
            ano = (String) parametros.get("anio");
            nombreTipoCobro = (String) parametros.get("nombreTipoCobro");

            if (parametros.get("rid") != null)
            {
                rid = (Map<String, Object>) parametros.get("rid");
            }
        }

        bloqueaTercVarios = false;
        indiceSubfacturacionconceptosbg = -1;
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FACTURACIONCONCEPTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            prefijo = SysmanFunciones.nvl(SessionUtil
                            .getSessionVarContainer(
                                            ConstantesFacturacionGenEnum.PREFIJO
                                                            .getValue()),
                            "0").toString();

            tipoFactRecaudo = SysmanFunciones.nvl(SessionUtil
                            .getSessionVarContainer(
                                            ConstantesFacturacionGenEnum.TIPOFACTURA_RECAUDO
                                                            .getValue()),
                            "0").toString();

            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>

        cargarListaEstratoSf178();
        cargarListaEstratoSf178E();
        cargarListaAuxiliarSf178();
        cargarListaAuxiliarSf178E();
        cargarListaCentroCostoSf178();
        cargarListaCentroCostoSf178E();
        cargarListaTerceroSf178();
        cargarListaTerceroSf178E();
        cargarListaTarifaSf178();
        cargarListaTarifaSf178E();
        cargarListaConceptoSf178();
        cargarListaConceptoSf178E();
        cargarListaReferencia();
        cargarListaReferenciaE();
        cargarListaFuenteRecurso();
        cargarListaFuenteRecursoE();
        cargarListaNit();

        cargarListaCentroCosto();
        cargarListaAuxiliar();

        cargarListaCodTarifaBase();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaGrupoConceptos();
        cargarListaCodDescripcion();
        cargarListaFuentesRecurso();
        cargarListaTipoMedioPago();
        cargarListaCuentaPago();
		cargarListaTipoContrato();        
		// </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        generarTotalesSub();
        cargarListaSubfacturacionconceptosbg();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>

        listaSubfacturacionconceptosbg = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.SF_OBJETO_COBRO;
        buscarLlave();
        asignarOrigenDatos();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANIO.getName(), ano);
        parametrosListado.put("TIPO_COBRO", tipoCobro);       
        
    }

    /**
     *
     * Carga la lista listaSubfacturacionconceptosbg la cual es la que
     * se encarga de gestionar los datos del crud del subformulario
     *
     */
    public void cargarListaSubfacturacionconceptosbg()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(tipoCobroCons, tipoCobro);
            param.put("CODIGOCOBRO", registro.getCampos().get(codigoCobroCons));
            param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()));
            listaSubfacturacionconceptosbg = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            GenericUrlEnum.SF_DETALLE_COBRO
                                                                                                                            .getGridKey())
                                                                                            .getUrl(),
                                                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            UrlServiceCache.SYSMANDSUNIST,
                                                            GenericUrlEnum.SF_DETALLE_COBRO
                                                                            .getTable()));
        }
        catch (SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaGrupoConceptos Metodo encargado de hacer el
     * llamado a la base datos y almacenar los datos en la lista
     * grupoConceptos
     */
    public void cargarListaGrupoConceptos()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipoCobroCons, tipoCobro);

        try
        {
            listaGrupoConceptos = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL9923
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // TIPOCOBRO 662005
    }

    /**
     * 
     * Carga la lista listaTipoMedioPago
     *
     */
    public void cargarListaTipoMedioPago()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaTipoMedioPago = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL5269
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    /**
	 * 
	 * Carga la lista ListaCuentaPago
	 *
	 */
	public void cargarListaCuentaPago() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		try {
			listaCuentaPago = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FacturacionconceptosControladorUrlEnum.URL5022.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

    /**
     *
     * Carga la lista listaCodDescripcion
     *
     */
    public void cargarListaCodDescripcion()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL14308
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipoCobroCons, tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCodDescripcion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaEstratoSf178 Metodo encargado de hacer el
     * llamado a la base datos y almacenar los datos en la lista
     * listaEstratoSf178
     *
     */
    public void cargarListaEstratoSf178()
    {

        Map<String, Object> param = new TreeMap<>();
        if (estratoConcepRela)
        {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);
            param.put(GeneralParameterEnum.CONCEPTO.getName(),
                            registroSub.getCampos().get(conceptoCons));

        }
        else
        {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);

        }
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL10456
                                                        .getValue());
        listaEstratoSf178 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaEstratoSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaEstratoSf178E
     */
    public void cargarListaEstratoSf178E()
    {

        Map<String, Object> param = new TreeMap<>();
        if (estratoConcepRela)
        {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);
            param.put(GeneralParameterEnum.CONCEPTO.getName(),
                            conceptoGrillaAux);

        }
        else
        {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);

        }
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL10456
                                                        .getValue());
        listaEstratoSf178E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaAuxiliarSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaAuxiliarSf178
     */
    public void cargarListaAuxiliarSf178()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL12286
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaAuxiliarSf178 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 23028
    }

    /**
     *
     * Carga la lista listaAuxiliarSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaAuxiliarSf178E
     */
    public void cargarListaAuxiliarSf178E()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL12286
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaAuxiliarSf178E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaCentroCostoSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaCentroCostoSf178
     */
    public void cargarListaCentroCostoSf178()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL13603
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCentroCostoSf178 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // 20059
    }

    /**
     *
     * Carga la lista listaCentroCostoSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaCentroCostoSf178
     */
    public void cargarListaCentroCostoSf178E()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL13603
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCentroCostoSf178E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaTerceroSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaTerceroSf178
     */
    public void cargarListaTerceroSf178()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL15005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroSf178 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");

        // 14126
    }

    /**
     *
     * Carga la lista listaTerceroSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaTerceroSf178E
     */
    public void cargarListaTerceroSf178E()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL15005
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroSf178E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");

        //
    }

    /**
     *
     * Carga la lista listaTarifaSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaTarifaSf178
     */
    public void cargarListaTarifaSf178()
    {
        Map<String, Object> param = new TreeMap<>();
        if (visibleTarifaBase) {
            
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);
            
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FacturacionconceptosControladorUrlEnum.URL1711
                                                            .getValue());
            listaTarifaSf178 = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());
            
        } else {
            
        if (tarifaConcepRela)
        {

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);
            param.put(GeneralParameterEnum.CONCEPTO.getName(),
                            registroSub.getCampos().get(conceptoCons));
        }
        else
        {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);

        }

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL16185
                                                        .getValue());
        listaTarifaSf178 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
        }

    }

    /**
     *
     * Carga la lista listaTarifaSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaTarifaSf178E
     */
    public void cargarListaTarifaSf178E()
    {

        Map<String, Object> param = new TreeMap<>();
        
        if (visibleTarifaBase) {
            
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);
            
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FacturacionconceptosControladorUrlEnum.URL1711
                                                            .getValue());
            listaTarifaSf178 = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());
            
        } else {
        
        if (tarifaConcepRela)
        {

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);
            param.put(GeneralParameterEnum.CONCEPTO.getName(),
                            conceptoGrillaAux);
        }
        else
        {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(tipoCobroCons, tipoCobro);

        }

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL16185
                                                        .getValue());
        listaTarifaSf178E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
        }

    }

    /**
     *
     * Carga la lista listaConceptoSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaConceptoSf178
     */
    public void cargarListaConceptoSf178()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipoCobroCons, tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean;
    	if(visibleRelacionContrato)
        {
    		param.put("CONTRATO", contrato);
    		
    		urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                                    FacturacionconceptosControladorUrlEnum.URL18022
                                                    .getValue());
        }
    	else
    	{
    		urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL18021
                                                        .getValue());
    	}

        listaConceptoSf178 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 663019
    }

    /**
     *
     * Carga la lista listaConceptoSf178
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaConceptoSf178E
     */
    public void cargarListaConceptoSf178E()
    {
    	UrlBean urlBean;
    	
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipoCobroCons, tipoCobro);
    	
    	if(visibleRelacionContrato)
        {
    		param.put("CONTRATO", contrato);
    		urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                                    FacturacionconceptosControladorUrlEnum.URL18022
                                                    .getValue());
        }
    	else
    	{
	    	urlBean = UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(
	                                        FacturacionconceptosControladorUrlEnum.URL18021
	                                                        .getValue());
        }       

        listaConceptoSf178E = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaReferencia
     *
     */
    public void cargarListaReferencia()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL17101
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaReferencia
     *
     */
    public void cargarListaReferenciaE()
    {
        listaReferenciaE = listaReferencia;
    }

    /**
     *
     * Carga la lista listaFuenteRecurso
     *
     */
    public void cargarListaFuenteRecurso()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL15596
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaFuenteRecurso
     *
     */
    public void cargarListaFuenteRecursoE()
    {
        listaFuenteRecursoE = listaFuenteRecurso;
    }

    /**
     *
     * Carga la lista listaNit
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaNit
     */
    public void cargarListaNit()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL20067
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNit = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");

        // 14128
    }

    /**
     *
     * Carga la lista listaAfectarContrato
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaAfectarContrato
     */
    public void cargarListaAfectarContrato()
    {    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL41460
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TERCERO.getName(), 
        		SysmanFunciones
                .nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()),""));

        listaAfectarContrato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "CONTRATO");

        // 664006
    }

    /**
     *
     * Carga la lista listaCodTarifaBase
     *
     */
    public void cargarListaCodTarifaBase()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL11370
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(tipoCobroCons, tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCodTarifaBase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaFuentesRecurso
     *
     */
    public void cargarListaFuentesRecurso()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL8740
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaFuentesRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaCentroCosto
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaCentroCosto
     */
    public void cargarListaCentroCosto()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL22766
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 20059
    }

    /**
     *
     * Carga la lista listaAuxiliar
     *
     * Metodo encargado de hacer el llamado a la base datos y
     * almacenar los datos en la lista listaAuxiliar
     */
    public void cargarListaAuxiliar()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL23432
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 23028
    }

    /**
     * carga lista de contratos por tercero seleccionado
     */
    public void cargarListaCONTRATOSTERCERO() {
    	try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.TERCERO.getName(), registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString());
            listaCONTRATOSTERCERO = RegistroConverter
                    .toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL664021
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
	
	/**
	 * 
	 * Carga la lista listaTipoContrato
	 *
	 */
	public void cargarListaTipoContrato() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		UrlBean urlBean;

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FacturacionconceptosControladorUrlEnum.URL73006.getValue());

		listaTipoContrato = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
		
	}

	/**
	 * 
	 * Carga la lista listaNumeroContrato
	 *
	 */
	public void cargarListaNroContrato() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPOCONTRATOINICIAL.getName(),
				SysmanFunciones.nvl(registro.getCampos().get(FacturacionconceptosControladorEnum.TIPOCONTRATOSIGEC.getValue()), ""));
		param.put(GeneralParameterEnum.TIPOCONTRATOFINAL.getName(),
				SysmanFunciones.nvl(registro.getCampos().get(FacturacionconceptosControladorEnum.TIPOCONTRATOSIGEC.getValue()), ""));
		param.put(GeneralParameterEnum.TERCEROINICIAL.getName(),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), ""));
		param.put(GeneralParameterEnum.TERCEROFINAL.getName(),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), ""));

		UrlBean urlBean;

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FacturacionconceptosControladorUrlEnum.URL82121.getValue());

		listaNroContrato = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NUMERO.getName());
	}

    /**
     * Metodo ejecutado al cambiar el control TerceroSf178 en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTerceroSf178C(int rowNum)
    {

        listaSubfacturacionconceptosbg.get(rowNum).getCampos()
                        .put(GeneralParameterEnum.TERCERO.getName(), auxiliar);
        listaSubfacturacionconceptosbg.get(rowNum).getCampos().put(
                        GeneralParameterEnum.SUCURSAL.getName(),
                        sucursalTerceroGrilla);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CANTIDAD
     *
     *
     *
     */
    public void cambiarCANTIDAD()
    {
        // <CODIGO_DESARROLLADO>
        String conceptoAux = registroSub.getCampos().get(conceptoCons)
                        .toString();

        obtenerRegistroConceptos(conceptoAux);
        
        if(aplicaFacturaDolares)
        {
        	cambiarValorUnitarioDolares();
        }
        
        if(visibleTarifaTercero)
        {
        	cambiarValorUnitarioNumeroLista();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCANTIDADC(int rowNum)
    {

        registroSub.getCampos().put("BASE_FIJA",
                        listaSubfacturacionconceptosbg.get(rowNum).getCampos()
                                        .get("BASE_FIJA"));

        String conceptoAux = listaSubfacturacionconceptosbg.get(rowNum)
                        .getCampos().get(conceptoCons).toString();

        registroSub.getCampos().put(cantidadCons,
                        listaSubfacturacionconceptosbg.get(rowNum).getCampos()
                                        .get(cantidadCons));

        obtenerRegistroConceptos(conceptoAux);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_BASE
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorCompraCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_NETO
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_IMPOCONSUMO
                                        .getValue());
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),valorIvaCons);
        
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),valorReteIva);
        
        
        if(aplicaFacturaDolares)
        {
        	cambiarValorUnitarioDolares();
        }
        
        if(visibleTarifaTercero)
        {
        	cambiarValorUnitarioNumeroLista();
        }

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorUnitario
     *
     *
     */
    public void cambiarValorUnitario()
    {
        // <CODIGO_DESARROLLADO>
        cambioValorUnitario = true;
        String conceptoAux = registroSub.getCampos().get(conceptoCons)
                        .toString();
        obtenerRegistroConceptos(conceptoAux);        
        cambioValorUnitario = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorUnitario en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorUnitarioC(int rowNum)
    {
        cambioValorUnitario = true;
        String conceptoAux = listaSubfacturacionconceptosbg.get(rowNum)
                        .getCampos().get(conceptoCons).toString();

        registroSub.getCampos().put(
                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                        .getValue(),
                        listaSubfacturacionconceptosbg.get(rowNum).getCampos()
                                        .get(FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                        .getValue()));
        obtenerRegistroConceptos(conceptoAux);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_BASE
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorCompraCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_NETO
                                        .getValue());
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),valorIvaCons);
        
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),valorReteIva);

    }
    
    /**
     * Metodo ejecutado al cambiar el control AjusteDecimal
     *
     *
     */
    public void cambiarAjusteDecimal()
    {
    	double valorAjusteDecimal = retornarDouble(registroSub,
                FacturacionconceptosControladorEnum.AJUSTE_DECIMAL
                                .getValue());
    	
    	double valorNeto = retornarDouble(registroSub,
                FacturacionconceptosControladorEnum.VALOR_NETO
                                .getValue());
    	
    	double valorTotal = valorNeto+valorAjusteDecimal;
    	
    	registroSub.getCampos().put(
                FacturacionconceptosControladorEnum.VALOR_NETO
                                .getValue(), valorTotal);    	
    }
    
    /**
     * Metodo ejecutado al cambiar el control AjusteDecimal
     *
     *
     */
    public void cambiarAjusteDecimalC(int rowNum)
    {
    	double valorAjusteDecimal = retornarDouble(listaSubfacturacionconceptosbg.get(rowNum),
                FacturacionconceptosControladorEnum.AJUSTE_DECIMAL
                                .getValue());
    	
    	double valorNeto = retornarDouble(listaSubfacturacionconceptosbg.get(rowNum),
                FacturacionconceptosControladorEnum.VALOR_NETO
                                .getValue());
    	
    	double valorTotal = valorNeto+valorAjusteDecimal;
    	
    	registroSub.getCampos().put(
                FacturacionconceptosControladorEnum.VALOR_NETO
                                .getValue(), valorTotal);  
    	
    	agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                FacturacionconceptosControladorEnum.VALOR_NETO
                                .getValue());
    }
    
    /**
     * Metodo ejecutado al cambiar el control ValorUnitario
     *
     *
     */
    public void cambiarValorUnitarioDolares()
    {
		double valorUnitarioDolares = retornarDouble(registroSub,
                FacturacionconceptosControladorEnum.VALOR_UNITARIO_DOLARES
                                .getValue());
    	
    	double cantidad = Double
                .parseDouble(SysmanFunciones
                                .nvl(registroSub.getCampos().get(
                                                cantidadCons), "0")
                                .toString());
    	
    	double valorTRM =  Double
                .parseDouble(SysmanFunciones
                        .nvl(registro.getCampos().get("TRM_DOLARES_FAC"), "0")
                        .toString());
    	
    	double valorBaseF = valorUnitarioDolares*valorTRM;
    	
    	double valorTotalDolares = valorUnitarioDolares*cantidad;
    	
    	double valorTotal = valorBaseF*cantidad;
    	
    	registroSub.getCampos().put(
                FacturacionconceptosControladorEnum.BASE_FIJA
                                .getValue(), valorBaseF);
    	
    	registroSub.getCampos().put(
                FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                .getValue(), valorBaseF);
    	registroSub.getCampos().put(
                FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                .getValue(), valorBaseF);
    	
    	registroSub.getCampos().put(
                FacturacionconceptosControladorEnum.VALOR_TOTAL_DOLARES
                                .getValue(), valorTotalDolares); 
    	
    	registroSub.getCampos().put(
                FacturacionconceptosControladorEnum.VALOR_BASE
                                .getValue(), valorTotal);
    	
    	cambiarValorUnitario(); 
    }
    
    public void cambiarValorUnitarioDolaresC(int rowNum)
    {
    	cambiarValorUnitarioDolares();
    }    

    /**
     * Metodo ejecutado al cambiar el control ValorBase
     *
     *
     */
    public void cambiarValorBase()
    {
        // <CODIGO_DESARROLLADO>
        String conceptoAux = registroSub.getCampos().get(conceptoCons)
                        .toString();
        logicaCambiarValorBase(conceptoAux);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorBaseFijo
     *
     *
     */
    public void cambiarValorBaseFijo()
    {
        // <CODIGO_DESARROLLADO>
        cambioConcepto = true;
		obtenerRegistroConceptos(registroSub.getCampos().get(conceptoCons));
        cambioConcepto = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorBase en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorBaseC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        String conceptoAux = listaSubfacturacionconceptosbg.get(rowNum)
                        .getCampos().get(conceptoCons).toString();

        registroSub.getCampos().put(
                        FacturacionconceptosControladorEnum.VALOR_BASE
                                        .getValue(),
                        listaSubfacturacionconceptosbg.get(rowNum).getCampos()
                                        .get(FacturacionconceptosControladorEnum.VALOR_BASE
                                                        .getValue()));
        logicaCambiarValorBase(conceptoAux);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_BASE
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_NETO
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorIvaCons);
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorRetefuenteCons);
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorDescuentoCons);
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
        		valorReteIva);
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valoricaCons);
        
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),valorReteIva);
        

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorBaseFijo en la fila
     * seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorBaseFijoC(int rowNum)
    {

        cambioBaseFija = true;

        auxiliarBaseFija = SysmanFunciones.nvlDbl(listaSubfacturacionconceptosbg
                        .get(rowNum).getCampos()
                        .get(FacturacionconceptosControladorEnum.BASE_FIJA
                                        .getValue()),
                        0.0);

        extraerValorTarifa(listaSubfacturacionconceptosbg.get(rowNum)
                        .getCampos().get(tarifaCons));

        obtenerRegistroConceptos(listaSubfacturacionconceptosbg.get(rowNum)
                        .getCampos().get(conceptoCons));

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_BASE
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorCompraCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorIvaCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorRetefuenteCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorDescuentoCons);
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
        		        valorReteIva);
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valoricaCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_NETO
                                        .getValue());

        cambioBaseFija = false;
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CantidadGrupo
     *
     *
     */
    public void cambiarCantidadGrupo()
    {
        // <CODIGO_DESARROLLADO>

        validarCantidadGrupo();

        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control factura dolares
     * 
     */
    public void cambiarFacturaDolares() {
        if ((boolean) registro.getCampos()
                        .get(FacturacionconceptosControladorEnum.FACTURA_DOLARES.getValue())) {

        	aplicaFacturaDolares = true;
        }
        else {
        	aplicaFacturaDolares = false;
        }
    }

    public void cambiarValorUnitarioNumeroLista(){
    	
    	try {
    		//CC_1378 MROSERO 04/2025
			Map<String, Object> param = new TreeMap<>();
			Date fechaSolicitud = (Date) registro.getCampos()
					.get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD.getValue());
			SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
        
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);					
			param.put("NUMEROLISTA", SysmanFunciones.nvl(registroSub.getCampos().get(GeneralParameterEnum.NUMEROLISTA.getName()), ""));
			param.put("CONCEPTO", SysmanFunciones.nvl(registroSub.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()), ""));
			param.put("FECHA", formatFecha.format(fechaSolicitud));

			Registro reg = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FacturacionconceptosControladorUrlEnum.URL001.getValue())
											.getUrl(),
									param));

			registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_UNITARIO.getValue(),
					reg.getCampos().get(FacturacionconceptosControladorEnum.VALOR.getValue()));

		
		double valorUnitarioLista = retornarDouble(reg,
                FacturacionconceptosControladorEnum.VALOR
                                .getValue());
    	
    	double cantidad = Double
                .parseDouble(SysmanFunciones
                                .nvl(registroSub.getCampos().get(
                                                cantidadCons), "0")
                                .toString());
    

    	double valorTotalLista = valorUnitarioLista*cantidad;
    	    	
			registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_UNIDAD.getValue(), valorUnitarioLista);

			registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_BASE.getValue(), valorUnitarioLista);

			registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_NETO.getValue(), valorTotalLista);

			registroSub.getCampos().put(FacturacionconceptosControladorEnum.BASE_FIJA.getValue(), valorTotalLista);

    	
		} catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
    	cambiarValorUnitario(); 
    	
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * CONTRATO en la vista
     *
     *
     */
    public void aceptarContrato()
    {
        // <CODIGO_DESARROLLADO>
        dialogoVisible = false;
        asignarValoresContrato();
        // </CODIGO_DESARROLLADO>
    }
    
   public void aceptarconsecutivo() {
	   dialogoConsecutivo = false;
	   try {
		consecutivoobjetocobro = ejbFactGeneralCuatro
		           .consecutivoobj(compania, Integer.parseInt(ano),tipoCobro);
		 
		 registro.getCampos().put("CODIGO_COBRO", consecutivoobjetocobro);
	   } catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			   
   }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * recaudar en la vista
     *
     */
    public void aceptarrecaudar()
    {
        dialogoVisibleRecaudar = false;
        oprimirCmdFacturar();

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * recaudar en la vista
     *
     */
    public void cancelarrecaudar()
    {
        // <CODIGO_DESARROLLADO>
        dialogoVisibleRecaudar = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmdRecaudo
     * 
     */
    public void retornarFormulariocmdRecaudo(SelectEvent event)
    {
        cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        agregarRegistroNuevo(false);
    }

    /**
     * Metodo ejecutado al cambiar el control cmbCambiar
     *
     *
     */
    public void retornarFormulariocmbCambiar(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        if (event.getObject() == null)
        {
            return;
        }
        Direccionador direccionador = (Direccionador) event.getObject();
        String ruta = direccionador.getRuta();
        if (direccionador.getNumForm() != null)
        {
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else if ((ruta != null) && (direccionador.getParametros() != null))
        {
            SessionUtil.redireccionar(direccionador);
        }
        else if (ruta != null)
        {
            SessionUtil.redireccionar(ruta);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EstratoSf178 en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEstratoSf178C(int rowNum)
    {

        Object conceptoAux = listaSubfacturacionconceptosbg.get(rowNum)
                        .getCampos().get(conceptoCons);

        listaSubfacturacionconceptosbg.get(rowNum).getCampos()
                        .put(GeneralParameterEnum.ESTRATO.getName(), auxiliar);

        registroSub.getCampos().put(
                        FacturacionconceptosControladorEnum.BASE_FIJA
                                        .getValue(),
                        listaSubfacturacionconceptosbg.get(rowNum).getCampos()
                                        .get(FacturacionconceptosControladorEnum.BASE_FIJA
                                                        .getValue()));

        if (SysmanFunciones.validarVariableVacio(auxiliar))
        {
            Registro regVacio = new Registro();
            logicaSeleccionarEstrato(regVacio, conceptoAux);
        }
        else
        {

            Registro estrato = retornarRegistroEstrato(auxiliar);

            if (estrato != null)
            {

                logicaSeleccionarEstrato(estrato, conceptoAux);
            }
        }

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_BASE
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorCompraCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_NETO
                                        .getValue());

        cambioEstrato = false;

    }

    /**
     * Metodo ejecutado al cambiar el control TarifaSf178 en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTarifaSf178C(int rowNum)
    {
        // <CODIGO_DESARROLLADO>

        cambioTarifa = true;

        Object conceptoAux = listaSubfacturacionconceptosbg.get(rowNum)
                        .getCampos().get(conceptoCons);

        listaSubfacturacionconceptosbg.get(rowNum).getCampos().put(tarifaCons,
                        auxiliar);

        registroSub.getCampos().put(
                        FacturacionconceptosControladorEnum.BASE_FIJA
                                        .getValue(),
                        listaSubfacturacionconceptosbg.get(rowNum).getCampos()
                                        .get(FacturacionconceptosControladorEnum.BASE_FIJA
                                                        .getValue()));

        if (SysmanFunciones.validarVariableVacio(auxiliar))
        {
            Registro regVacio = new Registro();
            logicaSeleccionarTarifa(regVacio, conceptoAux);
        }
        else
        {

            Registro tarifa = retornarRegistroTarifa(auxiliar);

            if (tarifa != null)
            {

                logicaSeleccionarTarifa(tarifa, conceptoAux);
            }
        }

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_BASE
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorCompraCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                        .getValue());

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorIvaCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorRetefuenteCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valorDescuentoCons);
        
        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
        		        valorReteIva);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        valoricaCons);

        agregarValoresRegistroSub(listaSubfacturacionconceptosbg.get(rowNum),
                        FacturacionconceptosControladorEnum.VALOR_NETO
                                        .getValue());

        cambioTarifa = false;
    }
    
    /**
     * Metodo ejecutado al cambiar el control ConceptoSf178C en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarConceptoSf178C(int rowNum)
    {
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * EFECTIVO en la vista
     *
     *
     */
    public void aceptarEFECTIVO()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEstratoSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEstratoSf178(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registroSub.getCampos().put(GeneralParameterEnum.ESTRATO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        logicaSeleccionarEstrato(registroAux,
                        registroSub.getCampos().get(conceptoCons));
        cambioEstrato = false;
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEstratoSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEstratoSf178E(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarSf178(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarSf178E(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoSf178(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoSf178E(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroSf178(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos().get("NIT"));
        registroSub.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroSf178E(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = retornarString(registroAux, "NIT");

        sucursalTerceroGrilla = retornarString(registroAux,
                        GeneralParameterEnum.SUCURSAL.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTarifaSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTarifaSf178(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(tarifaCons, registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        aplicaTarifa = true;

        logicaSeleccionarTarifa(registroAux,
                        registroSub.getCampos().get(conceptoCons));
        cambioTarifa = false;
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTarifaSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTarifaSf178E(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoSf178(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        cambioConcepto = true;

        registroSub.getCampos().put(conceptoCons, registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        
        registroSub.getCampos().put(
                FacturacionconceptosControladorEnum.AJUSTE_DECIMAL
                                .getValue(),0);
        
        codigoConcepto = registroAux.getCampos()
                .get(GeneralParameterEnum.CODIGO.getName()).toString();

        registroSub.getCampos().put(nombreConceptoCons,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        if(visibleTarifaTercero){
        	registroSub.getCampos().put(numeroLista,ObtenerNumeroLista("codnumeroLista"));
        }
        
        if(visibleRelacionContrato)
        {        	
        	registroSub.getCampos().put(valorDescuentoCons, registroAux.getCampos()
                    .get(valorDescuentoCons));//JM CC 2693
        	
        	dctoContrato =   Double.parseDouble(registroAux.getCampos().get(valorDescuentoCons).toString());//JM CC 2693
        	
        	registroSub.getCampos().put(cantidadCons, registroAux.getCampos()
                    .get(cantidadCons));
        	registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_UNITARIO
                    .getValue(), registroAux.getCampos()
                    .get(FacturacionconceptosControladorEnum.VALOR_UNITARIO
                            .getValue()));
        	registroSub.getCampos().put(tarifaCons, registroAux.getCampos()
                            .get(tarifaCons));
        	registroSub.getCampos().put(FacturacionconceptosControladorEnum.BASE_FIJA
        	            .getValue(), registroAux.getCampos()
        	                    .get(FacturacionconceptosControladorEnum.VALOR_BASE
        	                                    .getValue()));
        	registroSub.getCampos().put(FacturacionconceptosControladorEnum.CENTRO_UTILIDAD
                    .getValue(), registroAux.getCampos()
                    .get(FacturacionconceptosControladorEnum.CENTRO_UTILIDAD
                            .getValue())); 
        	registroSub.getCampos().put(FacturacionconceptosControladorEnum.NOMBRE_CENTRO_UTILIDAD
                    .getValue(), registroAux.getCampos()
                    .get(FacturacionconceptosControladorEnum.NOMBRE_CENTRO_UTILIDAD
                            .getValue())); 

        }
        else
        {
        	registroSub.getCampos().put(cantidadCons,
                    registro.getCampos().get(cantidadGrupoCons));
        	registroSub.getCampos().put(
                                FacturacionconceptosControladorEnum.BASE_FIJA
                                                .getValue(),
                                registro.getCampos().get("BASE_FIJA_ANT"));
        }

        prepararTarifa(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

        registroSub.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.TERCERO
                                        .getName()));

        registroSub.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registro.getCampos().get(GeneralParameterEnum.SUCURSAL
                                        .getName()));

        extraerValorTarifa(registroSub.getCampos().get(tarifaCons));

        cargarListaTarifaSf178();
        prepararEstrato(registroSub.getCampos().get(conceptoCons));
        cargarListaEstratoSf178();
        bloqueaComponentes(registroAux);
        obtenerRegistroConceptos(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        
        if(aplicaFacturaDolares)
        {
        	registroSub.getCampos().put(
	                FacturacionconceptosControladorEnum.VALOR_BASE
	                                .getValue(),0);
	        
	        registroSub.getCampos().put(
	                FacturacionconceptosControladorEnum.VALOR_UNITARIO
	                                .getValue(),0);
        	
	        registroSub.getCampos().put(
	                FacturacionconceptosControladorEnum.VALOR_UNITARIO_DOLARES
	                                .getValue(),0);
	        
	        registroSub.getCampos().put(
	                FacturacionconceptosControladorEnum.VALOR_TOTAL_DOLARES
	                                .getValue(),0);
        }
        
        if(visibleRelacionContrato)
        {
        	try {
				saldoActualConcepto =  String.valueOf(ejbFactGenTres
				        .obtenerSaldoConcepto(compania,
						        tipoCobro,
						        registroSub.getCampos().get(conceptoCons).toString(),
						        new BigInteger(contrato)));
				
				JsfUtil.agregarMensajeInformativoDialogo(
	                    "El saldo actual para este concepto es de: "+ saldoActualConcepto.toString());
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }
        
        if(visibleTarifaTercero)
        {

        	registroSub.getCampos().put(
	                FacturacionconceptosControladorEnum.VALOR_BASE
	                                .getValue(),0);
	        
	        registroSub.getCampos().put(
	                FacturacionconceptosControladorEnum.VALOR_UNITARIO
	                                .getValue(),0);
        	
	        registroSub.getCampos().put(
	                FacturacionconceptosControladorEnum.VALOR_NETO
	                                .getValue(),0);
	        
	        registroSub.getCampos().put(
	                FacturacionconceptosControladorEnum.VALOR_UNIDAD
	                                .getValue(),0);	  
	        if (!registroSub.getCampos().get(GeneralParameterEnum.NUMEROLISTA.getName()).equals("")){
	        cambiarValorUnitarioNumeroLista();
	        }  
        }       

        cambioConcepto = false;

    }

    
    private String ObtenerNumeroLista(String codnumeroLista) {
    	
    	Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCERO.getName(),
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), ""));

         try {
			regNumeroLista = RegistroConverter
			                 .toRegistro(requestManager.get(
			                                 UrlServiceUtil.getInstance()
			                                                 .getUrlServiceByUrlByEnumID(
			                                                		 FacturacionconceptosControladorUrlEnum.URL005.getValue())
			                                                 .getUrl(),
			                                 param));
		} catch (SystemException e1) {
			e1.printStackTrace();
		}

         if (regNumeroLista != null)
         {
        	 
             codnumeroLista = SysmanFunciones.nvl(regNumeroLista.getCampos().get("CODIGO"), "0").toString();
             
         }else {
        	 
        	 codnumeroLista = "";
        	 
         }
    	
		return codnumeroLista;    	
    }
    
    
    private void extraerValorTarifa(Object tarifa)
    {
        Registro regTarifa = retornarRegistroTarifa(tarifa);

        if (regTarifa != null)
        {

            if ((boolean) regTarifa.getCampos().get(tomatVlrCons))
            {
                vlrTarifa = retornarDouble(regTarifa,
                                GeneralParameterEnum.PORCENTAJE.getName());

            }
            else
            {
                vlrTarifa = SysmanFunciones.nvlDbl(regTarifa.getCampos()
                                .get(GeneralParameterEnum.PORCENTAJE.getName()),
                                100)
                    / 100;
            }
        }
        else
        {
            vlrTarifa = 1;
        }

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoSf178
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoSf178E(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        cambioConcepto = true;
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
        prepararTarifa(auxiliar);
        cargarListaTarifaSf178E();

        cambioConcepto = false;
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferencia(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("REFERENCIA", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecurso(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE_RECURSO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecursoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaNit
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNit(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(nitTerceroFacturadoCons,
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("NOMBRE_TERCEROFACTURADO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(nombreTerceroCons, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

        registro.getCampos().put(direccionCons,
                        registroAux.getCampos().get(direccionCons));
        registro.getCampos().put(telefonosCons,
                        registroAux.getCampos().get(telefonosCons));
        registro.getCampos().put(codigoPostalCons,
                        registroAux.getCampos().get(codigoPostalCons));

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        registro.getCampos().put("SUCURSAL_TERCEROFACTURADO",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        registro.getCampos().put("EMAIL",
                registroAux.getCampos().get(
                                GeneralParameterEnum.DIRECCIONEMAIL.getName()));
		if (visibleSigec) {

			cargarListaNroContrato();
		}	

        if (visibleTarifaTercero) {
			validacionFechaVencimiento();
			validacionNumeroLista();
		}

		validarTerceroVarios(registroAux);
		cargarListaAfectarContrato();
		validaSujetoaReportarContrato();
		cargarListaCONTRATOSTERCERO();
	}
    
    public void actulizarTercero()
    {
    	
    	Map<String, Object> parametros = new HashMap<>();
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.ANO.getName(), ano);
		parametros.put(GeneralParameterEnum.TIPOCOBRO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TIPOCOBRO.getName()));
		parametros.put("CODIGO_COBRO", registro.getCampos().get("CODIGO_COBRO"));
		parametros.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		parametros.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		
		String ulrEnumId = FacturacionconceptosControladorUrlEnum.URL675006.getValue();
		UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ulrEnumId);
		Parameter parameter = new Parameter();
		parameter.setFields(parametros);
		try {
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		 cargarListaSubfacturacionconceptosbg();
    	
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAfectarContrato
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAfectarContrato(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPOCONTRATO",
                registroAux.getCampos().get("TIPO"));
        
        registro.getCampos().put("CONTRATO",
                        registroAux.getCampos().get("NUMERO"));
        
        registro.getCampos().put("OBJETO_CONTRATO",
                registroAux.getCampos().get("OBJETO_CONTRATO"));
        
        registro.getCampos().put("OBSERVACIONES",
                registroAux.getCampos().get("OBJETO_CONTRATO"));

        contrato = registroAux.getCampos().get("NUMERO").toString();     
        
        registro.getCampos().put("TIPO_PAGO",
                registroAux.getCampos().get("EQUIVALENTE_DIAN"));
          
        registro.getCampos().put("DESCRIPCION_TIPOPAGO",
                registroAux.getCampos().get("NOMBRE"));
        
        registro.getCampos().put("DIAS",
                registroAux.getCampos().get("DIAS"));        
        
        objetoContrato = registro.getCampos().put("OBSERVACIONES",
                registroAux.getCampos().get("OBJETO_CONTRATO")).toString();
        
        cargarListaConceptoSf178();
        cargarListaConceptoSf178E();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodDescripcion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodDescripcion(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COD_DESCRIPCION", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(
                        FacturacionconceptosControladorEnum.OBSERVACIONES
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodTarifaBase
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodTarifaBase(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COD_TARIFABASE", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put("BASE_FIJA_ANT",
                        registroAux.getCampos().get("VALOR"));

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuentesRecurso
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuentesRecurso(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE_RECURSO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(nombreFuenteCons, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCosto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(nombreCentroCostoCons,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliar
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliar(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(nombreAuxiliarCons,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRuta
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CODIGORUTA.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGORUTA
                                                        .getName()));
    }

    public void cambiarnumerodeLista() {

		try {
			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);					
			param.put("NUMEROLISTA", SysmanFunciones.nvl(registroSub.getCampos().get(GeneralParameterEnum.NUMEROLISTA.getName()), ""));
			param.put("CONCEPTO", SysmanFunciones.nvl(registroSub.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()), ""));

			Registro reg = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FacturacionconceptosControladorUrlEnum.URL001.getValue())
											.getUrl(),
									param));

			registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_UNITARIO.getValue(),
					reg.getCampos().get(FacturacionconceptosControladorEnum.VALOR.getValue()));
			

			registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_BASE.getValue(),
					reg.getCampos().get(FacturacionconceptosControladorEnum.VALOR.getValue()));
			

			registroSub.getCampos().put(FacturacionconceptosControladorEnum.VALOR_NETO.getValue(),
					reg.getCampos().get(FacturacionconceptosControladorEnum.VALOR.getValue()));
		} catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

    public void cambiarnumerodeListaC(int rowNum) {
         
         registroSub.getCampos().put(numeroLista,
                 listaSubfacturacionconceptosbg.get(rowNum).getCampos()
                                 .get(numeroLista));
        
   }
	public void seleccionarFilaTipoContrato(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TIPOCONTRATOSIGEC",
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		cargarListaNroContrato();
	}	
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listanoContrato
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */

	public void seleccionarFilaNroContrato(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("NROCONTRATOSIGEC",
				registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
	}    
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton CmdCopiaFactura en la
     * vista
     *
     *
     */
    public void oprimirCmdCopiaFactura()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (!existeDetalles())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3884"));

        }
        else
        {

            String nroFactura = retornarString(registro, nroFacturaCons);
          //  boolean impreso = (boolean) registro.getCampos().get(impresoCons);
            boolean impreso = Boolean.parseBoolean(registro.getCampos().get(impresoCons).toString());

            if (SysmanFunciones.validarVariableVacio(nroFactura) || !impreso)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3881"));
                return;
            }
            if (valorNetoValidacion <= 0)
            {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(mensajeProperties));
                return;
            }

            genInforme(FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto actualizarForm en
     * la vista
     *
     *
     */
    public void ejecutaractualizarForm()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(impresoCons, true);
        agregarRegistroNuevo(false);
        cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmdCargarConceptos en la
     * vista
     *
     *
     */
    public void oprimircmdCargarConceptos()
    {
        // <CODIGO_DESARROLLADO>

        if (!validarCantidadGrupo())
        {
            return;
        }

        try
        {
            agregarRegistroNuevo(false);

            String cantidad = retornarString(registro, cantidadGrupoCons);
            String grupo = retornarString(registro, "GRUPO_CONCEPTOS");

            Date fechaSolicitud = (Date) registro.getCampos()
                            .get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD
                                            .getValue());
            String codigoCobro = retornarString(registro, codigoCobroCons);

            if ("0".equals(grupo)
                || SysmanFunciones.validarVariableVacio(grupo))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3884"));
                return;
            }

            if (SysmanFunciones.validarVariableVacio(cantidad)
                || (Integer.parseInt(cantidad) < 0))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3885"));
                return;

            }

            ejbFactGenUno.cargarConceptos(compania, Integer.parseInt(ano),
                            tipoCobro, codigoCobro,
                            Integer.parseInt(grupo), fechaSolicitud,
                            Integer.parseInt(cantidad),
                            SessionUtil.getUser().getCodigo());
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3368"));
        }
        catch (NumberFormatException | SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmdRecaudo en la vista
     *
     *
     */
    public void oprimircmdRecaudo()
    {
        // <CODIGO_DESARROLLADO>

        if (validarFacturacion())
        {

            if (valorUnidadAux >= 0)
            {

                if ((boolean) registro.getCampos().get("INDPAGO"))
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3909"));
                }
                else
                {
                    try
                    {
                        Map<String, Object> param = new TreeMap<>();

                        param.put(GeneralParameterEnum.COMPANIA.getName(),
                                        compania);

                        param.put("TIPO_FACTURA", tipoCobro);

                        param.put("NUMERO_FACTURA", retornarString(registro,
                                        nroFacturaCons));

                        Registro regFactura = RegistroConverter
                                        .toRegistro(requestManager.get(
                                                        UrlServiceUtil.getInstance()
                                                                        .getUrlServiceByUrlByEnumID(
                                                                                        FacturacionconceptosControladorUrlEnum.URL12942
                                                                                                        .getValue())
                                                                        .getUrl(),
                                                        param));

                        String[] campos = { "tipoCobro", "codigoCobro",
                                            "nroFactura", "fechaVencimiento",
                                            "vlrFactura",
                                            "tercero", "nombreTercero",
                                            "bancoPago", "nombreBanco", "tipoC",
                                            "tipoAbono", "abono",
                                            "indInterfazada", "indDiferida","indPagosParciales",
                                            "observaciones", "anio","aplicaFacturaDolares", "vlrTRMFactura","fecha_expedicion" };

                        String[] valores = { tipoCobro,
                                             retornarString(registro,
                                                             codigoCobroCons),
                                             retornarString(registro,
                                                             nroFacturaCons),
                                             SysmanFunciones.convertirAFechaCadena(
                                                             (Date) regFactura
                                                                             .getCampos()
                                                                             .get(FacturacionconceptosControladorEnum.FECHA_VENCIMIENTO
                                                                                             .getValue()),
                                                             "dd/MM/yyyy"),
                                             retornarString(regFactura,
                                                             "VALOR_TOTAL"),
                                             retornarString(regFactura,
                                                             GeneralParameterEnum.TERCERO
                                                                             .getName()),
                                             retornarString(regFactura,
                                                             GeneralParameterEnum.NOMBRE
                                                                             .getName()),
                                             retornarString(regFactura, ""),
                                             retornarString(regFactura,
                                                             "NOMBRE_BANCO"),
                                             retornarString(regFactura,
                                                             "TIPO_FACTURA"),
                                             retornarString(regFactura,
                                                             "TIPO_ABONO"),
                                             retornarString(regFactura,
                                                             "NRO_ABONO"),
                                             retornarString(regFactura,
                                                             "INTERFAZADA"),
                                             retornarString(regFactura,
                                                             "DIFERIDA"),
                                             retornarString(regFactura, "PAGOS_PARCIALES"),
                                             retornarString(regFactura,
                                                             FacturacionconceptosControladorEnum.OBSERVACIONES
                                                                             .getValue()),
                                             ano,
                                             String.valueOf(aplicaFacturaDolares),
                                             retornarString(regFactura,
                                                     "TRM_DOLARES_FAC"),
                                             SysmanFunciones.convertirAFechaCadena(
                                                     (Date) regFactura
                                                                     .getCampos()
                                                                     .get(FacturacionconceptosControladorEnum.FECHA_EXPEDICION
                                                                                     .getValue()),
                                                     "dd/MM/yyyy"),};

                        SessionUtil.cargarModalDatosFlash(
                                        String.valueOf(GeneralCodigoFormaEnum.FRM_REGISTRO_PAGOS_CONTROLADOR
                                                        .getCodigo()),
                                        SessionUtil.getModulo(), campos,
                                        valores);

                    }
                    catch (SystemException | ParseException e)
                    {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }

            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3910"));
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarFacturacion()
    {
        if (anulada)
        {
            dialogoVisibleRecaudar = false;
            return false;
        }
        else
        {
            if (SysmanFunciones
                            .validarVariableVacio(SysmanFunciones
                                            .nvl(registro.getCampos().get(
                                                            nroFacturaCons), "")
                                            .toString())
                || SysmanFunciones.validarVariableVacio(
                                SysmanFunciones.nvl(
                                                registro.getCampos().get(
                                                                "TIPOFACTURA"),
                                                "").toString())
                || !(boolean) registro.getCampos().get(impresoCons))
            {

                dialogoVisibleRecaudar = true;

                return false;
            }

            return true;
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmbTercero en la vista
     *
     *
     */
    public void oprimirCmbTercero()
    {
        agregarRegistroNuevo(false);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rowidFacturacionConcepto", registro.getLlave());
        parametros.put("anio", ano);
        parametros.put("tipoCobro", tipoCobro);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String
                        .valueOf(GeneralCodigoFormaEnum.TERCEROS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmbCambiar en la vista
     *
     *
     */
    public void oprimircmbCambiar()
    {
        // <CODIGO_DESARROLLADO>

        SessionUtil.cargarModal(String.valueOf(
                        GeneralCodigoFormaEnum.FRMSELECCIONARTIPOCOBROS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton DiferirFactura en la vista
     *
     *
     */
    public void oprimirDiferirFactura()
    {
        // <CODIGO_DESARROLLADO>

        if ((boolean) registro.getCampos().get("INDPAGO"))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3909"));
        }
        else
        {
            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            nroFacturaCons))
            {

                String[] campos = { "tipoCobro", "codigoCobro", "nroFactura",
                                    "anio" };
                String[] valores = { tipoCobro,
                                     retornarString(registro, codigoCobroCons),
                                     retornarString(registro, nroFacturaCons),
                                     ano };

                SessionUtil.cargarModalDatosFlashCerrar(
                                String.valueOf(GeneralCodigoFormaEnum.FRM_DIFERIRFACTURA_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(), campos, valores);
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3908"));
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton DiferirFactura en la vista
     *
     *
     */
    public void oprimirAmortizacion()
    {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "tipoCobro", "nroAbono" };
        String[] valores = { tipoCobro,
                             retornarString(registro, "NUMERO_ABONO") };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRM_AMORTIZACION_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(), campos, valores);

        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarDian en la vista
     *
     */
    public void oprimirReenviarFactElectronica()
    {
        int tipoformato;
        String respuesta;
        String respuestac;
        String idFactura;
        String codigoReporte;
        String prFormato; 
        String msg;
        String prefijoFact;
        RespuestaApi respuestaApis;
        boolean permiteCodigoBarra = false;
        Gson gson = new Gson();
        APIFrida api = new APIFrida();
        String numeroFactura = retornarString(registro, nroFacturaCons);
        

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(codigoCobroCons));

        Registro rstipoformato;
        try
        {

            prFormato = ejbSysmanUtil.consultarParametro(compania,
                            "SF FORMATO FACTURACION ELECTRONICA",
                            SessionUtil.getModulo(), new Date(), false);

            rstipoformato = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmDocDianControladorUrlEnum.URL8245
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rstipoformato != null)
            {

                tipoformato = Integer.parseInt(rstipoformato
                                .getCampos().get("INDICADORFAC")
                                .toString());

            }
            else
            {
                tipoformato = 1;
            }

       
           

            Map<String, Object> param2 = new TreeMap<>();

            param2.put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano(new Date()));
            param2.put("CODIGO", tipoCobro); //mod JM CC 3182 
            param2.put("COMPANIA", compania); //mod JM CC 3182 

            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmDocDianControladorUrlEnum.URL2735
                                                                                            .getValue())
                                                            .getUrl(),
                                            param2));

            if (rs != null)
            {

                codigoReporte = SysmanFunciones
                                .nvl(rs.getCampos().get("FORMATO"),
                                                "30002195")
                                .toString();
                
                prefijoFact = SysmanFunciones
                        .nvl(rs.getCampos().get("PREFIJO"),
                        		tipoCobro)
                .toString();//mod JM CC 3182 

            }
            else
            {
                codigoReporte = prFormato;
                prefijoFact = tipoCobro;//mod JM CC 3182 
            }

            Registro rsCertificado = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmRangoProduccionDianUrlEnum.URL9457
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));

            if (rsCertificado != null)
            {

                File archivo = new File(
                                rsCertificado.getCampos().get(
                                                "RUTA_CERTIFICADO")
                                                .toString());

                String nombreCertificado = archivo.getName();

                byte[] archivoBytes = Files
                                .readAllBytes(archivo.toPath());

                String certificado = Base64.getEncoder()
                                .encodeToString(archivoBytes);

                String passCertificado = Base64.getEncoder()
                                .encodeToString(rsCertificado
                                                .getCampos()
                                                .get(CONTRA_CERTIFICADO)
                                                .toString()
                                                .getBytes());


                ParametrosReenviarCorreoFactura paramReeenviar = new ParametrosReenviarCorreoFactura();
                
                paramReeenviar.setTipoFormato(Integer.toString(tipoformato));

                paramReeenviar.setNumFormato(numeroFactura);

                paramReeenviar.setPrefijo(prefijoFact); //mod JM CC 3182 

                paramReeenviar.setNombreCertificado(nombreCertificado);

                paramReeenviar.setCertificado(certificado);

                paramReeenviar.setPassCertificado(passCertificado);

                paramReeenviar.setNumDocumentoContribuyente(nitCompania); 
                
                paramReeenviar.setCodigoReporte(codigoReporte);
                
                paramReeenviar.setMuestraCodigoBarras(permiteCodigoBarra);
                
                paramReeenviar.setActividadesEconomicas(actividadesEconomicas);
                
               
                ////////ID DE FACTURA ///////////////
                
                String url = ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_REST, "69", new Date(), false);

                //respuesta = api.cargarEnvioFacatura("890701933", "42586", "FEM2", url);
                
                respuesta = api.cargarEnvioFacatura(nitCompania, numeroFactura,
                                SysmanFunciones.nvl(prefijoFact, "").toString(), url); //mod JM CC 3182 

                ParametrosEnvioFacturaFiltros respuestaApi = gson.fromJson(respuesta,
                                ParametrosEnvioFacturaFiltros.class);

                idFactura = Integer.toString(respuestaApi.getCuerpo().getId());
                paramReeenviar.setIdFactura(idFactura);
        
                //////////////REENVIO CORREO ////////////
                String json = gson.toJson(paramReeenviar,
                		ParametrosReenviarCorreoFactura.class);
                respuestac = api.postReenvioCorreoFactura(url, json);
                respuestaApis = gson.fromJson(respuestac, RespuestaApi.class);

                if (respuestaApis.getCodigo() != 0)
                {
                   // correo no enviado 
                	msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue());
    				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
    				JsfUtil.agregarMensajeError(msg);
    				
                }
                else
                {
                   //correo enviado  
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_PROCESO_EJECUTADO"));
                } 
            }

        }
        catch (SystemException | IOException
                        | com.sysman.util.SysmanException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarDian en la vista
     *
     */
    public void oprimirEnviarDian() throws SysmanException
    {
        String url;
        String log = null;

        archivoDescarga = null;

        log = "|---------------         LOG DE ERRORES         ---------------|";
        
        // se consulta el parametro que define si se usa Facturador externo o FRIDA
        String facturadorExterno = "";
        try {
        	facturadorExterno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
        			MANEJA_FACTURACION_ELECTRONICA_EXTERNA, "69", new Date(), false),"NO");
        }catch (Exception e) {
        	facturadorExterno = "NO";
		}
        if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) {
	        if (validarEnviarDian())
	        {
	
	            Map<String, Object> param = new TreeMap<>();
	            /**
	             * @author ldiaz
	             * en este punto se manupula la hora de la fecha solicitud del registro para que este no la lleve en ceros
	             * pues el systema requiere que este entre 1 y 12 
	             */
	            Date fechaSolicitud = (Date) registro
	            			.getCampos()
	            				.get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD
	            						.getValue());
	            SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
	                       
	            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	            param.put(GeneralParameterEnum.FECHAINICIAL.getName(),formatFecha.format(fechaSolicitud));
	            param.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(fechaSolicitud));
	
	            param.put(FacturacionconceptosControladorEnum.NUMEROFACTURA
	                            .getValue(),
	                            retornarString(registro, nroFacturaCons));
	            param.put(tipoCobroCons, tipoCobro);
	
	            try
	            {
	
	                url = ejbSysmanUtil.consultarParametro(compania,
	                		URL_SERVICIO_REST, "69", new Date(), false);
	
	                List<Registro> listaFactSysman = RegistroConverter
	                                .toListRegistro(
	                                                requestManager.getList(
	                                                                UrlServiceUtil
	                                                                                .getInstance()
	                                                                                .getUrlServiceByUrlByEnumID(
	                                                                                                FacturacionconceptosControladorUrlEnum.URL3578
	                                                                                                                .getValue())
	                                                                                .getUrl(),
	                                                                param));
	
	                if (!listaFactSysman.isEmpty())
	                {
	
	                    for (Registro reg : listaFactSysman)
	                    {
	
	                        String respuesta;
	                        APIFrida apiFrida = new APIFrida();
	
	                        respuesta = apiFrida.cargarTercero(nitCompania,
	                                        reg.getCampos().get("NUMTERCERO")
	                                                        .toString(),
	                                        url);
	
	                        Gson gson = new Gson();
	                        RespuestaApi respuestaApi = gson.fromJson(respuesta,
	                                        RespuestaApi.class);
	                        if (respuestaApi.getCodigo() != 0) {
								log = log + "\n" + crearTecero(reg.getCampos().get("NUMTERCERO").toString(), url);
							}else {
								log = log + "\n" + actualizaTecero(reg.getCampos().get("NUMTERCERO").toString(), url);
							}
	
	                        if (reg.getCampos().get("CODIGOPRODUCTO") != null)
	                        {
	                            validarProducto(url, reg.getCampos()
	                                            .get("CODIGOPRODUCTO").toString());
	
	                        }
	                    }
	
	                    validarRango(url);
	
	                    // Verificacion Factura
	
	                    listaFactSysman = RegistroConverter
	                                    .toListRegistro(
	                                                    requestManager.getList(
	                                                                    UrlServiceUtil
	                                                                                    .getInstance()
	                                                                                    .getUrlServiceByUrlByEnumID(
	                                                                                                    FacturacionconceptosControladorUrlEnum.URL3579
	                                                                                                                    .getValue())
	                                                                                    .getUrl(),
	                                                                    param));
	
	                    for (Registro reg : listaFactSysman)
	                    {
	
	                        String prefijo = SysmanFunciones
	                                        .nvl(reg.getCampos().get("PREFIJO"), "")
	                                        .toString();
	
	                        String numeroFactura = SysmanFunciones
	                                        .nvl(reg.getCampos()
	                                                        .get(FacturacionconceptosControladorEnum.NUMEROFACTURA
	                                                                        .getValue()),
	                                                        "")
	                                        .toString();
	
	                        boolean facExiste = false;
	
	                        String tipoFormato;
	                        if ("NC".equals(prefijo))
	                            tipoFormato = "02";
	                        else
	                        {
	                            tipoFormato = "ND".equals(prefijo) ? "03" : "01";
	                        }
	
	                        String respuesta;
	                        APIFrida api = new APIFrida();
	
	                        respuesta = api.cargarEnvioFacatura(nitCompania, url);
	
	                        Gson gson = new Gson();
	                        RespuestaEnvioFactura respuestaApi = gson.fromJson(
	                                        respuesta,
	                                        RespuestaEnvioFactura.class);
	
	                        for (int i = 0; i < respuestaApi.getCuerpo()
	                                        .size(); i++)
	                        {
	                        	facExiste = false;
	                            List<Object> datos = (List<Object>) respuestaApi
	                                            .getCuerpo()
	                                            .get(i);
	
	                            if (numeroFactura
	                                            .equals(new DecimalFormat(
	                                                            "#.####################################")
	                                                                            .format(datos.get(
	                                                                                            0)))
	                                && prefijo.equals(datos.get(1)))
	                            {
	                                facExiste = true;
	                            }
	
	                            // Borrar Factura
	                            if (facExiste)
	                            {
	                                Registro rs = RegistroConverter
	                                                .toRegistro(requestManager.get(
	                                                                UrlServiceUtil.getInstance()
	                                                                                .getUrlServiceByUrlByEnumID(
	                                                                                                FacturacionconceptosControladorUrlEnum.URL9457
	                                                                                                                .getValue())
	                                                                                .getUrl(),
	                                                                null));
	
	                                if (rs != null)
	                                {
	
	                                    File archivo = new File(
	                                                    rs.getCampos().get(
	                                                                    "RUTA_CERTIFICADO")
	                                                                    .toString());
	
	                                    String nombreCertificado = archivo
	                                                    .getName();
	
	                                    byte[] archivoBytes = Files
	                                                    .readAllBytes(archivo
	                                                                    .toPath());
	
	                                    String certificado = Base64.getEncoder()
	                                                    .encodeToString(archivoBytes);
	
	                                    String passCertificado = Base64.getEncoder()
	                                                    .encodeToString(rs
	                                                                    .getCampos()
	                                                                    .get(CONTRA_CERTIFICADO)
	                                                                    .toString()
	                                                                    .getBytes());
	
	                                    ParametroDeleteEnvioFactura paramDelete = new ParametroDeleteEnvioFactura();
	
	                                    paramDelete.setTipoFormato(tipoFormato);
	                                    paramDelete.setNumFormato(numeroFactura);
	                                    paramDelete.setPrefijo(prefijo);
	                                    paramDelete.setCertificado(certificado);
	                                    paramDelete.setNombreCertificado(
	                                                    nombreCertificado);
	                                    paramDelete.setPassCertificado(
	                                                    passCertificado);
	                                    paramDelete.setNumDocumentoContribuyente(
	                                                    nitCompania);
	
	                                    Gson gson2 = new Gson();
	                                    String json = gson2.toJson(paramDelete,
	                                                    ParametroDeleteEnvioFactura.class);
	
	                                    APIFrida apiFrida = new APIFrida();
	
	                                    String val = apiFrida.deleteEnvioFactura(url, json);
	                                    log = log + "\n" + val;
	                                    
	                                    if (val.startsWith("!")) {
	                                    	throw new SysmanException(val);
	                                    }
	
	                                }
	
	                            }
	                        }
	
	                    }
	                    // VerificacionFactira
	
	                    log = log + "\n" + exportarFacturas(url,
	                                    retornarString(registro, nroFacturaCons),
	                                    tipoCobro);
	
	                    log = log + "\n" + envioDianIndividual(url,
	                                    retornarString(registro, nroFacturaCons)); 
	
	                }
	
	                ByteArrayInputStream streamTexto = JsfUtil
	                                .serializarPlano(log);
	                archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
	                                "LogEnviarDian.txt");
	
	                JsfUtil.agregarMensajeInformativo(
	                                idioma.getString("MSM_PROCESO_EJECUTADO"));
	
	            }
	            catch (SystemException | IOException
	                            | JRException | com.sysman.util.SysmanException e)
	            {
	                logger.error(e.getMessage(), e);
	                JsfUtil.agregarMensajeError(e.getMessage());
	            }
	
	        }
	        else 
	        {
	
	            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3910"));
	        }
    	}else if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())) {
    		try {	
    			log = log  +  "\n" + exportarFacturadorExterno(retornarString(registro, nroFacturaCons), tipoCobro);
	    		
	    		ByteArrayInputStream streamTexto;
			
				streamTexto = JsfUtil
				        .serializarPlano(log);
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
	                    "LogEnviarDian.txt");
	    		JsfUtil.agregarMensajeInformativo(
	                    idioma.getString("MSM_PROCESO_EJECUTADO"));
			} catch (JRException | IOException | JAXBException e) {
				logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
			}     		
    	}
    }

    private String exportarFacturadorExterno(String numeroFactura, String tipoCobro) throws JAXBException {
    	Documento documento = new Documento();
		String respuesta = "";
		try {
			// se consulta la url configurada en el parametro
			String url = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					URL_SERVICIO_SOAP, "69", new Date(), false),"");
			if(url == null || url.equals("NO")) {
				JsfUtil.agregarMensajeError(idioma.getString("MSM_ERROR_URL_SOAP"));
	            return idioma.getString("MSM_ERROR_URL_SOAP");
			}
			
	        Map<String, Object> param = new TreeMap<>();
	        /**
	         * @author ldiaz
	         * en este punto se manupula la hora de la fecha solicitud del registro para que este no la lleve en ceros
	         * pues el systema requiere que este entre 1 y 12 
	         */
	        Date fechaSolicitud = (Date) registro
	        			.getCampos()
	        				.get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD
	        						.getValue());
	        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
	                   
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.FECHAINICIAL.getName(),formatFecha.format(fechaSolicitud));
	        param.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(fechaSolicitud));
	        param.put(FacturacionconceptosControladorEnum.NUMEROFACTURA.getValue(),
	                        numeroFactura);
	        param.put("TIPOCOBRO", tipoCobro);

        
			Registro rs2 = RegistroConverter
			                    .toRegistro(requestManager.get(
			                                    UrlServiceUtil.getInstance()
			                                                    .getUrlServiceByUrlByEnumID(
			                                                                    FrmFactLoteControladorUrlEnum.URL661077
			                                                                                    .getValue())
			                                                    .getUrl(),
			                                    param));
			
			if(rs2 != null) {
				//Datos basico fijos
				documento.setNumeroDocumento(SysmanFunciones
						.nvl(rs2.getCampos().get("PREFIJO"), "")
						.toString()+numeroFactura);
				documento.setTipoDocumento("FA");
		        documento.setSubtipoDocumento("01");
		        documento.setTipoOperacion("10");
				documento.setFechaDocumento(SysmanFunciones
												.nvl(rs2.getCampos().get("FECHAFACTURA"), "")
														.toString());
				documento.setDivisa(SysmanFunciones
                        .nvl(rs2.getCampos().get("TIPO_MONEDA"), "")
                        .toString().equals("602")?"COP":"");
				documento.setDireccionFactura(SysmanFunciones
                        .nvl(rs2.getCampos().get("DIRECCIONCOMPANIA"), "")
                        .toString());
				documento.setDistritoFactura(SysmanFunciones
                        .nvl(rs2.getCampos().get("DEPTOCOMPANIA"), "")
                        .toString());
		        documento.setCiudadFactura(SysmanFunciones
                        .nvl(rs2.getCampos().get("DEPTOCOMPANIA"), "")
                        .toString()+SysmanFunciones
                        .nvl(rs2.getCampos().get("CIUDADCOMPANIA"), "")
                        .toString());
		        documento.setPaisFactura("CO");
		        //datos de proveedor
		        Proveedor p = new Proveedor(); 
		        p.setIdProveedor(nitCompania+"-"+SysmanFunciones
                        .nvl(rs2.getCampos().get("DIGITOVERIFICACIONCOMPANIA"), "").toString());
		        documento.setProveedor(p);
		        // Fin Proveedor
		        String tipoPersona = SysmanFunciones
                        .nvl(rs2.getCampos().get("NATURALEZATERCERO"), "")
                        .toString();
		        
		        // se crean datos del cliente
		        Cliente c = new Cliente();
		        c.setTipoPersonaCliente(tipoPersona.equals("N")?"1":"2");
		        c.setDireccionCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("DIRECCIONTERCERO"), "")
                        .toString());
		        c.setCodigoPostalCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("CODIGOPOSTALTERCERO"), "")
                        .toString());
		        c.setTelefonoCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("TELEFONOS"), "")
                        .toString());
		        c.setEmailCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("EMAILTECERO"), "")
                        .toString());
		        c.setPaisCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("PAISTERCERO"), "")
                        .toString());
		        c.setIdCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("NUMTERCERO"), "").toString()+"-"+SysmanFunciones
                        .nvl(rs2.getCampos().get("DIGITOVERIFICACIONTERCERO"), "").toString());
				c.setTipoDocumentoIdCliente("31");
				c.setDistritoCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("DEPTOTERCERO"), "")
                        .toString());
		        c.setCiudadCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("DEPTOTERCERO"), "")
                        .toString() + SysmanFunciones 
                        .nvl(rs2.getCampos().get("CIUDADTERCERO"), "")
                        .toString());
		        c.setRazonSocialCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("RAZONSOCIAL"), "")
                        .toString());
		        c.setNombreCliente(tipoPersona.equals("N")?SysmanFunciones
                        .nvl(rs2.getCampos().get("NOMBRETERCERO"), "")
                        .toString():SysmanFunciones
                        .nvl(rs2.getCampos().get("RAZONSOCIAL"), "")
                        .toString());
		        c.setApellido1Cliente(tipoPersona.equals("N")?SysmanFunciones
                        .nvl(rs2.getCampos().get("NOMBRETERCERO"), "")
                        .toString():SysmanFunciones
                        .nvl(rs2.getCampos().get("RAZONSOCIAL"), "")
                        .toString());
		        c.setApellido2Cliente(tipoPersona.equals("N")?SysmanFunciones
                        .nvl(rs2.getCampos().get("NOMBRETERCERO"), "")
                        .toString():SysmanFunciones
                        .nvl(rs2.getCampos().get("RAZONSOCIAL"), "")
                        .toString());
		        c.setRegimenCliente("49");
		        
		        documento.setCliente(c);
		        // fin cliente
		        // gestios de impuestos
		        BigDecimal totalImpuestos = new BigDecimal("0");
		        Impuestos impuestosFin = new Impuestos();
		        Registro impuestosGeneralesIva = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmFactLoteControladorUrlEnum.URL5768
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));
		        if (impuestosGeneralesIva != null)
		        {		        	
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesIva.getCampos().get("SUMADEVALORIMPUESTO").toString(), 0).toString()) > 0) {
			        	Impuesto impuesto = new Impuesto();
			        	impuesto.setBaseImpuesto(new BigDecimal(impuestosGeneralesIva.getCampos().get("SUMADEBASEIMPUESTOIVA").toString()));
			        	impuesto.setPorcImpuesto(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesIva.getCampos().get(
                                        "PORCIVA"),
                        "0").toString()));
			        	double valorImpuesto = SysmanFunciones.redondear((Double.parseDouble(impuestosGeneralesIva.getCampos().get("SUMADEBASEIMPUESTOIVA").toString())*Double.parseDouble(impuestosGeneralesIva.getCampos().get("PORCIVA").toString())/100),2);
			        	impuesto.setValorImpuesto(new BigDecimal(valorImpuesto).setScale(2, RoundingMode.HALF_UP));

			        	impuesto.setCodImpuesto("01");
			        	impuestosFin.getImpuesto().add(impuesto);
			        	totalImpuestos = totalImpuestos.add(impuesto.getValorImpuesto());
		        	}
		        }
		        Registro impuestosGeneralesInc = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmFactLoteControladorUrlEnum.URL5773
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));
		        if (impuestosGeneralesInc != null)
		        {
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesInc.getCampos().get("SUMADEIMPUESTO_INC").toString(), 0).toString()) > 0) {
		        		Impuesto impuesto = new Impuesto();
			        	impuesto.setBaseImpuesto(new BigDecimal(impuestosGeneralesInc.getCampos().get("SUMADEBASEIMPUESTOINC").toString()));
			        	impuesto.setPorcImpuesto(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesInc.getCampos().get(
                                        "PORCENTAJE"),
                        "0").toString()));
						double valorImpuesto = SysmanFunciones.redondear((Double.parseDouble(impuestosGeneralesInc.getCampos().get("SUMADEBASEIMPUESTOINC").toString())*Double.parseDouble(impuestosGeneralesInc.getCampos().get("PORCENTAJE").toString())/100),2);
			        	impuesto.setValorImpuesto(new BigDecimal(valorImpuesto).setScale(2, RoundingMode.HALF_UP));
			        	impuesto.setCodImpuesto("04");
			        	impuestosFin.getImpuesto().add(impuesto);
			        	totalImpuestos = totalImpuestos.add(impuesto.getValorImpuesto());
		        	}			        
		        }
		        // gestion retenciones
		        BigDecimal totalRetenciones = new BigDecimal("0");
		        Retenciones retencionesFin = new Retenciones();
		        Registro impuestosGeneralesReteIva = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmFactLoteControladorUrlEnum.URL5771
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));
		        if (impuestosGeneralesReteIva != null)
		        {
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesReteIva.getCampos().get("SUMADERETEIVA").toString(), 0).toString()) > 0) {
		        		Retencion retencion = new Retencion();
		        		double baseRetencion = SysmanFunciones.redondear((Double.parseDouble(impuestosGeneralesIva.getCampos().get("SUMADEBASEIMPUESTOIVA").toString())*Double.parseDouble(impuestosGeneralesIva.getCampos().get("PORCIVA").toString())/100),2);
		        		retencion.setBaseRetencion(new BigDecimal(baseRetencion).setScale(2, RoundingMode.HALF_UP));

		        		retencion.setPorcRetencion(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesReteIva.getCampos().get(
                                        "PORCENTAJE"),
                        "0").toString()));
						double valorRetencion = SysmanFunciones.redondear(baseRetencion*Double.parseDouble(impuestosGeneralesReteIva.getCampos().get("PORCENTAJE").toString())/100,2);
			        	retencion.setValorRetencion(new BigDecimal(valorRetencion).setScale(2, RoundingMode.HALF_UP));
			        	retencion.setCodRetencion("05");
			        	retencionesFin.getRetencion().add(retencion);
			        	totalRetenciones = totalRetenciones.add(retencion.getValorRetencion());
		        	}
			        
		        }
		        Registro impuestosGeneralesReteFuente = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmFactLoteControladorUrlEnum.URL5769
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));

		        if (impuestosGeneralesReteFuente != null)
		        {
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesReteFuente.getCampos().get("SUMADEVALOR_RETEFUENTE").toString(), 0).toString()) > 0) {
		        		Retencion retencion = new Retencion();
			        	retencion.setBaseRetencion(new BigDecimal(impuestosGeneralesReteFuente.getCampos().get("SUMADEBASEIMPUESTORETEFUENTE").toString()));
			        	retencion.setPorcRetencion(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesReteFuente.getCampos().get(
                                        "PORCENTAJE"),
                        "0").toString()).setScale(2));
						double valorRetencion = SysmanFunciones.redondear((Double.parseDouble(impuestosGeneralesReteFuente.getCampos().get("SUMADEBASEIMPUESTORETEFUENTE").toString())*Double.parseDouble(impuestosGeneralesReteFuente.getCampos().get("PORCENTAJE").toString())/100),2);
			        	retencion.setValorRetencion(new BigDecimal(valorRetencion).setScale(2, RoundingMode.HALF_UP));
			        	retencion.setCodRetencion("06");
			        	retencionesFin.getRetencion().add(retencion);
			        	totalRetenciones = totalRetenciones.add(retencion.getValorRetencion());
		        	}			        
		        }			        
		     // se agregan los productos 
		        List<Registro> listaProductos = RegistroConverter
		        .toListRegistro(
		                requestManager.getList(
		                                UrlServiceUtil
		                                                .getInstance()
		                                                .getUrlServiceByUrlByEnumID(
		                                                                FrmFactLoteControladorUrlEnum.URL2974
		                                                                                .getValue())
		                                                .getUrl(),
		                                param));
		        Lineas lineas = new Lineas();
	        	int cont = 1;
	        	double sumProdPorFactura = 0;
	        	double sumDescuentosFactura = 0;
	        	double sumsubTotalFactura = 0;
		        if(!listaProductos.isEmpty()) {
		        	
			        for(Registro reg: listaProductos) {
			        	//representa los items o productos de la factura
			        	Linea linea = new Linea();
				        linea.setNumLinea(cont);
				        linea.setIdEstandarReferencia("00"+cont);
				        linea.setDescripcionItem(SysmanFunciones
                                .nvl(reg.getCampos().get(
                                        "DESCRIPCIONPRODUCTO"),
                                        "")
                        .toString());
				        linea.setUnidadMedida("NIU");
				        linea.setUnidadesLinea(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString(), 0).toString()));
				        linea.setPrecioUnidad(new BigDecimal(SysmanFunciones
                                .nvl(reg.getCampos()
                                        .get("VALORUNITARIO"),
                                        "0")
                        .toString()));
				        double subTotalLinea = Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VALORUNITARIO").toString(), 0).toString())*Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString(), 0).toString());
				        linea.setSubtotalLinea(new BigDecimal(subTotalLinea).setScale(2, RoundingMode.HALF_UP));
				        //descuentos
				        if(Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VALOR_DESCUENTO").toString(), 0).toString()) > 0) {
				        	Double porcentajeDescuenteo = new Double("0");
					        porcentajeDescuenteo = (Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VALOR_DESCUENTO").toString(), 0).toString())*100)/Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("TOTALITEM").toString(), 0).toString());
					        linea.setPorcDescuentoLinea(new BigDecimal(porcentajeDescuenteo).setScale(2, RoundingMode.HALF_UP));
					        linea.setDescuentoLinea(new BigDecimal(SysmanFunciones.nvl(reg.getCampos().get("VALOR_DESCUENTO").toString(), 0).toString()));
					        sumDescuentosFactura = sumDescuentosFactura + linea.getDescuentoLinea().doubleValue();
					    }
				        double totalLinea = subTotalLinea - Double.parseDouble(reg.getCampos().get("VALOR_DESCUENTO").toString());
				        linea.setTotalLinea(new BigDecimal(totalLinea).setScale(2, RoundingMode.HALF_UP));




				        // se suman los totales de los producctos
				        sumProdPorFactura = sumProdPorFactura + linea.getTotalLinea().doubleValue();				        
				        //se agrega impuesto principal IVA en caso de tener el impuesto
				        if(Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("BASEIMPUESTOIVA").toString(), 0).toString()) > 0) {
					        linea.setPorcImpuestoLinea(new BigDecimal(reg.getCampos().get("PORCIVA").toString()).setScale(2));
					        double valorImpuesto = SysmanFunciones.redondear((Double.parseDouble(reg.getCampos().get("BASEIMPUESTOIVA").toString())*Double.parseDouble(reg.getCampos().get("PORCIVA").toString())/100),2);
					        linea.setValorImpuestoLinea(new BigDecimal(valorImpuesto).setScale(2, RoundingMode.HALF_UP));

					        linea.setCodImpuestoLinea("01");
				        }
				        // se suman los subtotales de los producctos
				        sumsubTotalFactura = sumsubTotalFactura + linea.getSubtotalLinea().doubleValue();

				        lineas.getLinea().add(linea);
				        cont++; 
			        }
			        
		        }		        
		        documento.setLineas(lineas);  		     

		        // Se agregan los datos totales
		        DatosTotales totalesFact = new DatosTotales();  
		        double totalBase = sumsubTotalFactura - sumDescuentosFactura;
		        double totalDoc = totalBase + totalImpuestos.doubleValue();

		        totalesFact.setAPagar(new BigDecimal(totalDoc).setScale(2, RoundingMode.HALF_UP));		        
		        totalesFact.setSubtotal(new BigDecimal(sumsubTotalFactura).setScale(2, RoundingMode.HALF_UP));
		        totalesFact.setTotalBase(new BigDecimal(totalBase).setScale(2, RoundingMode.HALF_UP));
		        totalesFact.setTotalImpuestos(totalImpuestos);
		        totalesFact.setTotalGastos(new BigDecimal(0));
		        totalesFact.setTotalRetenciones(totalRetenciones);
		        totalesFact.setTotalDocumento(new BigDecimal(totalDoc).setScale(2, RoundingMode.HALF_UP));
		        
		        // total factura
		        BigDecimal totalFactFin = totalesFact.getTotalBase();
		        totalFactFin = totalFactFin.add(totalImpuestos);
		        
		        //totalesFact.setTotalDocumento(new BigDecimal(totalDoc).setScale(2, RoundingMode.HALF_UP));
		        totalesFact.setTotalDocumento(totalFactFin);
		        totalesFact.setAPagar(totalFactFin);
		        documento.setImpuestos(impuestosFin);
		        documento.setRetenciones(retencionesFin);
		        
		        documento.setDatosTotales(totalesFact);
		        
		        CondicionPago formaPago = new CondicionPago();
		        formaPago.setMedioPago(SysmanFunciones
                        .nvl(rs2.getCampos().get("MEDIOPAGO"), "")
                        .toString());
		        formaPago.setFormaPago(SysmanFunciones
                        .nvl(rs2.getCampos().get("TIPO_PAGO"), "")
                        .toString());
		        formaPago.setFechaPago(SysmanFunciones
                        .nvl(rs2.getCampos().get("FECHA_VENCIMIENTO"), "")
                        .toString());  
		        CondicionesPago condicionesFormaPago = new CondicionesPago();
		        condicionesFormaPago.getCondicionPago().add(formaPago);
		        documento.setCondicionesPago(condicionesFormaPago);
		        ApiInvoway apiInvoway = new ApiInvoway();
		        
		        // se consulta los parametros para traer el usuario y la contrasea del ws invoway
		        String user = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
		        		USUARIO_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false),"");
		        
		        String pass = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
		        		CLAVE_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false),"");
		        
		      //Se deja este codigo comentando para cuando se requiera obtener el XML para revisar datos CC3686 -MPEREZ
		        JAXBContext context = JAXBContext.newInstance(Documento.class);
		        Marshaller marshaller = context.createMarshaller();
		        StringWriter writer = new StringWriter();
		        marshaller.marshal(documento, writer);

		        String xml = writer.toString();
		        
		        archivoDescarga = JsfUtil.exportarXmlStreamed("XML_Invoway",
		        		xml);
	            
	            respuesta = apiInvoway.postEnvioFactura(url, documento, pass, user);
			}  
		        
		} catch (SystemException | IOException | com.sysman.util.SysmanException e1) {
			logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
            return "Error al generar el XML";
		}
		
		return respuesta;
	}
    private void verificarDescuentos(Map<String, Object> parametrosConsulta, Double nConceptos, Double sumProdPorFactura) throws NumberFormatException, SystemException {
    	int digRedDian = Integer.parseInt(SysmanFunciones
                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                "SF NUMERO DIGITOS REDONDEO DIAN",
                                SessionUtil.getModulo(), new Date(),
                                false), "0").toString());
    	double descuento = 0;
    	double redondeodescuento = 0;
    	Registro rs4 = RegistroConverter
                .toRegistro(requestManager.get(
                                UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmFactLoteControladorUrlEnum.URL4587
                                                                                .getValue())
                                                .getUrl(),
                                                parametrosConsulta));

		if (rs4 != null)
		{
			if (digRedDian == 0)
		    {
		        totalFactura = SysmanFunciones.redondear(
		                        SysmanFunciones.nvlDbl((SysmanFunciones
		                                        .nvlDbl(rs4.getCampos()
		                                                        .get(
		                                                                        GeneralParameterEnum.TOTAL
		                                                                                        .getName()),
		                                                        0)
		                            -
		                            (SysmanFunciones.nvlDbl(rs4
		                                            .getCampos()
		                                            .get("DESCUENTO_FACTURA"),
		                                            0)
		                                / nConceptos)
		                            - descuento), 0),
		                        2);
		    }
		    else if (digRedDian % 10 == 0)
		    {
		        totalFactura = (SysmanFunciones.nvlDbl(SysmanFunciones
		                        .nvlDbl(rs4.getCampos()
		                                        .get(
		                                                        GeneralParameterEnum.TOTAL
		                                                                        .getName()),
		                                        0)
		            - (SysmanFunciones.nvlDbl(rs4
		                            .getCampos()
		                            .get("DESCUENTO_FACTURA"),
		                            0)
		                / nConceptos)
		            - descuento, 0) / digRedDian + 0.501) * digRedDian;
		
		    }
		    else
		    {
		        totalFactura = SysmanFunciones.nvlDbl((SysmanFunciones
		                        .nvlDbl(rs4.getCampos().get(
		                                        GeneralParameterEnum.TOTAL
		                                                        .getName()),
		                                        0)
		            -
		            (SysmanFunciones.nvlDbl(rs4.getCampos()
		                            .get("DESCUENTO_FACTURA"), 0)
		                / nConceptos)
		            - descuento), 0) + digRedDian;
		    }
		
		    if (sumProdPorFactura < totalFactura &&
		        (totalFactura - sumProdPorFactura) > 1)
		    {
		
		        ParametrosCargos paramCargos = new ParametrosCargos();
		
		        paramCargos.setValor((int) SysmanFunciones
		                        .redondear(totalFactura
		                            - sumProdPorFactura, 2));
		
//		        listaParamCargos.add(paramCargos);
		
		    }
		    else if (sumProdPorFactura > totalFactura &&
		        (sumProdPorFactura - totalFactura) > 1)
		    {
		
		        redondeodescuento = SysmanFunciones.redondear(
		                        sumProdPorFactura
		                            - totalFactura,
		                        2);
		
		        ParametrosDescuentos paramDescuento = new ParametrosDescuentos();
		
		        paramDescuento.setTipo("05");
		
		        paramDescuento.setValor(
		                        (int) redondeodescuento);
		
//		        listaParamDescuentos.add(paramDescuento);
		
		    }
		}
    }
	private String envioDianIndividual(String url, String numFactura)
    {
        int tipoformato;
        String codigoReporte;
        String prFormato;
        String log = "";
        StringBuilder pieCodigoBarra = new StringBuilder();
        StringBuilder pieTextoBarra = new StringBuilder();
        String codigoBarra;

        Date fechaVencimiento = (Date) registro.getCampos()
                        .get(FacturacionconceptosControladorEnum.FECHA_VENCIMIENTO
                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(codigoCobroCons));

        Registro rstipoformato;
        try
        {

            prFormato = ejbSysmanUtil.consultarParametro(compania,
                            "SF FORMATO FACTURACION ELECTRONICA",
                            SessionUtil.getModulo(), new Date(), false);

            rstipoformato = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmDocDianControladorUrlEnum.URL8245
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rstipoformato != null)
            {

                tipoformato = Integer.parseInt(rstipoformato
                                .getCampos().get("INDICADORFAC")
                                .toString());

            }
            else
            {
                tipoformato = 1;
            }

            // consumirEalvarez

            Map<String, Object> param2 = new TreeMap<>();

            param2.put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano(new Date()));
            param2.put("CODIGO", tipoCobro); //mod JM CC 3250 
            param2.put("COMPANIA", compania); //mod JM CC 3250 

            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmDocDianControladorUrlEnum.URL2735
                                                                                            .getValue())
                                                            .getUrl(),
                                            param2));

            if (rs != null)
            {

                codigoReporte = SysmanFunciones
                                .nvl(rs.getCampos().get("FORMATO"),
                                                "30002195")
                                .toString();

            }
            else
            {
                codigoReporte = prFormato;
            }

            Registro rsCertificado = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmRangoProduccionDianUrlEnum.URL9457
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));

            if (rsCertificado != null)
            {

                File archivo = new File(
                                rsCertificado.getCampos().get(
                                                "RUTA_CERTIFICADO")
                                                .toString());

                String nombreCertificado = archivo.getName();

                byte[] archivoBytes = Files
                                .readAllBytes(archivo.toPath());

                String certificado = Base64.getEncoder()
                                .encodeToString(archivoBytes);

                String passCertificado = Base64.getEncoder()
                                .encodeToString(rsCertificado
                                                .getCampos()
                                                .get(CONTRA_CERTIFICADO)
                                                .toString()
                                                .getBytes());

                ParametrosLegalizarFactura paramLegalizar = new ParametrosLegalizarFactura();

                paramLegalizar.setTipoSalida(1);
                paramLegalizar.setTestSetId(rsCertificado
                                .getCampos().get("TES_ID")
                                .toString());
                paramLegalizar.setCodigoReporte(codigoReporte);

                ParametrosFormato paramFormato = new ParametrosFormato();

                paramFormato.setTipoFormato(
                                Integer.toString(tipoformato));

                paramFormato.setNumFormato(numFactura);

                paramFormato.setPrefijo(prefijo);

                paramFormato.setNombreCertificado(
                                nombreCertificado);

                paramFormato.setCertificado(certificado);

                paramFormato.setPassCertificado(passCertificado);

                paramFormato.setNumDocumentoContribuyente(
                                nitCompania);
                
                paramFormato.setActividadesEconomicas(actividadesEconomicas);

                // Adicion Codigo Barras

                boolean permiteCodigoBarra = false;

                permiteCodigoBarra = ("SI").equals(
                                SysmanFunciones.nvlStr(ejbSysmanUtil
                                                .consultarParametro(
                                                                compania,
                                                                "SF PERMITE GENERAR CODIGO BARRA FACTURA ELECTRONICA",
                                                                SessionUtil.getModulo(),
                                                                new Date(),
                                                                false),
                                                "NO"));

                paramFormato.setMuestraCodigoBarras(
                                permiteCodigoBarra);
                if (permiteCodigoBarra)
                {

                    String codigoEan;

                    Map<String, Object> paramEan = new TreeMap<>();
                    paramEan.put(GeneralParameterEnum.COMPANIA
                                    .getName(),
                                    compania);
                    paramEan.put("ANO", ano);
                    paramEan.put(GeneralParameterEnum.CODIGO
                                    .getName(),
                                    tipoCobro);
                    Registro rsEan = RegistroConverter
                                    .toRegistro(requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    FacturacionconceptosControladorUrlEnum.URL1717
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    paramEan));

                    codigoEan = rsEan.getCampos().get("CODIGOEAN")
                                    .toString();

                    pieCodigoBarra.append((char) 205);
                    pieCodigoBarra.append((char) 102);
                    pieCodigoBarra.append(415);
                    pieCodigoBarra.append(codigoEan);
                    pieCodigoBarra.append("8020");
                    pieCodigoBarra.append(SysmanFunciones.padl(
                                    tipoFactRecaudo,
                                    5, "0"));
                    pieCodigoBarra.append(SysmanFunciones.padl(
                                    numFactura,
                                    19, "0"));
                    pieCodigoBarra.append((char) 102);
                    pieCodigoBarra.append("3900");
                    pieCodigoBarra.append(SysmanFunciones.padl(
                                    registro.getCampos().get("VALOR_TOTAL")
                                                    .toString(),
                                    14, "0"));
                    pieCodigoBarra.append((char) 102);
                    pieCodigoBarra.append(96);
                    pieCodigoBarra.append(SysmanFunciones.convertirAFechaCadena(
                                    fechaVencimiento, "yyyyMMdd"));

                    codigoBarra = ejbCodigoBarras
                                    .imprimirCodigoBarras(
                                                    pieCodigoBarra.toString());

                    paramFormato.setCodigoBarras(codigoBarra);

                    pieTextoBarra.append((char) 40);
                    pieTextoBarra.append(415);
                    pieTextoBarra.append((char) 41);
                    pieTextoBarra.append(codigoEan);
                    pieTextoBarra.append((char) 40);
                    pieTextoBarra.append("8020");
                    pieTextoBarra.append((char) 41);
                    pieTextoBarra.append(SysmanFunciones.padl(
                                    tipoFactRecaudo,
                                    5, "0"));
                    pieTextoBarra.append(SysmanFunciones.padl(numFactura,
                                    19, "0"));
                    pieTextoBarra.append((char) 40);
                    pieTextoBarra.append("3900");
                    pieTextoBarra.append((char) 41);
                    pieTextoBarra.append(SysmanFunciones.padl(
                                    registro.getCampos().get("VALOR_TOTAL")
                                                    .toString(),
                                    14, "0"));
                    pieTextoBarra.append((char) 40);
                    pieTextoBarra.append(96);
                    pieTextoBarra.append((char) 41);
                    pieTextoBarra.append(SysmanFunciones.convertirAFechaCadena(
                                    fechaVencimiento, "yyyyMMdd"));

                    paramFormato.setTextoBarras(
                                    pieTextoBarra.toString());
                }

                paramLegalizar.setParamFormato(paramFormato);

                String respuestaLegalizar;
                APIFrida apiFrida = new APIFrida();

                Gson gson2 = new Gson();
                String json = gson2.toJson(paramLegalizar,
                                ParametrosLegalizarFactura.class);

                respuestaLegalizar = apiFrida
                                .postFormatoLegalizar(url, json);

                RespuestaApi respuestaApis = gson2.fromJson(
                                respuestaLegalizar,
                                RespuestaApi.class);

                if (respuestaApis.getCodigo() != 0)
                {
                    log = log + "\n" + respuestaApis.getMensaje()
                        + " "
                        + (tipoformato == 1 ? " FACTURA:"
                            : " NOTA:")
                        + numFactura
                        + ". Proceso terminado ";
                }
                else
                {
                    log = log + "\n" +
                        crearReportesFrida(respuestaLegalizar, numFactura);

                }
            }

        }
        catch (SystemException | IOException
                        | com.sysman.util.SysmanException | ParseException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return log;
    }

    private String crearReportesFrida(String respuestaLegalizar,
        String factura)
    {

        String log = "";

        OutputStream outZip = null;
        OutputStream outPdf = null;

        Gson gson = new Gson();
        RespuestaFridaLegalizar legalizar = gson.fromJson(respuestaLegalizar,
                        RespuestaFridaLegalizar.class);
        
        Map<String, Object> cuerpo = (Map<String, Object>) legalizar.getCuerpo();
        if(cuerpo != null 
        		&& cuerpo.get("zip") != null 
        		&& cuerpo.get("Reporte") != null) {
	        String zip = (String) cuerpo.get("zip");
	        String pdf = (String) cuerpo.get("Reporte");
	
	        Registro rsRutaArchivos;
	        try
	        {
	            rsRutaArchivos = RegistroConverter
	                            .toRegistro(requestManager.get(
	                                            UrlServiceUtil.getInstance()
	                                                            .getUrlServiceByUrlByEnumID(
	                                                                            FrmRangoProduccionDianUrlEnum.URL9457
	                                                                                            .getValue())
	                                                            .getUrl(),
	                                            null));
	
	            if (rsRutaArchivos != null)
	            {
	
	                String ruta = SysmanFunciones
	                                .nvl(rsRutaArchivos.getCampos()
	                                                .get("RUTA_FACTURAS"), "")
	                                .toString();
	
	                if (!ruta.isEmpty())
	                {
	
	                    File verificar = new File(ruta);
	                    if (!verificar.isDirectory())
	                    {
	                        verificar.mkdirs();
	                    }
	
	                    if (zip != null)
	                    {
	
	                        byte[] archivoZip = Base64.getDecoder().decode(zip);
	
	                        outZip = new FileOutputStream(ruta + "/" + "FACTURA_"
	                            + factura + "_"
	                            + SysmanFunciones.convertirAFechaCadena(new Date(),
	                                            "YYYYMMDD_HHMMSS")
	                            + ".zip");
	                        outZip.write(archivoZip);
	                        outZip.close();
	
	                    }
	
	                    if (pdf != null)
	                    {
	                        byte[] archivoPdf = Base64.getDecoder().decode(pdf);
	
	                        outPdf = new FileOutputStream(ruta + "/" + "FACTURA_"
	                            + factura + "_"
	                            + SysmanFunciones.convertirAFechaCadena(new Date(),
	                                            "YYYYMMDD_HHMMSS")
	                            + ".pdf");
	                        outPdf.write(archivoPdf);
	                        outPdf.close();
	                    }
	
	                    // --- Lectura del tag de respuesta de la DIAN
	                    ArrayList<Map<String, Object>> resultadoDian = (ArrayList<Map<String, Object>>) cuerpo.get("resultadoProcesoDian");
	                    Map<String, Object> resultadoDianCuerpo = resultadoDian.get(0);
	                    if (!resultadoDianCuerpo.get("IsValid").equals("true")
	                                    )
	                    {
	                        log = SysmanFunciones.nvl(cuerpo.get("resultadoProcesoDian"), "").toString();
	                    }
	
	                }
	                else
	                {
	                    JsfUtil.agregarMensajeAlerta(
	                                    "Debe crear la ruta en donde se generaran los reportes");
	                }
	
	            }
	
	        }
	        catch (SystemException | ParseException | IOException e)
	        {
	
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	
	        }
        }else {
        	log = log + SysmanFunciones.nvl(legalizar.getMensaje(),"");
        }
        return log;

    }

    private String exportarFacturas(String url, String numeroFactura,
        String tipoFactura)
    {
        String respuesta = null;
        String datosFacturaBancos;
        String observacionAdicional = "";
        String strObservaciones;
        double valorIVAConcepto = 0;
        double sumProdPorFactura = 0;
        double descuento = 0;

        double redondeodescuento = 0;
        int digRedDian;

        int nConceptos = 0;

        ParametrosEnvioFactura paramFactura = new ParametrosEnvioFactura();

        ParametroCuerpoEnvioFactura paramCuerpoFactura = new ParametroCuerpoEnvioFactura();

        Map<String, Object> param = new TreeMap<>();
        /**
         * @author ldiaz
         * en este punto se manupula la hora de la fecha solicitud del registro para que este no la lleve en ceros
         * pues el systema requiere que este entre 1 y 12 
         */
        Date fechaSolicitud = (Date) registro
        			.getCampos()
        				.get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD
        						.getValue());
        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
                   
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(),formatFecha.format(fechaSolicitud));
        param.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(fechaSolicitud));
        param.put(FacturacionconceptosControladorEnum.NUMEROFACTURA.getValue(),
                        numeroFactura);
        param.put("TIPOCOBRO", tipoFactura);

        try
        {

            digRedDian = Integer.parseInt(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SF NUMERO DIGITOS REDONDEO DIAN",
                                            SessionUtil.getModulo(), new Date(),
                                            false), "0")
                            .toString());
            Registro rs2 = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmFactLoteControladorUrlEnum.URL3564
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rs2 != null)
            {

                Map<String, Object> param2 = new TreeMap<>();
				param2.put(GeneralParameterEnum.CODIGO.getName(),
						rs2.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()));

                Registro rs3 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL9512
                                                                                                .getValue())
                                                                .getUrl(),
                                                param2));

                paramFactura.setCreatedBy(rs3.getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString());

                paramFactura.setNumerocontribuyente(nitCompania);

                if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                                "SF SELECCIONAR CUENTA DE RECAUDO PARA FACTURACION ELECTRONICA",
                                SessionUtil.getModulo(), new Date(), false)))
                {
                	//INI CC2731 - MPEREZ
                	if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "SF MANEJA OBSERVACIONES ADICIONALES EN FACTURACION ELECTRONICA",
                            SessionUtil.getModulo(), new Date(), false)))
                	{
                		observacionAdicional = ejbSysmanUtil.consultarParametro(
                                compania, "DATOS FACTURA BANCOS",
                                SessionUtil.getModulo(), new Date(), false);
                	}
                	//FIN CC2731 - MPEREZ

                    Map<String, Object> params = new TreeMap<>();

                    params.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    params.put(GeneralParameterEnum.ANO.getName(),
                                    SysmanFunciones.ano(new Date()));
                    params.put(GeneralParameterEnum.CUENTA.getName(),
                                    SysmanFunciones.nvl(rs2.getCampos().get(
                                                    "CUENTA_RECAUDO"), ""));

                    rs3 = RegistroConverter
                                    .toRegistro(requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    FrmFactLoteControladorUrlEnum.URL3651
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    params));

                    if (rs3 != null)
                    {

                        datosFacturaBancos = SysmanFunciones
                                        .nvl(rs3.getCampos().get("LEYENDA"), "")
                                        .toString();
                    }
                    else
                    {
                        datosFacturaBancos = "";
                    }

                }
                else
                {

                    datosFacturaBancos = ejbSysmanUtil.consultarParametro(
                                    compania, "DATOS FACTURA BANCOS",
                                    SessionUtil.getModulo(), new Date(), false);

                }

                strObservaciones = SysmanFunciones.nvl(rs2.getCampos().get(
                                GeneralParameterEnum.OBSERVACIONES.getName()),
                                "").toString();
            
                paramCuerpoFactura.setNumTercero(SysmanFunciones
                                .nvl(rs2.getCampos().get("NUMTERCERO"), "")
                                .toString());

                paramCuerpoFactura.setFechafactura(SysmanFunciones
                                .nvl(rs2.getCampos().get("FECHAFACTURA"), "")
                                .toString());

                paramCuerpoFactura.setFechaVencimiento(SysmanFunciones
                                .nvl(rs2.getCampos().get("FECHA_VENCIMIENTO"),
                                                "")
                                .toString());

                paramCuerpoFactura.setNumerofactura(
                                Integer.parseInt(numeroFactura));

                paramCuerpoFactura.setTelefonoCliente(SysmanFunciones
                                .nvl(rs2.getCampos().get("TELEFONOS"), "")
                                .toString());

                paramCuerpoFactura.setTipoPago(SysmanFunciones
                                .nvl(rs2.getCampos().get("TIPO_PAGO"), "")
                                .toString());

                paramCuerpoFactura.setMedioPago(SysmanFunciones
                                .nvl(rs2.getCampos().get("MEDIOPAGO"), "")
                                .toString());

                paramCuerpoFactura.setTipoMoneda(SysmanFunciones
                                .nvl(rs2.getCampos().get("TIPO_MONEDA"), "")
                                .toString());

                paramCuerpoFactura.setPrefijo(SysmanFunciones
                                .nvl(rs2.getCampos().get("PREFIJO"), "")
                                .toString());

                paramCuerpoFactura.setTipoOperacion(SysmanFunciones
                                .nvl(rs2.getCampos().get("TIPOFACDIAN"), "")
                                .toString());

                paramCuerpoFactura.setObservacionesFactura(strObservaciones
                    + "<br/>" + observacionAdicional + "<br/>" + datosFacturaBancos);

                paramCuerpoFactura.setReviso(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "SF NOMBRE REVISO EN FACTURA",
                                                SessionUtil.getModulo(),
                                                new Date(), false), "")
                                .toString());

                paramCuerpoFactura.setDatosSoftware(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "DATOS SOFTWARE FACTURA ELECTRONICA",
                                                SessionUtil.getModulo(),
                                                new Date(), false), "")
                                .toString());
                
                //7742153_FACGENERAL prefijo+contrato
                paramCuerpoFactura.setPrefijoOrden(SysmanFunciones
                        .nvl(rs2.getCampos().get("PREFIJOORDEN"), "")
                        .toString());
                
                paramCuerpoFactura.setNumeroPrefijoOrden(SysmanFunciones
                        .nvl(rs2.getCampos().get("NUMEROPREFIJOORDEN"), "")
                        .toString());
                
                if (!"0".equals(rs2.getCampos().get("DIFERIDA").toString()))
                {

                    paramCuerpoFactura.setNumCuotas(SysmanFunciones
                                    .nvl(rs2.getCampos().get(
                                                    "CUOTAS_DIFERIDAS"), "0")
                                    .toString());

                }

                // Productos

                List<ParametrosItems> listaParamItems = new ArrayList<>();
                int contador = 0;
                Map<String, Object> param3 = new TreeMap<>();
                /**
                 * @author ldiaz
                 * en este punto se manupula la hora de la fecha solicitud del registro para que este no la lleve en ceros
                 * pues el systema requiere que este entre 1 y 12 
                 */
                fechaSolicitud = (Date) registro
                			.getCampos()
                				.get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD
                						.getValue());
                formatFecha = new SimpleDateFormat("dd/MM/yyyy");
                           
                param3.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param3.put(GeneralParameterEnum.FECHAINICIAL.getName(),formatFecha.format(fechaSolicitud));
                param3.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(fechaSolicitud));
                param3.put("NUMEROFACTURA", numeroFactura);
                param3.put("TIPOCOBRO", tipoFactura);

                List<Registro> listaProductos = RegistroConverter
                                .toListRegistro(
                                                requestManager.getList(
                                                                UrlServiceUtil
                                                                                .getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmFactLoteControladorUrlEnum.URL2974
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                param3));

                if (!listaProductos.isEmpty())
                {

                    for (Registro reg3 : listaProductos)
                    {

                        valorIVAConcepto = SysmanFunciones.nvlDbl(reg3
                                        .getCampos().get("BASEIMPUESTOIVA"), 0)
                            *
                            (SysmanFunciones.nvlDbl(
                                            reg3.getCampos().get("PORCIVA"), 0)
                                / 100);

                        sumProdPorFactura = sumProdPorFactura
                            + (SysmanFunciones.nvlDbl(reg3
                                            .getCampos().get("VALORUNITARIO"),
                                            0)

                                * SysmanFunciones.nvlDbl(reg3
                                                .getCampos().get("CANTIDAD"),
                                                0))
                            - SysmanFunciones.nvlDbl(reg3
                                            .getCampos().get("VALOR_DESCUENTO"),
                                            0)
                            + valorIVAConcepto;

                        ParametrosItems paramItems = new ParametrosItems();

                        paramItems.setCodigoproducto(SysmanFunciones
                                        .nvl(reg3.getCampos().get(
                                                        "CODIGOPRODUCTO"), "")
                                        .toString());
                        /**
						 * @author ljdiaz
						 * @descrpcion: se cambia el tipo de dato que recibe la cantidad de el producto, para que esta reciba cantidades decimales y entenera.
						 */
						paramItems.setCantidad(Double.parseDouble(reg3.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString()));

                        paramItems.setDescripcionproducto(SysmanFunciones
                                        .nvl(reg3.getCampos().get(
                                                        "DESCRIPCIONPRODUCTO"),
                                                        "")
                                        .toString());

                        paramItems.setValorunitario(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("VALORUNITARIO"),
                                                                        "0")
                                                        .toString()));

                        paramItems.setTipoDescuento("05");

                        paramItems.setDescuentoItem(SysmanFunciones
                                        .nvl(reg3.getCampos().get(
                                                        "VALOR_DESCUENTO"),
                                                        "0")
                                        .toString());

                        paramItems.setTotalitem(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("TOTALITEM"),
                                                                        "0")
                                                        .toString()));

                        List<ParametrosItemsImpuestos> listaParamItemImpuestos = new ArrayList<>();

                        ParametrosItemsImpuestos paramItemImpuestos = new ParametrosItemsImpuestos();
                        paramItemImpuestos.setTipo("01");

                        paramItemImpuestos.setBase(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTOIVA"),
                                                                        "0")
                                                        .toString()));

                        paramItemImpuestos.setPorcentaje(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTOIVA"),
                                                                        "0")
                                                        .toString()) == 0
                                                            ? 0
                                                            : Double.parseDouble(
                                                                            SysmanFunciones
                                                                                            .nvl(reg3.getCampos()
                                                                                                            .get("PORCIVA"),
                                                                                                            "0")
                                                                                            .toString()));

                        paramItemImpuestos.setValor(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("VALORIMPUESTO"),
                                                                        "0")
                                                        .toString()));

                        ParametrosItemsImpuestos paramItemImpuestos2 = new ParametrosItemsImpuestos();
                        paramItemImpuestos2.setTipo("06");

                        paramItemImpuestos2.setBase(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTORETEFUENTE"),
                                                                        "0")
                                                        .toString()));

                        paramItemImpuestos2.setPorcentaje(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTORETEFUENTE"),
                                                                        "0")
                                                        .toString()) == 0
                                                            ? 0
                                                            : Double.parseDouble(
                                                                            SysmanFunciones
                                                                                            .nvl(reg3.getCampos()
                                                                                                            .get("PORCENTAJERETEFUENTEX"),
                                                                                                            "0")
                                                                                            .toString()));

                        paramItemImpuestos2.setValor(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("VALOR_RETEFUENTE"),
                                                                        "0")
                                                        .toString()));

                        ParametrosItemsImpuestos paramItemImpuestos3 = new ParametrosItemsImpuestos();
                        paramItemImpuestos3.setTipo("03");

                        paramItemImpuestos3.setBase(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTOICA"),
                                                                        "0")
                                                        .toString()));

                        paramItemImpuestos3.setPorcentaje(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTOICA"),
                                                                        "0")
                                                        .toString()) == 0
                                                            ? 0
                                                            : Double.parseDouble(
                                                                            SysmanFunciones
                                                                                            .nvl(reg3.getCampos()
                                                                                                            .get("PORCENTAJEICAX"),
                                                                                                            "0")
                                                                                            .toString()));

                        paramItemImpuestos3.setValor(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("VALOR_ICA"),
                                                                        "0")
                                                        .toString()));

                        ParametrosItemsImpuestos paramItemImpuestos4 = new ParametrosItemsImpuestos();
                        paramItemImpuestos4.setTipo("05");

                        paramItemImpuestos4.setBase(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTORETEIVA"),
                                                                        "0")
                                                        .toString()));

                        paramItemImpuestos4.setPorcentaje(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTORETEIVA"),
                                                                        "0")
                                                        .toString()) == 0
                                                            ? 0
                                                            : Double.parseDouble(
                                                                            SysmanFunciones
                                                                                            .nvl(reg3.getCampos()
                                                                                                            .get("PORCENTAJERETEIVADX"),
                                                                                                            "0")
                                                                                            .toString()));

                        paramItemImpuestos4.setValor(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("RETEIVA"),
                                                                        "0")
                                                        .toString()));

                        ParametrosItemsImpuestos paramItemImpuestos5 = new ParametrosItemsImpuestos();
                        paramItemImpuestos5.setTipo("07");

                        paramItemImpuestos5.setBase(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTORETEICA"),
                                                                        "0")
                                                        .toString()));

                        paramItemImpuestos5.setPorcentaje(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTORETEICA"),
                                                                        "0")
                                                        .toString()) == 0
                                                            ? 0
                                                            : Double.parseDouble(
                                                                            SysmanFunciones
                                                                                            .nvl(reg3.getCampos()
                                                                                                            .get("PORCENTAJERETEICAX"),
                                                                                                            "0")
                                                                                            .toString()));

                        paramItemImpuestos5.setValor(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("RETEICA"),
                                                                        "0")
                                                        .toString()));

                        ParametrosItemsImpuestos paramItemImpuestos6 = new ParametrosItemsImpuestos();
                        paramItemImpuestos6.setTipo("02");

                        paramItemImpuestos6.setBase(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTOINC"),
                                                                        "0")
                                                        .toString()));

                        paramItemImpuestos6.setPorcentaje(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("BASEIMPUESTOINC"),
                                                                        "0")
                                                        .toString()) == 0
                                                            ? 0
                                                            : Integer.parseInt(
                                                                            SysmanFunciones
                                                                                            .nvl(reg3.getCampos()
                                                                                                            .get("PORCENTAJEINCX"),
                                                                                                            "0")
                                                                                            .toString()));

                        paramItemImpuestos6.setValor(
                                        Double.parseDouble(SysmanFunciones
                                                        .nvl(reg3.getCampos()
                                                                        .get("IMPUESTO_INC"),
                                                                        "0")
                                                        .toString()));

                        if (paramItemImpuestos.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos);
                        }
                        if (paramItemImpuestos2.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos2);
                        }
                        if (paramItemImpuestos3.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos3);
                        }
                        if (paramItemImpuestos4.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos4);
                        }
                        if (paramItemImpuestos5.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos5);
                        }
                        if (paramItemImpuestos6.getBase() != 0.0)
                        {
                            listaParamItemImpuestos.add(paramItemImpuestos6);
						}                      
                          
                        paramItems.setImpuestos(listaParamItemImpuestos);

                        listaParamItems.add(contador, paramItems);

                        contador++;

                        nConceptos++;
                    }

                }
                // agregar lista items
                paramCuerpoFactura.setItems(listaParamItems);
                Map<String, Object> param4 = new TreeMap<>();
                /**
                 * @author ldiaz
                 * en este punto se manupula la hora de la fecha solicitud del registro para que este no la lleve en ceros
                 * pues el systema requiere que este entre 1 y 12 
                 */
                fechaSolicitud = (Date) registro
                			.getCampos()
                				.get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD
                						.getValue());
                formatFecha = new SimpleDateFormat("dd/MM/yyyy");
                           
                param4.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param4.put(GeneralParameterEnum.FECHAINICIAL.getName(),formatFecha.format(fechaSolicitud));
                param4.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(fechaSolicitud));
                param4.put("NUMEROFACTURA", numeroFactura);
                param4.put("TIPOCOBRO", tipoFactura);

                /*Registro rs4 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL4987
                                                                                                .getValue())
                                                                .getUrl(),
                                                param4));

                if (rs4 != null)
                {

                    descuento = SysmanFunciones.nvlDbl(rs4.getCampos().get(
                                    GeneralParameterEnum.TOTAL.getName()), 0);
                    
                  //7716448 mrosero (05/08/2022)              
                    	descuento = descuento*-1;                   
                    
                } */ //COMENTADO POR  JM CC 2693 (OSEA WDF porque el descuento es el total de la factura??? quien me explica??)

                List<ParametrosCargos> listaParamCargos = new ArrayList<>();
                List<ParametrosDescuentos> listaParamDescuentos = new ArrayList<>();

                Registro rs4 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL4587
                                                                                                .getValue())
                                                                .getUrl(),
                                                param4));

                if (rs4 != null)
                {

                    if (digRedDian == 0)
                    {
                        totalFactura = SysmanFunciones.redondear(
                                        SysmanFunciones.nvlDbl((SysmanFunciones
                                                        .nvlDbl(rs4.getCampos()
                                                                        .get(
                                                                                        GeneralParameterEnum.TOTAL
                                                                                                        .getName()),
                                                                        0)
                                            -
                                            (SysmanFunciones.nvlDbl(rs4
                                                            .getCampos()
                                                            .get("DESCUENTO_FACTURA"),
                                                            0)
                                                / nConceptos)
                                            - descuento), 0),
                                        2);
                    }
                    else if (digRedDian % 10 == 0)
                    {
                        totalFactura = SysmanFunciones.redondear((SysmanFunciones.nvlDbl(SysmanFunciones
                                        .nvlDbl(rs4.getCampos()
                                                        .get(
                                                                        GeneralParameterEnum.TOTAL
                                                                                        .getName()),
                                                        0)
                            - (SysmanFunciones.nvlDbl(rs4
                                            .getCampos()
                                            .get("DESCUENTO_FACTURA"),
                                            0)
                                / nConceptos)
                            - descuento, 0) / digRedDian + 0.501) , digRedDian);

                    }
                    else
                    {
                        totalFactura = SysmanFunciones.redondear(SysmanFunciones.nvlDbl((SysmanFunciones
                                        .nvlDbl(rs4.getCampos().get(
                                                        GeneralParameterEnum.TOTAL
                                                                        .getName()),
                                                        0)
                            -
                            (SysmanFunciones.nvlDbl(rs4.getCampos()
                                            .get("DESCUENTO_FACTURA"), 0)
                                / nConceptos)
                            - descuento), 0) , digRedDian);
                    }

                    if (sumProdPorFactura < totalFactura &&
                        (totalFactura - sumProdPorFactura) > 1)
                    {

                        ParametrosCargos paramCargos = new ParametrosCargos();

                        paramCargos.setValor((int) SysmanFunciones
                                        .redondear(totalFactura
                                            - sumProdPorFactura, 2));

                        listaParamCargos.add(paramCargos);
                        
                       


                    }
                    else if (sumProdPorFactura > totalFactura &&
                        (sumProdPorFactura - totalFactura) > 1)
                    {

                        redondeodescuento = SysmanFunciones.redondear(
                                        sumProdPorFactura
                                            - totalFactura,
                                        2);

                        ParametrosDescuentos paramDescuento = new ParametrosDescuentos();

                        paramDescuento.setTipo("05");

                        paramDescuento.setValor(
                                        (int) redondeodescuento);

                        listaParamDescuentos.add(paramDescuento);

                    } else if ((sumProdPorFactura - totalFactura) == 0 && SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0) > 0)  // JM CC 2693
                        {
                    	
						
						                        	
						ParametrosDescuentos paramDescuento = new ParametrosDescuentos();
						                        	
						paramDescuento.setTipo("05");
						
						paramDescuento.setValor((int) SysmanFunciones.redondear(SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0), 2));
						
						listaParamDescuentos.add(paramDescuento);
						

                        }
                }
                
                if(listaParamCargos.size() != 0)
                	paramCuerpoFactura.setCargos(listaParamCargos);
                if(listaParamDescuentos.size() != 0)
                	paramCuerpoFactura.setDescuentos(listaParamDescuentos);

                rs4 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL7452
                                                                                                .getValue())
                                                                .getUrl(),
                                                param4));

                if (rs4 != null)
                {

                    paramCuerpoFactura.setSubtotalfactura(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get(
                                                    "SUBTOTALFACTURA"), "0")
                                                    .toString()));

                    paramCuerpoFactura.setValorfactura(totalFactura);

                    paramCuerpoFactura.setReteFuente(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get(
                                                    "RETE"), "0")
                                                    .toString()));

                    paramCuerpoFactura.setReteIva(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get(
                                                    "RETE_IVA"), "0")
                                                    .toString()));

                    paramCuerpoFactura
                                    .setTotalBaseGravableIva(Double.parseDouble(
                                                    SysmanFunciones.nvl(rs4
                                                                    .getCampos()
                                                                    .get(
                                                                                    "BASEGRAVABLEIVA"),
                                                                    "0")
                                                                    .toString()));

                    paramCuerpoFactura
                                    .setTotalBaseGravableRete(
                                                    Double.parseDouble(
                                                                    SysmanFunciones.nvl(
                                                                                    rs4
                                                                                                    .getCampos()
                                                                                                    .get(
                                                                                                                    "BASEGRAVABLERETE"),
                                                                                    "0")
                                                                                    .toString()));

                    paramCuerpoFactura
                                    .setTotalBaseGravableIca(Double.parseDouble(
                                                    SysmanFunciones.nvl(rs4
                                                                    .getCampos()
                                                                    .get(
                                                                                    "BASEGRAVABLEICA"),
                                                                    "0")
                                                                    .toString()));

                    paramCuerpoFactura.setTotalBaseGravableReteiva(
                                    Double.parseDouble(
                                                    SysmanFunciones.nvl(rs4
                                                                    .getCampos()
                                                                    .get(
                                                                                    "BASEGRAVABLERETEIVA"),
                                                                    "0")
                                                                    .toString()));

                    paramCuerpoFactura
                                    .setTotalBaseGravableIca(Double.parseDouble(
                                                    SysmanFunciones.nvl(rs4
                                                                    .getCampos()
                                                                    .get(
                                                                                    "BASEGRAVABLERETEICA"),
                                                                    "0")
                                                                    .toString()));

                    paramCuerpoFactura
                                    .setTotalBaseGravableInc(Double.parseDouble(
                                                    SysmanFunciones.nvl(rs4
                                                                    .getCampos()
                                                                    .get(
                                                                                    "BASEGRAVABLEINC"),
                                                                    "0")
                                                                    .toString()));
                    paramCuerpoFactura.setValorIvaFactura(Double.parseDouble(
                            SysmanFunciones.nvl(rs4.getCampos().get(
                                            "IVA"), "0")
                                            .toString()));

                    paramCuerpoFactura.setValorIcaFactura(Double.parseDouble(
                            SysmanFunciones.nvl(rs4.getCampos().get(
                                            "ICA"), "0")
                                            .toString()));

                    
                    
                    paramCuerpoFactura.setNumeroConceptos(nConceptos);

                    paramCuerpoFactura.setReteIca(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get(
                                                    "RETE_ICA"), "0")
                                                    .toString()));

                    paramCuerpoFactura.setValorIncFactura(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get(
                                                    "INC"), "0")
                                                    .toString()));
                    
                    if(paramCuerpoFactura.getValorIvaFactura() == 0 && 
                    		paramCuerpoFactura.getValorIcaFactura() == 0 &&
                    			paramCuerpoFactura.getValorIncFactura() == 0 &&
                    				paramCuerpoFactura.getReteIva() == 0 &&
                    					paramCuerpoFactura.getReteFuente() == 0 &&
                    						paramCuerpoFactura.getReteIca() == 0) {
                    	paramCuerpoFactura.setTotalBaseImponible(Double.parseDouble(
                                "0"));
                    }else {
                    	if(nConceptos > 1 ) {
                    		paramCuerpoFactura.setTotalBaseImponible(paramCuerpoFactura
                                    .getTotalBaseGravableIva() + paramCuerpoFactura
                                    .getTotalBaseGravableIca() + paramCuerpoFactura
                                    .getTotalBaseGravableInc() - SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0) ); //JM CC 2693
                    	}else {
                    		paramCuerpoFactura.setTotalBaseImponible(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get(
                                                    "BASEIMPONIBLE"), "0")
                                                    .toString())- SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTOPARCIAL"), 0) );//JM CC 2693
                    	}
                    }
                    paramCuerpoFactura.setDescuentoItems(Double.parseDouble(
                                    SysmanFunciones.nvl(rs4.getCampos().get(
                                                    "DESCUENTOPARCIAL"), "0")
                                                    .toString()));

                    paramCuerpoFactura.setDescuentoFactura(
                                    (SysmanFunciones.nvlDbl(
                                                    rs4.getCampos().get(
                                                                    "DESCUENTO_FACTURA"),
                                                    0)

                                        / nConceptos + redondeodescuento));
                    // correcion de toma de campo descripcion que no sobrepase los 250 caracteres
                    String descFact = SysmanFunciones
                            .nvl(rs4.getCampos().get(
                                    "OBSERVACIONES"), "0").toString();
                    
                    paramCuerpoFactura.setDescripcion(descFact);

                }
                // debido a que es cambio transversal se crea validacion para que en caso de haberla puesto en 0, 
                // se consulte de nuevo y se pueda llevar dicha descripcio - 7741036 telepacifico
                if(paramCuerpoFactura.getDescripcion().equals("0")) {
                	Registro rsDes = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmFactLoteControladorUrlEnum.URL661076
                                                                                            .getValue())
                                                            .getUrl(),
                                            param4));
                	if(rsDes != null) {
                		String descFact = SysmanFunciones
                                .nvl(rsDes.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()),"0").toString();
                		paramCuerpoFactura.setDescripcion(descFact);
                	}
                }
                

                List<ParametrosImpuestos> listaParamImpuestos = new ArrayList<>();

                // impuestos
                // PORCENTAJEIVAX
                Registro rs5 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL5768
                                                                                                .getValue())
                                                                .getUrl(),
                                                param4));

                ParametrosImpuestos paramImpuestos = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos.setTipo("01");
                    paramImpuestos.setBase(0);
                    paramImpuestos.setPorcentaje(0);
                    paramImpuestos.setValor(0);

                    // listaParamImpuestos.add(0, paramImpuestos);

                }
                else
                {
                    paramImpuestos.setTipo("01");
                    paramImpuestos.setBase(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEBASEIMPUESTOIVA"),
                                                    "0").toString()));

                    paramImpuestos.setPorcentaje(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "PORCIVA"),
                                                    "0").toString()));

                    paramImpuestos.setValor(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEVALORIMPUESTO"),
                                                    "0").toString()));

                    // listaParamImpuestos.add(0, paramImpuestos);
                }
                if (paramImpuestos.getBase() != 0)
                {
                    listaParamImpuestos.add(0, paramImpuestos);
                }
                // PORCENTAJERETEFUENTEX

                rs5 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL5769
                                                                                                .getValue())
                                                                .getUrl(),
                                                param4));

                ParametrosImpuestos paramImpuestos2 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos2.setTipo("06");
                    paramImpuestos2.setBase(0);
                    paramImpuestos2.setPorcentaje(0);
                    paramImpuestos2.setValor(0);

                    // listaParamImpuestos.add(1, paramImpuestos2);

                }
                else
                {
                    paramImpuestos2.setTipo("06");
                    paramImpuestos2.setBase(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEBASEIMPUESTORETEFUENTE"),
                                                    "0").toString()));

                    paramImpuestos2.setPorcentaje(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    GeneralParameterEnum.PORCENTAJE
                                                                                    .getName()),
                                                    "0").toString()));

                    paramImpuestos2.setValor(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEVALOR_RETEFUENTE"),
                                                    "0").toString()));

                    // listaParamImpuestos.add(1, paramImpuestos2);
                }
                if (paramImpuestos2.getBase() != 0)
                {
                    listaParamImpuestos.add(1, paramImpuestos2);
                }
                // PORCENTAJEICAX

                rs5 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL5770
                                                                                                .getValue())
                                                                .getUrl(),
                                                param4));

                ParametrosImpuestos paramImpuestos3 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos3.setTipo("03");
                    paramImpuestos3.setBase(0);
                    paramImpuestos3.setPorcentaje(0);
                    paramImpuestos3.setValor(0);

                    // listaParamImpuestos.add(2, paramImpuestos3);

                }
                else
                {
                    paramImpuestos3.setTipo("03");
                    paramImpuestos3.setBase(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEBASEIMPUESTOICA"),
                                                    "0").toString()));

                    paramImpuestos3.setPorcentaje(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    GeneralParameterEnum.PORCENTAJE
                                                                                    .getName()),
                                                    "0").toString()));

                    paramImpuestos3.setValor(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEVALOR_ICA"),
                                                    "0").toString()));

                    // listaParamImpuestos.add(2, paramImpuestos3);
                }
                if (paramImpuestos3.getBase() != 0)
                {
                    listaParamImpuestos.add(2, paramImpuestos3);
                }
                // PORCENTAJERETEIVADX
                rs5 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL5771
                                                                                                .getValue())
                                                                .getUrl(),
                                                param4));

                ParametrosImpuestos paramImpuestos4 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos4.setTipo("05");
                    paramImpuestos4.setBase(0);
                    paramImpuestos4.setPorcentaje(0);
                    paramImpuestos4.setValor(0);

                    // listaParamImpuestos.add(3, paramImpuestos4);

                }
                else
                {
                    paramImpuestos4.setTipo("05");
                    paramImpuestos4.setBase(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEBASEIMPUESTORETEIVA"),
                                                    "0").toString()));

                    paramImpuestos4.setPorcentaje(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    GeneralParameterEnum.PORCENTAJE
                                                                                    .getName()),
                                                    "0").toString()));

                    paramImpuestos4.setValor(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADERETEIVA"),
                                                    "0").toString()));

                    // listaParamImpuestos.add(3, paramImpuestos4);
                }
                if (paramImpuestos4.getBase() != 0)
                {
                    listaParamImpuestos.add(3, paramImpuestos4);
                }
                // PORCENTAJERETEICAX

                rs5 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL5772
                                                                                                .getValue())
                                                                .getUrl(),
                                                param4));

                ParametrosImpuestos paramImpuestos5 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos5.setTipo("07");
                    paramImpuestos5.setBase(0);
                    paramImpuestos5.setPorcentaje(0);
                    paramImpuestos5.setValor(0);

                    // listaParamImpuestos.add(4, paramImpuestos5);

                }
                else
                {
                    paramImpuestos5.setTipo("07");
                    paramImpuestos5.setBase(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEBASEIMPUESTORETEICA"),
                                                    "0").toString()));

                    paramImpuestos5.setPorcentaje(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    GeneralParameterEnum.PORCENTAJE
                                                                                    .getName()),
                                                    "0").toString()));

                    paramImpuestos5.setValor(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEIMPUESTO_INC"),
                                                    "0").toString()));

                    // listaParamImpuestos.add(4, paramImpuestos5);
                }
                if (paramImpuestos5.getBase() != 0)
                {
                    listaParamImpuestos.add(4, paramImpuestos5);
                }
                // PORCENTAJEINCX

                rs5 = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmFactLoteControladorUrlEnum.URL5773
                                                                                                .getValue())
                                                                .getUrl(),
                                                param4));

                ParametrosImpuestos paramImpuestos6 = new ParametrosImpuestos();
                if (rs5 == null)
                {

                    paramImpuestos6.setTipo("02");
                    paramImpuestos6.setBase(0);
                    paramImpuestos6.setPorcentaje(0);
                    paramImpuestos6.setValor(0);

                    // listaParamImpuestos.add(5, paramImpuestos6);

                }
                else
                {
                    paramImpuestos6.setTipo("02");
                    paramImpuestos6.setBase(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEBASEIMPUESTOINC"),
                                                    "0").toString()));

                    paramImpuestos6.setPorcentaje(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    GeneralParameterEnum.PORCENTAJE
                                                                                    .getName()),
                                                    "0").toString()));

                    paramImpuestos6.setValor(
                                    Double.parseDouble(SysmanFunciones.nvl(
                                                    rs5.getCampos().get(
                                                                    "SUMADEIMPUESTO_INC"),
                                                    "0").toString()));

                    // listaParamImpuestos.add(5, paramImpuestos6);
                }
                if (paramImpuestos6.getBase() != 0)
                {
                    listaParamImpuestos.add(5, paramImpuestos6);
                }
                paramCuerpoFactura.setImpuestos(listaParamImpuestos);
            }

            List<ParametroCuerpoEnvioFactura> listaCuerpoFactura = new ArrayList<>();
            listaCuerpoFactura.add(paramCuerpoFactura);

            paramFactura.setFacturas(listaCuerpoFactura);

            APIFrida api2 = new APIFrida();

            Gson gson2 = new Gson();
            String json = gson2.toJson(paramFactura,
                            ParametrosEnvioFactura.class);

            respuesta = api2.postEnvioFactura(url, json);

            RespuestaApi respuestaApi = gson2.fromJson(respuesta,
                            RespuestaApi.class);

            if (respuestaApi.getCodigo() != 0)
            {
                respuesta = respuestaApi.getMensaje();
            }

            return respuesta + "\n" + json;

        }
        catch (SystemException | IOException
                        | com.sysman.util.SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;

    }

    private void validarRango(String url)
    {
        borrarDatosTablaDetRangoFact();

        String respuesta;
        APIFrida api = new APIFrida();

        try
        {
            respuesta = api.cargarRangoFacturacion(nitCompania, url);

            Gson gson = new Gson();
            RespuestaRangoFacturacion respuestaApi = gson.fromJson(
                            respuesta,
                            RespuestaRangoFacturacion.class);

            for (RespuestaCuerpoRangoFacturacion respuestaCuerpoRangoFacturacion : respuestaApi
                            .getCuerpo())
            {
                insertarRangos(respuestaCuerpoRangoFacturacion);

            }

        }
        catch (IOException | com.sysman.util.SysmanException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void insertarRangos(
        RespuestaCuerpoRangoFacturacion respuestaCuerpoRangoFacturacion)
    {

        String urlEnumId = GenericUrlEnum.DET_RANGO_FACT
                        .getCreateKey();

        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

        Map<String, Object> params = new TreeMap<>();
        try
        {

            Date fechaDesde = formato.parse(
                            respuestaCuerpoRangoFacturacion
                                            .getFechadesde()
                                            .replace("-", "/"));

            Date fechaHasta = formato.parse(
                            respuestaCuerpoRangoFacturacion
                                            .getFechahasta()
                                            .replace("-", "/"));

            formato.applyPattern(SysmanFunciones.FORMATO_FECHA_ESTANDAR);

            params.put("ID", respuestaCuerpoRangoFacturacion.getId());

            params.put("PREFIJO", respuestaCuerpoRangoFacturacion.getPrefijo());

            params.put("NUMERO_RES",
                            respuestaCuerpoRangoFacturacion
                                            .getNumeroresolucion());
            params.put("CLAVE_TEC",
                            respuestaCuerpoRangoFacturacion.getClavetecnica());
            params.put("RANGO_INI",
                            respuestaCuerpoRangoFacturacion.getRangoinicial());
            params.put("RANGO_FIN",
                            respuestaCuerpoRangoFacturacion.getRangofinal());

            params.put("FECHADESDE",
                            formato.format(fechaDesde));

            params.put("FECHAHASTA",
                            formato.format(fechaHasta));

            params.put("TIPOAMBIENTE",
                            respuestaCuerpoRangoFacturacion
                                            .getIdFeTipoAmbiente());

            params.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            params.put(GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            params);
        }
        catch (SystemException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void borrarDatosTablaDetRangoFact()
    {
        Map<String, Object> params = new TreeMap<>();

        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionconceptosControladorUrlEnum.URL8521
                                                        .getValue());

        try
        {
            requestManager.delete(urlDelete.getUrl(), params);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void validarProducto(String url, String codigoProducto)
    {

        String respuesta;
        APIFrida api = new APIFrida();
        Gson gson = new Gson();

        try
        {
            respuesta = api.cargarItem(url, codigoProducto);

            RespuestaApi respuestaApi = gson.fromJson(
                            respuesta,
                            RespuestaApi.class);

            if (respuestaApi.getCodigo() != 0)
            {
                crearProducto(url, codigoProducto);
            }
        }
        catch (IOException | com.sysman.util.SysmanException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void crearProducto(String url, String codigoProducto)
    {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        param.put(GeneralParameterEnum.CODIGO.getName(), codigoProducto);

        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL2486
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rs != null)
            {
                ParamItem params = new ParamItem();
                ParamItems paramsItems = new ParamItems();

                List<ParamItem> listaParams = new ArrayList<>();

                params.setCodigoProducto(codigoProducto);
                params.setCreatedBy(SessionUtil.getUser().getCodigo());
                params.setDescripcionProducto(SysmanFunciones
                                .nvl(rs.getCampos().get("DESCRIPCIONPRODUCTO"),
                                                "")
                                .toString());

                params.setUnidadMedida(SysmanFunciones
                                .nvl(rs.getCampos().get("UNIDADMEDIDA"),
                                                "")
                                .toString());

                params.setValorItem(SysmanFunciones
                                .nvl(rs.getCampos().get("VALORITEM"),
                                                "")
                                .toString());

                listaParams.add(params);

                paramsItems.setItems(listaParams);

                Gson gson = new Gson();
                String json = gson.toJson(paramsItems,
                                ParamItems.class);
                APIFrida apiFrida = new APIFrida();

                apiFrida.postItem(url, json);

            }
        }
        catch (SystemException | IOException
                        | com.sysman.util.SysmanException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String crearTecero(String tercero, String url)
                    throws SysmanException
    {
        String respuesta = null;
        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.NIT.getName(), tercero);

        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL7354
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            ParametrosTerceroLote paramTercero = new ParametrosTerceroLote();

            ParametrosTercero param = new ParametrosTercero();
          //7742397_FACGENERAL
			if (rs.getCampos().get(GeneralParameterEnum.PAIS.getName()).equals("CO")) {
				
				param.setCodigomunicipio(SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGOCIUDAD.getName()), "").toString());

				param.setCiudad(
						SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CIUDAD.getName()), "").toString());

				param.setCodigodepartamento(
						SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGODEPARTAMENTO.getName()), "").toString());

				param.setDepartamento(SysmanFunciones
						.nvl(rs.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()), "").toString());

			} else {
				param.setCodigomunicipio(GeneralParameterEnum.CODMUNICIPIO.getName());

				param.setCiudad(GeneralParameterEnum.NOMCIUDAD.getName());

				param.setCodigodepartamento(GeneralParameterEnum.CODDEPARTAMENTO.getName());

				param.setDepartamento(GeneralParameterEnum.NOMCIUDAD.getName());			

			}	

            param.setCorreoelectronico(SysmanFunciones.nvl(
                            rs.getCampos().get("CORREOELECTRONICO"), "")
                            .toString());

            param.setDireccion(SysmanFunciones.nvl(
                            rs.getCampos().get(GeneralParameterEnum.DIRECCION
                                            .getName()),
                            "")
                            .toString());

            param.setDireccionfiscal(SysmanFunciones.nvl(
                            rs.getCampos().get("DIRECCIONFISCAL"), "")
                            .toString());

            param.setCodigopostal(SysmanFunciones.nvl(
                            rs.getCampos().get("CODIGOPOSTAL"), "")
                            .toString());

            param.setNumerodocumento(SysmanFunciones.nvl(
                            rs.getCampos().get("NUMERODOCUMENTO"), "")
                            .toString());

            param.setTelefono(SysmanFunciones.nvl(
                            rs.getCampos().get("TELEFONO"), "")
                            .toString());
            
            param.setNombretercero(SysmanFunciones.toString(SysmanFunciones.nvl(rs.getCampos().get("NOMBRETERCERO"), "")).replace("&", "").replaceAll("\\s+", " ").trim());

            param.setTipoidentificacion(SysmanFunciones.nvl(
                            rs.getCampos().get("TIPOIDENTIFICACION"), "")
                            .toString());

            param.setDigitoverificacion(SysmanFunciones.nvl(
                            rs.getCampos().get("DIGITOVERIFICACION"), "")
                            .toString());

			param.setPais(SysmanFunciones.nvl(
							rs.getCampos().get(GeneralParameterEnum.PAIS.getName()), "")
							.toString());

            param.setTipoorganizacion(SysmanFunciones.nvl(
                            rs.getCampos().get("TIPOORGANIZACION"), "")
                            .toString());

            param.setTiporegimen(SysmanFunciones.nvl(
                            rs.getCampos().get("TIPOREGIMEN"), "")
                            .toString());
            

            // Iteracion sobre obligaciones fisacles del tercero

            Map<String, Object> param2 = new TreeMap<>();

            param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param2.put(GeneralParameterEnum.NIT.getName(), tercero);

            List<Registro> listaObligaciones = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil
                                                                            .getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FacturacionconceptosControladorUrlEnum.URL2054
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param2));

            if (!listaObligaciones.isEmpty())
            {
                String responsabilidadesFiscales = "";

                for (Registro reg : listaObligaciones)
                {
                    responsabilidadesFiscales = responsabilidadesFiscales + ","
                        + reg
                                        .getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName())
                                        .toString();
                }

                param.setResponsabilidadesfiscales(
                                responsabilidadesFiscales.substring(1,
                                                responsabilidadesFiscales
                                                                .length()));
            }

            paramTercero.setContribuyente(nitCompania);

            List<ParametrosTercero> listaParam = new ArrayList<>();

            listaParam.add(param);

            paramTercero.setTerceros(listaParam);

            Gson gson = new Gson();
            String json = gson.toJson(paramTercero, ParametrosTerceroLote.class);
            APIFrida apiFrida = new APIFrida();

            respuesta = apiFrida.postTercero(url, json);

        }
        catch (SystemException | IOException | com.sysman.util.SysmanException
                        | RuntimeException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return respuesta;

    }
    
    /**
	 * Se crea metodo que permite realizar la actualizacion del tercero
	 * @param tercero
	 * @param url
	 * @return
	 * @throws SysmanException
	 */
	private String actualizaTecero(String tercero, String url) throws SysmanException {
		String respuesta = null;
		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), tercero);

		try {
			Registro rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FacturacionconceptosControladorUrlEnum.URL7354.getValue())
									.getUrl(),
							params));

			ParametrosTerceroLote paramTercero = new ParametrosTerceroLote();

			ParametrosTercero param = new ParametrosTercero();

			//7742397_FACGENERAL
		if (rs.getCampos().get(GeneralParameterEnum.PAIS.getName()).equals("CO")) {
			
			param.setCodigomunicipio(SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGOCIUDAD.getName()), "").toString());

			param.setCiudad(
					SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CIUDAD.getName()), "").toString());

			param.setCodigodepartamento(
					SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGODEPARTAMENTO.getName()), "").toString());

			param.setDepartamento(SysmanFunciones
					.nvl(rs.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()), "").toString());

		} else {
			param.setCodigomunicipio(GeneralParameterEnum.CODMUNICIPIO.getName());

			param.setCiudad(GeneralParameterEnum.NOMCIUDAD.getName());

			param.setCodigodepartamento(GeneralParameterEnum.CODDEPARTAMENTO.getName());

			param.setDepartamento(GeneralParameterEnum.NOMCIUDAD.getName());			

		}
		
			param.setCorreoelectronico(SysmanFunciones.nvl(rs.getCampos().get("CORREOELECTRONICO"), "").toString());

			param.setDireccion(
					SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.DIRECCION.getName()), "").toString());

			param.setDireccionfiscal(SysmanFunciones.nvl(rs.getCampos().get("DIRECCIONFISCAL"), "").toString());

			param.setCodigopostal(SysmanFunciones.nvl(rs.getCampos().get("CODIGOPOSTAL"), "").toString());

			param.setNumerodocumento(SysmanFunciones.nvl(rs.getCampos().get("NUMERODOCUMENTO"), "").toString());

			param.setTelefono(SysmanFunciones.nvl(rs.getCampos().get("TELEFONO"), "").toString());

			param.setNombretercero(SysmanFunciones.toString(SysmanFunciones.nvl(rs.getCampos().get("NOMBRETERCERO"), "")).replace("&", "").replaceAll("\\s+", " ").trim());

			param.setTipoidentificacion(SysmanFunciones.nvl(rs.getCampos().get("TIPOIDENTIFICACION"), "").toString());

			param.setDigitoverificacion(SysmanFunciones.nvl(rs.getCampos().get("DIGITOVERIFICACION"), "").toString());

			param.setPais(SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.PAIS.getName()), "").toString());

			param.setTipoorganizacion(SysmanFunciones.nvl(rs.getCampos().get("TIPOORGANIZACION"), "").toString());

			param.setTiporegimen(SysmanFunciones.nvl(rs.getCampos().get("TIPOREGIMEN"), "").toString());
			
			param.setContribuyente(nitCompania);

			// Iteracion sobre obligaciones fisacles del tercero

			Map<String, Object> param2 = new TreeMap<>();

			param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param2.put(GeneralParameterEnum.NIT.getName(), tercero);

			List<Registro> listaObligaciones = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FacturacionconceptosControladorUrlEnum.URL2054.getValue())
									.getUrl(),
							param2));

			if (!listaObligaciones.isEmpty()) {
				String responsabilidadesFiscales = "";

				for (Registro reg : listaObligaciones) {
					responsabilidadesFiscales = responsabilidadesFiscales + ","
							+ reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
				}

				param.setResponsabilidadesfiscales(
						responsabilidadesFiscales.substring(1, responsabilidadesFiscales.length()));
			}

			paramTercero.setContribuyente(nitCompania);

			List<ParametrosTercero> listaParam = new ArrayList<>();

			listaParam.add(param);

			paramTercero.setTerceros(listaParam);

			Gson gson = new Gson();
			String json = gson.toJson(param, ParametrosTercero.class);
			APIFrida apiFrida = new APIFrida();

			respuesta = apiFrida.putTercero(url, json);

		} catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta;

	}

    private boolean validarEnviarDian()
    {
        if (SysmanFunciones
                        .validarVariableVacio(SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        nroFacturaCons), "")
                                        .toString())
            || SysmanFunciones.validarVariableVacio(
                            SysmanFunciones.nvl(
                                            registro.getCampos().get(
                                                            "TIPOFACTURA"),
                                            "").toString())
            || !(boolean) registro.getCampos().get(impresoCons))
        {

            return false;
        }

        return true;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ImprimirFacturaElectronica
     * en la vista
     *
     */
    public void oprimirImprimirFacturaElectronica()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String url = "";
        
        String facturadorExterno;
		try {
			facturadorExterno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					MANEJA_FACTURACION_ELECTRONICA_EXTERNA, "69", new Date(), false), "NO");

			if (facturadorExterno.equals("NO")) {
		
		        Date fechaVencimiento = (Date) registro.getCampos()
		                        .get(FacturacionconceptosControladorEnum.FECHA_VENCIMIENTO
		                                        .getValue());
		
		        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
		        Map<String, Object> param = new TreeMap<>();
		
		        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		        param.put(GeneralParameterEnum.FECHAINICIAL.getName(), formatFecha.format(registro
		                        .getCampos()
		                        .get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD
		                                        .getValue())));
		        param.put(GeneralParameterEnum.FECHAFINAL.getName(), formatFecha.format(registro
		                        .getCampos()
		                        .get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD
		                                        .getValue())));
		
		        param.put(FacturacionconceptosControladorEnum.NUMEROFACTURA
		                        .getValue(),
		                        retornarString(registro, nroFacturaCons));
		        param.put(tipoCobroCons, tipoCobro);
		
		        List<Registro> listaFactSysman;
		        try
		        {
		            listaFactSysman = RegistroConverter
		                            .toListRegistro(
		                                            requestManager.getList(
		                                                            UrlServiceUtil
		                                                                            .getInstance()
		                                                                            .getUrlServiceByUrlByEnumID(
		                                                                                            FacturacionconceptosControladorUrlEnum.URL3579
		                                                                                                            .getValue())
		                                                                            .getUrl(),
		                                                            param));
		
		            for (Registro reg : listaFactSysman)
		            {
		                String prefijo = SysmanFunciones
		                                .nvl(reg.getCampos().get("PREFIJO"), "")
		                                .toString();
		
		                String numeroFactura = SysmanFunciones
		                                .nvl(reg.getCampos()
		                                                .get(FacturacionconceptosControladorEnum.NUMEROFACTURA
		                                                                .getValue()),
		                                                "")
		                                .toString();
		
		                String tipoFormato;
		                if ("NC".equals(prefijo))
		                    tipoFormato = "02";
		                else
		                {
		                    tipoFormato = "ND".equals(prefijo) ? "03" : "01";
		                }
		
		                ParametroEjecucionApiReporte paramEjecucion = new ParametroEjecucionApiReporte();
		
		                ParametroCuerpoEjecucionReporte paramCuerpoEjecucion = new ParametroCuerpoEjecucionReporte();
		
		                // Se implementa codigo que obtiene el codigo del tipo de cobro, que es el reporte personalizado de factura electronica
		
		                Map<String, Object> param2 = new TreeMap<>();
		
		                param2.put(GeneralParameterEnum.ANO.getName(),
		                                ano);
		                param2.put("CODIGO", tipoCobro); //mod JM CC 3250 
		                param2.put("COMPANIA", compania); //mod JM CC 3250 
		
		                Registro rs = RegistroConverter
		                        .toRegistro(requestManager.get(
		                                        UrlServiceUtil.getInstance()
		                                                        .getUrlServiceByUrlByEnumID(
		                                                                        FrmDocDianControladorUrlEnum.URL2735
		                                                                                        .getValue())
		                                                        .getUrl(),
		                                        param2));
		                String codigoReporte ="";
				        if (rs != null)
				        {
				
				            codigoReporte = SysmanFunciones
				                            .nvl(rs.getCampos().get("FORMATO"),
				                                            "30002195")
				                            .toString();
				
				        }
				        else
				        {
				            codigoReporte = "30002195";
				        }
		                // fin obtencion codigo configurado en tipo de cobro
		                paramEjecucion.setCodigoReporte(codigoReporte);
		
		                paramEjecucion.setCompania(compania);
		
		                paramEjecucion.setEntidad("General");
		
		                paramEjecucion.setFormatoReporte("pdf");
		
		                paramEjecucion.setIdioma("es");
		
		                url = ejbSysmanUtil.consultarParametro(compania,
		                		URL_SERVICIO_REST, "69", new Date(), false);
		
		                url = url + "formato/generarReporte?numFactura="
		                    + numeroFactura
		                    + "&numContribuyente=" + nitCompania + "&tipoFormato="
		                    + tipoFormato + "&prefijo=" + prefijo;
		
		                paramEjecucion.setUrl(url);
		
		                paramEjecucion.setUsaAdaptador(false);
		
		                paramCuerpoEjecucion.setNumFactura(numeroFactura);
		
		                paramCuerpoEjecucion.setNumContribuyente(nitCompania);
		
		                paramCuerpoEjecucion.setTipoFormato(tipoFormato);
		
		                paramCuerpoEjecucion.setPrefijo(prefijo);
		                
		                paramCuerpoEjecucion.setTextoPieDeFactura(textoPieDeFactura);
		                
		                paramCuerpoEjecucion.setActividadesEconomicas(actividadesEconomicas);
		
		                // codigo barra
		                boolean permiteCodigoBarra = false;
		
		                StringBuilder pieCodigoBarra = new StringBuilder();
		                StringBuilder pieTextoBarra = new StringBuilder();
		
		                permiteCodigoBarra = ("SI").equals(
		                                SysmanFunciones.nvlStr(ejbSysmanUtil
		                                                .consultarParametro(
		                                                                compania,
		                                                                "SF PERMITE GENERAR CODIGO BARRA FACTURA ELECTRONICA",
		                                                                SessionUtil.getModulo(),
		                                                                new Date(),
		                                                                false),
		                                                "NO"));
		
		                if (permiteCodigoBarra)
		                {
		
		                    String codigoEan;
		
		                    Map<String, Object> paramEan = new TreeMap<>();
		                    paramEan.put(GeneralParameterEnum.COMPANIA
		                                    .getName(),
		                                    compania);
		                    paramEan.put("ANO", ano);
		                    paramEan.put(GeneralParameterEnum.CODIGO
		                                    .getName(),
		                                    tipoCobro);
		                    Registro rsEan = RegistroConverter
		                                    .toRegistro(requestManager.get(
		                                                    UrlServiceUtil.getInstance()
		                                                                    .getUrlServiceByUrlByEnumID(
		                                                                                    FacturacionconceptosControladorUrlEnum.URL1717
		                                                                                                    .getValue())
		                                                                    .getUrl(),
		                                                    paramEan));
		
		                    codigoEan = rsEan.getCampos().get("CODIGOEAN")
		                                    .toString();
		
		                    pieCodigoBarra.append((char) 205);
		                    pieCodigoBarra.append((char) 102);
		                    pieCodigoBarra.append(415);
		                    pieCodigoBarra.append(codigoEan);
		                    pieCodigoBarra.append("8020");
		                    pieCodigoBarra.append(SysmanFunciones.padl(
		                                    tipoFactRecaudo,
		                                    5, "0"));
		                    pieCodigoBarra.append(SysmanFunciones.padl(
		                                    numeroFactura,
		                                    19, "0"));
		                    pieCodigoBarra.append((char) 102);
		                    pieCodigoBarra.append("3900");
		                    pieCodigoBarra.append(SysmanFunciones.padl(
		                                    registro.getCampos().get("VALOR_TOTAL")
		                                                    .toString(),
		                                    14, "0"));
		                    pieCodigoBarra.append((char) 102);
		                    pieCodigoBarra.append(96);
		                    pieCodigoBarra.append(SysmanFunciones.convertirAFechaCadena(
		                                    fechaVencimiento, "yyyyMMdd"));
		
		                    String codigoBarra = ejbCodigoBarras
		                                    .imprimirCodigoBarras(
		                                                    pieCodigoBarra.toString());
		
		                    paramCuerpoEjecucion.setCodigoBarras(codigoBarra);
		
		                    pieTextoBarra.append((char) 40);
		                    pieTextoBarra.append(415);
		                    pieTextoBarra.append((char) 41);
		                    pieTextoBarra.append(codigoEan);
		                    pieTextoBarra.append((char) 40);
		                    pieTextoBarra.append("8020");
		                    pieTextoBarra.append((char) 41);
		                    pieTextoBarra.append(SysmanFunciones.padl(
		                                    tipoFactRecaudo,
		                                    5, "0"));
		                    pieTextoBarra.append(SysmanFunciones.padl(numeroFactura,
		                                    19, "0"));
		                    pieTextoBarra.append((char) 40);
		                    pieTextoBarra.append("3900");
		                    pieTextoBarra.append((char) 41);
		                    pieTextoBarra.append(SysmanFunciones.padl(
		                                    registro.getCampos().get("VALOR_TOTAL")
		                                                    .toString(),
		                                    14, "0"));
		                    pieTextoBarra.append((char) 40);
		                    pieTextoBarra.append(96);
		                    pieTextoBarra.append((char) 41);
		                    pieTextoBarra.append(SysmanFunciones.convertirAFechaCadena(
		                                    fechaVencimiento, "yyyyMMdd"));
		
		                    paramCuerpoEjecucion.setTextoBarras(
		                                    pieTextoBarra.toString());
		                }
		
		                // se consulta el parametro original y copia 
		                String validaOrignalYCopia = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, MANEJA_REPORTE_FACT_ELECTRO_ORIGINAL_Y_COPIA, "69", new Date(), false),"NO");;
		                if(validaOrignalYCopia.equals(GeneralParameterEnum.SI.getName())) {
		                	// lista de reporte
		                	List<byte[]> listadoReportesOriginalYcopia =  new ArrayList<>();
		                	// genera el original
		                	paramCuerpoEjecucion.setOriginalycopia("ORIGINAL");
		                	
		                	paramEjecucion.setParamReporte(paramCuerpoEjecucion);
		                	
		                	APIFrida apiFrida = new APIFrida();
		
		                    Gson gson2 = new Gson();
		                    String json = gson2.toJson(paramEjecucion,
		                                    ParametroEjecucionApiReporte.class);
		
		                    String respuestaReporte = apiFrida
		                                    .postGestionApiReporte(json);
		
		                    RespuestaApi respuestaApis = gson2.fromJson(
		                                    respuestaReporte,
		                                    RespuestaApi.class);
		                    byte[] reporte = null;
		                    if (respuestaApis.getCodigo() == 0)
		                    {
		                    	reporte  = Base64.getDecoder().decode(
		                                        respuestaApis.getCuerpo().toString());
		                    	listadoReportesOriginalYcopia.add(reporte);
		                    }
		                    
		                    // genera copia
		                    respuestaReporte = null;
		                	paramCuerpoEjecucion.setOriginalycopia("COPIA");
		                	
		                	paramEjecucion.setParamReporte(paramCuerpoEjecucion);
		                	
		                	json = gson2.toJson(paramEjecucion,
		                                    ParametroEjecucionApiReporte.class);
		
		                    respuestaReporte = apiFrida
		                                    .postGestionApiReporte(json);
		
		                    respuestaApis = gson2.fromJson(
		                                    respuestaReporte,
		                                    RespuestaApi.class);
		                    reporte = null;
		                    if (respuestaApis.getCodigo() == 0)
		                    {
		                    	reporte  = Base64.getDecoder().decode(
		                                        respuestaApis.getCuerpo().toString());
		                    	listadoReportesOriginalYcopia.add(reporte);
		                    }
		                    
		                    // genera copia
		                    respuestaReporte = null;
		                	paramCuerpoEjecucion.setOriginalycopia("COPIA");
		                	
		                	paramEjecucion.setParamReporte(paramCuerpoEjecucion);
		                	
		                	json = gson2.toJson(paramEjecucion,
		                                    ParametroEjecucionApiReporte.class);
		
		                    respuestaReporte = apiFrida
		                                    .postGestionApiReporte(json);
		
		                    respuestaApis = gson2.fromJson(
		                                    respuestaReporte,
		                                    RespuestaApi.class);
		                    reporte = null;
		                    if (respuestaApis.getCodigo() == 0)
		                    {
		                    	reporte  = Base64.getDecoder().decode(
		                                        respuestaApis.getCuerpo().toString());
		                    	listadoReportesOriginalYcopia.add(reporte);
		                    }
		                    // se procesa y se genera un solo pdf en byte[]
		                    
		                    InputStream reporteStram = new ByteArrayInputStream(
		                    		mergePdfBytes(listadoReportesOriginalYcopia));
		                    archivoDescarga = new DefaultStreamedContent(reporteStram,
		                            ConstanteArchivo.PDF.getContentType(),
		                            "Factura" + numeroFactura
		                                + ConstanteArchivo.PDF.getExtension());
		
		                }else {     	
		                
			                paramEjecucion.setParamReporte(paramCuerpoEjecucion);
			
			                APIFrida apiFrida = new APIFrida();
			
			                Gson gson2 = new Gson();
			                String json = gson2.toJson(paramEjecucion,
			                                ParametroEjecucionApiReporte.class);
			
			                String respuestaReporte = apiFrida
			                                .postGestionApiReporte(json);
			
			                RespuestaApi respuestaApis = gson2.fromJson(
			                                respuestaReporte,
			                                RespuestaApi.class);
			
			                if (respuestaApis.getCodigo() == 0)
			                {
			
			                    byte[] reporte = Base64.getDecoder().decode(
			                                    respuestaApis.getCuerpo().toString());
			                    InputStream reporteStram = new ByteArrayInputStream(
			                                    reporte);
			                    archivoDescarga = new DefaultStreamedContent(reporteStram,
			                                    ConstanteArchivo.PDF.getContentType(),
			                                    "Factura" + numeroFactura
			                                        + ConstanteArchivo.PDF.getExtension());
			
			                }
		                }
		            }
		
		        }
		        catch (SystemException | ParseException | IOException
		                        | com.sysman.util.SysmanException
		                        | RuntimeException e)
		        {
		
		            logger.error(e.getMessage(), e);
		            JsfUtil.agregarMensajeError(e.getMessage());
		        }
			}else {
				try {
					String numeroFactura = SysmanFunciones.toString(registro.getCampos().get("NRO_FACTURA"));
					String tipoFactura = SysmanFunciones.toString(registro.getCampos().get("TIPOCOBRO"));
					String prefijo = "";
					
					    Map<String, Object> parametros = new HashMap<>();
					    Map<String, Object> reemplazar = new HashMap<>();

					    reemplazar.put("compania", compania);
					    reemplazar.put("tipo",     tipoFactura);
					    reemplazar.put("ano",      registro.getCampos().get("ANO"));
					    reemplazar.put("numero",   numeroFactura);

					    Map<String, Object> param = new TreeMap<>();
					    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					    param.put(GeneralParameterEnum.NIT.getName(), SysmanFunciones.toString(registro.getCampos().get("TERCERO")));

					    List<Registro> listaObligaciones = RegistroConverter.toListRegistro(
					        requestManager.getList(
					            UrlServiceUtil.getInstance()
					                .getUrlServiceByUrlByEnumID(FacturacionconceptosControladorUrlEnum.URL2054.getValue())
					                .getUrl(),
					            param));

					    String responsabilidadesFiscales = SysmanFunciones.toString(
					    		SysmanFunciones.nvl(listaObligaciones.get(0).getCampos().get("CODIGO"),""));
//					    		listaObligaciones.stream()
//					        .map(reg -> reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString())
//					        .collect(Collectors.joining(","));
					    
				        Map<String, Object> parametro = new TreeMap<>();
				        /**
				         * @author ldiaz
				         * en este punto se manupula la hora de la fecha solicitud del registro para que este no la lleve en ceros
				         * pues el systema requiere que este entre 1 y 12 
				         */
				        prefijo = "SETT"+numeroFactura;						


					    String ano     = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
					    String numero  = registro.getCampos().get(GeneralParameterEnum.CODIGO_COBRO.getName()).toString();
					    
					    String cude    = consultarCudeInvoway(numero, ano, prefijo, "FA");

					    if (cude.isEmpty()) {
					        JsfUtil.agregarMensajeError("El Documento no ha sido enviado a la DIAN");
					        return;
					    }
					    
					    String codigoTipoNeg = SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("TIPO_PAGO"),""));
					    String tipoNegociacion = "";
					    
					    switch (codigoTipoNeg) {
						case "1":
							tipoNegociacion = "PAGO REALIZADO DE CONTADO";
							break;
						case "2":
							tipoNegociacion = "PAGO REALIZADO A CREDITO";
							break;
						default:
							tipoNegociacion = codigoTipoNeg;
							break;
						}
					
					    parametros.put("P_TIPO_NEGOCIACION", tipoNegociacion);
					    parametros.put("P_OBSERVACIONES",    registro.getCampos().get(GeneralParameterEnum.OBSERVACIONES.getName()).toString());
					    parametros.put("P_ELABORO",          registro.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString());
					    parametros.put("P_ELABORO_FECHA",    new SimpleDateFormat("M/d/yy h:mm a").format((Date) registro.getCampos().get(GeneralParameterEnum.DATE_CREATED.getName())));
					    parametros.put("P_RES_FISCALES",     responsabilidadesFiscales);
					    parametros.put("P_CUDE",             cude);
					    parametros.put("P_TITULO",           "FACTURA ELECTR�NICA DE VENTA");
					    parametros.put("P_NOMBRE_COMPANIA",  SessionUtil.getCompaniaIngreso().getNombre());
					    parametros.put("P_NIT_COMPANIA",     SessionUtil.getCompaniaIngreso().getNit());
					    parametros.put("TIPONUMERO",         prefijo);

					    Reporteador.resuelveConsulta("002933ReporteDianInvowayFact", Integer.valueOf(modulo), reemplazar, parametros);
					    archivoDescarga = JsfUtil.exportarStreamed("DocEnvioDianFact", parametros, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

					} catch (JRException | IOException | SysmanException | SystemException e) {
						logger.error(e.getMessage(), e);
						JsfUtil.agregarMensajeError(e.getMessage());
					}
				}
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
	}

    
    private String consultarCudeInvoway(String numeroFactura, String ano, String prefijo, String clase) {
	    try {
	        ApiInvoway apiInvoway = new ApiInvoway();

			String nitCompania = SessionUtil.getCompaniaIngreso().getNit();

			String url = ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_SOAP, "69", new Date(), false);
	        
			String user = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	        		USUARIO_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false),"");
	        
	        String pass = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	        		CLAVE_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false),"");

	        if (SysmanFunciones.validarVariableVacio(url)) {
	            JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO SOAP");
	            return "";
	        }

	        List<InfoEstadosFactura> respuesta = apiInvoway.consultarFactura(
	                url,
	                prefijo,
	                ano,
	                clase,
	                nitCompania,
	                pass,
	                user);

	        if (respuesta != null && respuesta.size() > 0) {
	            return SysmanFunciones.nvlStr(respuesta.get(0).getUUID(), "");
	        }

	        return "";

	    } catch (SystemException | IOException | com.sysman.util.SysmanException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	        return "";
	    }
	}

    
    /**
     * metodo que permite tomar los bytes y unirlos en un solo pdf
     * @param pdfByteList
     * @return
     * @throws IOException
     */
    public static byte[] mergePdfBytes(List<byte[]> pdfByteList) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte[] pdfBytes : pdfByteList) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
            merger.addSource(inputStream);
        }

        merger.setDestinationStream(outputStream);
        merger.mergeDocuments(null);

        return outputStream.toByteArray();
    }
    /**
     *
     * Metodo ejecutado al oprimir el boton CmdFacturar en la vista
     *
     *
     */
    public void oprimirCmdFacturar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String url;

        if (!validaciones())
        {
            return;
        }
        if (valorNetoValidacion <= 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensajeProperties));
            return;
        }
        Long codigoCobro = Long
                        .parseLong(retornarString(registro, codigoCobroCons));
        Long nroFactura = Long.parseLong(SysmanFunciones
                        .nvl(registro.getCampos().get(nroFacturaCons), "0")
                        .toString());

        try
        {

            ejbFactGenDos.facturarConceptos(compania, tipoCobro,
                            codigoCobro, nroFactura, Integer.parseInt(ano),
                            SessionUtil.getUser().getCodigo());

            cargarRegistro(registro.getLlave(), accion,
                            registro.getIndice());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
            genInforme(ReportesBean.FORMATOS.PDF);
            registro.getCampos().put(impresoCons, true);
            
//validacion por check MANEJO RESOLUCION DE FACTURACION. 
        //mrosero CC_1323 02/04/2025 - Se agrega una validacion adicional con un parametro para validar el proceso
            
			if (manejaResFacturacion && "SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJO DE ENVIO AUTOMATICO A FRIDA AL FACTURAR", SessionUtil.getModulo(), new Date(), true),
					"NO"))) {

				url = ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_REST, "69", new Date(), false);

				exportarFacturas(url, retornarString(registro, nroFacturaCons), tipoCobro);
			}
            
            agregarRegistroNuevo(false);

        }

        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo de insercion del formulario Subfacturacionconceptosbg
     *
     */
    public void agregarRegistroSubSubfacturacionconceptosbg()
    {
        try
        {
			registroSub.getCampos().put(FacturacionconceptosControladorEnum.TIPOCONTRATOSIGEC.getValue(),
					registro.getCampos().get(FacturacionconceptosControladorEnum.TIPOCONTRATOSIGEC.getValue()));
			registroSub.getCampos().put(FacturacionconceptosControladorEnum.NROCONTRATOSIGEC.getValue(),
					registro.getCampos().get(FacturacionconceptosControladorEnum.NROCONTRATOSIGEC.getValue()));

        	registroSub.getCampos().remove(existenciaCons);
            registroSub.getCampos().remove(
                            FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                            .getValue());

            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(tipoCobroCons,
                            registro.getCampos().get(tipoCobroCons));
            registroSub.getCampos().put(codigoCobroCons,
                            registro.getCampos().get(codigoCobroCons));
            registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.ANO
                                            .getName()));
            registroSub.getCampos().put("FECHA_FIN_COBRO", new Date());
            registroSub.getCampos().put("FECHA_INICIO_COBRO", new Date());
            registroSub.getCampos().remove(nombreConceptoCons);
            
            if(visibleRelacionContrato)
            {
            	registroSub.getCampos().remove(FacturacionconceptosControladorEnum.NOMBRE_CENTRO_UTILIDAD.getValue());
            	double saldoActual = Double.parseDouble(SysmanFunciones
                        .nvl(saldoActualConcepto, "0").toString());
            	
            	double valorNetoConcepto = retornarDouble(registroSub,
                        FacturacionconceptosControladorEnum.VALOR_NETO
                        .getValue()); 
            	if(valorNetoConcepto <= saldoActual)
            	{
            		saldoActual = saldoActual - valorNetoConcepto;
            		ejbFactGenTres.actualizarSaldoActualConcepto(compania,
        			        tipoCobro,
        			        registroSub.getCampos().get(conceptoCons).toString(),
        			        new BigInteger(contrato), 
        			        new BigDecimal(saldoActual));
            		
            		valorActualConcepto = registroSub.getCampos().get(FacturacionconceptosControladorEnum.VALOR_NETO
                            .getValue()).toString();
            		
            		saldoActualConcepto = Double.toString(saldoActual);
            	}
            	else
            	{
            		JsfUtil.agregarMensajeError(
                            "No tiene suficiente saldo para este concepto");
                	return;
            	}
            		
            }
            // se consulta el consecutivo maximo se le suma 1 y se envia como consecutivo
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.ANO.getName(), ano);

            param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCobro);
            
            param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get(codigoCobroCons));
            try {
                Registro registro = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FacturacionconceptosControladorUrlEnum.URL675005
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (registro != null) {
                	int consecutivo = (int) registro.getCampos().get(consConsecitivoDetalleCobro);
                	registroSub.getCampos().put(consConsecitivoDetalleCobro,
                			consecutivo + 1);
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
          
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_COBRO
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaSubfacturacionconceptosbg();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
            generarTotalesSub();
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Subfacturacionconceptosbg
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubfacturacionconceptosbg(RowEditEvent event)
    {

        Registro reg = (Registro) event.getObject();
        try
        {
            int conteo;
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().remove(nombreConceptoCons);
            reg.getCampos().remove(impresoCons);
            reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
            reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
            reg.getCampos().remove(FacturacionconceptosControladorEnum.NOMBRE_CENTRO_UTILIDAD.getValue());
            reg.getCampos().remove(FacturacionconceptosControladorEnum.CONSECUTIVO.getValue());
            
            if(visibleRelacionContrato)
            {
            	//reg.getCampos().remove(FacturacionconceptosControladorEnum.NOMBRE_CENTRO_UTILIDAD.getValue());
            	
            	BigDecimal saldoActual = ejbFactGenTres
                        .obtenerSaldoConcepto(compania,
            			        tipoCobro,
            			        reg.getCampos().get(conceptoCons).toString(),
            			        new BigInteger(contrato));
            	
            	BigDecimal valorAnteriorConcepto  = ejbFactGenTres
                        .obtenerValorNetoAnterior(compania,
                        		Integer.parseInt(ano),
            			        tipoCobro,
            			        reg.getCampos().get(conceptoCons).toString(),
            			        new BigInteger(reg.getCampos().get(codigoCobroCons).toString()));
            	
            	double valorNetoConcepto = retornarDouble(reg,
                        FacturacionconceptosControladorEnum.VALOR_NETO
                        .getValue());
            	
            	double saldoActualizar = saldoActual.doubleValue() + valorAnteriorConcepto.doubleValue();
            	if(valorNetoConcepto <= saldoActualizar)
            	{
            		saldoActualizar = saldoActualizar - valorNetoConcepto; 
            		
            		ejbFactGenTres.actualizarSaldoActualConcepto(compania,
        			        tipoCobro,
        			        reg.getCampos().get(conceptoCons).toString(),
        			        new BigInteger(contrato), 
        			        new BigDecimal(saldoActualizar));
            	}
            	else
            	{
            		JsfUtil.agregarMensajeError(
                            "No tiene suficiente saldo para este concepto");
                	return;
            	}
            		
            }
        	reg.getCampos().put(FacturacionconceptosControladorEnum.TERCERO.getValue(), 
        			registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
        	
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_COBRO
                                                            .getUpdateKey());

            conteo = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(), reg.getCampos(),
                            reg.getLlave());

            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
            generarTotalesSub();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSubfacturacionconceptosbg();
        }
    }

    /**
     * Metodo de eliminacion del formulario Subfacturacionconceptosbg
     *
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubfacturacionconceptosbg(Registro reg)
    {
        try
        {
            int conteo;
            
            if(visibleRelacionContrato)
            {
            	BigDecimal saldoActual = ejbFactGenTres
                        .obtenerSaldoConcepto(compania,
            			        tipoCobro,
            			        reg.getCampos().get(conceptoCons).toString(),
            			        new BigInteger(contrato));
            	
            	double valorConcepto  = retornarDouble(reg,
                        FacturacionconceptosControladorEnum.VALOR_NETO
                        .getValue());
            	
            	double saldoActualizar = saldoActual.doubleValue()+valorConcepto;        
            		
            	ejbFactGenTres.actualizarSaldoActualConcepto(compania,
        			        tipoCobro,
        			        reg.getCampos().get(conceptoCons).toString(),
        			        new BigInteger(contrato), 
        			        new BigDecimal(saldoActualizar));              		
            }

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_COBRO
                                                            .getDeleteKey());
            conteo = requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            cargarListaSubfacturacionconceptosbg();
            generarTotalesSub();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subfacturacionconceptosbg
     *
     */
    public void cancelarEdicionSubfacturacionconceptosbg()
    {
        cargarListaSubfacturacionconceptosbg();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {

        // <CODIGO_DESARROLLADO>
        validarFechaPreliquidacion();
        validacionesParametros();
        validaIndicadores();
        validarTitulosParametro();
        validarResFacturacion();
             
        // </CODIGO_DESARROLLADO>
    }

    private void validarResFacturacion() {
    	Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), ano);

        param.put("TIPOCOBRO", tipoCobro);

        try {
            Registro registro = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL5021
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (registro != null) {

                manejaResFacturacion = (boolean) registro.getCampos()
                                .get("MANEJA_RESFACTURACION");

            }
            else {
	              manejaResFacturacion = false;

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    private void validaIndicadores()
    {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), ano);

        param.put(GeneralParameterEnum.CODIGO.getName(), tipoCobro);

        try
        {
            Registro registro = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL54545
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (registro != null)
            {

                manejaDescripcion = (boolean) registro.getCampos()
                                .get("MANEJADESCRIP_EST");

                manejaTarifaBase = (boolean) registro.getCampos()
                                .get("MANEJATARIFABASE");

                codigoEan = SysmanFunciones
                                .nvl(registro.getCampos().get("CODIGOEAN"), "")
                                .toString();

            }
            else
            {
                manejaDescripcion = false;

                manejaTarifaBase = false;

                codigoEan = "";

            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void validacionesParametros()
    {
        try
        {

            cargarBotonTercero = "SI".equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "SF CREAR TERCERO DESDE LA FACTURACION DE CONCEPTOS",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "NO"));

            boolean baseGravable = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SF OBLIGA A BASE GRAVABLE",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"));
            if (baseGravable)
            {

                obligaBaseGravable = true;
                nobligaBaseGravable = false;
            }
            else
            {

                obligaBaseGravable = false;
                nobligaBaseGravable = true;
            }

            boolean fechaVenmanual = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SF FECHA VENCIMIENTO MANUAL",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"));

            if (fechaVenmanual)
            {
                visibleFechaVencManual = true;
            }
            else
            {
                visibleFechaVencManual = false;
            }
            
         // 7713469 FACTGENERAL(10/08/2022 mrosero)
 			boolean facturarA = "SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
 					"SF FACTURA A TERCERO NO REGISTRADO EN EL SISTEMA", SessionUtil.getModulo(), new Date(), true),
 					"NO"));
 			if (facturarA) {
 				visibleFacturarA = true;
 			} else {
 				visibleFacturarA = false;
 			}
 			// 7713469 FACTGENERAL(10/08/2022 mrosero)

            visibleCambioTipo = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SF CAMBIAR TIPO DE COBRO DESDE FACTURACION",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"));

            visibleImpresionFacturaElectronica = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SF PERMITE CONFIGURAR FACTURA ELECTRONICA",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"));
         			
         // 7711609 mperez - Permite verificar si se debe mostrar el combo cuenta de pago en el formulario de facturaci�n general
         			visibleCuentaPago = "SI".equals(SysmanFunciones
         					.nvl(ejbSysmanUtil.consultarParametro(compania, "SF SELECCIONAR CUENTA DE RECAUDO PARA FACTURACION ELECTRONICA",
         							SessionUtil.getModulo(), new Date(), true), "NO"));
         			
         		// 7723037 mperez - Permite verificar si se deben mostrar los campos de facturacion en dolares			         			
         			visibleFacturaDolares = "SI".equals(SysmanFunciones
         					.nvl(ejbSysmanUtil.consultarParametro(compania, "SF MANEJA FACTURACION EN DOLARES",
         							SessionUtil.getModulo(), new Date(), true), "NO"));
         			
         		// 7727401 mperez - Permite verificar si se deben mostrar los campos de relacionar contrato		         			
         			visibleRelacionContrato = "SI".equals(SysmanFunciones
         					.nvl(ejbSysmanUtil.consultarParametro(compania, "SF MANEJA RELACION CONTRATO",
         							SessionUtil.getModulo(), new Date(), true), "NO"));
         		
         		// 7739058 grojas - Permite determinar si se cargan las tarifas base
         			visibleTarifaBase = "SI".equals(SysmanFunciones
                                                .nvl(ejbSysmanUtil.consultarParametro(compania, "SF MANEJA TARIFAS BASE PARA FACTURAR",
                                                                SessionUtil.getModulo(), new Date(), true), "NO"));
         			
         			
         			visibleTarifaTercero = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
        					"SF MANEJA TARIFA POR TERCERO", SessionUtil.getModulo(), new Date(), true), "NO")) ? true : false;
         			
         		//	7750150 grojas - Parametro que permite visualizar los combos de prefijo y contrato
         			visiblePrefijoContrato = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                            "SF MANEJA PREFIJO Y CONTRATO", SessionUtil.getModulo(), new Date(), true), "NO")) ? true : false;
         			
        			visibleSigec = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
        					"SF MANEJA FACTURACION DE ESTAMPILLA ELECTRONICA", SessionUtil.getModulo(), new Date(), true),
        					"NO")) ? true : false;
         	bloqEmail = "NO".equals(SysmanFunciones.nvl(
         					ejbSysmanUtil.consultarParametro(compania,"PERMITE EDITAR CORREO EN FACTURA",SessionUtil.getModulo(),new Date(),true), 
         				"SI"));
         	//C C 386_ljdiaz
 			String visibleCotizaContratoStr = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                    "ACTIVAR CAMPOS PERSONALIZADOS CONTRATOS", SessionUtil.getModulo(), new Date(), true), "NO");
 			if(visibleCotizaContratoStr.equals("SI")) {
 				visiblePrefijoContrato = true;
 				visibleCotizaContrato = true;
 				tituloGrillaPrincipal = idioma.getString("TT_FR1441_INMUBELES");
 			}else {
 				visiblePrefijoContrato = true;
 				visibleCotizaContrato = false;
 				tituloGrillaPrincipal = idioma.getString("TT_FR1441");
 			}
 			
 			manPagoParcial = "SI".equals(SysmanFunciones.nvlStr(
            	    ejbSysmanUtil.consultarParametro(compania,"SF APLICA PAGOS PARCIALES", 
            	        SessionUtil.getModulo(),new Date(),true),"NO"));
 			
 			/*Parametro que obtiene el texto de pie de pagina que debe ir en la factura electronica CC1763*/
 			textoPieDeFactura = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                    "TEXTO PIE DE FACTURA", SessionUtil.getModulo(), new Date(), true), " ");
 			
 			/*Parametro que indica si se pueden ajustarlos decimales del valor total*/
 			permiteAjusteDecimales = "SI".equals(SysmanFunciones
 					.nvl(ejbSysmanUtil.consultarParametro(compania, "SF PERMITE AJUSTE DE DECIMALES EN FACTURACION GENERAL",
 							SessionUtil.getModulo(), new Date(), true), "NO"));
 			
 			/*Parametro que obtiene la configuracion de las actividades economicas que debe ir en la factura electronica CC2974*/
 			actividadesEconomicas = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                    "ACTIVIDADES ECONOMICAS EN FACTURA ELECTRONICA", SessionUtil.getModulo(), new Date(), true), " ");
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro()
    {
    	
		// <CODIGO_DESARROLLADO>
        precargarRegistro();
        inicializarRegistroNuevo();
        anulada = false;
        facturado = false;
        numFacturaObjCobro = "";

        aplicaFacturaDolares = Boolean.parseBoolean(SysmanFunciones.nvlStr(registro.getCampos()
                .get(FacturacionconceptosControladorEnum.FACTURA_DOLARES
                                .getValue()).toString(), "false"));
        
        contrato = SysmanFunciones.nvl(registro.getCampos().get("CONTRATO"),"").toString();
        
        validaSujetoaReportarContrato();
       //  validacionFechaVencimiento();
         cargarListaConceptoSf178();
         cargarListaConceptoSf178E();
       

        if (nitCompania.contains("-"))
        {
            int fin = nitCompania.indexOf("-");
            nitCompania = nitCompania.substring(0, fin);
        }

        if (accion.equals(ACCION_INSERTAR))
        {
            registro.getCampos().put(cantidadGrupoCons, 1);
        }

        if (css == null)
        {
            generarTotalesSub();

        }
        else
        {

            boolean impreso = Boolean.parseBoolean(SysmanFunciones.nvlStr(registro.getCampos()
            		.get(impresoCons).toString(), "false"));;
            boolean facturaAnulada = Boolean.parseBoolean(SysmanFunciones.nvlStr(registro.getCampos()
                            .get(FacturacionconceptosControladorEnum.ANULADA
                                            .getValue()).toString(), "false"));;
            if (impreso)
            {

                facturado = true;
                if (registro.getCampos().get(nroFacturaCons) != null)
                {
                    numFacturaObjCobro = registro.getCampos().get(nroFacturaCons).toString();
                }
                validarFacturaCobro();
            }

            if (facturaAnulada)
            {
                anulada = true;
            }
            cargarListaAfectarContrato();
            
        }
        cargarListaCONTRATOSTERCERO();
        if(accion.equals("m") && css != null) {
        	fechaVencimientoDcto = (Date) SysmanFunciones.nvl(registro.getCampos().get("FECHAVENCIDSCTO"), new Date());
        	checkConDscto = (boolean) registro.getCampos().get("APLICA_DSCTO").toString().equals("true") || registro.getCampos().get("APLICA_DSCTO").toString().equals("-1")?true:false;
        	valorNeto = registro.getCampos().get(GeneralParameterEnum.VALOR_TOTAL.getName()).toString();
        }
        
        if (accion.equals(ACCION_INSERTAR)) {
	        Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.ANO.getName(), ano);
	        param.put(GeneralParameterEnum.TIPO_COBRO.getName(),  tipoCobro);
	    
	        
	        try {
	  			Registro rsExiste = RegistroConverter.toRegistro(
	  					requestManager.get(UrlServiceUtil.getInstance()
	  							.getUrlServiceByUrlByEnumID(
	  									FacturacionconceptosControladorUrlEnum.URL666021
	  									.getValue())
	  							.getUrl(), param));
	
	  			if (rsExiste.getCampos().get("EXISTE").toString().equals("0")) {
	  				dialogoConsecutivo = true;
	  				
	//  				String consecutivo = ano + "000001";
	//  				registro.getCampos().put("CONSECUTIVO_COBRO", consecutivo);
	  			}
	  			
	  			else {
	  				consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo("SF_OBJETO_COBRO", 
							SysmanFunciones.concatenar("COMPANIA = ''", compania , "'' AND ANO = ''", ano ,"'' AND TIPOCOBRO = ''", tipoCobro ,"''"), 
							GeneralParameterEnum.CODIGO_COBRO.getName());

	  				registro.getCampos().put("CODIGO_COBRO", consecutivo);
	  			}
	  		}
	  		catch (SystemException e) {
	  			JsfUtil.agregarMensajeError(e.getMessage());
	  			logger.error(e.getMessage(), e);
	
	  		}
        }
        //
        // </CODIGO_DESARROLLADO>
    }

    
    public void validaSujetoaReportarContrato() {
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.TERCERO.getName(), registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
        
        try {
			Registro existe = RegistroConverter
			        .toRegistro(
			                        requestManager.get(
			                                        UrlServiceUtil.getInstance()
			                                                        .getUrlServiceByUrlByEnumID(
			                                                        		FacturacionconceptosControladorUrlEnum.URL82127
			                                                                                        .getValue())
			                                                        .getUrl(),
			                                        param));
			if(existe != null) {
				int cantidad = Integer.parseInt(SysmanFunciones.toString(existe.getCampos().get(GeneralParameterEnum.TOTAL.getName())));
				sujetoareportar =cantidad>0?true:false;
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
    	 if(registro.getCampos().get("CODIGO_COBRO") == null) {
    			 asignarValoresRegistro(codigoCobroCons, generarCodigoCobro());
    	 }
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(tipoCobroCons, tipoCobro);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        if(registro.getCampos().get("OBSERVACIONES") != null && registro.getCampos().get("OBSERVACIONES").toString().contains("\"")){
        	String descripcion = registro.getCampos().get("OBSERVACIONES").toString().replaceAll("\"", "");
        
        	descripcion = descripcion.replaceAll("\'", "");
        
        	registro.getCampos().put("OBSERVACIONES", descripcion);
        }
        registro.getCampos().put("REF_FACTURACION", prefijo);

        registro.getCampos().remove(
                        FacturacionconceptosControladorEnum.ANULADA.getValue());
        registro.getCampos().remove(nombreAuxiliarCons);
        registro.getCampos().remove(nombreCentroCostoCons);
        registro.getCampos().remove(nombreFuenteCons);
        
        if(aplicaFacturaDolares && (registro.getCampos().get("TRM_DOLARES_FAC") == null || registro.getCampos().get("TRM_DOLARES_FAC") == "0" )) {
        	JsfUtil.agregarMensajeError(
                    "Debe ingresar el valor de la T.R.M");
        	return false;
        	
        }
        if(visibleRelacionContrato && !visibleFechaVencManual)
        {
        	int diasTipoPago = Integer.parseInt(SysmanFunciones.nvl(registro.getCampos().get("DIAS"), 0).toString());
        	
        	Date fechaTipoPago = new Date();        	
        	SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
        	Calendar calendar = Calendar.getInstance();
        	calendar.setTime(fechaTipoPago); // Configuramos la fecha que se recibe
        	calendar.add(Calendar.DAY_OF_YEAR, diasTipoPago);
        	
        	Date fechaVencimientoTipoPago = calendar.getTime();
        	
        	registro.getCampos().put(
                    FacturacionconceptosControladorEnum.FECHA_VENCIMIENTO
                                    .getValue(),
                                    formatFecha.format(fechaVencimientoTipoPago));  
        }
        if(visibleCotizaContrato) {
        	registro.getCampos().put("APLICA_DSCTO",
                                    checkConDscto);
        	registro.getCampos().put(
                    "FECHAVENCIDSCTO",
                                    fechaVencimientoDcto);
        }
        registro.getCampos().remove("DIAS");
        registro.getCampos().remove("EMAIL");
        //registro.getCampos().remove("TIPOCPTE_PAGO");
        //registro.getCampos().remove("NROCPTE_PAGO");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarDespues()
    {
    	if(visibleRelacionContrato)
        {   	
    		registro.getCampos().put("OBJETO_CONTRATO", objetoContrato);
        }
    	if(visibleCotizaContrato) {
    		registro.getLlave().put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
    		registro.getLlave().put(GeneralParameterEnum.KEY_ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString());
    		registro.getLlave().put("KEY_TIPOCOBRO", registro.getCampos().get("TIPOCOBRO").toString());
    		registro.getLlave().put("KEY_CODIGO_COBRO", registro.getCampos().get("CODIGO_COBRO").toString());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
    		param.put(GeneralParameterEnum.TIPO.getName(),tipoCobro);
    		param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get("TXTCONTRATO"));
    		try {
				List<Registro> detallesContrato = RegistroConverter
				.toListRegistro(
				                requestManager
				                                .getList(
				                                                UrlServiceUtil.getInstance()
				                                                                .getUrlServiceByUrlByEnumID(
				                                                                                FacturacionconceptosControladorUrlEnum.URL682002.getValue())
				                                                                .getUrl(),
				                                                param));
				for(Registro reg: detallesContrato) {
					registroSub = reg;
					agregarRegistroSubSubfacturacionconceptosbg();
				}
			
				//HU004 Cremil descuento aplicado por parametro
	            if(visibleCotizaContrato) {
	            	if(checkConDscto) {            		
    					double parametroPorcDscto = Double.parseDouble(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
    					        "SF DESCUENTO FACTURA", SessionUtil.getModulo(), new Date(), false), 0).toString());
    					Registro regAux = new Registro();
    					regAux.getCampos().put(GeneralParameterEnum.VALOR_TOTAL.getName(),(Double.parseDouble(registro.getCampos().get(GeneralParameterEnum.VALOR_TOTAL.getName()).toString())-(Double.parseDouble(registro.getCampos().get(GeneralParameterEnum.VALOR_TOTAL.getName()).toString())*(parametroPorcDscto/100))));
    					valorNeto = regAux.getCampos().get(GeneralParameterEnum.VALOR_TOTAL.getName()).toString();
    					regAux.getCampos().put("DATE_MODIFIED",new Date());
    					regAux.getCampos().put("MODIFIED_BY", SessionUtil.getUser().getCodigo());
    					regAux.setLlave(registro.getLlave());
    					UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                		FacturacionconceptosControladorUrlEnum.URL666019.getValue());

    					int conteo = requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(), regAux.getCampos(),
                                regAux.getLlave());

		                if (conteo > 0)
		                {
		                    JsfUtil.agregarMensajeInformativo(
		                                    idioma.getString("MSM_REGISTRO_MODIFICADO"));
		                }
            	  	}
	            }
    		} catch (SystemException e) {
				logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
			} 
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
            cargarListaSubfacturacionconceptosbg();
            rid = registro.getLlave();
    	}
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NUMERO_ABONO");
        registro.getCampos().remove("TIPOCPTE_PAGO");
        registro.getCampos().remove("NROCPTE_PAGO");
        registro.getCampos().remove(nombreTerceroCons);
        registro.getCampos().remove(nombreAuxiliarCons);
        registro.getCampos().remove(nombreCentroCostoCons);
        registro.getCampos().remove(objetoContratoCons);
        registro.getCampos().remove(nombreFuenteCons);
        registro.getCampos().remove("DIAS");
        registro.getCampos().remove("EMAIL"); // mperez 7711609
        
        registro.getCampos().remove(
                        FacturacionconceptosControladorEnum.ANULADA.getValue());
        if (SysmanFunciones.ano((Date) registro.getCampos()
                        .get("FECHA_SOLICITUD")) != Integer.parseInt(ano))
        {
            JsfUtil.agregarMensajeError(
                            "la fecha de la Solicitud no corresponde al año de trabajo");
            return false;

        }

        if (accion.equals(ACCION_MODIFICAR))
        {
            String descripcion = registro.getCampos().get("OBSERVACIONES").toString().replaceAll("\"", "");
            descripcion = descripcion.replaceAll("\'", "");
            registro.getCampos().put("OBSERVACIONES", descripcion);
            registro.getCampos().put("REF_FACTURACION", prefijo);
            registro.getCampos().remove("VALOR_TOTAL");

            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove("ANO");
            registro.getCampos().remove(tipoCobroCons);
            registro.getCampos().remove("CODIGO_COBRO");
        }        
        
        if(manejaResFacturacion && (registro.getCampos().get("TIPO_MEDIOPAGO") == null || registro.getCampos().get("TIPO_PAGO") == null )) {
        	JsfUtil.agregarMensajeError(
                    "Campos MEDIO DE PAGO Y TIPO DE PAGO obligatorios por manejar resoluci�n de facturaci�n");      	
        	
        	return false;  
        }
        
        if(aplicaFacturaDolares && (registro.getCampos().get("TRM_DOLARES_FAC") == null || registro.getCampos().get("TRM_DOLARES_FAC") == "0" )) {
        	JsfUtil.agregarMensajeError(
                    "Debe ingresar el valor de la T.R.M");
        	return false;
        	
        }
        
        if(visibleCotizaContrato && accion.equals("m")) {
        	registro.getCampos().remove("APLICA_DSCTO");
            registro.getCampos().remove("FECHAVENCIDSCTO");
            if(registro.getCampos().get("INDPAGO") != null)
            	registro.getCampos().put("INDPAGO", registro.getCampos().get("INDPAGO").toString().equals("true")?-1:0);
            if(registro.getCampos().get("IMPRESO") != null)
            	registro.getCampos().put("IMPRESO", registro.getCampos().get("IMPRESO").toString().equals("true")?-1:0);
        }
        // </CODIGO_DESARROLLADO>
        return validarcierrepresupuestal();
    }

    public boolean validarcierrepresupuestal()
    {
        Registro reg = null;
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put("ANO", ano);

        try
        {
            reg = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL15069
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));

            if (reg != null)
            {
                if ("0".equals(reg.getCampos().get("CANTIDAD").toString()))
                {
                    return true;
                }
                else
                {
                    JsfUtil.agregarMensajeError(
                                    "Ya existe Cierre contable para el a�o"
                                        + " " + ano);
                    return false;
                }
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues()
    {
    	if(visibleRelacionContrato)
        {    	   		
    		registro.getCampos().put("OBJETO_CONTRATO", objetoContrato);
        }
    	actulizarTercero();
        return true;
        
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     *
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private String retornarString(Registro reg, String campo)
    {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private double retornarDouble(Registro reg, String campo)
    {
        return Double.parseDouble(SysmanFunciones
                        .nvl(reg.getCampos().get(campo), "0").toString());
    }

    private Long generarCodigoCobro()
    {
        String criterio = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "'' AND TIPOCOBRO = ''", tipoCobro,
                        "'' AND SUBSTR(CODIGO_COBRO,1,4) = ", ano);
        Long respuesta = null;
        int anoConsecutivo = SysmanFunciones.ano(new Date());
        try
        {
            respuesta = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "SF_OBJETO_COBRO", criterio, codigoCobroCons,
                            SysmanFunciones.concatenar(
                                            String.valueOf(anoConsecutivo),
                                            "000001"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return respuesta;
    }

    private void asignarValoresContrato()
    {

        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAuxiliarContrato.getCampos()
                                        .get(GeneralParameterEnum.TERCERO
                                                        .getName()));
        registro.getCampos().put(nitTerceroFacturadoCons,
                        registroAuxiliarContrato.getCampos()
                                        .get(GeneralParameterEnum.TERCERO
                                                        .getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAuxiliarContrato.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        registro.getCampos().put("SUCURSAL_TERCEROFACTURADO",
                        registroAuxiliarContrato.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAuxiliarContrato.getCampos()
                                        .get(GeneralParameterEnum.AUXILIAR
                                                        .getName()));
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAuxiliarContrato.getCampos()
                                        .get(GeneralParameterEnum.CENTRO_COSTO
                                                        .getName()));

        registro.getCampos().put(direccionCons, registroAuxiliarContrato
                        .getCampos().get(direccionCons));
        registro.getCampos().put(telefonosCons, registroAuxiliarContrato
                        .getCampos().get(telefonosCons));
        registro.getCampos().put(codigoPostalCons, registroAuxiliarContrato
                        .getCampos().get(codigoPostalCons));
        registro.getCampos().put(nombreTerceroCons, registroAuxiliarContrato
                        .getCampos().get("NOMBRE_TERCERO"));
        registro.getCampos().put(nombreCentroCostoCons,
                        registroAuxiliarContrato.getCampos()
                                        .get(nombreCentroCostoCons));
        registro.getCampos().put(nombreAuxiliarCons, registroAuxiliarContrato
                        .getCampos().get(nombreAuxiliarCons));

        registro.getCampos().put(objetoContratoCons, registroAuxiliarContrato
                        .getCampos().get(objetoContratoCons));

        registroAuxiliarContrato = null;

    }

    private void validarTerceroVarios(Registro reg)
    {
        if (SysmanConstantes.CONS_TERCERO.equals(retornarString(reg, "NIT")))
        {
            bloqueaTercVarios = false;
        }
        else
        {
            bloqueaTercVarios = true;
        }

    }

    private void inicializarRegistroNuevo()
    {
        if (css == null)
        {
            if (Integer.parseInt(ano) < SysmanFunciones.ano(new Date()))
            {
                try
                {
                    registro.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.FECHA_SOLICITUD
                                                    .getValue(),
                                    SysmanFunciones.convertirAFecha(
                                                    "31/12/" + ano));
                }
                catch (ParseException e)
                {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
            else
            {
                registro.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.FECHA_SOLICITUD
                                                .getValue(),
                                new Date());
            }
            asignarValoresRegistro(nitTerceroFacturadoCons,
                            SysmanConstantes.CONS_TERCERO);
            asignarValoresRegistro("NOMBRE_TERCEROFACTURADO", "NINGUNO NADA");
            asignarValoresRegistro(nombreTerceroCons, "NINGUNO NADA");
            asignarValoresRegistro(nombreFuenteCons, "VARIOS");
            asignarValoresRegistro(GeneralParameterEnum.TERCERO.getName(),
                            SysmanConstantes.CONS_TERCERO);
            asignarValoresRegistro(GeneralParameterEnum.CENTRO_COSTO.getName(),
                            SysmanConstantes.CONS_CENTRO);
            asignarValoresRegistro(nombreCentroCostoCons, "NINGUNO");
            asignarValoresRegistro(GeneralParameterEnum.AUXILIAR.getName(),
                            SysmanConstantes.CONS_AUXILIAR);
            asignarValoresRegistro(
                            GeneralParameterEnum.FUENTE_RECURSO.getName(),
                            SysmanConstantes.CONS_FUENTE);
            asignarValoresRegistro(nombreAuxiliarCons, "NINGUNO");
            asignarValoresRegistro("NRO_ABONO", 0);
            asignarValoresRegistro("TASA_INTERES", 0);
            asignarValoresRegistro("VLR_EFECTIVO", 0);
            asignarValoresRegistro("VLR_CREDITO", 0);
            asignarValoresRegistro("CUOTAS_DIFERIDAS", 0);

            asignarValoresRegistro(cantidadGrupoCons, 1); // Se inicializa en 1 MPEREZ - 7723037
            asignarValoresRegistro("VLR_PROM_CUOTA", 0);
            asignarValoresRegistro("TOTAL_GRUPO", 0);
            //aqui agregamos el valor de FACTURA_DOLARES
            asignarValoresRegistro("FACTURA_DOLARES", false);

        }
        if(visibleTarifaTercero)
        {
        	validacionNumeroLista();
        }
    }

    private void asignarValoresRegistro(String campo, Object valor)
    {
        registro.getCampos().put(campo, valor);
    }

    /**
     * Como En acces le cambian la consulta al combo tarifa cuando se
     * selecciona el concepto, Se hace este metodo en el cual usa la
     * variable tarifaConcepRela para identificar cual consulta
     * deberia cargar el combo. Esta variable luego se valida en el
     * metodo cargarListaTarifa
     */
    private void prepararTarifa(Object concepto)
    {

        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CONCEPTO.getName(), concepto);

            Registro reg = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL22800
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (reg == null)
            {
                tarifaConcepRela = false;
            }
            else
            {
                tarifaConcepRela = true;
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void prepararEstrato(Object concepto)
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CONCEPTO.getName(), concepto);

            Registro reg = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL22801
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (reg == null)
            {
                estratoConcepRela = false;
            }
            else
            {
                estratoConcepRela = true;
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void bloqueaComponentes(Registro reg)
    {
        if (aplicaFacturaDolares)
        {
        	bloqueaValorUnitario = true;
        }
        else 
        {
	    	if ((boolean) reg.getCampos().get("PERMITEMODIFICARVALOR"))
	        {
	            bloqueaValorUnitario = false;
	        }
	        else
	        {
	            bloqueaValorUnitario = true;
	        }
        }

        if ((boolean) reg.getCampos().get("PERMITEMODIFICARCANTIDAD"))
        {
            bloqueaCantidad = false;
        }
        else
        {
            bloqueaCantidad = true;
        }
        
        if ((boolean) reg.getCampos().get("PERMITE_AJUSTE_DECIMALES"))
        {
            bloqueaAjusteDecimal = false;
        }
        else
        {
        	bloqueaAjusteDecimal = true;
        }
    }

    private void validarValorUnitarioValorBase(Registro reg, int digRedondeo,
        double cantidad)
    {

        try
        {
            if (cambioValorUnitario || visibleRelacionContrato)
            {
                reg.getCampos().put(
                                FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                .getValue(),
                                registroSub.getCampos().get(
                                                FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                                .getValue()));
            }

            double valorBaseConcepto = retornarDouble(reg,
                            FacturacionconceptosControladorEnum.VALOR_BASE
                                            .getValue());
            double valorBase = 0;
            double valorUnitario;
            if (digRedondeo == 0)
            {
                registroSub.getCampos().put(
                                FacturacionconceptosControladorEnum.VALOR_BASE
                                                .getValue(),
                                valorBase);
            }
            
            // 7707082 (mrosero) 11/02/2022
          else if(digRedondeo == 2)
          {
        	  valorBase = (retornarDouble(reg,
        			  					  FacturacionconceptosControladorEnum.VALOR_UNITARIO
        			  					  				   .getValue())
        			  	* cantidad);

        	  registroSub.getCampos().put(
                      FacturacionconceptosControladorEnum.VALOR_BASE
                                      .getValue(),
                      valorBase);
          }
          //7707082 (mrosero) 11/02/2022
                        
            else
            {

                valorBase = retornarConFix(
                                ((retornarDouble(reg,
                                                FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                                .getValue())
                                    * cantidad)
                                    / digRedondeo) + 0.501,
                                digRedondeo);

                registroSub.getCampos().put(
                                FacturacionconceptosControladorEnum.VALOR_BASE
                                                .getValue(),
                                valorBase);

            }

            if (Double.doubleToRawLongBits(valorBase) == 0)
            {
                if (digRedondeo == 0)
                {

                    registroSub.getCampos().put(
                                    FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                    .getValue(),
                                    0);
                    registroSub.getCampos().put(
                                    FacturacionconceptosControladorEnum.VALOR_BASE
                                                    .getValue(),
                                    0);
                }
                
               // 7707082 (mrosero) 11/02/2022
                else if(digRedondeo == 2)
                {
                	  valorUnitario = valorBaseConcepto;

              registroSub.getCampos().put(
                              FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                              .getValue(),
                              valorUnitario);

              valorBase = (valorBaseConcepto * cantidad);
              
              registroSub.getCampos().put(
                              FacturacionconceptosControladorEnum.VALOR_BASE
                                              .getValue(),
                              valorBase);
                }
               // 7707082 (mrosero) 11/02/2022
                
                else
                {
                	                    
                    if(visibleTarifaTercero) {
                    	
                    	Map<String, Object> param = new TreeMap<>();
                        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                        param.put(GeneralParameterEnum.CONCEPTO.getName(), codigoConcepto);
                        param.put(GeneralParameterEnum.NUMEROLISTA.getName(), ObtenerNumeroLista("codnumeroLista"));
                        
                    	 Registro regValor = RegistroConverter
                                 .toRegistro(requestManager.get(
                                                 UrlServiceUtil.getInstance()
                                                                 .getUrlServiceByUrlByEnumID(
                                                                                 FacturacionconceptosControladorUrlEnum.URL1921001
                                                                                                 .getValue())
                                                                 .getUrl(),
                                                 param));
                    	if(regValor != null) {
                    		registroSub.getCampos().put(
                    	FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                    .getValue(),
                                    regValor.getCampos().get(GeneralParameterEnum.VALOR.getName()));
                    		valorBaseConcepto = Double.parseDouble(regValor.getCampos().get(GeneralParameterEnum.VALOR.getName()).toString());
                    	}
                    			
                    }else {
                    		
                    valorUnitario = retornarConFix(
                                    (valorBaseConcepto / digRedondeo) + 0.501,
                                    digRedondeo);

                    registroSub.getCampos().put(
                                    FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                    .getValue(),
                                    valorUnitario);
                    }
                    
                    valorBase = retornarConFix(((valorBaseConcepto * cantidad)
                        / digRedondeo) + 0.501, digRedondeo);
                    registroSub.getCampos().put(
                                    FacturacionconceptosControladorEnum.VALOR_BASE
                                                    .getValue(),
                                    valorBase);

                }

            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarDatosDesdeConcepto(Registro reg)
    {

        try
        {

            if (!(boolean) reg.getCampos().get("AFECTAINVENTARIO"))
            {

                registroSub.getCampos().put(existenciaCons, 999);
            }
            else
            {

                Long existencia = ejbFactGenCero.insertarElementoConcepto(
                                compania, Integer.parseInt(ano), tipoCobro,
                                retornarString(reg,
                                                GeneralParameterEnum.CODIGO
                                                                .getName()),
                                retornarDouble(reg, " PORCETAJE_UTILIDAD"),
                                SessionUtil.getUser().getCodigo());

                if (existencia == -1)
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3816"));
                    registroSub.getCampos().put(existenciaCons, 0);
                }
                else
                {
                    registroSub.getCampos().put(existenciaCons, existencia);
                }
            }
            
            int digRedondeo = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_BASE").toString());

            double cantidad = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroSub.getCampos().get(
                                                            cantidadCons), "0")
                                            .toString());
            if (digRedondeo == 0)
            {
                asignarValoresRegistro(valorCompraCons, 0);
            }
            else
            {

                double valorCompraAux = ((retornarConFix(
                                (retornarDouble(reg, valorCompraCons)
                                    / digRedondeo) + 0.501,
                                digRedondeo)
                    * retornarDouble(registroSub, cantidadCons)) / digRedondeo)
                    + 0.501;

                double valorCompra = retornarConFix(valorCompraAux,
                                digRedondeo);

                registroSub.getCampos().put(valorCompraCons, valorCompra);

            }
            if(!aplicaFacturaDolares)
            {
	            if ((boolean) reg.getCampos().get("PERMITEMODIFICARVALOR"))
	            {
	                if (Double.doubleToRawLongBits(retornarDouble(registroSub,
	                                FacturacionconceptosControladorEnum.VALOR_UNIDAD
	                                                .getValue())) == 0)
	                {
	
	                    validarValorUnitarioValorBase(reg, digRedondeo, cantidad);
	                }
	                else
	                {
	
	                    if(visibleTarifaTercero)
                            {
                                double valorUnitario = retornarDouble(registroSub,
                                                    FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                                    .getValue());
                
                                    registroSub.getCampos().put(
                                                    FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                                    .getValue(),
                                                    valorUnitario);
                                    
                                    double valorBase = SysmanFunciones.redondear(
                                            (valorUnitario * cantidad),
                                            digRedondeo);
                
                                    registroSub.getCampos().put(
                                                    FacturacionconceptosControladorEnum.VALOR_BASE
                                                                    .getValue(),
                                                    valorBase);
                            }
                            
                            else {
                                    double valorUnitario = retornarDouble(registroSub,
                                                    FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                                                    .getValue());
                
                                    registroSub.getCampos().put(
                                                    FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                                    .getValue(),
                                                    valorUnitario);
                                    
                                    double valorBase = retornarConFix(
                                                    ((valorUnitario * cantidad) / digRedondeo)
                                                        + 0.501,
                                                    digRedondeo);
                
                                    registroSub.getCampos().put(
                                                    FacturacionconceptosControladorEnum.VALOR_BASE
                                                                    .getValue(),
                                                    valorBase);
                            }              
	                    
	
	                }
	
	            }
	
	            else if (Double.doubleToRawLongBits(
	                            retornarDouble(registroSub,
	                                            FacturacionconceptosControladorEnum.VALOR_UNIDAD
	                                                            .getValue())) == 0
	                && !Boolean.parseBoolean(reg.getCampos()
	                                .get(FacturacionconceptosControladorEnum.APLICAFORMULA
	                                                .getValue())
	                                .toString()))
	            {
	                double valorUnitarioAux = retornarConFix(
	                                (retornarDouble(reg,
	                                                FacturacionconceptosControladorEnum.VALOR_BASE
	                                                                .getValue())
	                                    / digRedondeo)
	                                    + 0.501,
	                                digRedondeo);
	
	                registroSub.getCampos().put(
	                                FacturacionconceptosControladorEnum.VALOR_UNITARIO
	                                                .getValue(),
	                                valorUnitarioAux);
	
	                double valorBaseAux = retornarConFix(
	                                ((retornarDouble(reg,
	                                                FacturacionconceptosControladorEnum.VALOR_BASE
	                                                                .getValue())
	                                    * cantidad)
	                                    / digRedondeo)
	                                    + 0.501,
	                                digRedondeo);
	
	                registroSub.getCampos().put(
	                                FacturacionconceptosControladorEnum.VALOR_BASE
	                                                .getValue(),
	                                valorBaseAux);
	
	            }
	            else
	            {
	
	                if (!Boolean.parseBoolean(
	                                reg.getCampos().get(
	                                                FacturacionconceptosControladorEnum.APLICAFORMULA
	                                                                .getValue())
	                                                .toString())
	                    || cambioEstrato || cambioTarifa)
	                {
	
	                    registroSub.getCampos().put(
	                                    FacturacionconceptosControladorEnum.VALOR_UNITARIO
	                                                    .getValue(),
	                                    registroSub.getCampos().get(
	                                                    FacturacionconceptosControladorEnum.VALOR_UNIDAD
	                                                                    .getValue()));
	
	                }
	                double valorBaseAux = retornarConFix(
	                                ((retornarDouble(registroSub,
	                                                FacturacionconceptosControladorEnum.VALOR_UNITARIO
	                                                                .getValue())
	                                    * cantidad) / digRedondeo) + 0.501,
	                                digRedondeo);
	
	                registroSub.getCampos().put(
	                                FacturacionconceptosControladorEnum.VALOR_BASE
	                                                .getValue(),
	                                valorBaseAux);
	            }
            }
            registroSub.getCampos().put("CUENTA_BANCO",
                            reg.getCampos().get("CUENTA_RECAUDO"));

            hallarValorBaseAplicaFormula(reg);
            hallarValorDescuento(reg);
            hallarValorIva(reg);
            hallarValorReteIva(reg);
            hallarValorReteFuente(reg);
            hallarValorIca(reg);
            hallarValorImpoconsumo(reg, cantidad);
            hallarValorNetoUnitarioUnidad(reg);
            asignarValorCreeSub(reg);
            cambioValorUnitario = false;
        }
        catch (NumberFormatException |

                        SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorImpoconsumo(Registro reg, double cantidad)
    {
        if ((boolean) reg.getCampos().get("APLICAIMPOCONSUMO"))
        {

            registroSub.getCampos().put(
                            FacturacionconceptosControladorEnum.VALOR_IMPOCONSUMO
                                            .getValue(),
                            (retornarDouble(reg,
                                            FacturacionconceptosControladorEnum.VALOR_IMPOCONSUMO
                                                            .getValue())
                                * cantidad));

        }
        else
        {
            registroSub.getCampos().put(
                            FacturacionconceptosControladorEnum.VALOR_IMPOCONSUMO
                                            .getValue(),
                            "0");

        }

    }

    /**
     * La variable global aplicatarifa se invoca al cambiar el combo
     * tarifa, por lo que este metodo se ejecuta al cambiar el
     * concepto y al cambiar tarifa ya que tiene que ser recalculado
     * sabiendo si es con tarifa o no .
     *
     * @param reg
     */

    private void hallarValorDescuento(Registro reg)
    {
        try
        {
            BigDecimal valorDescuento;

            double porcentajeDescuento = Double.parseDouble(reg.getCampos()
                            .get("PORCENTAJEDESCUENTO").toString());

            boolean aplicaDescuento = (boolean) reg.getCampos()
                            .get(aplicaDescuentoCons);

            double valorBase = retornarDouble(registroSub,
                            FacturacionconceptosControladorEnum.VALOR_BASE
                                            .getValue());

            valorBase = valorBase * (porcentajeDescuento / 100);
            int redDesc = Integer.parseInt(reg.getCampos()
                            .get("FACTOR_RED_DESCUENTO").toString());

        
            	
                if ((Double.doubleToRawLongBits(valorBase) == 0)
                        || (Double.doubleToRawLongBits(redDesc) == 0))
                    {

                        registroSub.getCampos().put(valorDescuentoCons, 0);
                        registroSub.getCampos().put(valorReteIva, 0);
                    }
                



                    if (aplicaDescuento)
                    {

                        valorDescuento = ejbFactGenUno.cargarValorConceptoIndica(
                                        aplicaDescuento,
                                        new BigDecimal(Double.toString(valorBase)),
                                        redDesc);

                        registroSub.getCampos().put(valorDescuentoCons, valorDescuento);
                    }

                    else
                    {
                        registroSub.getCampos().put(valorDescuentoCons, 0);
                    }
           
            
                    if(visibleRelacionContrato && dctoContrato > 0) {//JM CC 2693
                    	registroSub.getCampos().put(valorDescuentoCons, dctoContrato);
                    }

            aplicaTarifa = false;
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * La variable global aplicatarifa se invoca al cambiar el combo
     * tarifa, por lo que este metodo se ejecuta al cambiar el
     * concepto y al cambiar tarifa ya que tiene que ser recalculado
     * sabiendo si es con tarifa o no .
     *
     * @param reg
     */

    private void hallarValorReteIva(Registro reg)
    {
     
     //MOD JM 18/11/2024  realizar el calculo solo si aplica 
   	 boolean aplicaReteIva = (boolean) reg.getCampos().get("APLICARETEIVA");
   	 BigDecimal valorReteIva; 
   	 
   	 if(aplicaReteIva) {	 
        try
        {
            //BigDecimal valorReteIva;

            double porcentajeReteIva = Double.parseDouble(SysmanFunciones.nvlStr(reg.getCampos()
            		.get("PORCENTAJERETEIVA").toString(), "0"));

            /*boolean aplicaReteIva = (boolean) reg.getCampos()
                            .get("APLICARETEIVA");*/

            double valorBase = retornarDouble(registroSub,
            		valorIvaCons);

            valorBase = valorBase * (porcentajeReteIva / 100);
           
            

            if ( (Double.doubleToRawLongBits(valorBase) == 0) )
            {

                registroSub.getCampos().put("VALOR_RETEIVA", 0);
            }
            //if (aplicaReteIva) //COMENTADO POR JM 18/11/2024  se cambia de posicion la condicion del if / else
            //{
            	int redDesc = Integer.parseInt(reg.getCampos()
                         .get("FACTOR_RED_DESCUENTO").toString());
            	
            	valorReteIva = ejbFactGenUno.cargarValorConceptoIndica(
                        aplicaReteIva,
                        new BigDecimal(Double.toString(valorBase)),
                        redDesc);

                registroSub.getCampos().put("VALOR_RETEIVA", valorReteIva);
            /*} 

            else
            {
                registroSub.getCampos().put("VALOR_RETEIVA", 0);
            } */
            
	        }
	        catch (SystemException e)
	        {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
	   	}else {
	   		registroSub.getCampos().put("VALOR_RETEIVA", 0);
	    }
    }
    
    private void hallarValorBaseAplicaFormula(Registro reg)
    {
        try
        {

            double valorBaseFijo;
            double cantidad = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroSub.getCampos().get(
                                                            cantidadCons), "1")
                                            .toString());

            if (cambioBaseFija)
            {

                valorBaseFijo = auxiliarBaseFija;

            }
            else
            {

                valorBaseFijo = Double.parseDouble(SysmanFunciones
                                .nvl(registroSub.getCampos().get(
                                                FacturacionconceptosControladorEnum.BASE_FIJA
                                                                .getValue()),
                                                "0")
                                .toString());
            }

            String formula = reg.getCampos().get(formulaCons).toString()
                            .replace("VlrBaseF()",
                                            Double.toString(valorBaseFijo));

            formula = formula.replace("Tarifa()",
                            Double.toString(vlrTarifa <= 0.0 ? 1 : vlrTarifa));

            formula = formula.replace("SalarioDiario()", ejbFactGenDos
                            .retornarValorAnual(compania, Integer.parseInt(ano),
                                            "SALARIOMINIMO", true)
                            .toString());

            formula = formula.replace("UVT()",
                            ejbFactGenDos.retornarValorAnual(compania,
                                            Integer.parseInt(ano), "VALORUVT",
                                            false).toString());

            int digRedondeo = Integer.parseInt(reg.getCampos()
                            .get("FACTOR_RED_BASETOTAL").toString());
            if (((boolean) reg.getCampos().get(
                            FacturacionconceptosControladorEnum.APLICAFORMULA
                                            .getValue())
                && cambioConcepto) || cambioBaseFija)
            {

                BigDecimal valorBaseUnitario = ejbFactGenTres
                                .reemplazarFormula(formula);

                valorBaseUnitario = ejbFactGenUno
                                .cargarValorConceptoCant(Double.parseDouble(
                                                valorBaseUnitario.toString()),
                                                1, digRedondeo);

                double valorBase = valorBaseUnitario.doubleValue() * cantidad;

                registroSub.getCampos().put("VALOR_BASE", valorBase);
                registroSub.getCampos().put("VALOR_UNITARIO",
                                valorBaseUnitario);

            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorIva(Registro reg)
    {
        try
        {
            boolean aplicaIva = (boolean) reg.getCampos().get(aplicaIvaCons);
            int digIva = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_IVA").toString());
            double valorBase = retornarDouble(registroSub,
                            FacturacionconceptosControladorEnum.VALOR_BASE
                                            .getValue());
            double valorDescuento = retornarDouble(reg, valorDescuentoCons) + SysmanFunciones.nvlDbl(dctoContrato,0) ; //JM CC2693
            double porcentajeIva = retornarDouble(reg, "PORCENTAJEIVA");

            if (!aplicaIva || (Double.doubleToRawLongBits(digIva) == 0))
            {
                registroSub.getCampos().put(valorIvaCons, 0);
            }
            else
            {
                BigDecimal valorIva;

                valorBase = (valorBase - valorDescuento)
                    * (porcentajeIva / 100);

                valorIva = ejbFactGenUno.cargarValorConceptoIndica(aplicaIva,
                                new BigDecimal(Double.toString(valorBase)),
                                digIva);

                registroSub.getCampos().put(valorIvaCons, valorIva);
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorReteFuente(Registro reg)
    {

        try
        {
            int redRete = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_RETE").toString());
            double valorBase = retornarDouble(registroSub,
                            FacturacionconceptosControladorEnum.VALOR_BASE
                                            .getValue());
            double porcentajeRetefuente = retornarDouble(reg,
                            "PORCENTAJERETEFUENTE");
            BigDecimal valorRetefuente;

            boolean aplicaRetefuente = (boolean) reg.getCampos()
                            .get(aplicaReteFuenteCons);
            if (aplicaRetefuente)
            {

                valorBase = valorBase * (porcentajeRetefuente / 100);

                valorRetefuente = ejbFactGenUno.cargarValorConceptoIndica(
                                aplicaRetefuente,
                                new BigDecimal(Double.toString(valorBase)),
                                redRete);

                registroSub.getCampos().put(valorRetefuenteCons,
                                valorRetefuente);
            }
            else
            {
                registroSub.getCampos().put(valorRetefuenteCons, 0);
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorIca(Registro reg)
    {

        try
        {
            boolean aplicaIca = (boolean) reg.getCampos().get(aplicaIcaCons);
            int redIca = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_ICA").toString());
            double valorBase = retornarDouble(registroSub,
                            FacturacionconceptosControladorEnum.VALOR_BASE
                                            .getValue());
            double porcentajeIca = retornarDouble(reg, "PORCENTAJEICA");
            BigDecimal valorIca;

            if (aplicaIca)
            {

                valorBase = valorBase * (porcentajeIca / 100);

                valorIca = ejbFactGenUno.cargarValorConceptoIndica(aplicaIca,
                                new BigDecimal(Double.toString(valorBase)),
                                redIca);

                registroSub.getCampos().put(valoricaCons, valorIca);
            }
            else
            {
                registroSub.getCampos().put(valoricaCons, "0");
            }

        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorNetoUnitarioUnidad(Registro reg)
    {

        double valorBase = retornarDouble(registroSub,
                        FacturacionconceptosControladorEnum.VALOR_BASE
                                        .getValue());
        double valorIva = retornarDouble(registroSub, valorIvaCons);
        double valorRetefuente = retornarDouble(registroSub,
                        valorRetefuenteCons);
        double valorDescuento = retornarDouble(registroSub, valorDescuentoCons);
        double valorIca = retornarDouble(registroSub, valoricaCons);
        double valorCompra = retornarDouble(registroSub, valorCompraCons);
        double valorImpoconsumoAux = retornarDouble(registroSub,
                        FacturacionconceptosControladorEnum.VALOR_IMPOCONSUMO
                                        .getValue());
        double valorReteiva = retornarDouble(registroSub, FacturacionconceptosControladorEnum.VALOR_RETEIVA
                .getValue());
        double valorNetoAux = ((valorBase + valorIva + valorRetefuente
            + valorImpoconsumoAux) - valorDescuento)
            + valorIca + valorReteiva;
        
        if (valorNetoAux < 0)
        {
            registroSub.getCampos()
                            .put(FacturacionconceptosControladorEnum.VALOR_NETO
                                            .getValue(), 0);
        }
        else
        {
            registroSub.getCampos()
                            .put(FacturacionconceptosControladorEnum.VALOR_NETO
                                            .getValue(), valorNetoAux);
        }

        double valorUtilidad = valorBase - valorCompra;
        registroSub.getCampos().put("VALOR_UTILIDAD", valorUtilidad);

        registroSub.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        retornarString(reg, GeneralParameterEnum.REFERENCIA
                                        .getName()));

        registroSub.getCampos().put(
                        GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        retornarString(reg, GeneralParameterEnum.FUENTE_RECURSO
                                        .getName()));

        registroSub.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        retornarString(reg, GeneralParameterEnum.CENTRO_COSTO
                                        .getName()));
        registroSub.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        retornarString(reg, GeneralParameterEnum.AUXILIAR
                                        .getName()));

    }

    private void generarTotalesSub()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("CODIGOCOBRO", registro.getCampos().get(codigoCobroCons));
            param.put(tipoCobroCons, tipoCobro);
            param.put(GeneralParameterEnum.ANO.getName(), ano);

            Registro regTotales = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL22803
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            valorUnidadAux = retornarDouble(regTotales,
                            FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                            .getValue());

            valorUnidad = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales,
                                            FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                            .getValue()));

            valor = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "VALOR"));
            iva = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "IVA"));
            reteFuente = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "RETEFUENTE"));
            descuento = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "DESCUENTO"));
            ica = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "ICA"));
            valorNeto = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales, "VALORNETO"));
            
            valorDolares = new java.text.DecimalFormat(formatoMonedaCons)
                    .format(retornarDouble(regTotales, "VALOR_TOTAL_DOLARES")); //mperez

            valorNetoValidacion = retornarDouble(regTotales, "VALORNETO");

            valorImpoconsumo = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales,
                                            "VALORIMPOCONSUMO"));

            deudaAnterior = new java.text.DecimalFormat(formatoMonedaCons)
                            .format(retornarDouble(regTotales,
                                            "DEUDA_ANTERIOR"));

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private String claseContable()
    {
        Registro regClaseContable = null;
        String claseContable = "";

        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(tipoCobroCons, tipoCobro);
            regClaseContable = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL22804
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            claseContable = retornarString(regClaseContable, "CLASE_CONTABLE");
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return claseContable;
    }

	private boolean validacionFechaVencimiento() {
		boolean rta = true;
		String tercero = registro.getCampos().get("TERCERO").toString();
		Date fechaSolicitud = (Date) registro.getCampos()
				.get(FacturacionconceptosControladorEnum.FECHA_SOLICITUD.getValue());
		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

		try {
			if (visibleTarifaTercero) {
				Date fechaVencimiento = ejbFactGenDos.calcularVencimiento(compania, tercero,
						formatFecha.format(fechaSolicitud));
				registro.getCampos().put(FacturacionconceptosControladorEnum.FECHA_VENCIMIENTO.getValue(),
						fechaVencimiento);
				agregarRegistroNuevo(false);
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			rta = false;
		}

		return rta;

	}

	public void validacionNumeroLista() {

		String tercero = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), "")
				.toString();
		if (tercero != "999999999999999999") {

			try {

				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.TERCERO.getName(),
						SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), ""));
				Registro regs = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												FacturacionconceptosControladorUrlEnum.URL002.getValue())
										.getUrl(),
								param));

				String numLista = SysmanFunciones
						.nvl(regs.getCampos().get(GeneralParameterEnum.NUMEROLISTA.getName()), "").toString();

				if (numLista.equals("")) {
					JsfUtil.agregarMensajeInformativoDialogo("El tercero no tiene numero de lista relacionado,  ");
				}

			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}
	}

	

    private boolean validaciones()
    {
        boolean rta = true;
        try
        {
            if (!visibleFechaVencManual && !visibleRelacionContrato)
            {                
            	Date fechaVencimiento = ejbFactGenDos.calcularFechaVencimiento(
                                compania, tipoCobro, indPreliquidacion,
                                retornarString(registro, codigoCobroCons));
                registro.getCampos().put(
                                FacturacionconceptosControladorEnum.FECHA_VENCIMIENTO
                                                .getValue(),
                                fechaVencimiento);               
                agregarRegistroNuevo(false);
            }

            if (valorUnidadAux <= 0)
            {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(mensajeProperties));
                rta = false;
            }

            String claseContable = claseContable();
            if (SysmanFunciones.validarVariableVacio(claseContable))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3849"));
                rta = false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            rta = false;
        }
        return rta;
    }

    public void validarFacturaCobro()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("NUMEROCOBRO", registro.getCampos().get(codigoCobroCons));
            param.put(tipoCobroCons, tipoCobro);
            param.put(GeneralParameterEnum.ANO.getName(), ano);

            Registro regNroFactura = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL22807
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            tipoFactura = retornarString(regNroFactura,
                            FacturacionconceptosControladorEnum.TIPO_FACTURA
                                            .getValue());

            numFactura = retornarString(regNroFactura,
                            FacturacionconceptosControladorEnum.NUMERO_FACTURA
                                            .getValue());

            if (!numFacturaObjCobro.equals(numFactura))
            {
                registro.getCampos().put("TIPOFACTURA", tipoCobro);
                registro.getCampos().put("NRO_FACTURA", numFactura);
                agregarRegistroNuevo(false);
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void genInforme(ReportesBean.FORMATOS formato)
    {
        try
        {
            String informe = obtenerFormato();
            String factura = retornarString(registro, nroFacturaCons);
            Map<String, Object> reemplazar = new HashMap<>();

            if (ejbSysmanUtil.consultarParametro(compania,
                            "SF CODIGO EAN POR CADA TIPO DE COBRO",
                            SessionUtil.getModulo(), new Date(), false)
                            .equals("NO"))
            {

                codigoEan = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "SF CODIGO EAN",
                                                SessionUtil.getModulo(),
                                                new Date(), false), "")
                                .toString();
            }
            else
            {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put("ANO", ano);
                param.put(GeneralParameterEnum.CODIGO.getName(), tipoCobro);
                Registro rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FacturacionconceptosControladorUrlEnum.URL1717
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
                codigoEan = rs.getCampos().get("CODIGOEAN").toString();
            }            
            
            reemplazar.put("codigoEan", codigoEan);
            reemplazar.put("anio", ano);
            reemplazar.put("tipoFactura", tipoCobro);
            reemplazar.put("facturaInicial", factura);
            reemplazar.put("facturaFinal", factura);
            reemplazar.put("compania", compania);
            // PARAMETROS PARA GENERACION DE INFORME
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            
            //inicio desarrollo parametros mrosero
            parametros.put("PR_DIRECCIONCOMPANIA",
                    SessionUtil.getCompaniaIngreso().getDireccion());
            parametros.put("PR_TELEFONOCOMPANIA",
                    SessionUtil.getCompaniaIngreso().getTelefono());
            parametros.put("PR_CIUDADCOMPANIA",
                    SessionUtil.getCompaniaIngreso().getCiudad());
            //fin desarrollo mrosero
            
            
            parametros.put("PR_USUARIO", SessionUtil.getUser().getCodigo());
            
            parametros.put("PR_CUENTABANCO1",
                            obtenerParametro("SF BANCO CUENTA 1", ""));
            parametros.put("PR_CUENTABANCO2",
                            obtenerParametro("SF BANCO CUENTA 2", ""));
            parametros.put("PR_CUENTABANCO3",
                            obtenerParametro("SF BANCO CUENTA 3", ""));
            parametros.put("PR_CUENTABANCO4",
                            obtenerParametro("SF BANCO CUENTA 4", ""));
            parametros.put("PR_SF_CPTO_PPAL_PRODESARROLLO",
                            obtenerParametro(
                                            "SF CONCEPTO PRINCIPAL PRODESARROLLO",
                                            ""));
            parametros.put("PR_SF_MANEJA_CODIGO_BARRAS",
                            "SI".equalsIgnoreCase(obtenerParametro(
                                            "SF MANEJA CODIGO DE BARRAS",
                                            "SI")));
            parametros.put("PR_ENCABEZADO",ejbSysmanUtil.consultarParametro(compania,"ENCABEZADO FORMATO FACTURA",modulo, new Date(), false));
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            // IMPLEMETACION FACTURA EN DOLARES - TICKET7723037 - MPEREZ
            parametros.put("PR_FACTURADOLARES", aplicaFacturaDolares);
            
			//INFORME FACTURA IDCBIS
			parametros.put("PR_COPIA", true);
			parametros.put("PR_ORIGINAL", true);
			
			if(visibleCotizaContrato) {
				//PARAMETROS PARA REPORTE PERSONALIZADO CON VALORES DE CANTIDAD AGRUPADO Y VALORES UNITARIOS
				String cantidadesConcatenadas = "";
				String valUnitaroConcatenadas = "";
				double baseGravable = 0.0;
				NumberFormat formatoImporte = NumberFormat.getCurrencyInstance();
		    	//Si se desea forzar el formato español:
		    	formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es","CO"));
				for(Registro regAux: listaSubfacturacionconceptosbg) {
					if(Double.parseDouble(regAux.getCampos().get("VALOR_IVA").toString()) > 0 ){
						baseGravable = baseGravable + Double.parseDouble(regAux.getCampos().get("VALOR_BASE").toString());
					}
					if(cantidadesConcatenadas.equals("") && valUnitaroConcatenadas.equals("")){
						cantidadesConcatenadas = regAux.getCampos().get("CANTIDAD").toString();
						valUnitaroConcatenadas = formatoImporte.format(regAux.getCampos().get("VALOR_UNITARIO"));
					}else {
						cantidadesConcatenadas = cantidadesConcatenadas + "; " +regAux.getCampos().get("CANTIDAD").toString();
						valUnitaroConcatenadas = valUnitaroConcatenadas + "; " +formatoImporte.format(regAux.getCampos().get("VALOR_UNITARIO"));
					}
				}
				
				parametros.put("PR_VALORBASE", baseGravable);
				parametros.put("PR_CANTIDADESCONCAT", cantidadesConcatenadas);
				parametros.put("PR_VALUNITARIOCONCAT", valUnitaroConcatenadas);
				
				Map<String, Object> params = new TreeMap<>();
		        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		        params.put(GeneralParameterEnum.TERCERO.getName(), registro.getCampos().get("TERCERO"));
		        params.put("FECHAFILTRO", new SimpleDateFormat("dd/MM/YYYY").format(new Date()).toString());
		        
		        Registro rs = RegistroConverter
		                            .toRegistro(requestManager.get(
		                                            UrlServiceUtil.getInstance()
		                                                            .getUrlServiceByUrlByEnumID(
		                                                                            FacturacionconceptosControladorUrlEnum.URL39124
		                                                                                            .getValue())
		                                                            .getUrl(),
		                                            params));

		       if (rs != null){
		    	    parametros.put("PR_DEUDA_ANT_TERCERO", formatoImporte.format(rs.getCampos().get("TOTAL_VENCIDO"))); 	
		        }else {
		        	parametros.put("PR_DEUDA_ANT_TERCERO", "$ 0.0");
		        }
			}
			
            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SystemException | JRException | IOException
                        | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String obtenerParametro(String nombreParametro,
        String valorDefault)
    {
        String parametro = null;
        try
        {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(),
                            true);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Permite obtener el nombre del formato con el que se desea
     * generar la factura
     *
     * @return nombre del formato a generar
     */
    private String obtenerFormato()
    {
        String formato = "";
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        params.put(FacturarLotesControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL22805
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            if ((rs != null)
                && !SysmanFunciones.validarCampoVacio(rs.getCampos(),
                                "FORMATO_FACTURA"))
            {
                formato = rs.getCampos().get("FORMATO_FACTURA").toString();
            }
            else
            {
                formato = obtenerParametro("SF FORMATO FACTURACION",
                                "001493INFFACSTD010");
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return formato;
    }

    private boolean existeDetalles()
    {
        boolean rta;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(codigoCobroCons, retornarString(registro, codigoCobroCons));
        param.put(tipoCobroCons, tipoCobro);
        Registro reg = null;
        try
        {
            reg = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL19054
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (reg != null)
        {
            rta = true;
        }
        else
        {
            rta = false;
        }

        return rta;

    }

    /**
     * Evalua si el parametro "SF MANEJA CALCULO CREE" esta en si,
     * calcula el valor cree de acuerdo a la configuracion realizada
     * en el concepto y lo actualiza en la Base de Datos
     */
    private void asignarValorCreeSub(Registro reg)
    {
        try
        {
            boolean manejaCree = "SI".equalsIgnoreCase(
                            obtenerParametro("SF MANEJA CALCULO CREE", "NO"));

            if (manejaCree)
            {
                Map<String, Object> params = new TreeMap<>();
                params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                params.put(GeneralParameterEnum.ANO.getName(), ano);
                params.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO
                                .getValue(), tipoCobro);
                params.put(GeneralParameterEnum.CONCEPTO.getName(),
                                reg.getCampos().get(GeneralParameterEnum.CODIGO
                                                .getName()));

                Registro rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ConceptosNoFacturadosControladorUrlEnum.URL005
                                                                                                .getValue())
                                                                .getUrl(),
                                                params));
                if (rs != null)
                {
                    registroSub.getCampos().put("VALOR_CREE",
                                    rs.getCampos().get("CREE"));
                }

            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void obtenerRegistroConceptos(Object concepto)
    {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(), ano);
        params.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        params.put(GeneralParameterEnum.CODIGO.getName(), concepto);

        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL36973
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            cargarDatosDesdeConcepto(rs);

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private double retornarConFix(double operacion, int digRedondeo)
                    throws SystemException
    {

        BigDecimal valorBigD = BigDecimal.valueOf(operacion);
        valorBigD = ejbSysmanUtil.fix(valorBigD);

        return valorBigD.doubleValue() * digRedondeo;

    }

    private void agregarValoresRegistroSub(Registro reg, String nombreCampo)
    {
        reg.getCampos().put(nombreCampo,
                        registroSub.getCampos().get(nombreCampo));
    }

    private boolean validarCantidadGrupo()
    {

        boolean rta = true;
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cantidadGrupoCons))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3892"));
            rta = false;
        }
        else
        {
            int cantidad = Integer.parseInt(registro.getCampos()
                            .get(cantidadGrupoCons).toString());
            if (cantidad == 0)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3892"));
                rta = false;
            }
        }
        return rta;
    }

    private void validarFechaPreliquidacion()
    {
        try
        {
            boolean visibleFecha = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SF APLICA FECHA PRELIQUIDACION",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"));

            if (visibleFecha)
            {
                visibleFechaPreliqui = true;
            }
            else
            {
                visibleFechaPreliqui = false;
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private Registro retornarRegistroEstrato(Object concepto)
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(tipoCobroCons, tipoCobro);
        param.put(GeneralParameterEnum.CODIGO.getName(), concepto);
        Registro reg = null;
        try
        {
            reg = listaEstratoSf178E.getRegistroUnico(param);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return reg;
    }

    private Registro retornarRegistroTarifa(Object concepto)
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(tipoCobroCons, tipoCobro);
        param.put(GeneralParameterEnum.CODIGO.getName(), concepto);
        Registro reg = null;

        if (concepto != null)
        {

            try
            {
                reg = listaTarifaSf178E.getRegistroUnico(param);
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            return reg;
        }
        else
        {
            return reg;
        }
    }

    private void logicaSeleccionarEstrato(Registro registroAux,
        Object concepto)
    {
        if (SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        GeneralParameterEnum.CODIGO.getName()))
        {
            registroSub.getCampos().put(
                            FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                            .getValue(),
                            "0");
            vlrEstrato = 1;
        }

        obtenerRegistroConceptos(concepto);

        vlrEstrato = retornarDouble(registroAux, tarifaCons);
        boolean tomarVlr = (boolean) SysmanFunciones
                        .nvl(registroAux.getCampos().get(tomatVlrCons), false);
        double vlrUnidad;
        if (tomarVlr)
        {
            vlrUnidad = retornarDouble(registroSub,
                            FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                            .getValue())
                * vlrEstrato * vlrTarifa;

        }
        else
        {
            vlrEstrato = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroAux.getCampos().get(
                                                            tarifaCons), "100")
                                            .toString())
                / 100;
            vlrUnidad = retornarDouble(registroSub,
                            FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                            .getValue())
                * vlrEstrato * vlrTarifa;

        }

        registroSub.getCampos()
                        .put(FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                        .getValue(), vlrUnidad);
        cambioEstrato = true;
        obtenerRegistroConceptos(concepto);

    }

    private Registro retornarRegistroConcepto(Object concepto)
    {
        Map<String, Object> parametros = new TreeMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);
        parametros.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO
                        .getValue(), tipoCobro);
        parametros.put(GeneralParameterEnum.CODIGO.getName(), concepto);

        Registro reg = null;
        try
        {
            reg = new Registro(listaConceptoSf178.getRegistroUnico(parametros)
                            .getCampos());
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return reg;
    }

    private void logicaSeleccionarTarifa(Registro reg, Object concepto)
    {

        Registro registroConcepto = retornarRegistroConcepto(concepto);
        
        if(visibleTarifaBase) {
            
            vlrTarifa = retornarDouble(reg,
                            GeneralParameterEnum.PORCENTAJE.getName());
            
            registroSub.getCampos().put(
                            FacturacionconceptosControladorEnum.VALOR_UNIDAD.getValue(),vlrTarifa);
            
            registroSub.getCampos().put(FacturacionconceptosControladorEnum.BASE_FIJA
                            .getValue(), vlrTarifa);
            
        }
        
        else {

            if (SysmanFunciones.validarCampoVacio(reg.getCampos(),
                            GeneralParameterEnum.CODIGO.getName()))
            {
                aplicaTarifa = false;
                vlrTarifa = 1;
                registroSub.getCampos().put(
                                FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                                .getValue(),
                                retornarDouble(registroSub,
                                                FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                                .getValue())
                                    * vlrTarifa * vlrEstrato);
            }
            else
            {
    
                obtenerRegistroConceptos(concepto);
    
                if ((boolean) reg.getCampos().get(tomatVlrCons))
                {
                    vlrTarifa = retornarDouble(reg,
                                    GeneralParameterEnum.PORCENTAJE.getName());
                    if ((boolean) registroConcepto.getCampos()
                                    .get(aplicaDescuentoCons))
                    {
                        registroSub.getCampos().put(
                                        FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                                        .getValue(),
                                        registroSub.getCampos().get(
                                                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                                        .getValue()));
                    }
                    else
                    {
                        double vlrTarifaAux = retornarDouble(registroSub,
                                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                        .getValue())
                            * vlrTarifa * vlrEstrato;
                        registroSub.getCampos().put(
                                        FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                                        .getValue(),
                                        vlrTarifaAux);
                    }
                }
                else
                {
                    vlrTarifa = SysmanFunciones.nvlDbl(reg.getCampos().get(
                                    GeneralParameterEnum.PORCENTAJE.getName()), 100)
                        / 100;
                    if ((boolean) registroConcepto.getCampos()
                                    .get(aplicaDescuentoCons))
                    {
                        registroSub.getCampos().put(
                                        FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                                        .getValue(),
                                        registroSub.getCampos().get(
                                                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                                        .getValue()));
                    }
                    else
                    {
                        registroSub.getCampos().put(
                                        FacturacionconceptosControladorEnum.VALOR_UNIDAD
                                                        .getValue(),
                                        retornarDouble(registroSub,
                                                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                                        .getValue())
                                            * vlrTarifa * vlrEstrato);
                    }
                }
            }
        
        }
        cambioTarifa = true;
        obtenerRegistroConceptos(concepto);

    }

    public void activarEdicionSubfacturacionconceptosbg(Registro r)
    {
        indiceSubfacturacionconceptosbg = listaSubfacturacionconceptosbg
                        .indexOf(r);
        Registro registroAlEdit = new Registro(new HashMap<>(r.getCampos()));
        conceptoGrillaAux = retornarString(registroAlEdit, conceptoCons);

        prepararEstrato(conceptoGrillaAux);
        cargarListaEstratoSf178E();

        prepararTarifa(conceptoGrillaAux);
        cargarListaTarifaSf178E();

        registroSub.getCampos().put(conceptoCons,
                        registroAlEdit.getCampos().get(conceptoCons));
        registroSub.getCampos().put(cantidadCons,
                        registroAlEdit.getCampos().get(cantidadCons));

        registroSub.getCampos().put(
                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                        .getValue(),
                        registroAlEdit.getCampos().get(
                                        FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                                        .getValue()));

    }

    private void logicaCambiarValorBase(Object concepto)
    {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(), ano);
        params.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        params.put(GeneralParameterEnum.CODIGO.getName(), concepto);

        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL36973
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            double valorIva = calcularValoresImpuest(rs, aplicaIvaCons,
                            "PORCENTAJEIVA");
            registroSub.getCampos().put(valorIvaCons, valorIva);

            double valorRete = calcularValoresImpuest(rs, aplicaReteFuenteCons,
                            "PORCENTAJERETEFUENTE");
            registroSub.getCampos().put(valorRetefuenteCons, valorRete);

            double valorDescuen = calcularValoresImpuest(rs,
                            aplicaDescuentoCons, "PORCENTAJEDESCUENTO");
            registroSub.getCampos().put(valorDescuentoCons, valorDescuen);

            double valorIca = calcularValoresImpuest(rs, aplicaIcaCons,
                            "PORCENTAJEICA");
            registroSub.getCampos().put(valoricaCons, valorIca);

            double valorBase = retornarDouble(registroSub,
                            FacturacionconceptosControladorEnum.VALOR_BASE
                                            .getValue());
            double valorNet = valorBase + valorIva + valorRete + valorDescuen
                + valorIca;
            if (valorNet < 0)
            {
                valorNet = 0;
            }
            
            double valorUnitario = valorBase
                / retornarDouble(registroSub, cantidadCons);

            registroSub.getCampos()
                            .put(FacturacionconceptosControladorEnum.VALOR_BASE
                                            .getValue(), valorBase);
            registroSub.getCampos().put(
                            FacturacionconceptosControladorEnum.VALOR_UNITARIO
                                            .getValue(),
                            valorUnitario);
            registroSub.getCampos()
                            .put(FacturacionconceptosControladorEnum.VALOR_NETO
                                            .getValue(), valorNet);

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private double calcularValoresImpuest(Registro rs, String indicador,
        String campo)
    {
        double valorRetornar;
        int digRedondeo = 0;

        boolean aplica = (boolean) SysmanFunciones
                        .nvl(rs.getCampos().get(indicador), false);
        double porcentaje = retornarDouble(rs, campo);

        try
        {
            digRedondeo = Integer.parseInt(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "REDONDEO VALOR",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "2")
                            .toString());
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (aplica)
        {
            valorRetornar = SysmanFunciones.redondear(
                            retornarDouble(registroSub,
                                            FacturacionconceptosControladorEnum.VALOR_BASE
                                                            .getValue())
                                * (porcentaje / 100),
                            digRedondeo);
               }
        else
        {
            valorRetornar = 0;
        }
        return valorRetornar;
    }

    private void validarTitulosParametro()
    {
        try
        {
            boolean parametro = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SF OCULTAR CAMPOS MODULO DE FACTURACION",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"));

            if (parametro)
            {

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(tipoCobroCons, tipoCobro);
                param.put(GeneralParameterEnum.ANO.getName(), ano);

                Registro reg = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FacturacionconceptosControladorUrlEnum.URL35638
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if ((reg != null)
                    && !SysmanFunciones.validarCampoVacio(reg.getCampos(),
                                    "LIQUIDACION")
                    && (boolean) SysmanFunciones.nvl(
                                    reg.getCampos().get("LIQUIDACION"),
                                    false))
                {
                    tituloCantidad = idioma.getString("TC_CB4320");
                    tituloTarifa = idioma.getString("TG_FR1414_8");
                    tituloEstrato = idioma.getString("TG_BASE");
                    tituloValor = idioma.getString("TG_SUB_TOTAL");
                }
                else
                {
                    tituloCantidad = idioma.getString("TG_CANTIDAD2");
                    tituloTarifa = idioma.getString("TG_TARIFA");
                    tituloEstrato = idioma.getString("TC_CB147");
                    tituloValor = idioma.getString("TG_VALOR6");
                }

            }
            else
            {
                tituloCantidad = idioma.getString("TG_CANTIDAD2");
                tituloTarifa = idioma.getString("TG_TARIFA");
                tituloEstrato = idioma.getString("TC_CB147");
                tituloValor = idioma.getString("TG_VALOR6");
            }
            
            tituloTarifaBase = idioma.getString("NG_CB4740_2");
            
            if (visibleTarifaBase) {
                
                tituloTarifaBase = idioma.getString("TG_VALOR");
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnEnviarSIGEC en la vista
     *
     */
	public String oprimirBtnEnviarSIGEC() {
		// <CODIGO_DESARROLLADO>
		/*
		 * RUTINA PARA A. SERVICIO REPORTE DE ACTO/DOCUMENTO
		 */
		String url;
		String token = null;
		String log = null;
		archivoDescarga = null;

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST GATEWAY SIGEC", "1", new Date(), false);
			token = ejbSysmanUtil.consultarParametro(compania, "TOKEN AUTORIZACION SIGEC", "1", new Date(), false);
		
			log = "|---------------         LOG DE LOGICA SERVICIO ACTO DOCUMENTO / SIGEC        ---------------|";

			log = log + "\n" + servicioActo_Documento(token, url);
			
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "Log.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return log;
		// </CODIGO_DESARROLLADO>
	}

	private String servicioActo_Documento(String token, String url) {
		String respuesta = "";
		String json = null;

		Map<String, Object> params = new TreeMap<>();
		SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), registro.getCampos().get("TERCERO"));
		params.put(GeneralParameterEnum.CLASEORDEN.getName(), registro.getCampos().get("TIPOCONTRATOSIGEC"));//claseF);//(String) parametrosEntrada.get(PARAMETRO_CLASEF);
		params.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get("NROCONTRATOSIGEC"));
		try {
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FacturacionconceptosControladorUrlEnum.URL1928003.getValue())
											.getUrl(),
									params));

			ParametrosSIGEC param = new ParametrosSIGEC();
			String fechaInicio = formatFecha.format(rs.getCampos().get(FacturacionconceptosControladorEnum.FECHAINICIO.getValue()));
			String fechaFin = formatFecha.format(rs.getCampos().get(FacturacionconceptosControladorEnum.FECHAFINALIZACION.getValue()));
			BigInteger valorBigInteger = (BigInteger) rs.getCampos().get(FacturacionconceptosControladorEnum.PLATAFORMA.getValue());
			BigInteger valorBigInteger1 = (BigInteger) rs.getCampos().get(FacturacionconceptosControladorEnum.VALORTOTAL.getValue());
			
			Integer plataforma = valorBigInteger.intValue();
			Integer valorTotal = valorBigInteger1.intValue();			
			
			param.setPlatform(plataforma);
			param.setActDocumentCode(SysmanFunciones.nvl(rs.getCampos().get(FacturacionconceptosControladorEnum.EQUIV_SIGEC.getValue()), "").toString());
			param.setGeneratorFactValue(valorTotal);
			param.setPayerDocumentParametricTypeCode(SysmanFunciones.nvl(rs.getCampos().get(FacturacionconceptosControladorEnum.SIGEC.getValue()), "").toString());
			param.setTaxpayerDocumentNumber(SysmanFunciones.nvl(rs.getCampos().get(FacturacionconceptosControladorEnum.TERCERO.getValue()), "").toString());
			param.setTaxpayerName(SysmanFunciones.nvl(rs.getCampos().get(FacturacionconceptosControladorEnum.NOMBRE.getValue()), "").toString());
			param.setGeneratorFactStartDate(fechaInicio);
			param.setGeneratorFactEndDate(fechaFin);
			param.setParametricActDocumentCodeType(SysmanFunciones.nvl(rs.getCampos().get(FacturacionconceptosControladorEnum.TIPO_SIGEC.getValue()), "").toString());

			Gson gson = new Gson();
			json = gson.toJson(param, ParametrosSIGEC.class);
			APISIGEC apiSigec = new APISIGEC();

			respuesta = apiSigec.postActoDocumento(token, url, json);
			RespuestaApiSigec respuestaApiSigec = gson.fromJson(respuesta, RespuestaApiSigec.class);
			respuestaApiSigec.getMessage();
			

		} catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta +  "\n" +
				"|------------------------------- JSON --------------------------------------------|"
				+ "\n" + json;

	}


    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaGrupoConceptos
     *
     * @return listaGrupoConceptos
     */
    public List<Registro> getListaGrupoConceptos()
    {
        return listaGrupoConceptos;
    }

    /**
     * Asigna la lista listaGrupoConceptos
     *
     * @param listaGrupoConceptos
     * Variable a asignar en listaGrupoConceptos
     */
    public void setListaGrupoConceptos(List<Registro> listaGrupoConceptos)
    {
        this.listaGrupoConceptos = listaGrupoConceptos;
    }

    /**
     * Retorna la lista listaTipoMedioPago
     * 
     * @return listaTipoMedioPago
     */
    public List<Registro> getListaTipoMedioPago()
    {
        return listaTipoMedioPago;
    }

    /**
     * Asigna la lista listaTipoMedioPago
     * 
     * @param listaTipoMedioPago
     * Variable a asignar en listaTipoMedioPago
     */
    public void setListaTipoMedioPago(List<Registro> listaTipoMedioPago)
    {
        this.listaTipoMedioPago = listaTipoMedioPago;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la variable auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    /**
     * Retorna la lista listaEstratoSf178
     *
     * @return listaEstratoSf178
     */
    public RegistroDataModelImpl getListaEstratoSf178()
    {
        return listaEstratoSf178;
    }

    /**
     * Asigna la lista listaEstratoSf178
     *
     * @param listaEstratoSf178
     * Variable a asignar en listaEstratoSf178
     */
    public void setListaEstratoSf178(RegistroDataModelImpl listaEstratoSf178)
    {
        this.listaEstratoSf178 = listaEstratoSf178;
    }

    /**
     * Retorna la lista listaEstratoSf178
     *
     * @return listaEstratoSf178
     */
    public RegistroDataModelImpl getListaEstratoSf178E()
    {
        return listaEstratoSf178E;
    }

    /**
     * Asigna la lista listaEstratoSf178
     *
     * @param listaEstratoSf178
     * Variable a asignar en listaEstratoSf178
     */
    public void setListaEstratoSf178E(
        RegistroDataModelImpl listaEstratoSf178E)
    {
        this.listaEstratoSf178E = listaEstratoSf178E;
    }

    /**
     * Retorna la lista listaAuxiliarSf178
     *
     * @return listaAuxiliarSf178
     */
    public RegistroDataModelImpl getListaAuxiliarSf178()
    {
        return listaAuxiliarSf178;
    }

    /**
     * Asigna la lista listaAuxiliarSf178
     *
     * @param listaAuxiliarSf178
     * Variable a asignar en listaAuxiliarSf178
     */
    public void setListaAuxiliarSf178(
        RegistroDataModelImpl listaAuxiliarSf178)
    {
        this.listaAuxiliarSf178 = listaAuxiliarSf178;
    }

    /**
     * Retorna la lista listaAuxiliarSf178
     *
     * @return listaAuxiliarSf178
     */
    public RegistroDataModelImpl getListaAuxiliarSf178E()
    {
        return listaAuxiliarSf178E;
    }

    /**
     * Asigna la lista listaAuxiliarSf178
     *
     * @param listaAuxiliarSf178
     * Variable a asignar en listaAuxiliarSf178
     */
    public void setListaAuxiliarSf178E(
        RegistroDataModelImpl listaAuxiliarSf178E)
    {
        this.listaAuxiliarSf178E = listaAuxiliarSf178E;
    }

    /**
     * Retorna la lista listaCentroCostoSf178
     *
     * @return listaCentroCostoSf178
     */
    public RegistroDataModelImpl getListaCentroCostoSf178()
    {
        return listaCentroCostoSf178;
    }

    /**
     * Asigna la lista listaCentroCostoSf178
     *
     * @param listaCentroCostoSf178
     * Variable a asignar en listaCentroCostoSf178
     */
    public void setListaCentroCostoSf178(
        RegistroDataModelImpl listaCentroCostoSf178)
    {
        this.listaCentroCostoSf178 = listaCentroCostoSf178;
    }

    /**
     * Retorna la lista listaCentroCostoSf178
     *
     * @return listaCentroCostoSf178
     */
    public RegistroDataModelImpl getListaCentroCostoSf178E()
    {
        return listaCentroCostoSf178E;
    }

    /**
     * Asigna la lista listaCentroCostoSf178
     *
     * @param listaCentroCostoSf178
     * Variable a asignar en listaCentroCostoSf178
     */
    public void setListaCentroCostoSf178E(
        RegistroDataModelImpl listaCentroCostoSf178E)
    {
        this.listaCentroCostoSf178E = listaCentroCostoSf178E;
    }

    /**
     * Retorna la lista listaTerceroSf178
     *
     * @return listaTerceroSf178
     */
    public RegistroDataModelImpl getListaTerceroSf178()
    {
        return listaTerceroSf178;
    }

    /**
     * Asigna la lista listaTerceroSf178
     *
     * @param listaTerceroSf178
     * Variable a asignar en listaTerceroSf178
     */
    public void setListaTerceroSf178(RegistroDataModelImpl listaTerceroSf178)
    {
        this.listaTerceroSf178 = listaTerceroSf178;
    }

    /**
     * Retorna la lista listaTerceroSf178
     *
     * @return listaTerceroSf178
     */
    public RegistroDataModelImpl getListaTerceroSf178E()
    {
        return listaTerceroSf178E;
    }

    /**
     * Asigna la lista listaTerceroSf178
     *
     * @param listaTerceroSf178
     * Variable a asignar en listaTerceroSf178
     */
    public void setListaTerceroSf178E(
        RegistroDataModelImpl listaTerceroSf178E)
    {
        this.listaTerceroSf178E = listaTerceroSf178E;
    }

    /**
     * Retorna la lista listaTarifaSf178
     *
     * @return listaTarifaSf178
     */
    public RegistroDataModelImpl getListaTarifaSf178()
    {
        return listaTarifaSf178;
    }

    /**
     * Asigna la lista listaTarifaSf178
     *
     * @param listaTarifaSf178
     * Variable a asignar en listaTarifaSf178
     */
    public void setListaTarifaSf178(RegistroDataModelImpl listaTarifaSf178)
    {
        this.listaTarifaSf178 = listaTarifaSf178;
    }

    /**
     * Retorna la lista listaTarifaSf178
     *
     * @return listaTarifaSf178
     */
    public RegistroDataModelImpl getListaTarifaSf178E()
    {
        return listaTarifaSf178E;
    }

    /**
     * Asigna la lista listaTarifaSf178
     *
     * @param listaTarifaSf178
     * Variable a asignar en listaTarifaSf178
     */
    public void setListaTarifaSf178E(RegistroDataModelImpl listaTarifaSf178E)
    {
        this.listaTarifaSf178E = listaTarifaSf178E;
    }

    /**
     * Retorna la lista listaConceptoSf178
     *
     * @return listaConceptoSf178
     */
    public RegistroDataModelImpl getListaConceptoSf178()
    {
        return listaConceptoSf178;
    }

    /**
     * Asigna la lista listaConceptoSf178
     *
     * @param listaConceptoSf178
     * Variable a asignar en listaConceptoSf178
     */
    public void setListaConceptoSf178(
        RegistroDataModelImpl listaConceptoSf178)
    {
        this.listaConceptoSf178 = listaConceptoSf178;
    }

    /**
     * Retorna la lista listaConceptoSf178
     *
     * @return listaConceptoSf178
     */
    public RegistroDataModelImpl getListaConceptoSf178E()
    {
        return listaConceptoSf178E;
    }

    /**
     * Asigna la lista listaConceptoSf178
     *
     * @param listaConceptoSf178
     * Variable a asignar en listaConceptoSf178
     */
    public void setListaConceptoSf178E(
        RegistroDataModelImpl listaConceptoSf178E)
    {
        this.listaConceptoSf178E = listaConceptoSf178E;
    }

    /**
     * Retorna la lista listaReferencia
     *
     * @return listaReferencia
     */
    public RegistroDataModelImpl getListaReferencia()
    {
        return listaReferencia;
    }

    /**
     * Asigna la lista listaReferencia
     *
     * @param listaReferencia
     * Variable a asignar en listaReferencia
     */
    public void setListaReferencia(RegistroDataModelImpl listaReferencia)
    {
        this.listaReferencia = listaReferencia;
    }

    /**
     * Retorna la lista listaReferencia
     *
     * @return listaReferencia
     */
    public RegistroDataModelImpl getListaReferenciaE()
    {
        return listaReferenciaE;
    }

    /**
     * Asigna la lista listaReferencia
     *
     * @param listaReferencia
     * Variable a asignar en listaReferencia
     */
    public void setListaReferenciaE(RegistroDataModelImpl listaReferenciaE)
    {
        this.listaReferenciaE = listaReferenciaE;
    }

    /**
     * Retorna la lista listaFuenteRecurso
     *
     * @return listaFuenteRecurso
     */
    public RegistroDataModelImpl getListaFuenteRecurso()
    {
        return listaFuenteRecurso;
    }

    /**
     * Asigna la lista listaFuenteRecurso
     *
     * @param listaFuenteRecurso
     * Variable a asignar en listaFuenteRecurso
     */
    public void setListaFuenteRecurso(
        RegistroDataModelImpl listaFuenteRecurso)
    {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }

    /**
     * Retorna la lista listaFuenteRecurso
     *
     * @return listaFuenteRecurso
     */
    public RegistroDataModelImpl getListaFuenteRecursoE()
    {
        return listaFuenteRecursoE;
    }

    /**
     * Asigna la lista listaFuenteRecurso
     *
     * @param listaFuenteRecurso
     * Variable a asignar en listaFuenteRecurso
     */
    public void setListaFuenteRecursoE(
        RegistroDataModelImpl listaFuenteRecursoE)
    {
        this.listaFuenteRecursoE = listaFuenteRecursoE;
    }

    /**
     * Retorna la lista listaNit
     *
     * @return listaNit
     */
    public RegistroDataModelImpl getListaNit()
    {
        return listaNit;
    }

    /**
     * Asigna la lista listaNit
     *
     * @param listaNit
     * Variable a asignar en listaNit
     */
    public void setListaNit(RegistroDataModelImpl listaNit)
    {
        this.listaNit = listaNit;
    }

    /**
     * Retorna la lista listaAfectarContrato
     *
     * @return listaAfectarContrato
     */
    public RegistroDataModelImpl getListaAfectarContrato()
    {
        return listaAfectarContrato;
    }

    /**
     * Asigna la lista listaAfectarContrato
     *
     * @param listaAfectarContrato
     * Variable a asignar en listaAfectarContrato
     */
    public void setListaAfectarContrato(
        RegistroDataModelImpl listaAfectarContrato)
    {
        this.listaAfectarContrato = listaAfectarContrato;
    }

    /**
     * Retorna la lista listaCodDescripcion
     *
     * @return listaCodDescripcion
     */
    public RegistroDataModelImpl getListaCodDescripcion()
    {
        return listaCodDescripcion;
    }

    /**
     * Asigna la lista listaCodDescripcion
     *
     * @param listaCodDescripcion
     * Variable a asignar en listaCodDescripcion
     */
    public void setListaCodDescripcion(
        RegistroDataModelImpl listaCodDescripcion)
    {
        this.listaCodDescripcion = listaCodDescripcion;
    }

    /**
     * Retorna la lista listaCodTarifaBase
     *
     * @return listaCodTarifaBase
     */
    public RegistroDataModelImpl getListaCodTarifaBase()
    {
        return listaCodTarifaBase;
    }

    /**
     * Asigna la lista listaCodTarifaBase
     *
     * @param listaCodTarifaBase
     * Variable a asignar en listaCodTarifaBase
     */
    public void setListaCodTarifaBase(
        RegistroDataModelImpl listaCodTarifaBase)
    {
        this.listaCodTarifaBase = listaCodTarifaBase;
    }

    /**
     * Retorna la lista listaFuentesRecurso
     *
     * @return listaFuentesRecurso
     */
    public RegistroDataModelImpl getListaFuentesRecurso()
    {
        return listaFuentesRecurso;
    }

    /**
     * Asigna la lista listaFuentesRecurso
     *
     * @param listaFuentesRecurso
     * Variable a asignar en listaFuentesRecurso
     */
    public void setListaFuentesRecurso(
        RegistroDataModelImpl listaFuentesRecurso)
    {
        this.listaFuentesRecurso = listaFuentesRecurso;
    }

    /**
     * Retorna la lista listaCentroCosto
     *
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCosto()
    {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     *
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto)
    {
        this.listaCentroCosto = listaCentroCosto;
    }

    /**
     * Retorna la lista listaAuxiliar
     *
     * @return listaAuxiliar
     */
    public RegistroDataModelImpl getListaAuxiliar()
    {
        return listaAuxiliar;
    }

    /**
     * Asigna la lista listaAuxiliar
     *
     * @param listaAuxiliar
     * Variable a asignar en listaAuxiliar
     */
    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar)
    {
        this.listaAuxiliar = listaAuxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    public boolean isDialogoVisible()
    {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible)
    {
        this.dialogoVisible = dialogoVisible;
    }

    /**
     * Retorna la lista listaSubfacturacionconceptosbg
     *
     * @return listaSubfacturacionconceptosbg
     */
    public List<Registro> getListaSubfacturacionconceptosbg()
    {
        return listaSubfacturacionconceptosbg;
    }

    /**
     * Asigna la lista listaSubfacturacionconceptosbg
     *
     * @param listaSubfacturacionconceptosbg
     * Variable a asignar en listaSubfacturacionconceptosbg
     */
    public void setListaSubfacturacionconceptosbg(
        List<Registro> listaSubfacturacionconceptosbg)
    {
        this.listaSubfacturacionconceptosbg = listaSubfacturacionconceptosbg;
    }

    public Registro getRegistroSub()
    {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    // </SET_GET_ADICIONALES>

    public boolean isObligaBaseGravable()
    {
        return obligaBaseGravable;
    }

    public void setObligaBaseGravable(boolean obligaBaseGravable)
    {
        this.obligaBaseGravable = obligaBaseGravable;
    }

    public boolean isBloqueaTercVarios()
    {
        return bloqueaTercVarios;
    }

    public void setBloqueaTercVarios(boolean bloqueaTercVarios)
    {
        this.bloqueaTercVarios = bloqueaTercVarios;
    }

    public boolean isNobligaBaseGravable()
    {
        return nobligaBaseGravable;
    }

    public void setNobligaBaseGravable(boolean nobligaBaseGravable)
    {
        this.nobligaBaseGravable = nobligaBaseGravable;
    }

    public boolean isDialogoVisibleRecaudar()
    {
        return dialogoVisibleRecaudar;
    }

    public void setDialogoVisibleRecaudar(boolean dialogoVisibleRecaudar)
    {
        this.dialogoVisibleRecaudar = dialogoVisibleRecaudar;
    }

    public boolean isBloqueaValorUnitario()
    {
        return bloqueaValorUnitario;
    }

    public void setBloqueaValorUnitario(boolean bloqueaValorUnitario)
    {
        this.bloqueaValorUnitario = bloqueaValorUnitario;
    }

    public boolean isBloqueaCantidad()
    {
        return bloqueaCantidad;
    }

    public void setBloqueaCantidad(boolean bloqueaCantidad)
    {
        this.bloqueaCantidad = bloqueaCantidad;
    }

    public String getValorUnidad()
    {
        return valorUnidad;
    }

    public void setValorUnidad(String valorUnidad)
    {
        this.valorUnidad = valorUnidad;
    }

    public String getValor()
    {
        return valor;
    }

    public void setValor(String valor)
    {
        this.valor = valor;
    }

    public String getIva()
    {
        return iva;
    }

    public void setIva(String iva)
    {
        this.iva = iva;
    }

    public String getDeudaAnterior()
    {
        return deudaAnterior;
    }

    public void setDeudaAnterior(String deudaAnterior)
    {
        this.deudaAnterior = deudaAnterior;
    }

    public String getReteFuente()
    {
        return reteFuente;
    }

    public void setReteFuente(String reteFuente)
    {
        this.reteFuente = reteFuente;
    }

    public String getDescuento()
    {
        return descuento;
    }

    public void setDescuento(String descuento)
    {
        this.descuento = descuento;
    }

    public String getIca()
    {
        return ica;
    }

    public void setIca(String ica)
    {
        this.ica = ica;
    }

    public String getValorNeto()
    {
        return valorNeto;
    }

    public void setValorNeto(String valorNeto)
    {
        this.valorNeto = valorNeto;
    }

    public String getValorImpoconsumo()
    {
        return valorImpoconsumo;
    }

    public void setValorImpoconsumo(String valorImpoconsumo)
    {
        this.valorImpoconsumo = valorImpoconsumo;
    }

    public boolean isVisibleFechaVencManual()
    {
        return visibleFechaVencManual;
    }

    public void setVisibleFechaVencManual(boolean visibleFechaVencManual)
    {
        this.visibleFechaVencManual = visibleFechaVencManual;
    }

    public boolean isVisibleFechaPreliqui()
    {
        return visibleFechaPreliqui;
    }

    public void setVisibleFechaPreliqui(boolean visibleFechaPreliqui)
    {
        this.visibleFechaPreliqui = visibleFechaPreliqui;
    }

    public int getIndiceSubfacturacionconceptosbg()
    {
        return indiceSubfacturacionconceptosbg;
    }

    public void setIndiceSubfacturacionconceptosbg(
        int indiceSubfacturacionconceptosbg)
    {
        this.indiceSubfacturacionconceptosbg = indiceSubfacturacionconceptosbg;
    }

    public String getTituloCantidad()
    {
        return tituloCantidad;
    }

    public void setTituloCantidad(String tituloCantidad)
    {
        this.tituloCantidad = tituloCantidad;
    }

    public String getTituloEstrato()
    {
        return tituloEstrato;
    }

    public void setTituloEstrato(String tituloEstrato)
    {
        this.tituloEstrato = tituloEstrato;
    }

    public String getTituloTarifa()
    {
        return tituloTarifa;
    }

    public void setTituloTarifa(String tituloTarifa)
    {
        this.tituloTarifa = tituloTarifa;
    }
    
    public String getTituloTarifaBase()
    {
        return tituloTarifaBase;
    }

    public void setTituloTarifaBase(String tituloTarifaBase)
    {
        this.tituloTarifaBase = tituloTarifaBase;
    }

    public String getTituloValor()
    {
        return tituloValor;
    }

    public void setTituloValor(String tituloValor)
    {
        this.tituloValor = tituloValor;
    }

    public boolean isManejaDescripcion()
    {
        return manejaDescripcion;
    }

    public void setManejaDescripcion(boolean manejaDescripcion)
    {
        this.manejaDescripcion = manejaDescripcion;
    }

    public boolean isManejaTarifaBase()
    {
        return manejaTarifaBase;
    }

    public void setManejaTarifaBase(boolean manejaTarifaBase)
    {
        this.manejaTarifaBase = manejaTarifaBase;
    }

    /**
	 * @return the manejaResFacturacion
	 */
	public boolean isManejaResFacturacion() {
		return manejaResFacturacion;
	}

	/**
	 * @param manejaResFacturacion the manejaResFacturacion to set
	 */
	public void setManejaResFacturacion(boolean manejaResFacturacion) {
		this.manejaResFacturacion = manejaResFacturacion;
	}

	public String getNombreTipoCobro() {
        return nombreTipoCobro;
    }

    public void setNombreTipoCobro(String nombreTipoCobro)
    {
        this.nombreTipoCobro = nombreTipoCobro;
    }

    public boolean isCargarBotonTercero()
    {
        return cargarBotonTercero;
    }

    public void setCargarBotonTercero(boolean cargarBotonTercero)
    {
        this.cargarBotonTercero = cargarBotonTercero;
    }

    public boolean isFacturado()
    {
        return facturado;
    }

    public void setFacturado(boolean facturado)
    {
        this.facturado = facturado;
    }

    public boolean isAnulada()
    {
        return anulada;
    }

    public void setAnulada(boolean anulada)
    {
        this.anulada = anulada;
    }

    public boolean isVisibleCambioTipo()
    {
        return visibleCambioTipo;
    }

    public void setVisibleCambioTipo(boolean visibleCambioTipo)
    {
        this.visibleCambioTipo = visibleCambioTipo;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public boolean isVisibleImpresionFacturaElectronica()
    {
        return visibleImpresionFacturaElectronica;
    }

    public void setVisibleImpresionFacturaElectronica(boolean visibleImpresionFacturaElectronica) {
		this.visibleImpresionFacturaElectronica = visibleImpresionFacturaElectronica;
	}
    
	public List<Registro> getListaCuentaPago() {
		return listaCuentaPago;
	}

	public void setListaCuentaPago(List<Registro> listaCuentaPago) {
		this.listaCuentaPago = listaCuentaPago;
	}
	
	/**
	 * @return the visibleCuentaPago
	 */
	public boolean isVisibleCuentaPago() {
		return visibleCuentaPago;
	}

	/**
	 * @param visibleCuentaPago the visibleCuentaPago to set
	 */
	public void setVisibleCuentaPago(boolean visibleCuentaPago) {
		this.visibleCuentaPago = visibleCuentaPago;
	}
	
	// 7713469 FACTGENERAL(10/08/2022 mrosero)
		public boolean isVisibleFacturarA() {
			return visibleFacturarA;
		}

		public void setVisibleFacturarA(boolean visibleFacturarA) {
			this.visibleFacturarA = visibleFacturarA;
		}
	// 7713469 FACTGENERAL(10/08/2022 mrosero)

		/**
		 * @return the valorDolares
		 */
		public String getValorDolares() {
			return valorDolares;
		}

		/**
		 * @param valorDolares the valorDolares to set
		 */
		public void setValorDolares(String valorDolares) {
			this.valorDolares = valorDolares;
		}

		/**
		 * @return the visibleFacturaDolares
		 */
		public boolean isVisibleFacturaDolares() {
			return visibleFacturaDolares;
		}

		/**
		 * @param visibleFacturaDolares the visibleFacturaDolares to set
		 */
		public void setVisibleFacturaDolares(boolean visibleFacturaDolares) {
			this.visibleFacturaDolares = visibleFacturaDolares;
		}

		/**
		 * @return the aplicaFacturaDolares
		 */
		public boolean isAplicaFacturaDolares() {
			return aplicaFacturaDolares;
		}

		/**
		 * @param aplicaFacturaDolares the aplicaFacturaDolares to set
		 */
		public void setAplicaFacturaDolares(boolean aplicaFacturaDolares) {
			this.aplicaFacturaDolares = aplicaFacturaDolares;
		}

		/**
		 * @return the visibleRelacionContrato
		 */
		public boolean isVisibleRelacionContrato() {
			return visibleRelacionContrato;
		}

		/**
		 * @param visibleRelacionContrato the visibleRelacionContrato to set
		 */
		public void setVisibleRelacionContrato(boolean visibleRelacionContrato) {
			this.visibleRelacionContrato = visibleRelacionContrato;
		}

		/**
		 * @return the saldoActualConcepto
		 */
		public String getSaldoActualConcepto() {
			return saldoActualConcepto;
		}

		/**
		 * @param saldoActualConcepto the saldoActualConcepto to set
		 */
		public void setSaldoActualConcepto(String saldoActualConcepto) {
			this.saldoActualConcepto = saldoActualConcepto;
		}

		/**
		 * @return the valorActualConcepto
		 */
		public String getValorActualConcepto() {
			return valorActualConcepto;
		}

		/**
		 * @param valorActualConcepto the valorActualConcepto to set
		 */
		public void setValorActualConcepto(String valorActualConcepto) {
			this.valorActualConcepto = valorActualConcepto;
		}

		public RegistroDataModelImpl getListanumerolista() {
			return listanumerolista;
		}

		public void setListanumerolista(RegistroDataModelImpl listanumerolista) {
			this.listanumerolista = listanumerolista;
		}

		public RegistroDataModelImpl getListanumerolistaE() {
			return listanumerolistaE;
		}

		public void setListanumerolistaE(RegistroDataModelImpl listanumerolistaE) {
			this.listanumerolistaE = listanumerolistaE;
		}

		public boolean isVisibleTarifaTercero() {
			return visibleTarifaTercero;
		}

		public void setVisibleTarifaTercero(boolean visibleTarifaTercero) {
			this.visibleTarifaTercero = visibleTarifaTercero;
		}
		
		public boolean isvisiblePrefijoContrato() {
			return visiblePrefijoContrato;
		}

		public void setvisiblePrefijoContrato(boolean visiblePrefijoContrato) {
			this.visiblePrefijoContrato = visiblePrefijoContrato;
		}

		/**
		 * @return the numeroLista
		 */
		public String getNumeroLista() {
			return numeroLista;
		}

		/**
		 * @return the codigoConcepto
		 */
		public String getCodigoConcepto() {
			return codigoConcepto;
		}

		/**
		 * @param codigoConcepto the codigoConcepto to set
		 */
		public void setCodigoConcepto(String codigoConcepto) {
			this.codigoConcepto = codigoConcepto;
		}

		public boolean isVisibleCotizaContrato() {
			return visibleCotizaContrato;
		}

		public void setVisibleCotizaContrato(boolean visibleCotizaContrato) {
			this.visibleCotizaContrato = visibleCotizaContrato;
		}

		public String getTituloGrillaPrincipal() {
			return tituloGrillaPrincipal;
		}

		public void setTituloGrillaPrincipal(String tituloGrillaPrincipal) {
			this.tituloGrillaPrincipal = tituloGrillaPrincipal;
		}

		public List<Registro> getListaCONTRATOSTERCERO() {
			return listaCONTRATOSTERCERO;
		}

		public void setListaCONTRATOSTERCERO(List<Registro> listaCONTRATOSTERCERO) {
			this.listaCONTRATOSTERCERO = listaCONTRATOSTERCERO;
		}

		public Date getFechaVencimientoDcto() {
			return fechaVencimientoDcto;
		}

		public void setFechaVencimientoDcto(Date fechaVencimientoDcto) {
			this.fechaVencimientoDcto = fechaVencimientoDcto;
		}

		public boolean isCheckConDscto() {
			return checkConDscto;
		}

		public void setCheckConDscto(boolean checkConDscto) {
			this.checkConDscto = checkConDscto;
		}

		public RegistroDataModelImpl getListaTipoContrato() {
			return listaTipoContrato;
		}

		public void setListaTipoContrato(RegistroDataModelImpl listaTipoContrato) {
			this.listaTipoContrato = listaTipoContrato;
		}

		public RegistroDataModelImpl getListaNroContrato() {
			return listaNroContrato;
		}

		public void setListaNroContrato(RegistroDataModelImpl listaNroContrato) {
			this.listaNroContrato = listaNroContrato;
		}

		public boolean isVisibleSigec() {
			return visibleSigec;
		}

		public void setVisibleSigec(boolean visibleSigec) {
			this.visibleSigec = visibleSigec;
		}

		/**
		 * @return the sujetoareportar
		 */
		public boolean isSujetoareportar() {
			return sujetoareportar;
		}

		/**
		 * @param sujetoareportar the sujetoareportar to set
		 */
		public void setSujetoareportar(boolean sujetoareportar) {
			this.sujetoareportar = sujetoareportar;
		}	
	/**
	 * @return the bloqEmail
	 */
	public boolean isBloqEmail() {
		return bloqEmail;
	}
	/**
	 * @param bloqEmail the bloqEmail to set
	 */
	public void setBloqEmail(boolean bloqEmail) {
		this.bloqEmail = bloqEmail;
	}
	
	/**
	 * @return the manPagoParcial
	 */
	public boolean isManPagoParcial() {
		return manPagoParcial;
	}

	/**
	 * @param manPagoParcial the manPagoParcial to set
	 */
	public void setManPagoParcial(boolean manPagoParcial) {
		this.manPagoParcial = manPagoParcial;
	}

	public boolean isPermiteAjusteDecimales() {
		return permiteAjusteDecimales;
	}

	public void setPermiteAjusteDecimales(boolean permiteAjusteDecimales) {
		this.permiteAjusteDecimales = permiteAjusteDecimales;
	}

	public boolean isBloqueaAjusteDecimal() {
		return bloqueaAjusteDecimal;
	}

	public void setBloqueaAjusteDecimal(boolean bloqueaAjusteDecimal) {
		this.bloqueaAjusteDecimal = bloqueaAjusteDecimal;
	}
	
    public boolean isDialogoConsecutivo() {
		return dialogoConsecutivo;
	}

	public void setDialogoConsecutivo(boolean dialogoConsecutivo) {
		this.dialogoConsecutivo = dialogoConsecutivo;
	}
	
	public BigDecimal getConsecutivoobjetocobro() {
			return consecutivoobjetocobro;
	}

	public void setConsecutivoobjetocobro(BigDecimal consecutivoobjetocobro) {
			this.consecutivoobjetocobro = consecutivoobjetocobro;
	}
	
	public String getActividadesEconomicas() {
		return actividadesEconomicas;
	}

	public void setActividadesEconomicas(String actividadesEconomicas) {
		this.actividadesEconomicas = actividadesEconomicas;
	}
}
