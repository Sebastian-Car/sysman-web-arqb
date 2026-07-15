/*-
 * Frinformecuentasmedicas1Controlador.java
 *
 * 1.0
 * 
 * 18/08/2022
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
 * @version 1.0, 18/08/2022
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class Frinformecuentasmedicas1Controlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String claseCuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String claseCuentaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaClaseCuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaClaseCuentaFinal;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de Frinformecuentasmedicas1Controlador
	 */
	public Frinformecuentasmedicas1Controlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			//2363
			numFormulario = GeneralCodigoFormaEnum.FRM_INFORME_CUENTAS_MEDICAS_CONTROLADOR.getCodigo();
			validarPermisos();

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		cargarListaClaseCuentaInicial();
		cargarListaClaseCuentaFinal();

		abrirFormulario();
		
		try {
            fechaInicial=SysmanFunciones.primeroDeMesFecha(new Date());
            fechaFinal= SysmanFunciones.ultimoDiaDate(new Date());
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            
        }
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		claseCuentaInicial = null;
		claseCuentaFinal = null;
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaClaseCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
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
		
		listaClaseCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		                		urlBean.getUrlConteo().getUrl(), param,
		                		true, "CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaClaseCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
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
		
		listaClaseCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
		                	  urlBean.getUrlConteo().getUrl(), param,
		                	  true, "CODIGO");
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
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
			
			informes.add(Reporteador.resuelveConsulta("800539InformeAuditoriasCuentasMedicasFT024",
                    Integer.parseInt(SessionUtil.getModulo()),
                    reemplazos));
			hojas.add("FT024");
			
			archivoDescarga= JsfUtil.exportarHojaDatosStreamed(informes.get(0), ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL, hojas.get(0));
			
			
		} catch (JRException | IOException | ParseException | SQLException | DRException |com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
		    logger.error(e.getMessage(), e);
		    JsfUtil.agregarMensajeError(e.getMessage());
			}  
	}
		
	

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control FechaInicial
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarFechaInicial() 
	{
		if(fechaFinal==null)
            fechaFinal= fechaInicial;
		claseCuentaInicial = null;
        claseCuentaFinal = null;
        cargarListaClaseCuentaInicial();
        cargarListaClaseCuentaFinal();
        
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaClaseCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaClaseCuentaInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
        claseCuentaInicial = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaClaseCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaClaseCuentaFinal(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
        claseCuentaFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        " ")
                        .toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ClaseCuentaInicial
	 * 
	 * @return ClaseCuentaInicial
	 */
	public String getClaseCuentaInicial() {
		return claseCuentaInicial;
	}

	/**
	 * Asigna la variable ClaseCuentaInicial
	 * 
	 * @param ClaseCuentaInicial Variable a asignar en ClaseCuentaInicial
	 */
	public void setClaseCuentaInicial(String claseCuentaInicial) {
		this.claseCuentaInicial = claseCuentaInicial;
	}

	/**
	 * Retorna la variable ClaseCuentaFinal
	 * 
	 * @return ClaseCuentaFinal
	 */
	public String getClaseCuentaFinal() {
		return claseCuentaFinal;
	}

	/**
	 * Asigna la variable ClaseCuentaFinal
	 * 
	 * @param ClaseCuentaFinal Variable a asignar en ClaseCuentaFinal
	 */
	public void setClaseCuentaFinal(String claseCuentaFinal) {
		this.claseCuentaFinal = claseCuentaFinal;
	}

	/**
	 * Retorna la variable FechaInicial
	 * 
	 * @return FechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * Asigna la variable FechaInicial
	 * 
	 * @param FechaInicial Variable a asignar en FechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * Retorna la variable FechaFinal
	 * 
	 * @return FechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * Asigna la variable FechaFinal
	 * 
	 * @param FechaFinal Variable a asignar en FechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @return the listaClaseCuentaInicial
	 */
	public RegistroDataModelImpl getListaClaseCuentaInicial() {
		return listaClaseCuentaInicial;
	}

	/**
	 * @param listaClaseCuentaInicial the listaClaseCuentaInicial to set
	 */
	public void setListaClaseCuentaInicial(RegistroDataModelImpl listaClaseCuentaInicial) {
		this.listaClaseCuentaInicial = listaClaseCuentaInicial;
	}

	/**
	 * @return the listaClaseCuentaFinal
	 */
	public RegistroDataModelImpl getListaClaseCuentaFinal() {
		return listaClaseCuentaFinal;
	}

	/**
	 * @param listaClaseCuentaFinal the listaClaseCuentaFinal to set
	 */
	public void setListaClaseCuentaFinal(RegistroDataModelImpl listaClaseCuentaFinal) {
		this.listaClaseCuentaFinal = listaClaseCuentaFinal;
	}

	

}
