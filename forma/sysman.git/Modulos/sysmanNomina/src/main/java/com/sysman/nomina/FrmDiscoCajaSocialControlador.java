/*-
 * FrmDiscoCajaSocialControlador.java
 *
 * 1.0
 * 
 * 16/02/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.ByteArrayInputStream;
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
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
import com.sysman.nomina.enums.DiscoBancoPopularControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 16/02/2021
 * @author jorduz
 */
@ManagedBean
@ViewScoped
public class  FrmDiscoCajaSocialControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private String anio;
	private String mes;
	private String periodo;
	private String proceso;
	private String banco;
	private Date fechaReporte;
	private String nombreBanco;
	private String concpago;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAno1;
	private List<Registro> listaMes1;
	private List<Registro> listaPeriodo1;
	private List<Registro> listaProceso;
	//</DECLARAR_LISTAS>
	private StreamedContent archivoDescarga;
	@EJB
	private EjbNominaCuatroRemote ejbNominaCuatro;
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaBanco1;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmDiscoCajaSocialControlador
	 */
	public FrmDiscoCajaSocialControlador() {
		super();

		compania = SessionUtil.getCompania();
		proceso = SessionUtil.getSessionVar("procesoNomina").toString();
		anio = SessionUtil.getSessionVar("anioNomina").toString();
		mes = SessionUtil.getSessionVar("mesNomina").toString();
		periodo = SessionUtil.getSessionVar("periodoNomina").toString();
		fechaReporte = new Date();
		concpago = "nominacentr";
		try {
			numFormulario=2242;
			validarPermisos();
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
		cargarListaAno1();
		cargarListaMes1();
		cargarListaPeriodo1();
		cargarListaProceso();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaBanco1();
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

		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAno1(){
		// listaAno1 = service.getListado(conectorPool, "SELECT DISTINCT "+
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno1 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DiscoBancoPopularControladorUrlEnum.URL4522.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}	
	}
	/**
	 * 
	 * Carga la lista listaMes1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaMes1(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		try {
			listaMes1 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DiscoBancoPopularControladorUrlEnum.URL4996.getValue())
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
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaPeriodo1(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		String urlEnumId = DiscoBancoPopularControladorUrlEnum.URL6204.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();

		try {
			listaPeriodo1 = RegistroConverter.toListRegistro(requestManager.getList(url, param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaProceso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaProceso(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaProceso = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DiscoBancoPopularControladorUrlEnum.URL6203.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaBanco1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaBanco1(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DiscoBancoPopularControladorUrlEnum.URL6961.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaBanco1 = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"BANCO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarDisco
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirGenerarDisco() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarDiscoD
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirGenerarDiscoD() {
		archivoDescarga = null;
		try {
			String datos = ejbNominaCuatro.generarDiscoCajaSocialDuitama(compania, Integer.parseInt(proceso), Integer.parseInt(periodo), Integer.parseInt(mes), Integer.parseInt(anio), fechaReporte, concpago, banco);
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(datos);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
					"CAJASOCIAL.txt");
		} catch (NumberFormatException | SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaBanco1
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBanco1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		banco = SysmanFunciones.nvl(registroAux.getCampos().get("BANCO"), " ").toString();
		nombreBanco = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), " ").toString();

	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable mes
	 * 
	 * @return  mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * Asigna la variable  mes
	 * 
	 * @param  mes
	 * Variable a asignar en  mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * Retorna la variable periodo
	 * 
	 * @return  periodo
	 */
	public String getPeriodo() {
		return periodo;
	}
	/**
	 * Asigna la variable  periodo
	 * 
	 * @param  periodo
	 * Variable a asignar en  periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	/**
	 * Retorna la variable proceso
	 * 
	 * @return  proceso
	 */
	public String getProceso() {
		return proceso;
	}
	/**
	 * Asigna la variable  proceso
	 * 
	 * @param  proceso
	 * Variable a asignar en  proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	/**
	 * Retorna la variable banco
	 * 
	 * @return  banco
	 */
	public String getBanco() {
		return banco;
	}
	/**
	 * Asigna la variable  banco
	 * 
	 * @param  banco
	 * Variable a asignar en  banco
	 */
	public void setBanco(String banco) {
		this.banco = banco;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno1
	 * 
	 * @return listaAno1
	 */
	public List<Registro> getListaAno1() {
		return listaAno1;
	}
	/**
	 * Asigna la lista listaAno1
	 * 
	 * @param listaAno1
	 * Variable a asignar en  listaAno1
	 */
	public void setListaAno1(List<Registro> listaAno1) {
		this.listaAno1 = listaAno1;
	}
	/**
	 * Retorna la lista listaMes1
	 * 
	 * @return listaMes1
	 */
	public List<Registro> getListaMes1() {
		return listaMes1;
	}
	/**
	 * Asigna la lista listaMes1
	 * 
	 * @param listaMes1
	 * Variable a asignar en  listaMes1
	 */
	public void setListaMes1(List<Registro> listaMes1) {
		this.listaMes1 = listaMes1;
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
	 * Variable a asignar en  listaPeriodo1
	 */
	public void setListaPeriodo1(List<Registro> listaPeriodo1) {
		this.listaPeriodo1 = listaPeriodo1;
	}
	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public List<Registro> getListaProceso() {
		return listaProceso;
	}
	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso
	 * Variable a asignar en  listaProceso
	 */
	public void setListaProceso(List<Registro> listaProceso) {
		this.listaProceso = listaProceso;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaBanco1
	 * 
	 * @return listaBanco1
	 */
	public RegistroDataModelImpl getListaBanco1() {
		return listaBanco1;
	}
	public void setListaBanco1(RegistroDataModelImpl listaBanco1) {
		this.listaBanco1 = listaBanco1;
	}
	public Date getFechaReporte() {
		return fechaReporte;
	}
	public void setFechaReporte(Date fechaReporte) {
		this.fechaReporte = fechaReporte;
	}
	public String getNombreBanco() {
		return nombreBanco;
	}
	public void setNombreBanco(String nombreBanco) {
		this.nombreBanco = nombreBanco;
	}
	public String getConcpago() {
		return concpago;
	}
	public void setConcpago(String concpago) {
		this.concpago = concpago;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	//</SET_GET_LISTAS_COMBO_GRANDE>
}
