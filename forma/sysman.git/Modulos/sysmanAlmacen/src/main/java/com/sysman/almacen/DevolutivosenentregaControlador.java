/*-
 * DevolutivosenentregaControlador.java
 *
 * 1.0
 * 
 * 24/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.DevolutivosenentregaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import net.sf.jasperreports.engine.JRException;
/**
 * Formulario que permite imprimir reporte de los devolutivos recibidos en comdato
 *
 * @version 1.0, 24/10/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  DevolutivosenentregaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo ;
	private  String reporte ;
	//<DECLARAR_ATRIBUTOS>

	private String elementoInicial;

	private String elementoFinal;

	private String elementoInicialNombre;

	private String elementoFinalNombre;

	private StreamedContent archivoDescarga;

	private RegistroDataModelImpl listacmbElementoDesde;

	private RegistroDataModelImpl listacmbElementoHasta;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	public DevolutivosenentregaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOS_ENENTREGA_CONTROLADOR .getCodigo();
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
		cargarListacmbElementoDesde();

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
	 * Carga la lista listacmbElementoDesde
	 *
	 */
	public void cargarListacmbElementoDesde(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosenentregaControladorUrlEnum.URL001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGOELEMENTO");
	}
	/**
	 * 
	 * Carga la lista listacmbElementoHasta
	 *
	 */
	public void cargarListacmbElementoHasta(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosenentregaControladorUrlEnum.URL002.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ELEMENTO", elementoInicial);
		listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGOELEMENTO");
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
		mtdGenerarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
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
		archivoDescarga=null;   
		mtdGenerarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}

	public void mtdGenerarInforme(ReportesBean.FORMATOS formato)  
	{
		try{
			reporte= "001945devolutivosenentrega";
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("elementoInicial",elementoInicial);
			reemplazar.put("elementoFinal",elementoFinal);
			reemplazar.put("compania",compania);
			
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),reemplazar, parametros);  
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (JRException | IOException | SysmanException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacmbElementoDesde
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoInicial = registroAux.getCampos().get("CODIGOELEMENTO").toString();
		elementoInicialNombre = registroAux.getCampos().get("NOMBRELARGO").toString();
		elementoFinal = null;
		elementoFinalNombre = null;
		cargarListacmbElementoHasta();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacmbElementoHasta
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacmbElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal= registroAux.getCampos().get("CODIGOELEMENTO").toString();
		elementoFinalNombre = registroAux.getCampos().get("NOMBRELARGO").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable elementoInicial
	 * 
	 * @return  elementoInicial
	 */
	public String getElementoInicial() {
		return elementoInicial;
	}
	/**
	 * Asigna la variable  elementoInicial
	 * 
	 * @param  elementoInicial
	 * Variable a asignar en  elementoInicial
	 */
	public void setElementoInicial(String elementoInicial) {
		this.elementoInicial = elementoInicial;
	}
	/**
	 * Retorna la variable elementoFinal
	 * 
	 * @return  elementoFinal
	 */
	public String getElementoFinal() {
		return elementoFinal;
	}
	/**
	 * Asigna la variable  elementoFinal
	 * 
	 * @param  elementoFinal
	 * Variable a asignar en  elementoFinal
	 */
	public void setElementoFinal(String elementoFinal) {
		this.elementoFinal = elementoFinal;
	}
	/**
	 * Retorna la variable elementoInicialNombre
	 * 
	 * @return  elementoInicialNombre
	 */
	public String getElementoInicialNombre() {
		return elementoInicialNombre;
	}
	/**
	 * Asigna la variable  elementoInicialNombre
	 * 
	 * @param  elementoInicialNombre
	 * Variable a asignar en  elementoInicialNombre
	 */
	public void setElementoInicialNombre(String elementoInicialNombre) {
		this.elementoInicialNombre = elementoInicialNombre;
	}
	/**
	 * Retorna la variable elementoFinalNombre
	 * 
	 * @return  elementoFinalNombre
	 */
	public String getElementoFinalNombre() {
		return elementoFinalNombre;
	}
	/**
	 * Asigna la variable  elementoFinalNombre
	 * 
	 * @param  elementoFinalNombre
	 * Variable a asignar en  elementoFinalNombre
	 */
	public void setElementoFinalNombre(String elementoFinalNombre) {
		this.elementoFinalNombre = elementoFinalNombre;
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
	 * Retorna la lista listacmbElementoDesde
	 * 
	 * @return listacmbElementoDesde
	 */
	public RegistroDataModelImpl getListacmbElementoDesde() {
		return listacmbElementoDesde;
	}
	/**
	 * Asigna la lista listacmbElementoDesde
	 * 
	 * @param listacmbElementoDesde
	 * Variable a asignar en  listacmbElementoDesde
	 */
	public void setListacmbElementoDesde(RegistroDataModelImpl listacmbElementoDesde) {
		this.listacmbElementoDesde = listacmbElementoDesde;
	}
	/**
	 * Retorna la lista listacmbElementoHasta
	 * 
	 * @return listacmbElementoHasta
	 */
	public RegistroDataModelImpl getListacmbElementoHasta() {
		return listacmbElementoHasta;
	}
	/**
	 * Asigna la lista listacmbElementoHasta
	 * 
	 * @param listacmbElementoHasta
	 * Variable a asignar en  listacmbElementoHasta
	 */
	public void setListacmbElementoHasta(RegistroDataModelImpl listacmbElementoHasta) {
		this.listacmbElementoHasta = listacmbElementoHasta;
	}
	public String getReporte() {
		return reporte;
	}
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}
	public EjbSysmanUtilRemote getEjbSysmanUtil() {
		return ejbSysmanUtil;
	}
	public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil) {
		this.ejbSysmanUtil = ejbSysmanUtil;
	}
	public String getCompania() {
		return compania;
	}
	public String getModulo() {
		return modulo;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
