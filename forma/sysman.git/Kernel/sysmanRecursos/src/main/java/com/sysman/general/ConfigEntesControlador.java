/*-
 * ConfigEntesControlador.java
 *
 * 1.0
 * 
 * 15/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ConfigEntesControladorEnum;
import com.sysman.general.enums.ConfigEntesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 15/06/2023
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  ConfigEntesControlador  extends BeanBaseContinuoAcmeImpl{

	private final String compania;

	private int indice;

	private String tipo;

	private String subTipo;
	
	private boolean visibleSubTipo;
	
	private boolean visibleSubTipo3;

	private List<Registro> listaSubTipo;

	private List<Registro> listaTipo;

	private RegistroDataModelImpl listaSubTipo2;

	private int departamento;

	private List<Registro> listaTipoE;

	private String auxiliar;

	private String subTipo2;

	private String subTipoaux;

	private List<Registro> listaSubTipo3;

	private String subTipo3;
	
	private Boolean visibleAppui;
	
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

	public ConfigEntesControlador() {
		super();
		compania = SessionUtil.getCompania();
		tipo="1";
		subTipo="0";
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_CONFIG_ENTES_CONTROLADOR
                    .getCodigo();
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
		enumBase = GenericUrlEnum.INFORMES_ENTES;
		buscarLlave();
		reasignarOrigen();	
		registro= new Registro();
		abrirFormulario();
		cargarListaTipo();

	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		parametrosListado.put(GeneralParameterEnum.TIPO.getName(),tipo);
		parametrosListado.put("SUBTIPO",subTipo);



	}
	/**
	 * Retorna la variable indice 
	 * @return indice
	 */
	public int getIndice() {
		return indice;
	}
	
	public void cargarListaTipo() {
		
		try {
			listaTipo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ConfigEntesControladorUrlEnum.URL1907001
									.getValue())
							.getUrl(), null));
			
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	public void cambiarSubTipo3() {
		subTipo = subTipo3;
		reasignarOrigen();
		cargarDatosTipo();
	}
	
	
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaSubTipo
	 *
	 */
	public void cargarListaSubTipo(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),String.valueOf(compania));
		param.put(GeneralParameterEnum.TIPO.getName(),tipo);

		try {
			listaSubTipo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ConfigEntesControladorUrlEnum.URL1750005
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}


	}
	
	public void cargarListaSubTipo3(){
		Map<String, Object> param = new TreeMap<>();
		 param.put(ConfigEntesControladorEnum.PARAM_CATEGORIA.getValue(), Integer.parseInt(ConfigEntesControladorEnum.CATEGORIA.getValue()));
	
    	try {
    		listaSubTipo3 = RegistroConverter.toListRegistro(
			        requestManager.getList(UrlServiceUtil.getInstance()
			                .getUrlServiceByUrlByEnumID(
			                		ConfigEntesControladorUrlEnum.URL1032010
			                                                .getValue())
			                .getUrl(), param));
		} catch (SystemException e) {
			 logger.error(e.getMessage(), e);
	         JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaSubTipo2(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ConfigEntesControladorUrlEnum.URL1750006
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),String.valueOf(compania));
		param.put(GeneralParameterEnum.TIPO.getName(),tipo);

		listaSubTipo2 = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "SUBTIPO");


	}

	public void cargarListaSubTipo2E(){		
	}



	public void cambiarTipo(){
	    
	        visibleSubTipo = false;
	        visibleSubTipo3 = false;
	    
		if(tipo.equals("1")||tipo.equals("3")||tipo.equals("7")||tipo.equals("9")||tipo.equals("10")){			
			subTipo="0";
			subTipoaux ="0";
			reasignarOrigen();

		}else if(tipo.equals("2")){
			cargarListaSubTipo2();
		}
		else if(tipo.equals("8")){
			visibleSubTipo3 = true;
			cargarListaSubTipo3();
		}else if(tipo.equals("4")||tipo.equals("5")){
			visibleSubTipo = true;
			cargarListaSubTipo();
		}
		cargarDatosTipo();
		cargarDatosCreacion();
	}


	public void cambiarSubTipo(){
		subTipoaux = subTipo;
		reasignarOrigen();
		cargarDatosTipo();
	}


	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */

	public void seleccionarFilaSubTipo2(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		subTipo2 = registroAux.getCampos().get("SUBTIPO").toString();
		subTipo = subTipo2;
		subTipoaux = subTipo2;
		reasignarOrigen();		
		cargarDatosTipo();
		cargarDatosCreacion();		

	}

	public void seleccionarFilaSubTipo2E(SelectEvent event) {
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoE(SelectEvent event) {
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
		cargarDatosTipo();
		cargarDatosCreacion();
		
		try {
			visibleAppui = (ejbSysmanUtil.consultarParametro(compania,
			        "MANEJA REPORTES APPUI", "-1",
			        new Date(), true)).equals("SI");
		} catch (SystemException e) {
			e.printStackTrace();
		}


		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
		cargarDatosTipo();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		// se valida y cuando la validación de la consulta llegue true se retorna true y cuando no exista se retorna false
		//boolean result;
		//String vigAppui = SysmanFunciones.toString(registro.getCampos().get("VIGENCIAS_APPUI")).toUpperCase();
		String vigAppui = SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("VIGENCIAS_APPUI"),"")).toUpperCase();
		registro.getCampos().put("VIGENCIAS_APPUI",SysmanFunciones.nvlStr(vigAppui,""));
		
		return validarConsulta(cargado);
		//return result;

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
		registro.getCampos().remove("DATE_CREATED");
		registro.getCampos().remove("CREATED_BY");

		//</CODIGO_DESARROLLADO>
		return validarConsulta(cargado);
	}

	public void cargarDatosTipo() {
		registro.getCampos().put("TIPO", tipo);
		if (subTipo==null){		
			registro.getCampos().put("SUBTIPO", subTipoaux);
		}
		else{
			registro.getCampos().put("SUBTIPO", subTipo);	
		}
	}

	public void cargarDatosCreacion() {
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);
		registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),SessionUtil.getUser().getCodigo());
		registro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),new Date());
	}

	public boolean validarConsulta(boolean resultado) {
		String reporte = registro.getCampos().get("CONSULTA").toString();
		try {
			String sql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()),null);

			resultado=(sql.isEmpty())?false: true;			

		} catch (NumberFormatException e) {
			JsfUtil.agregarMensajeError(
					idioma.getString("MSM_INFORME_VAR_NO_EXISTE").replace("s$reporte$s", reporte) + e.getMessage());
			logger.error(e.getMessage(), e);
		}return resultado;
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
		subTipoaux = registro.getCampos().get("SUBTIPO").toString();
		//</CODIGO_DESARROLLADO>
		return true;
	}	

	@Override
	public boolean eliminarAntes() {
		return false;
	}
	@Override
	public boolean eliminarDespues() {
		return false;
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
	 * Metodo ejecutado cuando se activa la edicion de un registro del
	 * formulario
	 * 
	 *
	 * @param registro
	 * registro del cual se activo la edicion
	 */
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		cargarDatosTipo();
		cargarDatosCreacion();
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipo
	 * 
	 * @return  tipo
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * Asigna la variable  tipo
	 * 
	 * @param  tipo
	 * Variable a asignar en  tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	/**
         * Retorna la variable tipo
         * 
         * @return  tipo
         */
        public boolean getvisibleSubTipo() {
                return visibleSubTipo;
        }
        /**
         * Asigna la variable  tipo
         * 
         * @param  tipo
         * Variable a asignar en  tipo
         */
        public void setvisibleSubTipo(boolean visibleSubTipo) {
                this.visibleSubTipo = visibleSubTipo;
        }
	/**
	 * Retorna la variable subTipoFiltro
	 * 
	 * @return  subTipoFiltro
	 */
	public String getSubTipo() {
		return subTipo;
	}
	/**
	 * Asigna la variable  subTipoFiltro
	 * 
	 * @param  subTipoFiltro
	 * Variable a asignar en  subTipoFiltro
	 */
	public void setSubTipo(String subTipo) {
		this.subTipo = subTipo;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaSubTipo
	 * 
	 * @return listaSubTipo
	 */
	public List<Registro> getListaSubTipo() {
		return listaSubTipo;
	}
	/**
	 * Asigna la lista listaSubTipo
	 * 
	 * @param listaSubTipo
	 * Variable a asignar en  listaSubTipo
	 */
	public void setListaSubTipo(List<Registro> listaSubTipo) {
		this.listaSubTipo = listaSubTipo;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public List<Registro> getListaTipo() {
		return listaTipo;
	}
	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo
	 * Variable a asignar en  listaTipo
	 */
	public void setListaTipo(List<Registro> listaTipo) {
		this.listaTipo = listaTipo;
	}
	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public List<Registro> getListaTipoE() {
		return listaTipoE;
	}
	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo
	 * Variable a asignar en  listaTipo
	 */
	public void setListaTipoE(List<Registro> listaTipoE) {
		this.listaTipoE = listaTipoE;
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
	/**
	 * @return the departamento
	 */
	public int getDepartamento() {
		return departamento;
	}
	/**
	 * @param departamento the departamento to set
	 */
	public void setDepartamento(int departamento) {
		this.departamento = departamento;
	}
	/**
	 * @return the listaSubTipo2
	 */
	public RegistroDataModelImpl getListaSubTipo2() {
		return listaSubTipo2;
	}
	/**
	 * @param listaSubTipo2 the listaSubTipo2 to set
	 */
	public void setListaSubTipo2(RegistroDataModelImpl listaSubTipo2) {
		this.listaSubTipo2 = listaSubTipo2;
	}
	/**
	 * @return the subTipo2
	 */
	public String getSubTipo2() {
		return subTipo2;
	}
	/**
	 * @param subTipo2 the subTipo2 to set
	 */
	public void setSubTipo2(String subTipo2) {
		this.subTipo2 = subTipo2;
	}
	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}
	/**
	 * @return the subTipoaux
	 */
	public String getSubTipoaux() {
		return subTipoaux;
	}
	/**
	 * @param subTipoaux the subTipoaux to set
	 */
	public void setSubTipoaux(String subTipoaux) {
		this.subTipoaux = subTipoaux;
	}
	/**
	 * @return the listaSubTipo3
	 */
	public List<Registro> getListaSubTipo3() {
		return listaSubTipo3;
	}
	/**
	 * @param listaSubTipo3 the listaSubTipo3 to set
	 */
	public void setListaSubTipo3(List<Registro> listaSubTipo3) {
		this.listaSubTipo3 = listaSubTipo3;
	}
	/**
	 * @return the visibleSubTipo3
	 */
	public boolean isVisibleSubTipo3() {
		return visibleSubTipo3;
	}
	/**
	 * @param visibleSubTipo3 the visibleSubTipo3 to set
	 */
	public void setVisibleSubTipo3(boolean visibleSubTipo3) {
		this.visibleSubTipo3 = visibleSubTipo3;
	}
	/**
	 * @return the subTipo3
	 */
	public String getSubTipo3() {
		return subTipo3;
	}
	/**
	 * @param subTipo3 the subTipo3 to set
	 */
	public void setSubTipo3(String subTipo3) {
		this.subTipo3 = subTipo3;
	}
	/**
	 * @return the visibleAppui
	 */
	public Boolean getVisibleAppui() {
		return visibleAppui;
	}
	/**
	 * @param visibleAppui the visibleAppui to set
	 */
	public void setVisibleAppui(Boolean visibleAppui) {
		this.visibleAppui = visibleAppui;
	}



}
