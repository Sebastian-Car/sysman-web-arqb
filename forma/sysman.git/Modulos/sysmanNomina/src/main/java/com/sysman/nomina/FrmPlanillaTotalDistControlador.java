/*-
 * FrmPlanillaTotalDistControlador.java
 *
 * 1.0
 * 
 * 10/10/2024
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
import com.sysman.nomina.enums.ReporteAcumuladosControladorUrlEnum;
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
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 10/10/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmPlanillaTotalDistControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String planilla;
	private String anio;
	private String mes;
	private String periodo;
	private String proceso;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAno;
	private List<Registro> listaMes;
	private List<Registro> listaPeriodo;
	private List<Registro> listaProceso;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmPlanillaTotalDistControlador
	 */
	public FrmPlanillaTotalDistControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2484;
			validarPermisos();
			//<INI_ADICIONAL>
			anio = (String) SessionUtil.getSessionVar("anioNomina");
			mes = (String) SessionUtil.getSessionVar("mesNomina");
			periodo = (String) SessionUtil.getSessionVar("periodoNomina");
			proceso = (String) SessionUtil.getSessionVar("procesoNomina");
			planilla = "1";
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
		cargarListaAno();
		cargarListaMes();
		cargarListaPeriodo();
		cargarListaProceso();
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
		/*
FR2484-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
     formularioAbrir 1, Me.Name
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ReporteAcumuladosControladorUrlEnum.URL5200
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
	 * Carga la lista listaMes
	 *
	 */
	public void cargarListaMes(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaMes = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ReporteAcumuladosControladorUrlEnum.URL6048
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
	 * Carga la lista listaPeriodo
	 *
	 */
	public void cargarListaPeriodo(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			param.put(GeneralParameterEnum.MES.getName(), mes);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

			listaPeriodo = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ReporteAcumuladosControladorUrlEnum.URL6761
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
	 * Carga la lista listaProceso
	 *
	 */
	public void cargarListaProceso(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaProceso = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ReporteAcumuladosControladorUrlEnum.URL9007
									.getValue())
							.getUrl(), param));
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
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		generarInforme(FORMATOS.EXCEL);         
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Presentar
	 * en la vista
	 *
	 *
	 */
	public void oprimirPresentar() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;    
		generarInforme(FORMATOS.PDF);             
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	public void generarInforme(FORMATOS formato)  {
		try {
			Map<String, Object> reemplazos = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			reemplazos.put("anio", anio);
			reemplazos.put("mes", mes);
			reemplazos.put("proceso", proceso);
			reemplazos.put("periodo", periodo);


			String titulo = "Periodo: " + periodo + " de " + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)] + " de " + anio;
			parametros.put("PR_TITULO", titulo);
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());

			String reporte = "";

			if(planilla.equals("1")) {
				reporte = "002637PLANILLACCDIST";
			}else {
				reporte = "002638PLANILLACDCDIST";
			}	

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazos, parametros);


			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * 
	 */
	public void cambiarAno() {
		//<CODIGO_DESARROLLADO>
		cargarListaMes();
		cargarListaPeriodo();
		mes = null;
		periodo = null;
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Mes
	 * 
	 * 
	 */
	public void cambiarMes() {
		//<CODIGO_DESARROLLADO>
		cargarListaPeriodo();
		periodo = null;
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Proceso
	 * 
	 * 
	 */
	public void cambiarProceso() {
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
	 * Retorna la variable planilla
	 * 
	 * @return  planilla
	 */
	public String getPlanilla() {
		return planilla;
	}
	/**
	 * Asigna la variable  planilla
	 * 
	 * @param  planilla
	 * Variable a asignar en  planilla
	 */
	public void setPlanilla(String planilla) {
		this.planilla = planilla;
	}
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
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
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
