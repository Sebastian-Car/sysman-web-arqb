/*-
 * FrmCantidadTramitesControlador.java
 *
 * 1.0
 * 
 * 23/02/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.workflow.enums.FrmImpMasivaDocumentosControladorUrlEnum;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 23/02/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmCantidadTramitesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>

	private String proceso;
	private String etapa;
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
	private RegistroDataModelImpl listaProceso;
	private RegistroDataModelImpl listaEtapa;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmCantidadTramitesControlador
	 */
	public FrmCantidadTramitesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2246;
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
		cargarListaProceso(); 
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
	 * Carga la lista listaProceso
	 *
	 */
	public void cargarListaProceso(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmImpMasivaDocumentosControladorUrlEnum.URL0001
						.getValue());

		listaProceso = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaEtapa
	 *
	 */
	public void cargarListaEtapa(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROCESOJUD.getName(), proceso);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmImpMasivaDocumentosControladorUrlEnum.URL0002
						.getValue());

		listaEtapa = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Generar
	 * en la vista
	 *
	 *
	 */
	public void oprimirGenerar() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		generarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	public void generarInforme(FORMATOS formato){

		try {

			Map<String, Object> reemplazos = new HashMap<String, Object>();
			reemplazos.put("proceso", proceso);
			reemplazos.put("etapa", etapa);
			reemplazos.put("usuario", SessionUtil.getUser().getCodigo());

			String reporte = "800417TramitePorEtapa";

			String sql= Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazos);
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato,reporte);

		} catch (JRException | IOException | SQLException | DRException  | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
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
	 * listaProceso
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProceso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proceso = registroAux.getCampos().get("CODIGO").toString();
		cargarListaEtapa();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaEtapa
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEtapa(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		etapa= registroAux.getCampos().get("CODIGO").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable proceso
	 * 
	 * @return  proceso
	 */
	public String getProceso() {
		return proceso;
	}
	/**
	 * Asigna la variable  proceso
	 * 
	 * @param  proceso
	 * Variable a asignar en  proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	/**
	 * Retorna la variable etapa
	 * 
	 * @return  etapa
	 */
	public String getEtapa() {
		return etapa;
	}
	/**
	 * Asigna la variable  etapa
	 * 
	 * @param  etapa
	 * Variable a asignar en  etapa
	 */
	public void setEtapa(String etapa) {
		this.etapa = etapa;
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
	 * @return the listaProceso
	 */
	public RegistroDataModelImpl getListaProceso() {
		return listaProceso;
	}
	/**
	 * @return the listaEtapa
	 */
	public RegistroDataModelImpl getListaEtapa() {
		return listaEtapa;
	}
	/**
	 * @param listaProceso the listaProceso to set
	 */
	public void setListaProceso(RegistroDataModelImpl listaProceso) {
		this.listaProceso = listaProceso;
	}
	/**
	 * @param listaEtapa the listaEtapa to set
	 */
	public void setListaEtapa(RegistroDataModelImpl listaEtapa) {
		this.listaEtapa = listaEtapa;
	}
	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
