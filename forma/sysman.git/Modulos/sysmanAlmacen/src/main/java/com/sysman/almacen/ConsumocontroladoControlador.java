/*-
 * ConsumocontroladoControlador.java
 *
 * 1.0
 * 
 * 24/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.almacen.enums.ConsumoControladoControladorUrlEnum;
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
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 * Fomrulario que permite generar infrome de consumo controlado
 *
 * @version 1.0, 24/10/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class  ConsumocontroladoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo ;
	private  String reporte ;
	private String digitos;
	//<DECLARAR_ATRIBUTOS>

	private String dependenciaInicial;

	private String dependenciaFinal;

	private String responsableInicial;

	private String responsableFinal;

	private Date fechaInicial;

	private Date fechaFinal;

	private String nomResponsableInicial;

	private String nomResponsableFinal;

	private String nomDependenciaInicial;

	private String nomDependenciaFinal;

	private StreamedContent archivoDescarga;


	private RegistroDataModelImpl listaCodigoInicial;

	private RegistroDataModelImpl listaCodigoFinal;

	private RegistroDataModelImpl listaResponsableIni;

	private RegistroDataModelImpl listaResponsableFin;

	/**
	 * Crea una nueva instancia de ConsumocontroladoControlador
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	public ConsumocontroladoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.CONSUMO_CONTROLADO_CONTROLADOR.getCodigo();
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
		fechaInicial = new Date();
		fechaFinal = new Date();
		try {
			digitos = ejbSysmanUtil.consultarParametro(compania,
					"DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),false);
		}
		catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		cargarListaCodigoInicial(); 
		cargarListaResponsableIni(); 
		cargarListaResponsableFin();
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
	 * Carga la lista listaCodigoInicial
	 *
	 */
	public void cargarListaCodigoInicial(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID
				(ConsumoControladoControladorUrlEnum.URL001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaCodigoFinal
	 *
	 */
	public void cargarListaCodigoFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID
				(ConsumoControladoControladorUrlEnum.URL002.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("CODIGOINICIAL", dependenciaInicial);
		listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), 
				urlBean.getUrlConteo().getUrl(), param, true,"CODIGO");
	}
	/**
	 * 
	 * Carga la lista listaResponsableIni
	 *
	 */
	public void cargarListaResponsableIni(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID
				(ConsumoControladoControladorUrlEnum.URL003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaResponsableIni = new RegistroDataModelImpl(urlBean.getUrl(), 
				urlBean.getUrlConteo().getUrl(), param, true,"CEDULA");
	}
	/**
	 * 
	 * Carga la lista listaResponsableFin
	 *
	 */
	public void cargarListaResponsableFin(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID
				(ConsumoControladoControladorUrlEnum.URL003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		listaResponsableFin = new RegistroDataModelImpl(urlBean.getUrl(), 
				urlBean.getUrlConteo().getUrl(), param, true,"CEDULA");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton PDF
	 * en la vista
	 *
	 *
	 */
	public void oprimirPDF() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;   
		mtdGenerarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EXCEL
	 * en la vista
	 *
	 *
	 */
	public void oprimirEXCEL() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;     
		mtdGenerarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	public void mtdGenerarInforme(ReportesBean.FORMATOS formato)  
	{
		try{
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("compania",compania);
			reemplazar.put("digitos",digitos);
			reemplazar.put("dependenciaInicial",dependenciaInicial);
			reemplazar.put("dependenciaFinal",dependenciaFinal);
			reemplazar.put("responsableInicial",responsableInicial);
			reemplazar.put("responsableFinal",responsableFinal);
			reemplazar.put("fechaInicial",SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazar.put("fechaFinal",SysmanFunciones.convertirAFechaCadena(fechaFinal));

			String formatoRep = SysmanFunciones
					.nvl(ejbSysmanUtil.consultarParametro(compania,
							"FORMATO CONSUMO CONTROLADO", modulo,
							new Date(), false),
							"001947InformeConsumoControlado")
					.toString();
			String consultaInforme = "001947InformeConsumoControlado";

			if ("001947InformeConsumoControlado".equals(formatoRep)) {

			}
			Reporteador.resuelveConsulta(consultaInforme,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar, parametros);


			parametros.put("PR_FORMS_CONSUMO_CONTROLADO_FECHAINICIAL", 
					SysmanFunciones.convertirAFechaCadena(fechaInicial));
			parametros.put("PR_FORMS_CONSUMO_CONTROLADO_FECHAFINAL",
					SysmanFunciones.convertirAFechaCadena(fechaFinal));

			archivoDescarga = JsfUtil.exportarStreamed(formatoRep,
					parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);


		}
		catch (JRException | IOException | SysmanException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (SystemException e) {
			e.printStackTrace();
		}

	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaInicial = registroAux.getCampos().get("CODIGO").toString();
		nomDependenciaInicial = registroAux.getCampos().get("NOMBRE").toString();
		dependenciaFinal = null;
		nomDependenciaFinal = null;
		cargarListaCodigoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaFinal= registroAux.getCampos().get("CODIGO").toString();
		nomDependenciaFinal = registroAux.getCampos().get("NOMBRE").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaResponsableIni
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResponsableIni(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		responsableInicial = registroAux.getCampos().get("CEDULA").toString();
		nomResponsableInicial = registroAux.getCampos().get("NOMBRE").toString();

	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaResponsableFin
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaResponsableFin(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		responsableFinal= registroAux.getCampos().get("CEDULA").toString();
		nomResponsableFinal = registroAux.getCampos().get("NOMBRE").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable dependenciaInicial
	 * 
	 * @return  dependenciaInicial
	 */
	public String getDependenciaInicial() {
		return dependenciaInicial;
	}
	/**
	 * Asigna la variable  dependenciaInicial
	 * 
	 * @param  dependenciaInicial
	 * Variable a asignar en  dependenciaInicial
	 */
	public void setDependenciaInicial(String dependenciaInicial) {
		this.dependenciaInicial = dependenciaInicial;
	}
	/**
	 * Retorna la variable dependenciaFinal
	 * 
	 * @return  dependenciaFinal
	 */
	public String getDependenciaFinal() {
		return dependenciaFinal;
	}
	/**
	 * Asigna la variable  dependenciaFinal
	 * 
	 * @param  dependenciaFinal
	 * Variable a asignar en  dependenciaFinal
	 */
	public void setDependenciaFinal(String dependenciaFinal) {
		this.dependenciaFinal = dependenciaFinal;
	}
	/**
	 * Retorna la variable responsableInicial
	 * 
	 * @return  responsableInicial
	 */
	public String getResponsableInicial() {
		return responsableInicial;
	}
	/**
	 * Asigna la variable  responsableInicial
	 * 
	 * @param  responsableInicial
	 * Variable a asignar en  responsableInicial
	 */
	public void setResponsableInicial(String responsableInicial) {
		this.responsableInicial = responsableInicial;
	}
	/**
	 * Retorna la variable responsableFinal
	 * 
	 * @return  responsableFinal
	 */
	public String getResponsableFinal() {
		return responsableFinal;
	}
	/**
	 * Asigna la variable  responsableFinal
	 * 
	 * @param  responsableFinal
	 * Variable a asignar en  responsableFinal
	 */
	public void setResponsableFinal(String responsableFinal) {
		this.responsableFinal = responsableFinal;
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
	 * Retorna la variable nomResponsableInicial
	 * 
	 * @return  nomResponsableInicial
	 */
	public String getNomResponsableInicial() {
		return nomResponsableInicial;
	}
	/**
	 * Asigna la variable  nomResponsableInicial
	 * 
	 * @param  nomResponsableInicial
	 * Variable a asignar en  nomResponsableInicial
	 */
	public void setNomResponsableInicial(String nomResponsableInicial) {
		this.nomResponsableInicial = nomResponsableInicial;
	}
	/**
	 * Retorna la variable nomResponsableFinal
	 * 
	 * @return  nomResponsableFinal
	 */
	public String getNomResponsableFinal() {
		return nomResponsableFinal;
	}
	/**
	 * Asigna la variable  nomResponsableFinal
	 * 
	 * @param  nomResponsableFinal
	 * Variable a asignar en  nomResponsableFinal
	 */
	public void setNomResponsableFinal(String nomResponsableFinal) {
		this.nomResponsableFinal = nomResponsableFinal;
	}
	/**
	 * Retorna la variable NomDependenciaInicial
	 * 
	 * @return  NomDependenciaInicial
	 */

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public String getNomDependenciaInicial() {
		return nomDependenciaInicial;
	}
	public void setNomDependenciaInicial(String nomDependenciaInicial) {
		this.nomDependenciaInicial = nomDependenciaInicial;
	}
	public String getNomDependenciaFinal() {
		return nomDependenciaFinal;
	}
	public void setNomDependenciaFinal(String nomDependenciaFinal) {
		this.nomDependenciaFinal = nomDependenciaFinal;
	}
	public String getCompania() {
		return compania;
	}
	public String getModulo() {
		return modulo;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getReporte() {
		return reporte;
	}
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCodigoInicial
	 * 
	 * @return listaCodigoInicial
	 */
	public RegistroDataModelImpl getListaCodigoInicial() {
		return listaCodigoInicial;
	}
	/**
	 * Asigna la lista listaCodigoInicial
	 * 
	 * @param listaCodigoInicial
	 * Variable a asignar en  listaCodigoInicial
	 */
	public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial) {
		this.listaCodigoInicial = listaCodigoInicial;
	}
	/**
	 * Retorna la lista listaCodigoFinal
	 * 
	 * @return listaCodigoFinal
	 */
	public RegistroDataModelImpl getListaCodigoFinal() {
		return listaCodigoFinal;
	}
	/**
	 * Asigna la lista listaCodigoFinal
	 * 
	 * @param listaCodigoFinal
	 * Variable a asignar en  listaCodigoFinal
	 */
	public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
		this.listaCodigoFinal = listaCodigoFinal;
	}
	/**
	 * Retorna la lista listaResponsableIni
	 * 
	 * @return listaResponsableIni
	 */
	public RegistroDataModelImpl getListaResponsableIni() {
		return listaResponsableIni;
	}
	/**
	 * Asigna la lista listaResponsableIni
	 * 
	 * @param listaResponsableIni
	 * Variable a asignar en  listaResponsableIni
	 */
	public void setListaResponsableIni(RegistroDataModelImpl listaResponsableIni) {
		this.listaResponsableIni = listaResponsableIni;
	}
	/**
	 * Retorna la lista listaResponsableFin
	 * 
	 * @return listaResponsableFin
	 */
	public RegistroDataModelImpl getListaResponsableFin() {
		return listaResponsableFin;
	}
	/**
	 * Asigna la lista listaResponsableFin
	 * 
	 * @param listaResponsableFin
	 * Variable a asignar en  listaResponsableFin
	 */
	public void setListaResponsableFin(RegistroDataModelImpl listaResponsableFin) {
		this.listaResponsableFin = listaResponsableFin;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	public String getDigitos() {
		return digitos;
	}
	public void setDigitos(String digitos) {
		this.digitos = digitos;
	}
	public EjbSysmanUtilRemote getEjbSysmanUtil() {
		return ejbSysmanUtil;
	}
	public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil) {
		this.ejbSysmanUtil = ejbSysmanUtil;
	}
}
