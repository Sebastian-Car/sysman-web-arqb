/*-
 * FrmPasarInfEstFinancieroControlador.java
 *
 * 1.0
 * 
 * 04/05/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
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
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.enums.FrmConfigurarConceptosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
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
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
/**
 *
 * @version 1.0, 04/05/2026
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmPasarInfEstFinancieroControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private int anioOrigen;
	private int anioDestino;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAnioOrigen;
	private List<Registro> listaAnioDestino;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	private EjbContabilidadSieteRemote contabilidadSieteRemote;
	/**
	 * Crea una nueva instancia de FrmPasarInfEstFinancieroControlador
	 */
	public FrmPasarInfEstFinancieroControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2582;
			validarPermisos();
			//<INI_ADICIONAL>
			anioOrigen = SysmanFunciones.ano(new Date()) - 1;
			anioDestino = SysmanFunciones.ano(new Date());
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
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
		//<CARGAR_LISTA>
		cargarListaAnioOrigen();
		cargarListaAnioDestino();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
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
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnioOrigen
	 *
	 */
	public void cargarListaAnioOrigen(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		try {
			listaAnioOrigen = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmConfigurarConceptosControladorUrlEnum.URL4366
							.getValue())
					.getUrl(),
					param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaAnioDestino
	 *
	 */
	public void cargarListaAnioDestino(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioOrigen);

		try {
			listaAnioDestino = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							FrmConfigurarConceptosControladorUrlEnum.URL4072
							.getValue())
					.getUrl(),
					param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar
	 * en la vista
	 *
	 *
	 */
	public void oprimirAceptar() {
		//<CODIGO_DESARROLLADO>
		try {
			if(SessionUtil.getMenuActual().equals("10220080101")) {
				contabilidadSieteRemote.copiarConfigConceptos(compania, anioDestino, anioOrigen);
				JsfUtil.agregarMensajeInformativo("Proceso Finalizado");
				
				String[] campos = {};
				Object[] valores = {};
				SessionUtil.redireccionarFormularioModalFormulario(SessionUtil.getModulo(), String.valueOf(GeneralCodigoFormaEnum.FRM_CONFIGURAR_CONCEPTOS_CONTROLADOR.getCodigo()), campos, valores, true);
	                
			} else if(SessionUtil.getMenuActual().equals("10220080102")) {
				contabilidadSieteRemote.copiarConfigPlanContable(compania, anioDestino, anioOrigen);
				JsfUtil.agregarMensajeInformativo("Proceso Finalizado");
				
				String[] campos = {};
				Object[] valores = {};
				SessionUtil.redireccionarFormularioModalFormulario(SessionUtil.getModulo(), String.valueOf(2547), campos, valores, true);
	                
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control AnioOrigen
	 * 
	 * 
	 */
	public void cambiarAnioOrigen() {
		//<CODIGO_DESARROLLADO>
		cargarListaAnioDestino();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>

	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnioOrigen
	 * 
	 * @return listaAnioOrigen
	 */
	public List<Registro> getListaAnioOrigen() {
		return listaAnioOrigen;
	}
	/**
	 * @return the anioOrigen
	 */
	public int getAnioOrigen() {
		return anioOrigen;
	}
	/**
	 * @param anioOrigen the anioOrigen to set
	 */
	public void setAnioOrigen(int anioOrigen) {
		this.anioOrigen = anioOrigen;
	}
	/**
	 * @return the anioDestino
	 */
	public int getAnioDestino() {
		return anioDestino;
	}
	/**
	 * @param anioDestino the anioDestino to set
	 */
	public void setAnioDestino(int anioDestino) {
		this.anioDestino = anioDestino;
	}
	/**
	 * Asigna la lista listaAnioOrigen
	 * 
	 * @param listaAnioOrigen
	 * Variable a asignar en  listaAnioOrigen
	 */
	public void setListaAnioOrigen(List<Registro> listaAnioOrigen) {
		this.listaAnioOrigen = listaAnioOrigen;
	}
	/**
	 * Retorna la lista listaAnioDestino
	 * 
	 * @return listaAnioDestino
	 */
	public List<Registro> getListaAnioDestino() {
		return listaAnioDestino;
	}
	/**
	 * Asigna la lista listaAnioDestino
	 * 
	 * @param listaAnioDestino
	 * Variable a asignar en  listaAnioDestino
	 */
	public void setListaAnioDestino(List<Registro> listaAnioDestino) {
		this.listaAnioDestino = listaAnioDestino;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
