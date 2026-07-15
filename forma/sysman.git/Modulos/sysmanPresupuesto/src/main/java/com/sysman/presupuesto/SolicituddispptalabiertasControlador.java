/*-
 * SolicituddispptalabiertasControlador.java
 *
 * 1.0
 * 
 * 09/09/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
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
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.presupuesto.enums.FrmDisponibilidadPptalAbiertasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 09/09/2019
 * @author jalfonso
 */
@ManagedBean
@ViewScoped
public class  SolicituddispptalabiertasControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>

	private int anio;
	private String cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
	private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaAnio;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaRubroInicial;
	private RegistroDataModelImpl listaRubroFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de SolicituddispptalabiertasControlador
	 */
	public SolicituddispptalabiertasControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		anio = SysmanFunciones.ano(new Date());
		try {

			//2103
			numFormulario= GeneralCodigoFormaEnum.SOLICITUD_DIS_PPTAL_ABIERTAS_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
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
		cargarListaAnio();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaRubroInicial();
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
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmDisponibilidadPptalAbiertasControladorUrlEnum.URL3328
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaRubroInicial
	 *
	 */
	public void cargarListaRubroInicial(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDisponibilidadPptalAbiertasControladorUrlEnum.URL3725
						.getValue());


		Map<String, Object> parametro = new HashMap<>();

		parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametro.put(GeneralParameterEnum.ANO.getName(), anio);

		listaRubroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), parametro,
				true, GeneralParameterEnum.CODIGO.getName());

	}
	/**
	 * 
	 * Carga la lista listaRubroFinal
	 *
	 */
	public void cargarListaRubroFinal(){

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDisponibilidadPptalAbiertasControladorUrlEnum.URL4635
						.getValue());


		Map<String, Object> parametro = new HashMap<>();

		parametro.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		parametro.put(GeneralParameterEnum.ANO.getName(), anio);
		parametro.put("CUENTAINICIAL", cuentaInicial);


		listaRubroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), parametro,
				true, GeneralParameterEnum.CODIGO.getName());
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
		archivoDescarga=null; 
		GenerarInforme(ReportesBean.FORMATOS.PDF);
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
		archivoDescarga=null;
		GenerarInforme(ReportesBean.FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>

	public void GenerarInforme(FORMATOS formato) {

		try {

			Map<String, Object> reemplazos = new HashMap<>();
			Map<String , Object> parametros = new HashMap<>();


			reemplazos.put("anio", anio);
			reemplazos.put("cuentaInicial", cuentaInicial);
			reemplazos.put("cuentaFinal", cuentaFinal);
			parametros.put("PR_ENTREREFERENCIAS", "Entre la Cuenta Inicial " + cuentaInicial + " y la Cuenta Final " + cuentaFinal);


			Reporteador.resuelveConsulta("002046SolicitudDispAbiertas", Integer.parseInt(modulo), reemplazos, parametros);


			archivoDescarga = JsfUtil.exportarStreamed("002046SolicitudDispAbiertas", parametros, ConectorPool.ESQUEMA_SYSMAN, formato);


		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}



	}


	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * 
	 */
	public void cambiarAnio() {
		//<CODIGO_DESARROLLADO>
		cuentaInicial = null;
		cuentaFinal = null;
		cargarListaRubroInicial();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaRubroInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaRubroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial= registroAux.getCampos().get("CODIGO").toString();

		cargarListaRubroFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaRubroFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaRubroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal= registroAux.getCampos().get("CODIGO").toString();
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
	public int getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(int anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return  cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}
	/**
	 * Asigna la variable  cuentaInicial
	 * 
	 * @param  cuentaInicial
	 * Variable a asignar en  cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}
	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return  cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}
	/**
	 * Asigna la variable  cuentaFinal
	 * 
	 * @param  cuentaFinal
	 * Variable a asignar en  cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
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
	 * @param listaAnio
	 * Variable a asignar en  listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaRubroInicial
	 * 
	 * @return listaRubroInicial
	 */
	public RegistroDataModelImpl getListaRubroInicial() {
		return listaRubroInicial;
	}
	/**
	 * Asigna la lista listaRubroInicial
	 * 
	 * @param listaRubroInicial
	 * Variable a asignar en  listaRubroInicial
	 */
	public void setListaRubroInicial(RegistroDataModelImpl listaRubroInicial) {
		this.listaRubroInicial = listaRubroInicial;
	}
	/**
	 * Retorna la lista listaRubroFinal
	 * 
	 * @return listaRubroFinal
	 */
	public RegistroDataModelImpl getListaRubroFinal() {
		return listaRubroFinal;
	}
	/**
	 * Asigna la lista listaRubroFinal
	 * 
	 * @param listaRubroFinal
	 * Variable a asignar en  listaRubroFinal
	 */
	public void setListaRubroFinal(RegistroDataModelImpl listaRubroFinal) {
		this.listaRubroFinal = listaRubroFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
