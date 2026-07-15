/*-
 * LisauxpptalfuenterecursosControlador.java
 *
 * 1.0
 * 
 * 23/10/2020
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.AuxiliarPptalProyectosControladorEnum;
import com.sysman.presupuesto.enums.LisauxpptalfuenterecursosControladorEnum;
import com.sysman.presupuesto.enums.LisauxpptalfuenterecursosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descri
 * pcion para la clase.
 *
 * @version 1.0, 23/10/2020
 * @author dcastiblanco
 */
@ManagedBean
@ViewScoped
public class  LisauxpptalfuenterecursosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Constante a nivel de clase que aloja el nombre de la compania
	 * con la que esta interactuando el usuario
	 */
	private final String modulo;
	/**
	 * constante que almaneca el modulo
	 */
	private final String strCodigo;
	/**
	 * Constante que almacenara la cadena "CODIGO"
	 * 
	 */
	private final String cAnio;
	private String tipoInicial;
	/**
	 * Esta variable se encarga de almacenar el tipo inicial
	 */
	private String tipoFinal;
	/**
	 * Esta variable se encarga de almacenar el tipo final
	 */
	private String fuenteInicial;
	/**
	 * Esta variable se encarga de almacenar la fuente inicial
	 */
	private String fuenteFinal;
	/**
	 * Esta variable se encarga de almacenar la fuente final
	 */
	private String cuentaInicial;
	/**
	 * Esta variable se encarga de almacenar la cuenta inicial
	 */
	private String cuentaFinal;
	/**
	 *Esta variable se encarga de almacenar la cuenta final
	 */
	private Date fechaInicial;
	/**
	 * Esta variable se encarga de almacenar la fecha inicial
	 */
	private Date fechaFinal;
	/**
	 * Esta variable se encarga de almacenar la fecha final
	 */
	private int anio;
	/**
	 * Esta variable se encarga de almacenar la anio
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private RegistroDataModelImpl listaTipoInicial;
	/**
	 *  Listado de registros para la listaTipoInicial
	 */
	private RegistroDataModelImpl listaTipoFinal;
	/**
	 * Listado de registros para la listaTipoFinal
	 */
	private RegistroDataModelImpl listaFuenteInicial;
	/**
	 * Listado de registros para la listaFuenteInicial
	 */
	private RegistroDataModelImpl listaFuenteFinal;
	/**
	 * Listado de registros para la listaFuenteFinal
	 */
	private RegistroDataModelImpl listaCuentaInicial;
	/**
	 * Listado de registros para la listaCuentaInicial
	 */
	private RegistroDataModelImpl listaCuentaFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de LisauxpptalfuenterecursosControlador
	 */
	public LisauxpptalfuenterecursosControlador() {
		super();
		compania = SessionUtil.getCompania();
		strCodigo = GeneralParameterEnum.CODIGO.getName();
		modulo = SessionUtil.getModulo();
		fechaFinal = new Date();
		fechaInicial = new Date();
		anio = SysmanFunciones.ano(fechaInicial);
		cAnio = GeneralParameterEnum.ANO.getName();
		try {
			numFormulario = GeneralCodigoFormaEnum.LISAUXPPTAL_FUENTE_RECURSOS_CONTROLADOR
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			Logger.getLogger(LisauxpptalfuenterecursosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
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
		tipoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaTipoInicial(); 
		cargarListaTipoFinal(); 
		/*cargarListaFuenteInicial(); 
		cargarListaFuenteFinal(); */
		//cargarListaCuentaInicial(); 
		//cargarListaCuentaFinal();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
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
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaTipoInicial
	 *
	 */
	public void cargarListaTipoInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisauxpptalfuenterecursosControladorUrlEnum.URL5264
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, strCodigo);
	}

	/**
	 * 
	 * Carga la lista listaTipoFinal
	 *
	 */
	public void cargarListaTipoFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisauxpptalfuenterecursosControladorUrlEnum.URL5993
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(LisauxpptalfuenterecursosControladorEnum.CODIGOINICIAL.getValue(),
				tipoInicial);

		listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, strCodigo);
	}
	/**
	 * 
	 * Carga la lista listaFuenteInicial
	 *
	 */
	public void cargarListaFuenteInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisauxpptalfuenterecursosControladorUrlEnum.URL11906
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anio);

		listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, strCodigo);
	}
	/**
	 * 
	 * Carga la lista listaFuenteFinal
	 *
	 */
	public void cargarListaFuenteFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisauxpptalfuenterecursosControladorUrlEnum.URL12612
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(LisauxpptalfuenterecursosControladorEnum.CODIGOINICIAL.getValue(),
				fuenteInicial);
		param.put(GeneralParameterEnum.ANO.getName(),anio);

		listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, strCodigo);
	}

	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 *
	 */
	public void cargarListaCuentaInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisauxpptalfuenterecursosControladorUrlEnum.URL3766
						.getValue());
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cAnio, SysmanFunciones.ano(fechaInicial));
        

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
	}
	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 *
	 */
	public void cargarListaCuentaFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						LisauxpptalfuenterecursosControladorUrlEnum.URL4700
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(cAnio, SysmanFunciones.ano(fechaInicial));
	    param.put(LisauxpptalfuenterecursosControladorEnum.PARAM3
	                        .getValue(), cuentaInicial);

	        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	                        urlBean.getUrlConteo().getUrl(), param,
	                        true, strCodigo);
		
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Imprimir
	 * en la vista
	 * @throws SysmanException 
	 *
	 *
	 */
	public void oprimirImprimir() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generarInforme(ReportesBean.FORMATOS.PDF);

	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 * @throws SysmanException 
	 *
	 */
	public void oprimirExcel()  {
		archivoDescarga = null;
		generarInforme(ReportesBean.FORMATOS.EXCEL97);
	}
	public void generarInforme(FORMATOS formatos) {
		HashMap<String, Object> reemplazar = new HashMap<>();

		try {
			reemplazar.put("fechaInicial",
					SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazar.put("fechaFinal",
					SysmanFunciones.convertirAFechaCadena(fechaFinal));
			reemplazar.put("cuentaInicial", cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			reemplazar.put("fuenteInicial", fuenteInicial);
			reemplazar.put("fuenteFinal", fuenteFinal);
			reemplazar.put("tipoInicial", tipoInicial);
			reemplazar.put("tipoFinal", tipoFinal);


			// MANEJO DE PARAMETROS DE REEMPLAZO
			Map<String, Object> parametros = new HashMap<>();
			// MANEJO DE PARAMETROS DEL REPORTE
			parametros.put("PR_CUENTAFINAL", cuentaFinal);

			parametros.put("PR_FECHAFINAL",
					SysmanFunciones.convertirAFechaCadena(fechaFinal));
			parametros.put("PR_CUENTAINICIAL", cuentaInicial);
			parametros.put("PR_FUENTEINICIAL", fuenteInicial);
			parametros.put("PR_FUENTEFINAL", fuenteFinal);
			parametros.put("PR_FECHAINICIAL", SysmanFunciones
					.convertirAFechaCadena(fechaInicial));


			Reporteador.resuelveConsulta("002160AUXILIARPRESUPUESTALFUENTEIDI",
					Integer.valueOf(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(
					"002160AUXILIARPRESUPUESTALFUENTEIDI", parametros,
					ConectorPool.ESQUEMA_SYSMAN, formatos);
		}
		catch (JRException | IOException | SysmanException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control FechaInicial
	 * 
	 * 
	 */
	public void cambiarFechaInicial() {
		//<CODIGO_DESARROLLADO>
		cuentaInicial = null;
		fuenteInicial = null;
		cargarListaCuentaInicial();
		cargarListaFuenteInicial();
	}
	/**
	 * Metodo ejecutado al cambiar el control FechaFinal
	 * 
	 * 
	 */
	public void cambiarFechaFinal() {
		//<CODIGO_DESARROLLADO>

	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipoInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(strCodigo), " ")
				.toString();
		tipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;

		cargarListaTipoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTipoFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get(strCodigo), " ")
				.toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteInicial = SysmanFunciones
				.nvl(registroAux.getCampos().get(strCodigo), " ")
				.toString();

		cargarListaFuenteFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFinal = SysmanFunciones
				.nvl(registroAux.getCampos().get(strCodigo), " ")
				.toString();
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
		cuentaInicial= registroAux.getCampos().get(strCodigo).toString();
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
		cuentaFinal= registroAux.getCampos().get(strCodigo).toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipo
	 * 
	 * @return  tipo
	 */
	public String getTipoInicial() {
		return tipoInicial;
	}
	/**
	 * Asigna la variable  tipo
	 * 
	 * @param  tipo
	 * Variable a asignar en  tipo
	 */
	public void setTipoInicial(String tipoInicial) {
		this.tipoInicial = tipoInicial;
	}
	/**
	 * Retorna la variable tipoFinal
	 * 
	 * @return  tipoFinal
	 */
	public String getTipoFinal() {
		return tipoFinal;
	}
	/**
	 * Asigna la variable  tipoFinal
	 * 
	 * @param  tipoFinal
	 * Variable a asignar en  tipoFinal
	 */
	public void setTipoFinal(String tipoFinal) {
		this.tipoFinal = tipoFinal;
	}
	/**
	 * Retorna la variable fuenteInicial
	 * 
	 * @return  fuenteInicial
	 */
	public String getFuenteInicial() {
		return fuenteInicial;
	}
	/**
	 * Asigna la variable  fuenteInicial
	 * 
	 * @param  fuenteInicial
	 * Variable a asignar en  fuenteInicial
	 */
	public void setFuenteInicial(String fuenteInicial) {
		this.fuenteInicial = fuenteInicial;
	}
	/**
	 * Retorna la variable fuenteFinal
	 * 
	 * @return  fuenteFinal
	 */
	public String getFuenteFinal() {
		return fuenteFinal;
	}
	/**
	 * Asigna la variable  fuenteFinal
	 * 
	 * @param  fuenteFinal
	 * Variable a asignar en  fuenteFinal
	 */
	public void setFuenteFinal(String fuenteFinal) {
		this.fuenteFinal = fuenteFinal;
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
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * Asigna la variable  fechaInicial
	 * 
	 * @param  fechaInicial
	 * Variable a asignar en  fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
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
	 * Retorna la lista listaTipoInicial
	 * 
	 * @return listaTipoInicial
	 */
	public RegistroDataModelImpl getListaTipoInicial() {
		return listaTipoInicial;
	}
	/**
	 * Asigna la lista listaTipoInicial
	 * 
	 * @param listaTipoInicial
	 * Variable a asignar en  listaTipoInicial
	 */
	public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
		this.listaTipoInicial = listaTipoInicial;
	}
	/**
	 * Retorna la lista listaTipoFinal
	 * 
	 * @return listaTipoFinal
	 */
	public RegistroDataModelImpl getListaTipoFinal() {
		return listaTipoFinal;
	}
	/**
	 * Asigna la lista listaTipoFinal
	 * 
	 * @param listaTipoFinal
	 * Variable a asignar en  listaTipoFinal
	 */
	public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
		this.listaTipoFinal = listaTipoFinal;
	}
	/**
	 * Retorna la lista listaFuenteInicial
	 * 
	 * @return listaFuenteInicial
	 */
	public RegistroDataModelImpl getListaFuenteInicial() {
		return listaFuenteInicial;
	}
	/**
	 * Asigna la lista listaFuenteInicial
	 * 
	 * @param listaFuenteInicial
	 * Variable a asignar en  listaFuenteInicial
	 */
	public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
		this.listaFuenteInicial = listaFuenteInicial;
	}
	/**
	 * Retorna la lista listaFuenteFinal
	 * 
	 * @return listaFuenteFinal
	 */
	public RegistroDataModelImpl getListaFuenteFinal() {
		return listaFuenteFinal;
	}
	/**
	 * Asigna la lista listaFuenteFinal
	 * 
	 * @param listaFuenteFinal
	 * Variable a asignar en  listaFuenteFinal
	 */
	public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
		this.listaFuenteFinal = listaFuenteFinal;
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
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
