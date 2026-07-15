/*-
 * FrmlistfacentrefechasControlador.java
 *
 * 1.0
 * 
 * 20/12/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.facturaciongeneral.enums.FrmListFacEntreFechasControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmListadoFacturacionControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 20/12/2023
 * @author SYSMAN
 */
@ManagedBean
@ViewScoped
public class FrmlistfacentrefechasControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>

	private String nitInicial;
	private String nitFinal;
	private String conceptoInicial;
	private String conceptoFinal;
	private String centroUtilidadIni;
	private String centroUtilidadFin;
	private String terceroInicial;
	private String terceroFinal;
	private String concepDesIni;
	private String concepDesFin;
	private String centroUtiDesIni;
	private String centroUtiDesFin;
	
	/**
     * variable de session que almacena el tipo de cobro
     */
    private String tipoCobro;
    /**
     * variable de session que almacena el nombre del tipo de cobro
     */
    private String nombreTipoCobro;
    /**
     * variable que almacena el ano de cobro
     */
    private String anoCobro;
    /**
     * variable que almacena la fecha inicial
     */

    private Date fechaInicial;
    /**
     * variable que almacena la fecha final
     */

    private Date fechaFinal;
    
    /**
     * impresion especial excel
     */   
    private boolean reporteExcel;
	
    /**
     * variable que almacena el nombre del vendedor
     */
    private String vendedorInicial;
    
    /**
     * variable que almacena el nombre del vendedor
     */
    private String vendedorFinal;
    
    /**
     * variable que almacena el nombre del vendedor
     */
    private String nombreVendedorIni;
    
    /**
     * variable que almacena el nombre del vendedor
     */
    private String nombreVendedorFin;
    
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listanitInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listanitFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaconceptoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaconceptoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacentroUtilidadIni;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacentroUtilidadFin;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listavendedorInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listavendedorFinal;

//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmlistfacentrefechasControlador
	 */
	public FrmlistfacentrefechasControlador() {
		super();
		compania = SessionUtil.getCompania();
		tipoCobro = SysmanFunciones
                .nvl(SessionUtil.getSessionVar(
                                ConstantesFacturacionGenEnum.TIPOCOBRO
                                                .getValue()),
                                "")
                .toString();
		nombreTipoCobro = SysmanFunciones
                .nvl(SessionUtil.getSessionVar(
                                ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
                                                .getValue()),
                                "")
                .toString();
		anoCobro = SysmanFunciones
		                .nvl(SessionUtil.getSessionVar(
		                                ConstantesFacturacionGenEnum.ANIO
		                                                .getValue()),
		                                "")
		                .toString();
		try {
			numFormulario = 2438;
			validarPermisos();

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

		cargarListanitInicial();
		cargarListanitFinal();
		cargarListaconceptoInicial();
		cargarListaconceptoFinal();
		cargarListacentroUtilidadIni();
		cargarListacentroUtilidadFin();
		cargarListavendedorInicial();
		cargarListavendedorFinal();

		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() 
	{
		fechaInicial = fechaFinal = new Date();
		
		nitInicial= SysmanConstantes.DEFECTOINICIAL_STRING;
		nitFinal= SysmanConstantes.DEFECTOFINAL_STRING;
		conceptoInicial= SysmanConstantes.DEFECTOINICIAL_STRING;
		conceptoFinal= SysmanConstantes.DEFECTOFINAL_STRING;
		centroUtilidadIni= SysmanConstantes.DEFECTOINICIAL_STRING;
		centroUtilidadFin= SysmanConstantes.DEFECTOFINAL_STRING;
		terceroInicial= SysmanConstantes.DEFECTOINICIAL_STRING;
		terceroFinal= SysmanConstantes.DEFECTOFINAL_STRING;
		concepDesIni= SysmanConstantes.DEFECTOINICIAL_STRING;
		concepDesFin= SysmanConstantes.DEFECTOFINAL_STRING;
		centroUtiDesIni= SysmanConstantes.DEFECTOINICIAL_STRING;
		centroUtiDesFin= SysmanConstantes.DEFECTOFINAL_STRING;
		vendedorInicial= SysmanConstantes.DEFECTOINICIAL_STRING;
		vendedorFinal= SysmanConstantes.DEFECTOFINAL_STRING;
		
		
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listanitInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListanitInicial() {
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrmListFacEntreFechasControladorUrlEnum.URL14006
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		
		listanitInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                FrmListadoFacturacionControladorEnum.NIT.getValue());
	}

	/**
	 * 
	 * Carga la lista listanitFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListanitFinal() {	
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrmListFacEntreFechasControladorUrlEnum.URL14010
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(FrmListadoFacturacionControladorEnum.TERCEROINICIAL
		                .getValue(),
		                nitInicial);

		listanitFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), param, true,
                FrmListadoFacturacionControladorEnum.NIT.getValue());
	}

	/**
	 * 
	 * Carga la lista listaconceptoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaconceptoInicial() {
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrmListFacEntreFechasControladorUrlEnum.URL663001
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(FrmListadoFacturacionControladorEnum.TIPOCOBRO
		                .getValue(),
		                tipoCobro);
		param.put(GeneralParameterEnum.ANO.getName(),
		                anoCobro);
		
		listaconceptoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.CODIGO.getName());
	}

	/**
     * 
     * Carga la lista listaconceptoFinal
     *
     * TODO DOCUMENTACION ADICIONAL
     */
	public void cargarListaconceptoFinal(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
            .getUrlServiceByUrlByEnumID(
            		FrmListFacEntreFechasControladorUrlEnum.URL663003
                                            .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);
		param.put(FrmListadoFacturacionControladorEnum.TIPOCOBRO
		            .getValue(),
		            tipoCobro);
		param.put(GeneralParameterEnum.ANO.getName(),
		            anoCobro);
		param.put(FrmListadoFacturacionControladorEnum.CONCEPTOINICIAL
		            .getValue(),
		            conceptoInicial);
		
		listaconceptoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
		            urlBean.getUrlConteo().getUrl(), param, true,
		            GeneralParameterEnum.CODIGO.getName());
	}

	/**
     * 
     * Carga la lista listacentroUtilidadIni
     *
     * TODO DOCUMENTACION ADICIONAL
     */
	public void cargarListacentroUtilidadIni(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrmListFacEntreFechasControladorUrlEnum.URL1915003
                                                .getValue());

		Map<String, Object> param = new TreeMap<>();
		
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anoCobro);
		
		listacentroUtilidadIni = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.CODIGO.getName());
	}

	/**
     * 
     * Carga la lista listacentroUtilidadFin
     *
     * TODO DOCUMENTACION ADICIONAL
     */
	public void cargarListacentroUtilidadFin(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrmListFacEntreFechasControladorUrlEnum.URL1915003
                                                .getValue());

		Map<String, Object> param = new TreeMap<>();
		
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anoCobro);
		param.put(GeneralParameterEnum.CENTRO_UTILIDAD_INI.getName(),
	            centroUtilidadIni);		
		
		listacentroUtilidadFin = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.CODIGO.getName());
	}
	
	/**
     * 
     * Carga la lista listavendedorInicial
     *
     * TODO DOCUMENTACION ADICIONAL
     */
	public void cargarListavendedorInicial(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrmListFacEntreFechasControladorUrlEnum.URL14122
                                                .getValue());

		Map<String, Object> param = new TreeMap<>();
		
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);
		
		listavendedorInicial = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.NIT.getName());
	}
	
	/**
     * 
     * Carga la lista listavendedorFinal
     *
     * TODO DOCUMENTACION ADICIONAL
     */
	public void cargarListavendedorFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrmListFacEntreFechasControladorUrlEnum.URL14202
                                                .getValue());

		Map<String, Object> param = new TreeMap<>();
		
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                compania);
		
		param.put("VENDEDORINICIAL",vendedorInicial);
		
		listavendedorFinal = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.NIT.getName());
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirPdf() {
		archivoDescarga = null;
		reporteExcel = false;
        generaInforme(FORMATOS.PDF);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirExcel() {
		 archivoDescarga = null;
		 reporteExcel = true;
	     generaInforme(FORMATOS.EXCEL);
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
     * metodo que contiene la logica para imprimir un reporte en
     * formato pdf y excel
     * 
     * @param formato
     */
    private void generaInforme(FORMATOS formato) {
    	String reporte = "002526ListadoFacXFechasCentroUtilidad";
        try {
            
        	Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("nitInicial", nitInicial);
            reemplazar.put("nitFinal", nitFinal);
            reemplazar.put("conceptoInicial", conceptoInicial);
            reemplazar.put("conceptoFinal", conceptoFinal);
            reemplazar.put("centroUtilidadIni", centroUtilidadIni);
            reemplazar.put("centroUtilidadFin", centroUtilidadFin);
            reemplazar.put("vendedorInicial", vendedorInicial);
            reemplazar.put("vendedorFinal", vendedorFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("tipoCobro", tipoCobro);

            Reporteador.resuelveConsulta(
            				reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_TIPOCOBRO", nombreTipoCobro);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal));
            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", reporteExcel);
            archivoDescarga = JsfUtil.exportarStreamed(
            				reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ",
                            ex.getMessage(), " ",
                            reporte));
            Logger.getLogger(FrmlistadoRecaudoDifControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (ParseException | JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listanitInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilanitInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        nitInicial = SysmanFunciones.nvl(registroAux.getCampos().get(
                        FrmListadoFacturacionControladorEnum.NIT.getValue()),
                        "")
                        .toString();
        terceroInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "")
                        .toString();
        terceroFinal = null;
        nitFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        conceptoInicial = null;
        conceptoFinal = null;
        concepDesIni = null;
        concepDesFin = null;
        cargarListanitFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listanitFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilanitFinal(SelectEvent event) {
		 Registro registroAux = (Registro) event.getObject();
	        nitFinal = SysmanFunciones.nvl(registroAux.getCampos().get(
	                        FrmListadoFacturacionControladorEnum.NIT.getValue()),
	                        "")
	                        .toString();
	        terceroFinal = SysmanFunciones.nvl(registroAux.getCampos().get(
	                        GeneralParameterEnum.NOMBRE.getName()),
	                        "")
	                        .toString();
	        conceptoInicial = null;
	        conceptoFinal = null;
	        concepDesIni = null;
	        concepDesFin = null;
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaconceptoInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaconceptoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        conceptoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        concepDesIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        conceptoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        concepDesFin = null;
        cargarListaconceptoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaconceptoFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaconceptoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        conceptoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        concepDesFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacentroUtilidadIni
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacentroUtilidadIni(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        centroUtilidadIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        centroUtiDesIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.DESCRIPCION.getName()),
                                        "")
                        .toString();
        centroUtilidadFin = SysmanConstantes.DEFECTOFINAL_STRING;
        centroUtiDesFin = null;
        cargarListacentroUtilidadFin();
		
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listacentroUtilidadFin
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacentroUtilidadFin(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroUtilidadFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
		centroUtiDesFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.DESCRIPCION.getName()),
                                        "")
                        .toString();
		
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listavendedorInicial
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilavendedorInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        vendedorInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NIT.getName()),
                                        "")
                        .toString();
        nombreVendedorIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        vendedorFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        nombreVendedorFin = null;
        cargarListavendedorFinal();
		
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listavendedorFinal
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilavendedorFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		vendedorFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NIT.getName()),
                                        "")
                        .toString();
		nombreVendedorFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
		
	}

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable nitInicial
	 * 
	 * @return nitInicial
	 */
	public String getNitInicial() {
		return nitInicial;
	}

	/**
	 * Asigna la variable nitInicial
	 * 
	 * @param nitInicial Variable a asignar en nitInicial
	 */
	public void setNitInicial(String nitInicial) {
		this.nitInicial = nitInicial;
	}

	/**
	 * Retorna la variable nitFinal
	 * 
	 * @return nitFinal
	 */
	public String getNitFinal() {
		return nitFinal;
	}

	/**
	 * Asigna la variable nitFinal
	 * 
	 * @param nitFinal Variable a asignar en nitFinal
	 */
	public void setNitFinal(String nitFinal) {
		this.nitFinal = nitFinal;
	}

	/**
	 * Retorna la variable conceptoInicial
	 * 
	 * @return conceptoInicial
	 */
	public String getConceptoInicial() {
		return conceptoInicial;
	}

	/**
	 * Asigna la variable conceptoInicial
	 * 
	 * @param conceptoInicial Variable a asignar en conceptoInicial
	 */
	public void setConceptoInicial(String conceptoInicial) {
		this.conceptoInicial = conceptoInicial;
	}

	/**
	 * Retorna la variable conceptoFinal
	 * 
	 * @return conceptoFinal
	 */
	public String getConceptoFinal() {
		return conceptoFinal;
	}

	/**
	 * Asigna la variable conceptoFinal
	 * 
	 * @param conceptoFinal Variable a asignar en conceptoFinal
	 */
	public void setConceptoFinal(String conceptoFinal) {
		this.conceptoFinal = conceptoFinal;
	}

	/**
	 * Retorna la variable centroUtilidadIni
	 * 
	 * @return centroUtilidadIni
	 */
	public String getCentroUtilidadIni() {
		return centroUtilidadIni;
	}

	/**
	 * Asigna la variable centroUtilidadIni
	 * 
	 * @param centroUtilidadIni Variable a asignar en centroUtilidadIni
	 */
	public void setCentroUtilidadIni(String centroUtilidadIni) {
		this.centroUtilidadIni = centroUtilidadIni;
	}

	/**
	 * Retorna la variable centroUtilidadFin
	 * 
	 * @return centroUtilidadFin
	 */
	public String getCentroUtilidadFin() {
		return centroUtilidadFin;
	}

	/**
	 * Asigna la variable centroUtilidadFin
	 * 
	 * @param centroUtilidadFin Variable a asignar en centroUtilidadFin
	 */
	public void setCentroUtilidadFin(String centroUtilidadFin) {
		this.centroUtilidadFin = centroUtilidadFin;
	}

	/**
	 * Retorna la variable terceroInicial
	 * 
	 * @return terceroInicial
	 */
	public String getTerceroInicial() {
		return terceroInicial;
	}

	/**
	 * Asigna la variable terceroInicial
	 * 
	 * @param terceroInicial Variable a asignar en terceroInicial
	 */
	public void setTerceroInicial(String terceroInicial) {
		this.terceroInicial = terceroInicial;
	}

	/**
	 * Retorna la variable terceroFinal
	 * 
	 * @return terceroFinal
	 */
	public String getTerceroFinal() {
		return terceroFinal;
	}

	/**
	 * Asigna la variable terceroFinal
	 * 
	 * @param terceroFinal Variable a asignar en terceroFinal
	 */
	public void setTerceroFinal(String terceroFinal) {
		this.terceroFinal = terceroFinal;
	}

	/**
	 * Retorna la variable concepDesIni
	 * 
	 * @return concepDesIni
	 */
	public String getConcepDesIni() {
		return concepDesIni;
	}

	/**
	 * Asigna la variable concepDesIni
	 * 
	 * @param concepDesIni Variable a asignar en concepDesIni
	 */
	public void setConcepDesIni(String concepDesIni) {
		this.concepDesIni = concepDesIni;
	}

	/**
	 * Retorna la variable concepDesFin
	 * 
	 * @return concepDesFin
	 */
	public String getConcepDesFin() {
		return concepDesFin;
	}

	/**
	 * Asigna la variable concepDesFin
	 * 
	 * @param concepDesFin Variable a asignar en concepDesFin
	 */
	public void setConcepDesFin(String concepDesFin) {
		this.concepDesFin = concepDesFin;
	}

	/**
	 * Retorna la variable centroUtiDesIni
	 * 
	 * @return centroUtiDesIni
	 */
	public String getCentroUtiDesIni() {
		return centroUtiDesIni;
	}

	/**
	 * Asigna la variable centroUtiDesIni
	 * 
	 * @param centroUtiDesIni Variable a asignar en centroUtiDesIni
	 */
	public void setCentroUtiDesIni(String centroUtiDesIni) {
		this.centroUtiDesIni = centroUtiDesIni;
	}

	/**
	 * Retorna la variable centroUtiDesFin
	 * 
	 * @return centroUtiDesFin
	 */
	public String getCentroUtiDesFin() {
		return centroUtiDesFin;
	}

	/**
	 * Asigna la variable centroUtiDesFin
	 * 
	 * @param centroUtiDesFin Variable a asignar en centroUtiDesFin
	 */
	public void setCentroUtiDesFin(String centroUtiDesFin) {
		this.centroUtiDesFin = centroUtiDesFin;
	}

	public String getTipoCobro() {
		return tipoCobro;
	}

	public void setTipoCobro(String tipoCobro) {
		this.tipoCobro = tipoCobro;
	}

	/**
	 * @return the nombreTipoCobro
	 */
	public String getNombreTipoCobro() {
		return nombreTipoCobro;
	}

	/**
	 * @param nombreTipoCobro the nombreTipoCobro to set
	 */
	public void setNombreTipoCobro(String nombreTipoCobro) {
		this.nombreTipoCobro = nombreTipoCobro;
	}

	public String getAnoCobro() {
		return anoCobro;
	}

	public void setAnoCobro(String anoCobro) {
		this.anoCobro = anoCobro;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
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
	 * Retorna la lista listanitInicial
	 * 
	 * @return listanitInicial
	 */
	public RegistroDataModelImpl getListanitInicial() {
		return listanitInicial;
	}

	/**
	 * Asigna la lista listanitInicial
	 * 
	 * @param listanitInicial Variable a asignar en listanitInicial
	 */
	public void setListanitInicial(RegistroDataModelImpl listanitInicial) {
		this.listanitInicial = listanitInicial;
	}

	/**
	 * Retorna la lista listanitFinal
	 * 
	 * @return listanitFinal
	 */
	public RegistroDataModelImpl getListanitFinal() {
		return listanitFinal;
	}

	/**
	 * Asigna la lista listanitFinal
	 * 
	 * @param listanitFinal Variable a asignar en listanitFinal
	 */
	public void setListanitFinal(RegistroDataModelImpl listanitFinal) {
		this.listanitFinal = listanitFinal;
	}

	/**
	 * Retorna la lista listaconceptoInicial
	 * 
	 * @return listaconceptoInicial
	 */
	public RegistroDataModelImpl getListaconceptoInicial() {
		return listaconceptoInicial;
	}

	/**
	 * Asigna la lista listaconceptoInicial
	 * 
	 * @param listaconceptoInicial Variable a asignar en listaconceptoInicial
	 */
	public void setListaconceptoInicial(RegistroDataModelImpl listaconceptoInicial) {
		this.listaconceptoInicial = listaconceptoInicial;
	}

	/**
	 * Retorna la lista listaconceptoFinal
	 * 
	 * @return listaconceptoFinal
	 */
	public RegistroDataModelImpl getListaconceptoFinal() {
		return listaconceptoFinal;
	}

	/**
	 * Asigna la lista listaconceptoFinal
	 * 
	 * @param listaconceptoFinal Variable a asignar en listaconceptoFinal
	 */
	public void setListaconceptoFinal(RegistroDataModelImpl listaconceptoFinal) {
		this.listaconceptoFinal = listaconceptoFinal;
	}

	/**
	 * Retorna la lista listacentroUtilidadIni
	 * 
	 * @return listacentroUtilidadIni
	 */
	public RegistroDataModelImpl getListacentroUtilidadIni() {
		return listacentroUtilidadIni;
	}

	/**
	 * Asigna la lista listacentroUtilidadIni
	 * 
	 * @param listacentroUtilidadIni Variable a asignar en listacentroUtilidadIni
	 */
	public void setListacentroUtilidadIni(RegistroDataModelImpl listacentroUtilidadIni) {
		this.listacentroUtilidadIni = listacentroUtilidadIni;
	}

	/**
	 * Retorna la lista listacentroUtilidadFin
	 * 
	 * @return listacentroUtilidadFin
	 */
	public RegistroDataModelImpl getListacentroUtilidadFin() {
		return listacentroUtilidadFin;
	}

	/**
	 * Asigna la lista listacentroUtilidadFin
	 * 
	 * @param listacentroUtilidadFin Variable a asignar en listacentroUtilidadFin
	 */
	public void setListacentroUtilidadFin(RegistroDataModelImpl listacentroUtilidadFin) {
		this.listacentroUtilidadFin = listacentroUtilidadFin;
	}
//</SET_GET_LISTAS_COMBO_GRANDE>

	/**
	 * @return the vendedorInicial
	 */
	public String getVendedorInicial() {
		return vendedorInicial;
	}

	/**
	 * @param vendedorInicial the vendedorInicial to set
	 */
	public void setVendedorInicial(String vendedorInicial) {
		this.vendedorInicial = vendedorInicial;
	}

	/**
	 * @return the vendedorFinal
	 */
	public String getVendedorFinal() {
		return vendedorFinal;
	}

	/**
	 * @param vendedorFinal the vendedorFinal to set
	 */
	public void setVendedorFinal(String vendedorFinal) {
		this.vendedorFinal = vendedorFinal;
	}

	/**
	 * @return the nombreVendedorIni
	 */
	public String getNombreVendedorIni() {
		return nombreVendedorIni;
	}

	/**
	 * @param nombreVendedorIni the nombreVendedorIni to set
	 */
	public void setNombreVendedorIni(String nombreVendedorIni) {
		this.nombreVendedorIni = nombreVendedorIni;
	}

	/**
	 * @return the nombreVendedorFin
	 */
	public String getNombreVendedorFin() {
		return nombreVendedorFin;
	}

	/**
	 * @param nombreVendedorFin the nombreVendedorFin to set
	 */
	public void setNombreVendedorFin(String nombreVendedorFin) {
		this.nombreVendedorFin = nombreVendedorFin;
	}

	/**
	 * @return the listavendedorInicial
	 */
	public RegistroDataModelImpl getListavendedorInicial() {
		return listavendedorInicial;
	}

	/**
	 * @param listavendedorInicial the listavendedorInicial to set
	 */
	public void setListavendedorInicial(RegistroDataModelImpl listavendedorInicial) {
		this.listavendedorInicial = listavendedorInicial;
	}

	/**
	 * @return the listavendedorFinal
	 */
	public RegistroDataModelImpl getListavendedorFinal() {
		return listavendedorFinal;
	}

	/**
	 * @param listavendedorFinal the listavendedorFinal to set
	 */
	public void setListavendedorFinal(RegistroDataModelImpl listavendedorFinal) {
		this.listavendedorFinal = listavendedorFinal;
	}

}
