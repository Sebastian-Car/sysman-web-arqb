/*-
 * FrmInfProcesosJudicialesControlador.java
 *
 * 1.0
 * 
 * 07/01/2021
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmInfProcesosJudicialesControladorUrlEnum;
import com.sysman.workflow.enums.FrmTramitesControladorUrlEnum;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 07/01/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmInfProcesosJudicialesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private String tipoProceso;
	private String tramite;
	private String nitContribuyente;
	private static final String PERSUASIVO = "20000";
	private static final String COACTIVO = "20001";
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
	/**
	 */
	private RegistroDataModelImpl listaTipoProceso;
	private Registro rsTramite;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmInfProcesosJudicialesControlador
	 */
	public FrmInfProcesosJudicialesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2230;
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
		cargarListaTipoProceso();
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
	 * Carga la lista listaTipoProceso
	 *
	 */
	public void cargarListaTipoProceso(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmInfProcesosJudicialesControladorUrlEnum.URL001
						.getValue());

		listaTipoProceso = new RegistroDataModelImpl(urlBean.getUrl(),
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
		archivoDescarga=null; 
		generarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>

	public void generarInforme(FORMATOS formato){

		try {
			if(tramite.isEmpty()) {
				cargarTramite();
			}
			String reporte;
			String consulta;
			Map<String, Object> reemplazos = new HashMap<String, Object>();
			Map<String, Object> parametros = new HashMap<String, Object>();
			reemplazos.put("proceso", tipoProceso);
			reemplazos.put("tramite", tramite);


			if(tipoProceso.equals(PERSUASIVO) || tipoProceso.equals(COACTIVO)) {
				reporte = "002211InformeProcesosJudiciales";
				consulta = "002211ConsultaEtapas";
			}else {
				reporte = "002213InformeProcesosJudiciales";
				consulta = "002213ConsultaEtapas";
			}
			String sql = Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazos);

			String sql1 = Reporteador.resuelveConsulta(consulta,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazos);

			parametros.put("PR_STRSQL", sql);
			parametros.put("PR_STRSQL1", sql1);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL);

		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void cargarTramite() {

		try {
			Map<String, Object> param = new HashMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.NIT.getName(), nitContribuyente);


			rsTramite = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmInfProcesosJudicialesControladorUrlEnum.URL002.getValue())
							.getUrl(),
							param));

			if (rsTramite != null) {

				tramite = SysmanFunciones.nvl(rsTramite.getCampos().get("NUMERO_TRAMITE"), "0").toString();
			}


		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipoProceso
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoProceso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoProceso= registroAux.getCampos().get("CODIGO").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipoProceso
	 * 
	 * @return  tipoProceso
	 */
	public String getTipoProceso() {
		return tipoProceso;
	}
	/**
	 * Asigna la variable  tipoProceso
	 * 
	 * @param  tipoProceso
	 * Variable a asignar en  tipoProceso
	 */
	public void setTipoProceso(String tipoProceso) {
		this.tipoProceso = tipoProceso;
	}

	public String getTramite() {
		return tramite;
	}
	public void setTramite(String tramite) {
		this.tramite = tramite;
	}
	/**
	 * Retorna la variable nitContribuyente
	 * 
	 * @return  nitContribuyente
	 */
	public String getNitContribuyente() {
		return nitContribuyente;
	}
	/**
	 * Asigna la variable  nitContribuyente
	 * 
	 * @param  nitContribuyente
	 * Variable a asignar en  nitContribuyente
	 */
	public void setNitContribuyente(String nitContribuyente) {
		this.nitContribuyente = nitContribuyente;
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
	 * Retorna la lista listaTipoProceso
	 * 
	 * @return listaTipoProceso
	 */
	public RegistroDataModelImpl getListaTipoProceso() {
		return listaTipoProceso;
	}
	/**
	 * Asigna la lista listaTipoProceso
	 * 
	 * @param listaTipoProceso
	 * Variable a asignar en  listaTipoProceso
	 */
	public void setListaTipoProceso(RegistroDataModelImpl listaTipoProceso) {
		this.listaTipoProceso = listaTipoProceso;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
