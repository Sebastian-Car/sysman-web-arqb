/*-
 * MantenimientoVehiculosControlador.java
 *
 * 1.0
 * 
 * 27/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.mantenimientoactivos;

import java.io.IOException;
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
import com.sysman.mantenimientoactivos.enums.MantenimientoVehiculosControladorEnum;
import com.sysman.mantenimientoactivos.enums.MantenimientoVehiculosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que genera un informe sobre mantenimiento de Vehiculos
 *
 * @version 1.0, 27/11/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class MantenimientoVehiculosControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;

	EjbSysmanUtil ejbSysmanUtil;
	// <DECLARAR_ATRIBUTOS>

	private String elementoInicial;
	private String elementoFinal;
	private String serieInicial;
	private String serieFinal;
	private String elementoInicialCp;
	private String elementoFinalCp;
	private String serieInicialCp;
	private String serieFinalCp;

	private Date fechaInicialCp;
	private Date fechaFinalCp;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	private RegistroDataModelImpl listaelementoInicial;
	private RegistroDataModelImpl listaelementoFinal;
	private RegistroDataModelImpl listaserieInicial;
	private RegistroDataModelImpl listaserieFinal;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de MantenimientoVehiculosControlador
	 */
	public MantenimientoVehiculosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();

		fechaInicialCp = new Date();
		fechaFinalCp = new Date();

		try {
			// 1997
			numFormulario = GeneralCodigoFormaEnum.MANTENIMIENTO_VEHICULOS_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
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

		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaelementoInicial();
		cargarListaserieInicial();
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
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

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaelementoInicial
	 *
	 */
	public void cargarListaelementoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MantenimientoVehiculosControladorUrlEnum.URL507011.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaelementoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGOELEMENTO.getName());
	}

	/**
	 * 
	 * Carga la lista listaelementoFinal
	 *
	 */
	public void cargarListaelementoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MantenimientoVehiculosControladorUrlEnum.URL507013.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(MantenimientoVehiculosControladorEnum.CODIGO.getValue(), elementoInicial);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaelementoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGOELEMENTO.getName());
	}

	/**
	 * 
	 * Carga la lista listaserieInicial
	 *
	 */
	public void cargarListaserieInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MantenimientoVehiculosControladorUrlEnum.URL507015.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CODIGOELEINICIAL", elementoFinal);

		listaserieInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.SERIE.getName());
	}

	/**
	 * 
	 * Carga la lista listaserieFinal
	 *
	 */
	public void cargarListaserieFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(MantenimientoVehiculosControladorUrlEnum.URL507017.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(MantenimientoVehiculosControladorEnum.SERIEINICIAL.getValue(), serieInicial);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaserieFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.SERIE.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton generaExcel en la vista
	 *
	 *
	 */
	public void oprimirgeneraExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton generaPDF en la vista
	 *
	 *
	 */
	public void oprimirgeneraPDF() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;

		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>

	private void generarReporte(FORMATOS formato) {
		Map<String, Object> parametros = new HashMap<>();
		Map<String, Object> reemplazar = new HashMap<>();
		if (fechaInicialCp.before(fechaFinalCp) || (fechaInicialCp.equals(fechaFinalCp))) {

			String reporte = "001968ReporteDeMantenimiento";
			try {
				reemplazar.put(MantenimientoVehiculosControladorEnum.compania.getValue(), compania);
				reemplazar.put(MantenimientoVehiculosControladorEnum.fechaInicial.getValue(),
						SysmanFunciones.formatearFecha(fechaInicialCp));
				reemplazar.put(MantenimientoVehiculosControladorEnum.fechaFinal.getValue(),
						SysmanFunciones.formatearFecha(fechaFinalCp));
				reemplazar.put(MantenimientoVehiculosControladorEnum.elementoInicial.getValue(), elementoInicial);
				reemplazar.put(MantenimientoVehiculosControladorEnum.elementoFinal.getValue(), elementoFinal);
				reemplazar.put(MantenimientoVehiculosControladorEnum.serieInicial.getValue(), serieInicial);
				reemplazar.put(MantenimientoVehiculosControladorEnum.serieFinal.getValue(), serieFinal);

				parametros.put("PR_COMPANIA", SessionUtil.getCompaniaIngreso().getNombre().toUpperCase());
				parametros.put("PR_FECHA_INICIAL", fechaInicialCp);
				parametros.put("PR_FECHA_FINAL", fechaFinalCp);

				Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

				archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
			} catch (JRException | IOException | SysmanException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		} else {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4263"));
		}
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaelementoInicial
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaelementoInicial(SelectEvent event) {

		Registro registroAux = (Registro) event.getObject();
		elementoInicial = registroAux.getCampos().get(MantenimientoVehiculosControladorEnum.CODIGOELEMENTO.getValue())
				.toString();
		elementoInicialCp = registroAux.getCampos().get(MantenimientoVehiculosControladorEnum.NOMBRELARGO.getValue())
				.toString();
		cargarListaelementoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaelementoFinal
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaelementoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal = registroAux.getCampos().get(MantenimientoVehiculosControladorEnum.CODIGOELEMENTO.getValue())
				.toString();
		elementoFinalCp = registroAux.getCampos().get(MantenimientoVehiculosControladorEnum.NOMBRELARGO.getValue())
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaserieInicial
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaserieInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		serieInicial = registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()).toString();

		serieInicialCp = registroAux.getCampos().get(MantenimientoVehiculosControladorEnum.NOMBRELARGO.getValue())
				.toString();
		cargarListaserieFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaserieFinal
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaserieFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		serieFinal = registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()).toString();
		serieFinalCp = registroAux.getCampos().get(MantenimientoVehiculosControladorEnum.NOMBRELARGO.getValue())
				.toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable elementoInicial
	 * 
	 * @return elementoInicial
	 */
	public String getElementoInicial() {
		return elementoInicial;
	}

	/**
	 * Asigna la variable elementoInicial
	 * 
	 * @param elementoInicial
	 *            Variable a asignar en elementoInicial
	 */
	public void setElementoInicial(String elementoInicial) {
		this.elementoInicial = elementoInicial;
	}

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
	 * @param elementoFinal
	 *            Variable a asignar en elementoFinal
	 */
	public void setElementoFinal(String elementoFinal) {
		this.elementoFinal = elementoFinal;
	}

	/**
	 * Retorna la variable serieInicial
	 * 
	 * @return serieInicial
	 */
	public String getSerieInicial() {
		return serieInicial;
	}

	/**
	 * Asigna la variable serieInicial
	 * 
	 * @param serieInicial
	 *            Variable a asignar en serieInicial
	 */
	public void setSerieInicial(String serieInicial) {
		this.serieInicial = serieInicial;
	}

	/**
	 * Retorna la variable serieFinal
	 * 
	 * @return serieFinal
	 */
	public String getSerieFinal() {
		return serieFinal;
	}

	/**
	 * Asigna la variable serieFinal
	 * 
	 * @param serieFinal
	 *            Variable a asignar en serieFinal
	 */
	public void setSerieFinal(String serieFinal) {
		this.serieFinal = serieFinal;
	}

	/**
	 * Retorna la variable elementoInicialCp
	 * 
	 * @return elementoInicialCp
	 */
	public String getElementoInicialCp() {
		return elementoInicialCp;
	}

	/**
	 * Asigna la variable elementoInicialCp
	 * 
	 * @param elementoInicialCp
	 *            Variable a asignar en elementoInicialCp
	 */
	public void setElementoInicialCp(String elementoInicialCp) {
		this.elementoInicialCp = elementoInicialCp;
	}

	/**
	 * Retorna la variable elementoFinalCp
	 * 
	 * @return elementoFinalCp
	 */
	public String getElementoFinalCp() {
		return elementoFinalCp;
	}

	/**
	 * Asigna la variable elementoFinalCp
	 * 
	 * @param elementoFinalCp
	 *            Variable a asignar en elementoFinalCp
	 */
	public void setElementoFinalCp(String elementoFinalCp) {
		this.elementoFinalCp = elementoFinalCp;
	}

	/**
	 * Retorna la variable serieInicialCp
	 * 
	 * @return serieInicialCp
	 */
	public String getSerieInicialCp() {
		return serieInicialCp;
	}

	/**
	 * Asigna la variable serieInicialCp
	 * 
	 * @param serieInicialCp
	 *            Variable a asignar en serieInicialCp
	 */
	public void setSerieInicialCp(String serieInicialCp) {
		this.serieInicialCp = serieInicialCp;
	}

	/**
	 * Retorna la variable serieFinalCp
	 * 
	 * @return serieFinalCp
	 */
	public String getSerieFinalCp() {
		return serieFinalCp;
	}

	/**
	 * Asigna la variable serieFinalCp
	 * 
	 * @param serieFinalCp
	 *            Variable a asignar en serieFinalCp
	 */
	public void setSerieFinalCp(String serieFinalCp) {
		this.serieFinalCp = serieFinalCp;
	}

	/**
	 * Retorna la variable fechaInicialCp
	 * 
	 * @return fechaInicialCp
	 */
	public Date getFechaInicialCp() {
		return fechaInicialCp;
	}

	/**
	 * Asigna la variable fechaInicialCp
	 * 
	 * @param fechaInicialCp
	 *            Variable a asignar en fechaInicialCp
	 */
	public void setFechaInicialCp(Date fechaInicialCp) {
		this.fechaInicialCp = fechaInicialCp;
	}

	/**
	 * Retorna la variable fechaFinalCp
	 * 
	 * @return fechaFinalCp
	 */
	public Date getFechaFinalCp() {
		return fechaFinalCp;
	}

	/**
	 * Asigna la variable fechaFinalCp
	 * 
	 * @param fechaFinalCp
	 *            Variable a asignar en fechaFinalCp
	 */
	public void setFechaFinalCp(Date fechaFinalCp) {
		this.fechaFinalCp = fechaFinalCp;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaelementoInicial
	 * 
	 * @return listaelementoInicial
	 */
	public RegistroDataModelImpl getListaelementoInicial() {
		return listaelementoInicial;
	}

	/**
	 * Asigna la lista listaelementoInicial
	 * 
	 * @param listaelementoInicial
	 *            Variable a asignar en listaelementoInicial
	 */
	public void setListaelementoInicial(RegistroDataModelImpl listaelementoInicial) {
		this.listaelementoInicial = listaelementoInicial;
	}

	/**
	 * Retorna la lista listaelementoFinal
	 * 
	 * @return listaelementoFinal
	 */
	public RegistroDataModelImpl getListaelementoFinal() {
		return listaelementoFinal;
	}

	/**
	 * Asigna la lista listaelementoFinal
	 * 
	 * @param listaelementoFinal
	 *            Variable a asignar en listaelementoFinal
	 */
	public void setListaelementoFinal(RegistroDataModelImpl listaelementoFinal) {
		this.listaelementoFinal = listaelementoFinal;
	}

	/**
	 * Retorna la lista listaserieInicial
	 * 
	 * @return listaserieInicial
	 */
	public RegistroDataModelImpl getListaserieInicial() {
		return listaserieInicial;
	}

	/**
	 * Asigna la lista listaserieInicial
	 * 
	 * @param listaserieInicial
	 *            Variable a asignar en listaserieInicial
	 */
	public void setListaserieInicial(RegistroDataModelImpl listaserieInicial) {
		this.listaserieInicial = listaserieInicial;
	}

	/**
	 * Retorna la lista listaserieFinal
	 * 
	 * @return listaserieFinal
	 */
	public RegistroDataModelImpl getListaserieFinal() {
		return listaserieFinal;
	}

	/**
	 * Asigna la lista listaserieFinal
	 * 
	 * @param listaserieFinal
	 *            Variable a asignar en listaserieFinal
	 */
	public void setListaserieFinal(RegistroDataModelImpl listaserieFinal) {
		this.listaserieFinal = listaserieFinal;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

}
