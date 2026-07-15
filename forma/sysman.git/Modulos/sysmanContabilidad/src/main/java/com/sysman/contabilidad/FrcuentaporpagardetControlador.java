/*-
 * FrcuentaporpagardetControlador.java
 *
 * 1.0
 * 
 * 22/11/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad; 
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.sysman.contabilidad.enums.frcuentaporpagardetControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 22/11/2023
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  FrcuentaporpagardetControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private String cuentaInicial;
	private String cuentaFinal;
	private Date fechaCorte; 
	private String anio;
	private StreamedContent archivoDescarga;
	private RegistroDataModelImpl listaCuentaInicial;
	private RegistroDataModelImpl listaCuentaFinal;
	private List<Registro> listaano;
	String clasecuenta = "P,E";
	Date fecha = new Date();


	public FrcuentaporpagardetControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=GeneralCodigoFormaEnum.FRCUENTAPORPAGARDETCONTROLADOR
					.getCodigo();
			validarPermisos();
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
		cargarListaCuentaInicial(); 
		cargarListaCuentaFinal();
		cargarListaano();
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){

	}


	public void cargarListaano(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		try {
			listaano = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									frcuentaporpagardetControladorUrlEnum.URL4002
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Carga la lista listaCuentaInicial
	 */
	public void cargarListaCuentaInicial(){
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							frcuentaporpagardetControladorUrlEnum.URL29025
							.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.FECHA.getName(), SysmanFunciones.convertirAFechaCadena(fecha));

			param.put(GeneralParameterEnum.CLASECUENTA.getName(), clasecuenta);

			listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 * 
	 */
	public void cargarListaCuentaFinal(){
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							frcuentaporpagardetControladorUrlEnum.URL29025
							.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.FECHA.getName(), SysmanFunciones.convertirAFechaCadena(fecha));

			param.put(GeneralParameterEnum.CLASECUENTA.getName(), clasecuenta);

			listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, "CODIGO");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	public void oprimirExcel() {

		archivoDescarga=null;    
		generaReporte(FORMATOS.EXCEL);
	}


	private void generaReporte(FORMATOS formato) {

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			String nombreConsulta = "800598CuentasPorPagarDetallado"; 
			reemplazar.put("compania", compania);
			reemplazar.put("ano",anio);
			reemplazar.put("cuentainicial",cuentaInicial);
			reemplazar.put("cuentafinal",cuentaFinal);
			reemplazar.put("fechacorte",sdf.format(fechaCorte));


			parametros.put("PR_COMPANIA",SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_FECHACORTE",sdf.format(fechaCorte));
			Reporteador.resuelveConsulta(nombreConsulta,Integer.parseInt(SessionUtil.getModulo()),reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(nombreConsulta, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);			

		} catch (IOException  | JRException | SysmanException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial= registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaFinal
	 *
	 * 
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal= registroAux.getCampos().get("CODIGO").toString();
	}
	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return  cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}
	/**
	 * Asigna la variable  cuentaInicial
	 * 
	 * @param  cuentaInicial
	 * Variable a asignar en  cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}
	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return  cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}
	/**
	 * Asigna la variable  cuentaFinal
	 * 
	 * @param  cuentaFinal
	 * Variable a asignar en  cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}


	/**
	 * @return the anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * @param anio the anio to set
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable fechaCorte
	 * 
	 * @return  fechaCorte
	 */
	public Date getFechaCorte() {
		return fechaCorte;
	}
	/**
	 * Asigna la variable  fechaCorte
	 * 
	 * @param  fechaCorte
	 * Variable a asignar en  fechaCorte
	 */
	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * Retorna la lista listaCuentaInicial
	 * 
	 * @return listaCuentaInicial
	 */
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}
	/**
	 * Asigna la lista listaCuentaInicial
	 * 
	 * @param listaCuentaInicial
	 * Variable a asignar en  listaCuentaInicial
	 */
	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}
	/**
	 * Retorna la lista listaCuentaFinal
	 * 
	 * @return listaCuentaFinal
	 */
	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}
	/**
	 * Asigna la lista listaCuentaFinal
	 * 
	 * @param listaCuentaFinal
	 * Variable a asignar en  listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}
	/**
	 * @return the listaano
	 */
	public List<Registro> getListaano() {
		return listaano;
	}
	/**
	 * @param listaano the listaano to set
	 */
	public void setListaano(List<Registro> listaano) {
		this.listaano = listaano;
	}


}
