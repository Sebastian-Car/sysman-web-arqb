/*-
 * Cdpsolicituddisponibilidadpresupuestal.java
 *
 * 1.0
 * 
 * 10/07/2019
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

import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
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
import com.sysman.presupuesto.enums.CdpsolicituddisponibilidadpresupuestalUrlEnum;
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
 * @version 1.0, 10/07/2019
 * @author obarragan
 */
@ManagedBean
@ViewScoped
public class  Cdpsolicituddisponibilidadpresupuestal extends BeanBaseModal{
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
	private String numeroSolicitud;
	private String tipo;
	private String nombreNumeroSolicitud;
	private String nombreTipo;
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
	private RegistroDataModelImpl listanumeroSolicitud;
	private RegistroDataModelImpl listatipo;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de Cdpsolicituddisponibilidadpresupuestal
	 */
	public Cdpsolicituddisponibilidadpresupuestal() {
		super();
		compania = SessionUtil.getCompania();
		anio = SysmanFunciones.ano(new Date());
		modulo = SessionUtil.getModulo();
		try {
			//2095
			numFormulario=GeneralCodigoFormaEnum.CDP_SOLICITUD_DISPONIBILIDAD_PRESUPUESTAL.getCodigo();
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
		cargarListaanio();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListatipo();
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
                                            		CdpsolicituddisponibilidadpresupuestalUrlEnum.URL4828
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
	 * Carga la lista listanumeroSolicitud
	 *
	 */
	public void cargarListanumeroSolicitud(){
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPOSOLICITUD", tipo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		CdpsolicituddisponibilidadpresupuestalUrlEnum.URL0001
                                                        .getValue());

        listanumeroSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());		
	}
	/**
	 * 
	 * Carga la lista listatipo
	 *
	 */
	public void cargarListatipo(){
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);        
        param.put("ID", 1);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		CdpsolicituddisponibilidadpresupuestalUrlEnum.URL0002
                                                        .getValue());

        listatipo = new RegistroDataModelImpl(urlBean.getUrl(),
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
    	try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("anio", anio);
            reemplazar.put("numeroSolicitud", numeroSolicitud);
            reemplazar.put("tipo", tipo);

            Reporteador.resuelveConsulta("002026CdpSolicitudDisponibilidadPresupuestal",
                            Integer.parseInt(modulo), reemplazar, parametros);

            // Parametros diseño reporte
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_ANIO", anio);
            parametros.put("PR_NUMERO_SOLICITUD", numeroSolicitud);
            parametros.put("PR_TIPO", tipo);
            
            parametros.put("PR_DETALLE", nombreNumeroSolicitud);
            
            // Parametros diseño reporte

            archivoDescarga = JsfUtil.exportarStreamed(
                            "002026CdpSolicitudDisponibilidadPresupuestal",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
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
		tipo = null;
		nombreTipo = null;
		numeroSolicitud = null;
		nombreNumeroSolicitud = null;
		inicializar();
		//</CODIGO_DESARROLLADO>
	}

	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listanumeroSolicitud
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilanumeroSolicitud(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		numeroSolicitud= registroAux.getCampos().get("NUMERO").toString();
		nombreNumeroSolicitud = registroAux.getCampos().get("OBJETO").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listatipo
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilatipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipo= registroAux.getCampos().get("ID").toString();
		nombreTipo = registroAux.getCampos().get("NOMBRE").toString();
		numeroSolicitud = null;
		nombreNumeroSolicitud = null;
		cargarListanumeroSolicitud();
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
	 * Retorna la variable numeroSolicitud
	 * 
	 * @return  numeroSolicitud
	 */
	public String getNumeroSolicitud() {
		return numeroSolicitud;
	}
	/**
	 * Asigna la variable  numeroSolicitud
	 * 
	 * @param  numeroSolicitud
	 * Variable a asignar en  numeroSolicitud
	 */
	public void setNumeroSolicitud(String numeroSolicitud) {
		this.numeroSolicitud = numeroSolicitud;
	}
	/**
	 * Retorna la variable tipo
	 * 
	 * @return  tipo
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * Asigna la variable  tipo
	 * 
	 * @param  tipo
	 * Variable a asignar en  tipo
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	/**
	 * Retorna la variable nombreNumeroSolicitud
	 * 
	 * @return  nombreNumeroSolicitud
	 */
	public String getNombreNumeroSolicitud() {
		return nombreNumeroSolicitud;
	}
	/**
	 * Asigna la variable  nombreNumeroSolicitud
	 * 
	 * @param  nombreNumeroSolicitud
	 * Variable a asignar en  nombreNumeroSolicitud
	 */
	public void setNombreNumeroSolicitud(String nombreNumeroSolicitud) {
		this.nombreNumeroSolicitud = nombreNumeroSolicitud;
	}
	/**
	 * Retorna la variable nombreTipo
	 * 
	 * @return  nombreTipo
	 */
	public String getNombreTipo() {
		return nombreTipo;
	}
	/**
	 * Asigna la variable  nombreTipo
	 * 
	 * @param  nombreTipo
	 * Variable a asignar en  nombreTipo
	 */
	public void setNombreTipo(String nombreTipo) {
		this.nombreTipo = nombreTipo;
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
	 * Retorna la lista listanumeroSolicitud
	 * 
	 * @return listanumeroSolicitud
	 */
	public RegistroDataModelImpl getListanumeroSolicitud() {
		return listanumeroSolicitud;
	}
	/**
	 * Asigna la lista listanumeroSolicitud
	 * 
	 * @param listanumeroSolicitud
	 * Variable a asignar en  listanumeroSolicitud
	 */
	public void setListanumeroSolicitud(RegistroDataModelImpl listanumeroSolicitud) {
		this.listanumeroSolicitud = listanumeroSolicitud;
	}
	/**
	 * Retorna la lista listatipo
	 * 
	 * @return listatipo
	 */
	public RegistroDataModelImpl getListatipo() {
		return listatipo;
	}
	/**
	 * Asigna la lista listatipo
	 * 
	 * @param listatipo
	 * Variable a asignar en  listatipo
	 */
	public void setListatipo(RegistroDataModelImpl listatipo) {
		this.listatipo = listatipo;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
