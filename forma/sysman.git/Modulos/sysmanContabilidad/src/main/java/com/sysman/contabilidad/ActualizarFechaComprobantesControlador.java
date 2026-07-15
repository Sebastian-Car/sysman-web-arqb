/*-
 * ActualizarFechaComprobantesControlador.java
 *
 * 1.0
 * 
 * 14/10/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.text.ParseException;
import java.util.Date;
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

import com.ibm.icu.text.SimpleDateFormat;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.contabilidad.enums.ActualizarFechaComprobantesControladorUrlEnum;
import com.sysman.contabilidad.enums.AnalisiscarteracxcControladorUrlEnum;
import com.sysman.contabilidad.enums.EgrycomafectadosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 14/10/2021
 * @author aceleita
 */
@ManagedBean
@ViewScoped
public class ActualizarFechaComprobantesControlador extends BeanBaseModal {
	private String compania;
	private String nombreCompania;
	private String anio;
	private String tipo;
	private String nombreTipo;
	private String comprobante;
	private List<Registro> listaCompania;
	private List<Registro> listaAno;
	private RegistroDataModelImpl listaTipo;
	private RegistroDataModelImpl listaComprobante;
	private Date fechaAnterior;
	private Date fechaActual;
	private String descripcion;
	private String valorTotal;

	@EJB
	EjbContabilidadCeroRemote ejbContabilidadCeroRemote;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de ActualizarFechaComprobantesControlador
	 */
	public ActualizarFechaComprobantesControlador() {
		super();
		numFormulario = GeneralCodigoFormaEnum.CAMBIARFECHACOMPROBANTE_CONTROLADOR.getCodigo();
		compania = SessionUtil.getCompania();
		nombreCompania = SessionUtil.getCompania();
		fechaActual = new Date();
		try {
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		cargarListaCompania();
		cargarListaAno();
		cargarListaTipo();
		cargarListaComprobante();
		abrirFormulario();
	}

//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaCompania
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCompania() {
		// 59001 COMPAÑIA
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaCompania = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ActualizarFechaComprobantesControladorUrlEnum.URL0001.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * 
	 * Carga la lista listaAno
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAno() {
		// 4002 COMPAÑIA

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ActualizarFechaComprobantesControladorUrlEnum.URL0002.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaTipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaTipo() {
		// 15005 COMPAÑIA
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		ActualizarFechaComprobantesControladorUrlEnum.URL0003
                                                .getValue());
		
		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, "CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaComprobante
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaComprobante() {
		// 72002 COMPAÑIA, TIPO, AÑO

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), tipo);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		ActualizarFechaComprobantesControladorUrlEnum.URL0004
                                                .getValue());
		
		listaComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param,
                true, "NUMERO");
		
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_CAMBIAR>	
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCompania
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCompania(SelectEvent event) {
		nombreCompania = null;
		nombreTipo = null;
		Registro registroAux = (Registro) event.getObject();
		compania = registroAux.getCampos().get("CODIGO").toString();
		nombreCompania = registroAux.getCampos().get("NOMBRE").toString();
		cargarListaTipo();
		cargarListaComprobante();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaAno
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	
	public void seleccionarFilaAno(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		anio = registroAux.getCampos().get("NUMERO").toString();
		cargarListaComprobante();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipo(SelectEvent event) {
		nombreTipo = null;
		Registro registroAux = (Registro) event.getObject();
		tipo = registroAux.getCampos().get("CODIGO").toString();
		nombreTipo = registroAux.getCampos().get("NOMBRE").toString();
		cargarListaComprobante();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaComprobante
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 * @throws ParseException 
	 */
	public void seleccionarFilaComprobante(SelectEvent event) throws ParseException {
		comprobante = null;
		descripcion = null;
		valorTotal = null;
		fechaAnterior = null;
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		Registro registroAux = (Registro) event.getObject();
		comprobante = registroAux.getCampos().get("NUMERO").toString();
		descripcion = registroAux.getCampos().get("DESCRIPCION").toString();
		valorTotal = registroAux.getCampos().get("VLR_DOCUMENTO").toString();
		String fechaA = registroAux.getCampos().get("FECHA").toString();
		fechaAnterior = formato.parse(fechaA);
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>	
//</METODOS_ARBOL>
//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pantalla en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPantalla() {
		// <CODIGO_DESARROLLADO>
		boolean condicionTipoDeMov = (tipo == null) || tipo.isEmpty();

		if ((fechaActual == null) || condicionTipoDeMov || (comprobante == null) || comprobante.isEmpty()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("MSM_FALTAN_DATOS_PROCESO"));
		}

		try {
			String respuesta = ejbContabilidadCeroRemote.cambiarFechaComprobante(compania, anio, tipo, comprobante, fechaActual,
					SessionUtil.getUser().getCodigo());
			
			Integer.parseInt(respuesta);
				
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (NumberFormatException | SystemException ex) {
			JsfUtil.agregarMensajeError(ex.getMessage());
			Logger.getLogger(ActualizarFechaComprobantesControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Comando32 en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirComando32() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_BOTONES>	
//<METODOS_SUBFORM>	
//</METODOS_SUBFORM>	
//<METODOS_ADICIONALES>	
//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<SET_GET_ATRIBUTOS>

	/**
	 * Retorna la variable compania
	 * 
	 * @return compania
	 */
	public String getCompania() {
		return compania;
	}

	/**
	 * Asigna la variable compania
	 * 
	 * @param compania Variable a asignar en compania
	 */

	public void setCompania(String compania) {
		this.compania = compania;
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
	 * Retorna la variable tipo
	 * 
	 * @return tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * Asigna la variable tipo
	 * 
	 * @param tipo Variable a asignar en tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * Retorna la variable comprobante
	 * 
	 * @return comprobante
	 */
	public String getComprobante() {
		return comprobante;
	}

	/**
	 * Asigna la variable comprobante
	 * 
	 * @param comprobante Variable a asignar en comprobante
	 */
	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCompania
	 * 
	 * @return listaCompania
	 */
	public List<Registro> getListaCompania() {
		return listaCompania;
	}

	/**
	 * Asigna la lista listaCompania
	 * 
	 * @param listaCompania Variable a asignar en listaCompania
	 */
	public void setListaCompania(List<Registro> listaCompania) {
		this.listaCompania = listaCompania;
	}

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

	/**
	 * Retorna la lista listaTipo
	 * 
	 * @return listaTipo
	 */
	public RegistroDataModelImpl getListaTipo() {
		return listaTipo;
	}

	/**
	 * Asigna la lista listaTipo
	 * 
	 * @param listaTipo Variable a asignar en listaTipo
	 */
	public void setListaTipo(RegistroDataModelImpl listaTipo) {
		this.listaTipo = listaTipo;
	}

	/**
	 * Retorna la lista listaComprobante
	 * 
	 * @return listaComprobante
	 */
	public RegistroDataModelImpl getListaComprobante() {
		return listaComprobante;
	}

	/**
	 * Asigna la lista listaComprobante
	 * 
	 * @param listaComprobante Variable a asignar en listaComprobante
	 */
	public void setListaComprobante(RegistroDataModelImpl listaComprobante) {
		this.listaComprobante = listaComprobante;
	}

	public String getNombreCompania() {
		return nombreCompania;
	}

	public void setNombreCompania(String nombreCompania) {
		this.nombreCompania = nombreCompania;
	}

	public Date getFechaAnterior() {
		return fechaAnterior;
	}

	public void setFechaAnterior(Date fechaAnterior) {
		this.fechaAnterior = fechaAnterior;
	}

	public Date getFechaActual() {
		return fechaActual;
	}

	public void setFechaActual(Date fechaActual) {
		this.fechaActual = fechaActual;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(String valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getNombreTipo() {
		return nombreTipo;
	}

	public void setNombreTipo(String nombreTipo) {
		this.nombreTipo = nombreTipo;
	}

//</SET_GET_LISTAS_COMBO_GRANDE>
//<SET_GET_LISTAS_SUBFORM>
//</SET_GET_LISTAS_SUBFORM>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_ADICIONALES>	
//</SET_GET_ADICIONALES>
}
