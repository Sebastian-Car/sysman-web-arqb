package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
import com.sysman.contabilidad.enums.ComprobantecntsControladorUrlEnum;
import com.sysman.contabilidad.enums.SubdetallecomprobantecntsControladorEnum;
import com.sysman.contabilidad.enums.SubdetallecomprobantecntsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Usuario;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.print.attribute.standard.RequestingUserName;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.search.FieldCache.DoubleParser;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 07/03/2016
 *
 * @author jlramirez
 * @version 2, 11/04/2017, proceso de Refactoring y modificaciones segun especificaciones de SONARLINT, Manejo EJBs
 *
 * @author jrodrigueza
 * @version 3, 25/05/2017 Funcionalidad generar comprobante presupuestal desde dialogo, antes de cerrar el formulario, eliminada.
 *
 * @author dcastiblanco
 * @version 4, 19/07/2021 Se realiza validacion para que cargue la fuente cuando la cuenta tiene validado un fuente en plan contable.
 *
 */
@ManagedBean
@ViewScoped
public class SubdetallecomprobantecntsControlador
                extends BeanBaseContinuoAcmeImpl
{

    /**
     * Para redireccionar al formulario del comprobante contable.
     */
    private static final String RUTA_ENCABEZADO_CONTAB = "/comprobantecnt.sysman";
    private static final String CTIPOCPTEAFECT = "TIPO_CPTE_AFECT";
    private static final String CTIPOMOV = "tipoMov";
    private static final String COPCIONMENU = "opcionMenu";
    private static final String CCUENTAPPTAL = "CUENTAPPTAL";
    private static final String CNOMCENTROCOSTO = "NOMBRECENTROCOSTO";
    private static final String CNOMBRETERCERO = "NOMBRETERCERO";
    private static final String CNOMBREAUXILIAR = "NOMBREAUXILIAR";
    private static final String CVALORDEBITO = "VALOR_DEBITO";
    private static final String CVALORCREDITO = "VALOR_CREDITO"; 
    private static final String CSALDO = "SALDO";
    private static final String CCMPTEAFECTADO = "CMPTE_AFECTADO";
    private static final String CANOAFECT = "ANO_AFECT";
    private static final String CCONCEPTOEX = "CONCEPTO_EX";
    private static final String CNOMBREPCONTABLE = "NOMBREPLANCONTABLE";
    private static final String CCLASECUENTA = "CLASECUENTA";
    private static final String CMENSAJEREV = "mensajeRev";
    private static final String CSALDO13 = "SALDO13";
    private static final String CPORCRETENCION = "PORCRETENCION";
    private static final String CPERMITEMODBASE = "PERMITEMODIFICARBASE";
    private static final String CNRODOCUMENTO = "NRO_DOCUMENTO";
    private static final String CDRESPSUCURSALCNT = "D_RESPSUCURSALCNT";
    private static final String CBASEGRAVABLE = "BASE_GRAVABLE";
    private static final String TIPO = "TIPO";
    private static final String CODIGOE = "CODIGO";
    private static final String CSALDOC = "#$saldo$#";
    private Usuario usuario;
    
    private String compania;
    private String modulo;
    private Map<String, Object> rid;
    private String anio;
    private Date fechaPar;
    private String fecha;
    private String tercero;
    private String sucursal;
    private String nitcompania;
    private String tipoComp;
    private String mes;
    private String titulo;
    private String numeroComp;
    private String encabezado;
    private String totalDebito;
    private String totalCredito;
    private String diferencia;
    private String auxNombre;
    private String auxSucursal;
    private String impreso;
    private String estadoPeriodo;
    private String clase;
    private String responsable;
    private String sucursalResponsable;
    private String referencia;
    private String fuenteRecurso;
    private String valorDocumento;
    private String debitoAux;
    private String creditoAux;
    private String saldoAuxAfec;
    private String valorGirar;
    private String tipoFactura;
    private String nFactura;
    private String anioFact;
    private String factura;
    private String rubro;
    private String mensaje;
    private String controlChequera;
    private String opcionMenu;
    private String tipoDistribuir;
    private String valorDistribuir;
    private String descripcion;
    private boolean permitemodificarbase;
    private boolean permitemodificarCredito;
    private boolean cargarSaldoCuentaAux;
    private boolean porcentajeBloqueado;
    private boolean porcentajeRetencionBloqueado;
    private boolean validaAuxiliares;
    private boolean porcentajeRetencionCargar;
    private boolean reconocimientoCargar;
    private boolean fechaPlanoCargar;
    private boolean cargaPptal;
    private boolean cargaResDep;
    private boolean activoReg;
    private boolean activoReg2;
    private boolean creditoNuevoBloqueado;
    private boolean debitoNuevoBloqueado;
    private boolean afectadoBloqueado;
    private boolean afecta;
    private boolean facturacionVisible;
    private boolean rubroVisible;
    private boolean fechaBloqueada;
    private boolean bloqConceptoCUDS =  true;
    private boolean bloquearProceso = false;

	boolean manFuente = false;
    boolean manAuxiliar = false;
    boolean manReferencia = false;
    boolean manCentro = false;
    private String filtroFuentes;
    private String filtrosCentros;
    private String filtrosReferencias;
    private String filtrosAuxiliares;
    private boolean distribucionVisible;
    /**
     * Indicador que permite cerrar el formulario del Detalle Contable.
     */
    private boolean cerrar;
    private boolean manejaTercero;
    private boolean manejaReferencia;
    private boolean manejaCentro;
    private boolean manejaAuxiliar;
    /**
     * Indicador que valida si la cuenta maneja fuente de recursos
     */
    private boolean manejaFuente;
    /**
     * Indicadto que permite validar si el combo de auxiliar se bloquea o no
     */
    private boolean bloqueaAuxiliar = false;
    private boolean conceptoExBloqueado;
    /**
     * Activa el campo cuenta(CB1986)
     */
    private boolean bloqueacuentas = true;

    private RegistroDataModelImpl listaCentroCosto;
    private RegistroDataModelImpl listaCentroCostoE;
    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaTerceroE;
    private RegistroDataModelImpl listaAuxiliar;
    private RegistroDataModelImpl listaAuxiliarE;
    private RegistroDataModelImpl listaCmpteAfectado;
    private RegistroDataModelImpl listaCmpteAfectadoE;
    private RegistroDataModelImpl listaConceptoEx;
    private RegistroDataModelImpl listaConceptoExE;
    private RegistroDataModelImpl listaBancoRetencion;
    private RegistroDataModelImpl listaBancoRetencionE;
    private RegistroDataModelImpl listaCuenta;
    private RegistroDataModelImpl listaCuentaE;
    private RegistroDataModelImpl listaResponsable;
    private RegistroDataModelImpl listaResponsableE;
    private RegistroDataModelImpl listaRubro;
    private RegistroDataModelImpl listaRubroE;
    private RegistroDataModelImpl listaCmpReferencia;
    private RegistroDataModelImpl listaCmpReferenciaE;

    private RegistroDataModelImpl listaCmbFuenteRecurso;
    private RegistroDataModelImpl listaCmbFuenteRecursoE;
    private RegistroDataModelImpl listaCodigoNota;
    private RegistroDataModelImpl listaCodigoNotaE;

    /**
     * Copia del valor de la base gravable. Se captura en el evento activarEdicion.
     */
    private Object copiaValorBase;

    /**
     * Lista que carga los conceptos de flujo de efectivo
     */
    private RegistroDataModelImpl listaCodigoFlujoEfectivo;
    /**
     * Lista que carga los conceptos de flujo de efectivo en la grilla
     */
    private RegistroDataModelImpl listaCodigoFlujoEfectivoE;

    @EJB
    private EjbSysmanUtilRemote sysmanUtil;
    @EJB
    private EjbContabilidadSeisRemote contabilidadSeis;
    @EJB
    private EjbContabilidadUnoRemote contabilidadUno;
    /**
     * Lista de rubros presupuestales configurados en la equivalencia presupuestal de la cuenta contable. Lista de formulario nuevo registro.
     */
    private RegistroDataModelImpl listaCuentaPptal;
    /**
     * Lista de rubros presupuestales configurados en la equivalencia presupuestal de la cuenta contable. Lista del formulario continuo.
     */
    private RegistroDataModelImpl listaCuentaPptalE;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaConceptoCUDS;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaConceptoCUDSE;
   

	private Registro registroActivo;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;
    private List<Registro> listaTipoDoc;
    private List<Registro> listaDependencia;
    private List<Registro> listaTipoFactura;
    private List<Registro> listaNumeroFactura;
    private List<Registro> listaAnio;
    private List<Registro> listaGruposTesoreria;
    private List<Registro> gruposasignados;
    // private List<Registro> listaCuentaPresupuestal;
    private List<Registro> listaTipoCpteAfect;
    private List<Registro> listaprocesoJudicial;
    
    private int indice;
    private int indiceRevelaciones;
    private String valorGirarDialogo;
    private boolean dialogoValorGirarVisible;
    private double baseGravable;
    /**
     * Valor base para el calculo de IVA asignado al comprobante contable.
     */
    private double ivaFacturado;
    /**
     * Numeros de cheques, referencia de pago o numeros de factura asociado al comprobante contable.
     */
    private String numeroDocumento;
    /**
     * Codigo de la cuenta contable seleccionada.
     */
    private String cuenta;
    /**
     * Indica si se permite cambiar la cuenta presupuestal asociada a la cuenta contable.
     */
    private boolean permiteModificarCuentaPptal;
    /**
     * Codigo del rubro presupuestal capturado en el evento {@link #activarEdicion(Registro)}.
     */
    private String cuentaPptalAux;
    /**
     * Identifica la accion con la que se cargo el registro en el comprobante contable.
     */
    private String accionEncabezado;

    /**
     * Codigo del centro de costo seleccionado en el header
     */
    private String centroCosto;

    /**
     * Condigo del auxiliar-fuente recursos seleccionado en el header
     */
    private String auxiliarFuenteRecursos;

    /**
     * Condigo del auxiliar-fuente recursos seleccionado en el header
     */
    private String codigoFlujoEfectivo;
    /**
     * variable estado que valida el concepto
     */
    private boolean dialogoConceptos;
    private boolean dialogoComprobantePresupuestal;
    private boolean estadoComprobantePresupuestal;
    private String compRelacionado;
    private boolean redireccionar;

    /**
     * Atributo que indica si se bloquea o no el campo de flujo de efectivo
     */
    private boolean bloqueaFlujoEfectivo;
    private String idTipoFe;
    private String manejaControlFuente;
    private String SALDO;
    private boolean bloqNotaAjusteDs;
	private String paramModifCreditoGravable;
	private String copiaValCredito;
	private String copiaBaseGravable;
	private boolean registroNuevo;
	private String clasesOmitidas;
	private boolean cremilVisible;
	private boolean sigecVisible;
	
	private boolean funcionarioVisible;
	
	private boolean manejaPlanCompleto;
	
	

	/**
	 * indica si el comprobante relacionado a los detalles ha sido enviado a la dian
	 */
	private Boolean yaEnviadoDianExito;
	private String sucursalJud;
	private String terceroJud;
	private Map<String,Object> parametroswf;
    /**
     * Creates a new instance of SubdetallecomprobantecntsControlador
     */
    public SubdetallecomprobantecntsControlador()
    {
        super();
        try
        {
        	SessionUtil.setSessionVar("modulo", "1");
            usuario = SessionUtil.getUser();
            compania = SessionUtil.getCompania();
            modulo = SessionUtil.getModulo();
            nitcompania = SessionUtil.getCompaniaIngreso().getNit();
            estadoComprobantePresupuestal = true;
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                anio = extraerString(parametrosEntrada.get("anio"));
                tipoComp = extraerString(parametrosEntrada.get("tipoComp"));
                mes = extraerString(parametrosEntrada.get("mes"));
                numeroComp = extraerString(parametrosEntrada.get("numeroComp"));
                fechaPar = (Date) parametrosEntrada.get("fechaPar");
                tercero = extraerString(parametrosEntrada
                                .get(GeneralParameterEnum.TERCERO.getName()));
                sucursal = extraerString(parametrosEntrada.get("sucursal"));
                titulo = extraerString(parametrosEntrada.get("titulo"));
                impreso = extraerString(parametrosEntrada.get("impreso"));
                estadoPeriodo = extraerString(
                                parametrosEntrada.get("estadoPeriodo"));
                referencia = extraerString(parametrosEntrada.get("referencia"));
                valorDocumento = extraerString(
                                parametrosEntrada.get("valorDocumento"));
                valorGirar = extraerString(parametrosEntrada.get("valorGirar"));
                mensaje = extraerString(parametrosEntrada.get("mensaje"));
                controlChequera = extraerString(parametrosEntrada
                                .get("controlChequera"));
                opcionMenu = extraerString(parametrosEntrada.get(COPCIONMENU));
                descripcion = extraerString(
                                parametrosEntrada.get("descripcion"));
                accionEncabezado = extraerString(
                                parametrosEntrada.get("accion"));
                compRelacionado = extraerString(
                                parametrosEntrada.get("compRelacionado"));

                centroCosto = SysmanFunciones.nvlStr(extraerString(
                                parametrosEntrada.get("centroCosto")),
                                SysmanConstantes.CONS_CENTRO);

                auxiliarFuenteRecursos = SysmanFunciones.nvlStr(extraerString(
                                parametrosEntrada.get("auxiliar")),
                                SysmanConstantes.CONS_AUXILIAR);

                codigoFlujoEfectivo = SysmanFunciones.nvlStr(extraerString(
                                parametrosEntrada.get("codigoFlujoEfectivo")),
                                "");               

                redireccionar = false;
                
                yaEnviadoDianExito = (boolean) parametrosEntrada.get("yaEnviadoDian");
                parametroswf = (Map<String,Object>) parametrosEntrada.get("parametroswf");
            }
            else
            {
                SessionUtil.redireccionarMenuPermisos();
            }
            fecha = SysmanFunciones.convertirAFechaCadena(fechaPar);
            numFormulario = GeneralCodigoFormaEnum.SUBDETALLECOMPROBANTECNTS_CONTROLADOR
                            .getCodigo();
            indicadorClonarPermisos = true;
            validarPermisos();
        }
        catch (ParseException | SysmanException ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.DETALLECOMPROBANTECNT;
        buscarLlave();
        if(opcionMenu.equals("R")) {
        	bloqNotaAjusteDs = false;
        }else {
        	bloqNotaAjusteDs = true;
        }
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPOCPTE.getName(),tipoComp);
        bloqConceptoCUDS =  true;
        try
        {
        	manejaPlanCompleto = "SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
					"MUESTRA PLAN CONTABLE COMPLETO EN IMPUTACION", modulo, new Date(), true), "NO"));
        	
        	List<Registro> aux = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubdetallecomprobantecntsControladorUrlEnum.URL1895011
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            for (Registro aux1 : aux)
            {
                  if ( Integer.parseInt(SysmanFunciones.nvl(aux1.getCampos()
                                        .get("EXISTE"),
                                        "").toString()) > 0) {
                	  bloqConceptoCUDS =  false;
                	  
                  }
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
        }   
        

        encabezado = (tipoComp + " " + titulo + " - " + numeroComp)
                        .toUpperCase();
        reasignarOrigen();

        activoReg = false;
        activoReg2 = false;
        registro = new Registro();
        cargarListaTipoCpteAfect();
        cargarListaTipoDoc();
        cargarListaResponsable();
        cargarListaResponsableE();
        cargarListaDependencia();
        cargarListaCentroCosto();
        cargarListaCentroCostoE();
        cargarListaTercero();
        cargarListaTerceroE();
        cargarListaAuxiliar();
        cargarListaAuxiliarE();
        //cargarListaCmpteAfectado(); //JM 28/11/2024
        //cargarListaCmpteAfectadoE(); //JM 28/11/2024
        cargarListaConceptoEx();
        cargarListaConceptoExE();
        cargarListaBancoRetencion();
        cargarListaBancoRetencionE();
        cargarListaCuenta();
        cargarListaCuentaE();
        cargarListaCuentaPptal();
        cargarListaCuentaPptalE();
        cargarListaConceptoCUDS(); 
        cargarListaConceptoCUDSE();
        cargarListaCmpReferencia();
        cargarListaCmpReferenciaE();
        cargarListaCmbFuenteRecurso();
        cargarListaCmbFuenteRecursoE();

        cargarListaCodigoFlujoEfectivo();
        cargarListaCodigoFlujoEfectivoE();

        abrirFormulario();
        valorGirarDialogo = "0";       
        
    }

    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put("TIPOCOMP", tipoComp);
        parametrosListado.put("NUMEROCOMP", numeroComp);
        parametrosListado.put("MES", mes);
        try {
			if ("NO".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
					"SALDO DE CUENTA POR EL PERIODO DEL DOCUMENTO", modulo, new Date(), true), "NO"))) {
				parametrosListado.put("SALDO", mes);
			} else {
				parametrosListado.put("SALDO", "13");
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}

    }

    public void cargarListaTipoDoc()
    {
        try
        {
            listaTipoDoc = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL32536
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoFactura()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaTipoFactura = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL32918
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNumeroFactura()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioFact);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM0.getValue(),
                        tipoFactura);
        param.put(GeneralParameterEnum.TERCERO.getName(), tercero);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
        String strCuenta = SysmanFunciones.nvlStr(
                        extraerString(registro.getCampos().get(
                                        GeneralParameterEnum.CUENTA.getName())),
                        "");
        param.put(GeneralParameterEnum.CUENTA.getName(), strCuenta);
        try
        {
            listaNumeroFactura = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL33498
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnio()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL34590
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaResponsable()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL34974
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.RESPONSABLE.getName());

    }

    public void cargarListaResponsableE()
    {

        listaResponsableE = listaResponsable;

    }

    public void cargarListaDependencia()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.RESPONSABLE.getName(), responsable);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursalResponsable);

        try
        {
            listaDependencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL35978
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCentroCosto()
    {
        try
        {
            UrlBean urlBean = null;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            if ("SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(
                            compania, "MANEJA RESTRICCIONES EN CENTRO DE COSTO",
                            modulo, new Date(), true), "NO")))
            {
                if (manCentro)
                {
                    urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    SubdetallecomprobantecntsControladorUrlEnum.URL530
                                                                    .getValue());
                }
                else
                {
                    urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    SubdetallecomprobantecntsControladorUrlEnum.URL38336
                                                                    .getValue());
                }

                Object strCuenta = SysmanFunciones.nvlStr(
                                extraerString(registro.getCampos()
                                                .get(GeneralParameterEnum.CUENTA
                                                                .getName())),
                                "");
                param.put(GeneralParameterEnum.CUENTA.getName(), strCuenta);

            }
            else
            {
                if ("SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(
                                compania, "CONTROLA CENTRO COSTO PPTAL EN CONTABILIDAD",
                                modulo, new Date(), true), "NO")))
                {
                    if (manCentro)
                    {
                        urlBean = UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        SubdetallecomprobantecntsControladorUrlEnum.URL555
                                                                        .getValue());
                        param.put(SubdetallecomprobantecntsControladorEnum.CENTROS
                                        .getValue(), filtrosCentros);
                    }
                    else
                    {
                        urlBean = UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        SubdetallecomprobantecntsControladorUrlEnum.URL39720
                                                                        .getValue());
                    }

                }
                else
                {
                    urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    SubdetallecomprobantecntsControladorUrlEnum.URL39720
                                                                    .getValue());
                }
            }
            listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());

        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cargarListaCentroCostoE()
    {
        try
        {
            UrlBean urlBean;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);

            if ("SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(
                            compania, "CONTROLA CENTRO COSTO PPTAL EN CONTABILIDAD",
                            modulo, new Date(), true), "NO")))
            {
                if (manCentro)
                {
                    urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    SubdetallecomprobantecntsControladorUrlEnum.URL555
                                                                    .getValue());
                    param.put(SubdetallecomprobantecntsControladorEnum.CENTROS
                                    .getValue(), filtrosCentros);
                }
                else
                {
                    urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    SubdetallecomprobantecntsControladorUrlEnum.URL39720
                                                                    .getValue());
                }
            }
            else
            {
                urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantecntsControladorUrlEnum.URL39720
                                                                .getValue());
            }

            listaCentroCostoE = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());

        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCodigoNota()
    {
    	
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL29151
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.TIPO.getName(), idTipoFe);

        listaCodigoNota = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaCodigoNota
     *
     */
    public void cargarListaCodigoNotaE()
    {

        listaCodigoNotaE = listaCodigoNota;

    }

    public void cargarListaTercero()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL41839
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroE()
    {

        listaTerceroE = listaTercero;

    }

    /**
     *
     */
    public void cargarListaCmbFuenteRecurso()
    {

        UrlBean urlBean;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        if (manFuente)
        {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubdetallecomprobantecntsControladorUrlEnum.URL606
                                                            .getValue());
            param.put(SubdetallecomprobantecntsControladorEnum.FUENTES
                            .getValue(), filtroFuentes);

        }
        else
        {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubdetallecomprobantecntsControladorUrlEnum.URL48168
                                                            .getValue());
        }

        listaCmbFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCmbFuenteRecursoE()
    {

        listaCmbFuenteRecursoE = listaCmbFuenteRecurso;

    }

    public void cargarListaCmpReferencia()
    {
        UrlBean urlBean;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        if (manReferencia)
        {

            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubdetallecomprobantecntsControladorUrlEnum.URL672
                                                            .getValue());
            param.put(SubdetallecomprobantecntsControladorEnum.REFERENCIAS
                            .getValue(), filtrosReferencias);
        }
        else
        {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubdetallecomprobantecntsControladorUrlEnum.URL48167
                                                            .getValue());
        }

        listaCmpReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCmpReferenciaE()
    {

        listaCmpReferenciaE = listaCmpReferencia;

    }

    public void cargarListaAuxiliar()
    {
        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        if (manAuxiliar)
        {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubdetallecomprobantecntsControladorUrlEnum.URL704
                                                            .getValue());
            param.put(SubdetallecomprobantecntsControladorEnum.AUXILIARES
                            .getValue(), filtrosAuxiliares);
        }
        else
        {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubdetallecomprobantecntsControladorUrlEnum.URL43194
                                                            .getValue());
        }

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaAuxiliarE()
    {

        listaAuxiliarE = listaAuxiliar;
    }

    public void cargarListaCmpteAfectado()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL39122
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);        
        param.put(SubdetallecomprobantecntsControladorEnum.TERCEROCOMPROBANTE
                .getValue(),SysmanFunciones.nvl(registro.getCampos().
                		get(GeneralParameterEnum.TERCERO.getName()), tercero));
        param.put(SubdetallecomprobantecntsControladorEnum.SUCURSALCOMPROBANTE
                .getValue(),
                SysmanFunciones.nvl(registro.getCampos().
                		get(GeneralParameterEnum.SUCURSAL.getName()), sucursal));
        param.put(SubdetallecomprobantecntsControladorEnum.COMPRELACIONADO
                        .getValue(),
                        compRelacionado);
        param.put(SubdetallecomprobantecntsControladorEnum.FECHACOMPROBANTE
                        .getValue(),
                        fechaPar);
        param.put(SubdetallecomprobantecntsControladorEnum.ANOCOMPROBANTE
                        .getValue(),
                        anio);
      //JM INI 28/11/2024
        String cuentaComprobanteAfectar = SysmanFunciones.nvlStr(
                extraerString(registro.getCampos().get(GeneralParameterEnum.CUENTA.getName())),
                "");
        
        param.put(GeneralParameterEnum.CUENTA.getName(),
        		cuentaComprobanteAfectar);
        //JM FIN 28/11/2024

        String tipoComprobanteAfectar = SysmanFunciones.nvlStr(
                        extraerString(registro.getCampos().get(CTIPOCPTEAFECT)),
                        "");
        param.put(SubdetallecomprobantecntsControladorEnum.TIPO.getValue(),
                        tipoComprobanteAfectar);

        listaCmpteAfectado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    public void cargarListaCmpteAfectadoE()
    {

        listaCmpteAfectadoE = listaCmpteAfectado;
    }

    public void cargarListaConceptoEx()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL48172
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM4.getValue(),
                        "1001");
        listaConceptoEx = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaConceptoExE()
    {

        listaConceptoExE = listaConceptoEx;
    }

    public void cargarListaBancoRetencion()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL49710
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaBancoRetencion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaBancoRetencionE()
    {

        listaBancoRetencionE = listaBancoRetencion;
    }

    public void cargarListaCuenta()
    {
    	UrlBean urlBean = null ;
    	if(manejaPlanCompleto)
    	{
    		urlBean = UrlServiceUtil.getInstance()    		
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL16232
                                                        .getValue());
    	}
    	else
    	{
    		urlBean = UrlServiceUtil.getInstance()    		
                    .getUrlServiceByUrlByEnumID(
                                    SubdetallecomprobantecntsControladorUrlEnum.URL51446
                                                    .getValue());
    	}
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCuentaE()
    {

        listaCuentaE = listaCuenta;
    }

    public void cargarListaTipoCpteAfect()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubdetallecomprobantecntsControladorEnum.TERCEROCOMPROBANTE
                        .getValue(),SysmanFunciones.nvl(registro.getCampos().
                        		get(GeneralParameterEnum.TERCERO.getName()), tercero));
        param.put(SubdetallecomprobantecntsControladorEnum.SUCURSALCOMPROBANTE
                        .getValue(),
                        SysmanFunciones.nvl(registro.getCampos().
                        		get(GeneralParameterEnum.SUCURSAL.getName()), sucursal));
        param.put(SubdetallecomprobantecntsControladorEnum.COMPRELACIONADO
                        .getValue(),
                        compRelacionado);
        param.put(SubdetallecomprobantecntsControladorEnum.FECHACOMPROBANTE
                        .getValue(),
                        fechaPar);
        param.put(SubdetallecomprobantecntsControladorEnum.ANOCOMPROBANTE
                        .getValue(),
                        anio);
        try
        {
            listaTipoCpteAfect = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL48166
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaRubro()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL55412
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM7.getValue(),
                        getCuentaPptal());

        listaRubro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaRubroE()
    {

        listaRubroE = listaRubro;

    }

    /**
     *
     * Carga la lista listaCuentaPptal del formulario nuevo registro.
     */
    public void cargarListaCuentaPptal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL56874
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.CUENTA.getName()));

        listaCuentaPptal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
    }

    /**
     *
     * Carga la lista listaCuentaPptal asociada al formulario continuo.
     */
    public void cargarListaCuentaPptalE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL56874
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);

        listaCuentaPptalE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        

    }

    /**
     *
     * Carga la lista listaCodigoFlujoEfectivo
     *
     */
    public void cargarListaCodigoFlujoEfectivo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubdetallecomprobantecntsControladorUrlEnum.URL675
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoFlujoEfectivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCodigoFlujoEfectivo
     *
     */
    public void cargarListaCodigoFlujoEfectivoE()
    {

        listaCodigoFlujoEfectivoE = listaCodigoFlujoEfectivo;
    }
    /**
     * 
     * Carga la lista listaConceptoCUDS
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaConceptoCUDS(){
    	
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					SubdetallecomprobantecntsControladorUrlEnum.URL1895004
    					.getValue());

    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.ANIO.getName(), anio);

    	Object strCuenta = SysmanFunciones.nvlStr(
    			extraerString(registro.getCampos()
    					.get(GeneralParameterEnum.CUENTA
    							.getName())),
    			"");
    	
        if("".equals(strCuenta)) {
        	strCuenta =  cuenta;
        }
        param.put(GeneralParameterEnum.CODIGOCUENTA.getName(), strCuenta);
	
	    listaConceptoCUDS = new RegistroDataModelImpl(urlBean.getUrl(),
			    urlBean.getUrlConteo().getUrl(), param,
			    true, GeneralParameterEnum.CODIGO_CONCEPTO.getName());


    }
    /**
     * 
     * Carga la lista listaConceptoCUDS
     *
     * TODO DOCUMENTACION ADICIONAL
     */
public void  cargarListaConceptoCUDSE(){
	
	UrlBean urlBean = UrlServiceUtil.getInstance()
			.getUrlServiceByUrlByEnumID(
					SubdetallecomprobantecntsControladorUrlEnum.URL1895004
					.getValue());

	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	param.put(GeneralParameterEnum.ANIO.getName(), anio);

	Object strCuenta = SysmanFunciones.nvlStr(
			extraerString(registro.getCampos()
					.get(GeneralParameterEnum.CUENTA
							.getName())),
			"");
	if("".equals(strCuenta)) {
    	strCuenta =  cuenta;
    }
	param.put(GeneralParameterEnum.CODIGOCUENTA.getName(), strCuenta);

	listaConceptoCUDSE = new RegistroDataModelImpl(urlBean.getUrl(),
		    urlBean.getUrlConteo().getUrl(), param,
		    true, GeneralParameterEnum.CODIGO_CONCEPTO.getName());
}

/**
 * 
 * Carga la lista listaprocesoJudicial
 *
 */
public void cargarListaprocesoJudicial(){

	Map<String, Object> param = new TreeMap<>();
	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	param.put(GeneralParameterEnum.TERCERO.getName(), terceroJud);
	param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursalJud);

	try {
		listaprocesoJudicial = RegistroConverter.toListRegistro(
				requestManager.getList(UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								SubdetallecomprobantecntsControladorUrlEnum.URL1935007
								.getValue())
						.getUrl(), param));
	} catch (SystemException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
//</METODOS_CARGAR_LISTA>
    
    
    
    public void oprimirCalcularTotales()
    {
        asignarTotales();
    }

    public void oprimirGenerarcuentasPptal(ActionEvent ac)
    {
        // NO SE IMPLEMENTA
    }

    public void oprimirRevelaciones(Registro reg, int indice)
    {
        indiceRevelaciones = indice;
    }

    public void oprimirAuxRevelaciones()
    {
        String[] campos = { "anio", "tipoComp", "numeroComp", "consecutivo" };
        String consecutivo = extraerString(listaInicial.getDatasource()
                        .get(indiceRevelaciones % 10).getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName()));
        String[] valores = { anio, tipoComp, numeroComp, consecutivo };
        SessionUtil.cargarModalDatosFlashCerrar("567", modulo, campos, valores);
    }
    
	public void oprimirEliminarDetalleLote() {
		String[] campos = { "comprobante", "tipo", "anio" };
		Object[] valores = { numeroComp, tipoComp, anio };

		SessionUtil.cargarModalDatosFlash(
				Integer.toString(
						GeneralCodigoFormaEnum.ELIMINAR_DETALLE_CNT_LOTE_CONTROLADOR
						.getCodigo()),
				modulo, campos,
				valores);
	}
	
	public void retornarFormularioEliminarDetalleLote(SelectEvent event) {
		inicializar();
	}
	

    public void seleccionarFilaRubro(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        rubro = SysmanFunciones.nvlStr(
                        extraerString(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName())),
                        "");
    }

    public void seleccionarFilaRubroE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaPptal del dialogo Nuevo Registro.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaPptal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CCUENTAPPTAL, registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        try {
	        if ("SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(
	                compania, "MANEJA EQUIVALENTE PRESUPUESTAL FIJO AUTOMATICO",
	                modulo, new Date(), true), "NO")))
	        {
	        	registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()));
	        	
	        	registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()));
	        	
	        	registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()));
	        	
	        	registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.FUENTE_RECURSO.getName())); 
	        }
	        
        } catch (SystemException e){
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaPptal, ubicada en el formulario continuo.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaPptalE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        
        try {
	        if ("SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(
	                compania, "MANEJA EQUIVALENTE PRESUPUESTAL FIJO AUTOMATICO",
	                modulo, new Date(), true), "NO")))
	        {
	        	registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()));
	        	
	        	registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()));
	        	
	        	registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()));
	        	
	        	registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.FUENTE_RECURSO.getName())); 
	        }
	        
        } catch (SystemException e){
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoCUDS
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoCUDS(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("CONCEPTO_CUDS", registroAux.getCampos().get("CODIGO_CONCEPTO"));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConceptoCUDS
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoCUDSE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	auxiliar =  SysmanFunciones
                .nvl(registroAux.getCampos().get("CODIGO_CONCEPTO"),
                        "")
        .toString();
    }
    
    public void seleccionarFilaCentroCosto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(CNOMCENTROCOSTO, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
    }

    public void seleccionarFilaCentroCostoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        auxNombre = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
    }

    public void seleccionarFilaTercero(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(CNOMBRETERCERO, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        GeneralParameterEnum.SUCURSAL.getName()))
        {
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            sucursalJud = SysmanFunciones.toString(registroAux.getCampos()
                    .get(GeneralParameterEnum.SUCURSAL
                            .getName()));
        }
        else
        {
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            "999");
        }
        
        terceroJud = SysmanFunciones.toString(registroAux.getCampos().get("NIT"));
        
        cargarListaprocesoJudicial();
        registro.getCampos().put(CTIPOCPTEAFECT, "");
        registro.getCampos().put(CCMPTEAFECTADO, "");
        cargarListaTipoCpteAfect();
        cargarListaCmpteAfectado();
        terceroJud = null;
	    sucursalJud = null;

    }

    public void seleccionarFilaTerceroE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        auxNombre = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
        auxSucursal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()),
                                        "")
                        .toString();
        
        terceroJud = auxiliar;
        sucursalJud = auxSucursal;
        cargarListaprocesoJudicial();
        registro.getCampos().put(CTIPOCPTEAFECT, "");
        registro.getCampos().put(CCMPTEAFECTADO, "");
        cargarListaTipoCpteAfect();
        cargarListaCmpteAfectado();
        terceroJud = null;
	    sucursalJud = null;
        
    }

    public void seleccionarFilaCmbFuenteRecurso(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaCmbFuenteRecursoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .toString(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()));
        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoFlujoEfectivo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFlujoEfectivo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(
                        SubdetallecomprobantecntsControladorEnum.CODIGO_FLUJO_EFECTIVO
                                        .getValue(),
                        registroAux.getCampos().get("CODIGO").toString());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoFlujoEfectivo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFlujoEfectivoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    public void seleccionarFilaCmpReferencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        referencia = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();

        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaCmpReferenciaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .toString(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

    }

    public void seleccionarFilaAuxiliar(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(CNOMBREAUXILIAR, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
    }

    public void seleccionarFilaAuxiliarE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        auxNombre = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
    }

    public void seleccionarFilaCmpteAfectado(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        if (registro.getCampos().get(CVALORDEBITO) != null
            && Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos().get(CVALORDEBITO), "0.0")
                            .toString()) > Double.parseDouble(
                                            SysmanFunciones
                                                            .nvl(registroAux.getCampos()
                                                                            .get(CSALDO),
                                                                            "0.0")
                                                            .toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB767"));
            return;
        }
        if (registro.getCampos().get(CVALORCREDITO) != null
            && Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos().get(CVALORCREDITO), "0.0")
                            .toString()) > Double.parseDouble(
                                            SysmanFunciones
                                                            .nvl(registroAux.getCampos()
                                                                            .get(CSALDO),
                                                                            "0.0")
                                                            .toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB768"));
            return;
        }
        registro.getCampos().put(CCMPTEAFECTADO, registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()));
        registro.getCampos().put(CANOAFECT, registroAux.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));
        //JM INI 22/11/2024
        registro.getCampos().put(SubdetallecomprobantecntsControladorEnum.CONSECUTIVOAFECTADO.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString());
        
        String naturaleza = extraerString(
				registroAux.getCampos().get("NATURALEZA"));
        BigDecimal valor = new BigDecimal(
				extraerString(registroAux.getCampos()
						.get("SALDO")));
        
        if ("D".equals(naturaleza)) {
			registro.getCampos().put(CVALORCREDITO, valor);
			cambiarValorCredito();
		}
		else {
			registro.getCampos().put(CVALORDEBITO, valor);
			cambiarValorDebito();
		}
        //JM FIN 22/11/2024
 
       /* Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), registroAux.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()));
        param.put(GeneralParameterEnum.NUMERO.getName(), registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()));
        param.put(GeneralParameterEnum.CUENTA.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.CUENTA.getName()));
        param.put(GeneralParameterEnum.TIPO_CPTE.getName(), registro.getCampos().get(CTIPOCPTEAFECT)); //JM 22/11/2024
        
        /*try
        {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL127121
                                                                            .getValue())
                                            .getUrl(), param));
            if (reg != null)
            {
                registro.getCampos()
                                .put(SubdetallecomprobantecntsControladorEnum.CONSECUTIVOAFECTADO
                                                .getValue(),
                                                reg.getCampos().get(
                                                                GeneralParameterEnum.CONSECUTIVO
                                                                                .getName())
                                                                .toString());
                
               
               
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        } */ //comentado por JM 28/11/2024
    }

    public void seleccionarFilaCmpteAfectadoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO
                                                        .getName()),
                                        "")
                        .toString();
        saldoAuxAfec = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CSALDO), "")
                        .toString();
    }

    public void seleccionarFilaConceptoEx(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CCONCEPTOEX, registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaConceptoExE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    public void seleccionarFilaBancoRetencion(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("BANCO_RETENCION",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaBancoRetencionE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
    }

    public void seleccionarFilaCuenta(SelectEvent event)
    {

        bloqueaFlujoEfectivo = false;
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(CNOMBREPCONTABLE, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.NATURALEZA.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NATURALEZA
                                                        .getName()));
        registro.getCampos().put(CCLASECUENTA,
                        registroAux.getCampos().get(CCLASECUENTA));

        if ("I".equals(registroAux.getCampos().get(CCLASECUENTA)))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            "TB_TB4298"));
        }

        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registroAux.getCampos().get(
                                            GeneralParameterEnum.CODIGO
                                                            .getName()));

            Registro rsCuentaBloqueada = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL1258
                                                                            .getValue())
                                            .getUrl(), param));

            if ("SI".equals(rsCuentaBloqueada.getCampos().get("BLOQUEACUENTA")
                            .toString()))
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4239"));
                registro.getCampos().put(GeneralParameterEnum.CUENTA.getName(),
                                null);
                return;

            }

            manejaTercero = !"0"
                            .equals(SysmanFunciones
                                            .nvl(registroAux.getCampos().get(
                                                            "MANEJA_TERCERO"),
                                                            "")
                                            .toString());
            registro.getCampos().put("MAN_AUX_TER",
                            "-1".equals(registroAux.getCampos()
                                            .get(SubdetallecomprobantecntsControladorEnum.MANEJA_TERCERO
                                                            .getValue())
                                            .toString())
                                                ? SubdetallecomprobantecntsControladorEnum.TRUE
                                                                .getValue()
                                                                .toLowerCase()
                                                : SubdetallecomprobantecntsControladorEnum.FALSE
                                                                .getValue()
                                                                .toLowerCase());
            registro.getCampos().put("MAN_AUX_GEN",
                            "-1".equals(registroAux.getCampos()
                                            .get(SubdetallecomprobantecntsControladorEnum.MANEJA_AUXILIAR
                                                            .getValue())
                                            .toString())
                                                ? SubdetallecomprobantecntsControladorEnum.TRUE
                                                                .getValue()
                                                                .toLowerCase()
                                                : SubdetallecomprobantecntsControladorEnum.FALSE
                                                                .getValue()
                                                                .toLowerCase());
            registro.getCampos().put("MAN_CEN_CTO",
                            "-1".equals(registroAux.getCampos()
                                            .get(SubdetallecomprobantecntsControladorEnum.MANEJA_CENTRO
                                                            .getValue())
                                            .toString())
                                                ? SubdetallecomprobantecntsControladorEnum.TRUE
                                                                .getValue()
                                                                .toLowerCase()
                                                : SubdetallecomprobantecntsControladorEnum.FALSE
                                                                .getValue()
                                                                .toLowerCase());
            manejaCentro = !"0"
                            .equals(SysmanFunciones
                                            .nvl(registroAux.getCampos().get(
                                                            "MANEJA_CENTRO"),
                                                            "")
                                            .toString());
            manejaAuxiliar = !"0"
                            .equals(SysmanFunciones
                                            .nvl(registroAux.getCampos().get(
                                                            "MANEJA_AUXILIAR"),
                                                            "")
                                            .toString());
            manejaFuente = !"0"
                            .equals(SysmanFunciones
                                            .nvl(registroAux.getCampos().get(
                                                            "MANEJA_FUENTE"),
                                                            "")
                                            .toString());

            manejaReferencia = !"0"
                            .equals(SysmanFunciones
                                            .nvl(registroAux.getCampos().get(
                                                            "MANEJA_REFERENCIA"),
                                                            "")
                                            .toString());
            modificarBase(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(GeneralParameterEnum.CUENTA
                                                            .getName()),
                                            "")
                            .toString());
            registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                            SysmanFunciones.validarVariableVacio(tercero)
                                ? SysmanConstantes.CONS_TERCERO
                                : tercero);
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);
            registro.getCampos().put(CCONCEPTOEX,
                            traerConceptoExogena(registroAux.getCampos()));

            registro.getCampos().put("CENTRO_COSTO",
                            "0".equals(registroAux.getCampos()
                                            .get("MANEJA_CENTRO").toString())
                                                ? centroCosto
                                                : null);

            registro.getCampos().put("AUXILIAR",
                            "0".equals(registroAux.getCampos()
                                            .get("MANEJA_AUXILIAR").toString())
                                                ? auxiliarFuenteRecursos
                                                : null);

            conceptoExBloqueado = !(boolean) registroAux.getCampos()
                            .get("MOSTRARF1001");

            if ("SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(
                            compania, "MANEJA FACTURACION", modulo, new Date(),
                            true), "NO"))
                && "C".equals(getClaseCuenta(CCLASECUENTA))
                && "S".equals(clase))
            {
                afecta = "SI".equals(SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "AFECTAR FACTURAS DE ARRENDAMIENTO",
                                                modulo, new Date(), true),
                                                "NO"));
                cargarListaTipoFactura();
                cargarListaNumeroFactura();
                cargarListaAnio();
                facturacionVisible = true;

            }
            evaluarManejos();
            if ("I".equals(clase) || "B".equals(clase))
            {
                validarCuentasEquivalentes(false);
            }

            if (!Boolean.parseBoolean(registroAux.getCampos()
                            .get(SubdetallecomprobantecntsControladorEnum.MOSTRAR_EN_FLUJO
                                            .getValue())
                            .toString()))
            {

                bloqueaFlujoEfectivo = true;
                registro.getCampos().put(
                                SubdetallecomprobantecntsControladorEnum.CODIGO_FLUJO_EFECTIVO
                                                .getValue(),
                                "");
            }
            else
            {

                if (!evaluarFlujoEfectivo())
                {
                    String flujoEfectivo = SysmanFunciones
                                    .nvl(registro.getCampos().get(
                                                    SubdetallecomprobantecntsControladorEnum.CODIGO_FLUJO_EFECTIVO
                                                                    .getValue()),
                                                    "")
                                    .toString();
                    if (SysmanFunciones.validarVariableVacio(flujoEfectivo))
                    {
                        JsfUtil.agregarMensajeAlerta(
                                        idioma.getString("TB_TB4318"));
                    }
                }
            }            
         // INI_7746161_CONTABILIDAD mrosero      
			
			try {
				boolean legalizacion=false;

				param.put(GeneralParameterEnum.TIPO_CPTE.getName(), tipoComp);
				Registro aux = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										SubdetallecomprobantecntsControladorUrlEnum.URL15080.getValue())
								.getUrl(),
						param));

				legalizacion = (boolean) aux.getCampos().get("LEGALIZACION");

				if (legalizacion && registro.getCampos().get(GeneralParameterEnum.CLASECUENTA.getName()).toString().equals("I")) {

					Map<String, Object> param1 = new TreeMap<>();
					param1.put(GeneralParameterEnum.ANO.getName(), anio);
					param1.put(GeneralParameterEnum.CUENTA.getName(),
							registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()));

					Registro rscuenta = RegistroConverter.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubdetallecomprobantecntsControladorUrlEnum.URL12016.getValue())
									.getUrl(),
							param1));

					if (rscuenta.getCampos()!=null) {
						
						double pctAplicar = Double.parseDouble((rscuenta.getCampos().get("PCT_APLICAR")).toString());
						double pctBase = Double.parseDouble((rscuenta.getCampos().get("PCT_BASE")).toString());
						
						double ValorTotalRetencion = ((baseGravable * pctAplicar)/pctBase);

						registro.getCampos().put(CVALORCREDITO, ValorTotalRetencion);
					} 
				}

			} catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
            // FIN_7746161_CONTABILIDAD mrosero
                if ("SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(
                            compania, "CALCULA VALOR CREDITO AUTOMATICO EN CUENTA POR PAGAR", modulo, new Date(),
                            true), "NO"))) {

                	
			// INI_7735123_CONTABILIDAD mrosero
			String clase = registro.getCampos().get(GeneralParameterEnum.CLASECUENTA.getName()).toString();

			if (clase.equals("P") || clase.equals("E")) {

				String valortotalCredito = totalCredito.replace(".00", "").replaceAll(",", "");
				String valortotaldebito = totalDebito.replace(".00", "").replaceAll(",", "");
				
				// Convierte la cadena resultante en un valor double.
				
				double valortotalCredito1 = Double.parseDouble(valortotalCredito);
				double valortotaldebito1 = Double.parseDouble(valortotaldebito);

				if (valortotalCredito1 > valortotaldebito1) {
					registro.getCampos().put(CVALORCREDITO, "0");
				} else {

					String diferencia1 = diferencia.replace(".00", "").replaceAll(",", "");
					// Convierte la cadena resultante en un valor double.
					double valorDouble = Double.parseDouble(diferencia1);

					registro.getCampos().put(CVALORCREDITO, valorDouble);
				}

			}
			
                }
			// FIN_7735123_CONTABILIDAD mrosero
			
			
                
            cargarListaCuentaPptal();
            cargarListaCuentaPptalE();
            cargarListaConceptoCUDS();
            cargarListaConceptoCUDSE();
            
        }

        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoNota
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoNota(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_NOTA",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoNota
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoNotaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    public void evaluarManejos()
    {
        try
        {
            if ("B".equals(getClaseCuenta(CCLASECUENTA)))
            {
                registro.getCampos().put("PORCENTAJERETENCION", "100");
                porcentajeBloqueado = true;
            }
            else
            {
                porcentajeBloqueado = false;
            }

            if ("SI".equals(SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "MANEJA RESTRICCIONES EN CENTRO DE COSTO",
                                            modulo,
                                            new Date(), true), "NO")))
            {
                registro.getCampos().put(
                                GeneralParameterEnum.CENTRO_COSTO.getName(),
                                null);
                registro.getCampos().put(CNOMCENTROCOSTO, null);
                cargarListaCentroCosto();
            }
            if (manejaTercero)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB769"));
            }
            if (manejaCentro)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB770"));
            }
            if (manejaAuxiliar)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB771"));
            }
            if (manejaFuente)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4125"));
            }

            if (manejaFuente && !manejaAuxiliar)
            {

                bloqueaAuxiliar = true;
            }
            else
            {
                bloqueaAuxiliar = false;
            }
            if ("SI".equals(SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "MANEJA DISTRIBUCION CONTABLE",
                                            modulo, new Date(), true),
                                            "NO")))
            {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), anio);
                param.put(GeneralParameterEnum.CUENTA.getName(), SysmanFunciones
                                .nvl(registro.getCampos()
                                                .get(GeneralParameterEnum.CUENTA
                                                                .getName()),
                                                "")
                                .toString());
                Registro regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubdetallecomprobantecntsControladorUrlEnum.URL44003
                                                                                .getValue())
                                                .getUrl(), param));

                if (!"0".equals(SysmanFunciones.nvl(regAux.getCampos()
                                .get(GeneralParameterEnum.CUENTA.getName()),
                                " ")
                                .toString()))
                {
                    distribucionVisible = true;
                }
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Trae el codigo del concepto de exogena configurado para la cuenta seleccionada.
     *
     * @param mapCuenta
     * Datos de la cuenta seleccionada en el combo grande.
     * @return concepto de exogena asociado a la cuenta.
     */
    private String traerConceptoExogena(Map<String, Object> mapCuenta)
    {
        String exogena = "";
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        SysmanFunciones.nvl(mapCuenta.get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "").toString());
        Registro registro;
        try
        {
            registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL53095
                                                                            .getValue())
                                            .getUrl(), param));
            if (registro != null)
            {
                exogena = SysmanFunciones
                                .nvl(registro.getCampos().get("CONCEPTOEX"), "")
                                .toString();
            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return exogena;
    }

    public void seleccionarFilaCuentaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        auxNombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();

        if ("I".equals(registroAux.getCampos().get(CCLASECUENTA)))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            "TB_TB4298"));
        }
    }

    public void seleccionarFilaResponsable(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("D_RESPONSABLECNT",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.RESPONSABLE
                                                        .getName()));
        registro.getCampos().put(CDRESPSUCURSALCNT,
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
        registro.getCampos().put("D_DEPENDENCIACNT", null);
        responsable = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.RESPONSABLE
                                                        .getName()),
                                        "")
                        .toString();
        sucursalResponsable = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()), "")
                        .toString();
        cargarListaDependencia();
    }

    public void seleccionarFilaResponsableE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.RESPONSABLE
                                                        .getName()),
                                        "")
                        .toString();
        auxSucursal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()), "")
                        .toString();
        activoReg2 = false;
    }

    public void cambiarResponsableC(int rowNum)
    {
        if (!activoReg2)
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CDRESPSUCURSALCNT, auxSucursal);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put("D_DEPENDENCIACNT", null);
        }

        responsable = extraerString(
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("D_RESPONSABLECNT"));
        sucursalResponsable = extraerString(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(CDRESPSUCURSALCNT));
        cargarListaDependencia();
    }

    public void cambiarCentroCostoC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(CNOMCENTROCOSTO, auxNombre);
    }

    public void cambiarTerceroC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(CNOMBRETERCERO, auxNombre);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(),
                                        auxSucursal);
        registro.getCampos().put(CTIPOCPTEAFECT, "");
        registro.getCampos().put(CCMPTEAFECTADO, "");
        cargarListaTipoCpteAfect();
        cargarListaCmpteAfectado();
    }

    public void cambiarAuxiliarC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(CNOMBREAUXILIAR, auxNombre);
    }

    public void cambiarCuentaC(int rowNum)
    {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(CNOMBREPCONTABLE, auxNombre);
    }

    public void cambiarCmpteAfectadoC(int rowNum)
    {
        // <CODIGO_DESARROLLADOR>
        // </CODIGO_DESARROLLADOR>
    }

    public void cambiarTipoCpteAfect()
    {
        registro.getCampos().put(CCMPTEAFECTADO, null);
        cargarListaCmpteAfectado();
    }

    public void cambiarValorDebito()
    {
        //registro.getCampos().put(CTIPOCPTEAFECT, null); //JM 28/11/2024 CC_394
        //registro.getCampos().put(CCMPTEAFECTADO, null); //JM 28/11/2024 CC_394
        String valor = SysmanFunciones.nvlStr(
                        extraerString(registro.getCampos().get(CVALORDEBITO)),
                        "0");
        creditoNuevoBloqueado = !"0".equals(valor);
    }

    /**
     * Metodo ejecutado al cambiar el control CuentaPptal en la fila seleccionada dentro de la grilla.
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCuentaPptalC(int rowNum)
    {
        // heredado del bean base
    }

    public void cambiarValorDebitoC(int rowNum)
    {
        try
        {
            if ("SI".equals(SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "CONTROLAR VALOR A PAGAR EN EGRESO",
                                            modulo, new Date(), true), "NO")))
            {
                String valord;
                double valorDebito = Double.parseDouble(
                                extraerString(listaInicial.getDatasource()
                                                .get(rowNum % 10).getCampos()
                                                .get(CVALORDEBITO)));
                if (valorDebito < Double
                                .parseDouble(debitoAux))
                {
                    valord = "0";
                }
                else
                {
                    valord = extraerString(listaInicial.getDatasource()
                                    .get(rowNum % 10)
                                    .getCampos().get(CVALORDEBITO));
                }
                String numCuenta = extraerString(
                                listaInicial.getDatasource().get(rowNum % 10)
                                                .getCampos().get("ID"));
                if (verificarCxp(numCuenta, valord) < 0)
                {
                    listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                    .put(CVALORDEBITO, debitoAux);
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB772"));
                    return;
                }
            }

        }
        catch (NumberFormatException | SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        boolean pagadoBanco = Boolean.parseBoolean(extraerString(
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("PAGADOBANCO")));
        if (pagadoBanco)
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CVALORDEBITO, debitoAux);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB773"));
        }
    }

    public void cambiarValorCredito()
    {
        //registro.getCampos().put(CTIPOCPTEAFECT, null); //JM 28/11/2024 CC_394
        //registro.getCampos().put(CCMPTEAFECTADO, null); //JM 28/11/2024 CC_394
        String valor = SysmanFunciones
                        .nvlStr(extraerString(registro.getCampos()
                                        .get(CVALORCREDITO)), "0");
        debitoNuevoBloqueado = !"0".equals(valor);
    }

    public void cambiarValorCreditoC(int rowNum)
    {
        boolean pagadoBanco = Boolean.parseBoolean(extraerString(
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("PAGADOBANCO")));
        if (pagadoBanco)
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CVALORCREDITO, creditoAux);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB773"));
        }
        if (permitemodificarCredito)
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(CVALORCREDITO, creditoAux);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB774"));
        }
    }

    public void cambiarBaseGravableC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        String tipoE = "";
        String codigoE = "";
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "I");
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM5.getValue(),
                        tipoComp);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM6.getValue(),
                        numeroComp);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
        List<Registro> aux;
        try
        {
            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL133408
                                                                            .getValue())
                                            .getUrl(), param));
            if (!aux.isEmpty())
            {
                boolean permitirModBase = true;
                permitirModBase = (boolean) aux.get(0).getCampos().get(CPERMITEMODBASE);
                tipoE = (String) aux.get(0).getCampos().get(TIPO);
                codigoE = (String) aux.get(0).getCampos().get(CODIGOE);

                if (permitirModBase == false)
                {

                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB4400")
                                    .replace("#$tipo$#",
                                                    String.valueOf(tipoE))
                                    .replace("#$codigo$#",
                                                    String.valueOf(codigoE))

                    );

                    listaInicial.getDatasource().get(indice % 10).getCampos()
                                    .put(CBASEGRAVABLE, copiaValorBase);
                }
            }
            
			boolean legalizacion=false;
			
	        Map<String, Object> param2 = new TreeMap<>();
	        
	        param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param2.put(GeneralParameterEnum.ANO.getName(), anio);
	        param2.put(GeneralParameterEnum.CODIGO.getName(), cuenta);
			param2.put(GeneralParameterEnum.TIPO_CPTE.getName(), tipoComp);
			
			Registro auxiliar = RegistroConverter.toRegistro(requestManager.get(
					UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubdetallecomprobantecntsControladorUrlEnum.URL15080.
									getValue())
							.getUrl(),
					param2));
			
			
			legalizacion = (boolean) auxiliar.getCampos().get("LEGALIZACION");


			Registro registroD = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubdetallecomprobantecntsControladorUrlEnum.URL16220.
											getValue())
									.getUrl(),
									param2));

			String claseCuenta = registroD.getCampos().get("CLASECUENTA").toString();
			
        	if (legalizacion && claseCuenta.equals("I")) {
        		
        		String id = listaInicial.getDatasource().get(rowNum % 10)
        				.getCampos().get(GeneralParameterEnum.ID.getName()).toString();
        		
        		id = id.substring(0, id.indexOf(" "));
        		
				Map<String, Object> param3 = new TreeMap<>();
				param3.put(GeneralParameterEnum.ANO.getName(), anio);
				param3.put(GeneralParameterEnum.CUENTA.getName(),id);

				 Registro rscuenta = RegistroConverter.toRegistro(requestManager.get(
						UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										SubdetallecomprobantecntsControladorUrlEnum.URL12016.
										getValue())
								.getUrl(),
						param3));

				if (rscuenta.getCampos()!=null) {
					
					double pctAplicar = Double.parseDouble((rscuenta.getCampos().get("PCT_APLICAR")).toString());
					double pctBase = Double.parseDouble((rscuenta.getCampos().get("PCT_BASE")).toString());
					
	        		double baseG = Double.parseDouble(listaInicial.getDatasource().get(rowNum % 10).getCampos().get(CBASEGRAVABLE).toString());

					
					double ValorTotalRetencion = ((baseG * pctAplicar)/pctBase);

	        		listaInicial.getDatasource().get(rowNum % 10).getCampos().put(GeneralParameterEnum.VALOR_CREDITO.getName(),ValorTotalRetencion);

				} 
			}
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarBaseGravable(){
    	try
    	{
    		boolean legalizacion=false;
    		
    		Map<String, Object> paramm = new TreeMap<>();

    		paramm.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		paramm.put(GeneralParameterEnum.ANO.getName(), anio);
    		paramm.put(GeneralParameterEnum.CODIGO.getName(), SysmanFunciones
                    .nvl(registro.getCampos()
                            .get(GeneralParameterEnum.CUENTA
                                            .getName()),
                            "")
            .toString());
    		
    		
    		Registro registroD = RegistroConverter
    				.toRegistro(
    						requestManager.get(
    								UrlServiceUtil.getInstance()
    								.getUrlServiceByUrlByEnumID(
    										SubdetallecomprobantecntsControladorUrlEnum.URL16220.
    										getValue())
    								.getUrl(),
    								paramm));
    		
    		String claseCuenta = registroD.getCampos().get("CLASECUENTA").toString();
    		
    		paramm.put(GeneralParameterEnum.TIPO_CPTE.getName(), tipoComp);
    		
    		Registro auxiliar = RegistroConverter.toRegistro(requestManager.get(
    				UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						SubdetallecomprobantecntsControladorUrlEnum.URL15080.
    						getValue())
    				.getUrl(),
    				paramm));
    		
    		
    		legalizacion = (boolean) auxiliar.getCampos().get("LEGALIZACION");

    		if (legalizacion && claseCuenta.equals("I")) {
    			
    			Map<String, Object> param1 = new TreeMap<>();
    			
    			param1.put(GeneralParameterEnum.ANO.getName(), anio);
    			param1.put(GeneralParameterEnum.CUENTA.getName(),
    								registro.getCampos().get("CUENTA").toString());

    			Registro rscuenta = RegistroConverter.toRegistro(
    					requestManager.get(
    					UrlServiceUtil.getInstance()
    					.getUrlServiceByUrlByEnumID(
    							SubdetallecomprobantecntsControladorUrlEnum.URL12016.
    							getValue())
    					.getUrl(),
    					param1));

    			if (rscuenta.getCampos()!=null) {

    				double pctAplicar = Double.parseDouble((rscuenta.getCampos().get("PCT_APLICAR")).toString());
    				double pctBase = Double.parseDouble((rscuenta.getCampos().get("PCT_BASE")).toString());

    				double baseG = Double.parseDouble(registro.getCampos().get(CBASEGRAVABLE).toString());
    				
    				int factorRedondeo = Integer.parseInt((rscuenta.getCampos().get("FACTORREDONDEO")).toString());
    				int redondeo;
    				
    				if (factorRedondeo == 1)
    				{
    					redondeo = 0;
    				}
    				else if (factorRedondeo == 10)
    				{
    					redondeo = -1;
    				}
    				else if (factorRedondeo == 100)
    				{
    					redondeo = -2;
    				}
    				else if (factorRedondeo == 1000)
    				{
    					redondeo = -3;
    				}
    				else if (factorRedondeo == 10000)
    				{
    					redondeo = -4;
    				}
    				else redondeo = 0;

    				double ValorTotalRetencion = SysmanFunciones.redondear(((baseG * pctAplicar)/pctBase),redondeo);
    				
    				registro.getCampos().put(CVALORCREDITO, ValorTotalRetencion);
    			} 
    			
    		}
    	}
    		catch (SystemException e)
    		{
    			Logger.getLogger(SubdetallecomprobantecntsControlador.class
    					.getName()).log(Level.SEVERE, null, e);
    			JsfUtil.agregarMensajeError(e.getMessage());
    		}	
    	}

    
    public void cambiarAnio()
    {
        nFactura = null;
        cargarListaNumeroFactura();
    }

    public void cambiarTipoFactura()
    {
        nFactura = null;
        cargarListaNumeroFactura();
    }

    /**
     * Metodo ejecutado al cambiar el control TipoNota
     *
     *
     */
    public void cambiarTipoNota()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get("TIPO_NOTA"));

            Registro rsTipoFe;

            rsTipoFe = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubdetallecomprobantecntsControladorUrlEnum.URL29150
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rsTipoFe != null)
            {

                idTipoFe = SysmanFunciones
                                .nvl(rsTipoFe.getCampos().get("CODIGO"), "0")
                                .toString();
                cargarListaCodigoNota();
                cargarListaCodigoNotaE();
            }
        }
        catch (SystemException e)
        {
            e.printStackTrace();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TipoNota en la fila seleccionada dentro de la grilla
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoNotaC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos().get("TIPO_NOTA"));

            Registro rsTipoFe;

            rsTipoFe = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubdetallecomprobantecntsControladorUrlEnum.URL29150
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rsTipoFe != null)
            {

                idTipoFe = SysmanFunciones
                                .nvl(rsTipoFe.getCampos().get("CODIGO"), "0")
                                .toString();
                
                cargarListaCodigoNota();
                cargarListaCodigoNotaE();
                
            }
        }
        catch (SystemException e)
        {
            e.printStackTrace();
        }
        // </CODIGO_DESARROLLADO>
    }
    
    
    /**
     * Metodo ejecutado al cambiar el control ConceptoCUDS en la fila
     * seleccionada dentro de la grilla
     * 
     * TODO DOCUMENTACION ADICIONAL
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
public void cambiarConceptoCUDSC(int rowNum) {
         // Para el cambio en una fila  selecciona (PARA FORMULARIOS CONTINUOS) se realiza como lo muestra la siguiente linea
         // listaInicial.getDatasource().get(rowNum % 10).getCampos().put("FECHALARGA", "hola "); 
         // Para el cambio en una fila  selecciona (PARA SUBFORMULARIOS) se realiza como lo muestra la siguiente linea
         // listaInicial.get(rowNum).getCampos().put("FECHALARGA", "hola "); 
         //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }

    private void inicializarRegistroNuevo()
    {
        if (!SysmanFunciones.validarVariableVacio(referencia))
        {
            registro.getCampos().put("REFERENCIA", referencia);
        }
        if (!SysmanFunciones.validarVariableVacio(tercero))
        {
            registro.getCampos().put("TERCERO", tercero);
            terceroJud = tercero;
        }
        if (!SysmanFunciones.validarVariableVacio(sucursal))
        {
            registro.getCampos().put("SUCURSAL", sucursal);
            sucursalJud = sucursal;
        }

        if (!SysmanFunciones.validarVariableVacio(centroCosto))
        {
            registro.getCampos().put(
                            GeneralParameterEnum.CENTRO_COSTO.getName(),
                            centroCosto);
        }

        if (!SysmanFunciones.validarVariableVacio(auxiliarFuenteRecursos))
        {
            registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                            auxiliarFuenteRecursos);
        }

        if (!SysmanFunciones.validarVariableVacio(codigoFlujoEfectivo))
        {
            registro.getCampos().put(
                            SubdetallecomprobantecntsControladorEnum.CODIGO_FLUJO_EFECTIVO
                                            .getValue(),
                            codigoFlujoEfectivo);
        }   
        
        registro.getCampos().put(CVALORDEBITO, 0.00);
        registro.getCampos().put(CVALORCREDITO, 0.00);
        cargarListaprocesoJudicial();
        terceroJud = null;
	    sucursalJud = null;
    }

    @Override
    public void abrirFormulario()
    {
        comprobarpermisoscuentas();

        inicializarRegistroNuevo();
        try
        {
        	funcionarioVisible = "SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
					"MANEJA NOMBRE FUNCIONARIO EN PLANO CONTABILIZAR", "6", new Date(), true), "NO"));  
        	
            if ("-1".equals(mensaje))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB775"));
            }
            clase = getClaseContable();
            cargarSaldoCuentaAux = true;
            cargaResDep = true;
            cargaPptal = false;
            porcentajeRetencionCargar = true;
            reconocimientoCargar = true;
            fechaPlanoCargar = true;
            porcentajeRetencionBloqueado = true;
            sigecVisible = true;
            asignarTotales();
            cargarSaldoCuentaAux = "SI"
                            .equals(SysmanFunciones.nvl(sysmanUtil
                                            .consultarParametro(compania,
                                                            "MOSTRAR SALDO CUENTA AUXILIAR EN MOVIMIENTOS",
                                                            modulo, new Date(),
                                                            true),
                                            "NO"));
            

            if ("true".equals(impreso) || "C".equals(estadoPeriodo)
                || ACCION_VER.equals(accionEncabezado))
            {
                permisos[0] = false;
                permisos[1] = false;
                permisos[2] = false;
            }

            if ("E".equals(clase))
            {
                afectadoBloqueado = false;
                if ("NO".equals(SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "MANEJA PAGO DE RETENCIONES POR BANCO",
                                                modulo, new Date(), true),
                                                "NO")))
                {
                    porcentajeRetencionCargar = false;
                }
                else
                {
                    cambiarVisibilidadPorc();
                }
            }
            else if ("SI"
                    .equals(SysmanFunciones.nvl(sysmanUtil
                            .consultarParametro(compania,
                                            "MANEJA COMPROBANTE AFECTADO EN LOS NBA",
                                            modulo, new Date(),
                                            true),
                            "NO")) && "NBA".equals(tipoComp))
            {
            	afectadoBloqueado = false;
            }
            else
            {
                afectadoBloqueado = true;
                reconocimientoCargar = !"I".equals(clase);
            }

            fechaPlanoCargar = "SI"
                            .equals(SysmanFunciones.nvl(sysmanUtil
                                            .consultarParametro(compania,
                                                            "MUESTRA FECHA DE CONSIGNACION POR PLANO",
                                                            modulo,
                                                            new Date(), true),
                                            "NO"));
            cargaResDep = "SI"
                            .equals(SysmanFunciones.nvl(sysmanUtil
                                            .consultarParametro(compania,
                                                            "DEJAR MOSTRAR DEPENDENCIA Y RESPONSABLE PARA TODOS LOS COMPROBANTES EN LA IMPUTACION",
                                                            modulo, new Date(),
                                                            true),
                                            "NO"));
            porcentajeRetencionBloqueado = !"SI"
                            .equals(SysmanFunciones.nvl(sysmanUtil
                                            .consultarParametro(compania,
                                                            "PERMITE MODIFICAR EL PORCENTAJE DE LA RETENCION",
                                                            modulo, new Date(),
                                                            true),
                                            "NO"));

            validaAuxiliares = "SI"
                            .equals(SysmanFunciones.nvl(sysmanUtil
                                            .consultarParametro(compania,
                                                            "PERMITE CREAR AUXILIARES VARIOS EN EL PLAN PRESUPUESTAL",
                                                            modulo, new Date(),
                                                            true),
                                            "NO"));

            manejaControlFuente = SysmanFunciones.nvl(sysmanUtil.consultarParametro(
                            compania, "MANEJA CONTROL DE RUBRO POR FUENTE",
                            modulo, new Date(), true), "NO").toString();
            
            sigecVisible = "SI".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
					"SF MANEJA FACTURACION DE ESTAMPILLA ELECTRONICA", "69", new Date(), true), "NO"));
            
            if("SI".equals(SysmanFunciones
                    .nvlStr(sysmanUtil.consultarParametro(compania,
                            "MANEJA PROCESOS JUDICIALES CREMIL",
                            SessionUtil.getModulo(), new Date(),
                            false), "NO")) && (tipoComp.equals("CPJ") || tipoComp.equals("OST"))) {
            	
            	if(tipoComp.equals("CPJ")) {
            		bloquearProceso = true;
            	}
     			cremilVisible = true;
     		}
            else
            {
				cremilVisible = false;
			}            
            

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(SubdetallecomprobantecntsControladorEnum.PARAM5
                            .getValue(), tipoComp);
            param.put(SubdetallecomprobantecntsControladorEnum.PARAM6
                            .getValue(), numeroComp);

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL127104
                                                                            .getValue())
                                            .getUrl(), param));
            baseGravable = Double.parseDouble(
                            SysmanFunciones.nvl(
                                            regAux.getCampos().get("VLR_BASE"),
                                            "").toString());
            ivaFacturado = Double.parseDouble(
                            SysmanFunciones.nvl(regAux.getCampos()
                                            .get("VLR_BASEIVA"), "")
                                            .toString());
            numeroDocumento = SysmanFunciones
                            .nvl(regAux.getCampos().get(CNRODOCUMENTO), "")
                            .toString();
            registro.getCampos().put(CBASEGRAVABLE, baseGravable);
            registro.getCampos().put("BASE_IVA", ivaFacturado);
            registro.getCampos().put(CNRODOCUMENTO, numeroDocumento);

            if (tipoComp.equals("COM"))
            {
                fechaBloqueada = true;

            }
            else
            {
                fechaBloqueada = false;

            }
            
        }
        catch (

        SystemException ex)

        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + ""
                                + ex.getMessage());
        }

    }

    public void cambiarVisibilidadPorc()
    {
        try
        {
            if ("SI".equals(SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "PERMITIR MODIFICAR PORCENTAJE DE RETENCION POR BANCO",
                                            modulo, new Date(), true), "NO")))
            {
                porcentajeRetencionCargar = true;
                porcentajeRetencionBloqueado = false;
            }
            else
            {
                porcentajeRetencionCargar = true;
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + ""
                                + ex.getMessage());
        }
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
        activoReg = false;
        activoReg2 = false;
        responsable = null;
        sucursalResponsable = null;
        cargarListaDependencia();
    }

    /**
     * Metodo que valida si un cadena es numerica o no
     *
     * @param cadena
     * @return
     */
    private boolean isNumeric(String cadena)
    {
        return NumberUtils.isNumber(cadena);
    }

    public void alertarManejos()
    {
        if (manejaTercero && "".equals(SysmanFunciones.nvl(
                        registro.getCampos()
                                        .get(GeneralParameterEnum.TERCERO
                                                        .getName()),
                        "").toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB776"));
        }
        if (manejaCentro
            && "".equals(SysmanFunciones.nvl(registro.getCampos()
                            .get(GeneralParameterEnum.CENTRO_COSTO
                                            .getName()),
                            "").toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB777"));
        }
        if (manejaAuxiliar
            && "".equals(SysmanFunciones.nvl(registro.getCampos()
                            .get(GeneralParameterEnum.AUXILIAR.getName()),
                            "").toString()))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB778"));
        }
    }

    public boolean evaluarFlujoEfectivo()
    {

        Map<String, Object> paramDisminuido = new HashMap<>();
        paramDisminuido.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        paramDisminuido.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        paramDisminuido.put(GeneralParameterEnum.CUENTA.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CUENTA.getName()));
        Registro rsDisminuido;
        try
        {
            rsDisminuido = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL676
                                                                            .getValue())
                                            .getUrl(),
                                            paramDisminuido));

            if (rsDisminuido != null)
            {

                String mostrarFlujo = SysmanFunciones
                                .nvl(rsDisminuido
                                                .getCampos()
                                                .get(SubdetallecomprobantecntsControladorEnum.MOSTRAR_EN_FLUJO
                                                                .getValue()),
                                                "")
                                .toString();
                if (mostrarFlujo.equals("-1"))
                {
                    return false;
                }
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    public boolean validarAuxiliar()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CUENTA.getName(),
                            registro.getCampos().get("CUENTA").toString());

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL8571
                                                                            .getValue())
                                            .getUrl(), param));

            if (validaAuxiliares && Integer.parseInt(regAux.getCampos()
                            .get("CANTIDAD").toString()) > 0)
            {
                for (int i = 1; i < 6; i++)
                {
                    if (!generarMensaje(i))
                    {
                        return false;
                    }
                }
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    public boolean generarMensaje(int aux)
    {
        String indicador = null;
        switch (aux)
        {
        case 1:
            if (manejaAuxiliar
                && registro.getCampos().get("AUXILIAR").toString()
                                .equals(SysmanConstantes.CONS_AUXILIAR))
            {
                indicador = "Auxiliar";
            }
            break;
        case 2:
            if (manejaCentro
                && registro.getCampos().get("CENTRO_COSTO").toString()
                                .equals(SysmanConstantes.CONS_CENTRO))
            {
                indicador = "Centro de Costo";
            }
            break;
        case 3:
            if (manejaFuente
                && registro.getCampos().get("FUENTE_RECURSO").toString()
                                .equals(SysmanConstantes.CONS_FUENTE))
            {
                indicador = "Fuente de Recurso";
            }
            break;
        case 4:
            if (manejaTercero && registro.getCampos().get("TERCERO").toString()
                            .equals(SysmanConstantes.CONS_TERCERO))
            {
                indicador = "Tercero";
            }
            break;
        case 5:
            if (manejaReferencia
                && registro.getCampos().get("REFERENCIA").toString()
                                .equals(SysmanConstantes.CONS_REFERENCIA))
            {
                indicador = "Referencia";
            }
            break;
        default:
            break;
        }
        if (indicador == null)
        {
            return true;
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4261")
                            .replace("#indicador#", indicador));
            return false;
        }

    }

    @Override
    public boolean insertarAntes()
    {
        try
        {
        	if(yaEnviadoDianExito) {
				JsfUtil.agregarMensajeError(idioma.getString("MSG_DOCUMENTO_ENVIADO"));
				return false;
			}
        	registroNuevo = true;
            alertarManejos();
            long aux = sysmanUtil.generarConsecutivoConValorInicial(
                            "DETALLE_COMPROBANTE_CNT",
                            "DETALLE_COMPROBANTE_CNT.COMPANIA =''"
                                + compania + "''"
                                + " AND DETALLE_COMPROBANTE_CNT.ANO =" + anio
                                + " AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = ''"
                                + tipoComp
                                + "'' AND DETALLE_COMPROBANTE_CNT.COMPROBANTE="
                                + numeroComp,
                            "CONSECUTIVO", "1");
            //JM 05/12/2024 CC344
            String descripcionaux = ""; 
            if ("NO".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
					"PERMITE MODIFICAR DESCRIPCION EN DETALLE CONTABLE", modulo, new Date(), true), "NO"))) {
            	descripcionaux = descripcion;
			} else {
				descripcionaux = SysmanFunciones.nvl(registro.getCampos().get("DESCRIPCION").toString(),descripcion).toString();
				if( descripcionaux.equalsIgnoreCase("")){
					descripcionaux = descripcion;
				}
			}
            
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(), anio);
            registro.getCampos().put("TIPO_CPTE", tipoComp);
            registro.getCampos().put("COMPROBANTE", numeroComp);
            registro.getCampos().put("DESCRIPCION", descripcionaux);
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            aux);
            // registro.getCampos().put(CPORCRETENCION,
            // "0.0");
            registro.getCampos().put("FECHA",
                            fechaPar);
            registro.getCampos().put("HORA", new Date());
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            GeneralParameterEnum.TERCERO.getName()))
            {
                registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                                tercero);
                registro.getCampos().put(
                                GeneralParameterEnum.SUCURSAL.getName(),
                                sucursal);
            }

            if (!"".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(CCMPTEAFECTADO),
                                            "")
                            .toString()))
            {

                Map<String, Object> parametros = new HashMap<>();
                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(GeneralParameterEnum.ANO.getName(), anio);
                parametros.put(SubdetallecomprobantecntsControladorEnum.PARAM5
                                .getValue(), tipoComp);
                parametros.put(SubdetallecomprobantecntsControladorEnum.PARAM6
                                .getValue(), numeroComp);
                parametros.put(SubdetallecomprobantecntsControladorEnum.PARAM1
                                .getValue(),
                                SysmanFunciones
                                                .nvl(registro.getCampos().get(
                                                                CANOAFECT), "")
                                                .toString());
                parametros.put(SubdetallecomprobantecntsControladorEnum.PARAM8
                                .getValue(),
                                SysmanFunciones.nvl(
                                                registro.getCampos()
                                                                .get(CTIPOCPTEAFECT),
                                                "").toString());
                parametros.put(SubdetallecomprobantecntsControladorEnum.PARAM9
                                .getValue(),
                                SysmanFunciones.nvl(
                                                registro.getCampos()
                                                                .get(CCMPTEAFECTADO),
                                                "").toString());
                parametros.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                parametros.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantecntsControladorUrlEnum.URL135833
                                                                .getValue());
                requestManager.saveCount(urlCreate.getUrl(),
                                urlCreate.getMetodo(), parametros);

            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.MAN_AUX_GEN
                                        .getValue());
        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.MAN_AUX_TER
                                        .getValue());
        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.CLASECUENTA
                                        .getValue());
        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.NOMBREPLANCONTABLE
                                        .getValue());
        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.MAN_CEN_CTO
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.MOVIMIENTO.getName());
        return validarAuxiliar();
    }

    @Override
    public boolean insertarDespues()
    {
        asignarTotales();
        debitoNuevoBloqueado = false;
        creditoNuevoBloqueado = false;
        inicializar();
        inicializarRegistroNuevo();
        reasignarOrigen();

        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
    	if(yaEnviadoDianExito) {
			JsfUtil.agregarMensajeError(idioma.getString("MSG_DOCUMENTO_ENVIADO"));
			return false;
		}
    	Registro registroD = null;
    	String basGravable = " ";    	
    	String claseCuenta = " ";
    	String valorCredito = " ";
    	String claseComprobante = " ";

    	try {
    		paramModifCreditoGravable = SysmanFunciones.nvlStr(sysmanUtil
    				.consultarParametro(compania,
    						"CONTROLA MODIFICACION VALORES DE CUENTAS IMPUESTO EN IMPUTACION CONTABLE",
    						modulo, new Date(),
    						true),"NO");

    		clasesOmitidas = SysmanFunciones.nvlStr(sysmanUtil
    				.consultarParametro(compania,
    						"COMPROBANTES MODIFICACION MANUAL",
    						modulo, new Date(),
    						true),"");
    		claseComprobante = registro.getCampos().get("TIPO_CPTE").toString();
    		
    		/**
    		 * Verifica si la clase del comprobante es "N" (Nota Contable) o Clase "I" (Ingresos) 
    		 * y valida que los valores de crdito y dbito no superen los saldos originales.
    		 * TICKET: 7801666 CFBARRERA
    		 */
    	    
    		if (clase.equals("N") || clase.equals("I") || clase.equals("B")) {

				if (registro.getCampos().get(CTIPOCPTEAFECT) != null && registro.getCampos().get(CANOAFECT) != null
						&& registro.getCampos().get(CCMPTEAFECTADO) != null) {

					BigDecimal valorDisparador = contabilidadSeis.validarcsaldoafectado(compania,
							registro.getCampos().get(CANOAFECT).toString(),
							registro.getCampos().get(CTIPOCPTEAFECT).toString(),
							registro.getCampos().get(CCMPTEAFECTADO).toString(),
							registro.getCampos().get(CVALORCREDITO).toString(),
							registro.getCampos().get(CVALORDEBITO).toString(),
							numeroComp,
							tipoComp,
							anio,
							registro.getCampos().get("CUENTA").toString());

						if (valorDisparador.doubleValue()< 0 ){
							JsfUtil.agregarMensajeAlerta(idioma.getString("MSG_VALORNOPERMITIDO"));
							return false;
						}else {       
	
									try {
										 contabilidadSeis.actualizarsaldoafect(compania,
												registro.getCampos().get(CANOAFECT).toString(),
												registro.getCampos().get(CTIPOCPTEAFECT).toString(),
												registro.getCampos().get(CCMPTEAFECTADO).toString(),
												registro.getCampos().get(CVALORCREDITO).toString(),
												registro.getCampos().get(CVALORDEBITO).toString(),
												numeroComp,
												tipoComp,
												anio,
												registro.getCampos().get("CUENTA").toString(),"0",
												SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()))); //CC1737_FACTURACION(MROSERO));
									} catch (SystemException e) {
							            JsfUtil.agregarMensajeError(e.getMessage());
							            logger.error(e.getMessage(), e);
									}
									
								
				} }
			}// FIN: TICKET: 7801666 CFBARRERA'
			
			

    		if(!registroNuevo) {
    			Map<String, Object> paramm = new TreeMap<>();
    			paramm.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    			paramm.put(GeneralParameterEnum.ANO.getName(), anio);
    			paramm.put(GeneralParameterEnum.CODIGO.getName(), cuenta);


    			registroD = RegistroConverter
    					.toRegistro(
    							requestManager.get(
    									UrlServiceUtil.getInstance()
    									.getUrlServiceByUrlByEnumID(
    											SubdetallecomprobantecntsControladorUrlEnum.URL16220.getValue())
    									.getUrl(),
    									paramm));

    			claseCuenta = registroD.getCampos().get("CLASECUENTA").toString();
    			valorCredito = registro.getCampos().get(CVALORCREDITO).toString();
    			basGravable = registro.getCampos().get(CBASEGRAVABLE).toString();
    		}

    	} catch (SystemException e1) {
    		e1.printStackTrace();
    	}    	
    	
    	if(!registroNuevo) {    		
    		try {
    			
    		    if(clase.equals("I") || clase.equals("B")) {
    		    
        		    if(registro.getCampos().get(CTIPOCPTEAFECT) != null && 
        					registro.getCampos().get(CANOAFECT) != null && registro.getCampos().get(CCMPTEAFECTADO)!= null)
        			{
    	    			BigDecimal valorFacturado = contabilidadSeis.obtenerValorFacturado(compania, registro.getCampos().get(CANOAFECT).toString(), 
    							registro.getCampos().get(CTIPOCPTEAFECT).toString(), registro.getCampos().get(CCMPTEAFECTADO).toString());
        	    			if(Double.parseDouble(SysmanFunciones.nvl(
                                    registro.getCampos().get(CVALORCREDITO),"0").toString()) > valorFacturado.doubleValue())
        	    			{
        	    				JsfUtil.agregarMensajeAlerta(idioma.getString("MSG_VALORNOPERMITIDO"));//El valor que intenta ingresar supera el valor de la factura. Por favor verifique.
        	    				return false;
        	    			}
        			}
    		    }
				
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		if(paramModifCreditoGravable.equals("SI")) {
    			//Si       la clase no est� omitida            y la cuenta es de impuesto   y   se modific� el valor credito          o     la base gravable               entonces
    			if (!clasesOmitidas.contains(claseComprobante) && "I".equals(claseCuenta) && (!(copiaValCredito.equals(valorCredito)) || !(copiaBaseGravable.equals(basGravable)))) {
    				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4448"));//no permite actualizar	
    				return false;
    			}else { // si est� omitida se permite actualizar normalmente
    				return actualizar();
    			}	

    		}else { // si el parametro de control est� en NO se permite actualizar normalmente
    			return actualizar();

    		} // si el registro es nuevo, se permite actualizar normalmente
    	}return actualizar();
    }

    public boolean actualizar() {
    	try
        {

            if (!activoReg && "Z".equals(clase))
            {
                registro.getCampos().put("CIERRE", "-1");
            }

            double credito = Math.floor(
                            Double.parseDouble(SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get(CVALORCREDITO),
                                            "0").toString())
                                * 100
                                + 0.001)
                / 100;
            double debito = Math
                            .floor(Double.parseDouble(SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get(CVALORDEBITO),
                                            "0").toString())
                                * 100
                                + 0.001)
                / 100;
            if (!revisarAfectados())
            {
                return false;
            }
            calcularBase(debito, credito);
            if (!evaluarCuentas())
            {
                return false;
            }
            
            //JM 05/12/2024 CC344
            String descripcionaux = ""; 
            if ("NO".equals(SysmanFunciones.nvl(sysmanUtil.consultarParametro(compania,
					"PERMITE MODIFICAR DESCRIPCION EN DETALLE CONTABLE", modulo, new Date(), true), "NO"))) {
            	descripcionaux = descripcion;
			} else {
				descripcionaux = SysmanFunciones.nvl(registro.getCampos().get("DESCRIPCION").toString(),descripcion).toString();
				if( descripcionaux.equalsIgnoreCase("")){
					descripcionaux = descripcion;
				}
			}
            registro.getCampos().put("DESCRIPCION", descripcionaux);
            
            registro.getCampos().put(CVALORCREDITO, SysmanFunciones.redondear(
                            new BigDecimal(credito), 2));
            registro.getCampos().put("EJECUCION_CREDITO",
                            new BigDecimal(credito));
            registro.getCampos().put(CVALORDEBITO, SysmanFunciones
                            .redondear(new BigDecimal(debito), 2));
            registro.getCampos().put("EJECUCION_DEBITO", debito);         

            registro.getCampos().remove("CLASE_CONTABLE");
            registro.getCampos().remove(CNOMCENTROCOSTO);
            registro.getCampos().remove(CNOMBRETERCERO);
            registro.getCampos().remove(CNOMBREAUXILIAR);
            registro.getCampos().remove(CNOMBREPCONTABLE);
            registro.getCampos().remove(CDRESPSUCURSALCNT);
            registro.getCampos().remove(CCLASECUENTA);
            registro.getCampos().remove("MOVIMIENTO");
            registro.getCampos().remove("MAN_CEN_CTO");
            registro.getCampos().remove("MAN_AUX_TER");
            registro.getCampos().remove("MAN_AUX_GEN");
            registro.getCampos().remove(CSALDO13);
            registro.getCampos().remove("ID");
            registro.getCampos().remove("DATE_CREATED");
            registro.getCampos().remove("CREATED_BY");
            registro.getCampos().remove("MOSTRARF1001");
            registro.getCampos().remove(GeneralParameterEnum.MOVIMIENTO.getName());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CUENTA.getName(), SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(GeneralParameterEnum.CUENTA
                                                            .getName()),
                                            "")
                            .toString());

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL48939
                                                                            .getValue())
                                            .getUrl(), param));

            if ((!"0".equals(regAux.getCampos().get("MAN_AUX_REF").toString())

                || (boolean) regAux.getCampos().get("OBLIGA_REFERENCIA"))
                && (SysmanFunciones.validarVariableVacio((String) registro
                                .getCampos()
                                .get(GeneralParameterEnum.REFERENCIA.getName()))
                    ||
                    SysmanConstantes.CONS_REFERENCIA.equals(registro.getCampos()
                                    .get(GeneralParameterEnum.REFERENCIA
                                                    .getName())
                                    .toString())))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB779"));
                return false;
            }

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL29149
                                                                            .getValue())
                                            .getUrl(), param));

            if ((!"0".equals(regAux.getCampos().get("MAN_AUX_FUE").toString())

                || (boolean) regAux.getCampos().get("OBLIGA_FUENTE"))
                && (SysmanFunciones.validarVariableVacio((String) registro
                                .getCampos()
                                .get(GeneralParameterEnum.FUENTE_RECURSO
                                                .getName()))
                    ||
                    SysmanConstantes.CONS_FUENTE.equals(registro.getCampos()
                                    .get(GeneralParameterEnum.FUENTE_RECURSO
                                                    .getName())
                                    .toString())))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4335"));
                return false;
            }

            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL29148
                                                                            .getValue())
                                            .getUrl(), param));

            if ((!"0".equals(regAux.getCampos().get("MAN_CEN_CTO").toString())

                || (boolean) regAux.getCampos().get("OBLIGA_CENTRO"))
                && (SysmanConstantes.CONS_CENTRO.equals(registro.getCampos()
                                .get(GeneralParameterEnum.CENTRO_COSTO
                                                .getName())
                                .toString())
                    || SysmanFunciones.validarVariableVacio((String) registro
                                    .getCampos()
                                    .get(GeneralParameterEnum.CENTRO_COSTO
                                                    .getName()))))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4336"));
                return false;
            }

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        return true;
    }

    /**
     * M�todo que valida, si la cuenta seleccionada en un comprobante de clase "I"o"B", tiene un rubro presupuestal asociado, si tiene un rubro equivalente asociado este valida que de acuerdo a los
     * auxiliares seleccionados se encuentre en plan presupuestal, en la tabla SALDO_AUX_PPTAL
     */
    public void validarCuentasEquivalentes(boolean activa)
    {
        StringBuilder rtaFue = new StringBuilder();
        StringBuilder rtaCen = new StringBuilder();
        StringBuilder rtaAux = new StringBuilder();
        StringBuilder rtaRef = new StringBuilder();

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), activa ? cuenta
            : SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(GeneralParameterEnum.CUENTA
                                                            .getName()),
                                            "")
                            .toString());

        try
        {
            List<Registro> listaCuenta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL2194
                                                                            .getValue())
                                            .getUrl(), param));

            if (!listaCuenta.isEmpty())
            {

                for (Registro cuenta : listaCuenta)
                {
                    param.put(GeneralParameterEnum.CUENTA.getName(),
                                    SysmanFunciones
                                                    .nvl(cuenta.getCampos().get(
                                                                    GeneralParameterEnum.RUBRO
                                                                                    .getName()),
                                                                    "")
                                                    .toString());
                    Registro rs = RegistroConverter.toRegistro(
                                    requestManager.get(UrlServiceUtil
                                                    .getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                                    SubdetallecomprobantecntsControladorUrlEnum.URL2310
                                                                                    .getValue())
                                                    .getUrl(), param));

                    manFuente = (boolean) (manFuente ? true
                        : rs.getCampos().get("MAN_AUX_FUE"));
                    manAuxiliar = (boolean) (manAuxiliar ? true
                        : rs.getCampos().get("MAN_AUX_GEN"));
                    manReferencia = (boolean) (manReferencia ? true
                        : rs.getCampos().get("MAN_AUX_REF"));
                    manCentro = (boolean) (manCentro ? true
                        : rs.getCampos().get("MAN_CEN_CTO"));
                }

                param.put(GeneralParameterEnum.CUENTA.getName(), activa ? cuenta
                    : SysmanFunciones
                                    .nvl(registro.getCampos()
                                                    .get(GeneralParameterEnum.CUENTA
                                                                    .getName()),
                                                    "")
                                    .toString());

                List<Registro> rubroAux = RegistroConverter.toListRegistro(
                                requestManager.getList(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubdetallecomprobantecntsControladorUrlEnum.URL2222
                                                                                .getValue())
                                                .getUrl(), param));

                if (manejaControlFuente.equals("NO"))
                {
                    if (manFuente)
                    {
                        for (Registro reg : rubroAux)
                        {

                            rtaFue.append("#" + reg.getCampos()
                                            .get(GeneralParameterEnum.FUENTE_RECURSO
                                                            .getName())
                                + "#").append(",");
                        }

                        filtroFuentes = rtaFue.toString();
                        filtroFuentes = filtroFuentes.substring(0,
                                        filtroFuentes.length() - 1);
                        cargarListaCmbFuenteRecurso();
                        cargarListaCmbFuenteRecursoE();
                        manFuente = false;

                    }
                }
                else
                {
                    manFuente = false;
                }

                if (manCentro)
                {
                    for (Registro reg : rubroAux)
                    {

                        rtaCen.append("#" + reg.getCampos()
                                        .get(GeneralParameterEnum.CENTRO_COSTO
                                                        .getName())
                            + "#").append(",");
                    }

                    filtrosCentros = rtaCen.toString();
                    filtrosCentros = filtrosCentros.substring(0,
                                    filtrosCentros.length() - 1);
                    cargarListaCentroCosto();
                    cargarListaCentroCostoE();
                    manCentro = false;

                }

                if (manReferencia)
                {
                    for (Registro reg : rubroAux)
                    {

                        rtaRef.append("#" + reg.getCampos()
                                        .get(GeneralParameterEnum.REFERENCIA
                                                        .getName())
                            + "#").append(",");
                    }

                    filtrosReferencias = rtaRef.toString();
                    filtrosReferencias = filtrosReferencias.substring(0,
                                    filtrosReferencias.length() - 1);
                    cargarListaCmpReferencia();
                    cargarListaCmpReferenciaE();
                    manReferencia = false;

                }

                if (manAuxiliar)
                {
                    for (Registro reg : rubroAux)
                    {

                        rtaAux.append("#" + reg.getCampos()
                                        .get(GeneralParameterEnum.AUXILIAR
                                                        .getName())
                            + "#").append(",");
                    }

                    filtrosAuxiliares = rtaAux.toString();
                    filtrosAuxiliares = filtrosAuxiliares.substring(0,
                                    filtrosAuxiliares.length() - 1);
                    cargarListaAuxiliar();
                    cargarListaAuxiliarE();
                    manAuxiliar = false;

                }

            }
            else
            {
                manFuente = false;
                cargarListaCmbFuenteRecurso();
                cargarListaCmbFuenteRecursoE();
                manAuxiliar = false;
                cargarListaAuxiliar();
                cargarListaAuxiliarE();
                manReferencia = false;
                cargarListaCmpReferencia();
                cargarListaCmpReferenciaE();
                manCentro = false;
                cargarListaCentroCosto();
                cargarListaCentroCostoE();
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void calcularBase(double debito, double credito)
    {
        try
        {
            if ("SI".equals(SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "CALCULAR BASE CON VALOR RETENIDO",
                                            modulo,
                                            new Date(), true), "NO"))
                && (registro.getCampos().get(CBASEGRAVABLE) == null
                    || "0".equals(SysmanFunciones
                                    .nvl(registro.getCampos()
                                                    .get(CBASEGRAVABLE), "")
                                    .toString()))
                && "I".equals(SysmanFunciones
                                .nvl(registro.getCampos().get(CCLASECUENTA),
                                                "")
                                .toString()))
            {

                Map<String, Object> param2 = new TreeMap<>();
                param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param2.put(GeneralParameterEnum.ANO.getName(), anio);
                param2.put(GeneralParameterEnum.CUENTA.getName(),
                                SysmanFunciones.nvl(registro.getCampos()
                                                .get(GeneralParameterEnum.CUENTA
                                                                .getName()),
                                                "").toString());

                Registro regAux;

                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubdetallecomprobantecntsControladorUrlEnum.URL46483
                                                                                .getValue())
                                                .getUrl(), param2));

                if (regAux != null
                    && !"0".equals(SysmanFunciones
                                    .nvl(regAux.getCampos().get("PCT_APLICAR"),
                                                    "")
                                    .toString()))
                {
                    registro.getCampos().put(CBASEGRAVABLE,
                                    (debito + credito) * 100
                                        / Double.parseDouble(SysmanFunciones
                                                        .nvl(regAux
                                                                        .getCampos()
                                                                        .get("PCT_APLICAR"),
                                                                        "0.0")
                                                        .toString()));
                }
            }
        }
        catch (SystemException | NumberFormatException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean revisarAfectados()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM5
                        .getValue(), tipoComp);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM6
                        .getValue(), numeroComp);
        try
        {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL120500
                                                                            .getValue())
                                            .getUrl(), param));

            if (!activoReg && "E".equals(clase)
                && !"0".equals(SysmanFunciones
                                .nvl(regAux.getCampos()
                                                .get(GeneralParameterEnum.CUENTA
                                                                .getName()),
                                                "")
                                .toString())
                && "P".equals(SysmanFunciones
                                .nvl(registro.getCampos().get(CCLASECUENTA),
                                                "")
                                .toString()))
            {

                if (registro.getCampos().get(CTIPOCPTEAFECT) == null)
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB780"));
                    return false;
                }

                if (!evaluarAfectados())
                {
                    return false;
                }
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean evaluarCuentas()
    {
        DecimalFormat df = new DecimalFormat("#,###.00");
        if (activoReg)
        {

            if (!evaluarClase(df))
            {
                return false;
            }

            if (getSaldo13(Double.parseDouble(
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get(CSALDO13), "0.0")
                                            .toString())) < 0)
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB785").replace(
                                                CSALDOC,
                                                df.format(getSaldo13(Double
                                                                .parseDouble(SysmanFunciones
                                                                                .nvl(registro.getCampos()
                                                                                                .get(CSALDO13),
                                                                                                "0.0")
                                                                                .toString())))));
            }
        }
        return true;
    }

    public boolean evaluarClase(DecimalFormat df)
    {
        try
        {
            if ("B".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(CCLASECUENTA),
                                            "")
                            .toString())
                && "SI".equals(SysmanFunciones
                                .nvl(sysmanUtil.consultarParametro(compania,
                                                "CONTROLAR SALDO EN CUENTAS BANCARIAS",
                                                modulo, new Date(), true),
                                                "NO")))
            {
                if (!"0".equals(registro.getCampos()
                                .get(CVALORCREDITO))
                    && Double.parseDouble(
                                    SysmanFunciones.nvl(registro.getCampos()
                                                    .get(CVALORCREDITO),
                                                    "0.0")
                                                    .toString()) > Double
                                                                    .parseDouble(SysmanFunciones
                                                                                    .nvl(registro
                                                                                                    .getCampos()
                                                                                                    .get(CSALDO13),
                                                                                                    "0.0")
                                                                                    .toString()))
                {
                    JsfUtil.agregarMensajeError(
                                    idioma.getString("TB_TB784").replace(
                                                    CSALDOC,
                                                    df.format(SysmanFunciones
                                                                    .nvl(registro.getCampos()
                                                                                    .get(CSALDO13),
                                                                                    "0.0")
                                                                    .toString())));
                    return false;
                }
                if (getSaldo13(Double.parseDouble(SysmanFunciones
                                .nvl(registro.getCampos().get(CSALDO13),
                                                "0.0")
                                .toString())) < 0)
                {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB785")
                                    .replace(CSALDOC,
                                                    df.format(getSaldo13(
                                                                    Double.parseDouble(
                                                                                    SysmanFunciones
                                                                                                    .nvl(registro.getCampos()
                                                                                                                    .get(CSALDO13),
                                                                                                                    "0.0")
                                                                                                    .toString())))));
                    return false;
                }
            }
        }
        catch (NumberFormatException | SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean evaluarAfectados()
    {
        try
        {
            if ("SI".equals(SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "MANEJA COMPROBANTE AFECTADO CERO",
                                            modulo, new Date(), true), "NO")))
            {
                registro.getCampos().put(CCMPTEAFECTADO, "0");
            }
            else
            {
                if (!evaluarAfectados2())
                {
                    return false;
                }
                saldoAuxAfec = null;
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean evaluarAfectados2()
    {
        if (registro.getCampos().get(CTIPOCPTEAFECT) == null
            || registro.getCampos().get(CCMPTEAFECTADO) == null
            || "0".equals(SysmanFunciones
                            .nvl(registro.getCampos().get(
                                            CCMPTEAFECTADO), "")
                            .toString()))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB781"));
            return false;
        }
        else if (registro.getCampos()
                        .get(CCMPTEAFECTADO) != null
            && saldoAuxAfec != null && !evaluarDebito())
        {
            return false;
        }

        return true;
    }

    public boolean evaluarDebito()
    {
        if (registro.getCampos().get(CVALORDEBITO) != null
            && Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(CVALORDEBITO),
                                            "0.0")
                            .toString()) > Double.parseDouble(
                                            saldoAuxAfec))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB782"));
            return false;

        }
        if (registro.getCampos().get(CVALORDEBITO) != null
            && Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(CVALORCREDITO),
                                            "0.0")
                            .toString()) > Double.parseDouble(
                                            saldoAuxAfec))
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB783"));
            return false;
        }
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {

        asignarTotales();
        responsable = null;
        sucursalResponsable = null;
        cargarListaDependencia();
        String porcRetencion = getClaseCuenta(CPORCRETENCION);
        if ("".equals(porcRetencion))
        {
            porcRetencion = "0";
        }

        if (!"0".equals(porcRetencion)
            && "I".equals(getClaseCuenta(CCLASECUENTA))
            && SysmanFunciones.redondear(
                            Double.parseDouble(SysmanFunciones
                                            .nvl(registro.getCampos()
                                                            .get(CBASEGRAVABLE),
                                                            "0.0")
                                            .toString())
                                * Double.parseDouble(porcRetencion)
                                / 100,
                            2) <= Double.parseDouble(SysmanFunciones
                                            .nvl(registro.getCampos()
                                                            .get(CVALORDEBITO),
                                                            "0.0")
                                            .toString())
                                + Double.parseDouble(SysmanFunciones
                                                .nvl(registro.getCampos()
                                                                .get(CVALORCREDITO),
                                                                "0.0")
                                                .toString())
                                - 50
            || SysmanFunciones.redondear(
                            Double.parseDouble(SysmanFunciones
                                            .nvl(registro.getCampos()
                                                            .get(CBASEGRAVABLE),
                                                            "0.0")
                                            .toString())
                                * Double.parseDouble(porcRetencion)
                                / 100,
                            2) >= Double.parseDouble(
                                            SysmanFunciones.nvl(registro
                                                            .getCampos()
                                                            .get(CVALORDEBITO),
                                                            "0.0")
                                                            .toString())
                                + Double.parseDouble(
                                                SysmanFunciones.nvl(registro
                                                                .getCampos()
                                                                .get(CVALORCREDITO),
                                                                "0.0")
                                                                .toString())
                                + 50)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB787").replace(
                                            "#$valor$#",
                                            String.valueOf(SysmanFunciones
                                                            .redondear(Double
                                                                            .parseDouble(SysmanFunciones
                                                                                            .nvl(registro
                                                                                                            .getCampos()
                                                                                                            .get(CBASEGRAVABLE),
                                                                                                            "0.0")
                                                                                            .toString())
                                                                * Double.parseDouble(porcRetencion)
                                                                / 100, 2))));
        }
        
        asignarTotales();
        inicializarRegistroNuevo();
        registroActivo = null;
        activoReg = false;
        
        
        return true;
    }

    @Override
    public boolean eliminarAntes()
    // <CODIGO_DESARROLLADO>
    {
    	/**
		 * Elimina los saldos de los comporbantes creados - TICKET: 7801666 CFBARRERA
		 */
    	if ((clase.equals("N")|| clase.equals("I") || clase.equals("B")) && registro.getCampos().get(CTIPOCPTEAFECT) != null && registro.getCampos().get(CANOAFECT) != null
				&& registro.getCampos().get(CCMPTEAFECTADO) != null) {
    	try {
			 contabilidadSeis.actualizarsaldoafect(compania,
					registro.getCampos().get(CANOAFECT).toString(),
					registro.getCampos().get(CTIPOCPTEAFECT).toString(),
					registro.getCampos().get(CCMPTEAFECTADO).toString(),
					registro.getCampos().get(CVALORCREDITO).toString(),
					registro.getCampos().get(CVALORDEBITO).toString(),
					numeroComp,
					tipoComp,
					anio,
					registro.getCampos().get("CUENTA").toString(),"-1",
					SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()))); //CC1737_FACTURACION(MROSERO));
		} catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
		 }
    	}
    	
    	if(tipoComp.equals("OST")) 
    	{
    		try {
				contabilidadSeis.actualizarsaldopj(compania,
						registro.getCampos().get("CUENTA").toString(),
						registro.getCampos().get(CVALORDEBITO).toString(),
						registro.getCampos().get("NUMEROPROCESO").toString());
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
 
		try {
	    	
	    	Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
	        param.put(GeneralParameterEnum.COMPROBANTE.getName(), registro.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()));
	        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
	        param.put(GeneralParameterEnum.TIPO_CPTE.getName(), tipoComp);
	        
	        Registro rs = RegistroConverter
                    .toRegistro(requestManager.get(
                                    UrlServiceUtil.getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                    		SubdetallecomprobantecntsControladorUrlEnum.URL39125.getValue())
                                                    .getUrl(),
                                    param));
	
	        if (rs != null)
	        {
	            String tipo = SysmanFunciones.toString(rs.getCampos().get("TIPO_CPTE"));
	            String comprobante = SysmanFunciones.toString(rs.getCampos().get("COMPROBANTE"));
	            Date fecha = (Date) rs.getCampos().get("FECHA");
	            String fechaC = SysmanFunciones.convertirAFechaCadena(fecha);
	           
	            JsfUtil.agregarMensajeAlerta("La orden ya se encuentra afectada por el tipo: " 
	            	    + tipo + ", comprobante: " + comprobante + ", fecha: " + fechaC);
	        	
	        	return false;
	        }

		

		} catch (SystemException | ParseException e) {
			Logger.getLogger(SubdetallecomprobantecntsControlador.class
                    .getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
        // </CODIGO_DESARROLLADO>
        return true;
     }

    @Override
    public boolean eliminarDespues()
    {
    	if(registro.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString().equals("webservice")) {
            // se elimina el detalle del comprobante pero de la tabla detalle_cpte_afect_ws
    	    	try {
    		    	int delete = 0;
    		    	
    		    	Map<String, Object> param = new TreeMap<>();
    		        param.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
    		        param.put(GeneralParameterEnum.KEY_ANO.getName(), anio);
    		        param.put(GeneralParameterEnum.KEY_COMPROBANTE.getName(), numeroComp);
    		        param.put(GeneralParameterEnum.KEY_CONSECUTIVO_CPTE.getName(), registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
    		        param.put(GeneralParameterEnum.KEY_TIPO_CPTE.getName(), tipoComp);
    		        param.put(GeneralParameterEnum.KEY_DESCRIPCION.getName(), registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
    		        
    		        UrlBean urlBean = UrlServiceUtil.getInstance()
    					.getUrlServiceByUrlByEnumID(SubdetallecomprobantecntsControladorUrlEnum.URL191400D.getValue());
    	
    			
    				delete = requestManager.delete(urlBean.getUrl(), param);
    			} catch (SystemException e) {
    				Logger.getLogger(SubdetallecomprobantecntsControlador.class
    	                    .getName()).log(Level.SEVERE, null, e);
    				JsfUtil.agregarMensajeError(e.getMessage());
    			}
        	}
    	
    	Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
		param.put(GeneralParameterEnum.COMPROBANTE.getName(),
				registro.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()).toString());
		param.put(GeneralParameterEnum.CUENTA.getName(), 
				SysmanFunciones.nvl(registro.getCampos().get(
						GeneralParameterEnum.CUENTA.getName()),"").toString());

		try {
			
			Registro registroD;
			
			registroD = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													SubdetallecomprobantecntsControladorUrlEnum.URL69003.getValue())
											.getUrl(),
									param));
			
			String tipoRetencion = "";
			String codigoRetencion = "";
			
			if (registroD != null) {
			    tipoRetencion = registroD.getCampos().get("TIPORETENCION").toString();
			    codigoRetencion = registroD.getCampos().get("CODIGORETENCION").toString();
			}
			
			HashMap<String, Object> llaveDorden = new HashMap<>();
            llaveDorden.put(SubdetallecomprobantecntsControladorEnum.KEY_COMPANIA
                            .getValue(), compania);
            llaveDorden.put(SubdetallecomprobantecntsControladorEnum.KEY_ANO
                            .getValue(), anio);
            llaveDorden.put(SubdetallecomprobantecntsControladorEnum.KEY_TIPO
                            .getValue(), tipoComp);
            llaveDorden.put(SubdetallecomprobantecntsControladorEnum.KEY_NUMERO
                            .getValue(),registro.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()).toString());
            llaveDorden.put(SubdetallecomprobantecntsControladorEnum.KEY_TIPORETENCION
            		.getValue(), tipoRetencion);
            llaveDorden.put(SubdetallecomprobantecntsControladorEnum.KEY_CODIGORETENCION
            		.getValue(), codigoRetencion);

            UrlBean urlDelete =  UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
							SubdetallecomprobantecntsControladorUrlEnum.URL6900D.getValue());
            
            requestManager.delete(urlDelete.getUrl(), llaveDorden);
            
												
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        asignarTotales();

        return true;
    }

    @Override
    public void removerCombos()
    {
        registro.getCampos()
                        .remove(GeneralParameterEnum.DEPARTAMENTO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CIUDAD.getName());
        registro.getCampos().remove("PAIS");
        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.MAN_AUX_GEN
                                        .getValue());
        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.MAN_AUX_TER
                                        .getValue());
        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.CLASECUENTA
                                        .getValue());
        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.NOMBREPLANCONTABLE
                                        .getValue());
        registro.getCampos()
                        .remove(SubdetallecomprobantecntsControladorEnum.MAN_CEN_CTO
                                        .getValue());

        registro.getCampos().remove(
                        SubdetallecomprobantecntsControladorEnum.MOSTRAR_EN_FLUJO
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.MOVIMIENTO.getName());

    }

    @Override
    public void asignarValoresRegistro()
    {
        porcentajeBloqueado = false;
        registro.getCampos().put(CBASEGRAVABLE, baseGravable);
        registro.getCampos().put("BASE_IVA", ivaFacturado);
        registro.getCampos().put(CNRODOCUMENTO, numeroDocumento);
        inicializarRegistroNuevo();

    }

    public String getClaseCuenta(String campo)
    {
        String rta = "";
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.CUENTA
                                                        .getName()),
                                        "")
                        .toString());
        try
        {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL160183
                                                                            .getValue())
                                            .getUrl(), param));

            rta = regAux != null && regAux.getCampos().get(campo) != null
                ? regAux.getCampos().get(campo).toString()
                : "";
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        return rta;
    }

    public String getClaseContable()
    {
        String rta = "";
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM5.getValue(),
                        tipoComp);
        try
        {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL42516
                                                                            .getValue())
                                            .getUrl(), param));
            rta = SysmanFunciones
                            .nvl(regAux.getCampos().get("CLASE_CONTABLE"), "")
                            .toString();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        return rta;
    }

    public String getCuentaPptal()
    {
        String rta = "";
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.CUENTA
                                                        .getName()),
                                        "")
                        .toString());
        try
        {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL159016
                                                                            .getValue())
                                            .getUrl(), param));
            if (regAux != null)
            {
                rta = SysmanFunciones.nvl(
                                regAux.getCampos().get("CUENTA_PPTAL")
                                                .toString(),
                                "").toString();
            }
            else
            {
                rta = "''";
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        return rta;
    }

    public double getSaldo13(Double saldo13)
    {
        double neto = Double.parseDouble(SysmanFunciones
                        .nvl(registro.getCampos().get(CVALORDEBITO), "0")
                        .toString())
            - Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos().get(CVALORCREDITO), "0")
                            .toString());
        double netoAnt = Double.parseDouble(SysmanFunciones
                        .nvl(registroActivo.getCampos()
                                        .get(CVALORDEBITO), "0")
                        .toString())
            - Double.parseDouble(SysmanFunciones
                            .nvl(registroActivo.getCampos()
                                            .get(CVALORCREDITO), "0")
                            .toString());
        double saldo;
        if ("D".equals(SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.NATURALEZA
                                                        .getName()),
                                        "")
                        .toString()))
        {
            saldo = saldo13 + neto - netoAnt;
        }
        else
        {
            saldo = saldo13 + neto * -1 - netoAnt * -1;
        }
        return saldo;
    }

    public void asignarTotales()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM5.getValue(),
                        tipoComp);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM6.getValue(),
                        numeroComp);
        try
        {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL136529
                                                                            .getValue())
                                            .getUrl(), param));
            totalDebito = SysmanFunciones
                            .nvlStr(extraerString(regAux.getCampos()
                                            .get(CVALORDEBITO)), "0");
            totalCredito = SysmanFunciones.nvlStr(extraerString(
                            regAux.getCampos().get(CVALORCREDITO)), "0");
            diferencia = SysmanFunciones.nvlStr(
                            extraerString(regAux.getCampos().get("TOTAL")), "");
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void activarEdicion(Registro registroIn)
    {
        indice = listaInicial.getRowIndex();
        bloqueaFlujoEfectivo = false;

        Map<String, Object> registroIni = registroIn.getCampos();
        
        copiaValCredito = registroIni.get(CVALORCREDITO).toString();
        copiaBaseGravable = registroIni.get(CBASEGRAVABLE).toString();
        registroNuevo = false;
        
        copiaValorBase = registroIni.get(CBASEGRAVABLE);

        cuenta = SysmanFunciones.nvl(
                        registroIn.getCampos().get(
                                        GeneralParameterEnum.CUENTA.getName()),
                        "")
                        .toString();
        cargarListaCuentaPptal();
        cargarListaCuentaPptalE();
        cargarListaConceptoCUDS();
        cargarListaConceptoCUDSE();
        
	       terceroJud =  SysmanFunciones.toString(SysmanFunciones.nvl(
	                registroIn.getCampos().get(
	                                GeneralParameterEnum.TERCERO.getName()),
	                ""));
	       sucursalJud =  SysmanFunciones.toString(SysmanFunciones.nvl(
	                registroIn.getCampos().get(
	                                GeneralParameterEnum.SUCURSAL.getName()),
	                ""));
	    cargarListaprocesoJudicial();
	    terceroJud = null;
	    sucursalJud = null;
        
        modificarBase(cuenta);
        activoReg = true;
        activoReg2 = true;
        HashMap<String, Object> auxReg = new HashMap<>();
        auxReg.putAll(registroIn.getCampos());
        registroActivo = new Registro(auxReg);
        debitoAux = SysmanFunciones
                        .nvl(registroIn.getCampos().get(CVALORDEBITO), "")
                        .toString();
        creditoAux = SysmanFunciones
                        .nvl(registroIn.getCampos().get(CVALORCREDITO), "")
                        .toString();
        permiteModificarCuentaPptal = !existeComprobantePptal(registroIn);
        if ("I".equals(clase) || "B".equals(clase))
        {
            validarCuentasEquivalentes(true);
        }

        if ("0".equals(registroIn.getCampos()
                        .get(SubdetallecomprobantecntsControladorEnum.MOSTRAR_EN_FLUJO
                                        .getValue())
                        .toString()))
        {
            bloqueaFlujoEfectivo = true;
        }
        registro.getCampos().put("TIPO_NOTA", (registroIn.getCampos().get("TIPO_NOTA") != null)?registroIn.getCampos().get("TIPO_NOTA").toString():null);
        cambiarTipoNota();
    }

    /**
     * Verifica si se guardaron los datos del comprobante presupuestal. Ademas, captura la cuenta presupuestal en una variable auxiliar.
     *
     * @param registro
     * Detalle que se esta editando.
     * @return Verdadero si ya se genero el comprobante presupuestal.
     */
    private boolean existeComprobantePptal(Registro registro)
    {
        boolean rta = false;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM5.getValue(),
                        tipoComp);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM6.getValue(),
                        numeroComp);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.CONSECUTIVO
                                                        .getName()));
        try
        {
            Registro comprobantePresupuestal = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL40949
                                                                            .getValue())
                                            .getUrl(), param));
            String tipoPptal = SysmanFunciones
                            .nvl(comprobantePresupuestal.getCampos()
                                            .get("TIPOPPTAL"), "")
                            .toString();
            String numeroPPtal = SysmanFunciones.nvl(comprobantePresupuestal
                            .getCampos().get("NUMEROPPTAL"), "").toString();
            String consecutivoPpto = SysmanFunciones.nvl(comprobantePresupuestal
                            .getCampos().get("CONSECUTIVOPPTO"), "").toString();
            cuentaPptalAux = SysmanFunciones
                            .nvl(registro.getCampos().get(CCUENTAPPTAL), "")
                            .toString();
            rta = tipoPptal != null && numeroPPtal != null
                && consecutivoPpto != null;
        }
        catch (SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        return rta;
    }

    public void modificarBase(String cuenta)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "I");
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM5.getValue(),
                        tipoComp);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM6.getValue(),
                        numeroComp);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
        List<Registro> aux;
        try
        {
            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL133408
                                                                            .getValue())
                                            .getUrl(), param));
            if (!aux.isEmpty())
            {
                permitemodificarbase = "0".equals(aux.get(0).getCampos()
                                .get(CPERMITEMODBASE) == null ? "0"
                                    : aux.get(0).getCampos()
                                                    .get(CPERMITEMODBASE));
                permitemodificarCredito = "0".equals(aux.get(0).getCampos()
                                .get("PERMITEMODIFICAR") == null ? "0"
                                    : aux.get(0).getCampos()
                                                    .get(CPERMITEMODBASE));
            }
            else
            {
                permitemodificarbase = false;
                permitemodificarCredito = false;
            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void ejecutarrcCerrar()
    {
    	/*
    	 * valida si el centro de costo y el detalle estan con varios, esto siempre y cuando el parametro este en SI
    	 */
    	boolean referenciaYCentroCostoVarios = validarReferenciaYCentroCostoVarios();
        validarConceptoExogenas(permisos[2]);
        if (!redireccionar && referenciaYCentroCostoVarios)
        {
            redireccionar();
        }

    }

    public void redireccionar()
    {

        cerrar = true;
        asignarTotales();
        validarDiferencia();
        validarComprobanteEnDolares();
        redireccionar = false;

        // if ()
        // {
        // JsfUtil.agregarMensajeInformativo(idioma.getString("TE_DG190"));
        // }
        if (cerrar || !permisos[0] &&
            !permisos[1] &&
            !permisos[2])
        {
            boolean indNoComprPptal = consultarGenerarComprobantePresupuestal();
            String[] campos = { "rid", "ano", "mes", CTIPOMOV, COPCIONMENU,
                                "accion", "indicadorNoPpto", "parametroswf" };
            Object[] valores = { rid, anio, mes, tipoComp, opcionMenu,
                                 accionEncabezado, indNoComprPptal, parametroswf };
            SessionUtil.redireccionar(RUTA_ENCABEZADO_CONTAB, campos, valores);
        }

    }
    private boolean validarReferenciaYCentroCostoVarios() {
    	try {
			String causacionAuto = SysmanFunciones.nvlStr(sysmanUtil.consultarParametro(compania,
			        "CONTROLA AUXILIAR Y CENTRO COSTO EN CAUSACION AUTOMATICA",
			        modulo,
			        new Date(), true), "NO");
			if(causacionAuto.equals("SI")) {
				List<Registro> listadoEvalua = listaInicial.getDatasource();
				int cont = 0;
				String cuentas = "";
				for(Registro reg: listadoEvalua) {
					if((reg.getCampos().get("REFERENCIA") != null && reg.getCampos().get("REFERENCIA").toString().equals("99999999999999999999")) || (reg.getCampos().get("CENTRO_COSTO") != null && reg.getCampos().get("CENTRO_COSTO").toString().equals("99999999999999999999"))) {
						cont ++;
						if(cont == 1) {
							cuentas = reg.getCampos().get("CUENTA").toString();
						}else {
							cuentas = cuentas + "," +reg.getCampos().get("CUENTA").toString();
						}
					}
				}
				if(cont > 0) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("MSM_INFO_REFERENCIA_CENTRO_COSTO_EN_DETALLES").replace("s$cuentas$s", cuentas));
					return false;
				}else {
					return true;
				}
			}else {
				return true;
			}
		} catch (SystemException e) {
			Logger.getLogger(SubdetallecomprobantecntsControlador.class
                    .getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return true;
		}
    	
    }
    public void evaluarManejoControl(BigDecimal vlrDocumentoAux)
    {
        if ("SI".equals(getParametro(
                        "MANEJA CONTROL DE VALOR A GIRAR MAYOR QUE VALOR DOCUMENTO",
                        "NO")))
        {
            BigDecimal valorDocumentoAux = vlrDocumentoAux;
            BigDecimal valorTotalDebito = BigDecimal.valueOf(Double
                            .parseDouble(totalDebito.replace(",", "")));
            if (valorDocumentoAux.compareTo(valorTotalDebito) != 0)
            {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB800"));
            }
        }
    }

    /**
     * Si el comprobante esta descuadrado en dolares, no permite cerrar el detalle contable.
     */
    private void validarComprobanteEnDolares()
    {
        if ("SI".equals(getParametro("MANEJA DOLARES", "NO")))
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(SubdetallecomprobantecntsControladorEnum.PARAM5
                            .getValue(), tipoComp);
            param.put(SubdetallecomprobantecntsControladorEnum.PARAM6
                            .getValue(), numeroComp);
            Registro regAux;
            try
            {
                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubdetallecomprobantecntsControladorUrlEnum.URL159786
                                                                                .getValue())
                                                .getUrl(), param));

                BigDecimal creditoDolares = new BigDecimal(extraerString(
                                regAux.getCampos().get("DEBITODOL")));
                BigDecimal debitoDolares = new BigDecimal(extraerString(
                                regAux.getCampos().get("CREDITODOL")));
                if (creditoDolares.compareTo(debitoDolares) != 0)
                {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB797")
                                    .replace("#$debitos$#",
                                                    String.valueOf(debitoDolares))
                                    .replace("#$creditos$#",
                                                    String.valueOf(creditoDolares))
                                    .replace("#$diferencia$#.",
                                                    String.valueOf(Math
                                                                    .abs(debitoDolares
                                                                                    .doubleValue()
                                                                        - creditoDolares.doubleValue()))));
                    cerrar = false;
                }
            }
            catch (SystemException e)
            {
                Logger.getLogger(SubdetallecomprobantecntsControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    /**
     * Si el comprobante esta descuadrado no permite cerrar el detalle contable.
     */
    private void validarDiferencia()
    {
        BigDecimal valorDiferencia = BigDecimal.valueOf(
                        Double.parseDouble(diferencia.replace(",", "")));
        if (valorDiferencia.compareTo(BigDecimal.ZERO) != 0)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB794")
                            .replace("#$debitos$#", totalDebito)
                            .replace("#$creditos$#", totalCredito)
                            .replace("#$diferencia$#", diferencia));
            cerrar = false;
        }
    }

    /**
     * Para mostrar advertencia al usuario respecto a la configuracion del concepto de exogenas.
     *
     * @param permiteModificar
     * Indicador de edicion.
     */
    private void validarConceptoExogenas(boolean permiteModificar)
    {
        String valorParametro = getParametro(
                        "VALIDAR CONCEPTO DE EXOGENAS EN IMPUTACION CONTABLE",
                        "NO");
        if (permiteModificar && "SI".equals(valorParametro))
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(SubdetallecomprobantecntsControladorEnum.PARAM5
                            .getValue(), tipoComp);
            param.put(SubdetallecomprobantecntsControladorEnum.PARAM6
                            .getValue(), numeroComp);
            List<Registro> conceptos;
            try
            {
                conceptos = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubdetallecomprobantecntsControladorUrlEnum.URL119247
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                analizarConceptos(conceptos);
            }
            catch (SystemException e)
            {
                Logger.getLogger(SubdetallecomprobantecntsControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void analizarConceptos(List<Registro> conceptos)
    {
        for (Registro concepto : conceptos)
        {
            if (concepto.getCampos().get(CCONCEPTOEX) == null)
            {
                dialogoConceptos = true;
                redireccionar = true;
                break;
            }
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo conceptos en la vista
     *
     *
     */
    public void aceptarconceptos()
    {
        dialogoConceptos = false;

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo conceptos en la vista
     *
     *
     */
    public void cancelarconceptos()
    {
        dialogoConceptos = false;
        redireccionar();
    }

    private boolean consultarGenerarComprobantePresupuestal()
    {

        Map<String, Object> param1 = new TreeMap<>();
        param1.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param1.put(GeneralParameterEnum.ANO.getName(), anio);
        param1.put("TIPOCOMPROBANTE", tipoComp);
        param1.put("NUMEROCOMPROBANTE", numeroComp);

        Registro regPptal;
        try
        {
            regPptal = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComprobantecntsControladorUrlEnum.URL18261
                                                                            .getValue())
                                            .getUrl(), param1));

            if (Integer.parseInt(regPptal.getCampos()
                            .get(GeneralParameterEnum.CANTIDAD.getName())
                            .toString()) == 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo comprobantepresupuestal en la vista
     *
     *
     */
    public void aceptarcomprobantepresupuestal()
    {
        // dialogo
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo comprobantepresupuestal en la vista
     *
     */
    public void cancelarcomprobantepresupuestal()
    {
        // dialogo
    }

    public double verificarCxp(String cuenta, String valorD)
    {
        double rta = 0;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM5
                        .getValue(), tipoComp);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM6
                        .getValue(), numeroComp);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM11
                        .getValue(), valorD);
        Registro regAux;
        try
        {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubdetallecomprobantecntsControladorUrlEnum.URL157271
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            if (regAux != null)
            {
                rta = Double.parseDouble(
                                SysmanFunciones.nvl(
                                                regAux.getCampos().get(
                                                                "VALOR_CXP"),
                                                "").toString());
            }
            else
            {
                rta = 0;
            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    public void cambiardialogoFacturacion()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiardialogoRubro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiardialogoAfectarRes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDialogoDistribucion()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control NroDocumento
     *
     */
    public void cambiarNroDocumento()
    {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "NRO_DOCUMENTO")
            && !isNumeric(registro.getCampos().get("NRO_DOCUMENTO")
                            .toString()))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4234"));
            registro.getCampos().put("NRO_DOCUMENTO", null);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control NroDocumento en la fila seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarNroDocumentoC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarCampoVacio(
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos(),
                        "NRO_DOCUMENTO")
            && !isNumeric(listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos().get("NRO_DOCUMENTO")
                            .toString()))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4234"));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put("NRO_DOCUMENTO", null);

        }
        // </CODIGO_DESARROLLADO>
    }

    public void aceptardialogoFacturacion()
    {
        if (afecta)
        {
            registro.getCampos().put(CTIPOCPTEAFECT,
                            SysmanFunciones.nvl(tipoFactura, "SRV"));
            registro.getCampos().put(CCMPTEAFECTADO,
                            SysmanFunciones.nvl(nFactura, "0"));
            registro.getCampos().put(CANOAFECT, anio);
        }
        else
        {
            if (factura != null)
            {
                evaluarAfectados3();
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB804"));
            }
        }
        facturacionVisible = false;
    }

    public void evaluarAfectados3()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubdetallecomprobantecntsControladorEnum.PARAM12
                        .getValue(), factura);
        List<Registro> aux;
        try
        {
            aux = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubdetallecomprobantecntsControladorUrlEnum.URL35965
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            if (aux.isEmpty())
            {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB803"));
            }
            else
            {
                registro.getCampos().put(CVALORCREDITO,
                                aux.get(0).getCampos().get("VALORCRE"));
                registro.getCampos().put(CTIPOCPTEAFECT, "SRV");
                registro.getCampos().put(CCMPTEAFECTADO, factura);
                registro.getCampos().put(CANOAFECT, anio);
            }

        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cancelardialogoFacturacion()
    {
        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB805"));
        facturacionVisible = false;
    }

    public void aceptardialogoRubro()
    {
        registro.getCampos().put(CCUENTAPPTAL, rubro);
        rubroVisible = false;
    }

    public void cancelardialogoRubro()
    {
        rubroVisible = false;
    }

    public void aceptarDialogoDistribucion()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(),
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.CUENTA
                                                        .getName()),
                                        "").toString());

        try
        {
            List<Registro> aux = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubdetallecomprobantecntsControladorUrlEnum.URL48173
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            for (Registro aux1 : aux)
            {
                long consecutivo = sysmanUtil.generarSiguienteConsecutivo(
                                "DETALLE_COMPROBANTE_CNT",
                                "DETALLE_COMPROBANTE_CNT.COMPANIA =''"
                                    + compania + "''"
                                    + " AND DETALLE_COMPROBANTE_CNT.ANO ="
                                    + anio
                                    + " AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = ''"
                                    + tipoComp
                                    + "'' AND DETALLE_COMPROBANTE_CNT.COMPROBANTE="
                                    + numeroComp,
                                "CONSECUTIVO");
                Map<String, Object> param2 = new TreeMap<>();
                param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param2.put(GeneralParameterEnum.ANO.getName(), anio);
                param2.put(GeneralParameterEnum.CUENTA.getName(),
                                SysmanFunciones.nvl(aux1.getCampos()
                                                .get("CUENTA_DIS"),
                                                "").toString());

                Registro regAux = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubdetallecomprobantecntsControladorUrlEnum.URL48174
                                                                                                .getValue())
                                                                .getUrl(),
                                                param2));

                String naturaleza = SysmanFunciones
                                .nvl(regAux.getCampos()
                                                .get(GeneralParameterEnum.NATURALEZA
                                                                .getName()),
                                                "")
                                .toString();
                double valorCuen = Double.parseDouble(valorDistribuir)
                    * Double.parseDouble(
                                    SysmanFunciones.nvl(
                                                    aux1.getCampos().get(
                                                                    "PORCENTAJE"),
                                                    "0.0").toString())
                    / 100;
                Map<String, Object> parametros = new HashMap<>();
                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(GeneralParameterEnum.ANO.getName(), anio);
                parametros.put(SubdetallecomprobantecntsControladorEnum.PARAM5
                                .getValue(), tipoComp);
                parametros.put(SubdetallecomprobantecntsControladorEnum.PARAM6
                                .getValue(), numeroComp);
                parametros.put(GeneralParameterEnum.CUENTA.getName(),
                                aux1.getCampos().get("CUENTA_DIS"));
                parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                consecutivo);
                parametros.put(GeneralParameterEnum.NATURALEZA.getName(),
                                naturaleza);
                parametros.put(GeneralParameterEnum.DESCRIPCION.getName(),
                                descripcion);
                parametros.put(GeneralParameterEnum.FECHA.getName(), fecha);
                parametros.put(GeneralParameterEnum.TERCERO.getName(), tercero);
                parametros.put(GeneralParameterEnum.SUCURSAL.getName(),
                                sucursal);
                parametros.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                                aux1.getCampos().get(
                                                GeneralParameterEnum.CENTRO_COSTO
                                                                .getName()));
                parametros.put(GeneralParameterEnum.AUXILIAR.getName(),
                                auxiliar);
                parametros.put(SubdetallecomprobantecntsControladorEnum.PARAM13
                                .getValue(), valorCuen);
                parametros.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                parametros.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantecntsControladorUrlEnum.URL48175
                                                                .getValue());
                requestManager.saveCount(urlCreate.getUrl(),
                                urlCreate.getMetodo(), parametros);

            }
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB808"));
        distribucionVisible = false;
        reasignarOrigen();
    }

    public void cancelarDialogoDistribucion()
    {
        distribucionVisible = false;
    }

    public void retornarFormularioRevelaciones(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioAuxRevelaciones()
    {
        if (SessionUtil.getSessionVar(CMENSAJEREV) != null)
        {
            JsfUtil.agregarMensajeInformativo(
                            SessionUtil.getSessionVar(CMENSAJEREV).toString());
            SessionUtil.removeSessionVar(CMENSAJEREV);
        }
    }

    public void aceptardialogoValorGirar()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            // ACA
            BigDecimal vlrDocumentoAux = contabilidadSeis.calcularValorAGirar(
                            compania, Integer.parseInt(anio), tipoComp,
                            new BigInteger(numeroComp), clase,
                            BigDecimal.ZERO, new BigDecimal(valorDocumento),
                            new BigDecimal(totalDebito),
                            new BigDecimal(valorGirarDialogo));

            if ("SI".equals(SysmanFunciones
                            .nvl(sysmanUtil.consultarParametro(compania,
                                            "MANEJA CONTROL DE VALOR A GIRAR MAYOR QUE VALOR DOCUMENTO",
                                            modulo, new Date(), true), "NO"))
                && SysmanFunciones.redondear(vlrDocumentoAux,
                                2) != BigDecimal.valueOf(
                                                SysmanFunciones.redondear(
                                                                Double.parseDouble(
                                                                                totalDebito
                                                                                                .replace(",", "")),
                                                                2)))
            {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB800"));
            }
            if (cerrar)
            {
                String[] campos = { "rid", "ano", "mes", CTIPOMOV,
                                    COPCIONMENU };
                Object[] valores = { rid, anio, mes, tipoComp, opcionMenu };
                SessionUtil.redireccionar(RUTA_ENCABEZADO_CONTAB, campos,
                                valores);
            }
        }
        catch (NumberFormatException | SystemException ex)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + " "
                                + ex.getMessage());
        }

        dialogoValorGirarVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelardialogoValorGirar()
    {
        // <CODIGO_DESARROLLADO>
        dialogoValorGirarVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * public void gruposcuentas() { Registro grupo= sysmanUtil.consultarParametro(compania, "MANEJA COMPROBANTE AFECTADO CERO", modulo, new Date(), true);
     *
     * listaGruposTesoreria.add(grupo); }
     **/

    /**
     * Trae el valor almacenado en la base de datos para el parametro ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault)
    {
        String parametro = null;
        try
        {
            parametro = sysmanUtil.consultarParametro(compania, nombreParametro,
                            modulo, new Date(), true);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * comprueba si el usuario tiene permisos para seleccionar cuentas
     **/
    public void comprobarpermisoscuentas()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("USUARIO", usuario.getCodigo());
        try
        {
            gruposasignados = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubdetallecomprobantecntsControladorUrlEnum.URL29152
                                                                            .getValue())
                                            .getUrl(), param));
            String grupos = sysmanUtil.consultarParametro(compania,
                            "NOMBRE GRUPO TESORERIA",
                            modulo, new Date(), true);

            String[] asignados = grupos.split(",");

            for (int i = 0; i < gruposasignados.size(); i++)
            {
                String grupo = gruposasignados.get(i).getCampos().get("GRUPO").toString();

                for (String asignado : asignados)
                {
                    if (grupo.equals(asignado))
                    {
                        bloqueacuentas = false;
                        System.out.println(asignado + "<<<<<bloqueado false");
                        break;
                    }
                }
                if (!bloqueacuentas)
                {
                    break;
                }
            }

            System.out.println("grupos:" + grupos);

            System.out.println("gruposasignados:" + gruposasignados.get(0).getCampos().get("GRUPO").toString());
        }
        catch (SystemException e)
        {
            Logger.getLogger(SubdetallecomprobantecntsControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    private String obtenerParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = sysmanUtil.consultarParametro(SessionUtil.getCompania(), nombreParametro,
					SessionUtil.getModulo(), new Date(), true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}
    /**
     * Extrae la cadena que representa al objeto, solo si es diferente de nulo.
     *
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object)
    {
        return object != null ? object.toString() : null;
    }

    public List<Registro> getListaTipoDoc()
    {
        return listaTipoDoc;
    }

    public void setListaTipoDoc(List<Registro> listaTipoDoc)
    {
        this.listaTipoDoc = listaTipoDoc;
    }

    public RegistroDataModelImpl getListaResponsable()
    {
        return listaResponsable;
    }

    public void setListaResponsable(RegistroDataModelImpl listaResponsable)
    {
        this.listaResponsable = listaResponsable;
    }

    public RegistroDataModelImpl getListaResponsableE()
    {
        return listaResponsableE;
    }

    public void setListaResponsableE(RegistroDataModelImpl listaResponsableE)
    {
        this.listaResponsableE = listaResponsableE;
    }

    public List<Registro> getListaDependencia()
    {
        return listaDependencia;
    }

    public void setListaDependencia(List<Registro> listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getFecha()
    {
        return fecha;
    }

    public void setFecha(String fecha)
    {
        this.fecha = fecha;
    }

    public String getTercero()
    {
        return tercero;
    }

    public void setTercero(String tercero)
    {
        this.tercero = tercero;
    }

    public String getSucursal()
    {
        return sucursal;
    }

    public void setSucursal(String sucursal)
    {
        this.sucursal = sucursal;
    }

    public RegistroDataModelImpl getListaCentroCosto()
    {
        return listaCentroCosto;
    }

    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto)
    {
        this.listaCentroCosto = listaCentroCosto;
    }

    public RegistroDataModelImpl getListaCentroCostoE()
    {
        return listaCentroCostoE;
    }

    public void setListaCentroCostoE(RegistroDataModelImpl listaCentroCostoE)
    {
        this.listaCentroCostoE = listaCentroCostoE;
    }

    public RegistroDataModelImpl getListaTercero()
    {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero)
    {
        this.listaTercero = listaTercero;
    }

    public RegistroDataModelImpl getListaTerceroE()
    {
        return listaTerceroE;
    }

    public void setListaTerceroE(RegistroDataModelImpl listaTerceroE)
    {
        this.listaTerceroE = listaTerceroE;
    }

    public RegistroDataModelImpl getListaCmbFuenteRecurso()
    {
        return listaCmbFuenteRecurso;
    }

    public void setListaCmbFuenteRecurso(
        RegistroDataModelImpl listaCmbFuenteRecurso)
    {
        this.listaCmbFuenteRecurso = listaCmbFuenteRecurso;
    }

    public RegistroDataModelImpl getListaCmbFuenteRecursoE()
    {
        return listaCmbFuenteRecursoE;
    }

    public void setListaCmbFuenteRecursoE(
        RegistroDataModelImpl listaCmbFuenteRecursoE)
    {
        this.listaCmbFuenteRecursoE = listaCmbFuenteRecursoE;
    }

    /**
     * Retorna la lista listaCodigoFlujoEfectivo
     *
     * @return listaCodigoFlujoEfectivo
     */
    public RegistroDataModelImpl getListaCodigoFlujoEfectivo()
    {
        return listaCodigoFlujoEfectivo;
    }

    /**
     * Asigna la lista listaCodigoFlujoEfectivo
     *
     * @param listaCodigoFlujoEfectivo
     * Variable a asignar en listaCodigoFlujoEfectivo
     */
    public void setListaCodigoFlujoEfectivo(
        RegistroDataModelImpl listaCodigoFlujoEfectivo)
    {
        this.listaCodigoFlujoEfectivo = listaCodigoFlujoEfectivo;
    }

    /**
     * Retorna la lista listaCodigoFlujoEfectivo
     *
     * @return listaCodigoFlujoEfectivo
     */
    public RegistroDataModelImpl getListaCodigoFlujoEfectivoE()
    {
        return listaCodigoFlujoEfectivoE;
    }

    /**
     * Asigna la lista listaCodigoFlujoEfectivo
     *
     * @param listaCodigoFlujoEfectivo
     * Variable a asignar en listaCodigoFlujoEfectivo
     */
    public void setListaCodigoFlujoEfectivoE(
        RegistroDataModelImpl listaCodigoFlujoEfectivoE)
    {
        this.listaCodigoFlujoEfectivoE = listaCodigoFlujoEfectivoE;
    }

    public RegistroDataModelImpl getListaCmpReferencia()
    {
        return listaCmpReferencia;
    }

    public RegistroDataModelImpl getListaCmpReferenciaE()
    {
        return listaCmpReferenciaE;
    }

    public void setListaCmpReferenciaE(
        RegistroDataModelImpl listaCmpReferenciaE)
    {
        this.listaCmpReferenciaE = listaCmpReferenciaE;
    }

    public void setListaCmpReferencia(
        RegistroDataModelImpl listaCmpReferencia)
    {
        this.listaCmpReferencia = listaCmpReferencia;
    }

    public RegistroDataModelImpl getListaAuxiliar()
    {
        return listaAuxiliar;
    }

    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar)
    {
        this.listaAuxiliar = listaAuxiliar;
    }

    public RegistroDataModelImpl getListaAuxiliarE()
    {
        return listaAuxiliarE;
    }

    public void setListaAuxiliarE(RegistroDataModelImpl listaAuxiliarE)
    {
        this.listaAuxiliarE = listaAuxiliarE;
    }

    public RegistroDataModelImpl getListaCmpteAfectado()
    {
        return listaCmpteAfectado;
    }

    public void setListaCmpteAfectado(
        RegistroDataModelImpl listaCmpteAfectado)
    {
        this.listaCmpteAfectado = listaCmpteAfectado;
    }

    public RegistroDataModelImpl getListaCmpteAfectadoE()
    {
        return listaCmpteAfectadoE;
    }

    public void setListaCmpteAfectadoE(
        RegistroDataModelImpl listaCmpteAfectadoE)
    {
        this.listaCmpteAfectadoE = listaCmpteAfectadoE;
    }

    public RegistroDataModelImpl getListaConceptoEx()
    {
        return listaConceptoEx;
    }

    public void setListaConceptoEx(RegistroDataModelImpl listaConceptoEx)
    {
        this.listaConceptoEx = listaConceptoEx;
    }

    public RegistroDataModelImpl getListaConceptoExE()
    {
        return listaConceptoExE;
    }

    public void setListaConceptoExE(RegistroDataModelImpl listaConceptoExE)
    {
        this.listaConceptoExE = listaConceptoExE;
    }

    public RegistroDataModelImpl getListaBancoRetencion()
    {
        return listaBancoRetencion;
    }

    public void setListaBancoRetencion(
        RegistroDataModelImpl listaBancoRetencion)
    {
        this.listaBancoRetencion = listaBancoRetencion;
    }

    public RegistroDataModelImpl getListaBancoRetencionE()
    {
        return listaBancoRetencionE;
    }

    public void setListaBancoRetencionE(
        RegistroDataModelImpl listaBancoRetencionE)
    {
        this.listaBancoRetencionE = listaBancoRetencionE;
    }

    public RegistroDataModelImpl getListaCuenta()
    {
        return listaCuenta;
    }

    public void setListaCuenta(RegistroDataModelImpl listaCuenta)
    {
        this.listaCuenta = listaCuenta;
    }

    public RegistroDataModelImpl getListaCuentaE()
    {
        return listaCuentaE;
    }

    public void setListaCuentaE(RegistroDataModelImpl listaCuentaE)
    {
        this.listaCuentaE = listaCuentaE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getNitcompania()
    {
        return nitcompania;
    }

    public void setNitcompania(String nitcompania)
    {
        this.nitcompania = nitcompania;
    }

    public String getTipoComp()
    {
        return tipoComp;
    }

    public void setTipoComp(String tipoComp)
    {
        this.tipoComp = tipoComp;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getNumeroComp()
    {
        return numeroComp;
    }

    public void setNumeroComp(String numeroComp)
    {
        this.numeroComp = numeroComp;
    }

    public String getEncabezado()
    {
        return encabezado;
    }

    public void setEncabezado(String encabezado)
    {
        this.encabezado = encabezado;
    }

    public String getTotalDebito()
    {
        return totalDebito;
    }

    public void setTotalDebito(String totalDebito)
    {
        this.totalDebito = totalDebito;
    }

    public String getTotalCredito()
    {
        return totalCredito;
    }

    public void setTotalCredito(String totalCredito)
    {
        this.totalCredito = totalCredito;
    }

    public String getDiferencia()
    {
        return diferencia;
    }

    public void setDiferencia(String diferencia)
    {
        this.diferencia = diferencia;
    }

    public String getAuxNombre()
    {
        return auxNombre;
    }

    public void setAuxNombre(String auxNombre)
    {
        this.auxNombre = auxNombre;
    }

    public String getAuxSucursal()
    {
        return auxSucursal;
    }

    public void setAuxSucursal(String auxSucursal)
    {
        this.auxSucursal = auxSucursal;
    }

    public List<Registro> getListaTipoCpteAfect()
    {
        return listaTipoCpteAfect;
    }

    public void setListaTipoCpteAfect(List<Registro> listaTipoCpteAfect)
    {
        this.listaTipoCpteAfect = listaTipoCpteAfect;
    }

    public int getIndice()
    {
        return indice;
    }

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    public boolean isPermitemodificarbase()
    {
        return permitemodificarbase;
    }

    public void setPermitemodificarbase(boolean permitemodificarbase)
    {
        this.permitemodificarbase = permitemodificarbase;
    }

    public boolean isCargarSaldoCuentaAux()
    {
        return cargarSaldoCuentaAux;
    }

    public void setCargarSaldoCuentaAux(boolean cargarSaldoCuentaAux)
    {
        this.cargarSaldoCuentaAux = cargarSaldoCuentaAux;
    }

    public String getImpreso()
    {
        return impreso;
    }

    public void setImpreso(String impreso)
    {
        this.impreso = impreso;
    }

    public String getEstadoPeriodo()
    {
        return estadoPeriodo;
    }

    public void setEstadoPeriodo(String estadoPeriodo)
    {
        this.estadoPeriodo = estadoPeriodo;
    }

    public String getClase()
    {
        return clase;
    }

    public void setClase(String clase)
    {
        this.clase = clase;
    }

    public boolean isCargaFuenteRecurso()
    {
        return validaAuxiliares;
    }

    public void setCargaFuenteRecurso(
        boolean cargaFuenteRecurso)
    {
        this.validaAuxiliares = cargaFuenteRecurso;
    }

    public boolean isPorcentajeRetencionBloqueado()
    {
        return porcentajeRetencionBloqueado;
    }

    public void setPorcentajeRetencionBloqueado(
        boolean porcentajeRetencionBloqueado)
    {
        this.porcentajeRetencionBloqueado = porcentajeRetencionBloqueado;
    }

    public boolean isPorcentajeRetencionCargar()
    {
        return porcentajeRetencionCargar;
    }

    public void setPorcentajeRetencionCargar(
        boolean porcentajeRetencionCargar)
    {
        this.porcentajeRetencionCargar = porcentajeRetencionCargar;
    }

    public boolean isReconocimientoCargar()
    {
        return reconocimientoCargar;
    }

    public void setReconocimientoCargar(boolean reconocimientoCargar)
    {
        this.reconocimientoCargar = reconocimientoCargar;
    }

    public boolean isFechaPlanoCargar()
    {
        return fechaPlanoCargar;
    }

    public void setFechaPlanoCargar(boolean fechaPlanoCargar)
    {
        this.fechaPlanoCargar = fechaPlanoCargar;
    }

    public boolean isCargaPptal()
    {
        return cargaPptal;
    }

    public void setCargaPptal(boolean cargaPptal)
    {
        this.cargaPptal = cargaPptal;
    }

    public boolean isCargaResDep()
    {
        return cargaResDep;
    }

    public void setCargaResDep(boolean cargaResDep)
    {
        this.cargaResDep = cargaResDep;
    }

    public String getResponsable()
    {
        return responsable;
    }

    public void setResponsable(String responsable)
    {
        this.responsable = responsable;
    }

    public String getSucursalResponsable()
    {
        return sucursalResponsable;
    }

    public void setSucursalResponsable(String sucursalResponsable)
    {
        this.sucursalResponsable = sucursalResponsable;
    }

    public boolean isDialogoValorGirarVisible()
    {
        return dialogoValorGirarVisible;
    }

    public void setDialogoValorGirarVisible(boolean dialogoValorGirarVisible)
    {
        this.dialogoValorGirarVisible = dialogoValorGirarVisible;
    }

    public boolean isActivoReg()
    {
        return activoReg;
    }

    public void setActivoReg(boolean activoReg)
    {
        this.activoReg = activoReg;
    }

    public Date getFechaPar()
    {
        return fechaPar;
    }

    public void setFechaPar(Date fechaPar)
    {
        this.fechaPar = fechaPar;
    }

    public boolean isCreditoNuevoBloqueado()
    {
        return creditoNuevoBloqueado;
    }

    public void setCreditoNuevoBloqueado(boolean creditoNuevoBloqueado)
    {
        this.creditoNuevoBloqueado = creditoNuevoBloqueado;
    }

    public boolean isDebitoNuevoBloqueado()
    {
        return debitoNuevoBloqueado;
    }

    public void setDebitoNuevoBloqueado(boolean debitoNuevoBloqueado)
    {
        this.debitoNuevoBloqueado = debitoNuevoBloqueado;
    }

    public String getReferencia()
    {
        return referencia;
    }

    public void setReferencia(String referencia)
    {
        this.referencia = referencia;
    }

    public String getFuenteRecurso()
    {
        return fuenteRecurso;
    }

    public void setFuenteRecurso(String fuenteRecurso)
    {
        this.fuenteRecurso = fuenteRecurso;
    }

    public String getValorDocumento()
    {
        return valorDocumento;
    }

    public void setValorDocumento(String valorDocumento)
    {
        this.valorDocumento = valorDocumento;
    }

    public String getCompania()
    {
        return compania;
    }

    public void setCompania(String compania)
    {
        this.compania = compania;
    }

    public String getModulo()
    {
        return modulo;
    }

    public void setModulo(String modulo)
    {
        this.modulo = modulo;
    }

    public String getDebitoAux()
    {
        return debitoAux;
    }

    public void setDebitoAux(String debitoAux)
    {
        this.debitoAux = debitoAux;
    }

    public String getCreditoAux()
    {
        return creditoAux;
    }

    public void setCreditoAux(String creditoAux)
    {
        this.creditoAux = creditoAux;
    }

    public boolean isPermitemodificarCredito()
    {
        return permitemodificarCredito;
    }

    public void setPermitemodificarCredito(boolean permitemodificarCredito)
    {
        this.permitemodificarCredito = permitemodificarCredito;
    }

    public String getSaldoAuxAfec()
    {
        return saldoAuxAfec;
    }

    public void setSaldoAuxAfec(String saldoAuxAfec)
    {
        this.saldoAuxAfec = saldoAuxAfec;
    }

    public Registro getRegistroActivo()
    {
        return registroActivo;
    }

    public void setRegistroActivo(Registro registroActivo)
    {
        this.registroActivo = registroActivo;
    }

    public boolean isAfectadoBloqueado()
    {
        return afectadoBloqueado;
    }

    public void setAfectadoBloqueado(boolean afectadoBloqueado)
    {
        this.afectadoBloqueado = afectadoBloqueado;
    }

    public boolean isActivoReg2()
    {
        return activoReg2;
    }

    public void setActivoReg2(boolean activoReg2)
    {
        this.activoReg2 = activoReg2;
    }

    public String getValorGirar()
    {
        return valorGirar;
    }

    public void setValorGirar(String valorGirar)
    {
        this.valorGirar = valorGirar;
    }

    public List<Registro> getListaTipoFactura()
    {
        return listaTipoFactura;
    }

    public void setListaTipoFactura(List<Registro> listaTipoFactura)
    {
        this.listaTipoFactura = listaTipoFactura;
    }

    public List<Registro> getListaNumeroFactura()
    {
        return listaNumeroFactura;
    }

    public void setListaNumeroFactura(List<Registro> listaNumeroFactura)
    {
        this.listaNumeroFactura = listaNumeroFactura;
    }

    public List<Registro> getListaAnio()
    {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio)
    {
        this.listaAnio = listaAnio;
    }

    public String getTipoFactura()
    {
        return tipoFactura;
    }

    public void setTipoFactura(String tipoFactura)
    {
        this.tipoFactura = tipoFactura;
    }

    public String getnFactura()
    {
        return nFactura;
    }

    public void setnFactura(String nFactura)
    {
        this.nFactura = nFactura;
    }

    public String getAnioFact()
    {
        return anioFact;
    }

    public void setAnioFact(String anioFact)
    {
        this.anioFact = anioFact;
    }

    public String getFactura()
    {
        return factura;
    }

    public void setFactura(String factura)
    {
        this.factura = factura;
    }

    public boolean isAfecta()
    {
        return afecta;
    }

    public void setAfecta(boolean afecta)
    {
        this.afecta = afecta;
    }

    public boolean isFacturacionVisible()
    {
        return facturacionVisible;
    }

    public void setFacturacionVisible(boolean facturacionVisible)
    {
        this.facturacionVisible = facturacionVisible;
    }

    public String getRubro()
    {
        return rubro;
    }

    public void setRubro(String rubro)
    {
        this.rubro = rubro;
    }

    public boolean isRubroVisible()
    {
        return rubroVisible;
    }

    public void setRubroVisible(boolean rubroVisible)
    {
        this.rubroVisible = rubroVisible;
    }

    public boolean isFechaBloqueada()
    {
        return fechaBloqueada;
    }

    public void setFechaBloqueada(boolean fechaBloqueada)
    {
        this.fechaBloqueada = fechaBloqueada;
    }

    public RegistroDataModelImpl getListaRubro()
    {
        return listaRubro;
    }

    public void setListaRubro(RegistroDataModelImpl listaRubro)
    {
        this.listaRubro = listaRubro;
    }

    public RegistroDataModelImpl getListaRubroE()
    {
        return listaRubroE;
    }

    public void setListaRubroE(RegistroDataModelImpl listaRubroE)
    {
        this.listaRubroE = listaRubroE;
    }

    /**
     * Retorna la lista listaCuentaPptal
     *
     * @return listaCuentaPptal
     */
    public RegistroDataModelImpl getListaCuentaPptal()
    {
        return listaCuentaPptal;
    }

    /**
     * Asigna la lista listaCuentaPptal
     *
     * @param listaCuentaPptal
     * Variable a asignar en listaCuentaPptal
     */
    public void setListaCuentaPptal(RegistroDataModelImpl listaCuentaPptal)
    {
        this.listaCuentaPptal = listaCuentaPptal;
    }

    /**
     * Retorna la lista listaCuentaPptal
     *
     * @return listaCuentaPptal
     */
    public RegistroDataModelImpl getListaCuentaPptalE()
    {
        return listaCuentaPptalE;
    }

    /**
     * Asigna la lista listaCuentaPptal
     *
     * @param listaCuentaPptal
     * Variable a asignar en listaCuentaPptal
     */
    public void setListaCuentaPptalE(RegistroDataModelImpl listaCuentaPptalE)
    {
        this.listaCuentaPptalE = listaCuentaPptalE;
    }

    public boolean isPorcentajeBloqueado()
    {
        return porcentajeBloqueado;
    }

    public void setPorcentajeBloqueado(boolean porcentajeBloqueado)
    {
        this.porcentajeBloqueado = porcentajeBloqueado;
    }

    public String getMes()
    {
        return mes;
    }

    public void setMes(String mes)
    {
        this.mes = mes;
    }

    public String getMensaje()
    {
        return mensaje;
    }

    public void setMensaje(String mensaje)
    {
        this.mensaje = mensaje;
    }

    public String getControlChequera()
    {
        return controlChequera;
    }

    public void setControlChequera(String controlChequera)
    {
        this.controlChequera = controlChequera;
    }

    public String getValorGirarDialogo()
    {
        return valorGirarDialogo;
    }

    public void setValorGirarDialogo(String valorGirarDialogo)
    {
        this.valorGirarDialogo = valorGirarDialogo;
    }

    public String getOpcionMenu()
    {
        return opcionMenu;
    }

    public void setOpcionMenu(String opcionMenu)
    {
        this.opcionMenu = opcionMenu;
    }

    public boolean isCerrar()
    {
        return cerrar;
    }

    public void setCerrar(boolean cerrar)
    {
        this.cerrar = cerrar;
    }

    public boolean isManejaTercero()
    {
        return manejaTercero;
    }

    public void setManejaTercero(boolean manejaTercero)
    {
        this.manejaTercero = manejaTercero;
    }

    public boolean isManejaCentro()
    {
        return manejaCentro;
    }

    public void setManejaCentro(boolean manejaCentro)
    {
        this.manejaCentro = manejaCentro;
    }

    public boolean isManejaAuxiliar()
    {
        return manejaAuxiliar;
    }

    public void setManejaAuxiliar(boolean manejaAuxiliar)
    {
        this.manejaAuxiliar = manejaAuxiliar;
    }

    public boolean isManejaFuente()
    {
        return manejaFuente;
    }

    public void setManejaFuente(boolean manejaFuente)
    {
        this.manejaFuente = manejaFuente;
    }

    public boolean isBloqueaAuxiliar()
    {
        return bloqueaAuxiliar;
    }

    public void setBloqueaAuxiliar(boolean bloqueaAuxiliar)
    {
        this.bloqueaAuxiliar = bloqueaAuxiliar;
    }

    public String getTipoDistribuir()
    {
        return tipoDistribuir;
    }

    public void setTipoDistribuir(String tipoDistribuir)
    {
        this.tipoDistribuir = tipoDistribuir;
    }

    public String getValorDistribuir()
    {
        return valorDistribuir;
    }

    public void setValorDistribuir(String valorDistribuir)
    {
        this.valorDistribuir = valorDistribuir;
    }

    public boolean isDistribucionVisible()
    {
        return distribucionVisible;
    }

    public void setDistribucionVisible(boolean distribucionVisible)
    {
        this.distribucionVisible = distribucionVisible;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public boolean isConceptoExBloqueado()
    {
        return conceptoExBloqueado;
    }

    public int getIndiceRevelaciones()
    {
        return indiceRevelaciones;
    }

    public void setIndiceRevelaciones(int indiceRevelaciones)
    {
        this.indiceRevelaciones = indiceRevelaciones;
    }

    public void setConceptoExBloqueado(boolean conceptoExBloqueado)
    {
        this.conceptoExBloqueado = conceptoExBloqueado;
    }

    public boolean isDialogoConceptos()
    {
        return dialogoConceptos;
    }

    public void setDialogoConceptos(boolean dialogoConceptos)
    {
        this.dialogoConceptos = dialogoConceptos;
    }

    public boolean isDialogoComprobantePresupuestal()
    {
        return dialogoComprobantePresupuestal;
    }

    public void setDialogoComprobantePresupuestal(
        boolean dialogoComprobantePresupuestal)
    {
        this.dialogoComprobantePresupuestal = dialogoComprobantePresupuestal;
    }

    public boolean isEstadoComprobantePresupuestal()
    {
        return estadoComprobantePresupuestal;
    }

    public void setEstadoComprobantePresupuestal(
        boolean estadoComprobantePresupuestal)
    {
        this.estadoComprobantePresupuestal = estadoComprobantePresupuestal;
    }

    public boolean isPermiteModificarCuentaPptal()
    {
        return permiteModificarCuentaPptal;
    }

    public void setPermiteModificarCuentaPptal(
        boolean permiteModificarCuentaPptal)
    {
        this.permiteModificarCuentaPptal = permiteModificarCuentaPptal;
    }

    public String getCuentaPptalAux()
    {
        return cuentaPptalAux;
    }

    public void setCuentaPptalAux(String cuentaPptalAux)
    {
        this.cuentaPptalAux = cuentaPptalAux;
    }

    public boolean isBloqueaFlujoEfectivo()
    {
        return bloqueaFlujoEfectivo;
    }

    public void setBloqueaFlujoEfectivo(boolean bloqueaFlujoEfectivo)
    {
        this.bloqueaFlujoEfectivo = bloqueaFlujoEfectivo;
    }

    public RegistroDataModelImpl getListaCodigoNota()
    {
        return listaCodigoNota;
    }

    public void setListaCodigoNota(RegistroDataModelImpl listaCodigoNota)
    {
        this.listaCodigoNota = listaCodigoNota;
    }

    public RegistroDataModelImpl getListaCodigoNotaE()
    {
        return listaCodigoNotaE;
    }

    public void setListaCodigoNotaE(RegistroDataModelImpl listaCodigoNotaE)
    {
        this.listaCodigoNotaE = listaCodigoNotaE;
    }

    public boolean isBloqueacuentas()
    {
        return bloqueacuentas;
    }

    public void setBloqueacuentas(boolean bloqueacuentas)
    {
        this.bloqueacuentas = bloqueacuentas;
    }

    public List<Registro> getListaGruposTesoreria()
    {
        return listaGruposTesoreria;
    }

    public void setListaGruposTesoreria(List<Registro> listaGruposTesoreria)
    {
        this.listaGruposTesoreria = listaGruposTesoreria;
    }

    public List<Registro> getGruposasignados()
    {
        return gruposasignados;
    }

    public void setGruposasignados(List<Registro> gruposasignados)
    {
        this.gruposasignados = gruposasignados;
    }

    public Usuario getUsuario()
    {
        return usuario;
    }

    public void setUsuario(Usuario usuario)
    {
        this.usuario = usuario;
    }
    public RegistroDataModelImpl getListaConceptoCUDS() {
		return listaConceptoCUDS;
	}

	public void setListaConceptoCUDS(RegistroDataModelImpl listaConceptoCUDS) {
		this.listaConceptoCUDS = listaConceptoCUDS;
	}
	public RegistroDataModelImpl getListaConceptoCUDSE() {
		return listaConceptoCUDSE;
	}

	public void setListaConceptoCUDSE(RegistroDataModelImpl listaConceptoCUDSE) {
		this.listaConceptoCUDSE = listaConceptoCUDSE;
	}
   
	public boolean isBloqConceptoCUDS() {
		return bloqConceptoCUDS;
	}

	public void setBloqConceptoCUDS(boolean bloqConceptoCUDS) {
		this.bloqConceptoCUDS = bloqConceptoCUDS;
	}

	public boolean isBloqNotaAjusteDs() {
		return bloqNotaAjusteDs;
	}

	public void setBloqNotaAjusteDs(boolean bloqNotaAjusteDs) {
		this.bloqNotaAjusteDs = bloqNotaAjusteDs;
	}

	public boolean isSigecVisible() {
		return sigecVisible;
	}

	public void setSigecVisible(boolean sigecVisible) {
		this.sigecVisible = sigecVisible;
	}	
	
	public boolean isCremilVisible() {
		return cremilVisible;
	}

	public void setCremilVisible(boolean cremilVisible) {
		this.cremilVisible = cremilVisible;
	}

	public List<Registro> getListaprocesoJudicial() {
		return listaprocesoJudicial;
	}

	public void setListaprocesoJudicial(List<Registro> listaprocesoJudicial) {
		this.listaprocesoJudicial = listaprocesoJudicial;
	}

	public boolean isBloquearProceso() {
		return bloquearProceso;
	}

	public void setBloquearProceso(boolean bloquearProceso) {
		this.bloquearProceso = bloquearProceso;
	}
	
	public boolean isFuncionarioVisible() {
		return funcionarioVisible;
	}

	public void setFuncionarioVisible(boolean funcionarioVisible) {
		this.funcionarioVisible = funcionarioVisible;
	}

	public boolean isManejaPlanCompleto() {
		return manejaPlanCompleto;
	}

	public void setManejaPlanCompleto(boolean manejaPlanCompleto) {
		this.manejaPlanCompleto = manejaPlanCompleto;
	}
	
}
