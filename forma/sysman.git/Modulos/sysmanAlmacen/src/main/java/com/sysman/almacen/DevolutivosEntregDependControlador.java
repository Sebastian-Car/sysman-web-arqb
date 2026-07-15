/*-
 * DevolutivosEntregDependControlador.java
 *
 * 1.0
 * 
 * 18/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.DevolutivosEntregDependControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import  com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 *Formulario que permite imprimir el reporte de devoluciones de entrega por dependencia
 *
 * @version 1.0, 18/10/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  DevolutivosEntregDependControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	
	private final String modulo ;
	private  String reporte ;
	private  String digitos ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * variable que almacena la codigo inicial
	 */
	private String codigoInicial;
	/**
	 * Variable que almacena el codigo Final
	 */
	private String codigoFinal;
	/**
	 * Variable almacena nombre del codigo Inicial
	 */
	private String nombreCodigoInicial;
	/**
	 * Variable almacena nombre del codigo Final
	 */
	private String nombreCodigoFinal;
	/**
	 * variable que almacena la fecha inicial
	 */
	private Date fechaEntradaInicial;
	/**
	 * variable que almacena la fecha final
	 */
	private Date fechaEntradaFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	
	private RegistroDataModelImpl listaCodigoInicial;
	
	private RegistroDataModelImpl listaCodigoFinal;
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	public DevolutivosEntregDependControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOS_ENTREG_DEPEND_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
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
		fechaEntradaInicial = new Date();
		fechaEntradaFinal = new Date();
		try {
			digitos = ejbSysmanUtil.consultarParametro(compania,"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),false);
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		cargarListaCodigoInicial(); 
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
	 * Carga la lista listaCodigoInicial
	 *
	 */
	public void cargarListaCodigoInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosEntregDependControladorUrlEnum.URL2472.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,"CODIGO");

	}
	/**
	 * 
	 * Carga la lista listaCodigoFinal
	 *
	 */
	public void cargarListaCodigoFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosEntregDependControladorUrlEnum.URL2473.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CODIGOINICIAL", codigoInicial);
		 listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF
	 * en la vista
	 *
	 *
	 */
	public void oprimirPDF() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		generarReporte(ReportesBean.FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		generarReporte(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	
	private void generarReporte(FORMATOS formato) {
		try{
			reporte="001930DevolutivosEntregadosporDependencia"; 
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("codigoInicial",codigoInicial);
			reemplazar.put("codigoFinal",codigoFinal);
			reemplazar.put("fechaEntradaInicial",SysmanFunciones.formatearFecha(fechaEntradaInicial));
			reemplazar.put("fechaEntradaFinal",SysmanFunciones.formatearFecha(fechaEntradaFinal));
			reemplazar.put("digitos",digitos);
			reemplazar.put("compania",compania);

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),reemplazar, parametros);    
			
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoInicial
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoInicial= SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"),"").toString();
		nombreCodigoInicial= registroAux.getCampos().get("NOMBRE").toString();
		codigoFinal = null;
		nombreCodigoFinal = null;
		cargarListaCodigoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoFinal
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoFinal= SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"),"").toString();
		nombreCodigoFinal= registroAux.getCampos().get("NOMBRE").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable codigoInicial
	 * 
	 * @return  codigoInicial
	 */
	public String getCodigoInicial() {
		return codigoInicial;
	}
	/**
	 * Asigna la variable  codigoInicial
	 * 
	 * @param  codigoInicial
	 * Variable a asignar en  codigoInicial
	 */
	public void setCodigoInicial(String codigoInicial) {
		this.codigoInicial = codigoInicial;
	}
	/**
	 * Retorna la variable codigoFinal
	 * 
	 * @return  codigoFinal
	 */
	public String getCodigoFinal() {
		return codigoFinal;
	}
	/**
	 * Asigna la variable  codigoFinal
	 * 
	 * @param  codigoFinal
	 * Variable a asignar en  codigoFinal
	 */
	public void setCodigoFinal(String codigoFinal) {
		this.codigoFinal = codigoFinal;
	}
	/**
	 * Retorna la variable nombreCodigoInicial
	 * 
	 * @return  nombreCodigoInicial
	 */
	public String getNombreCodigoInicial() {
		return nombreCodigoInicial;
	}
	/**
	 * Asigna la variable  nombreCodigoInicial
	 * 
	 * @param  nombreCodigoInicial
	 * Variable a asignar en  nombreCodigoInicial
	 */
	public void setNombreCodigoInicial(String nombreCodigoInicial) {
		this.nombreCodigoInicial = nombreCodigoInicial;
	}
	/**
	 * Retorna la variable nombreCodigoFinal
	 * 
	 * @return  nombreCodigoFinal
	 */
	public String getNombreCodigoFinal() {
		return nombreCodigoFinal;
	}
	/**
	 * Asigna la variable  nombreCodigoFinal
	 * 
	 * @param  nombreCodigoFinal
	 * Variable a asignar en  nombreCodigoFinal
	 */
	public void setNombreCodigoFinal(String nombreCodigoFinal) {
		this.nombreCodigoFinal = nombreCodigoFinal;
	}
	/**
	 * Retorna la variable fechaEntradaInicial
	 * 
	 * @return  fechaEntradaInicial
	 */
	public Date getFechaEntradaInicial() {
		return fechaEntradaInicial;
	}
	/**
	 * Asigna la variable  fechaEntradaInicial
	 * 
	 * @param  fechaEntradaInicial
	 * Variable a asignar en  fechaEntradaInicial
	 */
	public void setFechaEntradaInicial(Date fechaEntradaInicial) {
		this.fechaEntradaInicial = fechaEntradaInicial;
	}
	/**
	 * Retorna la variable fechaEntradaFinal
	 * 
	 * @return  fechaEntradaFinal
	 */
	public Date getFechaEntradaFinal() {
		return fechaEntradaFinal;
	}
	/**
	 * Asigna la variable  fechaEntradaFinal
	 * 
	 * @param  fechaEntradaFinal
	 * Variable a asignar en  fechaEntradaFinal
	 */
	public void setFechaEntradaFinal(Date fechaEntradaFinal) {
		this.fechaEntradaFinal = fechaEntradaFinal;
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
	 * Retorna la lista listaCodigoInicial
	 * 
	 * @return listaCodigoInicial
	 */
	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	public String getCompania() {
		return compania;
	}
	public RegistroDataModelImpl getListaCodigoInicial() {
		return listaCodigoInicial;
	}
	public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial) {
		this.listaCodigoInicial = listaCodigoInicial;
	}
	public RegistroDataModelImpl getListaCodigoFinal() {
		return listaCodigoFinal;
	}
	public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
		this.listaCodigoFinal = listaCodigoFinal;
	}
	public String getModulo() {
		return modulo;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public String getReporte() {
		return reporte;
	}
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}
	public String getDigitos() {
		return digitos;
	}
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}
	public EjbSysmanUtilRemote getEjbSysmanUtil() {
		return ejbSysmanUtil;
	}
	public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil) {
		this.ejbSysmanUtil = ejbSysmanUtil;
	}
	
}
