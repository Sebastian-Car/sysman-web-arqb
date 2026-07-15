/*-
 * FrmrepnovedadesprgControlador.java

 */
package com.sysman.workflow;
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
import org.primefaces.model.StreamedContent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmRegistroProcesosControladorUrlEnum;
import com.sysman.workflow.enums.FrmrepnovedadesprgControladorUrlEnum;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 30/01/2023
 * @author avega
 */
@ManagedBean
@ViewScoped
public class  FrmrepnovedadesprgControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	
	private String dependenciaFinal;
	
	private String dependenciaInicial;
	
	private String procesoInicial;
	
	private String procesoFinal;
	
	private Date fechaInicial;
	
	private Date fechaFinal;
	
	private RegistroDataModelImpl listaCbDependenciaFinal;
	
	private RegistroDataModelImpl listaCbDependenciaInicial;
	
	private RegistroDataModelImpl listaCbProcesoInicial;
	
	private RegistroDataModelImpl listaCbProcesoFinal;
	private StreamedContent archivoDescarga;
	private String modulo;
	
	public FrmrepnovedadesprgControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();

		try {
			numFormulario=GeneralCodigoFormaEnum.FR_REPORTE_NOVEDADES_PRORROGA.getCodigo();
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
		cargarListaCbDependenciaInicial();
		cargarListaCbProcesoInicial(); 
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		try {
			fechaInicial = SysmanFunciones.primeroDeMesFecha(new Date());
			fechaFinal= SysmanFunciones.ultimoDiaDate(new Date());
			procesoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
			procesoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
			dependenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
			dependenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaCbDependenciaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCbDependenciaFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), dependenciaInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmrepnovedadesprgControladorUrlEnum.URL62107
						.getValue());

		listaCbDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());


	}
	/**
	 * 
	 * Carga la lista listaCbDependenciaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCbDependenciaInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmrepnovedadesprgControladorUrlEnum.URL62105
						.getValue());

		listaCbDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());


	}
	/**
	 * 
	 * Carga la lista listaCbProcesoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCbProcesoInicial(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmrepnovedadesprgControladorUrlEnum.URL988010
						.getValue());

		listaCbProcesoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());

	}
	/**
	 * 
	 * Carga la lista listaCbProcesoFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCbProcesoFinal(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), procesoInicial);
		

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmrepnovedadesprgControladorUrlEnum.URL988012
						.getValue());

		listaCbProcesoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());


	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPdf() {
		//<CODIGO_DESARROLLADO>
				archivoDescarga=null;     
				generarInforme(ReportesBean.FORMATOS.PDF);
				//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
				archivoDescarga=null;     
				generarInforme(ReportesBean.FORMATOS.PDF);
				//</CODIGO_DESARROLLADO>
	}
	
	private void generarInforme(FORMATOS formato) {
		try {
			String reporte;
			reporte = "002433ReporteNovedadesProrroga";
			Map<String, Object> reemplazos = new HashMap<String, Object>();
			Map<String, Object> parametros = new HashMap<String, Object>();

			reemplazos.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazos.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
			reemplazos.put("procesoIni", procesoInicial);
			reemplazos.put("procesoFin", procesoFinal);
			reemplazos.put("dependenciaIni", dependenciaInicial);
			reemplazos.put("dependenciaFin", dependenciaFinal);

			parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());



			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazos, parametros);
			

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		} catch (JRException | IOException  | ParseException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
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
	 * listaCbDependenciaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbDependenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		setDependenciaFinal(registroAux.getCampos().get("CODIGO").toString());

	
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCbDependenciaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbDependenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaInicial= registroAux.getCampos().get("CODIGO").toString();

		cargarListaCbDependenciaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCbProcesoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbProcesoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		        procesoInicial= registroAux.getCampos().get("CODIGO").toString();
		        
		cargarListaCbProcesoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCbProcesoFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbProcesoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		        procesoFinal= registroAux.getCampos().get("CODIGO").toString();
		        
		
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
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
	 * Retorna la variable procesoInicial
	 * 
	 * @return  procesoInicial
	 */
	public String getProcesoInicial() {
		return procesoInicial;
	}
	/**
	 * Asigna la variable  procesoInicial
	 * 
	 * @param  procesoInicial
	 * Variable a asignar en  procesoInicial
	 */
	public void setProcesoInicial(String procesoInicial) {
		this.procesoInicial = procesoInicial;
	}
	/**
	 * Retorna la variable procesoFinal
	 * 
	 * @return  procesoFinal
	 */
	public String getProcesoFinal() {
		return procesoFinal;
	}
	/**
	 * Asigna la variable  procesoFinal
	 * 
	 * @param  procesoFinal
	 * Variable a asignar en  procesoFinal
	 */
	public void setProcesoFinal(String procesoFinal) {
		this.procesoFinal = procesoFinal;
	}
	/**
	 * Retorna la variable FECHAINICIAL
	 * 
	 * @return  FECHAINICIAL
	 */
	
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCbDependenciaFinal
	 * 
	 * @return listaCbDependenciaFinal
	 */
	public RegistroDataModelImpl getListaCbDependenciaFinal() {
		return listaCbDependenciaFinal;
	}
	/**
	 * @return the fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * Asigna la lista listaCbDependenciaFinal
	 * 
	 * @param listaCbDependenciaFinal
	 * Variable a asignar en  listaCbDependenciaFinal
	 */
	public void setListaCbDependenciaFinal(RegistroDataModelImpl listaCbDependenciaFinal) {
		this.listaCbDependenciaFinal = listaCbDependenciaFinal;
	}
	/**
	 * Retorna la lista listaCbDependenciaInicial
	 * 
	 * @return listaCbDependenciaInicial
	 */
	public RegistroDataModelImpl getListaCbDependenciaInicial() {
		return listaCbDependenciaInicial;
	}
	/**
	 * Asigna la lista listaCbDependenciaInicial
	 * 
	 * @param listaCbDependenciaInicial
	 * Variable a asignar en  listaCbDependenciaInicial
	 */
	public void setListaCbDependenciaInicial(RegistroDataModelImpl listaCbDependenciaInicial) {
		this.listaCbDependenciaInicial = listaCbDependenciaInicial;
	}
	/**
	 * Retorna la lista listaCbProcesoInicial
	 * 
	 * @return listaCbProcesoInicial
	 */
	public RegistroDataModelImpl getListaCbProcesoInicial() {
		return listaCbProcesoInicial;
	}
	/**
	 * Asigna la lista listaCbProcesoInicial
	 * 
	 * @param listaCbProcesoInicial
	 * Variable a asignar en  listaCbProcesoInicial
	 */
	public void setListaCbProcesoInicial(RegistroDataModelImpl listaCbProcesoInicial) {
		this.listaCbProcesoInicial = listaCbProcesoInicial;
	}
	/**
	 * Retorna la lista listaCbProcesoFinal
	 * 
	 * @return listaCbProcesoFinal
	 */
	public RegistroDataModelImpl getListaCbProcesoFinal() {
		return listaCbProcesoFinal;
	}
	/**
	 * Asigna la lista listaCbProcesoFinal
	 * 
	 * @param listaCbProcesoFinal
	 * Variable a asignar en  listaCbProcesoFinal
	 */
	public void setListaCbProcesoFinal(RegistroDataModelImpl listaCbProcesoFinal) {
		this.listaCbProcesoFinal = listaCbProcesoFinal;
	}
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	
	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
