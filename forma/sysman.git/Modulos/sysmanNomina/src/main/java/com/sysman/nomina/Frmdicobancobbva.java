/*-
 * Frmdicobancobbva.java
 *
 * 1.0
 * 
 * 15/02/2021
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
import com.sysman.nomina.enums.DiscoBancoPopularControladorUrlEnum;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 15/02/2021
 * @author jorduz
 */
@ManagedBean
@ViewScoped
public class  Frmdicobancobbva extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>


	private String anio;
	private String mes;
	private String periodo;
	private String banco;
	private String proceso;
	private String nombreBanco;
	private Date fechaReporte;
	private String fechaReferencia;
	private String nb;
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>

	private List<Registro> listaAno1;
	private List<Registro> listaMes1;
	private List<Registro> listaPeriodo1;
	private List<Registro> listaProceso;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaBanco1;
	@EJB
	private EjbNominaCuatroRemote ejbNominaCuatro;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de Frmdicobancobbva
	 */
	public Frmdicobancobbva() {
		super();
		compania = SessionUtil.getCompania();
		proceso = SessionUtil.getSessionVar("procesoNomina").toString();
		anio = SessionUtil.getSessionVar("anioNomina").toString();
		mes = SessionUtil.getSessionVar("mesNomina").toString();
		periodo = SessionUtil.getSessionVar("periodoNomina").toString();
		fechaReporte = new Date();
		try {
			numFormulario=2241;
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
		cargarListaBanco1();
		cargarListaProceso();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
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
	/**
	 * 
	 * Carga la lista listaProceso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaProceso(){
		//listaProceso = service.getListado(conectorPool, "SELECT "+
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
	//</METODOS_CARGAR_LISTA>
	public void seleccionarFilaBanco1(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		banco = SysmanFunciones.nvl(registroAux.getCampos().get("BANCO"), " ").toString();
		nombreBanco = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), " ").toString();
	}
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
		archivoDescarga = null;
		try {
			String datos = ejbNominaCuatro.generarDiscobbva_Cash(compania, Integer.parseInt(proceso), Integer.parseInt(periodo), Integer.parseInt(mes), Integer.parseInt(anio), fechaReporte, banco);
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(datos);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
					"PLANOCASHSYSMAN.txt");
		} catch (NumberFormatException | SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EnviarExcel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirEnviarExcel() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Archivo
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirArchivo() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton NuevoArchivo
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton GenerarDiscoNUEVOFORMATO
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirGenerarDiscoNUEVOFORMATO() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}

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
	/**
	 * Retorna la variable fechaReporte
	 * 
	 * @return  fechaReporte
	 */

	/**
	 * Retorna la variable fechaReferencia
	 * 
	 * @return  fechaReferencia
	 */
	public String getFechaReferencia() {
		return fechaReferencia;
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	public Date getFechaReporte() {
		return fechaReporte;
	}
	public void setFechaReporte(Date fechaReporte) {
		this.fechaReporte = fechaReporte;
	}
	/**
	 * Asigna la variable  fechaReferencia
	 * 
	 * @param  fechaReferencia
	 * Variable a asignar en  fechaReferencia
	 */
	
	
	
	public void setFechaReferencia(String fechaReferencia) {
		this.fechaReferencia = fechaReferencia;
	}
	public String getNb() {
		return nb;
	}
	public void setNb(String nb) {
		this.nb = nb;
	}
	
	public String getProceso() {
		return proceso;
	}
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	public String getNombreBanco() {
		return nombreBanco;
	}
	public void setNombreBanco(String nombreBanco) {
		this.nombreBanco = nombreBanco;
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
	 * Retorna la lista listaBanco1
	 * 
	 * @return listaBanco1
	 */

	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public List<Registro> getListaProceso() {
		return listaProceso;
	}
	public RegistroDataModelImpl getListaBanco1() {
		return listaBanco1;
	}
	public void setListaBanco1(RegistroDataModelImpl listaBanco1) {
		this.listaBanco1 = listaBanco1;
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
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
