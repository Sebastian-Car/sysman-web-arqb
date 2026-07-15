/*-
 * CdpAfectaSolicitudDisponibilidad.java
 *
 * 1.0
 * 
 * 04/07/2019
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
import com.sysman.bancoproyectos.enums.CdpAfectaSolicitudDisponibilidadUrlEnum;
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
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 04/07/2019
 * @author obarragan
 */
@ManagedBean
@ViewScoped
public class  CdpAfectaSolicitudDisponibilidad extends BeanBaseModal{
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

	private List<Registro> listaAnio;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaNumeroSolicitud;
	private RegistroDataModelImpl listaTipo;

	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de CdpAfectaSolicitudDisponibilidad
	 */
	public CdpAfectaSolicitudDisponibilidad() {
		super();
		compania = SessionUtil.getCompania();
		anio = SysmanFunciones.ano(new Date());
		modulo = SessionUtil.getModulo();
		try {
			//2092
			numFormulario=GeneralCodigoFormaEnum.CDP_AFECTA_SOLCITUD_DISPONIBILIDAD.getCodigo();
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
		cargarListaAnio();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		
		cargarListaTipo();
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
                                            		CdpAfectaSolicitudDisponibilidadUrlEnum.URL4828
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
	 * Carga la lista listaNumeroSolicitud
	 *
	 */
	public void cargarListaNumeroSolicitud(){
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), anio);
        param.put("TIPOT", tipo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		CdpAfectaSolicitudDisponibilidadUrlEnum.URL0001
                                                        .getValue());

        listaNumeroSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
	}
	
	public void cargarListaTipo(){
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
 

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		CdpAfectaSolicitudDisponibilidadUrlEnum.URL0002
                                                        .getValue());

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "TIPOT");
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

            Reporteador.resuelveConsulta("002025CdpAfectaSolicitudDisponibilidad",
                            Integer.parseInt(modulo), reemplazar, parametros);

            // Parametros diseño reporte
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_ANIO", anio);
            parametros.put("PR_NUMERO_SOLICITUD", numeroSolicitud);
            parametros.put("PR_DETALLE", nombreNumeroSolicitud);
            parametros.put("PR_TIPO", tipo);
            
            // Parametros diseño reporte

            archivoDescarga = JsfUtil.exportarStreamed(
                            "002025CdpAfectaSolicitudDisponibilidad",
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
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * 
	 */
	public void cambiarAnio() {
		//<CODIGO_DESARROLLADO>
		tipo =null;
		nombreTipo = null;
		numeroSolicitud = null; 
		nombreNumeroSolicitud = null;		
		inicializar();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiartipo() {
		//<CODIGO_DESARROLLADO>
		nombreTipo = null;
		numeroSolicitud = null; 
		nombreNumeroSolicitud = null;		
		inicializar();
		//</CODIGO_DESARROLLADO>
	}	
	
	public void cambiarnumeroSolicitud() {
		//<CODIGO_DESARROLLADO>
		nombreNumeroSolicitud = null;
		inicializar();
		//</CODIGO_DESARROLLADO>
	}
	
	
	
	
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaNumeroSolicitud
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNumeroSolicitud(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		numeroSolicitud= registroAux.getCampos().get("CODIGO").toString();
		nombreNumeroSolicitud = registroAux.getCampos().get("OBJETO").toString();
	}
	
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaTipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipo= registroAux.getCampos().get("TIPOT").toString();
		nombreTipo = registroAux.getCampos().get("NOMBRE").toString();
		cargarListaNumeroSolicitud();
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
	 * Retorna la lista listaNumeroSolicitud
	 * 
	 * @return listaNumeroSolicitud
	 */
	public RegistroDataModelImpl getListaNumeroSolicitud() {
		return listaNumeroSolicitud;
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
	 * Asigna la lista listaNumeroSolicitud
	 * 
	 * @param listaNumeroSolicitud
	 * Variable a asignar en  listaNumeroSolicitud
	 */
	public void setListaNumeroSolicitud(RegistroDataModelImpl listaNumeroSolicitud) {
		this.listaNumeroSolicitud = listaNumeroSolicitud;
	}
	
	public RegistroDataModelImpl getListaTipo() {
		return listaTipo;
	}
	public void setListaTipo(RegistroDataModelImpl listaTipo) {
		this.listaTipo = listaTipo;
	}	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
