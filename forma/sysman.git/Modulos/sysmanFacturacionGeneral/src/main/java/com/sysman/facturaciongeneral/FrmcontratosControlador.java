/*-
 * FrmcontratosControlador.java
 *
 * 1.0
 * 
 * 08/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralDosRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralTresRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralUnoRemote;
import com.sysman.facturaciongeneral.enums.ConceptosNoFacturadosControladorEnum;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmcontratosControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmcontratosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmcotizacionsControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase encargada de gestionar la opción Contratos del módulo
 * Facturación General.
 *
 * @version 1.0, 08/08/2018
 * @author dnino
 */
@ManagedBean
@ViewScoped
public class FrmcontratosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Constante que almacena el nombre de usuario quien inició la
     * sesión.
     */
    private final String usuario;

    /**
     * Constante que almacena el año actual.
     */
    private final int anoActual;

    /**
     * Código del tipo de cobro seleccionado previamente en el modal
     * Seleccionar tipo de cobro.
     */
    private final String tipoCobro;
    /**
     * Sucursal seleccionada desde combo tercero
     */
    private String sucursal;
    /**
     * Anio de trabajo que ha sido seleccionado al ingresar al modulo
     * de Facturacion General
     */
    private String anio;
    /**
     * Nombre del Tercero seleccionado
     */
    private String desTercero;
    
    /**
     * Nombre del Vendedor seleccionado
     */
    private String desVendedor;
    /**
     * Nombre del Centro de costo seleccionado
     */
    private String desCentroCosto;
    /**
     * Almacena el nombre del auxilar seleccionado para ponerlo en el
     * campo descripción de auxiliar.
     */
    private String desAuxiliar;

    /** Variable que guarda el número del contrato. */
    private String numero;
    /** Variable que guarda el tipo de registro. */
    private String tipoDelReg;

    /**
     * Nombre de estado de la adjudicacion
     */
    private String nombreEstado;
    /**
     * Variable que almacena el valor de la tarifa seleccionada en el
     * detalle de cada concepto
     */
    private double vlrTarifa;

    /**
     * Variable que almacena el valor del estrato seleccionado en el
     * detalle de cada concepto
     */
    private double vlrEstrato;
    
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    
    /** Variable que guarda el valor unitario. */
    private String valorUnitario;
    /** Variable que guarda el valor base. */
    private String valorBase;
    /** Variable que guarda el valor iva. */
    private String valorIVA;
    /** Variable que guarda el valor de la retencion. */
    private String valorRete;
    /** Variable que guarda el valor de descuento. */
    private String valorDescuento;
    /** Variable que guarda el valor ICA. */
    private String valorICA;
    /** Variable que guarda el valor neto. */
    private String valorNeto;
    /** Variable que guarda el valor Impocosnumo. */
    private String valorImpoconsumo;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista Inumuebles de la pestaña conceptos */
    private RegistroDataModelImpl listaCInmueble;
    /** Lista Nombre concepto de la pestaña conceptos */
    private RegistroDataModelImpl listaNOMBRECONCEPTOC;
    /** Lista para Editar de concepto de la pestaña conceptos */
    private RegistroDataModelImpl listaNOMBRECONCEPTOCE;
    /** Lista Tarifas de la pestaña conceptos */
    private RegistroDataModelImpl listaTARIFAC;
    /** Lista Editar Tarifas de la pestaña conceptos */
    private RegistroDataModelImpl listaTARIFACE;
    /** Lista Terceros de la pestaña conceptos */
    private RegistroDataModelImpl listaTERCEROC;
    /** Lista Editar Terceros de la pestaña conceptos */
    private RegistroDataModelImpl listaTERCEROCE;
    /** Lista Centros de costo de la pestaña conceptos */
    private RegistroDataModelImpl listacentroCostoC;
    /** Lista Editar Centros de costo de la pestaña conceptos */
    private RegistroDataModelImpl listacentroCostoCE;
    /** Lista Auxiliares de la pestaña conceptos */
    private RegistroDataModelImpl listaAUXILIARC;
    /** Lista Editar Auxiliares de la pestaña conceptos */
    private RegistroDataModelImpl listaAUXILIARCE;
    /** Lista Conceptos de la pestaña conceptos */
    private RegistroDataModelImpl listaCONCEPTOC;
    /** Lista Editar Conceptos de la pestaña conceptos */
    private RegistroDataModelImpl listaCONCEPTOCE;
    /** Lista Estratos de la pestaña conceptos */
    private RegistroDataModelImpl listaESTRATOC;
    /** Lista Editar Estratos de la pestaña conceptos */
    private RegistroDataModelImpl listaESTRATOCE;
    /** Lista Centros de utilidad de la pestaña conceptos */
    private RegistroDataModelImpl listacentroUtilidad;
    /** Lista Editar Centros de costo de la pestaña conceptos */
    private RegistroDataModelImpl listacentroUtilidadE;

    /**
     * Lista que carga las referencias
     */
    private RegistroDataModelImpl listaReferencia;
    /**
     * Lista que carga las referencias en la grilla
     */
    private RegistroDataModelImpl listaReferenciaE;
    /**
     * Lista que carga las fuentes de recuros
     */
    private RegistroDataModelImpl listaFuenteRecurso;
    /**
     * Lista que carga las fuentes de recuros en la grilla
     */
    private RegistroDataModelImpl listaFuenteRecursoE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /** Lista Centros de costo de la pestaña General */
    private RegistroDataModelImpl listaTERCEROG;
    private RegistroDataModelImpl listaVENDEDOR;
    /** Lista Centros de costo de la pestaña General */
    private RegistroDataModelImpl listacentroCostoG;
    /** Lista Centros de costo de la pestaña General */
    private RegistroDataModel listaAFECTARCOTIZACIONG;
    /** Lista Centros de costo de la pestaña General */
    private RegistroDataModelImpl listaAUXILIARG;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /** Lista Sub Formulario Conceptos */
    private List<Registro> listaSubconceptoscontrato;
    /**
     * Lista que carga la informacion de locales
     */
    private RegistroDataModelImpl listaCodigoLocal;
    /**
     * Lista que carga la informacion de tipos de pago
     */
    private RegistroDataModelImpl listaTipoPago;

    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /** Atributo de referencia para el subformulario */
    private Registro registroSub;

    /**
     * Atributo que indica si se esta cambiando la base fija en la
     * grilla
     */
    private boolean cambioBaseFija;

    /**
     * Variable que determina si se ha cambiado el valor unitario
     */
    private boolean cambioValorUnitario;

    /**
     * Atributo que indica si se esta cambiando el estrato
     */
    private boolean cambioEstrato;

    /**
     * Atributo que indica si se esta cambiando la tarifa
     */
    private boolean cambioTarifa;
    /**
     * Atributo que indica si se esta cambiando el concepto
     */
    private boolean cambioConcepto;

    /**
     * Variable encargada de almacenar temporalmente el indice al
     * activar edicion
     */
    private int indiceSubconceptoscontrato;
    
    private boolean visibleCentroUtilidad; 
    
    private boolean manejaTarifaBase;

	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFactGenCero;
    @EJB
    private EjbFacturacionGeneralUnoRemote ejbFactGenUno;
    @EJB
    private EjbFacturacionGeneralDosRemote ejbFactGenDos;
    @EJB
    private EjbFacturacionGeneralTresRemote ejbFactGenTres;
    
    private RegistroDataModelImpl listanumeroCotizacion;
    
    private Boolean visibleCotizaContrato;
    
    private RegistroDataModelImpl listainmueble;
    
    private RegistroDataModelImpl listainmuebleE;
    
    private RegistroDataModelImpl listadestinacion;
    
    private String numeroCotizacion;
    
    private String tipoCotizacion;
    
    private List<Registro> listaDetalleCotizacion;
    
    private String vendedorLabel;
    
    private String nomRepresentanteLegal;
    
    private RegistroDataModelImpl listarepresentanteLegal;
    
    private RegistroDataModelImpl listaORDENADORGASTO;
    
    private RegistroDataModelImpl listaPlantillas;
    
    private String nombreordenadorgasto;
    
    private String paramNombreReporte = "";
    
    private String plantilla;
    private String nombrePlantilla;
	private Date fechaPlantilla;
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmcontratosControlador
     */
    public FrmcontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        tipoCobro = (String) SessionUtil.getSessionVar("tipoCobro");
        usuario = SessionUtil.getUser().getCodigo();
        anoActual = SysmanFunciones.ano(new Date());
        anio = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());
        vendedorLabel = "Vendedor";
       visibleCotizaContrato = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_CONTRATO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTARIFAC();
        cargarListaTARIFACE();
        cargarListaTERCEROC();
        cargarListaTERCEROCE();
        cargarListacentroCostoC();
        cargarListacentroCostoCE();
        cargarListaAUXILIARC();
        cargarListaAUXILIARCE();
        cargarListaCONCEPTOC();
        cargarListaCONCEPTOCE();
        cargarListaESTRATOC();
        cargarListaESTRATOCE();
        cargarListaTERCEROG();
        cargarListacentroCostoG();
        cargarListaAFECTARCOTIZACIONG();
        cargarlistaAUXILIARG();
        cargarListaCInmueble();
        cargarListaNOMBRECONCEPTOC();
        cargarListaCodigoLocal();
        cargarListaReferencia();
        cargarListaReferenciaE();
        cargarListaFuenteRecurso();
        cargarListaFuenteRecursoE();
        cargarListaVENDEDOR();
        cargarListacentroUtilidad();
        cargarListacentroUtilidadE();
        cargarListaTipoPago();
        cargarListadestinacion();
        cargarListanumeroCotizacion();
        cargarListaInmueble();
        cargarListarepresentanteLegal();
        cargarListaORDENADORGASTO();
        cargarListaPlantillas();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubconceptoscontrato();

        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubconceptoscontrato = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.SF_CONTRATOS;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
        // SE CONSULTA PARAMETRO PARA CAMPOS PERSONALIZADOS}
        String validaCamposPersonalizados = "";
        try {
			validaCamposPersonalizados = SysmanFunciones
					.nvlStr(ejbSysmanUtil.consultarParametro(compania,
			                "ACTIVAR CAMPOS PERSONALIZADOS CONTRATOS",
			                SessionUtil.getModulo(), new Date(),
			                true), "NO");
			paramNombreReporte = SysmanFunciones
        			.nvlStr(ejbSysmanUtil.consultarParametro(compania,
		                "NOMBRE REPORTE PERSONALIZADO CONTRATOS",
			                SessionUtil.getModulo(), new Date(),
			                	false), "NO");
		} catch (SystemException e) {
			validaCamposPersonalizados = "NO";
		}
        if(validaCamposPersonalizados.equals("SI")) {        	
        	visibleCotizaContrato = true;
        	vendedorLabel = "Supervisor";
        }else {
        	visibleCotizaContrato = false;
        }
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA
                        .getName(), compania);
        parametrosListado.put(GeneralParameterEnum.TIPO
                        .getName(), tipoCobro);
        parametrosListado.put(GeneralParameterEnum.ANO
                        .getName(), anio);
    }

    /**
     * Carga la lista listaSub_conceptoscontrato
     */
    public void cargarListaSubconceptoscontrato() {
        numero = registro.getCampos().get(GeneralParameterEnum.NUMERO.getName())
                        .toString();
        tipoDelReg = registro.getCampos().get("TIPO").toString();

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.TIPO.getName()));
        param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()));
        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));

        try {

            listaSubconceptoscontrato = RegistroConverter
                            .toListRegistro(requestManager
                                            .getList(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            GenericUrlEnum.SF_DETALLE_CONTRATO
                                                                                            .getGridKey())
                                                            .getUrl(), param),
                                            CacheUtil.getLlaveServicio(
                                                            UrlServiceCache.SYSMANDSUNIST,
                                                            GenericUrlEnum.SF_DETALLE_CONTRATO
                                                                            .getTable()));

        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaInmueble() {
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                FrmcontratosControladorUrlEnum.URL1947001
                                                .getValue());

		Map<String, Object> param = new TreeMap<>();
		
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                String.valueOf(compania));
		
		listainmueble = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                "CODIGOINMUEBLE");
		listainmuebleE = listainmueble;
    }
    public void cargarListanumeroCotizacion() {
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                FrmcontratosControladorUrlEnum.URL678008
                                                .getValue());

		Map<String, Object> param = new TreeMap<>();
		
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		                String.valueOf(compania));
		
		listanumeroCotizacion = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true,
		                GeneralParameterEnum.NUMERO.getName());
    }
    /**
     * 
     * Carga la lista listaCInmueble
     *
     */
    public void cargarListaCInmueble() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL351
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listaCInmueble = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO.getName());
    }

    /**
     * 
     * Carga la lista listaNOMBRECONCEPTOC
     *
     */
    public void cargarListaNOMBRECONCEPTOC() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL340
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(GeneralParameterEnum.ANO.getName(),
                        String.valueOf(anio));
        param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                        String.valueOf(tipoCobro));

        listaNOMBRECONCEPTOC = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTARIFAC
     *
     */
    public void cargarListaTARIFAC() {
        
        if (manejaTarifaBase) {
            
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmcontratosControladorUrlEnum.URL1711
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            String.valueOf(compania));
            param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                            String.valueOf(tipoCobro));
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);

            listaTARIFAC = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());    
            
        } else {
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL365
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));
        param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                        String.valueOf(tipoCobro));
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaTARIFAC = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
        }
    }

    /**
     * 
     * Carga la lista listaTARIFAC
     *
     */
    public void cargarListaTARIFACE() {
        
        if (manejaTarifaBase) {
            
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmcontratosControladorUrlEnum.URL1711
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            String.valueOf(compania));
            param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                            String.valueOf(tipoCobro));
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);

            listaTARIFACE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());    
            
        } else {        
        
        
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmcontratosControladorUrlEnum.URL365
                                                            .getValue());
    
            Map<String, Object> param = new TreeMap<>();
    
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            String.valueOf(compania));
            param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                            String.valueOf(tipoCobro));
            param.put(GeneralParameterEnum.ANO.getName(),
                            anio);
            listaTARIFACE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());
        }
    }

    /**
     * 
     * Carga la lista listaTERCEROC
     *
     */
    public void cargarListaTERCEROC() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL458
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listaTERCEROC = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.NIT.getValue());
    }

    /**
     * 
     * Carga la lista listaTERCEROC
     *
     */
    public void cargarListaTERCEROCE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL458
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listaTERCEROCE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.NIT.getValue());
    }

    /**
     * 
     * Carga la lista listacentroCostoC
     *
     */
    public void cargarListacentroCostoC() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL545
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacentroCostoC = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacentroCostoC
     *
     */
    public void cargarListacentroCostoCE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL545
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacentroCostoCE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.CODIGO.getValue());
    }
    
    /**
     * 
     * Carga la lista listacentroUtilidad
     *
     */
    public void cargarListacentroUtilidad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL185
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacentroUtilidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacentroUtilidad
     *
     */
    public void cargarListacentroUtilidadE() {
        
        listacentroUtilidadE = listacentroUtilidad;
    }

    /**
     * 
     * Carga la lista listaAUXILIARC
     *
     */
    public void cargarListaAUXILIARC() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL632
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaAUXILIARC = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.CODIGO.getValue());
    }

    /**
     * 
     * Carga la lista listaAUXILIARC
     *
     */
    public void cargarListaAUXILIARCE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL632
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaAUXILIARCE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.CODIGO.getValue());
    }

    /**
     * 
     * Carga la lista listaCONCEPTOC
     *
     */
    public void cargarListaCONCEPTOC() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL726
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        listaCONCEPTOC = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.CODIGO.getValue());
    }

    /**
     * 
     * Carga la lista listaCONCEPTOC
     *
     */
    public void cargarListaCONCEPTOCE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL726
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        listaCONCEPTOCE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.CODIGO.getValue());
    }

    /**
     * 
     * Carga la lista listaESTRATOC
     *
     */
    public void cargarListaESTRATOC() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL320
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaESTRATOC = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaESTRATOC
     *
     */
    public void cargarListaESTRATOCE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL320
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaESTRATOCE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTERCEROG
     *
     */
    public void cargarListaTERCEROG() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL458
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listaTERCEROG = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.NIT.getValue());
    }

    /**
     * 
     * Carga la lista listaVENDEDOR
     *
     */
    public void cargarListaVENDEDOR() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listaVENDEDOR = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.NIT.getValue());
    }

    
    /**
     * 
     * Carga la lista listacentroCostoG
     *
     */
    public void cargarListacentroCostoG() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL545
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listacentroCostoG = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAFECTARCOTIZACIONG
     *
     */
    public void cargarListaAFECTARCOTIZACIONG() {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * 
     * Carga la lista listaAUXILIARG
     *
     */
    public void cargarlistaAUXILIARG() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL632
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaAUXILIARG = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.CODIGO.getValue());

    }

    /**
     * 
     * Carga la lista listaCodigoLocal
     *
     */
    public void cargarListaCodigoLocal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL635
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCodigoLocal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.CODIGO.getValue());

    }

    /**
     * 
     * Carga la lista listaReferencia
     *
     */
    public void cargarListaReferencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL171
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaReferencia
     *
     */
    public void cargarListaReferenciaE() {
        listaReferenciaE = listaReferencia;
    }

    /**
     * 
     * Carga la lista listaFuenteRecurso
     *
     */
    public void cargarListaFuenteRecurso() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL184
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteRecurso
     *
     */
    public void cargarListaFuenteRecursoE() {
        listaFuenteRecursoE = listaFuenteRecurso;
    }
    
    /**
     * 
     * Carga la lista listacentroUtilidad
     *
     */
    public void cargarListaTipoPago() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL1925001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }
    
    public void cargarListadestinacion() {
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                		FrmcontratosControladorUrlEnum.URL1897007
                                                .getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		listadestinacion = new RegistroDataModelImpl(urlBean.getUrl(),
		                urlBean.getUrlConteo().getUrl(), param, true, "ID_CIIU");
    }
    
    public void cargarListarepresentanteLegal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL458
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listarepresentanteLegal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.NIT.getValue());
    }
    
    public void cargarListaORDENADORGASTO() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcontratosControladorUrlEnum.URL458
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listaORDENADORGASTO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmcontratosControladorEnum.NIT.getValue());
    }
    
    public void cargarListaPlantillas() {
    	Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.APLICACION.getName(),SessionUtil.getModulo());
		param.put(GeneralParameterEnum.TIPO.getName(),"112");
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmcontratosControladorUrlEnum.URL104082.getValue());
		
		
		listaPlantillas = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(), 
						  param, true,GeneralParameterEnum.CODIGO.getName());
    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control CANTIDAD
     * 
     * 
     */
    public void cambiarCANTIDAD() {
        // <CODIGO_DESARROLLADO>
        cambioConcepto = true;

        String conceptoAux = registroSub.getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName())
                        .toString();

        obtenerRegistroConceptos(conceptoAux);

        cambioConcepto = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorBaseFijo
     *
     *
     */
    public void cambiarValorBaseFijo() {
        // <CODIGO_DESARROLLADO>
        cambioConcepto = true;
        obtenerRegistroConceptos(registroSub.getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName()));
        cambioConcepto = false;
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control ValorBase
     *
     *
     */
    public void cambiarVALOR_BASE()
    {
        // <CODIGO_DESARROLLADO>
        String conceptoAux = registroSub.getCampos().get(GeneralParameterEnum.CONCEPTO.getName())
                        .toString();
        logicaCambiarValorBase(conceptoAux);
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control ValorUnitario
     *
     *
     */
    public void cambiarVALOR_UNITARIO()
    {
        // <CODIGO_DESARROLLADO>
        cambioValorUnitario = true;
        String conceptoAux = registroSub.getCampos().get(GeneralParameterEnum.CONCEPTO.getName())
                .toString();
        obtenerRegistroConceptos(conceptoAux);        
        cambioValorUnitario = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TARIFAC en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTARIFACC(int rowNum) {
        cambioTarifa = true;

        Object conceptoAux = listaSubconceptoscontrato.get(rowNum)
                        .getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName());

        listaSubconceptoscontrato.get(rowNum).getCampos().put(
                        FrmcontratosControladorEnum.TARIFA.getValue(),
                        auxiliar);

        registroSub.getCampos().put(
                        FrmcontratosControladorEnum.BASE_FIJA.getValue(),
                        listaSubconceptoscontrato.get(rowNum).getCampos()
                                        .get(FrmcontratosControladorEnum.BASE_FIJA
                                                        .getValue()));

        if (SysmanFunciones.validarVariableVacio(auxiliar)) {
            Registro regVacio = new Registro();
            logicaSeleccionarTarifa(regVacio, conceptoAux);
        }
        else {

            Registro tarifa = retornarRegistroTarifa(auxiliar);

            if (tarifa != null) {

                logicaSeleccionarTarifa(tarifa, conceptoAux);
            }
        }

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_BASE.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_COMPRA.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_UNITARIO.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_IVA.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_RETE.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_DESCUENTO.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_ICA.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_NETO.getValue());

        cambioTarifa = false;
    }

    /**
     * Metodo ejecutado al cambiar el control ESTRATOC en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarESTRATOCC(int rowNum) {
        Object conceptoAux = listaSubconceptoscontrato.get(rowNum)
                        .getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName());

        listaSubconceptoscontrato.get(rowNum).getCampos()
                        .put(GeneralParameterEnum.ESTRATO.getName(), auxiliar);

        registroSub.getCampos().put(
                        FrmcontratosControladorEnum.BASE_FIJA.getValue(),
                        listaSubconceptoscontrato.get(rowNum).getCampos()
                                        .get(FrmcontratosControladorEnum.BASE_FIJA
                                                        .getValue()));

        if (SysmanFunciones.validarVariableVacio(auxiliar)) {
            Registro regVacio = new Registro();
            logicaSeleccionarEstrato(regVacio, conceptoAux);
        }
        else {

            Registro estrato = retornarRegistroEstrato(auxiliar);

            if (estrato != null) {

                logicaSeleccionarEstrato(estrato, conceptoAux);
            }
        }

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_BASE.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_COMPRA.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_UNITARIO.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_NETO.getValue());

        cambioEstrato = false;
    }

    /**
     * Metodo ejecutado al cambiar el control CANTIDAD en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCANTIDADC(int rowNum) {
        registroSub.getCampos().put(
                        FrmcontratosControladorEnum.BASE_FIJA.getValue(),
                        listaSubconceptoscontrato.get(rowNum).getCampos()
                                        .get(FrmcontratosControladorEnum.BASE_FIJA
                                                        .getValue()));

        String conceptoAux = listaSubconceptoscontrato.get(rowNum).getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName())
                        .toString();

        registroSub.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(),
                        listaSubconceptoscontrato.get(rowNum).getCampos()
                                        .get(GeneralParameterEnum.CANTIDAD
                                                        .getName()));

        obtenerRegistroConceptos(conceptoAux);

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_BASE.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_COMPRA.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_UNITARIO.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_NETO.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_IMPOCONSUMO
                                        .getValue());
    }

    /**
     * Metodo ejecutado al cambiar el control ValorBaseFijo en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorBaseFijoC(int rowNum) {
        cambioBaseFija = true;

        extraerValorTarifa(listaSubconceptoscontrato.get(rowNum)
                        .getCampos()
                        .get(FrmcontratosControladorEnum.TARIFA.getValue()));

        obtenerRegistroConceptos(listaSubconceptoscontrato.get(rowNum)
                        .getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName()));

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_BASE.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_COMPRA.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_UNITARIO.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_IVA.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_RETE.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_DESCUENTO.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_ICA.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
                        FrmcontratosControladorEnum.VALOR_NETO.getValue());

        cambioBaseFija = false;
    }
    
    /**
     * Metodo ejecutado al cambiar el control ValorBase en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarVALOR_BASEC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        String conceptoAux = listaSubconceptoscontrato.get(rowNum)
                        .getCampos().get(GeneralParameterEnum.CONCEPTO.getName()).toString();

        registroSub.getCampos().put(
        		FrmcontratosControladorEnum.VALOR_BASE
                                        .getValue(),
                                        listaSubconceptoscontrato.get(rowNum).getCampos()
                                        .get(FrmcontratosControladorEnum.VALOR_BASE
                                                        .getValue()));
        logicaCambiarValorBase(conceptoAux);

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_BASE
                                        .getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_UNITARIO
                                        .getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_NETO
                                        .getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_IVA.getValue());
        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_RETE.getValue());
        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_DESCUENTO.getValue());
        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_ICA.getValue());

        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control ValorUnitario en la fila
     * seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarVALOR_UNITARIOC(int rowNum)
    {
        cambioValorUnitario = true;
        String conceptoAux = listaSubconceptoscontrato.get(rowNum)
                        .getCampos().get(GeneralParameterEnum.CONCEPTO.getName()).toString();

        registroSub.getCampos().put(
        		FrmcontratosControladorEnum.VALOR_UNITARIO
                                        .getValue(),
                                        listaSubconceptoscontrato.get(rowNum).getCampos()
                                        .get(FrmcontratosControladorEnum.VALOR_UNITARIO
                                                        .getValue()));
        obtenerRegistroConceptos(conceptoAux);

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_BASE
                                        .getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_COMPRA.getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_UNITARIO
                                        .getValue());

        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_NETO
                                        .getValue());
        agregarValoresRegistroSub(listaSubconceptoscontrato.get(rowNum),
        		FrmcontratosControladorEnum.VALOR_IVA.getValue());

    }       
     	
	public void oprimirpdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		
		if(plantilla != null && !plantilla.isEmpty()) {
			generaPlantilla();
		} else {
			generarInforme(FORMATOS.PDF);
		}
		// </CODIGO_DESARROLLADO>
	}

	private void generarInforme(FORMATOS formato) {

		try {
			String nombreReporte = "";
				
			if(paramNombreReporte.equals("NO")) {
				nombreReporte = "002506ContrantosTel";
			}else {
				nombreReporte = "002651contratopersonalizado";
			}
			Map<String, Object> parametros = new HashMap<>();
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("numero", numero);
			reemplazar.put("tipoCobro", tipoCobro);
			reemplazar.put("compania", compania);
			parametros.put("PR_NOMBRECOMPANIA",
                    SessionUtil.getCompaniaIngreso().getNombre());
			Reporteador.resuelveConsulta(nombreReporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar,
					parametros);

			archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
			
		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
    
	private void generaPlantilla() {
		String[] campos = new String[3];
		String[] valores = new String[3];
		
        campos[0] = "codigoPlantilla";
        campos[1] = "fechaPlantilla";
        campos[2] = "nombreDocDescarga";

        valores[0] = plantilla;
        valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
        valores[2] = nombrePlantilla;
        
        HashMap<String, String> variablesConsultaW = new HashMap<>();
        variablesConsultaW.put("s$compania$s","'" + compania + "'");
        variablesConsultaW.put("s$tipo$s",tipoCobro);
        variablesConsultaW.put("s$numero$s",numero);
        variablesConsultaW.put("s$anio$s",anio);
        variablesConsultaW.put("s$usuario$s",SessionUtil.getUser().toString());
        
        SessionUtil.setSessionVar("variablesConsultaWord",variablesConsultaW);

        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR.getCodigo()),
                        SessionUtil.getModulo(),campos,valores);
	}
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTARIFAC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTARIFAC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registroSub.getCampos().put(
                        FrmcontratosControladorEnum.TARIFA.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName())
                                        .toString());

        logicaSeleccionarTarifa(registroAux,
                        registroSub.getCampos()
                                        .get(GeneralParameterEnum.CONCEPTO
                                                        .getName()));
        cambioTarifa = false;

    }

    /*
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTARIFAC
     *
     * @param event objeto que encapsula la accion proveniente de la
     * vista
     */
    public void seleccionarFilaNOMBRECONCEPTOC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTARIFAC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTARIFACE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTERCEROC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTERCEROC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("TERCERO",
                        registroAux.getCampos().get("NIT"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTERCEROC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTERCEROCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("NIT");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCostoC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroCostoC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("CENTRO_COSTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCostoC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroCostoCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCostoC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroUtilidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("CENTRO_UTILIDAD",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCostoC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroUtilidadE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }


    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAUXILIARC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAUXILIARC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("AUXILIAR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAUXILIARC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAUXILIARCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCONCEPTOC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCONCEPTOC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        cambioConcepto = true;
        registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registroSub.getCampos().put(
                        FrmcontratosControladorEnum.NOMBRECONCEPTO.getValue(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()));

        registroSub.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(),
                        "1");

        registroSub.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.TERCERO
                                        .getName()));

        registroSub.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registro.getCampos().get(GeneralParameterEnum.SUCURSAL
                                        .getName()));

        extraerValorTarifa(registroSub.getCampos()
                        .get(FrmcontratosControladorEnum.TARIFA.getValue()));

        obtenerRegistroConceptos(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

        cambioConcepto = false;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCONCEPTOC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCONCEPTOCE(SelectEvent event) {
        cambioConcepto = true;
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();

        cambioConcepto = false;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaESTRATOC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaESTRATOC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("ESTRATO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        logicaSeleccionarEstrato(registroAux,
                        registroSub.getCampos()
                                        .get(GeneralParameterEnum.CONCEPTO
                                                        .getName()));
        cambioEstrato = false;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaESTRATOC
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaESTRATOCE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("REFERENCIA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecurso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTE_RECURSO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecursoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTERCEROG
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTERCEROG(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos()
                                        .get(FrmcontratosControladorEnum.NIT
                                                        .getValue())
                                        .toString());
        desTercero = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
        sucursal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaVENDEDOR
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaVENDEDOR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("VENDEDOR",
                        registroAux.getCampos()
                                        .get(FrmcontratosControladorEnum.NIT
                                                        .getValue())
                                        .toString());
        desVendedor = SysmanFunciones.nvl(registroAux.getCampos()
                .get(GeneralParameterEnum.NOMBRE.getName()), "")
                .toString();
        sucursal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()), "")
                        .toString();
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCostoG
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroCostoG(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CENTRO_COSTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName())
                                        .toString());
        desCentroCosto = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAFECTARCOTIZACIONG
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAFECTARCOTIZACIONG(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NUMCOTIZACION",
                        registroAux.getCampos().get("TIPO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAUXILIARG
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAUXILIARG(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("AUXILIAR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        desAuxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoLocal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoLocal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(FrmcontratosControladorEnum.LOCAL.getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(FrmcontratosControladorEnum.ANCHO.getValue(),
                        registroAux.getCampos()
                                        .get(FrmcontratosControladorEnum.ANCHO
                                                        .getValue()));

        registro.getCampos().put(FrmcontratosControladorEnum.LARGO.getValue(),
                        registroAux.getCampos()
                                        .get(FrmcontratosControladorEnum.LARGO
                                                        .getValue()));

        registro.getCampos().put(FrmcontratosControladorEnum.AREA.getValue(),
                        registroAux.getCampos()
                                        .get(FrmcontratosControladorEnum.AREA
                                                        .getValue()));

        nombreEstado = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        FrmcontratosControladorEnum.NOMBRE_ESTADO
                                                        .getValue()),
                        "").toString();

        registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));

        registro.getCampos().put(
                        FrmcontratosControladorEnum.NOMBRE_LUGAR.getValue(),
                        registroAux.getCampos().get(
                                        FrmcontratosControladorEnum.NOMBRE_LUGAR
                                                        .getValue()));

        registro.getCampos().put(
                        FrmcontratosControladorEnum.NOMBRE_UBICACION.getValue(),
                        registroAux.getCampos().get(
                                        FrmcontratosControladorEnum.NOMBRE_UBICACION
                                                        .getValue()));

        registro.getCampos().put(
                        FrmcontratosControladorEnum.CODIGO_ANTERIOR.getValue(),
                        registroAux.getCampos().get(
                                        FrmcontratosControladorEnum.CODIGO_ANTERIOR
                                                        .getValue()));

    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoPago
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoPago(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_PAGO",
                        registroAux.getCampos()
                                        .get(FrmcontratosControladorEnum.CODIGO
                                                        .getValue())
                                        .toString());
        
        
    }
    public void seleccionarFilanumeroCotizacion(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	numeroCotizacion = registroAux.getCampos()
                .get("NUMERO")
                .toString();
    	tipoCotizacion = registroAux.getCampos()
    			.get("TIPO")
      			.toString();
        registro.getCampos().put("NUMCOTIZACION", numeroCotizacion);
        registro.getCampos().put("TIPOCOTIZACION", tipoCotizacion);
        registro.getCampos().put("TIEMPO_CONTRATADO", registroAux.getCampos()
                .get("TIEMPO_COTIZADO")
        .toString());
        registro.getCampos().put("TERCERO", registroAux.getCampos()
                .get("TERCERO")
        .toString().trim());
        desTercero = registroAux.getCampos()
                .get("NOMBRETERCERO")
        .toString().trim();
        registro.getCampos().put("TOTAL_TCONTRATADO", registroAux.getCampos()
                .get("TOTAL_TCOTIZADO")
        .toString());
        registro.getCampos().put("PORCFONDO", registroAux.getCampos()
                .get("PORC_FONDO")
        .toString());
        registro.getCampos().put("EDIFICIO", registroAux.getCampos()
                .get("EDIFICIO")
        .toString());
        registro.getCampos().put("TOTALAREA", registroAux.getCampos()
	            .get("TOTAL_AREA")
	    .toString());
        registro.getCampos().put("TOTALPERIODO", registroAux.getCampos()
                .get("TOTAL_PERIODO")
        .toString().trim());
      
    }
    public void seleccionarFilainmueble(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("INMUEBLE",
                        registroAux.getCampos()
                                        .get("CODIGOINMUEBLE")
                                        .toString());
    }
    public void seleccionarFilainmuebleE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registroSub.getCampos().put("INMUEBLE",
                registroAux.getCampos()
                                .get("CODIGOINMUEBLE")
                                .toString());
    }
    public void seleccionarFiladestinacion(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("DESTINACION", registroAux.getCampos()
                .get("ID_CIIU")
        .toString());
    }
    public void seleccionarFilarepresentanteLegal(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        if(!registroAux.getCampos().isEmpty()) {
	    	registro.getCampos().put("REPRESENTANTELEGAL", registroAux.getCampos()
	                	.get("NIT")
	                	.toString());
	        nomRepresentanteLegal = registroAux.getCampos()
				                .get("NOMBRE")
				        .toString();
        }else {
        	registro.getCampos().put("REPRESENTANTELEGAL", "");
        	nomRepresentanteLegal = "";
        }
    }
    public void seleccionarFilaORDENADORGASTO(SelectEvent event){
    	Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ORDENADORGASTO", registroAux.getCampos()
                	.get("NIT")
                	.toString().trim());
        nombreordenadorgasto = registroAux.getCampos()
			                .get("NOMBRE")
			        .toString();
    }
    
    public void seleccionarFilaPlantillas(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        plantilla = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        nombrePlantilla = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        fechaPlantilla = (Date) registroAux.getCampos().get(GeneralParameterEnum.FECHA.getName());
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdImprimir en la vista
     *
     *
     */
    public void oprimirCmdImprimir() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdAprobar en la vista
     *
     *
     */
    public void oprimirCmdAprobar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Sub_conceptoscontrato
     * 
     */
    public void agregarRegistroSubSubconceptoscontrato() {
        try {

            registroSub.getCampos()
                            .remove(FrmcontratosControladorEnum.VALOR_UNIDAD
                                            .getValue());

            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.ANO
                                            .getName()));

            registroSub.getCampos().put(GeneralParameterEnum.TIPO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.TIPO
                                            .getName()));
            registroSub.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.NUMERO
                                            .getName()));
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            
            //INI_TICKET 7727401 MPEREZ
            registroSub.getCampos().put(
            		FrmcontratosControladorEnum.SALDO_ACTUAL
                    .getValue(),
                    registroSub.getCampos().get(FrmcontratosControladorEnum.VALOR_NETO
                            .getValue()));
            //FIN_TICKET 7727401 MPEREZ            

            registroSub.getCampos()
                            .remove(FrmcontratosControladorEnum.NOMBRECONCEPTO
                                            .getValue());
         // se valida los conceptos existentes
 			if(!visibleCotizaContrato) {
 				Map<String, Object> param = new TreeMap<>();
 			
 				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
 				param.put(GeneralParameterEnum.NUMERO.getName(), SysmanFunciones.nvl(registroSub.getCampos().get(GeneralParameterEnum.NUMERO.getName()), ""));
 				param.put(GeneralParameterEnum.CONCEPTO.getName(), SysmanFunciones.nvl(registroSub.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()), ""));
 	
 				Registro reg = RegistroConverter
 						.toRegistro(
 								requestManager.get(
 										UrlServiceUtil.getInstance()
 												.getUrlServiceByUrlByEnumID(
 														FrmcontratosControladorUrlEnum.URL682003.getValue())
 												.getUrl(),
 										param));
 				if(!reg.getCampos().isEmpty()) {
 					if(Integer.parseInt(reg.getCampos().get("EXISTE").toString()) > 0) {
 						JsfUtil.agregarMensajeError("El concepto ya se encuentra asociado a un detalle.");
 						return;
 					}else {
 						registroSub.getCampos().put("INMUEBLE",(int) SysmanFunciones.nvl(reg.getCampos().get("CONSEC"), 0) + 1);
 					}
 				}
 			}

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_CONTRATO
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaSubconceptoscontrato();

            cargarTotales();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Sub_conceptoscontrato
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubconceptoscontrato(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(FrmcontratosControladorEnum.NOMBRECONCEPTO
                            .getValue());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            usuario);
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            
            //INI_TICKET 7727401 MPEREZ
            reg.getCampos().put(FrmcontratosControladorEnum.SALDO_ACTUAL
                    .getValue(),reg.getCampos().get(FrmcontratosControladorEnum.VALOR_NETO
                    .getValue()));
            //FIN_TICKET 7727401 MPEREZ

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_CONTRATO
                                                            .getUpdateKey());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            cargarTotales();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

            cargarListaSubconceptoscontrato();

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void activarEdicionSubconceptoscontrato(Registro r) {
        indiceSubconceptoscontrato = listaSubconceptoscontrato.indexOf(r);
        Registro registroAlEdit = new Registro(new HashMap<>(r.getCampos()));

        cargarListaESTRATOCE();

        cargarListaTARIFACE();

        registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                        registroAlEdit.getCampos()
                                        .get(GeneralParameterEnum.CONCEPTO
                                                        .getName()));
        registroSub.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(),
                        registroAlEdit.getCampos()
                                        .get(GeneralParameterEnum.CANTIDAD
                                                        .getName()));

        registroSub.getCampos().put(
                        FrmcontratosControladorEnum.VALOR_UNITARIO.getValue(),
                        registroAlEdit.getCampos().get(
                                        FrmcontratosControladorEnum.VALOR_UNITARIO
                                                        .getValue()));
    }

    /**
     * Metodo de eliminacion del formulario Sub_conceptoscontrato
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubconceptoscontrato(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_CONTRATO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarTotales();
            cargarListaSubconceptoscontrato();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Sub_conceptoscontrato
     *
     */
    public void cancelarEdicionSubconceptoscontrato() {
        cargarListaSubconceptoscontrato();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    private void agregarValoresRegistroSub(Registro reg, String nombreCampo) {
        reg.getCampos().put(nombreCampo,
                        registroSub.getCampos().get(nombreCampo));
    }

    private void extraerValorTarifa(Object tarifa) {
        Registro regTarifa = retornarRegistroTarifa(tarifa);

        if (regTarifa != null) {

            if ((boolean) regTarifa.getCampos().get(
                            FrmcontratosControladorEnum.TOMAR_VLR.getValue())) {
                vlrTarifa = retornarDouble(regTarifa,
                                GeneralParameterEnum.PORCENTAJE.getName());

            }
            else {
                vlrTarifa = SysmanFunciones.nvlDbl(regTarifa.getCampos()
                                .get(GeneralParameterEnum.PORCENTAJE.getName()),
                                100)
                    / 100;
            }
        }
        else {
            vlrTarifa = 1;
        }

    }

    private Registro retornarRegistroTarifa(Object concepto) {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("TIPOCOBRO", tipoCobro);
        param.put(GeneralParameterEnum.CODIGO.getName(), concepto);
        Registro reg = null;

        if (concepto != null) {

            try {
                reg = listaTARIFAC.getRegistroUnico(param);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            return reg;
        }
        else {
            return reg;
        }
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     *
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }
    
    private void logicaCambiarValorBase(Object concepto)
    {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(), anio);
        params.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        params.put(GeneralParameterEnum.CODIGO.getName(), concepto);

        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                            		FrmcontratosControladorUrlEnum.URL727
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            double valorIva = calcularValoresImpuest(rs, FrmcontratosControladorEnum.APLICAIVA.getValue(),
                            "PORCENTAJEIVA");
            registroSub.getCampos().put(FrmcontratosControladorEnum.VALOR_IVA
                    .getValue(), valorIva);

            double valorRete = calcularValoresImpuest(rs, FrmcontratosControladorEnum.APLICARETEFUENTE.getValue(),
                            "PORCENTAJERETEFUENTE");
            registroSub.getCampos().put(FrmcontratosControladorEnum.VALOR_RETE.getValue(), valorRete);

            double valorDescuen = calcularValoresImpuest(rs,
            		FrmcontratosControladorEnum.APLICADESCUENTO
                    .getValue(), "PORCENTAJEDESCUENTO");
            registroSub.getCampos().put(FrmcontratosControladorEnum.APLICADESCUENTO
                    .getValue(), valorDescuen);

            double valorIca = calcularValoresImpuest(rs, FrmcontratosControladorEnum.APLICAICA.getValue(),
                            "PORCENTAJEICA");
            registroSub.getCampos().put(FrmcontratosControladorEnum.VALOR_ICA.getValue(), valorIca);

            double valorBase = retornarDouble(registroSub,
            		FrmcontratosControladorEnum.VALOR_BASE
                                            .getValue());
            double valorNet = valorBase + valorIva + valorRete + valorDescuen
                + valorIca;
            if (valorNet < 0)
            {
                valorNet = 0;
            }
            
            double valorUnitario = valorBase
                / retornarDouble(registroSub, FrmcontratosControladorEnum.CANTIDAD.getValue());

            registroSub.getCampos()
                            .put(FrmcontratosControladorEnum.VALOR_BASE
                                            .getValue(), valorBase);
            registroSub.getCampos().put(
            		FrmcontratosControladorEnum.VALOR_UNITARIO
                                            .getValue(),
                            valorUnitario);
            registroSub.getCampos()
                            .put(FrmcontratosControladorEnum.VALOR_NETO
                                            .getValue(), valorNet);

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    private double calcularValoresImpuest(Registro rs, String indicador,
            String campo)
        {
            double valorRetornar;
            int digRedondeo = 0;

            boolean aplica = (boolean) SysmanFunciones
                            .nvl(rs.getCampos().get(indicador), false);
            double porcentaje = retornarDouble(rs, campo);

            try
            {
                digRedondeo = Integer.parseInt(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "REDONDEO VALOR",
                                                SessionUtil.getModulo(), new Date(),
                                                true), "2")
                                .toString());
            }
            catch (NumberFormatException | SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            if (aplica)
            {
                valorRetornar = SysmanFunciones.redondear(
                                retornarDouble(registroSub,
                                		FrmcontratosControladorEnum.VALOR_BASE
                                                                .getValue())
                                    * (porcentaje / 100),
                                digRedondeo);
                   }
            else
            {
                valorRetornar = 0;
            }
            return valorRetornar;
        }

    public void cargarTotales() {
        Map<String, Object> param = new TreeMap<>();
        numero = registro.getCampos().get(GeneralParameterEnum.NUMERO.getName())
                        .toString();
        tipoDelReg = registro.getCampos().get("TIPO").toString();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), tipoDelReg);
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        try {

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcontratosControladorUrlEnum.URL350
                                                                            .getValue())
                                            .getUrl(), param));

            valorUnitario = SysmanFunciones.nvlStr(
                            extraerString(regAux.getCampos().get("TUNIT")),
                            "0");
            valorBase = SysmanFunciones.nvlStr(
                            extraerString(regAux.getCampos().get("TVALBASE")),
                            "0");
            valorIVA = SysmanFunciones.nvlStr(
                            extraerString(regAux.getCampos().get("TIVA")),
                            "0");
            valorRete = SysmanFunciones.nvlStr(
                            extraerString(regAux.getCampos().get("TRETE")),
                            "0");
            valorDescuento = SysmanFunciones.nvlStr(extraerString(
                            regAux.getCampos().get("TDES")), "0");
            valorICA = SysmanFunciones.nvlStr(
                            extraerString(regAux.getCampos().get("TICA")),
                            "0");
            valorNeto = SysmanFunciones.nvlStr(
                            extraerString(regAux.getCampos().get("TNET")),
                            "0");

            valorImpoconsumo = SysmanFunciones.nvlStr(
                            extraerString(regAux.getCampos()
                                            .get("TIMPOCONSUMO")),
                            "0");
        }
        catch (SystemException e) {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    private void obtenerRegistroConceptos(Object concepto) {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(), anio);
        params.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        params.put(GeneralParameterEnum.CODIGO.getName(), concepto);

        try {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmcontratosControladorUrlEnum.URL727
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            cargarDatosDesdeConcepto(rs);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarDatosDesdeConcepto(Registro reg) {
        try {
            int digRedondeo = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_BASE").toString());

            double cantidad = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroSub.getCampos()
                                                            .get(GeneralParameterEnum.CANTIDAD
                                                                            .getName()),
                                                            "1")
                                            .toString());

            if (digRedondeo == 0) {
                asignarValoresRegistro(FrmcontratosControladorEnum.VALOR_COMPRA
                                .getValue(), 0);
            }
            else {

                double valorCompraAux;

                valorCompraAux = ((retornarConFix(
                                (retornarDouble(reg,
                                                FrmcontratosControladorEnum.VALOR_COMPRA
                                                                .getValue())
                                    / digRedondeo) + 0.501,
                                digRedondeo)
                    * retornarDouble(registroSub,
                                    GeneralParameterEnum.CANTIDAD.getName()))
                    / digRedondeo)
                    + 0.501;

                double valorCompra = retornarConFix(valorCompraAux,
                                digRedondeo);

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_COMPRA
                                                .getValue(), valorCompra);

            }

            if ((boolean) reg.getCampos().get("PERMITEMODIFICARVALOR")) {

                if (Double.doubleToRawLongBits(retornarDouble(registroSub,
                                FrmcontratosControladorEnum.VALOR_UNIDAD
                                                .getValue())) == 0) {

                    validarValorUnitarioValorBase(reg, digRedondeo, cantidad);
                }
                else {

                    double valorUnitario = retornarDouble(registroSub,
                                    FrmcontratosControladorEnum.VALOR_UNIDAD
                                                    .getValue());

                    double valorBase = retornarConFix(
                                    ((valorUnitario * cantidad) / digRedondeo)
                                        + 0.501,
                                    digRedondeo);

                    registroSub.getCampos()
                                    .put(FrmcontratosControladorEnum.VALOR_BASE
                                                    .getValue(), valorBase);

                }
            }

            else if (Double.doubleToRawLongBits(
                            retornarDouble(registroSub,
                                            FrmcontratosControladorEnum.VALOR_UNIDAD
                                                            .getValue())) == 0
                && !Boolean.parseBoolean(reg.getCampos()
                                .get(FrmcontratosControladorEnum.APLICAFORMULA
                                                .getValue())
                                .toString())) {
                double valorUnitarioAux = retornarConFix(
                                (retornarDouble(reg,
                                                FrmcontratosControladorEnum.VALOR_BASE
                                                                .getValue())
                                    / digRedondeo)
                                    + 0.501,
                                digRedondeo);

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_UNITARIO
                                                .getValue(),
                                                valorUnitarioAux);

                double valorBaseAux = retornarConFix(
                                ((retornarDouble(reg,
                                                FrmcontratosControladorEnum.VALOR_BASE
                                                                .getValue())
                                    * cantidad)
                                    / digRedondeo) + 0.501,
                                digRedondeo);

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_BASE
                                                .getValue(), valorBaseAux);

            }
            else {

                if (!Boolean.parseBoolean(
                                reg.getCampos().get(
                                                FrmcontratosControladorEnum.APLICAFORMULA
                                                                .getValue())
                                                .toString())
                    || cambioEstrato || cambioTarifa) {

                    registroSub.getCampos().put(
                                    FrmcontratosControladorEnum.VALOR_UNITARIO
                                                    .getValue(),
                                    registroSub.getCampos()
                                                    .get(FrmcontratosControladorEnum.VALOR_UNIDAD
                                                                    .getValue()));

                }
                double valorBaseAux = retornarConFix(
                                ((retornarDouble(registroSub,
                                                FrmcontratosControladorEnum.VALOR_UNITARIO
                                                                .getValue())
                                    * cantidad) / digRedondeo) + 0.501,
                                digRedondeo);

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_BASE
                                                .getValue(), valorBaseAux);

            }

            registroSub.getCampos().put("CUENTA_BANCO",
                            reg.getCampos().get("CUENTA_RECAUDO"));

            hallarValorBaseAplicaFormula(reg);
            hallarValorDescuento(reg);
            hallarValorIva(reg);
            hallarValorReteFuente(reg);
            hallarValorIca(reg);
            hallarValorImpoconsumo(reg, cantidad);
            hallarValorNetoUnitarioUnidad(reg);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void hallarValorBaseAplicaFormula(Registro reg) {
        try {

            double valorBaseFijo;
            double cantidad = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroSub.getCampos().get(
                                                            GeneralParameterEnum.CANTIDAD
                                                                            .getName()),
                                                            "1")
                                            .toString());

            valorBaseFijo = Double.parseDouble(SysmanFunciones
                            .nvl(registroSub.getCampos().get(
                                            FrmcontratosControladorEnum.BASE_FIJA
                                                            .getValue()),
                                            "0")
                            .toString());

            String formula = reg.getCampos().get("FORMULA").toString().replace(
                            "VlrBaseF()",
                            Double.toString(valorBaseFijo));

            formula = formula.replace("Tarifa()",
                            Double.toString(vlrTarifa <= 0.0 ? 1 : vlrTarifa));

            formula = formula.replace("SalarioDiario()", ejbFactGenDos
                            .retornarValorAnual(compania,
                                            Integer.parseInt(anio),
                                            "SALARIOMINIMO", true)
                            .toString());

            formula = formula.replace("UVT()",
                            ejbFactGenDos.retornarValorAnual(compania,
                                            Integer.parseInt(anio), "VALORUVT",
                                            false).toString());

            int digRedondeo = Integer.parseInt(reg.getCampos()
                            .get("FACTOR_RED_BASETOTAL").toString());
            if (((boolean) reg.getCampos().get(
                            FrmcontratosControladorEnum.APLICAFORMULA
                                            .getValue())
                && cambioConcepto) || cambioBaseFija) {

                BigDecimal valorBaseUnitario = ejbFactGenTres
                                .reemplazarFormula(formula);

                valorBaseUnitario = ejbFactGenUno
                                .cargarValorConceptoCant(Double.parseDouble(
                                                valorBaseUnitario.toString()),
                                                1, digRedondeo);

                double valorBase = valorBaseUnitario.doubleValue() * cantidad;

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_BASE
                                                .getValue(), valorBase);
                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_UNITARIO
                                                .getValue(),
                                                valorBaseUnitario);

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.BASE_FIJA
                                                .getValue(), valorBaseUnitario);

            }

            registroSub.getCampos()
                            .put(FrmcontratosControladorEnum.BASE_FIJA
                                            .getValue(),
                                            registroSub.getCampos().get(
                                                            FrmcontratosControladorEnum.VALOR_UNITARIO
                                                                            .getValue()));

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * La variable global aplicatarifa se invoca al cambiar el combo
     * tarifa, por lo que este metodo se ejecuta al cambiar el
     * concepto y al cambiar tarifa ya que tiene que ser recalculado
     * sabiendo si es con tarifa o no .
     *
     * @param reg
     */

    private void hallarValorDescuento(Registro reg) {
        try {
            BigDecimal valorDescuento;

            double porcentajeDescuento = Double.parseDouble(reg.getCampos()
                            .get("PORCENTAJEDESCUENTO").toString());

            boolean aplicaDescuento = (boolean) reg.getCampos()
                            .get(FrmcontratosControladorEnum.APLICADESCUENTO
                                            .getValue());

            double valorBase = retornarDouble(registroSub,
                            FrmcontratosControladorEnum.VALOR_BASE.getValue());

            valorBase = valorBase * (porcentajeDescuento / 100);
            int redDesc = Integer.parseInt(reg.getCampos()
                            .get("FACTOR_RED_DESCUENTO").toString());

            if ((Double.doubleToRawLongBits(valorBase) == 0)
                || (Double.doubleToRawLongBits(redDesc) == 0)) {

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_DESCUENTO
                                                .getValue(), 0);
            }

            if (aplicaDescuento) {

                valorDescuento = ejbFactGenUno.cargarValorConceptoIndica(
                                aplicaDescuento,
                                new BigDecimal(Double.toString(valorBase)),
                                redDesc);

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_DESCUENTO
                                                .getValue(), valorDescuento);
            }

            else {
                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_DESCUENTO
                                                .getValue(), 0);
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorIva(Registro reg) {
        try {
            boolean aplicaIva = (boolean) reg.getCampos().get("APLICAIVA");
            int digIva = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_IVA").toString());
            double valorBase = retornarDouble(registroSub,
                            FrmcontratosControladorEnum.VALOR_BASE.getValue());
            double valorDescuento = retornarDouble(reg,
                            FrmcontratosControladorEnum.VALOR_DESCUENTO
                                            .getValue());
            double porcentajeIva = retornarDouble(reg, "PORCENTAJEIVA");

            if (!aplicaIva || (Double.doubleToRawLongBits(digIva) == 0)) {
                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_IVA
                                                .getValue(), 0);
            }
            else {
                BigDecimal valorIva;

                valorBase = (valorBase - valorDescuento)
                    * (porcentajeIva / 100);

                valorIva = ejbFactGenUno.cargarValorConceptoIndica(aplicaIva,
                                new BigDecimal(Double.toString(valorBase)),
                                digIva);

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_IVA
                                                .getValue(), valorIva);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorReteFuente(Registro reg) {

        try {
            int redRete = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_RETE").toString());
            double valorBase = retornarDouble(registroSub,
                            FrmcontratosControladorEnum.VALOR_BASE.getValue());
            double porcentajeRetefuente = retornarDouble(reg,
                            "PORCENTAJERETEFUENTE");
            BigDecimal valorRetefuente;

            boolean aplicaRetefuente = (boolean) reg.getCampos()
                            .get("APLICARETEFUENTE");
            if (aplicaRetefuente) {

                valorBase = valorBase * (porcentajeRetefuente / 100);

                valorRetefuente = ejbFactGenUno.cargarValorConceptoIndica(
                                aplicaRetefuente,
                                new BigDecimal(Double.toString(valorBase)),
                                redRete);

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_RETE
                                                .getValue(),
                                                valorRetefuente);
            }
            else {
                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_RETE
                                                .getValue(), 0);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorIca(Registro reg) {

        try {
            boolean aplicaIca = (boolean) reg.getCampos().get("APLICAICA");
            int redIca = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_ICA").toString());
            double valorBase = retornarDouble(registroSub,
                            FrmcontratosControladorEnum.VALOR_BASE.getValue());
            double porcentajeIca = retornarDouble(reg, "PORCENTAJEICA");
            BigDecimal valorIca;

            if (aplicaIca) {

                valorBase = valorBase * (porcentajeIca / 100);

                valorIca = ejbFactGenUno.cargarValorConceptoIndica(aplicaIca,
                                new BigDecimal(Double.toString(valorBase)),
                                redIca);

                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_ICA
                                                .getValue(), valorIca);
            }
            else {
                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_ICA
                                                .getValue(), "0");
            }

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorImpoconsumo(Registro reg, double cantidad) {
        if ((boolean) reg.getCampos().get("APLICAIMPOCONSUMO")) {

            registroSub.getCampos().put(
                            FrmcontratosControladorEnum.VALOR_IMPOCONSUMO
                                            .getValue(),
                            (retornarDouble(reg,
                                            FrmcontratosControladorEnum.VALOR_IMPOCONSUMO
                                                            .getValue())
                                * cantidad));

        }
        else {
            registroSub.getCampos().put(
                            FrmcontratosControladorEnum.VALOR_IMPOCONSUMO
                                            .getValue(),
                            "0");

        }

    }

    private void hallarValorNetoUnitarioUnidad(Registro reg) {

        double valorBase = retornarDouble(registroSub,
                        FrmcontratosControladorEnum.VALOR_BASE.getValue());
        double valorIva = retornarDouble(registroSub,
                        FrmcontratosControladorEnum.VALOR_IVA.getValue());
        double valorRetefuente = retornarDouble(registroSub,
                        FrmcontratosControladorEnum.VALOR_RETE.getValue());
        double valorDescuento = retornarDouble(registroSub,
                        FrmcontratosControladorEnum.VALOR_DESCUENTO.getValue());
        double valorIca = retornarDouble(registroSub,
                        FrmcontratosControladorEnum.VALOR_ICA.getValue());
        double valorCompra = retornarDouble(registroSub,
                        FrmcontratosControladorEnum.VALOR_COMPRA.getValue());
        double valorImpoconsumoAux = retornarDouble(registroSub,
                        FrmcontratosControladorEnum.VALOR_IMPOCONSUMO
                                        .getValue());

        double valorNetoAux = ((valorBase + valorIva + valorRetefuente
            + valorImpoconsumoAux) - valorDescuento)
            + valorIca;

        if (valorNetoAux < 0) {
            registroSub.getCampos()
                            .put(FrmcontratosControladorEnum.VALOR_NETO
                                            .getValue(), 0);
        }
        else {
            registroSub.getCampos()
                            .put(FrmcontratosControladorEnum.VALOR_NETO
                                            .getValue(), valorNetoAux);
        }

        double valorUtilidad = valorBase - valorCompra;
        registroSub.getCampos().put("VALOR_UTILIDAD", valorUtilidad);

        registroSub.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        retornarString(reg, GeneralParameterEnum.REFERENCIA
                                        .getName()));

        registroSub.getCampos().put(
                        GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        retornarString(reg, GeneralParameterEnum.FUENTE_RECURSO
                                        .getName()));

        registroSub.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        retornarString(reg, GeneralParameterEnum.CENTRO_COSTO
                                        .getName()));
        registroSub.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        retornarString(reg, GeneralParameterEnum.AUXILIAR
                                        .getName()));

    }

    private double retornarConFix(double operacion, int digRedondeo)
                    throws SystemException {

        BigDecimal valorBigD = BigDecimal.valueOf(operacion);
        valorBigD = ejbSysmanUtil.fix(valorBigD);

        return valorBigD.doubleValue() * digRedondeo;

    }

    private double retornarDouble(Registro reg, String campo) {
        return Double.parseDouble(SysmanFunciones
                        .nvl(reg.getCampos().get(campo), "0").toString());
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private void asignarValoresRegistro(String campo, Object valor) {
        registro.getCampos().put(campo, valor);
    }

    private void validarValorUnitarioValorBase(Registro reg, int digRedondeo,
        double cantidad) {

        try {
            if (cambioValorUnitario) {                
                reg.getCampos().put(
                		FrmcontratosControladorEnum.VALOR_UNITARIO
                                        .getValue(),
                        registroSub.getCampos().get(
                        		FrmcontratosControladorEnum.VALOR_UNITARIO
                                                        .getValue()));
            }

            double valorBaseConcepto = retornarDouble(reg,
                            FrmcontratosControladorEnum.VALOR_BASE.getValue());
            double valorBase = 0;
            double valorUnitario;
            if (digRedondeo == 0) {
                registroSub.getCampos()
                                .put(FrmcontratosControladorEnum.VALOR_BASE
                                                .getValue(), valorBase);
            }
            else {

                valorBase = retornarConFix(
                                ((retornarDouble(reg,
                                                FrmcontratosControladorEnum.VALOR_UNITARIO
                                                                .getValue())
                                    * cantidad)
                                    / digRedondeo) + 0.501,
                                digRedondeo);

                registroSub.getCampos().put(
                                FrmcontratosControladorEnum.VALOR_BASE
                                                .getValue(),
                                valorBase);

            }

            if (Double.doubleToRawLongBits(valorBase) == 0) {
                if (digRedondeo == 0) {

                    registroSub.getCampos().put(
                                    FrmcontratosControladorEnum.VALOR_UNITARIO
                                                    .getValue(),
                                    0);
                    registroSub.getCampos()
                                    .put(FrmcontratosControladorEnum.VALOR_BASE
                                                    .getValue(), 0);
                }
                else {

                    valorUnitario = retornarConFix(
                                    (valorBaseConcepto / digRedondeo) + 0.501,
                                    digRedondeo);

                    registroSub.getCampos().put(
                                    FrmcontratosControladorEnum.VALOR_UNITARIO
                                                    .getValue(),
                                    valorUnitario);

                    valorBase = retornarConFix(((valorBaseConcepto * cantidad)
                        / digRedondeo) + 0.501, digRedondeo);
                    registroSub.getCampos()
                                    .put(FrmcontratosControladorEnum.VALOR_BASE
                                                    .getValue(), valorBase);

                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void logicaSeleccionarEstrato(Registro registroAux,
        Object concepto) {
        if (SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        GeneralParameterEnum.CODIGO.getName())) {
            registroSub.getCampos().put(
                            FrmcontratosControladorEnum.VALOR_UNIDAD.getValue(),
                            "0");
            vlrEstrato = 1;
        }

        obtenerRegistroConceptos(concepto);

        vlrEstrato = retornarDouble(registroAux,
                        FrmcontratosControladorEnum.TARIFA.getValue());
        boolean tomarVlr = (boolean) SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrmcontratosControladorEnum.TOMAR_VLR
                                                        .getValue()),
                                        false);
        double vlrUnidad;
        if (tomarVlr) {
            vlrUnidad = retornarDouble(registroSub,
                            FrmcontratosControladorEnum.VALOR_UNITARIO
                                            .getValue())
                * vlrEstrato * vlrTarifa;

        }
        else {
            vlrEstrato = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroAux.getCampos().get(
                                                            FrmcontratosControladorEnum.TARIFA
                                                                            .getValue()),
                                                            "100")
                                            .toString())
                / 100;
            vlrUnidad = retornarDouble(registroSub,
                            FrmcontratosControladorEnum.VALOR_UNITARIO
                                            .getValue())
                * vlrEstrato * vlrTarifa;

        }

        registroSub.getCampos()
                        .put(FrmcontratosControladorEnum.VALOR_UNIDAD
                                        .getValue(), vlrUnidad);
        cambioEstrato = true;
        obtenerRegistroConceptos(concepto);

    }

    private void logicaSeleccionarTarifa(Registro reg, Object concepto) {

        Registro registroConcepto = retornarRegistroConcepto(concepto);
        
        if(manejaTarifaBase) {
            
            vlrTarifa = retornarDouble(reg,
                            GeneralParameterEnum.PORCENTAJE.getName());
            
            registroSub.getCampos().put(
                            FrmcontratosControladorEnum.VALOR_UNIDAD.getValue(),vlrTarifa); 
            
            registroSub.getCampos().put(
                            FrmcontratosControladorEnum.VALOR_UNITARIO.getValue(),vlrTarifa);
            
        }
        
        else {

        if (SysmanFunciones.validarCampoVacio(reg.getCampos(),
                        GeneralParameterEnum.CODIGO.getName())) {

            vlrTarifa = 1;
            registroSub.getCampos().put(
                            FrmcontratosControladorEnum.VALOR_UNIDAD.getValue(),
                            retornarDouble(registroSub,
                                            FrmcontratosControladorEnum.VALOR_UNITARIO
                                                            .getValue())
                                * vlrTarifa * vlrEstrato);
        }
        else {

            obtenerRegistroConceptos(concepto);

            if ((boolean) reg.getCampos().get(
                            FrmcontratosControladorEnum.TOMAR_VLR.getValue())) {
                vlrTarifa = retornarDouble(reg,
                                GeneralParameterEnum.PORCENTAJE.getName());
                if ((boolean) registroConcepto.getCampos()
                                .get(FrmcontratosControladorEnum.APLICADESCUENTO
                                                .getValue())) {
                    registroSub.getCampos().put(
                                    FrmcontratosControladorEnum.VALOR_UNIDAD
                                                    .getValue(),
                                    registroSub.getCampos()
                                                    .get(FrmcontratosControladorEnum.VALOR_UNITARIO
                                                                    .getValue()));
                }
                else {
                    double vlrTarifaAux = retornarDouble(registroSub,
                                    FrmcontratosControladorEnum.VALOR_UNITARIO
                                                    .getValue())
                        * vlrTarifa * vlrEstrato;
                    registroSub.getCampos().put(
                                    FrmcontratosControladorEnum.VALOR_UNIDAD
                                                    .getValue(),
                                    vlrTarifaAux);
                }
            }
            else {
                vlrTarifa = SysmanFunciones.nvlDbl(reg.getCampos().get(
                                GeneralParameterEnum.PORCENTAJE.getName()), 100)
                    / 100;
                if ((boolean) registroConcepto.getCampos()
                                .get(FrmcontratosControladorEnum.APLICADESCUENTO
                                                .getValue())) {
                    registroSub.getCampos().put(
                                    FrmcontratosControladorEnum.VALOR_UNIDAD
                                                    .getValue(),
                                    registroSub.getCampos()
                                                    .get(FrmcontratosControladorEnum.VALOR_UNITARIO
                                                                    .getValue()));
                }
                else {
                    registroSub.getCampos().put(
                                    FrmcontratosControladorEnum.VALOR_UNIDAD
                                                    .getValue(),
                                    retornarDouble(registroSub,
                                                    FrmcontratosControladorEnum.VALOR_UNITARIO
                                                                    .getValue())
                                        * vlrTarifa * vlrEstrato);
                }
            }
        }
        
        }
        cambioTarifa = true;
        obtenerRegistroConceptos(concepto);

    }

    private Registro retornarRegistroConcepto(Object concepto) {
        Registro reg = null;

        Map<String, Object> parametros = new TreeMap<>();

        try {
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.ANO.getName(), anio);
            parametros.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(),
                            tipoCobro);
            parametros.put(GeneralParameterEnum.CODIGO.getName(), concepto);

            reg = new Registro(listaCONCEPTOC.getRegistroUnico(parametros)
                            .getCampos());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return reg;
    }

    private Registro retornarRegistroEstrato(Object concepto) {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(FrmcontratosControladorEnum.TIPOCOBRO.getValue(), tipoCobro);
        param.put(GeneralParameterEnum.CODIGO.getName(), concepto);
        Registro reg = null;
        try {
            reg = listaESTRATOCE.getRegistroUnico(param);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return reg;
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
	    try {
			visibleCentroUtilidad = "SI".equals(SysmanFunciones
							.nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA CENTRO DE UTILIDAD",
									SessionUtil.getModulo(), new Date(), true), "NO"));
			
			manejaTarifaBase = "SI".equals(SysmanFunciones
                                        .nvl(ejbSysmanUtil.consultarParametro(compania, "SF MANEJA TARIFAS BASE PARA FACTURAR",
                                                        SessionUtil.getModulo(), new Date(), true), "NO"));
			
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (accion.equals(ACCION_INSERTAR)) {
            registro.getCampos().put(GeneralParameterEnum.TIPO.getName(),
                            tipoCobro);

            desTercero = "";
            desVendedor= "";
            nombreEstado = "";
        }
        else if (accion.equals(ACCION_MODIFICAR)) {

            desTercero = SysmanFunciones
                            .nvl(registro.getCampos().get(
                                            FrmcontratosControladorEnum.NOMBRETERCERO
                                                            .getValue()),
                                            "")
                            .toString();
            
            desVendedor = SysmanFunciones
                    .nvl(registro.getCampos().get(
                                    FrmcontratosControladorEnum.NOMBREVENDEDOR
                                                    .getValue()),
                                    "")
                    .toString();
            nombreEstado = SysmanFunciones
                            .nvl(registro.getCampos().get(
                                            FrmcontratosControladorEnum.NOMBRE_ESTADO
                                                            .getValue()),
                                            "")
                            .toString();
            
            nomRepresentanteLegal = SysmanFunciones
				            .nvl(registro.getCampos().get(
				                            "NOMREPLEGAL"),
				                            "")
				            .toString();
            
            nombreordenadorgasto = SysmanFunciones
				            .nvl(registro.getCampos().get(
				                            "NOMORDENGASTO"),
				                            "")
				            .toString();
            cargarTotales();

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        String inicial = "";

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
        registro.getCampos().put(GeneralParameterEnum.TIPO.getName(),
                        tipoCobro);

        registro.getCampos().remove(
                        FrmcontratosControladorEnum.NOMBRETERCERO.getValue());
        registro.getCampos().remove("COTIZACION");
        registro.getCampos().remove(
                FrmcontratosControladorEnum.NOMBREVENDEDOR.getValue());
        tipoDelReg = registro.getCampos().get("TIPO").toString();
        inicial = anoActual + "000001";
        try {
            long consecutivo = ejbSysmanUtil
                            .generarConsecutivoConValorInicial(
                                            "SF_CONTRATOS",
                                            "COMPANIA = ''" + compania
                                                + "''" +
                                                "AND TIPO = ''" + tipoDelReg
                                                + "''" +
                                                "AND  SUBSTR(NUMERO, 1,4) = ''"
                                                + anoActual
                                                + "''",
                                            "NUMERO",
                                            inicial);
            registro.getCampos().put(
                            GeneralParameterEnum.NUMERO.getName(),
                            consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
    	if(visibleCotizaContrato) {
    		cargarListaDetalleCotizacion();
	    	for(Registro reg: listaDetalleCotizacion) {
	    		registroSub = reg;
	    		
	    		agregarRegistroSubSubconceptoscontrato();
	    	}
    	}
        // </CODIGO_DESARROLLADO>
        return true;
    }
    
    private void cargarListaDetalleCotizacion() {
    	Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoCotizacion);
		param.put(GeneralParameterEnum.NUMERO.getName(), numeroCotizacion);
		
		try {
		
			listaDetalleCotizacion = RegistroConverter
			                .toListRegistro(requestManager
			                                .getList(UrlServiceUtil
			                                                .getInstance()
			                                                .getUrlServiceByUrlByEnumID(
			                                                		FrmcontratosControladorUrlEnum.URL683003.getValue())
			                                                .getUrl(), param));
		
			
			
		}catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {

        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(FrmcontratosControladorEnum.NOMBRETERCERO
                                            .getValue());
            registro.getCampos()
            				.remove(FrmcontratosControladorEnum.NOMBREVENDEDOR
            								.getValue());
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove("COTIZACION");
            registro.getCampos().remove(
                            FrmcontratosControladorEnum.ANCHO.getValue());
            registro.getCampos().remove(
                            FrmcontratosControladorEnum.LARGO.getValue());
            registro.getCampos().remove(
                            FrmcontratosControladorEnum.AREA.getValue());
            registro.getCampos()
                            .remove(FrmcontratosControladorEnum.NOMBRE_ESTADO
                                            .getValue());
            registro.getCampos()
                            .remove(GeneralParameterEnum.DESCRIPCION.getName());
            registro.getCampos().remove(FrmcontratosControladorEnum.NOMBRE_LUGAR
                            .getValue());
            registro.getCampos()
                            .remove(FrmcontratosControladorEnum.NOMBRE_UBICACION
                                            .getValue());
            registro.getCampos()
                            .remove(FrmcontratosControladorEnum.CODIGO_ANTERIOR
                                            .getValue());
            // campos usados con parametro activo
            registro.getCampos()
            				.remove("NOMREPLEGAL");
            
            registro.getCampos()
            				.remove("NOMORDENGASTO");

        }

        return true;
    }

    private void actualizarEstadoLocal() {
        if (!SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(FrmcontratosControladorEnum.LOCAL
                                                        .getValue()),
                                        "")
                        .toString().isEmpty()) {

            Map<String, Object> reg = new TreeMap<>();

            try {
                reg.put("ESTADO", "A");

                reg.put("DATE_MODIFIED", new Date());
                reg.put("MODIFIED_BY", usuario);
                reg.put("KEY_COMPANIA", compania);
                reg.put("KEY_CODIGO", registro.getCampos().get(
                                FrmcontratosControladorEnum.LOCAL.getValue()));

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmcontratosControladorUrlEnum.URL525
                                                                .getValue());

                Parameter parameter = new Parameter();
                parameter.setFields(reg);

                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(), parameter);

                cargarRegistro(registro.getLlave(), accion,
                                registro.getIndice());

                cargarRegistro();
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        actualizarEstadoLocal();
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
    	if(visibleCotizaContrato) {
	    	  Map<String, Object> params = new TreeMap<>();
	          params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	          params.put(GeneralParameterEnum.ANO.getName(), anio);
	          params.put(GeneralParameterEnum.NUMERO.getName(),
	                          registro.getCampos().get("NUMERO").toString());
	          params.put(GeneralParameterEnum.TIPO.getName(),
	                  registro.getCampos().get("TIPO").toString());
	
	          try
	          {
	              Registro rs = RegistroConverter
	                              .toRegistro(requestManager.get(
	                                              UrlServiceUtil.getInstance()
	                                                              .getUrlServiceByUrlByEnumID(
	                                                              		FrmcontratosControladorUrlEnum.URL664020
	                                                                                              .getValue())
	                                                              .getUrl(),
	                                              params));
	              if(rs != null && Integer.parseInt(rs.getCampos().get("EXITO").toString()) > 0) {
	            	  JsfUtil.agregarMensajeAlerta(idioma.getString("MSM_NO_SE_PUEDE_ELIMINAR_EL_CONTRATO"));
	            	  return false;
	              }else {
	            	boolean respuesta = ejbFactGenTres
	            				.eliminarDetallesContratoParametrizado(compania, tipoCobro, registro.getCampos().get("NUMERO").toString());
	            	if(!respuesta) {
	            		JsfUtil.agregarMensajeAlerta(idioma.getString("MSM_NO_SE_PUEDE_ELIMINAR_EL_CONTRATO"));
		            	return false;
	            	}
	              }
	          }catch (Exception e) {
	        	  logger.error(e.getMessage(), e);
	              JsfUtil.agregarMensajeError(e.getMessage());
	          }
    	}
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable nombreEstado
     * 
     * @return nombreEstado
     */
    public String getNombreEstado() {
        return nombreEstado;
    }

    /**
     * Asigna la variable nombreEstado
     * 
     * @param nombreEstado
     * Variable a asignar en nombreEstado
     */
    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCInmueble
     * 
     * @return listaCInmueble
     */

    /**
     * Asigna la lista listaCInmueble
     * 
     * @param listaCInmueble
     * Variable a asignar en listaCInmueble
     */

    public RegistroDataModelImpl getListaCInmueble() {
        return listaCInmueble;
    }

    public void setListaCInmueble(RegistroDataModelImpl listaCInmueble) {
        this.listaCInmueble = listaCInmueble;
    }

    /**
     * Retorna la lista listaNOMBRECONCEPTOC
     * 
     * @return listaNOMBRECONCEPTOC
     */
    public RegistroDataModelImpl getListaNOMBRECONCEPTOC() {
        return listaNOMBRECONCEPTOC;
    }

    /**
     * Asigna la lista listaNOMBRECONCEPTOC
     * 
     * @param listaNOMBRECONCEPTOC
     * Variable a asignar en listaNOMBRECONCEPTOC
     */
    public void setListaNOMBRECONCEPTOC(
        RegistroDataModelImpl listaNOMBRECONCEPTOC) {
        this.listaNOMBRECONCEPTOC = listaNOMBRECONCEPTOC;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTARIFAC
     * 
     * @return listaTARIFAC
     */

    /**
     * Asigna la lista listaTARIFAC
     * 
     * @param listaTARIFAC
     * Variable a asignar en listaTARIFAC
     */

    /**
     * Retorna la lista listaTARIFAC
     * 
     * @return listaTARIFAC
     */

    public RegistroDataModelImpl getListaNOMBRECONCEPTOCE() {
        return listaNOMBRECONCEPTOCE;
    }

    public void setListaNOMBRECONCEPTOCE(
        RegistroDataModelImpl listaNOMBRECONCEPTOCE) {
        this.listaNOMBRECONCEPTOCE = listaNOMBRECONCEPTOCE;
    }

    public RegistroDataModelImpl getListaTARIFAC() {
        return listaTARIFAC;
    }

    public void setListaTARIFAC(RegistroDataModelImpl listaTARIFAC) {
        this.listaTARIFAC = listaTARIFAC;
    }

    /**
     * Asigna la lista listaTARIFAC
     * 
     * @param listaTARIFAC
     * Variable a asignar en listaTARIFAC
     */
    /**
     * Retorna la lista listaTERCEROC
     * 
     * @return listaTERCEROC
     */
    public RegistroDataModelImpl getListaTERCEROC() {
        return listaTERCEROC;
    }

    public RegistroDataModelImpl getListaTARIFACE() {
        return listaTARIFACE;
    }

    public void setListaTARIFACE(RegistroDataModelImpl listaTARIFACE) {
        this.listaTARIFACE = listaTARIFACE;
    }

    /**
     * Asigna la lista listaTERCEROC
     * 
     * @param listaTERCEROC
     * Variable a asignar en listaTERCEROC
     */
    public void setListaTERCEROC(RegistroDataModelImpl listaTERCEROC) {
        this.listaTERCEROC = listaTERCEROC;
    }

    /**
     * Retorna la lista listaTERCEROC
     * 
     * @return listaTERCEROC
     */
    public RegistroDataModelImpl getListaTERCEROCE() {
        return listaTERCEROCE;
    }

    /**
     * Asigna la lista listaTERCEROC
     * 
     * @param listaTERCEROC
     * Variable a asignar en listaTERCEROC
     */
    public void setListaTERCEROCE(RegistroDataModelImpl listaTERCEROCE) {
        this.listaTERCEROCE = listaTERCEROCE;
    }

    /**
     * Retorna la lista listacentroCostoC
     * 
     * @return listacentroCostoC
     */

    /**
     * Asigna la lista listacentroCostoC
     * 
     * @param listacentroCostoC
     * Variable a asignar en listacentroCostoC
     */

    /**
     * Retorna la lista listacentroCostoC
     * 
     * @return listacentroCostoC
     */

    /**
     * Asigna la lista listacentroCostoC
     * 
     * @param listacentroCostoC
     * Variable a asignar en listacentroCostoC
     */

    public RegistroDataModelImpl getListacentroCostoC() {
        return listacentroCostoC;
    }

    public void setListacentroCostoC(RegistroDataModelImpl listacentroCostoC) {
        this.listacentroCostoC = listacentroCostoC;
    }

    public RegistroDataModelImpl getListacentroCostoCE() {
        return listacentroCostoCE;
    }

    public void setListacentroCostoCE(
        RegistroDataModelImpl listacentroCostoCE) {
        this.listacentroCostoCE = listacentroCostoCE;
    }

    public void setListacentroCostoG(RegistroDataModelImpl listacentroCostoG) {
        this.listacentroCostoG = listacentroCostoG;
    }

    /**
     * Retorna la lista listaAUXILIARC
     * 
     * @return listaAUXILIARC
     */
    public RegistroDataModelImpl getListaAUXILIARC() {
        return listaAUXILIARC;
    }

    /**
     * Asigna la lista listaAUXILIARC
     * 
     * @param listaAUXILIARC
     * Variable a asignar en listaAUXILIARC
     */
    public void setListaAUXILIARC(RegistroDataModelImpl listaAUXILIARC) {
        this.listaAUXILIARC = listaAUXILIARC;
    }

    /**
     * Retorna la lista listaAUXILIARC
     * 
     * @return listaAUXILIARC
     */
    public RegistroDataModelImpl getListaAUXILIARCE() {
        return listaAUXILIARCE;
    }

    /**
     * Asigna la lista listaAUXILIARC
     * 
     * @param listaAUXILIARC
     * Variable a asignar en listaAUXILIARC
     */
    public void setListaAUXILIARCE(RegistroDataModelImpl listaAUXILIARCE) {
        this.listaAUXILIARCE = listaAUXILIARCE;
    }

    /**
     * Retorna la lista listaCONCEPTOC
     * 
     * @return listaCONCEPTOC
     */

    /**
     * Asigna la lista listaCONCEPTOC
     * 
     * @param listaCONCEPTOC
     * Variable a asignar en listaCONCEPTOC
     */
    public RegistroDataModelImpl getListaCONCEPTOC() {
        return listaCONCEPTOC;
    }

    /**
     * Retorna la lista listaCONCEPTOC
     * 
     * @return listaCONCEPTOC
     */
    public void setListaCONCEPTOC(RegistroDataModelImpl listaCONCEPTOC) {
        this.listaCONCEPTOC = listaCONCEPTOC;
    }

    /**
     * Asigna la lista listaCONCEPTOC
     * 
     * @param listaCONCEPTOC
     * Variable a asignar en listaCONCEPTOC
     */

    /**
     * Retorna la lista listaESTRATOC
     * 
     * @return listaESTRATOC
     */

    public void setListaCONCEPTOCE(RegistroDataModelImpl listaCONCEPTOCE) {
        this.listaCONCEPTOCE = listaCONCEPTOCE;
    }

    public RegistroDataModelImpl getListaCONCEPTOCE() {
        return listaCONCEPTOCE;
    }

    /**
     * Retorna la lista listaReferencia
     * 
     * @return listaReferencia
     */
    public RegistroDataModelImpl getListaReferencia() {
        return listaReferencia;
    }

    /**
     * Asigna la lista listaReferencia
     * 
     * @param listaReferencia
     * Variable a asignar en listaReferencia
     */
    public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
        this.listaReferencia = listaReferencia;
    }

    /**
     * Retorna la lista listaReferencia
     * 
     * @return listaReferencia
     */
    public RegistroDataModelImpl getListaReferenciaE() {
        return listaReferenciaE;
    }

    /**
     * Asigna la lista listaReferencia
     * 
     * @param listaReferencia
     * Variable a asignar en listaReferencia
     */
    public void setListaReferenciaE(RegistroDataModelImpl listaReferenciaE) {
        this.listaReferenciaE = listaReferenciaE;
    }

    /**
     * Retorna la lista listaFuenteRecurso
     * 
     * @return listaFuenteRecurso
     */
    public RegistroDataModelImpl getListaFuenteRecurso() {
        return listaFuenteRecurso;
    }

    /**
     * Asigna la lista listaFuenteRecurso
     * 
     * @param listaFuenteRecurso
     * Variable a asignar en listaFuenteRecurso
     */
    public void setListaFuenteRecurso(
        RegistroDataModelImpl listaFuenteRecurso) {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }

    /**
     * Retorna la lista listaFuenteRecurso
     * 
     * @return listaFuenteRecurso
     */
    public RegistroDataModelImpl getListaFuenteRecursoE() {
        return listaFuenteRecursoE;
    }

    /**
     * Asigna la lista listaFuenteRecurso
     * 
     * @param listaFuenteRecurso
     * Variable a asignar en listaFuenteRecurso
     */
    public void setListaFuenteRecursoE(
        RegistroDataModelImpl listaFuenteRecursoE) {
        this.listaFuenteRecursoE = listaFuenteRecursoE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    public RegistroDataModelImpl getListaESTRATOC() {
        return listaESTRATOC;
    }

    public void setListaESTRATOC(RegistroDataModelImpl listaESTRATOC) {
        this.listaESTRATOC = listaESTRATOC;
    }

    public RegistroDataModelImpl getListaESTRATOCE() {
        return listaESTRATOCE;
    }

    public void setListaESTRATOCE(RegistroDataModelImpl listaESTRATOCE) {
        this.listaESTRATOCE = listaESTRATOCE;
    }
    
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getDesTercero() {
        return desTercero;
    }

    public void setDesTercero(String desTercero) {
        this.desTercero = desTercero;
    }
    
    public String getDesVendedor() {
        return desVendedor;
    }

    public void setDesVendedor(String desVendedor) {
        this.desVendedor = desVendedor;
    }

    public String getDesCentroCosto() {
        return desCentroCosto;
    }

    public void setDesCentroCosto(String desCentroCosto) {
        this.desCentroCosto = desCentroCosto;
    }

    public String getDesAuxiliar() {
        return desAuxiliar;
    }

    public void setDesAuxiliar(String desAuxiliar) {
        this.desAuxiliar = desAuxiliar;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(String valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public String getValorBase() {
        return valorBase;
    }

    public void setValorBase(String valorBase) {
        this.valorBase = valorBase;
    }

    public String getValorIVA() {
        return valorIVA;
    }

    public void setValorIVA(String valorIVA) {
        this.valorIVA = valorIVA;
    }

    public String getValorRete() {
        return valorRete;
    }

    public void setValorRete(String valorRete) {
        this.valorRete = valorRete;
    }

    public String getValorDescuento() {
        return valorDescuento;
    }

    public void setValorDescuento(String valorDescuento) {
        this.valorDescuento = valorDescuento;
    }

    public String getValorICA() {
        return valorICA;
    }

    public void setValorICA(String valorICA) {
        this.valorICA = valorICA;
    }

    public String getValorNeto() {
        return valorNeto;
    }

    public void setValorNeto(String valorNeto) {
        this.valorNeto = valorNeto;
    }

    public String getValorImpoconsumo() {
        return valorImpoconsumo;
    }

    public void setValorImpoconsumo(String valorImpoconsumo) {
        this.valorImpoconsumo = valorImpoconsumo;
    }

    /**
     * Retorna la lista listaTERCEROG
     * 
     * @return listaTERCEROG
     */
    public RegistroDataModelImpl getListaTERCEROG() {
        return listaTERCEROG;
    }

    /**
     * Asigna la lista listaTERCEROG
     * 
     * @param listaTERCEROG
     * Variable a asignar en listaTERCEROG
     */
    public void setListaTERCEROG(RegistroDataModelImpl listaTERCEROG) {
        this.listaTERCEROG = listaTERCEROG;
    }
    
    /**
     * Retorna la lista listaVENDEDOR
     * 
     * @return listaVENDEDOR
     */
    public RegistroDataModelImpl getListaVENDEDOR() {
        return listaVENDEDOR;
    }
    
    /**
     * Asigna la lista listaVENDEDOR
     * 
     * @param listaVENDEDOR
     * Variable a asignar en listaVENDEDOR
     */
    public void setListaVENDEDOR(RegistroDataModelImpl listaVENDEDOR) {
        this.listaVENDEDOR = listaVENDEDOR;
    }

 


    /**
     * Retorna la lista listacentroCostoG
     * 
     * @return listacentroCostoG
     */
    public RegistroDataModelImpl getListacentroCostoG() {
        return listacentroCostoG;
    }

    /**
     * Asigna la lista listacentroCostoG
     * 
     * @param listacentroCostoG
     * Variable a asignar en listacentroCostoG
     */
    /**
     * Retorna la lista listaAFECTARCOTIZACIONG
     * 
     * @return listaAFECTARCOTIZACIONG
     */
    public RegistroDataModel getListaAFECTARCOTIZACIONG() {
        return listaAFECTARCOTIZACIONG;
    }

    /**
     * Asigna la lista listaAFECTARCOTIZACIONG
     * 
     * @param listaAFECTARCOTIZACIONG
     * Variable a asignar en listaAFECTARCOTIZACIONG
     */
    public void setListaAFECTARCOTIZACIONG(
        RegistroDataModel listaAFECTARCOTIZACIONG) {
        this.listaAFECTARCOTIZACIONG = listaAFECTARCOTIZACIONG;
    }

    /**
     * Retorna la lista listaAUXILIARG
     * 
     * @return listaAUXILIARG
     */
    public RegistroDataModelImpl getlistaAUXILIARG() {
        return listaAUXILIARG;
    }

    /**
     * Asigna la lista listaAUXILIARG
     * 
     * @param listaAUXILIARG
     * Variable a asignar en listaAUXILIARG
     */
    public void setlistaAUXILIARG(RegistroDataModelImpl listaAUXILIARG) {
        this.listaAUXILIARG = listaAUXILIARG;
    }

    /**
     * Retorna la lista listaCodigoLocal
     * 
     * @return listaCodigoLocal
     */
    public RegistroDataModelImpl getListaCodigoLocal() {
        return listaCodigoLocal;
    }

    /**
     * Asigna la lista listaCodigoLocal
     * 
     * @param listaCodigoLocal
     * Variable a asignar en listaCodigoLocal
     */
    public void setListaCodigoLocal(RegistroDataModelImpl listaCodigoLocal) {
        this.listaCodigoLocal = listaCodigoLocal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    public List<Registro> getListaSubconceptoscontrato() {
        return listaSubconceptoscontrato;
    }

    public void setListaSubconceptoscontrato(
        List<Registro> listaSubconceptoscontrato) {
        this.listaSubconceptoscontrato = listaSubconceptoscontrato;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public int getIndiceSubconceptoscontrato() {
        return indiceSubconceptoscontrato;
    }

    public void setIndiceSubconceptoscontrato(int indiceSubconceptoscontrato) {
        this.indiceSubconceptoscontrato = indiceSubconceptoscontrato;
    }

	/**
	 * @return the listacentroUtilidad
	 */
	public RegistroDataModelImpl getListacentroUtilidad() {
		return listacentroUtilidad;
	}

	/**
	 * @param listacentroUtilidad the listacentroUtilidad to set
	 */
	public void setListacentroUtilidad(RegistroDataModelImpl listacentroUtilidad) {
		this.listacentroUtilidad = listacentroUtilidad;
	}

	/**
	 * @return the listacentroUtilidadE
	 */
	public RegistroDataModelImpl getListacentroUtilidadE() {
		return listacentroUtilidadE;
	}

	/**
	 * @param listacentroUtilidadE the listacentroUtilidadE to set
	 */
	public void setListacentroUtilidadE(RegistroDataModelImpl listacentroUtilidadE) {
		this.listacentroUtilidadE = listacentroUtilidadE;
	}
	
	public boolean isVisibleCentroUtilidad() {
		return visibleCentroUtilidad;
	}

	public void setVisibleCentroUtilidad(boolean visibleCentroUtilidad) {
		this.visibleCentroUtilidad = visibleCentroUtilidad;
	}

	/**
	 * @return the listaTipoPago
	 */
	public RegistroDataModelImpl getListaTipoPago() {
		return listaTipoPago;
	}

	/**
	 * @param listaTipoPago the listaTipoPago to set
	 */
	public void setListaTipoPago(RegistroDataModelImpl listaTipoPago) {
		this.listaTipoPago = listaTipoPago;
	}

	public RegistroDataModelImpl getListanumeroCotizacion() {
		return listanumeroCotizacion;
	}

	public void setListanumeroCotizacion(RegistroDataModelImpl listanumeroCotizacion) {
		this.listanumeroCotizacion = listanumeroCotizacion;
	}

	public Boolean getVisibleCotizaContrato() {
		return visibleCotizaContrato;
	}

	public void setVisibleCotizaContrato(Boolean visibleCotizaContrato) {
		this.visibleCotizaContrato = visibleCotizaContrato;
	}

	public RegistroDataModelImpl getListainmueble() {
		return listainmueble;
	}

	public void setListainmueble(RegistroDataModelImpl listainmueble) {
		this.listainmueble = listainmueble;
	}

	public RegistroDataModelImpl getListainmuebleE() {
		return listainmuebleE;
	}

	public void setListainmuebleE(RegistroDataModelImpl listainmuebleE) {
		this.listainmuebleE = listainmuebleE;
	}

	public RegistroDataModelImpl getListadestinacion() {
		return listadestinacion;
	}

	public void setListadestinacion(RegistroDataModelImpl listadestinacion) {
		this.listadestinacion = listadestinacion;
	}

	public String getVendedorLabel() {
		return vendedorLabel;
	}

	public void setVendedorLabel(String vendedorLabel) {
		this.vendedorLabel = vendedorLabel;
	}

	public String getNomRepresentanteLegal() {
		return nomRepresentanteLegal;
	}

	public void setNomRepresentanteLegal(String nomRepresentanteLegal) {
		this.nomRepresentanteLegal = nomRepresentanteLegal;
	}

	public RegistroDataModelImpl getListarepresentanteLegal() {
		return listarepresentanteLegal;
	}

	public void setListarepresentanteLegal(RegistroDataModelImpl listarepresentanteLegal) {
		this.listarepresentanteLegal = listarepresentanteLegal;
	}

	public RegistroDataModelImpl getListaORDENADORGASTO() {
		return listaORDENADORGASTO;
	}

	public void setListaORDENADORGASTO(RegistroDataModelImpl listaORDENADORGASTO) {
		this.listaORDENADORGASTO = listaORDENADORGASTO;
	}
	
	public RegistroDataModelImpl getListaPlantillas() {
        return listaPlantillas;
    }
	
	public void setListaPlantillas(RegistroDataModelImpl listaPlantillas) {
        this.listaPlantillas = listaPlantillas;
    }

	public String getNombreordenadorgasto() {
		return nombreordenadorgasto;
	}

	public void setNombreordenadorgasto(String nombreordenadorgasto) {
		this.nombreordenadorgasto = nombreordenadorgasto;
	}
	
	public String getPlantilla() {
        return plantilla;
    }
	
	public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }
    // </SET_GET_ADICIONALES>
}
