/*-
 * FrmDistribucionFinalControlador.java
 *
 * 1.0
 * 
 * 14/10/2024
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
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmDistribucionFinalControladorUrlEnum;
import com.sysman.nomina.enums.ReporteAcumuladosControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
/**
 *
 * @version 1.0, 14/10/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmDistribucionFinalControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private boolean datosHst;
	private boolean verOrdenado;
	private boolean verOrdenadoAux;
	private String anio;
	private String mes;
	private String periodo;
	private String proceso;
	private String empInicial;
	private String empFinal;
	private String centroInicial;
	private String centroFinal;
	private String conceptoInicial;
	private String conceptoFinal;
	private String ordenado;
	private String auxInicial;
	private String auxFinal;
	private String ordenadoAux;
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
	private RegistroDataModelImpl listaEmpInicial;
	private RegistroDataModelImpl listaEmpFinal;
	private RegistroDataModelImpl listaCenInicial;
	private RegistroDataModelImpl listaCenFinal;
	private RegistroDataModelImpl listaConInicial;
	private RegistroDataModelImpl listaConFinal;
	private RegistroDataModelImpl listaauxiliarInicial;
	private RegistroDataModelImpl listaauxiliarFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private String parametro;
	/**
	 * Crea una nueva instancia de FrmDistribucionFinalControlador
	 */
	public FrmDistribucionFinalControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2485;
			validarPermisos();
			//<INI_ADICIONAL>
			anio = (String) SessionUtil.getSessionVar("anioNomina");
			mes = (String) SessionUtil.getSessionVar("mesNomina");
			periodo = (String) SessionUtil.getSessionVar("periodoNomina");
			proceso = (String) SessionUtil.getSessionVar("procesoNomina");
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
		cargarListaEmpInicial(); 
		cargarListaCenInicial();
		cargarListaConInicial(); 
		cargarListaauxiliarInicial();
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
		try {
			parametro = SysmanFunciones.nvlStr(SysmanFunciones.toString(ejbSysmanUtil.consultarParametro(compania, "MANEJA PROCESO DISTRIBUCION POR AUXILIAR", modulo, new Date(), false)), "NO");

			if(parametro.equals("SI")){
				verOrdenadoAux = true;
			}else{
				verOrdenado = true;
			}
            
			ordenado = "1";
			ordenadoAux = "1";
			empInicial = "0";
			empFinal = "999";
			centroInicial = "0";
			centroFinal = SysmanConstantes.CONS_CENTRO;
			conceptoInicial = "0";
			conceptoFinal = "999";
			auxInicial = "0";
			auxFinal = SysmanConstantes.CONS_AUXILIAR;

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

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
	/**
	 * 
	 * Carga la lista listaEmpInicial
	 *
	 */
	public void cargarListaEmpInicial(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDistribucionFinalControladorUrlEnum.URL210012
						.getValue());
		listaEmpInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ID_DE_EMPLEADO");
	}
	/**
	 * 
	 * Carga la lista listaEmpFinal
	 *
	 */
	public void cargarListaEmpFinal(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(), empInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDistribucionFinalControladorUrlEnum.URL210014
						.getValue());
		listaEmpFinal= new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ID_DE_EMPLEADO");
	}
	/**
	 * 
	 * Carga la lista listaCenInicial
	 *
	 */
	public void cargarListaCenInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDistribucionFinalControladorUrlEnum.URL20013
						.getValue());
		listaCenInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaCenFinal
	 *
	 */
	public void cargarListaCenFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDistribucionFinalControladorUrlEnum.URL20015
						.getValue());
		listaCenFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaConInicial
	 *
	 */
	public void cargarListaConInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDistribucionFinalControladorUrlEnum.URL151001
						.getValue());
		listaConInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ID_DE_CONCEPTO");
	}
	/**
	 * 
	 * Carga la lista listaConFinal
	 *
	 */
	public void cargarListaConFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CONCEPTO.getName(), conceptoInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDistribucionFinalControladorUrlEnum.URL151005
						.getValue());
		listaConFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "ID_DE_CONCEPTO");
	}
	/**
	 * 
	 * Carga la lista listaauxiliarInicial
	 *
	 */
	public void cargarListaauxiliarInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDistribucionFinalControladorUrlEnum.URL23010
						.getValue());
		listaauxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaauxiliarFinal
	 *
	 */
	public void cargarListaauxiliarFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put("CODIGOINICIAL", auxInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDistribucionFinalControladorUrlEnum.URL23019
						.getValue());
		listaauxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
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

	public void generarInforme(FORMATOS formato)  {
		try {

			Map<String, Object> reemplazos = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();
			reemplazos.put("anio", anio);
			reemplazos.put("mes", mes);
			reemplazos.put("proceso", proceso);
			reemplazos.put("periodo", periodo);
			reemplazos.put("emplInicial", empInicial);
			reemplazos.put("emplFinal", empFinal);
			reemplazos.put("centroInicial", centroInicial);
			reemplazos.put("centroFinal", centroFinal);
			reemplazos.put("conceptoInicial", conceptoInicial);
			reemplazos.put("conceptoFinal", conceptoFinal);
			reemplazos.put("auxInicial", auxInicial);
			reemplazos.put("auxFinal", auxFinal);


			String titulo = "Periodo: " + periodo + " del mes de " + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)] + " del año " + anio;
			parametros.put("PR_TITULO", titulo);
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());
			String reporte = "";
			if (parametro.equals("SI")) {

				switch (ordenadoAux) {
				case "1":
					reporte = "002643DistribucionFinalEmpAuxCentCon";
					break;
				case "2":
					reporte =  "002640DistribucionFinalAuxCon";
					break;
				case "3":
					reporte = "002641DistribucionFinalConempAux";
					break;
				case "4":
					reporte =  "002642DistribucionFinalEmpAux";
					break;
				}
			}else {
				if (datosHst) {
					reporte =  "002645DistribucionfinalHistoricos";
				}else {
					if (ordenado.equals("1")) {
						reporte =  "002639Distribucionfinal";
					}else {
						reporte =  "002644DistribucionFinalEmp";
					}
				}
			}

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazos, parametros);


			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);

		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarDistAuxPer
	 * en la vista
	 *
	 *
	 */
	public void oprimirConsultarDistAuxPer() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		generaReporte("800651ConsulDist_ID_CC_Y_AUX_Periodo");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarDistAuxMes
	 * en la vista
	 *
	 *
	 */
	public void oprimirConsultarDistAuxMes() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generaReporte("800652ConsulDist_ID_CC_Y_AUX_Mes");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarDistAuxAnio
	 * en la vista
	 *
	 *
	 */
	public void oprimirConsultarDistAuxAnio() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		generaReporte("800653ConsulDist_ID_CC_Y_AUX_Anio");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ConsultarDistAuxCuen
	 * en la vista
	 *
	 *
	 */
	public void oprimirConsultarDistAuxCuen() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generaReporte("800650ConsulDist_ID_CC_Y_AUX_Acum"); 
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	private void generaReporte(String reporte) {
		try {

			// PARAMETROS DE REEMPLAZO EN LA CONSULTA
			HashMap<String, Object> reemplazos = new HashMap<>();
			reemplazos.put("anio", anio);
			reemplazos.put("mes", mes);
			reemplazos.put("proceso", proceso);
			reemplazos.put("periodo", periodo);


			// MANEJO DE PARAMETROS DEL REPORTE
			Map<String, Object> parametros = new HashMap<>();

			String sql= Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazos);
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, reporte);

		}
		catch (JRException | IOException | NumberFormatException  | DRException | SQLException  | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
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
		anio = null;
		mes = null;
		periodo = null;
		cargarListaAno();
		cargarListaMes();
		cargarListaPeriodo();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Compara
	 * 
	 * 
	 */
	public void cambiarCompara() {
		//<CODIGO_DESARROLLADO>

		if (datosHst) {
			verOrdenado = false;
			verOrdenadoAux = false;
		}else {
			if(parametro.equals("SI")){
				verOrdenadoAux = true;
			}else{
				verOrdenado = true;
			}
		}


		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEmpInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEmpInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		empInicial= SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_EMPLEADO"));

		empFinal = null;
		cargarListaEmpFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEmpFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEmpFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		empFinal= SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_EMPLEADO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCenInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCenInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroInicial= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));

		centroFinal = null;
		cargarListaCenFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCenFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCenFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroFinal= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		conceptoInicial= SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_CONCEPTO"));

		conceptoFinal = null;
		cargarListaConFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaConFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaConFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		conceptoFinal= SysmanFunciones.toString(registroAux.getCampos().get("ID_DE_CONCEPTO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaauxiliarInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaauxiliarInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxInicial= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));

		auxFinal = null;
		cargarListaauxiliarFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaauxiliarFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaauxiliarFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxFinal= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable datosHst
	 * 
	 * @return  datosHst
	 */
	public boolean getDatosHst() {
		return datosHst;
	}
	/**
	 * Asigna la variable  datosHst
	 * 
	 * @param  datosHst
	 * Variable a asignar en  datosHst
	 */
	public void setDatosHst(boolean datosHst) {
		this.datosHst = datosHst;
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
	 * Retorna la variable empInicial
	 * 
	 * @return  empInicial
	 */
	public String getEmpInicial() {
		return empInicial;
	}
	/**
	 * Asigna la variable  empInicial
	 * 
	 * @param  empInicial
	 * Variable a asignar en  empInicial
	 */
	public void setEmpInicial(String empInicial) {
		this.empInicial = empInicial;
	}
	/**
	 * Retorna la variable empFinal
	 * 
	 * @return  empFinal
	 */
	public String getEmpFinal() {
		return empFinal;
	}
	/**
	 * Asigna la variable  empFinal
	 * 
	 * @param  empFinal
	 * Variable a asignar en  empFinal
	 */
	public void setEmpFinal(String empFinal) {
		this.empFinal = empFinal;
	}
	/**
	 * Retorna la variable centroInicial
	 * 
	 * @return  centroInicial
	 */
	public String getCentroInicial() {
		return centroInicial;
	}
	/**
	 * Asigna la variable  centroInicial
	 * 
	 * @param  centroInicial
	 * Variable a asignar en  centroInicial
	 */
	public void setCentroInicial(String centroInicial) {
		this.centroInicial = centroInicial;
	}
	/**
	 * Retorna la variable centroFinal
	 * 
	 * @return  centroFinal
	 */
	public String getCentroFinal() {
		return centroFinal;
	}
	/**
	 * Asigna la variable  centroFinal
	 * 
	 * @param  centroFinal
	 * Variable a asignar en  centroFinal
	 */
	public void setCentroFinal(String centroFinal) {
		this.centroFinal = centroFinal;
	}
	/**
	 * Retorna la variable conceptoInicial
	 * 
	 * @return  conceptoInicial
	 */
	public String getConceptoInicial() {
		return conceptoInicial;
	}
	/**
	 * Asigna la variable  conceptoInicial
	 * 
	 * @param  conceptoInicial
	 * Variable a asignar en  conceptoInicial
	 */
	public void setConceptoInicial(String conceptoInicial) {
		this.conceptoInicial = conceptoInicial;
	}
	/**
	 * Retorna la variable conceptoFinal
	 * 
	 * @return  conceptoFinal
	 */
	public String getConceptoFinal() {
		return conceptoFinal;
	}
	/**
	 * Asigna la variable  conceptoFinal
	 * 
	 * @param  conceptoFinal
	 * Variable a asignar en  conceptoFinal
	 */
	public void setConceptoFinal(String conceptoFinal) {
		this.conceptoFinal = conceptoFinal;
	}
	/**
	 * Retorna la variable ordenado
	 * 
	 * @return  ordenado
	 */
	public String getOrdenado() {
		return ordenado;
	}
	/**
	 * Asigna la variable  ordenado
	 * 
	 * @param  ordenado
	 * Variable a asignar en  ordenado
	 */
	public void setOrdenado(String ordenado) {
		this.ordenado = ordenado;
	}
	/**
	 * Retorna la variable auxInicial
	 * 
	 * @return  auxInicial
	 */
	public String getAuxInicial() {
		return auxInicial;
	}
	/**
	 * Asigna la variable  auxInicial
	 * 
	 * @param  auxInicial
	 * Variable a asignar en  auxInicial
	 */
	public void setAuxInicial(String auxInicial) {
		this.auxInicial = auxInicial;
	}
	/**
	 * Retorna la variable auxFinal
	 * 
	 * @return  auxFinal
	 */
	public String getAuxFinal() {
		return auxFinal;
	}
	/**
	 * Asigna la variable  auxFinal
	 * 
	 * @param  auxFinal
	 * Variable a asignar en  auxFinal
	 */
	public void setAuxFinal(String auxFinal) {
		this.auxFinal = auxFinal;
	}
	/**
	 * Retorna la variable ordenadoAux
	 * 
	 * @return  ordenadoAux
	 */
	public String getOrdenadoAux() {
		return ordenadoAux;
	}
	/**
	 * Asigna la variable  ordenadoAux
	 * 
	 * @param  ordenadoAux
	 * Variable a asignar en  ordenadoAux
	 */
	public void setOrdenadoAux(String ordenadoAux) {
		this.ordenadoAux = ordenadoAux;
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
	/**
	 * Retorna la lista listaEmpInicial
	 * 
	 * @return listaEmpInicial
	 */
	public RegistroDataModelImpl getListaEmpInicial() {
		return listaEmpInicial;
	}
	/**
	 * Asigna la lista listaEmpInicial
	 * 
	 * @param listaEmpInicial
	 * Variable a asignar en  listaEmpInicial
	 */
	public void setListaEmpInicial(RegistroDataModelImpl listaEmpInicial) {
		this.listaEmpInicial = listaEmpInicial;
	}
	/**
	 * Retorna la lista listaEmpFinal
	 * 
	 * @return listaEmpFinal
	 */
	public RegistroDataModelImpl getListaEmpFinal() {
		return listaEmpFinal;
	}
	/**
	 * Asigna la lista listaEmpFinal
	 * 
	 * @param listaEmpFinal
	 * Variable a asignar en  listaEmpFinal
	 */
	public void setListaEmpFinal(RegistroDataModelImpl listaEmpFinal) {
		this.listaEmpFinal = listaEmpFinal;
	}
	/**
	 * Retorna la lista listaCenInicial
	 * 
	 * @return listaCenInicial
	 */
	public RegistroDataModelImpl getListaCenInicial() {
		return listaCenInicial;
	}
	/**
	 * Asigna la lista listaCenInicial
	 * 
	 * @param listaCenInicial
	 * Variable a asignar en  listaCenInicial
	 */
	public void setListaCenInicial(RegistroDataModelImpl listaCenInicial) {
		this.listaCenInicial = listaCenInicial;
	}
	/**
	 * Retorna la lista listaCenFinal
	 * 
	 * @return listaCenFinal
	 */
	public RegistroDataModelImpl getListaCenFinal() {
		return listaCenFinal;
	}
	/**
	 * Asigna la lista listaCenFinal
	 * 
	 * @param listaCenFinal
	 * Variable a asignar en  listaCenFinal
	 */
	public void setListaCenFinal(RegistroDataModelImpl listaCenFinal) {
		this.listaCenFinal = listaCenFinal;
	}
	/**
	 * Retorna la lista listaConInicial
	 * 
	 * @return listaConInicial
	 */
	public RegistroDataModelImpl getListaConInicial() {
		return listaConInicial;
	}
	/**
	 * Asigna la lista listaConInicial
	 * 
	 * @param listaConInicial
	 * Variable a asignar en  listaConInicial
	 */
	public void setListaConInicial(RegistroDataModelImpl listaConInicial) {
		this.listaConInicial = listaConInicial;
	}
	/**
	 * Retorna la lista listaConFinal
	 * 
	 * @return listaConFinal
	 */
	public RegistroDataModelImpl getListaConFinal() {
		return listaConFinal;
	}
	/**
	 * Asigna la lista listaConFinal
	 * 
	 * @param listaConFinal
	 * Variable a asignar en  listaConFinal
	 */
	public void setListaConFinal(RegistroDataModelImpl listaConFinal) {
		this.listaConFinal = listaConFinal;
	}
	/**
	 * Retorna la lista listaauxiliarInicial
	 * 
	 * @return listaauxiliarInicial
	 */
	public RegistroDataModelImpl getListaauxiliarInicial() {
		return listaauxiliarInicial;
	}
	/**
	 * Asigna la lista listaauxiliarInicial
	 * 
	 * @param listaauxiliarInicial
	 * Variable a asignar en  listaauxiliarInicial
	 */
	public void setListaauxiliarInicial(RegistroDataModelImpl listaauxiliarInicial) {
		this.listaauxiliarInicial = listaauxiliarInicial;
	}
	/**
	 * Retorna la lista listaauxiliarFinal
	 * 
	 * @return listaauxiliarFinal
	 */
	public RegistroDataModelImpl getListaauxiliarFinal() {
		return listaauxiliarFinal;
	}
	/**
	 * Asigna la lista listaauxiliarFinal
	 * 
	 * @param listaauxiliarFinal
	 * Variable a asignar en  listaauxiliarFinal
	 */
	public void setListaauxiliarFinal(RegistroDataModelImpl listaauxiliarFinal) {
		this.listaauxiliarFinal = listaauxiliarFinal;
	}
	/**
	 * @return the verOrdenado
	 */
	public boolean isVerOrdenado() {
		return verOrdenado;
	}
	/**
	 * @param verOrdenado the verOrdenado to set
	 */
	public void setVerOrdenado(boolean verOrdenado) {
		this.verOrdenado = verOrdenado;
	}
	/**
	 * @return the verOrdenadoAux
	 */
	public boolean isVerOrdenadoAux() {
		return verOrdenadoAux;
	}
	/**
	 * @param verOrdenadoAux the verOrdenadoAux to set
	 */
	public void setVerOrdenadoAux(boolean verOrdenadoAux) {
		this.verOrdenadoAux = verOrdenadoAux;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
