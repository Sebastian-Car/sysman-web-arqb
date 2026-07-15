/*-
 * FrpasarterceroscompaniasControlador.java
 *
 * 1.0
 * 
 * 01/11/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.services.RegistroDataModel;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/11/2023
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrpasarterceroscompaniasControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String companiaOrigen;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String companiaDestino;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModel listacompaniaOrigen;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModel listacompaniaDestino;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	
	@EJB
    private EjbGeneralesRemote ejbGenerales;
	/**
	 * Crea una nueva instancia de FrpasarterceroscompaniasControlador
	 */
	public FrpasarterceroscompaniasControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = 2433;
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListacompaniaOrigen();
		cargarListacompaniaDestino();
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacompaniaOrigen
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacompaniaOrigen() {
		listacompaniaOrigen = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FR2433_nuevo:TBCB8366",
				"SELECT CODIGO,NOMBRE FROM COMPANIA ORDER BY CODIGO", true, "CODIGO");
	}

	/**
	 * 
	 * Carga la lista listacompaniaDestino
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacompaniaDestino() {
		listacompaniaDestino = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, ":FR2433_nuevo:TBCB8367",
				"SELECT CODIGO,NOMBRE FROM COMPANIA ORDER BY CODIGO", true, "CODIGO");
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnAceptar en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirbtnAceptar() {
		try {
			ejbGenerales.copiarTerceroXCompania(companiaOrigen, companiaDestino);
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacompaniaOrigen
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacompaniaOrigen(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		companiaOrigen = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacompaniaDestino
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacompaniaDestino(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		companiaDestino = registroAux.getCampos().get("CODIGO").toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable companiaOrigen
	 * 
	 * @return companiaOrigen
	 */
	public String getCompaniaOrigen() {
		return companiaOrigen;
	}

	/**
	 * Asigna la variable companiaOrigen
	 * 
	 * @param companiaOrigen Variable a asignar en companiaOrigen
	 */
	public void setCompaniaOrigen(String companiaOrigen) {
		this.companiaOrigen = companiaOrigen;
	}

	/**
	 * Retorna la variable companiaDestino
	 * 
	 * @return companiaDestino
	 */
	public String getCompaniaDestino() {
		return companiaDestino;
	}

	/**
	 * Asigna la variable companiaDestino
	 * 
	 * @param companiaDestino Variable a asignar en companiaDestino
	 */
	public void setCompaniaDestino(String companiaDestino) {
		this.companiaDestino = companiaDestino;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacompaniaOrigen
	 * 
	 * @return listacompaniaOrigen
	 */
	public RegistroDataModel getListacompaniaOrigen() {
		return listacompaniaOrigen;
	}

	/**
	 * Asigna la lista listacompaniaOrigen
	 * 
	 * @param listacompaniaOrigen Variable a asignar en listacompaniaOrigen
	 */
	public void setListacompaniaOrigen(RegistroDataModel listacompaniaOrigen) {
		this.listacompaniaOrigen = listacompaniaOrigen;
	}

	/**
	 * Retorna la lista listacompaniaDestino
	 * 
	 * @return listacompaniaDestino
	 */
	public RegistroDataModel getListacompaniaDestino() {
		return listacompaniaDestino;
	}

	/**
	 * Asigna la lista listacompaniaDestino
	 * 
	 * @param listacompaniaDestino Variable a asignar en listacompaniaDestino
	 */
	public void setListacompaniaDestino(RegistroDataModel listacompaniaDestino) {
		this.listacompaniaDestino = listacompaniaDestino;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
