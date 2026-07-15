/*-
 * FrmFactoresliquidacionprimasemestralControlador.java
 *
 * 1.0
 * 
 * 11/09/2020
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FactoresPrimaJunControladorUrlEnum;
import com.sysman.nomina.enums.FrmFactoresliquidacionprimasemestralControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.persistencia.sqlserver.SysmanUtl;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
 * 
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/09/2020
 * @author eorozco
 */
@ManagedBean
@ViewScoped
public class  FrmFactoresliquidacionprimasemestralControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	private String ano1;

	private String mes;

	private String periodo;
	private String obs;

	private StreamedContent archivoDescarga;
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
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
	private String modulo;
	private String procesoNomina;
	private String anoNomina;
	private String mesNomina;
	private String periodoNomina;

	
	
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmFactoresliquidacionprimasemestralControlador
	 */
	public FrmFactoresliquidacionprimasemestralControlador() {
		super();

		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
		anoNomina = (String) SessionUtil.getSessionVar("anioNomina");
		mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
		periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
		try {
			//2186
			numFormulario= GeneralCodigoFormaEnum.FRM_FACTORES_LIQUIDACION_PRIMASEMESTRAL_CONTROLADOR.getCodigo();
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
	public void inicializar(){
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		  ano1 = anoNomina;
	        mes = mesNomina;
	        periodo = periodoNomina;
	        cargarListaMes1();
	        cargarListaPeriodo1();
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
											FrmFactoresliquidacionprimasemestralControladorUrlEnum.URL001
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
											FrmFactoresliquidacionprimasemestralControladorUrlEnum.URL002
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


	public void cargarListaPeriodo1(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
					procesoNomina);
			param.put(GeneralParameterEnum.ANO.getName(), ano1);
			param.put(GeneralParameterEnum.MES.getName(), mes);

			listaPeriodo1 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmFactoresliquidacionprimasemestralControladorUrlEnum.URL003
											.getValue())
									.getUrl(),
									param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// listaPeriodo1 = service.getListado(conectorPool, "SELECT DISTINCT "+
		//"     PERIODO "+
		//" FROM "+
		//"     PERIODOS "+
		//" WHERE "+
		//"     PERIODO <> 00");
	}


	  

	public void oprimirPreliminarBancos() {
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
	public void oprimirImprimirBancos() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generaInforme(ReportesBean.FORMATOS.EXCEL97);
		//</CODIGO_DESARROLLADO>
	}
	 private void generaInforme(ReportesBean.FORMATOS formato) {
	        try {
	        	Date fechaParametros = SysmanFunciones.ultimoDiaDate(SysmanFunciones
	        			.convertirAFecha("01/" + mes + "/"
	        					+ ano1));
	        	Map<String, Object> parametros = new HashMap<>();
	        	HashMap<String, Object> reemplazar = new HashMap<>();
	        	reemplazar.put("ano", ano1);
	        	reemplazar.put("mes", mes);
	        	reemplazar.put("periodo", periodo);
	        	reemplazar.put("idProceso", procesoNomina);
	        	// MANEJO DE PARAMETROS DEL REPORTE
	        	
	        	
	        	String reporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
	        			"FORMATO FACTORES PRIMA SEMESTRAL", SessionUtil.getModulo(),
	        			new Date(), true),"002138FACTORESLIQUIDACIONPRIMASEMESTRAL");
	            
	        	Reporteador.resuelveConsulta(reporte,
	        			Integer.parseInt(SessionUtil.getModulo()),
	        			reemplazar,
	        			parametros);

	        	parametros.put("PR_OBSERVACIONES", obs);

	        	String nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
	        	String jefeRH = ejbSysmanUtil.consultarParametro(compania,
	        			"NOMBRE JEFE DESARROLLO HUMANO",
	        			SessionUtil.getModulo(),
	        			new Date(), true);
	        	String jefeNomina = ejbSysmanUtil.consultarParametro(compania,
	        			"NOMBRE JEFE NOMINA", SessionUtil.getModulo(),
	        			new Date(), true);
	        	String cargoeElabora = consultarParametro(
	        			"CARGO DE QUIEN LIQUIDA NOMINA",
	        			fechaParametros);
	        	parametros.put("PR_NOMBREEMPRESA", nombreEmpresa);
	        	parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeRH);
	        	parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
	        	parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA",
	        			cargoeElabora);
	            
	            archivoDescarga = JsfUtil.exportarStreamed(
	            		reporte, parametros,
                        ConectorPool.ESQUEMA_SYSMAN, formato);
    }
    catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException | ParseException e) {
        logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage());
    }

}
	 public String consultarParametro(String nombre, Date fecha)
             throws SystemException {
 return ejbSysmanUtil.consultarParametro(compania, nombre, modulo, fecha,
                 false);
}
	
		public void cambiarAno1() {
	        //<CODIGO_DESARROLLADO>
			    mes = null;
		        periodo = null;
		        cargarListaMes1();
		        cargarListaPeriodo1();
	       //</CODIGO_DESARROLLADO>
	   }

	public void cambiarMes1() {
	        //<CODIGO_DESARROLLADO>
	    periodo = null;
	    cargarListaPeriodo1();
	       //</CODIGO_DESARROLLADO>
	   }
	
	
	


	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>

	/**
	 * Retorna la variable obs
	 * 
	 * @return  obs
	 */
	public String getObs() {
		return obs;
	}
	/**
	 * Asigna la variable  obs
	 * 
	 * @param  obs
	 * Variable a asignar en  obs
	 */
	public void setObs(String obs) {
		this.obs = obs;
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
	 * Retorna la lista listaAno1
	 * 
	 * @return listaAno1
	 */
	public List<Registro> getListaAno1() {
		return listaAno1;
	}
	/**
	 * Asigna la lista listaAno1
	 * 
	 * @param listaAno1
	 * Variable a asignar en  listaAno1
	 */
	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
	}
	/**
	 * Retorna la lista listaMes1
	 * 
	 * @return listaMes1
	 */
	public List<Registro> getListaMes1() {
		return listaMes1;
	}
	/**
	 * Asigna la lista listaMes1
	 * 
	 * @param listaMes1
	 * Variable a asignar en  listaMes1
	 */
	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
	}
	/**
	 * Retorna la lista listaPeriodo1
	 * 
	 * @return listaPeriodo1
	 */
	public List<Registro> getListaPeriodo1() {
		return listaPeriodo1;
	}
	/**
	 * Asigna la lista listaPeriodo1
	 * 
	 * @param listaPeriodo1
	 * Variable a asignar en  listaPeriodo1
	 */
	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}
	public String getAno1() {
		return ano1;
	}
	public void setAno1(String ano1) {
		this.ano1 = ano1;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}


	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
