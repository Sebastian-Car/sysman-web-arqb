/*-
 * FrmConfPlanCntFlujoCgnControlador.java
 *
 * 1.0
 * 
 * 30/04/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.FrmConfPlanCntFlujoCgnControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmConfPlanCntFlujoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;

/**
 *
 * @version 1.0, 30/04/2020
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmConfPlanCntFlujoCgnControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private RegistroDataModelImpl listaConceptoFlujo;
	private RegistroDataModelImpl listaConceptoFlujoE;
	private String auxiliar;
	
	public FrmConfPlanCntFlujoCgnControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2171
			numFormulario= GeneralCodigoFormaEnum.FRM_CONFPLANCNT_FLUJOCGN_CONTROLADOR.getCodigo();
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
		enumBase = GenericUrlEnum.PLANCONTABLE;
		reasignarOrigen();		    
		buscarLlave();
		registro= new Registro();
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//	cargarListaConceptoFlujo(); 
		cargarListaConceptoFlujoE();
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

		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmConfPlanCntFlujoCgnControladorUrlEnum.URL0002
						.getValue());

		urlActualizacion = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmConfPlanCntFlujoCgnControladorUrlEnum.URL0003
						.getValue());
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaConceptoFlujo
	 *
	 */
	public void cargarListaConceptoFlujo(){

	}
	/**
	 * 
	 * Carga la lista listaConceptoFlujo
	 *
	 */
	public void  cargarListaConceptoFlujoE(){
		HashMap<String, Object> parametros = new HashMap<>();
		
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmConfPlanCntFlujoCgnControladorUrlEnum.URL0001
						.getValue());
		listaConceptoFlujoE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), parametros, true,
				GeneralParameterEnum.CODIGO.getName());

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
	 * listaConceptoFlujo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoFlujo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CONCEPTO_FLUJO_CGN",(Object) registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConceptoFlujo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConceptoFlujoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
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
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
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
	 * 
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
	 * 
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
	 * 
	 * 
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
	 * 
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
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
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
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	
	 private String extraerString(Object object) {
	        return object != null ? object.toString() : null;
	    }
	
	/**
	 * Retorna la lista listaConceptoFlujo
	 * 
	 * @return listaConceptoFlujo
	 */
	public RegistroDataModelImpl getListaConceptoFlujo() {
		return listaConceptoFlujo;
	}
	/**
	 * Asigna la lista listaConceptoFlujo
	 * 
	 * @param listaConceptoFlujo
	 * Variable a asignar en  listaConceptoFlujo
	 */
	public void setListaConceptoFlujo(RegistroDataModelImpl listaConceptoFlujo) {
		this.listaConceptoFlujo = listaConceptoFlujo;
	}
	/**
	 * Retorna la lista listaConceptoFlujo
	 * 
	 * @return listaConceptoFlujo
	 */
	public RegistroDataModelImpl getListaConceptoFlujoE() {
		return listaConceptoFlujoE;
	}
	/**
	 * Asigna la lista listaConceptoFlujo
	 * 
	 * @param listaConceptoFlujo
	 * Variable a asignar en  listaConceptoFlujo
	 */
	public void setListaConceptoFlujoE(RegistroDataModelImpl listaConceptoFlujoE) {
		this.listaConceptoFlujoE = listaConceptoFlujoE;
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
