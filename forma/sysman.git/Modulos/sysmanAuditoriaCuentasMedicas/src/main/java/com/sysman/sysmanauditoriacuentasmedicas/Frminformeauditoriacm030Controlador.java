/*-
 * Frminformeauditoriacm030Controlador.java
 *
 * 1.0
 * 
 * 01/09/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sysmanauditoriacuentasmedicas.enums.FrmImportarRipsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/09/2022
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class Frminformeauditoriacm030Controlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	
	
	private String claseCuentaInicial;
	
	private String claseCuentaFinal;
	
	private Date fechaInicial;
	
	private Date fechaFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	
	private RegistroDataModelImpl listaclaseCuentaInicial;
	
	private RegistroDataModelImpl listaclaseCuentaFinal;

	/**
	 * Crea una nueva instancia de Frminformeauditoriacm030Controlador
	 */
	public Frminformeauditoriacm030Controlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2364
			numFormulario = GeneralCodigoFormaEnum.FRM_INFORME_AUDITORIA_CM_030.getCodigo();
			validarPermisos();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() 
	{
		cargarListaClaseCuentaInicial();
		cargarListaClaseCuentaFinal();

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
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() 
	{
		claseCuentaInicial = null;
		claseCuentaFinal = null;
	}
	
	/**
	 * 
	 * Carga la lista listaclaseCuentaInicial
	 *
	 */
	public void cargarListaClaseCuentaInicial() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                FrmImportarRipsControladorUrlEnum.URL4395
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
	 * Carga la lista listaclaseCuentaFinal
	 *
	 */
	public void cargarListaClaseCuentaFinal() 
	{
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                FrmImportarRipsControladorUrlEnum.URL4395
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);
		
		listaclaseCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
		                	  urlBean.getUrlConteo().getUrl(), param,
		                	  true, "CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 *
	 */
	public void oprimirExcel() 
	{ 
		archivoDescarga = null;	
		
		ArrayList<String> informes=new ArrayList<String>();
        ArrayList<String> hojas=new ArrayList<String>();
		
	    try {
	    	Map<String, Object> reemplazos = new TreeMap<>();
	    	
	    	reemplazos.put("ClaseCuentaInicial", claseCuentaInicial);
		    reemplazos.put("ClaseCuentaFinal",claseCuentaFinal);
			reemplazos.put("FechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazos.put("FechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
			
			informes.add(Reporteador.resuelveConsulta("800540InformeAuditoriasCuentasMedicasFT030",
                    Integer.parseInt(SessionUtil.getModulo()),
                    reemplazos));
			hojas.add("FT030");
			
			archivoDescarga= JsfUtil.exportarHojaDatosStreamed(informes.get(0), ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL, hojas.get(0));
			
			
		} catch (JRException | IOException | ParseException | SQLException | DRException |com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
		    logger.error(e.getMessage(), e);
		    JsfUtil.agregarMensajeError(e.getMessage());
			}  
	}

	/**
	 * Metodo ejecutado al cambiar el control fechaInicial
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarfechaInicial() 
	{
		if(fechaFinal==null)
            fechaFinal= fechaInicial;        
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaclaseCuentaInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaclaseCuentaInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
        claseCuentaInicial = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaclaseCuentaFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaclaseCuentaFinal(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
        claseCuentaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
	}
	
	/**
	 * Retorna la variable claseCuentaInicial
	 * 
	 * @return claseCuentaInicial
	 */
	public String getClaseCuentaInicial() {
		return claseCuentaInicial;
	}

	/**
	 * Asigna la variable claseCuentaInicial
	 * 
	 * @param claseCuentaInicial Variable a asignar en claseCuentaInicial
	 */
	public void setClaseCuentaInicial(String claseCuentaInicial) {
		this.claseCuentaInicial = claseCuentaInicial;
	}

	/**
	 * Retorna la variable claseCuentaFinal
	 * 
	 * @return claseCuentaFinal
	 */
	public String getClaseCuentaFinal() {
		return claseCuentaFinal;
	}

	/**
	 * Asigna la variable claseCuentaFinal
	 * 
	 * @param claseCuentaFinal Variable a asignar en claseCuentaFinal
	 */
	public void setClaseCuentaFinal(String claseCuentaFinal) {
		this.claseCuentaFinal = claseCuentaFinal;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @return the listaclaseCuentaInicial
	 */
	public RegistroDataModelImpl getListaclaseCuentaInicial() {
		return listaclaseCuentaInicial;
	}

	/**
	 * @param listaclaseCuentaInicial the listaclaseCuentaInicial to set
	 */
	public void setListaclaseCuentaInicial(RegistroDataModelImpl listaclaseCuentaInicial) {
		this.listaclaseCuentaInicial = listaclaseCuentaInicial;
	}

	/**
	 * @return the listaclaseCuentaFinal
	 */
	public RegistroDataModelImpl getListaclaseCuentaFinal() {
		return listaclaseCuentaFinal;
	}

	/**
	 * @param listaclaseCuentaFinal the listaclaseCuentaFinal to set
	 */
	public void setListaclaseCuentaFinal(RegistroDataModelImpl listaclaseCuentaFinal) {
		this.listaclaseCuentaFinal = listaclaseCuentaFinal;
	}

	/**
	 * @return the fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
}
