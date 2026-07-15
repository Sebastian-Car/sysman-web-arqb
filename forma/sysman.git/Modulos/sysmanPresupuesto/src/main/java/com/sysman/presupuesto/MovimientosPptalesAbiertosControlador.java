/*-
 * MovimientosPptalesAbiertosControlador.java
 *
 * 1.0
 * 
 * 17/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import  com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.MovimientoPptalesAbiertosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * Formulario que permite imprimir reportes dependiendo a lo seleccionado
 *
 * @version 1.0, 17/11/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  MovimientosPptalesAbiertosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;

	private int tipoCuenta;
	private boolean ckCentroCosto;
	private boolean ckTercero;
	private boolean ckAuxiliar;
	private boolean ckReferencia;
	private boolean ckFuente;
	private String ano;
	private String comprobanteInicial;
	private String comprobanteFinal;
	private String centroInicial;
	private String centroFinal;
	private String terceroInicial;
	private String terceroFinal;
	private String auxiliarInicial;
	private String auxiliarFinal;
	private String referenciaInicial;
	private String referenciaFinal;
	private String fuenteInicial;
	private String fuenteFinal;
	private Date fechaInicial;
	private Date fechaFinal;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;

	private List<Registro> listaAno;
	private RegistroDataModelImpl listaComprobanteInicial;
	private RegistroDataModelImpl listaComprobanteFinal;
	private RegistroDataModelImpl listaCentroInicial;
	private RegistroDataModelImpl listaCentroFinal;
	private RegistroDataModelImpl listaTerceroInicial;
	private RegistroDataModelImpl listaTerceroFinal;
	private RegistroDataModelImpl listaAuxiliarInicial;
	private RegistroDataModelImpl listaAuxiliarFinal;
	private RegistroDataModelImpl listaReferenciaInicial;
	private RegistroDataModelImpl listaReferenciaFinal;
	private RegistroDataModelImpl listaFuenteInicial;
	private RegistroDataModelImpl listaFuenteFinal;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * Crea una nueva instancia de MovimientosPptalesAbiertosControlador
	 */
	public MovimientosPptalesAbiertosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		ano = Integer.toString(SysmanFunciones.ano(new Date()));
		fechaInicial = fechaFinal = new Date();
		tipoCuenta = 1;
		try {
			numFormulario=1987;
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}
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
		cargarListaAno();
		cargarListaComprobanteInicial();
		cargarListaCentroInicial();
		cargarListaTerceroInicial();
		cargarListaAuxiliarInicial();
		cargarListaReferenciaInicial();
		cargarListaFuenteInicial(); 

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

		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno(){

		try {

			Map<String, Object> parametros = new HashMap<>();

			parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaAno = RegistroConverter.toListRegistro(requestManager.
					getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
							MovimientoPptalesAbiertosControladorUrlEnum.URL000.getValue()).getUrl(), 
							parametros));

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaComprobanteInicial
	 *
	 */
	public void cargarListaComprobanteInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL001.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaComprobanteInicial =  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaComprobanteFinal
	 *
	 */
	public void cargarListaComprobanteFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL002.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CODIGOINICIAL", comprobanteInicial);

		listaComprobanteFinal =  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaCentroInicial
	 *
	 */
	public void cargarListaCentroInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL005.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANO", ano);

		listaCentroInicial =  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaCentroFinal
	 */
	public void cargarListaCentroFinal(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL006.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANO", ano);
		param.put("CENTRO_COSTO", centroInicial);
		listaCentroFinal =  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaTerceroInicial
	 *
	 */
	public void cargarListaTerceroInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL007.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTerceroInicial =  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"NIT");
	}
	/**
	 * 
	 * Carga la lista listaTerceroFinal
	 *
	 */
	public void cargarListaTerceroFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL008.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TERCEROINI", terceroInicial);

		listaTerceroFinal=  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"NIT");
	}
	/**
	 * 
	 * Carga la lista listaAuxiliarInicial
	 *
	 */
	public void cargarListaAuxiliarInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL009.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANIO", ano);

		listaAuxiliarInicial=  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaAuxiliarFinal
	 *
	 */
	public void cargarListaAuxiliarFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL010.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANIO", ano);
		param.put("CODIGOFINAL", auxiliarInicial);

		listaAuxiliarFinal=  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaReferenciaInicial
	 *
	 */
	public void cargarListaReferenciaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL003.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANO", ano);

		listaReferenciaInicial =  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaReferenciaFinal
	 *
	 */
	public void cargarListaReferenciaFinal(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL004.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANO", ano);
		param.put("REFERENCIAINICIAL", referenciaInicial);

		listaReferenciaFinal =  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaFuenteInicial
	 *
	 */
	public void cargarListaFuenteInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL011.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANO", ano);

		listaFuenteInicial=  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaFuenteFinal
	 *
	 */
	public void cargarListaFuenteFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MovimientoPptalesAbiertosControladorUrlEnum.URL012.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANO", ano);
		param.put("FUENTEINICIAL", fuenteInicial);

		listaFuenteFinal=  new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		generarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}

	public void generarInforme(FORMATOS formato) {

		try 
		{

			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();

			reemplazar.put("compania",compania);
			reemplazar.put("tipoCuenta", tipoCuenta);
			reemplazar.put("fechaInicial",SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazar.put("fechaFinal",SysmanFunciones.convertirAFechaCadena(fechaFinal));
			reemplazar.put("comprobanteInicial",comprobanteInicial);
			reemplazar.put("comprobanteFinal",comprobanteFinal);
			reemplazar.put("auxiliarInicial",auxiliarInicial);
			reemplazar.put("auxiliarFinal",auxiliarFinal);
			reemplazar.put("centroInicial",centroInicial);
			reemplazar.put("centroFinal",centroFinal);
			reemplazar.put("fuenteInicial",fuenteInicial);
			reemplazar.put("fuenteFinal",fuenteFinal);
			reemplazar.put("referenciaInicial",referenciaInicial);
			reemplazar.put("referenciaFinal",referenciaFinal);
			reemplazar.put("terceroInicial",terceroInicial);
			reemplazar.put("terceroFinal",terceroFinal);

			reemplazar.put("manAux", ckAuxiliar ? "1" : "0");
			reemplazar.put("manCen", ckCentroCosto ? "1" : "0");
			reemplazar.put("manFue", ckFuente ? "1" : "0");
			reemplazar.put("manRef", ckReferencia ? "1" : "0");
			reemplazar.put("manTer", ckTercero ? "1" : "0");
			reemplazar.put("manCuen", "0");

			Reporteador.resuelveConsulta("001955MovPptalAbierto", Integer.parseInt(modulo),reemplazar, parametros); 

			// Firmas
			parametros.put("PR_NOMBRE_GERENTE", 
					ejbSysmanUtil.consultarParametro(compania,"NOMBRE GERENTE", modulo ,new Date(), true));
			parametros.put("PR_CARGO_GERENTE", 
					ejbSysmanUtil.consultarParametro(compania,"CARGO GERENTE", modulo ,new Date(), true));
			parametros.put("PR_NOMBRE_ENCARGADO_DE_TESORERIA", 
					ejbSysmanUtil.consultarParametro(compania,"NOMBRE ENCARGADO DE TESORERIA",modulo ,new Date(), true));
			parametros.put("PR_CARGO_ENCARGADO_DE_TESORERIA", 
					ejbSysmanUtil.consultarParametro(compania,"CARGO ENCARGADO DE TESORERIA", modulo ,new Date(), true));
			// Firmas

			//Parametros diseño reporte
			parametros.put("PR_FECHAINICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			parametros.put("PR_FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));

			parametros.put("PR_COMPANIA", compania);
			parametros.put("PR_FUENTE_VISIBLE", ckFuente ? true : false);
			parametros.put("PR_TERCERO_VISIBLE", ckTercero ? true : false);
			parametros.put("PR_AUXILIAR_VISIBLE", ckAuxiliar ? true : false);
			parametros.put("PR_CENTRO_VISIBLE", ckCentroCosto ? true : false);
			parametros.put("PR_REFERENCIA_VISIBLE", ckReferencia ? true : false);
			parametros.put("PR_SUCURSAL_VISIBLE", false);
			parametros.put("PR_NRO_VISIBLE",false);
			parametros.put("PR_CONTRATO_VISIBLE", false);
			parametros.put("PR_CMPT_VISIBLE", false);
			parametros.put("PR_FECHA_VISIBLE",false);
			parametros.put("PR_CUENTA_VISIBLE", true);
			parametros.put("PR_NOMBRUBRO_VISIBLE", true);
			 //titulos
			parametros.put("PR_TIPO", true);
			parametros.put("PR_DEPENDENCIA", true);
			parametros.put("PR_COMPROBANTE", true);
			parametros.put("PR_RUBRO", false);
			//Parametros diseño reporte

			archivoDescarga = JsfUtil.exportarStreamed("001955MovPptalAbierto",parametros,ConectorPool.ESQUEMA_SYSMAN,formato);
		}
		catch (JRException | IOException | SysmanException | ParseException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} 
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	public void cambiarAno() {

		cargarListaComprobanteInicial();
		cargarListaComprobanteFinal(); 
		cargarListaCentroInicial();
		cargarListaCentroFinal(); 
		cargarListaTerceroInicial();
		cargarListaTerceroFinal(); 
		cargarListaAuxiliarInicial();
		cargarListaAuxiliarFinal(); 
		cargarListaReferenciaInicial();
		cargarListaReferenciaFinal(); 
		cargarListaFuenteInicial(); 
		cargarListaFuenteFinal();
	}
	/*
	 * Metodo ejecutado al cambiar el control CkCentroCosto
	 * 
	 * 
	 */
	public void cambiarCkCentroCosto() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control CkTercero
	 * 
	 * 
	 */
	public void cambiarCkTercero() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control CkAuxiliar
	 * 
	 * 
	 */
	public void cambiarCkAuxiliar() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control CkReferencia
	 * 
	 */
	public void cambiarCkReferencia() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control CkFuente
	 * 
	 * 
	 */
	public void cambiarCkFuente() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaComprobanteInicial
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaComprobanteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		comprobanteInicial= registroAux.getCampos().get("CODIGO").toString();
		comprobanteFinal = null ;
		cargarListaComprobanteFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaComprobanteFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaComprobanteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		comprobanteFinal= registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCentroInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroInicial= registroAux.getCampos().get("CODIGO").toString();
		centroFinal = null;
		cargarListaCentroFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCentroFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroFinal= registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTerceroInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroInicial= registroAux.getCampos().get("NIT").toString();
		terceroFinal = null ;
		cargarListaTerceroFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTerceroFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroFinal= registroAux.getCampos().get("NIT").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaAuxiliarInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarInicial= registroAux.getCampos().get("CODIGO").toString();
		auxiliarFinal = null;
		cargarListaAuxiliarFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaAuxiliarFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarFinal= registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReferenciaInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		referenciaInicial= registroAux.getCampos().get("CODIGO").toString();
		referenciaFinal = null;
		cargarListaReferenciaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReferenciaFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		referenciaFinal= registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteInicial= registroAux.getCampos().get("CODIGO").toString();
		fuenteFinal = null;
		cargarListaFuenteFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFinal= registroAux.getCampos().get("CODIGO").toString();
	}

	//<SET_GET_ATRIBUTOS>

	public int getTipoCuenta() {
		return tipoCuenta;
	}
	public void setTipoCuenta(int tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}
	public boolean isCkCentroCosto() {
		return ckCentroCosto;
	}
	public void setCkCentroCosto(boolean ckCentroCosto) {
		this.ckCentroCosto = ckCentroCosto;
	}
	public boolean isCkTercero() {
		return ckTercero;
	}
	public void setCkTercero(boolean ckTercero) {
		this.ckTercero = ckTercero;
	}
	public boolean isCkAuxiliar() {
		return ckAuxiliar;
	}
	public void setCkAuxiliar(boolean ckAuxiliar) {
		this.ckAuxiliar = ckAuxiliar;
	}
	public boolean isCkReferencia() {
		return ckReferencia;
	}
	public void setCkReferencia(boolean ckReferencia) {
		this.ckReferencia = ckReferencia;
	}
	public boolean isCkFuente() {
		return ckFuente;
	}
	public void setCkFuente(boolean ckFuente) {
		this.ckFuente = ckFuente;
	}
	public String getAno() {
		return ano;
	}
	public void setAno(String ano) {
		this.ano = ano;
	}
	public String getComprobanteInicial() {
		return comprobanteInicial;
	}
	public void setComprobanteInicial(String comprobanteInicial) {
		this.comprobanteInicial = comprobanteInicial;
	}
	public String getComprobanteFinal() {
		return comprobanteFinal;
	}
	public void setComprobanteFinal(String comprobanteFinal) {
		this.comprobanteFinal = comprobanteFinal;
	}
	public String getCentroInicial() {
		return centroInicial;
	}
	public void setCentroInicial(String centroInicial) {
		this.centroInicial = centroInicial;
	}
	public String getCentroFinal() {
		return centroFinal;
	}
	public void setCentroFinal(String centroFinal) {
		this.centroFinal = centroFinal;
	}
	public String getTerceroInicial() {
		return terceroInicial;
	}
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}
	public String getTerceroFinal() {
		return terceroFinal;
	}
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}
	public String getAuxiliarInicial() {
		return auxiliarInicial;
	}
	public void setAuxiliarInicial(String auxiliarInicial) {
		this.auxiliarInicial = auxiliarInicial;
	}
	public String getAuxiliarFinal() {
		return auxiliarFinal;
	}
	public void setAuxiliarFinal(String auxiliarFinal) {
		this.auxiliarFinal = auxiliarFinal;
	}

	public String getReferenciaInicial() {
		return referenciaInicial;
	}
	public void setReferenciaInicial(String referenciaInicial) {
		this.referenciaInicial = referenciaInicial;
	}
	public String getReferenciaFinal() {
		return referenciaFinal;
	}
	public void setReferenciaFinal(String referenciaFinal) {
		this.referenciaFinal = referenciaFinal;
	}
	public String getFuenteInicial() {
		return fuenteInicial;
	}
	public void setFuenteInicial(String fuenteInicial) {
		this.fuenteInicial = fuenteInicial;
	}
	public String getFuenteFinal() {
		return fuenteFinal;
	}
	public void setFuenteFinal(String fuenteFinal) {
		this.fuenteFinal = fuenteFinal;
	}
	public Date getFechaInicial() {
		return fechaInicial;
	}
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	public Date getFechaFinal() {
		return fechaFinal;
	}
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public List<Registro> getListaAno() {
		return listaAno;
	}
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	public RegistroDataModelImpl getListaComprobanteInicial() {
		return listaComprobanteInicial;
	}
	public void setListaComprobanteInicial(RegistroDataModelImpl listaComprobanteInicial) {
		this.listaComprobanteInicial = listaComprobanteInicial;
	}
	public RegistroDataModelImpl getListaComprobanteFinal() {
		return listaComprobanteFinal;
	}
	public void setListaComprobanteFinal(RegistroDataModelImpl listaComprobanteFinal) {
		this.listaComprobanteFinal = listaComprobanteFinal;
	}
	public RegistroDataModelImpl getListaCentroInicial() {
		return listaCentroInicial;
	}
	public void setListaCentroInicial(RegistroDataModelImpl listaCentroInicial) {
		this.listaCentroInicial = listaCentroInicial;
	}
	public RegistroDataModelImpl getListaCentroFinal() {
		return listaCentroFinal;
	}
	public void setListaCentroFinal(RegistroDataModelImpl listaCentroFinal) {
		this.listaCentroFinal = listaCentroFinal;
	}
	public RegistroDataModelImpl getListaTerceroInicial() {
		return listaTerceroInicial;
	}
	public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
		this.listaTerceroInicial = listaTerceroInicial;
	}
	public RegistroDataModelImpl getListaTerceroFinal() {
		return listaTerceroFinal;
	}
	public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
		this.listaTerceroFinal = listaTerceroFinal;
	}
	public RegistroDataModelImpl getListaAuxiliarInicial() {
		return listaAuxiliarInicial;
	}
	public void setListaAuxiliarInicial(RegistroDataModelImpl listaAuxiliarInicial) {
		this.listaAuxiliarInicial = listaAuxiliarInicial;
	}
	public RegistroDataModelImpl getListaAuxiliarFinal() {
		return listaAuxiliarFinal;
	}
	public void setListaAuxiliarFinal(RegistroDataModelImpl listaAuxiliarFinal) {
		this.listaAuxiliarFinal = listaAuxiliarFinal;
	}
	public RegistroDataModelImpl getListaReferenciaInicial() {
		return listaReferenciaInicial;
	}
	public void setListaReferenciaInicial(RegistroDataModelImpl listaReferenciaInicial) {
		this.listaReferenciaInicial = listaReferenciaInicial;
	}
	public RegistroDataModelImpl getListaReferenciaFinal() {
		return listaReferenciaFinal;
	}
	public void setListaReferenciaFinal(RegistroDataModelImpl listaReferenciaFinal) {
		this.listaReferenciaFinal = listaReferenciaFinal;
	}
	public RegistroDataModelImpl getListaFuenteInicial() {
		return listaFuenteInicial;
	}
	public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
		this.listaFuenteInicial = listaFuenteInicial;
	}
	public RegistroDataModelImpl getListaFuenteFinal() {
		return listaFuenteFinal;
	}
	public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
		this.listaFuenteFinal = listaFuenteFinal;
	}
	public String getCompania() {
		return compania;
	}
	public String getModulo() {
		return modulo;
	}

	//<SET_GET_ATRIBUTOS>
}
