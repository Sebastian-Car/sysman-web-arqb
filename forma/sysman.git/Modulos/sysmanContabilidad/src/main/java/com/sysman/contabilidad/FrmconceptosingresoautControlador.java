/*-
 * FrmconceptosingresoautControlador.java
 *
 * 1.0
 * 
 * 07/04/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.FrmconceptosingresoautControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 07/04/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  FrmconceptosingresoautControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anioBase;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anioDestino;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean anioBaseVisible;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private int ano;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaano;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaanioBase;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaanioDestino;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacuentaDebito;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacuentaDebitoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacuentaCredito;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacuentaCreditoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listarecaudarEn;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listarecaudarEnE;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmconceptosingresoautControlador
	 */
	public FrmconceptosingresoautControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.CA_CONCEPTOS_INGRESOS
					.getCodigo();
			ano = SysmanFunciones.ano(new Date());
			anioBase = String.valueOf(SysmanFunciones
					.ano(new Date()));
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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

		enumBase = GenericUrlEnum.CA_CONCEPTOS_INGRESOS;
		buscarLlave();
		reasignarOrigen();
		registro= new Registro();
		//<CARGAR_LISTA>
		cargarListaano();
		cargarListaanioBase();
		cargarListaanioDestino();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListacuentaDebito(); 
		cargarListacuentaDebitoE();
		cargarListacuentaCredito(); 
		cargarListacuentaCreditoE();
		cargarListacuentaRecaudarEn();
		cargarListacuentaRecaudarEnE();
		//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();

	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaano
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaano(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaano = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmconceptosingresoautControladorUrlEnum.URL4001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
	}
	/**
	 * 
	 * Carga la lista listaanioBase
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaanioBase(){
		listaanioBase = listaano;
	}
	/**
	 * 
	 * Carga la lista listaanioDestino
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaanioDestino(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioBase);

		try {
			listaanioDestino = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmconceptosingresoautControladorUrlEnum.URL4016
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacuentaDebito
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacuentaDebito(){
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmconceptosingresoautControladorUrlEnum.URL16221.getValue());
		listacuentaDebito = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
		
	}
	/**
	 * 
	 * Carga la lista listacuentaDebito
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListacuentaDebitoE(){
		listacuentaDebitoE = listacuentaDebito;
	}
	/**
	 * 
	 * Carga la lista listacuentaCredito
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacuentaCredito(){
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmconceptosingresoautControladorUrlEnum.URL16221.getValue());
		listacuentaCredito = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
		
	}
	/**
	 * 
	 * Carga la lista listacuentaCredito
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListacuentaCreditoE(){
		listacuentaCreditoE = listacuentaCredito;
	}
	/**
	 * 
	 * Carga la lista listarecaudarEn
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacuentaRecaudarEn(){
		Map<String, Object> param = new HashMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmconceptosingresoautControladorUrlEnum.URL16221.getValue());
		listarecaudarEn = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
		
	}
	/**
	 * 
	 * Carga la lista listarecaudarEnE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListacuentaRecaudarEnE(){
		listarecaudarEnE = listarecaudarEn;
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Iniciar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirIniciar() {
		//<CODIGO_DESARROLLADO>
		anioBaseVisible = true;
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control anioBase
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiaranioBase() {
		//<CODIGO_DESARROLLADO>
		cargarListaanioDestino();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarano() {
		//<CODIGO_DESARROLLADO>
		if (ano == 0) {
			JsfUtil.agregarMensajeAlerta(
					idioma.getString("TB_TB2680").replace("#ANIO#",
							Integer.toString(ano)));
		}
		reasignarOrigen();
		cargarListacuentaDebito(); 
		cargarListacuentaDebitoE();
		cargarListacuentaCredito(); 
		cargarListacuentaCreditoE();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * del dialogo anioBase en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void aceptaranioBase() {
		//<CODIGO_DESARROLLADO>
		if ("".equals(anioBase) || (anioBase == null)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2606"));
			return;
		}
		if ("".equals(anioDestino) || (anioDestino == null)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2607"));
			return;
		}

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioDestino);

		try {
			Registro reg = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmconceptosingresoautControladorUrlEnum.URL1997001
									.getValue())
							.getUrl(), param));

			if (Integer.parseInt(
					reg.getCampos().get("VALIDACION").toString()) > 0) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4095"));
				return;
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

		prepararAnoSiguiente();
		//</CODIGO_DESARROLLADO>
	}
	
	private void prepararAnoSiguiente() {
		
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put(GeneralParameterEnum.COMPANIA.name(), compania);
			parametros.put(GeneralParameterEnum.ANO.name(), anioBase);
			parametros.put("ANODESTINO", anioDestino);
			Parameter parameter = new Parameter();

			parameter.setFields(parametros);
			UrlBean urlInsertSelect = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmconceptosingresoautControladorUrlEnum.URL1997002
							.getValue());
			requestManager.save(urlInsertSelect.getUrl(),
					urlInsertSelect.getMetodo(), parameter);
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB920"));
			anioBaseVisible = false;

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cancelar
	 * del dialogo anioBase en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void cancelaranioBase() {
		//<CODIGO_DESARROLLADO>
		anioBaseVisible = false;
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ano en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiaranoC(int rowNum) {
		// Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola "); 
		// Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
		// listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacuentaDebito
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaDebito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTA_DEBITO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacuentaDebito
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaDebitoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacuentaCredito
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaCredito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTA_CREDITO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacuentaCredito
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaCreditoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get("CODIGO").toString();
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listarecaudarEn
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilarecaudarEn(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTA_RECAUDO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listarecaudarEnE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilarecaudarEnE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get("CODIGO").toString();
	}
	//</METODOS_COMBOS_GRANDES>
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
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
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
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);
		registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		anioBaseVisible = false;
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {
	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		// TODO Auto-generated method stub
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anioBase
	 * 
	 * @return  anioBase
	 */
	public String getAnioBase() {
		return anioBase;
	}
	/**
	 * Asigna la variable  anioBase
	 * 
	 * @param  anioBase
	 * Variable a asignar en  anioBase
	 */
	public void setAnioBase(String anioBase) {
		this.anioBase = anioBase;
	}
	/**
	 * Retorna la variable anioDestino
	 * 
	 * @return  anioDestino
	 */
	public String getAnioDestino() {
		return anioDestino;
	}
	/**
	 * Asigna la variable  anioDestino
	 * 
	 * @param  anioDestino
	 * Variable a asignar en  anioDestino
	 */
	public void setAnioDestino(String anioDestino) {
		this.anioDestino = anioDestino;
	}
	/**
	 * Retorna la variable ano
	 * 
	 * @return  ano
	 */
	public int getAno() {
		return ano;
	}
	/**
	 * Asigna la variable  ano
	 * 
	 * @param  ano
	 * Variable a asignar en  ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaano
	 * 
	 * @return listaano
	 */
	public List<Registro> getListaano() {
		return listaano;
	}
	/**
	 * Asigna la lista listaano
	 * 
	 * @param listaano
	 * Variable a asignar en  listaano
	 */
	public void setListaano(List<Registro> listaano) {
		this.listaano = listaano;
	}
	/**
	 * Retorna la lista listaanioBase
	 * 
	 * @return listaanioBase
	 */
	public List<Registro> getListaanioBase() {
		return listaanioBase;
	}
	/**
	 * Asigna la lista listaanioBase
	 * 
	 * @param listaanioBase
	 * Variable a asignar en  listaanioBase
	 */
	public void setListaanioBase(List<Registro> listaanioBase) {
		this.listaanioBase = listaanioBase;
	}
	/**
	 * Retorna la lista listaanioDestino
	 * 
	 * @return listaanioDestino
	 */
	public List<Registro> getListaanioDestino() {
		return listaanioDestino;
	}
	/**
	 * Asigna la lista listaanioDestino
	 * 
	 * @param listaanioDestino
	 * Variable a asignar en  listaanioDestino
	 */
	public void setListaanioDestino(List<Registro> listaanioDestino) {
		this.listaanioDestino = listaanioDestino;
	}
	
	public boolean isAnioBaseVisible() {
		return anioBaseVisible;
	}

	public void setAnioBaseVisible(boolean anioBaseVisible) {
		this.anioBaseVisible = anioBaseVisible;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacuentaDebito
	 * 
	 * @return listacuentaDebito
	 */
	public RegistroDataModelImpl getListacuentaDebito() {
		return listacuentaDebito;
	}
	/**
	 * Asigna la lista listacuentaDebito
	 * 
	 * @param listacuentaDebito
	 * Variable a asignar en  listacuentaDebito
	 */
	public void setListacuentaDebito(RegistroDataModelImpl listacuentaDebito) {
		this.listacuentaDebito = listacuentaDebito;
	}
	/**
	 * Retorna la lista listacuentaDebito
	 * 
	 * @return listacuentaDebito
	 */
	public RegistroDataModelImpl getListacuentaDebitoE() {
		return listacuentaDebitoE;
	}
	/**
	 * Asigna la lista listacuentaDebito
	 * 
	 * @param listacuentaDebito
	 * Variable a asignar en  listacuentaDebito
	 */
	public void setListacuentaDebitoE(RegistroDataModelImpl listacuentaDebitoE) {
		this.listacuentaDebitoE = listacuentaDebitoE;
	}
	/**
	 * Retorna la lista listacuentaCredito
	 * 
	 * @return listacuentaCredito
	 */
	public RegistroDataModelImpl getListacuentaCredito() {
		return listacuentaCredito;
	}
	/**
	 * Asigna la lista listacuentaCredito
	 * 
	 * @param listacuentaCredito
	 * Variable a asignar en  listacuentaCredito
	 */
	public void setListacuentaCredito(RegistroDataModelImpl listacuentaCredito) {
		this.listacuentaCredito = listacuentaCredito;
	}
	/**
	 * Retorna la lista listacuentaCredito
	 * 
	 * @return listacuentaCredito
	 */
	public RegistroDataModelImpl getListacuentaCreditoE() {
		return listacuentaCreditoE;
	}
	/**
	 * Asigna la lista listacuentaCredito
	 * 
	 * @param listacuentaCredito
	 * Variable a asignar en  listacuentaCredito
	 */
	public void setListacuentaCreditoE(RegistroDataModelImpl listacuentaCreditoE) {
		this.listacuentaCreditoE = listacuentaCreditoE;
	}
	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}
	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar
	 * Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar= auxiliar;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	public RegistroDataModelImpl getListarecaudarEn() {
		return listarecaudarEn;
	}
	public void setListarecaudarEn(RegistroDataModelImpl listarecaudarEn) {
		this.listarecaudarEn = listarecaudarEn;
	}
	public RegistroDataModelImpl getListarecaudarEnE() {
		return listarecaudarEnE;
	}
	public void setListarecaudarEnE(RegistroDataModelImpl listarecaudarEnE) {
		this.listarecaudarEnE = listarecaudarEnE;
	}
}
