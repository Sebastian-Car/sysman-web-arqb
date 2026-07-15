/*-
 * FrmconfigsycControlador.java
 *
 * 1.0
 * 
 * 23/06/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmconfigsycControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/06/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class FrmconfigsycControlador extends BeanBaseDatosAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ; 
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listagruposSys;

	private RegistroDataModelImpl listagruposSysE;

	private String auxiliar;
	
	private boolean tieneDetalles;

	private RegistroDataModelImpl listatipoCobro;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>

	private RegistroDataModelImpl listaSubconceptos;
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	/**
	 * Atributo de referencia para el subformulario 
	 */
	private Registro registroSub;
	
	private static final String CAMPO_CODIGO_SYC = "CODIGO_SYC";
	private static final String CAMPO_TIPO_COBRO = "TIPO_COBRO";
	private static final String CAMPO_NOMBRE_TIPO = "NOMBRE_TIPO";
	private static final String CAMPO_GRUPO_SYS = "GRUPO_SYS";
	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmconfigsycControlador
	 */
	public FrmconfigsycControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_CONFIG_SYC
					.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			registro = new Registro(new HashMap<String, Object>());
			registroSub = new Registro(new HashMap<String, Object>());
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas(){
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListagruposSys(); 
		cargarListagruposSysE();
		cargarListatipoCobro();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub(){
		//<CARGAR_LISTAS_SUBFORM>
		cargarListaSubconceptos();
		//</CARGAR_LISTAS_SUBFORM>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
	}
	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo(){
		//<CARGAR_LISTAS_SUBFORM_NULL>
		//</CARGAR_LISTAS_SUBFORM_NULL>
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
		enumBase = GenericUrlEnum.SF_CONFIG_SYC;
		tieneDetalles = false;
		buscarLlave();
		asignarOrigenDatos();
	}

	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(),
				SysmanFunciones.ano(new Date()));
	}
	/**
	 * 
	 * Carga la lista listaSubconceptos
	 */
	public void cargarListaSubconceptos(){
		try{
			
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(CAMPO_CODIGO_SYC,registro.getCampos().get(CAMPO_CODIGO_SYC));

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.SF_CONFIG_SYC_DET
							.getGridKey());
			
			listaSubconceptos = new RegistroDataModelImpl(
					urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(),
					param,
					CacheUtil.getLlaveServicio(
							urlConexionCache,
							GenericUrlEnum.SF_CONFIG_SYC_DET
							.getTable()));
			
		}catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listagruposSys
	 *
	 */
	public void cargarListagruposSys(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmconfigsycControladorUrlEnum.URL662001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), 
								registro.getCampos().get(CAMPO_TIPO_COBRO));

		listagruposSys = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
	}
	/**
	 * 
	 * Carga la lista listagruposSys
	 *
	 */
	public void  cargarListagruposSysE(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmconfigsycControladorUrlEnum.URL662001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPOCONTRATO.getName(), 
								registro.getCampos().get(CAMPO_TIPO_COBRO));

		listagruposSysE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listatipoCobro
	 *
	 */
	public void cargarListatipoCobro(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmconfigsycControladorUrlEnum.URL665006
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones
				.ano(new Date()));

		listatipoCobro = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listagruposSys
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilagruposSys(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSub.getCampos().put(CAMPO_GRUPO_SYS, 
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listagruposSys
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilagruposSysE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listatipoCobro
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilatipoCobro(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(CAMPO_TIPO_COBRO, 
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		registro.getCampos().put(CAMPO_NOMBRE_TIPO, 
				registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
		cargarListagruposSys();
		cargarListagruposSysE();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
	/**
	 * Metodo de insercion del formulario Subconceptos
	 * 
	 */   
	public void agregarRegistroSubSubconceptos() {
		try {
			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			registroSub.getCampos().put(CAMPO_CODIGO_SYC, registro.getCampos().get(CAMPO_CODIGO_SYC));
			registroSub.getCampos().put(
					GeneralParameterEnum.CREATED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(
					GeneralParameterEnum.DATE_CREATED.getName(),
					new Date());
			
			UrlBean urlCreate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.SF_CONFIG_SYC_DET
							.getCreateKey());
			requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
					registroSub.getCampos());
			cargarListaSubconceptos();
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_INGRESADO"));
			
		} catch (SystemException ex) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally{
			registroSub = new Registro(new HashMap<String, Object>());
			cargarRegistro();
		} 
	}
	/**
	 * Metodo de edicion del formulario Subconceptos
	 * 
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSubconceptos(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			reg.getCampos().put(
					GeneralParameterEnum.DATE_MODIFIED.getName(),
					new Date());
			
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.SF_CONFIG_SYC_DET
							.getUpdateKey());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
					reg.getCampos(),
					reg.getLlave());
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_MODIFICADO"));
			
			
		} catch (SystemException ex) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally{
			cargarListaSubconceptos(); 
			cargarRegistro();
		}
	}
	/**
	 * Metodo de eliminacion del formulario Subconceptos
	 * 
	 * 
	 * @param reg
	 * registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSubconceptos(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							GenericUrlEnum.SF_CONFIG_SYC_DET
							.getDeleteKey());
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_ELIMINADO"));
			cargarListaSubconceptos(); 
		} catch (SystemException ex) {
			Logger.getLogger(DependenciasControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSubconceptos();
			cargarRegistro();
		}
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 * para el subformulario Subconceptos
	 *
	 */
	public void cancelarEdicionSubconceptos(){
		
	}
	//</METODOS_SUBFORM>	
	//<METODOS_ADICIONALES>	
	//</METODOS_ADICIONALES>
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
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {

		precargarRegistro();
		cargarListaSubconceptos();
		cargarListagruposSys();
		cargarListagruposSysE();

		try
		{
			Map<String, Object> params = new TreeMap<>();
			params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			params.put(CAMPO_CODIGO_SYC,registro.getCampos().get(CAMPO_CODIGO_SYC));

			Registro rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmconfigsycControladorUrlEnum.URL2006001
									.getValue())
							.getUrl(),
							params));

			if ((rs != null)
					&& !SysmanFunciones.validarCampoVacio(rs.getCampos(),"CUENTA"))
			{
				int cuenta = (int) rs.getCampos().get("CUENTA");

				if (cuenta == 0) {
					tieneDetalles = false;
				} else {
					tieneDetalles = true;
				}
			}

		} catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		cargarListagruposSys();
		cargarListagruposSysE();
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
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
		cargarListagruposSys();
		cargarListagruposSysE();
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
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 */
	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listagruposSys
	 * 
	 * @return listagruposSys
	 */
	public RegistroDataModelImpl getListagruposSys() {
		return listagruposSys;
	}
	/**
	 * Asigna la lista listagruposSys
	 * 
	 * @param listagruposSys
	 * Variable a asignar en  listagruposSys
	 */
	public void setListagruposSys(RegistroDataModelImpl listagruposSys) {
		this.listagruposSys = listagruposSys;
	}
	/**
	 * Retorna la lista listagruposSys
	 * 
	 * @return listagruposSys
	 */
	public RegistroDataModelImpl getListagruposSysE() {
		return listagruposSysE;
	}
	/**
	 * Asigna la lista listagruposSys
	 * 
	 * @param listagruposSys
	 * Variable a asignar en  listagruposSys
	 */
	public void setListagruposSysE(RegistroDataModelImpl listagruposSysE) {
		this.listagruposSysE = listagruposSysE;
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
	
	public boolean getTieneDetalles() {
        return tieneDetalles;
    }
    
    public void setTieneDetalles(boolean tieneDetalles) {
        this.tieneDetalles= tieneDetalles;
    }
	/**
	 * Retorna la lista listatipoCobro
	 * 
	 * @return listatipoCobro
	 */
	public RegistroDataModelImpl getListatipoCobro() {
		return listatipoCobro;
	}
	/**
	 * Asigna la lista listatipoCobro
	 * 
	 * @param listatipoCobro
	 * Variable a asignar en  listatipoCobro
	 */
	public void setListatipoCobro(RegistroDataModelImpl listatipoCobro) {
		this.listatipoCobro = listatipoCobro;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	/**
	 * Retorna la lista listaSubconceptos
	 * 
	 * @return listaSubconceptos
	 */
	public RegistroDataModelImpl getListaSubconceptos() {
		return listaSubconceptos;
	}
	/**
	 * Asigna la lista listaSubconceptos
	 * 
	 * @param listaSubconceptos
	 * Variable a asignar en  listaSubconceptos
	 */
	public void setListaSubconceptos(RegistroDataModelImpl listaSubconceptos) {
		this.listaSubconceptos = listaSubconceptos;
	}
	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	/**
	 * Retorna el objeto registroSub
	 * 
	 * @return registroSub
	 */
	public Registro getRegistroSub() {
		return registroSub;
	}
	/**
	 * Asigna el objeto registroSub
	 * 
	 * @param registroSub
	 * Variable a asignar en registroSub
	 */
	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}
	//</SET_GET_ADICIONALES>
}
