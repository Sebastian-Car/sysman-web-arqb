/*-
 * FrminforetencionporcuentasControlador.java
 *
 * 1.0
 * 
 * 30/01/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.InformeDeRetencionesControladorUrlEnum;
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
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 30/01/2024
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrminforetencionporcuentasControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean detallado;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cuentaFinal;

	private String anio;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFinal;

	private StreamedContent archivoDescarga;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacuentaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacuentaFinal;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrminforetencionporcuentasControlador
	 */
	public FrminforetencionporcuentasControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_INFO_RETENCION_POR_CUENTAS_CONTROLADOR.getCodigo();
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
	public void inicializar() {
		fechaInicial = fechaFinal = new Date();
		anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
		cargarListacuentaInicial();
		cargarListacuentaFinal();
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		fechaInicial = fechaFinal = new Date();
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformeDeRetencionesControladorUrlEnum.URL3314.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listacuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listacuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(InformeDeRetencionesControladorUrlEnum.URL4368.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CUENTA.getName(), cuentaInicial);

		listacuentaFinal = listacuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPdf() {
		archivoDescarga = null;
		generaReporte(ReportesBean.FORMATOS.PDF);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		archivoDescarga = null;
		generaReporte(ReportesBean.FORMATOS.EXCEL97);
	}

	private void generaReporte(FORMATOS formato) {
		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			String reporte = detallado ? "002541INFORETENCIONXCUENTADETALLADO" : "002540INFORETENCIONXCUENTA";
			String fechaIni = SysmanFunciones.convertirAFechaCadena(fechaInicial);
			String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);
			reemplazar.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
			reemplazar.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal));
			reemplazar.put("cuentaInicial", cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			Map<String, Object> parametros = new HashMap<>();
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso().getNit());
			parametros.put("PR_FECHAINICIAL", fechaIni);
			parametros.put("PR_FECHAFINAL", fechaFin);
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	public void cambiarfechaInicial() {
		// <CODIGO_DESARROLLADO>
		anio = String.valueOf(SysmanFunciones.ano(fechaInicial));
		cargarListacuentaInicial();
		cuentaInicial = null;
		cuentaFinal = null;
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		cargarListacuentaFinal();
		cuentaFinal = null;
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable detallado
	 * 
	 * @return detallado
	 */
	public boolean getDetallado() {
		return detallado;
	}

	/**
	 * Asigna la variable detallado
	 * 
	 * @param detallado Variable a asignar en detallado
	 */
	public void setDetallado(boolean detallado) {
		this.detallado = detallado;
	}

	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}

	/**
	 * Asigna la variable cuentaInicial
	 * 
	 * @param cuentaInicial Variable a asignar en cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}

	/**
	 * Asigna la variable cuentaFinal
	 * 
	 * @param cuentaFinal Variable a asignar en cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
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

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listacuentaInicial
	 * 
	 * @return listacuentaInicial
	 */
	public RegistroDataModelImpl getListacuentaInicial() {
		return listacuentaInicial;
	}

	/**
	 * Asigna la lista listacuentaInicial
	 * 
	 * @param listacuentaInicial Variable a asignar en listacuentaInicial
	 */
	public void setListacuentaInicial(RegistroDataModelImpl listacuentaInicial) {
		this.listacuentaInicial = listacuentaInicial;
	}

	/**
	 * Retorna la lista listacuentaFinal
	 * 
	 * @return listacuentaFinal
	 */
	public RegistroDataModelImpl getListacuentaFinal() {
		return listacuentaFinal;
	}

	/**
	 * Asigna la lista listacuentaFinal
	 * 
	 * @param listacuentaFinal Variable a asignar en listacuentaFinal
	 */
	public void setListacuentaFinal(RegistroDataModelImpl listacuentaFinal) {
		this.listacuentaFinal = listacuentaFinal;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

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
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}
