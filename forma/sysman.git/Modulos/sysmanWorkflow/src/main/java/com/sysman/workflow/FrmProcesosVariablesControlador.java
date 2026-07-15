/*-
 * FrmProcesosVariablesControlador.java
 *
 * 1.0
 * 
 * 01/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmNodoVariablesControladorEnum;
import com.sysman.workflow.enums.FrmNodoVariablesControladorUrlEnum;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/12/2020
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmProcesosVariablesControlador extends BeanBaseDatosAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ; 
	/** Constante a nivel de clase que aloja la cadena: CATEGORIA. */
	private final String cCategoria = FrmNodoVariablesControladorEnum.CATEGORIA
			.getValue();
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaTipoDato;
	private List<Registro> listaEstado;
	private String codigoProceso;
	private String nombreProceso;
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
	 * Crea una nueva instancia de FrmProcesosVariablesControlador
	 */
	public FrmProcesosVariablesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//22133
			numFormulario = GeneralCodigoFormaEnum.FRM_PROCESOS_VARIABLES_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			Map<String, Object> paramEntrada = SessionUtil.getFlash();

			if (paramEntrada != null) {
				codigoProceso = paramEntrada
						.get(FrmNodoVariablesControladorEnum.PR_CODIGO_PROCESO
								.getValue())
						.toString();

				nombreProceso = paramEntrada
						.get(FrmNodoVariablesControladorEnum.PR_PROCESO_NOM
								.getValue())
						.toString();
				
			}
			// </INI_ADICIONAL>
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
		cargarListaTipoDato();
		cargarListaEstado();
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
		enumBase = GenericUrlEnum.PROCESO_VARIABLE;
		buscarLlave();
		asignarOrigenDatos();
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

		parametrosListado.put("COMPANIA", compania);
		parametrosListado.put("PROCESO", codigoProceso);
	}

	//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaTipoDato
	 *
	 */
	public void cargarListaTipoDato(){
		Map<String, Object> param = new TreeMap<>();
		param.put(cCategoria, 6); // Tipos de dato

		try {
			listaTipoDato = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmNodoVariablesControladorUrlEnum.URL5515
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaEstado
	 *
	 */
	public void cargarListaEstado(){
		Map<String, Object> param = new TreeMap<>();
		param.put(cCategoria, 4); // Tipos de estado

		try {
			listaEstado = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmNodoVariablesControladorUrlEnum.URL5515
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
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

		if (css == null) {
			registro.getCampos().put("CODIGO_PROCESO", codigoProceso);
			registro.getCampos().put("PROCESO_NOM", nombreProceso);
			registro.getCampos().put("ESTADO", 4); /*- Estado Tipo Activo*/
		}
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
		registro.getCampos().remove("PROCESO_NOM");
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
		registro.getCampos().remove("PROCESO_NOM");
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
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 */
	public void cerrarFormulario() {
		RequestContext.getCurrentInstance().closeDialog(null);
	}
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaTipoDato
	 * 
	 * @return listaTipoDato
	 */
	public List<Registro> getListaTipoDato() {
		return listaTipoDato;
	}
	/**
	 * Asigna la lista listaTipoDato
	 * 
	 * @param listaTipoDato
	 * Variable a asignar en  listaTipoDato
	 */
	public void setListaTipoDato(List<Registro> listaTipoDato) {
		this.listaTipoDato = listaTipoDato;
	}
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
	 * Variable a asignar en  listaEstado
	 */
	public void setListaEstado(List<Registro> listaEstado) {
		this.listaEstado = listaEstado;
	}
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
