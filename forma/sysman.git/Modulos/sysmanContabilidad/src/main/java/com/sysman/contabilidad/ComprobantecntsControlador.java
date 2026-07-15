package com.sysman.contabilidad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadCpteRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadCuatroRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadDosRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
import com.sysman.contabilidad.enums.ComprobantecntsControladorEnum;
import com.sysman.contabilidad.enums.ComprobantecntsControladorUrlEnum;
import com.sysman.contabilidad.enums.ConceptosdocsoportedianControladorUrlEnum;
import com.sysman.contabilidad.enums.EliminarComprobanteControladorEnum;
import com.sysman.contabilidad.enums.FrmestadodocsoporteControladorUrlEnum;
import com.sysman.contabilidad.enums.SubdetallecomprobantecntsControladorUrlEnum;
import com.sysman.contabilidad.reportes.ComprobantesContPresReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbCodigoBarrasRemote;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
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
import com.sysman.util.rest.ParametrosEnvioFacturaFiltros;
import com.sysman.util.rest.ParametrosFormato;
import com.sysman.util.rest.ParametrosImpuestos;
import com.sysman.util.rest.ParametrosItems;
import com.sysman.util.rest.ParametrosItemsImpuestos;
import com.sysman.util.rest.ParametrosLegalizarFactura;
import com.sysman.util.rest.ParametrosLiquidacionSIGEC;
import com.sysman.util.rest.ParametrosPagoSIGEC;
import com.sysman.util.rest.ParametrosTercero;
import com.sysman.util.rest.ParametrosTerceroLote;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaApiSigec;
import com.sysman.util.rest.RespuestaConsultarReporte;
import com.sysman.util.rest.RespuestaCuerpoRangoFacturacion;
import com.sysman.util.rest.RespuestaEnvioFactura;
import com.sysman.util.rest.RespuestaFormatoConsultas;
import com.sysman.util.rest.RespuestaFridaLegalizar;
import com.sysman.util.rest.RespuestaFridaLegalizarNotas;
import com.sysman.util.rest.RespuestaNotasReporte;
import com.sysman.util.rest.RespuestaRangoFacturacion;
import com.sysman.util.soap.ApiInvoway;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.Cliente;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.CondicionesPago;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.CondicionesPago.CondicionPago;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.DatosTotales;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.DocumentosReferenciados;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento.DocumentosReferenciados.DocumentoReferenciado;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.krysalis.barcode4j.tools.Length;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 07/03/2016
 *
 * @version 2, spina 11/04/2017 - se realiza refactorizacion dss y depuracion
 *          sonar
 *
 * @version 3, spina 02/05/2017 - se refactoriza para ejb
 *
 * @author jrodrigueza
 * @version 4, 25/05/2017 Implementacion del proceso de generar comprobante
 *          presupuestal de ingresos en {@link #oprimirCompCruce()}
 *
 * @author ybecerra
 * @version 5, 12/06/2017 Implementacion al llamado de GeneralCodigoFormaEnum,
 *          para el codigo del formulario .
 *
 * @author jrodrigueza
 * @version 6, 15/08/2017 Ajuste proceso de generacin de comprobante
 *          presupuestal de ingresos desde el bot&oacute;n <i>Generar
 *          comprobante presupuestal</i>.
 * 
 * @author asana
 * @version 7, 16/06/2018 Se realiza llamado a la funcion retencionPorTercero
 * 
 * @author Cesar Ochoa Versin 7.1 Se crea un llamado a
 *         generarComprobantePresupuestalClob que enva una cadena cuyo valor es
 *         la concatenacin de clobs, para corregir bug de cadenas enviadas con
 *         ms de 4000 caracteres.
 * 
 * @author gfigueredo
 * @version 7.2, Se cambia la conversion del valor de la cuenta por
 *          Double.parseDouble, {@link #verificarCuentaConciliada(SelectEvent)}
 * @see #verificarCuentaConciliada(SelectEvent)
 * 
 * @author ldiaz
 * @version 8, Se implementa funcionalidad de envio a la dian de documento
 *          soporte.
 * @since 13/10/2022
 * @see oprimirenviardian()
 */
@ManagedBean
@ViewScoped
public class ComprobantecntsControlador extends BeanBaseDatosAcmeImpl {
	private final String compania;
	private final String modulo;
	private final String usuario;
	private List<Registro> listaTipoPagoL;
	private List<Registro> listaTipoContrato;
	private RegistroDataModelImpl listaTIPOPAGOSIA;
//	private List<Registro> listaCODISIA;
	private List<Registro> gruposasignados;
	private RegistroDataModelImpl listaDependencia;
	private RegistroDataModel listaNumero;
	private RegistroDataModelImpl listaCODISIA;
	private RegistroDataModelImpl listaTercero;
	private RegistroDataModelImpl listaNumeroContrato;
	private RegistroDataModelImpl listaCompACopiar;
	private RegistroDataModelImpl listaCuenta;
	private RegistroDataModelImpl listaBanco;
	private RegistroDataModelImpl listaReferencia;
	private RegistroDataModelImpl listaCentroCosto;
	private RegistroDataModelImpl listaAuxiliar;
	private RegistroDataModelImpl listaCODPROYECTO;
	private RegistroDataModelImpl listaORDENADOR;
	private RegistroDataModelImpl listaConcepto;
	private RegistroDataModelImpl listaComprobanteACopiar;
	private String nombreComprobante;
	private String compACopiar;
	private Date fechaConsignacion;
	private String nombreTercero;
	private String ano;
	private String mes;
	private String tipoMov;
	private String sesionuser;
	private String NomCompania;
	private String NitCompania;
	private List<Registro> regComprobante;
	private boolean visibleComprobantePptal;
	private boolean visiblePresupuesto;
	private boolean visibleCompCruce;
	private String clase;
	private boolean visibleInhumacion;
	private boolean bloqueaObjetos;
	private boolean bloqBtRetenciones;
	private boolean visibleIVA;
	private boolean plano;
	private String responsableArea;
	private boolean visibleTipoPagoL;
	private String tituloLblDoc;
	private boolean visibleEgresoCNT;
	private boolean visibleBtBancos;
	private boolean visibleCompRelacionado;
	private boolean visiblePacTesoreria;
	private boolean editableContrato;
	private boolean visibleBtPACEjec;
	private boolean visibleOrdenador;
	private String terceroEnEncabezado;
	private boolean permiteApoderado;
	private boolean bloqTercero;
	private boolean bloqNumeroContrato;
	private boolean bloqCuenta;
	private boolean bloqBanco;
	private boolean bloqreferencia;
	private boolean bloqCentroCosto;
	private boolean bloqAuxiliar;
	private boolean bloqORDENADOR;
	private boolean bloqTipoContrato;
	private boolean bloqTIPOPAGOSIA;
	private boolean bloqCODISIA;
	private boolean bloqCODPROYECTO;
	private boolean bloqFecha;
	private boolean bloqDescripcion;
	private boolean bloqTexto;
	private boolean bloqNroDocumento;
	private boolean bloqVlrDocumento;
	private boolean bloqFechaVcnDoc;
	private boolean bloqVlrBase;
	private boolean bloqVlrBaseIVA;
	private boolean bloqVlrAGirar;
	private boolean bloqPorcIVA;
	private boolean bloqFECHAPAGADOGN;
	private boolean bloqCopiar;
	private String estadoPeriodo;
	private boolean bloqImpreso;
	private boolean guardaRegistro;
	private String formatoComprobante;
	private String compRelacionado;
	private boolean pideRetencion;
	private String fechaMinima;
	private String fechaMaxima;
	private String ordenador;
	private boolean bloqCONSFACTURA;
	private boolean bloqTxtFechaConsignacion;
	private boolean visibleIndAnticipo;
	private boolean visibleConsFactura;
	private boolean visibleContribuyente;
	private boolean visibleConcepto;
	private boolean visiblePagoApoderado;
	private boolean visibleTipoPagoSIA;
	private boolean visibleCodigoSIA;
	private boolean visiblePagadoGN;
	private boolean visibleFechaPagado;
	private boolean visiblePagoEnPlano;
	private boolean visibleCodigoProyecto;
	private boolean visibleNombreProyecto;
	private boolean visibleImprimir;
	private boolean visibleTxtLiquido;
	private boolean visibleTxtRespArea;
	private boolean visibleTxtReviso;
	private boolean visibleTxtFechaConsignacion;
	private boolean visibleNiif;
	private boolean visibleNoNiif;
	private boolean visibleImprimirNiif;
	private boolean bloqueaNumero;
	private String controlChequera;
	private String opcionMenu;
	private boolean grupo;
	private boolean visibleDGConcepto = false;
	private boolean visibleDGConservaDesc = false;
	private boolean contabilizarNiiFVisible = false;
	private boolean visibleDgConfirmacion = false;
	private boolean bloqCompACopiar = true;
	private boolean rtaConservaDescripcion;
	private Registro regAuxCompACopiar;
	private boolean visibleBtDepuracion;
	private boolean visibleBtFacEquivalente;
	private StreamedContent archivoDescarga;
	private boolean bloqFechaPagado;
	private boolean visibleBanco;
	private boolean visibleEnviado;
	private boolean visiblePagoEfectivo;
	private boolean bloqueacuentas = true;

	private boolean abrirPostConstruct = true;
	private String tipoCpteAfect;
	private List<String> mensajesParametros;
	private Registro registroPreAct;
	private Registro registroIniPreAct;
	private boolean mostrarInsert;
	private boolean mostrarUpdate;
	private boolean mostrarDelete;
	private String strVlrDocumento;
	private boolean validarGenerar = true;
	private String facturaProveedor;
	private boolean visibleLegalizado;
	private boolean sigecVisible;
	private boolean existePPTO;
	/**
	 * Permite definir si se carga o no el boton de "Eliminar Comprobante Pptal"
	 */
	private boolean cargarEliminarCptePptal;
	/**
	 * variable estado para controlar la apertura del modal generar comprobante
	 * presupuestal desde el detalle
	 */
	private boolean estadoComprobantePresupuestal;
	/**
	 * Atributo que permite validar si el boton Comp. Ingresos se activa o no
	 */
	private boolean activarCompIngreso;

	/**
	 * Indicador para saber si al tercero aplica o no Ley 1819
	 */
	private boolean ley1819;

	private static final String LEY1450 = "LEY1450";
	private static final String TIPOCPTE = "tipoCpte";
	private static final String NUMEROCPTE = "numeroCpte";
	private static final String CTECOMPACOPIAR = "CompACopiar";
	private static final String FECHACOMPROBANTE = "fechaComprobante";
	private static final String NUMEROCOMP = "numeroComp";
	private static final String CTEOPCIONMENU = "opcionMenu";
	private static final String SUCURSALCOMPROBANTE = "sucursalComprobante";
	private static final String TERCEROCOMPROBANTE = "terceroComprobante";
	private static final String TIPOCOMP = "tipoComp";
	private static final String CTEVISIBLEORDENADOR = "visibleOrdenador";
	private static final String TB_TB443 = "TB_TB443";
	private static final String NOMBRECOMPROBANTECONS = "nombreComprobante";
	private static final String TIPOCOMPROBANTECONS = "tipoComprobante";
	private static final String CLASECOMPROBANTECONS = "claseComprobante";
	private static final String INDICADOR_NO_PPTO = "indicadorNoPpto";
	
	private static final String URL_SERVICIO_SOAP= "URL SERVICIO SOAP";
    private static final String MANEJA_FACTURACION_ELECTRONICA_EXTERNA = "MANEJA FACTURACION ELECTRONICA EXTERNA";
    private static final String USUARIO_FACT_ELECTRONICA_EXTERNA = "USUARIO FACT ELECTRONICA EXTERNA";
    private static final String CLAVE_FACT_ELECTRONICA_EXTERNA = "CLAVE FACT ELECTRONICA EXTERNA";


	private ComprobantesContPresReporteador comprobantesContPresReporteador;

	/**
	 * Parametro NRO_DOCUMENTO.
	 */
	private static final String NRO_DOCUMENTO = "NRO_DOCUMENTO";
	/**
	 * Parametro VLR_DOCUMENTO.
	 */
	private static final String VLR_DOCUMENTO = "VLR_DOCUMENTO";
	/**
	 * Parametro TIPO.
	 */
	private static final String TIPO = "TIPO";
	/**
	 * Parametro TIPOPPT.
	 */
	
	private static final String TIPOPPT = "TIPOPPTAL";

	private String paramTipoACopiar;

	private String paramCausacionHeredada;

	private String compHeredar;

	private boolean visibleCausacionHeredada;

	private boolean cargarTerceros;
	
	private boolean manejaCausacion;
	
	private boolean manejaProcesoJudicial;
	
	private boolean manejaConceptoIng;

	private static final String CAMPO_NUMERO = "NUMERO";

	/**
	 * Lista que carga los conceptos de flujo de efectivo
	 */
	private RegistroDataModelImpl listaCodigoFlujoEfectivo;
	
	private RegistroDataModelImpl listaListaPlantillas;
	
	/**
	 * variable que almacena el valor del parametro: CONTROLA LONGITUD DE CONSECUTIVO
	 */
	private boolean controlaLongitud;

	@EJB
	private EjbSysmanUtilRemote sysmanUtil;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbGeneralesRemote ejbGenerales;

	@EJB
	private EjbContabilidadCpteRemote ejbContabilidadCpte;

	@EJB
	private EjbContabilidadDosRemote ejbContabilidadDos;

	@EJB
	private EjbContabilidadCuatroRemote ejbContabilidadCuatro;

	@EJB
	private EjbContabilidadSeisRemote ejbContabilidadSeis;
	@EJB
	private EjbContabilidadCincoRemote ejbContabilidadCinco;
	@EJB
	private EjbContabilidadSieteRemote ejbContabilidadSiete;
	
	@EJB
	private EjbContabilidadCeroRemote ejbContabilidadCero;
	
	/**
	 * Implementacion del EJB de ejbContabilidadUno para hacer el llamado a las
	 * funciones y EjbPresupuestoUnoRemote que se invocan dentro del Controlador y
	 * se encuentran almacenadas en el paquete PCK_PRESUPUESTO_COM1
	 */
	@EJB
	private EjbContabilidadUnoRemote ejbContabilidadUno;

	private boolean visibleDgCompPptal;
	private Registro rsTercero;
	private Object descri;
	// declaracion de variables documento soporte
	private String nroFacturaCons = "NRO_FACTURA";
	private String impresoCons = "IMPRESO";
	private String tipoCobroCons = "TIPOCOBRO";
	private String codigoCobroCons = "CODIGO_COBRO";
	double totalFactura = 0;
	private String prefijo = "";
	String nitSinDigito;
	private boolean bloqConceptoCUDS;
	private String consecutivoDian;
	private String resolucionDian;
	private String codigoNotaAjuste;
	
	private String  plantilla;
	private String nombrePlantilla;
	private Date fechaPlantilla;
	private String ivaActual;
	/**
	 * indica si el comprobante relacionado a los detalles ha sido enviado a la dian
	 */
	private Boolean yaEnviadoDianExito = false;
	/**
	 * Indica la referencia de tipo de factura de recaudo
	 */
	private String tipoFactRecaudo;
	/**
	 * Atributo que valida si el formulario se abre desde el boton comprobante
	 * contable desde el modulo de almacï¿½n.
	 */
	private Map<String, Object> parametrosAlm;
	/**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
	private boolean varVolver = true;
	
	private boolean visiblePresentarPlantillas;
	
	private Boolean visibleListaPlantillas = false;
	private boolean visibleFirme;

	@EJB
	private EjbCodigoBarrasRemote ejbCodigoBarras;
	private boolean controlaFactura;
	private Map<String,Object> parametroswf;
	private boolean verCerrar = true;
	private boolean imprimirPDF;
	private boolean manejaVarios;
	private boolean anticipoBool;

	public ComprobantecntsControlador() {
		super();
		SessionUtil.setSessionVar("modulo", "1");
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		usuario = SessionUtil.getUser().getCodigo();
		sesionuser = SysmanFunciones.concatenar(SysmanFunciones.nvl(SessionUtil.getUser().getNombre1(), " ").toString(),
				SysmanFunciones.nvl(SessionUtil.getUser().getNombre2(), " ").toString(),
				SysmanFunciones.nvl(SessionUtil.getUser().getApellido1(), " ").toString(),
				SysmanFunciones.nvl(SessionUtil.getUser().getApellido2(), " ").toString());
		NomCompania = SessionUtil.getCompaniaIngreso().getNombre();
		NitCompania = SessionUtil.getCompaniaIngreso().getNit();
		if (NitCompania.toString().contains("-")) {
			nitSinDigito = NitCompania.toString().split("-")[0];
		} else {
			nitSinDigito = NitCompania;
		}
		tipoCpteAfect = "";
		mostrarDelete = true;
		mostrarInsert = true;
		mostrarUpdate = true;
		visibleImprimir = true;
		try {
			numFormulario = GeneralCodigoFormaEnum.COMPROBANTECNTS_CONTROLADOR.getCodigo();
			validarPermisos();
			tipoFactRecaudo = SysmanFunciones
					.nvl(SessionUtil
							.getSessionVarContainer(ConstantesFacturacionGenEnum.TIPOFACTURA_RECAUDO.getValue()), "0")
					.toString();
			cargarFlash();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeInformativo(ex.getMessage());
			SessionUtil.redireccionarMenuPermisos();
			abrirPostConstruct = false;
			return;
		} finally {
			SessionUtil.cleanFlash();
		}
	}

	@SuppressWarnings("unchecked")
	public void cargarFlash() throws SysmanException {
		Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
		if (parametrosEntrada != null) {
			rid = (Map<String, Object>) parametrosEntrada.get("rid");
			ano = SysmanFunciones.nvl(parametrosEntrada.get("ano"), "").toString();
			mes = SysmanFunciones.nvl(parametrosEntrada.get("mes"), "").toString();
			tipoMov = SysmanFunciones.nvl(parametrosEntrada.get("tipoMov"), "").toString();
			estadoComprobantePresupuestal = Boolean
					.parseBoolean(SysmanFunciones.nvl(parametrosEntrada.get("generar"), false).toString());
			opcionMenu = SysmanFunciones.nvl(parametrosEntrada.get(CTEOPCIONMENU), "").toString();
			visibleOrdenador = (boolean) SysmanFunciones.nvl(parametrosEntrada.get(CTEVISIBLEORDENADOR), false);
			try {
			boolean Almacen = (boolean) SysmanFunciones.nvl(parametrosEntrada.get("almacen"), false);
			parametrosAlm = (Map<String, Object>) SessionUtil.getSessionVarContainer("parametrosAlm");
			if (parametrosAlm != null && parametrosAlm.get("retorno") != null) {
			    varVolver = (boolean) SysmanFunciones.nvl(parametrosAlm.get("retorno"), false);
			} else {
			    varVolver = false;
			}
			parametroswf = (Map<String,Object>) parametrosEntrada.get("parametroswf");
			if(parametroswf != null) {
				varVolver = false;
				verCerrar = false;
			}
			if (Almacen) {
				rid = (Map<String, Object>) parametrosEntrada.get("ridContable");
				tipoMov = SysmanFunciones.nvl(parametrosAlm.get("MovContable"), "").toString();
				ano = SysmanFunciones.nvl(parametrosAlm.get("anoMov"), "").toString();
				mes = SysmanFunciones.nvl(parametrosAlm.get("mesMov"), "").toString();
			}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				JsfUtil.agregarMensajeInformativo(ex.getMessage());
			}
			fechaMinima = "01/" + (mes.length() == 1 ? Integer.toString(0) + mes : mes) + "/" + ano;
			visibleDgCompPptal = (boolean) (parametrosEntrada.get(INDICADOR_NO_PPTO) != null
					? parametrosEntrada.get(INDICADOR_NO_PPTO)
					: visibleDgCompPptal);
			try {
				fechaMaxima = SysmanFunciones.ultimoDiaInt(SysmanFunciones.convertirAFecha(fechaMinima)) + "/"
						+ (mes.length() == 1 ? Integer.toString(0) + mes : mes) + "/" + ano;
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
			String accionDetalle = extraerString(parametrosEntrada.get("accion"));
			accion = accionDetalle != null ? accionDetalle : accion;
		} else {
			throw new SysmanException(idioma.getString("TB_TB440"));
		}
	}

	@Override
	public void iniciarListas() {
		cargarListaTercero();
		cargarListaCuenta();
		cargarListaReferencia();
		cargarListaCentroCosto();
		cargarListaAuxiliar();
		cargarListaORDENADOR();
		cargarListaTipoContrato();
		cargarListaTIPOPAGOSIA();
		cargarListaCODISIA();
		cargarListaCODPROYECTO();
		cargarListaDependencia();
		cargarListaComprobanteACopiar();
		cargarListaCodigoFlujoEfectivo();
		cargarListaListaPlantillas();

	}

	@Override
	public void iniciarListasSub() {
		cargarListaNumeroContrato();
		cargarListaCompACopiar();
		cargarListaBanco();
		cargarListaConcepto();
	}

	@Override
	public void iniciarListasSubNulo() {
		// Metodo heredado
	}

	@PostConstruct
	public void inicializar() {
		if(parametroswf != null) {
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('height','770px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('width','1830px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('top','45px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('left','125px');");
			
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('width','1800px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('height','550px');");
		}
		if (!abrirPostConstruct) {
			return;
		}
		enumBase = GenericUrlEnum.COMPROBANTE_CNT;
		buscarLlave();
		asignarOrigenDatos();
		comprobantesContPresReporteador = new ComprobantesContPresReporteador(ejbSysmanUtil);
		visiblePresupuesto = true;
		visibleComprobantePptal = true;

		bloqueosPorDefecto();
		try {
			responsableArea = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "NOMBRE RESPONSABLE DE AREA", modulo, new Date(), true),
					"").toString();
			String manejaPresupPrivado = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA PRESUPUESTO DE SECTOR PRIVADO", modulo, new Date(), true), "NO").toString();
			manejaCausacion = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA CAUSACION AUTOMATICA", modulo, new Date(), true),
					"NO").toString().equals("SI")?true:false;
			
			manejaProcesoJudicial = (SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA PROCESOS JUDICIALES CREMIL", modulo, new Date(), true),
					"NO").toString().equals("SI")?true:false);
			
			manejaVarios = (SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "PERMITE CREAR COMPROBANTES CON TERCERO VARIOS", modulo, new Date(), true),
					"NO").toString().equals("SI")?true:false);
			
			manejaConceptoIng = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA CAUSACION AUTOMATICA INGRESOS", modulo, new Date(), true),
					"NO").toString().equals("SI")?true:false;
			
			if ("SI".equalsIgnoreCase(manejaPresupPrivado)) {
				visibleComprobantePptal = false;
				visiblePresupuesto = false;
				visibleCompCruce = false;
			}
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.TIPOCPTE.getName(), tipoMov);
			setBloqConceptoCUDS(false);
			List<Registro> aux = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubdetallecomprobantecntsControladorUrlEnum.URL1895011.getValue())
							.getUrl(),
					param));

			for (Registro aux1 : aux) {
				if (Integer.parseInt(SysmanFunciones.nvl(aux1.getCampos().get("EXISTE"), "").toString()) > 0) {
					setBloqConceptoCUDS(true);
				}
			}			
			open();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);

		}
		
		//7742952_SIGEC (mrosero)
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPOCPTE.getName(), tipoMov);
		sigecVisible = false;

		Registro aux;
		try {
			aux = RegistroConverter.toRegistro(
                    requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                            		ComprobantecntsControladorUrlEnum.URL15079
                                                            .getValue())
                            .getUrl(), param));
			
			
			boolean valor = (boolean) aux.getCampos().get("ESTAMPILLA_SIGEC");

				if (valor) {
					sigecVisible = true;
				}

		} catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
		//
	}

	public void bloqueosPorDefecto() {
		bloqTercero = true;
		bloqBtRetenciones = true;
		bloqCopiar = true;
		bloqTipoContrato = true;
		bloqFecha = true;
		bloqreferencia = true;
		bloqNumeroContrato = true;
		bloqDescripcion = true;
		bloqTexto = true;
		bloqCentroCosto = true;
		bloqAuxiliar = true;
		bloqORDENADOR = true;
		bloqTIPOPAGOSIA = true;
		bloqCODISIA = true;
		bloqCODPROYECTO = true;
		bloqCuenta = true;
		bloqCONSFACTURA = true;
		bloqNroDocumento = true;
		bloqFechaPagado = true;
		bloqFechaVcnDoc = true;
		bloqPorcIVA = true;
		bloqVlrDocumento = true;
		bloqVlrBase = true;
		bloqVlrBaseIVA = true;
		bloqVlrAGirar = true;
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
		parametrosListado.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
		parametrosListado.put("MES", mes);
	}
	
	
	public void oprimircausacionAutomatica() {
		
		String ano = extraerString(registro.getCampos().get(GeneralParameterEnum.ANO.getName()));

		
		String[] campos = { "rid", "ano", "mes", TIPOCOMP, NUMEROCOMP, "vlrBase", "vlrBaseIva",
				NOMBRECOMPROBANTECONS, CTEOPCIONMENU, "tercero","sucursal", "fecha", CLASECOMPROBANTECONS};

		Object[] valores = { css, ano, mes, tipoMov,
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(),
				registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASE.getValue()).toString(),
				registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue()).toString(),
				nombreComprobante, opcionMenu,
				registro.getCampos().get(ComprobantecntsControladorEnum.TERCERO.getValue()),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()),
				registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),clase};

		int numFormulario = GeneralCodigoFormaEnum.FRM_MENU_CAUSACION_AUTOMATICA_CONTROLADOR.getCodigo();
		SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(numFormulario), modulo, campos, valores);
	}
	
	public void oprimirconceptosIngresos() {

		String ano = extraerString(registro.getCampos().get(GeneralParameterEnum.ANO.getName()));


		String[] campos = { "rid", "ano", "mes", TIPOCOMP, NUMEROCOMP,NOMBRECOMPROBANTECONS, CTEOPCIONMENU, "accion", "tercero","sucursal"};

		Object[] valores = { css, ano, mes, tipoMov,
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(),
				nombreComprobante, opcionMenu, accion, 
				registro.getCampos().get(ComprobantecntsControladorEnum.TERCERO.getValue()),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName())};

		int numFormulario = GeneralCodigoFormaEnum.CA_CAUSACION_INGRESOS.getCodigo();
		SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(numFormulario), modulo, campos, valores);
	}

	public void cargarListaTipoPagoL() {
		try {
			String tiposPagos = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "TIPOS DE PAGOS", modulo, new Date(), true), "")
					.toString();
			String[] tiposPagosArray = tiposPagos.split(",");
			if (tiposPagosArray.length == 0) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB442"));
				listaTipoPagoL = null;
				return;
			}
			HashMap<String, Object> aux;
			listaTipoPagoL = new ArrayList<>();
			for (int i = 0; i < tiposPagosArray.length; i++) {
				aux = new HashMap<>();
				aux.put("TIPOPAGO", tiposPagosArray[i]);
				listaTipoPagoL.add(new Registro(i, aux));
			}
		} catch (SystemException ex) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void cargarListaTipoContrato() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaTipoContrato = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13434.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaTIPOPAGOSIA() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13436.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTIPOPAGOSIA = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCODISIA() {
//		Map<String, Object> param = new TreeMap<>();
//		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//		try {
//			listaCODISIA = RegistroConverter
//					.toListRegistro(
//							requestManager.getList(
//									UrlServiceUtil.getInstance()
//											.getUrlServiceByUrlByEnumID(
//													ComprobantecntsControladorUrlEnum.URL34038.getValue())
//											.getUrl(),
//									param));
//		} catch (SystemException e) {
//			logger.error(e.getMessage(), e);
//			JsfUtil.agregarMensajeError(e.getMessage());
//		}

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL34038.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		setListaCODISIA(new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName()));
	}

	public void cargarListaListaPlantillas() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.MODULO.getName(), modulo);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoMov);

		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL104080.getValue());
		
		
		listaListaPlantillas = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
		
		visibleListaPlantillas = listaListaPlantillas==null?false:true;
	}
	
	public void cargarListaCODPROYECTO() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13438.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		listaCODPROYECTO = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaDependencia() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13439.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaTercero() {
		UrlBean urlBean = null;
		if(manejaVarios) {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL676.getValue());
		} else {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL14210.getValue());
		}
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaTercero = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	public void cargarListaNumeroContrato() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13441.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));

		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

		param.put(GeneralParameterEnum.FECHA.getName(), registro.getCampos().get(GeneralParameterEnum.FECHA.getName()));

		listaNumeroContrato = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NUMERO.getName());
	}

	public void cargarListaCompACopiar() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13479.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANOCOMP", ano);
		param.put("TIPOMOVIMIENTO", tipoMov);
		param.put("NUMEROCOMP",
				SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), 0));
		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

		listaCompACopiar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NUMERO.getName());
	}

	public void cargarListaCuenta() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13444.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaBanco() {
		if (permiteApoderado) {
			if ((boolean) SysmanFunciones.nvl(registro.getCampos().get("PAGOAPODERADO"), false)) {
				listaBancoApoderado();
			} else {
				listaBancoTercero();
			}
		} else {
			listaBancoTercero();
		}
	}

	public void listaBancoTercero() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13446.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));

		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));

		// listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
		// urlBean.getUrlConteo().getUrl(), param, true,
		// GeneralParameterEnum.CUENTA.getName());

		listaBanco = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CUENTA");

	}

	public void listaBancoApoderado() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13445.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		// listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
		// urlBean.getUrlConteo().getUrl(), param, true,
		// GeneralParameterEnum.CUENTA.getName());
		try {
			listaBanco = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
					CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.TERCEROPAGOS.getTable()));
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaReferencia() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13447.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(), ano);
		listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCentroCosto() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13448.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(), ano);
		listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCodigoFlujoEfectivo
	 *
	 */
	public void cargarListaCodigoFlujoEfectivo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL675.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listaCodigoFlujoEfectivo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaAuxiliar() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13449.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(), ano);
		listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaORDENADOR() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13450.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaORDENADOR = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CEDULA");
	}

	public void cargarListaConcepto() {
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL13451.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), SysmanFunciones.nvlStr(ejbSysmanUtil
					.consultarParametro(compania, "TIPO DE COBRO CONCEPTOS CONTABLES", modulo, new Date(), true), ""));

			param.put(ComprobantecntsControladorEnum.REGIMEN.getValue(),
					registro.getCampos().get(ComprobantecntsControladorEnum.REGIMEN.getValue()));
			listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
					GeneralParameterEnum.CODIGO.getName());
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaComprobanteACopiar() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL5331.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), paramTipoACopiar);

		listaComprobanteACopiar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());
	}

	public void oprimirBtnTercero() {
		if (css == null) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
			return;
		}

		Map<String, Object> param = new HashMap<>();
		param.put("rowidContabilidad", css);
		param.put("anio", ano);
		param.put("mes", mes);
		param.put("tipoMov", tipoMov);
		param.put("estadoComprobante", estadoComprobantePresupuestal);
		param.put("opcionMenu", opcionMenu);
		param.put("visibleOrdenador", visibleOrdenador);
		param.put("visibleDgCompPptal", visibleDgCompPptal);

		Direccionador direccionador = new Direccionador();
		direccionador.setParametros(param);

		direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.TERCEROS_CONTROLADOR.getCodigo()));
		SessionUtil.redireccionarForma(direccionador, modulo);

	}

	public void oprimirPidePptal() {
		// <CODIGO_DESARROLLADO>
		/*
		 * if (validarRegistroNuevo(null)) { return; }
		 */

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(EliminarComprobanteControladorEnum.TIPO.getValue(),
				registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		Registro regAux;
		try {
			regAux = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL5330.getValue())
											.getUrl(),
									param));

			if (Integer.parseInt(SysmanFunciones
					.nvlStr(regAux.getCampos().get(GeneralParameterEnum.CUENTA.getName()).toString(), "0")) > 0) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4240"));
				return;
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}

		String descripcion = extraerString(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
		descripcion = descripcion != null ? descripcion : "";
		if (ACCION_INSERTAR.equals(accion) && "".equals(descripcion)) {
			registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), ".");
		}
		List<Registro> clasesPptales = traerClasesPptales();
		if (clasesPptales == null || clasesPptales.isEmpty()) {
			validarGenerar = false;
			oprimirCompCruce();
		} else {
			abrirComprobanteCntAfectar();
		}
	}

	/**
	 * Construye una cadena que representan los registros de las equivalencias
	 * presupuestales separadas por punto y coma. Donde cada registro contiene los
	 * campos consecutivo, cuenta, valor y rubro presupuestal, separados por
	 * coma.<br>
	 * <b>Ej:</b> CONSECUTIVO,CUENTA,VALOR,RUBRO_PPTAL;CONSECUTIVO,CUENTA,VALOR,
	 * RUBRO_PPTAL
	 *
	 * @param equivalenciasPptales Lista de equivalencias presupuestales.
	 * @return cadena para insertar en el procedimiento que genera el comprobante
	 *         presupuestal.
	 */
	private String traerCadenaRegistros(List<Registro> equivalenciasPptales) {
		StringBuilder stringBuilder = new StringBuilder("TO_CLOB('");
		Object[] params = new Object[4];
		for (int i = 0; i < equivalenciasPptales.size(); i++) {
			Registro equivalencia = equivalenciasPptales.get(i);
			// }
			// for (Registro equivalencia : equivalenciasPptales) {
			Map<String, Object> campos = equivalencia.getCampos();
			for (Map.Entry<String, Object> entry : campos.entrySet()) {
				switch (entry.getKey()) {
				case "CONSECUTIVO":
					params[0] = entry.getValue();
					break;
				case "CUENTA":
					params[1] = entry.getValue();
					break;
				case "VALOR":
					params[2] = entry.getValue();
					break;
				case "RUBRO_PPTAL":
					params[3] = entry.getValue();
					break;
				default:
					break;
				}
			}
			/**
			 * @Cesar Ochoa Se deja un 400 como un nmero prudencial de registros en cada
			 *        cierre del to_clob (maximo 4000 caracteres) Si la funcin falla por la
			 *        longitud de los parmetros, se debe modificar esta funcin para reducir
			 *        la cantidad de caracteres que se insertan durante cada concatenacin de
			 *        clobs
			 */
			if (i % 400 == 0 && i != 0) {
				logger.info("divide string");
				stringBuilder.append("')||TO_CLOB('");
				logger.info(stringBuilder);
			}
			stringBuilder
					.append("" + params[0] + (char) 44 + params[1] + (char) 44 + params[2] + (char) 44 + params[3]);
			stringBuilder.append((char) 59);
		}
		if (stringBuilder.toString().endsWith("||TO_CLOB('")) {
			for (int i = 0; i < "||TO_CLOB('".length(); i++)
				stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		} else {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			stringBuilder.append("')");
		}
		return stringBuilder.toString();
	}

	/**
	 * Trae las clases equivalentes en presupuesto para el tipo de comprobante
	 * seleccionado.
	 *
	 * @return Lista de equivalencias presupuestales.
	 */
	private List<Registro> traerClasesPptales() {
		List<Registro> equivalencias = null;
		Map<String, Object> params = new TreeMap<>();
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(ComprobantecntsControladorEnum.TIPO.getValue(), registro.getCampos().get(TIPO));
		try {
			String urlEnumId = ComprobantecntsControladorUrlEnum.URL81274.getValue();
			String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
			List<Parameter> parameters = requestManager.getList(url, params);
			equivalencias = RegistroConverter.toListRegistro(parameters);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return equivalencias;
	}

	/**
	 * Abre el modal que permite seleccionar los comprobantes a afectar.
	 */
	private void abrirComprobanteCntAfectar() {
		String[] campos = obtenerCampos();
		String numero = extraerString(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		String vlrDocumento = extraerString(
				registro.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()));
		String tercero = extraerString(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		String sucursal = extraerString(registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		String fecha = null;
		try {
			fecha = SysmanFunciones
					.convertirAFechaCadena((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()));
		} catch (ParseException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
		String centroCosto = extraerString(registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()));
		String auxiliar = extraerString(registro.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()));
		String vlrAGirar = extraerString(registro.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue()));
		String nombreFormulario = "R";
		Object[] valores = { css, ano, mes, tipoMov, numero, clase, vlrDocumento, tercero, sucursal, compRelacionado,
				terceroEnEncabezado, tipoCpteAfect, fecha, centroCosto, auxiliar, controlChequera, vlrAGirar,
				opcionMenu, nombreFormulario };
		if (requiereSeleccionarRubros() && !tieneRubrosAsociados()) {
			JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB2739"));
			return;
		}
		int numFormulario = GeneralCodigoFormaEnum.COMPROBANTE_CNT_AFECTAR_CONTROLADOR.getCodigo();
		SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(numFormulario), modulo, campos, valores);
	}

	private String[] obtenerCampos() {
		return new String[] { "rowIdComprobante", "anoComprobante", "mesComprobante", TIPOCOMPROBANTECONS,
				"numeroComprobante", CLASECOMPROBANTECONS, "vlrDocumento", TERCEROCOMPROBANTE, SUCURSALCOMPROBANTE,
				"compRelacionado", "terceroEncabezado", "tipoCpteAfect", FECHACOMPROBANTE, "centroCosto",
				"auxiliarComprobante", "controlChequera", "vlrGirar", CTEOPCIONMENU, "nombreFormulario" };

	}

	/**
	 * Verifica si las cuentas del detalle tienen equivalencia presupuestal.
	 *
	 * @return verdadero si tiene rubros asociados
	 */
	private boolean tieneRubrosAsociados() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ComprobantecntsControladorEnum.TIPO.getValue(), registro.getCampos().get(TIPO));
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		List<Registro> registros = null;
		try {
			registros = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13452.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return registros == null ? true : registros.isEmpty();
	}

	/**
	 * Verifica si las cuentas del detalle tienen una o mas equivalencias
	 * presupuestales.
	 *
	 * @return Verdadero si una o mas cuentas tienen mas de una equivalencia
	 *         presupuestal.
	 */
	private boolean requiereSeleccionarRubros() {
		boolean respuesta = false;
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ComprobantecntsControladorEnum.TIPO.getValue(), registro.getCampos().get(TIPO));
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		List<Registro> registros = null;
		try {
			registros = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13453.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// SE COMENTA MIENTRAS SE DETERMINA EN QUE CASO SE DEBE
		// MOSTRAR EL MENSAJE
		// if ((registros == null) || registros.isEmpty()) {
		// JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2738"));
		// }
		for (Registro reg : registros) {
			int conteo = Integer
					.parseInt(SysmanFunciones.nvl(reg.getCampos().get("NUMERO_EQUIVALENCIAS"), "0").toString());
			if (conteo > 1) {
				respuesta = true;
			}
		}
		return respuesta;
	}

	public void oprimirCopiar() {
		// <CODIGO_DESARROLLADO>
		try {
			if (!tipoMov.isEmpty()
					&& !(SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
							.toString()).isEmpty()
					&& !registro.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString().isEmpty()) {
				if (validarRegistroNuevo(TB_TB443)) {
					return;
				}

				BigInteger bigNumero = new BigInteger(
						registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
				boolean valor = ejbContabilidadCpte.verificarTieneDetalle(compania, Integer.parseInt(ano), tipoMov,
						bigNumero);
				if (valor) {
					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB445"));
					bloqCompACopiar = true;
					return;
				} else {
					bloqCompACopiar = false;
					registro.getCampos().put(CTECOMPACOPIAR, "");
					cargarListaCompACopiar();
				}

			} else {
				bloqCompACopiar = true;
				registro.getCampos().put(CTECOMPACOPIAR, "");
				cargarListaCompACopiar();
			}

		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}
   
	
	public void oprimirCopiarCausacion() {

		if (!SysmanFunciones.validarVariableVacio(compHeredar)) {

			try {
				ejbContabilidadSiete.causarHeredandoConciliacion(compania, Integer.parseInt(ano),
						new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
						tipoMov, new BigInteger(compHeredar), usuario);

				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

				cargarRegistro(registro.getLlave(), accion, registro.getIndice());
				cargarRegistro();
				agregarRegistroNuevo(false);

			} catch (NumberFormatException | SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		} else {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4299"));
		}

	} 

	
	public void oprimirEliminarCOMPIngr() {
	    try {
	        ejbContabilidadCero.eliminarComprobantesIngreso(
	            compania,
	            Integer.parseInt(ano), 
	            SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TIPO.getName()), "").toString(), 
	            new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()).toString(), 
	            SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), "").toString() 
	        );

	        JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

	        cargarRegistro(registro.getLlave(), accion, registro.getIndice());
	        cargarRegistro();

	    } catch (NumberFormatException | SystemException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	}

	/**
	 * metodo que llama al oprimir el boton pdf
	 */
	public void oprimirPresentarPlantillas() {
		// <CODIGO_DESARROLLADO>
		if (plantilla == null) {
			oprimirImprimir();
		}else {
			generarPdfdesdeWord();
		}
		// </CODIGO_DESARROLLADO>
	}
	
	private void generarPdfdesdeWord() {
//		 TODO Auto-generated method stub
		 Map<String, Object> param = new HashMap<>();
	        param.put("s$compania$s", compania);
	        param.put("s$usuario$s", SessionUtil.getUser().getCodigo());
	        String[] campos = new String[3];

	        String[] valores = new String[3];
	        campos[0] = "codigoPlantilla";
	        campos[1] = "fechaPlantilla";
	        campos[2] = "nombreDocDescarga";

	        valores[0] = plantilla;
	        valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
	        valores[2] = nombrePlantilla;

	        HashMap<String, String> variablesConsultaW = new HashMap<>();
	        variablesConsultaW.put("s$compania$s", "'" + compania + "'");
	        variablesConsultaW.put("s$ano$s", ano);
	        variablesConsultaW.put("s$tipo$s",  "'" +  tipoMov + "'");
	        variablesConsultaW.put("s$numeroIni$s",registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
	        variablesConsultaW.put("s$numeroFin$s",registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
	        // variables por parametro para documento word
	        SessionUtil.setSessionVar("variablesConsultaWord", variablesConsultaW);

	        SessionUtil.cargarModalDatosFlash(
	                        Integer.toString(
	                                        GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
	                                                        .getCodigo()),
	                        SessionUtil.getModulo(),
	                        campos,
	                        valores);

	}

	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		imprimirPDF=true;
		if (!prepararImpresion()) {
			return;
		}
		
		if(visibleLegalizado && tipoMov.equals("ANT") && !(Boolean)registro.getCampos().get(ComprobantecntsControladorEnum.IMPRESO.getValue())) {
			envioNotificaciones();
		}

		actualizarValorPagosEnComprobantesContables();

		if ("E".equals(clase)) {
			String[] campos = { "anio", TIPOCOMP, NUMEROCOMP, "fecha"};
			String[] valores={ ano, tipoMov,
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), SysmanFunciones
					.formatearFecha((Date)registro.getCampos().get(GeneralParameterEnum.FECHA.getName())) };

			SessionUtil.cargarModalDatosFlash(
					Integer.toString(GeneralCodigoFormaEnum.FORMATO_EGRESO_CONTROLADOR.getCodigo()), modulo, campos,
					valores);
		} else {
			generaInformeFormatoComprobante(ReportesBean.FORMATOS.PDF, false);
		}
		
		//actualizarValorImpreso();
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirImpresora() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirTeceroContratos() {

		if (css == null) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
			return;
		}

		String[] campos = { "nit", "sucursal", "rid" };
		Object[] valores = { registro.getCampos().get("TERCERO"), registro.getCampos().get("SUCURSAL"), css };

		SessionUtil.cargarModalDatosFlash(String.valueOf(GeneralCodigoFormaEnum.TERCERO_OPS_CONTROLADOR.getCodigo()),
				modulo, campos, valores);

	}

	public void oprimirCalcularRetenciones() {
		// <CODIGO_DESARROLLADO>
		int uvt;
		if (validarRegistroNuevo(TB_TB443)) {
			return;
		}
		if (!SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.COMPANIA.getName())
				&& (Integer.parseInt(SysmanFunciones
						.nvl(registro.getCampos().get(GeneralParameterEnum.ANO.getName()), 0).toString()) != 0)
				&& !SysmanFunciones.validarCampoVacio(registro.getCampos(), TIPO) && (Long.parseLong(SysmanFunciones
						.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), 0).toString()) != 0)) {

			try {
				int consecutivo = ejbContabilidadDos.generarConsecutivoParaMensajes();
				BigDecimal retorno = ejbContabilidadDos.calcularRetencionesPagosLaboralesLey1450(compania,
						Integer.parseInt(modulo), new BigDecimal(consecutivo), Integer.parseInt(ano),
						(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()), tipoMov,
						new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
						SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), "")
								.toString(),
						registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString(), nombreTercero,
						BigDecimal.valueOf(SysmanFunciones.nvlDbl(
								registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASE.getValue()), 0)),
						BigDecimal.valueOf(SysmanFunciones.nvlDbl(
								registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue()), 0)),
						SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()), "")
								.toString(),
						SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()),
								SysmanConstantes.CONS_CENTRO).toString(),
						SysmanFunciones
								.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue()),
										"")
								.toString(),
						SessionUtil.getUser().getCodigo());
				uvt = retorno.intValue();
				calcularRetenciones(uvt, consecutivo);
			} catch (NamingException | SQLException | IllegalAccessException | InstantiationException
					| ClassNotFoundException | SystemException e) {

				Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}

		// </CODIGO_DESARROLLADO>
	}

	private void calcularRetenciones(int uvt, int consecutivo) throws IllegalAccessException, InstantiationException,
			ClassNotFoundException, SQLException, NamingException, SystemException {
		if (uvt == 0) {
			ejbContabilidadDos.calcularRetenciones(compania, Integer.parseInt(modulo), consecutivo,
					Integer.parseInt(ano), (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),
					tipoMov, new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()), "")
							.toString(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), "")
							.toString(),
					SysmanFunciones
							.nvl(registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "").toString(),
					nombreTercero,
					BigDecimal.valueOf(SysmanFunciones
							.nvlDbl(registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASE.getValue()), 0)),
					BigDecimal.valueOf(SysmanFunciones.nvlDbl(
							registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue()), 0)),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()), "")
							.toString(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()),
							SysmanConstantes.CONS_CENTRO).toString(),
					SysmanFunciones
							.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue()), "")
							.toString(),
					usuario, "99999999999999999999", "99999999999999999999");

		}

		Map<String, Object> param = new TreeMap<>();
		param.put("CONSECUTIVO", consecutivo);
		List<Registro> mensajes = RegistroConverter
				.toListRegistro(
						requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL13483.getValue())
										.getUrl(),
								param));

		for (Registro registro : mensajes) {
			JsfUtil.agregarMensajeInformativo(registro.getCampos().get("MENSAJE").toString());
		}

		boolean condicion = (Double.doubleToRawLongBits(SysmanFunciones
				.nvlDbl(registro.getCampos().get(ComprobantecntsControladorEnum.DEBITOSAFECTADOS.getValue()), 0)) != 0)
				|| (Double.doubleToRawLongBits(SysmanFunciones.nvlDbl(
						registro.getCampos().get(ComprobantecntsControladorEnum.CREDITOSAFECTADOS.getValue()),
						0)) != 0);
		if (condicion) {
			try {
				if ("SI".equals(ejbSysmanUtil.consultarParametro(compania, "NO PERMITE MODIFICAR COM DESPUES DE GIRO",
						modulo, new Date(), true))) {
					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB457"));
				} else {
					oprimirVerDetalle();
				}
			} catch (SystemException e) {
				Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		} else {
			oprimirVerDetalle();
		}

	}

	public void oprimirImprimirCheque() {
		// <CODIGO_DESARROLLADO>

		// Validar el parametro booleano con el fin de definir cual
		// chque sera impreso:
		// - Horizontal (true), "000750ChequeH"
		// - Vertical (false), "000749Cheque"
		String alineacion;
		try {
			alineacion = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"ORIENTACION DEL FORMATO DE CHEQUE", modulo, new Date(), true), "VERTICAL");
			if ("VERTICAL".equalsIgnoreCase(alineacion)) {
				generaInformeCheque(ReportesBean.FORMATOS.PDF, false);
			} else {// HORIZONTAL
				generaInformeCheque(ReportesBean.FORMATOS.PDF, true);
			}
		} catch (SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirVerDetalle() {
		// <CODIGO_DESARROLLADO>
		// se eiminan campos que no se van a trabar del registro en general, solo para
		// despues de facturar.
		if(accion.equals(ACCION_INSERTAR)) {			
			JsfUtil.agregarMensajeInformativo("Debe guardar el registro primero, para que  genere el detalle del comprobante.");
			return;
		}
		registro.getCampos().remove("CONSECUTIVODIAN");
		registro.getCampos().remove("RESOLUCIONDIAN");
		registro.getCampos().remove("PREFIJODIAN");
		if (ACCION_MODIFICAR.equals(accion) && validarRegistroNuevo(null)) {
			return;
		}
		
		if (manejaCausacion) {
			try {
				
				/* ejbContabilidadSeis.calcularValorNeto(compania, Integer.parseInt(ano), tipoMov,
						new BigInteger(SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()))), 0, 0);
				 */
				// mod JM 09-04-2025 
				ejbContabilidadSeis.calcularValorNeto(compania, ano, tipoMov,
					SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName())), "0", "0");

				
			} catch (NumberFormatException | SystemException e) {
				logger.error(e.getMessage(), e);
				SessionUtil.redireccionarMenuPermisos();
			}
		}
		
		String numeroCpte = extraerString(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		Date fechaCpte = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
		String tercero = extraerString(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		String sucursal = extraerString(registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		String impreso = extraerString(registro.getCampos().get(ComprobantecntsControladorEnum.IMPRESO.getValue()));
		String referencia = extraerString(registro.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()));
		String vlrDocumento = extraerString(
				registro.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()));
		String vlrAGirar = extraerString(registro.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue()));
		String descripcion = extraerString(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));

		String centroCosto = extraerString(registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()));

		String auxiliar = extraerString(registro.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()));

		String codigoFlujoEfectivo = extraerString(registro.getCampos().get("CODIGO_FLUJO_EFECTIVO"));		

		String[] campos = { "rid", "anio", "mes", TIPOCOMP, NUMEROCOMP, "fechaPar",
				GeneralParameterEnum.TERCERO.getName(), "sucursal", "titulo", "impreso", "estadoPeriodo", "referencia",
				"valorDocumento", "valorGirar", "mensaje", "descripcion", "controlChequera", CTEOPCIONMENU, "accion",
				"compRelacionado", "centroCosto", "auxiliar", "codigoFlujoEfectivo", "yaEnviadoDian", "parametroswf" };

		Object[] valores = { css, ano, mes, tipoMov, numeroCpte, fechaCpte, tercero, sucursal, nombreComprobante,
				impreso, estadoPeriodo, referencia, vlrDocumento, vlrAGirar, "0", descripcion, controlChequera,
				opcionMenu, accion, compRelacionado, centroCosto, auxiliar, codigoFlujoEfectivo, yaEnviadoDianExito, parametroswf };

		SessionUtil.redireccionarPorFormulario(modulo,
				Integer.toString(GeneralCodigoFormaEnum.SUBDETALLECOMPROBANTECNTS_CONTROLADOR.getCodigo()), campos,
				valores, true);
	}

	public void oprimirEnviarSIGEC() {

		// <CODIGO_DESARROLLADO>
		/*
		 * RUTINA PARA 
		 * B. SERVICIO REPORTE/ANULACIï¿½N DE LIQUIDACIï¿½N DE ESTAMPILLA 		 * 
		 * C. SERVICIO REPORTE/ANULACIï¿½N DE PAGOS DE UNA LIQUIDACIï¿½N DE ESTAMPILLA
		 */
		// </CODIGO_DESARROLLADO>
		
		String url;
		String token = null;
		String log = null;
		archivoDescarga = null;

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST GATEWAY SIGEC", "1", new Date(), false);
			token = ejbSysmanUtil.consultarParametro(compania, "TOKEN AUTORIZACION SIGEC", "1", new Date(), false);

			String tipoSigec = registro.getCampos().get(ComprobantecntsControladorEnum.TIPO_SIGEC.getValue()).toString();

			if (tipoSigec != null) {
				if (tipoSigec.equals("1") || tipoSigec.equals("2")) {
					log = "|---------------         LOG DE LOGICA SERVICIO LIQUIDACION / SIGEC        ---------------|";
					log = log + "\n" + servicioLiquidacion(token, url);
				} else {
					log = "|---------------         LOG DE LOGICA SERVICIO PAGO / SIGEC        ---------------|";
					log = log + "\n" + servicioPago(token, url);
				}
			}

			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "Log.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException | SysmanException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return;
		// </CODIGO_DESARROLLADO>
	}

	private String servicioLiquidacion(String token, String url) throws SysmanException {
		String respuesta = "";
		String json = "";

		Map<String, Object> params = new TreeMap<>();
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		params.put(ComprobantecntsControladorEnum.TIPOSIGEC.getValue(),
				registro.getCampos().get(ComprobantecntsControladorEnum.TIPO_SIGEC.getValue()));
		params.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		try {
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL004.getValue())
											.getUrl(),
									params));

			ParametrosLiquidacionSIGEC param = new ParametrosLiquidacionSIGEC();

			param.setType(((Double) rs.getCampos().get(ComprobantecntsControladorEnum.TIPO_SIGEC.getValue())).intValue());
			param.setActDocumentCode(SysmanFunciones
					.nvl(rs.getCampos().get(ComprobantecntsControladorEnum.EQUIV_SIGEC.getValue()), "").toString());
			param.setStampNumber(SysmanFunciones
					.nvl(rs.getCampos().get(ComprobantecntsControladorEnum.CUENTAPPTAL.getValue()), "0"));
			param.setLiquidatedValue(
					((Double) rs.getCampos().get(ComprobantecntsControladorEnum.VALOR_DEBITO.getValue())).intValue());
			param.setLiquidatedValueId(SysmanFunciones
					.nvl(rs.getCampos().get(ComprobantecntsControladorEnum.NUMERO.getValue()), "").toString());
			param.setPayerDocumentParametricTypeCode(
					SysmanFunciones.nvl(rs.getCampos().get(ComprobantecntsControladorEnum.SIGEC.getValue()), "").toString());
			param.setTaxpayerDocumentNumber(SysmanFunciones
					.nvl(rs.getCampos().get(ComprobantecntsControladorEnum.TERCERO.getValue()), "").toString());

			Gson gson = new Gson();
			json = gson.toJson(param, ParametrosLiquidacionSIGEC.class);
			APISIGEC apiSigec = new APISIGEC();

			respuesta = apiSigec.postLiquidacion(token, url, json);
			
			RespuestaApiSigec respuestaApiSigec = gson.fromJson(respuesta, RespuestaApiSigec.class);
			respuestaApiSigec.getMessage();

		} catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta +  "/n" +
				"|------------------------------- JSON --------------------------------------------|"
				+ json;
	}

	private String servicioPago(String token, String url) throws SysmanException {
		String respuesta = "";
		String json = "";

		Map<String, Object> params = new TreeMap<>();
		SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy-MM-dd");

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		params.put(ComprobantecntsControladorEnum.TIPOSIGEC.getValue(),
				registro.getCampos().get(ComprobantecntsControladorEnum.TIPO_SIGEC.getValue()));
		params.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		try {
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL005.getValue())
											.getUrl(),
									params));

			ParametrosPagoSIGEC param = new ParametrosPagoSIGEC();

			String fecha = formatFecha.format(rs.getCampos().get(GeneralParameterEnum.FECHA.getName()));

			param.setType(((Double) rs.getCampos().get(ComprobantecntsControladorEnum.TIPO_SIGEC.getValue())).intValue());
			param.setPaymentDate(fecha);
			param.setValuePayed((int) rs.getCampos().get(ComprobantecntsControladorEnum.VALOR.getValue()));
			param.setLiquidatedValueId(SysmanFunciones
					.nvl(rs.getCampos().get(ComprobantecntsControladorEnum.CMPTE_AFECTADO.getValue()), "").toString());
			param.setPaidValueId(SysmanFunciones
					.nvl(rs.getCampos().get(ComprobantecntsControladorEnum.NUMERO.getValue()), "").toString());

			Gson gson = new Gson();
			json = gson.toJson(param, ParametrosPagoSIGEC.class);
			APISIGEC apiSigec = new APISIGEC();

			respuesta = apiSigec.postPago(token, url, json);

			RespuestaApiSigec respuestaApiSigec = gson.fromJson(respuesta, RespuestaApiSigec.class);
			respuestaApiSigec.getMessage();

		} catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta +  "/n" +
		"|------------------------------- JSON --------------------------------------------|"
		+ json;

	}

	private boolean validarRegistroNuevo(String mensaje) {
		guardaRegistro = false;
		agregarRegistroNuevo(false);
		if (!guardaRegistro) {
			if (mensaje != null) {
				JsfUtil.agregarMensajeError(idioma.getString(mensaje));
			}
			return true;
		}
		// POR HPV EN CORPORINOQUIA EN SEP 22 de 2005
		if (!validarPacEgreso(false)) {
			return true;
		}
		return false;

	}

	public void oprimirCodificarRetenciones() {
		// <CODIGO_DESARROLLADO>
		boolean valor = false;
		guardaRegistro = false;
		agregarRegistroNuevo(false);

		try {
			if (!guardaRegistro) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB464"));
				return;
			}
			// POR HPV EN CORPORINOQUIA EN SEP 22 de 2005
			if (!validarPacEgreso(false)) {
				return;
			}

			// FIN DEL CAMBIO
			if ((Boolean) SysmanFunciones
					.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.AUTORETENEDOR.getValue()), false)) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB561"));
				// return;
			}
			if ((Double.doubleToRawLongBits(SysmanFunciones.nvlDbl(
					registro.getCampos().get(ComprobantecntsControladorEnum.DEBITOSAFECTADOS.getValue()), 0)) != 0)
					|| (Double.doubleToRawLongBits(SysmanFunciones.nvlDbl(
							registro.getCampos().get(ComprobantecntsControladorEnum.CREDITOSAFECTADOS.getValue()),
							0)) != 0)) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB457"));
				return;
			}

			BigInteger numcompro = new BigInteger(
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());

			valor = ejbContabilidadSeis.retencionPorTercero(compania, Integer.parseInt(ano), tipoMov, numcompro,
					registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString(),
					registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString(),
					registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASE.getValue()).toString(),
					registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue()).toString(),
					SessionUtil.getUser().toString());

			String[] campos = { "rid", "ano", "mes", TIPOCOMP, NUMEROCOMP, "vlrBase", "vlrBaseIva",
					NOMBRECOMPROBANTECONS, CTEOPCIONMENU, "terceroautoRetenedor", "tercero", "sucursal" };
			Object[] valores = { css, ano, mes, tipoMov,
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(),
					registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASE.getValue()).toString(),
					registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue()).toString(),
					nombreComprobante, opcionMenu,
					registro.getCampos().get(ComprobantecntsControladorEnum.AUTORETENEDOR.getValue()),
					SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName())),
					SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()))};
			SessionUtil.redireccionarPorFormulario(modulo,
					Integer.toString(GeneralCodigoFormaEnum.COMPROBANTECNTRETENCIONS_CONTROLADOR.getCodigo()), campos,
					valores, true);

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirPresupuesto() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirVerComprobantePptal() {
		// <CODIGO_DESARROLLADO>
		try {

			Map<String, Object> param1 = new TreeMap<>();
			param1.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param1.put(GeneralParameterEnum.ANO.getName(), ano);
			param1.put("TIPOCOMPROBANTE", tipoMov);
			param1.put("NUMEROCOMPROBANTE", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

			Registro regPptal = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL18261.getValue())
											.getUrl(),
									param1));

			if (Integer.parseInt(regPptal.getCampos().get("CANTIDAD").toString()) == 0) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3180"));
				return;
			}

			String nombreComp = null;
			String claseComprobante = null;

			String formatoComp = null;

			String pideTercero = null;

			String tipoVigenciaFutura = null;

			String estado = null;

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put("TIPOCOMP", tipoMov);

			Registro reg = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL18260.getValue())
											.getUrl(),
									param));

			if (reg != null) {

				nombreComp = reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
				claseComprobante = reg.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString();
				formatoComp = reg.getCampos().get("FORMATO").toString();
				pideTercero = ((boolean) reg.getCampos().get("TECEROIGUAL")) ? "S" : "N";
				tipoVigenciaFutura = reg.getCampos().get("TIPOVIGENCIAFUTURA").toString();

				estado = ejbSysmanUtil.verificarEstadoPeriodoAnual(compania, Integer.parseInt(ano),
						Integer.parseInt(modulo), 1);

			}

			String[] campos = { "rid", "ano", "mes", TIPOCOMPROBANTECONS, NOMBRECOMPROBANTECONS, CLASECOMPROBANTECONS,
					"formatoComprobante", "pideTercero", "tipoVigenciaFutura", "estadoPeriodo", "formularioRetorno",
					"ingreso" };

			Object[] valores = { css, ano, mes, tipoMov, nombreComp, claseComprobante, formatoComp, pideTercero,
					tipoVigenciaFutura, estado, numFormulario, false };

			SessionUtil.redireccionarPorFormulario("3",
					Integer.toString(GeneralCodigoFormaEnum.COMPROBANTEPPTALS_CONTROLADOR.getCodigo()), campos, valores,
					true);

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirComprobanteIngresos() {

		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);

			Registro rsTipoPptal = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL2278.getValue())
											.getUrl(),
									param));

			String nombreComp = null;
			String claseComprobante = null;

			String formatoComp = null;

			String pideTercero = null;

			String estado = null;

			Map<String, Object> paramDos = new TreeMap<>();
			paramDos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			paramDos.put("TIPOCOMP", rsTipoPptal.getCampos().get("TIPOPPTAL"));

			Registro reg = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL18260.getValue())
											.getUrl(),
									paramDos));

			if (reg != null) {

				nombreComp = reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
				claseComprobante = reg.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString();
				formatoComp = reg.getCampos().get("FORMATO").toString();
				pideTercero = ((boolean) reg.getCampos().get("TECEROIGUAL")) ? "S" : "N";
				estado = ejbSysmanUtil.verificarEstadoPeriodoAnual(compania, Integer.parseInt(ano),
						Integer.parseInt(modulo), 1);

			}

			String[] campos = { "rid", "ano", "mes", TIPOCOMPROBANTECONS, NOMBRECOMPROBANTECONS, CLASECOMPROBANTECONS,
					"formatoComprobante", "pideTercero", "tipoVigenciaFutura", "estadoPeriodo", "formularioRetorno",
					"tipoPptal", "ingreso", "ridPptal" };

			Map<String, Object> ridPptal = new HashMap<>();
			ridPptal.put("KEY_COMPANIA", compania);
			ridPptal.put("KEY_ANO", ano);
			ridPptal.put("KEY_TIPO", rsTipoPptal.getCampos().get("TIPOPPTAL"));
			ridPptal.put("KEY_NUMERO", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			Object[] valores = { css, ano, mes, tipoMov, nombreComp, claseComprobante, formatoComp, pideTercero, false,
					estado, numFormulario, rsTipoPptal.getCampos().get("TIPOPPTAL"), true, ridPptal };

			SessionUtil.redireccionarPorFormulario("3",
					Integer.toString(GeneralCodigoFormaEnum.COMPROBANTEPPTALS_CONTROLADOR.getCodigo()), campos, valores,
					true);

		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}

	}

	public void oprimirVerBancos() {
		// <CODIGO_DESARROLLADO>

		guardaRegistro = false;
		agregarRegistroNuevo(false);
		if (!guardaRegistro) {
			return;
		}
		// POR HPV EN CORPORINOQUIA EN SEP 22 de 2005
		if (!validarPacEgreso(false)) {
			return;
		}
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("anoEgreso", ano);
			parametros.put("tipoEgreso", tipoMov);
			parametros.put("numeroEgreso", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
			parametros.put("rowIdComprobante", css);
			parametros.put("vlrDocumento",
					registro.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()).toString());
			parametros.put(TERCEROCOMPROBANTE,
					registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString());
			parametros.put(SUCURSALCOMPROBANTE,
					registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString());
			parametros.put("centroCostoComprobante",
					registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()).toString());
			parametros.put("auxiliarComprobante",
					registro.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()).toString());
			parametros.put(FECHACOMPROBANTE, SysmanFunciones
					.convertirAFechaCadena((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));
			parametros.put("mesComprobante", mes);
			parametros.put(CTEVISIBLEORDENADOR, visibleOrdenador);
			parametros.put(CLASECOMPROBANTECONS, clase);
			parametros.put("vlrGirar",
					registro.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue()).toString());
			parametros.put(CTEOPCIONMENU, opcionMenu);
			parametros.put("botonBancos", true);

			SessionUtil.setFlashLocal(parametros);
			SessionUtil.cargarModalDatos(
					Integer.toString(GeneralCodigoFormaEnum.COMPROBANTECNTBANCOS_CONTROLADOR.getCodigo()), modulo);
		} catch (ParseException ex) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirComprobantesAAfectar() {
		try {
			// <CODIGO_DESARROLLADO>
			if (validarRegistroNuevo(null)) {
				return;
			}
			agregarDescripcionDefault();

			if (!SysmanFunciones.validarVariableVacio(tipoMov) && !SysmanFunciones
					.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.TERCERO.getName())) {

				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.ANO.getName(), ano);
				param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
				param.put(GeneralParameterEnum.NUMERO.getName(),
						SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), 0));

				List<Registro> x = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL13454.getValue())
										.getUrl(),
								param));

				if (!x.isEmpty() && (x.get(0).getCampos().get("X") != null)) {
					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB567"));
					return;
				}
			}

			String[] campos = obtenerCampos();
			Object[] valores = { css, ano, mes, tipoMov,
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), clase,
					registro.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()).toString(),
					registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString(),
					registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString(), compRelacionado,
					terceroEnEncabezado, tipoCpteAfect,
					SysmanFunciones.convertirAFechaCadena(
							(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())),
					registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()).toString(),
					registro.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()).toString(), controlChequera,
					registro.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue()).toString(),
					opcionMenu, "A" };
			if(opcionMenu.equals("X"))
			{
				Map<String, Object> param = new HashMap<>();
				param.put("rowIdComprobante", css);
				param.put("anoComprobante", ano);
				param.put("mesComprobante", mes);
				param.put(TIPOCOMPROBANTECONS, tipoMov);
				param.put("numeroComprobante", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
				param.put(CLASECOMPROBANTECONS, clase);
				param.put("vlrDocumento", registro.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()).toString());
				param.put(TERCEROCOMPROBANTE, registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString());
				param.put(SUCURSALCOMPROBANTE, registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString());
				param.put("compRelacionado", compRelacionado);
				param.put("terceroEncabezado", terceroEnEncabezado);
				param.put("tipoCpteAfect", tipoCpteAfect);
				param.put(FECHACOMPROBANTE, SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));
				param.put("centroCosto", registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()).toString());
				param.put("auxiliarComprobante", registro.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()).toString());
				param.put("controlChequera", controlChequera);
				param.put("vlrGirar", registro.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue()).toString());
				param.put(CTEOPCIONMENU, opcionMenu);
				param.put("nombreFormulario", "A");
				Direccionador direccionador = new Direccionador();
				direccionador.setNumForm("2544");
				direccionador.setParametros(param);
				SessionUtil.redireccionarForma(direccionador, modulo);				
			}
			else if(manejaProcesoJudicial && tipoMov.equals("OST"))
			{ 
				SessionUtil.cargarModalDatosFlashCerrar(
						Integer.toString(GeneralCodigoFormaEnum.FRM_SELECCIONAR_PROCESOS_JUDICIALES_CONTROLADOR.getCodigo()), modulo,
						campos, valores);
			}
			else
			{
				SessionUtil.cargarModalDatosFlashCerrar(
						Integer.toString(GeneralCodigoFormaEnum.COMPROBANTE_CNT_AFECTAR_CONTROLADOR.getCodigo()), modulo,
						campos, valores);				
			}
			cargarRegistro();
			// </CODIGO_DESARROLLADO>
		} catch (ParseException | SystemException ex) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private void agregarDescripcionDefault() {
		if (accion.equals(ACCION_INSERTAR)
				&& "".equals(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()))) {
			registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), ".");
		}
	}

	public void oprimirPacTesoreria() {
		// <CODIGO_DESARROLLADO>
		if (validarRegistroNuevo(null)) {
			return;
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		List<Registro> regAux = null;
		try {
			regAux = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13455.getValue())
											.getUrl(),
									param));
			if (!regAux.isEmpty() && "0".equals(regAux.get(0).getCampos().get("CONTEO").toString())) {
				String mensaje = ejbContabilidadCuatro.generarPacTesoreria(compania, Integer.parseInt(ano), tipoMov,
						new BigInteger(SysmanFunciones
								.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "").toString()),
						(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()), usuario);

				JsfUtil.agregarMensajeInformativo(mensaje);
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		Map<String, Object> parametros = new HashMap<>();
		parametros.put(GeneralParameterEnum.ANO.getName().toLowerCase(), ano);
		parametros.put(TIPOCOMP, tipoMov);
		parametros.put("fechaComp", registro.getCampos().get(GeneralParameterEnum.FECHA.getName()));
		parametros.put(NUMEROCOMP, registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		parametros.put("nombreComp", nombreComprobante);
		// <CODIGO_DESARROLLADO>
		SessionUtil.setFlashLocal(parametros);

		SessionUtil.cargarModalDatos(Integer.toString(GeneralCodigoFormaEnum.PACTESORERIACNTS_CONTROLADOR.getCodigo()),
				modulo);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirPACEjecutado() {
		// <CODIGO_DESARROLLADO>
		if (!validarPacEgreso(false)) {
			return;
		}

		if (!SysmanFunciones.validarVariableVacio(tipoMov)
				&& !SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.TERCERO.getName())) {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
			param.put(GeneralParameterEnum.NUMERO.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

			List<Registro> regAux = null;
			try {
				regAux = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL13456.getValue())
										.getUrl(),
								param));
			} catch (SystemException e1) {
				logger.error(e1.getMessage(), e1);
				JsfUtil.agregarMensajeError(e1.getMessage());
			}

			if ((regAux != null) && !regAux.isEmpty()) {
				if ("0".equals(regAux.get(0).getCampos().get("CONTEO").toString())) {
					JsfUtil.agregarMensajeError(idioma.getString("TB_TB1438"));
					return;
				} else {
					generarPacOpcionalAlGiro();
				}
			}
		}

		// </CODIGO_DESARROLLADO>
	}

	private void generarPacOpcionalAlGiro() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
			param.put(GeneralParameterEnum.NUMERO.getName(),
					registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()));
			if ("E".equals(clase)) {
				List<Registro> regAux = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL13457.getValue())
										.getUrl(),
								param));

				if (!regAux.isEmpty()) {
					configurarParametrosSalida(regAux.get(0));
				}
			} else if ("I".equals(clase)) {
				String respuesta = ejbContabilidadCuatro.crearPacDeIngresos(compania, Integer.parseInt(ano), tipoMov,
						new BigInteger(SysmanFunciones
								.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "").toString()),
						(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()), usuario);
				JsfUtil.agregarMensajeInformativo(respuesta);

			}
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException
				| NamingException | SystemException | ParseException e) {

			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void configurarParametrosSalida(Registro regAux) throws IllegalAccessException, InstantiationException,
			ClassNotFoundException, SQLException, NamingException, ParseException, SystemException {
		boolean respuesta = ejbContabilidadCuatro.generarPacProporcionalAlGiro(compania, Integer.parseInt(ano), tipoMov,
				new BigInteger(SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
						.toString()),
				Integer.parseInt(mes.length() == 1 ? ("0" + "" + mes) : mes),
				(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),
				new BigDecimal(SysmanFunciones.nvl(regAux.getCampos().get("TOTALGIRO"), "0.0").toString()), usuario);

		if (respuesta) {
			Double dblTotalGiro = Double
					.valueOf(SysmanFunciones.nvl(regAux.getCampos().get("TOTALGIRO"), "0").toString());
			String fechaInicial = "01/" + (mes.length() == 1 ? ("0" + mes) : mes) + "/" + ano;
			String fechaFinal = SysmanFunciones.convertirAFechaCadena(SysmanFunciones
					.ultimoDiaDate((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));

			Map<String, Object> parametrosSalida = new HashMap<>();
			parametrosSalida.put("numeroComprobante", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			parametrosSalida.put(NOMBRECOMPROBANTECONS, nombreComprobante);
			parametrosSalida.put("ano", ano);
			parametrosSalida.put(TIPOCOMPROBANTECONS, tipoMov);
			parametrosSalida.put(FECHACOMPROBANTE, registro.getCampos().get(GeneralParameterEnum.FECHA.getName()));
			parametrosSalida.put("totalGiro", dblTotalGiro);
			parametrosSalida.put("fechaInicial", SysmanFunciones.convertirAFecha(fechaInicial));
			parametrosSalida.put("fechaFinal", SysmanFunciones.convertirAFecha(fechaFinal));
			parametrosSalida.put(TERCEROCOMPROBANTE, registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
			parametrosSalida.put(SUCURSALCOMPROBANTE,
					registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
			SessionUtil.setFlash(parametrosSalida);
			SessionUtil.cargarModal(
					Integer.toString(GeneralCodigoFormaEnum.PEDIR_FECHAS_PAC_PROP_CONTROLADOR.getCodigo()), modulo);
		}

	}

	private String obtenerParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = sysmanUtil.consultarParametro(SessionUtil.getCompania(), nombreParametro,
					SessionUtil.getModulo(), new Date(), true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	/**
	 * Acciones que se ejecutan al oprimir el boton <b>Comp. Presup. Ingresos</b>.
	 */
	public void oprimirCompCruce() {
		// <CODIGO_DESARROLLADO>
		String fuenteRecurso = null;
		Object tipoComprobante = registro.getCampos().get(TIPO);
		Object comprobante = registro.getCampos().get(GeneralParameterEnum.NUMERO.getName());
		Map<String, Object> params = new HashMap<>();
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.ANO.getName(), ano);
		params.put(GeneralParameterEnum.TIPO_CPTE.getName(), tipoComprobante);
		params.put(GeneralParameterEnum.COMPROBANTE.getName(), comprobante);

		try {
			String urlEnumId = ComprobantecntsControladorUrlEnum.URL18259.getValue();
			String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

			List<Parameter> parameters = requestManager.getList(url, params);
			List<Registro> listEquivalenciasPptales = RegistroConverter.toListRegistro(parameters);

			if (listEquivalenciasPptales.isEmpty()) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3176"));
				return;

			} else if ("SI".equals(obtenerParametro("CONTROLA FUENTE DE RECURSO VARIOS EN COMPROBANTES COM", "NO"))) {

				for (Registro reg : listEquivalenciasPptales) {
					fuenteRecurso = reg.getCampos().get("FUENTE_RECURSO").toString();
					if ("99999999999999999999".equals(fuenteRecurso)) {

						JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4413"));
						return;
					}
				}
			} else {
				procesarListaEquivalencias(listEquivalenciasPptales);
			}		
			cargarRegistro();

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	// INICIO NOTAS DE AJUSTE

	public String enviardiannotas() {
		String url;
		String log = null;
		archivoDescarga = null;

		log = "|---------------         LOG DE LOGICA NOTAS DE AJUSTE DOC SOPORTE        ---------------|";

		Map<String, Object> param = new TreeMap<>();

		Date fechaSolicitud = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ComprobantecntsControladorEnum.NITCOMPANIA.getValue(), nitSinDigito);
		param.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaSolicitud));
		param.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaSolicitud));
		param.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
		try {

			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

			List<Registro> listaFactSysman = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1895023.getValue())
											.getUrl(),
									param));

			if (listaFactSysman.size() > 1) {
				Registro regtemp = new Registro();
				for (Registro r : listaFactSysman) {
					regtemp = r;
				}
				listaFactSysman.clear();
				listaFactSysman.add(regtemp);
			}

			if (!listaFactSysman.isEmpty()) {

				for (Registro reg : listaFactSysman) {

					String respuesta;
					APIFrida apiFrida = new APIFrida();

					respuesta = apiFrida.cargarTercero(nitSinDigito, reg.getCampos().get("NUMTERCERO").toString(), url);

					Gson gson = new Gson();
					RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);

					if (respuestaApi.getCodigo() != 0) {
						String respuestaTercero = crearTecero(reg.getCampos().get("NUMTERCERO").toString(), url);
						if(respuestaTercero.contains("NO_OK")) {
							respuestaTercero = respuestaTercero.replaceAll("NO_OK", "");
							log = log + "\n proceso de envio cancelado \n " + respuestaTercero;
							return log;
						}
						log = log + "\n" + respuestaTercero;
					} else {
						log = log + "\n" + actualizaTecero(reg.getCampos().get("NUMTERCERO").toString(), url);
					}

					if (reg.getCampos().get("CODIGOPRODUCTODIAN") != null) {
						validarProducto(url, reg.getCampos().get("CODIGOPRODUCTODIAN").toString());
					}
				}

				validarRango(url);

				// Verificacion Factura
				listaFactSysman = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL1895024.getValue())
										.getUrl(),
								param));

				for (Registro reg : listaFactSysman) {

					String numeroFactura = (reg.getCampos().get("COMPROBANTE").toString());
					boolean facExiste = false;
					String tipoFormato = "06";
					String respuesta;
					APIFrida api = new APIFrida();

					respuesta = api.cargarEnvioFacatura(nitSinDigito, url);

					Gson gson = new Gson();
					RespuestaEnvioFactura respuestaApi = gson.fromJson(respuesta, RespuestaEnvioFactura.class);

					for (int i = 0; i < (respuestaApi.getCuerpo()).size(); i++) {
						facExiste = false;
						List<Object> datos = (List<Object>) ((List<Object>) respuestaApi.getCuerpo()).get(i);

						if (numeroFactura != null && numeroFactura.equals(
								new DecimalFormat("#.####################################").format(datos.get(0)))
								&& prefijo.equals(datos.get(1))) {
							facExiste = true;
						}

						// Borrar Factura
						if (facExiste) {
							Registro rs = RegistroConverter.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL9457.getValue())
											.getUrl(),
									null));

							if (rs != null) {

								File archivo = new File(rs.getCampos().get("RUTA_CERTIFICADO").toString());

								String nombreCertificado = archivo.getName();

								byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

								String certificado = Base64.getEncoder().encodeToString(archivoBytes);

								String passCertificado = Base64.getEncoder()
										.encodeToString(rs.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());

								ParametroDeleteEnvioFactura paramDelete = new ParametroDeleteEnvioFactura();

								paramDelete.setTipoFormato(tipoFormato);
								paramDelete.setNumFormato(numeroFactura);
								paramDelete.setPrefijo(prefijo);
								paramDelete.setCertificado(certificado);
								paramDelete.setNombreCertificado(nombreCertificado);
								paramDelete.setPassCertificado(passCertificado);
								paramDelete.setNumDocumentoContribuyente(nitSinDigito);

								Gson gson2 = new Gson();
								String json = gson2.toJson(paramDelete, ParametroDeleteEnvioFactura.class);

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
// VerificacionFactura
				log = log + "\n" + exportarNotas(url, retornarString(registro, "NUMERO"), tipoMov);

				log = log + "\n" + oprimirEnviarNotasDian(url, retornarString(registro, "NUMERO"), consecutivoDian);

			} else {
				log = log + "\n"
						+ "Por favor verifique la configuracion del concepto cuds en la imputacion contable del documento. "
						+ registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
			}

//			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
//			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogIndEnviarDian.txt");
//
//			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException | IOException | com.sysman.util.SysmanException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return log;
	}

	
	private String exportarNotasFacExterno(String comprobante, String tipoCpte) {
		Documento documento = new Documento();
		String respuesta = "";
		Registro reg1 = null;
		try {
			ApiInvoway apiInvoway = new ApiInvoway();
			// se consulta la url configurada en el parametro
			String url = SysmanFunciones
					.nvlStr(ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_SOAP, "69", new Date(), false), "");

			// se consulta los parametros para traer el usuario y la contrasea del ws
			// invoway
			String user = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					USUARIO_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false), "");

			String pass = SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, CLAVE_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false),
					"");
			if (url == null) {
				JsfUtil.agregarMensajeError("Por favor configure la url para el envio.");
				return "Por favor configure la url para el envio.";
			}
			// informacion datos nota ajuste
			Map<String, Object> param1 = new TreeMap<>();
			String nitCompania = SessionUtil.getCompaniaIngreso().getNit();

			Date fechaSolicitud = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
			SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatFecha1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        param1.put(GeneralParameterEnum.NITCOMPANIA.getName(), nitCompania);
	        param1.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param1.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaSolicitud));
			param1.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaSolicitud));
			param1.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(), comprobante);
			param1.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoCpte);

			reg1 = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1895026.getValue())
											.getUrl(),
									param1));
			if (reg1 == null) {
				return	"No se ha encontrado toda la información necesaria, asegurese de haber configurado correctamente el comprobante.";
			}
			String tipoMedioPago = SysmanFunciones.nvl(reg1.getCampos().get("MEDIOPAGO").toString(),"10");
			String tipoPago = (reg1.getCampos().get("TIPO_PAGO").toString());
			String tipoMoneda = (reg1.getCampos().get("TIPO_MONEDA").toString());
			String facturadocsoporte = (reg1.getCampos().get("CONSECUTIVO_DIAN_AFECT").toString()); // NUMERO DE FAC DOC DIAN
			String prefijodse = (reg1.getCampos().get("PREFIJO_DIAN_AFECT").toString());
			String descripcion = SysmanFunciones.nvl(reg1.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()),"HONORARIOS").toString();
//			codigoNotaAjuste = (reg1.getCampos().get("CODIGO_NOTA").toString());
			
			
			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put("NUMEROFACTURA", comprobante);
			param.put(ComprobantecntsControladorEnum.TIPOCOMPROBANTE.getValue(), tipoCpte);

			// informacion de la nota de lo valores de la nota
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1895027.getValue())
											.getUrl(),
									param));

			if (rs != null) {
				Map<String, Object> param2 = new TreeMap<>();
				param2.put(GeneralParameterEnum.CODIGO.getName(),
						rs.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()));

				Date fechaFactura = (Date) rs.getCampos().get("FECHAFACTURA");

				prefijo = SysmanFunciones.nvl(registro.getCampos().get("PREFIJODIAN"), rs.getCampos().get("PREFIJODIAN")).toString();
				consecutivoDian = SysmanFunciones.nvl(registro.getCampos().get("CONSECUTIVODIAN"), rs.getCampos().get("CONSECUTIVODIAN")).toString();
				
				documento.setNumeroDocumento(prefijo + consecutivoDian);
				documento.setTipoDocumento("NA");
				documento.setSubtipoDocumento("95");
				documento.setTipoOperacion("10");//20
				documento.setDivisa(SysmanFunciones.nvl(tipoMoneda, "").toString().equals("602") ? "COP" : "");
				documento.setFechaDocumento(SysmanFunciones.nvl(formatFecha1.format(fechaFactura), "").toString());

				documento.setRefPedido(""); /// A que hace referencia
				documento.setDireccionFactura(
						SysmanFunciones.nvl(rs.getCampos().get("DIRECCIONCOMPANIA"), "").toString());
				documento.setDistritoFactura(SysmanFunciones.nvl(rs.getCampos().get("DEPTOCOMPANIA"), "").toString());
				documento.setCiudadFactura(SysmanFunciones.nvl(rs.getCampos().get("DEPTOCOMPANIA"), "").toString()
						+ SysmanFunciones.nvl(rs.getCampos().get("CIUDADCOMPANIA"), "").toString());
				documento.setDepartamentoFactura(
						SysmanFunciones.nvl(rs.getCampos().get("DEPTOCOMPANIA"), "").toString());
				documento.setPaisFactura("CO");
				documento.setMotivoRect("5");
				documento.setFechaIniFacturacion(SysmanFunciones.nvl(formatFecha1.format(fechaFactura), "").toString());
				documento.setFechaFinFacturacion(SysmanFunciones.nvl(formatFecha1.format(fechaFactura), "").toString());
				// datos de proveedor
				Proveedor p = new Proveedor();
				p.setIdProveedor(nitCompania+"-"+SysmanFunciones
                        .nvl(rs.getCampos().get("DIGITOVERIFICACIONCOMPANIA"), "").toString());
		        documento.setProveedor(p);

				// Fin Proveedor
				String tipoPersona = SysmanFunciones.nvl(rs.getCampos().get("NATURALEZATERCERO"), "").toString();

				// se crean datos del cliente
				Cliente c = new Cliente();
				c.setIdCliente(SysmanFunciones.nvl(rs.getCampos().get("NUMTERCERO"), "").toString() + "-"
						+ SysmanFunciones.nvl(rs.getCampos().get("DIGITOVERIFICACIONTERCERO"), "").toString());
				c.setTipoDocumentoIdCliente("31");
				c.setRazonSocialCliente(SysmanFunciones.nvl(rs.getCampos().get("RAZONSOCIAL"), "").toString());
				c.setNombreCliente(tipoPersona.equals("N")
						? SysmanFunciones.nvl(rs.getCampos().get("NOMBRETERCERO"), "").toString()
						: SysmanFunciones.nvl(rs.getCampos().get("RAZONSOCIAL"), "").toString());
				c.setApellido1Cliente(tipoPersona.equals("N")
					    ? SysmanFunciones.nvl(rs.getCampos().get("APELLIDO1TERCERO"), "").toString()
					    : "");
					c.setApellido2Cliente(tipoPersona.equals("N")
					    ? SysmanFunciones.nvl(rs.getCampos().get("APELLIDO2TERCERO"), "").toString()
					    : "");
				c.setTipoPersonaCliente(tipoPersona.equals("N") ? "2" : "1");
				c.setDireccionCliente(SysmanFunciones.nvl(rs.getCampos().get("DIRECCIONTERCERO"), "").toString());
				
				String dptoTercero = SysmanFunciones.nvl(rs.getCampos().get("DEPTOTERCERO"), "").toString();
				
				c.setDistritoCliente("");
				c.setCiudadCliente(SysmanFunciones.nvl(rs.getCampos().get("DEPTOTERCERO"), "").toString()
						+ SysmanFunciones.nvl(rs.getCampos().get("CIUDADTERCERO"), "").toString());
				c.setDepartamentoCliente(dptoTercero);
				c.setCodigoPostalCliente(SysmanFunciones.nvl(rs.getCampos().get("CODIGOPOSTALTERCERO"), "").toString());
				c.setPaisCliente("CO"); 
				c.setTelefonoCliente(SysmanFunciones.nvl(rs.getCampos().get("TELEFONOS"), "").toString());
				c.setEmailCliente(SysmanFunciones.nvl(rs.getCampos().get("EMAILTECERO"), "").toString());
				c.setRegimenCliente("49");
				documento.setCliente(c);
				// fin cliente

				// factura de referencia
				String anio = String.valueOf(SysmanFunciones.ano(new Date()));
				/*List<InfoEstadosFactura> respuestaConsulta = apiInvoway.consultarFactura(url,
						SysmanFunciones.nvl(prefijodse, "").toString() + facturadocsoporte, anio, "DE",
						nitSinDigito, pass, user);
				if (respuestaConsulta == null || respuestaConsulta.isEmpty()) {
					return "La factura relacionada a la nota no ha sido legalizada ante la dian";
				}*/
				DocumentosReferenciados documentosReferenciados = new DocumentosReferenciados();
				DocumentoReferenciado documentoAfectado = new DocumentoReferenciado();
				documentoAfectado
						.setFechaDocumentoRef((SysmanFunciones.nvl(formatFecha1.format(fechaFactura), "").toString()));
				documentoAfectado.setNumDocumentoRef(
						SysmanFunciones.nvl(prefijodse + facturadocsoporte,"").toString());
				//documentoAfectado.setUuidDocumentoRef(respuestaConsulta.get(0).getUUID());
				//documentoAfectado.setUuidDocumentoRef("2cebc12172cc765d627502722a61ec4835e9f5e69fbfa15415241a766d5e0c4ed9621f57899943fb207f09aa1aba5ce4");
				documentosReferenciados.getDocumentoReferenciado().add(documentoAfectado);
				documento.setDocumentosReferenciados(documentosReferenciados);			

				Date fechaIExpedicionC;
				Date fechaFExpedicionC;
				Map<String, Object> param4 = new TreeMap<>();

				// fechaFExpedicion = rs.getCampos().get("FECHAFACTURA").toString();

				fechaIExpedicionC = fechaFactura;// SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fechaIExpedicionC);
				c1.add(Calendar.DATE, -1);
				fechaIExpedicionC = c1.getTime();

				fechaFExpedicionC = fechaFactura;// SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
				Calendar c2 = Calendar.getInstance();
				c1.setTime(fechaFExpedicionC);
				fechaFExpedicionC = c2.getTime();

				param4.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param4.put("FECHAINICIAL", fechaIExpedicionC);
				param4.put("FECHAFINAL", fechaFExpedicionC);
				param4.put("NUMEROFACTURA", comprobante);
				param4.put("TIPONOTA", (rs.getCampos().get("TIPO").toString()));

				// informacion de los valores desde la NOTA
				Registro rs4 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL1895022.getValue())
										.getUrl(),
								param4));

				// gestion retenciones
//				BigDecimal totalRetenciones = new BigDecimal("0");
//				Retenciones retencionesFin = new Retenciones();
//				Retencion retencion = new Retencion();
//				retencion.setBaseRetencion(new BigDecimal(0));
//				retencion.setPorcRetencion(new BigDecimal(0));
//				retencion.setValorRetencion(new BigDecimal(0));
//				retencion.setCodRetencion("05");
//				retencionesFin.getRetencion().add(retencion);
//				totalRetenciones = totalRetenciones.add(retencion.getValorRetencion());

				// gestios de impuestos
				BigDecimal totalImpuestos = new BigDecimal("0");
				Impuestos impuestosFin = new Impuestos();

				if (!rs4.getCampos().get("BASEGRAVABLEIVA").toString().equals("0")
						&& !rs4.getCampos().get("IVA").toString().equals("0")) {
					Impuesto impuesto = new Impuesto();
					impuesto.setBaseImpuesto(
							new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));
					impuesto.setPorcImpuesto(
							new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("19"), "0").toString()));
					impuesto.setValorImpuesto(
							new BigDecimal(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));
					impuesto.setCodImpuesto("01");
					impuestosFin.getImpuesto().add(impuesto);
					totalImpuestos = totalImpuestos.add(impuesto.getValorImpuesto());
				}
				Lineas lineas = new Lineas();
				int cont = 1;
				double sumProdPorFactura = 0;
				Linea linea = new Linea();
				linea.setNumLinea(cont);
				linea.setIdEstandarReferencia("00" + cont);
				linea.setDescripcionItem(descripcion);
				linea.setUnidadMedida("NIU");
				linea.setUnidadesLinea(new BigDecimal(1));
				linea.setPrecioUnidad(
						new BigDecimal(SysmanFunciones.nvl(rs.getCampos().get("VALORUNITARIO"), "0").toString()));
				linea.setSubtotalLinea(
						new BigDecimal((SysmanFunciones.nvl(rs.getCampos().get("VALORUNITARIO"), "0").toString())));
				linea.setPorcDescuentoLinea(new BigDecimal(0));
				linea.setDescuentoLinea(new BigDecimal(0));
				linea.setTotalLinea(
						new BigDecimal((SysmanFunciones.nvl(rs.getCampos().get("VALORUNITARIO"), "0").toString())));
				// se suman los totales de los producctos
				sumProdPorFactura = sumProdPorFactura + linea.getTotalLinea().doubleValue();
				// se agrega impuesto principal IVA en caso de tener el impuesto
				 if (Double.parseDouble(SysmanFunciones.toString(
	                        SysmanFunciones.nvl(
	                                rs.getCampos().get("VALORIMPUESTO"),
	                                0))) > 0) {

	                	BigDecimal porcImpuesto = new BigDecimal(SysmanFunciones.toString(
	                		    SysmanFunciones.nvl(rs.getCampos().get("PORCENTAJEIVAX"), "0")));

	                		BigDecimal valorImpuesto = new BigDecimal(SysmanFunciones.toString(
	                		    SysmanFunciones.nvl(rs.getCampos().get("VALORIMPUESTO"), "0")));

	                		if (porcImpuesto.compareTo(BigDecimal.ZERO) > 0) {
	                		    linea.setCodImpuestoLinea("01");
	                		    linea.setPorcImpuestoLinea(porcImpuesto);
	                		    linea.setValorImpuestoLinea(valorImpuesto);
	                		}
	                }
				lineas.getLinea().add(linea);
				documento.setLineas(lineas);

				documento.setLineas(lineas);
				// Totales factura
//				TotalesCop totales = new TotalesCop();
//				totales.setValorPagarCop(SysmanFunciones.nvl(rs.getCampos().get("TOTALITEM"), "").toString());
//				totales.setSubtotalCop(SysmanFunciones.nvl(rs.getCampos().get("SUBTOTAL"), "").toString());
//				totales.setMonedaCop(
//						SysmanFunciones.nvl(rs.getCampos().get("TIPO_MONEDA"), "").toString().equals("602") ? "COP"
//								: "");
//
//				documento.setTotalesCop(totales);

				// Se agregan los datos totales
				DatosTotales totalesFact = new DatosTotales();

				BigDecimal subtotalLineas = linea.getTotalLinea();

				totalesFact.setSubtotal(subtotalLineas.setScale(2, RoundingMode.HALF_UP));
				totalesFact.setTotalBase(subtotalLineas.setScale(2, RoundingMode.HALF_UP));
				totalesFact.setTotalImpuestos(totalImpuestos.setScale(2, RoundingMode.HALF_UP));
				totalesFact.setTotalDocumento(subtotalLineas.add(totalImpuestos).setScale(2, RoundingMode.HALF_UP));
				totalesFact.setTotalRetenciones(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
				totalesFact.setAPagar(subtotalLineas.add(totalImpuestos).setScale(2, RoundingMode.HALF_UP));
				totalesFact.setTotalGastos(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));


				Date fechaVencimiento = (Date) rs.getCampos().get("FECHA_VENCIMIENTO");

				documento.setImpuestos(impuestosFin);

				documento.setDatosTotales(totalesFact);

				CondicionPago formaPago = new CondicionPago();				 
				formaPago.setMedioPago(SysmanFunciones.nvl(tipoMedioPago, "").toString());
				formaPago.setFormaPago(SysmanFunciones.nvl(tipoPago, "").toString());
				formaPago.setFechaPago(SysmanFunciones.nvl(formatFecha1.format(fechaVencimiento), "").toString());
				CondicionesPago condicionesFormaPago = new CondicionesPago();
				condicionesFormaPago.getCondicionPago().add(formaPago);
				documento.setCondicionesPago(condicionesFormaPago);
				
				/*Se deja estas lineas de codio para cuando sea necesario obtener el XML que se genera
		         * JAXBContext context = JAXBContext.newInstance(Documento.class);
		        Marshaller marshaller = context.createMarshaller();
		        StringWriter writer = new StringWriter();
		        marshaller.marshal(documento, writer);

		        String xml = writer.toString();
		        
		        archivoDescarga = JsfUtil.exportarXmlStreamed("XML_Invoway",
		        		xml);*/

				String xmlGenerado = documentoToXml(documento);
		        System.out.println("=== XML DOCUMENTO INVOWAY ===\n" + xmlGenerado);
		        
				respuesta = apiInvoway.postEnvioFactura(url, documento, pass, user);
				
				respuesta = respuesta + " Comprobante Número : " + comprobante 
	                    + " Consecutivo DIAN : " + consecutivoDian 
	                    + "\n";
				
				return respuesta;
			}else {
				JsfUtil.agregarMensajeError("No se ha encontrado toda la información necesaria, asegurese de haber configurado correctamente el comprobante.");
			}

		} catch (SystemException | IOException | com.sysman.util.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return "Error al generar el XML";
		}

		return respuesta;
	}

	
	private String exportarNotas(String url, String comprobante, String tipoCpte) {

		Registro reg1 = null;
		try {
			// informacion datos nota ajuste
			Map<String, Object> param1 = new TreeMap<>();

			Date fechaSolicitud = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
			SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
			param1.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaSolicitud));
			param1.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaSolicitud));
			param1.put(ComprobantecntsControladorEnum.COMPROBANTE.getValue(), comprobante);
			param1.put(ComprobantecntsControladorEnum.TIPOCOMPROBANTE.getValue(), tipoCpte);

			reg1 = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1895024.getValue())
											.getUrl(),
									param1));
		} catch (SystemException e1) {
			e1.printStackTrace();
		}

		String tipoMedioPago = (reg1.getCampos().get("TIPO_MEDIOPAGO").toString());
		String tipoPago = (reg1.getCampos().get("TIPO_PAGO").toString());
		String tipoMoneda = (reg1.getCampos().get("TIPO_MONEDA").toString());
		String tipoFacDIAN = (reg1.getCampos().get("TIPOFACDIAN").toString());
		String facturadocsoporte = (reg1.getCampos().get("NUMERO_FACTURA").toString()); // NUMERO DE FAC DOC DIAN
		String prefijodse = (reg1.getCampos().get("PREFIJO").toString());
		String descripcion = (reg1.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString());
		String log = null;
		int idFactura;
		String tipoForNota = "06";// tipo formato para nota
		String tipoFormato = "05";// tipo formato para doc soporte
		ParametrosEnvioFactura paramFactura = new ParametrosEnvioFactura();
		codigoNotaAjuste = (reg1.getCampos().get("CODIGO_NOTA").toString());

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("NUMEROFACTURA", comprobante);
		param.put(ComprobantecntsControladorEnum.TIPOCOMPROBANTE.getValue(), tipoCpte);

		try {
			// informacion de la nota de lo valores de la nota
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1895021.getValue())
											.getUrl(),
									param));

			if (rs != null) {

				Map<String, Object> param2 = new TreeMap<>();
				param2.put(GeneralParameterEnum.CODIGO.getName(),
						rs.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()));

				Registro rs2 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL9512.getValue())
										.getUrl(),
								param2));

				paramFactura.setCreatedBy(rs2.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());

				paramFactura.setNumerocontribuyente(nitSinDigito);

				String respuesta = null;
				APIFrida api = new APIFrida();

				respuesta = api.cargarEnvioFacatura(nitSinDigito, tipoFormato, facturadocsoporte, prefijodse, url);

				Gson gson = new Gson();
				ParametrosEnvioFacturaFiltros respuestaApi = gson.fromJson(respuesta,
						ParametrosEnvioFacturaFiltros.class);

				idFactura = respuestaApi.getCuerpo().getId();

				ParametroCuerpoEnvioFactura paramCuerpo = new ParametroCuerpoEnvioFactura();

				paramCuerpo.setTipoDeFactura(tipoForNota);
				paramCuerpo.setIdFactura(idFactura);
				paramCuerpo.setNumTercero(rs.getCampos().get("NUMTERCERO").toString());
				paramCuerpo.setFechafactura(SysmanFunciones.nvl(rs.getCampos().get("FECHAFACTURA"), "").toString());
				paramCuerpo.setFechaVencimiento(
						SysmanFunciones.nvl(rs.getCampos().get("FECHA_VENCIMIENTO"), "").toString());
				paramCuerpo.setNumerofactura(Integer.parseInt(comprobante));
				paramCuerpo.setTelefonoCliente(rs.getCampos().get("TELEFONOS").toString());
				paramCuerpo.setTipoPago(tipoPago);
				paramCuerpo.setMedioPago(tipoMedioPago);
				paramCuerpo.setTipoMoneda(tipoMoneda);
				paramCuerpo.setPrefijo("NA");
				paramCuerpo.setTipoOperacion(tipoFacDIAN);
				paramCuerpo.setObservacionesFactura(SysmanFunciones
						.nvl(rs.getCampos().get(GeneralParameterEnum.OBSERVACIONES.getName()), "").toString());
				paramCuerpo.setReviso(SysmanFunciones.nvl(
						ejbSysmanUtil.consultarParametro(compania, "SF NOMBRE REVISO EN FACTURA",
								SessionUtil.getModulo(), new Date(), false),
						rs.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString()).toString());

				// paramCuerpo.setcorreoEntrante("");
				paramCuerpo
						.setDatosSoftware(
								SysmanFunciones
										.nvl(ejbSysmanUtil.consultarParametro(compania,
												"DATOS SOFTWARE FACTURA ELECTRONICA", "69", new Date(), false), "")
										.toString());
				ParametrosItems paramItems = new ParametrosItems();

				paramItems.setCodigoproducto(rs.getCampos().get("CODIGOPRODUCTO").toString());

				paramItems.setCantidad(
						Double.parseDouble(rs.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString()));

				paramItems.setDescripcionproducto(descripcion);

				paramItems.setValorunitario(Double.parseDouble(SysmanFunciones.toString(rs.getCampos().get("VALORUNITARIO"))));

				paramItems.setTipoDescuento("05");

				paramItems.setDescuentoItem("0");

				paramItems.setTotalitem(Double.parseDouble(rs.getCampos().get("TOTALITEM").toString()));

				String fechaFExpedicion;
				Date fechaIExpedicionC;
				Date fechaFExpedicionC;
				Map<String, Object> param4 = new TreeMap<>();

				fechaFExpedicion = rs.getCampos().get("FECHAFACTURA").toString();

				fechaIExpedicionC = SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
				Calendar c = Calendar.getInstance();
				c.setTime(fechaIExpedicionC);
				c.add(Calendar.DATE, -1);
				fechaIExpedicionC = c.getTime();

				fechaFExpedicionC = SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fechaFExpedicionC);
				fechaFExpedicionC = c1.getTime();

				param4.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param4.put("FECHAINICIAL", fechaIExpedicionC);
				param4.put("FECHAFINAL", fechaFExpedicionC);
				param4.put("NUMEROFACTURA", comprobante);
				param4.put("TIPONOTA", (rs.getCampos().get("TIPO").toString()));

				// informacion de los valores desde la NOTA
				Registro rs4 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL1895022.getValue())
										.getUrl(),
								param4));

				List<ParametrosItemsImpuestos> listaParamItemImpuestos = new ArrayList<>();
				List<ParametrosImpuestos> listaParamImpuestosGnrl = new ArrayList<>();
				ParametrosImpuestos paramImpuesGnrl = new ParametrosImpuestos();
				ParametrosItems paramItemsSinImpesto = new ParametrosItems();
				// se traslada su declaracion
				List<ParametrosItems> listaParamItems = new ArrayList<>();
				// logica en el caso que el impuesto sea IVA.
				if (!rs4.getCampos().get("BASEGRAVABLEIVA").toString().equals("0")
						&& !rs4.getCampos().get("IVA").toString().equals("0")) {
					// se settean dentro del array impuestos en el detalle
					ParametrosItemsImpuestos paramItemImpuestos = new ParametrosItemsImpuestos();
					paramItemImpuestos.setTipo("01");
					paramItemImpuestos.setBase(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));
					paramItemImpuestos.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl("19", 0).toString()));
					paramItemImpuestos.setValor(
							Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));
					listaParamItemImpuestos.add(paramItemImpuestos);
					paramCuerpo.setPorcentajeIva(Double.parseDouble(SysmanFunciones.nvl("19", 0).toString()));
					// se settean en el general del detalle.
					paramItems.setValorunitario(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));
					Double totalItemDetalle = Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString())
							+ Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString());
					paramItems.setTotalitem(totalItemDetalle);
					// se settean los impuestos al array de impuestos de la factura en general

					paramImpuesGnrl.setBase(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));
					paramImpuesGnrl.setTipo("01");
					paramImpuesGnrl.setValor(
							Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));
					paramImpuesGnrl.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl("19", 0).toString()));
					listaParamImpuestosGnrl.add(paramImpuesGnrl);
					paramCuerpo.setImpuestos(listaParamImpuestosGnrl);

					double totalItemAjuste = Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("TOTAL"), "0").toString())
							- (paramImpuesGnrl.getBase() + paramImpuesGnrl.getValor());
					if (totalItemAjuste > 0) {
						// se foramtea el valor para que traiga solo dos decimales
						DecimalFormat df = new DecimalFormat("#");

						paramItemsSinImpesto.setCodigoproducto(rs.getCampos().get("CODIGOPRODUCTO").toString());

						paramItemsSinImpesto.setCantidad(1);

						paramItemsSinImpesto.setDescripcionproducto(descripcion);

						paramItemsSinImpesto.setValorunitario(Double.parseDouble(df.format(totalItemAjuste)));

						paramItemsSinImpesto.setTipoDescuento("05");

						paramItemsSinImpesto.setDescuentoItem("0");

						paramItemsSinImpesto.setTotalitem(Double.parseDouble(df.format(totalItemAjuste)));

						List<ParametrosItemsImpuestos> listaTempSinImpuesto = new ArrayList<>();
						paramItemsSinImpesto.setImpuestos(listaTempSinImpuesto);

						listaParamItems.add(paramItemsSinImpesto);

					}
				}

				ParametrosItemsImpuestos paramItemImpuestos2 = new ParametrosItemsImpuestos();
				paramItemImpuestos2.setTipo("06");

				paramItemImpuestos2.setBase(0);

				paramItemImpuestos2.setPorcentaje(0);

				paramItemImpuestos2.setValor(0);

				ParametrosItemsImpuestos paramItemImpuestos3 = new ParametrosItemsImpuestos();
				paramItemImpuestos3.setTipo("03");

				paramItemImpuestos3.setBase(0);

				paramItemImpuestos3.setPorcentaje(0);

				paramItemImpuestos3.setValor(0);

				ParametrosItemsImpuestos paramItemImpuestos4 = new ParametrosItemsImpuestos();
				paramItemImpuestos4.setTipo("05");

				paramItemImpuestos4.setBase(0);

				paramItemImpuestos4.setPorcentaje(0);

				paramItemImpuestos4.setValor(0);

				ParametrosItemsImpuestos paramItemImpuestos5 = new ParametrosItemsImpuestos();
				paramItemImpuestos5.setTipo("07");

				paramItemImpuestos5.setBase(0);

				paramItemImpuestos5.setPorcentaje(0);

				paramItemImpuestos5.setValor(0);

				ParametrosItemsImpuestos paramItemImpuestos6 = new ParametrosItemsImpuestos();
				paramItemImpuestos6.setTipo("02");

				paramItemImpuestos6.setBase(0);

				paramItemImpuestos6.setPorcentaje(0);

				paramItemImpuestos6.setValor(0);

				if (paramItemImpuestos2.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos2);
				}
				if (paramItemImpuestos3.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos3);
				}
				if (paramItemImpuestos4.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos4);
				}
				if (paramItemImpuestos5.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos5);
				}
				if (paramItemImpuestos6.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos6);
				}

				paramItems.setImpuestos(listaParamItemImpuestos);

				listaParamItems.add(paramItems);

				paramCuerpo.setItems(listaParamItems);

				paramCuerpo.setSubtotalfactura(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("SUBTOTALFACTURA"), "0").toString()));

				paramCuerpo.setValorfactura(
						Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("TOTAL"), "0").toString()));
				paramCuerpo.setReteFuente(0);
				paramCuerpo.setReteIva(0);
				paramCuerpo.setTotalBaseGravableIca(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEICA"), "0").toString()));
				paramCuerpo.setTotalBaseGravableInc(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEINC"), "0").toString()));
				paramCuerpo.setTotalBaseGravableIva(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEIVA"), "0").toString()));
				paramCuerpo.setTotalBaseGravableRete(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETE"), "0").toString()));
				paramCuerpo.setTotalBaseGravableIca(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEICA"), "0").toString()));
				paramCuerpo.setTotalBaseGravableReteiva(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEIVA"), "0").toString()));
				paramCuerpo.setTotalBaseImponible(
						Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));

				paramCuerpo.setValorIvaFactura(
						Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));

				paramCuerpo.setValorIcaFactura(0);
				paramCuerpo.setNumeroConceptos(listaParamItems.size());
				paramCuerpo.setReteIca(0);
				paramCuerpo.setValorIncFactura(0);
				paramCuerpo.setDescuentoItems(0);
				paramCuerpo.setDescuentoFactura(0);
				paramCuerpo.setDescripcion(descripcion);

				List<ParametroCuerpoEnvioFactura> listaParamCuerpo = new ArrayList<>();

				listaParamCuerpo.add(paramCuerpo);

				paramFactura.setFacturas(listaParamCuerpo);

				APIFrida api2 = new APIFrida();

				Gson gson2 = new Gson();

				String json = gson2.toJson(paramFactura, ParametrosEnvioFactura.class);
				if (json.toString().contains(ComprobantecntsControladorEnum.IMPUESTOIVA.getValue())) {
					json = json.toString().replace(ComprobantecntsControladorEnum.IMPUESTOIVA.getValue(), " ");
				}
				if (json.toString().contains(ComprobantecntsControladorEnum.VALORIMPUESTOIVA.getValue())) {
					json = json.toString().replace(ComprobantecntsControladorEnum.VALORIMPUESTOIVA.getValue(), " ");
				}
				if (json.toString().contains(ComprobantecntsControladorEnum.IMPUESTOICA.getValue())) {
					json = json.toString().replace(ComprobantecntsControladorEnum.IMPUESTOICA.getValue(), " ");
				}
				if (json.toString().contains(ComprobantecntsControladorEnum.VALORIMPUESTOICA.getValue())) {
					json = json.toString().replace(ComprobantecntsControladorEnum.VALORIMPUESTOICA.getValue(), " ");
				}
				if (json.toString().contains(ComprobantecntsControladorEnum.IMPUESTOIMPOCONSUMO.getValue())) {
					json = json.toString().replace(ComprobantecntsControladorEnum.IMPUESTOIMPOCONSUMO.getValue(), " ");
				}
				if (json.toString().contains(ComprobantecntsControladorEnum.VALORIMPUESTOIMPOCONSUMO.getValue())) {
					json = json.toString().replace(ComprobantecntsControladorEnum.VALORIMPUESTOIMPOCONSUMO.getValue(),
							" ");
				}
				if (json.toString().contains(ComprobantecntsControladorEnum.BASEGRAVAVLEDETALLE.getValue())) {
					json = json.toString().replace(ComprobantecntsControladorEnum.BASEGRAVAVLEDETALLE.getValue(), " ");
				}

				log = api2.postEnvioFactura(url, json);

				RespuestaApi respuestaApi2 = gson2.fromJson(respuesta, RespuestaApi.class);

				if (respuestaApi2.getCodigo() != 0) {
					log = respuestaApi2.getMensaje().toString();
				} else {
					log = log + "\n" + " LA NOTA DE AJUSTE DE DOC SOPORTE CON NUMERO " + comprobante
							+ " SE ENVIO CON EXITO " + json;
				}

				return log;

			}

		} catch (SystemException | IOException | ParseException | com.sysman.util.SysmanException e) {
			log = log + " " + e.getMessage();
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return log;
	}

	private String oprimirEnviarNotasDian(String url, String numFactura, String consecutivoDian) {

		archivoDescarga = null;

		String tipoformato;
		String codigoReporte = null;
		String prFormato;
		String log = "";
		StringBuilder pieCodigoBarra = new StringBuilder();
		StringBuilder pieTextoBarra = new StringBuilder();
		String codigoBarra;

		Date fechaVencimiento = (Date) registro.getCampos().get(ComprobantecntsControladorEnum.FECHA.getValue());
		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
			} else {
				prFormato = ejbSysmanUtil.consultarParametro(compania, "SF FORMATO FACTURACION ELECTRONICA",
						SessionUtil.getModulo(), new Date(), false);

				eliminarTabEstadoNotas();

				String respuesta;
				APIFrida api = new APIFrida();

				respuesta = api.cargarFormatoConsultarReporte(url, nitSinDigito, "10", "", "181",
						SysmanFunciones.convertirAFechaCadena(SysmanFunciones.convertirAFecha("18/05/2020"),
								"yyyy/MM/dd"),
						SysmanFunciones.convertirAFechaCadena(new Date(), "yyyy/MM/dd"), "0", "0", "0", "0", "1");

				Gson gson = new Gson();
				RespuestaConsultarReporte respuestaApi = gson.fromJson(respuesta, RespuestaConsultarReporte.class);

				for (RespuestaNotasReporte respuestaNotasReporte : respuestaApi.getCuerpo().getNotas()) {

					insertarNotas(respuestaNotasReporte);

				}
				// VERIFICA EL ESTADO DE NOTAS
				// pendiente validar que solo teome la nota del comprobante
				List<Registro> listaEstadoNotas = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														ComprobantecntsControladorUrlEnum.URL3562.getValue())
												.getUrl(),
										null));

				if (!listaEstadoNotas.isEmpty()) {

					for (Registro reg : listaEstadoNotas) {
						// validar tipo formato
						tipoformato = "06";

						// consumirEalvarez

						Map<String, Object> param2 = new TreeMap<>();

						param2.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));
						param2.put("PREFIJO", reg.getCampos().get(GeneralParameterEnum.CLASE.getName()));
						Registro rs = RegistroConverter.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL27351.getValue())
										.getUrl(),
								param2));

						if (rs != null) {

							codigoReporte = SysmanFunciones.nvl(rs.getCampos().get("FORMATO"), "30002195").toString();

						} else {
							codigoReporte = prFormato;
						}

						Registro rsCertificado = RegistroConverter
								.toRegistro(requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														ComprobantecntsControladorUrlEnum.URL9457.getValue())
												.getUrl(),
										null));

						if (rsCertificado != null) {

							File archivo = new File(rsCertificado.getCampos().get("RUTA_CERTIFICADO").toString());

							String nombreCertificado = archivo.getName();

							byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

							String certificado = Base64.getEncoder().encodeToString(archivoBytes);

							String passCertificado = Base64.getEncoder().encodeToString(
									rsCertificado.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());

							ParametrosLegalizarFactura paramLegalizar = new ParametrosLegalizarFactura();

							paramLegalizar.setTipoSalida(1);
							paramLegalizar.setTestSetId(rsCertificado.getCampos().get("TES_ID").toString());
							paramLegalizar.setCodigoReporte(codigoReporte);

							ParametrosFormato paramFormato = new ParametrosFormato();

							paramFormato.setTipoFormato(tipoformato);

							paramFormato.setNumFormato(reg.getCampos().get("NUMFORMATO").toString());

							paramFormato
									.setPrefijo(reg.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString());

							paramFormato.setNombreCertificado(nombreCertificado);

							paramFormato.setCertificado(certificado);

							paramFormato.setPassCertificado(passCertificado);

							paramFormato.setNumDocumentoContribuyente(nitSinDigito);

							paramFormato.setTipoAjusteNota(codigoNotaAjuste);

							// Adicion Codigo Barras

							boolean permiteCodigoBarra = false;

							permiteCodigoBarra = ("SI")
									.equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
											"SF PERMITE GENERAR CODIGO BARRA FACTURA ELECTRONICA",
											SessionUtil.getModulo(), new Date(), false), "NO"));

							paramFormato.setMuestraCodigoBarras(permiteCodigoBarra);
							if (permiteCodigoBarra) {

								String codigoEan;

								Map<String, Object> paramEan = new TreeMap<>();
								paramEan.put(GeneralParameterEnum.COMPANIA.getName(), compania);
								paramEan.put("ANO", ano);
								paramEan.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);
								Registro rsEan = RegistroConverter.toRegistro(requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														ComprobantecntsControladorUrlEnum.URL1717.getValue())
												.getUrl(),
										paramEan));

								codigoEan = rsEan.getCampos().get("CODIGOEAN").toString();

								pieCodigoBarra.append((char) 205);
								pieCodigoBarra.append((char) 102);
								pieCodigoBarra.append(415);
								pieCodigoBarra.append(codigoEan);
								pieCodigoBarra.append("8020");
								pieCodigoBarra.append(SysmanFunciones.padl(tipoFactRecaudo, 5, "0"));
								pieCodigoBarra.append(SysmanFunciones.padl(numFactura, 19, "0"));
								pieCodigoBarra.append((char) 102);
								pieCodigoBarra.append("3900");
								pieCodigoBarra.append(SysmanFunciones
										.padl(registro.getCampos().get("VALOR_TOTAL").toString(), 14, "0"));
								pieCodigoBarra.append((char) 102);
								pieCodigoBarra.append(96);
								pieCodigoBarra
										.append(SysmanFunciones.convertirAFechaCadena(fechaVencimiento, "yyyyMMdd"));

								codigoBarra = ejbCodigoBarras.imprimirCodigoBarras(pieCodigoBarra.toString());

								paramFormato.setCodigoBarras(codigoBarra);

								pieTextoBarra.append((char) 40);
								pieTextoBarra.append(415);
								pieTextoBarra.append((char) 41);
								pieTextoBarra.append(codigoEan);
								pieTextoBarra.append((char) 40);
								pieTextoBarra.append("8020");
								pieTextoBarra.append((char) 41);
								pieTextoBarra.append(SysmanFunciones.padl(tipoFactRecaudo, 5, "0"));
								pieTextoBarra.append(SysmanFunciones.padl(numFactura, 19, "0"));
								pieTextoBarra.append((char) 40);
								pieTextoBarra.append("3900");
								pieTextoBarra.append((char) 41);
								pieTextoBarra.append(SysmanFunciones
										.padl(registro.getCampos().get("VALOR_TOTAL").toString(), 14, "0"));
								pieTextoBarra.append((char) 40);
								pieTextoBarra.append(96);
								pieTextoBarra.append((char) 41);
								pieTextoBarra
										.append(SysmanFunciones.convertirAFechaCadena(fechaVencimiento, "yyyyMMdd"));

								paramFormato.setTextoBarras(pieTextoBarra.toString());
							}

							paramLegalizar.setParamFormato(paramFormato);

							// se consulta el parametro
							// con el fin de que se envie correo solo si tiene el paramentro en SI,
							// asginando como valor el 99

							String paramCorreoAuth = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
									"MANEJA ENVIO DE CORREO A TERCERO DOCUMENTO SOPORTE", SessionUtil.getModulo(),
									new Date(), false), "NO");
							if ("SI".equals(paramCorreoAuth)) {
								paramLegalizar.setTipoReporte(99);
							} else {
								paramLegalizar.setTipoReporte(0);
							}
							paramLegalizar.setPrefijo(paramFormato.getPrefijo());
							String respuestaLegalizar = null;
							APIFrida apiFrida = new APIFrida();

							Gson gson2 = new Gson();

							String json = gson2.toJson(paramLegalizar, ParametrosLegalizarFactura.class);

							respuestaLegalizar = apiFrida.postFormatoLegalizar(url, json);
							RespuestaApi respuestaApis = gson2.fromJson(respuestaLegalizar, RespuestaApi.class);
							// validar tipo formato
							if (respuestaApis != null && respuestaApis.getCodigo() != 0) {
								log = log + "\n" + respuestaApis.getMensaje() + " "
										+ " NOTA DE AJUSTE DOCUMENTO SOPORTE:"
										+ reg.getCampos().get("NUMFORMATO").toString()
										+ ". Proceso terminado satisfactoriamente ";
							} else if (respuestaApis != null && respuestaApis.getCodigo() == 0) {

								log = log + "\n"
										+ crearReportesFridaNotas(respuestaApis,
												reg.getCampos().get("NUMFORMATO").toString())
										+ "LA NOTA DE AJUSTE DE DOC SOPORTE CON NUMERO "
										+ reg.getCampos().get("NUMFORMATO").toString() + " ENVIADA";
							}
						}

					}
				}

			}

		} catch (SystemException | IOException | ParseException | com.sysman.util.SysmanException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return log;
	}

	private void eliminarTabEstadoNotas() {

		try {
			UrlBean urlDelete2 = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL1987.getValue());

			requestManager.delete(urlDelete2.getUrl(), null);
		} catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void insertarNotas(RespuestaNotasReporte respuestaNotasReporte) {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

		try {

			String urlEnumId2 = ComprobantecntsControladorUrlEnum.URL7456.getValue();

			Date fecha = formato.parse(respuestaNotasReporte.getFecha().replace("-", "/"));

			Map<String, Object> params2 = new TreeMap<>();

			params2.put(GeneralParameterEnum.CLASE.getName(), respuestaNotasReporte.getClase());
			params2.put("ESTADO", respuestaNotasReporte.getEstado());
			params2.put(GeneralParameterEnum.FECHA.getName(), fecha);
			params2.put("NUMFACTURA", respuestaNotasReporte.getNumFactura());
			params2.put("NUMFORMATO", respuestaNotasReporte.getNumFormato());
			params2.put(GeneralParameterEnum.OBSERVACION.getName(), respuestaNotasReporte.getObservacion());
			params2.put("TIPONOTA", respuestaNotasReporte.getTipoNota());
			params2.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			params2.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate2 = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId2);

			requestManager.save(urlCreate2.getUrl(), urlCreate2.getMetodo(), params2);

		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * @author mrosero 18/08/2023 Funciï¿½n creada para generar el zip y pdf de las
	 *         notas, debido a que el json de respuesta de la Dian,retorna en la
	 *         etiqueta "resultadoProcesoDian" un array de objetos que inicia con el
	 *         caracter [], lo cual se diferencia de la respuesta de las facturas
	 *         que inicia con el caracter {} y denota un objeto.
	 * @param respuestaLegalizar
	 * @param factura
	 * @return
	 */
	private String crearReportesFridaNotas(RespuestaApi respuestaLegalizar, String factura) {

		String log = "";

		OutputStream outZip = null;
		OutputStream outPdf = null;

		Gson gson = new Gson();

		RespuestaApi legalizar = respuestaLegalizar;
		Map<String, Object> param2 = (Map<String, Object>) legalizar.getCuerpo();
		String zip = param2.get("zip").toString(); // zip
		String pdf = null;
		if (param2.get("Reporte") != null) {
			pdf = param2.get("Reporte").toString();
		}

		Registro rsRutaArchivos;
		try {
			rsRutaArchivos = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL9457.getValue())
											.getUrl(),
									null));

			if (rsRutaArchivos != null) {

				String ruta = SysmanFunciones.nvl(rsRutaArchivos.getCampos().get("RUTA_FACTURAS"), "").toString();

				if (!ruta.isEmpty()) {

					File verificar = new File(ruta);
					if (!verificar.isDirectory()) {
						verificar.mkdirs();
					}

					if (zip != null) {

						byte[] archivoZip = Base64.getDecoder().decode(zip);

						outZip = new FileOutputStream(ruta + "/" + "NOTADOCSOPORTE" + factura + "_"
								+ SysmanFunciones.convertirAFechaCadena(new Date(), "YYYYMMDD_HHMMSS") + ".zip");
						outZip.write(archivoZip);
						outZip.close();

					}

					if (pdf != null) {
						byte[] archivoPdf = Base64.getDecoder().decode(pdf);

						outPdf = new FileOutputStream(ruta + "/" + "NOTADOCSOPORTE" + factura + "_"
								+ SysmanFunciones.convertirAFechaCadena(new Date(), "YYYYMMDD_HHMMSS") + ".pdf");
						outPdf.write(archivoPdf);
						outPdf.close();
					}
					// --- Lectura del tag de respuesta de la DIAN
					ArrayList param3 = (ArrayList) param2.get("resultadoProcesoDian");
					param2 = (Map<String, Object>) param3.get(0);
					if (param2.get("IsValid").toString().equals("false")) {// get de errores
						log = SysmanFunciones.nvl(param2.get("ErrorMessage"), "").toString();
					}

				} else {
					JsfUtil.agregarMensajeAlerta("Debe crear la ruta en donde se generaran los reportes");
				}

			}

		} catch (SystemException | ParseException | IOException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

		return log;

	}

	// FIN NOTAS DE AJUSTE

	/**
	 * metodo que se ejecuta al oprimir el evniar a dian en la pagina e iniciar con
	 * el envio del documento.
	 */
	public void oprimirenviardian() {

		// NOTAAJUSTE_CUDS
		Map<String, Object> paramr = new TreeMap<>();
		paramr.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		paramr.put(GeneralParameterEnum.TIPOCPTE.getName(), tipoMov);
		String log = null;
		
		// se consulta el parametro que define si se usa Facturador externo o FRIDA
        String facturadorExterno = "";
        try {
        	facturadorExterno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
        			MANEJA_FACTURACION_ELECTRONICA_EXTERNA, "69", new Date(), false),"NO");
        }catch (Exception e) {
        	facturadorExterno = "NO";
		}
		try {
			Registro rs1 = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL15077.getValue())
											.getUrl(),
									paramr));

			if (Integer.parseInt(SysmanFunciones.nvl(rs1.getCampos().get("EXISTE"), "").toString()) > 0) 
			{
				if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) 
				{
					log = enviardiannotas();
				}
				else
				{
					log = "|---------------         LOG DE LOGICA NOTAS DE AJUSTE DOC SOPORTE        ---------------|";
					log = log  +  "\n" + exportarNotasFacExterno(retornarString(registro, "NUMERO"), tipoMov);
				}

			} else if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) {

				String url;

				archivoDescarga = null;
				log = "|---------------         LOG DE LOGICA DOC SOPORTE         ---------------|";

				Map<String, Object> param = new TreeMap<>();
				/**
				 * @author ldiaz en este punto se manupula la hora de la fecha solicitud del
				 *         registro para que este no la lleve en ceros pues el systema requiere
				 *         que este entre 1 y 12
				 */
				Date fechaSolicitud = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
				SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(ComprobantecntsControladorEnum.NITCOMPANIA.getValue(), NitCompania);
				param.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaSolicitud));
				param.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaSolicitud));
				param.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(),
						registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
				try {

					url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);
					//url = "http://172.17.1.47:8040/sysman-erp-servicio-frida/servicio/";
					List<Registro> listaFactSysman = RegistroConverter
							.toListRegistro(requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1895002.getValue())
											.getUrl(),
									param));

					if (!listaFactSysman.isEmpty()) {

						for (Registro reg : listaFactSysman) {

							String respuesta;
							APIFrida apiFrida = new APIFrida();

							respuesta = apiFrida.cargarTercero(nitSinDigito,
									reg.getCampos().get("NUMTERCERO").toString(), url);

							Gson gson = new Gson();
							RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);

							if (respuestaApi.getCodigo() != 0) {
								String respuestaTercero = crearTecero(reg.getCampos().get("NUMTERCERO").toString(), url);
								if(respuestaTercero.contains("NO_OK")) {
									respuestaTercero = respuestaTercero.replaceAll("NO_OK", "");
									log = log + "\n proceso de envio cancelado \n " + respuestaTercero;
									ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
									archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnviarDian.txt");
									return;
								}
								log = log + "\n" + respuestaTercero;
							} else {
								log = log + "\n" + actualizaTecero(reg.getCampos().get("NUMTERCERO").toString(), url);
							}

							if (reg.getCampos().get("CODIGOPRODUCTODIAN") != null) {
								validarProducto(url, reg.getCampos().get("CODIGOPRODUCTODIAN").toString());
							}
						}

						validarRango(url);
						// aqui OK
						resolucionDian = SysmanFunciones.nvl(registro.getCampos().get("RESOLUCIONDIAN"), "").toString();
						prefijo = SysmanFunciones.nvl(registro.getCampos().get("PREFIJODIAN"), "").toString();
						consecutivoDian = SysmanFunciones.nvl(registro.getCampos().get("CONSECUTIVODIAN"), "")
								.toString();
						// se valida si el consecutivo esta en fecha y en el rango correspondiente a la
						// resulucion
						formatFecha = new SimpleDateFormat("dd/MM/yyyy");
						param.remove(GeneralParameterEnum.ANIO.getName());
						param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
						param.put("FECHARESO", formatFecha.format(new Date()));
						param.put(GeneralParameterEnum.NUMERO.getName(), resolucionDian);
						param.put("CONSECUTIVOUSAR", consecutivoDian);
						List<Registro> aux = RegistroConverter.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL1895015.getValue())
										.getUrl(),
								param));
						for (Registro aux1 : aux) {
							if (Integer.parseInt(
									SysmanFunciones.nvl(aux1.getCampos().get("EXISTE"), "").toString()) == 0) {
								JsfUtil.agregarMensajeError(
										"Por favor verifque la configuracion, pues su fecha o consecutivo no coinciden con la resuolucion");
								return;
							}
						}
						// Verificacion Factura

						for (Registro reg : listaFactSysman) {

							String numeroFactura = consecutivoDian;
							boolean facExiste = false;

							String tipoFormato;
							if ("NC".equals(prefijo))
								tipoFormato = "02";
							else {
								tipoFormato = "ND".equals(prefijo) ? "03" : "05";
							}

							String respuesta;
							APIFrida api = new APIFrida();

							respuesta = api.cargarEnvioFacatura(nitSinDigito, url);

							Gson gson = new Gson();
							RespuestaEnvioFactura respuestaApi = gson.fromJson(respuesta, RespuestaEnvioFactura.class);

							for (int i = 0; i < respuestaApi.getCuerpo().size(); i++) {
								facExiste = false;
								List<Object> datos = (List<Object>) respuestaApi.getCuerpo().get(i);

								if (numeroFactura != null && numeroFactura
										.equals(new DecimalFormat("#.####################################")
												.format(datos.get(0)))
										&& prefijo.equals(datos.get(1))) {
									facExiste = true;
								}

								// Borrar Factura
								if (facExiste) {
									Registro rs = RegistroConverter
											.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															ComprobantecntsControladorUrlEnum.URL9457.getValue())
													.getUrl(), null));

									if (rs != null) {

										File archivo = new File(rs.getCampos().get("RUTA_CERTIFICADO").toString());

										String nombreCertificado = archivo.getName();

										byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

										String certificado = Base64.getEncoder().encodeToString(archivoBytes);

										String passCertificado = Base64.getEncoder().encodeToString(
												rs.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());

										ParametroDeleteEnvioFactura paramDelete = new ParametroDeleteEnvioFactura();

										paramDelete.setTipoFormato(tipoFormato);
										paramDelete.setNumFormato(numeroFactura);
										paramDelete.setPrefijo(prefijo);
										paramDelete.setCertificado(certificado);
										paramDelete.setNombreCertificado(nombreCertificado);
										paramDelete.setPassCertificado(passCertificado);
										paramDelete.setNumDocumentoContribuyente(nitSinDigito);

										Gson gson2 = new Gson();
										String json = gson2.toJson(paramDelete, ParametroDeleteEnvioFactura.class);

										APIFrida apiFrida = new APIFrida();

										String val = apiFrida.deleteEnvioFactura(url, json);
										log = log + "\n" + val;

										if (val.startsWith("!")) {
											ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
											archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
													"LogEnviarDian.txt");

											JsfUtil.agregarMensajeInformativo(
													idioma.getString("MSM_PROCESO_EJECUTADO"));
											return;
										}

									}

								}
							}

						}
						// VerificacionFactura

						log = log + "\n" + exportarFacturas(url, retornarString(registro, "NUMERO"), tipoMov);

						log = log + "\n"
								+ envioDianIndividual(url, retornarString(registro, "NUMERO"), consecutivoDian);

					} else {
						log = log + "\n" + "DOC: "
								+ registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString() + "  -  "
								+ idioma.getString("MSM_DOCUMENTO_SIN_CUDS") + "\n";
					}


				} catch (SystemException | IOException | com.sysman.util.SysmanException | SysmanException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
			}
			else if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())) {
	    		try {	
					log = "|---------------         LOG DE LOGICA DOC SOPORTE         ---------------|";
	    			log = log  +  "\n" + exportarFacturadorExterno(retornarString(registro, "NUMERO"), tipoMov,retornarString(registro, "CONSECUTIVODIAN"));
		    		
		    		
				} catch (JAXBException e) {
					logger.error(e.getMessage(), e);
	                JsfUtil.agregarMensajeError(e.getMessage());
				}     		
	    	}
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnviarDian.txt"); 

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		} catch (SystemException | JRException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	/**
	 * permite recopilar la informacion para arcmar el JSON con la informacion de la
	 * documento soporte a enviar.
	 * 
	 * @param url
	 * @param numeroFactura
	 * @param tipoFactura
	 * @return
	 */
	private String exportarFacturas(String url, String numeroFactura, String tipoFactura) {
		String respuesta = null;
		String datosFacturaBancos;
		String strObservaciones;
		double valorIVAConcepto = 0;
		double valorBAseIVAConcepto = 0;
		double porcIVAConcepto = 0;
		double acumtotaldebitos = 0.0;
		double sumProdPorFactura = 0;
		double descuento = 0;
		double totalIvaFactura = 0;

		double redondeodescuento = 0;
		int digRedDian;

		int nConceptos = 0;

		ParametrosEnvioFactura paramFactura = new ParametrosEnvioFactura();

		ParametroCuerpoEnvioFactura paramCuerpoFactura = new ParametroCuerpoEnvioFactura();

		Map<String, Object> param = new TreeMap<>();
		/**
		 * @author ljdiaz en este punto se manupula la hora de la fecha solicitud del
		 *         registro para que este no la lleve en ceros pues el systema requiere
		 *         que este entre 1 y 12
		 */
		Date fechaSolicitud = (Date) registro.getCampos().get(ComprobantecntsControladorEnum.FECHA.getValue());
		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaSolicitud));
		param.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaSolicitud));
		param.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(), numeroFactura);
		param.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoFactura);
		param.put(GeneralParameterEnum.NITCOMPANIA.getName(), nitSinDigito);

		try {

			digRedDian = Integer.parseInt(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"SF NUMERO DIGITOS REDONDEO DIAN", SessionUtil.getModulo(), new Date(), false), "0").toString());
			// consula los datos de la factura para armar y consultar la informacion que
			// pertence al JSON final
			Registro rs2 = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL3564.getValue())
											.getUrl(),
									param));

			if (rs2 != null) {

				Map<String, Object> param2 = new TreeMap<>();
				param2.put(GeneralParameterEnum.CODIGO.getName(),
						rs2.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()));

				formatFecha = new SimpleDateFormat("yyyy-MM-dd");

				paramFactura.setCreatedBy(rs2.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString());
				paramFactura.setNumerocontribuyente(nitSinDigito);

				datosFacturaBancos = ejbSysmanUtil.consultarParametro(compania, "DATOS FACTURA BANCOS",
						SessionUtil.getModulo(), new Date(), false);

				strObservaciones = SysmanFunciones
						.nvl(rs2.getCampos().get(GeneralParameterEnum.OBSERVACIONES.getName()), "").toString();
				// el tipo de factura determina que es un documento soporte que se va a enviar
				paramCuerpoFactura.setTipoDeFactura("05");

				paramCuerpoFactura.setNumTercero(SysmanFunciones.nvl(rs2.getCampos().get("NUMTERCERO"), "").toString());

				paramCuerpoFactura.setFechafactura(
						SysmanFunciones.nvl(formatFecha.format(rs2.getCampos().get("FECHAFACTURA")), "").toString());

				paramCuerpoFactura.setFechaVencimiento(SysmanFunciones
						.nvl(formatFecha.format(rs2.getCampos().get("FECHA_VENCIMIENTO")), "").toString());

				paramCuerpoFactura.setNumerofactura(Integer.parseInt(consecutivoDian));

				paramCuerpoFactura
						.setTelefonoCliente(SysmanFunciones.nvl(rs2.getCampos().get("TELEFONOS"), "").toString());

				paramCuerpoFactura.setTipoPago(SysmanFunciones.nvl(rs2.getCampos().get("TIPO_PAGO"), "").toString());

				paramCuerpoFactura.setMedioPago(SysmanFunciones.nvl(rs2.getCampos().get("MEDIOPAGO"), "").toString());

				paramCuerpoFactura
						.setTipoMoneda(SysmanFunciones.nvl(rs2.getCampos().get("TIPO_MONEDA"), "").toString());

				paramCuerpoFactura.setPrefijo(SysmanFunciones.nvl(prefijo, "").toString());

				paramCuerpoFactura
						.setTipoOperacion(SysmanFunciones.nvl(rs2.getCampos().get("TIPOFACDIAN"), "").toString());

				paramCuerpoFactura.setObservacionesFactura(strObservaciones + "<br/>" + datosFacturaBancos);

				paramCuerpoFactura.setReviso(SysmanFunciones.nvl(
						ejbSysmanUtil.consultarParametro(compania, "SF NOMBRE REVISO EN FACTURA",
								SessionUtil.getModulo(), new Date(), false),
						rs2.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString()).toString());

				paramCuerpoFactura
						.setDatosSoftware(
								SysmanFunciones
										.nvl(ejbSysmanUtil.consultarParametro(compania,
												"DATOS SOFTWARE FACTURA ELECTRONICA", "69", new Date(), false), "")
										.toString());

				// Productos

				List<ParametrosItems> listaParamItems = new ArrayList<>();
				int contador = 0;
				Map<String, Object> param3 = new TreeMap<>();
				/**
				 * @author ldiaz en este punto se manupula la hora de la fecha solicitud del
				 *         registro para que este no la lleve en ceros pues el systema requiere
				 *         que este entre 1 y 12
				 */
				fechaSolicitud = (Date) registro.getCampos().get(ComprobantecntsControladorEnum.FECHA.getValue());
				formatFecha = new SimpleDateFormat("dd/MM/yyyy");

				param3.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param3.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaSolicitud));
				param3.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaSolicitud));
				param3.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(), numeroFactura);
				param3.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoFactura);
				param3.put(GeneralParameterEnum.NITCOMPANIA.getName(), nitSinDigito);

				List<Registro> listaProductos = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL2974.getValue())
										.getUrl(),
								param3));
				if (listaProductos.size() > 1) {
					
					Registro regtemp = new Registro();
					for (Registro r : listaProductos) {
						regtemp = r;
						acumtotaldebitos = acumtotaldebitos
								+ Double.parseDouble(SysmanFunciones.toString(SysmanFunciones
										.nvl(SysmanFunciones.toString(r.getCampos().get("VALORUNITARIO")).replace(",", "."), "0")));
					}
					regtemp.getCampos().put("VALORUNITARIO", acumtotaldebitos);
					regtemp.getCampos().put("TOTALITEM", acumtotaldebitos);
					regtemp.getCampos().put("SUBTOTAL", acumtotaldebitos);
					listaProductos.clear();
					listaProductos.add(regtemp);
				}
				
				
				if (!listaProductos.isEmpty()) {

					for (Registro reg3 : listaProductos) {

						valorIVAConcepto = SysmanFunciones.nvlDbl(reg3.getCampos().get("VALORUNITARIO"), 0)
								* (SysmanFunciones.nvlDbl(reg3.getCampos().get("PORCIVA"), 0) / 100);
						
						totalIvaFactura = totalIvaFactura + valorIVAConcepto;
						
						valorBAseIVAConcepto = SysmanFunciones.nvlDbl(reg3.getCampos().get("VALORUNITARIO"), 0);
						
						porcIVAConcepto = SysmanFunciones.nvlDbl(reg3.getCampos().get("PORCIVA"), 0);
						
						if (SysmanFunciones.nvlDbl(reg3.getCampos().get("VALOR_DESCUENTO"), 0) > 0) {
						sumProdPorFactura = sumProdPorFactura
								+ (SysmanFunciones
										.nvlDbl(SysmanFunciones.toString(reg3.getCampos().get("VALORUNITARIO")).replace(",", "."), 0)

										* SysmanFunciones.nvlDbl("1", 0))
								- SysmanFunciones.nvlDbl(reg3.getCampos().get("VALOR_DESCUENTO"), 0) + valorIVAConcepto;
						}

						ParametrosItems paramItems = new ParametrosItems();

						paramItems.setCodigoproducto(
								SysmanFunciones.nvl(reg3.getCampos().get("CODIGOPRODUCTO"), "").toString());
						/**
						 * @author ljdiaz
						 * @descrpcion: se cambia el tipo de dato que recibe la cantidad de el producto,
						 *              para que esta reciba cantidades decimales y entenera.
						 */
						paramItems.setCantidad(Double.parseDouble("1"));

						paramItems.setDescripcionproducto(
								SysmanFunciones.nvl(reg3.getCampos().get("DESCRIPCIONPRODUCTO"), "").toString());

						paramItems.setValorunitario(Double.parseDouble(SysmanFunciones.toString(SysmanFunciones
								.nvl(SysmanFunciones.toString(reg3.getCampos().get("VALORUNITARIO")).replace(",", "."), "0"))));

						paramItems.setTipoDescuento("05");

						paramItems.setDescuentoItem(SysmanFunciones.toString(
								SysmanFunciones.nvl(reg3.getCampos().get("VALOR_DESCUENTO"), "0")));

						paramItems.setTotalitem(Double.parseDouble(SysmanFunciones
								.nvl(reg3.getCampos().get("TOTALITEM").toString().replace(",", "."), "0").toString()));
						
						paramItems.setBaseGravable(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("VALORUNITARIO"), "0").toString()));
						
						paramItems.setValorIva(valorIVAConcepto);
						
						paramItems.setPorcentajeIva(porcIVAConcepto);

						List<ParametrosItemsImpuestos> listaParamItemImpuestos = new ArrayList<>();

						ParametrosItemsImpuestos paramItemImpuestos = new ParametrosItemsImpuestos();
					
						paramItemImpuestos.setTipo("01");
						
						if (valorIVAConcepto > 0) {
							paramItemImpuestos.setBase(Double.parseDouble(
									SysmanFunciones.nvl(reg3.getCampos().get("VALORUNITARIO"), "0").toString()));
						} else {
							paramItemImpuestos.setBase(0);
						}
						
						paramItemImpuestos.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("VALORUNITARIO"), "0").toString()) == 0 ? 0
										: Double.parseDouble(
												SysmanFunciones.nvl(reg3.getCampos().get("PORCIVA"), "0").toString()));

						paramItemImpuestos.setValor(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOIVA"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos2 = new ParametrosItemsImpuestos();
						paramItemImpuestos2.setTipo("06");

						paramItemImpuestos2.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEFUENTE"), "0").toString()));

						paramItemImpuestos2.setPorcentaje(Double.parseDouble(SysmanFunciones
								.nvl(reg3.getCampos().get("BASEIMPUESTORETEFUENTE"), "0").toString()) == 0 ? 0
										: Double.parseDouble(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJERETEFUENTEX"), "0").toString()));

						paramItemImpuestos2.setValor(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("VALOR_RETEFUENTE"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos3 = new ParametrosItemsImpuestos();
						paramItemImpuestos3.setTipo("03");

						paramItemImpuestos3.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOICA"), "0").toString()));

						paramItemImpuestos3.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOICA"), "0").toString()) == 0 ? 0
										: Double.parseDouble(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJEICAX"), "0").toString()));

						paramItemImpuestos3.setValor(Double
								.parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("VALOR_ICA"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos4 = new ParametrosItemsImpuestos();
						paramItemImpuestos4.setTipo("05");

						paramItemImpuestos4.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEIVA"), "0").toString()));

						paramItemImpuestos4.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEIVA"), "0").toString()) == 0
										? 0
										: Double.parseDouble(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJERETEIVADX"), "0").toString()));

						paramItemImpuestos4.setValor(Double
								.parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("RETEIVA"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos5 = new ParametrosItemsImpuestos();
						paramItemImpuestos5.setTipo("07");

						paramItemImpuestos5.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEICA"), "0").toString()));

						paramItemImpuestos5.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEICA"), "0").toString()) == 0
										? 0
										: Double.parseDouble(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJERETEICAX"), "0").toString()));

						paramItemImpuestos5.setValor(Double
								.parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("RETEICA"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos6 = new ParametrosItemsImpuestos();
						paramItemImpuestos6.setTipo("02");

						paramItemImpuestos6.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOINC"), "0").toString()));

						paramItemImpuestos6.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOINC"), "0").toString()) == 0 ? 0
										: Integer.parseInt(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJEINCX"), "0").toString()));

						paramItemImpuestos6.setValor(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("IMPUESTO_INC"), "0").toString()));

						if (paramItemImpuestos.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos);
						}
						if (paramItemImpuestos2.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos2);
						}
						if (paramItemImpuestos3.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos3);
						}
						if (paramItemImpuestos4.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos4);
						}
						if (paramItemImpuestos5.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos5);
						}
						if (paramItemImpuestos6.getBase() != 0.0) {
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
				 * @author ldiaz en este punto se manupula la hora de la fecha solicitud del
				 *         registro para que este no la lleve en ceros pues el systema requiere
				 *         que este entre 1 y 12
				 */
				fechaSolicitud = (Date) registro.getCampos().get(ComprobantecntsControladorEnum.FECHA.getValue());
				formatFecha = new SimpleDateFormat("dd/MM/yyyy");

				param4.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param4.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaSolicitud));
				param4.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaSolicitud));
				param4.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(), numeroFactura);
				param4.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoFactura);

				List<ParametrosCargos> listaParamCargos = new ArrayList<>();
				List<ParametrosDescuentos> listaParamDescuentos = new ArrayList<>();

				Registro rs4 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL4587.getValue())
										.getUrl(),
								param4));

				if (rs4 != null) {
					totalFactura = SysmanFunciones.nvlDbl(
							(SysmanFunciones.nvlDbl(rs4.getCampos().get(GeneralParameterEnum.TOTAL.getName()), 0)
									- (SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTO_FACTURA"), 0) / nConceptos)
									- descuento),
							0);

					if (sumProdPorFactura < totalFactura && (totalFactura - sumProdPorFactura) > 1 &&  sumProdPorFactura > 0) { //JM CC 2384

						ParametrosCargos paramCargos = new ParametrosCargos();

						paramCargos.setValor((int) SysmanFunciones.redondear(totalFactura - sumProdPorFactura, 2));

						listaParamCargos.add(paramCargos);

					} else if (sumProdPorFactura > totalFactura && (sumProdPorFactura - totalFactura) > 1) {

						redondeodescuento = SysmanFunciones.redondear(sumProdPorFactura - totalFactura, 2);

						ParametrosDescuentos paramDescuento = new ParametrosDescuentos();

						paramDescuento.setTipo("05");

						paramDescuento.setValor((int) redondeodescuento);

						listaParamDescuentos.add(paramDescuento);

					}
				}
				paramCuerpoFactura.setSubtotalfactura(totalFactura - totalIvaFactura); 

				paramCuerpoFactura.setValorfactura(totalFactura);

				paramCuerpoFactura.setNumeroConceptos(nConceptos);
				
				
				

				if (listaParamCargos.size() != 0)
					paramCuerpoFactura.setCargos(listaParamCargos);
				if (listaParamDescuentos.size() != 0)
					paramCuerpoFactura.setDescuentos(listaParamDescuentos);

				List<Registro> listaImpuestosTemp = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL7452.getValue())
										.getUrl(),
								param4));

				if (!listaImpuestosTemp.isEmpty()) {
					double descuentos = 0.0;
					for (Registro r : listaImpuestosTemp) {
						if (r.getCampos().get("TIPORETENCION").toString().equals("FUE")) {
							paramCuerpoFactura.setReteFuente(Double
									.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALOR"), "0").toString()));
							paramCuerpoFactura.setTotalBaseGravableRete(Double
									.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALORBASE"), "0").toString()));
						} else if (r.getCampos().get("TIPORETENCION").toString().equals("ICA")) {
							paramCuerpoFactura.setReteIca(Double
									.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALOR"), "0").toString()));
							paramCuerpoFactura.setTotalBaseGravableReteica(Double
									.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALORBASE"), "0").toString()));
						} //INI 7745457 MPEREZ
						  else if (r.getCampos().get("TIPORETENCION").toString().equals("IVA")) { 
							// INI 7743592 MPEREZ
							paramCuerpoFactura.setReteIva(Double
									.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALOR"), "0").toString()));
							paramCuerpoFactura.setTotalBaseGravableReteiva(Double
									.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALORBASE"), "0").toString()));
							// FIN 7743592 MPEREZ
						} //FIN 7745457 MPEREZ
						  else {							
							descuentos = descuentos + Double
									.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALOR"), "0").toString());
						}
					}
					paramCuerpoFactura.setDescuentoFactura(descuentos);
				}
				// se completa la informacion basica del documento a enviar. -ljdiaz
				rs4 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL1895010.getValue())
										.getUrl(),
								param4));

				if (rs4 != null) {
					paramCuerpoFactura.setSubtotalfactura(totalFactura - totalIvaFactura);
					paramCuerpoFactura.setTotalBaseGravableIva(totalFactura - totalIvaFactura);
					paramCuerpoFactura.setTotalBaseGravableInc(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLE"), "0").toString()));
					if (totalIvaFactura > 0) {
						paramCuerpoFactura.setTotalBaseImponible(totalFactura - totalIvaFactura);
					}else {
						paramCuerpoFactura.setTotalBaseImponible(Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));
					}
					
					
					paramCuerpoFactura.setValorIvaFactura(totalIvaFactura);
					paramCuerpoFactura.setDescuentoItems(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("DESCUENTOPARCIAL"), "0").toString()));
//					paramCuerpoFactura.setDescuentoFactura(Double.parseDouble(
//							SysmanFunciones.nvl(rs4.getCampos().get("DESCUENTO_FACTURA"), "0").toString()));
					paramCuerpoFactura
							.setDescripcion(SysmanFunciones.nvl(rs4.getCampos().get("OBSERVACIONES"), "0").toString());
					
					paramCuerpoFactura.setPorcentajeIva(porcIVAConcepto);
				}
				// fin
				List<ParametrosImpuestos> listaParamImpuestos = new ArrayList<>();
				// impuestos
				// PORCENTAJEIVAX
				Registro rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5768.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos = new ParametrosImpuestos();
				//valorIVAConcepto
				//porcIVAConcepto
				if (rs5 == null && (valorIVAConcepto + porcIVAConcepto ) == 0 ) {

					paramImpuestos.setTipo("01");
					paramImpuestos.setBase(0);
					paramImpuestos.setPorcentaje(0);
					paramImpuestos.setValor(0);

					// listaParamImpuestos.add(0, paramImpuestos);

				} else {
					
					if (rs5 != null) {
					paramImpuestos.setTipo("01");
					paramImpuestos.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTOIVA"), "0").toString()));

					paramImpuestos.setPorcentaje(
							Double.parseDouble(SysmanFunciones.nvl(rs5.getCampos().get("PORCIVA"), "0").toString()));

					paramImpuestos.setValor(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEVALORIMPUESTO"), "0").toString()));
					}else {
						paramImpuestos.setTipo("01");
						paramImpuestos.setBase(valorBAseIVAConcepto);

						paramImpuestos.setPorcentaje(porcIVAConcepto);

						paramImpuestos.setValor(valorIVAConcepto);
					}
					
					// listaParamImpuestos.add(0, paramImpuestos);
				}
				if (paramImpuestos.getBase() != 0) {
					listaParamImpuestos.add(0, paramImpuestos);
				}
				// PORCENTAJERETEFUENTEX

				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5769.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos2 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos2.setTipo("06");
					paramImpuestos2.setBase(0);
					paramImpuestos2.setPorcentaje(0);
					paramImpuestos2.setValor(0);

					// listaParamImpuestos.add(1, paramImpuestos2);

				} else {
					paramImpuestos2.setTipo("06");
					paramImpuestos2.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTORETEFUENTE"), "0").toString()));

					paramImpuestos2.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos2.setValor(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEVALOR_RETEFUENTE"), "0").toString()));

					// listaParamImpuestos.add(1, paramImpuestos2);
				}
				if (paramImpuestos2.getBase() != 0) {
					listaParamImpuestos.add(1, paramImpuestos2);
				}
				// PORCENTAJEICAX

				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5770.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos3 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos3.setTipo("03");
					paramImpuestos3.setBase(0);
					paramImpuestos3.setPorcentaje(0);
					paramImpuestos3.setValor(0);

					// listaParamImpuestos.add(2, paramImpuestos3);

				} else {
					paramImpuestos3.setTipo("03");
					paramImpuestos3.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTOICA"), "0").toString()));

					paramImpuestos3.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos3.setValor(Double
							.parseDouble(SysmanFunciones.nvl(rs5.getCampos().get("SUMADEVALOR_ICA"), "0").toString()));

					// listaParamImpuestos.add(2, paramImpuestos3);
				}
				if (paramImpuestos3.getBase() != 0) {
					listaParamImpuestos.add(2, paramImpuestos3);
				}
				// PORCENTAJERETEIVADX
				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5771.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos4 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos4.setTipo("05");
					paramImpuestos4.setBase(0);
					paramImpuestos4.setPorcentaje(0);
					paramImpuestos4.setValor(0);

					// listaParamImpuestos.add(3, paramImpuestos4);

				} else {
					paramImpuestos4.setTipo("05");
					paramImpuestos4.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTORETEIVA"), "0").toString()));

					paramImpuestos4.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos4.setValor(Double
							.parseDouble(SysmanFunciones.nvl(rs5.getCampos().get("SUMADERETEIVA"), "0").toString()));

					// listaParamImpuestos.add(3, paramImpuestos4);
				}
				if (paramImpuestos4.getBase() != 0) {
					listaParamImpuestos.add(3, paramImpuestos4);
				}
				// PORCENTAJERETEICAX

				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5772.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos5 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos5.setTipo("07");
					paramImpuestos5.setBase(0);
					paramImpuestos5.setPorcentaje(0);
					paramImpuestos5.setValor(0);

					// listaParamImpuestos.add(4, paramImpuestos5);

				} else {
					paramImpuestos5.setTipo("07");
					paramImpuestos5.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTORETEICA"), "0").toString()));

					paramImpuestos5.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos5.setValor(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEIMPUESTO_INC"), "0").toString()));

					// listaParamImpuestos.add(4, paramImpuestos5);
				}
				if (paramImpuestos5.getBase() != 0) {
					listaParamImpuestos.add(4, paramImpuestos5);
				}
				// PORCENTAJEINCX

				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5773.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos6 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos6.setTipo("02");
					paramImpuestos6.setBase(0);
					paramImpuestos6.setPorcentaje(0);
					paramImpuestos6.setValor(0);

					// listaParamImpuestos.add(5, paramImpuestos6);

				} else {
					paramImpuestos6.setTipo("02");
					paramImpuestos6.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTOINC"), "0").toString()));

					paramImpuestos6.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos6.setValor(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEIMPUESTO_INC"), "0").toString()));

					// listaParamImpuestos.add(5, paramImpuestos6);
				}
				if (paramImpuestos6.getBase() != 0) {
					listaParamImpuestos.add(5, paramImpuestos6);
				}
				paramCuerpoFactura.setImpuestos(listaParamImpuestos);
			}

			List<ParametroCuerpoEnvioFactura> listaCuerpoFactura = new ArrayList<>();
			listaCuerpoFactura.add(paramCuerpoFactura);

			paramFactura.setFacturas(listaCuerpoFactura);

			APIFrida api2 = new APIFrida();

			Gson gson2 = new Gson();
			String json = gson2.toJson(paramFactura, ParametrosEnvioFactura.class);

			respuesta = api2.postEnvioFactura(url, json);

			RespuestaApi respuestaApi = gson2.fromJson(respuesta, RespuestaApi.class);

			if (respuestaApi.getCodigo() != 0) {
				respuesta = "\n Error en el proceso creacion y envio JSON de la factura a FRIDA \n " + respuestaApi.getMensaje();
			}else {
				respuesta = "\n Documento almacenado con exito en FRIDA \n";
			}

		} catch (SystemException | IOException | com.sysman.util.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			respuesta = "Error en el proceso creacion y envio JSON de la factura a FRIDA";
		}

		return respuesta;

	}
	
	private String exportarFacturadorExterno(String numeroFactura, String tipoCobro, String consecutivoDian) throws JAXBException {
    	Documento documento = new Documento();
		String respuesta = "";
		try {
			// se consulta la url configurada en el parametro
			String url = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					URL_SERVICIO_SOAP, "69", new Date(), false),"");
			if(url == null) {
				JsfUtil.agregarMensajeError("Por favor configure la url para el envio.");
	            return "Por favor configure la url para el envio.";
			}
			
	        Map<String, Object> param = new TreeMap<>();
	        /**
	         * @author ldiaz
	         * en este punto se manupula la hora de la fecha solicitud del registro para que este no la lleve en ceros
	         * pues el systema requiere que este entre 1 y 12 
	         */
	        Date fechaSolicitud = (Date) registro.getCampos().get(ComprobantecntsControladorEnum.FECHA.getValue());
	        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
	        SimpleDateFormat formatFecha1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	                   
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(ComprobantecntsControladorEnum.FECHAINI.getValue(),formatFecha.format(fechaSolicitud));
	        param.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaSolicitud));
	        param.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(), numeroFactura);
	        param.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoCobro);
	        param.put(GeneralParameterEnum.NITCOMPANIA.getName(), nitSinDigito);
	                
	        Registro rs2 = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1895026.getValue())
											.getUrl(),
									param));
			
			if(rs2 != null) {
				//Datos basico fijos
				documento.setNumeroDocumento(SysmanFunciones
						.nvl(rs2.getCampos().get("PREFIJO"), "")
						.toString()+consecutivoDian);
				documento.setTipoDocumento("CC");
		        documento.setSubtipoDocumento("05");
		        documento.setTipoOperacion("10");
				documento.setFechaDocumento(SysmanFunciones
												.nvl(formatFecha1.format(rs2.getCampos().get("FECHAFACTURA")), "")
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
				documento.setDepartamentoFactura(SysmanFunciones
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
		        p.setIdProveedor(nitSinDigito+"-"+SysmanFunciones
                        .nvl(rs2.getCampos().get("DIGITOVERIFICACIONCOMPANIA"), "").toString());
		        documento.setProveedor(p);
		        // Fin Proveedor
		        String tipoPersona = SysmanFunciones
                        .nvl(rs2.getCampos().get("NATURALEZATERCERO"), "")
                        .toString();
		        
		        // se crean datos del cliente
		        Cliente c = new Cliente();
		        c.setTipoPersonaCliente(tipoPersona.equals("N")?"2":"1");
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
		        c.setPaisCliente("CO");
		        c.setIdCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("NUMTERCERO"), "").toString()+"-"+SysmanFunciones
                        .nvl(rs2.getCampos().get("DIGITOVERIFICACIONTERCERO"), "").toString());
				c.setTipoDocumentoIdCliente("31");
				c.setDistritoCliente(SysmanFunciones
                        .nvl(rs2.getCampos().get("DEPTOTERCERO"), "")
                        .toString());
				c.setDepartamentoCliente(SysmanFunciones
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
//		        BigDecimal totalImpuestos = new BigDecimal("0");
//		        Impuestos impuestosFin = new Impuestos();
//		        Registro impuestosGeneralesIva = RegistroConverter
//                        .toRegistro(requestManager.get(
//                                        UrlServiceUtil.getInstance()
//                                                        .getUrlServiceByUrlByEnumID(
//                                                        		ComprobantecntsControladorUrlEnum.URL5768
//                                                                                        .getValue())
//                                                        .getUrl(),
//                                        param));
//		        if (impuestosGeneralesIva != null)
//		        {
//		        	
//		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesIva.getCampos().get("SUMADEVALORIMPUESTO").toString(), 0).toString()) > 0) { 
//			        	Impuesto impuesto = new Impuesto();
//			        	impuesto.setBaseImpuesto(new BigDecimal(SysmanFunciones.nvl(impuestosGeneralesIva.getCampos().get("SUMADEBASEIMPUESTOIVA"),"0").toString())); 
//			        	impuesto.setPorcImpuesto(new BigDecimal(SysmanFunciones.nvl(
//			        			impuestosGeneralesIva.getCampos().get(
//                                        "PORCIVA"),
//                        "0").toString()));
//			        	impuesto.setValorImpuesto(new BigDecimal(SysmanFunciones.nvl(impuestosGeneralesIva.getCampos().get("SUMADEVALORIMPUESTO"),"0").toString())); // SUMADEVALORIMPUESTO
//			        	impuesto.setCodImpuesto("01");
//			        	impuestosFin.getImpuesto().add(impuesto);
//			        	totalImpuestos = totalImpuestos.add(impuesto.getValorImpuesto());
//		        	}
//		        }
//		        Registro impuestosGeneralesInc = RegistroConverter
//                        .toRegistro(requestManager.get(
//                                        UrlServiceUtil.getInstance()
//                                                        .getUrlServiceByUrlByEnumID(
//                                                        		ComprobantecntsControladorUrlEnum.URL5773
//                                                                                        .getValue())
//                                                        .getUrl(),
//                                        param));
//		        if (impuestosGeneralesInc != null)
//		        {
//		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesInc.getCampos().get("SUMADEIMPUESTO_INC").toString(), 0).toString()) > 0) { 
//			        	Impuesto impuesto = new Impuesto();
//			        	impuesto.setBaseImpuesto(new BigDecimal(SysmanFunciones.nvl(impuestosGeneralesInc.getCampos().get("SUMADEBASEIMPUESTOINC"),"0").toString())); 
//			        	impuesto.setPorcImpuesto(new BigDecimal(SysmanFunciones.nvl(
//			        			impuestosGeneralesInc.getCampos().get(
//                                        "PORCENTAJE"),
//                        "0").toString()));
//			        	impuesto.setValorImpuesto(new BigDecimal(SysmanFunciones.nvl(impuestosGeneralesInc.getCampos().get("SUMADEIMPUESTO_INC"),"0").toString())); 
//			        	impuesto.setCodImpuesto("04");
//			        	impuestosFin.getImpuesto().add(impuesto);
//			        	totalImpuestos = totalImpuestos.add(impuesto.getValorImpuesto());
//		        }
//			        
//		        }
		        // gestion retenciones
		        BigDecimal totalRetenciones = new BigDecimal("0");
		        Retenciones retencionesFin = new Retenciones();
		        Registro impuestosGeneralesReteIva = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                        		ComprobantecntsControladorUrlEnum.URL5771
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));
		        if (impuestosGeneralesReteIva != null)
		        {
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesReteIva.getCampos().get("SUMADERETEIVA").toString(), 0).toString()) > 0) { 
			        	Retencion retencion = new Retencion();
			        	retencion.setBaseRetencion(new BigDecimal(SysmanFunciones.nvl(impuestosGeneralesReteIva.getCampos().get("SUMADEBASEIMPUESTORETEIVA"),"0").toString())); 
			        	retencion.setPorcRetencion(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesReteIva.getCampos().get(
                                        "PORCENTAJE"),
                        "0").toString()));
			        	retencion.setValorRetencion(new BigDecimal(SysmanFunciones.nvl(impuestosGeneralesReteIva.getCampos().get("SUMADERETEIVA"),"0").toString())); 
			        	retencion.setCodRetencion("05");
			        	retencionesFin.getRetencion().add(retencion);
			        	totalRetenciones = totalRetenciones.add(retencion.getValorRetencion());
		        	}
			        
		        }
		        Registro impuestosGeneralesReteFuente = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                        		ComprobantecntsControladorUrlEnum.URL5769
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));

		        if (impuestosGeneralesReteFuente != null)
		        {
		        	if(Double.parseDouble(SysmanFunciones.nvl(impuestosGeneralesReteFuente.getCampos().get("SUMADEVALOR_RETEFUENTE").toString(), 0).toString()) > 0) {
			        	Retencion retencion = new Retencion();
			        	retencion.setBaseRetencion(new BigDecimal(SysmanFunciones.nvl(impuestosGeneralesReteFuente.getCampos().get("SUMADEBASEIMPUESTORETEFUENTE"),"0").toString())); 
			        	retencion.setPorcRetencion(new BigDecimal(SysmanFunciones.nvl(
			        			impuestosGeneralesReteFuente.getCampos().get(
                                        "PORCENTAJE"),
                        "0").toString()).setScale(2));
			        	retencion.setValorRetencion(new BigDecimal(SysmanFunciones.nvl(impuestosGeneralesReteFuente.getCampos().get("SUMADEVALOR_RETEFUENTE"),"0").toString())); 
			        	retencion.setCodRetencion("06");
			        	retencionesFin.getRetencion().add(retencion);
			        	totalRetenciones = totalRetenciones.add(retencion.getValorRetencion());
		        	}			        
		        }	
		        
		     // se agregan los productos 		        
		        List<Registro> listaProductos = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL2974.getValue())
										.getUrl(),
								param));
		        Lineas lineas = new Lineas();
	        	int cont = 1;
	        	double sumProdPorFactura = 0;
		        if(!listaProductos.isEmpty()) {
		        	
			        for(Registro reg: listaProductos) {	
			        	Linea linea = new Linea();
				        linea.setNumLinea(cont);
				        linea.setIdEstandarReferencia("00"+cont);
				        linea.setDescripcionItem(SysmanFunciones
                                .nvl(reg.getCampos().get(
                                        "DESCRIPCIONPRODUCTO"),
                                        "")
                        .toString());
				        linea.setUnidadMedida("NIU");
				        linea.setUnidadesLinea(new BigDecimal(1));
				        linea.setPrecioUnidad(new BigDecimal(SysmanFunciones
				        	    .nvl(reg.getCampos().get("TOTALITEM"), "0")  
				        	    .toString()));
				        
				        linea.setSubtotalLinea(new BigDecimal(SysmanFunciones
				        	    .nvl(reg.getCampos().get("TOTALITEM"), "0") 
				        	    .toString()));
				        
				        //descuentos SysmanFunciones.nvlDbl(reg3.getCampos().get("VALOR_DESCUENTO"), 0)
				       // if(Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VALOR_DESCUENTO").toString(), 0).toString()) > 0) {
				        	Double porcentajeDescuenteo = new Double("0");
					        porcentajeDescuenteo = (SysmanFunciones.nvlDbl(reg.getCampos().get("VALOR_DESCUENTO"), 0)*100)/SysmanFunciones.nvlDbl(reg.getCampos().get("TOTALITEM"), 0);
					        linea.setPorcDescuentoLinea(new BigDecimal(porcentajeDescuenteo));
					        linea.setDescuentoLinea(new BigDecimal(SysmanFunciones.nvlDbl(reg.getCampos().get("VALOR_DESCUENTO"), 0)));
					    //}//else Agregar datos en cero
					        linea.setTotalLinea(new BigDecimal(SysmanFunciones
					        	    .nvl(reg.getCampos().get("TOTALITEM"), "0")  
					        	    .toString()));
					        
				        // se suman los totales de los producctos
				        sumProdPorFactura = sumProdPorFactura + linea.getTotalLinea().doubleValue();
				        //se agrega impuesto principal IVA en caso de tener el impuesto
				        if (Double.parseDouble(SysmanFunciones.toString(
		                        SysmanFunciones.nvl(
		                                reg.getCampos().get("VALORIMPUESTO"),
		                                0))) > 0) {

		                	BigDecimal porcImpuesto = new BigDecimal(SysmanFunciones.toString(
		                		    SysmanFunciones.nvl(reg.getCampos().get("PORCENTAJEIVAX"), "0")));

		                		BigDecimal valorImpuesto = new BigDecimal(SysmanFunciones.toString(
		                		    SysmanFunciones.nvl(reg.getCampos().get("VALORIMPUESTO"), "0")));

		                		if (porcImpuesto.compareTo(BigDecimal.ZERO) > 0) {
		                		    linea.setCodImpuestoLinea("01");
		                		    linea.setPorcImpuestoLinea(porcImpuesto);
		                		    linea.setValorImpuestoLinea(valorImpuesto);
		                		}
		                }
				        //else Agregar datos en cero
				        
				        lineas.getLinea().add(linea);
				        cont++; 
			        }			        
		        }		        
		        documento.setLineas(lineas);
		        BigDecimal subtotalLineas = new BigDecimal("0");
		        for (Linea l : lineas.getLinea()) {
		            subtotalLineas = subtotalLineas.add(l.getTotalLinea());
		        }
		        //	Totales factura
		        verificarDescuentos(param, new Double(cont), sumProdPorFactura);
//		        TotalesCop totales = new TotalesCop();
//		        totales.setValorPagarCop(SysmanFunciones
//                        .nvl(rs2.getCampos().get("VALOR_TOTAL"), "")
//                        .toString());
//		        totales.setSubtotalCop(SysmanFunciones
//                        .nvl(rs2.getCampos().get("SUBTOTAL"), "")
//                        .toString());
//		        totales.setMonedaCop(SysmanFunciones
//                        .nvl(rs2.getCampos().get("TIPO_MONEDA"), "")
//                        .toString().equals("602")?"COP":"");
//		        
//		        documento.setTotalesCop(totales);
		        
		        // Se agregan los datos totales
		        DatosTotales totalesFact = new DatosTotales();
		        BigDecimal totalImpuestos = new BigDecimal("0");
		        
		        totalesFact.setSubtotal(subtotalLineas.setScale(2, RoundingMode.HALF_UP));
		        totalesFact.setTotalBase(subtotalLineas.setScale(2, RoundingMode.HALF_UP));
		        totalesFact.setTotalImpuestos(totalImpuestos); 
		        totalesFact.setTotalDocumento(subtotalLineas.setScale(2, RoundingMode.HALF_UP));
		        totalesFact.setAPagar(subtotalLineas.setScale(2, RoundingMode.HALF_UP));
		        totalesFact.setTotalRetenciones(totalRetenciones.setScale(2, RoundingMode.HALF_UP));
		        totalesFact.setTotalGastos(new BigDecimal("0").setScale(2, RoundingMode.HALF_UP));

		        documento.setDatosTotales(totalesFact);

		        CondicionPago formaPago = new CondicionPago();
		        formaPago.setMedioPago(SysmanFunciones
                        .nvl(rs2.getCampos().get("MEDIOPAGO"), "")
                        .toString());
		        formaPago.setFormaPago(SysmanFunciones
                        .nvl(rs2.getCampos().get("TIPO_PAGO"), "")
                        .toString());
		        formaPago.setFechaPago(SysmanFunciones
						.nvl(formatFecha1.format(rs2.getCampos().get("FECHA_VENCIMIENTO")), "")
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
	            
		        /*Se deja estas lineas de codio para cuando sea necesario obtener el XML que se genera
		         * JAXBContext context = JAXBContext.newInstance(Documento.class);
		        Marshaller marshaller = context.createMarshaller();
		        StringWriter writer = new StringWriter();
		        marshaller.marshal(documento, writer);

		        String xml = writer.toString();
		        
		        archivoDescarga = JsfUtil.exportarXmlStreamed("XML_Invoway",
		        		xml);*/
		        String xmlGenerado = documentoToXml(documento);
		        System.out.println("=== XML DOCUMENTO INVOWAY ===\n" + xmlGenerado);
		        
	            respuesta = apiInvoway.postEnvioFactura(url, documento, pass, user);
	            
	            respuesta = respuesta + " Comprobante Número : " + numeroFactura 
	                    + " Consecutivo DIAN : " + consecutivoDian 
	                    + "\n";

			}  
		        
		} catch (SystemException | IOException | com.sysman.util.SysmanException e1) {
			logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
            return "Error al generar el XML";
		}
		
		return respuesta;
	}


	private String documentoToXml(Documento documento) {
	    try {
	        JAXBContext context = JAXBContext.newInstance(Documento.class);
	        Marshaller marshaller = context.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	        
	        StringWriter sw = new StringWriter();
	        marshaller.marshal(documento, sw);
	        return sw.toString();
	    } catch (JAXBException e) {
	        logger.error("Error serializando Documento a XML: " + e.getMessage(), e);
	        return "ERROR: " + e.getMessage();
	    }
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
										ComprobantecntsControladorUrlEnum.URL4587.getValue())
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


	
	/**
	 * metodo que completa el envio a la dian y genera el reporte
	 * 
	 * @param url
	 * @param numFactura
	 * @return
	 */
	private String envioDianIndividual(String url, String numFactura, String consecutivoDian) {
		int tipoformato = 01;
		String codigoReporte = null;
		String prFormato;
		String log = "";
		StringBuilder pieCodigoBarra = new StringBuilder();
		StringBuilder pieTextoBarra = new StringBuilder();
		String codigoBarra;

		Date fechaVencimiento = (Date) registro.getCampos().get(ComprobantecntsControladorEnum.FECHA.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);

		Registro rstipoformato;
		try {
			// consumirEalvarez

			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL2735.getValue())
											.getUrl(),
									param));

			if (rs != null) {

//				codigoReporte = SysmanFunciones.nvl(rs.getCampos().get("FORMATO"), "30002195").toString();
				codigoReporte = "30002195";
			}

			Registro rsCertificado = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL9457.getValue())
											.getUrl(),
									null));

			if (rsCertificado != null) {

				File archivo = new File(rsCertificado.getCampos().get("RUTA_CERTIFICADO").toString());

				String nombreCertificado = archivo.getName();

				byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

				String certificado = Base64.getEncoder().encodeToString(archivoBytes);

				String passCertificado = Base64.getEncoder()
						.encodeToString(rsCertificado.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());

				ParametrosLegalizarFactura paramLegalizar = new ParametrosLegalizarFactura();

				paramLegalizar.setTipoSalida(1);

				paramLegalizar.setTestSetId(rsCertificado.getCampos().get("TES_ID").toString());

				paramLegalizar.setCodigoReporte(codigoReporte);

				ParametrosFormato paramFormato = new ParametrosFormato();

				paramFormato.setTipoFormato(Integer.toString(05));

				paramFormato.setNumFormato(consecutivoDian);

				paramFormato.setPrefijo(prefijo);

				paramFormato.setNombreCertificado(nombreCertificado);

				paramFormato.setCertificado(certificado);

				paramFormato.setPassCertificado(passCertificado);

				paramFormato.setNumDocumentoContribuyente(nitSinDigito);

				// Adicion Codigo Barras

				boolean permiteCodigoBarra = false;

				permiteCodigoBarra = ("SI").equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"SF PERMITE GENERAR CODIGO BARRA FACTURA ELECTRONICA", SessionUtil.getModulo(), new Date(),
						false), "NO"));

				paramFormato.setMuestraCodigoBarras(permiteCodigoBarra);
				if (permiteCodigoBarra) {

					String codigoEan;

					Map<String, Object> paramEan = new TreeMap<>();
					paramEan.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					paramEan.put("ANO", ano);
					paramEan.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);
					Registro rsEan = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1717.getValue())
											.getUrl(),
									paramEan));

					codigoEan = rsEan.getCampos().get("CODIGOEAN").toString();

					pieCodigoBarra.append((char) 205);
					pieCodigoBarra.append((char) 102);
					pieCodigoBarra.append(415);
					pieCodigoBarra.append(codigoEan);
					pieCodigoBarra.append("8020");
					pieCodigoBarra.append(SysmanFunciones.padl(tipoFactRecaudo, 5, "0"));
					pieCodigoBarra.append(SysmanFunciones.padl(numFactura, 19, "0"));
					pieCodigoBarra.append((char) 102);
					pieCodigoBarra.append("3900");
					pieCodigoBarra
							.append(SysmanFunciones.padl(registro.getCampos().get("VALOR_TOTAL").toString(), 14, "0"));
					pieCodigoBarra.append((char) 102);
					pieCodigoBarra.append(96);
					pieCodigoBarra.append(SysmanFunciones.convertirAFechaCadena(fechaVencimiento, "yyyyMMdd"));

					codigoBarra = ejbCodigoBarras.imprimirCodigoBarras(pieCodigoBarra.toString());

					paramFormato.setCodigoBarras(codigoBarra);

					pieTextoBarra.append((char) 40);
					pieTextoBarra.append(415);
					pieTextoBarra.append((char) 41);
					pieTextoBarra.append(codigoEan);
					pieTextoBarra.append((char) 40);
					pieTextoBarra.append("8020");
					pieTextoBarra.append((char) 41);
					pieTextoBarra.append(SysmanFunciones.padl(tipoFactRecaudo, 5, "0"));
					pieTextoBarra.append(SysmanFunciones.padl(numFactura, 19, "0"));
					pieTextoBarra.append((char) 40);
					pieTextoBarra.append("3900");
					pieTextoBarra.append((char) 41);
					pieTextoBarra
							.append(SysmanFunciones.padl(registro.getCampos().get("VALOR_TOTAL").toString(), 14, "0"));
					pieTextoBarra.append((char) 40);
					pieTextoBarra.append(96);
					pieTextoBarra.append((char) 41);
					pieTextoBarra.append(SysmanFunciones.convertirAFechaCadena(fechaVencimiento, "yyyyMMdd"));

					paramFormato.setTextoBarras(pieTextoBarra.toString());
				}

				paramLegalizar.setParamFormato(paramFormato);

				// se consulta el parametro
				// con el fin de que se envie correo solo si tiene el paramentro en SI,
				// asginando como valor el 99

				String paramCorreoAuth = SysmanFunciones.nvlStr(
						ejbSysmanUtil.consultarParametro(compania, "MANEJA ENVIO DE CORREO A TERCERO DOCUMENTO SOPORTE",
								SessionUtil.getModulo(), new Date(), false),
						"NO");
				if ("SI".equals(paramCorreoAuth)) {
					paramLegalizar.setTipoReporte(99);
				} else {
					paramLegalizar.setTipoReporte(0);
				}

				String respuestaLegalizar;
				APIFrida apiFrida = new APIFrida();

				Gson gson2 = new Gson();
				String json = gson2.toJson(paramLegalizar, ParametrosLegalizarFactura.class);

				respuestaLegalizar = apiFrida.postFormatoLegalizar(url, json);

				RespuestaApi respuestaApis = gson2.fromJson(respuestaLegalizar, RespuestaApi.class);
				if (respuestaApis.getCodigo() != 0) {
					log = log + "\n" + respuestaApis.getMensaje() + " " + " DOCUMENTO SOPORTE:" + numFactura
							+ ". Proceso terminado ";
				} else {
					log = log + "\n" + respuestaApis.getMensaje() + "  DOCUMENTO SOPORTE: " + numFactura
							+ ". Proceso terminado    \n" + crearReportesFrida(respuestaApis, consecutivoDian);
				}

			}
		} catch (SystemException | IOException | ParseException | com.sysman.util.SysmanException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return log;
	}

	/**
	 * Valida la existencia del producto
	 * 
	 * @param url
	 * @param codigoProducto
	 */
	private void validarProducto(String url, String codigoProducto) {

		String respuesta;
		APIFrida api = new APIFrida();
		Gson gson = new Gson();

		try {
			respuesta = api.cargarItem(url, codigoProducto);

			RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);

			if (respuestaApi.getCodigo() != 0) {
				crearProducto(url, codigoProducto);
			}
		} catch (IOException | com.sysman.util.SysmanException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Valida el Rango de facturacion registrado en Frida
	 * 
	 * @param url
	 */
	private void validarRango(String url) {
		borrarDatosTablaDetRangoFact();

		String respuesta;
		APIFrida api = new APIFrida();

		try {
			respuesta = api.cargarRangoFacturacion(nitSinDigito, url);

			Gson gson = new Gson();
			RespuestaRangoFacturacion respuestaApi = gson.fromJson(respuesta, RespuestaRangoFacturacion.class);

			for (RespuestaCuerpoRangoFacturacion respuestaCuerpoRangoFacturacion : respuestaApi.getCuerpo()) {
				insertarRangos(respuestaCuerpoRangoFacturacion);

			}

		} catch (IOException | com.sysman.util.SysmanException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo que elimina los rangos previamente
	 */
	private void borrarDatosTablaDetRangoFact() {
		Map<String, Object> params = new TreeMap<>();

		UrlBean urlDelete = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL8521.getValue());

		try {
			requestManager.delete(urlDelete.getUrl(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo que permite insertar el rango de facturacion a enviar.
	 * 
	 * @param respuestaCuerpoRangoFacturacion
	 */
	private void insertarRangos(RespuestaCuerpoRangoFacturacion respuestaCuerpoRangoFacturacion) {

		String urlEnumId = GenericUrlEnum.DET_RANGO_FACT.getCreateKey();

		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

		Map<String, Object> params = new TreeMap<>();
		try {

			Date fechaDesde = formato.parse(respuestaCuerpoRangoFacturacion.getFechadesde().replace("-", "/"));

			Date fechaHasta = formato.parse(respuestaCuerpoRangoFacturacion.getFechahasta().replace("-", "/"));

			formato.applyPattern(SysmanFunciones.FORMATO_FECHA_ESTANDAR);

			params.put("ID", respuestaCuerpoRangoFacturacion.getId());

			params.put("PREFIJO", respuestaCuerpoRangoFacturacion.getPrefijo());

			params.put("NUMERO_RES", respuestaCuerpoRangoFacturacion.getNumeroresolucion());
			params.put("CLAVE_TEC", respuestaCuerpoRangoFacturacion.getClavetecnica());
			params.put("RANGO_INI", respuestaCuerpoRangoFacturacion.getRangoinicial());
			params.put("RANGO_FIN", respuestaCuerpoRangoFacturacion.getRangofinal());

			params.put("FECHADESDE", formato.format(fechaDesde));

			params.put("FECHAHASTA", formato.format(fechaHasta));

			params.put("TIPOAMBIENTE", respuestaCuerpoRangoFacturacion.getIdFeTipoAmbiente());

			params.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			params.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), params);
		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que me permite registar un producto o item en Frida
	 * 
	 * @param url
	 * @param codigoProducto
	 */
	private void crearProducto(String url, String codigoProducto) {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));

		param.put(GeneralParameterEnum.CODIGO.getName(), codigoProducto);

		try {
			// revisar este no trae productos
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL2486.getValue())
											.getUrl(),
									param));

			if (rs != null) {
				ParamItem params = new ParamItem();
				ParamItems paramsItems = new ParamItems();

				List<ParamItem> listaParams = new ArrayList<>();

				params.setCodigoProducto(SysmanFunciones.nvl(rs.getCampos().get("CODIGOPRODUCTO"), "").toString());
				params.setCreatedBy(SessionUtil.getUser().getCodigo());
				params.setDescripcionProducto(
						SysmanFunciones.nvl(rs.getCampos().get("DESCRIPCIONPRODUCTO"), "").toString());

				params.setUnidadMedida(SysmanFunciones.nvl(rs.getCampos().get("UNIDADMEDIDA"), "").toString());

				params.setValorItem(SysmanFunciones.nvl(rs.getCampos().get("VALORITEM"), "").toString());

				listaParams.add(params);

				paramsItems.setItems(listaParams);

				Gson gson = new Gson();
				String json = gson.toJson(paramsItems, ParamItems.class);
				APIFrida apiFrida = new APIFrida();

				apiFrida.postItem(url, json);

			}
		} catch (SystemException | IOException | com.sysman.util.SysmanException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * metodo que nos permite crear el tercero en caso tal de no estar registrado en
	 * Frida
	 * 
	 * @param tercero
	 * @param url
	 * @return
	 * @throws SysmanException
	 */
	private String crearTecero(String tercero, String url) throws SysmanException {
		String respuesta = null;
		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), tercero);

		try {
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL7354.getValue())
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

			// Iteracion sobre obligaciones fisacles del tercero

			Map<String, Object> param2 = new TreeMap<>();

			param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param2.put(GeneralParameterEnum.NIT.getName(), tercero);

			List<Registro> listaObligaciones = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL2054.getValue())
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

			paramTercero.setContribuyente(nitSinDigito);

			List<ParametrosTercero> listaParam = new ArrayList<>();

			listaParam.add(param);

			paramTercero.setTerceros(listaParam);

			Gson gson = new Gson();
			String json = gson.toJson(paramTercero, ParametrosTerceroLote.class);
			APIFrida apiFrida = new APIFrida();

			respuesta = apiFrida.postTercero(url, json);
			
			RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);
			
			Map<String, Object> cuerpoRespuesta = (Map<String, Object>) respuestaApi.getCuerpo();
 			if(!cuerpoRespuesta.get("fallidos").equals("{}") && !cuerpoRespuesta.get("fallidos").toString().contains("Existente")) {
 				Map<String, Object> mensajeFallidos = (Map<String, Object>) cuerpoRespuesta.get("fallidos");
				respuesta = "\n El tercero: " + tercero + " presenta los siguientes errores en la informacion \n " + mensajeFallidos.get(tercero) + "NO_OK";
			}else {
				respuesta = "\n El tercero "+ tercero +" se creo con exito.";
			}

		} catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta;

	}

	private String actualizaTecero(String tercero, String url) throws SysmanException {
		String respuesta = null;
		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), tercero);

		try {
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL7354.getValue())
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

			param.setContribuyente(nitSinDigito);

			// Iteracion sobre obligaciones fisacles del tercero

			Map<String, Object> param2 = new TreeMap<>();

			param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param2.put(GeneralParameterEnum.NIT.getName(), tercero);

			List<Registro> listaObligaciones = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL2054.getValue())
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

			paramTercero.setContribuyente(nitSinDigito);

			List<ParametrosTercero> listaParam = new ArrayList<>();

			listaParam.add(param);

			paramTercero.setTerceros(listaParam);

			Gson gson = new Gson();
			String json = gson.toJson(param, ParametrosTercero.class);
			APIFrida apiFrida = new APIFrida();

			respuesta = apiFrida.putTercero(url, json);
			
			RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);
			
			if(respuestaApi.getMensaje().toString().equals("OK")) {
				respuesta = " Tercero " + tercero + " acutalizado correctamente. ";
			}else {
				respuesta = "Se presento mensaje al actualziar el tercero: " + tercero ;
			}

		} catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta;

	}

	private String retornarString(Registro reg, String campo) {
		return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? "" : reg.getCampos().get(campo).toString();
	}

	/**
	 * metodo para el uso en envio de documento soporte a la dian
	 * 
	 * @param respuestaLegalizar
	 * @param factura
	 * @return
	 */
	private String crearReportesFrida(RespuestaApi respuestaLegalizar, String factura) {

		String log = "";

		OutputStream outZip = null;
		OutputStream outPdf = null;

		Gson gson = new Gson();

		RespuestaApi legalizar = respuestaLegalizar;
		Map<String, Object> param2 = (Map<String, Object>) legalizar.getCuerpo();
		String zip = param2.get("zip").toString(); // zip
		String pdf = null;
		if (param2.get("Reporte") != null) {
			pdf = param2.get("Reporte").toString();
		}

		Registro rsRutaArchivos;
		try {
			rsRutaArchivos = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL9457.getValue())
											.getUrl(),
									null));

			if (rsRutaArchivos != null) {

				String ruta = SysmanFunciones.nvl(rsRutaArchivos.getCampos().get("RUTA_FACTURAS"), "").toString();

				if (!ruta.isEmpty()) {

					File verificar = new File(ruta);
					if (!verificar.isDirectory()) {
						verificar.mkdirs();
					}

					if (zip != null) {

						byte[] archivoZip = Base64.getDecoder().decode(zip);

						outZip = new FileOutputStream(ruta + "/" + "FACTURA_" + factura + "_"
								+ SysmanFunciones.convertirAFechaCadena(new Date(), "YYYYMMDD_HHMMSS") + ".zip");
						outZip.write(archivoZip);
						outZip.close();

					}

					if (pdf != null) {
						byte[] archivoPdf = Base64.getDecoder().decode(pdf);

						outPdf = new FileOutputStream(ruta + "/" + "FACTURA_" + factura + "_"
								+ SysmanFunciones.convertirAFechaCadena(new Date(), "YYYYMMDD_HHMMSS") + ".pdf");
						outPdf.write(archivoPdf);
						outPdf.close();
					}

					// --- Lectura del tag de respuesta de la DIAN
					ArrayList param3 = (ArrayList) param2.get("resultadoProcesoDian");
					param2 = (Map<String, Object>) param3.get(0);
					if (param2.get("IsValid").toString().equals("false")) {// get de errores
						log = SysmanFunciones.nvl(param2.get("ErrorMessage"), "").toString();
					}

				} else {
					JsfUtil.agregarMensajeAlerta("Debe crear la ruta en donde se generaran los reportes");
				}

			}

		} catch (SystemException | ParseException | IOException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

		return log;

	}

	public void oprimirimprimirdian() {
		archivoDescarga = null;
		String url = "";

		String facturadorExterno;
		try {
			facturadorExterno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					MANEJA_FACTURACION_ELECTRONICA_EXTERNA, "69", new Date(), false), "NO");

			if (facturadorExterno.equals("NO")) {

				Map<String, Object> param = new TreeMap<>();
				Date fechaSolicitud = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
				SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(ComprobantecntsControladorEnum.NITCOMPANIA.getValue(), nitSinDigito);
				param.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaSolicitud));
				param.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaSolicitud));
				param.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoMov);
				param.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(),
						registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());

				List<Registro> listaFactSysman;
				try {
					listaFactSysman = RegistroConverter
							.toListRegistro(requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1895018.getValue())
											.getUrl(),
									param));

					for (Registro reg : listaFactSysman) {
						String prefijo = SysmanFunciones.nvl(reg.getCampos().get("PREFIJODIAN"), "NA").toString();

						String numeroFactura = prefijo.equals("NA")
								? SysmanFunciones.nvl(reg.getCampos()
										.get(ComprobantecntsControladorEnum.NUMERO.getValue()).toString(), "")
										.toString()
								: SysmanFunciones.nvl(reg.getCampos()
										.get(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue()).toString(), "")
										.toString();

						String tipoFormato = prefijo.equals("NA") ? "06" : "05";

						ParametroEjecucionApiReporte paramEjecucion = new ParametroEjecucionApiReporte();

						ParametroCuerpoEjecucionReporte paramCuerpoEjecucion = new ParametroCuerpoEjecucionReporte();

						paramEjecucion.setCodigoReporte("30002195");

						paramEjecucion.setCompania(compania);

						paramEjecucion.setEntidad("General");

						paramEjecucion.setFormatoReporte("pdf");

						paramEjecucion.setIdioma("es");

						url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

						url = url + "formato/generarReporte?numFactura=" + numeroFactura + "&numContribuyente="
								+ nitSinDigito + "&tipoFormato=" + tipoFormato + "&prefijo=" + prefijo;

						paramEjecucion.setUrl(url);

						paramEjecucion.setUsaAdaptador(false);

						paramCuerpoEjecucion.setNumFactura(numeroFactura);

						paramCuerpoEjecucion.setNumContribuyente(nitSinDigito);

						paramCuerpoEjecucion.setTipoFormato(tipoFormato);

						paramCuerpoEjecucion.setPrefijo(prefijo);

						// codigo barra
						boolean permiteCodigoBarra = false;

						StringBuilder pieCodigoBarra = new StringBuilder();
						StringBuilder pieTextoBarra = new StringBuilder();

						permiteCodigoBarra = ("SI")
								.equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
										"SF PERMITE GENERAR CODIGO BARRA FACTURA ELECTRONICA", SessionUtil.getModulo(),
										new Date(), false), "NO"));

						if (permiteCodigoBarra) {

							String codigoEan;

							Map<String, Object> paramEan = new TreeMap<>();
							paramEan.put(GeneralParameterEnum.COMPANIA.getName(), compania);
							paramEan.put("ANO", ano);
							paramEan.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);
							Registro rsEan = RegistroConverter.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1717.getValue())
											.getUrl(),
									paramEan));

							codigoEan = rsEan.getCampos().get("CODIGOEAN").toString();

							pieCodigoBarra.append((char) 205);
							pieCodigoBarra.append((char) 102);
							pieCodigoBarra.append(415);
							pieCodigoBarra.append(codigoEan);
							pieCodigoBarra.append("8020");
							pieCodigoBarra.append(SysmanFunciones.padl(tipoFactRecaudo, 5, "0"));
							pieCodigoBarra.append(SysmanFunciones.padl(numeroFactura, 19, "0"));
							pieCodigoBarra.append((char) 102);
							pieCodigoBarra.append("3900");
							pieCodigoBarra.append(
									SysmanFunciones.padl(registro.getCampos().get("VALOR_TOTAL").toString(), 14, "0"));
							pieCodigoBarra.append((char) 102);
							pieCodigoBarra.append(96);
							pieCodigoBarra.append(SysmanFunciones.convertirAFechaCadena(fechaSolicitud, "yyyyMMdd"));

							String codigoBarra = ejbCodigoBarras.imprimirCodigoBarras(pieCodigoBarra.toString());

							paramCuerpoEjecucion.setCodigoBarras(codigoBarra);

							pieTextoBarra.append((char) 40);
							pieTextoBarra.append(415);
							pieTextoBarra.append((char) 41);
							pieTextoBarra.append(codigoEan);
							pieTextoBarra.append((char) 40);
							pieTextoBarra.append("8020");
							pieTextoBarra.append((char) 41);
							pieTextoBarra.append(SysmanFunciones.padl(tipoFactRecaudo, 5, "0"));
							pieTextoBarra.append(SysmanFunciones.padl(numeroFactura, 19, "0"));
							pieTextoBarra.append((char) 40);
							pieTextoBarra.append("3900");
							pieTextoBarra.append((char) 41);
							pieTextoBarra.append(
									SysmanFunciones.padl(registro.getCampos().get("VALOR_TOTAL").toString(), 14, "0"));
							pieTextoBarra.append((char) 40);
							pieTextoBarra.append(96);
							pieTextoBarra.append((char) 41);
							pieTextoBarra.append(SysmanFunciones.convertirAFechaCadena(fechaSolicitud, "yyyyMMdd"));

							paramCuerpoEjecucion.setTextoBarras(pieTextoBarra.toString());
						}

						paramEjecucion.setParamReporte(paramCuerpoEjecucion);

						APIFrida apiFrida = new APIFrida();

						Gson gson2 = new Gson();
						String json = gson2.toJson(paramEjecucion, ParametroEjecucionApiReporte.class);

						String respuestaReporte = apiFrida.postGestionApiReporte(json);

						RespuestaApi respuestaApis = gson2.fromJson(respuestaReporte, RespuestaApi.class);

						if (respuestaApis.getCodigo() == 0) {

							byte[] reporte = Base64.getDecoder().decode(respuestaApis.getCuerpo().toString());
							InputStream reporteStram = new ByteArrayInputStream(reporte);
							archivoDescarga = new DefaultStreamedContent(reporteStram,
									ConstanteArchivo.PDF.getContentType(),
									"DocumentoSoporte" + numeroFactura + ConstanteArchivo.PDF.getExtension());

						} else {
							JsfUtil.agregarMensajeError("El Documento no ha sido enviado a la dian");
						}

					}
					if (listaFactSysman.isEmpty()) {
						JsfUtil.agregarMensajeError("El Documento no ha sido enviado a la dian");
					}

				} catch (SystemException | ParseException | IOException | com.sysman.util.SysmanException
						| RuntimeException e) {

					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
			} else {
				try {

					String codigoMedioPago = "";
					String codigoTipoNeg = "";
					String medioPago = "";
					String tipoNegociacion = "";
					String tipoNumeroAfect = "";
					String numero = "";
					String prefijo = "";
					String tipoCpte = SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("TIPO"),""));
					String numeroCpte = SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("NUMERO"),""));
					String titulo = "";
					String nota = "";

					Map<String, Object> parametros = new HashMap<>();
					HashMap<String, Object> reemplazar = new HashMap<>();

					reemplazar.put("compania", compania);
					reemplazar.put("tipo", tipoCpte);
					reemplazar.put("numero", numeroCpte);

					Map<String, Object> param = new TreeMap<>();
					param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param.put(GeneralParameterEnum.TIPO.getName(), tipoCpte);
					param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO"));
					param.put(GeneralParameterEnum.NUMERO.getName(), numeroCpte);

					Registro rsExiste = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1896007.getValue())
											.getUrl(),
									param));

					Registro rsAfectado = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL39130.getValue())
											.getUrl(),
									param));

					if (rsExiste != null) {

						codigoMedioPago = SysmanFunciones.toString(SysmanFunciones.nvl(
								rsExiste.getCampos().get(ComprobantecntsControladorEnum.TIPO_MEDIO_PAGO.getValue()),
								""));
						switch (codigoMedioPago) {
						case "47":
							medioPago = "Transferencia";
							break;
						case "10":
							medioPago = "Efectivo";
							break;
						case "20":
							medioPago = "Cheque";
							break;
						default:
							medioPago = codigoMedioPago;
							break;
						}

						// --- Homologación Tipo Negociación ---
						codigoTipoNeg = SysmanFunciones.toString(SysmanFunciones.nvl(
								rsExiste.getCampos().get(ComprobantecntsControladorEnum.TIPO_PAGO.getValue()), ""));
						switch (codigoTipoNeg) {
						case "1":
							tipoNegociacion = "Pago realizado de contado";
							break;
						case "2":
							tipoNegociacion = "Pago realizado a crédito";
							break;
						default:
							tipoNegociacion = codigoTipoNeg;
							break;
						}
					}

					if (rsAfectado != null) {
						tipoNumeroAfect = SysmanFunciones
								.toString(SysmanFunciones.nvl(rsAfectado.getCampos().get("CMPTE_AFECTADO"), "") + " "
										+ SysmanFunciones.nvl(rsAfectado.getCampos().get("TIPO_CPTE_AFECT"), ""));
					}

					parametros.put("P_MEDIO_PAGO", medioPago);

					parametros.put("P_TIPO_NEGOCIACION", tipoNegociacion);

					parametros.put("P_OBSERVACIONES",
							registro.getCampos().get(ComprobantecntsControladorEnum.DESCRIPCION.getValue()).toString());

					parametros.put("P_ELABORO",
							registro.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString());

					Date elaboroFecha = (Date) registro.getCampos().get(GeneralParameterEnum.DATE_CREATED.getName());
					SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy h:mm a");
					parametros.put("P_ELABORO_FECHA", sdf.format(elaboroFecha));

					String ano = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
					String claseD = "";
					switch (tipoCpte) {
					case "DSE":
						numero = registro.getCampos().get(GeneralParameterEnum.CONSECUTIVODIAN.getName()).toString();
						prefijo = registro.getCampos().get(GeneralParameterEnum.PREFIJODIAN.getName()).toString();
						claseD = "CC";
						titulo = "Documento soporte en adquisiciones efectuadas a no obligados a facturar";
						break;
					case "CNA":
						numero = registro.getCampos().get(GeneralParameterEnum.CONSECUTIVODIAN.getName()).toString();
						prefijo = registro.getCampos().get(GeneralParameterEnum.PREFIJODIAN.getName()).toString();
						claseD = "NA";
						titulo = "Nota de ajuste documento soporte electronico";
						nota = "Documento afectado: " + tipoNumeroAfect + " Anulaci�n del documento soporte en adquisiciones efectuadas a sujetos no obligados a expedir";
						break;
					case "NCR":
						numero = numeroCpte;
						prefijo = "NC";
						claseD = prefijo;
						titulo = "NOTA ELECTR�NICA DE CR�DITO";
						nota = "Factura afectada: " + tipoNumeroAfect + " Anulaci�n defactura electr�nica";
						
						break;
					case "NDB":
						numero = numeroCpte;
						prefijo = "NB";
						claseD = prefijo;
						titulo = "NOTA ELECTR�NICA DE D�BITO";
						nota = "Factura afectada: " + tipoNumeroAfect + " Anulaci�n defactura electr�nica";
						
						break;
					default:
						numero = registro.getCampos().get(GeneralParameterEnum.CONSECUTIVODIAN.getName()).toString();
						prefijo = registro.getCampos().get(GeneralParameterEnum.PREFIJODIAN.getName()).toString();
						claseD = registro.getCampos().get(ComprobantecntsControladorEnum.TIPO.getValue()).toString()
								.equals("DSE") ? "CC" : "NA";
					    titulo = "Documento soporte en adquisiciones efectuadas a no obligados a facturar";
						break;
					}
					
					

					String cude = consultarCudeInvoway(numero, ano, prefijo, claseD);
					if (cude.equals("")) {
						JsfUtil.agregarMensajeError("El Documento no ha sido enviado a la dian");
						return;
					}

					parametros.put("P_CUDE", cude);
					parametros.put("P_TITULO",titulo);
					parametros.put("P_NOMBRE_COMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
					parametros.put("P_NIT_COMPANIA", SessionUtil.getCompaniaIngreso().getNit());
					parametros.put("P_NOTA", nota);

					Reporteador.resuelveConsulta("002932ReporteDianInvoway", Integer.valueOf(modulo), reemplazar,
							parametros);

					archivoDescarga = JsfUtil.exportarStreamed("DocEnvioDian", parametros, ConectorPool.ESQUEMA_SYSMAN,
							FORMATOS.PDF);

				} catch (JRException | IOException | SysmanException e) {
					JsfUtil.agregarMensajeError(e.getMessage());
					logger.error(e.getMessage(), e);
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
	                prefijo + numeroFactura,
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
	 * Si las cuentas contables tienen varias equivalencias abre el formulario
	 * <b>SeleccionRubrosPptales</b> para asociarlas con un rubro presupuestal. Como
	 * segunda instancia ejecuta la funcion
	 *
	 * @param lista Lista de equivalencias presupuestales.
	 */
	private void procesarListaEquivalencias(List<Registro> lista) {
		boolean abreFormulario = false;
		for (Registro reg : lista) {
			Map<String, Object> campos = reg.getCampos();
			int contador = Integer.parseInt(extraerString(campos.get("CONTADOR")));
			if (contador > 1) {
				abrirFormularioSeleccion(lista);
				abreFormulario = true;
				break;
			}
		}
		if (!abreFormulario) {
			// Ejecucion del procedimiento
			generarComprobantePresupuestal(lista);
			// Guarda el rubro pptal referencia para la afectacion
			guardarCuentaPptal(lista);
		}
	}

	/**
	 * En el detalle contable asocia el rubro presupuestal seleccionado para la
	 * generaci&oacute;n del comprobante presupuestal, con la respectiva cuenta
	 * contable.
	 *
	 * @param lista
	 */
	private void guardarCuentaPptal(List<Registro> lista) {
		for (Registro reg : lista) {
			Map<String, Object> campos = reg.getCampos();
			Map<String, Object> parametros = new HashMap<>();
			parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			parametros.put(GeneralParameterEnum.ANO.getName(), ano);
			parametros.put(GeneralParameterEnum.TIPO_CPTE.getName(),
					campos.get(GeneralParameterEnum.TIPO_CPTE.getName()));
			parametros.put(GeneralParameterEnum.COMPROBANTE.getName(),
					campos.get(GeneralParameterEnum.COMPROBANTE.getName()));
			parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(),
					campos.get(GeneralParameterEnum.CONSECUTIVO.getName()));
			parametros.put(ComprobantecntsControladorEnum.CUENTAPPTAL.getValue(),
					campos.get(ComprobantecntsControladorEnum.RUBRO_PPTAL.getValue()));
			parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
			String ulrEnumId = ComprobantecntsControladorUrlEnum.URL33613.getValue();
			UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ulrEnumId);
			Parameter parameter = new Parameter();
			parameter.setFields(parametros);
			try {
				requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	/**
	 * Abre el formulario modal continuo 1399 seleccionrubrospptales, para asociar
	 * cuentas presupuestales a las cuentas contables que tengan mas de una
	 * equivalencia.
	 *
	 * @param lista Lista de registros con las equivalencias presupuestales.
	 */
	private void abrirFormularioSeleccion(List<Registro> lista) {
		int anio = Integer.parseInt(extraerString(registro.getCampos().get(GeneralParameterEnum.ANO.getName())));
		BigInteger numero = new BigInteger(
				extraerString(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName())));
		Date fecha = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
		String tercero = extraerString(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		String sucursal = extraerString(registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		String descripcion = extraerString(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
		String numeroDoc = extraerString(registro.getCampos().get(NRO_DOCUMENTO));
		BigDecimal valorDoc = new BigDecimal(extraerString(registro.getCampos().get(VLR_DOCUMENTO)));
		String tipoCnt = extraerString(registro.getCampos().get(TIPO));

		/**
		 * Valida la creacion del comprobante presupuestal (CFBARRERA_CC:912)
		 */
		try {
            String tipoPptal = null;
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.CODIGO.getName(), tipoCnt);

			Registro rsTipoPptal = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ComprobantecntsControladorUrlEnum.URL2278.getValue())
									.getUrl(),
									param));

			if (rsTipoPptal.getCampos().get("TIPOPPTAL") == null && validarGenerar) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4197"));

			} else {
				tipoPptal = rsTipoPptal.getCampos().get("TIPOPPTAL").toString();

			}//(CFBARRERA_CC:912_FIN)


			Map<String, Object> comprobanteCnt = new HashMap<>();
			comprobanteCnt.put(GeneralParameterEnum.ANO.getName(), anio);
			comprobanteCnt.put(GeneralParameterEnum.NUMERO.getName(), numero);
			comprobanteCnt.put(GeneralParameterEnum.FECHA.getName(), fecha);
			comprobanteCnt.put(GeneralParameterEnum.TERCERO.getName(), tercero);
			comprobanteCnt.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
			comprobanteCnt.put(GeneralParameterEnum.DESCRIPCION.getName(), descripcion);
			comprobanteCnt.put(NRO_DOCUMENTO, numeroDoc);
			comprobanteCnt.put(VLR_DOCUMENTO, valorDoc);
			comprobanteCnt.put(TIPO, tipoCnt);
			comprobanteCnt.put(TIPOPPT, tipoPptal);

			String[] claves = { "PAR_COMPTE_CNT", "PAR_LIST_EQUIVALENCIAS" };
			Object[] valores = { comprobanteCnt, lista };
			SessionUtil.cargarModalDatosFlash(
					Integer.toString(GeneralCodigoFormaEnum.SELECCION_RUBROS_PPTALES_CONTROLADOR.getCodigo()), modulo,
					claves, valores);

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Ejecuta el procedimiento que genera el comprobante presupuestal.
	 * <q>Desde un registro de comprobantes contables, realiza la generacion de un
	 * comprobante presupuestal asociado.</q>
	 */
	private void generarComprobantePresupuestal(List<Registro> lista) {
		Map<String, Object> compteCnt = registro.getCampos();
		int anio = Integer.parseInt(extraerString(compteCnt.get(GeneralParameterEnum.ANO.getName())));
		BigInteger numero = new BigInteger(extraerString(compteCnt.get(GeneralParameterEnum.NUMERO.getName())));
		Date fecha = (Date) compteCnt.get(GeneralParameterEnum.FECHA.getName());
		String tercero = extraerString(compteCnt.get(GeneralParameterEnum.TERCERO.getName()));
		String sucursal = extraerString(compteCnt.get(GeneralParameterEnum.SUCURSAL.getName()));
		String descripcion = extraerString(compteCnt.get(GeneralParameterEnum.DESCRIPCION.getName()));
		String numeroDoc = extraerString(compteCnt.get(NRO_DOCUMENTO));
		BigDecimal valorDoc = new BigDecimal(extraerString(compteCnt.get(VLR_DOCUMENTO)));
		String tipo = extraerString(compteCnt.get(TIPO));
		String cadenaInsertar = traerCadenaRegistros(lista);
		int numeroRegistros = lista.size();
		
		try {

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.CODIGO.getName(), tipo);

			Registro rsTipoPptal = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL2278.getValue())
											.getUrl(),
									param));

			if (rsTipoPptal.getCampos().get("TIPOPPTAL") == null && validarGenerar) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4197"));

			} else {
				logger.info(cadenaInsertar);
				ejbContabilidadUno.generarComprobantePresupuestalClob(compania, anio, tipo, numero, fecha, tercero,
						sucursal, descripcion, numeroDoc, valorDoc,
						validarGenerar ? rsTipoPptal.getCampos().get("TIPOPPTAL").toString() : tipo, cadenaInsertar,
						numeroRegistros, usuario);

				JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(idioma.getString("MSM_PROCESO_EJECUTADO"),
						validarGenerar ? idioma.getString("TB_TB4243").replace("s$numero$s", String.valueOf(numero))
								.replace("s$tipo$s", rsTipoPptal.getCampos().get("TIPOPPTAL").toString()

								) : " "));
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		validarGenerar = true;
	}

	public void oprimirNIIF() {
		boolean noNiif;
		if (ACCION_INSERTAR.equals(accion)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1454"));
			return;
		}

		noNiif = (boolean) (registro.getCampos().get("NONIIF") == null ? 0 : registro.getCampos().get("NONIIF"));

		if (noNiif) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1455"));
			return;
		}

		try {
			boolean rta = ejbContabilidadCpte.prepararNiif(compania, Integer.parseInt(ano), Integer.parseInt(modulo),
					tipoMov, new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
					usuario);

			if (rta) {
				contabilizarNiiFVisible = true;
			} else {
				contabilizarNIIF();
				contabilizarNiiFVisible = false;
			}

		} catch (NumberFormatException | SystemException e) {

			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirDepuracion() {
		// <CODIGO_DESARROLLADO>
		// Si el tercero no tiene el indicador de LEY1450 activo, este
		// boton no estara visible.
		generaInformeDepuracionRenta(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirfacequivalente() {
		// <CODIGO_DESARROLLADO>
		// Si el tercero no tiene el indicador de LEY1450 activo, este
		// boton no estara visible.
		archivoDescarga = null;
		generaInformeFacEquivalente(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirimprimirniif() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		actualizarValorPagosEnComprobantesContables();
		generaInformeFormatoComprobante(FORMATOS.PDF, true);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirlibrofiscal() {
		// <CODIGO_DESARROLLADO>
		/**
		 * @author dmaldonado Segun conversacion con Yolima Sandoval, este boton no se
		 *         migra, pues no se utiliza actualmente. 17/03/2016
		 */
		// </CODIGO_DESARROLLADO>
	}

	/**
	 *
	 * Metodo ejecutado al oprimir el boton EliminarCptePptal en la vista
	 *
	 * Realiza el llamado al procedimiento
	 * PCK_CONTABILIDAD1.PR_ELIMINARCOMPROBANTEPPTAL, enviando como parametro los
	 * valores asociados al copmrobante contable con el que se esta trabajando
	 *
	 */
	public void oprimirEliminarCptePptal() {
		// <CODIGO_DESARROLLADO>
		visibleDgConfirmacion = true;
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarNumero() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}
	
	public void cambiarCONSFACTURA() {
		//<CODIGO_DESARROLLADO>
		alertaFactura();
		//</CODIGO_DESARROLLADO>
	}
	
	public boolean alertaFactura() {
		boolean rta = true;
		if (controlaFactura && clase.equals("P")) {
			try {
			 String valor;
			 Registro rs;

             Map<String, Object> param = new HashMap<>();
             param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
             param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
             param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
             param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get("CONSFACTURA"));

             
				rs = RegistroConverter
				                 .toRegistro(requestManager.get(
				                                 UrlServiceUtil.getInstance()
				                                                 .getUrlServiceByUrlByEnumID(
				                                                		 ComprobantecntsControladorUrlEnum.URL72118.getValue())
				                                                 .getUrl(),
				                                                 param));
				
				if(rs!= null) {
					
					String tipo = SysmanFunciones.toString(rs.getCampos().get(GeneralParameterEnum.TIPO.getName()));
					String numero = SysmanFunciones.toString(rs.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
					
					String mensaje = idioma.getString("TB_TB4454")
                    .replace("s$tipo$s", tipo)
                    .replace("s$numero$s", numero);
					
					JsfUtil.agregarMensajeError(mensaje);	
					rta =  false;
				}

			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
			
		}
		return rta; 
	}

	public void cambiarTxtFechaConsignacion() {

		Map<String, Object> parametros = new HashMap<>();
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametros.put(GeneralParameterEnum.ANO.getName(), ano);
		parametros.put(GeneralParameterEnum.TIPOCPTE.getName(),
				registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
		parametros.put(GeneralParameterEnum.NROCPTE.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		parametros.put(GeneralParameterEnum.FECHA.getName(), getFechaConsignacion());
		String ulrEnumId = ComprobantecntsControladorUrlEnum.URL39104.getValue();
		UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(ulrEnumId);
		Parameter parameter = new Parameter();
		parameter.setFields(parametros);
		try {
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cambiarVlrAGirar() {
		// <CODIGO_DESARROLLADO>
		try {
			if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA CONTROL DE VALOR A GIRAR MAYOR QUE VALOR DOCUMENTO", modulo, new Date(), true))
					&& (Double.valueOf(SysmanFunciones
							.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue()), 0)
							.toString()) > Double
									.valueOf(SysmanFunciones
											.nvl(registro.getCampos()
													.get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()), 0)
											.toString()))) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB1765"));
				registro.getCampos().put(ComprobantecntsControladorEnum.VLRAGIRAR.getValue(), 0);
			}
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarPAGADOGN() {
		if ((boolean) registro.getCampos().get("PAGADOGN")) {
			bloqFechaPagado = false;
		} else {
			registro.getCampos().put(ComprobantecntsControladorEnum.FECHAPAGADOGN.getValue(), "");
			bloqFechaPagado = true;
		}
	}

	public void cambiarFecha() {
		// <CODIGO_DESARROLLADO>
		Date fecha = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
		if (fecha == null) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB573"));
			try {
				registro.getCampos().put("", SysmanFunciones
						.convertirAFecha("01/" + (mes.length() == 1 ? Integer.toString(0) + mes : mes) + "/" + ano));
			} catch (ParseException e) {

				Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
			return;
		}
		try {
			if (fecha.before(SysmanFunciones.convertirAFecha(fechaMinima))
					|| fecha.after(SysmanFunciones.convertirAFecha(fechaMaxima))) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB576") + ano + "-" + mes);
				registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
						registroIni.get(GeneralParameterEnum.FECHA.getName()) == null ? ""
								: registroIni.get(GeneralParameterEnum.FECHA.getName()));
			}
		} catch (ParseException e) {

			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarDescripcion() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTexto() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarinformeCovid() {
		agregarRegistroNuevo(false);
		Map<String, Object> params = new TreeMap<>();
		params.put("INFORMECOVID", registro.getCampos().get("INFORMECOVID"));
		params.put("COMPANIA", compania);
		params.put("ANO", ano);
		params.put("TIPO", registro.getCampos().get(ComprobantecntsControladorEnum.TIPO.getValue()));
		params.put("NUMERO", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		UrlBean urlUpdate = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL6039.getValue());

		Parameter parameter = new Parameter();
		parameter.setFields(params);
		try {
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cambiarEnviado() {
		if ((boolean) registro.getCampos().get("ENVIADO")) {
			if (!bloqTercero) {
				bloqBtRetenciones = true;
				bloqFecha = true;
				bloqCopiar = true;
				bloqTercero = true;
				bloqDescripcion = true;
				bloqTexto = true;                                          
				bloqVlrDocumento = true;
				bloqNroDocumento = true;
				bloqVlrBase = true;
				bloqVlrBaseIVA = true;
				bloqVlrAGirar = true;
				bloqNumeroContrato = true;
				bloqFechaVcnDoc = true;
				bloqPorcIVA = true;
				bloqCentroCosto = true;
				bloqAuxiliar = true;
				bloqORDENADOR = true;
				bloqreferencia = true;
				bloqBanco = true;
				bloqCuenta = true;
			}
		} else {
			if (bloqTercero) {
				bloqBtRetenciones = false;
				bloqFecha = false;
				//bloqCopiar = false;
				try {
					if("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
							"MANEJA PROCESOS DE COPIAR MOVIMIENTOS", modulo, new Date(), true), "SI")))
						{
							bloqCopiar = false;
						}
					else
						{
							bloqCopiar = true;
						}
				} catch ( SystemException ex) {
					Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
				}	
				bloqTercero = false;
				bloqDescripcion = false;
				bloqTexto = false;
				bloqVlrDocumento = false;
				bloqNroDocumento = false;
				bloqVlrBase = false;
				bloqVlrBaseIVA = false;
				bloqVlrAGirar = false;
				bloqNumeroContrato = false;
				bloqFechaVcnDoc = false;
				bloqPorcIVA = false;
				bloqCentroCosto = false;
				bloqAuxiliar = false;
				bloqORDENADOR = false;
				bloqreferencia = false;
				bloqBanco = false;
				bloqCuenta = false;
			}
		}

	}

	public void cambiarImpreso() {
		if ((boolean) registro.getCampos().get(ComprobantecntsControladorEnum.IMPRESO.getValue())) {
			if (!bloqTercero) {
				if (pideRetencion) {
					bloqBtRetenciones = true;
				}
				cambiarValorVariablesBooleanas(true);
			}
		} else {
			if (bloqTercero) {
				if (pideRetencion) {
					bloqBtRetenciones = false;
				}
				cambiarValorVariablesBooleanas(false);
			}
		}
	}

	public void seleccionarFilaNumero(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
	}
	
	public void seleccionarFilaListaPlantillas(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		plantilla = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
        nombrePlantilla =  SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");
        
        visiblePresentarPlantillas = plantilla==null?false:true;
	}

	public void seleccionarFilaTercero(SelectEvent event) {
		if (!accion.equals(ACCION_INSERTAR) && ""
				.equals(SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), ""))) {
			registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
					registroIni.get(GeneralParameterEnum.TERCERO.getName()));
			return;

		}
		try {
			if ("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA COMPROBANTES POR CONCEPTO", modulo, new Date(), true), "NO"))
					&& !"".equals(SysmanFunciones.nvl(registro.getCampos().get("CONCEPTO"), ""))) {
				/*
				 * Se condiciona pues cuando seleccionan el concepto se cambia el porcentaje de
				 * IVA de acuerdo al regimen del tercero y el concepto igual
				 */
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB579"));
				return;
			}
		} catch (SystemException e1) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e1);
			JsfUtil.agregarMensajeError(e1.getMessage());
		}

		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), registroAux.getCampos().get("NIT"));
		registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		registro.getCampos().put(ComprobantecntsControladorEnum.AUTORETENEDOR.getValue(),
				registroAux.getCampos().get(ComprobantecntsControladorEnum.AUTORETENEDOR.getValue()));
		registro.getCampos().put(ComprobantecntsControladorEnum.REGIMEN.getValue(),
				registroAux.getCampos().get(ComprobantecntsControladorEnum.REGIMEN.getValue()));
		registro.getCampos().put(LEY1450, registroAux.getCampos().get(LEY1450));
		nombreTercero = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
		registro.getCampos().put(ComprobantecntsControladorEnum.NOMBRETERCERO.getValue(), nombreTercero);
		cargarListaBanco();
		registro.getCampos().put(ComprobantecntsControladorEnum.TIPOCONTRATO.getValue(), "");
		registro.getCampos().put(ComprobantecntsControladorEnum.NUMEROCONTRATO.getValue(), "");

		ley1819 = (boolean) registroAux.getCampos().get(ComprobantecntsControladorEnum.LEY1819.getValue());

		cargarListaNumeroContrato();
		cargarListaCompACopiar();

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);
		try {
			Registro rsRetencion = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL2298.getValue())
											.getUrl(),
									param));
			if ((boolean) rsRetencion.getCampos().get("PIDE_RETENCION")
					&& ((boolean) registroAux.getCampos().get(LEY1450) || (boolean) registroAux.getCampos()
							.get(ComprobantecntsControladorEnum.LEY1819.getValue()))) {
				visibleBtDepuracion = visibleBtFacEquivalente = true;

			} else {
				visibleBtDepuracion = visibleBtFacEquivalente = false;

			}
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoFlujoEfectivo
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFlujoEfectivo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_FLUJO_EFECTIVO", registroAux.getCampos().get("CODIGO").toString());
	}

	public void terceroAfterUpdate() {

		if (!accion.equals(ACCION_INSERTAR)
				&& !registroIni.get(GeneralParameterEnum.TERCERO.getName())
						.equals(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()))
				&& "E".equals(clase)) {
			// colocarBancoPorDefecto();
		}

		if ("E".equals(clase)) {
			try {
				if ("SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"MANEJA ALERTAS EN OPCION DE TERCEROS", modulo, new Date(), true), ""))) {
					validarSiElRegistroAplica();
				}
			} catch (SystemException e) {
				Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}

		verificarTerceroEmbargado();
		verificarTerceroGiroElectronico();
	}

	private void validarSiElRegistroAplica() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		List<Registro> regAux = null;
		try {
			regAux = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13461.getValue())
											.getUrl(),
									param));
			if (!regAux.isEmpty() && (boolean) SysmanFunciones.nvl(regAux.get(0).getCampos().get("APLICA"), false)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB581"));
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void verificarTerceroGiroElectronico() {
		// AGREGADO PARA VERIFICAR SI AL TERCERO SE LE PAGA POR GIRO
		// ELECTRONICO
		try {
			if ("SI".equals(ejbSysmanUtil.consultarParametro(compania, "MANEJA ALERTA DE PAGO ELECTRONICO A TERCEROS",
					modulo, new Date(), true))
					&& SysmanFunciones.nvl(
							ejbSysmanUtil.consultarParametro(compania,
									"ALERTA DE PAGO ELECTRONICO A TERCEROS EN COMPROBANTES", modulo, new Date(), true),
							"").toString().contains(tipoMov)) {
				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.TERCERO.getName(),
						registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
				param.put(GeneralParameterEnum.SUCURSAL.getName(),
						registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
				List<Registro> regAux = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL13463.getValue())
										.getUrl(),
								param));

				if (!regAux.isEmpty()
						&& (boolean) SysmanFunciones.nvl(regAux.get(0).getCampos().get("PAGOELECTRONICO"), false)) {
					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB594"));
				}
			}
		} catch (SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void verificarTerceroEmbargado() {
		// AGREGADO PARA VERIFICAR SI EL TERCERO ESTA EMBARGADO O NO
		try {
			if ("SI".equals(ejbSysmanUtil.consultarParametro(compania, "MANEJA ALERTA DE EMBARGO EN COMPROBANTES",
					modulo, new Date(), true))
					&& SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
							"ALERTA DE EMBARGO EN COMPROBANTES", modulo, new Date(), true), "").toString()
							.contains(tipoMov)) {

				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.TERCERO.getName(),
						registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
				param.put(GeneralParameterEnum.SUCURSAL.getName(),
						registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
				List<Registro> regAux;
				regAux = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL13462.getValue())
										.getUrl(),
								param));

				if (!regAux.isEmpty()
						&& (boolean) SysmanFunciones.nvl(regAux.get(0).getCampos().get("EMBARGO"), false)) {
					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB582") + new DecimalFormat("##,###.00")
							.format(SysmanFunciones.nvlDbl(regAux.get(0).getCampos().get("VALOREMBARGO"), 0)));
				}
			}
		} catch (SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void colocarBancoPorDefecto() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		List<Registro> reg = null;
		try {
			reg = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13464.getValue())
											.getUrl(),
									param));
			if (!reg.isEmpty()) {
				if (reg.get(0).getCampos() != null) {
					registro.getCampos().put(GeneralParameterEnum.BANCO.getName(),
							reg.get(0).getCampos().get(GeneralParameterEnum.BANCO.getName()));
					registro.getCampos().put(ComprobantecntsControladorEnum.CUENTABANCO.getValue(),
							reg.get(0).getCampos().get(GeneralParameterEnum.CUENTA.getName()));
				} else {
					registro.getCampos().put(GeneralParameterEnum.BANCO.getName(), "");
					registro.getCampos().put(ComprobantecntsControladorEnum.CUENTABANCO.getValue(), "");
				}
			} else {
				registro.getCampos().put(GeneralParameterEnum.BANCO.getName(), "");
				registro.getCampos().put(ComprobantecntsControladorEnum.CUENTABANCO.getValue(), "");
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void seleccionarFilaNumeroContrato(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(ComprobantecntsControladorEnum.NUMEROCONTRATO.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), ComprobantecntsControladorEnum.TEXTO.getValue())) {
			registro.getCampos().put(ComprobantecntsControladorEnum.TEXTO.getValue(),
					registroAux.getCampos().get("OBJETOCONTRATO"));
		}
		if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
				ComprobantecntsControladorEnum.NUMEROCONTRATO.getValue())) {
			registro.getCampos().put(ComprobantecntsControladorEnum.TIPOCONTRATO.getValue(),
					SysmanFunciones.nvl(registroAux.getCampos().get("CLASEORDEN"), ""));
			registro.getCampos().put(ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue(),
					SysmanFunciones.nvl(registroAux.getCampos().get(ComprobantecntsControladorEnum.NUMERO.getValue()), ""));
		} else {
			registro.getCampos().put(ComprobantecntsControladorEnum.TIPOCONTRATO.getValue(), "");
		}
	}

	public void seleccionarFilaCompACopiar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		compACopiar = registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();

		if (ACCION_INSERTAR.equals(accion)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB595"));
			return;
		}

		try {
			BigInteger bigCompACopiar = new BigInteger(compACopiar);
			String cuentas = ejbContabilidadCpte.verificarCompACopiar(compania,
					Integer.parseInt(SysmanFunciones
							.nvl(registroAux.getCampos().get(GeneralParameterEnum.ANO.getName()), "").toString()),
					SysmanFunciones.nvl(registroAux.getCampos().get(TIPO), "").toString(), bigCompACopiar);

			if (cuentas != null && !cuentas.isEmpty()) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB596") + cuentas + idioma.getString("TB_TB597"));
				compACopiar = "";
				return;
			}

			regAuxCompACopiar = registroAux;
			visibleDGConservaDesc = true;
		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo para continuar el evento seleccionarFila del combo CompACopiar,
	 * despues de mostrar el dialogo de confimacion para conservar la descripcion.
	 *
	 * @author dmaldonado
	 * @param regAux registro que viene del evento de seleccion
	 */
	public void continuarSelFilaCompACopiar(Registro regAux) {

		if (!rtaConservaDescripcion) {
			registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
					regAux.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
		}

		registro.getCampos().put(ComprobantecntsControladorEnum.TEXTO.getValue(),
				regAux.getCampos().get(ComprobantecntsControladorEnum.TEXTO.getValue()));

		if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
				ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue())) {
			registro.getCampos().put(ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue(),
					regAux.getCampos().get(ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue()));
		}
		registro.getCampos().put(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue(),
				regAux.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()));
		registro.getCampos().put(ComprobantecntsControladorEnum.VLR_BASE.getValue(),
				regAux.getCampos().get(ComprobantecntsControladorEnum.VLR_BASE.getValue()));
		registro.getCampos().put(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue(),
				regAux.getCampos().get(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue()));
		registro.getCampos().put(ComprobantecntsControladorEnum.VLRAGIRAR.getValue(),
				regAux.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue()));

		if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.TERCERO.getName())) {
			registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
					regAux.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
			registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
					regAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
		}
		agregarRegistroNuevo(false);

		try {
			ejbContabilidadDos.copiarComprobanteContable(compania,
					Integer.parseInt(SysmanFunciones.nvl(regAux.getCampos().get("ANO"), "").toString()),
					SysmanFunciones.nvl(regAux.getCampos().get(TIPO), "").toString(),
					new BigInteger(regAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), "")
							.toString(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "")
							.toString(),
					Integer.parseInt(ano), tipoMov,
					new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
					(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()), "")
							.toString(),
					SysmanFunciones
							.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue()), "")
							.toString(),
					usuario);
			cargarRegistro(registro.getLlave(), accion, registro.getIndice());
			cargarRegistro();
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3500"));
		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void seleccionarFilaCuenta(SelectEvent event) {
		if (!grupo) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB605"));
			return;
		}
		try {
			if (validarCuentaConciliada()) {
				return;
			}
			verificarCuentaConciliada(event);
		} catch (SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private boolean validarCuentaConciliada() throws SystemException {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				"'" + registro.getCampos().get(GeneralParameterEnum.COMPANIA.getName()) + "'");
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO"));

		param.put(ComprobantecntsControladorEnum.TIPO.getValue(), "'" + registro.getCampos().get(TIPO) + "'");

		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		List<Registro> rs = RegistroConverter
				.toListRegistro(
						requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL13466.getValue())
										.getUrl(),
								param));

		if (!rs.isEmpty()) {
			if (rs.size() > 1) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB606"));
				return true;
			} else {
				if ((boolean) SysmanFunciones.nvl(rs.get(0).getCampos().get("PAGADOBANCO"), false)) {
					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB607"));
					return true;
				}

			}
		}
		return false;
	}

	private void verificarCuentaConciliada(SelectEvent event) throws SystemException {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				"'" + registro.getCampos().get(GeneralParameterEnum.COMPANIA.getName()) + "'");
		param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO"));

		param.put(ComprobantecntsControladorEnum.TIPO.getValue(), "'" + registro.getCampos().get(TIPO) + "'");

		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		// verificar si la cuenta esta conciliada
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

		if (!"".equals(SysmanFunciones.nvl(registro.getCampos().get(TIPO), ""))
				&& (Double.doubleToRawLongBits(SysmanFunciones
						.nvlDbl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), 0)) != 0)
				&& !"".equals(SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), ""))
				&& !"".equals(
						SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()), ""))) {

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get("ANO"));

			param.put(ComprobantecntsControladorEnum.TIPOCOMPROBANTE.getValue(),
					"'" + SysmanFunciones.nvl(registro.getCampos().get("TIPOCOMPROBANTE"), "") + "'");

			param.put(GeneralParameterEnum.NUMERO.getName(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), 0));

			List<Registro> rs = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13467.getValue())
											.getUrl(),
									param));

			if (rs.isEmpty()) {
				if (!"SI".equals(controlChequera)) {
					registro.getCampos()
							.put(ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue(),
									SysmanFunciones.nvlDbl(Double.parseDouble(
											registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()) + ""), 0)
											+ 1);
				} else {
					determinarNoChequeraYChequeActual(param);
				}
			} else {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB567"));
			}
		}
	}

	private void determinarNoChequeraYChequeActual(Map<String, Object> param) {
		// determina no chequera y cheque actual
		param.remove(GeneralParameterEnum.NUMERO.getName());
		param.remove(ComprobantecntsControladorEnum.TIPO.getValue());
		param.remove(ComprobantecntsControladorEnum.TIPOCOMPROBANTE.getValue());
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.CUENTA.getName(),
				"" + registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()) + "");
		List<Registro> rsCta = null;
		try {
			rsCta = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13468.getValue())
											.getUrl(),
									param));

			if (!rsCta.isEmpty()) {
				registro.getCampos().put(ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue(),
						Integer.valueOf(SysmanFunciones.nvl(rsCta.get(0).getCampos().get("CHEQUE"), 0).toString()) + 1);
				if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
						"PERMITE MODIFICAR NUMERO CHEQUE EN CONTROL DE CHEQUERA", modulo, new Date(), true))) {
					bloqNroDocumento = false;
				} else {
					bloqNroDocumento = true;
				}
				if (Integer.valueOf(ejbSysmanUtil.consultarParametro(compania, "MINIMO NUMERO DE CHEQUES", modulo,
						new Date(), true)) >= Integer.valueOf(rsCta.get(0).getCampos().get("DISPONIBLES").toString())) {
					JsfUtil.agregarMensajeInformativo(
							idioma.getString("TB_TB615") + rsCta.get(0).getCampos().get("DISPONIBLES"));
				}
			} else {
				JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
						idioma.getString("TB_TB617"), " ", SysmanFunciones
								.nvl(registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()), "").toString(),
						" ", idioma.getString("TB_TB400")));
			}
		} catch (SystemException | NumberFormatException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void seleccionarFilaBanco(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.BANCO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.BANCO.getName()));
		registro.getCampos().put(ComprobantecntsControladorEnum.CUENTABANCO.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.CUENTA.getName()));
	}

	public void seleccionarFilaCODISIA(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODI_SIA", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaReferencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaCentroCosto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaAuxiliar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaORDENADOR(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		ordenador = SysmanFunciones.nvl(registroAux.getCampos().get("CARGO"), "").toString();
		registro.getCampos().put("ORDENADOR", registroAux.getCampos().get("CEDULA"));
		registro.getCampos().put("ORDENADORSUCURSAL",
				registroAux.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
	}

	public void seleccionarFilaCODPROYECTO(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("COD_PROYECTO", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put("NOMBRE_PROYECTO", registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
	}

	public void seleccionarFilaDependencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("DEPENDENCIACNT", registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaConcepto(SelectEvent event) {
		if (ACCION_INSERTAR.equals(accion)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB631"));
			return;
		}
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(ComprobantecntsControladorEnum.CONCEPTO_SF.getValue(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put(ComprobantecntsControladorEnum.TIPOCOBROCONCEPTO.getValue(),
				registroAux.getCampos().get("TIPOCOBRO"));
		previoDistribuirConcepto();

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTIPOPAGOSIA
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTIPOPAGOSIA(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TIPOPAGO_SIA", registroAux.getCampos().get("CODIGO"));
	}

	public void seleccionarFilaComprobanteACopiar(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();

		compHeredar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
				.toString();

	}

	private void previoDistribuirConcepto() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(ComprobantecntsControladorEnum.TIPOCOMPROBANTE.getValue(), tipoMov);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		List<Registro> auxReg = null;
		try {
			auxReg = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13467.getValue())
											.getUrl(),
									param));

			if (!auxReg.isEmpty()) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB3519"));
				undoConcepto();
				return;
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
		param.put(GeneralParameterEnum.NUMERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		try {
			auxReg = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13469.getValue())
											.getUrl(),
									param));

			// URL13469
			if (!auxReg.isEmpty()) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB632"));
				undoConcepto();
				return;
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		if (SysmanFunciones
				.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), SysmanConstantes.CONS_TERCERO)
				.equals(SysmanConstantes.CONS_TERCERO)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB634"));
			undoConcepto();
			return;
		}

		visibleDGConcepto = true;
		strVlrDocumento = idioma.getString("TB_TB3520").replace("s$strVlrDocumento$s",
				SysmanFunciones.nvl(registro.getCampos().get(VLR_DOCUMENTO), "0").toString());

	}

	public void undoConcepto() {
		registro.getCampos().put(ComprobantecntsControladorEnum.CONCEPTO_SF.getValue(),
				registroIni.get(ComprobantecntsControladorEnum.CONCEPTO_SF.getValue()));
	}

	public void aceptarConfirmaDistConcepto() {
		// <CODIGO_DESARROLLADO>
		visibleDGConcepto = false;
		distribuyeConcepto();
		// </CODIGO_DESARROLLADO>
	}

	public void cancelarConfirmaDistConcepto() {
		// <CODIGO_DESARROLLADO>
		visibleDGConcepto = false;
		undoConcepto();
		// </CODIGO_DESARROLLADO>
	}

	private int distribuyeConcepto() {
		String regimen = registro.getCampos().get(ComprobantecntsControladorEnum.REGIMEN.getValue()).toString();
		if ("C".equals(SysmanFunciones.nvlStr(regimen, ""))) {
			try {
				registro.getCampos().put(ComprobantecntsControladorEnum.PORCIVA.getValue(),
						SysmanFunciones
								.nvl(ejbSysmanUtil.consultarParametro(compania, "IVA", modulo, new Date(), true), "")
								.toString());
			} catch (SystemException e) {
				Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		} else {
			registro.getCampos().put(ComprobantecntsControladorEnum.PORCIVA.getValue(), 0);
		}

		cambiarVlrDocumento();

		try {
			ejbContabilidadCpte.insertarComprobanteCNTRet(compania, Integer.parseInt(ano), Integer.parseInt(modulo),
					tipoMov, new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
					new BigDecimal(
							registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue()).toString()),
					new BigDecimal(
							registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASE.getValue()).toString()),
					registro.getCampos().get(ComprobantecntsControladorEnum.TIPOCOBROCONCEPTO.getValue()).toString(),
					registro.getCampos().get(ComprobantecntsControladorEnum.CONCEPTO_SF.getValue()).toString(),
					usuario);

		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		oprimirCalcularRetenciones();

		int distribuyeConcepto = 0;
		try {
			distribuyeConcepto = Integer.valueOf(Acciones.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
					"PCK_CONTABILIDAD2.FC_DISTRIBUYECONCEPTO", "", Types.INTEGER).toString());

		} catch (NumberFormatException | IllegalAccessException | InstantiationException | ClassNotFoundException
				| SQLException | NamingException e) {

			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		if (distribuyeConcepto == 1) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB636"));
		} else if (distribuyeConcepto == 2) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB637"));
		}

		return distribuyeConcepto;
	}

	public void cambiarVlrDocumento() {
		// <CODIGO_DESARROLLADO>
		try {
			if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA CONTROL DE VALOR A GIRAR MAYOR QUE VALOR DOCUMENTO", modulo, new Date(), true))
					&& (Double.valueOf(SysmanFunciones
							.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue()), 0)
							.toString()) > Double
									.valueOf(SysmanFunciones
											.nvl(registro.getCampos()
													.get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()), 0)
											.toString()))) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB1765"));
				registro.getCampos().put(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue(), 0);
				return;
			}

			double valorBase;
			double valorIVA;
			int digitosRedondeo;
			double dblPorcIVA;
			double vlrDocumento = SysmanFunciones
					.nvlDbl(registro.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()), 0);
			dblPorcIVA = SysmanFunciones
					.nvlDbl(registro.getCampos().get(ComprobantecntsControladorEnum.PORCIVA.getValue()), 0);
			if (pideRetencion) {
				String regimen = SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.REGIMEN.getValue()), "N")
						.toString();
				if ("C".equals(regimen) || "G".equals(regimen) || "P".equals(regimen)) {
					digitosRedondeo = Integer.valueOf(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"DIGITOS DE REDONDEO IVA Y BASE", modulo, new Date(), true), "2"));
					valorBase = SysmanFunciones.redondear(vlrDocumento / (1 + (dblPorcIVA / 100)), digitosRedondeo);
					valorIVA = SysmanFunciones.redondear(vlrDocumento - valorBase, digitosRedondeo);
					validarActualizarValorDocNoCero(vlrDocumento, valorBase, valorIVA);
					registro.getCampos().put(ComprobantecntsControladorEnum.VLR_BASE.getValue(),
							SysmanFunciones.nvlDbl(valorBase, 0));
					registro.getCampos().put(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue(),
							SysmanFunciones.nvlDbl(valorIVA, 0));
				} else {
					registro.getCampos().put(ComprobantecntsControladorEnum.VLR_BASE.getValue(), vlrDocumento);
				}
			} else {
				registro.getCampos().put(ComprobantecntsControladorEnum.VLR_BASE.getValue(), vlrDocumento);
				registro.getCampos().put(ComprobantecntsControladorEnum.VLRAGIRAR.getValue(), vlrDocumento);
			}
			agregarRegistroNuevo(false);
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}
	
	public void cambiarPorcIVA()
	{	
		BigInteger bigNumero = new BigInteger(
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
		boolean valor;
		try {
			valor = ejbContabilidadCpte.verificarTieneDetalle(compania, Integer.parseInt(ano), tipoMov,
					bigNumero);			
			if (valor) 
			{
				registro.getCampos().put(ComprobantecntsControladorEnum.PORCIVA.getValue(), ivaActual);
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB445"));				
			}
			else
			{
				cambiarVlrDocumento();
			}
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}		
	}

	private void validarActualizarValorDocNoCero(double vlrDocumento, double valorBase, double valorIVA) {
		if ((Double.doubleToRawLongBits(vlrDocumento) != 0) && !ACCION_INSERTAR.equals(accion)) {
			try {
				ejbContabilidadCpte.actualizarValorDocNoCero(compania, Integer.valueOf(ano), tipoMov,
						new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
						new BigDecimal(registro.getCampos().get("VLR_BASE").toString()), BigDecimal.valueOf(valorBase),
						new BigDecimal(registro.getCampos().get("VLR_BASEIVA").toString()),
						BigDecimal.valueOf(valorIVA), usuario);
			} catch (NumberFormatException | SystemException e) {
				Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	public void ejecutarUndoValor() {
		registro.getCampos().put(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue(), 0);
	}

	public void aceptarConservarDescripcion() {
		// <CODIGO_DESARROLLADO>
		rtaConservaDescripcion = true;
		visibleDGConservaDesc = false;
		continuarSelFilaCompACopiar(regAuxCompACopiar);
		// </CODIGO_DESARROLLADO>
	}

	public void cancelarConservarDescripcion() {
		// <CODIGO_DESARROLLADO>
		rtaConservaDescripcion = false;
		visibleDGConservaDesc = false;
		continuarSelFilaCompACopiar(regAuxCompACopiar);
		// </CODIGO_DESARROLLADO>
	}

	/*
	 * Metodo de confirmacion para el boton de NIIF.
	 *
	 * @modificado por: acaceres
	 *
	 * @fecha: 05-06/09/2016
	 */
	public void aceptarConfirmaCompNiif() {
		// <CODIGO_DESARROLLADO>

		try {
			// <CODIGO_DESARROLLADO>
			boolean valor = ejbContabilidadCpte.borrarDetComproNiif(compania, Integer.parseInt(modulo),
					Integer.parseInt(ano), tipoMov,
					new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()));
			if (valor) {
				contabilizarNIIF();
				contabilizarNiiFVisible = false;
			}

		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void contabilizarNIIF() {
		Date fechaCreacion = (Date) (registro.getCampos().get("DATE_CREATED"));
		Date fecha = (Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName());
		Date fechaVncDoc = (Date) registro.getCampos().get(ComprobantecntsControladorEnum.FECHA_VCN_DOC.getValue());
		String porcentIva = String
				.valueOf(registro.getCampos().get(ComprobantecntsControladorEnum.PORCIVA.getValue()) == null ? 0
						: registro.getCampos().get(ComprobantecntsControladorEnum.PORCIVA.getValue()));
		Date fechaPagadoDogn = (Date) registro.getCampos().get(ComprobantecntsControladorEnum.FECHAPAGADOGN.getValue());
		boolean rta = false;
		try {

			rta = ejbContabilidadCpte.contabilizarNiif(compania, Integer.parseInt(modulo), fechaCreacion, fecha,
					fechaVncDoc, fechaPagadoDogn, Integer.parseInt(ano), tipoMov,
					new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()), "")
							.toString(),
					SysmanFunciones.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.TEXTO.getValue()), "")
							.toString(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), "")
							.toString(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "")
							.toString(),
					new BigDecimal(registro
							.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()).toString()),
					usuario,
					new BigDecimal(
							registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASE.getValue()).toString()),
					new BigDecimal(
							registro.getCampos().get(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue()).toString()),
					BigDecimal.valueOf(Double
							.parseDouble(SysmanFunciones.nvl(registro.getCampos().get("DEBITO"), "0.0").toString())),
					BigDecimal.valueOf(Double
							.parseDouble(SysmanFunciones.nvl(registro.getCampos().get("CREDITO"), "0.0").toString())),
					BigDecimal.valueOf(Double.parseDouble(SysmanFunciones
							.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue()), "0.0")
							.toString())),
					BigDecimal
							.valueOf(Double.parseDouble(SysmanFunciones
									.nvl(registro.getCampos()
											.get(ComprobantecntsControladorEnum.DEBITOSAFECTADOS.getValue()), "0.0")
									.toString())),
					new BigDecimal(registro.getCampos().get(ComprobantecntsControladorEnum.CREDITOSAFECTADOS.getValue())
							.toString()),
					Double.parseDouble(porcentIva),
					SysmanFunciones.nvl(registro.getCampos().get("CENTRO_COSTO"), "").toString(),
					SysmanFunciones.nvl(registro.getCampos().get("AUXILIAR"), "").toString(),
					SysmanFunciones.nvl(registro.getCampos().get("FUENTE_RECURSO"), "").toString(), SysmanFunciones
							.nvl(registro.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()), "").toString());

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		if (rta) {
			JsfUtil.agregarMensajeInformativo("Comprobante contabilizado correctamente.");
		}

		contabilizarNiiFVisible = false;
	}

	public void cancelarConfirmaCompNiif() {
		// <CODIGO_DESARROLLADO>
		JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
		// </CODIGO_DESARROLLADO>
	}

	public void cargarValoresInicialesComprobante() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
		try {
			regComprobante = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13474.getValue())
											.getUrl(),
									param));

			if (registro.getCampos().get(ComprobantecntsControladorEnum.FECHACONSIGNACION.getValue()) == null
					&& registro.getCampos().get(GeneralParameterEnum.FECHA.getName()) != null) {

				registro.getCampos().put(ComprobantecntsControladorEnum.FECHACONSIGNACION.getValue(),
						registro.getCampos().get(GeneralParameterEnum.FECHA.getName()));
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	@Override
	public void abrirFormulario() {
		comprobarpermisoscuentas();
		try {
			paramTipoACopiar = ejbSysmanUtil.consultarParametro(compania, "CUENTAS MEDICAS - TIPO A COPIAR", modulo,
					new Date(), false);

			paramCausacionHeredada = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CUENTAS MEDICAS - MANEJA CAUSACION HEREDADA", modulo, new Date(), false), "NO");

			plano = tipoMov.equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"TIPO COMPROBANTE GENERACION PLANO NOMINA", modulo, new Date(), true), "NOM")) ? true : false;
			
			manejaCausacion = (ejbSysmanUtil.consultarParametro(compania, "MANEJA CAUSACION AUTOMATICA", modulo, new Date(), true)).equals("SI")?true:false;
			
			visibleLegalizado = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA ALERTAS EN ANTICIPO", modulo, new Date(), false), "NO").equals("SI")?true:false;
			
			controlaFactura = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CONTROLA NUMERO FACTURA EN COMPROBANTE CONTABLE", modulo, new Date(), false), "NO").equals("SI")?true:false;
			
			controlaLongitud = "SI".equals(SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "CONTROLA LONGITUD DE CONSECUTIVO", "-1", new Date(), true),
					"NO"));
			
			visibleFirme = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, "MANEJA FECHA EN FIRME", modulo, new Date(), true),
					"NO"));

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	@Override
	public void cargarRegistro() {
		precargarRegistro();
		visibleCausacionHeredada = false;
		
		if (ACCION_MODIFICAR.equals(accion)) {
			bloqueaNumero = true;
		} //JM 10/10/2024

		try {

			cargarTerceros = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MOSTRAR BOTON TERCERO EN COMPROBANTE CNT", modulo, new Date(), true), "NO").equals("SI") ? true
							: false;

			nombreTercero = null;
			ivaActual = null;
			if (!ACCION_INSERTAR.equals(accion)) {
				ivaActual = SysmanFunciones.nvl(registro.getCampos().get("PORCIVA"), "0").toString();
				nombreTercero = extraerString(registro.getCampos().get("NOMBRETERCERO"));
				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);
				Registro rsRetencion = RegistroConverter
						.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														ComprobantecntsControladorUrlEnum.URL2298.getValue())
												.getUrl(),
										param));
				if ((boolean) rsRetencion.getCampos().get("PIDE_RETENCION")
						&& ((boolean) registro.getCampos().get(LEY1450) || (boolean) SysmanFunciones.nvl(
								registro.getCampos().get(ComprobantecntsControladorEnum.LEY1819.getValue()), false))) {
					visibleBtDepuracion = visibleBtFacEquivalente = true;

				} else {
					visibleBtDepuracion = visibleBtFacEquivalente = false;

				}

				Map<String, Object> paramPptal = new TreeMap<>();
				paramPptal.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				paramPptal.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);

				Registro rsTipoPptal = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL2278.getValue())
										.getUrl(),
								paramPptal));
				if (rsTipoPptal.getCampos().get("TIPOPPTAL") != null) {
					Map<String, Object> paramUno = new TreeMap<>();
					paramUno.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					paramUno.put(GeneralParameterEnum.ANO.getName(), ano);
					paramUno.put("TIPOCOMPROBANTE", rsTipoPptal.getCampos().get("TIPOPPTAL").toString());
					paramUno.put("NUMEROCOMPROBANTE", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
					Registro registroPptal = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL18261.getValue())
											.getUrl(),
									paramUno));
					if (Integer.parseInt(registroPptal.getCampos().get("CANTIDAD").toString()) == 0) {
						activarCompIngreso = true;
					} else {
						activarCompIngreso = false;
					}

				} else {
					activarCompIngreso = true;
				}
				evaluarCargaEliminarPptal();
				validarVisibilidadCausacion();
				obtenerOrdenador();

				if (visibleTxtFechaConsignacion) {
					setFechaConsignacion((Date) registro.getCampos().get("FECHAPAGADOGN"));
				}
				if (bloqConceptoCUDS) {
					// logica para verificacion el estado del documento soporte
					String consecutivoDianTemp = String.valueOf(registro.getCampos().get("NUMERO").toString())
							.split(ano)[1];
					Long consecDianTemp = Long.valueOf(consecutivoDianTemp);
					if(consecDianTemp.compareTo(new Long(String.valueOf(registro.getCampos().get("CONSECUTIVODIAN").toString()))) != 0) {
						consecDianTemp = Long.valueOf(String.valueOf(registro.getCampos().get("CONSECUTIVODIAN").toString()));
					}
					Registro regAux;
					param = new TreeMap<>();
					param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
					// Se valida como primera opcion que sea un documento soporte o nota de ajuste
					// enviado con exito
					regAux = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL15078.getValue())
											.getUrl(),
									param));
					if (consultarEstadoDocDian(
							regAux.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString().equals("05")
									? consecDianTemp.toString()
									: registro.getCampos().get("NUMERO").toString(),
							regAux.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString(),
							regAux.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString().equals("05")
									? tipoMov
									: "NA")) {
						yaEnviadoDianExito = true;
					}
				}
			} else {
				
				bloqueaNumero = !"SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"PERMITE MODIFICAR CONSECUTIVOS CONTABILIDAD", modulo, new Date(), true), "NO")); //JM 10/10/2024
				
				visibleBtDepuracion = visibleBtFacEquivalente = false;
				registro.getCampos().put(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue(), 0);
				registro.getCampos().put(ComprobantecntsControladorEnum.VLR_BASE.getValue(), 0);
				registro.getCampos().put(ComprobantecntsControladorEnum.VLR_BASEIVA.getValue(), 0);
				registro.getCampos().put(ComprobantecntsControladorEnum.VLRAGIRAR.getValue(), 0);
				registro.getCampos().put("ANO", ano);
				registro.getCampos().put(TIPO, tipoMov);
				Date fechaIns = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(fechaIns);
				int diaActual;
				diaActual = c.get(Calendar.DAY_OF_MONTH);
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.set(Calendar.MONTH, Integer.valueOf(mes) - 1);
				c.set(Calendar.YEAR, Integer.valueOf(ano));
				if (c.getActualMaximum(Calendar.DAY_OF_MONTH) < diaActual) {
					c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				} else {
					c.set(Calendar.DAY_OF_MONTH, diaActual);
				}
				registro.getCampos().put(GeneralParameterEnum.FECHA.getName(), c.getTime());
				registro.getCampos().put("HORA", c.getTime());
				registro.getCampos().put(ComprobantecntsControladorEnum.FECHAPAGADOGN.getValue(), c.getTime());

				registro.getCampos().put(ComprobantecntsControladorEnum.FECHA_VCN_DOC.getValue(),
						SysmanFunciones.sumarRestarDiasFecha(
								(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),
								Integer.valueOf((SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
										"DIAS VENCIMIENTO COMPROBANTE CONTABLE", modulo, new Date(), true), 30))
												.toString())));
				yaEnviadoDianExito = false;

			}
			compACopiar = null;
			bloqCompACopiar = true;
			visiblePacTesoreria = false;
			visibleDgConfirmacion = false;
			cargaInicialPeriodo();
			visibleDgCompPptal = visibleDgCompPptal && !bloqueaObjetos;
			current();
			if (!formatoComprobante.startsWith("0")) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1730"));
			}
			
			if (manejaCausacion || manejaConceptoIng) {
				try {
					
					ejbContabilidadSeis.calcularValorNeto(compania, Integer.parseInt(ano), tipoMov,
							new BigInteger(SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()))), 0, 0);

				} catch (NumberFormatException | SystemException e) {
					logger.error(e.getMessage(), e);
					SessionUtil.redireccionarMenuPermisos();
				}
			}
			
			validarDetallesCausacionAutomatica();
			
		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void validarVisibilidadCausacion() {

		boolean tieneDetalle;
		try {
			tieneDetalle = ejbContabilidadCpte.verificarTieneDetalle(compania, Integer.parseInt(ano), tipoMov,
					new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()));

			if ("SI".equals(paramCausacionHeredada) && "P".equals(opcionMenu) && !tieneDetalle) {
				visibleCausacionHeredada = true;
			}

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void current() {
		if (!accion.equals(ACCION_INSERTAR)) {
			validarConsecutivoPorCentroCosto();
		} else {
			if (bloqTercero) {
				cambiarValorVariablesBooleanas(false);
			}
			try {
				if ("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
						"MANEJA CONSECUTIVO POR CENTRO DE COSTO", modulo, new Date(), true), "NO"))) {
					bloqCentroCosto = false;
				}
			} catch (SystemException ex) {
				Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		if (ACCION_INSERTAR.equals(accion)) {
			registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), SysmanConstantes.CONS_TERCERO);
			registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), SysmanConstantes.CONS_SUCURSAL);
			cargarListaNumeroContrato();
			bloqVlrDocumento = true;
		}

	}

	private void validarConsecutivoPorCentroCosto() {
		bloqVlrDocumento = false;
		try {
			terceroAfterUpdate();
			bloqImpreso = "C".equals(estadoPeriodo) || !SessionUtil.getGrupo(modulo).isModificaComprobante();
			if ("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA CONSECUTIVO POR CENTRO DE COSTO", modulo, new Date(), true), "NO"))
					&& !registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName())
							.equals(SysmanConstantes.CONS_CENTRO)) {
				bloqCentroCosto = true;
			}
			if ((Boolean
					.valueOf(registro.getCampos().get(ComprobantecntsControladorEnum.IMPRESO.getValue()).toString()))
					|| "C".equals(estadoPeriodo) || (Boolean.valueOf(registro.getCampos().get("ENVIADO").toString()))) {
				if (!bloqTercero) {
					cambiarValorVariablesBooleanas(true);
				}
			} else {
				if (bloqTercero) {
					cambiarValorVariablesBooleanas(false);
				}
			}
		} catch (SystemException ex) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void cambiarValorVariablesBooleanas(boolean valor) {
		bloqFecha = valor;
		//bloqCopiar = valor;
		try {
			if("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA PROCESOS DE COPIAR MOVIMIENTOS", modulo, new Date(), true), "SI")))
				{
					bloqCopiar = valor;
				}
			else
				{
					bloqCopiar = true;
				}
		
		} catch ( SystemException ex) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
		bloqTercero = valor;
		bloqDescripcion = valor;
		bloqTexto = valor;
		bloqCODPROYECTO = valor;
		bloqVlrDocumento = valor;
		bloqVlrBase = valor;
		bloqVlrBaseIVA = valor;
		bloqVlrAGirar = valor;
		bloqBanco = valor;
		bloqCuenta = valor;
		bloqNumeroContrato = valor;
		bloqFechaVcnDoc = valor;
		bloqPorcIVA = valor;
		bloqCentroCosto = valor;
		bloqAuxiliar = valor;
		bloqORDENADOR = valor;
		bloqreferencia = valor;
		bloqNroDocumento = valor;
		bloqCODISIA = valor;
		bloqTIPOPAGOSIA = valor;
		bloqFECHAPAGADOGN = valor;
	}

	public void open() {
		try {
			String cptesAnticipo = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"TIPOS COMPROBANTES QUE MANEJAN ANTICIPO", modulo, new Date(), true), "").toString();

			visibleIndAnticipo = cptesAnticipo.contains(tipoMov) || anticipoBool;

			if ("NO".equals(ejbSysmanUtil.consultarParametro(compania, "ORDENAR COMPROBANTES POR NUMERO", modulo,
					new Date(), true))) {
				urlListado = UrlServiceUtil.getUrlBeanById(ComprobantecntsControladorUrlEnum.URL13480.getValue());
			} else {
				urlListado = UrlServiceUtil.getUrlBeanById(ComprobantecntsControladorUrlEnum.URL13481.getValue());
			}

			visibleConsFactura = "V".equals(opcionMenu) || "I".equals(opcionMenu);
			bloqCONSFACTURA = true;
			
			//TICKET 7739856 
			if ("P".equals(opcionMenu) || "E".equals(opcionMenu)) {
				visibleConsFactura = true;
				bloqCONSFACTURA = false;
			}	
			//TICKET 7739856
			

			visibleContribuyente = "I".equals(opcionMenu);

			visibleConcepto = "SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA COMPROBANTES POR CONCEPTO", modulo, new Date(), true), "NO"));
			validarTerceroEnCasoDePagoApoderado();
		} catch (NamingException | SQLException | IllegalAccessException | InstantiationException
				| ClassNotFoundException | SystemException ex) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void validarTerceroEnCasoDePagoApoderado() throws NamingException, SQLException, IllegalAccessException,
			InstantiationException, ClassNotFoundException, SystemException {

		// modificador: Harvey Dimate
		// fecha: 23/08/2010
		// control de cambio de tercero en caso de pago
		// apoderado para que no cree auxiliar y afectacion
		// contable
		visiblePagoApoderado = visibleTipoPagoSIA = visibleCodigoSIA = "E".equals(opcionMenu);

		if (visibleCodigoSIA && "SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
				"MANEJA PAGADO Y FECHA PAGADO EN EGRESOS", modulo, new Date(), true), "NO"))) {
			visiblePagadoGN = true;
			visibleFechaPagado = true;
		}
		if ("EGR".equals(tipoMov)) {
			visiblePagoEnPlano = true;
			visibleEnviado = true;
		}

		if ("COM".equals(tipoMov) || "EGR".equals(tipoMov)) {
			visibleCodigoProyecto = true;
			visibleNombreProyecto = true;
		}

		bloqueaNumero = !"SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
				"PERMITE MODIFICAR CONSECUTIVOS CONTABILIDAD", modulo, new Date(), true), "NO"));
		// activacion de campo ordenador

		if ("SI".equals(SysmanFunciones.nvlStr(
				ejbSysmanUtil.consultarParametro(compania, "MANEJA CONTROL DE CHEQUERAS", modulo, new Date(), true),
				"NO")) && "EGR".equals(tipoMov)) {
			controlChequera = "SI";
			bloqNroDocumento = true;
		} else {
			controlChequera = "NO";
		}

		validarParametrosVistaPreFechaConsig();

	}

	private void validarParametrosVistaPreFechaConsig() throws NamingException, SQLException, IllegalAccessException,
			InstantiationException, ClassNotFoundException, SystemException {
		if ("SI".equals(SysmanFunciones.nvl(
				ejbSysmanUtil.consultarParametro(compania, "MANEJA CONTROL VISTA PRELIMINAR", modulo, new Date(), true),
				""))) {

			String strnombregrupovistap = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE GRUPO VISTA PRELIMINAR", modulo, new Date(), true), "");
			visibleImprimir = false;
			String[] grupos = strnombregrupovistap.split(",");
			for (String grupoAux : grupos) {
				if (SessionUtil.getGrupo(modulo).getCodigo().equalsIgnoreCase(grupoAux)) {
					visibleImprimir = true;
				}
			}

		}

		if ("SI".equals(ejbSysmanUtil.consultarParametro(compania, "MUESTRA FECHA DE CONSIGNACION POR PLANO", modulo,
				new Date(), true))) {
			visibleTxtFechaConsignacion = true;
			bloqTxtFechaConsignacion = true;
		} else {
			visibleTxtFechaConsignacion = false;
		}
		if ("SI".equals(ejbSysmanUtil.consultarParametro(compania, "ENTIDAD APLICA NIIF", modulo, new Date(), true))) {
			String companiaNIIF = ejbSysmanUtil.consultarParametro(compania, idioma.getString("TB_TB3370"), modulo,
					new Date(), true);
			if (companiaNIIF == null) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB649"));
			} else {
				if (!companiaNIIF.equals(compania)) {
					visibleNiif = true;
					visibleNoNiif = true;
					visibleImprimirNiif = true;
				}
			}
		}
		verificarPeriodoConciliarYEstadoMes();
	}

	private void verificarPeriodoConciliarYEstadoMes() throws IllegalAccessException, InstantiationException,
			ClassNotFoundException, SQLException, NamingException, SystemException {
		estadoPeriodo = ejbGenerales.consultarEstadoDeVigencia(compania, Integer.parseInt(ano), "CT");
		if ("C".equals(estadoPeriodo)) {
			periodoCerrado("A");
		} else {
			estadoPeriodo = ejbSysmanUtil.verificarEstadoPeriodoMensual(compania, Integer.parseInt(ano),
					Integer.parseInt(mes), Integer.parseInt(modulo), 1);
			if ("C".equals(estadoPeriodo)) {
				periodoCerrado("M");
			}
		}

	}

	/**
	 * Este metodo se realiza con el fin de ejecutar las acciones que se deben
	 * realizar cuando el periodo esta cerrado.
	 *
	 * @author dmaldonado
	 * @param tipoCierre A cuando el anio esta cerrado, M cuando el mes esta
	 *                   cerrado.
	 */
	public void periodoCerrado(String tipoCierre) {
		if ("A".equals(tipoCierre)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1768"));
		} else if ("M".equals(tipoCierre)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1767"));
		}
		mostrarInsert = false;
		mostrarUpdate = false;
		mostrarDelete = false;
	}

	public void retornarFormularioComprobantesAAfectar(SelectEvent event) {
		if (event.getObject() != null) {
			SessionUtil.redireccionar((Direccionador) event.getObject());
		}
		cargarRegistro(registro.getLlave(), accion, registro.getIndice());
		cargarRegistro();
	}
	
	public void retornarFormularioconceptosIngresos(SelectEvent event) {
		if (event.getObject() != null) {
			SessionUtil.redireccionar((Direccionador) event.getObject());
		}
		cargarRegistro(registro.getLlave(), accion, registro.getIndice());
		cargarRegistro();
	}

	/**
	 * 06/12/17 jeguerrero Se agrego el cargarRegistro para que se muestren los
	 * datos que fueron actualizados cuando se hace la afectacion.
	 */
	public void oprimirRetornarFormularioVerBancos() {
		Map<String, Object> parametros = SessionUtil.getFlash();
		if (parametros != null) {
			visibleOrdenador = !Boolean.valueOf(parametros.get(CTEVISIBLEORDENADOR).toString());
		}

		cargarRegistro(registro.getLlave(), accion, registro.getIndice());
		cargarRegistro();
		SessionUtil.cleanFlash();
	}

	/**
	 * Este metodo contiene todas aquellas validaciones que en access se realizaban
	 * desde el formulario de periodo previo a la apertura del comprobante.
	 *
	 * @author dmaldonado
	 *
	 */
	private void cargaInicialPeriodo() {
		visibleBtBancos = false;
		visibleIVA = false;
		visibleInhumacion = false;
		visibleEnviado = true;
		visiblePagoEnPlano = false;
		visibleCompRelacionado = false;
		visiblePagoApoderado = false;
		try {
			// <CODIGO_DESARROLLADO>
			cargarValoresInicialesComprobante();
			nombreComprobante = SysmanFunciones
					.nvl(regComprobante.get(0).getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
			clase = SysmanFunciones.nvl(regComprobante.get(0).getCampos().get("CLASE_CONTABLE"), "").toString();
			formatoComprobante = SysmanFunciones.nvl(regComprobante.get(0).getCampos().get("FORMATO"), "").toString();
			compRelacionado = SysmanFunciones.nvl(regComprobante.get(0).getCampos().get("COMPRELACIONADO"), "")
					.toString();
			pideRetencion = Boolean.parseBoolean(regComprobante.get(0).getCampos().get("PIDE_RETENCION").toString());
			anticipoBool = Boolean.parseBoolean(SysmanFunciones.toString(regComprobante.get(0).getCampos().get("ANTICIPO")));
			tipoCpteAfect = compRelacionado;
			String manejaInhumacion = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA DATOS DE INHUMACION", modulo, new Date(), true),
					"NO").toString();

			visibleCompCruce = "P".equals(clase) || "E".equals(clase);

			if ("SI".equalsIgnoreCase(manejaInhumacion)) {
				String activaInhumacion = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
						"COMPROBANTE QUE ACTIVA DATOS DE INHUMACION", modulo, new Date(), true), "NO").toString();
				if (tipoMov.equalsIgnoreCase(activaInhumacion)) {
					visibleInhumacion = true;
				}
			}
			activarObjetos();
			if (!pideRetencion) {
				bloqBtRetenciones = true;
				visibleIVA = false;
				registro.getCampos().put(ComprobantecntsControladorEnum.PORCIVA.getValue(), 0);
			} else {
				boolean impreso = (boolean) SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.IMPRESO.getValue()), false);
				bloqBtRetenciones = impreso;
				visibleIVA = true;
				String iva = SysmanFunciones
						.nvl(ivaActual, ejbSysmanUtil.consultarParametro(compania, "IVA", modulo, new Date(), true)).toString();
				registro.getCampos().put(ComprobantecntsControladorEnum.PORCIVA.getValue(), iva);
			}
			validarParametrosYBooleanos();
			String nombreGrupoTesoreria = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "NOMBRE GRUPO TESORERIA", modulo, new Date(), true),
							"")
					.toString();
			String[] vectorGrupo = nombreGrupoTesoreria.split(",");
			grupo = false;
			for (String vectorGrupo1 : vectorGrupo) {
				if (SessionUtil.getGrupo(modulo).getCodigo().equals(vectorGrupo1.trim())) {
					grupo = true;
				}
			}
			terceroEnEncabezado = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"BUSCAR TERCERO EN EL ENCABEZADO", modulo, new Date(), true), "SI").toString();
			open();
			// </CODIGO_DESARROLLADO>
		} catch (NamingException | SQLException | SystemException ex) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void validarParametrosYBooleanos() throws NamingException, SQLException, SystemException {
		visibleTxtLiquido = visibleTxtRespArea = visibleTxtReviso = "SI"
				.equals(ejbSysmanUtil.consultarParametro(compania, "CAMPOS DE CONTROL EN ORDEN DE PAGO", modulo,
						new Date(), true))
				// dmaldonado: Se agrega la validacion de clase por
				// recomendacion de jvillate. 14/10/2016
				&& "P".equals(clase);
		if ("E".equalsIgnoreCase(clase)) {
			String manejaTipoPago = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA TIPO PAGO", modulo, new Date(), true), "NO")
					.toString();
			if ("SI".equalsIgnoreCase(manejaTipoPago)) {
				visibleTipoPagoL = true;
				cargarListaTipoPagoL();
			} else {
				visibleTipoPagoL = false;
				listaTipoPagoL = null;
			}
			tituloLblDoc = "No. Cheque:";
			visibleEgresoCNT = true;
			String permiteVariosBancos = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"PERMITE GIRAR POR VARIOS BANCOS", modulo, new Date(), true), "NO").toString();
			visibleBtBancos = "SI".equalsIgnoreCase(permiteVariosBancos);
		} else {
			String claseCuentas = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania,
							"TIPOS DE COMPROBANTES PARA ACTIVAR IMPRESION CHEQUE", modulo, new Date(), true), "EGR")
					.toString();
			tituloLblDoc = "No. Documento:";
			visibleEgresoCNT = claseCuentas.toUpperCase().contains(tipoMov);
		}
		validarParProvePACProg();
	}

	private void validarParProvePACProg() throws NamingException, SQLException, SystemException {
		String manejaProveedores = SysmanFunciones
				.nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA PROVEEDORES", modulo, new Date(), true), "NO")
				.toString();
		visibleBanco = visibleEnviado = visiblePagoEnPlano = visiblePagoEfectivo = "SI"
				.equalsIgnoreCase(manejaProveedores) && "E".equalsIgnoreCase(clase);
		muestraPagoCodigoNombre();
		if ("I".equals(clase) || "E".equals(clase)) {
			String pacProporcional = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "PAC PROPORCIONAL AL GIRO", modulo, new Date(), true),
					"NO").toString();
			visibleBtPACEjec = "SI".equalsIgnoreCase(pacProporcional);
		}
		String manejaProgramacion = SysmanFunciones.nvl(
				ejbSysmanUtil.consultarParametro(compania, "MANEJA PROGRAMACION DE PAGOS", modulo, new Date(), true),
				"NO").toString();
		String manejaSelector = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
				"MANEJA SELECTOR ORDENADOR DEL GASTO EN COM", modulo, new Date(), true), "NO").toString();
		String permiteApoderados = SysmanFunciones
				.nvl(ejbSysmanUtil.consultarParametro(compania, "PERMITE GIRAR AL APODERADO", modulo, new Date(), true),
						"NO")
				.toString();
		permiteApoderado = "SI".equalsIgnoreCase(permiteApoderados);
		cargarListaBanco();
		if ("P".equalsIgnoreCase(clase)
				&& ("SI".equalsIgnoreCase(manejaProgramacion) || "SI".equalsIgnoreCase(manejaSelector))) {
			visibleOrdenador = true;
		}
		if ("E".equalsIgnoreCase(clase) && permiteApoderado) {
			visiblePagoApoderado = true;
		}
	}

	private void muestraPagoCodigoNombre() throws NamingException, SQLException, SystemException {

		/*
		 * Muestra check pago en efectivo, codigo y nombre de proyecto para: Notas
		 * Cliente, Causacion de Cuentas por Cobrar, Comprobantes de Cierre y
		 * Consignaciones.
		 */
		if (Arrays.asList("N", "V", "Z", "S").contains(clase)) {
			visiblePagoEfectivo = true;
			visibleCodigoProyecto = true;
			visibleNombreProyecto = true;
		}

		if (!"".equals(SysmanFunciones.nvl(compRelacionado, "").toString().trim())) {
			visibleCompRelacionado = true;
		}

		if ("E".equals(clase) || "D".equals(clase) || "A".equals(clase) || "G".equals(clase)) {
			visiblePacTesoreria = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CONTROLAR EGRESO CONTRA PAC DE TESORERIA", modulo, new Date(), true), "NO"));
		}
		String limitaLista = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
				"LIMITAR A LA LISTA CONTRATOS EN CONTABILIDAD", modulo, new Date(), true), "SI").toString();
		editableContrato = !"SI".equalsIgnoreCase(limitaLista);

	}

	/**
	 * Habilita los botones de GenerarComprobantePptal, Imputacion Presupuestal y
	 * Comprobante Presupuestal
	 * 
	 * @throws SystemException
	 */

	public void activarObjetos() throws SystemException {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
		List<Registro> listaAux = null;
		try {
			listaAux = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL13476.getValue())
											.getUrl(),
									param));
			if (listaAux.isEmpty()) {
				bloqueaObjetos = true;
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		
		//JM 30/01/2025 CC 774
		Map<String, Object> param2 = new TreeMap<>();
		param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param2.put(GeneralParameterEnum.TIPOCPTE.getName(), registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
		existePPTO = false;

		Registro aux2;
		try {
			aux2 = RegistroConverter.toRegistro(
                    requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                            		ComprobantecntsControladorUrlEnum.URL25057
                                                            .getValue())
                            .getUrl(), param2));
			
			
			int valor = (int) aux2.getCampos().get("EXISTE");

				if (valor > 0) {
					existePPTO = true;
				}
				
		RequestContext.getCurrentInstance().execute("document.getElementById('FR560_nuevo:BT1092').style.display = '" + (existePPTO ? "block" : "none") + "';");

		} catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public boolean validarPacEgreso(boolean indImpresion) {
		boolean rta = false;
		try {
			int numModulo = Integer.parseInt(modulo);
			int numAno = Integer.parseInt(ano);
			Object object = registro.getCampos().get(GeneralParameterEnum.NUMERO.getName());
			BigInteger numero = new BigInteger(object != null ? String.valueOf(object) : "0");
			object = registro.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue());
			BigDecimal valorDocumento = BigDecimal.valueOf(SysmanFunciones.nvlDbl(object, 0.0));
			object = registro.getCampos().get(ComprobantecntsControladorEnum.VLRAGIRAR.getValue());
			BigDecimal valorGirar = BigDecimal.valueOf(SysmanFunciones.nvlDbl(object, 0.0));
			rta = ejbContabilidadCpte.validarPacEgreso(compania, numModulo, numAno, tipoMov, numero, clase,
					valorDocumento, valorGirar, indImpresion);
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return rta;
	}

	public void cambiarTerceroEnDetalle() {
		try {
			ejbContabilidadCpte.actualizarTerceroDet(compania, Integer.parseInt(ano), tipoMov,
					new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
					SysmanFunciones.nvl(registroIni.get(GeneralParameterEnum.TERCERO.getName()), "").toString(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), "")
							.toString(),
					SysmanFunciones.nvl(registroIni.get(GeneralParameterEnum.SUCURSAL.getName()), "").toString(),
					SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()), "")
							.toString(),
					usuario);
		} catch (NumberFormatException | SystemException e) {
			Logger.getLogger(ComprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void generaInformeDepuracionRenta(ReportesBean.FORMATOS formato) {

		archivoDescarga = null;
		try {
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(), ano);
			reemplazar.put(TIPOCPTE, tipoMov);
			reemplazar.put(NUMEROCPTE, registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

			parametros.put(ComprobantecntsControladorEnum.PR_ACCION1_EN_ORDEN_DE_PAGO.getValue(),
					SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							ComprobantecntsControladorEnum.ACCION1_EN_ORDEN_DE_PAGO.getValue(), modulo, new Date(),
							true), ""));
			parametros.put("PR_CARGO_CONTADOR", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "CARGO CONTADOR", modulo, new Date(), true), ""));
			parametros.put("PR_ACCION_EN_ORDEN_DE_PAGO",
					SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							ComprobantecntsControladorEnum.ACCION_EN_ORDEN_DE_PAGO.getValue(), modulo, new Date(),
							true), ""));
			parametros.put("PR_CARGO_CONTABLE_1", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "CARGO CONTABLE 1", modulo, new Date(), true), ""));
			parametros.put(ComprobantecntsControladorEnum.PR_NOMBRECOMPANIA.getValue(),
					SessionUtil.getCompaniaIngreso().getNombre());
			// Se reemplaza el reporte 000645DepuracionRenta
			String reporteDep = "001956DepuracionRenta1819";

			Reporteador.resuelveConsulta(reporteDep, Integer.valueOf(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporteDep, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	public void generaInformeFacEquivalente(ReportesBean.FORMATOS formato) {

		archivoDescarga = null;

		try {
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(), ano);
			reemplazar.put(TIPOCPTE, tipoMov);
			reemplazar.put(NUMEROCPTE, registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

			parametros.put(ComprobantecntsControladorEnum.PR_ACCION1_EN_ORDEN_DE_PAGO.getValue(),
					SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							ComprobantecntsControladorEnum.ACCION1_EN_ORDEN_DE_PAGO.getValue(), modulo, new Date(),
							true), ""));
			parametros.put("PR_CIUDADCOMPANIA", SessionUtil.getCompaniaIngreso().getCiudad());
			parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso().getNit());
			parametros.put(ComprobantecntsControladorEnum.PR_NOMBRECOMPANIA.getValue(),
					SessionUtil.getCompaniaIngreso().getNombre());

			Reporteador.resuelveConsulta("000664FACEQUIVALENTE", Integer.valueOf(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed("000664FACEQUIVALENTE", parametros, ConectorPool.ESQUEMA_SYSMAN,
					formato);
		} catch (JRException | IOException | SysmanException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	public boolean prepararImpresion() {
		if (accion.equals(ACCION_INSERTAR)) {
			JsfUtil.agregarMensajeError(idioma.getString(TB_TB443));
			return false;
		}
		try {
			Map<String, Object> paramTipo = new TreeMap<>();
			paramTipo.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			paramTipo.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos().get("TIPO"));
			Registro reg = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL47865.getValue())
											.getUrl(),
									paramTipo));
			boolean obliga = (boolean) reg.getCampos().get("OBLIGA_AFECT_PPTAL");

			if (obliga) {
				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.ANO.getName(), ano);
				param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
				param.put(GeneralParameterEnum.NUMERO.getName(),
						registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

				List<Registro> regAux = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL13478.getValue())
										.getUrl(),
								param));
				if (!regAux.get(0).getCampos().isEmpty()) {
					double vlrDocumento = SysmanFunciones.nvlDbl(
							registro.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()), 0);
					if (BigDecimal.valueOf(vlrDocumento).compareTo(BigDecimal.valueOf(
							SysmanFunciones.nvlDbl(regAux.get(0).getCampos().get("VALORDOCUMENTO"), 0))) != 0) {
						JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1753"));
						return false;
					}
				}
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// POR HPV EN CORPORINOQUIA EN SEP 22 de 2005
		if (!validarPacEgreso(false)) {
			return false;
		}
		return true;
	}

	/**
	 * Proceso que permite registrar la novedad en la tabla NOVEDADCONTRATO
	 */
	public void actualizarValorPagosEnComprobantesContables() {
		try {
			ejbContabilidadDos.actualizarValorPagosEnComprobantesContables(compania, Integer.parseInt(ano), tipoMov,
					new BigDecimal(SysmanFunciones
							.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "").toString()),
					(Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()),

					(Date) registro.getCampos().get(ComprobantecntsControladorEnum.FECHA_VCN_DOC.getValue()),
					BigDecimal.valueOf(SysmanFunciones.nvlDbl(
							registro.getCampos().get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()), 0)),
					BigDecimal.valueOf(SysmanFunciones
							.nvlDbl(registroIni.get(ComprobantecntsControladorEnum.VLR_DOCUMENTO.getValue()), 0)),
					SysmanFunciones
							.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.TIPOCONTRATO.getValue()), "")
							.toString(),
					BigDecimal.valueOf(SysmanFunciones.nvlDbl(
							registro.getCampos().get(ComprobantecntsControladorEnum.NUMEROCONTRATO.getValue()), 0)),
					clase, usuario);
		} catch (NumberFormatException | SystemException e) {
			e.printStackTrace();
		}
	}

	public void generaInformeFormatoComprobante(ReportesBean.FORMATOS formato, boolean niif) {
		String nombreCompania = "";
		String nitCompania = "";
		String companiaInforme;
		mensajesParametros = new ArrayList<>();
		if (!niif) {
			companiaInforme = compania;
			nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
			nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		} else {
			try {
				companiaInforme = ejbSysmanUtil.consultarParametro(compania, idioma.getString("TB_TB3370"), modulo,
						new Date(), true);
				if (companiaInforme == null) {
					JsfUtil.agregarMensajeError(idioma.getString("TB_TB1741"));
					return;
				}
				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), companiaInforme);

				List<Registro> regCompania = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL13482.getValue())
										.getUrl(),
								param));

				if (!regCompania.isEmpty()) {
					nombreCompania = SysmanFunciones
							.nvl(regCompania.get(0).getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
							.toString();
					nitCompania = SysmanFunciones.nvl(regCompania.get(0).getCampos().get("NIT"), "").toString();
				}
			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
				return;
			}
		}

		configurarParametrosReportes(companiaInforme, nombreCompania, nitCompania, formato);
	}

	private void configurarParametrosReportes(String companiaInforme, String nombreCompania, String nitCompania,
			FORMATOS formato) {
		archivoDescarga = null;
		try {
			Map<String, Object> parametros = new HashMap<>();
			Map<String, Object> reemplazar = new HashMap<>();
			// Remplazos Informe 001469COMSO
			

			// Fin reemplazos
			// reemplazar.put("compania", companiaInforme);
			reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(), ano);
			if(formatoComprobante.equals("002524RECIBODECAJAIDCBIS")) {
				reemplazar.put("tipoComprobante", tipoMov);
				reemplazar.put("compania", compania);
				reemplazar.put("comprobanteInicial", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
				reemplazar.put("comprobanteFinal", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
				
				parametros.put("PR_NOMBRECOMPANIA",SessionUtil.getCompaniaIngreso().getNombre());
				parametros.put("PR_NITCOMPANIA",SessionUtil.getCompaniaIngreso().getNit());
				parametros.put("PR_DIRECCION",SessionUtil.getCompaniaIngreso().getDireccion());
            	parametros.put("PR_TELEFONO",SessionUtil.getCompaniaIngreso().getTelefono());
            	
            	Reporteador.resuelveConsulta(formatoComprobante,//"002524RECIBODECAJAIDCBIS",
                		69, reemplazar, parametros);

            	archivoDescarga = JsfUtil.exportarStreamed(formatoComprobante,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
			}else{
				reemplazar.put("centroCosto", SysmanConstantes.CONS_CENTRO);
				reemplazar.put(TIPOCPTE, tipoMov);
				reemplazar.put("compania", compania);
				reemplazar.put(NUMEROCPTE, "= '" + registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()) + "'");
				reemplazar.put("numeroPptoInicial", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
				reemplazar.put("numeroPptoFinal", registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
				reemplazar.put("modulo", modulo);
				reemplazar.put("fechaInicial", SysmanFunciones.formatearFecha((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));
        		reemplazar.put("fechaFinal", SysmanFunciones.formatearFecha((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName()))); 
        		
    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
    		        "EEEE, d 'de' MMMM 'de' yyyy",
    		        new Locale("es", "CO"));
    		parametros.put("PR_FECHA_ACTUAL", LocalDate.now().format(formatter));
        		
			// 001890COMCAQ
			parametros.put("PR_NOMBRESESION", sesionuser);
			parametros.put("PR_NOMBRECOMPANIA", NomCompania);
			parametros.put("PR_NITCOMPANIA", NitCompania);
			parametros.put("PR_NOMBRE_DE_JEFE_DE_CONTABILIDAD", ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE JEFE DE CONTABILIDAD", modulo, new Date(), true));
			parametros.put("PR_CARGO_DE_JEFE_DE_CONTABILIDAD",
					ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE DE CONTABILIDAD", modulo, new Date(), true));
			parametros.put("PR_CARGO_1_COMPROBANTE_CONTABLE", ejbSysmanUtil.consultarParametro(compania,
					"CARGO1 COMPROBANTE CONTABLE", modulo, new Date(), true));

			parametros.put("PR_REVISO_EN_ORDEN_DE_PAGO",
					ejbSysmanUtil.consultarParametro(compania, "REVISO EN ORDEN DE PAGO", modulo, new Date(), true));

			parametros.put("PR_FIRMA_EN_INGRESO",
					ejbSysmanUtil.consultarParametro(compania, "FIRMA EN INGRESO", modulo, new Date(), true));

			parametros.put("PR_ACCION_EN_INGRESO",
					ejbSysmanUtil.consultarParametro(compania, "ACCION EN INGRESO", modulo, new Date(), true));

			parametros.put("PR_ACCION_EN_EGRESO",
					ejbSysmanUtil.consultarParametro(compania, "ACCION EN EGRESO", modulo, new Date(), true));

			parametros.put("PR_NOMBRE_JEFE_PRESUPUESTO", ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE JEFE DE PRESUPUESTO", modulo, new Date(), true));
			parametros.put("PR_CARGO_JEFE_PRESUPUESTO",
					ejbSysmanUtil.consultarParametro(compania, "CARGO PRESUPUESTO", modulo, new Date(), true));
			parametros.put("PR_NOMBRE_SECRETARIA_HACIENDA", ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE SECRETARIA DE HACIENDA", modulo, new Date(), true));
			parametros.put("PR_CARGO_SECRETARIA_HACIENDA", ejbSysmanUtil.consultarParametro(compania,
					"CARGO DE SECRETARIA DE HACIENDA", modulo, new Date(), true));
			parametros.put("PR_NOMBRE_SECRETARIO", ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE SECRETARIO", modulo, new Date(), true));
			parametros.put("PR_CARGO_CONTADOR", ejbSysmanUtil.consultarParametro(compania,
					"CARGO CONTADOR", modulo, new Date(), true));
			parametros.put("PR_CARGO_DIRECTOR_EJECUTIVO", ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DEL CARGO DIRECTOR EJECUTIVO", modulo, new Date(), true));
			
			parametros.put("PR_REVISADO_POR",
					ejbSysmanUtil.consultarParametro(compania, "REVISADO POR", modulo, new Date(), true));
			

			//

			Map<String, Object> valores = new HashMap<>();
			valores.put("informe", formatoComprobante);
			valores.put("formato", formato);
			valores.put("nombreCompania", nombreCompania);
			valores.put("nitCompania", nitCompania);
			valores.put("lote", false);
			valores.put("ordenador", ordenador);

			if (registro.getCampos().get(GeneralParameterEnum.FECHA.getName()) != null) {
				valores.put("fechas", SysmanFunciones
						.convertirAFechaCadena((Date) registro.getCampos().get(GeneralParameterEnum.FECHA.getName())));
			}
			
			archivoDescarga = comprobantesContPresReporteador.generarInforme(valores, parametros, reemplazar);
			
			}
		} catch (SystemException | ParseException | JRException | IOException | SysmanException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}

	public void ejecutaractualizaAlert() {
		if (mensajesParametros != null) {
			for (String mensaje : mensajesParametros) {
				JsfUtil.agregarMensajeAlerta(mensaje);
			}
		}
	}

	/**
	 * Proceso para actualizar el estado del check "impreso", que se bloquea y 
	 * se activa automï¿½ticamente al presionar el botï¿½n de generar PDF.
	 * <p>
	 * Este mï¿½todo gestiona la lï¿½gica de actualizaciï¿½n del estado de "impreso" 
	 * en funciï¿½n de la acciï¿½n especificada. Si la acciï¿½n es "VER", realiza la 
	 * actualizaciï¿½n mediante un servicio externo. En cualquier otro caso, 
	 * actualiza los datos directamente en el registro actual.
	 * </p>
	 * <p>
	 * TICKET: 7802946_CFBARRERA
	 * </p>
	 */
	public void ejecutaractualizaForm() {
	    // Validaciï¿½n de la acciï¿½n "VER"
		if (ACCION_VER.equals(accion) || imprimirPDF) {
	        try {
	            // Obtiene la URL y el mï¿½todo correspondiente para la actualizaciï¿½n
	            UrlBean urlUpdate = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL72123.getValue());

	            // Prepara los parï¿½metros para la actualizaciï¿½n
	            Map<String, Object> param = new HashMap<>();
	            param.put(ComprobantecntsControladorEnum.IMPRESO.getValue(), true);
	            //param.put("MODIFIED_BY", SessionUtil.getUser().getCodigo());
	            //param.put("DATE_MODIFIED", new Date());
	            param.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
	            param.put("KEY_ANO", registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
	            param.put("KEY_TIPO", registro.getCampos().get(GeneralParameterEnum.TIPO.getName()));
	            param.put("KEY_NUMERO", registro.getCampos().get(CAMPO_NUMERO));

	            // Crea el objeto de parï¿½metros
	            Parameter campos = new Parameter();
	            campos.setFields(param);

	            // Realiza la actualizaciï¿½n a travï¿½s del servicio
	            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), campos);

	            // Recarga el registro y actualiza alertas
	            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
	            ejecutaractualizaAlert();
	        } catch (SystemException e) {
	            // Manejo de errores
	            JsfUtil.agregarMensajeError(e.getMessage());
	            logger.error(e.getMessage(), e);
	        }
	        // Fin  TICKET: 7802946_CFBARRERA
	    } else {
	        // Actualiza directamente el registro y ejecuta acciones adicionales
	        registro.getCampos().put(ComprobantecntsControladorEnum.IMPRESO.getValue(), true);
	        agregarRegistroNuevo(false);
	        cambiarImpreso();
	        ejecutaractualizaAlert();
	    }
		imprimirPDF=false;
	}


	public void generaInformeCheque(ReportesBean.FORMATOS formato, boolean horizontal) {
		archivoDescarga = null;
		try {
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(), ano);
			reemplazar.put(TIPOCPTE, tipoMov);
			reemplazar.put(NUMEROCPTE, registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			reemplazar.put("modulo", modulo);
			reemplazar.put("nombreCompania", SessionUtil.getCompaniaIngreso().getNombre());

			Reporteador.resuelveConsulta("000749Cheque", Integer.valueOf(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(horizontal ? "000750ChequeH" : "000749Cheque", parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>

		validarTercero();
		if(!alertaFactura()) {
			return false;
		}else if (validarRetencion() && validarTipoPagoSIA()) {

			registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registro.getCampos().remove("REVISA");
			registro.getCampos().remove("LIQUIDA");

			BigInteger consecutivo = null;
			if ("0".equals(SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "0")
					.toString())) {
				try {
					consecutivo = ejbContabilidadCpte.enumerarComprobanteCnt(compania, Integer.parseInt(ano), tipoMov,
							BigInteger.ZERO,
							registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()).toString());
					registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), consecutivo);
				} catch (NumberFormatException | SystemException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}

			}

			if ("".equals(SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()), "")))

			{
				registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(), SysmanConstantes.CONS_TERCERO);
				registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), SysmanConstantes.CONS_SUCURSAL);
			}
			// </CODIGO_DESARROLLADO>
			
			if("SI".equals(obtenerParametro("PERMITIR ESPACIOS ESPECIALES EN DESCRIPCION CONTABLE","NO"))) {

        			String descripcion = registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString();
        	                descripcion = descripcion.replaceAll("\'", "");
        	                descripcion = descripcion.replaceAll("\"", "");
        	                registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), descripcion);
        	                
        	                String texto = registro.getCampos().get(GeneralParameterEnum.TEXTO.getName()).toString();
                                texto = texto.replaceAll("\'", "");
                                texto = texto.replaceAll("\"", "");
                                registro.getCampos().put(GeneralParameterEnum.TEXTO.getName(), texto);
			 
			}else {
			    
        			String descripcion = registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString()
        					.replaceAll("\r\n", "");
        			descripcion = descripcion.replaceAll("\'", "");
        			descripcion = descripcion.replaceAll("\"", "");
        			registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), descripcion);
        
        			String texto = registro.getCampos().get(GeneralParameterEnum.TEXTO.getName()).toString().replaceAll("\r\n",
        					"");
        			texto = texto.replaceAll("\'", "");
        			texto = texto.replaceAll("\"", "");
        			registro.getCampos().put(GeneralParameterEnum.TEXTO.getName(), texto);
			}

			if (bloqConceptoCUDS && consecutivo != null) {
				// obteiene el cosnecutivo maximo usado y le hace el aunmeto de 1 y se lo asigna
				// al campo.
				// se valida si esta o no tiene uno asigando toma el de
				Registro rs = new Registro();
				Registro rs1 = new Registro();
				String numeroResolucion = null;
				String consecutivoIniRes = "1";
				Long consecDianTemp = new Long("0");
				try {
					Map<String, Object> param = new TreeMap<>();
	
					param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);
					
					// obtenemos el nï¿½mero de resolucion configurado
					rs = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1896005.getValue())
											.getUrl(),
									param));
					
					if (rs != null) {
						numeroResolucion = SysmanFunciones.nvl(rs.getCampos().get("NUMERO"), "").toString();
						prefijo = SysmanFunciones.nvl(rs.getCampos().get("PREFIJO"), "").toString();
						consecutivoIniRes = SysmanFunciones.nvl(rs.getCampos().get("CONSECUTIVOINI"), "1").toString();
					}
					
					// obtenemos el nï¿½mero de consecutivo dian 
					
					Map<String, Object> param2 = new TreeMap<>();
					
					param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param2.put(GeneralParameterEnum.CODIGO.getName(), tipoMov);
					param2.put(GeneralParameterEnum.PREFIJO.getName(), prefijo);
					param2.put(GeneralParameterEnum.NUMERO.getName(), numeroResolucion);
					
					rs1 = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL1896006.getValue())
											.getUrl(),
									param2));
					if(rs1.getCampos().get(GeneralParameterEnum.CONSECDIAN.getName()) != null) {
						consecDianTemp = new Long(rs1.getCampos().get(GeneralParameterEnum.CONSECDIAN.getName()).toString()) + 1;
					}else {
						consecDianTemp = Long.valueOf(consecutivoIniRes);
					}
					
				} catch (SystemException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
					return false;
				}
				
				registro.getCampos().put("CONSECUTIVODIAN", consecDianTemp);
				registro.getCampos().put("PREFIJODIAN", prefijo);
				resolucionDian = numeroResolucion;
				registro.getCampos().put("RESOLUCIONDIAN", resolucionDian);
			}
			if (controlaLongitud) {
				String largoConsecutivo = SysmanFunciones.nvl(
						registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "").toString();
				if (largoConsecutivo.length() != 10) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4456"));
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean validarRetencion() {

		if (ley1819 && pideRetencion
				&& SysmanFunciones.validarVariableVacio(SysmanFunciones
						.nvl(registro.getCampos().get(ComprobantecntsControladorEnum.NRO_DOCUMENTO.getValue()), "")
						.toString())) {

			JsfUtil.agregarMensajeError(idioma.getString("TB_TB4316"));

			return false;
		} else {
			return true;
		}
	}

	private boolean validarTipoPagoSIA() {
		try {
			String parametro = "";
			parametro = (String) SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"TIPO COMPROBANTES PAGO SIA OBLIGATORIO", modulo, new Date(), true), "NO");

			if ((("SI"
					.equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
							"TIPO PAGO SIA OBLIGATORIO EN TESORERIA", modulo, new Date(), true), "NO"))
					&& !registro.getCampos().get("TIPOPAGO_SIA").equals("")
					&& ",".concat(parametro).concat(",").indexOf(",".concat(tipoMov).concat(",")) >= 0)

					|| "NO".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
							"TIPO PAGO SIA OBLIGATORIO EN TESORERIA", modulo, new Date(), true), "NO")))) {
				return true;
			} else {
				throw new SystemException("El tipo de pago sia es obligatorio para este comprobante" + " " + tipoMov);
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		bloqueaNumero = true; // JM 10/10/2024
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// se valida el tipo de comprobante para ver si es o no un documento soporte o
		// nota de ajuste enviado correctamente
		if (accion.equals(ACCION_MODIFICAR)) {
			if (yaEnviadoDianExito) {
				JsfUtil.agregarMensajeError(idioma.getString("MSG_DOCUMENTO_ENVIADO_EDIT"));
				return false;
			}
		}
		if(!alertaFactura()) {
			return false;
		}else if (validarTipoPagoSIA()) {
			cargarDescripcion();
			registro.getCampos().remove("REVISA");
			registro.getCampos().remove("LIQUIDA");

			guardaRegistro = true;
			if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.AUXILIAR.getName())) {
				registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), SysmanConstantes.CONS_AUXILIAR);
			}

			if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.CENTRO_COSTO.getName())) {
				registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(), SysmanConstantes.CONS_CENTRO);
			}

			if (SysmanFunciones.validarCampoVacio(registro.getCampos(), GeneralParameterEnum.REFERENCIA.getName())) {
				registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(), SysmanConstantes.CONS_REFERENCIA);
			}

			registroPreAct = new Registro();
			registroPreAct.setCampos(new HashMap<>(registro.getCampos()));
			if (!accion.equals(ACCION_INSERTAR)) {
				registroIniPreAct = new Registro();
				registroIniPreAct.setCampos(new HashMap<>(registroIni));
				
				if("SI".equals(obtenerParametro("PERMITIR ESPACIOS ESPECIALES EN DESCRIPCION CONTABLE","NO"))) {
				        String descripcion = registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString();
	                                descripcion = descripcion.replaceAll("\'", "");
	                                descripcion = descripcion.replaceAll("\"", "");
	                                registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), descripcion);
	                    Object valorTexto = registro.getCampos().get(GeneralParameterEnum.TEXTO.getName());
	                	if (valorTexto != null) {
	                                String texto = valorTexto.toString();
	                                texto = texto.replaceAll("\'", "");
	                                texto = texto.replaceAll("\"", "");
	                                registro.getCampos().put(GeneralParameterEnum.TEXTO.getName(), texto);
	                	}
	                                
				}else {
        				String descripcion = registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString()
        						.replaceAll("\r\n", "");
        				descripcion = descripcion.replaceAll("\'", "");
        				descripcion = descripcion.replaceAll("\"", "");
        				registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), descripcion);
        				
        				Object valorTexto = registro.getCampos().get(GeneralParameterEnum.TEXTO.getName());
        				if (valorTexto != null) {
        					String texto = valorTexto.toString().replaceAll("\r\n", "");
                                        texto = texto.replaceAll("\'", "");
                                        texto = texto.replaceAll("\"", "");
                                        registro.getCampos().put(GeneralParameterEnum.TEXTO.getName(), texto);
        				}               
				}

				
			}
			registro.getCampos().remove(ComprobantecntsControladorEnum.AUTORETENEDOR.getValue());
			registro.getCampos().remove(ComprobantecntsControladorEnum.REGIMEN.getValue());
			registro.getCampos().remove(ComprobantecntsControladorEnum.TIPOCOBROCONCEPTO.getValue());
			registro.getCampos().remove(LEY1450);
			registro.getCampos().remove(CTECOMPACOPIAR);
			//
			// agregados
			registro.getCampos().remove("ABONADO");
			registro.getCampos().remove("TERCERO_NSESION");
			registro.getCampos().remove(GeneralParameterEnum.FUENTE_RECURSO.getName());
			registro.getCampos().remove(ComprobantecntsControladorEnum.CREDITOSAFECTADOS.getValue());

			registro.getCampos().remove("SUCURSAL_NSESION");
			registro.getCampos().remove("PROGRAMADO");
			registro.getCampos().remove("TIPODEPRESUPUESTO");
			registro.getCampos().remove("FECHA_MODIFICADOR_CH");
			registro.getCampos().remove("USUARIO_ABONO");
			registro.getCampos().remove("MODIFICADOR_CH");
			registro.getCampos().remove(ComprobantecntsControladorEnum.DEBITOSAFECTADOS.getValue());
			registro.getCampos().remove("CONSECUTIVOWF");
			registro.getCampos().remove("DEBITOSAFECTADOS_CXP");
			registro.getCampos().remove("CREDITO");
			registro.getCampos().remove("TASADECAMBIO");
			registro.getCampos().remove(ComprobantecntsControladorEnum.REGISTRO.getValue());
			registro.getCampos().remove("PAGADOBANCO");
			registro.getCampos().remove("FECHAPROGPAGO");
			registro.getCampos().remove("FECHA_ABONO");
			registro.getCampos().remove("FECHARECEPCION");
			registro.getCampos().remove("DIA");
			registro.getCampos().remove("MES");
			registro.getCampos().remove("DEBITO");
			registro.getCampos().remove("ABONOCXP");
			registro.getCampos().remove(GeneralParameterEnum.ANULADO.getName());
			registro.getCampos().remove("NOMBRETERCERO");
			registro.getCampos().remove("CREDITOSAFECTADOS_CXP");
			registro.getCampos().remove("NOMBRE_PROYECTO");
			registro.getCampos().remove(LEY1450);
			registro.getCampos().remove("LEY1819");
			if (!accion.equals("i")) {
				registro.getCampos().remove("PREFIJODIAN");
				registro.getCampos().remove("CONSECUTIVODIAN");
				registro.getCampos().remove("RESOLUCIONDIAN");
			}
			validarTercero();
			if (css != null) {
				registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

				registro.getCampos().remove(GeneralParameterEnum.ANO.getName());

				registro.getCampos().remove(TIPO);
				registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
			}

			terceroAfterUpdate();
			
			// </CODIGO_DESARROLLADO>
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		if (registroIniPreAct == null) {
			return true;
		}

		if (!registroIniPreAct.getCampos().get(GeneralParameterEnum.TERCERO.getName())
				.equals(registroPreAct.getCampos().get(GeneralParameterEnum.TERCERO.getName()))) {
			terceroAfterUpdate();
		}
		
		actualizarValorPagosEnComprobantesContables();
		
		registro.setCampos(registroPreAct.getCampos());
		registroPreAct = null;
		registroIniPreAct = null;
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 *
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo dgCompPptal en la
	 * vista
	 *
	 *
	 */
	public void aceptardgCompPptal() {
		// <CODIGO_DESARROLLADO>
		try {
			String cuentasBancarias = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CONTROLA CUENTA BANCARIA Y RUBRO PRESUPUESTAL", modulo, new Date(), true), "NO");

			if ("SI".equals(cuentasBancarias)) {
				ejbContabilidadSiete.validarCuentasEquivalentes(compania, Integer.parseInt(ano), tipoMov,
						Long.parseLong(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()));

			}
			oprimirPidePptal();
		} catch (NumberFormatException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

		// </CODIGO_DESARROLLADO>
	}

	public void cargarDescripcion() {

		Map<String, String> param = (HashMap<String, String>) SessionUtil.getSessionVar("descripcionVlr");
		if (param != null) {
			registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), param.get("descripcion"));
			registro.getCampos().put("VLR_DOCUMENTO", param.get("vlrDocumento"));
			// SessionUtil.cleanFlash();
			SessionUtil.setSessionVar("descripcionVlr", null);
		}

		// try {
		//
		// Map<String, Object> parametrosEntrada =
		// SessionUtil.getFlash();
		// if (parametrosEntrada != null) {
		// rid = (Map<String, Object>) parametrosEntrada.get("rid");
		// urlLectura = UrlServiceUtil.getInstance()
		// .getUrlServiceByUrlByEnumID(
		// GenericUrlEnum.COMPROBANTE_CNT
		// .getReadKey());
		//
		// registro = RegistroConverter.toRegistro(
		// requestManager.get(urlLectura.getUrl(), rid));
		//
		// descri =
		// registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName());
		// Map<String, Object> param = new HashMap<>();
		//
		// param.put("descripcion", descri);
		//
		// SessionUtil.setSessionVar("descrVlr", param);
		//
		//
		// // actualizarAntes();
		// }
		//
		// } catch (SystemException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	/**
	 *
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo dgCompPptal en la
	 * vista
	 *
	 *
	 */
	public void cancelardgCompPptal() {
		// <CODIGO_DESARROLLADO>
		visibleDgCompPptal = false;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo
	 * ConfirmacionEliminacionComprobPptal en la vista
	 *
	 */
	public void aceptarDgConfirmacion() {
		// <CODIGO_DESARROLLADO>
		BigInteger numeroCpte = new BigInteger(
				registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
		String mensajeTiempo = " ";
		try {
			int segundosIni = LocalTime.now().getSecond();
			ejbContabilidadUno.eliminarComprobantePresupuestal(compania, Integer.parseInt(ano), tipoMov, numeroCpte,
					SessionUtil.getUser().getCodigo());
			int segundosFin = LocalTime.now().getSecond();
			if (segundosFin < segundosIni) {
				segundosFin = segundosFin + 60;
			}
			int tiempoEjecucion = segundosFin - segundosIni;
			mensajeTiempo = "Se eliminÃ³ exitosamente el comprobante " + numeroCpte + " en un tiempo de "
					+ tiempoEjecucion + " segundos.";

			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3823"));
			JsfUtil.agregarMensajeInformativo(mensajeTiempo);
			visibleDgConfirmacion = false;

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar del dialogo
	 * DgConfirmacionEliminacionComprobPptal en la vista
	 *
	 */
	public void cancelarDgConfirmacion() {
		// <CODIGO_DESARROLLADO>
		visibleDgConfirmacion = false;
		// </CODIGO_DESARROLLADO>
	}

	@Override
	public boolean eliminarAntes() {

		String numero = registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
		Registro regAux;
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipoMov);
			// Se valida como primera opcion que sea un documento soporte o nota de ajuste
			// enviado con exito
			if (bloqConceptoCUDS) {
				String consecutivoDianTemp = String.valueOf(numero).split(ano)[1];
				Long consecDianTemp = Long.valueOf(consecutivoDianTemp);
				regAux = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL15078.getValue())
										.getUrl(),
								param));

				if (consultarEstadoDocDian(
						regAux.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString().equals("05")
								? consecDianTemp.toString()
								: numero,
						regAux.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString(),
						regAux.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString().equals("05")
								? registro.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString()
								: "NA")) {
					JsfUtil.agregarMensajeError(idioma.getString("MSG_DOCUMENTO_ENVIADO"));
					return false;
				}
			}
			param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(EliminarComprobanteControladorEnum.TIPO.getValue(), tipoMov);
			param.put(GeneralParameterEnum.NUMERO.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
			
			regAux = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL5330.getValue())
											.getUrl(),
									param));

			if (Integer.parseInt(SysmanFunciones
					.nvlStr(regAux.getCampos().get(GeneralParameterEnum.CUENTA.getName()).toString(), "0")) > 0) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB492"));
				return false;
			}
			String mensajeTiempo = " ";
			long minutos = 0;
			long segundos = 0;
			LocalTime tiempoInicial = LocalTime.now();
			ejbContabilidadCinco.eliminarComprobantesCNT(compania, Integer.parseInt(ano), tipoMov,
					new BigInteger(registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString()),
					SessionUtil.getUser().getCodigo());
			LocalTime tiempoFinal = LocalTime.now();
			Duration diferencia = Duration.between(tiempoInicial, tiempoFinal);
			minutos = diferencia.toMinutes() % 60;
			segundos = diferencia.getSeconds() % 60;
			if (minutos == 0) {
				mensajeTiempo = "Se ha eliminado exitosamente el comprobante tipo " + tipoMov + " numero " + numero
						+ " en un tiempo de " + segundos + " segundos";
			} else {
				mensajeTiempo = "Se ha eliminado exitosamente el comprobante tipo " + tipoMov + " numero " + numero
						+ " en un tiempo de " + minutos + " minutos " + segundos + " segundos";
			}
			JsfUtil.agregarMensajeInformativo(mensajeTiempo);
			
			//CAUSACION AUTOMATICA
			if (manejaCausacion) {
				int delete = 0;
				Map<String, Object> paramc = new TreeMap<>();
				paramc.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
				paramc.put(GeneralParameterEnum.KEY_ANO.getName(), ano);
				paramc.put(GeneralParameterEnum.KEY_COMPROBANTE.getName(),
						registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
				paramc.put(GeneralParameterEnum.KEY_TIPO_CPTE.getName(),
						registro.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString());

				UrlBean urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL1933003.getValue());

				delete = requestManager.delete(urlBean.getUrl(), paramc);
			}
			//
			if (registro.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString().equals("webservice")) {
				// se elimina el detalle del comprobante pero de la tabla detalle_cpte_afect_ws
				try {
					int delete = 0;

					Map<String, Object> param2 = new TreeMap<>();
					param2.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
					param2.put(GeneralParameterEnum.KEY_ANO.getName(), ano);
					param2.put(GeneralParameterEnum.KEY_COMPROBANTE.getName(),
							registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString());
					param2.put(GeneralParameterEnum.KEY_TIPO_CPTE.getName(),
							registro.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString());

					UrlBean urlBean = UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL1914001.getValue());

					delete = requestManager.delete(urlBean.getUrl(), param2);
				} catch (SystemException e) {
					Logger.getLogger(SubdetallecomprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
			}
		} catch (NumberFormatException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public void ejecutarAlfallarInsercionActualizacion() {

		if (accion.equals(ACCION_INSERTAR)) {
			registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), null);
		}
	}

	/**
	 * Extrae la cadena que representa al objeto, solo si es diferente de nulo.
	 *
	 * @param object Un Objeto
	 * @return String que representa al objeto
	 */
	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}

	/**
	 * Evalua el valor del parametro "PERMITE ELIMINAR COMPROBANTE PRESUPUESTAL" y
	 * si existe el comprobante Presuspuestal equivalente al comprobante contable
	 * que se esta trabajando
	 */
	private void evaluarCargaEliminarPptal() {
		try {
			Map<String, Object> params = new TreeMap<>();
			params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			params.put(GeneralParameterEnum.ANO.getName(), ano);
			params.put(ComprobantecntsControladorEnum.TIPO.getValue(), tipoMov);
			params.put(GeneralParameterEnum.NUMERO.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL33614.getValue())
											.getUrl(),
									params));

			boolean parametroEliminar = "SI"
					.equalsIgnoreCase(
							SysmanFunciones.nvl(
									ejbSysmanUtil.consultarParametro(compania,
											"PERMITE ELIMINAR COMPROBANTE PRESUPUESTAL", modulo, new Date(), true),
									"NO").toString());
			boolean cargarBoton = Integer
					.parseInt(rs.getCampos().get(GeneralParameterEnum.CUENTA.getName()).toString()) != 0 ? true : false;

			cargarEliminarCptePptal = parametroEliminar && cargarBoton;
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	public void obtenerOrdenador() {
		ordenador = "";
		try {
		Map<String, Object> params = new TreeMap<>();
		
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put("CEDULAORDENADOR", registro.getCampos().get("ORDENADOR"));
		
		Registro rs = RegistroConverter
				.toRegistro(
						requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL84008.getValue())
										.getUrl(),
								params));
		
		if (rs != null) {
			ordenador = SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CARGO.getName()),"").toString();
		}
		
		} catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
		}
		
	}

	public void validarTercero() {
		int conteo = 0;
		try {

			String terceroPorMes = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"VALIDA TERCERO POR MES EN COMPROBANTE CNT", modulo, new Date(), true), "NO").toString();

			if (terceroPorMes.equals("SI")) {
				if (tipoMov.equals("FCC") || tipoMov.equals("FCF") || tipoMov.equals("FCP")) {

					Map<String, Object> param = new HashMap<>();
					param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param.put(GeneralParameterEnum.ANO.getName(), ano);
					param.put(GeneralParameterEnum.MES.getName(), mes);
					param.put(GeneralParameterEnum.TIPO.getName(), tipoMov);
					param.put(GeneralParameterEnum.TERCERO.getName(),
							registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
					param.put(GeneralParameterEnum.SUCURSAL.getName(),
							registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
					param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get("NUMERO"));

					rsTercero = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL677.getValue())
											.getUrl(),
									param));

					if (rsTercero != null) {

						conteo = Integer.parseInt(rsTercero.getCampos().get("EXISTE").toString());

						if (conteo >= 1) {

							JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4359")
									.replace("s$tercero$s",
											registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString())
									.replace("s$mes$s",
											SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)]));

						}
					}

				}
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * valida si el usuario tiene asignado el grupo del parmetro NOMBRE GRUPO
	 * TESORERIA para poder ver el botn de imputacin contable
	 **/
	public boolean grupoTesoreriaValido() {
		boolean visible = false;
		try {

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.USUARIO.getName(), usuario);
			param.put(GeneralParameterEnum.MODULO.getName(), modulo);

			List<Registro> rs = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL5332.getValue())
											.getUrl(),
									param));

			if ((rs != null) && !rs.isEmpty()) {
				visible = true;

			} else {

				visible = false;

			}

		} catch (SystemException ex) {
			Logger.getLogger(ComprobantecntsControladorEnum.class.getName()).log(Level.SEVERE, null, ex);
		}
		return visible;
	}

	/**
	 * comprueba si el usuario tiene permisos para seleccionar cuentas
	 **/
	public void comprobarpermisoscuentas() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("USUARIO", usuario);

		try {
			gruposasignados = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubdetallecomprobantecntsControladorUrlEnum.URL29152.getValue())
									.getUrl(),
							param));
			String grupos = sysmanUtil.consultarParametro(compania, "NOMBRE GRUPO TESORERIA", modulo, new Date(), true);

			String[] asignados = grupos.split(",");

			for (int i = 0; i < gruposasignados.size(); i++) {
				String grupo = gruposasignados.get(i).getCampos().get("GRUPO").toString();

				for (String asignado : asignados) {
					if (grupo.equals(asignado)) {
						bloqueacuentas = false;
						System.out.println(asignado + "<<<<<bloqueado false");
						break;
					}
				}
				if (!bloqueacuentas)
					break;
			}

			System.out.println("grupos:" + grupos);

			System.out.println("gruposasignados:" + gruposasignados.get(0).getCampos().get("GRUPO").toString());
		} catch (SystemException e) {
			Logger.getLogger(SubdetallecomprobantecntsControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * metodo por el cual se le realizara la consulta a los docmentos soporte y
	 * notas de ajuste
	 * 
	 * @param numeroFactura
	 * @param clase
	 * @param prefijoFactura
	 * @return
	 */
	private boolean consultarEstadoDocDian(String numeroFactura, String clase, String prefijoFactura) {
		String respuesta;
		RespuestaFormatoConsultas respuestaApi;
		try {
			String url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
			} else {

				Registro rs = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												FrmestadodocsoporteControladorUrlEnum.URL9457.getValue())
										.getUrl(),
								null));

				if (rs != null) {

					File archivo = new File(rs.getCampos().get("RUTA_CERTIFICADO").toString());

					String nombreCertificado = archivo.getName();

					byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

					String certificado = Base64.getEncoder().encodeToString(archivoBytes);

					String passCertificado = Base64.getEncoder()
							.encodeToString(rs.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());

					APIFrida api = new APIFrida();

					respuesta = api.postFormatoConsultas(url, nitSinDigito, clase, numeroFactura, prefijoFactura,
							nombreCertificado, certificado, passCertificado);

					Gson gson = new Gson();

					respuestaApi = gson.fromJson(respuesta, RespuestaFormatoConsultas.class);

					if (respuestaApi.getCuerpo().isIsValid()) {
						yaEnviadoDianExito = true;
						return true;
					} else {
						yaEnviadoDianExito = false;
						return false;
					}

				} else {
					JsfUtil.agregarMensajeAlerta(
							"Asegurese de configurar el certificado en el formulario CONFIGURACION CERTIFICADO DIAN");
					yaEnviadoDianExito = false;
					return false;

				}
			}

		} catch (SystemException | IOException | com.sysman.util.SysmanException e) {
			// se hace manejo de la excepcion pues en caso de no existir no mostrar mensaje,
			// ni reportar error, pues en si solo se activa en caso de existir la factura
			logger.error(e.getMessage());
			return false;
		}
		return true;

	}
	
public void envioNotificaciones() {
		
		try {

		String  correo                = "";
		String  correDescrip          = "";
		String  strAsunto             = "";
		String  correDescripFinal     = "";
		int  claseSolicitudCodigo     = 0;
		String emailFrom = "";
		String fechaVenc, fecha = null;
		
         fecha = SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get("FECHA"));
         fechaVenc = SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get("FECHA_VCN_DOC"));
		

		strAsunto = "CreaciÃ³n de Comprobante de Anticipo";
		correDescrip = SysmanFunciones.toString(ejbSysmanUtil.consultarParametro(compania,
				"CUERPO DEL CORREO CREACION ANTICIPOS", modulo, new Date(), false));
		if(correDescrip != null) {
		Map<String, Object> param = new TreeMap<>();
		param.put("nombreTercero", convertToCamelCase(nombreTercero).trim());
		param.put("comprobante", registro.getCampos().get("NUMERO"));
		param.put("valor", registro.getCampos().get("VLR_DOCUMENTO"));
		param.put("fecha", fecha);
		param.put("fechaVencimiento", fechaVenc);
		param.put("descripcion", registro.getCampos().get("DESCRIPCION"));
		

		correDescripFinal =   SysmanFunciones.remplazarVariableCorreo( correDescrip,
				param);
		
		correo = extraerCorreo();

		EmailPojo email = new EmailPojo();
		email.setFrom("CONTABILIDAD");
		email.setSubject(strAsunto);
		email.setBody(correDescripFinal);
		email.setTo(correo);
		ApiRestClient client = new ApiRestClient();
		try {
			client.postClient(email,compania);
		}catch (IOException  e) {

			logger.error(e.getMessage(), e);
		}
		}else {
			JsfUtil.agregarMensajeError("El parametro CUERPO DEL CORREO CREACION ANTICIPOS esta vacio.");
		}
        
		
        } catch (ParseException | SystemException ex) {
        	Logger.getLogger(SubdetallecomprobantecntsControlador.class.getName()).log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}


    	
	}
	
	
	 public static String convertToCamelCase(String input) {
	        // Dividimos la cadena por espacios y/o guiones
	        String[] words = input.split("[\\s-]+");

	        // Construimos el resultado en camelCase
	        StringBuilder camelCaseString = new StringBuilder();
	        for (int i = 0; i < words.length; i++) {
	            String word = words[i];
	            // Capitalizamos la primera letra de cada palabra
	            camelCaseString.append(Character.toUpperCase(word.charAt(0)));
	            if (word.length() > 1) {
	            	
	                camelCaseString.append(word.substring(1).toLowerCase());
	                camelCaseString.append(" ");
	            }
	            
	        }

	        return camelCaseString.toString();
	    }
	 
	 public String extraerCorreo() {
		 String correo = null;
		 String tercero = null;
		 try {
		tercero = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
		 Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.NIT.getName(), tercero);

			
				Registro rs = RegistroConverter
						.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														ComprobantecntsControladorUrlEnum.URL14168.getValue())
												.getUrl(),
										param));
				if(rs != null) {
					
					correo = SysmanFunciones.toString(rs.getCampos().get("DIRECCIONEMAIL"));
					
					
					if(correo == null) {
						
						JsfUtil.agregarMensajeAlerta(
								"Asegurese de configurar el campo DirecciÃ³n E-Mail del tercero: " + tercero);
						return null;
					}
				}
				
				
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 return correo;
		 
	 }
	 
	 private void validarDetallesCausacionAutomatica() {

		 UrlBean urlBean = UrlServiceUtil.getInstance()
				 .getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL1933009.getValue());
		 Map<String, Object> param = new TreeMap<>();
		 param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		 param.put(GeneralParameterEnum.ANO.getName(), ano);
		 param.put(GeneralParameterEnum.TIPOCPTE.getName(), tipoMov);
		 param.put(GeneralParameterEnum.NUMERO.getName(),
				 registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));

		 int conteo;
		 try {
			 conteo = Integer.parseInt(requestManager.get(urlBean.getUrl(), param).getFields()
					 .get("CONTEO").toString());
			 if (conteo > 0) {
				 bloqueaObjetos = true;
			 } 
		 } catch (SystemException e) {
			 logger.error(e.getMessage(), e);
			 JsfUtil.agregarMensajeError(e.getMessage());
		 }
	 }
	 
	 /**
	  * Metodo ejecutado desde un comando remoto en el boton volver del
	  * formulario
	  * 
	  * TODO DOCUMENTACION ADICIONAL
	  */
	 public void ejecutarrcVolver(){
		 //<CODIGO_DESARROLLADO>
		 
		 try {
			 if (parametrosAlm != null) {
				 
				 Map<String, Object> parametros = new HashMap<>();
		            // parametros.put("estadoAlmacen", estadoAlmacen);
		            parametros.put("anoMov", parametrosAlm.get("anoMov"));
		            parametros.put("mesMov", parametrosAlm.get("mesMov"));
		            parametros.put("tipoMov", parametrosAlm.get("tipoMov"));
		            parametros.put("fechaCorte", parametrosAlm.get("fechaCorte"));
		            parametros.put("claseDocAsoc", parametrosAlm.get("anoMov"));
		            parametros.put("inventarioInicial", parametrosAlm.get("inventarioInicial"));
		            parametros.put("claseMov", parametrosAlm.get("claseMov"));
		            parametros.put("tipoElementoMov", parametrosAlm.get("tipoElementoMov"));
		            parametros.put("tituloForm", parametrosAlm.get("tituloForm"));
		            parametros.put("cptoMov", parametrosAlm.get("cptoMov"));
		            parametros.put("manVtas", parametrosAlm.get("manVtas"));
		            parametros.put("claseDocAsMov", parametrosAlm.get("claseDocAsMov"));
		            parametros.put("generaPlaca", parametrosAlm.get("generaPlaca"));
		            parametros.put("tipoPersona", parametrosAlm.get("tipoPersona"));
		            parametros.put("pideCCto", parametrosAlm.get("pideCCto"));
		            parametros.put("nroInicial", parametrosAlm.get("nroInicial"));
		            parametros.put("claseBodOrig", parametrosAlm.get("claseBodOrig"));
		            parametros.put("claseBodDest", parametrosAlm.get("claseBodDest"));
		            parametros.put("obligaOrigen", parametrosAlm.get("obligaOrigen"));
		            parametros.put("obligaDestino", parametrosAlm.get("obligaDestino"));
		            parametros.put("obligaTercero", parametrosAlm.get("obligaTercero"));
		            parametros.put("obligaProveedor", parametrosAlm.get("obligaProveedor"));
		            parametros.put("igualarOrigDest", parametrosAlm.get("igualarOrigDest"));
		            parametros.put("obligaCampos", parametrosAlm.get("obligaCampos"));
		            parametros.put("bloqueaAux", parametrosAlm.get("bloqueaAux"));
		            parametros.put("rid", parametrosAlm.get("rid"));
		            
		            Direccionador direccionador = new Direccionador();
		            direccionador.setNumForm("469");
		            direccionador.setParametros(parametros);
		            SessionUtil.removeSessionVarContainer("parametrosAlm");
		            SessionUtil.redireccionarForma(direccionador, "10");
			 }
	       
		 } catch (NamingException e) {
	    		logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	    	}
		}

	public List<Registro> getListaTipoPagoL() {
		return listaTipoPagoL;
	}

	public void setListaTipoPagoL(List<Registro> listaTipoPagoL) {
		this.listaTipoPagoL = listaTipoPagoL;
	}

	public List<Registro> getListaTipoContrato() {
		return listaTipoContrato;
	}

	public void setListaTipoContrato(List<Registro> listaTipoContrato) {
		this.listaTipoContrato = listaTipoContrato;
	}

	public RegistroDataModelImpl getListaTIPOPAGOSIA() {
		return listaTIPOPAGOSIA;
	}

	public void setListaTIPOPAGOSIA(RegistroDataModelImpl listaTIPOPAGOSIA) {
		this.listaTIPOPAGOSIA = listaTIPOPAGOSIA;
	}

//	public List<Registro> getListaCODISIA() {
//		return listaCODISIA;
//	}
//
//	public void setListaCODISIA(List<Registro> listaCODISIA) {
//		this.listaCODISIA = listaCODISIA;
//	}

	public RegistroDataModelImpl getListaCODPROYECTO() {
		return listaCODPROYECTO;
	}

	public void setListaCODPROYECTO(RegistroDataModelImpl listaCODPROYECTO) {
		this.listaCODPROYECTO = listaCODPROYECTO;
	}

	public RegistroDataModelImpl getListaDependencia() {
		return listaDependencia;
	}

	public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
		this.listaDependencia = listaDependencia;
	}

	public RegistroDataModel getListaNumero() {
		return listaNumero;
	}

	public void setListaNumero(RegistroDataModel listaNumero) {
		this.listaNumero = listaNumero;
	}

	public RegistroDataModelImpl getListaTercero() {
		return listaTercero;
	}

	public void setListaTercero(RegistroDataModelImpl listaTercero) {
		this.listaTercero = listaTercero;
	}

	public RegistroDataModelImpl getListaNumeroContrato() {
		return listaNumeroContrato;
	}

	public void setListaNumeroContrato(RegistroDataModelImpl listaNumeroContrato) {
		this.listaNumeroContrato = listaNumeroContrato;
	}

	public RegistroDataModelImpl getListaCompACopiar() {
		return listaCompACopiar;
	}

	public void setListaCompACopiar(RegistroDataModelImpl listaCompACopiar) {
		this.listaCompACopiar = listaCompACopiar;
	}

	public RegistroDataModelImpl getListaCuenta() {
		return listaCuenta;
	}

	public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
		this.listaCuenta = listaCuenta;
	}

	public RegistroDataModelImpl getListaBanco() {
		return listaBanco;
	}

	public void setListaBanco(RegistroDataModelImpl listaBanco) {
		this.listaBanco = listaBanco;
	}

	public RegistroDataModelImpl getListaReferencia() {
		return listaReferencia;
	}

	public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
		this.listaReferencia = listaReferencia;
	}

	public RegistroDataModelImpl getListaCentroCosto() {
		return listaCentroCosto;
	}

	public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
		this.listaCentroCosto = listaCentroCosto;
	}

	public RegistroDataModelImpl getListaAuxiliar() {
		return listaAuxiliar;
	}

	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	public RegistroDataModelImpl getListaORDENADOR() {
		return listaORDENADOR;
	}

	public void setListaORDENADOR(RegistroDataModelImpl listaORDENADOR) {
		this.listaORDENADOR = listaORDENADOR;
	}

	public RegistroDataModelImpl getListaConcepto() {
		return listaConcepto;
	}

	public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
		this.listaConcepto = listaConcepto;
	}

	public RegistroDataModelImpl getListaComprobanteACopiar() {
		return listaComprobanteACopiar;
	}

	public void setListaComprobanteACopiar(RegistroDataModelImpl listaComprobanteACopiar) {
		this.listaComprobanteACopiar = listaComprobanteACopiar;
	}

	public String getNombreComprobante() {
		return nombreComprobante;
	}

	public void setNombreComprobante(String nombreComprobante) {
		this.nombreComprobante = nombreComprobante;
	}

	public String getCompACopiar() {
		return compACopiar;
	}

	public void setCompACopiar(String compACopiar) {
		this.compACopiar = compACopiar;
	}

	public Date getFechaConsignacion() {
		return fechaConsignacion;
	}

	public void setFechaConsignacion(Date fechaConsignacion) {
		this.fechaConsignacion = fechaConsignacion;
	}

	public String getNombreTercero() {
		return nombreTercero;
	}

	public void setNombreTercero(String nombreTercero) {
		this.nombreTercero = nombreTercero;
	}

	public boolean isVisibleComprobantePptal() {
		return visibleComprobantePptal;
	}

	public void setVisibleComprobantePptal(boolean visibleComprobantePptal) {
		this.visibleComprobantePptal = visibleComprobantePptal;
	}

	public boolean isVisiblePresupuesto() {
		return visiblePresupuesto;
	}

	public void setVisiblePresupuesto(boolean visiblePresupuesto) {
		this.visiblePresupuesto = visiblePresupuesto;
	}

	public boolean isVisibleCompCruce() {
		return visibleCompCruce;
	}

	public void setVisibleCompCruce(boolean visibleCompCruce) {
		this.visibleCompCruce = visibleCompCruce;
	}

	public boolean isBloqueaObjetos() {
		return bloqueaObjetos;
	}

	public void setBloqueaObjetos(boolean bloqueaObjetos) {
		this.bloqueaObjetos = bloqueaObjetos;
	}

	public boolean isBloqBtRetenciones() {
		return bloqBtRetenciones;
	}

	public void setBloqBtRetenciones(boolean bloqBtRetenciones) {
		this.bloqBtRetenciones = bloqBtRetenciones;
	}

	public boolean isVisibleIVA() {
		return visibleIVA;
	}

	public void setVisibleIVA(boolean visibleIVA) {
		this.visibleIVA = visibleIVA;
	}

	public String getTipoCpteAfect() {
		return tipoCpteAfect;
	}

	public void setTipoCpteAfect(String tipoCpteAfect) {
		this.tipoCpteAfect = tipoCpteAfect;
	}

	public String getResponsableArea() {
		return responsableArea;
	}

	public void setResponsableArea(String responsableArea) {
		this.responsableArea = responsableArea;
	}

	public boolean getVisibleTipoPagoL() {
		return visibleTipoPagoL;
	}

	public void setVisibleTipoPagoL(boolean visibleTipoPagoL) {
		this.visibleTipoPagoL = visibleTipoPagoL;
	}

	public String getTituloLblDoc() {
		return tituloLblDoc;
	}

	public void setTituloLblDoc(String tituloLblDoc) {
		this.tituloLblDoc = tituloLblDoc;
	}

	public boolean isVisibleEgresoCNT() {
		return visibleEgresoCNT;
	}

	public void setVisibleEgresoCNT(boolean visibleEgresoCNT) {
		this.visibleEgresoCNT = visibleEgresoCNT;
	}

	public boolean isVisibleBtBancos() {
		return visibleBtBancos;
	}

	public void setVisibleBtBancos(boolean visibleBtBancos) {
		this.visibleBtBancos = visibleBtBancos;
	}

	public boolean getVisibleCompRelacionado() {
		return visibleCompRelacionado;
	}

	public void setVisibleCompRelacionado(boolean visibleCompRelacionado) {
		this.visibleCompRelacionado = visibleCompRelacionado;
	}

	public boolean isEditableContrato() {
		return editableContrato;
	}

	public void setEditableContrato(boolean editableContrato) {
		this.editableContrato = editableContrato;
	}

	public boolean isVisibleBtPACEjec() {
		return visibleBtPACEjec;
	}

	public void setVisibleBtPACEjec(boolean visibleBtPACEjec) {
		this.visibleBtPACEjec = visibleBtPACEjec;
	}

	public boolean getVisibleOrdenador() {
		return visibleOrdenador;
	}

	public void setVisibleOrdenador(boolean visibleOrdenador) {
		this.visibleOrdenador = visibleOrdenador;
	}

	public boolean getBloqTercero() {
		return bloqTercero;
	}

	public void setBloqTercero(boolean bloqTercero) {
		this.bloqTercero = bloqTercero;
	}

	public boolean getBloqNumeroContrato() {
		return bloqNumeroContrato;
	}

	public void setBloqNumeroContrato(boolean bloqNumeroContrato) {
		this.bloqNumeroContrato = bloqNumeroContrato;
	}

	public boolean getBloqCuenta() {
		return bloqCuenta;
	}

	public void setBloqCuenta(boolean bloqCuenta) {
		this.bloqCuenta = bloqCuenta;
	}

	public boolean getBloqBanco() {
		return bloqBanco;
	}

	public void setBloqBanco(boolean bloqBanco) {
		this.bloqBanco = bloqBanco;
	}

	public boolean getBloqreferencia() {
		return bloqreferencia;
	}

	public void setBloqreferencia(boolean bloqreferencia) {
		this.bloqreferencia = bloqreferencia;
	}

	public boolean getBloqCentroCosto() {
		return bloqCentroCosto;
	}

	public void setBloqCentroCosto(boolean bloqCentroCosto) {
		this.bloqCentroCosto = bloqCentroCosto;
	}

	public boolean getBloqAuxiliar() {
		return bloqAuxiliar;
	}

	public void setBloqAuxiliar(boolean bloqAuxiliar) {
		this.bloqAuxiliar = bloqAuxiliar;
	}

	public boolean getBloqORDENADOR() {
		return bloqORDENADOR;
	}

	public void setBloqORDENADOR(boolean bloqORDENADOR) {
		this.bloqORDENADOR = bloqORDENADOR;
	}

	public boolean getBloqTipoContrato() {
		return bloqTipoContrato;
	}

	public void setBloqTipoContrato(boolean bloqTipoContrato) {
		this.bloqTipoContrato = bloqTipoContrato;
	}

	public boolean getBloqTIPOPAGOSIA() {
		return bloqTIPOPAGOSIA;
	}

	public void setBloqTIPOPAGOSIA(boolean bloqTIPOPAGOSIA) {
		this.bloqTIPOPAGOSIA = bloqTIPOPAGOSIA;
	}

	public boolean getBloqCODISIA() {
		return bloqCODISIA;
	}

	public void setBloqCODISIA(boolean bloqCODISIA) {
		this.bloqCODISIA = bloqCODISIA;
	}

	public boolean getBloqCODPROYECTO() {
		return bloqCODPROYECTO;
	}

	public void setBloqCODPROYECTO(boolean bloqCODPROYECTO) {
		this.bloqCODPROYECTO = bloqCODPROYECTO;
	}

	public boolean getBloqFecha() {
		return bloqFecha;
	}

	public void setBloqFecha(boolean bloqFecha) {
		this.bloqFecha = bloqFecha;
	}

	public boolean getBloqDescripcion() {
		return bloqDescripcion;
	}

	public void setBloqDescripcion(boolean bloqDescripcion) {
		this.bloqDescripcion = bloqDescripcion;
	}

	public boolean getBloqTexto() {
		return bloqTexto;
	}

	public void setBloqTexto(boolean bloqTexto) {
		this.bloqTexto = bloqTexto;
	}

	public boolean getBloqNroDocumento() {
		return bloqNroDocumento;
	}

	public void setBloqNroDocumento(boolean bloqNroDocumento) {
		this.bloqNroDocumento = bloqNroDocumento;
	}

	public boolean getBloqVlrDocumento() {
		return bloqVlrDocumento;
	}

	public void setBloqVlrDocumento(boolean bloqVlrDocumento) {
		this.bloqVlrDocumento = bloqVlrDocumento;
	}

	public boolean getBloqFechaVcnDoc() {
		return bloqFechaVcnDoc;
	}

	public void setBloqFechaVcnDoc(boolean bloqFechaVcnDoc) {
		this.bloqFechaVcnDoc = bloqFechaVcnDoc;
	}

	public boolean getBloqVlrBase() {
		return bloqVlrBase;
	}

	public void setBloqVlrBase(boolean bloqVlrBase) {
		this.bloqVlrBase = bloqVlrBase;
	}

	public boolean getBloqVlrBaseIVA() {
		return bloqVlrBaseIVA;
	}

	public void setBloqVlrBaseIVA(boolean bloqVlrBaseIVA) {
		this.bloqVlrBaseIVA = bloqVlrBaseIVA;
	}

	public boolean getBloqVlrAGirar() {
		return bloqVlrAGirar;
	}

	public void setBloqVlrAGirar(boolean bloqVlrAGirar) {
		this.bloqVlrAGirar = bloqVlrAGirar;
	}

	public boolean getBloqPorcIVA() {
		return bloqPorcIVA;
	}

	public void setBloqPorcIVA(boolean bloqPorcIVA) {
		this.bloqPorcIVA = bloqPorcIVA;
	}

	public boolean getBloqFECHAPAGADOGN() {
		return bloqFECHAPAGADOGN;
	}

	public void setBloqFECHAPAGADOGN(boolean bloqFECHAPAGADOGN) {
		this.bloqFECHAPAGADOGN = bloqFECHAPAGADOGN;
	}

	public boolean getBloqImpreso() {
		return bloqImpreso;
	}

	public void setBloqImpreso(boolean bloqImpreso) {
		this.bloqImpreso = bloqImpreso;
	}

	public boolean getBloqCONSFACTURA() {
		return bloqCONSFACTURA;
	}

	public void setBloqCONSFACTURA(boolean bloqCONSFACTURA) {
		this.bloqCONSFACTURA = bloqCONSFACTURA;
	}

	public boolean getBloqTxtFechaConsignacion() {
		return bloqTxtFechaConsignacion;
	}

	public void setBloqTxtFechaConsignacion(boolean bloqTxtFechaConsignacion) {
		this.bloqTxtFechaConsignacion = bloqTxtFechaConsignacion;
	}

	public boolean isVisibleIndAnticipo() {
		return visibleIndAnticipo;
	}

	public void setVisibleIndAnticipo(boolean visibleIndAnticipo) {
		this.visibleIndAnticipo = visibleIndAnticipo;
	}

	public boolean isVisibleConsFactura() {
		return visibleConsFactura;
	}

	public void setVisibleConsFactura(boolean visibleConsFactura) {
		this.visibleConsFactura = visibleConsFactura;
	}

	public boolean getVisibleContribuyente() {
		return visibleContribuyente;
	}

	public void setVisibleContribuyente(boolean visibleContribuyente) {
		this.visibleContribuyente = visibleContribuyente;
	}

	public boolean isVisibleConcepto() {
		return visibleConcepto;
	}

	public void setVisibleConcepto(boolean visibleConcepto) {
		this.visibleConcepto = visibleConcepto;
	}

	public boolean isVisiblePagoApoderado() {
		return visiblePagoApoderado;
	}

	public void setVisiblePagoApoderado(boolean visiblePagoApoderado) {
		this.visiblePagoApoderado = visiblePagoApoderado;
	}

	public boolean getVisibleTipoPagoSIA() {
		return visibleTipoPagoSIA;
	}

	public void setVisibleTipoPagoSIA(boolean visibleTipoPagoSIA) {
		this.visibleTipoPagoSIA = visibleTipoPagoSIA;
	}

	public boolean getVisibleCodigoSIA() {
		return visibleCodigoSIA;
	}

	public void setVisibleCodigoSIA(boolean visibleCodigoSIA) {
		this.visibleCodigoSIA = visibleCodigoSIA;
	}

	public boolean isVisiblePagadoGN() {
		return visiblePagadoGN;
	}

	public void setVisiblePagadoGN(boolean visiblePagadoGN) {
		this.visiblePagadoGN = visiblePagadoGN;
	}

	public void setVisibleNoNiif(boolean visibleNoNiif) {
		this.visibleNoNiif = visibleNoNiif;
	}

	public boolean isVisiblePagoEnPlano() {
		return visiblePagoEnPlano;
	}

	public void setVisiblePagoEnPlano(boolean visiblePagoEnPlano) {
		this.visiblePagoEnPlano = visiblePagoEnPlano;
	}

	public boolean isVisibleCodigoProyecto() {
		return visibleCodigoProyecto;
	}

	public void setVisibleCodigoProyecto(boolean visibleCodigoProyecto) {
		this.visibleCodigoProyecto = visibleCodigoProyecto;
	}

	public boolean isVisibleNombreProyecto() {
		return visibleNombreProyecto;
	}

	public void setVisibleNombreProyecto(boolean visibleNombreProyecto) {
		this.visibleNombreProyecto = visibleNombreProyecto;
	}

	public boolean isBloqFechaPagado() {
		return bloqFechaPagado;
	}

	public void setBloqFechaPagado(boolean bloqFechaPagado) {
		this.bloqFechaPagado = bloqFechaPagado;
	}

	public boolean isVisibleBanco() {
		return visibleBanco;
	}

	public void setVisibleBanco(boolean visibleBanco) {
		this.visibleBanco = visibleBanco;
	}

	public boolean isVisibleEnviado() {
		return visibleEnviado;
	}

	public void setVisibleEnviado(boolean visibleEnviado) {
		this.visibleEnviado = visibleEnviado;
	}

	public boolean isVisiblePagoEfectivo() {
		return visiblePagoEfectivo;
	}

	public void setVisiblePagoEfectivo(boolean visiblePagoEfectivo) {
		this.visiblePagoEfectivo = visiblePagoEfectivo;
	}

	public boolean isVisibleTxtLiquido() {
		return visibleTxtLiquido;
	}

	public void setVisibleTxtLiquido(boolean visibleTxtLiquido) {
		this.visibleTxtLiquido = visibleTxtLiquido;
	}

	public boolean isVisibleTxtRespArea() {
		return visibleTxtRespArea;
	}

	public void setVisibleTxtRespArea(boolean visibleTxtRespArea) {
		this.visibleTxtRespArea = visibleTxtRespArea;
	}

	public boolean isVisibleTxtReviso() {
		return visibleTxtReviso;
	}

	public void setVisibleTxtReviso(boolean visibleTxtReviso) {
		this.visibleTxtReviso = visibleTxtReviso;
	}

	public boolean isVisibleTxtFechaConsignacion() {
		return visibleTxtFechaConsignacion;
	}

	public void setVisibleTxtFechaConsignacion(boolean visibleTxtFechaConsignacion) {
		this.visibleTxtFechaConsignacion = visibleTxtFechaConsignacion;
	}

	public boolean isVisibleFechaPagado() {
		return visibleFechaPagado;
	}

	public void setVisibleFechaPagado(boolean visibleFechaPagado) {
		this.visibleFechaPagado = visibleFechaPagado;
	}

	public boolean isVisibleNiif() {
		return visibleNiif;
	}

	public void setVisibleNiif(boolean visibleNiif) {
		this.visibleNiif = visibleNiif;
	}

	public boolean isVisibleImprimirNiif() {
		return visibleImprimirNiif;
	}

	public void setVisibleImprimirNiif(boolean visibleImprimirNiif) {
		this.visibleImprimirNiif = visibleImprimirNiif;
	}

	public boolean isVisibleNoNiif() {
		return visibleNoNiif;
	}

	public boolean isBloqCopiar() {
		return bloqCopiar;
	}

	public void setBloqCopiar(boolean bloqCopiar) {
		this.bloqCopiar = bloqCopiar;
	}

	public boolean isBloqueaNumero() {
		return bloqueaNumero;
	}

	public void setBloqueaNumero(boolean bloqueaNumero) {
		this.bloqueaNumero = bloqueaNumero;
	}

	public boolean getVisibleDGConcepto() {
		return visibleDGConcepto;
	}

	public void setVisibleDGConcepto(boolean visibleDGConcepto) {
		this.visibleDGConcepto = visibleDGConcepto;
	}

	public boolean getVisibleDGConservaDesc() {
		return visibleDGConservaDesc;
	}

	public void setVisibleDGConservaDesc(boolean visibleDGConservaDesc) {
		this.visibleDGConservaDesc = visibleDGConservaDesc;
	}

	public boolean isBloqCompACopiar() {
		return bloqCompACopiar;
	}

	public void setBloqCompACopiar(boolean bloqCompACopiar) {
		this.bloqCompACopiar = bloqCompACopiar;
	}

	public boolean isVisibleInhumacion() {
		return visibleInhumacion;
	}

	public void setVisibleInhumacion(boolean visibleInhumacion) {
		this.visibleInhumacion = visibleInhumacion;
	}

	public boolean isVisibleImprimir() {
		return visibleImprimir;
	}

	public void setVisibleImprimir(boolean visibleImprimir) {
		this.visibleImprimir = visibleImprimir;
	}

	public boolean isVisibleBtDepuracion() {
		return visibleBtDepuracion;
	}

	public void setVisibleBtDepuracion(boolean visibleBtDepuracion) {
		this.visibleBtDepuracion = visibleBtDepuracion;
	}

	public boolean isVisibleBtFacEquivalente() {
		return visibleBtFacEquivalente;
	}

	public void setVisibleBtFacEquivalente(boolean visibleBtFacEquivalente) {
		this.visibleBtFacEquivalente = visibleBtFacEquivalente;
	}

	public boolean isContabilizarNiiFVisible() {
		return contabilizarNiiFVisible;
	}

	public void setContabilizarNiiFVisible(boolean contabilizarNiiFVisible) {
		this.contabilizarNiiFVisible = contabilizarNiiFVisible;
	}

	/**
	 * @return the visibleDgConfirmacion
	 */
	public boolean isVisibleDgConfirmacion() {
		return visibleDgConfirmacion;
	}

	/**
	 * @param visibleDgConfirmacion the visibleDgConfirmacion to set
	 */
	public void setVisibleDgConfirmacion(boolean visibleDgConfirmacion) {
		this.visibleDgConfirmacion = visibleDgConfirmacion;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getFechaMinima() {
		return fechaMinima;
	}

	public void setFechaMinima(String fechaMinima) {
		this.fechaMinima = fechaMinima;
	}

	public String getFechaMaxima() {
		return fechaMaxima;
	}

	public void setFechaMaxima(String fechaMaxima) {
		this.fechaMaxima = fechaMaxima;
	}

	public boolean isVisiblePacTesoreria() {
		return visiblePacTesoreria;
	}

	public void setVisiblePacTesoreria(boolean visiblePacTesoreria) {
		this.visiblePacTesoreria = visiblePacTesoreria;
	}

	public boolean isMostrarInsert() {
		return mostrarInsert;
	}

	public void setMostrarInsert(boolean mostrarInsert) {
		this.mostrarInsert = mostrarInsert;
	}

	public boolean isMostrarUpdate() {
		return mostrarUpdate;
	}

	public void setMostrarUpdate(boolean mostrarUpdate) {
		this.mostrarUpdate = mostrarUpdate;
	}

	public boolean isMostrarDelete() {
		return mostrarDelete;
	}

	public void setMostrarDelete(boolean mostrarDelete) {
		this.mostrarDelete = mostrarDelete;
	}

	public String getOrdenador() {
		return ordenador;
	}

	public void setOrdenador(String ordenador) {
		this.ordenador = ordenador;
	}

	public boolean isEstadoComprobantePresupuestal() {
		return estadoComprobantePresupuestal;
	}

	public void setEstadoComprobantePresupuestal(boolean estadoComprobantePresupuestal) {
		this.estadoComprobantePresupuestal = estadoComprobantePresupuestal;
	}

	/**
	 * @return the visibleDgCompPptal
	 */
	public boolean isVisibleDgCompPptal() {
		return visibleDgCompPptal;
	}

	/**
	 * @param visibleDgCompPptal the visibleDgCompPptal to set
	 */
	public void setVisibleDgCompPptal(boolean visibleDgCompPptal) {
		this.visibleDgCompPptal = visibleDgCompPptal;
	}

	public String getStrVlrDocumento() {
		return strVlrDocumento;
	}

	public void setStrVlrDocumento(String strVlrDocumento) {
		this.strVlrDocumento = strVlrDocumento;
	}

	public boolean isCargarEliminarCptePptal() {
		return cargarEliminarCptePptal;
	}

	public void setCargarEliminarCptePptal(boolean cargarEliminarCptePptal) {
		this.cargarEliminarCptePptal = cargarEliminarCptePptal;
	}

	public String getSesionuser() {
		return sesionuser;
	}

	public void setSesionuser(String sesionuser) {
		this.sesionuser = sesionuser;
	}

	public String getNomCompania() {
		return NomCompania;
	}

	public void setNomCompania(String nomCompania) {
		NomCompania = nomCompania;
	}

	public String getNitCompania() {
		return NitCompania;
	}

	public void setNitCompania(String nitCompania) {
		NitCompania = nitCompania;
	}

	public boolean isActivarCompIngreso() {
		return activarCompIngreso;
	}

	public void setActivarCompIngreso(boolean activarCompIngreso) {
		this.activarCompIngreso = activarCompIngreso;
	}

	public String getCompHeredar() {
		return compHeredar;
	}

	public void setCompHeredar(String compHeredar) {
		this.compHeredar = compHeredar;
	}

	public boolean isVisibleCausacionHeredada() {
		return visibleCausacionHeredada;
	}

	public void setVisibleCausacionHeredada(boolean visibleCausacionHeredada) {
		this.visibleCausacionHeredada = visibleCausacionHeredada;
	}

	/**
	 * Retorna la lista listaCodigoFlujoEfectivo
	 * 
	 * @return listaCodigoFlujoEfectivo
	 */
	public RegistroDataModelImpl getListaCodigoFlujoEfectivo() {
		return listaCodigoFlujoEfectivo;
	}

	/**
	 * Asigna la lista listaCodigoFlujoEfectivo
	 * 
	 * @param listaCodigoFlujoEfectivo Variable a asignar en
	 *                                 listaCodigoFlujoEfectivo
	 */
	public void setListaCodigoFlujoEfectivo(RegistroDataModelImpl listaCodigoFlujoEfectivo) {
		this.listaCodigoFlujoEfectivo = listaCodigoFlujoEfectivo;
	}

	/**
	 * @return the facturaProveedor
	 */
	public String getFacturaProveedor() {
		return facturaProveedor;
	}

	/**
	 * @param facturaProveedor the facturaProveedor to set
	 */
	public void setFacturaProveedor(String facturaProveedor) {
		this.facturaProveedor = facturaProveedor;
	}

	public boolean isCargarTerceros() {
		return cargarTerceros;
	}

	public void setCargarTerceros(boolean cargarTerceros) {
		this.cargarTerceros = cargarTerceros;
	}

	public boolean isPlano() {
		return plano;
	}

	public void setPlano(boolean plano) {
		this.plano = plano;
	}

	public List<Registro> getGruposasignados() {
		return gruposasignados;
	}

	public void setGruposasignados(List<Registro> gruposasignados) {
		this.gruposasignados = gruposasignados;
	}

	public boolean isBloqueacuentas() {
		return bloqueacuentas;
	}

	public void setBloqueacuentas(boolean bloqueacuentas) {
		this.bloqueacuentas = bloqueacuentas;
	}

	/**
	 * @return the listaCODISIA
	 */
	public RegistroDataModelImpl getListaCODISIA() {
		return listaCODISIA;
	}

	/**
	 * @param listaCODISIA the listaCODISIA to set
	 */
	public void setListaCODISIA(RegistroDataModelImpl listaCODISIA) {
		this.listaCODISIA = listaCODISIA;
	}

	public boolean isBloqConceptoCUDS() {
		return bloqConceptoCUDS;
	}

	public void setBloqConceptoCUDS(boolean bloqConceptoCUDS) {
		this.bloqConceptoCUDS = bloqConceptoCUDS;
	}

	/**
	 * @return the manejaCausacion
	 */
	public boolean isManejaCausacion() {
		return manejaCausacion;
	}

	/**
	 * @param manejaCausacion the manejaCausacion to set
	 */
	public void setManejaCausacion(boolean manejaCausacion) {
		this.manejaCausacion = manejaCausacion;
	}
	
	/**
	 * @return the manejaConceptoIng
	 */
	public boolean isManejaConceptoIng() {
		return manejaConceptoIng;
	}

	/**
	 * @param manejaConceptoIng the manejaConceptoIng to set
	 */
	public void setManejaConceptoIng(boolean manejaConceptoIng) {
		this.manejaConceptoIng = manejaConceptoIng;
	}

	/**
	 * @return the ivaActual
	 */
	public String getIvaActual() {
		return ivaActual;
	}

	/**
	 * @param ivaActual the ivaActual to set
	 */
	public void setIvaActual(String ivaActual) {
		this.ivaActual = ivaActual;
	}

	public boolean isManejaProcesoJudicial() {
		return manejaProcesoJudicial;
	}

	public void setManejaProcesoJudicial(boolean manejaProcesoJudicial) {
		this.manejaProcesoJudicial = manejaProcesoJudicial;
	}		
	
	public boolean isVisibleLegalizado() {
		return visibleLegalizado;
	}

	public void setVisibleLegalizado(boolean visibleLegalizado) {
		this.visibleLegalizado = visibleLegalizado;
	}
	
	public boolean isVarVolver() {
		return varVolver;
	}

	public void setVarVolver(boolean varVolver) {
		this.varVolver = varVolver;
	}
	
	public boolean isSigecVisible() {
		return sigecVisible;
	}

	public void setSigecVisible(boolean sigecVisible) {
		this.sigecVisible = sigecVisible;
	}
	
	public boolean isExistePPTO() {
		return existePPTO;
	}

	public void setExistePPTO(boolean existePPTO) {
		this.existePPTO = existePPTO;
	}
	
	public boolean isVerCerrar() {
		return verCerrar;
	}

	public void setVerCerrar(boolean verCerrar) {
		this.verCerrar = verCerrar;
	}

	/**
	 * @return the listaListaPlantillas
	 */
	public RegistroDataModelImpl getListaListaPlantillas() {
		return listaListaPlantillas;
	}

	/**
	 * @param listaListaPlantillas the listaListaPlantillas to set
	 */
	public void setListaListaPlantillas(RegistroDataModelImpl listaListaPlantillas) {
		this.listaListaPlantillas = listaListaPlantillas;
	}

	/**
	 * @return the plantilla
	 */
	public String getPlantilla() {
		return plantilla;
	}

	/**
	 * @param plantilla the plantilla to set
	 */
	public void setPlantilla(String plantilla) {
		this.plantilla = plantilla;
	}

	/**
	 * @return the visiblePresentarPlantillas
	 */
	public boolean isVisiblePresentarPlantillas() {
		return visiblePresentarPlantillas;
	}

	/**
	 * @param visiblePresentarPlantillas the visiblePresentarPlantillas to set
	 */
	public void setVisiblePresentarPlantillas(boolean visiblePresentarPlantillas) {
		this.visiblePresentarPlantillas = visiblePresentarPlantillas;
	}

	/**
	 * @return the visibleListaPlantillas
	 */
	public Boolean getVisibleListaPlantillas() {
		return visibleListaPlantillas;
	}

	/**
	 * @param visibleListaPlantillas the visibleListaPlantillas to set
	 */
	public void setVisibleListaPlantillas(Boolean visibleListaPlantillas) {
		this.visibleListaPlantillas = visibleListaPlantillas;
	}
	
	public boolean isImprimirPDF() {
		return imprimirPDF;
	}

	public void setImprimirPDF(boolean imprimirPDF) {
		this.imprimirPDF = imprimirPDF;
	}

	/**
	 * @return the visibleFirme
	 */
	public boolean isVisibleFirme() {
		return visibleFirme;
	}

	/**
	 * @param visibleFirme the visibleFirme to set
	 */
	public void setVisibleFirme(boolean visibleFirme) {
		this.visibleFirme = visibleFirme;
	}
}