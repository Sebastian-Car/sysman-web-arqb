/*-
 * FrmTableroPowerBIControlador.java
 *
 * 1.0
 * 
 * 29/04/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmTableroPowerBIControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;
/**
 *
 * @version 1.0, 29/04/2026
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class FrmTableroPowerBIControlador extends BeanBaseDatosAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ; 
	
	private String urlPowerBI;
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmTableroPowerBIControlador
	 */
	public FrmTableroPowerBIControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = 2523;
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
		tabla = "URL_TABLERO_BI";
		buscarLlave();
		asignarOrigenDatos();
		abrirFormulario();
	}
	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		origenDatos="";	
	}
	//<METODOS_CARGAR_LISTA>	
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
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
		urlPowerBI = generarAccesoSeguro();
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
	
	public String obtenerUrlBi() {
		String url = null;

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.CODIGO.getName(), SessionUtil.getMenuActual());

		try {
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmTableroPowerBIControladorUrlEnum.URL1968001.getValue())
									.getUrl(),
									param));

			if(rs != null) {
				url = SysmanFunciones.toString(rs.getCampos().get(GeneralParameterEnum.URL.getName()));
			}

			if(url == null || url.trim().isEmpty()) {
				return null;
			}

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return url;
	}
	
	public String generarAccesoSeguro() {
	    try {
	        String urlReal = obtenerUrlBi();

	        if (urlReal == null || urlReal.trim().isEmpty()) {
	            return null;
	        }

	        String uuid = UUID.randomUUID().toString();

	        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
	                .getExternalContext().getSession(false);
	        session.setAttribute("BI_URL_" + uuid, urlReal);

	        return uuid;

	    } catch (Exception e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(
	                "Error al cargar el tablero. Por favor, comuníquese con el administrador del sistema.");
	        return null;
	    }
	}

	
	//<SET_GET_ATRIBUTOS>
	/**
	 * @return the urlPowerBI
	 */
	public String getUrlPowerBI() {
		return urlPowerBI;
	}
	/**
	 * @param urlPowerBI the urlPowerBI to set
	 */
	public void setUrlPowerBI(String urlPowerBI) {
		this.urlPowerBI = urlPowerBI;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	//</SET_GET_ADICIONALES>
}
