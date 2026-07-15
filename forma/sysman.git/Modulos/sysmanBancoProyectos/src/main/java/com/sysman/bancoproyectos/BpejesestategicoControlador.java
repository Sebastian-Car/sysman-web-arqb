/*-
 * BpejesestategicoControlador.java
 *
 * 1.0
 * 
 * 27/10/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.bancoproyectos.enums.BpejesestategicoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 27/10/2025
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  BpejesestategicoControlador  extends BeanBaseContinuoAcmeImpl{
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
	private String politicaPublica;
	private String cmbPoliticaPublica;
	private RegistroDataModelImpl listaCmbPoliticaPublica;
	private RegistroDataModelImpl listaCmbPoliticaPublicaE;
	private String auxiliar;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	
	/**
	 * Crea una nueva instancia de BpejesestategicoControlador
	 */
	public BpejesestategicoControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=GeneralCodigoFormaEnum.BP_EJES_ESTRATEGICOS
                    .getCodigo();
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

		enumBase = GenericUrlEnum.BP_EJES_ESTRATEGICOS;
		registro = new Registro(new HashMap<String, Object>());

		buscarLlave();
		reasignarOrigen();
		cargarListaCmbPoliticaPublica(); 
		cargarListaCmbPoliticaPublicaE();
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
        parametrosListado.put("POLITICA", politicaPublica);
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCmbPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCmbPoliticaPublica(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						BpejesestategicoControladorUrlEnum.URL1986001
						.getValue());
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaCmbPoliticaPublica = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
	}
	/**
	 * 
	 * Carga la lista listaCmbPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void  cargarListaCmbPoliticaPublicaE(){
		listaCmbPoliticaPublicaE = listaCmbPoliticaPublica;
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
	 * listaCmbPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCmbPoliticaPublica(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		politicaPublica= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		cmbPoliticaPublica = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
		reasignarOrigen();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCmbPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCmbPoliticaPublicaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get(GeneralParameterEnum.COMPANIA.getName());
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
		
        
        try {
        	registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registro.getCampos().put("CODIGO_POLITICA", politicaPublica);
    		long codigo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                    "BP_POLITICA_EJE_ESTRATEGICO", "COMPANIA =" + compania + 
                    "AND CODIGO_POLITICA =" + politicaPublica, "CODIGO_EJE", "1");
    		registro.getCampos().put("CODIGO_EJE",codigo);
    		}
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        
        
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
	 * Retorna la variable politicaPublica
	 * 
	 * @return  politicaPublica
	 */
	public String getPoliticaPublica() {
		return politicaPublica;
	}
	/**
	 * Asigna la variable  politicaPublica
	 * 
	 * @param  politicaPublica
	 * Variable a asignar en  politicaPublica
	 */
	public void setPoliticaPublica(String politicaPublica) {
		this.politicaPublica = politicaPublica;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCmbPoliticaPublica
	 * 
	 * @return listaCmbPoliticaPublica
	 */
	public RegistroDataModelImpl getListaCmbPoliticaPublica() {
		return listaCmbPoliticaPublica;
	}
	/**
	 * Asigna la lista listaCmbPoliticaPublica
	 * 
	 * @param listaCmbPoliticaPublica
	 * Variable a asignar en  listaCmbPoliticaPublica
	 */
	public void setListaCmbPoliticaPublica(RegistroDataModelImpl listaCmbPoliticaPublica) {
		this.listaCmbPoliticaPublica = listaCmbPoliticaPublica;
	}
	/**
	 * Retorna la lista listaCmbPoliticaPublica
	 * 
	 * @return listaCmbPoliticaPublica
	 */
	public RegistroDataModelImpl getListaCmbPoliticaPublicaE() {
		return listaCmbPoliticaPublicaE;
	}
	/**
	 * Asigna la lista listaCmbPoliticaPublica
	 * 
	 * @param listaCmbPoliticaPublica
	 * Variable a asignar en  listaCmbPoliticaPublica
	 */
	public void setListaCmbPoliticaPublicaE(RegistroDataModelImpl listaCmbPoliticaPublicaE) {
		this.listaCmbPoliticaPublicaE = listaCmbPoliticaPublicaE;
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
	
	public String getCmbPoliticaPublica() {
        return cmbPoliticaPublica;
    }

    public void setCmbPoliticaPublica(String cmbPoliticaPublica) {
        this.cmbPoliticaPublica = cmbPoliticaPublica;
    }
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
