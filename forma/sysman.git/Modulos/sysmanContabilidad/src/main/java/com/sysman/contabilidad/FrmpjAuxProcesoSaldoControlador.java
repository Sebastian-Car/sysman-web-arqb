/*-
 * FrmpjAuxProcesoSaldoControlador.java
 *
 * 1.0
 * 
 * 31/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.FrmpjAuxProcesoSaldoControladorEnum;
import com.sysman.contabilidad.enums.FrmpjAuxProcesoSaldoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
/**
 *
 * @version 1.0, 31/05/2024
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmpjAuxProcesoSaldoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private boolean filtroTercero;
	private boolean sinConsecutivo;
	private String procesoIni;
	private String procesoFin;
	private String cuentaIni;
	private String cuentaFin;
	private String terceroIni;
	private String terceroFin;
	private Date fechaIni;
	private Date fechaFin;
	private String nomTerceroIni;
	private String nomTerceroFin;
	private int anio;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaProcesoInicial;
	private RegistroDataModelImpl listaProcesoFinal;
	private RegistroDataModelImpl listaCuentaInicial;
	private RegistroDataModelImpl listaCuentaFinal;
	private RegistroDataModelImpl listaTerceroInicial;
	private RegistroDataModelImpl listaTerceroFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de FrmpjAuxProcesoSaldoControlador
	 */
	public FrmpjAuxProcesoSaldoControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2470;
			validarPermisos();
			asignarFechas();
			anio = SysmanFunciones.ano(fechaIni);
			cuentaIni = SysmanConstantes.DEFECTOINICIAL_STRING;
			cuentaFin = SysmanConstantes.DEFECTOFINAL_STRING;
			procesoIni = SysmanConstantes.DEFECTOINICIAL_STRING;
			procesoFin = SysmanConstantes.DEFECTOFINAL_STRING;

			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaProcesoInicial();  
		cargarListaCuentaInicial();  
		cargarListaTerceroInicial(); 
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		/*
FR2470-AL_ABRIR
Private Sub Form_Open(Cancel As Integer)
   'formularioAbrir 1, Me.Name
   DoCmd.Restore
End Sub
		 */
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaProcesoInicial
	 *
	 */
	public void cargarListaProcesoInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmpjAuxProcesoSaldoControladorUrlEnum.URL0005.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaProcesoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NUMERO.getName());

	}
	/**
	 * 
	 * Carga la lista listaProcesoFinal
	 *
	 */
	public void cargarListaProcesoFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmpjAuxProcesoSaldoControladorUrlEnum.URL0006.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrmpjAuxProcesoSaldoControladorEnum.PARAM4.getValue(), procesoIni);

		listaProcesoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.NUMERO.getName());

	}
	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 */
	public void cargarListaCuentaInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmpjAuxProcesoSaldoControladorUrlEnum.URL0001.getValue());
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
	public void cargarListaCuentaFinal(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmpjAuxProcesoSaldoControladorUrlEnum.URL0002.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(FrmpjAuxProcesoSaldoControladorEnum.PARAM3.getValue(), cuentaIni);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

	}
	/**
	 * 
	 * Carga la lista listaTerceroInicial
	 *
	 */
	public void cargarListaTerceroInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmpjAuxProcesoSaldoControladorUrlEnum.URL0003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NIT");

	}
	/**
	 * 
	 * Carga la lista listaTerceroFinal
	 *
	 */
	public void cargarListaTerceroFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmpjAuxProcesoSaldoControladorUrlEnum.URL0004
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrmpjAuxProcesoSaldoControladorEnum.PARAM1.getValue(),
				terceroIni);

		listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NIT");

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(FORMATOS.PDF);            
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(FORMATOS.EXCEL);           
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	private void generaInforme(FORMATOS formato) {


		if(validarAniosFechas()) 
		{
			try {
				String formatoReporte = "002590AuxiliarProcesoConSaldos_CREMIL";
				Map<String, Object> parametros = new HashMap<>();
				HashMap<String, Object> reemplazar = new HashMap<>();

				reemplazar.put("cuentaInicial", cuentaIni);
				reemplazar.put("cuentaFinal", cuentaFin);
				reemplazar.put("procesoInicial", procesoIni);
				reemplazar.put("procesoFinal", procesoFin);
				reemplazar.put("fechaInicial",
						SysmanFunciones.formatearFecha(fechaIni));
				reemplazar.put("fechaFinal",
						SysmanFunciones.formatearFecha(fechaFin));            
				if(!filtroTercero) {
					terceroIni = "0";
					terceroFin = SysmanConstantes.CONS_TERCERO;
				}
				reemplazar.put("terceroInicial", terceroIni);
				reemplazar.put("terceroFinal", terceroFin);

				reemplazar.put("terceroCond",
						filtroTercero
						? " AND DETALLE_COMPROBANTE_CNT.TERCERO BETWEEN '"
						+ terceroIni + "' "
						+ "AND '" + terceroFin + "'"
						: "");
				reemplazar.put("consecutivo", sinConsecutivo ? "": ", DETALLE_COMPROBANTE_CNT.CONSECUTIVO");

				Reporteador.resuelveConsulta(formatoReporte,
						Integer.parseInt(SessionUtil.getModulo()),
						reemplazar, parametros);
				String titulo = "BALANCE POR PROCESO DE "
						+ SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones.mes(fechaIni)]
								.toUpperCase()
								+ " " + SysmanFunciones.dia(fechaIni) + " A "
								+ SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones.mes(fechaFin)]
										.toUpperCase()
										+ " " + SysmanFunciones.dia(fechaFin) + " DE " + anio;

				parametros.put("PR_TITULO_INFORME", titulo);
				parametros.put("PR_CUENTAINICIAL", cuentaIni);
				parametros.put("PR_CUENTAFINAL", cuentaFin);
				parametros.put("PR_NOMBRE_CONTADOR", 
						ejbSysmanUtil.consultarParametro(compania,"NOMBRE JEFE DE CONTABILIDAD", 
								SessionUtil.getModulo(), new Date(), true));
				parametros.put("PR_CARGO_CONTADOR",
						ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE DE CONTABILIDAD", 
								SessionUtil.getModulo(), new Date(), true));


				archivoDescarga = JsfUtil.exportarStreamed(formatoReporte,
						parametros,
						ConectorPool.ESQUEMA_SYSMAN,
						formato);
			}
			catch (JRException | IOException | SystemException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException
					e) {
				
				logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
			}

		}

	}

	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Fechafinal
	 * 
	 * 
	 */
	public void cambiarFechafinal() {
		//<CODIGO_DESARROLLADO>
		if(validarAniosFechas()) {
			anio = SysmanFunciones.ano(fechaIni);
			cargarListaCuentaInicial();
		}
		//</CODIGO_DESARROLLADO>
	}

	private boolean validarAniosFechas() {

		if (SysmanFunciones.ano(fechaIni) != SysmanFunciones.ano(fechaFin)) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3154"));
			return false;
		}        
		return true;
	}

	/**
	 * Metodo ejecutado al cambiar el control filtroTercero
	 * 
	 * 
	 */
	public void cambiarfiltroTercero() {
		//<CODIGO_DESARROLLADO>
		terceroIni = "0";
		terceroFin = SysmanConstantes.CONS_TERCERO;

		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProcesoInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProcesoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		procesoIni= SysmanFunciones.toString(registroAux.getCampos().get("NUMERO"));
		procesoFin = null;
		cargarListaProcesoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProcesoFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProcesoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		procesoFin= SysmanFunciones.toString(registroAux.getCampos().get("NUMERO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaIni= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
		cuentaFin = null;
		cargarListaCuentaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFin= SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTerceroInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroIni= SysmanFunciones.toString(registroAux.getCampos().get("NIT"));
		nomTerceroIni = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
		terceroFin = null;
		cargarListaTerceroFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTerceroFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTerceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroFin= SysmanFunciones.toString(registroAux.getCampos().get("NIT"));
		nomTerceroFin = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>

	public void asignarFechas() {

		try {
			String formatoFecha = "dd/MM/yyyy";
			SimpleDateFormat sdf = new SimpleDateFormat(formatoFecha);
			int anio = SysmanFunciones.ano(new Date());
			String fechaInicial = "01/01/"+ anio;
			String fechaFinal = "31/12/"+ anio;
			fechaIni = sdf.parse(fechaInicial);
			fechaFin = sdf.parse(fechaFinal);

		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * Retorna la variable filtroTercero
	 * 
	 * @return  filtroTercero
	 */
	public boolean getFiltroTercero() {
		return filtroTercero;
	}
	/**
	 * Asigna la variable  filtroTercero
	 * 
	 * @param  filtroTercero
	 * Variable a asignar en  filtroTercero
	 */
	public void setFiltroTercero(boolean filtroTercero) {
		this.filtroTercero = filtroTercero;
	}
	/**
	 * Retorna la variable sinConsecutivo
	 * 
	 * @return  sinConsecutivo
	 */
	public boolean getSinConsecutivo() {
		return sinConsecutivo;
	}
	/**
	 * Asigna la variable  sinConsecutivo
	 * 
	 * @param  sinConsecutivo
	 * Variable a asignar en  sinConsecutivo
	 */
	public void setSinConsecutivo(boolean sinConsecutivo) {
		this.sinConsecutivo = sinConsecutivo;
	}
	/**
	 * Retorna la variable procesoIni
	 * 
	 * @return  procesoIni
	 */
	public String getProcesoIni() {
		return procesoIni;
	}
	/**
	 * Asigna la variable  procesoIni
	 * 
	 * @param  procesoIni
	 * Variable a asignar en  procesoIni
	 */
	public void setProcesoIni(String procesoIni) {
		this.procesoIni = procesoIni;
	}
	/**
	 * Retorna la variable procesoFin
	 * 
	 * @return  procesoFin
	 */
	public String getProcesoFin() {
		return procesoFin;
	}
	/**
	 * Asigna la variable  procesoFin
	 * 
	 * @param  procesoFin
	 * Variable a asignar en  procesoFin
	 */
	public void setProcesoFin(String procesoFin) {
		this.procesoFin = procesoFin;
	}
	/**
	 * Retorna la variable cuentaIni
	 * 
	 * @return  cuentaIni
	 */
	public String getCuentaIni() {
		return cuentaIni;
	}
	/**
	 * Asigna la variable  cuentaIni
	 * 
	 * @param  cuentaIni
	 * Variable a asignar en  cuentaIni
	 */
	public void setCuentaIni(String cuentaIni) {
		this.cuentaIni = cuentaIni;
	}
	/**
	 * Retorna la variable cuentaFin
	 * 
	 * @return  cuentaFin
	 */
	public String getCuentaFin() {
		return cuentaFin;
	}
	/**
	 * Asigna la variable  cuentaFin
	 * 
	 * @param  cuentaFin
	 * Variable a asignar en  cuentaFin
	 */
	public void setCuentaFin(String cuentaFin) {
		this.cuentaFin = cuentaFin;
	}
	/**
	 * Retorna la variable terceroIni
	 * 
	 * @return  terceroIni
	 */
	public String getTerceroIni() {
		return terceroIni;
	}
	/**
	 * Asigna la variable  terceroIni
	 * 
	 * @param  terceroIni
	 * Variable a asignar en  terceroIni
	 */
	public void setTerceroIni(String terceroIni) {
		this.terceroIni = terceroIni;
	}
	/**
	 * Retorna la variable terceroFin
	 * 
	 * @return  terceroFin
	 */
	public String getTerceroFin() {
		return terceroFin;
	}
	/**
	 * Asigna la variable  terceroFin
	 * 
	 * @param  terceroFin
	 * Variable a asignar en  terceroFin
	 */
	public void setTerceroFin(String terceroFin) {
		this.terceroFin = terceroFin;
	}
	/**
	 * Retorna la variable fechaIni
	 * 
	 * @return  fechaIni
	 */
	public Date getFechaIni() {
		return fechaIni;
	}
	/**
	 * Asigna la variable  fechaIni
	 * 
	 * @param  fechaIni
	 * Variable a asignar en  fechaIni
	 */
	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}
	/**
	 * Retorna la variable fechaFin
	 * 
	 * @return  fechaFin
	 */
	public Date getFechaFin() {
		return fechaFin;
	}
	/**
	 * Asigna la variable  fechaFin
	 * 
	 * @param  fechaFin
	 * Variable a asignar en  fechaFin
	 */
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	/**
	 * Retorna la variable nomTerceroIni
	 * 
	 * @return  nomTerceroIni
	 */
	public String getNomTerceroIni() {
		return nomTerceroIni;
	}
	/**
	 * Asigna la variable  nomTerceroIni
	 * 
	 * @param  nomTerceroIni
	 * Variable a asignar en  nomTerceroIni
	 */
	public void setNomTerceroIni(String nomTerceroIni) {
		this.nomTerceroIni = nomTerceroIni;
	}
	/**
	 * Retorna la variable nomTerceroFin
	 * 
	 * @return  nomTerceroFin
	 */
	public String getNomTerceroFin() {
		return nomTerceroFin;
	}
	/**
	 * Asigna la variable  nomTerceroFin
	 * 
	 * @param  nomTerceroFin
	 * Variable a asignar en  nomTerceroFin
	 */
	public void setNomTerceroFin(String nomTerceroFin) {
		this.nomTerceroFin = nomTerceroFin;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaProcesoInicial
	 * 
	 * @return listaProcesoInicial
	 */
	public RegistroDataModelImpl getListaProcesoInicial() {
		return listaProcesoInicial;
	}
	/**
	 * Asigna la lista listaProcesoInicial
	 * 
	 * @param listaProcesoInicial
	 * Variable a asignar en  listaProcesoInicial
	 */
	public void setListaProcesoInicial(RegistroDataModelImpl listaProcesoInicial) {
		this.listaProcesoInicial = listaProcesoInicial;
	}
	/**
	 * Retorna la lista listaProcesoFinal
	 * 
	 * @return listaProcesoFinal
	 */
	public RegistroDataModelImpl getListaProcesoFinal() {
		return listaProcesoFinal;
	}
	/**
	 * Asigna la lista listaProcesoFinal
	 * 
	 * @param listaProcesoFinal
	 * Variable a asignar en  listaProcesoFinal
	 */
	public void setListaProcesoFinal(RegistroDataModelImpl listaProcesoFinal) {
		this.listaProcesoFinal = listaProcesoFinal;
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
	 * @param listaCuentaInicial
	 * Variable a asignar en  listaCuentaInicial
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
	 * @param listaCuentaFinal
	 * Variable a asignar en  listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
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
	 * @param listaTerceroInicial
	 * Variable a asignar en  listaTerceroInicial
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
	 * @param listaTerceroFinal
	 * Variable a asignar en  listaTerceroFinal
	 */
	public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
		this.listaTerceroFinal = listaTerceroFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
