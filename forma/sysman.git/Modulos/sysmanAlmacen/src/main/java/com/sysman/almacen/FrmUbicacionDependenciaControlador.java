/*-
 * FrmUbicacionDependenciaControlador.java
 *
 * 1.0
 * 
 * 21/12/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import javax.faces.bean.ManagedProperty;
import com.sysman.services.FormContinuoService;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;

import com.sysman.almacen.enums.FrmUbicacionDependenciaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;
/**
 * @version 1.0, 21/12/2023
 * @author Rent_16
 */
@ManagedBean
@ViewScoped
public class  FrmUbicacionDependenciaControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	/**
	 */
	private String dependencia;
	/**
	 */
	private String nombreDepen;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 */
	private RegistroDataModelImpl listacodigoDepen;
	/**
	 */
	private RegistroDataModelImpl listacodigoDepenE;
	/**
	 */
	private RegistroDataModelImpl listaUbicacion;
	/**
	 */
	private RegistroDataModelImpl listaUbicacionE;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmUbicacionDependenciaControlador
	 */
	public FrmUbicacionDependenciaControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2443;
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
		enumBase = GenericUrlEnum.DEPENDENCIA_UBICACION;
		reasignarOrigen();		    
		buscarLlave();
		registro= new Registro();
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListacodigoDepen(); cargarListacodigoDepenE();
		cargarListaUbicacion(); cargarListaUbicacionE();
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
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		parametrosListado.put(GeneralParameterEnum.DEPENDENCIA.getName(),
				dependencia);
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacodigoDepen
	 */
	public void cargarListacodigoDepen(){
		//listacodigoDepen =
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmUbicacionDependenciaControladorUrlEnum.URL6115
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listacodigoDepen = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	/**
	 * 
	 * Carga la lista listacodigoDepen
	 */
	public void  cargarListacodigoDepenE(){
		listacodigoDepenE = listacodigoDepen;
	}
	/**
	 * 
	 * Carga la lista listaUbicacion
	 */
	public void cargarListaUbicacion(){
	//	listaUbicacion = 
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmUbicacionDependenciaControladorUrlEnum.URL1922001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaUbicacion = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaUbicacion
	 */
	public void  cargarListaUbicacionE(){
		listaUbicacionE = listaUbicacion;
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacodigoDepen
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodigoDepen(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependencia = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		nombreDepen = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
		reasignarOrigen();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacodigoDepen
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodigoDepenE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
		
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaUbicacion
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaUbicacion(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("UBICACION", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("NOMBRE_UBICACION", registroAux.getCampos().get("NOMBRE"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaUbicacion
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaUbicacionE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
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
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);
		registro.getCampos().remove("NOMBRE_UBICACION");
		
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
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
	 * @return TODO VARIABLE
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
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
		registro.getCampos().remove("NOMBRE_UBICACION");
	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{

	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable dependencia
	 * 
	 * @return  dependencia
	 */
	public String getDependencia() {
		return dependencia;
	}
	/**
	 * Asigna la variable  dependencia
	 * 
	 * @param  dependencia
	 * Variable a asignar en  dependencia
	 */
	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}
	/**
	 * Retorna la variable nombreDepen
	 * 
	 * @return  nombreDepen
	 */
	public String getNombreDepen() {
		return nombreDepen;
	}
	/**
	 * Asigna la variable  nombreDepen
	 * 
	 * @param  nombreDepen
	 * Variable a asignar en  nombreDepen
	 */
	public void setNombreDepen(String nombreDepen) {
		this.nombreDepen = nombreDepen;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacodigoDepen
	 * 
	 * @return listacodigoDepen
	 */
	public RegistroDataModelImpl getListacodigoDepen() {
		return listacodigoDepen;
	}
	/**
	 * Asigna la lista listacodigoDepen
	 * 
	 * @param listacodigoDepen
	 * Variable a asignar en  listacodigoDepen
	 */
	public void setListacodigoDepen(RegistroDataModelImpl listacodigoDepen) {
		this.listacodigoDepen = listacodigoDepen;
	}
	/**
	 * Retorna la lista listacodigoDepen
	 * 
	 * @return listacodigoDepen
	 */
	public RegistroDataModelImpl getListacodigoDepenE() {
		return listacodigoDepenE;
	}
	/**
	 * Asigna la lista listacodigoDepen
	 * 
	 * @param listacodigoDepen
	 * Variable a asignar en  listacodigoDepen
	 */
	public void setListacodigoDepenE(RegistroDataModelImpl listacodigoDepenE) {
		this.listacodigoDepenE = listacodigoDepenE;
	}
	/**
	 * Retorna la lista listaUbicacion
	 * 
	 * @return listaUbicacion
	 */
	public RegistroDataModelImpl getListaUbicacion() {
		return listaUbicacion;
	}
	/**
	 * Asigna la lista listaUbicacion
	 * 
	 * @param listaUbicacion
	 * Variable a asignar en  listaUbicacion
	 */
	public void setListaUbicacion(RegistroDataModelImpl listaUbicacion) {
		this.listaUbicacion = listaUbicacion;
	}
	/**
	 * Retorna la lista listaUbicacion
	 * 
	 * @return listaUbicacion
	 */
	public RegistroDataModelImpl getListaUbicacionE() {
		return listaUbicacionE;
	}
	/**
	 * Asigna la lista listaUbicacion
	 * 
	 * @param listaUbicacion
	 * Variable a asignar en  listaUbicacion
	 */
	public void setListaUbicacionE(RegistroDataModelImpl listaUbicacionE) {
		this.listaUbicacionE = listaUbicacionE;
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
}
