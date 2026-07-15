/*-
 * FrmFactoresbonificacionpermanenciaControlador.java
 *
 * 1.0
 * 
 * 15/09/2020
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
import org.primefaces.model.StreamedContent;
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmFactoresbonificacionpermanenciaControladorUrlEnum;
import com.sysman.nomina.enums.FrmFactoresliquidacionprimasemestralControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
/**
 * @version 1.0, 15/09/2020
 * @author eorozco
 */
@ManagedBean
@ViewScoped
public class  FrmFactoresbonificacionpermanenciaControlador extends BeanBaseModal{

	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ano1;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String mes1;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String periodo;

	private StreamedContent archivoDescarga;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAno1;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaMes1;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaPeriodo1;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	private String modulo;
	private String procesoNomina;
	private String anoNomina;
	private String mesNomina;
	private String periodoNomina;

	/**
	 * Crea una nueva instancia de FrmFactoresbonificacionpermanenciaControlador
	 */
	public FrmFactoresbonificacionpermanenciaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
		anoNomina = (String) SessionUtil.getSessionVar("anioNomina");
		mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
		periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
		try {
			//2188
			numFormulario= GeneralCodigoFormaEnum.FRM_FACTORES_BONIFICACION_PERMANENCIA_CONTROLADOR.getCodigo();
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
	@PostConstruct
	public void inicializar() {

		cargarListaAno1();
		cargarListaMes1();
		cargarListaPeriodo1();
		abrirFormulario();
	}
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		ano1 = anoNomina;
		mes1 = mesNomina;
		periodo = periodoNomina;
		cargarListaMes1();
		cargarListaPeriodo1();      

		//</CODIGO_DESARROLLADO>
	}


	public void cargarListaAno1(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
					procesoNomina);

			listaAno1 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmFactoresbonificacionpermanenciaControladorUrlEnum.URL004
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// listaAno1 = service.getListado(conectorPool, "SELECT DISTINCT "+
		//"    ANO "+
		//" FROM "+
		//"     PERIODOS "+
		//" WHERE "+
		//"     ANO <> 0000"+
		//" ORDER BY "+
		//"  ANO DESC");
	}
	/**
	 * 
	 * Carga la lista listaMes1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaMes1(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
					procesoNomina);
			param.put(GeneralParameterEnum.ANO.getName(), ano1);

			listaMes1 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmFactoresbonificacionpermanenciaControladorUrlEnum.URL005
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// listaMes1 = service.getListado(conectorPool, "SELECT DISTINCT "+
		//"     PERIODOS.MES,"+
		//"     MES.NOMBRE "+
		//" FROM "+
		//"     MES "+
		//"         INNER JOIN PERIODOS "+
		//"         ON MES.NUMERO = PERIODOS.MES "+
		//" WHERE "+
		//"     PERIODOS.MES <> 00"+
		//"    ORDER BY PERIODOS.MES");
	}
	/**
	 * 
	 * Carga la lista listaPeriodo1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaPeriodo1(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
					procesoNomina);
			param.put(GeneralParameterEnum.ANO.getName(), ano1);
			param.put(GeneralParameterEnum.MES.getName(), mes1);

			listaPeriodo1 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmFactoresbonificacionpermanenciaControladorUrlEnum.URL006
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// listaPeriodo1 = service.getListado(conectorPool, "SELECT DISTINCT"+
		//"     periodo,"+
		//"     nom_periodo"+
		//" FROM"+
		//"     periodos"+
		//" WHERE"+
		//"     compania = :compania"+
		//"     AND id_de_proceso = :id_de_proceso"+
		//"     AND ano = :ano"+
		//"     AND mes = :mes"+
		//"     AND periodo NOT IN (0)"+
		//" ORDER BY"+
		//"     periodo");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PreliminarBancos
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirImprimirBancos() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generaInforme(ReportesBean.FORMATOS.EXCEL97);
		//</CODIGO_DESARROLLADO>
	}
	public void oprimirPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		generaInforme(ReportesBean.FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton ImprimirBancos
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */


	private void generaInforme(ReportesBean.FORMATOS formato) {
		try {
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("ano", ano1);
			reemplazar.put("mes", mes1);
			reemplazar.put("periodo", periodo);
			reemplazar.put("idProceso", procesoNomina);
			// MANEJO DE PARAMETROS DEL REPORTE
			Reporteador.resuelveConsulta("002123FACTORESBONIFICACIONPORPERMANENCIA",
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar,
					parametros);


			 String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
	            String jefeRH = ejbSysmanUtil.consultarParametro(compania,
	                            "NOMBRE JEFE DESARROLLO HUMANO",
	                            SessionUtil.getModulo(),
	                            new Date(), true);
	            String jefeNomina = ejbSysmanUtil.consultarParametro(compania,
	                            "NOMBRE JEFE NOMINA", SessionUtil.getModulo(),
	                            new Date(), true);
	            parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
	            parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeRH);
	            parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);


			archivoDescarga = JsfUtil.exportarStreamed("002123FACTORESBONIFICACIONPORPERMANENCIA", parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo ejecutado al cambiar el control Ano1
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAno1() {
		//<CODIGO_DESARROLLADO>
		mes1 = null;
		periodo = null;
		cargarListaMes1();
		cargarListaPeriodo1();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Mes1
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarMes1() {
		//<CODIGO_DESARROLLADO>
		periodo = null;
		cargarListaPeriodo1();
		//</CODIGO_DESARROLLADO>
	}
	
	
	
	public String getAno1() {
		return ano1;
	}
	public void setAno1(String ano1) {
		this.ano1 = ano1;
	}
	public String getMes1() {
		return mes1;
	}
	public void setMes1(String mes1) {
		this.mes1 = mes1;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public List<Registro> getListaAno1() {
		return listaAno1;
	}
	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
	}
	public List<Registro> getListaMes1() {
		return listaMes1;
	}
	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
	}
	public List<Registro> getListaPeriodo1() {
		return listaPeriodo1;
	}
	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}




	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>

}
