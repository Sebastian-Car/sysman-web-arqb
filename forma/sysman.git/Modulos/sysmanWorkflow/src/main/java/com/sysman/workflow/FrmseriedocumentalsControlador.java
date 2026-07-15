/*-
 * FrmseriedocumentalsControlador.java
 *
 * 1.0
 * 
 * 13/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.workflow.enums.FrmseriedocumentalsControladorEnum;
import com.sysman.workflow.enums.FrmseriedocumentalsControladorUrlEnum;

/**
 * Migración del formulario en access FRM_SERIEDOCUMENTAL a web con el
 * controlador FrmseriedocumentalsControlador
 *
 * @version 1.0, 13/04/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped

public class FrmseriedocumentalsControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;

	// <DECLARAR_ATRIBUTOS>
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista que contiene los detalles del combo Estado (CB5852).
	 */
	private List<Registro> listaEstado;
	private Float tiempoTotal;

	/**
	 * Lista que contiene los detalles del combo Proceso (CB5821) al
	 * insertar un registro.
	 */
	private List<Registro> listaVigencia;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmseriedocumentalsControlador
	 */
	public FrmseriedocumentalsControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = 1757;
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {

		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CARGAR_LISTA>
		cargarListaEstado();
		cargarListaVigencia();
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.SERIEDOCUMENTAL;

		buscarLlave();
		asignarOrigenDatos();

	}

	/**
	 * Se asignan las URLs correspondientes a las operaciones CRUD del
	 * formulario y los parametros de la grilla.
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(FrmseriedocumentalsControladorEnum.COMPANIA.getValue(), compania);

	}

	/**
	 * Metodo ejecutado al cambiar el control TIEMPO_RETENCION_ARGESTION
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarTIEMPO_RETENCION_ARGESTION() {
		calcularTiempoTotal();
	}
	/**
	 * Metodo ejecutado al cambiar el control TIEMPO_RETENCION_ARCENTRAL
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarTIEMPO_RETENCION_ARCENTRAL() {
		calcularTiempoTotal();
	}
	/**
	 * Metodo ejecutado al cambiar el control TIEMPO_RETENCION_AHISTORICO
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarTIEMPO_RETENCION_AHISTORICO() {
		calcularTiempoTotal();
	}


	/**
	 * Metodo ejecutado al cambiar el control UNIDAD_TIEMPO_ARGESTION
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarUNIDAD_TIEMPO_ARGESTION() {
		calcularTiempoTotal();
	}
	/**
	 * Metodo ejecutado al cambiar el control UNIDAD_TIEMPO_ARCENTRAL
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarUNIDAD_TIEMPO_ARCENTRAL() {
		calcularTiempoTotal();
	}
	/**
	 * Metodo ejecutado al cambiar el control UNIDAD_TIEMPO_AHISTORICO
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarUNIDAD_TIEMPO_AHISTORICO() {
		calcularTiempoTotal();
	}



	private void calcularTiempoTotal() {
		Float tiempoRetencionAhistorico = registro.getCampos().get("TIEMPO_RETENCION_AHISTORICO") != null ?
				Float.parseFloat(String.valueOf(registro.getCampos().get("TIEMPO_RETENCION_AHISTORICO"))): null;
		Float tiempoRetencionArcentral = registro.getCampos().get("TIEMPO_RETENCION_ARCENTRAL") != null ? 
						Float.parseFloat(String.valueOf(registro.getCampos().get("TIEMPO_RETENCION_ARCENTRAL"))):null;
		Float tiempoRetencionArgestion = registro.getCampos().get("TIEMPO_RETENCION_ARGESTION") != null ?
								Float.parseFloat(String.valueOf(registro.getCampos().get("TIEMPO_RETENCION_ARGESTION"))):null;
		String unidadTiempoArgestion =  registro.getCampos().get("UNIDAD_TIEMPO_ARGESTION") != null ?
								String.valueOf(registro.getCampos().get("UNIDAD_TIEMPO_ARGESTION")): null;
		String unidadTiempoArcentral = registro.getCampos().get("UNIDAD_TIEMPO_ARCENTRAL") != null?
								String.valueOf(registro.getCampos().get("UNIDAD_TIEMPO_ARCENTRAL")): null;
		String unidadTiempoAhistorico = registro.getCampos().get("UNIDAD_TIEMPO_AHISTORICO") != null ?
								String.valueOf(registro.getCampos().get("UNIDAD_TIEMPO_AHISTORICO")): null;
		if(tiempoRetencionAhistorico != null && 
				tiempoRetencionArcentral != null && 
				tiempoRetencionArgestion != null && 
				unidadTiempoArgestion != null &&
				unidadTiempoArcentral != null &&
				unidadTiempoAhistorico != null){
			tiempoRetencionAhistorico = convertirAnios(tiempoRetencionAhistorico, unidadTiempoAhistorico);
			tiempoRetencionArgestion = convertirAnios(tiempoRetencionArgestion, unidadTiempoArgestion);
			tiempoRetencionArcentral = convertirAnios(tiempoRetencionArcentral, unidadTiempoArcentral);
			this.tiempoTotal = tiempoRetencionAhistorico + tiempoRetencionArgestion + tiempoRetencionArcentral;
		}else {
			this.tiempoTotal = null;
		}

	}

	private float convertirAnios(float tiempo, String unidad) {
		switch (unidad) {
		case "A":
			return tiempo;
		case "M":
			return tiempo/12;
		case "D":
			return tiempo/365;
		default:
			return 0;
		}
	}

	/**
	 * 
	 * Carga la lista listaVigencia
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaVigencia(){


		Map<String, Object> param = new TreeMap<>();

		param.put(FrmseriedocumentalsControladorEnum.COMPANIA.getValue(), compania);

		try {
			listaVigencia = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmseriedocumentalsControladorUrlEnum.URL3349
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaEstado Asociada al combo Estado
	 *
	 * 
	 */
	public void cargarListaEstado() {

		Map<String, Object> param = new TreeMap<>();

		param.put(FrmseriedocumentalsControladorEnum.CATEGORIA.getValue(), 4);

		try {
			listaEstado = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmseriedocumentalsControladorUrlEnum.URL3348
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 * 
	 */
	@Override
	public void cargarRegistro() {

		// <CODIGO_DESARROLLADO>
		precargarRegistro();

		if (css == null) {
			registro.getCampos().put("ESTADO", 4);
			registro.getCampos().put("UNIDAD_TIEMPO_AHISTORICO", 'A');
			registro.getCampos().put("UNIDAD_TIEMPO_ARCENTRAL", 'A');
			registro.getCampos().put("UNIDAD_TIEMPO_ARGESTION", 'A');
		}			
		calcularTiempoTotal();
		
		

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y
	 * actualizacion del registro
	 * 
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 * 
	 * @return true
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaEstado
	 * 
	 * @return listaEstado
	 */
	public List<Registro> getListaEstado() {
		return listaEstado;
	}

	/**
	 * Asigna la lista listaEstado
	 * 
	 * @param listaEstado
	 * Variable a asignar en listaEstado
	 */
	public void setListaEstado(List<Registro> listaEstado) {
		this.listaEstado = listaEstado;
	}


	/**
	 * Retorna la lista listaVigencia
	 * 
	 * @return listaVigencia
	 */
	public List<Registro> getListaVigencia() {
		return listaVigencia;
	}
	/**
	 * Asigna la lista listaVigencia
	 * 
	 * @param listaVigencia
	 * Variable a asignar en  listaVigencia
	 */
	public void setListaVigencia(List<Registro> listaVigencia) {
		this.listaVigencia = listaVigencia;
	}
	// <SET_GET_ATRIBUTOS>
	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	// </SET_GET_LISTAS_SUBFORM>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_ADICIONALES>
	// </SET_GET_ADICIONALES>

	public Float getTiempoTotal() {
		return tiempoTotal;
	}

	public void setTiempoTotal(Float tiempoTotal) {
		this.tiempoTotal = tiempoTotal;
	}
}
