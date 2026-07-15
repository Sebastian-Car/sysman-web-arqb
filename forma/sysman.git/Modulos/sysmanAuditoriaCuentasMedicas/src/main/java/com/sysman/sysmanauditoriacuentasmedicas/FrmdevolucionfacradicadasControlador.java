/*-
 * FrmdevolucionfacradicadasControlador.java
 *
 * 1.0
 * 
 * 03/06/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;
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
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmImportarRipsControladorUrlEnum;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmdevolucionfacradicadasControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.enums.GeneralParameterEnum;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 03/06/2026
 * @author CristianFerneySuescu
 */
@ManagedBean
@ViewScoped
public class  FrmdevolucionfacradicadasControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	/**
	 * Código de la compañía en la cual inició sesión el usuario.
	 */
	private final String compania;

	/**
	 * Indica si el filtro del reporte se realiza por radicado o por tercero y fechas.
	 */
	private boolean porRadicado;

	/**
	 * NIT del tercero seleccionado para filtrar el reporte.
	 */
	private String tercero;

	/**
	 * Nombre de la clase de cuenta inicial seleccionada.
	 */
	private String claseCuentaInicial;

	/**
	 * Consecutivo del radicado seleccionado para filtrar el reporte.
	 */
	private String radicadoL;

	/**
	 * Tipo de filtro enviado al reporte: "SI" por radicado, "NO" por tercero y fechas.
	 */
	private String filtro;

	/**
	 * Fecha inicial del rango de búsqueda del reporte.
	 */
	private Date fechaInicial;

	/**
	 * Fecha final del rango de búsqueda del reporte.
	 */
	private Date fechaFinal;

	/**
	 * Nombre del tercero o paciente seleccionado.
	 */
	private String nombreTercero;

	/**
	 * Código de la clase de cuenta inicial seleccionada.
	 */
	private String codClaseCuentaInicial;

	/**
	 * Archivo generado para descarga en PDF o Excel desde la vista.
	 */
	private StreamedContent archivoDescarga;

	/**
	 * Lista de terceros disponibles para la compañía.
	 */
	private RegistroDataModelImpl listaTercero;

	/**
	 * Lista de clases de cuenta disponibles para la compañía.
	 */
	private RegistroDataModelImpl listaclaseCuentaInicial;

	/**
	 * Lista de radicados disponibles para la compañía.
	 */
	private RegistroDataModelImpl listaradicado;
	/**
	 * Crea una nueva instancia de FrmdevolucionfacradicadasControlador
	 */

	public FrmdevolucionfacradicadasControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//Formulario:2591
			numFormulario = GeneralCodigoFormaEnum.FRM_DEVOLUCION_FAC_RADICADA.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
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
		cargarListaTercero(); 
		cargarListaclaseCuentaInicial();
		cargarListaradicado();
		abrirFormulario();

		try {
			setFechaInicial(SysmanFunciones.primeroDeMesFecha(new Date()));
			setFechaFinal(SysmanFunciones.ultimoDiaDate(new Date()));
		}
		catch (ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){

	}

	/**
	 * 
	 * Carga la lista de clases de cuenta segun la compania seleccionada.
	 */
	public void cargarListaTercero(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmImportarRipsControladorUrlEnum.URL4391
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NIT");
	}


	/**
	 * 
	 * Carga la lista de clases de cuenta inicial segun la compania seleccionada.
	 */
	public void cargarListaclaseCuentaInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmdevolucionfacradicadasControladorUrlEnum.URL4395
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaclaseCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}


	/**
	 * 
	 * * Carga la lista de radicados segun la compania seleccionada.
	 */
	public void cargarListaradicado(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmdevolucionfacradicadasControladorUrlEnum.URL1823008
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaradicado  = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CONSECUTIVO");

	}

	/**
	 * 
	 * *Metodo en que se asigna el valor del filtro dependiendo de la seleccion del usuario, si es por radicado o por tercero y fechas
	 */
	public void cambiarckRadicado() {

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf
	 * en la vista 
	 *
	 */
	public void oprimirPdf() 
	{
		archivoDescarga = null;
		getInforme(FORMATOS.PDF);
	}


	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 */
	public void oprimirExcel() 
	{
		archivoDescarga = null;
		getInforme(FORMATOS.EXCEL97);  
	}


	/*
	 * Metodo encargado de obtener el informe dependiendo del formato seleccionado por el usuario,
	 *  se asignan los parametros necesarios para la consulta y se genera el archivo para descarga
	 */
	private void getInforme(FORMATOS formato) {

		HashMap<String, Object> reemplazar = new HashMap<>();

		try {

			if (porRadicado) {
				reemplazar.put("consecutivo", radicadoL);
				filtro = "SI";
				reemplazar.put("filtro", filtro);
			} else {
				filtro = "NO";
				reemplazar.put("filtro", filtro); 
				reemplazar.put("Tercero", tercero);
				reemplazar.put("ClaseCuentaInicial", codClaseCuentaInicial);
				reemplazar.put("FechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
				reemplazar.put("FechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));

			}

			// MANEJO DE PARAMETROS DE REEMPLAZO
			Map<String, Object> parametros = new HashMap<>();
			Date fechaActual = new Date();
			String consulta = "2957CertificadoAuditoriaDevuelto";

			parametros.put("PR_HORAG", SysmanFunciones.convertirAFechaCadena(fechaActual, "HH:mm:ss"));

			Reporteador.resuelveConsulta(
					consulta,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar,
					parametros
					);

			archivoDescarga = JsfUtil.exportarStreamed(
					consulta,
					parametros,
					ConectorPool.ESQUEMA_SYSMAN,
					formato
					);

		} catch (ParseException | JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Obtiene los datos del tercero seleccionado y los asigna a los campos
	 * de identificacion y nombre.
	 *
	 * @param event evento que contiene el tercero seleccionado
	 */
	public void seleccionarFilaTercero(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		tercero = SysmanFunciones
				.nvl(registroAux.getCampos().get("NIT"), "").toString();
		nombreTercero = SysmanFunciones
				.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
	}

	
	/**
	 * Obtiene los datos de la clase de cuenta seleccionada y los asigna
	 * a los campos de codigo y nombre.
	 *
	 * @param event evento que contiene la clase de cuenta seleccionada
	 */
	public void seleccionarFilaclaseCuentaInicial(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		codClaseCuentaInicial = SysmanFunciones.nvl(
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()),
				" ")
				.toString();
		claseCuentaInicial = SysmanFunciones.nvl(
				registroAux.getCampos().get(
						GeneralParameterEnum.NOMBRE.getName()),
				" ")
				.toString();
	}



	/**
	 * Obtiene los datos del radicado seleccionado y los asigna a los
	 * campos de radicado, tercero y nombre.
	 *
	 * @param event evento que contiene el registro seleccionado
	 */
	public void seleccionarFilaradicado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		radicadoL= SysmanFunciones.toString((registroAux.getCampos().get("CONSECUTIVO")));
		nombreTercero =SysmanFunciones.toString((registroAux.getCampos().get("NOMBRE")));
		tercero =SysmanFunciones.toString((registroAux.getCampos().get("NIT")));
	}



	// GETTERS Y SETTERS


	/**
	 * @return tercero
	 */
	public String getTercero() {
	    return tercero;
	}

	/**
	 * @return indica si la consulta es por radicado
	 */
	public boolean isPorRadicado() {
	    return porRadicado;
	}

	/**
	 * @param porRadicado valor a asignar
	 */
	public void setPorRadicado(boolean porRadicado) {
	    this.porRadicado = porRadicado;
	}

	/**
	 * @param tercero valor a asignar
	 */
	public void setTercero(String tercero) {
	    this.tercero = tercero;
	}

	/**
	 * @return claseCuentaInicial
	 */
	public String getClaseCuentaInicial() {
	    return claseCuentaInicial;
	}

	/**
	 * @param claseCuentaInicial valor a asignar
	 */
	public void setClaseCuentaInicial(String claseCuentaInicial) {
	    this.claseCuentaInicial = claseCuentaInicial;
	}

	/**
	 * @return radicadoL
	 */
	public String getRadicadoL() {
	    return radicadoL;
	}

	/**
	 * @param radicadoL valor a asignar
	 */
	public void setRadicadoL(String radicadoL) {
	    this.radicadoL = radicadoL;
	}

	/**
	 * @return nombreTercero
	 */
	public String getNombreTercero() {
	    return nombreTercero;
	}

	/**
	 * @return fechaInicial
	 */
	public Date getFechaInicial() {
	    return fechaInicial;
	}

	/**
	 * @param date valor a asignar
	 */
	public void setFechaInicial(Date date) {
	    this.fechaInicial = date;
	}

	/**
	 * @return fechaFinal
	 */
	public Date getFechaFinal() {
	    return fechaFinal;
	}

	/**
	 * @param date valor a asignar
	 */
	public void setFechaFinal(Date date) {
	    this.fechaFinal = date;
	}

	/**
	 * @param nombreTercero valor a asignar
	 */
	public void setNombreTercero(String nombreTercero) {
	    this.nombreTercero = nombreTercero;
	}

	/**
	 * @return archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
	    return archivoDescarga;
	}

	/**
	 * @return listaTercero
	 */
	public RegistroDataModelImpl getListaTercero() {
	    return listaTercero;
	}

	/**
	 * @param listaTercero valor a asignar
	 */
	public void setListaTercero(RegistroDataModelImpl listaTercero) {
	    this.listaTercero = listaTercero;
	}

	/**
	 * @return listaclaseCuentaInicial
	 */
	public RegistroDataModelImpl getListaclaseCuentaInicial() {
	    return listaclaseCuentaInicial;
	}

	/**
	 * @param listaclaseCuentaInicial valor a asignar
	 */
	public void setListaclaseCuentaInicial(
	        RegistroDataModelImpl listaclaseCuentaInicial) {
	    this.listaclaseCuentaInicial = listaclaseCuentaInicial;
	}

	/**
	 * @return listaradicado
	 */
	public RegistroDataModelImpl getListaradicado() {
	    return listaradicado;
	}

	/**
	 * @param listaradicado valor a asignar
	 */
	public void setListaradicado(RegistroDataModelImpl listaradicado) {
	    this.listaradicado = listaradicado;
	}

	/**
	 * @return codClaseCuentaInicial
	 */
	public String getCodClaseCuentaInicial() {
	    return codClaseCuentaInicial;
	}

	/**
	 * @param codClaseCuentaInicial valor a asignar
	 */
	public void setCodClaseCuentaInicial(String codClaseCuentaInicial) {
	    this.codClaseCuentaInicial = codClaseCuentaInicial;
	}
}
