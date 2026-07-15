/*-
 * FrmFacEstadoControlador.java
 *
 * 1.0
 * 
 * 18/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorEnum;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmDocDianControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmFacEstadoControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmRangoProduccionDianUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.persistencia.sqlserver.SysmanUtl;
import com.sysman.recursos.ejb.EjbCodigoBarrasRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.enums.ConstanteArchivo;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.ParametroCuerpoEjecucionReporte;
import com.sysman.util.rest.ParametroEjecucionApiReporte;
import com.sysman.util.rest.ParametrosEnvioFactura;
import com.sysman.util.rest.ParametrosFormato;
import com.sysman.util.rest.ParametrosLegalizarFactura;
import com.sysman.util.rest.ParametrosXmlCrear;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaConsultarReporte;
import com.sysman.util.rest.RespuestaEnvioFactura;
import com.sysman.util.rest.RespuestaFacturasReporte;
import com.sysman.util.rest.RespuestaFormatoConsultas;
import com.sysman.util.rest.RespuestaNotasReporte;
import com.sysman.util.soap.ApiInvoway;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.InfoEstadosFactura;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.json.JSONException;
import org.apache.tomcat.util.json.JSONObject;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Formulario que permite consultar los estados de facturacion
 *
 * @version 1.0, 18/12/2020
 * @author eamaya
 * 
 * @version 1.1, 08/10/2021
 * @author gfigueredo Se agrega campo DOCUMENTKEY para almacenar el codigo
 *         CUFE-CUDE
 */
@ManagedBean
@ViewScoped

public class FrmFacEstadoControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private final String usuario;

	private final String modulo;

	private String nitCompania;
	// <DECLARAR_ATRIBUTOS>

	private String todosFacturas;

	private String todosNotas;

	/**
	 * Variabale que almacena el indicador notas credito
	 */
	private boolean notasCredito;
	/**
	 * Variable que almacena el indicador notas debito
	 */
	private boolean notasDebito;
	/**
	 * Variable que almacena el indicador facturas
	 */
	private boolean facturas;
	/**
	 * Variable que almacena el numero de factura
	 */
	private String numeroFactura;
	/**
	 * Variable que almacena el estado
	 */
	private String estado;
	/**
	 * Variable que almacena el tipo consulta
	 */
	private String tipoConsulta;
	/**
	 * Variable que almacena la fecha fin
	 */
	private Date fechaFin;
	/**
	 * Variable qie almacena la fecha de inicio
	 */
	private Date fechaInicio;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * 
	 */
	private StreamedContent archivoDescargaNotas;
	/**
	 * Variable que administra el bloqueo del combo numero factura
	 */
	private boolean bloqueoNumeroFactura;
	/**
	 * Variable que administra el bloqueo de los botones de exportar excel y pdf
	 */
	private boolean bloqueoBotonesExportar;
	/**
	 * Variable que administra el bloqueo de los campos fecha
	 */
	private boolean bloqueoFecha;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista que carga el numero de factura
	 */
	private List<Registro> listaNumeroFactura;
	/**
	 * Lista que carga el tipo de consulta
	 */
	private List<Registro> listaTipoConsulta;
	/**
	 * Lista que carga el numero de notas, creada para invoway
	 */
	private List<Registro> listaNumeroNota;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Lista que carga el estado de facturas
	 */
	private RegistroDataModelImpl listaEstadoFacturas;
	/**
	 * Lista que carga el estadp de notas
	 */
	private RegistroDataModelImpl listaEstadoNotas;

	/**
	 * Variable que almacena la url del servicio de FRIDA
	 */
	private String url;

	private String tipoCobro;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	// </DECLARAR_ADICIONALES>

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil = new EjbSysmanUtil();
    @EJB
    private EjbCodigoBarrasRemote ejbCodigoBarras;

	private String ano;

	private String mesInicial;

	private String mesFinal;

	private String tipoFactRecaudo;
	// CONSTANTES DE IMPLEMENTACION CONSUMO DE SERVICIO INVOWAY
	private static final String URL_SERVICIO_SOAP= "URL SERVICIO SOAP";
	private static final String MANEJA_FACTURACION_ELECTRONICA_EXTERNA = "MANEJA FACTURACION ELECTRONICA EXTERNA";
	private static final String USUARIO_FACT_ELECTRONICA_EXTERNA = "USUARIO FACT ELECTRONICA EXTERNA";
	private static final String CLAVE_FACT_ELECTRONICA_EXTERNA = "CLAVE FACT ELECTRONICA EXTERNA";
	private String facturadorExterno = "";
	private String user = "";
	private String pass = "";

	private boolean bloqueoBotonesZip;
	/**
	 * Crea una nueva instancia de FrmFacEstadoControlador
	 */
	public FrmFacEstadoControlador() {
		super();
		compania = SessionUtil.getCompania();
		nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		usuario = SessionUtil.getUser().getCodigo();
		modulo = SessionUtil.getModulo();
		bloqueoNumeroFactura = true;
		bloqueoFecha = false;
		bloqueoBotonesZip = false;
		
		fechaFin = new Date();
		fechaInicio = new Date();

		tipoCobro = (String) SessionUtil.getSessionVar(ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
		listaEstadoNotas = new RegistroDataModelImpl();
		listaEstadoFacturas = new RegistroDataModelImpl();
		setBloqueoBotonesExportar(true);
		
		try {
			// 2223
			numFormulario = GeneralCodigoFormaEnum.FRM_FACESTADO_CONTROLADOR.getCodigo();
			validarPermisos();
			tipoFactRecaudo = SysmanFunciones.nvl(SessionUtil
	                .getSessionVarContainer(
	                                ConstantesFacturacionGenEnum.TIPOFACTURA_RECAUDO
	                                                .getValue()),
	                "0").toString();
			
			// <INI_ADICIONAL>
			// se consultan los parametros de envio a invoway
			// se consulta los parametros para traer el usuario y la contrase�a del ws invoway
	        user = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	        		USUARIO_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false),"");
	        
	        pass = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	        		CLAVE_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false),"");
	        
	        
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
		// se consulta el parametro que define si se usa Facturador externo o FRIDA
        try {
        	facturadorExterno = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
        			MANEJA_FACTURACION_ELECTRONICA_EXTERNA, "69", new Date(), false),"NO");
        	if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())) {
        		bloqueoBotonesZip = true;
        	}
        }catch (Exception e) {
        	e.printStackTrace();
        	facturadorExterno = "NO";
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>

		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>

		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		abrirFormulario();

		cargarListaEstadoFacturas();
		cargarListaEstadoNotas();
		cargarListaNumeroFactura();
		cargarListaTipoConsulta();

	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		origenDatos = "";
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaNumeroFactura
	 *
	 */
	public void cargarListaNumeroFactura() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.TIPO.getName(), "1");

		try {
			listaNumeroFactura = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmFacEstadoControladorUrlEnum.URL5559.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaTipoConsulta
	 *
	 */
	public void cargarListaTipoConsulta() {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.TIPO.getName(), "210");

		try {
			listaTipoConsulta = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmFacEstadoControladorUrlEnum.URL5881.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaEstadoFacturas
	 *
	 */
	public void cargarListaEstadoFacturas() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmFacEstadoControladorUrlEnum.URL6300.getValue());

		try {
			listaEstadoFacturas = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null,
					false, CacheUtil.getLlaveServicio(urlConexionCache, "ESTADOFAC"), true);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listaEstadoNotas
	 *
	 */
	public void cargarListaEstadoNotas() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmFacEstadoControladorUrlEnum.URL6811.getValue());

		try {
			listaEstadoNotas = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, false,
					CacheUtil.getLlaveServicio(urlConexionCache, "ESTADO_NOTAS"), true);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>

	/**
	 * Metodo ejecutado al cambiar el control TipoConsulta
	 * 
	 * 
	 */
	public void cambiarTipoConsulta() {
		// <CODIGO_DESARROLLADO>

		if ("0".equals(tipoConsulta)) {
			bloqueoNumeroFactura = false;
			bloqueoFecha = true;

			insertarFacturasCombo();
			cargarListaNumeroFactura();
		} else {
			bloqueoNumeroFactura = true;
			bloqueoFecha = false;
		}
		// </CODIGO_DESARROLLADO>
	}

	private void insertarFacturasCombo() {
		borrarDatosCombo();
		try {
			if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) {
				url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);
	
				if (SysmanFunciones.validarVariableVacio(url)) {
					JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
				} else {
	
					String respuesta;
					APIFrida api = new APIFrida();
	
					respuesta = api.cargarEnvioFacatura(nitCompania, url);
	
					Gson gson = new Gson();
					RespuestaEnvioFactura respuestaApi = gson.fromJson(respuesta, RespuestaEnvioFactura.class);
	
					for (int i = 0; i < respuestaApi.getCuerpo().size(); i++) {
	
						List<Object> datos = (List<Object>) respuestaApi.getCuerpo().get(i);
						insertarListaCombos(datos);
					}
	
				}
			}else if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())) {
				// se consultan las facturas en el systema dentro del rango de dias selecionado
				
			}
		} catch (SystemException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void insertarListaCombos(List<Object> datos) {

		Map<String, Object> params = new TreeMap<>();

		String urlEnumId = FrmFacEstadoControladorUrlEnum.URL5198.getValue();

		try {

			params.put("ID", "1");
			params.put(GeneralParameterEnum.CODIGO.getName(), datos.get(2));
			params.put(GeneralParameterEnum.DESCRIPCION.getName(),
					new BigDecimal(datos.get(0).toString()).toPlainString());

			params.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			params.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void borrarDatosCombo() {
		Map<String, Object> params = new TreeMap<>();

		UrlBean urlDelete = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmFacEstadoControladorUrlEnum.URL8974.getValue());

		try {
			requestManager.delete(urlDelete.getUrl(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEstadoFacturas
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEstadoFacturas(SelectEvent event) {
		// METODO_NO_IMPLEMEnTADO
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEstadoNotas
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEstadoNotas(SelectEvent event) {
		// METODO_NO_IMPLEMEnTADO
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoNotas en la vista
	 *
	 *
	 */
	public void oprimirConsultarEstadoNotas() {
		borrarDatosDocumentoDian();
		String numeroFactura;
		String clase;
		String prefijoFactura;

		for (Registro r : listaEstadoNotas.getSeleccionados()) {
			if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) {
				numeroFactura = r.getCampos().get("NUMFORMATO").toString();
	
				clase = r.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString();
	
				clase = ("NC").equals(clase) ? "02" : "03";
	
				prefijoFactura = r.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString();
	
				if (!"PERSISTIDA".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {
	
					if (consultarEstadoDocDian(numeroFactura, clase, prefijoFactura)) {
	
						String[] campos = { GeneralParameterEnum.COMPANIA.getName() };
	
						String[] valores = { compania };
	
						SessionUtil.cargarModalDatosFlashCerrar(
								Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_DIAN_CONTROLADOR.getCodigo()),
								modulo, campos, valores);
	
					}
	
				} else {
					JsfUtil.agregarMensajeErrorDialogo(
							"El documento " + r.getCampos().get("NUMFACTURA") + " no se ha legalizado frente a la DIAN");
				}
			}else if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())) {
				try {
					this.consultarEstadoDocDianInvoway(
							r.getCampos().get(GeneralParameterEnum.NUMFORMATO.getName()).toString(), 
							SysmanFunciones.convertirAFechaCadena((Date)r.getCampos().get(GeneralParameterEnum.FECHA.getName()), "yyyy"),
							r.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString(),
							r.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString());
					
					// se pasan los datos consultados
					String[] campos = { GeneralParameterEnum.COMPANIA.getName() };
					
					String[] valores = { compania };

					SessionUtil.cargarModalDatosFlashCerrar(
							Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_DIAN_CONTROLADOR.getCodigo()),
							modulo, campos, valores);
				}catch(Exception e) {
					JsfUtil.agregarMensajeAlerta("");
				}
			}
		}

	}

	private boolean consultarEstadoDocDian(String numeroFactura, String clase, String prefijoFactura) {

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
			} else {

				Registro rs = RegistroConverter
						.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmFacEstadoControladorUrlEnum.URL9457.getValue())
												.getUrl(),
										null));

				if (rs != null) {

					File archivo = new File(rs.getCampos().get("RUTA_CERTIFICADO").toString());

					String nombreCertificado = archivo.getName();

					byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

					String certificado = Base64.getEncoder().encodeToString(archivoBytes);

					String passCertificado = Base64.getEncoder()
							.encodeToString(rs.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());

					String respuesta;
					APIFrida api = new APIFrida();

					respuesta = api.postFormatoConsultas(url, nitCompania, clase, numeroFactura, prefijoFactura,
							nombreCertificado, certificado, passCertificado);

					Gson gson = new Gson();

					RespuestaFormatoConsultas respuestaApi = gson.fromJson(respuesta, RespuestaFormatoConsultas.class);

					if (respuestaApi.getCuerpo().isIsValid()) {

						insertarEstadoDian(numeroFactura, clase, respuestaApi.getCuerpo().getStatusDescription(),
								respuestaApi.getCuerpo().getXmlDocumentKey());

					} else {
						JsfUtil.agregarMensajeErrorDialogo("Factura: " + numeroFactura + "\n"
								+ SysmanFunciones
										.nvl(respuestaApi.getCuerpo().getErrorMessage(),
												respuestaApi.getCuerpo().getStatusDescription())
										.toString().replace(",", "\n"));

						return false;
					}

				} else {
					JsfUtil.agregarMensajeAlerta(
							"Asegurese de configurar el certificado en el formulario CONFIGURACION CERTIFICADO DIAN");

					return false;

				}
			}

		} catch (SystemException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return true;

	}
	private void consultarEstadoDocDianInvoway(String numeroFactura, String ano, String prefijo, String clase) {
			List<InfoEstadosFactura> respuesta = new ArrayList<>();
			try {
				ApiInvoway apiInvoway = new ApiInvoway();			
				
				url = ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_SOAP, "69", new Date(), false);
				
				if (SysmanFunciones.validarVariableVacio(url)) {
					JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO SOAP");
				} else {				
					respuesta = apiInvoway.consultarFactura(url, 
							prefijo+numeroFactura, 
							ano, 
							clase, 
							nitCompania, 
							pass, 
							user);
					if(respuesta.size() > 0) {
						insertarEstadoDian(numeroFactura, clase, respuesta.get(0).getObservacionesEstadoDIAN(),
							respuesta.get(0).getUUID());
					}else {
						insertarEstadoDian(numeroFactura, clase, idioma.getString("MSM_DOCUMENTO_NO_ENVIADO_INVOWAY"),
								prefijo+numeroFactura);
					}
				}
			} catch (SystemException | IOException | SysmanException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
	}
	/**
	 * metodo que realiza la conuslta del estado de la factura en invoway
	 * @param numeroFactura
	 * @param ano
	 * @param prefijo
	 * @param tercero
	 * @param valorTotal
	 * @param fecha
	 */
	private void consultarFacturasDocDianInvoway(String numeroFactura, String ano, String prefijo, String tercero, String valorTotal, String fecha) {
		List<InfoEstadosFactura> respuesta = new ArrayList<>();
		try {
			ApiInvoway apiInvoway = new ApiInvoway();			
			
			url = ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_SOAP, "69", new Date(), false);
			
			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
			} else {				
				respuesta = apiInvoway.consultarFactura(url, 
						prefijo+numeroFactura, 
						ano, 
						"FA", 
						nitCompania, 
						pass, 
						user);			
				
				if(respuesta.size() > 0) {
					RespuestaFacturasReporte respuestaFacturasReporte = new RespuestaFacturasReporte();
					respuestaFacturasReporte.setEstado(respuesta.get(0).getEstadoDIAN());
					respuestaFacturasReporte.setFecha(respuesta.get(0).getFechaAlta().getYear()+"/"+respuesta.get(0).getFechaAlta().getMonth()+"/"+respuesta.get(0).getFechaAlta().getDay());
					respuestaFacturasReporte.setNumFormato(numeroFactura);
					respuestaFacturasReporte.setObservacion(respuesta.get(0).getUUID());
					respuestaFacturasReporte.setPrefijo(prefijo);
					respuestaFacturasReporte.setTercero(tercero);
					respuestaFacturasReporte.setTotal(valorTotal);
					
					insertarFacturas(respuestaFacturasReporte);
				}			
			}
		} catch (SystemException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	private void consultarNotasDocDianInvoway(String numeroNota, String numeroFactura, String ano, String prefijo, String tercero, String valorTotal, String fecha) {
		List<InfoEstadosFactura> respuesta = new ArrayList<>();
		try {
			ApiInvoway apiInvoway = new ApiInvoway();			
			
			url = ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_SOAP, "69", new Date(), false);
			
			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
			} else {				
				respuesta = apiInvoway.consultarFactura(url, 
						prefijo+numeroNota, 
						ano, 
						prefijo, 
						nitCompania, 
						pass, 
						user);
				
				if(respuesta.size() > 0) {
					RespuestaNotasReporte respuestaNotasReporte = new RespuestaNotasReporte();
					
					respuestaNotasReporte.setNumFactura(numeroFactura);
					respuestaNotasReporte.setClase(prefijo);
					respuestaNotasReporte.setNumFormato(numeroNota);
					respuestaNotasReporte.setTipoNota(prefijo);
					respuestaNotasReporte.setEstado(respuesta.get(0).getEstadoDIAN().toString());
					respuestaNotasReporte.setObservacion(respuesta.get(0).getUUID());
					respuestaNotasReporte.setFecha(respuesta.get(0).getFechaAlta().getYear()+"/"+respuesta.get(0).getFechaAlta().getMonth()+"/"+respuesta.get(0).getFechaAlta().getDay());
				
					insertarNotas(respuestaNotasReporte);
				}			
			}
		} catch (SystemException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	private void insertarEstadoDian(String numeroFactura, String clase, String statusDescription, String documentkey) {
		Map<String, Object> params = new TreeMap<>();

		String urlEnumId = FrmFacEstadoControladorUrlEnum.URL4658.getValue();

		try {

			params.put("NUM_DOCUMENTO", numeroFactura);
			params.put(GeneralParameterEnum.TIPO_DOCUMENTO.getName(),
					"01".equals(clase) || "FA".equals(clase) ? "FACTURA" : "02".equals(clase) || "NC".equals(clase) ? "NOTA CREDITO" : "NOTA DEBITIO"

			);
			params.put("MENSAJE", statusDescription);
			params.put("DOCUMENTKEY", documentkey);

			params.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			params.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoFacturas en la vista
	 *
	 *
	 */
	public void oprimirConsultarEstadoFacturas() {
		borrarDatosDocumentoDian();
		String numeroFactura;
		String prefijoFactura;

		for (Registro r : listaEstadoFacturas.getSeleccionados()) {
			if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) {
				numeroFactura = r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
	
				prefijoFactura = r.getCampos().get("PREFIJO").toString();
	
				if (!"PERSISTIDA".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {
	
					if (consultarEstadoDocDian(numeroFactura, "01", prefijoFactura)) {
	
						String[] campos = { GeneralParameterEnum.COMPANIA.getName() };
	
						String[] valores = { compania };
	
						SessionUtil.cargarModalDatosFlashCerrar(
								Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_DIAN_CONTROLADOR.getCodigo()),
								modulo, campos, valores);
	
					}
	
				} else {
					JsfUtil.agregarMensajeAlerta("El documento " + r.getCampos().get(GeneralParameterEnum.NUMERO.getName())
							+ " no se ha legalizado frente a la DIAN");
				}
			}else if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())) {
				try {
					this.consultarEstadoDocDianInvoway(
							r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), 
							SysmanFunciones.convertirAFechaCadena((Date)r.getCampos().get(GeneralParameterEnum.FECHA.getName()), "yyyy"),
							r.getCampos().get(GeneralParameterEnum.PREFIJO.getName()).toString(),
							"FA");
					
					// se pasan los datos consultados
					String[] campos = { GeneralParameterEnum.COMPANIA.getName() };
					
					String[] valores = { compania };

					SessionUtil.cargarModalDatosFlashCerrar(
							Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_DIAN_CONTROLADOR.getCodigo()),
							modulo, campos, valores);
				}catch(Exception e) {
					JsfUtil.agregarMensajeAlerta("");
				}
			}
		}

	}

	private void borrarDatosDocumentoDian() {
		Map<String, Object> params = new TreeMap<>();

		UrlBean urlDelete = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmFacEstadoControladorUrlEnum.URL4567.getValue());

		try {
			requestManager.delete(urlDelete.getUrl(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ExportarDocumentosEmitidos en la vista
	 *
	 *
	 */
	public void oprimirExportarDocumentosEmitidos() {
		consultarEstadoFacturasTodas();
		archivoDescarga = null;
		generarInformeEmitidos(FORMATOS.EXCEL);
	}

	private void generarInformeEmitidos(FORMATOS formato) {

		String sql = Reporteador.resuelveConsulta("800495FacturasEmitidasDian",
				Integer.parseInt(SessionUtil.getModulo()), null);
		String sql1 = Reporteador.resuelveConsulta("800496NotasEmitidasDian", Integer.parseInt(SessionUtil.getModulo()),
				null);

		ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

		try {
			salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, formato);

			salidas[1] = JsfUtil.serializarHojaDatos(sql1, ConectorPool.ESQUEMA_SYSMAN, formato);

			String[] nombresArchivos = new String[2];
			nombresArchivos[0] = "FacturasEmitidas.xlsx";
			nombresArchivos[1] = "NotasEmitidas.xlsx";

			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

		} catch (JRException | IOException | SQLException | DRException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} 
	}

	private void generarInformeNoEmitidos(FORMATOS formato) throws ParseException {

		Map<String, Object> reemplazar = new TreeMap<>();
		reemplazar.put("compania", compania);
		reemplazar.put("fechaI", SysmanFunciones.convertirAFechaCadena(fechaInicio, "dd/MM/yyyy"));
		reemplazar.put("fechaF", SysmanFunciones.convertirAFechaCadena(fechaFin, "dd/MM/yyyy"));
		reemplazar.put("tipoCobro", tipoCobro);

		// sentencias para excel
		String sql = Reporteador.resuelveConsulta("800497FacturasNoEmitidasDian",
				Integer.parseInt(SessionUtil.getModulo()), reemplazar);
		String sql1 = Reporteador.resuelveConsulta("800498NotasNoEmitidasDian",
				Integer.parseInt(SessionUtil.getModulo()), reemplazar);
		
		//sentencias para PDF
		String reporte = "002371FEREPORTEBUSQUEDAFACTURANOEMITIDAS";
		String reporteNota = "002372FEREPORTEBUSQUEDANOTASNOEMITIDAS";
		
		Map<String, Object> parametros = new HashMap<>();
		
		parametros.put("compania", compania);
		parametros.put("fechaI", SysmanFunciones.convertirAFechaCadena(fechaInicio, "dd/MM/yyyy"));
		parametros.put("fechaF", SysmanFunciones.convertirAFechaCadena(fechaFin, "dd/MM/yyyy"));
		parametros.put("tipoCobro", tipoCobro);
		
		ByteArrayInputStream[] salidas = new ByteArrayInputStream[4];
		
		try {
			// generacion de excel
			salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, formato);

			salidas[1] = JsfUtil.serializarHojaDatos(sql1, ConectorPool.ESQUEMA_SYSMAN, formato);
			
			// generacion PDF
			salidas[2] = JsfUtil.serializarReporte(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

			salidas[3] = JsfUtil.serializarReporte(reporteNota, parametros, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
			
			String[] nombresArchivos = new String[4];
			// nombres Excel
			nombresArchivos[0] = "FacturasNoEmitidas.xlsx";
			nombresArchivos[1] = "NotasNoEmitidas.xlsx";
			// nombres PDF
			nombresArchivos[2] = "FacturasNoEmitidas.pdf";
			nombresArchivos[3] = "NotasNoEmitidas.pdf";
			
			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos, "No emitidas dian");

		} catch (JRException | IOException | SQLException | DRException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} 
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ExportarDocumentosNoEmitidos en la vista
	 * 
	 * @throws ParseException
	 *
	 *
	 */
	public void oprimirExportarDocumentosNoEmitidos() throws ParseException {
		archivoDescarga = null;
		generarInformeNoEmitidos(FORMATOS.EXCEL);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Buscar en la vista
	 *
	 *
	 */
	public void oprimirBuscar() {
		facturas = true;
		notasCredito = true;
		notasDebito = true;
		archivoDescarga = null;
		borrarDatosListas();
		try {
			if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) {
				url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);
	
				if (SysmanFunciones.validarVariableVacio(url)) {
					JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
				} else {
					String respuesta;
					APIFrida api = new APIFrida();
	
					respuesta = api.cargarFormatoConsultarReporte(url, nitCompania, tipoConsulta, numeroFactura, estado,
							SysmanFunciones.convertirAFechaCadena(fechaInicio, "yyyy/MM/dd"),
							SysmanFunciones.convertirAFechaCadena(fechaFin, "yyyy/MM/dd"), facturas ? "1" : "0",
							notasDebito ? "1" : "0", notasCredito ? "1" : "0");
	
					Gson gson = new Gson();
					RespuestaConsultarReporte respuestaApi = gson.fromJson(respuesta, RespuestaConsultarReporte.class);
	
					for (RespuestaFacturasReporte respuestaFacturasReporte : respuestaApi.getCuerpo().getFacturas()) {
	
						insertarFacturas(respuestaFacturasReporte);
					}
	
					for (RespuestaNotasReporte respuestaNotasReporte : respuestaApi.getCuerpo().getNotas()) {
	
						insertarNotas(respuestaNotasReporte);
					}	
					cargarListaEstadoFacturas();
					cargarListaEstadoNotas();
					/*
					 * ticket 7701903
					 */
					setBloqueoBotonesExportar(false);
				}
			}if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())) {
				// se buscan las facturas del sistema para validar el estado en invoway
				Map<String, Object> param = new TreeMap<>();
				// primer se buscan todas las facturas
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.TIPO_COBRO.getName(), tipoCobro);
				param.put(GeneralParameterEnum.FECHAINCIO.getName(), SysmanFunciones.convertirAFechaCadena(fechaInicio, "dd/MM/yyyy"));
				param.put(GeneralParameterEnum.FECHAFIN.getName(), SysmanFunciones.convertirAFechaCadena(fechaFin, "dd/MM/yyyy"));
				
				listaNumeroFactura = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmFacEstadoControladorUrlEnum.URL666015.getValue())
												.getUrl(),
										param));
				
				for(Registro reg : listaNumeroFactura) {
					if(reg.getCampos().get(GeneralParameterEnum.NRO_FACTURA.getName()) != null)
						this.consultarFacturasDocDianInvoway(reg.getCampos().get(GeneralParameterEnum.NRO_FACTURA.getName()).toString(), 
							reg.getCampos().get(GeneralParameterEnum.ANO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.REF_FACTURACION.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.VALOR_TOTAL.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.FECHA_SOLICITUD.getName()).toString()
							);
				}
				/* segundo se buscan todas las notas */
				param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.CLASE.getName(), "N");
				param.put(GeneralParameterEnum.FECHAINICIO.getName(), SysmanFunciones.convertirAFechaCadena(fechaInicio, "dd/MM/yyyy"));
				param.put(GeneralParameterEnum.FECHAFIN.getName(), SysmanFunciones.convertirAFechaCadena(fechaFin, "dd/MM/yyyy"));
				
				listaNumeroNota = RegistroConverter
						.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmFacEstadoControladorUrlEnum.URL72120.getValue())
											.getUrl(),
									param));
				
				for(Registro reg : listaNumeroNota) {
					this.consultarNotasDocDianInvoway(reg.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), 
							reg.getCampos().get(GeneralParameterEnum.NUMERO_FACTURA.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.ANO.getName()).toString(),
							"NC",
							reg.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.VLR_DOCUMENTO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString()
							);
				}
				
				for(Registro reg : listaNumeroNota) {
					this.consultarNotasDocDianInvoway(reg.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), 
							reg.getCampos().get(GeneralParameterEnum.NUMERO_FACTURA.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.ANO.getName()).toString(),
							"ND",
							reg.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.VLR_DOCUMENTO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString()
							);
				}
			}
		} catch (SystemException | ParseException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoFacturas en la vista
	 *
	 *
	 */
	public void consultarEstadoFacturasTodas() {
		borrarDatosDocumentoDian();
		String numeroFactura;
		String prefijoFactura;

		for (Registro r : listaEstadoFacturas) {

			numeroFactura = r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();

			prefijoFactura = r.getCampos().get("PREFIJO").toString();

			if (!"PERSISTIDA".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {

				if (consultarEstadoDocDian(numeroFactura, "01", prefijoFactura)) {

					String[] campos = { GeneralParameterEnum.COMPANIA.getName() };

					String[] valores = { compania };

					SessionUtil.cargarModalDatosFlashCerrar(
							Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_DIAN_CONTROLADOR.getCodigo()),
							modulo, campos, valores);

				}

			} else {
				JsfUtil.agregarMensajeAlerta("El documento " + r.getCampos().get(GeneralParameterEnum.NUMERO.getName())
						+ " no se ha legalizado frente a la DIAN");
			}

		}

		consultarEstadoNotasTodas();

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoNotas en la vista
	 *
	 *
	 */
	public void consultarEstadoNotasTodas() {
		// borrarDatosDocumentoDian();
		String numeroFactura;
		String clase;
		String prefijoFactura;

		for (Registro r : listaEstadoNotas) {
			
			numeroFactura = r.getCampos().get("NUMFORMATO").toString();

			clase = r.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString();

			clase = ("NC").equals(clase) ? "02" : "03";

			prefijoFactura = r.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString();

			if (!"PERSISTIDA".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {

				if (consultarEstadoDocDian(numeroFactura, clase, prefijoFactura)) {

					String[] campos = { GeneralParameterEnum.COMPANIA.getName() };

					String[] valores = { compania };

					SessionUtil.cargarModalDatosFlashCerrar(
							Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_DIAN_CONTROLADOR.getCodigo()),
							modulo, campos, valores);

				}

			} else {
				JsfUtil.agregarMensajeErrorDialogo(
						"El documento " + r.getCampos().get("NUMFACTURA") + " no se ha legalizado frente a la DIAN");
			}

		}

	}

	private void insertarFacturas(RespuestaFacturasReporte respuestaFacturasReporte) {

		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

		try {

			String urlEnumId = FrmFacEstadoControladorUrlEnum.URL9848.getValue();
			Date fecha = new Date();
			if(respuestaFacturasReporte.getFecha().contains("-")) {
				fecha = formato.parse(respuestaFacturasReporte.getFecha().replace("-", "/"));
			}else {
				fecha = formato.parse(respuestaFacturasReporte.getFecha());
			}
			Map<String, Object> params = new TreeMap<>();

			params.put(GeneralParameterEnum.ESTADO.getName(), respuestaFacturasReporte.getEstado());
			params.put(GeneralParameterEnum.NUMERO.getName(), respuestaFacturasReporte.getNumFormato());
			params.put(GeneralParameterEnum.TERCERO.getName(), respuestaFacturasReporte.getTercero());
			params.put(GeneralParameterEnum.TOTAL.getName(), new BigDecimal(respuestaFacturasReporte.getTotal()));
			params.put(GeneralParameterEnum.FECHA.getName(), fecha);
			params.put(GeneralParameterEnum.OBSERVACION.getName(), respuestaFacturasReporte.getObservacion());
			params.put("PREFIJO", respuestaFacturasReporte.getPrefijo());
			params.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			params.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
			UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), params);
		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void insertarNotas(RespuestaNotasReporte respuestaNotasReporte) {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

		try {

			String urlEnumId2 = FrmFacEstadoControladorUrlEnum.URL2548.getValue();

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

	public void oprimirExcel() {
		try {
			obtenerReporte(FORMATOS.EXCEL97);
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//generarInformeNotas(FORMATOS.EXCEL97);
	}

	public void oprimirpdf() {
		try {
			obtenerReporte(FORMATOS.PDF);
		} catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * ljdiaz
	 * se crea metodo para realziar la descarga del ZIP de el o los arcvhos Zip para descarga
	 */
	public void oprimirdescargaZipFact() {
		burcarZipRegListFact();
	}
	/**
	 * ljdiaz
	 * se crea metodo para realziar la descarga del ZIP de el o los arcvhos Zip para descarga
	 */
	public void oprimirdescargaZipNota() {
		burcarZipRegListNotas();
	}
	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	/**
	 * @author ljdiaz
	 * @param formato metodo que realizara la exportacion de la unformacion
	 *                Consultada en formato EXCEL y PDF
	 */
	public void obtenerReporte(FORMATOS formatos) throws SysmanException {
        String reporte = null;
        String reporteNota = null;
        boolean notas = false;
        boolean facturas = false;
        String[] nombresArchivos = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            if(formatos.name().equals("PDF")) {
            	reporte = "002369FEREPORTEBUSQUEDAFACTURA";
            	reporteNota = "002370FEREPORTEBUSQUEDANOTAS";
            	
            	ByteArrayInputStream[] salidas = null ;
            	if(listaEstadoFacturas.getRowCount() > 0 && listaEstadoNotas.getRowCount() > 0) {	
            		salidas = new ByteArrayInputStream[2];
            		salidas[0] = JsfUtil.serializarReporte(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
            		facturas = true;
            		salidas[1] = JsfUtil.serializarReporte(reporteNota, parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
            		notas = true;
            	}else if(listaEstadoFacturas.getRowCount() > 0) {	
            		salidas = new ByteArrayInputStream[1];
            		salidas[0] = JsfUtil.serializarReporte(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
            		facturas = true;
            	}else if(listaEstadoNotas.getRowCount() > 0) {
            		salidas = new ByteArrayInputStream[1];
            		salidas[1] = JsfUtil.serializarReporte(reporteNota, parametros, ConectorPool.ESQUEMA_SYSMAN, formatos);
            		notas = true;
            	}
            	if(facturas && notas) {
            		nombresArchivos = new String[2];
            		nombresArchivos[0] = "Facturas.pdf";
            		nombresArchivos[1] = "Notas.pdf";
            	}else if(facturas && notas == false) {
            		nombresArchivos = new String[1];
            		nombresArchivos[0] = "Facturas.pdf";
            	}else if(facturas == false && notas) {
            		nombresArchivos = new String[1];
            		nombresArchivos[1] = "Notas.pdf";
            	}

    			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);
			} else {
				reporte = "800524FEREPORTEBUSQUEDAFACTURAS";
				reporteNota = "800526FEREPORTEBUSQUEDANOTAS";

				String sql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()),
						reemplazar);
				String sql1 = Reporteador.resuelveConsulta(reporteNota, Integer.parseInt(SessionUtil.getModulo()),
						reemplazar);
				ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

				if (listaEstadoFacturas.getRowCount() > 0 && listaEstadoNotas.getRowCount() > 0) {
					salidas = new ByteArrayInputStream[2];
					salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, formatos);
					salidas[1] = JsfUtil.serializarHojaDatos(sql1, ConectorPool.ESQUEMA_SYSMAN, formatos);
					facturas = true;
					notas = true;
				} else if (listaEstadoFacturas.getRowCount() > 0) {
					salidas = new ByteArrayInputStream[1];
					salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, formatos);
					facturas = true;
				} else if (listaEstadoNotas.getRowCount() > 0) {
					salidas = new ByteArrayInputStream[1];
					salidas[0] = JsfUtil.serializarHojaDatos(sql1, ConectorPool.ESQUEMA_SYSMAN, formatos);
					notas = true;
				}

				if (facturas && notas) {
					nombresArchivos = new String[2];
					nombresArchivos[0] = "Facturas.xlsx";
					nombresArchivos[1] = "Notas.xlsx";
				} else if (facturas) {
					nombresArchivos = new String[1];
					nombresArchivos[0] = "Facturas.xlsx";
				} else if (notas) {
					nombresArchivos = new String[1];
					nombresArchivos[0] = "Notas.xlsx";
				}

    			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

            }
        }
        catch (FileNotFoundException e) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }
        catch (JRException | IOException | NumberFormatException | SQLException | DRException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }
	/**
	 * @author ljdiaz
	 */
	private void burcarZipRegListFact() {
		archivoDescarga = null;
		ArrayList<byte[]> facturas = new ArrayList<>();
		ArrayList<String> nombresArchivos = new  ArrayList<>();
		String prefijo = null;
		String certificado = null; 
		String passCertificado = null;
		String nombreCertificado = null;
		try{
			if(listaEstadoFacturas.getFiltradoMultiple().size() != 0) {
				String url2 = ""; 
				url = ejbSysmanUtil.consultarParametro(compania,
		                "URL SERVICIO REST", "69", new Date(), false);
				Registro rsCertificado = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmRangoProduccionDianUrlEnum.URL9457
                                                                                        .getValue())
                                                        .getUrl(),
                                        null));

			        if (rsCertificado != null) {
			
			            File archivo = new File(
			                            rsCertificado.getCampos().get(
			                                            "RUTA_CERTIFICADO")
			                                            .toString());
			
			            nombreCertificado = archivo.getName();
			
			            byte[] archivoBytes = Files
			                            .readAllBytes(archivo.toPath());
			
			            certificado = Base64.getEncoder()
			                            .encodeToString(archivoBytes);
			
			            passCertificado = Base64.getEncoder()
			                            .encodeToString(rsCertificado
			                                            .getCampos()
			                                            .get("CONTRA_CERTIFICADO")
			                                            .toString()
			                                            .getBytes());
			        }

				for (Registro r : listaEstadoFacturas.getFiltradoMultiple()) {
					
					numeroFactura = r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
					
					prefijo = SysmanFunciones
		                                .nvl(r.getCampos().get("PREFIJO"), "")
		                                .toString();
					Date fechaVencimiento = (Date) r.getCampos().get("FECHA");
	                String tipoFormato;
	                if ("NC".equals(prefijo))
	                    tipoFormato = "02";
	                else {
	                    tipoFormato = "ND".equals(prefijo) ? "03" : "01";
	                }
	
	                ParametroEjecucionApiReporte paramEjecucion = new ParametroEjecucionApiReporte();
	
	                ParametroCuerpoEjecucionReporte paramCuerpoEjecucion = new ParametroCuerpoEjecucionReporte();
	
	                Map<String, Object> param2 = new TreeMap<>();

	                param2.put(GeneralParameterEnum.ANO.getName(),
	                                SysmanFunciones.ano(fechaVencimiento));
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
	
	                
	
	                url2 = url + "formato/generarReporte?numFactura="
	                    + numeroFactura
	                    + "&numContribuyente=" + nitCompania + "&tipoFormato="
	                    + tipoFormato + "&prefijo=" + prefijo;
	
	                paramEjecucion.setUrl(url2);
	
	                paramEjecucion.setUsaAdaptador(false);
	
	                paramCuerpoEjecucion.setNumFactura(numeroFactura);
	
	                paramCuerpoEjecucion.setNumContribuyente(nitCompania);
	
	                paramCuerpoEjecucion.setTipoFormato(tipoFormato);
	
	                paramCuerpoEjecucion.setPrefijo(prefijo);
	                
	                /*Parametro que obtiene la configuracion de las actividades economicas que debe ir en la factura electronica CC2974*/
	                String actividadesEconomicas = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							                        "ACTIVIDADES ECONOMICAS EN FACTURA ELECTRONICA", 
							                        SessionUtil.getModulo(), new Date(), true), " ");
	                
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
	
	                if (permiteCodigoBarra) {
	
	                    String codigoEan;
	
	                    Map<String, Object> paramEan = new TreeMap<>();
	                    paramEan.put(GeneralParameterEnum.COMPANIA
	                                    .getName(),
	                                    compania);
	                    paramEan.put("ANO", SysmanFunciones.ano(fechaVencimiento));
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
	                                    r.getCampos().get("TOTAL")
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
	                                    r.getCampos().get("TOTAL")
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
	                if (respuestaApis.getCodigo() == 0) {
	
	                	reporte = Base64.getDecoder().decode(
	                                    respuestaApis.getCuerpo().toString());
	                	facturas.add(reporte);
	                	nombresArchivos.add("Factura_"+prefijo+"_"+numeroFactura+".pdf");

	                }

	                APIFrida api2 = new APIFrida();

	                String respuesta = api2.getXmlDocumento(url, nitCompania, numeroFactura, prefijo, tipoFormato);

	                RespuestaApi respuestaApi = gson2.fromJson(respuesta,
	                                RespuestaApi.class);

	                if (respuestaApi.getCodigo() != 0) {
	                    respuesta = respuestaApi.getMensaje();
	                }else {
	                	// toma la cadena devuelta y crea el Byte que correspondera al archivo xml
	                	byte [] xmlFinal = null;
							xmlFinal = respuestaApi.getCuerpo().toString().getBytes();
	                		facturas.add(xmlFinal);
							nombresArchivos.add("Factura_"+prefijo+"_"+numeroFactura+".xml");
	                }
	            }

			}else {
				JsfUtil.agregarMensajeInformativoVentana(idioma.getString("MSM_FACTURAS_NO_SELECCIONADAS_ZIP"));
			}
			// se crea el ZIP de salida con los datos suministrados
			ByteArrayInputStream[] salidas = new ByteArrayInputStream[facturas.size()];
			String[] nombreArchivos2 = new String[nombresArchivos.size()];
            ZipEntry ze;
            int cont = 0;
            for(byte[] f :facturas) {
	            salidas[cont] = new ByteArrayInputStream(f);
	            nombreArchivos2[cont] = nombresArchivos.get(cont);
	            cont = cont + 1;
			}
			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas,nombreArchivos2 , "Facturas Zip");
		} catch (IOException | SystemException | ParseException | SysmanException | RuntimeException | JRException | SQLException | DRException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	/**
	 * @author ljdiaz
	 */
	private void burcarZipRegListNotas() {
		archivoDescarga = null;
		ArrayList<byte[]> notas = new ArrayList<>();
		ArrayList<String> nombresArchivos = new  ArrayList<>();
		String prefijo = null;
		String certificado = null; 
		String passCertificado = null;
		String nombreCertificado = null;
		try{
			if(listaEstadoNotas.getFiltradoMultiple().size() != 0) {
				String url2 = ""; 
				url = ejbSysmanUtil.consultarParametro(compania,
		                "URL SERVICIO REST", "69", new Date(), false);
				Registro rsCertificado = RegistroConverter
                        .toRegistro(requestManager.get(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        FrmRangoProduccionDianUrlEnum.URL9457
                                                                                        .getValue())
                                                        .getUrl(),
                                        null));

			        if (rsCertificado != null) {
			
			            File archivo = new File(
			                            rsCertificado.getCampos().get(
			                                            "RUTA_CERTIFICADO")
			                                            .toString());
			
			            nombreCertificado = archivo.getName();
			
			            byte[] archivoBytes = Files
			                            .readAllBytes(archivo.toPath());
			
			            certificado = Base64.getEncoder()
			                            .encodeToString(archivoBytes);
			
			            passCertificado = Base64.getEncoder()
			                            .encodeToString(rsCertificado
			                                            .getCampos()
			                                            .get("CONTRA_CERTIFICADO")
			                                            .toString()
			                                            .getBytes());
			        }

				for (Registro r : listaEstadoNotas.getFiltradoMultiple()) {
					
					numeroFactura = r.getCampos().get("NUMFORMATO").toString();
					
					prefijo = SysmanFunciones
		                                .nvl(r.getCampos().get("CLASE"), "")
		                                .toString();
					Date fechaVencimiento = (Date) r.getCampos().get("FECHA");
	                String tipoFormato;
	                if ("NC".equals(prefijo))
	                    tipoFormato = "02";
	                else {
	                    tipoFormato = "ND".equals(prefijo) ? "03" : "01";
	                }
	
	                ParametroEjecucionApiReporte paramEjecucion = new ParametroEjecucionApiReporte();
	
	                ParametroCuerpoEjecucionReporte paramCuerpoEjecucion = new ParametroCuerpoEjecucionReporte();
	                
	                Map<String, Object> param2 = new TreeMap<>();

	                param2.put(GeneralParameterEnum.ANO.getName(),
	                                SysmanFunciones.ano(fechaVencimiento));
	                param2.put("PREFIJO", tipoCobro);
	                
	                Registro rs = RegistroConverter
	                        .toRegistro(requestManager.get(
	                                        UrlServiceUtil.getInstance()
	                                                        .getUrlServiceByUrlByEnumID(
	                                                                        FrmDocDianControladorUrlEnum.URL665033
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
	
	                
	
	                url2 = url + "formato/generarReporte?numFactura="
	                    + numeroFactura
	                    + "&numContribuyente=" + nitCompania + "&tipoFormato="
	                    + tipoFormato + "&prefijo=" + prefijo;
	
	                paramEjecucion.setUrl(url2);
	
	                paramEjecucion.setUsaAdaptador(false);
	
	                paramCuerpoEjecucion.setNumFactura(numeroFactura);
	
	                paramCuerpoEjecucion.setNumContribuyente(nitCompania);
	
	                paramCuerpoEjecucion.setTipoFormato(tipoFormato);
	
	                paramCuerpoEjecucion.setPrefijo(prefijo);
	
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
	
	                if (permiteCodigoBarra) {
	
	                    String codigoEan;
	
	                    Map<String, Object> paramEan = new TreeMap<>();
	                    paramEan.put(GeneralParameterEnum.COMPANIA
	                                    .getName(),
	                                    compania);
	                    paramEan.put("ANO", SysmanFunciones.ano(fechaVencimiento));
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
	                    		SysmanFunciones.nvl(r.getCampos().get("VALOR"),"0")
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
	                    		SysmanFunciones.nvl(r.getCampos().get("VALOR"),"0")
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
	                if (respuestaApis.getCodigo() == 0) {
	
	                	reporte = Base64.getDecoder().decode(
	                                    respuestaApis.getCuerpo().toString());
	                	notas.add(reporte);
	                	nombresArchivos.add("Nota_"+prefijo+"_"+numeroFactura+".pdf");

	                }
	                

	                APIFrida api2 = new APIFrida();

	                String respuesta = api2.getXmlDocumento(url, nitCompania, numeroFactura, prefijo, tipoFormato);

	                RespuestaApi respuestaApi = gson2.fromJson(respuesta,
	                                RespuestaApi.class);

	                if (respuestaApi.getCodigo() != 0) {
	                    respuesta = respuestaApi.getMensaje();
	                }else {
	                	// toma la cadena devuelta y crea el Byte que correspondera al archivo xml
	                	byte [] xmlFinal = null;
							xmlFinal = respuestaApi.getCuerpo().toString().getBytes();
	                		notas.add(xmlFinal);
							nombresArchivos.add("Nota_"+prefijo+"_"+numeroFactura+".xml");
	                }
	            }

			}else {
				JsfUtil.agregarMensajeInformativoVentana(idioma.getString("MSM_FACTURAS_NO_SELECCIONADAS_ZIP"));
			}
			// ojo desde aqui
			ByteArrayInputStream[] salidas = new ByteArrayInputStream[notas.size()];
			String[] nombreArchivos2 = new String[nombresArchivos.size()];
            ZipEntry ze;
            int cont = 0;
            for(byte[] f :notas) {
	            salidas[cont] = new ByteArrayInputStream(f);
	            nombreArchivos2[cont] = nombresArchivos.get(cont);
	            cont = cont + 1;
			}
			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas,nombreArchivos2 , "Notas Zip");
		} catch (IOException | SystemException | ParseException | SysmanException | RuntimeException | JRException | SQLException | DRException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	// </METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {

		if (nitCompania.contains("-")) {
			int fin = nitCompania.indexOf("-");
			nitCompania = nitCompania.substring(0, fin);
		}
		borrarDatosListas();

		// <CODIGO_DESARROLLADO>
		/*
		 * FR2223-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
		 * eliminarTabEstadoFact eliminarTabEstadoNotas Me.Recalc End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	private void borrarDatosListas() {

		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmFacEstadoControladorUrlEnum.URL5474.getValue());

			requestManager.delete(urlDelete.getUrl(), null);

			UrlBean urlDelete2 = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmFacEstadoControladorUrlEnum.URL5587.getValue());

			requestManager.delete(urlDelete2.getUrl(), null);

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	// <SET_GET_ATRIBUTOS>

	/**
	 * Retorna la variable todosFacturas
	 * 
	 * @return todosFacturas
	 */
	public String getTodosFacturas() {
		return todosFacturas;
	}

	public boolean isNotasCredito() {
		return notasCredito;
	}

	public void setNotasCredito(boolean notasCredito) {
		this.notasCredito = notasCredito;
	}

	public boolean isNotasDebito() {
		return notasDebito;
	}

	public void setNotasDebito(boolean notasDebito) {
		this.notasDebito = notasDebito;
	}

	public boolean isFacturas() {
		return facturas;
	}

	public void setFacturas(boolean facturas) {
		this.facturas = facturas;
	}

	/**
	 * Asigna la variable todosFacturas
	 * 
	 * @param todosFacturas Variable a asignar en todosFacturas
	 */
	public void setTodosFacturas(String todosFacturas) {
		this.todosFacturas = todosFacturas;
	}

	/**
	 * Retorna la variable todosNotas
	 * 
	 * @return todosNotas
	 */
	public String getTodosNotas() {
		return todosNotas;
	}

	/**
	 * Asigna la variable todosNotas
	 * 
	 * @param todosNotas Variable a asignar en todosNotas
	 */
	public void setTodosNotas(String todosNotas) {
		this.todosNotas = todosNotas;
	}

	/**
	 * Retorna la variable numeroFactura
	 * 
	 * @return numeroFactura
	 */
	public String getNumeroFactura() {
		return numeroFactura;
	}

	/**
	 * Asigna la variable numeroFactura
	 * 
	 * @param numeroFactura Variable a asignar en numeroFactura
	 */
	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	/**
	 * Retorna la variable estado
	 * 
	 * @return estado
	 */
	public String getEstado() {
		return estado;
	}

	/**
	 * Asigna la variable estado
	 * 
	 * @param estado Variable a asignar en estado
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}

	/**
	 * Retorna la variable tipoConsulta
	 * 
	 * @return tipoConsulta
	 */
	public String getTipoConsulta() {
		return tipoConsulta;
	}

	/**
	 * Asigna la variable tipoConsulta
	 * 
	 * @param tipoConsulta Variable a asignar en tipoConsulta
	 */
	public void setTipoConsulta(String tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}

	/**
	 * Retorna la variable fechaFin
	 * 
	 * @return fechaFin
	 */
	public Date getFechaFin() {
		return fechaFin;
	}

	/**
	 * Asigna la variable fechaFin
	 * 
	 * @param fechaFin Variable a asignar en fechaFin
	 */
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	/**
	 * Retorna la variable fechaInicio
	 * 
	 * @return fechaInicio
	 */
	public Date getFechaInicio() {
		return fechaInicio;
	}

	/**
	 * Asigna la variable fechaInicio
	 * 
	 * @param fechaInicio Variable a asignar en fechaInicio
	 */
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaNumeroFactura
	 * 
	 * @return listaNumeroFactura
	 */
	public List<Registro> getListaNumeroFactura() {
		return listaNumeroFactura;
	}

	/**
	 * Asigna la lista listaNumeroFactura
	 * 
	 * @param listaNumeroFactura Variable a asignar en listaNumeroFactura
	 */
	public void setListaNumeroFactura(List<Registro> listaNumeroFactura) {
		this.listaNumeroFactura = listaNumeroFactura;
	}

	/**
	 * Retorna la lista listaTipoConsulta
	 * 
	 * @return listaTipoConsulta
	 */
	public List<Registro> getListaTipoConsulta() {
		return listaTipoConsulta;
	}

	/**
	 * Asigna la lista listaTipoConsulta
	 * 
	 * @param listaTipoConsulta Variable a asignar en listaTipoConsulta
	 */
	public void setListaTipoConsulta(List<Registro> listaTipoConsulta) {
		this.listaTipoConsulta = listaTipoConsulta;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaEstadoFacturas
	 * 
	 * @return listaEstadoFacturas
	 */
	public RegistroDataModelImpl getListaEstadoFacturas() {
		return listaEstadoFacturas;
	}

	/**
	 * Asigna la lista listaEstadoFacturas
	 * 
	 * @param listaEstadoFacturas Variable a asignar en listaEstadoFacturas
	 */
	public void setListaEstadoFacturas(RegistroDataModelImpl listaEstadoFacturas) {
		this.listaEstadoFacturas = listaEstadoFacturas;
	}

	/**
	 * Retorna la lista listaEstadoNotas
	 * 
	 * @return listaEstadoNotas
	 */
	public RegistroDataModelImpl getListaEstadoNotas() {
		return listaEstadoNotas;
	}

	/**
	 * Asigna la lista listaEstadoNotas
	 * 
	 * @param listaEstadoNotas Variable a asignar en listaEstadoNotas
	 */
	public void setListaEstadoNotas(RegistroDataModelImpl listaEstadoNotas) {
		this.listaEstadoNotas = listaEstadoNotas;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>

	public boolean isBloqueoNumeroFactura() {
		return bloqueoNumeroFactura;
	}

	public void setBloqueoNumeroFactura(boolean bloqueoNumeroFactura) {
		this.bloqueoNumeroFactura = bloqueoNumeroFactura;
	}

	public boolean isBloqueoFecha() {
		return bloqueoFecha;
	}

	public void setBloqueoFecha(boolean bloqueoFecha) {
		this.bloqueoFecha = bloqueoFecha;
	}

	public StreamedContent getarchivoDescargaNotas() {
		return archivoDescargaNotas;
	}

	public void setarchivoDescargaNotas(StreamedContent archivoDescargaNotas) {
		this.archivoDescargaNotas = archivoDescargaNotas;
	}

	public boolean isBloqueoBotonesExportar() {
		return bloqueoBotonesExportar;
	}

	public void setBloqueoBotonesExportar(boolean bloqueoBotonesExportar) {
		this.bloqueoBotonesExportar = bloqueoBotonesExportar;
	}

	public List<Registro> getListaNumeroNota() {
		return listaNumeroNota;
	}

	public void setListaNumeroNota(List<Registro> listaNumeroNota) {
		this.listaNumeroNota = listaNumeroNota;
	}

	public boolean isBloqueoBotonesZip() {
		return bloqueoBotonesZip;
	}

	public void setBloqueoBotonesZip(boolean bloqueoBotonesZip) {
		this.bloqueoBotonesZip = bloqueoBotonesZip;
	}

	// </SET_GET_ADICIONALES>
}
