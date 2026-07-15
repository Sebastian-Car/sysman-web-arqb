/*-
 * FrmreportedecorrespondenciaControlador.java
 *
 * 1.0
 * 
 * 28/12/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;


import java.text.ParseException;
import java.io.IOException;
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
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
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
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmReporteDeCorrespondenciaControladorUrlEnum;
import net.sf.jasperreports.engine.JRException;


/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 28/12/2021
 * @author carenas
 */
@ManagedBean
@ViewScoped
public class FrmreportedecorrespondenciaControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	//<DECLARAR_ATRIBUTOS>
	
	private final String modulo;
	
	
	private boolean cInterna;

	
	private boolean cExterna;

	private boolean todos;

	private String procesoFinal;

	private String procresoInicial;

	private String dependenciaInicial;
	
	private String dependenciaFinal;
	
	private String nuevoRegistroInicial;
	
	private String nuevoRegistroFinal;
	
	private Date fechaInicial;
	
	private Date fechaFinal;
	private boolean numeracionUnica;
	
	
	private RegistroDataModelImpl listaprocresoInicial;
	
	private RegistroDataModelImpl listadependenciaInicial;
	
	private RegistroDataModelImpl listadependenciaFinal;
	
	private RegistroDataModelImpl listanuevoRegistroInicial;
	
	private RegistroDataModelImpl listanuevoRegistroFinal;
	
	
	private RegistroDataModelImpl listaprocesoFinal;

	public static final String NOMBRE_REPORTE = "FORMATO REPORTE DE CORRESPONDENCIA";


	
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	
	
	/**
	 * Crea una nueva instancia de FrmreportedecorrespondenciaControlador
	 */
	public FrmreportedecorrespondenciaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		fechaInicial = new Date();
		fechaFinal = new Date();
		//fechaInicial = SysmanFunciones.primeroDeMesFecha(new Date());
		//fechaFinal = SysmanFunciones.ultimoDiaDate(new Date());
		dependenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		dependenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		procresoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		procesoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		nuevoRegistroInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		nuevoRegistroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		numeracionUnica = true;
		
		todos = true;

		try {
			// 2328;
			numFormulario = GeneralCodigoFormaEnum.FRM_REPORTE_DE_CORRESPONDENCIA_CONTROLADOR
					.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
			;
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
		//<CARGAR_LISTA>
		cargarListaprocresoInicial();
		cargarListadependenciaInicial();
		cargarListanuevoRegistroInicial();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {

		
	}

	//<METODOS_CARGAR_LISTA>

	/**
	 * 
	 * Carga la lista listadependenciaInicial
	 *
	 */
	public void cargarListadependenciaInicial() {
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmReporteDeCorrespondenciaControladorUrlEnum.URL001
						.getValue());

		listadependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}

	

	/**
	 * 
	 * Carga la lista listadependenciaFinal
	 *
	 */
	public void cargarListadependenciaFinal() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), dependenciaInicial);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmReporteDeCorrespondenciaControladorUrlEnum.URL002
						.getValue());

		listadependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	
	
	/**
	 * 
	 * Carga la lista listaprocresoInicial
	 *
	 */
	public void cargarListaprocresoInicial() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERACION_UNICA.getName(), numeracionUnica?"-1":"0");


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmReporteDeCorrespondenciaControladorUrlEnum.URL988018.getValue());

		listaprocresoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());


	}


	/**
	 * 
	 * Carga la lista listaprocesoFinal
	 *
	 */
	public void cargarListaprocesoFinal() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.NUMERO.getName(), procresoInicial);
		param.put(GeneralParameterEnum.NUMERACION_UNICA.getName(), numeracionUnica?"-1":"0");
		


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmReporteDeCorrespondenciaControladorUrlEnum.URL988020.getValue());

		listaprocesoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listanuevoRegistroInicial
	 *
	 */
	public void cargarListanuevoRegistroInicial() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROCESOINICIAL.getName(), procresoInicial);
		param.put(GeneralParameterEnum.PROCESOFINAL.getName(), procesoFinal);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmReporteDeCorrespondenciaControladorUrlEnum.URL005.getValue());

		listanuevoRegistroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());
	}

	/**
	 * 
	 * Carga la lista listanuevoRegistroFinal
	 *
	 */
	public void cargarListanuevoRegistroFinal() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.PROCESOINICIAL.getName(), procresoInicial);
		param.put(GeneralParameterEnum.PROCESOFINAL.getName(), procesoFinal);
		param.put(GeneralParameterEnum.CODIGO.getName(), nuevoRegistroInicial);


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmReporteDeCorrespondenciaControladorUrlEnum.URL006.getValue());

		listanuevoRegistroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NUMERO.getName());
	}


	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton pdf en la vista
	 *
	 *
	 */
	public void oprimirpdf() {
		archivoDescarga=null;     
		generarInforme(ReportesBean.FORMATOS.PDF);
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton exel en la vista
	 *
	 *
	 */
	public void oprimirexel() {
		archivoDescarga=null;    
		generarInforme(ReportesBean.FORMATOS.EXCEL);
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}
	
	
	public void generarInforme(FORMATOS formato) {
		
		try {
			String reporte;
			reporte = SysmanFunciones.toString(ejbSysmanUtil.consultarParametro(compania, NOMBRE_REPORTE, modulo, new Date(), false));
			if(reporte == null || reporte.isEmpty()) {
				String msg = String.format("Configurar el parametro %s con el valor del reporte correspondiente.", NOMBRE_REPORTE);
				JsfUtil.agregarMensajeInformativo(msg);
				return;
			}
			Map<String, Object> reemplazos = new HashMap<String, Object>();
			
			String tipoCorres = "";
			
			if (cInterna) {
				tipoCorres = "AND TRAMITES.CORRESPONDENCIA = 1";
			}else if(cExterna) {
				tipoCorres = "AND TRAMITES.CORRESPONDENCIA = 2";
			}else {
				tipoCorres = "";
			}
			
			nuevoRegistroInicial = SysmanConstantes.DEFECTOINICIAL_STRING.equals(nuevoRegistroInicial) ? "0" : nuevoRegistroInicial;
			nuevoRegistroFinal = SysmanConstantes.DEFECTOFINAL_STRING.equals(nuevoRegistroFinal) ? SysmanConstantes.CONS_MAX_ID : nuevoRegistroFinal;

			reemplazos.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			reemplazos.put("fechaFinal", SysmanFunciones.convertirAFechaCadena(fechaFinal));
			reemplazos.put("dependenciaInicial", dependenciaInicial);
			reemplazos.put("dependenciaFinal", dependenciaFinal);
			reemplazos.put("procesoInicial", procresoInicial);
			reemplazos.put("procesoFinal", procesoFinal);
			reemplazos.put("registroInicial", nuevoRegistroInicial);
			reemplazos.put("registroFinal", nuevoRegistroFinal);
			reemplazos.put("tipoCorres", tipoCorres);
			reemplazos.put("numeracionUnica", numeracionUnica?"-1":"0");
			
			
			Map<String, Object> parametros = new HashMap<String, Object>();

			parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
			parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());



			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazos, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		} catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | ParseException | SystemException e) {
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
	 * Metodo ejecutado al seleccionar una fila de la lista listaprocesoFinal
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFiladependenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaInicial = registroAux.getCampos().get("CODIGO").toString();

		cargarListadependenciaFinal();

	}

	public void seleccionarFiladependenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaFinal = registroAux.getCampos().get("CODIGO").toString();

	}
	
	public void seleccionarFilaprocresoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		procresoInicial = registroAux.getCampos().get("CODIGO").toString();

		cargarListaprocesoFinal();
	}

	public void seleccionarFilaprocesoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		procesoFinal= registroAux.getCampos().get("CODIGO").toString();
		
		cargarListanuevoRegistroInicial();
	}


	public void seleccionarFilanuevoRegistroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		nuevoRegistroInicial= registroAux.getCampos().get("NUMERO").toString();
		
		cargarListanuevoRegistroFinal();
		
	}

	public void seleccionarFilanuevoRegistroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		nuevoRegistroFinal = registroAux.getCampos().get("NUMERO").toString();
	}
	
	//JM CC 1558 se crean los metodos que son llamados en el listener 
	// para que no genere error y disminuir los errores que se evian en el log 

		public void cambiarcInterna (){
			cInterna = true; 
			cExterna = false;
			todos = false;
		}
		
		public void cambiarcExterna (){
			cInterna = false; 
			cExterna = true;
			todos = false;
		}
		
		public void cambiartodos (){
			cInterna = false; 
			cExterna = false;
			todos = true;
		}
		/**
		 * Metodo ejecutado al cambiar el control NumeracionUnica
		 * 
		 * 
		 */
		public void cambiarNumeracionUnica() {
			//<CODIGO_DESARROLLADO>
			cargarListaprocresoInicial();
			//</CODIGO_DESARROLLADO>
		}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>





	/**
	 * Retorna la variable procesoFinal
	 * 
	 * @return procesoFinal
	 */
	public String getProcesoFinal() {
		return procesoFinal;
	}

	/**
	 * Asigna la variable procesoFinal
	 * 
	 * @param procesoFinal Variable a asignar en procesoFinal
	 */
	public void setProcesoFinal(String procesoFinal) {
		this.procesoFinal = procesoFinal;
	}

	/**
	 * Retorna la variable procresoInicial
	 * 
	 * @return procresoInicial
	 */
	public String getProcresoInicial() {
		return procresoInicial;
	}

	/**
	 * Asigna la variable procresoInicial
	 * 
	 * @param procresoInicial Variable a asignar en procresoInicial
	 */
	public void setProcresoInicial(String procresoInicial) {
		this.procresoInicial = procresoInicial;
	}

	/**
	 * Retorna la variable dependenciaInicial
	 * 
	 * @return dependenciaInicial
	 */
	public String getDependenciaInicial() {
		return dependenciaInicial;
	}

	
	/**
	 * Asigna la variable dependenciaInicial
	 * 
	 * @param dependenciaInicial Variable a asignar en dependenciaInicial
	 */
	public void setDependenciaInicial(String dependenciaInicial) {
		this.dependenciaInicial = dependenciaInicial;
	}

	/**
	 * Retorna la variable dependenciaFinal
	 * 
	 * @return dependenciaFinal
	 */
	public String getDependenciaFinal() {
		return dependenciaFinal;
	}

	/**
	 * Asigna la variable dependenciaFinal
	 * 
	 * @param dependenciaFinal Variable a asignar en dependenciaFinal
	 */
	public void setDependenciaFinal(String dependenciaFinal) {
		this.dependenciaFinal = dependenciaFinal;
	}


	public void setNuevoRegistroInicial(String nuevoRegistroInicial) {
		this.nuevoRegistroInicial = nuevoRegistroInicial;
	}

	/**
	 * Retorna la variable nuevoRegistroFinal
	 * 
	 * @return nuevoRegistroFinal
	 */
	public String getNuevoRegistroFinal() {
		return nuevoRegistroFinal;
	}

	/**
	 * Asigna la variable nuevoRegistroFinal
	 * 
	 * @param nuevoRegistroFinal Variable a asignar en nuevoRegistroFinal
	 */
	public void setNuevoRegistroFinal(String nuevoRegistroFinal) {
		this.nuevoRegistroFinal = nuevoRegistroFinal;
	}

	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>

	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	


	/**
	 * @return the listaprocresoInicial
	 */
	public RegistroDataModelImpl getListaprocresoInicial() {
		return listaprocresoInicial;
	}

	/**
	 * @param listaprocresoInicial the listaprocresoInicial to set
	 */
	public void setListaprocresoInicial(RegistroDataModelImpl listaprocresoInicial) {
		this.listaprocresoInicial = listaprocresoInicial;
	}

	/**
	 * @return the listadependenciaInicial
	 */
	public RegistroDataModelImpl getListadependenciaInicial() {
		return listadependenciaInicial;
	}

	/**
	 * @param listadependenciaInicial the listadependenciaInicial to set
	 */
	public void setListadependenciaInicial(RegistroDataModelImpl listadependenciaInicial) {
		this.listadependenciaInicial = listadependenciaInicial;
	}

	/**
	 * @return the listadependenciaFinal
	 */
	public RegistroDataModelImpl getListadependenciaFinal() {
		return listadependenciaFinal;
	}

	/**
	 * @param listadependenciaFinal the listadependenciaFinal to set
	 */
	public void setListadependenciaFinal(RegistroDataModelImpl listadependenciaFinal) {
		this.listadependenciaFinal = listadependenciaFinal;
	}

	/**
	 * @return the listanuevoRegistroInicial
	 */

	

	/**
	 * @return the listanuevoRegistroFinal
	 */
	public RegistroDataModelImpl getListanuevoRegistroFinal() {
		return listanuevoRegistroFinal;
	}

	public String getNuevoRegistroInicial() {
		return nuevoRegistroInicial;
	}

	public RegistroDataModelImpl getListanuevoRegistroInicial() {
		return listanuevoRegistroInicial;
	}

	public void setListanuevoRegistroInicial(RegistroDataModelImpl listanuevoRegistroInicial) {
		this.listanuevoRegistroInicial = listanuevoRegistroInicial;
	}

	/**
	 * @param listanuevoRegistroFinal the listanuevoRegistroFinal to set
	 */
	public void setListanuevoRegistroFinal(RegistroDataModelImpl listanuevoRegistroFinal) {
		this.listanuevoRegistroFinal = listanuevoRegistroFinal;
	}

	/**
	 * @return the listaprocesoFinal
	 */
	public RegistroDataModelImpl getListaprocesoFinal() {
		return listaprocesoFinal;
	}

	/**
	 * @param listaprocesoFinal the listaprocesoFinal to set
	 */
	public void setListaprocesoFinal(RegistroDataModelImpl listaprocesoFinal) {
		this.listaprocesoFinal = listaprocesoFinal;
	}



	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * @return the fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}

	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}

	public boolean iscInterna() {
		return cInterna;
	}

	public void setcInterna(boolean cInterna) {
		this.cInterna = cInterna;
	}

	public boolean iscExterna() {
		return cExterna;
	}

	public void setcExterna(boolean cExterna) {
		this.cExterna = cExterna;
	}

	public boolean isTodos() {
		return todos;
	}

	public void setTodos(boolean todos) {
		this.todos = todos;
	}

	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * @return the compania
	 */
	public String getCompania() {
		return compania;
	}

	/**
	 * @return the numeracionUnica
	 */
	public boolean isNumeracionUnica() {
		return numeracionUnica;
	}

	/**
	 * @param numeracionUnica the numeracionUnica to set
	 */
	public void setNumeracionUnica(boolean numeracionUnica) {
		this.numeracionUnica = numeracionUnica;
	}
}
