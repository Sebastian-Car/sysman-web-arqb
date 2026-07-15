/*-
 * InformeVacaciPorVigenciaControlador.java
 *
 * 1.0
 * 
 * 08/01/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.InformeVacaciPorVigenciaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que genera un informe de vacaciones por Vigencia con filtros año mes y
 * periodo
 *
 * @version 1.0, 08/01/2019
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class InformeVacaciPorVigenciaControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>
	private String ano;
	private String mes;
	private String periodo;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	private List<Registro> listaAno;
	private List<Registro> listaMes;
	private List<Registro> listaPeriodo1;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de InformeVacaciPorVigenciaControlador
	 */
	public InformeVacaciPorVigenciaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// 2019
			numFormulario = GeneralCodigoFormaEnum.INFORME_VACACI_POR_VIGENCIA_CONTROLADOR.getCodigo();
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
		cargarListaAno();
		// </CARGAR_LISTA>

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

		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	public void cargarListaAno() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InformeVacaciPorVigenciaControladorUrlEnum.URL471002.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMes
	 *
	 */
	public void cargarListaMes() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);

			listaMes = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InformeVacaciPorVigenciaControladorUrlEnum.URL7024.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaPeriodo1
	 *
	 */
	public void cargarListaPeriodo1() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.MES.getName(), mes);

			listaPeriodo1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											InformeVacaciPorVigenciaControladorUrlEnum.URL471003.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton generaPdf en la vista
	 *
	 *
	 */
	public void oprimirgeneraPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

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

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * 
	 */
	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		cargarListaMes();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarMes() {
		// <CODIGO_DESARROLLADO>
		cargarListaPeriodo1();
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>

	private void generarReporte(ReportesBean.FORMATOS formato) {

		HashMap<String, Object> reemplazar = new HashMap<>();
		HashMap<String, Object> parametros = new HashMap<>();
		String reporte = "001981INFORMEVACACIONESPORVIGENCIAS";

		reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		reemplazar.put("anio", ano);
		reemplazar.put("mes", mes);
		reemplazar.put("periodo", periodo);
		try {
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre().toUpperCase());
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano
	 *            Variable a asignar en ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes
	 *            Variable a asignar en mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
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
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno
	 *            Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}

	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes
	 *            Variable a asignar en listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}

	/**
	 * Retorna la lista listaPeriodo1
	 * 
	 * @return listaPeriodo1
	 */
	public List<Registro> getListaPeriodo1() {
		return listaPeriodo1;
	}

	/**
	 * Asigna la lista listaPeriodo1
	 * 
	 * @param listaPeriodo1
	 *            Variable a asignar en listaPeriodo1
	 */
	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
}
