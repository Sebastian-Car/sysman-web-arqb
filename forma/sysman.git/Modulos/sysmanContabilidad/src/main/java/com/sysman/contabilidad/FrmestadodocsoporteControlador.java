/*-
 * FrmestadodocsoporteControlador.java
 *
 * 1.0
 * 
 * 08/11/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.contabilidad.enums.FrmestadodocsoporteControladorUrlEnum;
import com.sysman.contabilidad.enums.LisBoletinCajaFechasControladorEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.RespuestaConsultarReporte;
import com.sysman.util.rest.RespuestaEnvioFactura;
import com.sysman.util.rest.RespuestaFacturasReporte;
import com.sysman.util.rest.RespuestaFormatoConsultas;
import com.sysman.util.rest.RespuestaNotasReporte;
import com.sysman.util.soap.ApiInvoway;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.InfoEstadosFactura;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 08/11/2022
 * @author ldiaz
 */
@ManagedBean
@ViewScoped
public class FrmestadodocsoporteControlador extends BeanBaseDatosAcme {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean facturas;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String tipoConsulta;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String numeroDocsoporte;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String estado;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaInicio;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFin;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * 
	 */
	private boolean bloqueoFecha;
	/**
	 * 
	 */
	private boolean bloqueoBotonesExportar;
	/**
	 * 
	 */
	private boolean bloqueoNumeroDocsoporte;

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaTipoConsulta;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaNumeroDocSoporte;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEstadoFacturas;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEstadoNotas;
	/**
	 * 
	 */
	private String nitCompania;
	/**
	 * 
	 */
	private String usuario;
	/**
	 * 
	 */
	private String modulo;
	/**
	 * 
	 */
	private String tipoCobro;
	/**
	 * Variable que almacena la url del servicio de FRIDA
	 */
	private String url;
	/**
	 * 
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil = new EjbSysmanUtil();
	/**
	 * 
	 */
	private List<Registro> listaTiposDocSoporte;

	// CONSTANTES DE IMPLEMENTACION CONSUMO DE SERVICIO INVOWAY
	private static final String URL_SERVICIO_SOAP= "URL SERVICIO SOAP";
	private static final String MANEJA_FACTURACION_ELECTRONICA_EXTERNA = "MANEJA FACTURACION ELECTRONICA EXTERNA";
	private static final String USUARIO_FACT_ELECTRONICA_EXTERNA = "USUARIO FACT ELECTRONICA EXTERNA";
	private static final String CLAVE_FACT_ELECTRONICA_EXTERNA = "CLAVE FACT ELECTRONICA EXTERNA";
	private String facturadorExterno = "";
	private String user = "";
	private String pass = "";

	private boolean bloqueoBotonesZip;
//</DECLARAR_LISTAS_COMBO_GRANDE>
//<DECLARAR_LISTAS_SUBFORM>
//</DECLARAR_LISTAS_SUBFORM>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_ADICIONALES>
//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmestadodocsoporteControlador
	 */
	public FrmestadodocsoporteControlador() {
		super();
		compania = SessionUtil.getCompania();
		if (SessionUtil.getCompaniaIngreso().getNit().toString().contains("-")) {
			nitCompania = SessionUtil.getCompaniaIngreso().getNit().toString().split("-")[0];
		} else {
			nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		}
		usuario = SessionUtil.getUser().getCodigo();
		modulo = SessionUtil.getModulo();
		bloqueoNumeroDocsoporte = true;
		bloqueoBotonesExportar = true;
		bloqueoFecha = false;

		fechaFin = new Date();
		fechaInicio = new Date();

		tipoCobro = (String) SessionUtil.getSessionVar(ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
		listaEstadoNotas = new RegistroDataModelImpl();
		listaEstadoFacturas = new RegistroDataModelImpl();
		try {
			// 2373
			numFormulario = GeneralCodigoFormaEnum.ESTADODOCSOPORTEDIAN.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
			// se consultan los parametros de envio a invoway
			// se consulta los parametros para traer el usuario y la contraseña del ws invoway
	        user = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	        		USUARIO_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false),"");
	        
	        pass = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	        		CLAVE_FACT_ELECTRONICA_EXTERNA, "69", new Date(), false),"");
//</INI_ADICIONAL>
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
		cargarListaEstadoFacturas();
		cargarListaEstadoNotas();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		cargarListaTipoConsulta();
		cargarListaNumeroDocSoporte();
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

		consultarTiposDocSoporte();

		cargarListaNumeroDocSoporte();
		cargarListaTipoConsulta();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		origenDatos = "";
	}

	/**
	 * Se realiza la asignacion de la variable origenGrilla por la consulta
	 * correspondiente de la grilla del formulario, se hace la asignacion de dicha
	 * consulta a los objetos listaInicial y listaInicialF
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void reasignarOrigenGrilla() {
		origenGrilla = "";
		if (listaInicial != null) {
			listaInicial.setOrigen(origenGrilla);
		}
		if (listaInicialF != null) {
			listaInicialF.setOrigen(origenGrilla);
		}
	}

//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaTipoConsulta
	 *
	 * TODO DOCUMENTACION ADICIONAL
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
													FrmestadodocsoporteControladorUrlEnum.URL5881.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaNumeroDocSoporte
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaNumeroDocSoporte() {
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.TIPO.getName(), "5");

		try {
			listaNumeroDocSoporte = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmestadodocsoporteControladorUrlEnum.URL5559.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaEstadoFacturas, este metodo tiene el nombre factuas pero apunta al documento soporte
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEstadoFacturas() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmestadodocsoporteControladorUrlEnum.URL6300.getValue());

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
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEstadoNotas() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmestadodocsoporteControladorUrlEnum.URL6811.getValue());

		try {
			listaEstadoNotas = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, false,
					CacheUtil.getLlaveServicio(urlConexionCache, "ESTADO_NOTAS"), true);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
	/**
	 * Metodo ejecutado al cambiar el control TipoConsulta
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarTipoConsulta() {
		// <CODIGO_DESARROLLADO>
		if ("0".equals(tipoConsulta)) {
			bloqueoNumeroDocsoporte = false;
			bloqueoFecha = true;

			insertarFacturasCombo();
			cargarListaNumeroDocSoporte();
		} else {
			bloqueoNumeroDocsoporte = true;
			bloqueoFecha = false;
		}
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEstadoFacturas
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEstadoFacturas(SelectEvent event) {
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEstadoNotas
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEstadoNotas(SelectEvent event) {
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoDocsoporte en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirConsultarEstadoDocsoporte() {
		// <CODIGO_DESARROLLADO>
		borrarDatosDocumentoDian();
		String numeroFactura;
		String prefijoFactura;

		for (Registro r : listaEstadoFacturas.getFiltradoMultiple()) {
			if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) {
				numeroFactura = r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
	
				prefijoFactura = r.getCampos().get(GeneralParameterEnum.PREFIJO.getName()).toString();
	
				if (!"PERSISTIDA".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {
	
					if (consultarEstadoDocDian(numeroFactura, "05", prefijoFactura)) {
	
						String[] campos = { GeneralParameterEnum.COMPANIA.getName() };
	
						String[] valores = { compania };
	
						SessionUtil.cargarModalDatosFlashCerrar(
								Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_SOP_DIAN_CONTROLADOR.getCodigo()),
								modulo, campos, valores);
	
					}
	
				} else {
					JsfUtil.agregarMensajeAlerta("El documento " + r.getCampos().get(GeneralParameterEnum.NUMERO.getName())
							+ " no se ha legalizado frente a la DIAN");
				}
			}else if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())){
				try {
					this.consultarEstadoDocDianInvoway(
							r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), 
							SysmanFunciones.convertirAFechaCadena((Date)r.getCampos().get(GeneralParameterEnum.FECHA.getName()), "yyyy"),
							r.getCampos().get(GeneralParameterEnum.PREFIJO.getName()).toString(),
							"DE");
					
					// se pasan los datos consultados
					String[] campos = { GeneralParameterEnum.COMPANIA.getName() };
					
					String[] valores = { compania };

					SessionUtil.cargarModalDatosFlashCerrar(
							Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_SOP_DIAN_CONTROLADOR.getCodigo()),
							modulo, campos, valores);
				}catch(Exception e) {
					JsfUtil.agregarMensajeAlerta("");
				}
			}
		}
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoNotaAjuste en la vista
	 */
	public void oprimirConsultarEstadoNotaAjuste() {
		// <CODIGO_DESARROLLADO>
		borrarDatosDocumentoDian();
		String numeroFactura;
		String prefijoFactura;

		for (Registro r : listaEstadoNotas.getFiltradoMultiple()) {
			if(facturadorExterno.equals(GeneralParameterEnum.NO.getName())) {
				numeroFactura = r.getCampos().get(GeneralParameterEnum.NUMFORMATO.getName()).toString();
	
				prefijoFactura = r.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString();
	
				if (!"PERSISTIDA".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {
					// 06 TIPO DE LA NOTAS DE AJUSTE
					if (consultarEstadoDocDian(numeroFactura, "06", prefijoFactura)) {
	
						String[] campos = { GeneralParameterEnum.COMPANIA.getName() };
	
						String[] valores = { compania };
	
						SessionUtil.cargarModalDatosFlashCerrar(
								Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_SOP_DIAN_CONTROLADOR.getCodigo()),
								modulo, campos, valores);
	
					}
	
				} else {
					JsfUtil.agregarMensajeAlerta("El documento " + r.getCampos().get(GeneralParameterEnum.NUMFACTURA.getName())
							+ " no se ha legalizado frente a la DIAN");
				}
			}else if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())){
				try {
					this.consultarEstadoDocDianInvoway(
							r.getCampos().get(GeneralParameterEnum.NUMFORMATO.getName()).toString(), 
							SysmanFunciones.convertirAFechaCadena((Date)r.getCampos().get(GeneralParameterEnum.FECHA.getName()), "yyyy"),
							"CNA",
							"NA");
					
					// se pasan los datos consultados
					String[] campos = { GeneralParameterEnum.COMPANIA.getName() };
					
					String[] valores = { compania };

					SessionUtil.cargarModalDatosFlashCerrar(
							Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_DOC_SOP_DIAN_CONTROLADOR.getCodigo()),
							modulo, campos, valores);
				}catch(Exception e) {
					JsfUtil.agregarMensajeAlerta("");
				}
			}
		}
				// </CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * @param numeroFactura
	 * @param ano
	 * @param prefijo
	 * @param clase
	 */
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
	 * 
	 * Metodo ejecutado al oprimir el boton Buscar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirBuscar() {
		// <CODIGO_DESARROLLADO>
		facturas = true;
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
	
					respuesta = api.cargarFormatoConsultarReporte(url, nitCompania, tipoConsulta, numeroDocsoporte, estado,
							SysmanFunciones.convertirAFechaCadena(fechaInicio, "yyyy/MM/dd"),
							SysmanFunciones.convertirAFechaCadena(fechaFin, "yyyy/MM/dd"), "0", "0", "0",
							"1" , "1");
	
					Gson gson = new Gson();
					RespuestaConsultarReporte respuestaApi = gson.fromJson(respuesta, RespuestaConsultarReporte.class);
	
					for (RespuestaFacturasReporte respuestaFacturasReporte : respuestaApi.getCuerpo()
							.getDocumentoSoporte()) {
	
						insertarFacturas(respuestaFacturasReporte);
	
					}
					// Se toman las notas de los documentos soporte
					for (RespuestaNotasReporte respuestaNotasReporte : respuestaApi.getCuerpo().getNotas()) {
	
						insertarNotas(respuestaNotasReporte);
	
					}
	
					cargarListaEstadoFacturas();
					cargarListaEstadoNotas();
					
					bloqueoBotonesExportar = false;
	
				}
			}else if(facturadorExterno.equals(GeneralParameterEnum.SI.getName())) {
				// se buscan las facturas del sistema para validar el estado en invoway
				Map<String, Object>param = new TreeMap<>();
				// primer se buscan todas las facturas
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.FECHAINICIO.getName(), SysmanFunciones.convertirAFechaCadena(fechaInicio, "dd/MM/yyyy"));
				param.put(GeneralParameterEnum.FECHAFIN.getName(), SysmanFunciones.convertirAFechaCadena(fechaFin, "dd/MM/yyyy"));
				
				listaNumeroDocSoporte = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmestadodocsoporteControladorUrlEnum.URL72121.getValue())
												.getUrl(),
										param));
				
				for(Registro reg : listaNumeroDocSoporte) {
					this.consultarDocSoporteDocDianInvoway(reg.getCampos().get(GeneralParameterEnum.CONSECUTIVODIAN.getName()).toString(), 
							reg.getCampos().get(GeneralParameterEnum.ANO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.PREFIJODIAN.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.VLR_DOCUMENTO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString()
							);
				}
				
				
				/* segundo se buscan todas las notas */
				param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), "CNA");
				param.put(GeneralParameterEnum.FECHAINICIO.getName(), SysmanFunciones.convertirAFechaCadena(fechaInicio, "dd/MM/yyyy"));
				param.put(GeneralParameterEnum.FECHAFIN.getName(), SysmanFunciones.convertirAFechaCadena(fechaFin, "dd/MM/yyyy"));
				
				listaNumeroDocSoporte = RegistroConverter
						.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmestadodocsoporteControladorUrlEnum.URL72122.getValue())
											.getUrl(),
									param));
				
				for(Registro reg : listaNumeroDocSoporte) {
					this.consultarNotasDocDianInvoway(reg.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString(), 
							reg.getCampos().get(GeneralParameterEnum.CONSECUTIVODIAN.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.COMPROBANTE_AFECT.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.ANO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.PREFIJODIAN.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.VLR_DOCUMENTO.getName()).toString(),
							reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString()
							);
				}
				// se recargan las listas de los documentos soporte
				cargarListaEstadoFacturas();
				cargarListaEstadoNotas();
				
			}
		} catch (SystemException | ParseException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * @param numeroFactura
	 * @param ano
	 * @param prefijo
	 * @param tercero
	 * @param valorTotal
	 * @param fecha
	 */
	private void consultarDocSoporteDocDianInvoway(String numeroFactura, String ano, String prefijo, String tercero, String valorTotal, String fecha) {
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
						"CC", 
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
	/**
	 * 
	 * @param numeroNota
	 * @param numeroFactura
	 * @param ano
	 * @param prefijo
	 * @param tercero
	 * @param valorTotal
	 * @param fecha
	 */
	private void consultarNotasDocDianInvoway(String numeroNota, String ConsecutivoDian, String numeroFactura, String ano, String prefijo, String tercero, String valorTotal, String fecha) {
		List<InfoEstadosFactura> respuesta = new ArrayList<>();
		try {
			ApiInvoway apiInvoway = new ApiInvoway();			
			
			url = ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_SOAP, "69", new Date(), false);
			
			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
			} else {				
				respuesta = apiInvoway.consultarFactura(url, 
						prefijo+ConsecutivoDian, 
						ano, 
						"NA", 
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
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		obtenerReporte(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton pdf en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirpdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		obtenerReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}
	
	// INI 7731065_CONTABILIDAD (MROSERO)
	
		public void obtenerReporte(FORMATOS formato) {
			String reporte = null;
	        String reporteNota = null;
	        boolean notas = false;
	        boolean facturas = false;
	        String[] nombresArchivos = null;
			try {
				HashMap<String, Object> reemplazar = new HashMap<>();
				reemplazar.put("compania", compania);
				Map<String, Object> parametros = new HashMap<>();
				
				 // MANEJO DE PARAMETROS DEL REPORTE
	            if(formato.name().equals(ReportesBean.FORMATOS.PDF.toString())) {
	            	reporte = "002466ReporteBusquedaDocSoporte";
	            	reporteNota = "002507FEREPORTEBUSQUEDANOTAS";
	            	
	            	ByteArrayInputStream[] salidas = null ;
	            	if(listaEstadoFacturas.getRowCount() > 0 && listaEstadoNotas.getRowCount() > 0) {
	            		Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
		                        reemplazar, parametros);	            		
	            		salidas = new ByteArrayInputStream[2];
	            		salidas[0] = new ByteArrayInputStream(JsfUtil.generarReporte(reporte, 
								parametros, formato, ConectorPool.ESQUEMA_SYSMAN));
	            		facturas = true;	 
	            		// Se consulta el nombre de la compania para el titulo del reporte
	            		UrlBean url = UrlServiceUtil.getInstance()
	            				.getUrlServiceByUrlByEnumID(FrmestadodocsoporteControladorUrlEnum.URL59010.getValue());
	            			
	            		Map<String, Object> param = new TreeMap<>();
	            	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	            	        
	            	    Registro rs = RegistroConverter.toRegistro(
	            	    			requestManager.get(url.getUrl(), param));
	            		String nombreCompania = null;
	            		
    					nombreCompania = rs.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();

	    				parametros = new HashMap<>();
	            		String sql = Reporteador.resuelveConsulta(reporteNota, reemplazar);
	            		parametros.put("PR_NOMBREEMPRESA", SysmanFunciones.nvl(nombreCompania, "Reporte"));
	            		parametros.put("PR_STRSQL", sql);
	            		salidas[1] = new ByteArrayInputStream(JsfUtil.generarReporte(reporteNota, 
								parametros, formato, ConectorPool.ESQUEMA_SYSMAN));
	            		
	            		notas = true;
	            	}else if(listaEstadoFacturas.getRowCount() > 0) {	
	            		salidas = new ByteArrayInputStream[1];
	            		Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
		                        reemplazar, parametros);	
	            		salidas[0] = JsfUtil.serializarReporte(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
	            		facturas = true;
	            	}else if(listaEstadoNotas.getRowCount() > 0) {
	            		salidas = new ByteArrayInputStream[1];
	            		Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
		                        reemplazar, parametros);	
	            		salidas[1] = JsfUtil.serializarReporte(reporteNota, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
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
	            }else {
	            	reporte = "800565ReporteBusquedaDocSoporte";
	            	reporteNota = "800594FEREPORTEBUSQUEDANOTASAJUSTE";
	            	
	            	String sql = Reporteador.resuelveConsulta(reporte,
	        				Integer.parseInt(SessionUtil.getModulo()), reemplazar);
	        		String sql1 = Reporteador.resuelveConsulta(reporteNota,
	        				Integer.parseInt(SessionUtil.getModulo()), reemplazar);
	            	ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

	        		
	            	if(listaEstadoFacturas.getRowCount() > 0 && listaEstadoNotas.getRowCount() > 0) {
		    			salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, formato);
		    			salidas[1] = JsfUtil.serializarHojaDatos(sql1, ConectorPool.ESQUEMA_SYSMAN, formato);
	            	}else if(listaEstadoFacturas.getRowCount() > 0) {
	            		salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, formato);
	            	}else if(listaEstadoNotas.getRowCount() > 0) {
	            		salidas[1] = JsfUtil.serializarHojaDatos(sql1, ConectorPool.ESQUEMA_SYSMAN, formato);
	            	}

	    			nombresArchivos = new String[2];
	    			nombresArchivos[0] = "Facturas.xlsx";
	    			nombresArchivos[1] = "Notas.xlsx";

	    			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

	            }
	        
			} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException
					| SQLException | DRException | NamingException | SystemException e) {
				JsfUtil.agregarMensajeError(
						idioma.getString("MSM_INFORME_VAR_NO_EXISTE").replace("s$reporte$s", reporte) + e.getMessage());
				logger.error(e.getMessage(), e);

			}

		}

	// FIN 7731065_CONTABILIDAD (MROSERO)

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ExportarDocumentosEmitidos en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExportarDocumentosEmitidos() {
		// <CODIGO_DESARROLLADO>
		consultarEstadoFacturasTodas();
		archivoDescarga = null;
		generarInformeEmitidos(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ExportarDocumentosNoEmitidos en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 * @throws ParseException 
	 *
	 */
	public void oprimirExportarDocumentosNoEmitidos() throws ParseException {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInformeNoEmitidos(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_BOTONES>	
//<METODOS_SUBFORM>	
//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>
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
		List<Registro> listaDocSoporte;
		try {
			listaDocSoporte = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmestadodocsoporteControladorUrlEnum.URL1856005.getValue())
											.getUrl(),
									null));
		
			for (Registro r : listaDocSoporte) {
	
				numeroFactura = r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
	
				prefijoFactura = r.getCampos().get("PREFIJO").toString();
	
				if (!"PERSISTIDA".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {
	
					consultarEstadoDocDian(numeroFactura, "05", prefijoFactura);
	
				} else {
					JsfUtil.agregarMensajeAlerta("El documento " + r.getCampos().get(GeneralParameterEnum.NUMERO.getName())
							+ " no se ha legalizado frente a la DIAN");
				}
	
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		consultarEstadoNotasTodas();

	}
	private void generarInformeEmitidos(FORMATOS formato) {
		Map<String, Object> reemplazar = new TreeMap<>();
        reemplazar.put("compania", compania);
		String sql = Reporteador.resuelveConsulta("800546DocsoporteEmitidasDian",
				Integer.parseInt(SessionUtil.getModulo()), reemplazar);
//		String sql1 = Reporteador.resuelveConsulta("800496NotasEmitidasDian", Integer.parseInt(SessionUtil.getModulo()),
//				null);

		ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

		try {
			salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, formato);

//			salidas[1] = JsfUtil.serializarHojaDatos(sql1, ConectorPool.ESQUEMA_SYSMAN, formato);

			String[] nombresArchivos = new String[2];
			nombresArchivos[0] = "FacturasEmitidas.xlsx";
//			nombresArchivos[1] = "NotasEmitidas.xlsx";

			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

		} catch (JRException e) {
			
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (IOException e) {
			
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (SQLException e) {
			
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (DRException e) {
			
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	
	private void generarInformeNoEmitidos(FORMATOS formato) throws ParseException {

		Map<String, Object> reemplazar = new TreeMap<>();
        reemplazar.put("compania", compania);
        reemplazar.put("fechaI", SysmanFunciones.convertirAFechaCadena(fechaInicio, "dd/MM/yyyy"));
        reemplazar.put("fechaF", SysmanFunciones.convertirAFechaCadena(fechaFin, "dd/MM/yyyy"));
        
		String sql = Reporteador.resuelveConsulta("800547DocsoporteNoEmitidasDian",
				Integer.parseInt(SessionUtil.getModulo()), reemplazar);
//		String sql1 = Reporteador.resuelveConsulta("800498NotasNoEmitidasDian", Integer.parseInt(SessionUtil.getModulo()),
//				reemplazar);

		ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

		try {
			salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, formato);

//			salidas[1] = JsfUtil.serializarHojaDatos(sql1, ConectorPool.ESQUEMA_SYSMAN, formato);

			String[] nombresArchivos = new String[2];
			nombresArchivos[0] = "FacturasNoEmitidas.xlsx";
//			nombresArchivos[1] = "NotasNoEmitidas.xlsx";

			archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

		} catch (JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	private boolean consultarEstadoDocDian(String numeroFactura, String clase, String prefijoFactura) {

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

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

	private void insertarEstadoDian(String numeroFactura, String clase, String statusDescription, String documentkey) {
		Map<String, Object> params = new TreeMap<>();

		String urlEnumId = FrmestadodocsoporteControladorUrlEnum.URL4658.getValue();

		try {

			params.put("NUM_DOCUMENTO", numeroFactura);
			params.put(GeneralParameterEnum.TIPO_DOCUMENTO.getName(),
					"05".equals(clase) || "DE".equals(clase) ? "DOC SOPORTE" : "NOTA AJUSTE"

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

	private void borrarDatosDocumentoDian() {
		Map<String, Object> params = new TreeMap<>();

		UrlBean urlDelete = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmestadodocsoporteControladorUrlEnum.URL4567.getValue());

		try {
			requestManager.delete(urlDelete.getUrl(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void insertarFacturas(RespuestaFacturasReporte respuestaFacturasReporte) {

		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

		try {

			String urlEnumId = FrmestadodocsoporteControladorUrlEnum.URL9848.getValue();

			Date fecha = formato.parse(respuestaFacturasReporte.getFecha().replace("-", "/"));

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

	private void borrarDatosListas() {

		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmestadodocsoporteControladorUrlEnum.URL5474.getValue());

			requestManager.delete(urlDelete.getUrl(), null);
			
			UrlBean urlDelete2 = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmestadodocsoporteControladorUrlEnum.URL5587.getValue());

			requestManager.delete(urlDelete2.getUrl(), null);

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void consultarTiposDocSoporte() {
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaTiposDocSoporte = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmestadodocsoporteControladorUrlEnum.URL1895017.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void insertarFacturasCombo() {
		borrarDatosCombo();
		try {
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
					// con esta lista listaTiposDocSoporte comparar y solo selecionar el que tiene
					// un tipo de docsoporte
					List<Object> datos = (List<Object>) respuestaApi.getCuerpo().get(i);
					for (Registro reg : listaTiposDocSoporte) {
						if (reg.getCampos().get("CODIGO").toString().equals(datos.get(1).toString())) {
							insertarListaCombos(datos);
						}
					}

				}

			}

		} catch (SystemException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void insertarListaCombos(List<Object> datos) {

		Map<String, Object> params = new TreeMap<>();

		String urlEnumId = FrmestadodocsoporteControladorUrlEnum.URL5198.getValue();

		try {
			// Se asigna como 5 debido a que es el tipo de documento soporte y asi
			// difernciarlo en caso tal de haber facturas
			params.put("ID", "5");
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
				.getUrlServiceByUrlByEnumID(FrmestadodocsoporteControladorUrlEnum.URL8974.getValue());

		try {
			requestManager.delete(urlDelete.getUrl(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * 
	 * @param respuestaNotasReporte
	 */
	private void insertarNotas(RespuestaNotasReporte respuestaNotasReporte) {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

		try {

			String urlEnumId2 = FrmestadodocsoporteControladorUrlEnum.URL2548.getValue();

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
//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR2223-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
		 * eliminarTabEstadoFact eliminarTabEstadoNotas Me.Recalc End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
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
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
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
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
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
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
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
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable facturas
	 * 
	 * @return facturas
	 */
	public boolean getFacturas() {
		return facturas;
	}

	/**
	 * Asigna la variable facturas
	 * 
	 * @param facturas Variable a asignar en facturas
	 */
	public void setFacturas(boolean facturas) {
		this.facturas = facturas;
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
	 * Retorna la variable numeroDocsoporte
	 * 
	 * @return numeroDocsoporte
	 */
	public String getNumeroDocsoporte() {
		return numeroDocsoporte;
	}

	/**
	 * Asigna la variable numeroDocsoporte
	 * 
	 * @param numeroDocsoporte Variable a asignar en numeroDocsoporte
	 */
	public void setNumeroDocsoporte(String numeroDocsoporte) {
		this.numeroDocsoporte = numeroDocsoporte;
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
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_LISTAS>
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

	/**
	 * Retorna la lista listaNumeroDocSoporte
	 * 
	 * @return listaNumeroDocSoporte
	 */
	public List<Registro> getListaNumeroDocSoporte() {
		return listaNumeroDocSoporte;
	}

	/**
	 * Asigna la lista listaNumeroDocSoporte
	 * 
	 * @param listaNumeroDocSoporte Variable a asignar en listaNumeroDocSoporte
	 */
	public void setListaNumeroDocSoporte(List<Registro> listaNumeroDocSoporte) {
		this.listaNumeroDocSoporte = listaNumeroDocSoporte;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
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
//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES>	
//</SET_GET_ADICIONALES>

	public boolean isBloqueoFecha() {
		return bloqueoFecha;
	}

	public void setBloqueoFecha(boolean bloqueoFecha) {
		this.bloqueoFecha = bloqueoFecha;
	}

	public boolean isBloqueoBotonesExportar() {
		return bloqueoBotonesExportar;
	}

	public void setBloqueoBotonesExportar(boolean bloqueoBotonesExportar) {
		this.bloqueoBotonesExportar = bloqueoBotonesExportar;
	}

	public boolean isBloqueoNumeroDocsoporte() {
		return bloqueoNumeroDocsoporte;
	}

	public void setBloqueoNumeroDocsoporte(boolean bloqueoNumeroDocsoporte) {
		this.bloqueoNumeroDocsoporte = bloqueoNumeroDocsoporte;
	}

	public boolean isBloqueoBotonesZip() {
		return bloqueoBotonesZip;
	}

	public void setBloqueoBotonesZip(boolean bloqueoBotonesZip) {
		this.bloqueoBotonesZip = bloqueoBotonesZip;
	}
}
