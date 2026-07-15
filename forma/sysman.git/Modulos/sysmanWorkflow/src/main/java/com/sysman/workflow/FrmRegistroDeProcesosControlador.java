/*-
 * FrmRegistroDeProcesosControlador.java
 *
 * 1.0
 * 
 * 16/09/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmRegistroProcesosControladorUrlEnum;

import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 16/09/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmRegistroDeProcesosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String dependenciaInicial;
	private String dependenciaFinal;
	private String tramiteInicial;
	private String tramiteFinal;
	private String responsableInicial;
	private String responsableFinal;
	private Date fechaInicial;
	private Date fechaFinal;

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
	private RegistroDataModelImpl listaTramiteInicial;
	private RegistroDataModelImpl listaTramiteFinal;
	private RegistroDataModelImpl listaDependenciaInicial;
	private RegistroDataModelImpl listaDependenciaFinal;
	private RegistroDataModelImpl listaResponsableInicial;
	private RegistroDataModelImpl listaResponsableFinal;
	private boolean estado;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmRegistroDeProcesosControlador
	 */
	public FrmRegistroDeProcesosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2318;
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
		cargarListaTramiteInicial();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaDependenciaInicial(); 
		cargarListaResponsableInicial(); 
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
		try {
			fechaInicial = SysmanFunciones.primeroDeMesFecha(new Date());
			fechaFinal= SysmanFunciones.ultimoDiaDate(new Date());
			tramiteInicial = "0";
			tramiteFinal = "999999999999";
			dependenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
			dependenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
			responsableInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
			responsableFinal = SysmanConstantes.DEFECTOFINAL_STRING;
			estado = true;
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaTramiteInicial
	 *
	 */
	public void cargarListaTramiteInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmRegistroProcesosControladorUrlEnum.URL001
						.getValue());

		listaTramiteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.NUMERO.getName());
	}
	/**
	 * 
	 * Carga la lista listaTramiteFinal
	 *
	 */
	public void cargarListaTramiteFinal(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), tramiteInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmRegistroProcesosControladorUrlEnum.URL002
						.getValue());

		listaTramiteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.NUMERO.getName());
	}
	/**
	 * 
	 * Carga la lista listaDependenciaInicial
	 *
	 */
	public void cargarListaDependenciaInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmRegistroProcesosControladorUrlEnum.URL003
						.getValue());

		listaDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());


	}
	/**
	 * 
	 * Carga la lista listaDependenciaFinal
	 *
	 */
	public void cargarListaDependenciaFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), dependenciaInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmRegistroProcesosControladorUrlEnum.URL004
						.getValue());

		listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());


	}
	/**
	 * 
	 * Carga la lista listaResponsableInicial
	 *
	 */
	public void cargarListaResponsableInicial(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmRegistroProcesosControladorUrlEnum.URL005
						.getValue());

		listaResponsableInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaResponsableFinal
	 *
	 */
	public void cargarListaResponsableFinal(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CODIGOINICIAL", responsableInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmRegistroProcesosControladorUrlEnum.URL006
						.getValue());

		listaResponsableFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		generarInforme(ReportesBean.FORMATOS.PDF);
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
		generarInforme(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>

	public void generarInforme(FORMATOS formato) {
		try {
			String reporte;
			reporte = "002298ResgistroDeProcesos";
			Map<String, Object> reemplazos = new HashMap<String, Object>();
			Map<String, Object> parametros = new HashMap<String, Object>();

			reemplazos.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazos.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
			reemplazos.put("tramiteIni", SysmanFunciones.nvl(tramiteInicial,"0"));
			reemplazos.put("tramiteFin", SysmanFunciones.nvl(tramiteFinal,"0"));
			reemplazos.put("dependenciaIni", dependenciaInicial);
			reemplazos.put("dependenciaFin", dependenciaFinal);
			reemplazos.put("responsableIni", responsableInicial);
			reemplazos.put("responsableFin", responsableFinal);
			reemplazos.put("responsableFin", responsableFinal);
			reemplazos.put("estado", estado);

			parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());



			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazos, parametros);
			

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}




	}
	//<METODOS_CAMBIAR>

	public void cambiarTramiteInicial() {
		cargarListaTramiteFinal();
	}

	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDependenciaInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDependenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaInicial= registroAux.getCampos().get("CODIGO").toString();

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
		setDependenciaFinal(registroAux.getCampos().get("CODIGO").toString());

	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaResponsableInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResponsableInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		responsableInicial= registroAux.getCampos().get("CODIGO").toString();

		cargarListaResponsableFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaResponsableFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResponsableFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		responsableFinal= registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTramiteInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTramiteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tramiteInicial= SysmanFunciones.toString(registroAux.getCampos().get("NUMERO"));
		
		cargarListaTramiteFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTramiteFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTramiteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tramiteFinal= SysmanFunciones.toString(registroAux.getCampos().get("NUMERO"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>

	/**
	 * Retorna la variable dependenciaInicial
	 * 
	 * @return  dependenciaInicial
	 */
	public String getDependenciaInicial() {
		return dependenciaInicial;
	}
	/**
	 * Asigna la variable  dependenciaInicial
	 * 
	 * @param  dependenciaInicial
	 * Variable a asignar en  dependenciaInicial
	 */
	public void setDependenciaInicial(String dependenciaInicial) {
		this.dependenciaInicial = dependenciaInicial;
	}
	/**
	 * Retorna la variable tramiteInicial
	 * 
	 * @return  tramiteInicial
	 */
	public String getTramiteInicial() {
		return tramiteInicial;
	}
	/**
	 * Asigna la variable  tramiteInicial
	 * 
	 * @param  tramiteInicial
	 * Variable a asignar en  tramiteInicial
	 */
	public void setTramiteInicial(String tramiteInicial) {
		this.tramiteInicial = tramiteInicial;
	}
	/**
	 * Retorna la variable tramiteFinal
	 * 
	 * @return  tramiteFinal
	 */
	public String getTramiteFinal() {
		return tramiteFinal;
	}
	/**
	 * Asigna la variable  tramiteFinal
	 * 
	 * @param  tramiteFinal
	 * Variable a asignar en  tramiteFinal
	 */
	public void setTramiteFinal(String tramiteFinal) {
		this.tramiteFinal = tramiteFinal;
	}
	/**
	 * Retorna la variable responsableInicial
	 * 
	 * @return  responsableInicial
	 */
	public String getResponsableInicial() {
		return responsableInicial;
	}
	/**
	 * Asigna la variable  responsableInicial
	 * 
	 * @param  responsableInicial
	 * Variable a asignar en  responsableInicial
	 */
	public void setResponsableInicial(String responsableInicial) {
		this.responsableInicial = responsableInicial;
	}
	/**
	 * Retorna la variable responsableFinal
	 * 
	 * @return  responsableFinal
	 */
	public String getResponsableFinal() {
		return responsableFinal;
	}
	/**
	 * Asigna la variable  responsableFinal
	 * 
	 * @param  responsableFinal
	 * Variable a asignar en  responsableFinal
	 */
	public void setResponsableFinal(String responsableFinal) {
		this.responsableFinal = responsableFinal;
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
	 * Retorna la lista listaDependenciaInicial
	 * 
	 * @return listaDependenciaInicial
	 */
	public RegistroDataModelImpl getListaDependenciaInicial() {
		return listaDependenciaInicial;
	}
	/**
	 * Asigna la lista listaDependenciaInicial
	 * 
	 * @param listaDependenciaInicial
	 * Variable a asignar en  listaDependenciaInicial
	 */
	public void setListaDependenciaInicial(RegistroDataModelImpl listaDependenciaInicial) {
		this.listaDependenciaInicial = listaDependenciaInicial;
	}
	/**
	 * Retorna la lista listaResponsableInicial
	 * 
	 * @return listaResponsableInicial
	 */
	public RegistroDataModelImpl getListaResponsableInicial() {
		return listaResponsableInicial;
	}
	/**
	 * Asigna la lista listaResponsableInicial
	 * 
	 * @param listaResponsableInicial
	 * Variable a asignar en  listaResponsableInicial
	 */
	public void setListaResponsableInicial(RegistroDataModelImpl listaResponsableInicial) {
		this.listaResponsableInicial = listaResponsableInicial;
	}
	/**
	 * Retorna la lista listaResponsableFinal
	 * 
	 * @return listaResponsableFinal
	 */
	public RegistroDataModelImpl getListaResponsableFinal() {
		return listaResponsableFinal;
	}
	/**
	 * Asigna la lista listaResponsableFinal
	 * 
	 * @param listaResponsableFinal
	 * Variable a asignar en  listaResponsableFinal
	 */
	public void setListaResponsableFinal(RegistroDataModelImpl listaResponsableFinal) {
		this.listaResponsableFinal = listaResponsableFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the listaDependenciaFinal
	 */
	public RegistroDataModelImpl getListaDependenciaFinal() {
		return listaDependenciaFinal;
	}
	/**
	 * @param listaDependenciaFinal the listaDependenciaFinal to set
	 */
	public void setListaDependenciaFinal(RegistroDataModelImpl listaDependenciaFinal) {
		this.listaDependenciaFinal = listaDependenciaFinal;
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
	/**
	 * @return the dependenciaFinal
	 */
	public String getDependenciaFinal() {
		return dependenciaFinal;
	}
	/**
	 * @param dependenciaFinal the dependenciaFinal to set
	 */
	public void setDependenciaFinal(String dependenciaFinal) {
		this.dependenciaFinal = dependenciaFinal;
	}
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public RegistroDataModelImpl getListaTramiteInicial() {
		return listaTramiteInicial;
	}
	public void setListaTramiteInicial(RegistroDataModelImpl listaTramiteInicial) {
		this.listaTramiteInicial = listaTramiteInicial;
	}
	public RegistroDataModelImpl getListaTramiteFinal() {
		return listaTramiteFinal;
	}
	public void setListaTramiteFinal(RegistroDataModelImpl listaTramiteFinal) {
		this.listaTramiteFinal = listaTramiteFinal;
	}


}
