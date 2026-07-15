/*-
 * FutFormulariosCovidControlador.java
 *
 * 1.0
 * 
 * 19/10/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;
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
import com.sysman.chipfut.enums.InformesFormulariosControladorUrlEnum;
import com.sysman.chipfut.enums.InversionesfutControladorUrlEnum;
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
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 19/10/2020
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FutFormulariosCovidControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>

	private boolean pesos;
	private int anioTrabajo;
	private int mesInicial;
	private String nombreInforme;
	private int mesFinal;
	private String codigoEntidad;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAnio;
	private List<Registro> listaMesInicial;

	private List<Registro> listaMesFinal;
	private StreamedContent archivoDescarga;
	private String periodo;
	private String encabezadoInforme;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	private String nombreConsulta;
	private String opcion;
	private boolean totales;
	private String nombreReporte;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FutFormulariosCovidControlador
	 */
	public FutFormulariosCovidControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			//2202
			numFormulario = GeneralCodigoFormaEnum.FUT_FORMULARIOS_COVID_CONTROLADOR.getCodigo();
			validarPermisos();
			anioTrabajo= SysmanFunciones.ano(new Date());
			codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
			opcion = "";
			nombreInforme = "1";

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
		//<CARGAR_LISTA>
		cargarListaAnio();
		cargarListaMesInicial();
		cargarListaMesFinal();
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
		mesInicial = mesFinal  = SysmanFunciones.mes(new Date());
		cargarVariables();
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									InformesFormulariosControladorUrlEnum.URL189
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
	 * Carga la lista listaMesInicial
	 *
	 */
	public void cargarListaMesInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioTrabajo);

		try {
			listaMesInicial = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									InversionesfutControladorUrlEnum.URL4612
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
	 * Carga la lista listaMesFinal
	 *
	 */
	public void cargarListaMesFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anioTrabajo);

		try {
			listaMesFinal = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									InversionesfutControladorUrlEnum.URL4612
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
	 * Metodo ejecutado al oprimir el boton generaPlano
	 * en la vista
	 *
	 *
	 */
	public void oprimirgeneraPlano() {		
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.TXT);
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * Define las acciones necesarias para generar el informe realiza
	 * el reemplazo de valores en la consulta del informe y envía los
	 * parámetros definidos
	 * 
	 * @param formato
	 */
	private void generarInforme(ReportesBean.FORMATOS formato) {
	//	generarVariables();
		HashMap<String, Object> reemplazar = new HashMap<>();
		reemplazar.put("anioTrabajo", anioTrabajo);
		reemplazar.put("mesFinal", mesFinal);
		reemplazar.put("mesInicial", mesInicial);
		reemplazar.put("mesanterior", mesInicial - 1);
		reemplazar.put("tipo", opcion);
		try {
			String parametro = ejbSysmanUtil.consultarParametro(
					compania,
					"DIGITO REDONDEO DE INFORMES FUT",
					SessionUtil.getModulo(),
					new Date(), true);

			parametro = SysmanFunciones.validarVariableVacio(parametro)
					? "0"
							: parametro;
			reemplazar.put("redondeo", parametro);
		}
		catch (SystemException e1) {
			logger.error(e1.getMessage(), e1);
			JsfUtil.agregarMensajeError(e1.getMessage());
		}

		try {
			archivoDescarga = JsfUtil.reportesFut(
					nombreConsulta, reemplazar,
					generarEncabezado(),
					formato,
					nombreReporte,
					modulo, totales);
		}
		catch (JRException | IOException | SQLException | DRException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException
				e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}


	private String generarEncabezado() {

		String retorno = "";
		try {			
			String separadorColumnas = "\t";
			periodo = SysmanFunciones.padl(String.valueOf(mesInicial), 2, "0").concat(SysmanFunciones.padl(String.valueOf(mesFinal),2,"0"));

		retorno = SysmanFunciones.concatenar("S",
				separadorColumnas,
				codigoEntidad,
				separadorColumnas, "1", periodo,
				separadorColumnas,
				String.valueOf(anioTrabajo),
				separadorColumnas,
				encabezadoInforme,
				separadorColumnas,
				SysmanFunciones.convertirAFechaCadena(
						new Date(),
						"dd-MM-yyyy"));
		}
		catch (ParseException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		return retorno;
	}

	private void generarVariables() {
		switch (nombreInforme) {
		case "1":
			mesInicial = 1;
			mesFinal = 3;
			break;
		case "2":
			mesInicial = 4;
			mesFinal = 6;
			break;
		case "3":
			mesInicial = 7;
			mesFinal = 9;
			break;
		default:
			mesInicial = 10;
			mesFinal = 12;
			break;

		}
	}


	public void cargarVariables() {

		if ("1".equals(nombreInforme)) {
			encabezadoInforme = "GASTOS_FUNCIONAMIENTO_COVID";
			nombreReporte = "GASTOS FUNCIONAMIENTO COVID";
			opcion = "F";
			nombreConsulta = "800183InformeFUTGastoFuncionamientoCovid";
			totales = true;
		}
		else if ("2".equals(nombreInforme)) {
			encabezadoInforme = "GASTOS_INVERSION_COVID";
			nombreReporte = "GASTOS INVERSION COVID";
			opcion = "I";
			nombreConsulta = "800184InformeFUTGastoInversionCovid";
			totales = true;
		}
		else if ("3".equals(nombreInforme)) {
			encabezadoInforme = "REPORTE_INGRESOS_COVID";
			nombreReporte = "REPORTE INGRESOS COVID";
			opcion = "I";
			nombreConsulta = "800187InformeFUTIngresosCovid";
			totales = true;
		}
		else if ("4".equals(nombreInforme)) {
			encabezadoInforme = "SERVICIO_DEUDA_COVID";
			nombreReporte = "SERVICIO DEUDA COVID";
			opcion = "I";
			nombreConsulta = "800204InformeFUTServicioDeudaCovid";
			totales = false;
		}
		else if ("5".equals(nombreInforme)) {
			encabezadoInforme = "TRANSFERENCIAS_COMPROMETIDAS_COVID";
			nombreReporte = "TRANSFERENCIAS COMPROMETIDAS COVID";
			opcion = "I";
			nombreConsulta = "800206InformeFUTTransferenciasComprometidasCovid";
			totales = false;
		}
		else if ("6".equals(nombreInforme)) {
			encabezadoInforme = "TRANSFERENCIAS_RECIBIDAS_COVID";
			nombreReporte = "TRANSFERENCIAS RECIBIDAS COVID";
			opcion = "I";
			nombreConsulta = "800188InformeFUTTransferenciasCovid";
			totales = false;
		}

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
		archivoDescarga = null;
		generarInforme(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton verificaConfiguracion
	 * en la vista
	 *
	 *
	 */
	public void oprimirverificaConfiguracion() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	public void cambiarNombreInforme() {
		//<CODIGO_DESARROLLADO>
		cargarVariables();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarAnio() {
		mesInicial = 0;
		mesFinal   = 0;

	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable pesos
	 * 
	 * @return  pesos
	 */
	public boolean getPesos() {
		return pesos;
	}
	/**
	 * Asigna la variable  pesos
	 * 
	 * @param  pesos
	 * Variable a asignar en  pesos
	 */
	public void setPesos(boolean pesos) {
		this.pesos = pesos;
	}
	/**
	 * Retorna la variable anioTrabajo
	 * 
	 * @return  anioTrabajo
	 */
	public int getAnioTrabajo() {
		return anioTrabajo;
	}
	/**
	 * Asigna la variable  anioTrabajo
	 * 
	 * @param  anioTrabajo
	 * Variable a asignar en  anioTrabajo
	 */
	public void setAnioTrabajo(int anioTrabajo) {
		this.anioTrabajo = anioTrabajo;
	}
	/**
	 * Retorna la variable mesInicial
	 * 
	 * @return  mesInicial
	 */
	public int getMesInicial() {
		return mesInicial;
	}
	/**
	 * Asigna la variable  mesInicial
	 * 
	 * @param  mesInicial
	 * Variable a asignar en  mesInicial
	 */
	public void setMesInicial(int mesInicial) {
		this.mesInicial = mesInicial;
	}
	/**
	 * Retorna la variable nombreInforme
	 * 
	 * @return  nombreInforme
	 */
	public String getNombreInforme() {
		return nombreInforme;
	}
	/**
	 * Asigna la variable  nombreInforme
	 * 
	 * @param  nombreInforme
	 * Variable a asignar en  nombreInforme
	 */
	public void setNombreInforme(String nombreInforme) {
		this.nombreInforme = nombreInforme;
	}
	/**
	 * Retorna la variable mesFinal
	 * 
	 * @return  mesFinal
	 */
	public int getMesFinal() {
		return mesFinal;
	}
	/**
	 * Asigna la variable  mesFinal
	 * 
	 * @param  mesFinal
	 * Variable a asignar en  mesFinal
	 */
	public void setMesFinal(int mesFinal) {
		this.mesFinal = mesFinal;
	}
	/**
	 * Retorna la variable codigoEntidad
	 * 
	 * @return  codigoEntidad
	 */
	public String getCodigoEntidad() {
		return codigoEntidad;
	}
	/**
	 * Asigna la variable  codigoEntidad
	 * 
	 * @param  codigoEntidad
	 * Variable a asignar en  codigoEntidad
	 */
	public void setCodigoEntidad(String codigoEntidad) {
		this.codigoEntidad = codigoEntidad;
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
	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio
	 * Variable a asignar en  listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}
	/**
	 * Retorna la lista listaMesInicial
	 * 
	 * @return listaMesInicial
	 */
	public List<Registro> getListaMesInicial() {
		return listaMesInicial;
	}
	/**
	 * Asigna la lista listaMesInicial
	 * 
	 * @param listaMesInicial
	 * Variable a asignar en  listaMesInicial
	 */
	public void setListaMesInicial(List<Registro> listaMesInicial) {
		this.listaMesInicial = listaMesInicial;
	}
	/**
	 * Retorna la lista listaMesFinal
	 * 
	 * @return listaMesFinal
	 */
	public List<Registro> getListaMesFinal() {
		return listaMesFinal;
	}
	/**
	 * Asigna la lista listaMesFinal
	 * 
	 * @param listaMesFinal
	 * Variable a asignar en  listaMesFinal
	 */
	public void setListaMesFinal(List<Registro> listaMesFinal) {
		this.listaMesFinal = listaMesFinal;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}


	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
