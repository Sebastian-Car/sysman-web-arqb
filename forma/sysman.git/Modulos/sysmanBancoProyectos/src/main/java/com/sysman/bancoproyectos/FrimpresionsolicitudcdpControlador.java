/*-
 * FrimpresionsolicitudcdpControlador.java
 *
 * 1.0
 * 
 * 23/01/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.bancoproyectos.enums.FrmimpresionSolicitudcdpControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 23/01/2023
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  FrimpresionsolicitudcdpControlador extends BeanBaseModal{
	
	private final String compania ;
	private String anio;
	private String dependencia;
	private String certificado;
	private int parametroLinea;
	private int parametroSector;
	private int parametroPrograma;	
	private StreamedContent archivoDescarga;
	private List<Registro> listaCbanio;
	private RegistroDataModelImpl listaCbdependencia;
	private RegistroDataModelImpl listaCbcertificado;
	private String modulo;
	
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	
	
	public FrimpresionsolicitudcdpControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=GeneralCodigoFormaEnum.FRM_IMPRESION_SOLICITUD_CDP.getCodigo();

			dependencia = SysmanFunciones.toString(SessionUtil.getUser().getDependencia().getCodigo());

			validarPermisos();
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
		cargarListaCbanio();
		cargarListaCbdependencia(); 
		cargarListaCbcertificado();
		abrirFormulario();
	}
	
	@Override
	public void abrirFormulario(){

		anio = Integer.toString(SysmanFunciones.ano(new Date()));

	}

	public void cargarListaCbanio(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaCbanio = RegistroConverter.toListRegistro(requestManager
					.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmimpresionSolicitudcdpControladorUrlEnum.URL4001
							.getValue()).getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaCbdependencia
	 *
	 */
	public void cargarListaCbdependencia(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmimpresionSolicitudcdpControladorUrlEnum.URL62007
							.getValue());

			listaCbdependencia = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param, true,
					GeneralParameterEnum.CODIGO.getName());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaCbcertificado
	 *
	 */
	public void cargarListaCbcertificado(){
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmimpresionSolicitudcdpControladorUrlEnum.URL130058.getValue());
			listaCbcertificado = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void oprimirExcel() {

		archivoDescarga=null; 
		generarInforme(FORMATOS.EXCEL);
		
	}


	public void oprimirPdf() {

		archivoDescarga=null;  
		generarInforme(FORMATOS.PDF);

	}

	private void generarInforme(FORMATOS formato) {
		getParamValues();
		
		String reporte = "002428ImpresionSolicitudCDP";

		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("paramLinea", parametroLinea);
			reemplazar.put("paramSector", parametroSector);
			reemplazar.put("paramPrograma", parametroPrograma);
			reemplazar.put("anio", anio);
			reemplazar.put("dependencia", dependencia);
			reemplazar.put("certificado", certificado);
			
			String strSql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar);

			
			HashMap<String,Object> parametros = new HashMap<>();			
			parametros.put("PR_STRSQL", strSql);
			
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		

	}

	//Obtiene el valor de los parametros 1) NUMERO DE DIGITOS EJE 2) NUMERO DE DIGITOS SECTOR 3)NUMERO DE DIGITOS PROGRAMA
	private void getParamValues() {
		try {
			parametroLinea = Integer.parseInt(ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS EJE", modulo,
					new Date(), false));
			parametroSector =Integer.parseInt( ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS SECTOR", modulo,
					new Date(), false));	
			parametroPrograma = Integer.parseInt(ejbSysmanUtil.consultarParametro(compania, "NUMERO DE DIGITOS PROGRAMA", modulo,
					new Date(), false));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}	
			
	}
	public void seleccionarFilaCbdependencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependencia = SysmanFunciones
				.nvl(registroAux.getCampos().get("CODIGO"),"")
				.toString();

		certificado = null;
		listaCbcertificado = null;
		cargarListaCbcertificado();
	}

	public void seleccionarFilaCbcertificado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		certificado = SysmanFunciones
				.nvl(registroAux.getCampos().get("CODIGO"), "")
				.toString();

	}

	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable dependencia
	 * 
	 * @return  dependencia
	 */
	public String getDependencia() {
		return dependencia;
	}
	/**
	 * Asigna la variable  dependencia
	 * 
	 * @param  dependencia
	 * Variable a asignar en  dependencia
	 */
	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}
	/**
	 * Retorna la variable certificado
	 * 
	 * @return  certificado
	 */
	public String getCertificado() {
		return certificado;
	}
	/**
	 * Asigna la variable  certificado
	 * 
	 * @param  certificado
	 * Variable a asignar en  certificado
	 */
	public void setCertificado(String certificado) {
		this.certificado = certificado;
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
	/**
	 * Retorna la lista listaCbanio
	 * 
	 * @return listaCbanio
	 */
	public List<Registro> getListaCbanio() {
		return listaCbanio;
	}
	/**
	 * Asigna la lista listaCbanio
	 * 
	 * @param listaCbanio
	 * Variable a asignar en  listaCbanio
	 */
	public void setListaCbanio(List<Registro> listaCbanio) {
		this.listaCbanio = listaCbanio;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCbdependencia
	 * 
	 * @return listaCbdependencia
	 */
	public RegistroDataModelImpl getListaCbdependencia() {
		return listaCbdependencia;
	}
	/**
	 * Asigna la lista listaCbdependencia
	 * 
	 * @param listaCbdependencia
	 * Variable a asignar en  listaCbdependencia
	 */
	public void setListaCbdependencia(RegistroDataModelImpl listaCbdependencia) {
		this.listaCbdependencia = listaCbdependencia;
	}
	/**
	 * Retorna la lista listaCbcertificado
	 * 
	 * @return listaCbcertificado
	 */
	public RegistroDataModelImpl getListaCbcertificado() {
		return listaCbcertificado;
	}
	/**
	 * Asigna la lista listaCbcertificado
	 * 
	 * @param listaCbcertificado
	 * Variable a asignar en  listaCbcertificado
	 */
	public void setListaCbcertificado(RegistroDataModelImpl listaCbcertificado) {
		this.listaCbcertificado = listaCbcertificado;
	}
	

}