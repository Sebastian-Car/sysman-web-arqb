/*-
 * FrmresumcncptosnomproyeccionespasivoControlador.java
 *
 * 1.0
 * 
 * 02/10/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
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
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.PeriodoTrabajoControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 02/10/2020
 * @author lmosquera
 */
@ManagedBean
@ViewScoped
public class  FrmresumcncptosnomproyeccionespasivoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String proceso;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ano;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String mes;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String periodo;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaCB7365;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaCB7363;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaCB7362;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaCB7364;
	
	private String modulo;
	
	@EJB
	private EjbSysmanUtil ejbSysmanUtil;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmresumcncptosnomproyeccionespasivoControlador
	 */
	public FrmresumcncptosnomproyeccionespasivoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
		ano = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		try {
			numFormulario=2199;
			validarPermisos();
			//<INI_ADICIONAL>
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
		cargarListaCB7365();
		cargarListaCB7363();
		cargarListaCB7362();
		cargarListaCB7364();
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
	 * Carga la lista listaCB7365 - Proceso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCB7365(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaCB7365 = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL4058
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaCB7363 - Lista Ano
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCB7363(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		try {
			listaCB7363 = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL4735
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	/**
	 * 
	 * Carga la lista listaCB7362- Lista Mes
	 *
	 * TODO DOCUMENTACION ADICIONAL 
	 */
	public void cargarListaCB7362(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		try {
			listaCB7362 = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL5723
									.getValue())
							.getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaCB7364 - Lista Proceso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCB7364(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		try {
			listaCB7364 = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoTrabajoControladorUrlEnum.URL7274
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirEXCEL() {
		generarReporte(FORMATOS.EXCEL);        
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPDF() {
		//generarReporte(FORMATOS.PDF);
	}

	public void generarReporte(FORMATOS formatos) {

		try {	
			archivoDescarga=null;
			
			String reporte = "002157RESUMENCONCEPTOSNOMINAPROYECCIONESPASIVO";
			String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();
			
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("proceso", proceso);
			reemplazar.put("ano", ano);
			reemplazar.put("mes", mes);
			reemplazar.put("periodo", periodo);
			
			/*
			 * Parámetros específicos para IDIPRON
			 * 
			Map<String, Object> parametros = new HashMap<>();
			
			String jefeDesarrolloHumano;
			jefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
			        "NOMBRE JEFE DESARROLLO HUMANO", modulo, new Date(), false);
	        String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
	                "CARGO  JEFE DESARROLLO HUMANO", modulo, new Date(), false); 
	        String jefeNomina = ejbSysmanUtil.consultarParametro(compania,
	                "NOMBRE JEFE NOMINA", modulo, new Date(), false);
	        String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania,
	                "CARGO RESPONSABLE DE NOMINA", modulo, new Date(), false);
		
	        parametros.put("PR_IMAGENES", sticker);
	        parametros.put("PR_ANO", ano);
	        parametros.put("PR_MES", mes);
	        parametros.put("PR_PERIODO", periodo);
	        parametros.put("PR_AHORA", new Date());
	        
	        parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
	        parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
	        parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
	        parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
	        */
	        
	        String strsql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar);
			
			//parametros.put("PR_STRSQL", strsql);
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strsql, ConectorPool.ESQUEMA_SYSMAN, formatos, "RESUMENCONCEPTOSNOMINAPROYECCIONESPASIVO");
	        
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SQLException | DRException e) {
			Logger.getLogger(ResumPorCentroCostoControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control CB7365
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarCB7365() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control CB7363
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarCB7363() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control CB7362
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarCB7362() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable proceso
	 * 
	 * @return  proceso
	 */
	public String getProceso() {
		return proceso;
	}
	/**
	 * Asigna la variable  proceso
	 * 
	 * @param  proceso
	 * Variable a asignar en  proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	/**
	 * Retorna la variable ano
	 * 
	 * @return  ano
	 */
	public String getAno() {
		return ano;
	}
	/**
	 * Asigna la variable  ano
	 * 
	 * @param  ano
	 * Variable a asignar en  ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}
	/**
	 * Retorna la variable mes
	 * 
	 * @return  mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * Asigna la variable  mes
	 * 
	 * @param  mes
	 * Variable a asignar en  mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * Retorna la variable periodo
	 * 
	 * @return  periodo
	 */
	public String getPeriodo() {
		return periodo;
	}
	/**
	 * Asigna la variable  periodo
	 * 
	 * @param  periodo
	 * Variable a asignar en  periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaCB7365
	 * 
	 * @return listaCB7365
	 */
	public List<Registro> getListaCB7365() {
		return listaCB7365;
	}
	/**
	 * Asigna la lista listaCB7365
	 * 
	 * @param listaCB7365
	 * Variable a asignar en  listaCB7365
	 */
	public void setListaCB7365(List<Registro> listaCB7365) {
		this.listaCB7365 = listaCB7365;
	}
	/**
	 * Retorna la lista listaCB7363
	 * 
	 * @return listaCB7363
	 */
	public List<Registro> getListaCB7363() {
		return listaCB7363;
	}
	/**
	 * Asigna la lista listaCB7363
	 * 
	 * @param listaCB7363
	 * Variable a asignar en  listaCB7363
	 */
	public void setListaCB7363(List<Registro> listaCB7363) {
		this.listaCB7363 = listaCB7363;
	}
	/**
	 * Retorna la lista listaCB7362
	 * 
	 * @return listaCB7362
	 */
	public List<Registro> getListaCB7362() {
		return listaCB7362;
	}
	/**
	 * Asigna la lista listaCB7362
	 * 
	 * @param listaCB7362
	 * Variable a asignar en  listaCB7362
	 */
	public void setListaCB7362(List<Registro> listaCB7362) {
		this.listaCB7362 = listaCB7362;
	}
	/**
	 * Retorna la lista listaCB7364
	 * 
	 * @return listaCB7364
	 */
	public List<Registro> getListaCB7364() {
		return listaCB7364;
	}
	/**
	 * Asigna la lista listaCB7364
	 * 
	 * @param listaCB7364
	 * Variable a asignar en  listaCB7364
	 */
	public void setListaCB7364(List<Registro> listaCB7364) {
		this.listaCB7364 = listaCB7364;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
