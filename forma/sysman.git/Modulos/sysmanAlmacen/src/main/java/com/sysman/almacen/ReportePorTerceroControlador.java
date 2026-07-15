/*-
 * ReportePorTerceroControlador.java
 *
 * 1.0
 * 
 * 17/08/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.enums.ReportePorTerceroControladorUrlEnum;
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
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 17/08/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  ReportePorTerceroControlador extends BeanBaseModal{
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
	private String dependenciaIni;
	private String dependenciaFin;
	private Date fechaInicio;
	private Date fechaFinal;
	private String nomProyectoIni;
	private String nomProyectoFin;
	private String nomDependenciaIni;
	private String nomDependenciaFin;
	private boolean bloqueo;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;	
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaProyectoInicio;
	private RegistroDataModelImpl listaProyectoFinal;
	private RegistroDataModelImpl listaDependenciaInicio;
	private RegistroDataModelImpl listaDependenciaFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ReportePorTerceroControlador
	 */
	public ReportePorTerceroControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			//2421
			numFormulario = GeneralCodigoFormaEnum.REPORTE_POR_TERCERO_CONTROLADOR.getCodigo();
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
		cargarListaDependenciaInicio();
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
	public void cargarListaProyectoInicio() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReportePorTerceroControladorUrlEnum.URL32003.getValue()
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
				.getUrlServiceByUrlByEnumID(ReportePorTerceroControladorUrlEnum.URL32013.getValue());
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
	 * Carga la lista listaDependenciaInicio
	 *
	 */
	public void cargarListaDependenciaInicio(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReportePorTerceroControladorUrlEnum.URL62015.getValue()
						);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		listaDependenciaInicio = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaDependenciaFinal
	 *
	 */
	public void cargarListaDependenciaFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ReportePorTerceroControladorUrlEnum.URL62013.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put("CODIGOINICIAL",
				dependenciaIni);

		listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
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
		archivoDescarga = null;
		generaInforme(ReportesBean.FORMATOS.EXCEL);        
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	public void generaInforme(ReportesBean.FORMATOS formato) {

		archivoDescarga = null;
		String reporte = "002491ReportePorTerceros";
		try {
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("fechaIni",
					SysmanFunciones.formatearFecha(fechaInicio));
			reemplazar.put("fechaFin",
					SysmanFunciones.formatearFecha(fechaFinal));
			reemplazar.put("proyectoIni", proyectoIni);
			reemplazar.put("proyectoFin", proyectoFin);
			reemplazar.put("dependenciaIni", dependenciaIni);
			reemplazar.put("dependenciaFin", dependenciaFin);
			parametros.put("PR_FECHA_INI", SysmanFunciones.convertirAFechaCadena(fechaInicio));
			parametros.put("PR_FECHA_FIN", SysmanFunciones.convertirAFechaCadena(fechaFinal));

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

			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			bloqueo = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA PEPS CONSUMO DE ALMACEN IDIPRON", modulo, new Date(), false), "NO").equals("SI")? false:true;
			Object parametro = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania, "FECHA DE CORTE PARA INICIO DEL ALMACEN", modulo, new Date(), false), new Date());
			fechaInicio = formato.parse(parametro.toString());
			fechaFinal = new Date();
			dependenciaIni = "0";
			dependenciaFin = "999999999999";
			proyectoIni = "0";
			proyectoFin = "999999999999";

		} catch (SystemException | ParseException e) {
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
	 * listaDependenciaInicio
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDependenciaInicio(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaIni= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		nomDependenciaIni = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
		dependenciaFin = null;
		nomDependenciaFin = null;
		cargarListaDependenciaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDependenciaFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDependenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaFin= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		nomDependenciaFin = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
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
	 * Retorna la variable dependenciaIni
	 * 
	 * @return  dependenciaIni
	 */
	public String getDependenciaIni() {
		return dependenciaIni;
	}
	/**
	 * Asigna la variable  dependenciaIni
	 * 
	 * @param  dependenciaIni
	 * Variable a asignar en  dependenciaIni
	 */
	public void setDependenciaIni(String dependenciaIni) {
		this.dependenciaIni = dependenciaIni;
	}
	/**
	 * Retorna la variable dependenciaFin
	 * 
	 * @return  dependenciaFin
	 */
	public String getDependenciaFin() {
		return dependenciaFin;
	}
	/**
	 * Asigna la variable  dependenciaFin
	 * 
	 * @param  dependenciaFin
	 * Variable a asignar en  dependenciaFin
	 */
	public void setDependenciaFin(String dependenciaFin) {
		this.dependenciaFin = dependenciaFin;
	}
	/**
	 * Retorna la variable fechaInicio
	 * 
	 * @return  fechaInicio
	 */
	public Date getFechaInicio() {
		return fechaInicio;
	}
	/**
	 * Asigna la variable  fechaInicio
	 * 
	 * @param  fechaInicio
	 * Variable a asignar en  fechaInicio
	 */
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
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
	 * Retorna la variable nomDependenciaIni
	 * 
	 * @return  nomDependenciaIni
	 */
	public String getNomDependenciaIni() {
		return nomDependenciaIni;
	}
	/**
	 * Asigna la variable  nomDependenciaIni
	 * 
	 * @param  nomDependenciaIni
	 * Variable a asignar en  nomDependenciaIni
	 */
	public void setNomDependenciaIni(String nomDependenciaIni) {
		this.nomDependenciaIni = nomDependenciaIni;
	}
	/**
	 * Retorna la variable nomDependenciaFin
	 * 
	 * @return  nomDependenciaFin
	 */
	public String getNomDependenciaFin() {
		return nomDependenciaFin;
	}
	/**
	 * Asigna la variable  nomDependenciaFin
	 * 
	 * @param  nomDependenciaFin
	 * Variable a asignar en  nomDependenciaFin
	 */
	public void setNomDependenciaFin(String nomDependenciaFin) {
		this.nomDependenciaFin = nomDependenciaFin;
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
	 * Retorna la lista listaDependenciaInicio
	 * 
	 * @return listaDependenciaInicio
	 */
	public RegistroDataModelImpl getListaDependenciaInicio() {
		return listaDependenciaInicio;
	}
	/**
	 * Asigna la lista listaDependenciaInicio
	 * 
	 * @param listaDependenciaInicio
	 * Variable a asignar en  listaDependenciaInicio
	 */
	public void setListaDependenciaInicio(RegistroDataModelImpl listaDependenciaInicio) {
		this.listaDependenciaInicio = listaDependenciaInicio;
	}
	/**
	 * Retorna la lista listaDependenciaFinal
	 * 
	 * @return listaDependenciaFinal
	 */
	public RegistroDataModelImpl getListaDependenciaFinal() {
		return listaDependenciaFinal;
	}
	/**
	 * Asigna la lista listaDependenciaFinal
	 * 
	 * @param listaDependenciaFinal
	 * Variable a asignar en  listaDependenciaFinal
	 */
	public void setListaDependenciaFinal(RegistroDataModelImpl listaDependenciaFinal) {
		this.listaDependenciaFinal = listaDependenciaFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
