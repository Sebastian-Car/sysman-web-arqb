/*-
 * KardexPorProyectoControlador.java
 *
 * 1.0
 * 
 * 07/12/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

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

import com.sysman.almacen.enums.KardexPorProyectoControladorEnum;
import com.sysman.almacen.enums.KardexPorProyectoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
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
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 07/12/2022
 * @author avega
 */
@ManagedBean
@ViewScoped
public class KardexPorProyectoControlador extends BeanBaseModal {

	private final String compania;
	private final String cCodigoElemento;
	private final String codigoProyecto;
    private final String moduloAlmacen;
    //private String proyectoIni;
	

//<DECLARAR_ATRIBUTOS>

	private String elementoFinal;
	private String elementoinicial;
	private String proyectoinicial;
	private String proyectofinal;
	private String nombreElementoFinal;
	private String nombreElementoInicial;
	private Date fechaFinal;
	private Date fechaInicial;
	private String nombreproyectofin;
	private String nombreProyectoIni;
	private StreamedContent archivoDescarga;
    private String tipo;


//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * 
	 */
	private RegistroDataModelImpl listaCBElemFinal;
	/**
	 * 
	 */
	private RegistroDataModelImpl listaCBElemInicial;
	/**
	 * 
	 */
	private RegistroDataModelImpl listaCBProyectoInicial;
	
	/**
	 * 
	 */
	private RegistroDataModelImpl listaCBProyectoFinal;
	
	

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de KardexPorProyectoControlador
	 */
	public KardexPorProyectoControlador() {
		super();
		compania = SessionUtil.getCompania();
		moduloAlmacen = SessionUtil.getModulo();

		cCodigoElemento = "CODIGOELEMENTO";
		codigoProyecto = "CODIGO";


		try {
			numFormulario = GeneralCodigoFormaEnum.FR_TARJETA_KARDEX_POR_PROYECTO
                    .getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
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
		fechaInicial = new Date();
        fechaFinal = new Date();
		abrirFormulario();
		cargarListaCBElemInicial();
		cargarListaCBProyectoInicial();    
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCBElemFinal
	 *
	 */
	public void cargarListaCBElemFinal() {
	
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                KardexPorProyectoControladorUrlEnum.URL7809
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(KardexPorProyectoControladorEnum.TIPOELEMENTO.getValue(),
		                tipo);
		param.put(KardexPorProyectoControladorEnum.ELEMENTODESDE.getValue(),
				elementoinicial);
		listaCBElemFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, cCodigoElemento);
	
	}

	/**
	 * 
	 * Carga la lista listaCBElemInicial
	 *
	 */
		public void cargarListaCBElemInicial() {
			
			 tipo = "C";
			UrlBean urlBean = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(
	                                KardexPorProyectoControladorUrlEnum.URL11488
	                                                .getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			 param.put(KardexPorProyectoControladorEnum.TIPOELEMENTO.getValue(),
                     tipo);
			
			listaCBElemInicial = new RegistroDataModelImpl(urlBean.getUrl(),
	                urlBean.getUrlConteo().getUrl(), param,
	                true, cCodigoElemento);
		}
		
		/**
		 * 
		 * Carga la lista listaCBProyectoInicial
		 *
		 */
		public void cargarListaCBProyectoInicial() {

			UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                                    KardexPorProyectoControladorUrlEnum.URL32016
                                                    .getValue());
			    Map<String, Object> param = new TreeMap<>();
			    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			    
			    
			    listaCBProyectoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                    urlBean.getUrlConteo().getUrl(), param,
                    true, codigoProyecto);
			
		}

	/**
	 * 
	 * Carga la lista listaCBProyectoFinal
	 *
	 */
	public void cargarListaCBProyectoFinal() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                KardexPorProyectoControladorUrlEnum.URL32018
                                                .getValue());
		
		    Map<String, Object> param = new TreeMap<>();
		    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		    param.put(KardexPorProyectoControladorEnum.PROYECTOINI.getValue(),
		                    proyectoinicial);
		    
		    listaCBProyectoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, codigoProyecto);
	}

	
	

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BTDF en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirBTPDF() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generarInforme(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	
private void generarInforme(FORMATOS pdf) {
		// TODO Auto-generated method stub
	try {
		String reporte;
		reporte = "002417TARJETAKARDEXPORPROYECTO";
		Map<String, Object> reemplazos = new HashMap<>();
		
		
		reemplazos.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
		reemplazos.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal)
                .replace("00:00:00", "23:59:59"));
		reemplazos.put("elementoInicial", elementoinicial);
		reemplazos.put("elementoFinal", elementoFinal);
		reemplazos.put("proyectoini", proyectoinicial);
		reemplazos.put("proyectofin", proyectofinal);
		

		Map<String, Object> parametros = new HashMap<>();

		parametros.put("PR_PROYECTOINICIAL", nombreProyectoIni);
		parametros.put("PR_PROYECTOFINAL", nombreproyectofin);
		parametros.put("PR_ELEMENTOINIL", nombreElementoInicial);
		parametros.put("PR_ELEMENTOFIN", nombreElementoFinal);
		parametros.put("PR_CODIGOINI", elementoinicial);
		parametros.put("PR_CODIGOFIN", elementoFinal);

		Reporteador.resuelveConsulta(reporte, Integer.parseInt(moduloAlmacen), reemplazos, parametros);

		archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, pdf);

	} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}

	}

	//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control CPFechaFinal
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarCPFechaFinal() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control CPFechaInicial
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarCPFechaInicial() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCBElemFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCBElemFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal = SysmanFunciones
						.nvl(registroAux.getCampos().get(cCodigoElemento), "")
						.toString();
		nombreElementoFinal = SysmanFunciones
						.nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
						.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCBElemInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCBElemInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoinicial = SysmanFunciones
						  .nvl(registroAux.getCampos().get(cCodigoElemento), "")
						  .toString();
		nombreElementoInicial = SysmanFunciones.
							nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
							.toString();

		elementoFinal = null;
		nombreElementoFinal = null;
		cargarListaCBElemFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCBProyectoFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCBProyectoFinal(SelectEvent event) {
		
		Registro registroAux = (Registro) event.getObject();
		proyectofinal = SysmanFunciones
						  .nvl(registroAux.getCampos().get(codigoProyecto), "")
						  .toString();
		nombreproyectofin = SysmanFunciones.
							nvl(registroAux.getCampos().get("NOMBREPROYECTO"), "")
							.toString();

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCBProyectoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCBProyectoInicial(SelectEvent event) {
		
		Registro registroAux = (Registro) event.getObject();
		proyectoinicial = SysmanFunciones
						  .nvl(registroAux.getCampos().get(codigoProyecto), "")
						  .toString();
		nombreProyectoIni = SysmanFunciones.
							nvl(registroAux.getCampos().get("NOMBREPROYECTO"), "")
							.toString();

		proyectofinal = null;
		nombreproyectofin = null;
		cargarListaCBProyectoFinal();

	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable elementoFinal
	 * 
	 * @return elementoFinal
	 */
	public String getElementoFinal() {
		return elementoFinal;
	}

	/**
	 * Asigna la variable elementoFinal
	 * 
	 * @param elementoFinal Variable a asignar en elementoFinal
	 */
	public void setElementoFinal(String elementoFinal) {
		this.elementoFinal = elementoFinal;
	}

	/**
	 * Retorna la variable elementoinicial
	 * 
	 * @return elementoinicial
	 */
	public String getElementoinicial() {
		return elementoinicial;
	}

	/**
	 * Asigna la variable elementoinicial
	 * 
	 * @param elementoinicial Variable a asignar en elementoinicial
	 */
	public void setElementoinicial(String elementoinicial) {
		this.elementoinicial = elementoinicial;
	}

	/**
	 * Retorna la variable proyectofinal
	 * 
	 * @return proyectofinal
	 */
	public String getProyectofinal() {
		return proyectofinal;
	}

	/**
	 * Asigna la variable proyectofinal
	 * 
	 * @param proyectofinal Variable a asignar en proyectofinal
	 */
	public void setProyectofinal(String proyectofinal) {
		this.proyectofinal = proyectofinal;
	}
	

	public String getProyectoinicial() {
		return proyectoinicial;
	}

	public void setProyectoinicial(String proyectoinicial) {
		this.proyectoinicial = proyectoinicial;
	}

	/**
	 * Retorna la variable nombreElementoFinal
	 * 
	 * @return nombreElementoFinal
	 */
	public String getNombreElementoFinal() {
		return nombreElementoFinal;
	}

	/**
	 * Asigna la variable nombreElementoFinal
	 * 
	 * @param nombreElementoFinal Variable a asignar en nombreElementoFinal
	 */
	public void setNombreElementoFinal(String nombreElementoFinal) {
		this.nombreElementoFinal = nombreElementoFinal;
	}

	/**
	 * Retorna la variable nombreElementoInicial
	 * 
	 * @return nombreElementoInicial
	 */
	public String getNombreElementoInicial() {
		return nombreElementoInicial;
	}

	/**
	 * Asigna la variable nombreElementoInicial
	 * 
	 * @param nombreElementoInicial Variable a asignar en nombreElementoInicial
	 */
	public void setNombreElementoInicial(String nombreElementoInicial) {
		this.nombreElementoInicial = nombreElementoInicial;
	}

	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}

	/**
	 * Asigna la variable fechaFinal
	 * 
	 * @param fechaFinal Variable a asignar en fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * Asigna la variable fechaInicial
	 * 
	 * @param fechaInicial Variable a asignar en fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * Retorna la variable nombreproyectofin
	 * 
	 * @return nombreproyectofin
	 */
	public String getNombreproyectofin() {
		return nombreproyectofin;
	}

	/**
	 * Asigna la variable nombreproyectofin
	 * 
	 * @param nombreproyectofin Variable a asignar en nombreproyectofin
	 */
	public void setNombreproyectofin(String nombreproyectofin) {
		this.nombreproyectofin = nombreproyectofin;
	}

	/**
	 * Retorna la variable nombreProyectoIni
	 * 
	 * @return nombreProyectoIni
	 */
	public String getNombreProyectoIni() {
		return nombreProyectoIni;
	}

	/**
	 * Asigna la variable nombreProyectoIni
	 * 
	 * @param nombreProyectoIni Variable a asignar en nombreProyectoIni
	 */
	public void setNombreProyectoIni(String nombreProyectoIni) {
		this.nombreProyectoIni = nombreProyectoIni;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
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
	 * Retorna la lista listaCBElemFinal
	 * 
	 * @return listaCBElemFinal
	 */
	public RegistroDataModelImpl getListaCBElemFinal() {
		return listaCBElemFinal;
	}

	/**
	 * Asigna la lista listaCBElemFinal
	 * 
	 * @param listaCBElemFinal Variable a asignar en listaCBElemFinal
	 */
	public void setListaCBElemFinal(RegistroDataModelImpl listaCBElemFinal) {
		this.listaCBElemFinal = listaCBElemFinal;
	}

	/**
	 * Retorna la lista listaCBElemInicial
	 * 
	 * @return listaCBElemInicial
	 */
	public RegistroDataModelImpl getListaCBElemInicial() {
		return listaCBElemInicial;
	}

	/**
	 * Asigna la lista listaCBElemInicial
	 * 
	 * @param listaCBElemInicial Variable a asignar en listaCBElemInicial
	 */
	public void setListaCBElemInicial(RegistroDataModelImpl listaCBElemInicial) {
		this.listaCBElemInicial = listaCBElemInicial;
	}

	/**
	 * Retorna la lista listaCBProyectoFinal
	 * 
	 * @return listaCBProyectoFinal
	 */
	public RegistroDataModelImpl getListaCBProyectoFinal() {
		return listaCBProyectoFinal;
	}

	/**
	 * Asigna la lista listaCBProyectoFinal
	 * 
	 * @param listaCBProyectoFinal Variable a asignar en listaCBProyectoFinal
	 */
	public void setListaCBProyectoFinal(RegistroDataModelImpl listaCBProyectoFinal) {
		this.listaCBProyectoFinal = listaCBProyectoFinal;
	}

	/**
	 * Retorna la lista listaCBProyectoInicial
	 * 
	 * @return listaCBProyectoInicial
	 */
	public RegistroDataModelImpl getListaCBProyectoInicial() {
		return listaCBProyectoInicial;
	}

	/**
	 * Asigna la lista listaCBProyectoInicial
	 * 
	 * @param listaCBProyectoInicial Variable a asignar en listaCBProyectoInicial
	 */
	public void setListaCBProyectoInicial(RegistroDataModelImpl listaCBProyectoInicial) {
		this.listaCBProyectoInicial = listaCBProyectoInicial;
	}

	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
