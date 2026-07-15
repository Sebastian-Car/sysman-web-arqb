/*-
 * FrmOrdenDepartamentalControlador.java
 *
 * 1.0
 * 
 * 18/06/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.RowEditEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 18/06/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmOrdenDepartamentalControlador extends BeanBaseDatosAcmeImpl{
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
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaMedida;
	@EJB
    private EjbSysmanUtilRemote sysmanUtil;
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	/**
	 * Atributo de referencia para el subformulario 
	 */
	private Registro registroSub;
	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de FrmOrdenDepartamentalControlador
	 */
	public FrmOrdenDepartamentalControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = 2516;
			validarPermisos();
			//<INI_ADICIONAL>
			registro = new Registro(new HashMap<String, Object>());
			registroSub = new Registro(new HashMap<String, Object>());
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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
		cargarListaMedida();
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
		
		listaMedida = null;
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
		enumBase = GenericUrlEnum.EJE_ESTRUCTURAL;
		buscarLlave();
		asignarOrigenDatos();
	}
	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	}

	/**
	 * 
	 * Carga la lista listaMedida
	 *
	 */
	public void cargarListaMedida(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put("CODIGO", registro.getCampos().get("CODIGO_EJE"));

			UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
					GenericUrlEnum.MEDIDA_PIGCCT.getGridKey()
					);

			listaMedida = new RegistroDataModelImpl(
					urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(),
					param,
					CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.MEDIDA_PIGCCT.getTable())
					);
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

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
	/**
	 * Metodo de insercion del formulario Medida
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */   
	public void agregarRegistroSubMedida() {

		try {
			registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),compania);			
			registroSub.getCampos().put("CODIGO_EJE", registro.getCampos().get("CODIGO_EJE"));
			registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),new Date());

			UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
					GenericUrlEnum.MEDIDA_PIGCCT.getCreateKey());

			requestManager.save(urlCreate.getUrl(),urlCreate.getMetodo(),registroSub.getCampos());
			cargarListaMedida();
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
		} catch(SystemException ex) {
			Logger.getLogger(FrmOrdenDepartamentalControlador.class.getName()).log(Level.SEVERE,null,ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSub = new Registro(new HashMap<String,Object>());
		}
	}
	/**
	 * Metodo de edicion del formulario Medida
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubMedida(RowEditEvent event) {
		
		Registro reg = (Registro) event.getObject();
    	try {
    		reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
					SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
					new Date());
			
	    	UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
									GenericUrlEnum.MEDIDA_PIGCCT.getUpdateKey());
	    	
			requestManager.update(urlUpdate.getUrl(),urlUpdate.getMetodo(),reg.getCampos(),reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
    	} catch(SystemException ex) {
			Logger.getLogger(FrmOrdenDepartamentalControlador.class.getName()).log(Level.SEVERE,null,ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaMedida();
        }
    	
	}
	
	/**
	 * Metodo de eliminacion del formulario Medida
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg
	 * registro seleccionado en el subformulario
	 */
	public void eliminarRegSubMedida(Registro reg) {
		try {
			UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
									GenericUrlEnum.MEDIDA_PIGCCT.getDeleteKey());
			
			requestManager.delete(urlDelete.getUrl(),reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			cargarListaMedida();
		} catch(SystemException ex) {
			Logger.getLogger(FrmOrdenDepartamentalControlador.class.getName()).log(Level.SEVERE,null,ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 * para el subformulario Medida
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cancelarEdicionMedida(){
		cargarListaMedida();
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
		
		String manejaTrazadores = "";
		try {
			manejaTrazadores = SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
									"APLICA TRAZADORES EN PLAN DE DESARROLLO",
									SessionUtil.getModulo(),new Date(),true),"NO");
		} catch(SystemException e) {
			e.printStackTrace();
		}
		
		try {
			if(manejaTrazadores.equals("NO")) {
				throw new SysmanException(
	                    idioma.getString("MSM_PERMISOS_ACCEDER"));
			}
		} catch(Exception ex) {
			 logger.error(ex.getMessage(),ex);
			 SessionUtil.redireccionarMenuPermisos();
       } 
	}
	
	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cargarRegistro() {
		//<CODIGO_DESARROLLADO>
		precargarRegistro();
		//</CODIGO_DESARROLLADO>
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
		registro.getCampos().put("COMPANIA", compania);
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
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	/**
	 * Retorna la lista listaMedida
	 * 
	 * @return listaMedida
	 */

	
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
	 * @return the listaMedida
	 */
	public RegistroDataModelImpl getListaMedida() {
		return listaMedida;
	}
	/**
	 * @param listaMedida the listaMedida to set
	 */
	public void setListaMedida(RegistroDataModelImpl listaMedida) {
		this.listaMedida = listaMedida;
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
