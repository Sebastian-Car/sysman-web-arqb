/*-
 * FrmNominaDianControlador.java
 *
 * 1.0
 * 
 * 15/02/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
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
import java.util.Base64;
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
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.nomina.enums.FrmNominaDianControladorEnum;
import com.sysman.nomina.enums.FrmNominaDianControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.RespuestaConsultarDetalleNomina;
import com.sysman.util.rest.RespuestaConsultarNomina;
import com.sysman.util.rest.RespuestaConsultarReporte;
import com.sysman.util.rest.RespuestaEnvioFactura;
import com.sysman.util.rest.RespuestaFacturasReporte;
import com.sysman.util.rest.RespuestaFormatoConsultas;
import com.sysman.util.rest.RespuestaNominaReporte;
import com.sysman.util.rest.RespuestaNotasReporte;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 15/02/2023
 * @author avega
 */
@ManagedBean
@ViewScoped
public class FrmNominaDianControlador extends BeanBaseDatosAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;

	private final String nombreCompania ; 

	private boolean NominaDeAjuste;

	private boolean bloqueoFecha;

	private boolean bloqueoConsecutivo;

	private boolean bloqueoBotonesExportar;

	private boolean NominaDeEliminacion;

	private String todosnominaBase;

	private String todosNotas;

	private String tipoNomina;

	private String consecutivo;

	private String pagTamanio;

	private Date fechaInicio;

	private Date fechaFin;

	private final String usuario;

	private StreamedContent archivoDescarga;

	private List<Registro> listaTipoConsulta;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaConsecutivo;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaNominaAjuste;

	private RegistroDataModelImpl listaNominaEliminacion;

	private RegistroDataModelImpl listaNominaBase;
	//listaEstadonominaBase




	private String nitEmpleador;

	private boolean nominaBase;


	private boolean todosNominaBase;


	private String pagInicio;




	private String url;

	private String tipoCobro;


	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	private String modulo;

	private boolean facturas;


	private boolean ignorarPaginado;



	/**
	 * Crea una nueva instancia de FrmNominaDianControlador
	 */
	public FrmNominaDianControlador() {
		super();
		compania = SessionUtil.getCompania();
		nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
		nitEmpleador = SessionUtil.getCompaniaIngreso().getNit();
		modulo = SessionUtil.getModulo();
		bloqueoFecha = false;
		usuario = SessionUtil.getUser().getCodigo();

		fechaFin = new Date();
		fechaInicio = new Date();
		ignorarPaginado = true;

		listaNominaAjuste = new RegistroDataModelImpl();
		listaNominaBase = new RegistroDataModelImpl();
		listaNominaEliminacion = new RegistroDataModelImpl();

		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_NOMINA_DIAN_CONTROLADOR.getCodigo();
			validarPermisos();

		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@Override
	public void iniciarListas(){
		
	}

	public void cargarlistaNominaBase(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1902006.getValue());

		try {
			listaNominaBase = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null,
					false, CacheUtil.getLlaveServicio(urlConexionCache, "ESTADO_NOM"), true);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarlistaNominaAjuste(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1902008.getValue());

		try {
			listaNominaAjuste = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, false,
					CacheUtil.getLlaveServicio(urlConexionCache, "ESTADO_NOM"), true);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}


	private void cargarListaNominaEliminacion() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1902010.getValue());

		try {
			listaNominaEliminacion = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), null, false,
					CacheUtil.getLlaveServicio(urlConexionCache, "ESTADO_NOM"), true);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	@Override
	public void iniciarListasSub(){

	}

	@Override
	public void iniciarListasSubNulo(){

	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		abrirFormulario();


	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		if (nitEmpleador.contains("-")) {
			int fin = nitEmpleador.indexOf("-");
			nitEmpleador = nitEmpleador.substring(0, fin);
		}
		borrarDatosNominas();
		tipoNomina = "T_NE_BASE";
	}
	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		origenDatos="";	
	}



	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNominaAjuste
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNominaAjuste(SelectEvent event) {
	}

	public void seleccionarFilaNominaEliminacion(SelectEvent event) {
	}

	public void seleccionarFilaNominaBase(SelectEvent event) {
	}

	//Consultar cualquier tipo de Nomina

	public void oprimirConsultarEstadoBase() {
		consultarEstadoDeNomina();
	}

	public void oprimirConsultarEstadoAjuste() {
		consultarEstadoDeNomina();
	}

	public void oprimirConsultarEstadoEliminacion(){
		consultarEstadoDeNomina();
	}


	private void consultarEstadoDeNomina() {

		borrarDatosConsultarNomina();
		String numero;
		String fecha;
		String nie045;

		for (Registro r : listaNominaBase.getSeleccionados()) {

			numero = r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
			tipoNomina = r.getCampos().get(FrmNominaDianControladorEnum.TIPO_NOMINA.getValue()).toString();
			fecha =  r.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString();
			nie045 = r.getCampos().get(GeneralParameterEnum.NUMERO_DOCUMENTO.getName()).toString();


			//T_NE_ACEPTADODIAN
			if (!"Aceptado por DIAN".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {

				if (consultarEstadoDocDian(numero,tipoNomina,fecha,nie045)) {

					String[] campos = { GeneralParameterEnum.NUMERO_DOCUMENTO.getName() };

					String[] valores = { nie045 };

					SessionUtil.cargarModalDatosFlashCerrar(
							Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_NOMINA_CONTROLADOR.getCodigo()),
							modulo, campos, valores);

				}

			} else {
				JsfUtil.agregarMensajeAlerta("El documento " + r.getCampos().get(GeneralParameterEnum.NUMERO.getName())
						+ " no se ha legalizado frente a la DIAN");
			}

		}

	}

	private void borrarDatosConsultarNomina() {

		try {

			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1903001.getValue());

			requestManager.delete(urlDelete.getUrl(), null);


		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private boolean consultarEstadoDocDian(String numero ,String codigoTipoNomina , String fechaGenNie008,String numeroDocumentoNie045) {

		try {

			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST NOMINA ELECTRONICA", "6", new Date(), false);

			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST NOMINA ELECTRONICA");
			} else {

				Registro rs = RegistroConverter
						.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												FrmNominaDianControladorUrlEnum.URL1882001.getValue())
										.getUrl(),
										null));

				if (rs != null) {

					File archivo = new File(rs.getCampos().get("RUTA_CERTIFICADO").toString());

					String testID = rs.getCampos().get("TES_ID").toString();

					String nombreCertificado = archivo.getName();

					byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

					String certBase64 = Base64.getEncoder().encodeToString(archivoBytes);

					String passCert = Base64.getEncoder()
							.encodeToString(rs.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());

					String respuesta;
					String mensaje;
					APIFrida api = new APIFrida();
					String usuarioAccion;
					usuarioAccion = usuario;

					respuesta = api.consultarEstadoNomina(url, codigoTipoNomina, fechaGenNie008, numeroDocumentoNie045, numero, nitEmpleador, usuarioAccion,
							passCert, testID, nombreCertificado, certBase64 );

					Gson gson = new Gson();

					RespuestaConsultarDetalleNomina respuestaApi = gson.fromJson(respuesta, RespuestaConsultarDetalleNomina.class);

					mensaje = respuestaApi.getCuerpo().getObservaciones().toString();

					if (mensaje.isEmpty()) {
						JsfUtil.agregarMensajeErrorDialogo("No existe informacion de la nomina");

					} else {
						insertarEstadoDian(numeroDocumentoNie045, tipoNomina, mensaje);
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


	private void insertarEstadoDian(String num_documento, String tipoNomina, String mensaje) {

		Map<String, Object> params = new TreeMap<>();

		String urlEnumId = FrmNominaDianControladorUrlEnum.URL1903002.getValue();

		try {

			params.put(GeneralParameterEnum.NUMERO_DOCUMENTO.getName(),num_documento);
			params.put(FrmNominaDianControladorEnum.TIPO_NOMINA.getValue(), tipoNomina);
			params.put(FrmNominaDianControladorEnum.MENSAJE.getValue(), mensaje);

			UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), params);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton NomBase_zip
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirNomBase_zip() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton NomAjuste_zip
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirNomAjuste_zip() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarEstadoAjuste
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */


	public void cambiarTipoNomina() {

	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Buscar
	 * en la vista
	 *
	 * 
	 */
	public void oprimirBuscar() {
		//<CODIGO_DESARROLLADO>

		archivoDescarga = null;
		pagInicio = "0";
		pagTamanio = "5";

		borrarDatosNominas();
		if ("LOTE".equals(tipoNomina)) {

			consultarNominas("T_NE_BASE");
			consultarNominas("T_NE_AJUSTE_EL");
			consultarNominas("T_NE_AJUSTE_RE");

		} else{

			consultarNominas(tipoNomina);
		}

	}

	private void consultarNominas(String tipoNomina) {

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST NOMINA ELECTRONICA", "6", new Date(), false);

			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST NOMINA ELECTRONICA");
			} else {
				String respuesta;
				APIFrida api = new APIFrida();

				respuesta = api.consultarTodasLasNominas(url, tipoNomina, pagInicio, pagTamanio,ignorarPaginado,
						SysmanFunciones.convertirAFechaCadena(fechaInicio, "dd/MM/yyyy"),
						SysmanFunciones.convertirAFechaCadena(fechaFin, "dd/MM/yyyy"),nitEmpleador);

				Gson gson = new Gson();

				RespuestaConsultarNomina respuestaApi = gson.fromJson(respuesta, RespuestaConsultarNomina.class);

				if(respuestaApi.getCuerpo().getDatos().size()==0) {
					JsfUtil.agregarMensajeAlerta("No se encontraron datos");
				}else {
					for (RespuestaNominaReporte respuestaNominaBaseReporte : respuestaApi.getCuerpo().getDatos()) {

						insertarNomina(respuestaNominaBaseReporte);

					}
				}

				cargarlistaNominaBase();
				cargarlistaNominaAjuste();
				cargarListaNominaEliminacion();

			}
		} catch (SystemException | ParseException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void insertarNomina(RespuestaNominaReporte respuestaNominaBaseReporte) {

		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

		try {

			String urlEnumId = FrmNominaDianControladorUrlEnum.URL1902002.getValue();

			Date fecha = formato.parse(respuestaNominaBaseReporte.getFECHAGEN().replace("-", "/"));
			HashMap<String, Object> params = new HashMap<>();

			params.put(GeneralParameterEnum.NUMERO_DOCUMENTO.getName(), respuestaNominaBaseReporte.getNUMERO_DOCUMENTO());
			params.put(GeneralParameterEnum.ESTADO.getName(), respuestaNominaBaseReporte.getTIPO_ESTADO());
			params.put(FrmNominaDianControladorEnum.CUNE.getValue(), respuestaNominaBaseReporte.getCUNE());
			params.put(GeneralParameterEnum.NUMERO.getName(), respuestaNominaBaseReporte.getNUMERO());
			params.put(GeneralParameterEnum.FECHA.getName(), fecha);
			params.put(FrmNominaDianControladorEnum.TIPO_NOMINA.getValue(), respuestaNominaBaseReporte.getTIPO_NOMINA());
			params.put(GeneralParameterEnum.OBSERVACIONES.getName(), respuestaNominaBaseReporte.getOBSERVACIONES());
			params.put(FrmNominaDianControladorEnum.CODIGO_TRABAJADOR.getValue(), respuestaNominaBaseReporte.getCODIGO_TRABAJADOR());
			UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);

			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), params);
		} catch (SystemException | ParseException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}


	public void oprimirExcel() {
		archivoDescarga=null;

		obtenerReporte(FORMATOS.EXCEL,tipoNomina);


	}

	public void oprimirPdf() {
		archivoDescarga=null;
		obtenerReporte(FORMATOS.PDF,tipoNomina);;
	}

	/**
	 * 
	 * Metodo que genera el reporte tanto en pdf como en excel
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void obtenerReporte(FORMATOS formatos,String Nomina) { 


		String base="002442Electronica_NominaBase";

		String extension = null;
		if (formatos == ReportesBean.FORMATOS.PDF)
		{
			extension = ".pdf";
		}
		else
		{
			extension = ".xls";
		}

		try {

			if(tipoNomina.equals("LOTE")) {
				String[] nombresArchivos = {"NOMINA BASE" + extension,
											"NOMINA AJUSTE" + extension};
											//"NOMINA ELIMINACION" + extension};

				Map<String, Object> parametrosB = new HashMap<>();
				Map<String, Object> parametrosA = new HashMap<>();
				Map<String, Object> parametrosE = new HashMap<>();
				
					
					HashMap<String, Object> reemplazarB = new HashMap<>();					
					HashMap<String, Object> reemplazarA = new HashMap<>();					
					HashMap<String, Object> reemplazarE = new HashMap<>();
					ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

					parametrosB.put("PR_NOMBRECOMPANIA",
							SessionUtil.getCompaniaIngreso().getNombre());
					parametrosB.put("PR_NOMBREREPORTE","REPORTE DE NOMINA BASE");
					reemplazarB.put("tipo_Nomina","'T_NE_BASE'");
					
					parametrosA.put("PR_NOMBRECOMPANIA",
							SessionUtil.getCompaniaIngreso().getNombre());
					parametrosA.put("PR_NOMBREREPORTE","REPORTE DE NOMINA AJUSTE");
					reemplazarA.put("tipo_Nomina","'T_NE_AJUSTE_RE'");
					
					parametrosE.put("PR_NOMBRECOMPANIA",
							SessionUtil.getCompaniaIngreso().getNombre());
					parametrosE.put("PR_NOMBREREPORTE","REPORTE DE NOMINA ELIMINACION");
					reemplazarE.put("tipo_Nomina","'T_NE_AJUSTE_EL'");

					String consultaB = Reporteador.resuelveConsulta("002442Electronica_NominaBase",
							Integer.parseInt(modulo), reemplazarB);
					
					parametrosB.put("PR_STRSQL", consultaB);
					
					salidas[0] = JsfUtil.serializarReporte(
							"002442Electronica_NominaBase", parametrosB,
							ConectorPool.ESQUEMA_SYSMAN, formatos);
					
					String consultaA = Reporteador.resuelveConsulta("002442Electronica_NominaBase",
							Integer.parseInt(modulo), reemplazarA);
					
					parametrosA.put("PR_STRSQL", consultaA);
					salidas[1] = JsfUtil.serializarReporte(
							"002442Electronica_NominaBase", parametrosA,
							ConectorPool.ESQUEMA_SYSMAN, formatos);
					
					String consultaE = Reporteador.resuelveConsulta("002442Electronica_NominaBase",
							Integer.parseInt(modulo), reemplazarE);
					
					parametrosE.put("PR_STRSQL", consultaE);
					/*salidas[2] = JsfUtil.serializarReporte(
							"002442Electronica_NominaBase", parametrosE,
							ConectorPool.ESQUEMA_SYSMAN, formatos);*/
					
					
					archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas,
							nombresArchivos);
					
			
		
				
			}else {
			
				HashMap<String, Object> parametros = new HashMap<>();
				parametros.put("PR_NOMBRECOMPANIA",nombreCompania);
				HashMap<String, Object> reemplazar = new HashMap<>();
				
				switch(tipoNomina) {
				case "T_NE_BASE": 
					parametros.put("PR_NOMBREREPORTE","REPORTE DE NOMINA BASE");
					reemplazar.put("tipo_Nomina","'T_NE_BASE'");
					break;
				case "T_NE_AJUSTE_RE": 
					parametros.put("PR_NOMBREREPORTE","REPORTE DE NOMINA DE AJUSTE");
					reemplazar.put("tipo_Nomina","'T_NE_AJUSTE_RE'");
					break;
				case "T_NE_AJUSTE_EL": 
					parametros.put("PR_NOMBREREPORTE","REPORTE DE NOMINA DE ELIMINACION");
					reemplazar.put("tipo_Nomina","'T_NE_AJUSTE_EL'");
					break;
				}
				
				
				

				
				// MANEJO DE PARAMETROS DEL REPORTE
				if(formatos.name().equals("PDF")) {

					Reporteador.resuelveConsulta(base,
							Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);

					ByteArrayInputStream[] salidas = new ByteArrayInputStream[1];

					salidas[0] = JsfUtil.serializarReporte(base, parametros, ConectorPool.ESQUEMA_SYSMAN,ReportesBean.FORMATOS.PDF);


					String[] nombresArchivos = new String[1];
					nombresArchivos[0] = "NOMINA BASE.pdf";

					archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);
				}else {

					String sql = Reporteador.resuelveConsulta(base,
							Integer.parseInt(SessionUtil.getModulo()), reemplazar);
					ByteArrayInputStream[] salidas = new ByteArrayInputStream[1];


					salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, formatos);

					String[] nombresArchivos = new String[1];
					nombresArchivos[0] = "NOMINA_BASE.xlsx";

					archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);
				}
			}
			
		}
		catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
        catch (SQLException | JRException | IOException | DRException ex) {
       
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        catch ( NumberFormatException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	
		
	}


/**
 * 
 * Metodo ejecutado al oprimir el boton ExportarDocumentosEmitidos
 * en la vista
 *
 * TODO DOCUMENTACION ADICIONAL
 *
 */
public void oprimirExportarDocumentosEmitidos() {
	consultarEstadoTodasNominas();
	archivoDescarga=null;            
	//generarInformeEmitidos(FORMATOS.EXCEL);
	JsfUtil.agregarMensajeAlerta(
			"Asegurese de configurar el certificado en el formulario CONFIGURACION CERTIFICADO DIAN");
}
/*private void generarInformeEmitidos(FORMATOS excel) {
	
	HashMap<String, Object> reemplazar = new HashMap<>();

	String sql = Reporteador.resuelveConsulta("002442Electronica_NominaBase",
			Integer.parseInt(SessionUtil.getModulo()), null);

	ByteArrayInputStream[] salidas = new ByteArrayInputStream[1];

	try {
		salidas[0] = JsfUtil.serializarHojaDatos(sql, ConectorPool.ESQUEMA_SYSMAN, excel);

		String[] nombresArchivos = new String[1];
		nombresArchivos[0] = "NominasEmitidas.xlsx";

		archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

	} catch (JRException | IOException | SQLException | DRException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	} 
}*/

private void consultarEstadoTodasNominas() {
	borrarDatosConsultarNomina();
	

	String numero;
	String fecha;
	String nie045;

	for (Registro r : listaNominaBase) {

		numero = r.getCampos().get(GeneralParameterEnum.NUMERO.getName()).toString();
		tipoNomina = r.getCampos().get(FrmNominaDianControladorEnum.TIPO_NOMINA.getValue()).toString();
		fecha =  r.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString();
		nie045 = r.getCampos().get(GeneralParameterEnum.NUMERO_DOCUMENTO.getName()).toString();


		//T_NE_ACEPTADODIAN
		if (!"Aceptado por DIAN".equals(r.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {

			if (consultarEstadoDocDian(numero,tipoNomina,fecha,nie045)) {

				String[] campos = { GeneralParameterEnum.NUMERO_DOCUMENTO.getName() };

				String[] valores = { nie045 };

				SessionUtil.cargarModalDatosFlashCerrar(
						Integer.toString(GeneralCodigoFormaEnum.FRM_ESTADO_NOMINA_CONTROLADOR.getCodigo()),
						modulo, campos, valores);

			}

		} else {
			JsfUtil.agregarMensajeAlerta("El documento " + r.getCampos().get(GeneralParameterEnum.NUMERO.getName())
					+ " no se ha legalizado frente a la DIAN");
		}break;

	}
	
}

/**
 * 
 * Metodo ejecutado al oprimir el boton ExportarDocumentosNoEmitidos
 * en la vista
 *
 * TODO DOCUMENTACION ADICIONAL
 *
 */
public void oprimirExportarDocumentosNoEmitidos() {
	consultarEstadoTodasNominas();
	archivoDescarga=null;            
	//generarInformeEmitidos(FORMATOS.EXCEL); 
	JsfUtil.agregarMensajeAlerta(
			"Asegurese de configurar el certificado en el formulario CONFIGURACION CERTIFICADO DIAN");
	
}

private void borrarDatosNominas() {
	// TODO Auto-generated method stub
	try {

		UrlBean urlDelete = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmNominaDianControladorUrlEnum.URL1902001.getValue());

		requestManager.delete(urlDelete.getUrl(), null);


	} catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}

/**
 * Metodo ejecutado en el momento despues de cargar el registro
 * 
 * TODO DOCUMENTACION ADICIONAL
 */
@Override
public void cargarRegistro() {
	//<CODIGO_DESARROLLADO>
	precargarRegistro();
	//</CODIGO_DESARROLLADO>
}

/**
 * Metodo ejecutado antes de realizar la insercion del registro
 * TODO DOCUMENTACION ADICIONAL
 * 
 * @return TODO VARIABLE
 */
@Override
public boolean insertarAntes(){
	//<CODIGO_DESARROLLADO>
	registro.getCampos().put("COMPANIA", compania);
	//</CODIGO_DESARROLLADO>
	return true;
}
@Override
public boolean actualizarDespues(){
	//<CODIGO_DESARROLLADO>
	//</CODIGO_DESARROLLADO>
	return true;
}
//<SET_GET_ATRIBUTOS>
/**
 * Retorna la variable facturas
 * 
 * @return  facturas
 */
public boolean getNominaNase() {
	return facturas;
}
/**
 * Asigna la variable  facturas
 * 
 * @param  facturas
 * Variable a asignar en  facturas
 */
public void setNominaNase(boolean facturas) {
	this.facturas = facturas;
}
/**
 * Retorna la variable NominaDeAjuste
 * 
 * @return  NominaDeAjuste
 */
public boolean getNominaDeAjuste() {
	return NominaDeAjuste;
}
/**
 * Asigna la variable  NominaDeAjuste
 * 
 * @param  NominaDeAjuste
 * Variable a asignar en  NominaDeAjuste
 */
public void setNominaDeAjuste(boolean NominaDeAjuste) {
	this.NominaDeAjuste = NominaDeAjuste;
}
/**
 * Retorna la variable NominaDeEliminacion
 * 
 * @return  NominaDeEliminacion
 */
public boolean getNominaDeEliminacion() {
	return NominaDeEliminacion;
}
/**
 * Asigna la variable  NominaDeEliminacion
 * 
 * @param  NominaDeEliminacion
 * Variable a asignar en  NominaDeEliminacion
 */

/**
 * Retorna la variable todosnominaBase
 * 
 * @return  todosnominaBase
 */
public String getTodosnominaBase() {
	return todosnominaBase;
}
/**
 * Asigna la variable  todosnominaBase
 * 
 * @param  todosnominaBase
 * Variable a asignar en  todosnominaBase
 */
public void setTodosnominaBase(String todosnominaBase) {
	this.todosnominaBase = todosnominaBase;
}
/**
 * Retorna la variable todosNotas
 * 
 * @return  todosNotas
 */
public String getTodosNotas() {
	return todosNotas;
}
/**
 * Asigna la variable  todosNotas
 * 
 * @param  todosNotas
 * Variable a asignar en  todosNotas
 */
public void setTodosNotas(String todosNotas) {
	this.todosNotas = todosNotas;
}

/**
 * Retorna la variable consecutivo
 * 
 * @return  consecutivo
 */
public String getConsecutivo() {
	return consecutivo;
}
/**
 * Asigna la variable  consecutivo
 * 
 * @param  consecutivo
 * Variable a asignar en  consecutivo
 */
public void setConsecutivo(String consecutivo) {
	this.consecutivo = consecutivo;
}
/**
 * Retorna la variable pagTamanio
 * 
 * @return  pagTamanio
 */
public String getEstado() {
	return pagTamanio;
}
/**
 * Asigna la variable  pagTamanio
 * 
 * @param  pagTamanio
 * Variable a asignar en  pagTamanio
 */
public void setEstado(String pagTamanio) {
	this.pagTamanio = pagTamanio;
}
/**
 * Retorna la variable fechaInicio
 * 
 * @return  fechaInicio
 */
public Date getFechaInicio() {
	return fechaInicio;
}
/**
 * Asigna la variable  fechaInicio
 * 
 * @param  fechaInicio
 * Variable a asignar en  fechaInicio
 */
public void setFechaInicio(Date fechaInicio) {
	this.fechaInicio = fechaInicio;
}
/**
 * Retorna la variable fechaFin
 * 
 * @return  fechaFin
 */
public Date getFechaFin() {
	return fechaFin;
}
/**
 * Asigna la variable  fechaFin
 * 
 * @param  fechaFin
 * Variable a asignar en  fechaFin
 */
public void setFechaFin(Date fechaFin) {
	this.fechaFin = fechaFin;
}
/**
 * Atributo usado para descargar contenidos de archivos desde la
 * vista
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
 * @param listaTipoConsulta
 * Variable a asignar en  listaTipoConsulta
 */
public void setListaTipoConsulta(List<Registro> listaTipoConsulta) {
	this.listaTipoConsulta = listaTipoConsulta;
}
/**
 * Retorna la lista listaConsecutivo
 * 
 * @return listaConsecutivo
 */
public List<Registro> getListaConsecutivo() {
	return listaConsecutivo;
}
/**
 * Asigna la lista listaConsecutivo
 * 
 * @param listaConsecutivo
 * Variable a asignar en  listaConsecutivo
 */
public void setListaConsecutivo(List<Registro> listaConsecutivo) {
	this.listaConsecutivo = listaConsecutivo;
}
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	


//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES>	
//</SET_GET_ADICIONALES>
/**
 * @return the bloqueoNumeroFactura
 */

/**
 * @return the listaNominaAjuste
 */
public RegistroDataModelImpl getlistaNominaAjuste() {
	return listaNominaAjuste;
}
/**
 * @param listaNominaAjuste the listaNominaAjuste to set
 */
public void setlistaNominaAjuste(RegistroDataModelImpl listaNominaAjuste) {
	this.listaNominaAjuste = listaNominaAjuste;
}
/**
 * @return the listaNominaBase
 */
public RegistroDataModelImpl getlistaNominaBase() {
	return listaNominaBase;
}
/**
 * @param listaNominaBase the listaNominaBase to set
 */
public void setlistaNominaBase(RegistroDataModelImpl listaNominaBase) {
	this.listaNominaBase = listaNominaBase;
}
/**
 * @param bloqueoNumeroFactura the bloqueoNumeroFactura to set
 */

/**
 * @return the bloqueoFecha
 */
public boolean isBloqueoFecha() {
	return bloqueoFecha;
}
/**
 * @param bloqueoFecha the bloqueoFecha to set
 */
public void setBloqueoFecha(boolean bloqueoFecha) {
	this.bloqueoFecha = bloqueoFecha;
}
/**
 * @return the bloqueoConsecutivo
 */
public boolean isBloqueoConsecutivo() {
	return bloqueoConsecutivo;
}
/**
 * @param bloqueoConsecutivo the bloqueoConsecutivo to set
 */
public void setBloqueoConsecutivo(boolean bloqueoConsecutivo) {
	this.bloqueoConsecutivo = bloqueoConsecutivo;
}
/**
 * @return the bloqueoBotonesExportar
 */
public boolean isBloqueoBotonesExportar() {
	return bloqueoBotonesExportar;
}
/**
 * @param bloqueoBotonesExportar the bloqueoBotonesExportar to set
 */
public void setBloqueoBotonesExportar(boolean bloqueoBotonesExportar) {
	this.bloqueoBotonesExportar = bloqueoBotonesExportar;
}
/**
 * @param archivoDescarga the archivoDescarga to set
 */
public void setArchivoDescarga(StreamedContent archivoDescarga) {
	this.archivoDescarga = archivoDescarga;
}

/**
 * @return the NominaDeEliminacion
 */
public boolean isNominaDeEliminacion() {
	return NominaDeEliminacion;
}
/**
 * @param NominaDeEliminacion the NominaDeEliminacion to set
 */
public void setNominaDeEliminacion(boolean NominaDeEliminacion) {
	this.NominaDeEliminacion = NominaDeEliminacion;
}
/**
 * @return the NominaDeAjuste
 */
public boolean isNominaDeAjuste() {
	return NominaDeAjuste;
}
/**
 * @param NominaDeAjuste the NominaDeAjuste to set
 */

/**
 * @return the nominaBase
 */
public boolean isnominaBase() {
	return nominaBase;
}
/**
 * @param nominaBase the nominaBase to set
 */
public void setnominaBase(boolean nominaBase) {
	this.nominaBase = nominaBase;
}
/**
 * @return the pagInicio
 */
public String getNumeroFactura() {
	return pagInicio;
}
/**
 * @param pagInicio the pagInicio to set
 */
public void setNumeroFactura(String pagInicio) {
	this.pagInicio = pagInicio;
}

/**
 * @return the tipoCobro
 */
public String getTipoCobro() {
	return tipoCobro;
}
/**
 * @param tipoCobro the tipoCobro to set
 */
public void setTipoCobro(String tipoCobro) {
	this.tipoCobro = tipoCobro;
}
/**
 * @return the modulo
 */
public String getModulo() {
	return modulo;
}
/**
 * @param modulo the modulo to set
 */
public void setModulo(String modulo) {
	this.modulo = modulo;
}

/**
 * @return the todosNominaBase
 */
public boolean isTodosNominaBase() {
	return todosNominaBase;
}

/**
 * @param todosNominaBase the todosNominaBase to set
 */
public void setTodosNominaBase(boolean todosNominaBase) {
	this.todosNominaBase = todosNominaBase;
}

/**
 * @return the listaNominaEliminacion
 */
public RegistroDataModelImpl getListaNominaEliminacion() {
	return listaNominaEliminacion;
}

/**
 * @param listaNominaEliminacion the listaNominaEliminacion to set
 */
public void setListaNominaEliminacion(RegistroDataModelImpl listaNominaEliminacion) {
	this.listaNominaEliminacion = listaNominaEliminacion;
}

/**
 * @return the listaNominaAjuste
 */
public RegistroDataModelImpl getListaNominaAjuste() {
	return listaNominaAjuste;
}

/**
 * @param listaNominaAjuste the listaNominaAjuste to set
 */
public void setListaNominaAjuste(RegistroDataModelImpl listaNominaAjuste) {
	this.listaNominaAjuste = listaNominaAjuste;
}

/**
 * @return the listaNominaBase
 */
public RegistroDataModelImpl getListaNominaBase() {
	return listaNominaBase;
}

/**
 * @param listaNominaBase the listaNominaBase to set
 */
public void setListaNominaBase(RegistroDataModelImpl listaNominaBase) {
	this.listaNominaBase = listaNominaBase;
}

/**
 * @return the nitEmpleador
 */
public String getNitCompania() {
	return nitEmpleador;
}

/**
 * @param nitEmpleador the nitEmpleador to set
 */
public void setNitCompania(String nitEmpleador) {
	this.nitEmpleador = nitEmpleador;
}

/**
 * @return the nominaBase
 */
public boolean isNominaBase() {
	return nominaBase;
}

/**
 * @param nominaBase the nominaBase to set
 */
public void setNominaBase(boolean nominaBase) {
	this.nominaBase = nominaBase;
}

/**
 * @return the facturas
 */
public boolean isFacturas() {
	return facturas;
}

/**
 * @param facturas the facturas to set
 */
public void setFacturas(boolean facturas) {
	this.facturas = facturas;
}

/**
 * @return the tipoNomina
 */
public String getTipoNomina() {
	return tipoNomina;
}

/**
 * @param tipoNomina the tipoNomina to set
 */
public void setTipoNomina(String tipoNomina) {
	this.tipoNomina = tipoNomina;
}

/**
 * @return the pagTamanio
 */
public String getPagTamanio() {
	return pagTamanio;
}

/**
 * @param pagTamanio the pagTamanio to set
 */
public void setPagTamanio(String pagTamanio) {
	this.pagTamanio = pagTamanio;
}




@Override
public boolean insertarDespues() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean actualizarAntes() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean eliminarAntes() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean eliminarDespues() {
	// TODO Auto-generated method stub
	return false;
}



}
