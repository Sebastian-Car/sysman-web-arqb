/*-
 * FrmAuxiliarPresupuestalPorContrato.java
 *
 * 1.0
 * 
 * 23/01/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import java.io.IOException;
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
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FrmAuxiliarPresupuestalPorContratoUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 23/01/2024
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class FrmAuxiliarPresupuestalPorContrato extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private String tipoContratoInicial;

	private String tipoContratoFinal;

	private String terceroInicial;

	private String terceroFinal;

	private String numeroContratoInicial;

	private String numeroContratoFinal;
	
	private String nombreTipoInicial;
	private String nombreTipoFinal;
	
	private String nombreTerceroInicial;
	private String nombreTerceroFinal;
	
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	private List<Registro> listaAno;

	private RegistroDataModelImpl listaTipoContratoInicial;

	private RegistroDataModelImpl listaTipoContratoFinal;

	private RegistroDataModelImpl listaTerceroInicial;

	private RegistroDataModelImpl listaTerceroFinal;

	private RegistroDataModelImpl listaNumeroContratoInicial;

	private RegistroDataModelImpl listaNumeroContratoFinal;

	/**
	 * Crea una nueva instancia de FrmAuxiliarPresupuestalPorContrato
	 */
	public FrmAuxiliarPresupuestalPorContrato() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRMAUXILIARPRESUPUESTALPORCONTRATO.getCodigo();
			validarPermisos();
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
		cargarListaTipoContratoInicial();
		cargarListaTipoContratoFinal();
		cargarListaTerceroInicial();
		cargarListaTerceroFinal();
		cargarListaNumeroContratoInicial();
		cargarListaNumeroContratoFinal();

		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
	}

//<METODOS_CARGAR_LISTA>
	
	/**
	 * 
	 * Carga la lista listaTipoContratoInicial
	 *
	 */
	public void cargarListaTipoContratoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmAuxiliarPresupuestalPorContratoUrlEnum.URL004.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTipoContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());
	}

	/**
	 * 
	 * Carga la lista listaTipoContratoFinal
	 *
	 */
	public void cargarListaTipoContratoFinal() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmAuxiliarPresupuestalPorContratoUrlEnum.URL004.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTipoContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());

	}

	/**
	 * 
	 * Carga la lista listaTerceroInicial
	 *
	 */
	public void cargarListaTerceroInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmAuxiliarPresupuestalPorContratoUrlEnum.URL002.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NIT.getName());
	}

	/**
	 * 
	 * Carga la lista listaTerceroFinal
	 *
	 */
	public void cargarListaTerceroFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmAuxiliarPresupuestalPorContratoUrlEnum.URL002.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCEROINICIAL.getName(), terceroInicial);

		listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NIT.getName());
	}

	/**
	 * 
	 * Carga la lista listaNumeroContratoInicial
	 *
	 */
	public void cargarListaNumeroContratoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmAuxiliarPresupuestalPorContratoUrlEnum.URL005.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCEROINICIAL.getName(), terceroInicial);
		param.put(GeneralParameterEnum.TERCEROFINAL.getName(), terceroFinal);
		param.put(GeneralParameterEnum.TIPOCONTRATOINICIAL.getName(), tipoContratoInicial);
		param.put(GeneralParameterEnum.TIPOCONTRATOFINAL.getName(), tipoContratoFinal);
		
		listaNumeroContratoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());
	}

	/**
	 * 
	 * Carga la lista listaNumeroContratoFinal
	 *
	 */
	public void cargarListaNumeroContratoFinal() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmAuxiliarPresupuestalPorContratoUrlEnum.URL005.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCEROINICIAL.getName(), terceroInicial);
		param.put(GeneralParameterEnum.TERCEROFINAL.getName(), terceroFinal);
		param.put(GeneralParameterEnum.TIPOCONTRATOINICIAL.getName(), tipoContratoInicial);
		param.put(GeneralParameterEnum.TIPOCONTRATOFINAL.getName(), tipoContratoFinal);
		
		listaNumeroContratoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaReporte(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaReporte(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	private void generaReporte(FORMATOS formato) {

		try {

			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			String nombreConsulta = "002538AUXILIARPORCONTRATOIDCBIS";
			reemplazar.put("compania", compania);
			reemplazar.put("tipoContratoInicial", tipoContratoInicial);
			reemplazar.put("tipoContratoFinal", tipoContratoFinal);
			reemplazar.put("terceroInicial", terceroInicial);
			reemplazar.put("terceroFinal", terceroFinal);
			reemplazar.put("numeroContratoInicial", numeroContratoInicial);
			reemplazar.put("numeroContratoFinal", numeroContratoFinal);
			
			Reporteador.resuelveConsulta(nombreConsulta, Integer.parseInt(SessionUtil.getModulo()), reemplazar,
					parametros);

			archivoDescarga = JsfUtil.exportarStreamed(nombreConsulta, parametros, ConectorPool.ESQUEMA_SYSMAN,
					formato);
		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoContratoInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoContratoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoContratoInicial = registroAux.getCampos().get("NUMERO").toString();
		nombreTipoInicial= registroAux.getCampos().get("NOMBRE").toString();
	
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoContratoFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoContratoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoContratoFinal = registroAux.getCampos().get("NUMERO").toString();
		nombreTipoFinal= registroAux.getCampos().get("NOMBRE").toString();
		cargarListaNumeroContratoInicial();
		cargarListaNumeroContratoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTerceroInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroInicial = registroAux.getCampos().get("NIT").toString();
		nombreTerceroInicial = registroAux.getCampos().get("NOMBRE").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTerceroFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroFinal = registroAux.getCampos().get("NIT").toString();
		nombreTerceroFinal= registroAux.getCampos().get("NOMBRE").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNumeroContratoInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNumeroContratoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		numeroContratoInicial = registroAux.getCampos().get("NUMERO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaNumeroContratoFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNumeroContratoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		numeroContratoFinal = registroAux.getCampos().get("NUMERO").toString();
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipoContratoInicial
	 * 
	 * @return tipoContratoInicial
	 */
	public String getTipoContratoInicial() {
		return tipoContratoInicial;
	}

	/**
	 * Asigna la variable tipoContratoInicial
	 * 
	 * @param tipoContratoInicial Variable a asignar en tipoContratoInicial
	 */
	public void setTipoContratoInicial(String tipoContratoInicial) {
		this.tipoContratoInicial = tipoContratoInicial;
	}

	/**
	 * Retorna la variable tipoContratoFinal
	 * 
	 * @return tipoContratoFinal
	 */
	public String getTipoContratoFinal() {
		return tipoContratoFinal;
	}

	/**
	 * Asigna la variable tipoContratoFinal
	 * 
	 * @param tipoContratoFinal Variable a asignar en tipoContratoFinal
	 */
	public void setTipoContratoFinal(String tipoContratoFinal) {
		this.tipoContratoFinal = tipoContratoFinal;
	}

	/**
	 * Retorna la variable terceroInicial
	 * 
	 * @return terceroInicial
	 */
	public String getTerceroInicial() {
		return terceroInicial;
	}

	/**
	 * Asigna la variable terceroInicial
	 * 
	 * @param terceroInicial Variable a asignar en terceroInicial
	 */
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}

	/**
	 * Retorna la variable terceroFinal
	 * 
	 * @return terceroFinal
	 */
	public String getTerceroFinal() {
		return terceroFinal;
	}

	/**
	 * Asigna la variable terceroFinal
	 * 
	 * @param terceroFinal Variable a asignar en terceroFinal
	 */
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

	/**
	 * Retorna la variable numeroContratoInicial
	 * 
	 * @return numeroContratoInicial
	 */
	public String getNumeroContratoInicial() {
		return numeroContratoInicial;
	}

	/**
	 * Asigna la variable numeroContratoInicial
	 * 
	 * @param numeroContratoInicial Variable a asignar en numeroContratoInicial
	 */
	public void setNumeroContratoInicial(String numeroContratoInicial) {
		this.numeroContratoInicial = numeroContratoInicial;
	}

	/**
	 * Retorna la variable numeroContratoFinal
	 * 
	 * @return numeroContratoFinal
	 */
	public String getNumeroContratoFinal() {
		return numeroContratoFinal;
	}

	/**
	 * Asigna la variable numeroContratoFinal
	 * 
	 * @param numeroContratoFinal Variable a asignar en numeroContratoFinal
	 */
	public void setNumeroContratoFinal(String numeroContratoFinal) {
		this.numeroContratoFinal = numeroContratoFinal;
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
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTipoContratoInicial
	 * 
	 * @return listaTipoContratoInicial
	 */
	public RegistroDataModelImpl getListaTipoContratoInicial() {
		return listaTipoContratoInicial;
	}

	/**
	 * Asigna la lista listaTipoContratoInicial
	 * 
	 * @param listaTipoContratoInicial Variable a asignar en
	 *                                 listaTipoContratoInicial
	 */
	public void setListaTipoContratoInicial(RegistroDataModelImpl listaTipoContratoInicial) {
		this.listaTipoContratoInicial = listaTipoContratoInicial;
	}

	/**
	 * Retorna la lista listaTipoContratoFinal
	 * 
	 * @return listaTipoContratoFinal
	 */
	public RegistroDataModelImpl getListaTipoContratoFinal() {
		return listaTipoContratoFinal;
	}

	/**
	 * Asigna la lista listaTipoContratoFinal
	 * 
	 * @param listaTipoContratoFinal Variable a asignar en listaTipoContratoFinal
	 */
	public void setListaTipoContratoFinal(RegistroDataModelImpl listaTipoContratoFinal) {
		this.listaTipoContratoFinal = listaTipoContratoFinal;
	}

	/**
	 * Retorna la lista listaTerceroInicial
	 * 
	 * @return listaTerceroInicial
	 */
	public RegistroDataModelImpl getListaTerceroInicial() {
		return listaTerceroInicial;
	}

	/**
	 * Asigna la lista listaTerceroInicial
	 * 
	 * @param listaTerceroInicial Variable a asignar en listaTerceroInicial
	 */
	public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
		this.listaTerceroInicial = listaTerceroInicial;
	}

	/**
	 * Retorna la lista listaTerceroFinal
	 * 
	 * @return listaTerceroFinal
	 */
	public RegistroDataModelImpl getListaTerceroFinal() {
		return listaTerceroFinal;
	}

	/**
	 * Asigna la lista listaTerceroFinal
	 * 
	 * @param listaTerceroFinal Variable a asignar en listaTerceroFinal
	 */
	public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
		this.listaTerceroFinal = listaTerceroFinal;
	}

	/**
	 * Retorna la lista listaNumeroContratoInicial
	 * 
	 * @return listaNumeroContratoInicial
	 */
	public RegistroDataModelImpl getListaNumeroContratoInicial() {
		return listaNumeroContratoInicial;
	}

	/**
	 * Asigna la lista listaNumeroContratoInicial
	 * 
	 * @param listaNumeroContratoInicial Variable a asignar en
	 *                                   listaNumeroContratoInicial
	 */
	public void setListaNumeroContratoInicial(RegistroDataModelImpl listaNumeroContratoInicial) {
		this.listaNumeroContratoInicial = listaNumeroContratoInicial;
	}

	/**
	 * Retorna la lista listaNumeroContratoFinal
	 * 
	 * @return listaNumeroContratoFinal
	 */
	public RegistroDataModelImpl getListaNumeroContratoFinal() {
		return listaNumeroContratoFinal;
	}

	/**
	 * Asigna la lista listaNumeroContratoFinal
	 * 
	 * @param listaNumeroContratoFinal Variable a asignar en
	 *                                 listaNumeroContratoFinal
	 */
	public void setListaNumeroContratoFinal(RegistroDataModelImpl listaNumeroContratoFinal) {
		this.listaNumeroContratoFinal = listaNumeroContratoFinal;
	}

	public String getNombreTipoInicial() {
		return nombreTipoInicial;
	}

	public void setNombreTipoInicial(String nombreTipoInicial) {
		this.nombreTipoInicial = nombreTipoInicial;
	}

	public String getNombreTipoFinal() {
		return nombreTipoFinal;
	}

	public void setNombreTipoFinal(String nombreTipoFinal) {
		this.nombreTipoFinal = nombreTipoFinal;
	}

	public String getNombreTerceroInicial() {
		return nombreTerceroInicial;
	}

	public void setNombreTerceroInicial(String nombreTerceroInicial) {
		this.nombreTerceroInicial = nombreTerceroInicial;
	}

	public String getNombreTerceroFinal() {
		return nombreTerceroFinal;
	}

	public void setNombreTerceroFinal(String nombreTerceroFinal) {
		this.nombreTerceroFinal = nombreTerceroFinal;
	}
	
	

}
