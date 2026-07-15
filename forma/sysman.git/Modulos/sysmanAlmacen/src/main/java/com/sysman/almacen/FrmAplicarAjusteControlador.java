/*-
 * FrmAplicarAjusteControlador.java
 *
 * 1.0
 * 
 * 01/12/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.FrmAplicarAjusteControladorUrlEnum;
import com.sysman.almacen.enums.MovimientosControladorEnum;
import com.sysman.almacen.enums.MovimientosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/12/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmAplicarAjusteControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private String movDebito;
	private String movCredito;
	private String dependencia;
	private String responsable;
	private String fuente;
	private String referencia;
	private String auxiliar;
	private String centroCosto;
	private String observaciones;
	private List<Registro> listaMovimientoDebito;
	private List<Registro> listaMovimientoCredito;
	private List<Registro> listadependencia;
	private RegistroDataModelImpl listaresponsable;
	private RegistroDataModelImpl listaFuenteRecurso;
	private RegistroDataModelImpl listaReferencia;
	private RegistroDataModelImpl listaAuxiliar;
	private RegistroDataModelImpl listaCentroDeCosto;
	private Date fechaActual;
	private int ano;
	private String bodegaOrigenDebito;
	private String bodegaOrigenCredito;

	@EJB
	private EjbAlmacenCincoRemote cincoRemote;
	private String bodega;
	private String fechaCorte;
	private String sucursalResponsable;
	/**
	 * Crea una nueva instancia de FrmAplicarAjusteControlador
	 */
	public FrmAplicarAjusteControlador() {
		super();
		compania = SessionUtil.getCompania();
		fechaActual = new Date();
		ano = SysmanFunciones.ano(fechaActual);
		fuente = "99999999999999999999";
		referencia = "99999999999999999999";
		auxiliar = "99999999999999999999";
		centroCosto = "99999999999999999999";

		try {
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {
				bodega = extraerString(parametrosEntrada.get("bodega"));
				fechaCorte = extraerString(parametrosEntrada.get("fechaCorte"));
			} else {
				SessionUtil.redireccionarMenuPermisos();
			}
			numFormulario = GeneralCodigoFormaEnum.APLICAR_AJUSTE_CONTROLADOR.getCodigo();// 2554;
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
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
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaMovimientoDebito();
		cargarListaMovimientoCredito();
		cargarListadependencia();
		cargarListaresponsable();
		cargarListaFuenteRecurso();
		cargarListaReferencia();
		cargarListaAuxiliar();
		cargarListaCentroDeCosto();
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
	}
	
	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}
	
	

	public void oprimirAplicarAjuste() {
		try {
		    Date fechaActual = new Date();
		    int anio = SysmanFunciones.ano(fechaActual);

		    String resultado = cincoRemote.aplicarAjusteInventario(
		            compania,
		            anio,
		            bodega,
		            bodega,
		            movCredito,
		            movDebito,
		            dependencia,
		            responsable,
		            sucursalResponsable,
		            responsable,
		            fuente,
		            referencia,
		            auxiliar,
		            centroCosto,
		            observaciones,
		            fechaCorte,      
		            bodegaOrigenDebito,
		            bodegaOrigenCredito,
		            SessionUtil.getUser().getCodigo()
		    );

		    JsfUtil.agregarMensajeInformativo(resultado);

		} catch (SystemException e) {
		    logger.error(e.getMessage(), e);
		    JsfUtil.agregarMensajeError(e.getMessage());
		}
		
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
	 * Carga la lista listaMovimientoDebito
	 *
	 */
	public void cargarListaMovimientoDebito() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), "C");
		param.put(GeneralParameterEnum.CONCEPTO.getName(), "AI");
		param.put(GeneralParameterEnum.CLASE.getName(), "E");

		try {
			String urlEnumId = FrmAplicarAjusteControladorUrlEnum.URL139034.getValue();
			listaMovimientoDebito = RegistroConverter.toListRegistro(requestManager
					.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMovimientoCredito
	 *
	 */
	public void cargarListaMovimientoCredito() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), "C");
		param.put(GeneralParameterEnum.CONCEPTO.getName(), "AI");
		param.put(GeneralParameterEnum.CLASE.getName(), "S");

		try {
			String urlEnumId = FrmAplicarAjusteControladorUrlEnum.URL139034.getValue();
			listaMovimientoCredito = RegistroConverter.toListRegistro(requestManager
					.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listadependencia
	 *
	 */
	public void cargarListadependencia() {
		listadependencia = null;
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(MovimientosControladorEnum.PARAM10.getValue(), "20");

		try {
			String urlEnumId = FrmAplicarAjusteControladorUrlEnum.URL62029.getValue();
			listadependencia = RegistroConverter.toListRegistro(requestManager
					.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl(), param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaresponsable
	 *
	 */
	public void cargarListaresponsable() {
		listaresponsable = crearListaResponsables(dependencia);
	}

	/**
	 * Crea una lista de combo grande con los responsables activos y responsables de
	 * almacen por dependencia.
	 *
	 * @param dependencia C&oacute;digo de la dependencia.
	 * @return lista de responsables.
	 */
	private RegistroDataModelImpl crearListaResponsables(Object dependencia) {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);
		String urlEnumId = MovimientosControladorUrlEnum.URL79165.getValue();
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
		return new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				MovimientosControladorEnum.PARAM6.getValue());
	}

	/**
	 * 
	 * Carga la lista listaFuenteRecurso
	 *
	 */
	public void cargarListaFuenteRecurso() {
		String urlEnum = MovimientosControladorUrlEnum.URL1223.getValue();
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnum);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), String.valueOf(ano));

		listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaReferencia
	 *
	 */
	public void cargarListaReferencia() {
		String urlEnum = MovimientosControladorUrlEnum.URL1224.getValue();
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnum);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), String.valueOf(ano));

		listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaAuxiliar
	 *
	 */
	public void cargarListaAuxiliar() {
		listaAuxiliar = null;
		String urlEnumId = MovimientosControladorUrlEnum.URL6299.getValue();
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), String.valueOf(ano));

		listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaCentroDeCosto
	 *
	 */
	public void cargarListaCentroDeCosto() {
		listaCentroDeCosto = null;
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), String.valueOf(ano));
		String urlEnumId = MovimientosControladorUrlEnum.URL84866.getValue();
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
		listaCentroDeCosto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaMovimientoDebito
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaMovimientoDebito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		movDebito = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		bodegaOrigenDebito = SysmanFunciones.toString(registroAux.getCampos().get("CLASE_BODEGA_ORIGEN"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaMovimientoCredito
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaMovimientoCredito(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		movCredito = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		bodegaOrigenCredito = SysmanFunciones.toString(registroAux.getCampos().get("CLASE_BODEGA_ORIGEN"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listadependencia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFiladependencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependencia = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		responsable = null;
		cargarListaresponsable();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaresponsable
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaresponsable(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		responsable = SysmanFunciones.toString(registroAux.getCampos().get("CEDULA"));
		sucursalResponsable =  SysmanFunciones.toString(registroAux.getCampos().get("SUCURSAL"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteRecurso
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteRecurso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuente = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferencia
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		referencia = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliar
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCentroDeCosto
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroDeCosto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroCosto = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable movDebito
	 * 
	 * @return movDebito
	 */
	public String getMovDebito() {
		return movDebito;
	}

	/**
	 * Asigna la variable movDebito
	 * 
	 * @param movDebito Variable a asignar en movDebito
	 */
	public void setMovDebito(String movDebito) {
		this.movDebito = movDebito;
	}

	/**
	 * Retorna la variable movCredito
	 * 
	 * @return movCredito
	 */
	public String getMovCredito() {
		return movCredito;
	}

	/**
	 * Asigna la variable movCredito
	 * 
	 * @param movCredito Variable a asignar en movCredito
	 */
	public void setMovCredito(String movCredito) {
		this.movCredito = movCredito;
	}

	/**
	 * Retorna la variable dependencia
	 * 
	 * @return dependencia
	 */
	public String getDependencia() {
		return dependencia;
	}

	/**
	 * Asigna la variable dependencia
	 * 
	 * @param dependencia Variable a asignar en dependencia
	 */
	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}

	/**
	 * Retorna la variable responsable
	 * 
	 * @return responsable
	 */
	public String getResponsable() {
		return responsable;
	}

	/**
	 * Asigna la variable responsable
	 * 
	 * @param responsable Variable a asignar en responsable
	 */
	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	/**
	 * @return the fuente
	 */
	public String getFuente() {
		return fuente;
	}

	/**
	 * @param fuente the fuente to set
	 */
	public void setFuente(String fuente) {
		this.fuente = fuente;
	}

	/**
	 * @return the referencia
	 */
	public String getReferencia() {
		return referencia;
	}

	/**
	 * @param referencia the referencia to set
	 */
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	/**
	 * @return the auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}

	/**
	 * @param auxiliar the auxiliar to set
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	/**
	 * @return the centroCosto
	 */
	public String getCentroCosto() {
		return centroCosto;
	}

	/**
	 * @param centroCosto the centroCosto to set
	 */
	public void setCentroCosto(String centroCosto) {
		this.centroCosto = centroCosto;
	}

	/**
	 * @return the compania
	 */
	public String getCompania() {
		return compania;
	}

	/**
	 * @return the observaciones
	 */
	public String getObservaciones() {
		return observaciones;
	}

	/**
	 * @param observaciones the observaciones to set
	 */
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	/**
	 * @return the listaMovimientoDebito
	 */
	public List<Registro> getListaMovimientoDebito() {
		return listaMovimientoDebito;
	}

	/**
	 * @param listaMovimientoDebito the listaMovimientoDebito to set
	 */
	public void setListaMovimientoDebito(List<Registro> listaMovimientoDebito) {
		this.listaMovimientoDebito = listaMovimientoDebito;
	}

	/**
	 * @return the listaMovimientoCredito
	 */
	public List<Registro> getListaMovimientoCredito() {
		return listaMovimientoCredito;
	}

	/**
	 * @param listaMovimientoCredito the listaMovimientoCredito to set
	 */
	public void setListaMovimientoCredito(List<Registro> listaMovimientoCredito) {
		this.listaMovimientoCredito = listaMovimientoCredito;
	}

	/**
	 * @return the listadependencia
	 */
	public List<Registro> getListadependencia() {
		return listadependencia;
	}

	/**
	 * @param listadependencia the listadependencia to set
	 */
	public void setListadependencia(List<Registro> listadependencia) {
		this.listadependencia = listadependencia;
	}

	/**
	 * @return the listaresponsable
	 */
	public RegistroDataModelImpl getListaresponsable() {
		return listaresponsable;
	}

	/**
	 * @param listaresponsable the listaresponsable to set
	 */
	public void setListaresponsable(RegistroDataModelImpl listaresponsable) {
		this.listaresponsable = listaresponsable;
	}

	/**
	 * @return the listaFuenteRecurso
	 */
	public RegistroDataModelImpl getListaFuenteRecurso() {
		return listaFuenteRecurso;
	}

	/**
	 * @param listaFuenteRecurso the listaFuenteRecurso to set
	 */
	public void setListaFuenteRecurso(RegistroDataModelImpl listaFuenteRecurso) {
		this.listaFuenteRecurso = listaFuenteRecurso;
	}

	/**
	 * @return the listaReferencia
	 */
	public RegistroDataModelImpl getListaReferencia() {
		return listaReferencia;
	}

	/**
	 * @param listaReferencia the listaReferencia to set
	 */
	public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
		this.listaReferencia = listaReferencia;
	}

	/**
	 * @return the listaAuxiliar
	 */
	public RegistroDataModelImpl getListaAuxiliar() {
		return listaAuxiliar;
	}

	/**
	 * @param listaAuxiliar the listaAuxiliar to set
	 */
	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	/**
	 * @return the listaCentroDeCosto
	 */
	public RegistroDataModelImpl getListaCentroDeCosto() {
		return listaCentroDeCosto;
	}

	/**
	 * @param listaCentroDeCosto the listaCentroDeCosto to set
	 */
	public void setListaCentroDeCosto(RegistroDataModelImpl listaCentroDeCosto) {
		this.listaCentroDeCosto = listaCentroDeCosto;
	}

	// </SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	

//</SET_GET_LISTAS_COMBO_GRANDE>
}
