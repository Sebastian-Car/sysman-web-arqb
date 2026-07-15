/*-
 * InmueblesisControlador.java
 *
 * 1.0
 * 
 * 22/11/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.enums.InmueblesisControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
/**
 *
 * @version 1.0, 22/11/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class InmueblesisControlador extends BeanBaseDatosAcmeImpl{
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
	private RegistroDataModelImpl listaElemento;
	private RegistroDataModelImpl listaSerie;
	private Object elemento;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de InmueblesisControlador
	 */
	public InmueblesisControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2134
			numFormulario = GeneralCodigoFormaEnum.INMUEBLESIS_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
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
		enumBase = GenericUrlEnum.BIENES_INMUEBLES;
		buscarLlave();
		asignarOrigenDatos();

		cargarListaElemento();

	}
	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	}

	//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 */
	public void cargarListaElemento(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InmueblesisControladorUrlEnum.URL001
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGOELEMENTO");

	}
	/**
	 * 
	 * Carga la lista listaSerie
	 *
	 */
	public void cargarListaSerie(){


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InmueblesisControladorUrlEnum.URL002
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

		listaSerie = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				"SERIE");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	


	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElemento
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElemento(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();


		elemento = registroAux.getCampos().get("CODIGOELEMENTO");
		registro.getCampos().put("ELEMENTO", registroAux.getCampos().get("CODIGOELEMENTO"));
		registro.getCampos().put("SERIE", "");
		cargarListaSerie();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSerie
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSerie(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("SERIE", registroAux.getCampos().get("SERIE"));
		registro.getCampos().put("FECHA_ADQUISICION", registroAux.getCampos().get("FECHAADQUISICION"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Comando61
	 * en la vista
	 *
	 *
	 */
	public void oprimirComando61() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Guardar
	 * en la vista
	 *
	 *
	 */
	public void oprimirGuardar() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Salir
	 * en la vista
	 *
	 */
	public void oprimirSalir() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
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
		/*
FR2134-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
    DoCmd.GoToRecord acForm, Me.Name, acNewRec
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		//<CODIGO_DESARROLLADO>
		precargarRegistro();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
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
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaElemento
	 * 
	 * @return listaElemento
	 */
	public RegistroDataModelImpl getListaElemento() {
		return listaElemento;
	}
	/**
	 * Asigna la lista listaElemento
	 * 
	 * @param listaElemento
	 * Variable a asignar en  listaElemento
	 */
	public void setListaElemento(RegistroDataModelImpl listaElemento) {
		this.listaElemento = listaElemento;
	}
	/**
	 * Retorna la lista listaSerie
	 * 
	 * @return listaSerie
	 */
	public RegistroDataModelImpl getListaSerie() {
		return listaSerie;
	}
	/**
	 * Asigna la lista listaSerie
	 * 
	 * @param listaSerie
	 * Variable a asignar en  listaSerie
	 */
	public void setListaSerie(RegistroDataModelImpl listaSerie) {
		this.listaSerie = listaSerie;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	//</SET_GET_ADICIONALES>
}
