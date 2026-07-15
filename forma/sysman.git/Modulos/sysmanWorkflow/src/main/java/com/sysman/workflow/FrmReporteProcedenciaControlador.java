/*-
 * FrmReporteProcedenciaControlador.java
 *
 * 1.0
 * 
 * 25/05/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmReporteProcedenciaControladorUrlEnum;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 25/05/2023
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmReporteProcedenciaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private boolean ckSinRespuesta;
	private boolean ckConRespuesta;
	private boolean cktodos;
	private String terceroInicial;
	private String terceroFinal;
	private String procesoInicial;
	private String procesoFinal;
	private String terceroNomIni;
	private String terceroNomFin;
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
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaTerceroInicial;
	private RegistroDataModelImpl listaTerceroFinal;
	private RegistroDataModelImpl listaProcesoInicial;
	private RegistroDataModelImpl listaProcesoFinal;
	private String modulo;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmReporteProcedenciaControlador
	 */
	public FrmReporteProcedenciaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2404;
			validarPermisos();
			cktodos = true;
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
		cargarListaTerceroInicial(); 
		cargarListaProcesoInicial(); 
		
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
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaTerceroInicial
	 *
	 */
	public void cargarListaTerceroInicial(){
		
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    
	    UrlBean urlBean = UrlServiceUtil.getInstance()
	        .getUrlServiceByUrlByEnumID(FrmReporteProcedenciaControladorUrlEnum.URL1040008
	            .getValue());
	    
	    listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
	        urlBean.getUrlConteo().getUrl(), param, true, "DOCUMENTO");
	    
	}
	/**
	 * 
	 * Carga la lista listaTerceroFinal
	 *
	 */
	public void cargarListaTerceroFinal(){
		
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.NUMERO.getName(), terceroInicial);
	    
	    UrlBean urlBean = UrlServiceUtil.getInstance()
	        .getUrlServiceByUrlByEnumID(FrmReporteProcedenciaControladorUrlEnum.URL1040010
	            .getValue());
	    
	    listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	        urlBean.getUrlConteo().getUrl(), param, true, "DOCUMENTO");
	    
	}
	/**
	 * 
	 * Carga la lista listaProcesoInicial
	 *
	 */
	public void cargarListaProcesoInicial(){
		
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    
	    UrlBean urlBean = UrlServiceUtil.getInstance()
	        .getUrlServiceByUrlByEnumID(FrmReporteProcedenciaControladorUrlEnum.URL988010
	            .getValue());
	    
	    listaProcesoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
	        urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	    
	}
	/**
	 * 
	 * Carga la lista listaProcesoFinal
	 *
	 */
	public void cargarListaProcesoFinal(){
		
		Map<String, Object> param = new TreeMap<>();
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    param.put(GeneralParameterEnum.NUMERO.getName(), procesoInicial);
	    
	    UrlBean urlBean = UrlServiceUtil.getInstance()
	        .getUrlServiceByUrlByEnumID(FrmReporteProcedenciaControladorUrlEnum.URL988010
	            .getValue());
	    
	    listaProcesoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	        urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	    
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
		
		try {
		archivoDescarga=null;    
		
		Map<String, Object> reemplazos = new HashMap<>();
		Map<String, Object> parametros = new HashMap<>();
		reemplazos.put("terceroInicial", terceroInicial);
		reemplazos.put("terceroFinal", terceroFinal);
		reemplazos.put("procesoInicial", procesoInicial);
		reemplazos.put("procesoFinal", procesoFinal);
		
		
		if(ckConRespuesta) {
			reemplazos.put("filtro", "AND N.ENVIO_CORRES NOT IN (0)");
		}else if(ckSinRespuesta) {
			reemplazos.put("filtro", "AND N.ENVIO_CORRES IN (0)");
		}else if(cktodos) {
			reemplazos.put("filtro", "");
		}
		
		String reporte = "002468ReporteProcedencia";
		
		Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazos, parametros);

        
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
			                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			 logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
		}

		//</CODIGO_DESARROLLADO>
	}
	
	
	
	
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	 /**
     * Metodo ejecutado al cambiar el control SinRespuesta
     * 
     * 
     */
public void cambiarSinRespuesta() {
         //<CODIGO_DESARROLLADO>
	ckSinRespuesta = true;
	ckConRespuesta = false;
	cktodos = false;
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control ConRespuesta
     * 
     * 
     */
public void cambiarConRespuesta() {
         //<CODIGO_DESARROLLADO>
	ckConRespuesta = true;
	ckSinRespuesta = false;
	cktodos = false;
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control Todos
     * 
     * 
     */
public void cambiarTodos() {
         //<CODIGO_DESARROLLADO>
	cktodos = true;
	ckConRespuesta = false;
	ckSinRespuesta = false;
        //</CODIGO_DESARROLLADO>
    }
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTerceroInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroInicial = SysmanFunciones.toString(registroAux.getCampos().get("DOCUMENTO"));
		terceroNomIni = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
		terceroFinal = null;
		terceroNomFin = null;
		cargarListaTerceroFinal(); 
		
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTerceroFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
	    terceroFinal= SysmanFunciones.toString(registroAux.getCampos().get("DOCUMENTO"));
	    terceroNomFin= SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProcesoInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProcesoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
	    procesoInicial= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	    procesoFinal = null;
	    cargarListaProcesoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProcesoFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProcesoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		procesoFinal= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ckSinRespuesta
	 * 
	 * @return  ckSinRespuesta
	 */
	public boolean getCkSinRespuesta() {
		return ckSinRespuesta;
	}
	/**
	 * Asigna la variable  ckSinRespuesta
	 * 
	 * @param  ckSinRespuesta
	 * Variable a asignar en  ckSinRespuesta
	 */
	public void setCkSinRespuesta(boolean ckSinRespuesta) {
		this.ckSinRespuesta = ckSinRespuesta;
	}
	/**
	 * Retorna la variable ckConRespuesta
	 * 
	 * @return  ckConRespuesta
	 */
	public boolean getCkConRespuesta() {
		return ckConRespuesta;
	}
	/**
	 * Asigna la variable  ckConRespuesta
	 * 
	 * @param  ckConRespuesta
	 * Variable a asignar en  ckConRespuesta
	 */
	public void setCkConRespuesta(boolean ckConRespuesta) {
		this.ckConRespuesta = ckConRespuesta;
	}
	/**
	 * Retorna la variable cktodos
	 * 
	 * @return  cktodos
	 */
	public boolean getCktodos() {
		return cktodos;
	}
	/**
	 * Asigna la variable  cktodos
	 * 
	 * @param  cktodos
	 * Variable a asignar en  cktodos
	 */
	public void setCktodos(boolean cktodos) {
		this.cktodos = cktodos;
	}
	/**
	 * Retorna la variable terceroInicial
	 * 
	 * @return  terceroInicial
	 */
	public String getTerceroInicial() {
		return terceroInicial;
	}
	/**
	 * Asigna la variable  terceroInicial
	 * 
	 * @param  terceroInicial
	 * Variable a asignar en  terceroInicial
	 */
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}
	/**
	 * Retorna la variable terceroFinal
	 * 
	 * @return  terceroFinal
	 */
	public String getTerceroFinal() {
		return terceroFinal;
	}
	/**
	 * Asigna la variable  terceroFinal
	 * 
	 * @param  terceroFinal
	 * Variable a asignar en  terceroFinal
	 */
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}
	/**
	 * Retorna la variable procesoInicial
	 * 
	 * @return  procesoInicial
	 */
	public String getProcesoInicial() {
		return procesoInicial;
	}
	/**
	 * Asigna la variable  procesoInicial
	 * 
	 * @param  procesoInicial
	 * Variable a asignar en  procesoInicial
	 */
	public void setProcesoInicial(String procesoInicial) {
		this.procesoInicial = procesoInicial;
	}
	/**
	 * Retorna la variable procesoFinal
	 * 
	 * @return  procesoFinal
	 */
	public String getProcesoFinal() {
		return procesoFinal;
	}
	/**
	 * Asigna la variable  procesoFinal
	 * 
	 * @param  procesoFinal
	 * Variable a asignar en  procesoFinal
	 */
	public void setProcesoFinal(String procesoFinal) {
		this.procesoFinal = procesoFinal;
	}
	/**
	 * Retorna la variable terceroNomIni
	 * 
	 * @return  terceroNomIni
	 */
	public String getTerceroNomIni() {
		return terceroNomIni;
	}
	/**
	 * Asigna la variable  terceroNomIni
	 * 
	 * @param  terceroNomIni
	 * Variable a asignar en  terceroNomIni
	 */
	public void setTerceroNomIni(String terceroNomIni) {
		this.terceroNomIni = terceroNomIni;
	}
	/**
	 * Retorna la variable terceroNomFin
	 * 
	 * @return  terceroNomFin
	 */
	public String getTerceroNomFin() {
		return terceroNomFin;
	}
	/**
	 * Asigna la variable  terceroNomFin
	 * 
	 * @param  terceroNomFin
	 * Variable a asignar en  terceroNomFin
	 */
	public void setTerceroNomFin(String terceroNomFin) {
		this.terceroNomFin = terceroNomFin;
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
	 * Retorna la lista listaTerceroInicial
	 * 
	 * @return listaTerceroInicial
	 */
	public RegistroDataModelImpl getListaTerceroInicial() {
		return listaTerceroInicial;
	}
	/**
	 * Asigna la lista listaTerceroInicial
	 * 
	 * @param listaTerceroInicial
	 * Variable a asignar en  listaTerceroInicial
	 */
	public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
		this.listaTerceroInicial = listaTerceroInicial;
	}
	/**
	 * Retorna la lista listaTerceroFinal
	 * 
	 * @return listaTerceroFinal
	 */
	public RegistroDataModelImpl getListaTerceroFinal() {
		return listaTerceroFinal;
	}
	/**
	 * Asigna la lista listaTerceroFinal
	 * 
	 * @param listaTerceroFinal
	 * Variable a asignar en  listaTerceroFinal
	 */
	public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
		this.listaTerceroFinal = listaTerceroFinal;
	}
	/**
	 * Retorna la lista listaProcesoInicial
	 * 
	 * @return listaProcesoInicial
	 */
	public RegistroDataModelImpl getListaProcesoInicial() {
		return listaProcesoInicial;
	}
	/**
	 * Asigna la lista listaProcesoInicial
	 * 
	 * @param listaProcesoInicial
	 * Variable a asignar en  listaProcesoInicial
	 */
	public void setListaProcesoInicial(RegistroDataModelImpl listaProcesoInicial) {
		this.listaProcesoInicial = listaProcesoInicial;
	}
	/**
	 * Retorna la lista listaProcesoFinal
	 * 
	 * @return listaProcesoFinal
	 */
	public RegistroDataModelImpl getListaProcesoFinal() {
		return listaProcesoFinal;
	}
	/**
	 * Asigna la lista listaProcesoFinal
	 * 
	 * @param listaProcesoFinal
	 * Variable a asignar en  listaProcesoFinal
	 */
	public void setListaProcesoFinal(RegistroDataModelImpl listaProcesoFinal) {
		this.listaProcesoFinal = listaProcesoFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
