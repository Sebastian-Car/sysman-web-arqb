/*-
 * CptePptalIngresoLotesControlador.java
 *
 * 1.0
 * 
 * 30/12/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
import com.sysman.contabilidad.ejb.impl.EjbContabilidadCero;
import com.sysman.contabilidad.ejb.impl.EjbContabilidadUno;
import com.sysman.contabilidad.enums.DescuadradosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.contabilidad.enums.cptePptalIngresoLotesControladorUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresGeneralRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.sysman.recursos.ejb.EjbSysmanUtilRemote;



/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 30/12/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class  CptePptalIngresoLotesControlador extends BeanBaseModal{
	/** Compañía a la que pertenece el comprobante. */
	private final String compania;

	/** Tipo de comprobante contable. */
	private String tipoComprobante;

	/** Mes del periodo contable (01–12). */
	private String mes;

	/** Año del periodo contable. */
	private String anio;

	/** Comprobante inicial del rango de consulta. */
	private String comprobanteInicial;

	/** Comprobante final del rango de consulta. */
	private String comprobanteFinal;

	/** Registros correspondientes al mes seleccionado. */
	private List<Registro> listaMes;

	/** Registros correspondientes al año seleccionado. */
	private List<Registro> listaAno;

	/** Lista de tipos de comprobante. */
	private RegistroDataModelImpl listaTipoComprobante;

	/** Lista de comprobantes iniciales. */
	private RegistroDataModelImpl listaComprobanteInicial;

	/** Lista de comprobantes finales. */
	private RegistroDataModelImpl listaComprobanteFinal;

	/** EJB para operaciones de contabilidad. */
	@EJB
	private EjbContabilidadUnoRemote ejbContabilidadUno;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de CptePptalIngresoLotesControlador
	 */
	public CptePptalIngresoLotesControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario= GeneralCodigoFormaEnum.CPTE_PPTAL_INGRESO_LOTES_CONTROLADOR
					.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
		}
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		//<CARGAR_LISTA>
		anio = String.valueOf(SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR));
		mes = String.valueOf(SysmanFunciones.mes(new Date()));
		cargarListaMes();
		cargarListaAno();
		cargarListaTipoComprobante();
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
	}

	/**
	 * Carga la lista de meses para la compania actual.
	 * Consulta el servicio enviando la compania como parametro
	 * y maneja errores mostrando el mensaje correspondiente.
	 */
	public void cargarListaMes(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaMes = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									cptePptalIngresoLotesControladorUrlEnum.URL4409
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Carga la lista de anos para la compania actual.
	 * Consulta el servicio enviando la compania como parametro
	 * y maneja errores mostrando el mensaje correspondiente.
	 */
	public void cargarListaAno(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							cptePptalIngresoLotesControladorUrlEnum.URL4162
							.getValue());
			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(urlBean.getUrl(), param));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Carga la lista de tipos de comprobante para la compania actual.
	 * Inicializa el modelo de datos con el servicio y sus parametros.
	 */
	public void cargarListaTipoComprobante(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						cptePptalIngresoLotesControladorUrlEnum.URL15087
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTipoComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * Carga la lista de comprobantes iniciales.
	 * Filtra por compania, ano, mes y tipo de comprobante.
	 */
	public void cargarListaComprobanteInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						cptePptalIngresoLotesControladorUrlEnum.URL72135
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoComprobante);

		listaComprobanteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());

	}

	/**
	 * Carga la lista de comprobantes finales.
	 * Filtra por compania, ano, mes y tipo de comprobante.
	 */
	public void cargarListaComprobanteFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						cptePptalIngresoLotesControladorUrlEnum.URL72135
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoComprobante);

		listaComprobanteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());
	}

	/**
	 * Maneja el cambio de ano seleccionado.
	 * Limpia valores dependientes y recarga las listas necesarias.
	 */
	public void cambiarAno() {
		if (tipoComprobante != null && !tipoComprobante.isEmpty()) {
			comprobanteInicial = "";
			comprobanteFinal = "";
			cargarListaComprobanteInicial();
			cargarListaComprobanteFinal();
			mes = "";
			tipoComprobante = "";

		}
	}

	/**
	 * Maneja el cambio de mes seleccionado.
	 * Limpia valores dependientes y recarga las listas necesarias.
	 */
	public void cambiarMes() {
		if (tipoComprobante != null && !tipoComprobante.isEmpty()) {
			comprobanteInicial = "";
			comprobanteFinal = "";
			tipoComprobante = "";
			cargarListaComprobanteInicial();
			cargarListaComprobanteFinal();
		}
	}

	/**
	 * Maneja la seleccion de un tipo de comprobante.
	 * Asigna el valor y carga la lista de comprobantes iniciales.
	 */
	public void seleccionarFilaTipoComprobante(SelectEvent event) {
		Registro registro = (Registro) event.getObject();
		tipoComprobante = (String) registro.getCampos().get("CODIGO");
		cargarListaComprobanteInicial();

	}

	/**
	 * Maneja la seleccion del comprobante inicial.
	 * Asigna el valor y carga la lista de comprobantes finales.
	 */
	public void seleccionarFilaComprobanteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		comprobanteInicial = SysmanFunciones.toString(registroAux.getCampos().get("NUMERO"));
		comprobanteFinal = "";
		cargarListaComprobanteFinal();

	}

	/**
	 * Maneja la seleccion del comprobante final.
	 * Asigna el valor seleccionado.
	 */
	public void seleccionarFilaComprobanteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		comprobanteFinal = SysmanFunciones.toString(registroAux.getCampos().get("NUMERO"));
	}

	/**
	 * Inicia el proceso de generacion masiva.
	 * Ejecuta el servicio y muestra el resultado al usuario.
	 */
	public void oprimirIniciar() {
		try {
			// Convertir año y mes de String a int
			int anioInt = Integer.parseInt(anio);
			int mesInt = Integer.parseInt(mes);

			// Parámetros: compania, año, mes, módulo (3=contabilidad), proceso (1)
			String estadoperiodo = ejbSysmanUtil.verificarEstadoPeriodoMensual(
					compania, anioInt, mesInt, 3, 1);

			if ("A".equals(estadoperiodo)) {
				ejbContabilidadUno.generarMasivoComPptal(
						compania, 
						tipoComprobante, 
						anio, 
						mes, 
						comprobanteInicial, 
						comprobanteFinal, 
						SessionUtil.getUser().getCodigo());

				JsfUtil.agregarMensajeInformativo(
						idioma.getString("MSM_PROCESO_EJECUTADO"));

			} else if ("C".equals(estadoperiodo)) {
				 SimpleDateFormat sdf = new SimpleDateFormat("MMMM", new Locale("es", "ES"));
		            Calendar cal = Calendar.getInstance();
		            cal.set(Calendar.MONTH, Integer.parseInt(mes) - 1); 
		            String nombreMes = sdf.format(cal.getTime()).toUpperCase();
		            
		            JsfUtil.agregarMensajeError(
		                idioma.getString("TB_TB4493")
		                    .replace("$#anio#$", anio)
		                    .replace("$#mes#$", nombreMes)
		            );
		        }

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Retorna la variable tipoComprobante
	 * 
	 * @return  tipoComprobante
	 */
	public String getTipoComprobante() {
		return tipoComprobante;
	}
	/**
	 * @return the mes
	 */


	/**
	 * Asigna la variable  tipoComprobante
	 * 
	 * @param  tipoComprobante
	 * Variable a asignar en  tipoComprobante
	 */
	public void setTipoComprobante(String tipoComprobante) {
		this.tipoComprobante = tipoComprobante;
	}


	/**
	 * @return the mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * @param mes the mes to set
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
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
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
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
	 * Variable a asignar en  listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
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
	 * @param listaAno
	 * Variable a asignar en  listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaTipoComprobante
	 * 
	 * @return listaTipoComprobante
	 */
	/**
	 * @return the listaTipoComprobante
	 */
	public RegistroDataModelImpl getListaTipoComprobante() {
		return listaTipoComprobante;
	}
	/**
	 * @param listaTipoComprobante the listaTipoComprobante to set
	 */
	public void setListaTipoComprobante(RegistroDataModelImpl listaTipoComprobante) {
		this.listaTipoComprobante = listaTipoComprobante;
	}
	/**
	 * @return the comprobanteInicial
	 */
	public String getComprobanteInicial() {
		return comprobanteInicial;
	}
	/**
	 * @param comprobanteInicial the comprobanteInicial to set
	 */
	public void setComprobanteInicial(String comprobanteInicial) {
		this.comprobanteInicial = comprobanteInicial;
	}
	/**
	 * @return the comprobanteFinal
	 */
	public String getComprobanteFinal() {
		return comprobanteFinal;
	}
	/**
	 * @param comprobanteFinal the comprobanteFinal to set
	 */
	public void setComprobanteFinal(String comprobanteFinal) {
		this.comprobanteFinal = comprobanteFinal;
	}
	/**
	 * @return the listaComprobanteInicial
	 */
	public RegistroDataModelImpl getListaComprobanteInicial() {
		return listaComprobanteInicial;
	}
	/**
	 * @param listaComprobanteInicial the listaComprobanteInicial to set
	 */
	public void setListaComprobanteInicial(RegistroDataModelImpl listaComprobanteInicial) {
		this.listaComprobanteInicial = listaComprobanteInicial;
	}
	/**
	 * @return the listaComprobanteFinal
	 */
	public RegistroDataModelImpl getListaComprobanteFinal() {
		return listaComprobanteFinal;
	}
	/**
	 * @param listaComprobanteFinal the listaComprobanteFinal to set
	 */
	public void setListaComprobanteFinal(RegistroDataModelImpl listaComprobanteFinal) {
		this.listaComprobanteFinal = listaComprobanteFinal;
	}



	//</SET_GET_LISTAS_COMBO_GRANDE>
}
