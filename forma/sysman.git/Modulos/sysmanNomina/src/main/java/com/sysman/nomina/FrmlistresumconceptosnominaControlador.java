/*-
 * FrmlistresumconceptosnominaControlador.java
 *
 * 1.0
 * 
 * 30/09/2020
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
 * @version 1.0, 30/09/2020
 * @author lmosquera
 */
@ManagedBean
@ViewScoped
public class  FrmlistresumconceptosnominaControlador extends BeanBaseModal{
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
	private List<Registro> listaProceso;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAno;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaMes;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaPeriodo;
	private String modulo;
	
	@EJB
	private EjbSysmanUtil ejbSysmanUtil;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmlistresumconceptosnominaControlador
	 */
	public FrmlistresumconceptosnominaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		proceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
		ano = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();
		mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();
		periodo = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();
		try {
			numFormulario=2195;
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
		cargarListaproceso();
		cargarListaano();
		cargarListames();
		cargarListaPeriodo();
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
	 * Carga la lista listaProceso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaproceso(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaProceso = RegistroConverter.toListRegistro(
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
	 * Carga la lista listaano
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaano(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		try {
			listaAno = RegistroConverter.toListRegistro(
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

	public void cargarListames(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		try {
			listaMes = RegistroConverter.toListRegistro(
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
	 * Carga la lista listaPeriodo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaPeriodo(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		try {
			listaPeriodo = RegistroConverter.toListRegistro(
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

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EXCEL
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
		generarReporte(FORMATOS.PDF);
	}

	public void generarReporte(FORMATOS formatos) {

		try {	
			archivoDescarga=null;
			
			String reporte = "002149RESUMENCONCEPTOSNOMINA";
			String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();
			
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("proceso", proceso);
			reemplazar.put("ano", ano);
			reemplazar.put("mes", mes);
			reemplazar.put("periodo", periodo);
			
			Map<String, Object> parametros = new HashMap<>();
			
			String jefeDesarrolloHumano;
			jefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
			        "NOMBRE JEFE DESARROLLO HUMANO", modulo, new Date(), false);
	        String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
	                "CARGO JEFE DESARROLLO HUMANO", modulo, new Date(), false); 
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
	        
	        String strsql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar);
			
			parametros.put("PR_STRSQL", strsql);
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
					formatos);
	        
		} catch (SystemException | JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			Logger.getLogger(ResumPorCentroCostoControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton NOMINACONTABILIDAD
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirNOMINACONTABILIDAD() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;            
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Proceso
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarProceso() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAno() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Mes
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarMes() {
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
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public List<Registro> getListaProceso() {
		return listaProceso;
	}
	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso
	 * Variable a asignar en  listaProceso
	 */
	public void setListaProceso(List<Registro> listaProceso) {
		this.listaProceso = listaProceso;
	}
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}
	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno
	 * Variable a asignar en  listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}
	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes
	 * Variable a asignar en  listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}
	/**
	 * Retorna la lista listaPeriodo
	 * 
	 * @return listaPeriodo
	 */
	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}
	/**
	 * Asigna la lista listaPeriodo
	 * 
	 * @param listaPeriodo
	 * Variable a asignar en  listaPeriodo
	 */
	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
