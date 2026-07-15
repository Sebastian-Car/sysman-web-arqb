/*-
 * FrmCambiarFechaFactControlador.java
 *
 * 1.0
 * 
 * 23/07/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralUnoRemote;
import com.sysman.facturaciongeneral.enums.FrmCambiarFechaFactControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmlistadoRecaudoDifControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @version 1.0, 23/07/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmCambiarFechaFactControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private String tipo;

	private String anio;

	private String facturaFinal;

	private boolean vencimiento;

	private boolean solicitud = true;

	private Date fecha;

	private Date fechaAnterior;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listaTipo;

	private List<Registro> listaAno;

	private RegistroDataModelImpl listaFacturaFinal;

	private String nombreTipo;

	private RegistroDataModelImpl listaFacturaInicial;

	private String facturaInicial;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
	EjbFacturacionGeneralUnoRemote ejbFacturacionGeneralUnoRemote;

	private String campo;

	/**
	 * Crea una nueva instancia de FrmCambiarFechaFactControlador
	 */
	public FrmCambiarFechaFactControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.CAMBIAR_FECHAS_FACTURAS_CONTROLADOR.getCodigo();
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
		cargarListaAno();
		abrirFormulario();
	}

	private void cargarListaFacturaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFechaFactControladorUrlEnum.URL661080.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipo);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		if (fechaAnterior != null) {
			param.put("FECHA_EXP", solicitud ? fechaAnterior : null);
			param.put("FECHA_VENC", vencimiento ? fechaAnterior : null);
		} else {
			param.put("FECHA_EXP", null);
			param.put("FECHA_VENC", null);
		}

		listaFacturaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmlistadoRecaudoDifControladorEnum.NUMERO_FACTURA.getValue());
	}

	private void cargarListaFacturaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFechaFactControladorUrlEnum.URL661082.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipo);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.FACTURA.getName(), facturaInicial);
		if (fechaAnterior != null) {
			param.put("FECHA_EXP", solicitud ? fechaAnterior : null);
			param.put("FECHA_VENC", vencimiento ? fechaAnterior : null);
		} else {
			param.put("FECHA_EXP", null);
			param.put("FECHA_VENC", null);
		}

		listaFacturaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				FrmlistadoRecaudoDifControladorEnum.NUMERO_FACTURA.getValue());
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

	public void cargarListaTipo() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmCambiarFechaFactControladorUrlEnum.URL665015.getValue());

		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");
	}

	public void cargarListaAno() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmCambiarFechaFactControladorUrlEnum.URL4002.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>

	public void cambiarFechaSolicitud() {
		vencimiento = (solicitud) ? false : true;
	}

	public void cambiarFechant() {
		cargarListaFacturaInicial();
		facturaInicial = null;
		facturaFinal = null;
	}

	/**
	 * Metodo ejecutado al cambiar el control fechaVencimiento
	 * 
	 * 
	 */
	public void cambiarfechaVencimiento() {
		solicitud = (vencimiento) ? false : true;
	}

	public void oprimirPantalla() {
		boolean condicionTipoDeMov = (tipo == null) || tipo.isEmpty();

		if ((fecha == null) || condicionTipoDeMov || (facturaInicial == null) || facturaFinal == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("MSM_FALTAN_DATOS_PROCESO"));
		} else {
			String opcion = (solicitud) ? "1" : "0";
			String respuesta;
			try {
				respuesta = ejbFacturacionGeneralUnoRemote.cambiarFechaFactura(compania, anio, tipo, opcion,
						facturaInicial, facturaFinal, fecha, SessionUtil.getUser().getCodigo());
				if (respuesta.contains("OK")) {
					JsfUtil.agregarMensajeInformativo("Proceso ejecutado exitosamente");
				} else {
					JsfUtil.agregarMensajeInformativo(respuesta);
				}
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
	}

	public void oprimirCancelar() {
		// <CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	public void cambiarAno() {
		cargarListaTipo();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipo
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipo(SelectEvent event) {
		nombreTipo = null;
		Registro registroAux = (Registro) event.getObject();
		tipo = registroAux.getCampos().get("CODIGO").toString();
		nombreTipo = registroAux.getCampos().get("NOMBRE").toString();
		cargarListaFacturaInicial();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFacturaInicial
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFacturaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		facturaInicial = SysmanFunciones.toString(registroAux.getCampos().get("NUMERO_FACTURA"));
		facturaFinal = null;
		cargarListaFacturaFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFacturaFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFacturaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		facturaFinal = SysmanFunciones.toString(registroAux.getCampos().get("NUMERO_FACTURA"));
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
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

//</SET_GET_ATRIBUTOS>

//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
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
	 * @return the facturaFinal
	 */
	public String getFacturaFinal() {
		return facturaFinal;
	}

	/**
	 * @param facturaFinal the facturaFinal to set
	 */
	public void setFacturaFinal(String facturaFinal) {
		this.facturaFinal = facturaFinal;
	}

	/**
	 * @return the facturaInicial
	 */
	public String getFacturaInicial() {
		return facturaInicial;
	}

	/**
	 * @param facturaInicial the facturaInicial to set
	 */
	public void setFacturaInicial(String facturaInicial) {
		this.facturaInicial = facturaInicial;
	}

	/**
	 * @return the listaFacturaFinal
	 */
	public RegistroDataModelImpl getListaFacturaFinal() {
		return listaFacturaFinal;
	}

	/**
	 * @return the listaFacturaInicial
	 */
	public RegistroDataModelImpl getListaFacturaInicial() {
		return listaFacturaInicial;
	}

	/**
	 * @param listaFacturaInicial the listaFacturaInicial to set
	 */
	public void setListaFacturaInicial(RegistroDataModelImpl listaFacturaInicial) {
		this.listaFacturaInicial = listaFacturaInicial;
	}

	/**
	 * @param listaFacturaFinal the listaFacturaFinal to set
	 */
	public void setListaFacturaFinal(RegistroDataModelImpl listaFacturaFinal) {
		this.listaFacturaFinal = listaFacturaFinal;
	}

	/**
	 * @return the nombreTipo
	 */
	public String getNombreTipo() {
		return nombreTipo;
	}

	/**
	 * @param nombreTipo the nombreTipo to set
	 */
	public void setNombreTipo(String nombreTipo) {
		this.nombreTipo = nombreTipo;
	}

	/**
	 * @return the vencimiento
	 */
	public boolean isVencimiento() {
		return vencimiento;
	}

	/**
	 * @param vencimiento the vencimiento to set
	 */
	public void setVencimiento(boolean vencimiento) {
		this.vencimiento = vencimiento;
	}

	/**
	 * @return the solicitud
	 */
	public boolean isSolicitud() {
		return solicitud;
	}

	/**
	 * @param solicitud the solicitud to set
	 */
	public void setSolicitud(boolean solicitud) {
		this.solicitud = solicitud;
	}

	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}

	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the fechaAnterior
	 */
	public Date getFechaAnterior() {
		return fechaAnterior;
	}

	/**
	 * @param fechaAnterior the fechaAnterior to set
	 */
	public void setFechaAnterior(Date fechaAnterior) {
		this.fechaAnterior = fechaAnterior;
	}

//</SET_GET_LISTAS_COMBO_GRANDE>
}
