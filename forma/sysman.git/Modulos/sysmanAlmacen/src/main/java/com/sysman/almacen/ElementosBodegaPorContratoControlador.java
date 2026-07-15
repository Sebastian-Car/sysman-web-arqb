/*-
 * ElementosBodegaPorContratoControlador.java
 *
 * 1.0
 * 
 * 24/08/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.enums.ElementosBodegaPorContratoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
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
 * @version 1.0, 24/08/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  ElementosBodegaPorContratoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String proyectoIni;
	private String proyectoFin;
	private String contratoFin;
	private String contratoIni;
	private Date fechaCorte;
	private String nomProyectoIni;
	private String nomContratoFin;
	private String nomContratoIni;
	private String nomProyectoFin;
	private boolean bloqueo;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;	
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaProyectoInicio;
	private RegistroDataModelImpl listaProyectoFinal;
	private RegistroDataModelImpl listaContratoFinal;
	private RegistroDataModelImpl listaContratoInicio;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ElementosBodegaPorContratoControlador
	 */
	public ElementosBodegaPorContratoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			//2423
			numFormulario= GeneralCodigoFormaEnum.ELEMENTOS_BODEGA_POR_CONTRATO_CONTROLADOR.getCodigo();
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
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaProyectoInicio();
		cargarListaContratoFinal(); 
		cargarListaContratoInicio();
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
		inicializarVariables();
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaProyectoInicio
	 *
	 */
	public void cargarListaProyectoInicio(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ElementosBodegaPorContratoControladorUrlEnum.URL32003.getValue()
						);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaProyectoInicio = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaProyectoFinal
	 *
	 */
	public void cargarListaProyectoFinal(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ElementosBodegaPorContratoControladorUrlEnum.URL32013.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put("NUMERO",
				proyectoIni);

		listaProyectoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
	}
	/**
	 * 
	 * Carga la lista listaContratoFinal
	 *
	 */
	public void cargarListaContratoFinal(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ElementosBodegaPorContratoControladorUrlEnum.URL73056.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
	}
	/**
	 * 
	 * Carga la lista listaContratoInicio
	 *
	 */
	public void cargarListaContratoInicio(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ElementosBodegaPorContratoControladorUrlEnum.URL73056.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaContratoInicio = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		
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
		generaInforme(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	
	public void generaInforme(ReportesBean.FORMATOS formato) {

		archivoDescarga = null;
		String reporte = "002492ElementosBodegaPorContrato";
		try {
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("fechaCorte",
					SysmanFunciones.formatearFecha(fechaCorte));
			reemplazar.put("proyectoIni", proyectoIni);
			reemplazar.put("proyectoFin", proyectoFin);
			reemplazar.put("contratoIni", contratoIni);
			reemplazar.put("contratoFin", contratoFin);
			parametros.put("PR_FECHA_CORTE", SysmanFunciones.convertirAFechaCadena(fechaCorte));
			Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	public void inicializarVariables() {

		try {

			bloqueo = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA PEPS CONSUMO DE ALMACEN IDIPRON", modulo, new Date(), false), "NO").equals("SI")? false:true;
			fechaCorte = new Date();
			contratoIni = " ";
			contratoFin = "ZZZ";
			proyectoIni = "0";
			proyectoFin = "999999999999";

		} catch (SystemException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProyectoInicio
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProyectoInicio(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proyectoIni= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		nomProyectoIni = SysmanFunciones.toString(registroAux.getCampos().get("NOMBREPROYECTO"));
		cargarListaProyectoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProyectoFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProyectoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proyectoFin= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		nomProyectoFin = SysmanFunciones.toString(registroAux.getCampos().get("NOMBREPROYECTO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaContratoFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaContratoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		contratoFin= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		nomContratoFin = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaContratoInicio
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaContratoInicio(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		contratoIni= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		nomContratoIni = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable proyectoIni
	 * 
	 * @return  proyectoIni
	 */
	public String getProyectoIni() {
		return proyectoIni;
	}
	/**
	 * Asigna la variable  proyectoIni
	 * 
	 * @param  proyectoIni
	 * Variable a asignar en  proyectoIni
	 */
	public void setProyectoIni(String proyectoIni) {
		this.proyectoIni = proyectoIni;
	}
	/**
	 * Retorna la variable proyectoFin
	 * 
	 * @return  proyectoFin
	 */
	public String getProyectoFin() {
		return proyectoFin;
	}
	/**
	 * Asigna la variable  proyectoFin
	 * 
	 * @param  proyectoFin
	 * Variable a asignar en  proyectoFin
	 */
	public void setProyectoFin(String proyectoFin) {
		this.proyectoFin = proyectoFin;
	}
	/**
	 * Retorna la variable contratoFin
	 * 
	 * @return  contratoFin
	 */
	public String getContratoFin() {
		return contratoFin;
	}
	/**
	 * Asigna la variable  contratoFin
	 * 
	 * @param  contratoFin
	 * Variable a asignar en  contratoFin
	 */
	public void setContratoFin(String contratoFin) {
		this.contratoFin = contratoFin;
	}
	/**
	 * Retorna la variable contratoIni
	 * 
	 * @return  contratoIni
	 */
	public String getContratoIni() {
		return contratoIni;
	}
	/**
	 * Asigna la variable  contratoIni
	 * 
	 * @param  contratoIni
	 * Variable a asignar en  contratoIni
	 */
	public void setContratoIni(String contratoIni) {
		this.contratoIni = contratoIni;
	}
	/**
	 * Retorna la variable fechaCorte
	 * 
	 * @return  fechaCorte
	 */
	public Date getFechaCorte() {
		return fechaCorte;
	}
	/**
	 * Asigna la variable  fechaCorte
	 * 
	 * @param  fechaCorte
	 * Variable a asignar en  fechaCorte
	 */
	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}
	/**
	 * Retorna la variable nomProyectoIni
	 * 
	 * @return  nomProyectoIni
	 */
	public String getNomProyectoIni() {
		return nomProyectoIni;
	}
	/**
	 * Asigna la variable  nomProyectoIni
	 * 
	 * @param  nomProyectoIni
	 * Variable a asignar en  nomProyectoIni
	 */
	public void setNomProyectoIni(String nomProyectoIni) {
		this.nomProyectoIni = nomProyectoIni;
	}
	/**
	 * Retorna la variable nomContratoFin
	 * 
	 * @return  nomContratoFin
	 */
	public String getNomContratoFin() {
		return nomContratoFin;
	}
	/**
	 * Asigna la variable  nomContratoFin
	 * 
	 * @param  nomContratoFin
	 * Variable a asignar en  nomContratoFin
	 */
	public void setNomContratoFin(String nomContratoFin) {
		this.nomContratoFin = nomContratoFin;
	}
	/**
	 * Retorna la variable nomContratoIni
	 * 
	 * @return  nomContratoIni
	 */
	public String getNomContratoIni() {
		return nomContratoIni;
	}
	/**
	 * Asigna la variable  nomContratoIni
	 * 
	 * @param  nomContratoIni
	 * Variable a asignar en  nomContratoIni
	 */
	public void setNomContratoIni(String nomContratoIni) {
		this.nomContratoIni = nomContratoIni;
	}
	/**
	 * Retorna la variable nomProyectoFin
	 * 
	 * @return  nomProyectoFin
	 */
	public String getNomProyectoFin() {
		return nomProyectoFin;
	}
	/**
	 * Asigna la variable  nomProyectoFin
	 * 
	 * @param  nomProyectoFin
	 * Variable a asignar en  nomProyectoFin
	 */
	public void setNomProyectoFin(String nomProyectoFin) {
		this.nomProyectoFin = nomProyectoFin;
	}
	/**
	 * @return the bloqueo
	 */
	public boolean isBloqueo() {
		return bloqueo;
	}
	/**
	 * @param bloqueo the bloqueo to set
	 */
	public void setBloqueo(boolean bloqueo) {
		this.bloqueo = bloqueo;
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
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaProyectoInicio
	 * 
	 * @return listaProyectoInicio
	 */
	public RegistroDataModelImpl getListaProyectoInicio() {
		return listaProyectoInicio;
	}
	/**
	 * Asigna la lista listaProyectoInicio
	 * 
	 * @param listaProyectoInicio
	 * Variable a asignar en  listaProyectoInicio
	 */
	public void setListaProyectoInicio(RegistroDataModelImpl listaProyectoInicio) {
		this.listaProyectoInicio = listaProyectoInicio;
	}
	/**
	 * Retorna la lista listaProyectoFinal
	 * 
	 * @return listaProyectoFinal
	 */
	public RegistroDataModelImpl getListaProyectoFinal() {
		return listaProyectoFinal;
	}
	/**
	 * Asigna la lista listaProyectoFinal
	 * 
	 * @param listaProyectoFinal
	 * Variable a asignar en  listaProyectoFinal
	 */
	public void setListaProyectoFinal(RegistroDataModelImpl listaProyectoFinal) {
		this.listaProyectoFinal = listaProyectoFinal;
	}
	/**
	 * Retorna la lista listaContratoFinal
	 * 
	 * @return listaContratoFinal
	 */
	public RegistroDataModelImpl getListaContratoFinal() {
		return listaContratoFinal;
	}
	/**
	 * Asigna la lista listaContratoFinal
	 * 
	 * @param listaContratoFinal
	 * Variable a asignar en  listaContratoFinal
	 */
	public void setListaContratoFinal(RegistroDataModelImpl listaContratoFinal) {
		this.listaContratoFinal = listaContratoFinal;
	}
	/**
	 * Retorna la lista listaContratoInicio
	 * 
	 * @return listaContratoInicio
	 */
	public RegistroDataModelImpl getListaContratoInicio() {
		return listaContratoInicio;
	}
	/**
	 * Asigna la lista listaContratoInicio
	 * 
	 * @param listaContratoInicio
	 * Variable a asignar en  listaContratoInicio
	 */
	public void setListaContratoInicio(RegistroDataModelImpl listaContratoInicio) {
		this.listaContratoInicio = listaContratoInicio;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
