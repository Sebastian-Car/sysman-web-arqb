/*-
 * ReporteEvaluacionesControlador.java
 *
 * 1.0
 * 
 * 26/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.ReporteEvaluacionesControladorEnum;
import com.sysman.hojasdevida.enums.ReporteEvaluacionesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Migracion del formulario access FRM_REPORTEEVALUACIONES a web controlador
 * ReporteEvaluacionesControlador forma frmreporteevaluaciones.xhtml creacion de
 * menu para abrir el formulario modal, creacion de properties para el
 * formulario modal, asi como generacion del informe a partir de un boton.
 * 
 * @version 1.0, 26/01/2018
 * @author crodriguez
 * 
 * @version 2.0, 16/04/2018
 * @author fperez
 * 
 *         Se añadieron campos para la descripción de cada uno de los combos.
 */
@ManagedBean
@ViewScoped
public class ReporteEvaluacionesControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la
	 * cual inicio sesion el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>

	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String evaluacionInicial;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String evaluacionFinal;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String cargoEvaluadorInicial;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String cargoEvaluadorFinal;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String cargoEvaluadoInicial;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String cargoEvaluadoFinal;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String criterioInicial;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String criterioFinal;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String escalafonInicialEvaluador;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String escalafonFinalEvaluador;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String escalafonInicialEvaluado;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private String escalafonFinalEvaluado;
	// </DECLARAR_ATRIBUTOS>

	private String claseEvaluacion;

	/** Fecha inicial de la evaluación. */
	private Date fechaEvaluacionIni;

	/** Fecha final de la evaluación. */
	private Date fechaEvaluacionFin;

	/** Nombre del escalafón del evaluador inicial. */
	private String nombreEscalafonEvaluadorIni;

	/** Nombre del escalafón del evaluador final. */
	private String nombreEscalafonEvaluadorFin;

	/** Nombre del cargo del evaluador inicial. */
	private String nombreCargoEvaluadorIni;

	/** Nombre del cargo del evaluador final. */
	private String nombreCargoEvaluadorFin;

	/** Nombre del escalafón del evaluado inicial. */
	private String nombreEscalafonEvaluadoIni;

	/** Nombre del escalafón del evaluado final. */
	private String nombreEscalafonEvaluadoFin;

	/** Nombre del cargo del evaluadi inicial. */
	private String nombreCargoEvaluadoIni;

	/** Nombre del cargo del evaluado final. */
	private String nombreCargoEvaluadoFin;

	/** Nombre del criterio inicial. */
	private String nombreCriterioIni;

	/** Nombre del criterio final. */
	private String nombreCriterioFin;

	private Date fechaIni;

	private Date fechaFin;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEvaluacionIni;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEvaluacionFin;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCargoEvaluadorIni;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCargoEvaluadorFin;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCargoEvaluadoIni;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCargoEvaluadoFin;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCriterioIni;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCriterioFin;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEscalafonIEvaluador;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEscalafonFEvaluador;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEscalafonIEvaluado;
	/**
	 * DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEscalafonFEvaluado;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ReporteEvaluacionesControlador
	 */
	public ReporteEvaluacionesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			// 1654
			numFormulario = GeneralCodigoFormaEnum.REPORTE_EVALUACIONES_CONTROLADOR.getCodigo();
			registro = new Registro(new HashMap<String, Object>());
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del
	 * Bean ha sido creado, en este se realizan las asignaciones iniciales
	 * necesarias para la visualizacion del formulario, como son tablas,
	 * origenes de datos, inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {

		claseEvaluacion = SessionUtil.getSessionVar(ConstantesHojasDeVidaEnum.CLASE_EVALUACION.getValue()).toString();

		// <CARGAR_LISTA>
		cargarListaEvaluacionIni();

		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCargoEvaluadorIni();
		cargarListaCargoEvaluadoIni();
		cargarListaCriterioIni();
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR1654-AL_ABRIR Private Sub Form_Open(Cancel As Integer) Me.Caption =
		 * UCase("REPORTE EVALUACIONES " & GetNClaseEvaluacion()) End Sub
		 */
		// </CODIGO_DESARROLLADO>
		fechaIni = fechaFin = new Date();
	}

	// <MES_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaEvaluacionIni
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEvaluacionIni() {

		// 947001
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL260.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaEvaluacionIni = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params, true,
				ReporteEvaluacionesControladorEnum.CONSECUTIVO.getValue());

	}

	/**
	 * 
	 * Carga la lista listaEvaluacionFin
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEvaluacionFin() {

		// 947003
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL288.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.CODIGO.getName(), evaluacionInicial);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaEvaluacionFin = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params, true,
				ReporteEvaluacionesControladorEnum.CONSECUTIVO.getValue());

	}

	/**
	 * 
	 * Carga la lista listaCargoEvaluadorIni
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCargoEvaluadorIni() {
		// 939005
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL313.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaCargoEvaluadorIni = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params,
				true, ReporteEvaluacionesControladorEnum.ID_DE_CARGO.getValue());

	}

	/**
	 * 
	 * Carga la lista listaCargoEvaluadorFin
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCargoEvaluadorFin() {

		// 939013
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL338.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.CODIGO.getName(), cargoEvaluadorInicial);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaCargoEvaluadorFin = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params,
				true, ReporteEvaluacionesControladorEnum.ID_DE_CARGO.getValue());
	}

	/**
	 * 
	 * Carga la lista listaCargoEvaluadoIni
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCargoEvaluadoIni() {

		// 939007 cargo evaluado

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL365.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaCargoEvaluadoIni = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params,
				true, ReporteEvaluacionesControladorEnum.ID_DE_CARGO.getValue());

	}

	/**
	 * 
	 * Carga la lista listaCargoEvaluadoFin
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCargoEvaluadoFin() {

		// 939015
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL388.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.CODIGO.getName(), cargoEvaluadoInicial);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaCargoEvaluadoFin = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params,
				true, ReporteEvaluacionesControladorEnum.ID_DE_CARGO.getValue());

	}

	/**
	 * 
	 * Carga la lista listaCriterioIni
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCriterioIni() {
		// 752003
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL415.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaCriterioIni = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCriterioFin
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCriterioFin() {
		// 752005

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL439.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(ReporteEvaluacionesControladorEnum.CODIGO_INICIAL.getValue(), criterioInicial);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaCriterioFin = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaEscalafonFEvaluador
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEscalafonFEvaluador() {
		// 939009
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL491.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(ReporteEvaluacionesControladorEnum.CODIGO_INICIAL.getValue(), escalafonInicialEvaluador);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaEscalafonFEvaluador = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaEscalafonIEvaluado
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEscalafonIEvaluado() {

		// 939003 Carga Escalafón Evaluado.
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL516.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaEscalafonIEvaluado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaEscalafonFEvaluado
	 *
	 * DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEscalafonFEvaluado() {

		// 939011
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReporteEvaluacionesControladorUrlEnum.URL540.getValue());

		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(ReporteEvaluacionesControladorEnum.CODIGO_INICIAL.getValue(), escalafonInicialEvaluado);
		params.put(ReporteEvaluacionesControladorEnum.CLASE_EVALUACION.getValue(), claseEvaluacion);

		listaEscalafonFEvaluado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), params,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	// </MES_CARGAR_LISTA>

	public void generarInforme(ReportesBean.FORMATOS formato) {
		Map<String, Object> remplazar = new HashMap<>();
		remplazar.put("claseEvaluacion", claseEvaluacion);
		remplazar.put("evaluacionInicial", evaluacionInicial);
		remplazar.put("evaluacionfinal", evaluacionFinal);
		remplazar.put("fechaIni", SysmanFunciones.formatearFecha(fechaIni));
		remplazar.put("fechaFin", SysmanFunciones.formatearFecha(fechaFin));
		remplazar.put("cargoEvaluadorInicial", cargoEvaluadorInicial);
		remplazar.put("cargoEvaluadorFinal", cargoEvaluadorFinal);
		remplazar.put("cargoEvaluadoInicial", cargoEvaluadoInicial);
		remplazar.put("cargoEvaluadoFinal", cargoEvaluadoFinal);
		remplazar.put("escalafonInicialEvaluador", escalafonInicialEvaluador);
		remplazar.put("escalafonFinalEvaluador", escalafonFinalEvaluador);
		remplazar.put("escalafonInicialEvaluado", escalafonInicialEvaluado);
		remplazar.put("escalafonFinalEvaluado", escalafonFinalEvaluado);
		remplazar.put("criterioInicial", "'" + criterioInicial + "'");
		remplazar.put("criterioFinal", "'" + criterioFinal + "'");

		Map<String, Object> params = new HashMap<>();
		Map<String, Object> paramsClase = new TreeMap<>();

		paramsClase.put(GeneralParameterEnum.CODIGO.getName(), claseEvaluacion);

		try {
			Registro registroClase = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ReporteEvaluacionesControladorUrlEnum.URL565.getValue())
											.getUrl(),
									paramsClase));
			params.put("PR_NOMBRE_CLASE_EVALUACION",
					registroClase.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));

			Reporteador.resuelveConsulta("001673INFINFORMEEVALUACIONES", Integer.parseInt(SessionUtil.getModulo()),
					remplazar, params);

			archivoDescarga = JsfUtil.exportarStreamed("001673INFINFORMEEVALUACIONES", params,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (SystemException | JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// <MES_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Presentar en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPresentar() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	// </MES_BOTONES>
	// <MES_CAMBIAR>
	// </MES_CAMBIAR>
	// <MES_COMBOS_GRANDES>

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEvaluacionIni
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 * @throws ParseException
	 */
	public void seleccionarFilaEvaluacionIni(SelectEvent event) throws ParseException {
		Registro registroAux = (Registro) event.getObject();
		evaluacionInicial = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.CONSECUTIVO.getValue())
				.toString();

		fechaEvaluacionIni = (Date) registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.FECHA.getValue());

		evaluacionFinal = null;
		cargarListaEvaluacionFin();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEvaluacionFin
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEvaluacionFin(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		evaluacionFinal = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.CONSECUTIVO.getValue())
				.toString();

		fechaEvaluacionFin = (Date) registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.FECHA.getValue());
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCargoEvaluadorIni
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCargoEvaluadorIni(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cargoEvaluadorInicial = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.ID_DE_CARGO.getValue())
				.toString();

		nombreCargoEvaluadorIni = registroAux.getCampos()
				.get(ReporteEvaluacionesControladorEnum.NOMBRE_DEL_CARGO.getValue()).toString();

		cargoEvaluadorFinal = null;
		nombreCargoEvaluadorFin = null;
		escalafonFinalEvaluador = null;
		nombreEscalafonEvaluadorFin = null;

		cargarListaCargoEvaluadorFin();

		escalafonInicialEvaluador = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.CODIGO.getValue())
				.toString();

		nombreEscalafonEvaluadorIni = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.NOMBRE.getValue())
				.toString();

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCargoEvaluadorFin
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCargoEvaluadorFin(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cargoEvaluadorFinal = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.ID_DE_CARGO.getValue())
				.toString();
		nombreCargoEvaluadorFin = registroAux.getCampos()
				.get(ReporteEvaluacionesControladorEnum.NOMBRE_DEL_CARGO.getValue()).toString();

		escalafonFinalEvaluador = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.CODIGO.getValue())
				.toString();

		nombreEscalafonEvaluadorFin = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.NOMBRE.getValue())
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCargoEvaluadoIni
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCargoEvaluadoIni(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cargoEvaluadoInicial = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.ID_DE_CARGO.getValue())
				.toString();

		nombreCargoEvaluadoIni = registroAux.getCampos()
				.get(ReporteEvaluacionesControladorEnum.NOMBRE_DEL_CARGO.getValue()).toString();

		escalafonInicialEvaluado = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.CODIGO.getValue())
				.toString();

		nombreEscalafonEvaluadoIni = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.NOMBRE.getValue())
				.toString();

		cargoEvaluadoFinal = null;
		nombreCargoEvaluadoFin = null;
		escalafonFinalEvaluado = null;
		nombreEscalafonEvaluadoFin = null;

		cargarListaCargoEvaluadoFin();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCargoEvaluadoFin
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCargoEvaluadoFin(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cargoEvaluadoFinal = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.ID_DE_CARGO.getValue())
				.toString();

		nombreCargoEvaluadoFin = registroAux.getCampos()
				.get(ReporteEvaluacionesControladorEnum.NOMBRE_DEL_CARGO.getValue()).toString();

		escalafonFinalEvaluado = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.CODIGO.getValue())
				.toString();

		nombreEscalafonEvaluadoFin = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.NOMBRE.getValue())
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCriterioIni
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCriterioIni(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		criterioInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();

		nombreCriterioIni = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.NOMBRE.getValue())
				.toString();

		criterioFinal = null;
		cargarListaCriterioFin();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCriterioFin
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCriterioFin(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		criterioFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();

		nombreCriterioFin = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.NOMBRE.getValue())
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEscalafonIEvaluado
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEscalafonIEvaluado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		escalafonInicialEvaluado = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();

		nombreEscalafonEvaluadoIni = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.NOMBRE.getValue())
				.toString();

		escalafonFinalEvaluado = null;
		cargarListaEscalafonFEvaluado();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEscalafonFEvaluado
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEscalafonFEvaluado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		escalafonFinalEvaluado = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();

		nombreEscalafonEvaluadoFin = registroAux.getCampos().get(ReporteEvaluacionesControladorEnum.NOMBRE.getValue())
				.toString();
	}

	// </MES_COMBOS_GRANDES>
	// <MES_ARBOL>
	// </MES_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable evaluacionInicial
	 * 
	 * @return evaluacionInicial
	 */
	public String getEvaluacionInicial() {
		return evaluacionInicial;
	}

	/**
	 * Asigna la variable evaluacionInicial
	 * 
	 * @param evaluacionInicial
	 *            Variable a asignar en evaluacionInicial
	 */
	public void setEvaluacionInicial(String evaluacionInicial) {
		this.evaluacionInicial = evaluacionInicial;
	}

	/**
	 * Retorna la variable evaluacionFinal
	 * 
	 * @return evaluacionFinal
	 */
	public String getEvaluacionFinal() {
		return evaluacionFinal;
	}

	/**
	 * Asigna la variable evaluacionFinal
	 * 
	 * @param evaluacionFinal
	 *            Variable a asignar en evaluacionFinal
	 */
	public void setEvaluacionFinal(String evaluacionFinal) {
		this.evaluacionFinal = evaluacionFinal;
	}

	/**
	 * Retorna la variable cargoEvaluadorInicial
	 * 
	 * @return cargoEvaluadorInicial
	 */
	public String getCargoEvaluadorInicial() {
		return cargoEvaluadorInicial;
	}

	/**
	 * Asigna la variable cargoEvaluadorInicial
	 * 
	 * @param cargoEvaluadorInicial
	 *            Variable a asignar en cargoEvaluadorInicial
	 */
	public void setCargoEvaluadorInicial(String cargoEvaluadorInicial) {
		this.cargoEvaluadorInicial = cargoEvaluadorInicial;
	}

	/**
	 * Retorna la variable cargoEvaluadorFinal
	 * 
	 * @return cargoEvaluadorFinal
	 */
	public String getCargoEvaluadorFinal() {
		return cargoEvaluadorFinal;
	}

	/**
	 * Asigna la variable cargoEvaluadorFinal
	 * 
	 * @param cargoEvaluadorFinal
	 *            Variable a asignar en cargoEvaluadorFinal
	 */
	public void setCargoEvaluadorFinal(String cargoEvaluadorFinal) {
		this.cargoEvaluadorFinal = cargoEvaluadorFinal;
	}

	/**
	 * Retorna la variable cargoEvaluadoInicial
	 * 
	 * @return cargoEvaluadoInicial
	 */
	public String getCargoEvaluadoInicial() {
		return cargoEvaluadoInicial;
	}

	/**
	 * Asigna la variable cargoEvaluadoInicial
	 * 
	 * @param cargoEvaluadoInicial
	 *            Variable a asignar en cargoEvaluadoInicial
	 */
	public void setCargoEvaluadoInicial(String cargoEvaluadoInicial) {
		this.cargoEvaluadoInicial = cargoEvaluadoInicial;
	}

	/**
	 * Retorna la variable cargoEvaluadoFinal
	 * 
	 * @return cargoEvaluadoFinal
	 */
	public String getCargoEvaluadoFinal() {
		return cargoEvaluadoFinal;
	}

	/**
	 * Asigna la variable cargoEvaluadoFinal
	 * 
	 * @param cargoEvaluadoFinal
	 *            Variable a asignar en cargoEvaluadoFinal
	 */
	public void setCargoEvaluadoFinal(String cargoEvaluadoFinal) {
		this.cargoEvaluadoFinal = cargoEvaluadoFinal;
	}

	/**
	 * Retorna la variable criterioInicial
	 * 
	 * @return criterioInicial
	 */
	public String getCriterioInicial() {
		return criterioInicial;
	}

	/**
	 * Asigna la variable criterioInicial
	 * 
	 * @param criterioInicial
	 *            Variable a asignar en criterioInicial
	 */
	public void setCriterioInicial(String criterioInicial) {
		this.criterioInicial = criterioInicial;
	}

	/**
	 * Retorna la variable criterioFinal
	 * 
	 * @return criterioFinal
	 */
	public String getCriterioFinal() {
		return criterioFinal;
	}

	/**
	 * Asigna la variable criterioFinal
	 * 
	 * @param criterioFinal
	 *            Variable a asignar en criterioFinal
	 */
	public void setCriterioFinal(String criterioFinal) {
		this.criterioFinal = criterioFinal;
	}

	/**
	 * Retorna la variable escalafonInicialEvaluador
	 * 
	 * @return escalafonInicialEvaluador
	 */
	public String getEscalafonInicialEvaluador() {
		return escalafonInicialEvaluador;
	}

	/**
	 * Asigna la variable escalafonInicialEvaluador
	 * 
	 * @param escalafonInicialEvaluador
	 *            Variable a asignar en escalafonInicialEvaluador
	 */
	public void setEscalafonInicialEvaluador(String escalafonInicialEvaluador) {
		this.escalafonInicialEvaluador = escalafonInicialEvaluador;
	}

	/**
	 * Retorna la variable escalafonFinalEvaluador
	 * 
	 * @return escalafonFinalEvaluador
	 */
	public String getEscalafonFinalEvaluador() {
		return escalafonFinalEvaluador;
	}

	/**
	 * Asigna la variable escalafonFinalEvaluador
	 * 
	 * @param escalafonFinalEvaluador
	 *            Variable a asignar en escalafonFinalEvaluador
	 */
	public void setEscalafonFinalEvaluador(String escalafonFinalEvaluador) {
		this.escalafonFinalEvaluador = escalafonFinalEvaluador;
	}

	/**
	 * Retorna la variable escalafonInicialEvaluado
	 * 
	 * @return escalafonInicialEvaluado
	 */
	public String getEscalafonInicialEvaluado() {
		return escalafonInicialEvaluado;
	}

	/**
	 * Asigna la variable escalafonInicialEvaluado
	 * 
	 * @param escalafonInicialEvaluado
	 *            Variable a asignar en escalafonInicialEvaluado
	 */
	public void setEscalafonInicialEvaluado(String escalafonInicialEvaluado) {
		this.escalafonInicialEvaluado = escalafonInicialEvaluado;
	}

	/**
	 * Retorna la variable escalafonFinalEvaluado
	 * 
	 * @return escalafonFinalEvaluado
	 */
	public String getEscalafonFinalEvaluado() {
		return escalafonFinalEvaluado;
	}

	/**
	 * Asigna la variable escalafonFinalEvaluado
	 * 
	 * @param escalafonFinalEvaluado
	 *            Variable a asignar en escalafonFinalEvaluado
	 */
	public void setEscalafonFinalEvaluado(String escalafonFinalEvaluado) {
		this.escalafonFinalEvaluado = escalafonFinalEvaluado;
	}

	/**
	 * Retorna la variable fechaEvaluacionIni
	 * 
	 * @return fechaEvaluacionIni
	 */
	public Date getFechaEvaluacionIni() {
		return fechaEvaluacionIni;
	}

	/**
	 * Asigna la variable fechaEvaluacionIni
	 * 
	 * @param fechaEvaluacionIni
	 *            Variable a asignar en fechaEvaluacionIni
	 */
	public void setFechaEvaluacionIni(Date fechaEvaluacionIni) {
		this.fechaEvaluacionIni = fechaEvaluacionIni;
	}

	/**
	 * Retorna la variable fechaEvaluacionFin
	 * 
	 * @return fechaEvaluacionFin
	 */
	public Date getFechaEvaluacionFin() {
		return fechaEvaluacionFin;
	}

	/**
	 * Asigna la variable fechaEvaluacionFin
	 * 
	 * @param fechaEvaluacionFin
	 *            Variable a asignar en fechaEvaluacionFin
	 */
	public void setFechaEvaluacionFin(Date fechaEvaluacionFin) {
		this.fechaEvaluacionFin = fechaEvaluacionFin;
	}

	/**
	 * Retorna la variable nombreEscalafonEvaluadorIni
	 * 
	 * @return nombreEscalafonEvaluadorIni
	 */
	public String getNombreEscalafonEvaluadorIni() {
		return nombreEscalafonEvaluadorIni;
	}

	/**
	 * Asigna la variable nombreEscalafonEvaluadorIni
	 * 
	 * @param nombreEscalafonEvaluadorIni
	 *            Variable a asignar en nombreEscalafonEvaluadorIni
	 */
	public void setNombreEscalafonEvaluadorIni(String nombreEscalafonEvaluadorIni) {
		this.nombreEscalafonEvaluadorIni = nombreEscalafonEvaluadorIni;
	}

	/**
	 * Retorna la variable nombreEscalafonEvaluadorFin
	 * 
	 * @return nombreEscalafonEvaluadorFin
	 */
	public String getNombreEscalafonEvaluadorFin() {
		return nombreEscalafonEvaluadorFin;
	}

	/**
	 * Asigna la variable nombreEscalafonEvaluadorIni
	 * 
	 * @param nombreEscalafonEvaluadorFin
	 *            Variable a asignar en nombreEscalafonEvaluadorFin
	 */
	public void setNombreEscalafonEvaluadorFin(String nombreEscalafonEvaluadorFin) {
		this.nombreEscalafonEvaluadorFin = nombreEscalafonEvaluadorFin;
	}

	/**
	 * Retorna la variable nombreCargoEvaluadorIni
	 * 
	 * @return nombreCargoEvaluadorIni
	 */
	public String getNombreCargoEvaluadorIni() {
		return nombreCargoEvaluadorIni;
	}

	/**
	 * Asigna la variable nombreCargoEvaluadorIni
	 * 
	 * @param nombreCargoEvaluadorIni
	 *            Variable a asignar en nombreCargoEvaluadorIni
	 */
	public void setNombreCargoEvaluadorIni(String nombreCargoEvaluadorIni) {
		this.nombreCargoEvaluadorIni = nombreCargoEvaluadorIni;
	}

	/**
	 * Retorna la variable nombreCargoEvaluadorFin
	 * 
	 * @return nombreCargoEvaluadorFin
	 */
	public String getNombreCargoEvaluadorFin() {
		return nombreCargoEvaluadorFin;
	}

	/**
	 * Asigna la variable nombreCargoEvaluadorFin
	 * 
	 * @param nombreCargoEvaluadorFin
	 *            Variable a asignar en nombreCargoEvaluadorFin
	 */
	public void setNombreCargoEvaluadorFin(String nombreCargoEvaluadorFin) {
		this.nombreCargoEvaluadorFin = nombreCargoEvaluadorFin;
	}

	/**
	 * Retorna la variable nombreEscalafonEvaluadoIni
	 * 
	 * @return nombreEscalafonEvaluadoIni
	 */
	public String getNombreEscalafonEvaluadoIni() {
		return nombreEscalafonEvaluadoIni;
	}

	/**
	 * Asigna la variable nombreEscalafonEvaluadoIni
	 * 
	 * @param nombreEscalafonEvaluadoIni
	 *            Variable a asignar en nombreEscalafonEvaluadoIni
	 */
	public void setNombreEscalafonEvaluadoIni(String nombreEscalafonEvaluadoIni) {
		this.nombreEscalafonEvaluadoIni = nombreEscalafonEvaluadoIni;
	}

	/**
	 * Retorna la variable nombreEscalafonEvaluadoFin
	 * 
	 * @return nombreEscalafonEvaluadoFin
	 */
	public String getNombreEscalafonEvaluadoFin() {
		return nombreEscalafonEvaluadoFin;
	}

	/**
	 * Asigna la variable nombreEscalafonEvaluadoFin
	 * 
	 * @param nombreEscalafonEvaluadoFin
	 *            Variable a asignar en nombreEscalafonEvaluadoFin
	 */
	public void setNombreEscalafonEvaluadoFin(String nombreEscalafonEvaluadoFin) {
		this.nombreEscalafonEvaluadoFin = nombreEscalafonEvaluadoFin;
	}

	/**
	 * Retorna la variable nombreCargoEvaluadoIni
	 * 
	 * @return nombreCargoEvaluadoIni
	 */
	public String getNombreCargoEvaluadoIni() {
		return nombreCargoEvaluadoIni;
	}

	/**
	 * Asigna la variable nombreCargoEvaluadoIni
	 * 
	 * @param nombreCargoEvaluadoIni
	 *            Variable a asignar en nombreCargoEvaluadoIni
	 */
	public void setNombreCargoEvaluadoIni(String nombreCargoEvaluadoIni) {
		this.nombreCargoEvaluadoIni = nombreCargoEvaluadoIni;
	}

	/**
	 * Retorna la variable nombreCargoEvaluadoFin
	 * 
	 * @return nombreCargoEvaluadoFin
	 */
	public String getNombreCargoEvaluadoFin() {
		return nombreCargoEvaluadoFin;
	}

	/**
	 * Asigna la variable nombreCargoEvaluadoFin
	 * 
	 * @param nombreCargoEvaluadoFin
	 *            Variable a asignar en nombreCargoEvaluadoFin
	 */
	public void setNombreCargoEvaluadoFin(String nombreCargoEvaluadoFin) {
		this.nombreCargoEvaluadoFin = nombreCargoEvaluadoFin;
	}

	/**
	 * Retorna la variable nombreCriterioIni
	 * 
	 * @return nombreCriterioIni
	 */
	public String getNombreCriterioIni() {
		return nombreCriterioIni;
	}

	/**
	 * Asigna la variable nombreCriterioIni
	 * 
	 * @param nombreCriterioIni
	 *            Variable a asignar en nombreCriterioIni
	 */
	public void setNombreCriterioIni(String nombreCriterioIni) {
		this.nombreCriterioIni = nombreCriterioIni;
	}

	/**
	 * Retorna la variable nombreCriterioFin
	 * 
	 * @return nombreCriterioFin
	 */
	public String getNombreCriterioFin() {
		return nombreCriterioFin;
	}

	/**
	 * Asigna la variable nombreCriterioFin
	 * 
	 * @param nombreCriterioFin
	 *            Variable a asignar en nombreCriterioFin
	 */
	public void setNombreCriterioFin(String nombreCriterioFin) {
		this.nombreCriterioFin = nombreCriterioFin;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaEvaluacionIni
	 * 
	 * @return listaEvaluacionIni
	 */
	public RegistroDataModelImpl getListaEvaluacionIni() {
		return listaEvaluacionIni;
	}

	/**
	 * Asigna la lista listaEvaluacionIni
	 * 
	 * @param listaEvaluacionIni
	 *            Variable a asignar en listaEvaluacionIni
	 */
	public void setListaEvaluacionIni(RegistroDataModelImpl listaEvaluacionIni) {
		this.listaEvaluacionIni = listaEvaluacionIni;
	}

	/**
	 * Retorna la lista listaEvaluacionFin
	 * 
	 * @return listaEvaluacionFin
	 */
	public RegistroDataModelImpl getListaEvaluacionFin() {
		return listaEvaluacionFin;
	}

	/**
	 * Asigna la lista listaEvaluacionFin
	 * 
	 * @param listaEvaluacionFin
	 *            Variable a asignar en listaEvaluacionFin
	 */
	public void setListaEvaluacionFin(RegistroDataModelImpl listaEvaluacionFin) {
		this.listaEvaluacionFin = listaEvaluacionFin;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaCargoEvaluadorIni
	 * 
	 * @return listaCargoEvaluadorIni
	 */
	public RegistroDataModelImpl getListaCargoEvaluadorIni() {
		return listaCargoEvaluadorIni;
	}

	/**
	 * Asigna la lista listaCargoEvaluadorIni
	 * 
	 * @param listaCargoEvaluadorIni
	 *            Variable a asignar en listaCargoEvaluadorIni
	 */
	public void setListaCargoEvaluadorIni(RegistroDataModelImpl listaCargoEvaluadorIni) {
		this.listaCargoEvaluadorIni = listaCargoEvaluadorIni;
	}

	/**
	 * Retorna la lista listaCargoEvaluadorFin
	 * 
	 * @return listaCargoEvaluadorFin
	 */
	public RegistroDataModelImpl getListaCargoEvaluadorFin() {
		return listaCargoEvaluadorFin;
	}

	/**
	 * Asigna la lista listaCargoEvaluadorFin
	 * 
	 * @param listaCargoEvaluadorFin
	 *            Variable a asignar en listaCargoEvaluadorFin
	 */
	public void setListaCargoEvaluadorFin(RegistroDataModelImpl listaCargoEvaluadorFin) {
		this.listaCargoEvaluadorFin = listaCargoEvaluadorFin;
	}

	/**
	 * Retorna la lista listaCargoEvaluadoIni
	 * 
	 * @return listaCargoEvaluadoIni
	 */
	public RegistroDataModelImpl getListaCargoEvaluadoIni() {
		return listaCargoEvaluadoIni;
	}

	/**
	 * Asigna la lista listaCargoEvaluadoIni
	 * 
	 * @param listaCargoEvaluadoIni
	 *            Variable a asignar en listaCargoEvaluadoIni
	 */
	public void setListaCargoEvaluadoIni(RegistroDataModelImpl listaCargoEvaluadoIni) {
		this.listaCargoEvaluadoIni = listaCargoEvaluadoIni;
	}

	/**
	 * Retorna la lista listaCargoEvaluadoFin
	 * 
	 * @return listaCargoEvaluadoFin
	 */
	public RegistroDataModelImpl getListaCargoEvaluadoFin() {
		return listaCargoEvaluadoFin;
	}

	/**
	 * Asigna la lista listaCargoEvaluadoFin
	 * 
	 * @param listaCargoEvaluadoFin
	 *            Variable a asignar en listaCargoEvaluadoFin
	 */
	public void setListaCargoEvaluadoFin(RegistroDataModelImpl listaCargoEvaluadoFin) {
		this.listaCargoEvaluadoFin = listaCargoEvaluadoFin;
	}

	/**
	 * Retorna la lista listaCriterioIni
	 * 
	 * @return listaCriterioIni
	 */
	public RegistroDataModelImpl getListaCriterioIni() {
		return listaCriterioIni;
	}

	/**
	 * Asigna la lista listaCriterioIni
	 * 
	 * @param listaCriterioIni
	 *            Variable a asignar en listaCriterioIni
	 */
	public void setListaCriterioIni(RegistroDataModelImpl listaCriterioIni) {
		this.listaCriterioIni = listaCriterioIni;
	}

	/**
	 * Retorna la lista listaCriterioFin
	 * 
	 * @return listaCriterioFin
	 */
	public RegistroDataModelImpl getListaCriterioFin() {
		return listaCriterioFin;
	}

	/**
	 * Asigna la lista listaCriterioFin
	 * 
	 * @param listaCriterioFin
	 *            Variable a asignar en listaCriterioFin
	 */
	public void setListaCriterioFin(RegistroDataModelImpl listaCriterioFin) {
		this.listaCriterioFin = listaCriterioFin;
	}

	/**
	 * Retorna la lista listaEscalafonIEvaluador
	 * 
	 * @return listaEscalafonIEvaluador
	 */
	public RegistroDataModelImpl getListaEscalafonIEvaluador() {
		return listaEscalafonIEvaluador;
	}

	/**
	 * Asigna la lista listaEscalafonIEvaluador
	 * 
	 * @param listaEscalafonIEvaluador
	 *            Variable a asignar en listaEscalafonIEvaluador
	 */
	public void setListaEscalafonIEvaluador(RegistroDataModelImpl listaEscalafonIEvaluador) {
		this.listaEscalafonIEvaluador = listaEscalafonIEvaluador;
	}

	/**
	 * Retorna la lista listaEscalafonFEvaluador
	 * 
	 * @return listaEscalafonFEvaluador
	 */
	public RegistroDataModelImpl getListaEscalafonFEvaluador() {
		return listaEscalafonFEvaluador;
	}

	/**
	 * Asigna la lista listaEscalafonFEvaluador
	 * 
	 * @param listaEscalafonFEvaluador
	 *            Variable a asignar en listaEscalafonFEvaluador
	 */
	public void setListaEscalafonFEvaluador(RegistroDataModelImpl listaEscalafonFEvaluador) {
		this.listaEscalafonFEvaluador = listaEscalafonFEvaluador;
	}

	/**
	 * Retorna la lista listaEscalafonIEvaluado
	 * 
	 * @return listaEscalafonIEvaluado
	 */
	public RegistroDataModelImpl getListaEscalafonIEvaluado() {
		return listaEscalafonIEvaluado;
	}

	/**
	 * Asigna la lista listaEscalafonIEvaluado
	 * 
	 * @param listaEscalafonIEvaluado
	 *            Variable a asignar en listaEscalafonIEvaluado
	 */
	public void setListaEscalafonIEvaluado(RegistroDataModelImpl listaEscalafonIEvaluado) {
		this.listaEscalafonIEvaluado = listaEscalafonIEvaluado;
	}

	/**
	 * Retorna la lista listaEscalafonFEvaluado
	 * 
	 * @return listaEscalafonFEvaluado
	 */
	public RegistroDataModelImpl getListaEscalafonFEvaluado() {
		return listaEscalafonFEvaluado;
	}

	/**
	 * Asigna la lista listaEscalafonFEvaluado
	 * 
	 * @param listaEscalafonFEvaluado
	 *            Variable a asignar en listaEscalafonFEvaluado
	 */
	public void setListaEscalafonFEvaluado(RegistroDataModelImpl listaEscalafonFEvaluado) {
		this.listaEscalafonFEvaluado = listaEscalafonFEvaluado;
	}

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

	@Override
	public void cargarRegistro() {
		precargarRegistro();
	}

	@Override
	public void iniciarListasSubNulo() {
		// No hay código aquí.

	}

	@Override
	public void iniciarListasSub() {
		// No hay código aquí.

	}

	@Override
	public void iniciarListas() {
		cargarListaEvaluacionIni();
		cargarListaEvaluacionFin();
		cargarListaCargoEvaluadorIni();
		cargarListaCargoEvaluadorFin();
		cargarListaCargoEvaluadoIni();
		cargarListaCargoEvaluadoFin();
		cargarListaCriterioIni();
		cargarListaCriterioFin();
		cargarListaEscalafonFEvaluador();
		cargarListaEscalafonIEvaluado();
		cargarListaEscalafonFEvaluado();

	}

	@Override
	public void asignarOrigenDatos() {
		origenDatos = "";

	}

	@Override
	public boolean insertarAntes() {
		// No hay código aquí.
		return false;
	}

	@Override
	public boolean insertarDespues() {
		// No hay código aquí.
		return false;
	}

	@Override
	public boolean actualizarAntes() {
		// No hay código aquí.
		return false;
	}

	@Override
	public boolean actualizarDespues() {
		// No hay código aquí.
		return false;
	}

	@Override
	public boolean eliminarAntes() {
		// No hay código aquí.
		return false;
	}

	@Override
	public boolean eliminarDespues() {
		// No hay código aquí.
		return false;
	}
}
