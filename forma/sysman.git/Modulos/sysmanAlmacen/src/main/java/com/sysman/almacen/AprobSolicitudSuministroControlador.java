/*-
 * AprobSolicitudSuministroControlador.java
 *
 * 1.0
 * 
 * 06/01/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;

import com.sysman.almacen.enums.AprobSolicitudSuministroControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.POrdenDeSuministroControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
/**
 *
 * @version 1.0, 06/01/2026
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class AprobSolicitudSuministroControlador extends BeanBaseDatosAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ; 
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaClaseBodega;
	private List<Registro> listaDependencia;
	private List<Registro> listaauxiliarCombo;
	private List<Registro> listaElemento;
	private List<Registro> listaDependenciaDos;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaResponsable;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>
	private List<Registro> listaSubordensuministro;
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	/**
	 * Atributo de referencia para el subformulario 
	 */
	private Registro registroSub;

	private String nombreAuxiliar;
	private String responsable;
	private int anio;
	private boolean habilitaApro;
	private boolean estado;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de AprobSolicitudSuministroControlador
	 */
	public AprobSolicitudSuministroControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = 2558;
			validarPermisos();
			//<INI_ADICIONAL>
			registroSub = new Registro(new HashMap<String, Object>());
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas(){
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaResponsable();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CARGAR_LISTA>
		cargarListaClaseBodega();
		cargarListaDependencia();
		cargarListaauxiliarCombo();
		cargarListaElemento();
		cargarListaDependenciaDos();
		//</CARGAR_LISTA>
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub(){
		//<CARGAR_LISTAS_SUBFORM>
		cargarListaSubordensuministro();
		anio = SysmanFunciones.getParteFecha(
				(Date) registro.getCampos().get(
						GeneralParameterEnum.FECHA.getName()),
				Calendar.YEAR);
		//</CARGAR_LISTAS_SUBFORM>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
	}
	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo(){
		//<CARGAR_LISTAS_SUBFORM_NULL>
		listaSubordensuministro = null;
		//</CARGAR_LISTAS_SUBFORM_NULL>
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
		tabla = GenericUrlEnum.ORDENDESUMINISTRO.getTable();
		buscarLlave();
		asignarOrigenDatos();
	}
	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		urlListado = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						AprobSolicitudSuministroControladorUrlEnum.URL39812
						.getValue());

		urlLectura = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						AprobSolicitudSuministroControladorUrlEnum.URL10900R
						.getValue());
	}

	/**
	 * 
	 * Carga la lista listaSubordensuministro
	 *
	 */
	public void cargarListaSubordensuministro(){

		try
		{
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ORDEN.getName(),
					registro.getCampos().get(GeneralParameterEnum.NUMERO
							.getName()));

			listaSubordensuministro = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									GenericUrlEnum.D_ORDENDESUMINISTRO
									.getGridKey())
							.getUrl(), param),
					CacheUtil.getLlaveServicio(urlConexionCache,
							"D_ORDENDESUMINISTRO"));
		}
		catch (SystemException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e)
		{
			logger.error(e.getMessage(), e);
		}
	}
	//<METODOS_CARGAR_LISTA>	
	/**
	 * 
	 * Carga la lista listaClaseBodega
	 *
	 */
	public void cargarListaClaseBodega(){
		try
		{
			listaClaseBodega = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									POrdenDeSuministroControladorUrlEnum.URL11750
									.getValue())
							.getUrl(), null));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaDependencia
	 *
	 */
	public void cargarListaDependencia(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try
		{
			listaDependencia = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									POrdenDeSuministroControladorUrlEnum.URL9994
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listaauxiliarCombo
	 *
	 */
	public void cargarListaauxiliarCombo(){
	}
	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 */
	public void cargarListaElemento(){
	}
	/**
	 * 
	 * Carga la lista listaDependenciaDos
	 *
	 */
	public void cargarListaDependenciaDos(){
	}
	/**
	 * 
	 * Carga la lista listaResponsable
	 *
	 */
	public void cargarListaResponsable(){
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaResponsable
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResponsable(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TERCERO", registroAux.getCampos().get("RESPONSABLE"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BT561
	 * en la vista
	 *
	 *
	 */
	public void oprimirBT561() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		//<CODIGO_DESARROLLADO>
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
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton AutorizarTodo
	 * en la vista
	 *
	 *
	 */
	public void oprimirAutorizarTodo() {
		//<CODIGO_DESARROLLADO>
		Registro reg = new Registro();
		reg.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), null);
		reg.getCampos().put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());
		reg.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		reg.getCampos().put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(), registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
		reg.getCampos().put(GeneralParameterEnum.CODIGO.getName(), null);
		updateCantidad(reg);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aprobar
	 * en la vista
	 *
	 *
	 */
	public void oprimirAprobar() {
		//<CODIGO_DESARROLLADO>
		cambiarEstado("APROBADO");
		cargarRegistro(registro.getLlave(), accion, registro.getIndice());
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Rechazar
	 * en la vista
	 *
	 *
	 */
	public void oprimirRechazar() {
		//<CODIGO_DESARROLLADO>
		cambiarEstado("RECHAZADO");
		cargarRegistro(registro.getLlave(), accion, registro.getIndice());
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>	
	//<METODOS_SUBFORM>	
	/**
	 * Metodo de insercion del formulario Subordensuministro
	 * 
	 */   
	public void agregarRegistroSubSubordensuministro() {
	}
	/**
	 * Metodo de edicion del formulario Subordensuministro
	 * 
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSubordensuministro(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		Registro registro = new Registro();
		registro.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), reg.getCampos().get("CANTIDADAPROBADA"));
		registro.getCampos().put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(), reg.getCampos().get(GeneralParameterEnum.ORDENDESUMINISTRO.getName()));
		registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		updateCantidad(registro);

	}

	public void updateCantidad(Registro reg) {
		try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(AprobSolicitudSuministroControladorUrlEnum.URL110012.getValue());

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
					reg.getCampos(),
					reg.getLlave());
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_REGISTRO_MODIFICADO"));
		} catch (SystemException e) {
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}finally{
			cargarListaSubordensuministro();     
		}

	}
	/**
	 * Metodo de eliminacion del formulario Subordensuministro
	 * 
	 * 
	 * @param reg
	 * registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSubordensuministro(Registro reg) {
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 * para el subformulario Subordensuministro
	 *
	 */
	public void cancelarEdicionSubordensuministro(){
		cargarListaSubordensuministro();
	}
	//</METODOS_SUBFORM>	
	/**
	 * Carga el nombre del responsable.
	 */
	private void cargarNombreResponsable()
	{
		Registro reg;

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.SUCURSAL.getName(),
				registro.getCampos().get(GeneralParameterEnum.SUCURSAL
						.getName()));
		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO
						.getName()));

		try
		{
			reg = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									POrdenDeSuministroControladorUrlEnum.URL18002
									.getValue())
							.getUrl(), param));
			responsable = reg.getCampos()
					.get(GeneralParameterEnum.NOMBRE.getName()) == null
					? ""
							: reg.getCampos()
							.get(GeneralParameterEnum.NOMBRE
									.getName())
							.toString();
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	private void cargarNombreAuxiliar()
	{
		Registro reg;

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get(GeneralParameterEnum.AUXILIAR
						.getName()));
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		try
		{
			reg = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									POrdenDeSuministroControladorUrlEnum.URL21552
									.getValue())
							.getUrl(), param));
			nombreAuxiliar = reg.getCampos()
					.get(GeneralParameterEnum.NOMBRE.getName()) == null
					? ""
							: reg.getCampos()
							.get(GeneralParameterEnum.NOMBRE
									.getName())
							.toString();
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * Metodo ejecutado cuando la accion es i
	 */
	private void cargarRegistroI()
	{
		inicializarValores();
		anio = SysmanFunciones.getParteFecha(
				(Date) registro.getCampos().get(
						GeneralParameterEnum.FECHA.getName()),
				Calendar.YEAR);
		cargarListaauxiliarCombo();
	}

	public void inicializarValores()
	{
		registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
				new Date());
		registro.getCampos().put("PERIODICIDAD", 0);
		registro.getCampos().put("UNIDAD_TIEMPO", "DIAS");
	}
	//<METODOS_ADICIONALES>	
	//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		habilitaApro = obtenerParametro("HABILITAR APROBACION DE SOLICITUDES DE SUMINISTRO", "NO").equals("SI");

		if(!habilitaApro) {

			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4492"));

		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 */
	@Override
	public void cargarRegistro() {
		//<CODIGO_DESARROLLADO>
		precargarRegistro();
		nombreAuxiliar = "";
		responsable = "";
		cargarListaResponsable();
		cargarListaauxiliarCombo();
		cargarRegistroI();
		cargarNombreAuxiliar();
		cargarNombreResponsable();
		String campoEstado = SysmanFunciones.toString(registro.getCampos().get("ESTADO"));

		estado = "APROBADO".equalsIgnoreCase(campoEstado) ||
				 "RECHAZADO".equalsIgnoreCase(campoEstado);
		//</CODIGO_DESARROLLADO>
	}


	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Obtiene el valor almacenado en la base de datos para el
	 * parametro ingresado.
	 *
	 * @param nombreParametro
	 * Nombre del parametro a consultar en la base de datos.
	 * @param valorDefault
	 * Valor por omision en caso de nulo.
	 * @return valor asignado al parametro
	 */
	private String obtenerParametro(String nombreParametro,
			String valorDefault)
	{
		String parametro = null;
		try
		{
			parametro = ejbSysmanUtil.consultarParametro(compania,
					nombreParametro, SessionUtil.getModulo(),
					new Date(), true);
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	public void cambiarEstado(String Estado) {
		Registro reg = new Registro();
		try
		{
			reg.getCampos().put(GeneralParameterEnum.ESTADO.getName(), Estado);
			reg.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
			reg.getCampos().put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos().get(GeneralParameterEnum.NUMERO.getName()));
			reg.getCampos().put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							AprobSolicitudSuministroControladorUrlEnum.URL109030.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
					reg.getCampos(),
					reg.getLlave());
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_PROCESO_EJECUTADO"));
		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaClaseBodega
	 * 
	 * @return listaClaseBodega
	 */
	public List<Registro> getListaClaseBodega() {
		return listaClaseBodega;
	}
	/**
	 * Asigna la lista listaClaseBodega
	 * 
	 * @param listaClaseBodega
	 * Variable a asignar en  listaClaseBodega
	 */
	public void setListaClaseBodega(List<Registro> listaClaseBodega) {
		this.listaClaseBodega = listaClaseBodega;
	}
	/**
	 * Retorna la lista listaDependencia
	 * 
	 * @return listaDependencia
	 */
	public List<Registro> getListaDependencia() {
		return listaDependencia;
	}
	/**
	 * Asigna la lista listaDependencia
	 * 
	 * @param listaDependencia
	 * Variable a asignar en  listaDependencia
	 */
	public void setListaDependencia(List<Registro> listaDependencia) {
		this.listaDependencia = listaDependencia;
	}
	/**
	 * Retorna la lista listaauxiliarCombo
	 * 
	 * @return listaauxiliarCombo
	 */
	public List<Registro> getListaauxiliarCombo() {
		return listaauxiliarCombo;
	}
	/**
	 * Asigna la lista listaauxiliarCombo
	 * 
	 * @param listaauxiliarCombo
	 * Variable a asignar en  listaauxiliarCombo
	 */
	public void setListaauxiliarCombo(List<Registro> listaauxiliarCombo) {
		this.listaauxiliarCombo = listaauxiliarCombo;
	}
	/**
	 * Retorna la lista listaElemento
	 * 
	 * @return listaElemento
	 */
	public List<Registro> getListaElemento() {
		return listaElemento;
	}
	/**
	 * Asigna la lista listaElemento
	 * 
	 * @param listaElemento
	 * Variable a asignar en  listaElemento
	 */
	public void setListaElemento(List<Registro> listaElemento) {
		this.listaElemento = listaElemento;
	}
	/**
	 * Retorna la lista listaDependenciaDos
	 * 
	 * @return listaDependenciaDos
	 */
	public List<Registro> getListaDependenciaDos() {
		return listaDependenciaDos;
	}
	/**
	 * Asigna la lista listaDependenciaDos
	 * 
	 * @param listaDependenciaDos
	 * Variable a asignar en  listaDependenciaDos
	 */
	public void setListaDependenciaDos(List<Registro> listaDependenciaDos) {
		this.listaDependenciaDos = listaDependenciaDos;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaResponsable
	 * 
	 * @return listaResponsable
	 */
	public RegistroDataModelImpl getListaResponsable() {
		return listaResponsable;
	}
	/**
	 * Asigna la lista listaResponsable
	 * 
	 * @param listaResponsable
	 * Variable a asignar en  listaResponsable
	 */
	public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
		this.listaResponsable = listaResponsable;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	/**
	 * Retorna la lista listaSubordensuministro
	 * 
	 * @return listaSubordensuministro
	 */
	public List<Registro> getListaSubordensuministro() {
		return listaSubordensuministro;
	}
	/**
	 * Asigna la lista listaSubordensuministro
	 * 
	 * @param listaSubordensuministro
	 * Variable a asignar en  listaSubordensuministro
	 */
	public void setListaSubordensuministro(List<Registro> listaSubordensuministro) {
		this.listaSubordensuministro = listaSubordensuministro;
	}
	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	/**
	 * Retorna el objeto registroSub
	 * 
	 * @return registroSub
	 */
	public Registro getRegistroSub() {
		return registroSub;
	}
	/**
	 * Asigna el objeto registroSub
	 * 
	 * @param registroSub
	 * Variable a asignar en registroSub
	 */
	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}
	//</SET_GET_ADICIONALES>
	/**
	 * @return the nombreAuxiliar
	 */
	public String getNombreAuxiliar() {
		return nombreAuxiliar;
	}
	/**
	 * @param nombreAuxiliar the nombreAuxiliar to set
	 */
	public void setNombreAuxiliar(String nombreAuxiliar) {
		this.nombreAuxiliar = nombreAuxiliar;
	}
	/**
	 * @return the responsable
	 */
	public String getResponsable() {
		return responsable;
	}
	/**
	 * @param responsable the responsable to set
	 */
	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}
	/**
	 * @return the habilitaApro
	 */
	public boolean isHabilitaApro() {
		return habilitaApro;
	}
	/**
	 * @param habilitaApro the habilitaApro to set
	 */
	public void setHabilitaApro(boolean habilitaApro) {
		this.habilitaApro = habilitaApro;
	}
	/**
	 * @return the estado
	 */
	public boolean isEstado() {
		return estado;
	}
	/**
	 * @param estado the estado to set
	 */
	public void setEstado(boolean estado) {
		this.estado = estado;
	}

}
