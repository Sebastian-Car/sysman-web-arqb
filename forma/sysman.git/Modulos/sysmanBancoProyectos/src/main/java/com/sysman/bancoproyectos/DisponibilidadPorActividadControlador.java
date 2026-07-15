/*-
 * DisponibilidadPorActividadControlador.java
 *
 * 1.0
 * 
 * 25/06/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

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


import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.bancoproyectos.enums.DisponibilidadPorActividadControladorUrlEnum;
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
 *
 * @version 1.0, 25/06/2019
 * @author obarragan
 */
@ManagedBean
@ViewScoped
public class  DisponibilidadPorActividadControlador extends BeanBaseModal{
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
	private String actividadInicial;
	private String actividadFinal;
	private Date fechaInicial;
	private Date fechaFinal;
	private String nombreActividadInicial;
	private String nombreActividadFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>

	private List<Registro> listaanio;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listaactividadInicial;
	/**
	 */
	private RegistroDataModelImpl listaactividadFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de DisponibilidadPorActividadControlador
	 */
	public DisponibilidadPorActividadControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// 2090
			numFormulario = GeneralCodigoFormaEnum.DISPONIBILIDAD_POR_ACTIVIDAD.getCodigo();
			validarPermisos();
			anio = SysmanFunciones.ano(new Date());
			fechaInicial = new Date();
			fechaFinal = new Date();
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
		cargarListaanio();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaactividadInicial();
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
	 * Carga la lista listaanio
	 *
	 */
	public void cargarListaanio(){
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        

        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		DisponibilidadPorActividadControladorUrlEnum.URL4828
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
	 * Carga la lista listaactividadInicial
	 *
	 */
	public void cargarListaactividadInicial(){
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisponibilidadPorActividadControladorUrlEnum.URL0001
                                                        .getValue());

        listaactividadInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());		
	}
	/**
	 * 
	 * Carga la lista listaactividadFinal
	 *
	 */
	public void cargarListaactividadFinal(){
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), anio);
        param.put("CODIGO_INICIAL", actividadInicial);
        

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		DisponibilidadPorActividadControladorUrlEnum.URL0002
                                                        .getValue());

        listaactividadFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());		
		
	}
	
	
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BT_pdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirBT_pdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BT_excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirBT_excel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		
		generarInforme(FORMATOS.EXCEL97);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	
	
    // <METODO GENERAR INFORME>
    public void generarInforme(FORMATOS formato) {
        if (!validarFechas()) {
            return;
        }
    	try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("anio", anio);
            reemplazar.put("fechaInicial", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("actividadInicial", actividadInicial);
            reemplazar.put("actividadFinal", actividadFinal);

            Reporteador.resuelveConsulta("002020SolicitudesDisponibilidadPorActividad",
                            Integer.parseInt(modulo), reemplazar, parametros);

            // Parametros diseño reporte
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_ANIO", anio);
            parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_ACTIVIDAD_INICIAL", actividadInicial);
            parametros.put("PR_ACTIVIDAD_FINAL", actividadFinal);
            // Parametros diseño reporte

            archivoDescarga = JsfUtil.exportarStreamed(
                            "002020SolicitudesDisponibilidadPorActividad",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | ParseException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage()); 
        }
    }

    // </METODO GENERAR INFORME>	
	
	
	
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control anio
	 * 
	 * 
	 */
	public void cambiaranio() {
		//<CODIGO_DESARROLLADO>
		actividadInicial = null;
		actividadFinal = null;
		nombreActividadInicial = null;
		nombreActividadFinal = null;
		inicializar();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaactividadInicial
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaactividadInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		actividadInicial= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		nombreActividadInicial = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
		cargarListaactividadFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaactividadFinal
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaactividadFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		actividadFinal= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
		nombreActividadFinal = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
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
	 * Retorna la variable actividadInicial
	 * 
	 * @return  actividadInicial
	 */
	public String getActividadInicial() {
		return actividadInicial;
	}
	/**
	 * Asigna la variable  actividadInicial
	 * 
	 * @param  actividadInicial
	 * Variable a asignar en  actividadInicial
	 */
	public void setActividadInicial(String actividadInicial) {
		this.actividadInicial = actividadInicial;
	}
	/**
	 * Retorna la variable actividadFinal
	 * 
	 * @return  actividadFinal
	 */
	public String getActividadFinal() {
		return actividadFinal;
	}
	/**
	 * Asigna la variable  actividadFinal
	 * 
	 * @param  actividadFinal
	 * Variable a asignar en  actividadFinal
	 */
	public void setActividadFinal(String actividadFinal) {
		this.actividadFinal = actividadFinal;
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
	 * Retorna la variable nombreActividadInicial
	 * 
	 * @return  nombreActividadInicial
	 */
	public String getNombreActividadInicial() {
		return nombreActividadInicial;
	}
	/**
	 * Asigna la variable  nombreActividadInicial
	 * 
	 * @param  nombreActividadInicial
	 * Variable a asignar en  nombreActividadInicial
	 */
	public void setNombreActividadInicial(String nombreActividadInicial) {
		this.nombreActividadInicial = nombreActividadInicial;
	}
	/**
	 * Retorna la variable nombreActividadFinal
	 * 
	 * @return  nombreActividadFinal
	 */
	public String getNombreActividadFinal() {
		return nombreActividadFinal;
	}
	/**
	 * Asigna la variable  nombreActividadFinal
	 * 
	 * @param  nombreActividadFinal
	 * Variable a asignar en  nombreActividadFinal
	 */
	public void setNombreActividadFinal(String nombreActividadFinal) {
		this.nombreActividadFinal = nombreActividadFinal;
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
	 * Retorna la lista listaanio
	 * 
	 * @return listaanio
	 */
	public List<Registro> getListaanio() {
		return listaanio;
	}
	/**
	 * Asigna la lista listaanio
	 * 
	 * @param listaanio
	 * Variable a asignar en  listaanio
	 */
	public void setListaanio(List<Registro> listaanio) {
		this.listaanio = listaanio;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaactividadInicial
	 * 
	 * @return listaactividadInicial
	 */
	public RegistroDataModelImpl getListaactividadInicial() {
		return listaactividadInicial;
	}
	/**
	 * Asigna la lista listaactividadInicial
	 * 
	 * @param listaactividadInicial
	 * Variable a asignar en  listaactividadInicial
	 */
	public void setListaactividadInicial(RegistroDataModelImpl listaactividadInicial) {
		this.listaactividadInicial = listaactividadInicial;
	}
	/**
	 * Retorna la lista listaactividadFinal
	 * 
	 * @return listaactividadFinal
	 */
	public RegistroDataModelImpl getListaactividadFinal() {
		return listaactividadFinal;
	}
	/**
	 * Asigna la lista listaactividadFinal
	 * 
	 * @param listaactividadFinal
	 * Variable a asignar en  listaactividadFinal
	 */
	public void setListaactividadFinal(RegistroDataModelImpl listaactividadFinal) {
		this.listaactividadFinal = listaactividadFinal;
	}
	
	
	
	//</SET_GET_LISTAS_COMBO_GRANDE>
    private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            rta = false;
        }
        return rta;
    }	
}
