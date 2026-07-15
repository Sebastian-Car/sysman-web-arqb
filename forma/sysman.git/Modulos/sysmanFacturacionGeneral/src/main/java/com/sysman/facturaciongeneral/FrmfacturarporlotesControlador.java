/*-
 * FrmfacturarporlotesControlador.java
 *
 * 1.0
 * 
 * 02/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.impl.EjbFacturacionGeneralDos;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FacturarLotesControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmfacturarporlotesControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.InmueblesControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.enums.ConstanteArchivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar las consultas de los inmuebles por
 * lotes
 *
 * @version 1.0, 02/10/2018
 * @author mvenegas
 * 
 * Se agrega informe 001912 y subinforme 001913
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class FrmfacturarporlotesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    private String usuario;
    /**
     * Variable que almacena el codigo para generar el codigo de
     * barras
     */

    private String codigoEan;
    /**nforme
     * variable tipo de comprobante
     */
    private String tipoComprobante;
    /**
     * variable mes
     */
    private String mes;
    /**
     * variable ubicacion inicial
     */
    private String ubicacionInicial;
    /**
     * variable ubicacion final
     */
    private String ubicacionFinal;
    /**
     * variable tercero inicial
     */
    private String terceroInicial;
    /**
     * variable tercero final
     */
    private String terceroFinal;
    /**
     * variable inmueble inicial
     */
    private String inmuebleInicial;
    /**
     * variable inmubeles final
     */
    private String inmuebleFinal;
    /**
     * variable ano
     */
    private String ano;
    /**
     * variable fecha de facturacion
     */
    private Date fechaFacturacion;
    /**
     * variable primer vencimiento
     */
    private Date primerVencimiento;
    /**
     * variable observaciones
     */
    private String observaciones;
    /**
     * Variable para el segundo vencimiento
     */
    private Date segundoVencimiento;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de meses
     */
    private List<Registro> listacmbMes;
    /**
     * Listado de años
     */
    private List<Registro> listatxtAno;
    /**
     * Listado de ubicaciones iniciales
     */
    private List<Registro> listaubicacionInicial;
    /**
     * Listado de ubicaciones finales
     */
    private List<Registro> listaubicacionFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de tipos de comprobasntes
     */
    private RegistroDataModelImpl listatipocomprobante;
    /**
     * Lista de inmuebles
     */
    private RegistroDataModelImpl listaInmuebleInicial;
    /**
     * TLista de inmuebles
     */
    private RegistroDataModelImpl listaInmuebleFinal;
    private StreamedContent archivoDescarga;

    /**
     * Constante que almacena el valor DENOMINACION
     */
    private String cDenominacion;
    @EJB
    private EjbFacturacionGeneralDos ejbFacturacionDos;
    
    private String cCodigoInmueble;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmfacturarporlotesControlador
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    private Date fechaLimiteDescuento;
    
    private boolean visibleCotizaContrato;
    
    private boolean facturaSinContrato;
    
    private boolean visibleInmueble;
    
    private boolean checkDescuentos;
    
    private boolean checkFacSinContrato;
    
    /**
     * TLista de inmuebles
     */
    private RegistroDataModelImpl listainmuebleFinalFacturacion;
    
    /**
     * TLista de inmuebles
     */
    private RegistroDataModelImpl listainmuebleIncialFacturacion;
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al tercero inicial
     */
    private RegistroDataModelImpl listaTerceroInicial;
    
    /**
     * Lista encargada de almacenar la respuesta de la base de datos
     * que corresponde al tercero final
     */
    private RegistroDataModelImpl listaTerceroFinal;    
    /**
     * variable inmueble inicial
     */
    private String inmuebleInicialFactArrenda;
    /**
     * variable inmubeles final
     */
    private String inmuebleFinalFactArrenda;
    /**
     * Constante encargada de almacenar el String NRO_FACTURA
     */
    private final String nroFacturaCons;
    
    public FrmfacturarporlotesControlador() throws ParseException {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        fechaFacturacion = new Date();
        primerVencimiento = SysmanFunciones.sumarRestarDiasFecha(new Date(), 10);
        usuario = SysmanFunciones.concatenar(SysmanFunciones.nvl(SessionUtil.getUser().getNombre1(), " ").toString(),
                        SysmanFunciones.nvl(SessionUtil.getUser().getNombre2(), " ").toString(),
                        SysmanFunciones.nvl(SessionUtil.getUser().getApellido1(), " ").toString(),
                        SysmanFunciones.nvl(SessionUtil.getUser().getApellido2(), " ").toString());
        codigoEan = "";
        cDenominacion = "DENOMINACION";
        cCodigoInmueble = "CODIGOINMUEBLE";
        nroFacturaCons = "NUMERO_FACTURA";
        fechaLimiteDescuento = new Date();
        try {
            numFormulario = 1946;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
    public void inicializar() {
    	String visibleCotizaContratoStr = "NO";
		try {
			visibleCotizaContratoStr = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania, "ACTIVAR CAMPOS PERSONALIZADOS CONTRATOS", SessionUtil.getModulo(), new Date(), false), "NO")
							.toString();
			facturaSinContrato = "SI".equals(SysmanFunciones
 					.nvl(ejbSysmanUtil.consultarParametro(compania, "FACTURACION MASIVA INMUEBLE SIN CONTRATO",
 							SessionUtil.getModulo(), new Date(), true), "NO"));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
		}
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListatipocomprobante();
        if(visibleCotizaContratoStr.equals("SI")) {
        	cargarListaInmuebleInicialOtros();
        	visibleCotizaContrato = true;
        	visibleInmueble = true;
        	tipoComprobante = "FA";
            inmuebleInicialFactArrenda = "0000000000";
            inmuebleFinalFactArrenda = "ZZZZZZZZZZ";
        } 
        else if(facturaSinContrato)
        {        	
        	tipoComprobante = (String) SessionUtil.getSessionVar(
                    ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        	visibleCotizaContrato = false;
        	visibleInmueble = true;
        	cargarListaubicacionInicial();
        	cargarListaubicacionFinal();
        	cargarListaTerceroInicial();
        }
        else {
        	visibleCotizaContrato = false;
        	visibleInmueble = false;
        	tipoComprobante = "TDV";
            inmuebleInicial = "000000";
            inmuebleFinal = "9999999999";
        	cargarListaInmuebleInicial();
        	cargarListaInmuebleFinal();        	
        }
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListatxtAno();
        cargarListacmbMes();
        // </CARGAR_LISTA>
        abrirFormulario();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbMes
     *
     */
    public void cargarListacmbMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        try {
            listacmbMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL0001.getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listatxtAno
     *
     */
    public void cargarListatxtAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listatxtAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL0002.getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    /**
     * 
     * Carga la lista listaubicacionInicial
     *
     */
	public void cargarListaubicacionInicial() {
		Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        try {
            listaubicacionInicial = RegistroConverter.toListRegistro(
            			 requestManager.getList(UrlServiceUtil.getInstance()
            							 .getUrlServiceByUrlByEnumID(
            									 InmueblesControladorUrlEnum.URL1982001.getValue())
            							 .getUrl(),param));
        } catch(SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        }
	}
	
	/**
     * 
     * Carga la lista listaubicacionFinal
     *
     */
	public void cargarListaubicacionFinal() {
		Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        try {
            listaubicacionFinal = RegistroConverter.toListRegistro(
            			 requestManager.getList(UrlServiceUtil.getInstance()
            							 .getUrlServiceByUrlByEnumID(
            									 InmueblesControladorUrlEnum.URL1982001.getValue())
            							 .getUrl(),param));
        } catch(SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(),e);
        }
	}
	
	/**
	 * 
	 * Carga la lista listaTerceroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaTerceroInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL14001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listaTerceroInicial = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.NIT.getName());
	}
	
	/**
	 * 
	 * Carga la lista listaTerceroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaTerceroFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL14176.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TERCEROINICIAL.getName(), terceroInicial);
		
		listaTerceroFinal = new RegistroDataModelImpl(
		                urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param,
		                true, GeneralParameterEnum.NIT.getName());
	}

    /**
     * 
     * Carga la lista listatipocomprobante
     *
     */
    public void cargarListatipocomprobante() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL0003.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listatipocomprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaInmuebleInicial
     *
     */
    public void cargarListaInmuebleInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL0004.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaInmuebleInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cDenominacion);

    }

    /**
     * 
     * Carga la lista listaInmuebleFinal
     *
     * 
     */
    public void cargarListaInmuebleFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL0004.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaInmuebleFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cDenominacion);
    }
    /**
     * 
     * Carga la lista listaInmuebleInicial
     *
     */
    public void cargarListaInmuebleInicialOtros() {

        if(facturaSinContrato)
        {
        	UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL1947005.getValue());

		    Map<String, Object> param = new TreeMap<>();
		    param.put(GeneralParameterEnum.COMPANIA.getName(),
		                    compania);
		    param.put(GeneralParameterEnum.UBICACIONINICIAL.getName(),
                    ubicacionInicial);
		    param.put(GeneralParameterEnum.UBICACIONFINAL.getName(),
                    ubicacionFinal);
		    param.put(GeneralParameterEnum.TERCEROINICIAL.getName(),
                    terceroInicial.toString());
		    param.put(GeneralParameterEnum.TERCEROFINAL.getName(),
                    terceroFinal);
		
		    listainmuebleIncialFacturacion = new RegistroDataModelImpl(urlBean.getUrl(),
		                    urlBean.getUrlConteo().getUrl(), param,
		                    true, cCodigoInmueble);
        }
        else
        {
	    	UrlBean urlBean = UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL1947001.getValue());
	
	        Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(),
	                        compania);
	
	        listainmuebleIncialFacturacion = new RegistroDataModelImpl(urlBean.getUrl(),
	                        urlBean.getUrlConteo().getUrl(), param,
	                        true, cCodigoInmueble);
        }

    }
    /**
     * 
     * Carga la lista listaInmuebleFinal
     *
     * 
     */
    public void cargarListaInmuebleFinalOtros() 
    {
    	if(facturaSinContrato)
        {
    		UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL1947007.getValue());

		    Map<String, Object> param = new TreeMap<>();
		    param.put(GeneralParameterEnum.COMPANIA.getName(),
		                    compania);		    
		    param.put(GeneralParameterEnum.CODIGOINMUEBLEINCIAL.getName(),
		            inmuebleInicialFactArrenda);
		    param.put(GeneralParameterEnum.UBICACIONINICIAL.getName(),
                    ubicacionInicial);
		    param.put(GeneralParameterEnum.UBICACIONFINAL.getName(),
                    ubicacionFinal);
		    param.put(GeneralParameterEnum.TERCEROINICIAL.getName(),
                    terceroInicial);
		    param.put(GeneralParameterEnum.TERCEROFINAL.getName(),
                    terceroFinal);
		
		    listainmuebleFinalFacturacion = new RegistroDataModelImpl(urlBean.getUrl(),
		                    urlBean.getUrlConteo().getUrl(), param,
		                    true, cCodigoInmueble);
        }
    	else
    	{
	    	UrlBean urlBean = UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL1947003.getValue());
	
	        Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(),
	                        compania);
	        param.put(GeneralParameterEnum.CODIGOINMUEBLEINCIAL.getName(),
	                inmuebleInicialFactArrenda);
	
	        listainmuebleFinalFacturacion = new RegistroDataModelImpl(urlBean.getUrl(),
	                        urlBean.getUrlConteo().getUrl(), param,
	                        true, cCodigoInmueble);
    	}
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cmbMes
     * 
     * 
     */
    public void cambiarcmbMes() {
        // <CODIGO_DESARROLLADO>
        observaciones = "Facturación del mes de " + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)];
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtAno
     * 
     * 
     */
    public void cambiartxtAno() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        observaciones = null;
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control ubicacionInicial
     * 
     * 
     */
    public void cambiarubicacionInicial() 
    {
    	ubicacionFinal = null;
    	inmuebleInicial = null;
    	inmuebleFinal = null;    	
    }
    
    /**
     * Metodo ejecutado al cambiar el control ubicacionFinal
     * 
     * 
     */
    public void cambiarubicacionFinal() 
    {
    	inmuebleInicial = null;
    	inmuebleFinal = null;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroInicial
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event) 
    {
        Registro registroAux = (Registro) event.getObject();
       	terceroInicial = registroAux.getCampos().get(GeneralParameterEnum.NIT.getName()).toString(); 
       	cargarListaTerceroFinal();
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroFinal
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event) 
    {
        Registro registroAux = (Registro) event.getObject();
       	terceroFinal = registroAux.getCampos().get(GeneralParameterEnum.NIT.getName()).toString();  
       	cargarListaInmuebleInicialOtros();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipocomprobante
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipocomprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipoComprobante = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaInmuebleInicial
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaInmuebleInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
       	inmuebleInicial = registroAux.getCampos().get(cDenominacion).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaInmuebleFinal
     *
     *
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaInmuebleFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        inmuebleFinal = registroAux.getCampos().get(cDenominacion).toString();
    }
    /**
     *  Metodo ejecutado al seleccionar una fila de la lista
     * listaInmuebleFinal facturacion general de contratos
     * @param event
     */
    public void seleccionarFilainmuebleFinalFacturacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
       	inmuebleFinalFactArrenda = registroAux.getCampos().get(cCodigoInmueble).toString();
    }
    /**
     *  Metodo ejecutado al seleccionar una fila de la lista
     * listaInmuebleFinal facturacion general de contratos
     * @param event
     */
    public void seleccionarFilainmuebleIncialFacturacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        inmuebleInicialFactArrenda = registroAux.getCampos().get(cCodigoInmueble).toString();
        cargarListaInmuebleFinalOtros();
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton btnCalcularFacturacion en
     * la vista
     *
     *
     * 
     */
    public void oprimirbtnCalcularFacturacion() {
        // <CODIGO_DESARROLLADO>
        try {
        	if(visibleCotizaContrato) {
        		ejbFacturacionDos.calculoFacturacionContratos(compania,
	                    Integer.parseInt(ano), Integer.parseInt(mes),
	                    tipoComprobante, fechaFacturacion, primerVencimiento,
	                    segundoVencimiento, inmuebleInicialFactArrenda, inmuebleFinalFactArrenda,
	                    observaciones, SessionUtil.getUser().getCodigo(), 0, checkDescuentos?"SI":"NO", fechaLimiteDescuento);
        	}
        	else if(checkFacSinContrato)
        	{
        		ejbFacturacionDos.calculoFacturacionSinContrato(compania,
                        Integer.parseInt(ano), Integer.parseInt(mes),
                        tipoComprobante, fechaFacturacion, primerVencimiento,
                        segundoVencimiento,ubicacionInicial,ubicacionFinal,
                        terceroInicial, terceroFinal, inmuebleInicialFactArrenda, inmuebleFinalFactArrenda,
                        observaciones, SessionUtil.getUser().getCodigo(), 0);
        	}
        	else 
        	{	
        		ejbFacturacionDos.calculoFacturacionCorabastos(compania,
                            Integer.parseInt(ano), Integer.parseInt(mes),
                            tipoComprobante, fechaFacturacion, primerVencimiento,
                            segundoVencimiento, inmuebleInicial, inmuebleFinal,
                            observaciones, SessionUtil.getUser().getCodigo(), 0);
        	}
            JsfUtil.agregarMensajeInformativo("Proceso ejecutado exitosamente.");
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnImprimirFacturacion en
     * la vista
     *
     *
     * 
     */
    public void oprimirbtnImprimirFacturacion() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            String reporte = "001912FACCOR4F";
            String subreporte = "001913detalleFACCORA";
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            List<byte[]> listadoReportesFacturas =  new ArrayList<>();
            if(visibleCotizaContrato) {
            	reporte = obtenerFormato();
            	// se consultas las facturas segun el mes y los imuebles
            	Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                param.put("IMFINICIAL", inmuebleInicialFactArrenda);
                param.put("IMFFINAL", inmuebleFinalFactArrenda);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
                
                List<Registro> listaFactEntreInmuebles = RegistroConverter.toListRegistro(
                                    requestManager.getList(UrlServiceUtil.getInstance()
                                                    .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL661079.getValue())
                                                    .getUrl(), param));
                
            	for(Registro reg: listaFactEntreInmuebles) {
	                String factura = retornarString(reg, nroFacturaCons);
	               
	                if (ejbSysmanUtil.consultarParametro(compania,
	                                "SF CODIGO EAN POR CADA TIPO DE COBRO",
	                                SessionUtil.getModulo(), new Date(), false)
	                                .equals("NO"))
	                {
	
	                    codigoEan = SysmanFunciones
	                                    .nvl(ejbSysmanUtil.consultarParametro(compania,
	                                                    "SF CODIGO EAN",
	                                                    SessionUtil.getModulo(),
	                                                    new Date(), false), "")
	                                    .toString();
	                }
	                else
	                {
	                    param = new TreeMap<>();
	                    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	                    param.put("ANO", ano);
	                    param.put(GeneralParameterEnum.CODIGO.getName(), tipoComprobante);
	                    Registro rs = RegistroConverter
	                                    .toRegistro(requestManager.get(
	                                                    UrlServiceUtil.getInstance()
	                                                                    .getUrlServiceByUrlByEnumID(
	                                                                                    FacturacionconceptosControladorUrlEnum.URL1717
	                                                                                                    .getValue())
	                                                                    .getUrl(),
	                                                    param));
	                    codigoEan = rs.getCampos().get("CODIGOEAN").toString();
	                }            
	                
	                reemplazar.put("codigoEan", codigoEan);
	                reemplazar.put("anio", ano);
	                reemplazar.put("tipoFactura", tipoComprobante);
	                reemplazar.put("facturaInicial", factura);
	                reemplazar.put("facturaFinal", factura);
	                reemplazar.put("compania", compania);
	                // PARAMETROS PARA GENERACION DE INFORME
	                parametros.put("PR_NOMBRECOMPANIA",
	                                SessionUtil.getCompaniaIngreso().getNombre());
	                parametros.put("PR_NITCOMPANIA",
	                                SessionUtil.getCompaniaIngreso().getNit());
	                
	                //inicio desarrollo parametros mrosero
	                parametros.put("PR_DIRECCIONCOMPANIA",
	                        SessionUtil.getCompaniaIngreso().getDireccion());
	                parametros.put("PR_TELEFONOCOMPANIA",
	                        SessionUtil.getCompaniaIngreso().getTelefono());
	                parametros.put("PR_CIUDADCOMPANIA",
	                        SessionUtil.getCompaniaIngreso().getCiudad());
	                //fin desarrollo mrosero
	                
	                
	                parametros.put("PR_USUARIO", SessionUtil.getUser().getCodigo());
	                
	                parametros.put("PR_CUENTABANCO1",
	                                obtenerParametro("SF BANCO CUENTA 1", ""));
	                parametros.put("PR_CUENTABANCO2",
	                                obtenerParametro("SF BANCO CUENTA 2", ""));
	                parametros.put("PR_CUENTABANCO3",
	                                obtenerParametro("SF BANCO CUENTA 3", ""));
	                parametros.put("PR_CUENTABANCO4",
	                                obtenerParametro("SF BANCO CUENTA 4", ""));
	                parametros.put("PR_SF_CPTO_PPAL_PRODESARROLLO",
	                                obtenerParametro(
	                                                "SF CONCEPTO PRINCIPAL PRODESARROLLO",
	                                                ""));
	                parametros.put("PR_SF_MANEJA_CODIGO_BARRAS",
	                                "SI".equalsIgnoreCase(obtenerParametro(
	                                                "SF MANEJA CODIGO DE BARRAS",
	                                                "SI")));
	                parametros.put("PR_ENCABEZADO",ejbSysmanUtil.consultarParametro(compania,"ENCABEZADO FORMATO FACTURA",modulo, new Date(), false));
	                // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muÃ±oz)
	                parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
	                // FIN IMPLEMENTACION MARCA_BLANCA
	                
	                if(visibleCotizaContrato) {
	    				//PARAMETROS PARA REPORTE PERSONALIZADO CON VALORES DE CANTIDAD AGRUPADO Y VALORES UNITARIOS
	    				String cantidadesConcatenadas = "";
	    				String valUnitaroConcatenadas = "";
	    				double baseGravable = 0.0;
	    				NumberFormat formatoImporte = NumberFormat.getCurrencyInstance();
	    		    	//Si se desea forzar el formato español:
	    		    	formatoImporte = NumberFormat.getCurrencyInstance(new Locale("es","CO"));
	    		    	
	    		    	
	    		    	param.clear();
	    	            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    	            param.put("TIPOCOBRO", tipoComprobante);
	    	            param.put("CODIGOCOBRO", reg.getCampos().get("CODIGO_COBRO").toString());
	    	            param.put(GeneralParameterEnum.ANO.getName(), ano);
	    	            List<Registro> listaSubfacturacionconceptosbg = RegistroConverter
	    	                            .toListRegistro(
	    	                                            requestManager
	    	                                                            .getList(
	    	                                                                            UrlServiceUtil.getInstance()
	    	                                                                                            .getUrlServiceByUrlByEnumID(
	    	                                                                                                            GenericUrlEnum.SF_DETALLE_COBRO
	    	                                                                                                                            .getGridKey())
	    	                                                                                            .getUrl(),
	    	                                                                            param),
	    	                                            CacheUtil.getLlaveServicio(
	    	                                                            UrlServiceCache.SYSMANDSUNIST,
	    	                                                            GenericUrlEnum.SF_DETALLE_COBRO
	    	                                                                            .getTable()));
	    	            
	    				for(Registro regAux: listaSubfacturacionconceptosbg) {
	    					if(Double.parseDouble(regAux.getCampos().get("VALOR_IVA").toString()) > 0 ){
	    						baseGravable = baseGravable + Double.parseDouble(regAux.getCampos().get("VALOR_BASE").toString());
	    					}
	    					if(cantidadesConcatenadas.equals("") && valUnitaroConcatenadas.equals("")){
	    						cantidadesConcatenadas = regAux.getCampos().get("CANTIDAD").toString();
	    						valUnitaroConcatenadas = formatoImporte.format(regAux.getCampos().get("VALOR_UNITARIO"));
	    					}else {
	    						cantidadesConcatenadas = cantidadesConcatenadas + "; " +regAux.getCampos().get("CANTIDAD").toString();
	    						valUnitaroConcatenadas = valUnitaroConcatenadas + "; " +formatoImporte.format(regAux.getCampos().get("VALOR_UNITARIO"));
	    					}
	    				}
	    				
	    				parametros.put("PR_VALORBASE", baseGravable);
	    				parametros.put("PR_CANTIDADESCONCAT", cantidadesConcatenadas);
	    				parametros.put("PR_VALUNITARIOCONCAT", valUnitaroConcatenadas);
	    				
	    				Map<String, Object> params = new TreeMap<>();
	    		        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    		        params.put(GeneralParameterEnum.TERCERO.getName(), reg.getCampos().get("TERCERO"));
	    		        params.put("FECHAFILTRO", new SimpleDateFormat("dd/MM/YYYY").format(new Date()).toString());
	    		        
	    		        Registro rs = RegistroConverter
	    		                            .toRegistro(requestManager.get(
	    		                                            UrlServiceUtil.getInstance()
	    		                                                            .getUrlServiceByUrlByEnumID(
	    		                                                                            FacturacionconceptosControladorUrlEnum.URL39124
	    		                                                                                            .getValue())
	    		                                                            .getUrl(),
	    		                                            params));

	    		       if (rs.getCampos().get("TOTAL_VENCIDO") != null){
	    		    	    parametros.put("PR_DEUDA_ANT_TERCERO", formatoImporte.format(rs.getCampos().get("TOTAL_VENCIDO"))); 	
	    		        }else {
	    		        	parametros.put("PR_DEUDA_ANT_TERCERO", "$ 0.0");
	    		        }
	    			}
	                Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
                	InputStream reporteFact = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato).getStream();
                	
                	listadoReportesFacturas.add(IOUtils.toByteArray(reporteFact));
            	}
            	if(!listaFactEntreInmuebles.isEmpty()) {
                	InputStream reporteStram = new ByteArrayInputStream(
                    		mergePdfBytes(listadoReportesFacturas));
                    archivoDescarga = new DefaultStreamedContent(reporteStram,
                            ConstanteArchivo.PDF.getContentType(),
                            "Facturas inmuebles entre: " + inmuebleInicial + " - " + inmuebleFinal
                                + ConstanteArchivo.PDF.getExtension());
            	}
            	
            }else {
	            if (ejbSysmanUtil.consultarParametro(compania,
	                            "SF CODIGO EAN POR CADA TIPO DE COBRO",
	                            SessionUtil.getModulo(), new Date(), false).equals("NO")) {
	                codigoEan = SysmanFunciones.nvl(
	                                ejbSysmanUtil.consultarParametro(compania, "SF CODIGO EAN", SessionUtil.getModulo(), new Date(), false), "")
	                                .toString();
	            }
	            else {
	                Map<String, Object> param = new TreeMap<>();
	                param.put("COMPANIA", compania);
	                param.put("ANO", ano);
	                reemplazar.put("comprobanteInicial", inmuebleInicial);
	                Registro rs = RegistroConverter.toRegistro(
	                                requestManager.get(UrlServiceUtil.getInstance()
	                                                .getUrlServiceByUrlByEnumID(
	                                                                FacturacionconceptosControladorUrlEnum.URL1717
	                                                                                .getValue())
	                                                .getUrl(), param));
	                codigoEan = rs.getCampos().get("CODIGOEAN").toString();
	            }
	
	            reemplazar.put("codigoEan", codigoEan);
	            reemplazar.put("compania", compania);
	            reemplazar.put("anio", ano);
	            reemplazar.put("tipoFactura", tipoComprobante);
	            reemplazar.put("comprobanteInicial", inmuebleInicial);
	            reemplazar.put("comprobanteFinal", inmuebleFinal);
	            reemplazar.put("usuario", usuario);
	
	            parametros.put("PR_STRSQL_DETALLE_FAC_CORA", subreporte);
	            
	            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
	            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            }            
        }
        catch (IOException | SysmanException | JRException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * metodo que permite tomar los bytes y unirlos en un solo pdf
     * @param pdfByteList
     * @return
     * @throws IOException
     */
    public static byte[] mergePdfBytes(List<byte[]> pdfByteList) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (byte[] pdfBytes : pdfByteList) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
            merger.addSource(inputStream);
        }

        merger.setDestinationStream(outputStream);
        merger.mergeDocuments(null);

        return outputStream.toByteArray();
    }
    /**
     * 
     * @param reg
     * @param campo
     * @return
     */
    private String retornarString(Registro reg, String campo)
    {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
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
                            new Date(),
                            true);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }
    /**
     * Permite obtener el nombre del formato con el que se desea
     * generar la factura
     *
     * @return nombre del formato a generar
     */
    private String obtenerFormato()
    {
        String formato = "";
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        params.put(FacturarLotesControladorEnum.TIPOCOBRO.getValue(),
                        tipoComprobante);

        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL22805
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            if ((rs != null)
                && !SysmanFunciones.validarCampoVacio(rs.getCampos(),
                                "FORMATO_FACTURA"))
            {
                formato = rs.getCampos().get("FORMATO_FACTURA").toString();
            }
            else
            {
                formato = obtenerParametro("SF FORMATO FACTURACION",
                                "001493INFFACSTD010");
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return formato;
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton btnContabilizarFacturacion
     * en la vista
     *
     *
     * 
     */
    public void oprimirbtnContabilizarFacturacion() {
        // <CODIGO_DESARROLLADO>
    	Long codigoCobro = new Long("0");
    	try {
    		Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoComprobante);
			param.put("INMUEBLEINCIAL", SysmanFunciones.nvl(inmuebleInicialFactArrenda, "0000000000"));
			param.put("INMUEBLEFINAL", SysmanFunciones.nvl(inmuebleFinalFactArrenda, "ZZZZZZZZZZ"));
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			
			List<Registro> listReg = RegistroConverter.toListRegistro(
                    requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmfacturarporlotesControladorUrlEnum.URL666016.getValue())
                            .getUrl(), param));
			for(Registro reg: listReg) {
				codigoCobro = (Long) reg.getCampos().get("CODIGO_COBRO");
				ejbFacturacionDos.facturarConceptos(compania, tipoComprobante,
				        codigoCobro, 0, Integer.parseInt(ano),
				        SessionUtil.getUser().getCodigo());
			}
			JsfUtil.agregarMensajeInformativo("Proceso ejecutado exitosamente.");
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>      
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la variable tipoComprobante
     * 
     * @return tipoComprobante
     */
    public String getTipoComprobante() {
        return tipoComprobante;
    }

    /**
     * Asigna la variable tipoComprobante
     * 
     * @param tipoComprobante
     * Variable a asignar en tipoComprobante
     */
    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable inmuebleInicial
     * 
     * @return inmuebleInicial
     */
    public String getInmuebleInicial() {
        return inmuebleInicial;
    }

    /**
     * Asigna la variable inmuebleInicial
     * 
     * @param inmuebleInicial
     * Variable a asignar en inmuebleInicial
     */
    public void setInmuebleInicial(String inmuebleInicial) {
        this.inmuebleInicial = inmuebleInicial;
    }

    /**
     * Retorna la variable inmuebleFinal
     * 
     * @return inmuebleFinal
     */
    public String getInmuebleFinal() {
        return inmuebleFinal;
    }

    /**
     * Asigna la variable inmuebleFinal
     * 
     * @param inmuebleFinal
     * Variable a asignar en inmuebleFinal
     */
    public void setInmuebleFinal(String inmuebleFinal) {
        this.inmuebleFinal = inmuebleFinal;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable fechaFacturacion
     * 
     * @return fechaFacturacion
     */
    public Date getFechaFacturacion() {
        return fechaFacturacion;
    }

    /**
     * Asigna la variable fechaFacturacion
     * 
     * @param fechaFacturacion
     * Variable a asignar en fechaFacturacion
     */
    public void setFechaFacturacion(Date fechaFacturacion) {
        this.fechaFacturacion = fechaFacturacion;
    }

    /**
     * Retorna la variable primerVencimiento
     * 
     * @return primerVencimiento
     */
    public Date getPrimerVencimiento() {
        return primerVencimiento;
    }

    /**
     * Asigna la variable primerVencimiento
     * 
     * @param primerVencimiento
     * Variable a asignar en primerVencimiento
     */
    public void setPrimerVencimiento(Date primerVencimiento) {
        this.primerVencimiento = primerVencimiento;
    }

    /**
     * Retorna la variable observaciones
     * 
     * @return observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Asigna la variable observaciones
     * 
     * @param observaciones
     * Variable a asignar en observaciones
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Retorna la variable segundoVencimiento
     * 
     * @return segundoVencimiento
     */
    public Date getSegundoVencimiento() {
        return segundoVencimiento;
    }

    /**
     * Asigna la variable segundoVencimiento
     * 
     * @param segundoVencimiento
     * Variable a asignar en segundoVencimiento
     */
    public void setSegundoVencimiento(Date segundoVencimiento) {
        this.segundoVencimiento = segundoVencimiento;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbMes
     * 
     * @return listacmbMes
     */
    public List<Registro> getListacmbMes() {
        return listacmbMes;
    }

    /**
     * Asigna la lista listacmbMes
     * 
     * @param listacmbMes
     * Variable a asignar en listacmbMes
     */
    public void setListacmbMes(List<Registro> listacmbMes) {
        this.listacmbMes = listacmbMes;
    }

    /**
     * Retorna la lista listatxtAno
     * 
     * @return listatxtAno
     */
    public List<Registro> getListatxtAno() {
        return listatxtAno;
    }

    /**
     * Asigna la lista listatxtAno
     * 
     * @param listatxtAno
     * Variable a asignar en listatxtAno
     */
    public void setListatxtAno(List<Registro> listatxtAno) {
        this.listatxtAno = listatxtAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listatipocomprobante
     * 
     * @return listatipocomprobante
     */
    public RegistroDataModelImpl getListatipocomprobante() {
        return listatipocomprobante;
    }

    /**
     * Asigna la lista listatipocomprobante
     * 
     * @param listatipocomprobante
     * Variable a asignar en listatipocomprobante
     */
    public void setListatipocomprobante(RegistroDataModelImpl listatipocomprobante) {
        this.listatipocomprobante = listatipocomprobante;
    }

    /**
     * Retorna la lista listaInmuebleInicial
     * 
     * @return listaInmuebleInicial
     */
    public RegistroDataModelImpl getListaInmuebleInicial() {
        return listaInmuebleInicial;
    }

    /**
     * Asigna la lista listaInmuebleInicial
     * 
     * @param listaInmuebleInicial
     * Variable a asignar en listaInmuebleInicial
     */
    public void setListaInmuebleInicial(RegistroDataModelImpl listaInmuebleInicial) {
        this.listaInmuebleInicial = listaInmuebleInicial;
    }

    /**
     * Retorna la lista listaInmuebleFinal
     * 
     * @return listaInmuebleFinal
     */
    public RegistroDataModelImpl getListaInmuebleFinal() {
        return listaInmuebleFinal;
    }

    /**
     * Asigna la lista listaInmuebleFinal
     * 
     * @param listaInmuebleFinal
     * Variable a asignar en listaInmuebleFinal
     */
    public void setListaInmuebleFinal(RegistroDataModelImpl listaInmuebleFinal) {
        this.listaInmuebleFinal = listaInmuebleFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

	public Date getFechaLimiteDescuento() {
		return fechaLimiteDescuento;
	}

	public void setFechaLimiteDescuento(Date fechaLimiteDescuento) {
		this.fechaLimiteDescuento = fechaLimiteDescuento;
	}

	public boolean getVisibleCotizaContrato() {
		return visibleCotizaContrato;
	}

	public void setVisibleCotizaContrato(boolean visibleCotizaContrato) {
		this.visibleCotizaContrato = visibleCotizaContrato;
	}

	public boolean isCheckDescuentos() {
		return checkDescuentos;
	}

	public void setCheckDescuentos(boolean checkDescuentos) {
		this.checkDescuentos = checkDescuentos;
	}

	public RegistroDataModelImpl getListainmuebleFinalFacturacion() {
		return listainmuebleFinalFacturacion;
	}

	public void setListainmuebleFinalFacturacion(RegistroDataModelImpl listainmuebleFinalFacturacion) {
		this.listainmuebleFinalFacturacion = listainmuebleFinalFacturacion;
	}

	public RegistroDataModelImpl getListainmuebleIncialFacturacion() {
		return listainmuebleIncialFacturacion;
	}

	public void setListainmuebleIncialFacturacion(RegistroDataModelImpl listainmuebleIncialFacturacion) {
		this.listainmuebleIncialFacturacion = listainmuebleIncialFacturacion;
	}

	public String getInmuebleFinalFactArrenda() {
		return inmuebleFinalFactArrenda;
	}

	public void setInmuebleFinalFactArrenda(String inmuebleFinalFactArrenda) {
		this.inmuebleFinalFactArrenda = inmuebleFinalFactArrenda;
	}

	public String getInmuebleInicialFactArrenda() {
		return inmuebleInicialFactArrenda;
	}

	public void setInmuebleInicialFactArrenda(String inmuebleInicialFactArrenda) {
		this.inmuebleInicialFactArrenda = inmuebleInicialFactArrenda;
	}

	public String getUbicacionInicial() {
		return ubicacionInicial;
	}

	public void setUbicacionInicial(String ubicacionInicial) {
		this.ubicacionInicial = ubicacionInicial;
	}

	public String getUbicacionFinal() {
		return ubicacionFinal;
	}

	public void setUbicacionFinal(String ubicacionFinal) {
		this.ubicacionFinal = ubicacionFinal;
	}

	public String getTerceroInicial() {
		return terceroInicial;
	}

	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}

	public String getTerceroFinal() {
		return terceroFinal;
	}

	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

	public List<Registro> getListaubicacionInicial() {
		return listaubicacionInicial;
	}

	public void setListaubicacionInicial(List<Registro> listaubicacionInicial) {
		this.listaubicacionInicial = listaubicacionInicial;
	}

	public List<Registro> getListaubicacionFinal() {
		return listaubicacionFinal;
	}

	public void setListaubicacionFinal(List<Registro> listaubicacionFinal) {
		this.listaubicacionFinal = listaubicacionFinal;
	}

	public boolean isFacturaSinContrato() {
		return facturaSinContrato;
	}

	public void setFacturaSinContrato(boolean facturaSinContrato) {
		this.facturaSinContrato = facturaSinContrato;
	}

	public boolean isVisibleInmueble() {
		return visibleInmueble;
	}

	public void setVisibleInmueble(boolean visibleInmueble) {
		this.visibleInmueble = visibleInmueble;
	}

	public boolean isCheckFacSinContrato() {
		return checkFacSinContrato;
	}

	public void setCheckFacSinContrato(boolean checkFacSinContrato) {
		this.checkFacSinContrato = checkFacSinContrato;
	}

	public RegistroDataModelImpl getListaTerceroInicial() {
		return listaTerceroInicial;
	}

	public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
		this.listaTerceroInicial = listaTerceroInicial;
	}

	public RegistroDataModelImpl getListaTerceroFinal() {
		return listaTerceroFinal;
	}

	public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
		this.listaTerceroFinal = listaTerceroFinal;
	}

}
