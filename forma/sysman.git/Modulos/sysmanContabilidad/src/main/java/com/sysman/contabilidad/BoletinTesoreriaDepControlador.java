/*-
 * boletinTesoreriaDepControlador.java
 *
 * 1.0
 * 
 * 18/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.IOException;
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
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BoletinTesoreriaDepControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
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
 * Clase que permite generar un reporte por codigo de cuentas fecha y año de
 * boletines de tesorería departamental
 *
 * @version 1.0, 18/12/2018
 * @author jrojas
 */
@ManagedBean
@ViewScoped
public class BoletinTesoreriaDepControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>
	private String codigoInicialO;
	private String codigoFinalO;
	private String mesFinalO;
	private String mesInicialO;
	private String ano;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	@EJB
	EjbSysmanUtil ejbSysmanUtil;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>

	private List<Registro> listamesFinal;
	private List<Registro> listamesInicial;
	private List<Registro> listaano;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listacodigoInicial;
	private RegistroDataModelImpl listacodigoFinal;

	// </DECLARAR_LISTAS_COMBO_GRANDE>

	public BoletinTesoreriaDepControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// 2012
			numFormulario = GeneralCodigoFormaEnum.BOLETIN_TESORERIA_DEP_CONTROLADOR.getCodigo();
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

		cargarListaano();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>

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
		ano = String.valueOf(SysmanFunciones.ano(new Date()));
		cargarListamesInicial();
		cargarListacodigoInicial();
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listamesFinal
	 *
	 * 
	 */
	public void cargarListamesFinal() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put("MESINICIAL", mesInicialO);

			listamesFinal = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													BoletinTesoreriaDepControladorUrlEnum.URL7046.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listamesInicial
	 *
	 * 
	 */
	public void cargarListamesInicial() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);

			listamesInicial = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													BoletinTesoreriaDepControladorUrlEnum.URL7045.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listaano
	 *
	 * 
	 */
	public void cargarListaano() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaano = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													BoletinTesoreriaDepControladorUrlEnum.URL4001.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listacodigoInicial
	 *
	 * 
	 */
	public void cargarListacodigoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(BoletinTesoreriaDepControladorUrlEnum.URL16176.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANIO", ano);

		listacodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listacodigoFinal
	 *
	 * 
	 */
	public void cargarListacodigoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(BoletinTesoreriaDepControladorUrlEnum.URL16178.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("ANIO", ano);
		param.put("NUMERO", codigoInicialO);

		listacodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton generaExcel en la vista
	 *
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
	 * 
	 */
	public void oprimirgeneraPDF() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	private void generarReporte(ReportesBean.FORMATOS formato) {
		try {
			String nombreMesInicial = ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mesInicialO)).toUpperCase();

			String nombreMesFinal = ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mesFinalO)).toUpperCase();

			HashMap<String, Object> reemplazar = new HashMap<>();
			HashMap<String, Object> parametros = new HashMap<>();
			String reporte;
			reporte = "001977BoletinDeBancos";

			reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			reemplazar.put("ano", ano);
			reemplazar.put("mesInicial", mesInicialO);
			reemplazar.put("mesFinal", mesFinalO);
			reemplazar.put("cuentaInicial", codigoInicialO);
			reemplazar.put("cuentaFinal", codigoFinalO);
			reemplazar.put("fechaInicial", mesInicialO);
			reemplazar.put("fechaFinal", mesFinalO);

			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre().toUpperCase());
			parametros.put("PR_MESINICIAL", nombreMesInicial);
			parametros.put("PR_MESFINAL", nombreMesFinal);
			parametros.put("PR_ANIO", ano);
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		}

		catch (JRException | IOException | SysmanException | NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control mesInicial
	 * 
	 * 
	 * 
	 */
	public void cambiarmesInicial() {
		// <CODIGO_DESARROLLADO>
		cargarListamesFinal();

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control ano
	 * 
	 * 
	 * 
	 */
	public void cambiarano() {
		// <CODIGO_DESARROLLADO>
		cargarListamesInicial();
		cargarListacodigoInicial();
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacodigoInicial
	 *
	 *
	 * 
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodigoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoInicialO = registroAux.getCampos().get("CODIGO").toString();

		codigoFinalO = null;
		cargarListacodigoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacodigoFinal
	 *
	 *
	 * 
	 * @param event
	 *            objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodigoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoFinalO = registroAux.getCampos().get("CODIGO").toString();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable codigoInicialO
	 * 
	 * @return codigoInicialO
	 */
	public String getCodigoInicialO() {
		return codigoInicialO;
	}

	/**
	 * Asigna la variable codigoInicialO
	 * 
	 * @param codigoInicialO
	 *            Variable a asignar en codigoInicialO
	 */
	public void setCodigoInicialO(String codigoInicialO) {
		this.codigoInicialO = codigoInicialO;
	}

	/**
	 * Retorna la variable codigoFinalO
	 * 
	 * @return codigoFinalO
	 */
	public String getCodigoFinalO() {
		return codigoFinalO;
	}

	/**
	 * Asigna la variable codigoFinalO
	 * 
	 * @param codigoFinalO
	 *            Variable a asignar en codigoFinalO
	 */
	public void setCodigoFinalO(String codigoFinalO) {
		this.codigoFinalO = codigoFinalO;
	}

	/**
	 * Retorna la variable mesFinalO
	 * 
	 * @return mesFinalO
	 */
	public String getMesFinalO() {
		return mesFinalO;
	}

	/**
	 * Asigna la variable mesFinalO
	 * 
	 * @param mesFinalO
	 *            Variable a asignar en mesFinalO
	 */
	public void setMesFinalO(String mesFinalO) {
		this.mesFinalO = mesFinalO;
	}

	/**
	 * Retorna la variable mesInicialO
	 * 
	 * @return mesInicialO
	 */
	public String getMesInicialO() {
		return mesInicialO;
	}

	/**
	 * Asigna la variable mesInicialO
	 * 
	 * @param mesInicialO
	 *            Variable a asignar en mesInicialO
	 */
	public void setMesInicialO(String mesInicialO) {
		this.mesInicialO = mesInicialO;
	}

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
	 * Retorna la lista listamesFinal
	 * 
	 * @return listamesFinal
	 */
	public List<Registro> getListamesFinal() {
		return listamesFinal;
	}

	/**
	 * Asigna la lista listamesFinal
	 * 
	 * @param listamesFinal
	 *            Variable a asignar en listamesFinal
	 */
	public void setListamesFinal(List<Registro> listamesFinal) {
		this.listamesFinal = listamesFinal;
	}

	/**
	 * Retorna la lista listamesInicial
	 * 
	 * @return listamesInicial
	 */
	public List<Registro> getListamesInicial() {
		return listamesInicial;
	}

	/**
	 * Asigna la lista listamesInicial
	 * 
	 * @param listamesInicial
	 *            Variable a asignar en listamesInicial
	 */
	public void setListamesInicial(List<Registro> listamesInicial) {
		this.listamesInicial = listamesInicial;
	}

	/**
	 * Retorna la lista listaano
	 * 
	 * @return listaano
	 */
	public List<Registro> getListaano() {
		return listaano;
	}

	/**
	 * Asigna la lista listaano
	 * 
	 * @param listaano
	 *            Variable a asignar en listaano
	 */
	public void setListaano(List<Registro> listaano) {
		this.listaano = listaano;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listacodigoInicial
	 * 
	 * @return listacodigoInicial
	 */
	public RegistroDataModelImpl getListacodigoInicial() {
		return listacodigoInicial;
	}

	/**
	 * Asigna la lista listacodigoInicial
	 * 
	 * @param listacodigoInicial
	 *            Variable a asignar en listacodigoInicial
	 */
	public void setListacodigoInicial(RegistroDataModelImpl listacodigoInicial) {
		this.listacodigoInicial = listacodigoInicial;
	}

	/**
	 * Retorna la lista listacodigoFinal
	 * 
	 * @return listacodigoFinal
	 */
	public RegistroDataModelImpl getListacodigoFinal() {
		return listacodigoFinal;
	}

	/**
	 * Asigna la lista listacodigoFinal
	 * 
	 * @param listacodigoFinal
	 *            Variable a asignar en listacodigoFinal
	 */
	public void setListacodigoFinal(RegistroDataModelImpl listacodigoFinal) {
		this.listacodigoFinal = listacodigoFinal;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
