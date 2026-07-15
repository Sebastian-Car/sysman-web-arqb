/*-
 * Frmauxiliarconsaldosporauxiliarg.java
 *
 * 1.0
 * 
 * 20/05/2022
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrmauxiliarconsaldosporauxiliargUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 20/05/2022
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class Frmauxiliarconsaldosporauxiliarg extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>

	private String tipoIni = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String tipoFin = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String cuentaFinal = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String auxiliarFinal = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String anio;
	private String mesInicial;
	private String mesFinal;
	private String modulo;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAnio;
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listaTipoInicial;
	private RegistroDataModelImpl listaTipoFinal;
	private RegistroDataModelImpl listaCuentaInicial;
	private RegistroDataModelImpl listaCuentaFinal;
	private RegistroDataModelImpl listaAuxiliarInicial;
	private RegistroDataModelImpl listaAuxiliarFinal;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	
	@EJB
	EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * Crea una nueva instancia de Frmauxiliarconsaldosporauxiliarg
	 */
	public Frmauxiliarconsaldosporauxiliarg() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		tipoIni = SysmanConstantes.DEFECTOINICIAL_STRING;
		tipoFin = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOINICIAL_STRING;
		auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		auxiliarFinal = SysmanConstantes.DEFECTOINICIAL_STRING;

		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_AUXILIAR_CON_SALDOS_POR_AUXILIARG.getCodigo();
			validarPermisos();

		} catch (Exception ex) {
			Logger.getLogger(Frmauxiliarconsaldosporauxiliarg.class.getName()).log(Level.SEVERE, null, ex);
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
//<CARGAR_LISTA>
		anio= String.valueOf(SysmanFunciones.ano(new Date()));
		abrirFormulario();
		cargarListaAnio();
		cargarListaTipoInicial();
		cargarListaTipoFinal();
		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
		cargarListaAuxiliarInicial();
		cargarListaAuxiliarFinal();

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

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAnio = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmauxiliarconsaldosporauxiliargUrlEnum.URL001.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaTipoInicial
	 *
	 */
	public void cargarListaTipoInicial() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmauxiliarconsaldosporauxiliargUrlEnum.URL002.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaTipoFinal
	 *
	 */
	public void cargarListaTipoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmauxiliarconsaldosporauxiliargUrlEnum.URL003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TIPOINICIAL", tipoIni);

		listaTipoFinal= new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 */
	public void cargarListaCuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmauxiliarconsaldosporauxiliargUrlEnum.URL004.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 */
	public void cargarListaCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmauxiliarconsaldosporauxiliargUrlEnum.URL005.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put("CODIGOINICIAL", cuentaInicial);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaAuxiliarInicial
	 *
	 */
	public void cargarListaAuxiliarInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmauxiliarconsaldosporauxiliargUrlEnum.URL006 // ptde
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaAuxiliarFinal
	 *
	 */
	public void cargarListaAuxiliarFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmauxiliarconsaldosporauxiliargUrlEnum.URL007 // ptde
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put("AUXILIARINICIAL", auxiliarInicial);

		listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarReporte(FORMATOS.EXCEL97);
		// </CODIGO_DESARROLLADO>
	}

	private void generarReporte(FORMATOS formato) {
		try {

			Map<String, Object> reemplazar = new HashMap<>();
			String reporte = "002375AUXPORSALDOAUXGEN";

			reemplazar.put("compania", compania);
			reemplazar.put("anio", anio);
			reemplazar.put("mesInicial", mesInicial);
			reemplazar.put("mesFinal", mesFinal);
			reemplazar.put("comprobanteInicial", tipoIni);
			reemplazar.put("comprobanteFinal", tipoFin);
			reemplazar.put("cuentaInicial", 24010201);			
			reemplazar.put("cuentaFinal", 51117901);			
			reemplazar.put("auxiliarInicial", 1);
			reemplazar.put("auxiliarFinal", auxiliarFinal);

			String nombreMesInicial = ejbSysmanUtil
	                 .mostrarNombreDeMes(Integer.parseUnsignedInt(mesInicial))
	                 .toUpperCase();
	         String nombreMesFinal = ejbSysmanUtil
	                 .mostrarNombreDeMes(Integer.parseUnsignedInt(mesFinal))
	                 .toUpperCase();

			Map<String, Object> parametros = new HashMap<>();

	         parametros.put("PR_TITULO", "MES DE " + nombreMesInicial + " AL MES DE  " + nombreMesFinal +" DEL "+ anio);
			parametros.put("PR_TITULO_CUENTAS", " Entre Cuentas " + cuentaInicial + " Y " + cuentaFinal);
			parametros.put("PR_TITULO_CENTROS", " Entre Auxiliares  " + auxiliarInicial + " Y " + auxiliarFinal);


				Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
				archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		

		} catch (JRException | IOException | SysmanException | NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Anio en la fila seleccionada dentro de
	 * la grilla
	 * 
	 * 
	 * @param rowNum indice de la fila seleccionada
	 */
	public void cambiarAnioC(int rowNum) {

		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoIni = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoFinal
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoFin = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliarInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarInicial = registroAux.getCampos().get("CODIGO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliarFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarFinal = registroAux.getCampos().get("CODIGO").toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipoIni
	 * 
	 * @return tipoIni
	 */
	public String getTipoIni() {
		return tipoIni;
	}

	/**
	 * Asigna la variable tipoIni
	 * 
	 * @param tipoIni Variable a asignar en tipoIni
	 */
	public void setTipoIni(String tipoIni) {
		this.tipoIni = tipoIni;
	}

	/**
	 * Retorna la variable tipoFin
	 * 
	 * @return tipoFin
	 */
	public String getTipoFin() {
		return tipoFin;
	}

	/**
	 * Asigna la variable tipoFin
	 * 
	 * @param tipoFin Variable a asignar en tipoFin
	 */
	public void setTipoFin(String tipoFin) {
		this.tipoFin = tipoFin;
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
	 * Retorna la variable auxiliarInicial
	 * 
	 * @return auxiliarInicial
	 */
	public String getAuxiliarInicial() {
		return auxiliarInicial;
	}

	/**
	 * Asigna la variable auxiliarInicial
	 * 
	 * @param auxiliarInicial Variable a asignar en auxiliarInicial
	 */
	public void setAuxiliarInicial(String auxiliarInicial) {
		this.auxiliarInicial = auxiliarInicial;
	}

	/**
	 * Retorna la variable auxiliarFinal
	 * 
	 * @return auxiliarFinal
	 */
	public String getAuxiliarFinal() {
		return auxiliarFinal;
	}

	/**
	 * Asigna la variable auxiliarFinal
	 * 
	 * @param auxiliarFinal Variable a asignar en auxiliarFinal
	 */
	public void setAuxiliarFinal(String auxiliarFinal) {
		this.auxiliarFinal = auxiliarFinal;
	}

	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio Variable a asignar en anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	/**
	 * Retorna la variable mesInicial
	 * 
	 * @return mesInicial
	 */
	public String getMesInicial() {
		return mesInicial;
	}

	/**
	 * Asigna la variable mesInicial
	 * 
	 * @param mesInicial Variable a asignar en mesInicial
	 */
	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}

	/**
	 * Retorna la variable mesFinal
	 * 
	 * @return mesFinal
	 */
	public String getMesFinal() {
		return mesFinal;
	}

	/**
	 * Asigna la variable mesFinal
	 * 
	 * @param mesFinal Variable a asignar en mesFinal
	 */
	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}

	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio Variable a asignar en listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTipoInicial
	 * 
	 * @return listaTipoInicial
	 */
	public RegistroDataModelImpl getListaTipoInicial() {
		return listaTipoInicial;
	}

	/**
	 * Asigna la lista listaTipoInicial
	 * 
	 * @param listaTipoInicial Variable a asignar en listaTipoInicial
	 */
	public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
		this.listaTipoInicial = listaTipoInicial;
	}

	/**
	 * Retorna la lista listaTipoFinal
	 * 
	 * @return listaTipoFinal
	 */
	public RegistroDataModelImpl getListaTipoFinal() {
		return listaTipoFinal;
	}

	/**
	 * Asigna la lista listaTipoFinal
	 * 
	 * @param listaTipoFinal Variable a asignar en listaTipoFinal
	 */
	public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
		this.listaTipoFinal = listaTipoFinal;
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
	 * @param listaCuentaInicial Variable a asignar en listaCuentaInicial
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
	 * @param listaCuentaFinal Variable a asignar en listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}

	/**
	 * Retorna la lista listaAuxiliarInicial
	 * 
	 * @return listaAuxiliarInicial
	 */
	public RegistroDataModelImpl getListaAuxiliarInicial() {
		return listaAuxiliarInicial;
	}

	/**
	 * Asigna la lista listaAuxiliarInicial
	 * 
	 * @param listaAuxiliarInicial Variable a asignar en listaAuxiliarInicial
	 */
	public void setListaAuxiliarInicial(RegistroDataModelImpl listaAuxiliarInicial) {
		this.listaAuxiliarInicial = listaAuxiliarInicial;
	}

	/**
	 * Retorna la lista listaAuxiliarFinal
	 * 
	 * @return listaAuxiliarFinal
	 */
	public RegistroDataModelImpl getListaAuxiliarFinal() {
		return listaAuxiliarFinal;
	}

	/**
	 * Asigna la lista listaAuxiliarFinal
	 * 
	 * @param listaAuxiliarFinal Variable a asignar en listaAuxiliarFinal
	 */
	public void setListaAuxiliarFinal(RegistroDataModelImpl listaAuxiliarFinal) {
		this.listaAuxiliarFinal = listaAuxiliarFinal;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>
}
