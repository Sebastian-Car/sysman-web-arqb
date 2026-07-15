/*-
 * AutoLiquidacionControlador.java
 *
 * 1.0
 * 
 * 24/09/2021
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
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.AutoLiquidacionControladorEnum;
import com.sysman.nomina.enums.AutoLiquidacionControladorUrlEnum;
import com.sysman.nomina.enums.ResumenConceptosControladorEnum;
import com.sysman.nomina.enums.ResumenConceptosControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.sesion.SessionBean;
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
 * @version 1.0, 24/09/2021
 * @author jorduz
 */
@ManagedBean
@ViewScoped
public class  AutoLiquidacionControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private String anio = (String) SessionUtil.getSessionVar("anioNomina");
	private String mes = (String) SessionUtil.getSessionVar("mesNomina");
	private String periodo = (String) SessionUtil.getSessionVar("periodoNomina");
	private String modulo = SessionUtil.getModulo();
	private String reportepdf;
	private String reportexcel;
	private String headerEspecial;
	private final String proceso = (String) SessionUtil.getSessionVar("procesoNomina");
	private String etiqueta;
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAnio;
	private List<Registro> listaMes;
	private List<Registro> listaPeriodo;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de AutoLiquidacionControlador
	 */
	public AutoLiquidacionControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=GeneralCodigoFormaEnum.REPORTES_AUTOLIQUIDACION
					.getCodigo();
			validarPermisos();

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
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	@PostConstruct
	public void inicializar(){
		//<CARGAR_LISTA>
		cargarListaAnio();
		cargarListaMes();
		cargarListaPeriodo();
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
		switch (SessionUtil.getMenuActual()) {
		case "603040201":
			etiqueta = "SALUD MENSUAL";
			reportepdf = "000093PlanillaResumenAutoSalud";
			reportexcel = "800490PlanillaResumenAutoSalud"; 
			break;

		case "603040203":
			etiqueta = "SALUD QUINCENAL";
			reportepdf = "000093PlanillaResumenAutoSalud";
			reportexcel = "800490PlanillaResumenAutoSalud"; 
			break;

		case "603040204":
			etiqueta = "SALUD QUINCENAL EMPLEADO";
			reportepdf = "000096PlanillaResumenAutoSaludQuincenalEmpleado";
			reportexcel = "800491PlanillaResumenAutoSaludQuincenalEmpleado"; 
			break;
		case "603040301":
			etiqueta = "PENSION MENSUAL";
			reportepdf = "000162PlanillaResumenAutoPension";
			reportexcel = "800492PlanillaResumenAutoPension"; 
			break;

		case "603040302":
			etiqueta = "PENSIÓN QUINCENAL";
			reportepdf = "000162PlanillaResumenAutoPension";
			reportexcel = "800492PlanillaResumenAutoPension"; 
			break;

		case "603040305":
			etiqueta = "PENSIÓN QUINCENAL EMPLEADO";
			reportepdf = "001658PlanillaResumenAutoPensionQuincenalEmpleado";
			reportexcel = "800493PlanillaResumenAutoPensionQuincenalEmpleado"; 
			break;

		case "603040402":
			etiqueta = "RIESGOS QUINCENAL";
			reportepdf = "000087PlanillaResumenAutoRiesgoQuincenal";
			reportexcel = "800494PlanillaResumenAutoRiesgoQuincenal"; 
			break;
			
		case "603040209":
			etiqueta = "LISTADO SALUD POR CENTRO DE COSTOS";
			reportepdf = "002465PlanillaSaludxCentroCostos";
			reportexcel = "800564PlanillaSaludxCentroCostos"; 
			break;
		
		case "603040309":
			etiqueta = "LISTADO PENSIÓN POR CENTRO DE COSTOS";
			reportepdf = "002469PlanillaPensionxCDC";
			reportexcel = "800573PlanillaPensionxCDC"; 
			break;
			
		case "603040408":
			etiqueta = "LISTADO RIESGOS POR CENTRO DE COSTOS";
			reportepdf = "002470PlanillaRiesgosxCDC";
			reportexcel = "800575PlanillaRiesgosxCDC"; 
			break;
		}  
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>

	public void cargarListaAnio(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		try {
			listaAnio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									AutoLiquidacionControladorUrlEnum.URL2846
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaMes(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.ANO.getName(),
				anio);
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		param.put(AutoLiquidacionControladorEnum.ID_PROCESO.getValue(),
				proceso);

		try {
			listaMes = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									AutoLiquidacionControladorUrlEnum.URL3599
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	public void cargarListaPeriodo(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

		param.put(AutoLiquidacionControladorEnum.PROCESO.getValue(), proceso);	
		param.put(GeneralParameterEnum.ANO.getName(),anio);
		param.put(GeneralParameterEnum.MES.getName(),mes);

		try {
			listaPeriodo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									AutoLiquidacionControladorUrlEnum.URL4753
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	public void oprimirBtnPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnExcel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirBtnExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInformexcel(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	
	private void generarInforme(FORMATOS formato) {
		try {

			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("procesoNomina", proceso);
			reemplazar.put("anioNomina", anio);
			reemplazar.put("mesNomina", mes);
			reemplazar.put("periodoNomina", periodo);


			// PARAMETROS PARA GENERACION DE INFORME
			Map<String, Object> parametros = new HashMap<>();

			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
            headerEspecial = ejbSysmanUtil.consultarParametro(compania, "FORMATOS ESPECIALES BUCARAMANGA", String.valueOf(SysmanConstantes.MODULO_NOMINA), new Date(), true);
            parametros.put("PR_HEADER_ESPECIAL", headerEspecial.equals("SI")?true:false);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
			Reporteador.resuelveConsulta(reportepdf, Integer.parseInt(modulo), reemplazar,parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reportepdf, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		} catch (JRException | IOException |  NumberFormatException |  com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void generarInformexcel(FORMATOS formato) {
			String sql = "";
		try {

			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("procesoNomina", proceso);
			reemplazar.put("anioNomina", anio);
			reemplazar.put("mesNomina", mes);
			reemplazar.put("periodoNomina", periodo);


            sql = Reporteador.resuelveConsulta(reportexcel,
                    Integer.parseInt(SessionUtil.getModulo()),
                    reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                    ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                    reportexcel);

		} catch (JRException | IOException |  NumberFormatException |  com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException  | SQLException | DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * Obtiene el valor almacenado en la base de datos para el parametro ingresado.
	 * 
	 * @param nombreParametro Nombre del parametro a consultar en la base de datos.
	 * @param valorDefault    Valor por omision en caso de nulo.
	 * @return valor asignado al parametro
	 */

	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}

	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

	public List<Registro> getListaMes() {
		return listaMes;
	}

	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}

	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}

	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	public String getCompania() {
		return compania;
	}
	public String getProceso() {
		return proceso;
	}
	public String getReportepdf() {
		return reportepdf;
	}
	public void setReportepdf(String reportepdf) {
		this.reportepdf = reportepdf;
	}
	public String getReportexcel() {
		return reportexcel;
	}
	public void setReportexcel(String reportexcel) {
		this.reportexcel = reportexcel;
	}
	public String getHeaderEspecial() {
		return headerEspecial;
	}
	public void setHeaderEspecial(String headerEspecial) {
		this.headerEspecial = headerEspecial;
	}
}
