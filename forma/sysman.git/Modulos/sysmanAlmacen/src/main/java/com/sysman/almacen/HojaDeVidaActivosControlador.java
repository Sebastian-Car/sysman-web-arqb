/*-
 * HojaDeVidaActivosControlador.java
 *
 * 1.0
 * 
 * 07/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.HojaDeVidaActivosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que imprime un reporte de hoja de vida de Activos
 *
 * @version 1.0, 07/12/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped

public class HojaDeVidaActivosControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>
	@EJB
	EjbSysmanUtil ejbSysmanUtil;
	private String serieInicial;
	private String serieFinal;
	private String parametro;
	private String condicion;
	private String leftJoin;
	private String serieInicialNombre;
	private String serieFinalNombre;
	/**
	 * /** Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaSerieInicial;
	private RegistroDataModelImpl listaSerieFinal;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de HojaDeVidaActivosControlador
	 */
	public HojaDeVidaActivosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// 2005
			numFormulario = GeneralCodigoFormaEnum.HOJA_DE_VIDA_ACTIVOS_CONTROLADOR.getCodigo();
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
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaSerieInicial();

		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		try {
			parametro = SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "MANEJA NIIF EN ALMACEN", modulo, new Date(), true), "");
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
		}
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaSerieInicial
	 *
	 */
	public void cargarListaSerieInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(HojaDeVidaActivosControladorUrlEnum.URL141129.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaSerieInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.SERIE.getName());
	}

	/**
	 * 
	 * Carga la lista listaSerieFinal
	 *
	 */
	public void cargarListaSerieFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(HojaDeVidaActivosControladorUrlEnum.URL141131.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("SERIEINICIAL", serieInicial);

		listaSerieFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
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

	public void oprimirGeneraPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	private void generarReporte(ReportesBean.FORMATOS formato) {
		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			HashMap<String, Object> parametros = new HashMap<>();

			if ("SI".equals(parametro)) {
				condicion = "DEVOLUTIVO.NIIF_VIDA_UTIL MESESVIDAUTIL,";
			} else {
				condicion = "ACTDEPRECIABLE.MESESVIDAUTIL,";
			}
			String reporte = "001975HojadeVidaActivo";
			reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			reemplazar.put("condicion", condicion);
			reemplazar.put("serieInicial", serieInicial);
			reemplazar.put("serieFinal", serieFinal);
			reemplazar.put("separador", SysmanFunciones.concatenar("'", File.separator, "'"));
			reemplazar.put("mesesvidautil", condicion);

			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre().toUpperCase());
			parametros.put("PR_USUARIO", SessionUtil.getUser().toString().toUpperCase());
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaSerieInicial
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSerieInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		serieInicial = registroAux.getCampos().get("SERIE").toString();
		serieInicialNombre = registroAux.getCampos().get("NOMBRECORTO").toString();
		cargarListaSerieFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaSerieFinal
	 *
	 *
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSerieFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		serieFinal = registroAux.getCampos().get("SERIE").toString();
		serieFinalNombre = registroAux.getCampos().get("NOMBRECORTO").toString();
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
	 * Retorna la lista listaSerieInicial
	 * 
	 * @return listaSerieInicial
	 */
	public RegistroDataModelImpl getListaSerieInicial() {
		return listaSerieInicial;
	}

	/**
	 * Asigna la lista listaSerieInicial
	 * 
	 * @param listaSerieInicial
	 *            Variable a asignar en listaSerieInicial
	 */
	public void setListaSerieInicial(RegistroDataModelImpl listaSerieInicial) {
		this.listaSerieInicial = listaSerieInicial;
	}

	/**
	 * Retorna la lista listaSerieFinal
	 * 
	 * @return listaSerieFinal
	 */
	public RegistroDataModelImpl getListaSerieFinal() {
		return listaSerieFinal;
	}

	/**
	 * Asigna la lista listaSerieFinal
	 * 
	 * @param listaSerieFinal
	 *            Variable a asignar en listaSerieFinal
	 */
	public void setListaSerieFinal(RegistroDataModelImpl listaSerieFinal) {
		this.listaSerieFinal = listaSerieFinal;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public String getSerieFinalNombre() {
		return serieFinalNombre;
	}

	public void setSerieFinalNombre(String serieFinalNombre) {
		this.serieFinalNombre = serieFinalNombre;
	}

	public String getSerieInicialNombre() {
		return serieInicialNombre;
	}

	public void setSerieInicialNombre(String serieInicialNombre) {
		this.serieInicialNombre = serieInicialNombre;
	}

	public String getParametro() {
		return parametro;
	}

	public void setParametro(String parametro) {
		this.parametro = parametro;
	}

	public String getCondicion() {
		return condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	public String getLeftJoin() {
		return leftJoin;
	}

	public void setLeftJoin(String leftJoin) {
		this.leftJoin = leftJoin;
	}
}
