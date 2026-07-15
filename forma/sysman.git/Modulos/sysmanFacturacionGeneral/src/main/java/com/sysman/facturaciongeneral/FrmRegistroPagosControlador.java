/*-
 * FrmRegistroPagosControlador.java
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

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.impl.EjbContabilidadCpteGeneral;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralTresRemote;
import com.sysman.facturaciongeneral.ejb.impl.EjbFacturacionGeneralABParciales;
import com.sysman.facturaciongeneral.enums.FrmRegistroPagosControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmRegistroPagosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmconsultapagoparcialControladorUrlEnum;
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
import com.sysman.util.rest.APISIGEC;
import com.sysman.util.rest.ParametrosSIGEC;
import com.sysman.util.rest.RespuestaApiSigec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite ejecutar el proceso de registrar pagos. Se
 * accede desde la ruta Panel Principal\Facturacion
 * General\Procesos\Registro de pagos
 *
 * @version 1.0, 22/11/2017
 * @author lcortes
 */
@ManagedBean
@ViewScoped
public class FrmRegistroPagosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que ingresa a la aplicacion
     */
    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que identifica el tipo de factura seleccionado.
     */
    private String tipoFactura;
    /**
     * Lista que carga los bancos de pago
     */
    private String bancoPago;
    /**
     * Variable que identifica el tercero seleccionado.
     */
    private String tercero;

    /**
     * Variable que identifica la sucursal de la factura
     */
    private String sucursal;
    /**
     * Variable que identifica la factura seleccionada.
     */
    private String noFactura;

    /**
     * Codigo de cobro de la factura
     */
    private String codigoCobro;

    private String compRecaudo;
    /**
     * Variable que identifica el valor de la factura seleccionada.
     */
    private String vlrFactura;

    private String cptRecaudo;

    /**
     * Variable que almacena el numero de comproabnte de recaudo
     */
    private String numeroComprobanteRecaudo;

    private boolean bloqueoRecaudo = false;

    /**
     * Variable que adopta el valor del parametro SF TOMAR CONSECUTIVO
     * PARAMETRIZADO EN EL TIPO DE COBRO
     */
    private String consecutivoParametrizado;
    /**
     * Variable que identifica la fecha de pago de la factura.
     */
    private Date fechaPago;
    /**
     * Variable que identifica el nombre del tercero seleccionado.
     */
    private String nombreTercero;
    /**
     * Variable que identifica el nombre del banco seleccionado.
     */
    private String nombreBanco;
    /**
     * Variable que identifica el nombre del tipo de comprobante.
     */
    private String nombreComprobante;
    /**
     * Variable que identifica la fecha de vencimiento de la factura.
     */
    private Date fechaVencimiento;

    /**
     * Variable que identifica la fecha de expedición de la factura
     */
    private Date fechaExpedicion;
    /**
     * Variable que identifica la fecha de pago en el banco.
     */
    private Date fechaPagoBanco;

    /**
     * Variable que administra el bloqueo del combo de banco
     */
    private boolean bloqueaBanco;

    /**
     * Indicador si el tipo de cobro maneja interfaz de recuado
     */
    private boolean indInterfazRecuado;

    /**
     * Indicador si la factura esta interfazada
     */
    private boolean indInterfazada;
    /**
     * Indicador si el tipo de cobro maneja inventario
     */
    private boolean indManejaInventario;

    /**
     * Indicador si la factura esta diferida
     */
    private boolean indDiferida;
    /**
     * Indicador si la factura tiene pagos parciales
     */
    private boolean indPagosParciales;

    /**
     * Variable que almacena el tipo de abono del numero de factura
     * selecconada
     */
    private String tipoAbono;
    /**
     * Variable que almacena el numero de abono de la factura
     * seleccionada
     */
    private String abono;

    /**
     * Indicador NOAPLICACAUSACION del tipo de cobro seleccionado
     */
    private boolean noCausar;

    /**
     * Valida la visibilidad del dialogo que valida facturas diferidas
     */
    private boolean dialogoVisibleDiferida;

    /**
     * Valida la visibilidad del dialogo que valida cuotas vencidas
     */
    private boolean dialogoCuotaVencida;
    
    /**
     * Valida la visibilidad del dialogo que valida la TRM de la factura vs la TRM del recaudo
     */
    private boolean dialogoVisibleTRM;
    
    /**
     * Valida la visibilidad del campo TRM
     */
    private boolean aplicaFacturaDolares;
    
    private boolean visibleTRM;

    /**
     * Variable que almacena el valor del parametro SF RECAUDAR
     * FACTURAS DE VIGENCIAS ANTERIORES
     */
    private String vigenciaAnterior;

    /**
     * Variable que almacena las observaciones de la factura
     */
    private String observaciones;

    /**
     * Variable que almacena el numero de la cuota del abono de la
     * factura diferida
     */
    private String cuotaAb;

    /**
     * Variable que almacena el valor de la cuota del abono de la
     * factura diferida
     */
    private String valorAb;
    
    /**
     * Variable que almacena el valor de la TRM para recaudo
     */
    private String valorTRM;
    
    /**
     * Variable que almacena el valor de la factura en dolares
     */
    private String vlrTRMFactura;
    
    /**
     * Variable que almacena el saldo de las facturas
     * cuando tienen pagos parciales 
     */
    private String saldo;
    
    /**
     * Valida la visibilidad del valor a pagar
     * pagos parciales
     */
    private boolean manPagoParcial;
    
    /**
     * Indicador si la factura tiene pagos parciales
     */
    private boolean indPagoParcial;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Listado de tipos de comprobante.
     */
    private List<Registro> listaCompRecaudo;

    /**
     * Listado de tipo de facturas.
     */
    private List<Registro> listaTipoFactura;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Listado de terceros.
     */
    private RegistroDataModel listaTercero;
    /**
     * Listado de facturas.
     */
    private RegistroDataModelImpl listaNoFactura;

    /**
     * Listado de bancos
     */

    private RegistroDataModelImpl listaBancoPago;

    private String anio;
    private String tipoCobro;
    private String parClaseCuenta;
    private String tipoC;
    // Del combo noFactura

    private boolean visibleTercero;
    private boolean visibleBanco;
    private boolean visibleCompRec;
    private boolean desdeFacConceptos;
    private boolean apliCaRec;

    private boolean registrarPagoDiferido;
    
    private boolean visibleSigec;
    
    private String campoAnio = "ANIO";
    /**
     * Variable que permite que se bloquee o no el boton Codificar Retenciones
     */
    private boolean bloqueoCodRet = false;
    /**
     * Variable que permite que se visualice o no el boton Codificar Retenciones
     */
    private boolean visibleCodifRet;
    /**
     * Variable que almacena el campo CPTE_RECAUDO de la tabla SF_TIPO_COBRO
     */
    private String cpteRecaudo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFactGeneralCero;

    @EJB
    private EjbFacturacionGeneralTresRemote ejbFactGeneralTres;

    @EJB
    private EjbFacturacionGeneralCuatroRemote ejbFactGeneralCuatro;
    
    @EJB
    private EjbFacturacionGeneralABParciales ejbFactGeneralABParciales;
    
    @EJB
	private EjbContabilidadCpteGeneral ejbContabilidadCpte;

    /**
     * Crea una nueva instancia de FrmRegistroPagosControlador
     */
    public FrmRegistroPagosControlador() {

        super();

        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        tipoCobro = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue())
                        .toString();

        tipoFactura = tipoCobro;

        bloqueaBanco = false;
        
        visibleSigec = false;

        anio = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue())
                        .toString();

        indInterfazRecuado = (boolean) SessionUtil
                        .getSessionVar(ConstantesFacturacionGenEnum.INTERFAZ_RECAUDO
                                        .getValue());

        indManejaInventario = (boolean) SessionUtil
                        .getSessionVar(ConstantesFacturacionGenEnum.MANEJA_INVENTARIO
                                        .getValue());

        cptRecaudo = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.CPTE_RECAUDO.getValue())
                        .toString();

        dialogoVisibleDiferida = false;

        dialogoCuotaVencida = false;
        
        dialogoVisibleTRM = false;

        desdeFacConceptos = false;

        try {

            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                noFactura = (String) parametros.get("nroFactura");
                fechaVencimiento = SysmanFunciones.convertirAFecha(
                                parametros.get("fechaVencimiento").toString());
                vlrFactura = (String) parametros.get("vlrFactura");
                tercero = (String) parametros.get("tercero");
                nombreTercero = (String) parametros.get("nombreTercero");

                bancoPago = (String) parametros.get("bancoPago");
                nombreBanco = (String) parametros.get("nombreBanco");
                tipoC = (String) parametros.get("tipoC");
                tipoAbono = (String) parametros.get("tipoAbono");
                abono = (String) parametros.get("abono");

                indInterfazada = Boolean.parseBoolean(
                                parametros.get("indInterfazada").toString());
                indDiferida = Boolean.parseBoolean(
                                parametros.get("indDiferida").toString());
                indPagosParciales = Boolean.parseBoolean(
                        parametros.get("indPagosParciales").toString());       
                observaciones = (String) parametros.get("observaciones");

                desdeFacConceptos = true;
                
                aplicaFacturaDolares = Boolean.parseBoolean(
                        parametros.get("aplicaFacturaDolares").toString()); // MPEREZ
                
                vlrTRMFactura = (String) parametros.get("vlrTRMFactura"); //MPEREZ
                
                fechaExpedicion = SysmanFunciones.convertirAFecha(
                        parametros.get("fecha_expedicion").toString());

            }
            
            valorTRM = "0";

            apliCaRec = (Boolean) SessionUtil
                            .getSessionVarContainer(
                                            ConstantesFacturacionGenEnum.APLICACUENTARE
                                                            .getValue());

            numFormulario = GeneralCodigoFormaEnum.FRM_REGISTRO_PAGOS_CONTROLADOR
                            .getCodigo();
                        
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
            SessionUtil.cleanFlash();
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

        // <CARGAR_LISTA>

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoFactura();
        cargarListaBancoPago();

        cargarListaNoFactura();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        fechaPago = new Date();
        fechaPagoBanco = new Date();
        
        visibleTRM = aplicaFacturaDolares;

        try {

            vigenciaAnterior = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SF RECAUDAR FACTURAS DE VIGENCIAS ANTERIORES",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO")
                            .toString();

            validarBancos();

            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "SF OCULTAR CAMPOS MODULO DE FACTURACION",
                            SessionUtil.getModulo(), new Date(), true))) {
                visibleTercero = false;
            }
            else {
                visibleTercero = true;
            }

            if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "SF MANEJA RECAUDO A FAVOR DE TERCEROS",
                            SessionUtil.getModulo(), new Date(), true))
                && (boolean) SessionUtil
                                .getSessionVar(ConstantesFacturacionGenEnum.MANEJA_RECAUDOTERCEROS
                                                .getValue())) {
                parClaseCuenta = SessionUtil.getSessionVar(
                                ConstantesFacturacionGenEnum.CLASE_CUENTASRECAUDO
                                                .getValue())
                                .toString();
            }
            else {
                parClaseCuenta = ejbSysmanUtil.consultarParametro(compania,
                                "SF MANEJAR CLASE CUENTAS PARA RECAUDOS",
                                SessionUtil.getModulo(), new Date(), true);
            }

            // ----------*********************----
            // CpteTipo y demás validaciones
            // ----------*********************----

            if ("SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "SF OCULTAR CAMPOS MODULO DE FACTURACION",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"))) {
                return;
            }

            if ("SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "SF MANEJA RECAUDO PARAMETRIZADO",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"))) {
                if ("SIN CONFIGURAR".equals(SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "SF CUENTA DE RECAUDO",
                                                SessionUtil.getModulo(),
                                                new Date(), true),
                                "SIN CONFIGURAR"))) {
                    JsfUtil.agregarMensajeError(
                                    "No se ha creado o configurado correctamente el parámetro - SF CUENTA DE RECAUDO - con el código de la cuenta de caja o bando donde se registra el pago");
                }
                else {
                    Map<String, Object> param = new TreeMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put(FrmRegistroPagosControladorEnum.ANIOFECPAGO
                                    .getValue(),
                                    String.valueOf(SysmanFunciones
                                                    .ano(fechaPago)));
                    param.put(FrmRegistroPagosControladorEnum.CUENTARECAUDO
                                    .getValue(),
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "SF CUENTA DE RECAUDO",
                                                    SessionUtil.getModulo(),
                                                    new Date(), true));

                    Registro regAux = RegistroConverter
                                    .toRegistro(requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    FrmRegistroPagosControladorUrlEnum.URL10619
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));

                    if (regAux != null) {
                        bancoPago = SysmanFunciones
                                        .nvlStr(regAux.getCampos().get(
                                                        GeneralParameterEnum.CODIGO
                                                                        .getName())
                                                        .toString(), "");
                        nombreBanco = SysmanFunciones
                                        .nvlStr(regAux.getCampos().get(
                                                        GeneralParameterEnum.NOMBRE
                                                                        .getName())
                                                        .toString(), "");
                        visibleBanco = true;
                    }
                    else {
                        JsfUtil.agregarMensajeAlerta(
                                        "La cuenta configurado en el parámetro - SF CUENTA DE RECAUDO - no existe para el plan contable de la vigencia \" & Year(Me!FECHAPAGO) & \" o no corresponde a una cuenta de recuado o no es de movimiento. Por favor verificar la configuración.");

                    }
                }
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), cptRecaudo);

            Registro regTCpte = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmRegistroPagosControladorUrlEnum.URL10618
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
            if (regTCpte != null) {
                compRecaudo = regTCpte.getCampos()
                                .get(GeneralParameterEnum.CODIGO.getName())
                                .toString();
                nombreComprobante = regTCpte.getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString();
                visibleCompRec = true;
            }
            else {
                JsfUtil.agregarMensajeAlerta("El tipo de comprobante "
                    + cptRecaudo
                    + " no se ha creado o no corresponde a un tipo de ingreso.");

            }
            
            visibleSigec = "SI".equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"SF MANEJA FACTURACION DE ESTAMPILLA ELECTRONICA", SessionUtil.getModulo(), new Date(), true),
					"NO")) ? true : false;
            
            
            manPagoParcial = "SI".equals(SysmanFunciones.nvlStr(
            	    ejbSysmanUtil.consultarParametro(compania,"SF APLICA PAGOS PARCIALES", 
            	        SessionUtil.getModulo(),new Date(),true),"NO"));
            
            visibleCodifRet = "SI".equals(SysmanFunciones.nvlStr(
            		ejbSysmanUtil.consultarParametro(compania,"SF REGISTRAR RETENCIONES EN RECAUDO", 
            			SessionUtil.getModulo(),new Date(),true),"NO"));
            
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private void validarBancos() {
        if (apliCaRec && !indDiferida) {

            bloqueaBanco = true;
        }
        else {
            bloqueaBanco = false;
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaBancoPago
     *
     */
    public void cargarListaBancoPago() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRegistroPagosControladorUrlEnum.URL9195
                                                        .getValue());

        listaBancoPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "CODIGO");

    }

    /**
     *
     * Carga la lista listaTipoFactura
     *
     */
    public void cargarListaTipoFactura() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANOCOBRO", anio);
        param.put("TIPOCOBRO", tipoCobro);

        try {
            listaTipoFactura = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmRegistroPagosControladorUrlEnum.URL11561
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
     * Carga la lista listaNoFactura
     *
     */
    public void cargarListaNoFactura() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPO_FACTURA", tipoFactura);

        if ("NO".equals(vigenciaAnterior)) {

            param.put(GeneralParameterEnum.ANO.getName(), anio);
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmRegistroPagosControladorUrlEnum.URL15013
                                                            .getValue());

            listaNoFactura = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            FrmRegistroPagosControladorEnum.NUMERO_FACTURA
                                            .getValue());

        }
        else {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmRegistroPagosControladorUrlEnum.URL15014
                                                            .getValue());
            listaNoFactura = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            FrmRegistroPagosControladorEnum.NUMERO_FACTURA
                                            .getValue());

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    private void traeCpteRecaudo() {
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(GeneralParameterEnum.ANIO.getName(),anio);
        param.put(GeneralParameterEnum.CODIGO.getName(),tipoCobro);
        
        try {
            Registro registro = RegistroConverter.toRegistro(
            		requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                    FrmRegistroPagosControladorUrlEnum.URL665034.getValue())
                             .getUrl(),param));
            
            if(registro != null) {
            	cpteRecaudo = registro.getCampos().get("CPTE_RECAUDO").toString();
            } else {
            	JsfUtil.agregarMensajeInformativoDialogo("Por verifique que el campo Tipo de Cpte para Recaudo en los TIPOS DE COBRO para el año "
            											  +anio+ " Código " +tipoCobro+ "se encuentre diligenciado.");
            	return;
            }
        } catch(SystemException e) {
            logger.error(e.getMessage(),e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    private BigInteger traeConseCnt (int anioCodRet) {
    	BigInteger consecutivo = null;
    	try {
			consecutivo = ejbContabilidadCpte.enumerarComprobanteCnt(compania,anioCodRet,cpteRecaudo,BigInteger.ZERO,"");
		} catch(SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    	return consecutivo;
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton CodifRet
     * en la vista
     *
     *
     */
    public void oprimirCodifRet() {
    	traeCpteRecaudo();
    	String anioCodRet = String.valueOf(SysmanFunciones.ano(fechaPago));
    	BigInteger numeroComp = traeConseCnt(Integer.parseInt(anioCodRet));
    	
    	String[] campos = { "anio", "tipoComp", "numeroComp" };
		String[] valores = { anioCodRet, cpteRecaudo, String.valueOf(numeroComp) };

		SessionUtil.cargarModalDatosFlashCerrar(String.valueOf(
				GeneralCodigoFormaEnum.CODIFICARRETENCIONES_CONTROLADOR.getCodigo()),
				SessionUtil.getModulo(),campos,valores);
    }
    /**
     *
     * Metodo ejecutado al oprimir el boton Recaudar en la vista
     *
     *
     */
    public void oprimirRecaudar() 
    {
    	if(aplicaFacturaDolares)
    	{
    		if(Double.parseDouble(valorTRM)<=0)
    		{
	    		JsfUtil.agregarMensajeError(
	                    "Debe ingresar el valor de la T.R.M");
	        	return;
    		}
    		
    		/* Valida si el valor de la trm de la factura es diferente al valor de la trm del recaudo*/
    		if(Double.parseDouble(valorTRM) != Double.parseDouble(vlrTRMFactura)) 
    		{
    			dialogoVisibleTRM = true;
    		}
    		else
    		{        		
    			recaudar();
    		}
    	}     	
    	else
    	{
    		recaudar();   
    	}
    }
    
    /**
    * Metodo encargado de realizar el proceso de recaudo
    */
    private void recaudar()
    {
    	archivoDescarga = null;
        String mensaje;
        if (validarFechas()) {
        	
            try {  
            	
                String sinTipoCobro = SysmanFunciones
                                .nvlStr(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "SF RECAUDAR FACTURA SIN INCLUIR TIPO DE COBRO",
                                                SessionUtil.getModulo(),
                                                new Date(),
                                                true), "NO");

                String manejaIntSPublicos = SysmanFunciones
                                .nvlStr(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "SF MANEJA FINANCIABLES A SERVICIOS PUBLICOS",
                                                SessionUtil.getModulo(),
                                                new Date(),
                                                true), "NO");

                consecutivoParametrizado = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                "SF TOMAR CONSECUTIVO PARAMETRIZADO EN EL TIPO DE COBRO",
                                                SessionUtil.getModulo(),
                                                new Date(),
                                                true),
                                "NO");

                if ("SI".equals(sinTipoCobro)) {
                    tipoFactura = tipoC;
                }

                if ("NO".equals(consecutivoParametrizado)
                    && SysmanFunciones.comparaFechas(fechaVencimiento,
                                    fechaPago)
                    && "NO".equals(vigenciaAnterior)) {

                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4308"));

                    return;
                }

                validarNoCobroCausacion(tipoFactura, anio);
                
                if (indInterfazRecuado) {
                    
                    anio = String.valueOf(SysmanFunciones.ano(fechaPago));
                }                    

                if (indInterfazRecuado && !indInterfazada && !noCausar
                    && !indDiferida) {

                    if (!ejbFactGeneralTres.interfazarFactura(compania,
                                    tipoFactura,
                                    new BigInteger(noFactura), fechaPago,
                                    true, indManejaInventario, usuario)) {
                        return;
                    }
                    else {
                        indInterfazada = true;
                    }
                }
                if (!indInterfazada && "NO".equals(manejaIntSPublicos)
                    && !noCausar) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB4312"));

                    return;
                }
                
                if (indPagosParciales) {
                	if (!"".equals(bancoPago)) {
                		mensaje = ejbFactGeneralABParciales.recaudarPagoParcial(
    							compania, 
    							Integer.parseInt(anio), 
    							tipoC,
    							new BigInteger(noFactura), 
    							fechaPago,
    							fechaPagoBanco,
    							bancoPago, 
    							usuario);
                		JsfUtil.agregarMensajeInformativoDialogo(mensaje);	
                	}
                	else
                	{
                		JsfUtil.agregarMensajeError(idioma.getString("TB_TB1020"));
                	}
                	
                }
                else  {
                	validarDiferida();
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporteComprobanteRecaudo() throws SystemException {
        archivoDescarga = null;

        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try {
            reemplazar.put("compania", compania);
            reemplazar.put("anio", anio);
            reemplazar.put("tipoComprobante", cptRecaudo);
            reemplazar.put("comprobanteInicial", numeroComprobanteRecaudo);
            reemplazar.put("comprobanteFinal", numeroComprobanteRecaudo);

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            /*INICIO mperez*/
            String reporteRecaudo = (String) (SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(
            		compania, 
            		"SF FORMATO COMPROBANTE DE RECAUDO", 
            		SessionUtil.getModulo(), 
            		new Date(), 
            		true), "002060INFINGSTD01"));            
            if (reporteRecaudo.equals("002524RECIBODECAJAIDCBIS")){
            	reemplazar.remove("anio");
            	reemplazar.put("ano", anio);
            	parametros.put("PR_DIRECCION",SessionUtil.getCompaniaIngreso().getDireccion());
            	parametros.put("PR_TELEFONO",SessionUtil.getCompaniaIngreso().getTelefono());
            }
            Reporteador.resuelveConsulta(reporteRecaudo,//"002060INFINGSTD01",
            		Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporteRecaudo, //"002060INFINGSTD01",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
            /*FIN mperez*/
            
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarFechas() {
        if (SysmanFunciones.comparaFechas(fechaPago, fechaExpedicion)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4306"));
            return false;
        }
        return true;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Salir en la vista
     *
     */
    public void oprimirSalir() {

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    private void validarDiferida() {
        try {

            if (indDiferida) {
                dialogoVisibleDiferida = true;
            }
            else {
                if ("NO".equals(consecutivoParametrizado) &&

                    SysmanFunciones.comparaFechas(fechaVencimiento, fechaPago)
                    && "NO".equals(vigenciaAnterior)) {

                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4308"));

                    return;

                }

                indDiferida = false;

                if (ejbFactGeneralTres.verificarDescuentoCobro(compania,
                                tipoCobro, new BigInteger(noFactura))) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB4310"));

                }
                else {
                	
                	try {
                		
                		double vlrPagar = Double.parseDouble(SysmanFunciones.nvl(saldo,"0").toString());
                	
	                	if ((manPagoParcial && (Double.valueOf(vlrFactura) != vlrPagar)) || indPagoParcial) {
	                		
	                		numeroComprobanteRecaudo = ejbFactGeneralTres
	                                .recaudarFactura(compania, tipoCobro,
	                                                new BigInteger(noFactura),
	                                                observaciones,
	                                                bancoPago, fechaPago,
	                                                Integer.parseInt(anio),
	                                                indDiferida, usuario, BigDecimal.valueOf(vlrPagar));
	                	} else {
	                	
	                		numeroComprobanteRecaudo = ejbFactGeneralTres
	                                    .recaudarFactura(compania, tipoCobro,
	                                                    new BigInteger(noFactura),
	                                                    observaciones,
	                                                    bancoPago, fechaPago,
	                                                    Integer.parseInt(anio),
	                                                    indDiferida, usuario, BigDecimal.valueOf(0));
	                		
	                		JsfUtil.agregarMensajeInformativo(
	                                idioma.getString("TB_TB4309"));
	                	}
	                	JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4307"));
                	}
                	
                	catch (NumberFormatException e) {
                		e.printStackTrace();
                	}

                    generarReporteComprobanteRecaudo();

                    bloqueoRecaudo = true;
                    bloqueoCodRet = true;
                }
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void validarNoCobroCausacion(String tipoFactura, String anio) {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TIPOCOBRO", tipoFactura);
        param.put("ANOCOBRO", anio);

        try {
            Registro registro = RegistroConverter
                            .toRegistro(
                                            requestManager.get(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmRegistroPagosControladorUrlEnum.URL4444
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            if (registro != null) {
                noCausar = (boolean) registro.getCampos()
                                .get("NOAPLICACAUSACION");
            }
            else {
                noCausar = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al cambiar el control TipoFactura
     * 
     */
    public void cambiarTipoFactura() {
        cargarListaNoFactura();

    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * validarDiferida en la vista
     *
     */
    public void aceptarValidarDiferida() {

        String[] arrayMensaje;
        Date fechaAb;

        dialogoVisibleDiferida = false;
        try {
            String mensaje = ejbFactGeneralCero.consultarAbono(compania,
                            tipoAbono, new BigInteger(abono));

            if ("NE".equals(mensaje)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4311"));

            }
            else {
                arrayMensaje = mensaje.split(",");
                cuotaAb = arrayMensaje[0];
                fechaAb = SysmanFunciones.convertirAFecha(arrayMensaje[1]);
                valorAb = arrayMensaje[2];

                registrarPagoDiferido = true;

                if (SysmanFunciones.comparaFechas(fechaAb, new Date())) {
                    dialogoCuotaVencida = true;
                    registrarPagoDiferido = false;
                }

                if (registrarPagoDiferido) {
                    registrarPagoDiferida();
                }

            }
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void registrarPagoDiferida() {
        try {

            numeroComprobanteRecaudo = ejbFactGeneralCuatro
                            .manejarInterfazContableAbono(compania, tipoCobro,
                                            new BigInteger(codigoCobro),
                                            tipoAbono, new BigInteger(abono),
                                            Integer.parseInt(cuotaAb),
                                            fechaPago, tercero, sucursal,
                                            indManejaInventario, bancoPago,
                                            new BigDecimal(valorAb),
                                            usuario);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4307"));

            generarReporteComprobanteRecaudo();

        }
        catch (SystemException e) {
            logger.error(e.getMessage(),
                            e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * ValidarDiferida en la vista
     *
     */
    public void cancelarValidarDiferida() {
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4309"));
        dialogoVisibleDiferida = false;
        indDiferida = false;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * CuotaVencida en la vista
     *
     *
     */
    public void aceptarCuotaVencida() {
        registrarPagoDiferida();

        dialogoCuotaVencida = false;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * CuotaVencida en la vista
     *
     */
    public void cancelarCuotaVencida() {

        registrarPagoDiferido = false;
        dialogoCuotaVencida = false;

    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * validar TRM en la vista
     *
     *
     */
    public void aceptarValidarTRM() 
    {    
    	try 
    	{
			ejbFactGeneralTres.actualizarTotalFacturaxTRM(compania,
			        tipoFactura,
			        new BigInteger(noFactura), new BigDecimal(vlrFactura), 
			        new BigDecimal(valorTRM), Integer.parseInt(anio));
			
			recaudar();
    		dialogoVisibleTRM = false; 
		} 
    	catch (SystemException | NumberFormatException e) 
    	{
			e.printStackTrace();
		}    		     	
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * CuotaVencida en la vista
     *
     */
    public void cancelarValidarTRM() {
        
        dialogoVisibleTRM = false;
        return;

    }
    
    
    public String oprimirBtnEnviarSIGEC() {
    	/*
		 * RUTINA PARA A. SERVICIO REPORTE DE ACTO/DOCUMENTO
		 */
		String url;
		String token = null;
		String log = null;
		archivoDescarga = null;

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST GATEWAY SIGEC", "1", new Date(), false);
			token = ejbSysmanUtil.consultarParametro(compania, "TOKEN AUTORIZACION SIGEC", "1", new Date(), false);
		
			log = "|---------------         LOG DE LOGICA SERVICIO ACTO DOCUMENTO / SIGEC        ---------------|";

			log = log + "\n" + servicioActo_Documento(token, url);
			
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "Log.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return log;

	}

    
    private String servicioActo_Documento(String token, String url) {
		String respuesta = "";
		String json = null;
		String nroContratoSigec = null;
		String tipoContratoSigec = null;

		Map<String, Object> param1 = new TreeMap<>();
		param1.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param1.put(campoAnio, anio);
		param1.put(GeneralParameterEnum.TIPO.getName(), tipoFactura);
		param1.put(GeneralParameterEnum.NUMERO.getName(), noFactura);

		try {
			Registro rsSigec = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmRegistroPagosControladorUrlEnum.URL666020.getValue())
							.getUrl(), param1));

			if (rsSigec != null) {
				nroContratoSigec = rsSigec.getCampos().get(FrmRegistroPagosControladorEnum.NROCONTRATOSIGEC.getValue()).toString();
				tipoContratoSigec = rsSigec.getCampos().get(FrmRegistroPagosControladorEnum.TIPOCONTRATOSIGEC.getValue()).toString();
			}
		
		Map<String, Object> params = new TreeMap<>();
		SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), tercero);
		params.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContratoSigec);//claseF);//(String) parametrosEntrada.get(PARAMETRO_CLASEF);
		params.put(GeneralParameterEnum.NUMERO.getName(), nroContratoSigec);
	
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmRegistroPagosControladorUrlEnum.URL1928003.getValue())
											.getUrl(),
									params));

			ParametrosSIGEC param = new ParametrosSIGEC();
			String fechaInicio = formatFecha.format(rs.getCampos().get(FrmRegistroPagosControladorEnum.FECHAINICIO.getValue()));
			String fechaFin = formatFecha.format(rs.getCampos().get(FrmRegistroPagosControladorEnum.FECHAFINALIZACION.getValue()));
			BigInteger valorBigInteger = (BigInteger) rs.getCampos().get(FrmRegistroPagosControladorEnum.PLATAFORMA.getValue());
			BigInteger valorBigInteger1 = (BigInteger) rs.getCampos().get(FrmRegistroPagosControladorEnum.VALORTOTAL.getValue());
			
			Integer plataforma = valorBigInteger.intValue();
			Integer valorTotal = valorBigInteger1.intValue();			
			
			param.setPlatform(plataforma);
			param.setActDocumentCode(SysmanFunciones.nvl(rs.getCampos().get(FrmRegistroPagosControladorEnum.EQUIV_SIGEC.getValue()), "").toString());
			param.setGeneratorFactValue(valorTotal);
			param.setPayerDocumentParametricTypeCode(SysmanFunciones.nvl(rs.getCampos().get(FrmRegistroPagosControladorEnum.SIGEC.getValue()), "").toString());
			param.setTaxpayerDocumentNumber(SysmanFunciones.nvl(rs.getCampos().get(FrmRegistroPagosControladorEnum.TERCERO.getValue()), "").toString());
			param.setTaxpayerName(SysmanFunciones.nvl(rs.getCampos().get(FrmRegistroPagosControladorEnum.NOMBRE.getValue()), "").toString());
			param.setGeneratorFactStartDate(fechaInicio);
			param.setGeneratorFactEndDate(fechaFin);
			param.setParametricActDocumentCodeType(SysmanFunciones.nvl(rs.getCampos().get(FrmRegistroPagosControladorEnum.TIPO_SIGEC.getValue()), "").toString());

			Gson gson = new Gson();
			json = gson.toJson(param, ParametrosSIGEC.class);
			APISIGEC apiSigec = new APISIGEC();

			respuesta = apiSigec.postActoDocumento(token, url, json);
			RespuestaApiSigec respuestaApiSigec = gson.fromJson(respuesta, RespuestaApiSigec.class);
			respuestaApiSigec.getMessage();
			

		} catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta +  "\n" +
				"|------------------------------- JSON --------------------------------------------|"
				+ "\n" + json;

	}

	// </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoPago
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoPago(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoPago = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        nombreBanco = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tercero = registroAux.getCampos().get("TERCERO").toString();
    }

    public void seleccionarFilaNoFactura(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        noFactura = registroAux.getCampos()
                        .get(FrmRegistroPagosControladorEnum.NUMERO_FACTURA
                                        .getValue())
                        .toString();

        codigoCobro = registroAux.getCampos().get("CODIGO_COBRO").toString();

        anio = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.ANO.getName()),
                        "0").toString();

        fechaExpedicion = (Date) registroAux.getCampos()
                        .get("FECHA_EXPEDICION");

        fechaVencimiento = (Date) registroAux.getCampos()
                        .get("FECHA_VENCIMIENTO");

        vlrFactura = registroAux.getCampos().get("VALOR_TOTAL").toString();
        
        tercero = registroAux.getCampos()
                        .get(GeneralParameterEnum.TERCERO.getName()).toString();

        sucursal = registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName())
                        .toString();

        nombreTercero = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();

        tipoC = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPO_FACTURA"), "")
                        .toString();

        tipoAbono = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPO_ABONO"), "")
                        .toString();

        abono = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NRO_ABONO"), "")
                        .toString();

        indInterfazada = (boolean) registroAux.getCampos().get("INTERFAZADA").toString().equalsIgnoreCase("true"); // JM MOD CC 3052

        indDiferida = (boolean) registroAux.getCampos().get("DIFERIDA").toString().equalsIgnoreCase("true");  // JM MOD CC 3052
        
        indPagosParciales = (boolean) registroAux.getCampos().get("PAGOS_PARCIALES").toString().equalsIgnoreCase("true");  // JM MOD CC 3052 // JM MOD CC 3052
        
        valorAb = "0";
        
        valorTRM = "0";

        if (indDiferida) {

            valorAb = valorCuotaAbonoDiferida();

        }
        
        bloqueoRecaudo = false;
        bloqueoCodRet = false;
        
        if (indPagosParciales) {

            valorAb = valorCuotaAbonoParcial();

        }

        observaciones = SysmanFunciones
                        .nvl(registroAux.getCampos().get("OBSERVACIONES"), "")
                        .toString();

        if(manPagoParcial) {
        	
        	try {
        	Map<String, Object> params = new TreeMap<>();
        	params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.TIPO.getName(), tipoCobro);
            params.put(GeneralParameterEnum.FACTURA.getName(), noFactura);
            
            Parameter parameter = new Parameter();
            parameter.setFields(params);
            
            Registro saldoFactura = new Registro();
            saldoFactura = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                            		FrmconsultapagoparcialControladorUrlEnum.URL1955002
                                                            .getValue()).getUrl(), params));
            
	            if (Objects.isNull(saldoFactura)){
	                saldo = vlrFactura;
	                indPagoParcial = false;
	            }else{
	            	saldo = saldoFactura.getCampos().get("SALDO").toString();
	            	indPagoParcial = (boolean) saldoFactura.getCampos().get("IND_PAGOPARCIAL");
	            }
            
            
        	} catch (SystemException e){
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        	
        }

        validarBancos();

    }

    private String valorCuotaAbonoDiferida() {

        String[] arrayMensaje;
        String valorCuota = "";
        String mensaje;
        try {
            mensaje = ejbFactGeneralCero.consultarAbono(compania, tipoAbono,
                            new BigInteger(abono));

            if ("NE".equals(mensaje)) {
                valorCuota = "0";

            }
            else {
                arrayMensaje = mensaje.split(",");
                valorCuota = arrayMensaje[2];
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return valorCuota;
    }

    private String valorCuotaAbonoParcial() {

        String[] arrayMensaje;
        String valorCuota = "";
        String mensaje;
        int posicion= 0 ;
        try {
            mensaje = ejbFactGeneralABParciales.cuotasActPagoParcial(compania, Integer.parseInt(anio), tipoC, new BigInteger(noFactura));
            posicion = mensaje.indexOf("-");
            if (posicion > -1) {
            	arrayMensaje = mensaje.split("-");
                valorCuota = arrayMensaje[2];            	
            }
            else {
            	JsfUtil.agregarMensajeInformativoDialogo(mensaje);
                valorCuota = "0";
                bloqueoRecaudo = true;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return valorCuota;
    }
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoFactura
     *
     * @return tipoFactura
     */
    public String getTipoFactura() {
        return tipoFactura;
    }

    /**
     * Asigna la variable tipoFactura
     *
     * @param tipoFactura
     * Variable a asignar en tipoFactura
     */
    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = tipoFactura;
    }

    /**
     * Retorna la variable pagoBanco
     *
     * @return pagoBanco
     */
    public String getBancoPago() {
        return bancoPago;
    }

    /**
     * Asigna la variable pagoBanco
     *
     * @param pagoBanco
     * Variable a asignar en pagoBanco
     */
    public void setBancoPago(String bancoPago) {
        this.bancoPago = bancoPago;
    }

    /**
     * Retorna la variable tercero
     *
     * @return tercero
     */
    public String getTercero() {
        return tercero;
    }

    /**
     * Asigna la variable tercero
     *
     * @param tercero
     * Variable a asignar en tercero
     */
    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    /**
     * Retorna la variable noFactura
     *
     * @return noFactura
     */
    public String getNoFactura() {
        return noFactura;
    }

    /**
     * Asigna la variable noFactura
     *
     * @param noFactura
     * Variable a asignar en noFactura
     */
    public void setNoFactura(String noFactura) {
        this.noFactura = noFactura;
    }

    /**
     * Retorna la variable compRecaudo
     *
     * @return compRecaudo
     */
    public String getCompRecaudo() {
        return compRecaudo;
    }

    /**
     * Asigna la variable compRecaudo
     *
     * @param compRecaudo
     * Variable a asignar en compRecaudo
     */
    public void setCompRecaudo(String compRecaudo) {
        this.compRecaudo = compRecaudo;
    }

    /**
     * Retorna la variable vlrFactura
     *
     * @return vlrFactura
     */
    public String getVlrFactura() {
        return vlrFactura;
    }

    /**
     * Asigna la variable vlrFactura
     *
     * @param vlrFactura
     * Variable a asignar en vlrFactura
     */
    public void setVlrFactura(String vlrFactura) {
        this.vlrFactura = vlrFactura;
    }

    /**
     * Retorna la variable fechaPago
     *
     * @return fechaPago
     */
    public Date getFechaPago() {
        return fechaPago;
    }

    /**
     * Asigna la variable fechaPago
     *
     * @param fechaPago
     * Variable a asignar en fechaPago
     */
    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    /**
     * Retorna la variable nombreTercero
     *
     * @return nombreTercero
     */
    public String getNombreTercero() {
        return nombreTercero;
    }

    /**
     * Asigna la variable nombreTercero
     *
     * @param nombreTercero
     * Variable a asignar en nombreTercero
     */
    public void setNombreTercero(String nombreTercero) {
        this.nombreTercero = nombreTercero;
    }

    /**
     * Retorna la variable nombreBanco
     *
     * @return nombreBanco
     */
    public String getNombreBanco() {
        return nombreBanco;
    }

    /**
     * Asigna la variable nombreBanco
     *
     * @param nombreBanco
     * Variable a asignar en nombreBanco
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    /**
     * Retorna la variable nombreComprobante
     *
     * @return nombreComprobante
     */
    public String getNombreComprobante() {
        return nombreComprobante;
    }

    /**
     * Asigna la variable nombreComprobante
     *
     * @param nombreComprobante
     * Variable a asignar en nombreComprobante
     */
    public void setNombreComprobante(String nombreComprobante) {
        this.nombreComprobante = nombreComprobante;
    }

    /**
     * Retorna la variable fechaVencimiento
     *
     * @return fechaVencimiento
     */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * Asigna la variable fechaVencimiento
     *
     * @param fechaVencimiento
     * Variable a asignar en fechaVencimiento
     */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Retorna la variable fechaPagoBanco
     *
     * @return fechaPagoBanco
     */
    public Date getFechaPagoBanco() {
        return fechaPagoBanco;
    }

    /**
     * Asigna la variable fechaPagoBanco
     *
     * @param fechaPagoBanco
     * Variable a asignar en fechaPagoBanco
     */
    public void setFechaPagoBanco(Date fechaPagoBanco) {
        this.fechaPagoBanco = fechaPagoBanco;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaTipoFactura
     * 
     * @return listaTipoFactura
     */
    public List<Registro> getListaTipoFactura() {
        return listaTipoFactura;
    }

    /**
     * Asigna la lista listaTipoFactura
     * 
     * @param listaTipoFactura
     * Variable a asignar en listaTipoFactura
     */
    public void setListaTipoFactura(List<Registro> listaTipoFactura) {
        this.listaTipoFactura = listaTipoFactura;
    }

    /**
     * Retorna la lista listaCompRecaudo
     *
     * @return listaCompRecaudo
     */
    public List<Registro> getListaCompRecaudo() {
        return listaCompRecaudo;
    }

    /**
     * Asigna la lista listaCompRecaudo
     *
     * @param listaCompRecaudo
     * Variable a asignar en listaCompRecaudo
     */
    public void setListaCompRecaudo(List<Registro> listaCompRecaudo) {
        this.listaCompRecaudo = listaCompRecaudo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaTercero
     *
     * @return listaTercero
     */
    public RegistroDataModel getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     *
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModel listaTercero) {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaBancoPago
     * 
     * @return listaBancoPago
     */
    public RegistroDataModelImpl getListaBancoPago() {
        return listaBancoPago;
    }

    /**
     * Asigna la lista listaBancoPago
     * 
     * @param listaBancoPago
     * Variable a asignar en listaBancoPago
     */
    public void setListaBancoPago(RegistroDataModelImpl listaBancoPago) {
        this.listaBancoPago = listaBancoPago;
    }

    /**
     * Retorna la lista listaNoFactura
     *
     * @return listaNoFactura
     */
    public RegistroDataModelImpl getListaNoFactura() {
        return listaNoFactura;
    }

    /**
     * Asigna la lista listaNoFactura
     *
     * @param listaNoFactura
     * Variable a asignar en listaNoFactura
     */
    public void setListaNoFactura(RegistroDataModelImpl listaNoFactura) {
        this.listaNoFactura = listaNoFactura;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public boolean isBloqueaBanco() {
        return bloqueaBanco;
    }

    public void setBloqueaBanco(boolean bloqueaBanco) {
        this.bloqueaBanco = bloqueaBanco;
    }

    public boolean isDialogoVisibleDiferida() {
        return dialogoVisibleDiferida;
    }

    public void setDialogoVisibleDiferida(boolean dialogoVisibleDiferida) {
        this.dialogoVisibleDiferida = dialogoVisibleDiferida;
    }

    public boolean isDialogoCuotaVencida() {
        return dialogoCuotaVencida;
    }

    public void setDialogoCuotaVencida(boolean dialogoCuotaVencida) {
        this.dialogoCuotaVencida = dialogoCuotaVencida;
    }

    public boolean isBloqueoRecaudo() {
        return bloqueoRecaudo;
    }

    public void setBloqueoRecaudo(boolean bloqueoRecaudo) {
        this.bloqueoRecaudo = bloqueoRecaudo;
    }

    public boolean isDesdeFacConceptos() {
        return desdeFacConceptos;
    }

    public void setDesdeFacConceptos(boolean desdeFacConceptos) {
        this.desdeFacConceptos = desdeFacConceptos;
    }

    public String getValorAb() {
        return valorAb;
    }

    public void setValorAb(String valorAb) {
        this.valorAb = valorAb;
    }

	/**
	 * @return the valorTRM
	 */
	public String getValorTRM() {
		return valorTRM;
	}

	/**
	 * @param valorTRM the valorTRM to set
	 */
	public void setValorTRM(String valorTRM) {
		this.valorTRM = valorTRM;
	}

	/**
	 * @return the vlrTRMFactura
	 */
	public String getVlrTRMFactura() {
		return vlrTRMFactura;
	}

	/**
	 * @param vlrTRMFactura the vlrTRMFactura to set
	 */
	public void setVlrTRMFactura(String vlrTRMFactura) {
		this.vlrTRMFactura = vlrTRMFactura;
	}

	/**
	 * @return the visibleTRM
	 */
	public boolean isVisibleTRM() {
		return visibleTRM;
	}

	/**
	 * @param visibleTRM the visibleTRM to set
	 */
	public void setVisibleTRM(boolean visibleTRM) {
		this.visibleTRM = visibleTRM;
	}

	/**
	 * @return the dialogoVisibleTRM
	 */
	public boolean isDialogoVisibleTRM() {
		return dialogoVisibleTRM;
	}

	/**
	 * @param dialogoVisibleTRM the dialogoVisibleTRM to set
	 */
	public void setDialogoVisibleTRM(boolean dialogoVisibleTRM) {
		this.dialogoVisibleTRM = dialogoVisibleTRM;
	}
	
	/**
	 * @return the visibleSigec
	 */
	public boolean isVisibleSigec() {
		return visibleSigec;
	}

	/**
	 * @param visibleSigec the visibleSigec to set
	 */
	public void setVisibleSigec(boolean visibleSigec) {
		this.visibleSigec = visibleSigec;
	}

	/**
	 * @return the bloqueoCodRet
	 */
	public boolean isBloqueoCodRet() {
		return bloqueoCodRet;
	}

	/**
	 * @param bloqueoCodRet the bloqueoCodRet to set
	 */
	public void setBloqueoCodRet(boolean bloqueoCodRet) {
		this.bloqueoCodRet = bloqueoCodRet;
	}
	
	/**
	 * @return the visibleCodifRet
	 */
	public boolean isVisibleCodifRet() {
		return visibleCodifRet;
	}

	/**
	 * @param visibleCodifRet the visibleCodifRet to set
	 */
	public void setVisibleCodifRet(boolean visibleCodifRet) {
		this.visibleCodifRet = visibleCodifRet;
	}
	
	/**
	 * @return the campoAnio
	 */
	public String getCampoAnio() {
		return campoAnio;
	}

	/**
	 * @param campoAnio the campoAnio to set
	 */
	public void setCampoAnio(String campoAnio) {
		this.campoAnio = campoAnio;
	}
	
    /**
     * Retorna la variable saldo
     * 
     * @return  saldo
     */
	public String getSaldo() {
        return saldo;
    }
    /**
     * Asigna la variable  saldo
     * 
     * @param  saldo
     * Variable a asignar en  saldo
     */
    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }
    
    /**
	 * @return the manPagoParcial
	 */
	public boolean isManPagoParcial() {
		return manPagoParcial;
	}

	/**
	 * @param manPagoParcial the manPagoParcial to set
	 */
	public void setManPagoParcial(boolean manPagoParcial) {
		this.manPagoParcial = manPagoParcial;
	}
    
}
