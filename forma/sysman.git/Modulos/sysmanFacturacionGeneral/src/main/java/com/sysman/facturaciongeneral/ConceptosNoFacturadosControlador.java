/*-
 * ConceptosNoFacturadosControlador.java
 *
 * 1.0
 *
 * 22/11/2017
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
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralDosRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralTresRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralUnoRemote;
import com.sysman.facturaciongeneral.enums.ConceptosNoFacturadosControladorEnum;
import com.sysman.facturaciongeneral.enums.ConceptosNoFacturadosControladorUrlEnum;
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
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
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
 * Formulario que permite realizar el recaudo por tercero
 *
 * @version 1.0, 22/11/2017
 * @author amonroy
 *
 * @version 2.0, 09/10/2018, Proceso de Refactoring DSS y cambio en la
 * logica de negocio
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ConceptosNoFacturadosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el codigo del modulo en el que se esta
     * trabajando actualmente
     */
    private final String modulo;
    /**
     * Tipo de cobro que ha sido selecionado al ingresar al modulo de
     * Facturacion General
     */
    private String tipoCobro;
    /**
     * Anio de trabajo que ha sido seleccionado al ingresar al modulo
     * de Facturacion General
     */
    private String anio;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Almacena el valor total neto por concepto
     */
    private String totalNeto;
    /**
     * Atributo que almacena el valor del total del campo "Valor
     * Unidad" en el subformulario SubConceptosNoFacturadosBg
     */
    private String totalValorUnidad;

    /**
     * Atributo que almacena el valor del total del campo "Valor
     * Unidad" sin formato de moneda
     */
    private Double totalValorUnidadAux;
    /**
     * Atributo que almacena el valor del total del campo "Valor" en
     * el subformulario SubConceptosNoFacturadosBg
     */
    private String totalValor;
    /**
     * Atributo que almacena el valor del total del campo "Total" en
     * el subformulario SubConceptosNoFacturadosBg
     */
    private String totalNetoPie;

    /**
     * Atributo que almacena el valor del total del campo "Total" sin
     * formato moneda
     *
     */
    private Double totalNetoPieAux;

    /**
     * Atributo que almacena el valor del total del campo "Retefuente"
     * en el subformulario SubConceptosNoFacturadosBg
     */
    private String totalRetefuente;
    /**
     * Atributo que almacena el valor del total del campo "Descuento"
     * en el subformulario SubConceptosNoFacturadosBg
     */
    private String totalDescuento;
    /**
     * Atributo que almacena el valor del total del campo "ICA" en el
     * subformulario SubConceptosNoFacturadosBg
     */
    private String totalIca;
    /**
     * Atributo que almacena el valor del total del campo "IVA" en el
     * subformulario SubConceptosNoFacturadosBg
     */
    private String totalIva;
    /**
     * Indica el concepto seleccionado posee cuenta de recaudo
     * configurada
     */
    private boolean poseeCuentaRecaudo;
    /**
     * Numero de cuenta de recaudo cuando el concepto que se esta
     * trabajando posee configurada esta cuenta
     */
    private String cuentaRecaudo;

    /**
     * Almacena la sucursal del tercero que ha sido seleccionado en el
     * subformulario
     */
    private String sucursalTerceroConcepto;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Indicador que obtiene el valor del parametro "SF FECHA
     * VENCIMIENTO MANUAL", permite visualizar el campo de "Fecha
     * Vencimiento"
     */
    private boolean visibleFechaVencimiento;
    /**
     * Indicador que obtiene el valor del parametro "SF OBLIGA A BASE
     * GRAVABLE", permite visualizar el campo de "Base Gravable" en el
     * subformulario SubconceptosNoFacturadosBg
     */
    private boolean visibleBaseGravable;
    /**
     * Indicador que obtiene el valor del parametro "SF MANEJA
     * CONSECUTIVO DELINEACION URBANA", permite a visualizacion del
     * campo"Nro. Formulario Delineación Urbana"
     */
    private boolean manejaDelineacion;
    /**
     * Indicador que obtiene el valor del parametro "SF FACTURA A
     * TERCERO NO REGISTRADOS EN EL SISTEMA",permite visualizar los
     * campos de "Facturar a"
     */
    private boolean terceroNoRegistrado;
    /**
     * Indicador que obtiene el valor del parametro "SF PERMITE
     * DIGITAR NRO FACTURA", afecta el bloqueo del campo "Nro
     * Documento"
     */
    private boolean bloqueaNroFactura;
    /**
     * Indicador para bloquear los campos del Tercero a Facturar
     */
    private boolean bloqueadoFacturado;
    /**
     * Indicador para bloquear el campo de "Valor" en el Subformulario
     */
    private boolean bloqueadoValorBase;
    /**
     * Indicador para bloquear el campo de "Cantidad" en el
     * Subformulario
     */
    private boolean bloqueadoCantidad;
    /**
     * Almacena el valor del parametro "SF REDONDEO VALOR"
     */
    private int digitoRedondeo;
    /**
     * Almacena el valor del parametro "SF REDONDEO DE IVA"
     */
    private int redondeoIva;

    /**
     * Variable encargada de almacenar temporalmente si aplica
     * preliquidacion
     */
    private boolean indPreliquidacion;

    /**
     * Atributo que indica si el tipo de cobro seleccionado maneja
     * inventario
     */
    private boolean indManejaInventario;

    /**
     * Almacena el valor del parametro "SF REDONDEO DE ICA"
     */
    private int redondeoICA;
    /**
     * Almacena el valor del parametro "SF REDONDEO DEL DESCUENTO"
     */
    private int redondeoDescuento;
    /**
     * Almacena la informacion del concepto que ha sido seleccionado
     */
    private Registro conceptoActual;

    /**
     * Variable que almacena el valor de la tarifa seleccionada en el
     * detalle de cada concepto
     */
    private double vlrTarifa;

    /**
     * Atributo que almacena el nurvo valor de la base fija en la
     * grill
     */
    private double auxiliarBaseFija;

    /**
     * Variable que indica si el cobro ya fue facturado para habilitar
     * edición de formulario
     */
    private boolean facturado;

    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Implementacion del EJB de EjbFacturacionGeneralCeroRemote para
     * hacer el llamado a las funciones que se invocan dentro del
     * Controlador y se encuentran almacenadas en el paquete
     * PCK_FACT_GENERAL
     */
    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFacturacionGeneralCero;
    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFactGenCero;
    @EJB
    private EjbFacturacionGeneralUnoRemote ejbFactGenUno;
    @EJB
    private EjbFacturacionGeneralDosRemote ejbFactGenDos;
    @EJB
    private EjbFacturacionGeneralTresRemote ejbFactGenTres;
    @EJB
    private EjbFacturacionGeneralCuatroRemote ejbFactGenCuatro;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los grupos de conceptos
     */
    private List<Registro> listaGrupoConceptos;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que carga los conceptos en el detalle
     */
    private RegistroDataModelImpl listaConcepto;
    /**
     * Lista que carga los conceptos en el detalle
     */
    private RegistroDataModelImpl listaConceptoE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Atributo que indica si se esta cambiando la base fija en la
     * grilla
     */
    private boolean cambioBaseFija;

    /**
     * Lista que carga los terceros a facturar
     */
    private RegistroDataModelImpl listaNitTercero;
    /**
     * Lista que carga los centro de costo a facturar
     */
    private RegistroDataModelImpl listaCentroCosto;
    /**
     * Lista que carga los auxiliares a facturar
     */
    private RegistroDataModelImpl listaAuxiliar;
    /**
     * Lista que carga los contratos a facturar
     */
    private RegistroDataModel listaContratoAfectar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista que carga los conceptos a facturar
     */
    private List<Registro> listaSubconceptosnofacturadosbg;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ConceptosNoFacturadosControlador
     */
    public ConceptosNoFacturadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());

        anio = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());

        indPreliquidacion = (boolean) SessionUtil
                        .getSessionVar(ConstantesFacturacionGenEnum.INDPRELIQUIDACION
                                        .getValue());

        indManejaInventario = (boolean) SessionUtil
                        .getSessionVar(ConstantesFacturacionGenEnum.MANEJA_INVENTARIO
                                        .getValue());

        bloqueadoValorBase = false;

        facturado = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.CONCEPTOS_NO_FACTURADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            conceptoActual = new Registro(new HashMap<String, Object>());

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

        cargarListaConcepto();
        cargarListaConceptoE();

        cargarListaNitTercero();
        cargarListaCentroCosto();
        cargarListaAuxiliar();
        cargarListaContratoAfectar();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaGrupoConceptos();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubconceptosnofacturadosbg();
        evaluarConCuentaRecaudo();
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
        listaSubconceptosnofacturadosbg = null;
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
        enumBase = GenericUrlEnum.SF_OBJETO_COBRO;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put("TIPO_COBRO", tipoCobro);
    }

    /**
     *
     * Carga la lista listaSubconceptosnofacturadosbg
     *
     */
    public void cargarListaSubconceptosnofacturadosbg() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                            tipoCobro);

            param.put("CODIGOCOBRO",
                            registro.getCampos().get(
                                            ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                                            .getValue()));
            param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()));

            listaSubconceptosnofacturadosbg = RegistroConverter
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
        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        calcularTotales();
    }

    // <METODOS_CARGAR_LISTA>

    /**
     *
     * Carga la lista listaGrupoConceptos
     *
     */
    public void cargarListaGrupoConceptos() {
        //

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);

        try {
            listaGrupoConceptos = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ConceptosNoFacturadosControladorUrlEnum.URL14101
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaConcepto
     *
     */
    public void cargarListaConcepto() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosNoFacturadosControladorUrlEnum.URL15077
                                                        .getValue());

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaConcepto
     *
     */
    public void cargarListaConceptoE() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        param.put("POSEECUENTA", poseeCuentaRecaudo ? 1 : 0);
        param.put("CUENTA", cuentaRecaudo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosNoFacturadosControladorUrlEnum.URL17965
                                                        .getValue());
        listaConceptoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaNitTercero
     *
     */
    public void cargarListaNitTercero() {
        //

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosNoFacturadosControladorUrlEnum.URL24417
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNitTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    /**
     *
     * Carga la lista listaCentroCosto
     *
     */
    public void cargarListaCentroCosto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosNoFacturadosControladorUrlEnum.URL25342
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaAuxiliar
     *
     */
    public void cargarListaAuxiliar() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosNoFacturadosControladorUrlEnum.URL26250
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaContratoAfectar
     *
     */
    public void cargarListaContratoAfectar() {
        listaContratoAfectar = new RegistroDataModel(
                        ConectorPool.ESQUEMA_SYSMAN, ":FR1467_nuevo:TBCB4814",
                        "SELECT (SF_CONTRATOS.TIPO|| ' - '|| SF_CONTRATOS.NUMERO) CONTRATO,"
                            + "   SF_CONTRATOS.FECHA_INICIO,"
                            + "   SF_CONTRATOS.FECHA_FINALIZACION,"
                            + "   SF_CONTRATOS.TERCERO,"
                            + "   SF_CONTRATOS.VALOR_TOTAL,"
                            + "   SF_CONTRATOS.OBJETO_CONTRATO,"
                            + "   SF_CONTRATOS.TIPO,"
                            + "   TO_CHAR(SF_CONTRATOS.NUMERO) NUMERO,"
                            + "   SF_CONTRATOS.ESTADO,"
                            + "   SF_CONTRATOS.ANO,  "
                            + "   SF_CONTRATOS.SUCURSAL,"
                            + "   SF_CONTRATOS.CENTRO_COSTO,"
                            + "   SF_CONTRATOS.AUXILIAR,"
                            + "   CENTRO_COSTO.NOMBRE NOMBRE_CENTRO_COSTO,"
                            + "   AUXILIAR.NOMBRE NOMBREAUXILIAR,"
                            + "   TERCERO.NOMBRE NOMBRE_TERCERO,"
                            + "   TERCERO.DIRECCION," + "   TERCERO.TELEFONOS,"
                            + "   TERCERO.CODIGOPOSTAL"
                            + " FROM SF_CONTRATOS" + " LEFT JOIN CENTRO_COSTO"
                            + " ON SF_CONTRATOS.COMPANIA      = CENTRO_COSTO.COMPANIA"
                            + " AND SF_CONTRATOS.CENTRO_COSTO = CENTRO_COSTO.CODIGO"
                            + " AND SF_CONTRATOS.ANO          = CENTRO_COSTO.ANO"
                            + " LEFT JOIN AUXILIAR"
                            + " ON SF_CONTRATOS.COMPANIA  = AUXILIAR.COMPANIA"
                            + " AND SF_CONTRATOS.ANO      = AUXILIAR.ANO"
                            + " AND SF_CONTRATOS.AUXILIAR = AUXILIAR.CODIGO"
                            + " LEFT JOIN TERCERO"
                            + " ON SF_CONTRATOS.COMPANIA    = TERCERO.COMPANIA"
                            + " AND SF_CONTRATOS.TERCERO    = TERCERO.NIT"
                            + " AND SF_CONTRATOS.SUCURSAL   = TERCERO.SUCURSAL"
                            + " WHERE SF_CONTRATOS.COMPANIA = '"
                            + compania + "' "
                            + " AND SF_CONTRATOS.ESTADO     = 2",
                        true, ConceptosNoFacturadosControladorEnum.CONTRATO
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control BaseFija
     *
     */
    public void cambiarBaseFija() {
        // <CODIGO_DESARROLLADO>

        obtenerRegistroConceptos(registroSub.getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName()));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control BaseFija en la fila
     * seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarBaseFijaC(int rowNum) {
        cambioBaseFija = true;

        auxiliarBaseFija = SysmanFunciones.nvlDbl(
                        listaSubconceptosnofacturadosbg.get(rowNum).getCampos()
                                        .get(ConceptosNoFacturadosControladorEnum.BASE_FIJA
                                                        .getValue()),
                        0.0);

        obtenerRegistroConceptos(
                        listaSubconceptosnofacturadosbg.get(rowNum).getCampos()
                                        .get(GeneralParameterEnum.CONCEPTO
                                                        .getName()));

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                        .getValue());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_COMPRA
                                        .getValue());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                        .getValue());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_NETO
                                        .getValue());

        cambioBaseFija = false;
    }

    /**
     * Metodo ejecutado al cambiar el control Cantidad
     *
     * Al cambiar la cantidad del concepto, se deben actualizar
     * diferentes valores asocidos al registro del sub, por esta razon
     * se realiza el llamado al metodo inicializarValoresSub()
     *
     */
    public void cambiarCantidad() {
        // <CODIGO_DESARROLLADO>
        String conceptoAux = registroSub.getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName())
                        .toString();

        obtenerRegistroConceptos(conceptoAux);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Concepto en la fila
     * seleccionada dentro de la grilla
     *
     * Evalua si el concepto seleccionado afecta inventario y realiza
     * la asignacion de valores al listado del registro que se esta
     * trabajando
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarConceptoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        evaluarAfectacionInventario(conceptoActual);

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        GeneralParameterEnum.NOMBRE.getName());
        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                        .getValue());
        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                        .getValue());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_DESCUENTO
                                        .getValue());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_IVA
                                        .getValue());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_RETEFUENTE
                                        .getValue());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_ICA
                                        .getValue());
        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        ConceptosNoFacturadosControladorEnum.VALOR_NETO
                                        .getValue());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        GeneralParameterEnum.CENTRO_COSTO.getName());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        GeneralParameterEnum.AUXILIAR.getName());

        asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                        GeneralParameterEnum.TERCERO.getName());
        validarCuentaRecaudo(conceptoActual);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TerceroConcepto en la
     * fila seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTerceroConcepto() {

    }

    public void cambiarTerceroConceptoC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        listaSubconceptosnofacturadosbg.get(rowNum).getCampos().put(
                        GeneralParameterEnum.SUCURSAL.getName(),
                        sucursalTerceroConcepto);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Cantidad en la fila
     * seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCantidadC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        int cantidad = Integer.parseInt(listaSubconceptosnofacturadosbg
                        .get(rowNum).getCampos()
                        .get(GeneralParameterEnum.CANTIDAD.getName())
                        .toString());

        if (validarCantidad(cantidad)) {

            registroSub.getCampos().put(
                            ConceptosNoFacturadosControladorEnum.BASE_FIJA
                                            .getValue(),
                            listaSubconceptosnofacturadosbg.get(rowNum)
                                            .getCampos()
                                            .get(ConceptosNoFacturadosControladorEnum.BASE_FIJA
                                                            .getValue()));

            String conceptoAux = listaSubconceptosnofacturadosbg.get(rowNum)
                            .getCampos()
                            .get(GeneralParameterEnum.CONCEPTO.getName())
                            .toString();

            registroSub.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(),
                            listaSubconceptosnofacturadosbg
                                            .get(rowNum).getCampos()
                                            .get(GeneralParameterEnum.CANTIDAD
                                                            .getName()));
            obtenerRegistroConceptos(conceptoAux);

            asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                            ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                            .getValue());

            asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                            ConceptosNoFacturadosControladorEnum.VALOR_COMPRA
                                            .getValue());

            asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                            ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                            .getValue());

            asignarListado(listaSubconceptosnofacturadosbg.get(rowNum),
                            ConceptosNoFacturadosControladorEnum.VALOR_NETO
                                            .getValue());
        }
        else {
            cancelarEdicionSubconceptosnofacturadosbg();
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoActual = registroAux;

        registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registroSub.getCampos().put(
                        ConceptosNoFacturadosControladorEnum.NOMBRECONCEPTO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

        registroSub.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(),
                        registro.getCampos().get(
                                        ConceptosNoFacturadosControladorEnum.CANTIDAD_GRUPO
                                                        .getValue()));

        registroSub.getCampos().put(
                        ConceptosNoFacturadosControladorEnum.BASE_FIJA
                                        .getValue(),
                        registro.getCampos().get("BASE_FIJA_ANT"));

        registroSub.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.TERCERO
                                        .getName()));

        registroSub.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registro.getCampos().get(GeneralParameterEnum.SUCURSAL
                                        .getName()));

        obtenerRegistroConceptos(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    private void obtenerRegistroConceptos(Object concepto) {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(), anio);
        params.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        params.put(GeneralParameterEnum.CODIGO.getName(), concepto);

        try {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ConceptosNoFacturadosControladorUrlEnum.URL28509
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

            if (!(boolean) reg.getCampos().get("AFECTAINVENTARIO")) {

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.EXISTENCIA
                                                .getValue(),
                                999);
            }
            else {

                Long existencia = ejbFactGenCero.insertarElementoConcepto(
                                compania, Integer.parseInt(anio), tipoCobro,
                                retornarString(reg,
                                                GeneralParameterEnum.CODIGO
                                                                .getName()),
                                retornarDouble(reg, " PORCETAJE_UTILIDAD"),
                                SessionUtil.getUser().getCodigo());

                if (existencia == -1) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3816"));
                    registroSub.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.EXISTENCIA
                                                    .getValue(),
                                    0);
                }
                else {
                    registroSub.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.EXISTENCIA
                                                    .getValue(),
                                    existencia);
                }
            }

            int digRedondeo = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_BASE").toString());

            double cantidad = Double.parseDouble(SysmanFunciones
                            .nvl(registroSub.getCampos()
                                            .get(GeneralParameterEnum.CANTIDAD
                                                            .getName()),
                                            "0")
                            .toString());
            if (digRedondeo == 0) {
                asignarValoresRegistro(
                                ConceptosNoFacturadosControladorEnum.VALOR_COMPRA
                                                .getValue(),
                                0);
            }
            else {

                double valorCompraAux = ((retornarConFix(
                                (retornarDouble(reg,
                                                ConceptosNoFacturadosControladorEnum.VALOR_COMPRA
                                                                .getValue())
                                    / digRedondeo) + 0.501,
                                digRedondeo)
                    * retornarDouble(registroSub,
                                    GeneralParameterEnum.CANTIDAD.getName()))
                    / digRedondeo) + 0.501;

                double valorCompra = retornarConFix(valorCompraAux,
                                digRedondeo);

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_COMPRA
                                                .getValue(),
                                valorCompra);

            }

            if ((boolean) reg.getCampos().get(
                            ConceptosNoFacturadosControladorEnum.PERMITEMODIFICARVALOR
                                            .getValue())) {

                if (Double.doubleToRawLongBits(retornarDouble(registroSub,
                                ConceptosNoFacturadosControladorEnum.VALOR_UNIDAD
                                                .getValue())) == 0) {

                    validarValorUnitarioValorBase(reg, digRedondeo, cantidad);
                }
                else {

                    double valorUnitario = retornarDouble(registroSub,
                                    ConceptosNoFacturadosControladorEnum.VALOR_UNIDAD
                                                    .getValue());

                    registroSub.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                                    .getValue(),
                                    valorUnitario);

                    double valorBase = retornarConFix(
                                    ((valorUnitario * cantidad) / digRedondeo)
                                        + 0.501,
                                    digRedondeo);

                    registroSub.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                    .getValue(),
                                    valorBase);

                }

            }
            else if (Double.doubleToRawLongBits(
                            retornarDouble(registroSub,
                                            ConceptosNoFacturadosControladorEnum.VALOR_UNIDAD
                                                            .getValue())) == 0) {
                double valorUnitarioAux = retornarConFix(
                                (retornarDouble(reg,
                                                ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                                .getValue())
                                    / digRedondeo)
                                    + 0.501,
                                digRedondeo);

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                                .getValue(),
                                valorUnitarioAux);

                double valorBaseAux = retornarConFix(
                                ((retornarDouble(reg,
                                                ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                                .getValue())
                                    * cantidad)
                                    / digRedondeo) + 0.501,
                                digRedondeo);

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                .getValue(),
                                valorBaseAux);

            }
            else {

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                                .getValue(),
                                registroSub.getCampos().get(
                                                ConceptosNoFacturadosControladorEnum.VALOR_UNIDAD
                                                                .getValue()));

                double valorBaseAux = retornarConFix(
                                ((retornarDouble(registroSub,
                                                ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                                                .getValue())
                                    * cantidad) / digRedondeo) + 0.501,
                                digRedondeo);

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                .getValue(),
                                valorBaseAux);
            }

            registroSub.getCampos().put("CUENTA_BANCO",
                            reg.getCampos().get(
                                            ConceptosNoFacturadosControladorEnum.CUENTA_RECAUDO
                                                            .getValue()));

            hallarValorBaseAplicaFormula(reg);
            hallarValorDescuento(reg);
            hallarValorIva(reg);
            hallarValorReteFuente(reg);
            hallarValorIca(reg);
            hallarValorNetoUnitarioUnidad(reg);
            asignarValorCreeSub(reg);

        }
        catch (NumberFormatException | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void validarValorUnitarioValorBase(Registro reg, int digRedondeo,
        double cantidad) {

        try {

            double valorBaseConcepto = retornarDouble(reg,
                            ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                            .getValue());
            double valorBase = 0;
            double valorUnitario;
            if (digRedondeo == 0) {
                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                .getValue(),
                                valorBase);
            }
            else {

                valorBase = retornarConFix(
                                ((retornarDouble(reg,
                                                ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                                                .getValue())
                                    * cantidad) / digRedondeo) + 0.501,
                                digRedondeo);

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                .getValue(),
                                valorBase);

            }

            if (Double.doubleToRawLongBits(valorBase) == 0) {
                if (digRedondeo == 0) {

                    registroSub.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                                    .getValue(),
                                    0);
                    registroSub.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                    .getValue(),
                                    0);
                }
                else {

                    valorUnitario = retornarConFix(
                                    (valorBaseConcepto / digRedondeo) + 0.501,
                                    digRedondeo);

                    registroSub.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                                    .getValue(),
                                    valorUnitario);

                    valorBase = retornarConFix(((valorBaseConcepto * cantidad)
                        / digRedondeo) + 0.501, digRedondeo);
                    registroSub.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                    .getValue(),
                                    valorBase);

                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorBaseAplicaFormula(Registro reg) {
        try {

            double valorBaseFijo;
            double cantidad = Double.parseDouble(SysmanFunciones
                            .nvl(registroSub.getCampos()
                                            .get(GeneralParameterEnum.CANTIDAD
                                                            .getName()),
                                            "1")
                            .toString());

            if (cambioBaseFija) {

                valorBaseFijo = auxiliarBaseFija;

            }
            else {

                valorBaseFijo = Double.parseDouble(registroSub.getCampos()
                                .get(ConceptosNoFacturadosControladorEnum.BASE_FIJA
                                                .getValue())
                                .toString());
            }

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
            if ((boolean) reg.getCampos().get("APLICAFORMULA")) {

                BigDecimal valorBaseUnitario = ejbFactGenTres
                                .reemplazarFormula(formula);

                valorBaseUnitario = ejbFactGenUno
                                .cargarValorConceptoCant(Double.parseDouble(
                                                valorBaseUnitario.toString()),
                                                1, digRedondeo);

                double valorBase = valorBaseUnitario.doubleValue() * cantidad;

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                                .getValue(),
                                valorBase);
                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                                .getValue(),
                                valorBaseUnitario);

            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorDescuento(Registro reg) {
        try {
            BigDecimal valorDescuento;

            double porcentajeDescuento = Double.parseDouble(reg.getCampos()
                            .get("PORCENTAJEDESCUENTO").toString());

            boolean aplicaDescuento = (boolean) reg.getCampos()
                            .get("APLICADESCUENTO");

            double valorBase = retornarDouble(registroSub,
                            ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                            .getValue());

            valorBase = valorBase * (porcentajeDescuento / 100);
            int redDesc = Integer.parseInt(reg.getCampos()
                            .get("FACTOR_RED_DESCUENTO").toString());

            if ((Double.doubleToRawLongBits(valorBase) == 0)
                || (Double.doubleToRawLongBits(redDesc) == 0)) {

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_DESCUENTO
                                                .getValue(),
                                0);
            }

            if (aplicaDescuento) {

                valorDescuento = ejbFactGenUno.cargarValorConceptoIndica(
                                aplicaDescuento,
                                new BigDecimal(Double.toString(valorBase)),
                                redDesc);

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_DESCUENTO
                                                .getValue(),
                                valorDescuento);
            }

            else {
                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_DESCUENTO
                                                .getValue(),
                                0);
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
                            ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                            .getValue());
            double porcentajeIca = retornarDouble(reg, "PORCENTAJEICA");
            BigDecimal valorIca;

            if (aplicaIca) {

                valorBase = valorBase * (porcentajeIca / 100);

                valorIca = ejbFactGenUno.cargarValorConceptoIndica(aplicaIca,
                                new BigDecimal(Double.toString(valorBase)),
                                redIca);

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_ICA
                                                .getValue(),
                                valorIca);
            }
            else {
                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_ICA
                                                .getValue(),
                                "0");
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
                            ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                            .getValue());
            double valorDescuento = retornarDouble(reg,
                            ConceptosNoFacturadosControladorEnum.VALOR_DESCUENTO
                                            .getValue());

            double porcentajeIva = retornarDouble(reg,
                            ConceptosNoFacturadosControladorEnum.PORCENTAJEIVA
                                            .getValue());

            if (!aplicaIva || (Double.doubleToRawLongBits(digIva) == 0)) {
                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_IVA
                                                .getValue(),
                                0);
            }
            else {
                BigDecimal valorIva;

                valorBase = (valorBase - valorDescuento)
                    * (porcentajeIva / 100);

                valorIva = ejbFactGenUno.cargarValorConceptoIndica(aplicaIva,
                                new BigDecimal(Double.toString(valorBase)),
                                digIva);

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_IVA
                                                .getValue(),
                                valorIva);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void hallarValorNetoUnitarioUnidad(Registro reg) {

        double valorBase = retornarDouble(registroSub,
                        ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                        .getValue());
        double valorIva = retornarDouble(registroSub,
                        ConceptosNoFacturadosControladorEnum.VALOR_IVA
                                        .getValue());
        double valorRetefuente = retornarDouble(registroSub,
                        ConceptosNoFacturadosControladorEnum.VALOR_RETEFUENTE
                                        .getValue());
        double valorDescuento = retornarDouble(registroSub,
                        ConceptosNoFacturadosControladorEnum.VALOR_DESCUENTO
                                        .getValue());
        double valorIca = retornarDouble(registroSub,
                        ConceptosNoFacturadosControladorEnum.VALOR_ICA
                                        .getValue());
        double valorCompra = retornarDouble(registroSub,
                        ConceptosNoFacturadosControladorEnum.VALOR_COMPRA
                                        .getValue());

        double valorNetoAux = ((valorBase + valorIva + valorRetefuente)
            - valorDescuento) + valorIca;

        if (valorNetoAux < 0) {
            registroSub.getCampos()
                            .put(ConceptosNoFacturadosControladorEnum.VALOR_NETO
                                            .getValue(), 0);
        }
        else {
            registroSub.getCampos()
                            .put(ConceptosNoFacturadosControladorEnum.VALOR_NETO
                                            .getValue(), valorNetoAux);
        }

        double valorUtilidad = valorBase - valorCompra;
        registroSub.getCampos().put("VALOR_UTILIDAD", valorUtilidad);
        registroSub.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.VALOR_UNIDAD
                                        .getValue(), 0);

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

    private void hallarValorReteFuente(Registro reg) {

        try {
            int redRete = Integer.parseInt(
                            reg.getCampos().get("FACTOR_RED_RETE").toString());
            double valorBase = retornarDouble(registroSub,
                            ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                            .getValue());
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

                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_RETEFUENTE
                                                .getValue(),
                                valorRetefuente);
            }
            else {
                registroSub.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.VALOR_RETEFUENTE
                                                .getValue(),
                                0);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Evalua si el parametro "SF MANEJA CALCULO CREE" esta en si,
     * calcula el valor cree de acuerdo a la configuracion realizada
     * en el concepto y lo actualiza en la Base de Datos
     */
    private void asignarValorCreeSub(Registro reg) {
        try {
            boolean manejaCree = "SI".equalsIgnoreCase(
                            obtenerParametro("SF MANEJA CALCULO CREE", "NO"));

            if (manejaCree) {
                Map<String, Object> params = new TreeMap<>();
                params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                params.put(GeneralParameterEnum.ANO.getName(), anio);
                params.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO
                                .getValue(), tipoCobro);
                params.put(GeneralParameterEnum.CONCEPTO.getName(),
                                reg.getCampos().get(GeneralParameterEnum.CODIGO
                                                .getName()));

                Registro rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ConceptosNoFacturadosControladorUrlEnum.URL005
                                                                                                .getValue())
                                                                .getUrl(),
                                                params));
                if (rs != null) {
                    registroSub.getCampos().put("VALOR_CREE",
                                    rs.getCampos().get("CREE"));
                }

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private double retornarConFix(double operacion, int digRedondeo)
                    throws SystemException {

        BigDecimal valorBigD = BigDecimal.valueOf(operacion);
        valorBigD = ejbSysmanUtil.fix(valorBigD);

        return valorBigD.doubleValue() * digRedondeo;

    }

    private void asignarValoresRegistro(String campo, Object valor) {
        registro.getCampos().put(campo, valor);
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        conceptoActual = registroAux;
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        registroSub.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        retornarString(registroAux,
                                        GeneralParameterEnum.NOMBRE.getName()));
        registroSub.getCampos().put(GeneralParameterEnum.CANTIDAD.getName(), 1);

        bloqueadoValorBase = !(boolean) registroAux.getCampos()
                        .get(ConceptosNoFacturadosControladorEnum.PERMITEMODIFICARVALOR
                                        .getValue());
        bloqueadoCantidad = !(boolean) registroAux.getCampos()
                        .get("PERMITEMODIFICARCANTIDAD");

        inicializarValoresSub(registroAux);
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos().get("NIT"));
        registroSub.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, "NIT");
        sucursalTerceroConcepto = retornarString(registroAux,
                        GeneralParameterEnum.SUCURSAL.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCostoConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCostoConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNitTercero
     *
     * Cuando el tercero seleccionado es el de omision, se habilitan
     * los campos de "Facturar a"
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNitTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        String nitTercero = retornarString(registroAux, "NIT");
        String nombreTercero = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        nitTercero);

        registro.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.NOMBRETERCERO
                                        .getValue(), nombreTercero);
        registro.getCampos().put("DIRECCION",
                        registroAux.getCampos().get("DIRECCION"));
        registro.getCampos().put("CODIGOPOSTAL",
                        registroAux.getCampos().get("CODIGOPOSTAL"));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        registro.getCampos().put("NIT_TERCEROFACTURADO", nitTercero);
        registro.getCampos().put("NOMBRE_TERCEROFACTURADO", nombreTercero);

        setBloqueadoFacturado(
                        !SysmanConstantes.CONS_TERCERO.equals(nitTercero));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        String nombreCentro = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().put("NOMBRE_CENTRO_COSTO", nombreCentro);
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        String nombreAuxiliar = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

        registro.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.NOMBREAUXILIAR
                                        .getValue(), nombreAuxiliar);
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaContratoAfectar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaContratoAfectar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        ConceptosNoFacturadosControladorEnum.CONTRATO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        ConceptosNoFacturadosControladorEnum.CONTRATO
                                                        .getValue()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     *
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private double retornarDouble(Registro reg, String campo) {
        return Double.parseDouble(SysmanFunciones
                        .nvl(reg.getCampos().get(campo), "0").toString());
    }

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     *
     * @param rs
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return
     */
    private double retornarDoble(Registro rs, String campo) {
        return SysmanFunciones.validarCampoVacio(rs.getCampos(), campo) ? 0
            : Double.parseDouble(rs.getCampos().get(campo).toString());
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
        String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Obtiene el valor del parametro ingresado y retorna su valor de
     * tipo entero
     *
     * @param nombreParametro
     * Parametro a consultar en la Base de Datos
     * @param valorOmision
     * Valor por omision si en la BD no tiene registrado ningun valor
     * @return El valor del parametro
     */
    private int obtenerParametroRedondeo(String nombreParametro,
        int valorOmision) {
        return Integer.parseInt(obtenerParametro(nombreParametro,
                        String.valueOf(valorOmision)));
    }

    /**
     * Realiza las asignaciones iniciales cuando se va a realizar una
     * insercion
     */
    private void inicializarValores() {
        if (css == null) {
            if (Integer.parseInt(anio) < SysmanFunciones.ano(new Date())) {
                try {
                    registro.getCampos().put(
                                    ConceptosNoFacturadosControladorEnum.FECHA_SOLICITUD
                                                    .getValue(),
                                    SysmanFunciones.convertirAFecha(
                                                    "31/12/" + anio));
                    registro.getCampos().put("FECHA_VENCIMIENTO",
                                    SysmanFunciones.convertirAFecha(
                                                    "31/12/" + anio));
                }
                catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
            else {
                registro.getCampos().put(
                                ConceptosNoFacturadosControladorEnum.FECHA_SOLICITUD
                                                .getValue(),
                                new Date());
                registro.getCampos().put("FECHA_VENCIMIENTO", new Date());
            }

            registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                            SysmanConstantes.CONS_TERCERO);
            registro.getCampos().put(
                            ConceptosNoFacturadosControladorEnum.NOMBRETERCERO
                                            .getValue(),
                            "NINGUNO NADA");
            registro.getCampos().put("NIT_TERCEROFACTURADO",
                            SysmanConstantes.CONS_TERCERO);
            registro.getCampos().put("NOMBRE_TERCEROFACTURADO", "NINGUNO NADA");
            registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                            SysmanConstantes.CONS_AUXILIAR);
            registro.getCampos().put("NOMBRECENTRO", "NINGUNO");
            registro.getCampos().put(
                            GeneralParameterEnum.CENTRO_COSTO.getName(),
                            SysmanConstantes.CONS_CENTRO);
            registro.getCampos().put(
                            ConceptosNoFacturadosControladorEnum.NOMBREAUXILIAR
                                            .getValue(),
                            "VARIOS");

            String periodoContrato = SysmanFunciones.ano(new Date())
                + SysmanFunciones.strZero(Integer
                                .toString(SysmanFunciones.mes(new Date())), 2);
            registro.getCampos().put("PERIODOCONTRATO", periodoContrato);

        }
        setBloqueadoFacturado(!SysmanConstantes.CONS_TERCERO
                        .equals(retornarString(registro,
                                        GeneralParameterEnum.TERCERO
                                                        .getName())));
    }

    /**
     * De acuerdo a la configuracion realizada en el concepto
     * seleccionado, en el indicador "Afectar Almacen" se actualiza el
     * valor de la existencia
     *
     * @param registroConcepto
     * Informacion asociada a un concepto seleccionado
     */
    private void evaluarAfectacionInventario(Registro registroConcepto) {
        if (!(boolean) registroConcepto.getCampos().get("AFECTAINVENTARIO")) {
            registroConcepto.getCampos()
                            .put(ConceptosNoFacturadosControladorEnum.EXISTENCIA
                                            .getValue(), 999);
        }
        else {
            try {
                long existencia = ejbFacturacionGeneralCero
                                .insertarElementoConcepto(compania,
                                                Integer.parseInt(anio),
                                                tipoCobro,
                                                registroConcepto.getCampos()
                                                                .get(GeneralParameterEnum.CODIGO
                                                                                .getName())
                                                                .toString(),
                                                Double.parseDouble(
                                                                registroConcepto.getCampos()
                                                                                .get("PORCETAJE_UTILIDAD")
                                                                                .toString()),
                                                SessionUtil.getUser()
                                                                .getCodigo());

                if (existencia == -1) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB3816"));
                }
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Realiza la asignacion inicial a los valores del registro del
     * subformulario, debido a que la mayoria de estos valores depende
     * del concepto seleccioa¡nado
     *
     * @param registroConcepto
     * Registro con la informacion relacionada al concepto con el que
     * se esta trabajandos
     */
    private void inicializarValoresSub(Registro registroConcepto) {

        boolean modificarValor = (boolean) registroConcepto.getCampos()
                        .get(ConceptosNoFacturadosControladorEnum.PERMITEMODIFICARVALOR
                                        .getValue());
        boolean aplicaFormula = (boolean) registroConcepto.getCampos()
                        .get("APLICAFORMULA");
        boolean aplicaDescuento = (boolean) registroConcepto.getCampos()
                        .get("APLICADESCUENTO");
        boolean aplicaIva = (boolean) registroConcepto.getCampos()
                        .get("APLICAIVA");
        boolean aplicaRetefuente = (boolean) registroConcepto.getCampos()
                        .get("APLICARETEFUENTE");
        boolean aplicaIca = (boolean) registroConcepto.getCampos()
                        .get("APLICAICA");

        double valorUnitario = retornarDoble(registroSub,
                        ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                        .getValue());
        double valorBase = retornarDoble(registroSub,
                        ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                        .getValue());
        double cantidad = retornarDoble(registroSub,
                        GeneralParameterEnum.CANTIDAD.getName());

        double valorCompra = SysmanFunciones
                        .redondear(SysmanFunciones
                                        .redondear(retornarDoble(
                                                        registroConcepto,
                                                        ConceptosNoFacturadosControladorEnum.VALOR_COMPRA
                                                                        .getValue()),
                                                        digitoRedondeo)
                            * cantidad, digitoRedondeo);

        registroSub.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.VALOR_COMPRA
                                        .getValue(), valorCompra);

        if (modificarValor) {
            valorBase = SysmanFunciones.redondear(valorUnitario * cantidad,
                            digitoRedondeo);
        }
        else {
            double valorBaseConcepto = retornarDoble(registroConcepto,
                            ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                            .getValue());
            valorUnitario = SysmanFunciones.redondear(valorBaseConcepto,
                            digitoRedondeo);
            valorBase = SysmanFunciones.redondear(valorBaseConcepto * cantidad,
                            digitoRedondeo);
        }

        if (aplicaFormula) {
            int redForm = digitosPorConcepto(registroConcepto) == 0
                ? digitoRedondeo
                : digitosPorConcepto(registroConcepto);
            valorUnitario = SysmanFunciones.nvlDbl(
                            registroConcepto.getCampos().get("FORMULA"), 0)
                * (cantidad == 0 ? 1 : cantidad);
            valorBase = SysmanFunciones.redondear(valorUnitario * cantidad,
                            redForm);
        }

        // Calcula valor descuento
        double valorDescuento = aplicaDescuento
            ? SysmanFunciones.redondear(valorBase, redondeoDescuento)
            : 0;

        double valorIva = aplicaIva
            ? SysmanFunciones.redondear(
                            (valorBase - valorDescuento)
                                * Double.parseDouble(registroConcepto
                                                .getCampos()
                                                .get(ConceptosNoFacturadosControladorEnum.PORCENTAJEIVA
                                                                .getValue())
                                                .toString()),
                            redondeoIva)
            : 0;

        double valorRetefuente = aplicaRetefuente
            ? SysmanFunciones.redondear(
                            (valorBase - valorDescuento)
                                * Double.parseDouble(registroConcepto
                                                .getCampos()
                                                .get(ConceptosNoFacturadosControladorEnum.PORCENTAJEIVA
                                                                .getValue())
                                                .toString()),
                            redondeoIva)
            : 0;

        double valorIca = aplicaIca ? SysmanFunciones.redondear(
                        valorBase
                            * Double.parseDouble(registroConcepto.getCampos()
                                            .get("PORCENTAJEICA").toString()),
                        redondeoICA)
            : 0;

        double valorNeto = ((valorBase + valorIva + valorRetefuente)
            - valorDescuento) + valorIca;

        if (valorNeto < 0) {
            valorNeto = 0;
        }
        else if (valorUnitario == 0) {
            valorUnitario = valorBase / cantidad;
        }

        String centroCostoConcepto = retornarString(registroConcepto,
                        GeneralParameterEnum.CENTRO_COSTO.getName());
        String auxiliarConcepto = retornarString(registroConcepto,
                        GeneralParameterEnum.AUXILIAR.getName());
        String auxiliarTercero = SysmanConstantes.CONS_TERCERO;

        registroSub.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                        .getValue(), valorUnitario);
        registroSub.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.VALOR_BASE
                                        .getValue(), valorBase);
        registroSub.getCampos().put(
                        ConceptosNoFacturadosControladorEnum.VALOR_DESCUENTO
                                        .getValue(),
                        valorDescuento);
        registroSub.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.VALOR_IVA
                                        .getValue(), valorIva);
        registroSub.getCampos().put(
                        ConceptosNoFacturadosControladorEnum.VALOR_RETEFUENTE
                                        .getValue(),
                        valorRetefuente);
        registroSub.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.VALOR_ICA
                                        .getValue(), valorIca);
        registroSub.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.VALOR_NETO
                                        .getValue(), valorNeto);
        registroSub.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCostoConcepto);
        registroSub.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        auxiliarConcepto);
        registroSub.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        auxiliarTercero);
    }

    /**
     * Obtiene los digitos de redondeo por concepto
     *
     * @param registroConcepto
     * Concepto que ha sido seleccionado
     * @return Digitos de redondeo, valor obtenido por BD, por omision
     * cero (0)
     */
    private int digitosPorConcepto(Registro registroConcepto) {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(), anio);
        params.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        params.put(ConceptosNoFacturadosControladorEnum.CONCEPTO.getValue(),
                        registroConcepto.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        int digitos = 0;
        try {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ConceptosNoFacturadosControladorUrlEnum.URL002
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));
            digitos = Integer.parseInt(
                            rs.getCampos().get("DIGITOSR").toString());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return digitos;
    }

    /**
     * Consulta el valor del campo ingresado por parametro en el
     * registro del subformulario y lo asina al listado que tambien
     * ingresa por parametro
     *
     * @param listado
     * Listado de registros en el que se desea realizar la asignacion
     * @param rowNum
     * Registro exacto que se desea modificar
     * @param campo
     * Campo dentro del registro que se va a consultar
     */
    private void asignarListado(Registro reg, String campo) {
        reg.getCampos().put(campo, registroSub.getCampos().get(campo));
    }

    /**
     * Calcula el valor de los totales para el pie del subformulario
     */
    private void calcularTotales() {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                        tipoCobro);
        params.put("CODIGOCOBRO",
                        registro.getCampos().get(
                                        ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                                        .getValue()));
        params.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ConceptosNoFacturadosControladorUrlEnum.URL003
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            totalValorUnidadAux = retornarDoble(rs,
                            ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                            .getValue());
            totalValorUnidad = asignarFormato(rs,
                            ConceptosNoFacturadosControladorEnum.VALOR_UNITARIO
                                            .getValue());
            totalValor = asignarFormato(rs, "VALOR");
            totalIva = asignarFormato(rs, "IVA");
            totalRetefuente = asignarFormato(rs, "RETEFUENTE");
            totalDescuento = asignarFormato(rs, "DESCUENTO");
            totalIca = asignarFormato(rs, "ICA");
            totalNetoPie = asignarFormato(rs, "VALORNETO");
            totalNetoPieAux = retornarDoble(rs, "VALORNETO");

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Obtiene el valor del campo ingresado por parametro y le asigna
     * el formato de moneda
     *
     * @param reg
     * Registro en el que se va a cosultar el valor
     * @param campo
     * Nombre del campo a consultar en el registro ingresao por
     * parametro
     * @return Valor del campo ingresado por parametro con formato
     * moneda
     */
    private String asignarFormato(Registro reg, String campo) {
        return new java.text.DecimalFormat("$ #,##0.00")
                        .format(Double.parseDouble(
                                        reg.getCampos().get(campo).toString()));
    }

    /**
     * Verifica si la "Cuenta Recaudo" configurada en el concepto
     * seleccionado es valida en el plan contable para la vigencia en
     * la que se esta trabajando
     *
     * @param registroConcepto
     * Registro con la informacion del concepto que ha sido
     * seleccionado
     */
    private void validarCuentaRecaudo(Registro registroConcepto) {
        String cuentaRecaudoAux = retornarString(registroConcepto,
                        ConceptosNoFacturadosControladorEnum.CUENTA_RECAUDO
                                        .getValue());

        if (!SysmanFunciones.validarVariableVacio(cuentaRecaudoAux)) {
            poseeCuentaRecaudo = true;
            cuentaRecaudo = cuentaRecaudoAux;

            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.ANO.getName(), anio);
            params.put(GeneralParameterEnum.CUENTA.getName(), cuentaRecaudoAux);

            try {
                Registro rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ConceptosNoFacturadosControladorUrlEnum.URL004
                                                                                                .getValue())
                                                                .getUrl(),
                                                params));
                if ((rs == null) || (SysmanFunciones
                                .validarCampoVacio(rs.getCampos(), "EXISTE"))) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB3851"));
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Evalua si la cantidad ingresada es menor a uno
     *
     * @param cantidadActual
     * Cantidad registrada por el usuario
     * @return verdadero si la cantidad es menor a 1
     */
    private boolean validarCantidad(int cantidadActual) {

        if (cantidadActual < 1) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1487"));

            return false;

        }
        return true;
    }

    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton CargarConceptos en la
     * vista
     *
     */
    public void oprimirCargarConceptos() {
        // <CODIGO_DESARROLLADO>

        if (!validarCantidadGrupo()) {
            return;
        }

        try {
            agregarRegistroNuevo(false);

            String cantidad = retornarString(registro,
                            ConceptosNoFacturadosControladorEnum.CANTIDAD_GRUPO
                                            .getValue());
            String grupo = retornarString(registro, "GRUPO_CONCEPTOS");
            Date fechaSolicitud = (Date) registro.getCampos()
                            .get(ConceptosNoFacturadosControladorEnum.FECHA_SOLICITUD
                                            .getValue());
            String codigoCobro = retornarString(registro,
                            ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                            .getValue());

            if ("0".equals(grupo)
                || SysmanFunciones.validarVariableVacio(grupo)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3884"));
                return;
            }

            if (SysmanFunciones.validarVariableVacio(cantidad)
                || (Integer.parseInt(cantidad) < 0)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3885"));
                return;

            }

            ejbFactGenUno.cargarConceptos(compania, Integer.parseInt(anio),
                            tipoCobro, codigoCobro,
                            Integer.parseInt(grupo), fechaSolicitud,
                            Integer.parseInt(cantidad),
                            SessionUtil.getUser().getCodigo());
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3368"));
        }
        catch (NumberFormatException | SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarCantidadGrupo() {

        boolean rta = true;
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        ConceptosNoFacturadosControladorEnum.CANTIDAD_GRUPO
                                        .getValue())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3892"));
            rta = false;
        }
        else {
            int cantidad = Integer.parseInt(registro.getCampos()
                            .get(ConceptosNoFacturadosControladorEnum.CANTIDAD_GRUPO
                                            .getValue())
                            .toString());
            if (cantidad == 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3892"));
                rta = false;
            }
        }
        return rta;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton BtnGenerarCpte en la vista
     *
     */
    public void oprimirBtnGenerarCpte() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validaciones()) {
            return;
        }

        if (totalNetoPieAux <= 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3826"));
            return;
        }

        Long codigoCobro = Long
                        .parseLong(retornarString(registro,
                                        ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                                        .getValue()));
        Long nroFactura = Long.parseLong(SysmanFunciones
                        .nvl(registro.getCampos().get("NRO_FACTURA"), "0")
                        .toString());
        Date fecha = (Date) registro.getCampos().get(
                        ConceptosNoFacturadosControladorEnum.FECHA_SOLICITUD
                                        .getValue());

        try {
            ejbFactGenDos.facturarConceptos(compania, tipoCobro, codigoCobro,
                            nroFactura, Integer.parseInt(anio),
                            SessionUtil.getUser().getCodigo());

            ejbFactGenCuatro.manejarInterfazContableNoFacturado(compania,
                            tipoCobro,
                            new BigInteger(
                                            retornarString(registro,
                                                            ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                                                            .getValue())),
                            fecha,
                            retornarString(registro,
                                            GeneralParameterEnum.TERCERO
                                                            .getName()),
                            retornarString(registro,
                                            GeneralParameterEnum.SUCURSAL
                                                            .getName()),
                            retornarString(registro, "OBSERVACIONES"),
                            indManejaInventario,
                            SessionUtil.getUser().getCodigo());

            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
            generarInforme(ReportesBean.FORMATOS.PDF);
            agregarRegistroNuevo(false);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto actualizarForm en
     * la vista
     *
     *
     *
     * public void ejecutaractualizarForm() { // <CODIGO_DESARROLLADO>
     * agregarRegistroNuevo(false);
     * cargarRegistro(registro.getLlave(), accion,
     * registro.getIndice()); // </CODIGO_DESARROLLADO> }
     *
     * /**
     *
     * Metodo ejecutado al oprimir el boton BtnImprimirCpte en la
     * vista
     *
     */
    public void oprimirBtnImprimirCpte() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (validarFacturacion()) {
            generarInforme(ReportesBean.FORMATOS.PDF);
        }

        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {

        String comprobante = retornarString(registro, "NROCPTE_CTBLE");
        /*
         * Validación creada para asignar el valor null al comprobante cuando viene vacio,
         * y asi en la consulta poder traer todos los elementos por los demás filtros, sin
         * restricción por el comprobante.
         */
        if (comprobante.equals(""))
        	comprobante = "null";

        Map<String, Object> reemplazar = new TreeMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try {
            reemplazar.put("tipo", tipoCobro);
            reemplazar.put("comprobanteInicial", comprobante);
            reemplazar.put("comprobanteFinal", comprobante);
            // PARAMETROS PARA GENERACION DE INFORME

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
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

            Reporteador.resuelveConsulta("001491INFRECAUDO",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed("001491INFRECAUDO",
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarFacturacion() {
        String nroFactura = retornarString(registro, "NRO_FACTURA");

        boolean impreso = (boolean) registro.getCampos()
                        .get(ConceptosNoFacturadosControladorEnum.IMPRESO
                                        .getValue());

        if (SysmanFunciones.validarVariableVacio(nroFactura) || !impreso) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3881"));
            return false;
        }

        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Valida si el primer concepto definido en el cobro posee una
     * Cuenta de Reacaudo definida, metodo necesario para recargar el
     * listado de registros para el combo de Concepto (Cuando se ha
     * cerrado el formulario y se quieren hacer mosificaciones)
     */
    private void evaluarConCuentaRecaudo() {
        if (!listaSubconceptosnofacturadosbg.isEmpty()) {

            try {
                Map<String, Object> parametros = new TreeMap<>();
                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(GeneralParameterEnum.ANO.getName(), anio);
                parametros.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO
                                .getValue(), tipoCobro);
                parametros.put(GeneralParameterEnum.CODIGO.getName(),
                                listaSubconceptosnofacturadosbg.get(0)
                                                .getCampos()
                                                .get(GeneralParameterEnum.CONCEPTO
                                                                .getName()));

                Registro conceptoAux = new Registro(listaConcepto
                                .getRegistroUnico(parametros).getCampos());

                if ((!SysmanFunciones.validarCampoVacio(conceptoAux.getCampos(),
                                ConceptosNoFacturadosControladorEnum.CUENTA_RECAUDO
                                                .getValue()))) {
                    poseeCuentaRecaudo = true;
                    cuentaRecaudo = retornarString(conceptoAux,
                                    ConceptosNoFacturadosControladorEnum.CUENTA_RECAUDO
                                                    .getValue());
                    cargarListaConcepto();
                }

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    /**
     * Metodo de insercion del formulario Subconceptosnofacturadosbg
     *
     * Adiciona y remueve los campos necesario para realizar la
     * insercion en el subformulario Subconceptosnofacturadosbg
     */
    public void agregarRegistroSubSubconceptosnofacturadosbg() {

        try {
            registroSub.getCampos().remove(
                            ConceptosNoFacturadosControladorEnum.EXISTENCIA
                                            .getValue());
            registroSub.getCampos().remove(
                            ConceptosNoFacturadosControladorEnum.VALOR_UNIDAD
                                            .getValue());
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(
                            ConceptosNoFacturadosControladorEnum.TIPOCOBRO
                                            .getValue(),
                            registro.getCampos().get(
                                            ConceptosNoFacturadosControladorEnum.TIPOCOBRO
                                                            .getValue()));
            registroSub.getCampos().put(
                            ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                            .getValue(),
                            registro.getCampos().get(
                                            ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                                            .getValue()));
            registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.ANO
                                            .getName()));
            registroSub.getCampos().put("FECHA_FIN_COBRO", new Date());
            registroSub.getCampos().put("FECHA_INICIO_COBRO", new Date());
            registroSub.getCampos().remove(
                            ConceptosNoFacturadosControladorEnum.NOMBRECONCEPTO
                                            .getValue());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_COBRO
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaSubconceptosnofacturadosbg();

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
     * Metodo de edicion del formulario Subconceptosnofacturadosbg
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubconceptosnofacturadosbg(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().remove(
                            ConceptosNoFacturadosControladorEnum.NOMBRECONCEPTO
                                            .getValue());
            reg.getCampos().remove(ConceptosNoFacturadosControladorEnum.IMPRESO
                            .getValue());
            reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
            reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_COBRO
                                                            .getUpdateKey());

            int conteo;
            conteo = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(), reg.getCampos(),
                            reg.getLlave());
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubconceptosnofacturadosbg();
        }
    }

    /**
     * Metodo de eliminacion del formulario Subconceptosnofacturadosbg
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubconceptosnofacturadosbg(Registro reg) {

        try {
            int conteo;
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SF_DETALLE_COBRO
                                                            .getDeleteKey());
            conteo = requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            cargarListaSubconceptosnofacturadosbg();

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subconceptosnofacturadosbg
     *
     */
    public void cancelarEdicionSubconceptosnofacturadosbg() {
        cargarListaSubconceptosnofacturadosbg();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    private boolean validaciones() {
        try {
            if (!visibleFechaVencimiento) {

                Date fechaVencimiento = ejbFactGenDos.calcularFechaVencimiento(
                                compania, tipoCobro, indPreliquidacion,
                                retornarString(registro,
                                                ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                                                .getValue()));
                registro.getCampos().put("FECHA_VENCIMIENTO", fechaVencimiento);
                agregarRegistroNuevo(false);
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        boolean rta = true;
        if (totalValorUnidadAux <= 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3826"));
            rta = false;
        }

        String claseContable = claseContable();
        if (SysmanFunciones.validarVariableVacio(claseContable)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3849"));
            rta = false;
        }

        return rta;
    }

    private String claseContable() {
        Registro regClaseContable = null;
        String claseContable = "";

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO.getValue(),
                            tipoCobro);
            regClaseContable = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ConceptosNoFacturadosControladorUrlEnum.URL29788
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            claseContable = retornarString(regClaseContable, "CLASE_CONTABLE");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return claseContable;
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        manejaDelineacion = "SI".equalsIgnoreCase(obtenerParametro(
                        "SF MANEJA CONSECUTIVO DELINEACION URBANA", "NO"));
        terceroNoRegistrado = "SI"
                        .equalsIgnoreCase(obtenerParametro(
                                        "SF FACTURA A TERCERO NO REGISTRADOS EN EL SISTEMA",
                                        "NO"));
        visibleFechaVencimiento = "SI".equalsIgnoreCase(
                        obtenerParametro("SF FECHA VENCIMIENTO MANUAL", "NO"));
        bloqueaNroFactura = "SI".equalsIgnoreCase(obtenerParametro(
                        "SF PERMITE DIGITAR NRO FACTURA", "NO"));
        digitoRedondeo = obtenerParametroRedondeo("SF REDONDEO VALOR", 2);
        redondeoIva = obtenerParametroRedondeo("SF REDONDEO DE IVA", 0);

        redondeoICA = obtenerParametroRedondeo("SF REDONDEO DE ICA", 0);
        redondeoDescuento = obtenerParametroRedondeo(
                        "SF REDONDEO DEL DESCUENTO", 0);
        visibleBaseGravable = "SI".equalsIgnoreCase(
                        obtenerParametro("SF OBLIGA A BASE GRAVABLE", "NO"));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        inicializarValores();

        facturado = false;

        if (css != null) {
            boolean impreso = (boolean) registro.getCampos()
                            .get(ConceptosNoFacturadosControladorEnum.IMPRESO
                                            .getValue());

            if (impreso) {

                facturado = true;
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * Adiciona valores a los campos de la llave de la tabla y remueve
     * los campos en el registro que no perteneen a la tabla principal
     *
     * @return Autorizacion par realizar la insercion
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos()
                        .put(ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                        .getValue(), generarCodigoCobro());
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ANO", anio);
        registro.getCampos().put(ConceptosNoFacturadosControladorEnum.TIPOCOBRO
                        .getValue(), tipoCobro);
        registro.getCampos().remove(
                        ConceptosNoFacturadosControladorEnum.NOMBRETERCERO
                                        .getValue());
        registro.getCampos().remove("NOMBRECENTRO");
        registro.getCampos().remove(
                        ConceptosNoFacturadosControladorEnum.NOMBREAUXILIAR
                                        .getValue());

        // </CODIGO_DESARROLLADO>
        return true;
    }

    public boolean validarcierrepresupuestal() {
        Registro reg = null;
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("COMPANIA", compania);
        parametros.put("ANO", anio);

        try {
            reg = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ConceptosNoFacturadosControladorUrlEnum.URL15069
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ("0".equals(reg.getCampos().get("CANTIDAD").toString())) {
            return true;
        }
        else {
            JsfUtil.agregarMensajeError("Ya existe Cierre contable para el año"
                + " " + anio);
            return false;
        }

    }

    private Long generarCodigoCobro() {
        String criterio = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "'' AND TIPOCOBRO = ''", tipoCobro,
                        "'' AND SUBSTR(CODIGO_COBRO,1,4) = ", anio);
        Long respuesta = null;
        int anoConsecutivo = SysmanFunciones.ano(new Date());
        try {
            respuesta = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "SF_OBJETO_COBRO", criterio,
                            ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                            .getValue(),
                            SysmanFunciones.concatenar(
                                            String.valueOf(anoConsecutivo),
                                            "000001"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return respuesta;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return Autorizacion par realizar la insercion
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
     * Remueve los campos en el registro que no perteneen a la tabla
     * principal
     *
     * Verifica el estado del mes para permitir o no la actualizacion
     *
     * @return Autorizacion para realizar la actualizacion
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NUMERO_ABONO");
        registro.getCampos().remove("NOMBRE_CENTRO_COSTO");
        registro.getCampos().remove(
                        ConceptosNoFacturadosControladorEnum.NOMBREAUXILIAR
                                        .getValue());
        registro.getCampos().remove(
                        ConceptosNoFacturadosControladorEnum.NOMBRETERCERO
                                        .getValue());
        registro.getCampos().remove("NUMERO_ABONO");

        registro.getCampos().remove("NOMBRE_CENTRO_COSTO");
        registro.getCampos().remove("OBJETO_CONTRATO");
        registro.getCampos().remove("NOMBRE_FUENTE_RECURSO");
        registro.getCampos().remove("ANULADA");
        if (SysmanFunciones.ano((Date) registro.getCampos()
                        .get("FECHA_SOLICITUD")) != Integer.parseInt(anio)) {
            JsfUtil.agregarMensajeError(
                            "la fecha de la Solicitud no corresponde al año de trabajo");
            return false;

        }

        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos().remove("VALOR_TOTAL");

            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove("ANO");
            registro.getCampos().remove("TIPOCOBRO");
            registro.getCampos().remove("CODIGO_COBRO");
        }

        // </CODIGO_DESARROLLADO>
        return validarcierrepresupuestal();
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        if (css != null) {
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     * Valida si la factura ya ha sido facturada
     *
     * Cuando permite la eliminación de la factura tambien se eliminan
     * los detalles asociados a una factura
     *
     * @return Autorizacion para realizar la eliminacion
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        boolean impreso = (boolean) registro.getCampos()
                        .get(ConceptosNoFacturadosControladorEnum.IMPRESO
                                        .getValue());
        if (impreso) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3809"));
            return false;
        }

        int conteo;

        UrlBean eliminarDetalles = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosNoFacturadosControladorUrlEnum.URL001
                                                        .getValue());
        try {
            conteo = requestManager.delete(eliminarDetalles.getUrl(),
                            registro.getLlave());

            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3810")
                                .replace("s$codigo$s", registro
                                                .getCampos()
                                                .get(ConceptosNoFacturadosControladorEnum.CODIGO_COBRO
                                                                .getValue())
                                                .toString()));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
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
     * Retorna la variable totalNeto
     *
     * @return totalNeto
     */
    public String getTotalNeto() {
        return totalNeto;
    }

    /**
     * Asigna la variable totalNeto
     *
     * @param totalNeto
     * Variable a asignar en totalNeto
     */
    public void setTotalNeto(String totalNeto) {
        this.totalNeto = totalNeto;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable visibleFechaVencimiento
     *
     * @return visibleFechaVencimiento
     */
    public boolean isVisibleFechaVencimiento() {
        return visibleFechaVencimiento;
    }

    /**
     * Asigna la variable visibleFechaVencimiento
     *
     * @param visibleFechaVencimiento
     * Variable a asignar en visibleFechaVencimiento
     */
    public void setVisibleFechaVencimiento(boolean visibleFechaVencimiento) {
        this.visibleFechaVencimiento = visibleFechaVencimiento;
    }

    /**
     * Retorna la variable visibleBaseGravable
     *
     * @return visibleBaseGravable
     */
    public boolean isVisibleBaseGravable() {
        return visibleBaseGravable;
    }

    /**
     * Asigna la variable visibleBaseGravable
     *
     * @param visibleBaseGravable
     */
    public void setVisibleBaseGravable(boolean visibleBaseGravable) {
        this.visibleBaseGravable = visibleBaseGravable;
    }

    /**
     * Retorna la variable manejaDelineacion
     *
     * @return manejaDelineacion
     */
    public boolean isManejaDelineacion() {
        return manejaDelineacion;
    }

    /**
     * Asigna la variable manejaDelineacion
     *
     * @param manejaDelineacion
     * Variable a asignar en manejaDelineacion
     */
    public void setManejaDelineacion(boolean manejaDelineacion) {
        this.manejaDelineacion = manejaDelineacion;
    }

    /**
     * Retorna la variable terceroNoRegistrado
     *
     * @return terceroNoRegistrado
     */
    public boolean isTerceroNoRegistrado() {
        return terceroNoRegistrado;
    }

    /**
     * Asigna la variable terceroNoRegistrado
     *
     * @param terceroNoRegistrado
     * Variable a asignar en terceroNoRegistrado
     */
    public void setTerceroNoRegistrado(boolean terceroNoRegistrado) {
        this.terceroNoRegistrado = terceroNoRegistrado;
    }

    /**
     * Retorna la variable bloqueadoFacturado
     *
     * @return bloqueadoFacturado
     */
    public boolean isBloqueadoFacturado() {
        return bloqueadoFacturado;
    }

    /**
     * Asigna la variable bloqueadoFacturado
     *
     * @param bloqueadoFacturado
     * Variable a asignar en bloqueadoFacturado
     */
    public void setBloqueadoFacturado(boolean bloqueadoFacturado) {
        this.bloqueadoFacturado = bloqueadoFacturado;
    }

    public boolean isBloqueaNroFactura() {
        return bloqueaNroFactura;
    }

    public void setBloqueaNroFactura(boolean bloqueaNroFactura) {
        this.bloqueaNroFactura = bloqueaNroFactura;
    }

    public boolean isBloqueadoValorBase() {
        return bloqueadoValorBase;
    }

    public void setBloqueadoValorBase(boolean bloqueadoValorBase) {
        this.bloqueadoValorBase = bloqueadoValorBase;
    }

    public boolean isBloqueadoCantidad() {
        return bloqueadoCantidad;
    }

    public void setBloqueadoCantidad(boolean bloqueadoCantidad) {
        this.bloqueadoCantidad = bloqueadoCantidad;
    }

    public String getTotalValorUnidad() {
        return totalValorUnidad;
    }

    public void setTotalValorUnidad(String totalValorUnidad) {
        this.totalValorUnidad = totalValorUnidad;
    }

    public String getTotalValor() {
        return totalValor;
    }

    public void setTotalValor(String totalValor) {
        this.totalValor = totalValor;
    }

    public String getTotalNetoPie() {
        return totalNetoPie;
    }

    public void setTotalNetoPie(String totalNetoPie) {
        this.totalNetoPie = totalNetoPie;
    }

    public String getTotalRetefuente() {
        return totalRetefuente;
    }

    public void setTotalRetefuente(String totalRetefuente) {
        this.totalRetefuente = totalRetefuente;
    }

    public String getTotalDescuento() {
        return totalDescuento;
    }

    public void setTotalDescuento(String totalDescuento) {
        this.totalDescuento = totalDescuento;
    }

    public String getTotalIca() {
        return totalIca;
    }

    public void setTotalIca(String totalIca) {
        this.totalIca = totalIca;
    }

    public String getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(String totalIva) {
        this.totalIva = totalIva;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaGrupoConceptos
     *
     * @return listaGrupoConceptos
     */
    public List<Registro> getListaGrupoConceptos() {
        return listaGrupoConceptos;
    }

    /**
     * Asigna la lista listaGrupoConceptos
     *
     * @param listaGrupoConceptos
     * Variable a asignar en listaGrupoConceptos
     */
    public void setListaGrupoConceptos(List<Registro> listaGrupoConceptos) {
        this.listaGrupoConceptos = listaGrupoConceptos;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaConcepto
     *
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConcepto() {
        return listaConcepto;
    }

    /**
     * Asigna la lista listaConcepto
     *
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    /**
     * Retorna la lista listaConcepto
     *
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConceptoE() {
        return listaConceptoE;
    }

    /**
     * Asigna la lista listaConcepto
     *
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConceptoE(RegistroDataModelImpl listaConceptoE) {
        this.listaConceptoE = listaConceptoE;
    }

    /**
     * Retorna la variable auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
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

    /**
     * Retorna la lista listaNitTercero
     *
     * @return listaNitTercero
     */
    public RegistroDataModelImpl getListaNitTercero() {
        return listaNitTercero;
    }

    /**
     * Asigna la lista listaNitTercero
     *
     * @param listaNitTercero
     * Variable a asignar en listaNitTercero
     */
    public void setListaNitTercero(RegistroDataModelImpl listaNitTercero) {
        this.listaNitTercero = listaNitTercero;
    }

    /**
     * Retorna la lista listaCentroCosto
     *
     * @return listaCentroCosto
     */
    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    /**
     * Asigna la lista listaCentroCosto
     *
     * @param listaCentroCosto
     * Variable a asignar en listaCentroCosto
     */
    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    /**
     * Retorna la lista listaAuxiliar
     *
     * @return listaAuxiliar
     */
    public RegistroDataModelImpl getListaAuxiliar() {
        return listaAuxiliar;
    }

    /**
     * Asigna la lista listaAuxiliar
     *
     * @param listaAuxiliar
     * Variable a asignar en listaAuxiliar
     */
    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }

    /**
     * Retorna la lista listaContratoAfectar
     *
     * @return listaContratoAfectar
     */
    public RegistroDataModel getListaContratoAfectar() {
        return listaContratoAfectar;
    }

    /**
     * Asigna la lista listaContratoAfectar
     *
     * @param listaContratoAfectar
     * Variable a asignar en listaContratoAfectar
     */
    public void setListaContratoAfectar(
        RegistroDataModel listaContratoAfectar) {
        this.listaContratoAfectar = listaContratoAfectar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubconceptosnofacturadosbg
     *
     * @return listaSubconceptosnofacturadosbg
     */
    public List<Registro> getListaSubconceptosnofacturadosbg() {
        return listaSubconceptosnofacturadosbg;
    }

    /**
     * Asigna la lista listaSubconceptosnofacturadosbg
     *
     * @param listaSubconceptosnofacturadosbg
     * Variable a asignar en listaSubconceptosnofacturadosbg
     */
    public void setListaSubconceptosnofacturadosbg(
        List<Registro> listaSubconceptosnofacturadosbg) {
        this.listaSubconceptosnofacturadosbg = listaSubconceptosnofacturadosbg;
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

    public boolean isFacturado() {
        return facturado;
    }

    public void setFacturado(boolean facturado) {
        this.facturado = facturado;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    // </SET_GET_ADICIONALES>
}
