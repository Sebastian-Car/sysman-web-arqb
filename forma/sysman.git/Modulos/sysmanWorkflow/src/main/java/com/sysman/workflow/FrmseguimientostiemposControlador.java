/*-
 * FrmseguimientostiemposControlador.java
 *
 * 1.0
 * 
 * 23/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.workflow.enums.FrmSeguimientoATiemposControladorUrlEnum;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.pattern.LineSeparatorPatternConverter;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/10/2019
 * @author jpulido
 */
@ManagedBean
@ViewScoped
public class  FrmseguimientostiemposControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	 private final String compania ;
	 /**
		 * Atributo usado para almacenar  la lista dependencia inicial
		 *
     **/
private List<Registro> listacmbdependenciaInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private List<Registro> listacmbdependenciaIFinal;
	 
	
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para almacenar anio
	 */
	private String anio;
	/**
	 * Atributo usado para almacenar dependenciaInicial
	 */
	private String dependenciaInicial;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
private String dependenciaFinal;
	
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private String nombreInicial;
	private String nombreFinal;
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * Atributo que almacena la lista de años
	 */
	private List<Registro> listaano;
	
	@EJB
    EjbSysmanUtil ejbSysmanUtil;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmseguimientostiemposControlador
	 */
	public FrmseguimientostiemposControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_SEGUIMIENTO_TIEMPOS
                    .getCodigo();
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
		cargarListaano();
		 cargarListacmbdependenciaInicial();
		 cargarListacmbdependenciaIFinal();
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
	 * Carga la lista listaano
	 *
	 */
	public void cargarListaano(){
		Map<String, Object> parametros = new HashMap<>();

		parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaano = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmSeguimientoATiemposControladorUrlEnum.URL0001
									.getValue())
							.getUrl(),
							parametros));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
    /**
     * 
     * Carga la lista listacmbdependenciaInicial
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListacmbdependenciaInicial(){
	Map<String, Object> parametros = new HashMap<>();
	parametros.put("COMPANIA", compania);
	try {
		listacmbdependenciaInicial = RegistroConverter
				.toListRegistro(requestManager
						.getList(UrlServiceUtil
								.getInstance()
								.getUrlServiceByUrlByEnumID(FrmSeguimientoATiemposControladorUrlEnum.URL0002.getValue()
										)
								.getUrl(), parametros));
		System.out.println("LISTA INICIAL: "+listacmbdependenciaInicial);
	}
	catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}
    /**
     * 
     * Carga la lista listacmbdependenciaIFinal
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void cargarListacmbdependenciaIFinal(){
	Map<String, Object> parametros = new HashMap<>();

	parametros.put("COMPANIA", compania);

	try {
		listacmbdependenciaIFinal = RegistroConverter
				.toListRegistro(requestManager
						.getList(UrlServiceUtil
								.getInstance()
								.getUrlServiceByUrlByEnumID(FrmSeguimientoATiemposControladorUrlEnum.URL0002.getValue()
										)
								.getUrl(), parametros));
		
	}
	catch (SystemException e) {
		logger.error(e.getMessage(), e);
		JsfUtil.agregarMensajeError(e.getMessage());
	}
}
	
	public void seleccionarFilaDependenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaInicial= registroAux.getCampos().get("CODIGO").toString();
		nombreInicial = registroAux.getCampos().get("NOMBRE").toString();
	}
	public void seleccionarFilaDependenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		dependenciaFinal= registroAux.getCampos().get("CODIGO").toString();
		nombreFinal = registroAux.getCampos().get("NOMBRE").toString();
	}
	
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnExcel
	 * en la vista

	 *
	 */
	public void oprimirBtnExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null; 
		generaReporte(FORMATOS.EXCEL);
		
		//</CODIGO_DESARROLLADO>
	}
	
	 private void generaReporte(FORMATOS formato) {
	        try {
	        	
	            String reporte = "002000SeguimientosTiempos";


	            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
	            HashMap<String, Object> reemplazar = new HashMap<>();
	            reemplazar.put("compania", compania);
	            reemplazar.put("ano", anio);
	            reemplazar.put("dependenciaInicial",dependenciaInicial);
	            reemplazar.put("dependenciaFinal",dependenciaFinal);


	            // MANEJO DE PARAMETROS DEL REPORTE
	            Map<String, Object> parametros = new HashMap<>();

	            String sql= Reporteador.resuelveConsulta(reporte,
	                            Integer.parseInt(SessionUtil.getModulo()),
	                            reemplazar);
	            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato, "Seguimiento a tiempos");
            
	        }
	        catch (JRException | IOException | NumberFormatException  | DRException | SQLException | SysmanException e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
	    }
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
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
	 * Retorna la lista listaano
	 * 
	 * @return listaano
	 */
	public List<Registro> getListaano() {
		return listaano;
	}
	/**
	 * Asigna la lista listaano
	 * 
	 * @param listaano
	 * Variable a asignar en  listaano
	 */
	public void setListaano(List<Registro> listaano) {
		this.listaano = listaano;
	}
	public List<Registro> getListacmbdependenciaInicial() {
        return listacmbdependenciaInicial;
    }
    /**
     * Asigna la lista listacmbdependenciaInicial
     * 
     * @param listacmbdependenciaInicial
     * Variable a asignar en  listacmbdependenciaInicial
     */
public void setListacmbdependenciaInicial(List<Registro> listacmbdependenciaInicial) {
        this.listacmbdependenciaInicial = listacmbdependenciaInicial;
    }
    /**
     * Retorna la lista listacmbdependenciaIFinal
     * 
     * @return listacmbdependenciaIFinal
     */
public List<Registro> getListacmbdependenciaIFinal() {
        return listacmbdependenciaIFinal;
    }
    /**
     * Asigna la lista listacmbdependenciaIFinal
     * 
     * @param listacmbdependenciaIFinal
     * Variable a asignar en  listacmbdependenciaIFinal
     */
public void setListacmbdependenciaIFinal(List<Registro> listacmbdependenciaIFinal) {
        this.listacmbdependenciaIFinal = listacmbdependenciaIFinal;
    }
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	public String getDependenciaInicial() {
		return dependenciaInicial;
	}
	public void setDependenciaInicial(String dependenciaInicial) {
		this.dependenciaInicial = dependenciaInicial;
	}
	public String getDependenciaFinal() {
		return dependenciaFinal;
	}
	public void setDependenciaFinal(String dependenciaFinal) {
		this.dependenciaFinal = dependenciaFinal;
	}
	public String getNombreInicial() {
		return nombreInicial;
	}
	public void setNombreInicial(String nombreInicial) {
		this.nombreInicial = nombreInicial;
	}
	public String getNombreFinal() {
		return nombreFinal;
	}
	public void setNombreFinal(String nombreFinal) {
		this.nombreFinal = nombreFinal;
	}
}
