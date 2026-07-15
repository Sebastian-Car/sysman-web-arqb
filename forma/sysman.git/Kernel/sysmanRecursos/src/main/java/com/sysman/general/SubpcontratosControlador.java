package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SubpcontratosControladorEnum;
import com.sysman.general.enums.SubpcontratosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
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
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodriguezr
 * @version 1, 29/12/2015
 * 
 * @author jlramirez
 * @version 2, 05/04/2017, proceso de Refactoring y modificaciones
 * segUn especificaciones de SONARLINT
 */
@ManagedBean
@ViewScoped
public class SubpcontratosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private static final String CRUBROPPTO = "RUBROPPTO";
    private static final String CTIPOMOV = "TIPOMOVIMIENTO";
    private static final String CVLRTOTALF = "VLRTOTALF";
    private List<Registro> listaNumeroPpto;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaCodigoUnspsc;
    private RegistroDataModelImpl listaDependencia;
    private String claseOrden;
    private String numeroOrden;
    private String tercero;
    private String valorFinal;
    private String dependencia;
    private boolean actualizaPlanDeCompras;
    private boolean valoresUnitarios;
    private boolean subPContratoPermiteAgregar;
    private boolean subPContratoPermiteEditar;
    private boolean subPContratoPermiteEliminar;
    private List<Registro> listaTipoPpto;
    private List<Registro> listaTipoRegistro;
    private RegistroDataModelImpl listaRegistro;
    private RegistroDataModelImpl listaRubroPpto;
    private RegistroDataModelImpl listaUnidad;
    private String tipoRegistro;
    private boolean cuatroPorMilVisible;
    private boolean cuatroPorMilLocked;
    private String manejaCuatroPorMil;
    private String ocultaSiESDIS;
    private boolean numeropptoVisible;
    private boolean rubropptoVisible;
    private boolean valorpptoVisible;
    private boolean registroVisible;
    private boolean tipoRegistroVisible;
    private boolean dependenciaVisible;
    private boolean etqNumeropptoVisible;
    private boolean etqRubropptoVisible;
    private boolean etqValorpptoVisible;
    private boolean etqRegistroVisible;
    private boolean etqTipoRegistroVisible;
    private boolean etqDependenciaVisible;
    private boolean aniopptoVisible;
    private boolean consecutivopptoVisible;
    private boolean etqTipopptoVisible;
    private boolean pideVencimiento;

    private String manejaAlertInsItems;
    private String controlaItems;
    private String controlaValorContVsItems;
    private String codigo;
    private boolean tipopptoVisible;
    private String valorPpto;
    private boolean porcDescGlobalLocked;
    private boolean porcIVAGlobalLocked;
    private String porcIVAGlobal;
    private String porcDescGlobal;
    private boolean muestraAfectar;
    private double codigoc;
    private double cantidadt;
    private Map<String, Object> ridPContrato;
    private String vigencia;
    private String titulo;
    private boolean aportantes;
    private String tipoCpte;
    private String numPpto;
    private String nombreElemento;
    private String impConsumo;
    private String valorTotalContrato;

    private static final String CAMPO_VLRTOTAL = "VLRTOTAL";
    private static final String CAMPO_ORDENDESUMINISTRO = "ORDENDESUMINISTRO";
    private static final String CAMPO_VALORPPTO = "VALORPPTO";
    private static final String CAMPO_VALORUNITARIO = "VALORUNITARIO";
    private static final String CAMPO_PORCIVA = "PORCIVA";
    private static final String CAMPO_ORDENDECOMPRA = "ORDENDECOMPRA";
    private static final String VARIBALE_CADENA = "#$cadena#$";
    private static final String CAMPO_PORCDESC = "PORCDESC";
    private static final String CAMPO_ITEM_AFECT = "ITEM_AFECT";
    private static final String CAMPO_ITEMORIGEN = "ITEMORIGEN";
    private static final String CAMPO_NUMEROPPTO = "NUMEROPPTO";
    private static final String CAMPO_TIPOPPTO = "TIPOPPTO";
    private static final String CAMPO_NOMBREELEM = "NOMBRELARGO";
    private static final String CAMPO_IMPCONSUMO = "IMPCONSUMO";
    private static final String CAMPO_VALOR_IMPCONSUMO = "VALORIMPCONSUMO";
    private static final String CAMPO_PORCENTAJE_AIU = "PORC_AIU";
    private static final String CAMPO_VALOR_AIU = "VALOR_AIU";
    private static final int TOP_BASE = 520;
    private static final int SALTO = 26;
    public static final int IDX_PORC_IVA = 2;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	private Map<String, Object> parametroswf;
	private String modulo;
	private boolean preciosUnitarios;
	private boolean aplicaAIU;
	private boolean visibleAIU;
	private BigDecimal sumaPorcentaje;
    public SubpcontratosControlador() {
        compania = SessionUtil.getCompania();
        try {
        	
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
        	modulo = SessionUtil.getModulo();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridPContrato = (Map<String, Object>) parametrosEntrada
                                .get("rowidPcontrato");
                vigencia = (String) parametrosEntrada.get("vigencia");
                titulo = (String) parametrosEntrada.get("titulo");
                claseOrden = (String) parametrosEntrada.get("claseF");
                numeroOrden = (String) parametrosEntrada.get("numeroOrden");
                tercero = (String) parametrosEntrada.get("tercero");
                valorFinal = (String) parametrosEntrada.get("valorFinal");
                dependencia = (String) parametrosEntrada.get("dependencia");
                actualizaPlanDeCompras = Boolean
                                .parseBoolean((String) parametrosEntrada
                                                .get("actualizaPlanDeCompras"));
                valoresUnitarios = Boolean
                                .parseBoolean((String) parametrosEntrada
                                                .get("valoresUnitarios"));
                subPContratoPermiteAgregar = Boolean
                                .parseBoolean((String) parametrosEntrada.get(
                                                "subPContratoPermiteAgregar"));
                subPContratoPermiteEditar = Boolean
                                .parseBoolean((String) parametrosEntrada.get(
                                                "subPContratoPermiteEditar"));
                subPContratoPermiteEliminar = Boolean
                                .parseBoolean((String) parametrosEntrada.get(
                                                "subPContratoPermiteEliminar"));
                porcDescGlobalLocked = Boolean
                                .parseBoolean((String) parametrosEntrada
                                                .get("porcDescGlobalLocked"));
                porcIVAGlobalLocked = Boolean
                                .parseBoolean((String) parametrosEntrada
                                                .get("porcIVAGlobalLocked"));
                porcIVAGlobal = (String) parametrosEntrada.get("porcIVAGlobal");
                porcDescGlobal = (String) parametrosEntrada
                                .get("porcDescGlobal");

                aportantes = Boolean.parseBoolean(
                                parametrosEntrada.get("convenio").toString());
                
                impConsumo = (String) parametrosEntrada.get("impConsumo");
                
                valorTotalContrato = (String) parametrosEntrada.get("txtValorTotal");
                
                preciosUnitarios = Boolean.parseBoolean((String)
                        parametrosEntrada.get("preciosUni"));
                sumaPorcentaje = (BigDecimal) parametrosEntrada.get("sumaPorc");
            }
            registro = new Registro(new HashMap<String, Object>());
            numeropptoVisible = true;
            rubropptoVisible = true;
            valorpptoVisible = true;
            tipopptoVisible = true;
            consecutivopptoVisible = true;
            aniopptoVisible = true;
            registroVisible = true;
            tipoRegistroVisible = true;
            dependenciaVisible = true;
            etqNumeropptoVisible = true;
            etqRubropptoVisible = true;
            etqTipopptoVisible = true;
            etqValorpptoVisible = true;
            etqRegistroVisible = true;
            etqTipoRegistroVisible = true;
            etqDependenciaVisible = true;
            pideVencimiento = true;
            numFormulario = GeneralCodigoFormaEnum.SUBPCONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException | NamingException ex) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.DORDENDECOMPRA;
        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void iniciarListas() {
        cargarListaElemento();
        cargarListaCodigoUnspsc();
        cargarListaDependencia();
        cargarListaTipoPpto();
        cargarListaTipoRegistro();
        cargarListaUnidad();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        parametrosListado.put("NUMEROORDEN", numeroOrden);
    }

    public void consecutivoCodigo() {
        try {
            String cod = String
                            .valueOf(ejbSysmanUtil.generarSiguienteConsecutivo(
                                            tabla,
                                            "COMPANIA     = ''"
                                                + compania + "'' "
                                                + " AND  CLASEORDEN  = ''"
                                                + claseOrden
                                                + "'' "
                                                + " AND ORDENDECOMPRA ="
                                                + numeroOrden,
                                            "CODIGO"));

            codigo = registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()) == null
                                ? cod
                                : registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName())
                                                .toString();
        }
        catch (SystemException ex) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void cargarListaNumeroPpto() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubpcontratosControladorEnum.PARAM1.getValue(), tipoCpte);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroOrden);
        try {
            listaNumeroPpto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpcontratosControladorUrlEnum.URL49126
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoPpto() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaTipoPpto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpcontratosControladorUrlEnum.URL11080
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTipoRegistro() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoRegistro = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpcontratosControladorUrlEnum.URL35064
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaRegistro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratosControladorUrlEnum.URL12639
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubpcontratosControladorEnum.PARAM0.getValue(),
                        tipoRegistro);
        param.put(SubpcontratosControladorEnum.PARAM11.getValue(),
                        numeroOrden);

        listaRegistro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    public void cargarListaElemento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratosControladorUrlEnum.URL13576
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaCodigoUnspsc() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratosControladorUrlEnum.URL13645
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoUnspsc = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOCUBS");
    }

    public void cargarListaDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratosControladorUrlEnum.URL14881
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaRubroPpto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpcontratosControladorUrlEnum.URL15501
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubpcontratosControladorEnum.PARAM1.getValue(), tipoCpte);
        param.put(SubpcontratosControladorEnum.PARAM2.getValue(),
                        numPpto);

        listaRubroPpto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CUENTA.getName());
    }
    
    /**
     * 
     * Carga la lista listaUnidad
     *
     */
    public void cargarListaUnidad(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        SubpcontratosControladorUrlEnum.URL10280
                        .getValue());
        listaUnidad = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(), null, true,
                "UNIDAD");
    }

    public void cambiarTipoRegistro() {
        // <CODIGO_DESARROLLADO>
        tipoRegistro = registro.getCampos().get("TIPO_REGISTRO") == null ? "RES"
            : registro.getCampos().get("TIPO_REGISTRO").toString();
        cargarListaRegistro();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTipoPpto() {
        // <CODIGO_DESARROLLADO>
        ocultaSiESDIS = getParametro("OCULTA CAMPOS SI EL TIPO ES DIS", "NO");
        if ("NO".equals(ocultaSiESDIS)) {
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CAMPO_TIPOPPTO)) {
                numeropptoVisible = false;
                rubropptoVisible = false;
                valorpptoVisible = false;
                consecutivopptoVisible = false;
                aniopptoVisible = false;
                tipopptoVisible = false;
                registroVisible = false;
                tipoRegistroVisible = false;
                dependenciaVisible = false;
                etqNumeropptoVisible = false;
                etqRubropptoVisible = false;
                etqTipopptoVisible = false;
                etqValorpptoVisible = false;
                etqRegistroVisible = false;
                etqTipoRegistroVisible = false;
                etqDependenciaVisible = false;
            }
            else {
                numeropptoVisible = true;
                rubropptoVisible = true;
                valorpptoVisible = true;
                tipopptoVisible = true;
                consecutivopptoVisible = true;
                aniopptoVisible = true;
                registroVisible = true;
                tipoRegistroVisible = true;
                dependenciaVisible = true;
                etqNumeropptoVisible = true;
                etqRubropptoVisible = true;
                etqTipopptoVisible = true;
                etqValorpptoVisible = true;
                etqRegistroVisible = true;
                etqTipoRegistroVisible = true;
                etqDependenciaVisible = true;
            }
        }
        tipoCpte = registro.getCampos().get(CAMPO_TIPOPPTO) == null ? " "
            : registro.getCampos().get(CAMPO_TIPOPPTO).toString();
        cargarListaNumeroPpto();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidad() {
        // <CODIGO_DESARROLLADO>
        controlaItems = getParametro("CONTROLA ITEMS DE PLAN DE COMPRAS", "NO");
        double nCantidad = getDblVal(GeneralParameterEnum.CANTIDAD.getName());

        if (nCantidad < 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1487"));
            registro.getCampos().put(
                            GeneralParameterEnum.CANTIDAD.getName(), 0);
        }
        else {

            String nCodigo = registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()) == null
                                ? "0"
                                : registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName())
                                                .toString();
            codigoc = Double.parseDouble(nCodigo);

            registro.getCampos().put("SALDOCANT", nCantidad);
            
            calcularTotal();

            if ("SI".equals(controlaItems) && (registro.getCampos()
                            .get(CAMPO_ORDENDESUMINISTRO) != null)) {
                cantidadt = getCantidadDt(nCantidad);
                BigDecimal cantidad = BigDecimal.valueOf(nCantidad);
                if (cantidadt < 0) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2141"));
                    registro.getCampos().put(
                                    GeneralParameterEnum.CANTIDAD.getName(), 0);
                    registro.getCampos().put(CAMPO_ITEM_AFECT, 0);

                }
                else if (cantidad.compareTo(BigDecimal.ZERO) != 0) {
                    return;
                }
                else {
                    muestraAfectar = true;
                }
            }

        }
        calcularValorAIU();
        // </CODIGO_DESARROLLADO>
    }

    private double getCantidadDt(double nuevacantidad) {

        double res = 0;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubpcontratosControladorEnum.PARAM4.getValue(),
                        nuevacantidad);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoc);
        param.put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(),
                        registro.getCampos()
                                        .get(CAMPO_ORDENDESUMINISTRO) == null
                                            ? " "
                                            : registro.getCampos()
                                                            .get(CAMPO_ORDENDESUMINISTRO)
                                                            .toString());
        Registro rs;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpcontratosControladorUrlEnum.URL35066
                                                                            .getValue())
                                            .getUrl(), param));
            if (rs != null) {
                res = getDblVal(rs, "CANTIDADT");
            }
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return res;

    }

    public void aceptarafectarReq() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.CANTIDAD.getName(), cantidadt);
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.CODIGO.getName(), codigoc);
            parametros.put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(),
                            registro.getCampos()
                                            .get(CAMPO_ORDENDESUMINISTRO) == null
                                                ? " "
                                                : registro.getCampos()
                                                                .get(CAMPO_ORDENDESUMINISTRO)
                                                                .toString());
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            Parameter parameter = new Parameter();

            parameter.setFields(parametros);
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubpcontratosControladorUrlEnum.URL46540
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(), parameter);
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(CAMPO_ITEM_AFECT, -1);
        muestraAfectar = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarafectarReq() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private void calcularTotal() {
        String redondearValorIvaOC = getParametro("REDONDEAR VALOR IVA EN O.C.",
                        "NO");
        String digitosRedondeoValorIVAOC = getParametro(
                        "DIGITOS REDONDEO VALOR IVA EN O.C.", "0");
        String redondearUnitarioIvaOC = getParametro(
                        "REDONDEAR UNITARIO CON IVA EN O.C.", "NO");
        String redondearValorTotalOC = getParametro(
                        "REDONDEAR VALOR TOTAL EN O.C.", "NO");
        String digitosRedondeoValorTotalOC = getParametro(
                        "DIGITOS REDONDEO VALOR TOTAL O.C.", "0");
        
        String redondeoValor = getParametro("REDONDEO VALOR","2");
        
        double dbltotal;
        double dblDescuento;
        double valorNeto = 0;
        double dblIva = 0;
        double dblValorImpConsumo = 0;
        double dblfogarin;
        double dblValorUni = getDblVal(CAMPO_VALORUNITARIO);
        //JM 19/03/2025
        if(Integer.parseInt(redondeoValor) == 0) {
        	dblValorUni =  SysmanFunciones.redondear((dblValorUni+0.01),0);
        	registro.getCampos().put(CAMPO_VALORUNITARIO, dblValorUni);
        }
        double dblValorCant = getDblVal(
                        GeneralParameterEnum.CANTIDAD.getName());
        dbltotal = dblValorUni * dblValorCant;
        valorNeto = dbltotal;
        // Me.SALDOCANT.Locked = True
        double dblPorcDesc = getDblVal(CAMPO_PORCDESC);
        dblDescuento = SysmanFunciones.redondear((dbltotal * dblPorcDesc) / 100,
                        2);
        int numDigitosRedondeoValorIVAO = Integer
                        .parseInt(digitosRedondeoValorIVAOC);
        double dblPorcIVA = getDblVal(CAMPO_PORCIVA);
        
        double dblImpConsumo = getDblVal(CAMPO_IMPCONSUMO);
        dblValorImpConsumo = SysmanFunciones.redondear((dblValorUni * dblImpConsumo / 100) * dblValorCant, 0);
        
        if ("NO".equals(redondearValorIvaOC)) {
            if (dblPorcIVA > 0.0) {
                dblIva = (dbltotal - dblDescuento) * (dblPorcIVA / 100);
            }
        }
        else {
            dblIva = (SysmanFunciones.redondear(
                            (dbltotal - dblDescuento)
                                * (1 + (dblPorcIVA / 100)),
                            numDigitosRedondeoValorIVAO)
                - dbltotal) + dblDescuento;
        }

        dbltotal = (dbltotal - dblDescuento) + dblIva + dblValorImpConsumo;
        int numDigRedondeoUnitarioIvaOc = Integer
                        .parseInt(digitosRedondeoValorIVAOC);
        double valorTotal;
        double dblVlrUniDI;
        if (dblValorCant > 0.0) {
            int numeroDigitos;
            if ("SI".equals(redondearUnitarioIvaOC)) {
                numeroDigitos = numDigRedondeoUnitarioIvaOc;
            }
            else {
                numeroDigitos = 2;
            }
            dblVlrUniDI = SysmanFunciones
                            .redondear(dbltotal / dblValorCant, numeroDigitos);
            if ("SI".equals(redondearValorTotalOC)) {
                numeroDigitos = Integer.parseInt(digitosRedondeoValorTotalOC);
            }
            else {
                numeroDigitos = 2;
            }
            
            if (Integer.parseInt(redondeoValor) > 0 ) {
            	valorTotal = SysmanFunciones.redondear(dblVlrUniDI * dblValorCant,
                            	numeroDigitos);
            }else {
            	
            	valorTotal =  (dblValorUni * dblValorCant) - SysmanFunciones.redondear((((dblValorUni * dblPorcDesc)/100) * dblValorCant),0)
            			+ SysmanFunciones.redondear((((dblValorUni*dblPorcIVA)/100) * dblValorCant),0) 
            			+dblValorImpConsumo; //JM totalizar iva, descuentos y luego sumar/restar al subtotal 
            }
        }
        else {
            dblVlrUniDI = 0.0;
            valorTotal = 0.0;
        }
        registro.getCampos().put("VALORUNITARIODI",
                        new BigDecimal(dblVlrUniDI));
        registro.getCampos().put(CAMPO_VLRTOTAL, new BigDecimal(valorTotal));
        registro.getCampos().put("VLRDESCUENTO", new BigDecimal(dblDescuento));
        registro.getCampos().put("VLRIVA", new BigDecimal(dblIva));
        registro.getCampos().put(CAMPO_VALOR_IMPCONSUMO, new BigDecimal(dblValorImpConsumo));
        if ("SI".equals(manejaCuatroPorMil)) {
            dblfogarin = (valorNeto * 4) / 1000; // Se modifica el
                                                 // valorTotal por
                                                 // valorNeto para que
                                                 // calcule el 4xmil
                                                 // libre de impuestos
            registro.getCampos().put("CUATROXMIL", dblfogarin);
        }
    }

    private void calcularTotalInverso() {
        double dblIva = 0;
        double dblTotalR;
        double dblDescuento = 0;
        double valorNeto = 0;
        double dblfogarin;

        String vlrTotal = registro.getCampos().get(CAMPO_VLRTOTAL) == null
            ? "0.0"
            : registro.getCampos().get(CAMPO_VLRTOTAL).toString();
        double dbltotal = Double.parseDouble(vlrTotal);
        valorNeto = dbltotal;
        double dblPorcIVA = getDblVal(CAMPO_PORCIVA);
        double dblresultado;

        if (dblPorcIVA > 0) {
            dblresultado = SysmanFunciones
                            .redondear(dbltotal / (1 + (dblPorcIVA / 100)), 0);
            dblIva = dbltotal - dblresultado;
        }
        else {
            dblresultado = dbltotal;
        }
        double dblPorcDesc = getDblVal(CAMPO_PORCDESC);

        if (dblPorcDesc > 0) {
            dblTotalR = SysmanFunciones.redondear(
                            dblresultado / (1 - (dblPorcDesc / 100)), 0);
            dblDescuento = dblTotalR - dblresultado;
        }
        
        
        registro.getCampos().put(CAMPO_VLRTOTAL, dbltotal);
        registro.getCampos().put("VLRDESCUENTO", dblDescuento);
        registro.getCampos().put("VLRIVA", dblIva);
        if ("SI".equals(manejaCuatroPorMil)) {
            dblfogarin = (valorNeto * 4) / 1000; // Se modifica el
                                                 // valorTotal por
                                                 // valorNeto para que
                                                 // calcule el 4xmil
                                                 // libre de impuestos
            registro.getCampos().put("CUATROXMIL", dblfogarin);
        }
    }
    /**
     * Calcula los valores de AIU para un ítem de orden de compra.
     *
     * Realiza:
     * - Subtotal = ValorUnitario × Cantidad × (1 - %Descuento)
     * - AIU = Subtotal × (%AIU / 100)
     * - IVA e Impuesto al Consumo calculados sobre el AIU
     * - Total = (ValorUnitario × Cantidad) - Descuento + AIU + IVA + ImpConsumo
     *
     * Actualiza en el registro: AIU, IVA, ImpConsumo y Valor Total.
     * 
     * Nota: Solo se ejecuta si visibleAIU = true y validar el tirgger AU_ORDENDECOMPRA_AIU
     */
    public void calcularValorAIU() {
        if(visibleAIU) {
            BigDecimal dblIva = BigDecimal.ZERO;
            BigDecimal vlrImpConsumo = BigDecimal.ZERO;
            BigDecimal vlrTotal = BigDecimal.ZERO;
            String digitosRedondeoValorIVAOC = getParametro(
                    "DIGITOS REDONDEO VALOR IVA EN O.C.", "0");
            int numDigitosRedondeoValorIVAO = Integer
                    .parseInt(digitosRedondeoValorIVAOC);
            BigDecimal vlrUnitario;
            BigDecimal cantidad;

            // LECTURA DE VALORES DEL REGISTRO
            vlrUnitario = new BigDecimal(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get(CAMPO_VALORUNITARIO), BigDecimal.ZERO)));
            cantidad = new BigDecimal(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()), BigDecimal.ZERO)));
            BigDecimal dblPorcIVA = new BigDecimal(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get(CAMPO_PORCIVA), BigDecimal.ZERO)));
            BigDecimal dblImpConsumo = new BigDecimal(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get(CAMPO_IMPCONSUMO), BigDecimal.ZERO)));
            BigDecimal vlrDescuento = new BigDecimal(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("VLRDESCUENTO"), BigDecimal.ZERO)));
            BigDecimal dblPorcDesc = new BigDecimal(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get(CAMPO_PORCDESC), BigDecimal.ZERO)));

            // FACTOR AIU: (ADMON + IMPREVISTOS + UTILIDAD) / 100
            BigDecimal factorAIU = sumaPorcentaje.divide(
                    BigDecimal.valueOf(100),
                    6,
                    RoundingMode.HALF_UP
            );

            // FACTOR DESCUENTO: (1 - (PORCDESC / 100))
            BigDecimal factorDescuento = BigDecimal.ONE.subtract(
                    dblPorcDesc.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
            );

            // SUBTOTAL: VALORUNITARIO * (1 - (PORCDESC / 100)) * CANTIDAD
            BigDecimal subtotal = vlrUnitario.multiply(factorDescuento).multiply(cantidad);

            // VALOR AIU: VALORUNITARIO * (1 - (PORCDESC / 100)) * CANTIDAD * (SUMAPORC / 100)
            BigDecimal valorAIU = subtotal.multiply(factorAIU);

            // IVA SOBRE EL VALOR AIU
            dblIva = SysmanFunciones.redondear(valorAIU.multiply(dblPorcIVA.divide(
                            BigDecimal.valueOf(100),
                            6,
                            RoundingMode.HALF_UP
                            )),
                    numDigitosRedondeoValorIVAO);

            // IMPUESTO AL CONSUMO SOBRE EL VALOR AIU = valorAIU × (IMPCONSUMO / 100)
            vlrImpConsumo = SysmanFunciones.redondear(valorAIU.multiply(dblImpConsumo.divide(
                    BigDecimal.valueOf(100),
                    6,
                    RoundingMode.HALF_UP
                    )),
                    numDigitosRedondeoValorIVAO);

         // VALOR TOTAL: (CANTIDAD * VALOR UNITARIO) - VALOR DESCUENTO + AIU + IVA + IMP CONSUMO
            BigDecimal base = cantidad.multiply(vlrUnitario);

            vlrTotal = base
                    .subtract(vlrDescuento)
                    .add(valorAIU)
                    .add(dblIva)
                    .add(vlrImpConsumo);

            // ALMACENA VALORES CALCULADOS EN EL REGISTRO
            registro.getCampos().put(CAMPO_PORCENTAJE_AIU, sumaPorcentaje);
            registro.getCampos().put(CAMPO_VALOR_AIU, valorAIU);
            registro.getCampos().put("VLRIVA", dblIva);
            registro.getCampos().put(CAMPO_VALOR_IMPCONSUMO, vlrImpConsumo);
            registro.getCampos().put(CAMPO_VLRTOTAL, vlrTotal);
        }
    }
    public void cambiarVlrTotal() {
        // <CODIGO_DESARROLLADO>
        calcularTotalInverso();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorUnitario() {
        // <CODIGO_DESARROLLADO>
        calcularTotal();
        calcularValorAIU();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorPpto() {
        // <CODIGO_DESARROLLADO>
        double dblppto = getDblVal(CAMPO_VALORPPTO);
        if (dblppto > Double.parseDouble(valorPpto)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2143")
                            .replace("#$valorPpto#$", valorPpto));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPorcIVA() {
        // <CODIGO_DESARROLLADO>
        calcularTotal();
        calcularValorAIU();
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarImpConsumo() {
        // <CODIGO_DESARROLLADO>
        calcularTotal();
        calcularValorAIU();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNumeroPpto() {
        // <CODIGO_DESARROLLADO>
        numPpto = registro.getCampos().get(CAMPO_NUMEROPPTO) == null ? " "
            : registro.getCampos().get(CAMPO_NUMEROPPTO).toString();
        registro.getCampos().put(CRUBROPPTO, "");
        cargarListaRubroPpto();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTexto46() {
        // <CODIGO_DESARROLLADO>
        calcularTotal();
        calcularValorAIU();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()));
        registro.getCampos().put(CAMPO_NOMBREELEM, registroAux.getCampos()
                        .get(CAMPO_NOMBREELEM).toString());

        double valor;
        BigDecimal valorUnitario = BigDecimal
                        .valueOf(getDblVal(CAMPO_VALORUNITARIO));
        if (valorUnitario.compareTo(BigDecimal.ZERO) == 0) {
            Object object = registroAux.getCampos().get("PRECIO");
            valor = object == null ? 0.0
                : Double.parseDouble(object.toString());
        }
        else {
            valor = valorUnitario.doubleValue();
        }
        //JM 19/03/2025
        String redondeoValor = getParametro("REDONDEO VALOR","2");
        if(redondeoValor.equals("0")) {
        	valor = SysmanFunciones.redondear((valor+0.01),0);
        }
        	
        
        registro.getCampos().put(CAMPO_VALORUNITARIO, valor);
        registro.getCampos().put("UNIDAD",
                registroAux.getCampos().get("UNIDAD"));
        registro.getCampos().put("REFERENCIA",
                registroAux.getCampos().get("REFERENCIA"));
        pideVencimiento = !(boolean) registroAux.getCampos().get("PIDE_VENCIMIENTO");
        registro.getCampos().put(CAMPO_PORCENTAJE_AIU, sumaPorcentaje);
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.CODIGOELEMENTO.getName(), registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()));
               
		try {
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													SubpcontratosControladorUrlEnum.URL158005.getValue())
											.getUrl(),
									param));
			if (rs.getCampos().get("MESESVIDAUTIL") != null) {

				registro.getCampos().put("NIIF_VIDA_UTIL", rs.getCampos().get("MESESVIDAUTIL"));
			} else {
				registro.getCampos().put("NIIF_VIDA_UTIL", 0);
			}
			
		} catch (SystemException e) {
			Logger.getLogger(SubpcontratosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}      
    }

    public void seleccionarFilaCodigoUnspsc(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGOCUBS",
                        registroAux.getCampos()
                                        .get("CODIGOCUBS"));
    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        String depend = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO
                                        .getName()) == null ? " "
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.CODIGO
                                                                            .getName())
                                                            .toString();
        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        depend);
    }

    public void seleccionarFilaRegistro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("REGISTRO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()));
    }

    public void seleccionarFilaRubroPpto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CRUBROPPTO,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CUENTA.getName()));
        String rubroppto = registro.getCampos().get(CRUBROPPTO) == null ? " "
            : registro.getCampos().get(CRUBROPPTO).toString();
        valorPpto = registroAux.getCampos().get(CAMPO_VALORPPTO) == null ? " "
            : registroAux.getCampos().get(CAMPO_VALORPPTO).toString();
        if (rubroppto != null) {
            registro.getCampos().put("CONSECUTIVOPPTO",
                            registroAux.getCampos().get("CONSECUTIVO"));
            registro.getCampos().put(CAMPO_VALORPPTO,
                            registroAux.getCampos().get(CAMPO_VALORPPTO));
            registro.getCampos().put("ANOPPTO",
                            registroAux.getCampos().get(GeneralParameterEnum.ANO
                                            .getName()));
            cargarListaElemento();
        }
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUnidad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUnidad(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("UNIDAD", registroAux.getCampos().get("UNIDAD"));
    }


    @Override
    public void abrirFormulario() {
        manejaCuatroPorMil = getParametro("MANEJA CUATRO POR MIL", "NO");
        aplicaAIU = "SI".equals(getParametro("APLICAR AIU EN ENTRADAS DE ALMACEN", "NO"));
        if ("SI".equals(manejaCuatroPorMil)) {
            cuatroPorMilVisible = true;
            cuatroPorMilLocked = true;
        }
        else {
            cuatroPorMilVisible = false;
        }
        visibleAIU = aplicaAIU && preciosUnitarios;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (ACCION_INSERTAR.equals(accion)) {
            registro.getCampos().put(CAMPO_ORDENDECOMPRA, numeroOrden);
            registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            dependencia);
            registro.getCampos().put(CAMPO_PORCIVA, porcIVAGlobal);
            registro.getCampos().put(CAMPO_PORCDESC, porcDescGlobal);
            impConsumo = impConsumo != null ? impConsumo : "0";
            registro.getCampos().put(CAMPO_IMPCONSUMO, impConsumo);
            registro.getCampos().put(CAMPO_PORCENTAJE_AIU, 0);
            registro.getCampos().put(CAMPO_VALOR_AIU, 0);
        }

        ocultaSiESDIS = getParametro("OCULTA CAMPOS SI EL TIPO ES DIS", "NO");

        if ("NO".equals(ocultaSiESDIS)) {

            numeropptoVisible = false;
            rubropptoVisible = false;
            valorpptoVisible = false;
            consecutivopptoVisible = false;
            aniopptoVisible = false;
            tipopptoVisible = false;
            registroVisible = false;
            tipoRegistroVisible = false;
            dependenciaVisible = false;
            etqNumeropptoVisible = false;
            etqRubropptoVisible = false;
            etqTipopptoVisible = false;
            etqValorpptoVisible = false;
            etqRegistroVisible = false;
            etqTipoRegistroVisible = false;
            etqDependenciaVisible = false;
        }
        else {
            numeropptoVisible = true;
            rubropptoVisible = true;
            valorpptoVisible = true;
            tipopptoVisible = true;
            consecutivopptoVisible = true;
            aniopptoVisible = true;
            registroVisible = true;
            tipoRegistroVisible = true;
            dependenciaVisible = true;
            etqNumeropptoVisible = true;
            etqRubropptoVisible = true;
            etqTipopptoVisible = true;
            etqValorpptoVisible = true;
            etqRegistroVisible = true;
            etqTipoRegistroVisible = true;
            etqDependenciaVisible = true;

        }
        
        pideVencimiento = !(boolean) SysmanFunciones.nvl(registro.getCampos().get("PIDE_VENCIMIENTO"),false);

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        consecutivoCodigo();
        registro.getCampos()
                        .remove(GeneralParameterEnum.CENTRODECOSTO.getName());
        // registro.getCampos().remove("CODIGOCUBS");
        registro.getCampos().remove("IMPREVISTOS");
        registro.getCampos().remove("PAGOTERCEROS");
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), codigo);
        registro.getCampos().put(CAMPO_ORDENDECOMPRA, numeroOrden);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        controlaItems = getParametro("CONTROLA ITEMS DE PLAN DE COMPRAS", "NO");
        String manejaServicios = getParametro("MANEJA SERVICIOS", "NO");
        manejaAlertInsItems = getParametro("MANEJA ALERTA INSERTANDO ITEMS",
                        "NO");
        if (tieneMovimientoAlmacen()) {
            return false;
        }
        double dblValPpto = getDblVal(CAMPO_VALORPPTO);
        double dblValItemAdic = getDblVal(CAMPO_VLRTOTAL);
        if ("SI".equals(manejaAlertInsItems) && dblValItemAdic > dblValPpto) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2086"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(dependencia)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2090"));
            return false;
        }
        definirNumeroPpto();
        if ("ODT".equals(claseOrden) && "SI".equals(manejaServicios)) {
            registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(),
                            "9999999999999999");
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private void definirNumeroPpto() {
        int numeroPpto = getIntVal(CAMPO_NUMEROPPTO);
        if (numeroPpto == 0) {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
            param.put(SubpcontratosControladorEnum.PARAM3.getValue(),
                            numeroOrden);
            Registro regNumeroPpto;
            try {
                regNumeroPpto = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubpcontratosControladorUrlEnum.URL45434
                                                                                .getValue())
                                                .getUrl(), param));
                registro.getCampos().put(CAMPO_NUMEROPPTO,
                                regNumeroPpto == null ? "0"
                                    : regNumeroPpto.getCampos().get("PRIMERO"));
            }
            catch (SystemException e) {
                Logger.getLogger(SubpcontratosControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    /**
     * Metodo que verifica que no tenga movimiento en almacen
     * 
     * @return
     */
    private boolean tieneMovimientoAlmacen() {
        boolean res = false;
        StringBuilder cadena = new StringBuilder("");
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        param.put(SubpcontratosControladorEnum.PARAM3.getValue(), numeroOrden);
        param.put(GeneralParameterEnum.TERCERO.getName(), tercero);
        List<Registro> rs;
        try {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpcontratosControladorUrlEnum.URL35065
                                                                            .getValue())
                                            .getUrl(), param));
            if (!rs.isEmpty()) {
                for (Registro reg : rs) {
                    String numeromovimiento = reg.getCampos()
                                    .get(GeneralParameterEnum.NUMERO
                                                    .getName()) == null
                                                        ? " "
                                                        : reg.getCampos()
                                                                        .get(GeneralParameterEnum.NUMERO
                                                                                        .getName())
                                                                        .toString();
                    String tipoMovimiento = reg.getCampos()
                                    .get(CTIPOMOV) == null ? " "
                                        : reg.getCampos().get(CTIPOMOV)
                                                        .toString();
                    cadena.append("-->>" + tipoMovimiento + " "
                        + numeromovimiento
                        + " ");
                }
                String msg = idioma.getString("TB_TB2144");
                msg = msg.replace(VARIBALE_CADENA, cadena);
                JsfUtil.agregarMensajeError(msg);
                res = true;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return res;

    }

    /**
     * Trae el valor de un campo del registro de tipo entero. En caso
     * de nulo trae el valor en cero.
     * 
     * @param nombreCampo
     * Nombre del campo en el registro.
     * @return Valor del campo numerico como <code>int</code>
     */
    private int getIntVal(String nombreCampo) {
        Object object = registro.getCampos().get(nombreCampo);
        return object == null ? 0 : Integer.parseInt(object.toString());
    }

    /**
     * Trae el valor de un campo del registro de tipo decimal. En caso
     * de nulo trae el valor en cero.
     * 
     * @param nombreCampo
     * Nombre del campo en el registro.
     * @return Valor del campo numerico como <code>double</code>
     */
    private double getDblVal(String nombreCampo) {
        return getDblVal(registro, nombreCampo);
    }

    /**
     * Trae el valor de un campo del registro de tipo decimal. En caso
     * de nulo trae el valor en cero.
     * 
     * @param unRegistro
     * El objeto Registro que se va a evaluar.
     * @param nombreCampo
     * Nombre del campo en el registro.
     * @return Valor del campo numerico como <code>double</code>
     */
    private double getDblVal(Registro unRegistro, String nombreCampo) {
        Object object = unRegistro.getCampos().get(nombreCampo);
        return object == null ? 0.0 : Double.parseDouble(object.toString());
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        actualizarDespues();
        // </CODIGO_DESARROLLADO>
        return true;

    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // registro.getCampos().remove("CODIGOCUBS");
        registro.getCampos().remove(CAMPO_NOMBREELEM);
        registro.getCampos().remove("VALORCONTRATO");
        registro.getCampos().remove("VALOR");
        registro.getCampos().remove("PIDE_VENCIMIENTO");

        if (registro.getCampos().get("ITEM_AFECT") == null) {
            registro.getCampos().put("ITEM_AFECT", 0);
        }
        manejaAlertInsItems = getParametro("MANEJA ALERTA INSERTANDO ITEMS",
                        "NO");
        String manejaValorMaxODC = getParametro("MANEJA VALOR MAXIMO PARA ODC",
                        "NO");
        double dblValPpto;
        double dblValItemAdic;
        boolean rta = true;

        if (!valoresUnitarios) {

            StringBuilder cadena = new StringBuilder();
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
            param.put(SubpcontratosControladorEnum.PARAM3.getValue(),
                            numeroOrden);
            param.put(GeneralParameterEnum.TERCERO.getName(), tercero);
            List<Registro> rs;
            try {
                rs = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubpcontratosControladorUrlEnum.URL35065
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
                if (!rs.isEmpty()) {
                    cargarMovimiento(cadena, rs);
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2144")
                                                    .replace(VARIBALE_CADENA,
                                                                    cadena
                                                                                    .toString()));
                    rta = false;
                }
            }
            catch (SystemException e) {
                Logger.getLogger(SubpcontratosControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        dblValPpto = getDblVal(CAMPO_VALORPPTO);
        dblValItemAdic = registro.getCampos().get(CAMPO_VLRTOTAL) == null ? 0.0
            : Double.parseDouble(
                            registro.getCampos().get(CAMPO_VLRTOTAL)
                                            .toString());
        if ("SI".equals(manejaAlertInsItems) && dblValItemAdic > dblValPpto) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2086"));
        }
        if ("SI".equals(manejaValorMaxODC) && "ODC".equals(claseOrden)) {
            validarValorOrden();
        }
        registro.getCampos();
        
        reemplazarComillas(registro.getCampos(), "MARCA");
        reemplazarComillas(registro.getCampos(), "ESPECIFICACION");
        
        return rta;
        // </CODIGO_DESARROLLADO>
    }

    private void reemplazarComillas(Map<String, Object> campos, String nombreCampo) {
        String valor = SysmanFunciones.nvlStr(SysmanFunciones.toString(campos.get(nombreCampo)), "");
        if (valor.contains("'")) {
            valor = valor.replace("'", "|");
            campos.put(nombreCampo, valor);
        }
    }
    
    public void cargarMovimiento(StringBuilder cadena, List<Registro> rs) {
        String numeromovimiento;
        String tipoMovimiento;
        for (int i = 0; i < rs.size(); i++) {
            numeromovimiento = rs.get(i).getCampos()
                            .get(GeneralParameterEnum.NUMERO
                                            .getName()) == null
                                                ? " "
                                                : rs.get(i).getCampos()
                                                                .get(GeneralParameterEnum.NUMERO
                                                                                .getName())
                                                                .toString();
            tipoMovimiento = rs.get(i).getCampos()
                            .get(CTIPOMOV) == null ? " "
                                : rs.get(i).getCampos()
                                                .get(CTIPOMOV)
                                                .toString();
            cadena.append("-->>" + tipoMovimiento + " "
                + numeromovimiento + " ");
        }
    }

    private void validarValorOrden() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubpcontratosControladorEnum.PARAM3.getValue(), numeroOrden);
        Registro rs;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubpcontratosControladorUrlEnum.URL54274
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            double dblVlrMax = 0.0;
            if (!rs.getCampos().isEmpty()) {
                String totalVlrTotal = rs.getCampos()
                                .get("TOTVLRTOTAL") == null ? "0.0"
                                    : rs.getCampos()
                                                    .get("TOTVLRTOTAL")
                                                    .toString();
                double dblTotalVlrTotal = Double.parseDouble(totalVlrTotal);
                String vlrTotal = registro.getCampos()
                                .get(CAMPO_VLRTOTAL) == null ? "0.0"
                                    : registro.getCampos()
                                                    .get(CAMPO_VLRTOTAL)
                                                    .toString();
                double dblVlrTotal = validarConversion(vlrTotal);
                dblVlrMax = dblVlrMax + dblTotalVlrTotal + dblVlrTotal;
                String valorMaximoODC = getParametro(
                                "VALOR MAXIMO PARA LAS ORDENES DE COMPRA",
                                "0.0");

                double dblvalorMaximoODC = Double.parseDouble(valorMaximoODC);
                if (dblVlrMax > dblvalorMaximoODC) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2146"));
                }
            }
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public double validarConversion(String valorEntrada) {
        double valor = 0;
        try {
            valor = Double.parseDouble(valorEntrada);
        }
        catch (Exception e) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2137"));
        }
        return valor;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        controlaValorContVsItems = getParametro(
                        "CONTROLA VALOR DEL CONTRATO VS ITEMS", "NO");
        boolean rta = true;
        if ("m".equals(accion)) {
            String ordenDeSuministro = registro.getCampos()
                            .get(CAMPO_ORDENDESUMINISTRO) == null ? " "
                                : registro.getCampos()
                                                .get(CAMPO_ORDENDESUMINISTRO)
                                                .toString();
            String itemOrigen = registro.getCampos()
                            .get(CAMPO_ITEMORIGEN) == null ? " "
                                : registro.getCampos().get(CAMPO_ITEMORIGEN)
                                                .toString();
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(),
                            ordenDeSuministro);
            param.put(GeneralParameterEnum.CODIGO.getName(), itemOrigen);
            Registro rs;
            try {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubpcontratosControladorUrlEnum.URL57030
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
                if (rs != null) {

                    actualizarDetalles(ordenDeSuministro,
                                    itemOrigen);
                }
                registro.getCampos().put("VALORCONTRATO",
                                valorFinal == null ? "0" : valorFinal);

            }
            catch (SystemException ex) {
                Logger.getLogger(SubpcontratosControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
            Map<String, Object> param2 = new TreeMap<>();
            param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param2.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
            param2.put(SubpcontratosControladorEnum.PARAM3.getValue(),
                            numeroOrden);
            Registro rs1;
            try {
                rs1 = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubpcontratosControladorUrlEnum.URL44286
                                                                                                .getValue())
                                                                .getUrl(),
                                                param2));
                String vlrTotalF;
                double valoritems;
                if (rs1 != null) {
                    vlrTotalF = rs1.getCampos().get(CVLRTOTALF) == null ? "0.0"
                        : rs1.getCampos().get(CVLRTOTALF).toString();
                    valoritems = Double.parseDouble(vlrTotalF);
                }
                else {
                    valoritems = 0.0;
                }
                actualizarDetalles2(valoritems);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            // </CODIGO_DESARROLLADO>
        }
        else {
            Map<String, Object> param2 = new TreeMap<>();
            param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param2.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
            param2.put(SubpcontratosControladorEnum.PARAM3.getValue(),
                            numeroOrden);
            Registro rs1;
            try {
                rs1 = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubpcontratosControladorUrlEnum.URL44286
                                                                                                .getValue())
                                                                .getUrl(),
                                                param2));

                String vlrTotalF;
                double valoritems;
                if (rs1 != null) {
                    vlrTotalF = rs1.getCampos().get(CVLRTOTALF) == null ? "0.0"
                        : rs1.getCampos().get(CVLRTOTALF).toString();
                    valoritems = Double.parseDouble(vlrTotalF);
                }
                else {
                    valoritems = 0.0;
                }
                actualizarDetalles2(valoritems);
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        if (registro.getLlave() != null && !registro.getLlave().isEmpty()) {
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        }

        return rta;
    }

    public void actualizarDetalles2(double valoritems) {
        try {
            if ("SI".equals(controlaValorContVsItems)
                && valoritems > Double.parseDouble(valorTotalContrato)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2145"));
                /*
                 * Si el Valor Total de los Items Excede El Valor
                 * Total del Contrato se deja en cero el valor
                 * unitario y cantidad
                 */
                Map<String, Object> parametros = new HashMap<>();

                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(GeneralParameterEnum.VALOR.getName(), 0);
                parametros.put(GeneralParameterEnum.CANTIDAD.getName(), 0);
                parametros.put(SubpcontratosControladorEnum.PARAM6.getValue(),
                                0);
                parametros.put(SubpcontratosControladorEnum.PARAM7.getValue(),
                                0);
                parametros.put(SubpcontratosControladorEnum.PARAM8.getValue(),
                                0);
                parametros.put(SubpcontratosControladorEnum.PARAM10.getValue(),
                                0);
                parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());

                parametros.put(GeneralParameterEnum.DATE_MODIFIED
                                .getName(),
                                new Date());
                parametros.put(GeneralParameterEnum.NUMERO.getName(),
                                numeroOrden);
                parametros.put(GeneralParameterEnum.CLASEORDEN.getName(),
                                claseOrden);
                parametros.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()) == null
                                                                    ? " "
                                                                    : registro.getCampos()
                                                                                    .get(GeneralParameterEnum.CODIGO
                                                                                                    .getName())
                                                                                    .toString());

                Parameter parameter = new Parameter();

                parameter.setFields(parametros);
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubpcontratosControladorUrlEnum.URL15502
                                                                .getValue());
                if (requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(), parameter) > 0) {
                    registro.getCampos().put(
                                    GeneralParameterEnum.VALOR.getName(), 0);
                    registro.getCampos().put(
                                    GeneralParameterEnum.CANTIDAD.getName(), 0);
                    registro.getCampos().put(SubpcontratosControladorEnum.PARAM6
                                    .getValue(), 0);
                    registro.getCampos().put(SubpcontratosControladorEnum.PARAM7
                                    .getValue(), 0);
                    registro.getCampos().put(SubpcontratosControladorEnum.PARAM8
                                    .getValue(), 0);
                    registro.getCampos()
                                    .put(SubpcontratosControladorEnum.PARAM10
                                                    .getValue(), 0);

                }
                ;

                codigo = registro.getCampos()
                                .get(GeneralParameterEnum.CODIGO
                                                .getName()) == null ? " "
                                                    : registro.getCampos()
                                                                    .get(GeneralParameterEnum.CODIGO
                                                                                    .getName())
                                                                    .toString();
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2147")
                                .replace("#$codigo#$", codigo));

            }
        }
        catch (SystemException ex) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void actualizarDetalles(String ordenDeSuministro,
        String itemOrigen) {
        /*
         * Si tiene cantidades por entregar no es vacio el detalle ni
         * el header
         */
        int saldo = 0;
        int filas;
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(SubpcontratosControladorEnum.PARAM5
                            .getValue(),
                            0);
            parametros.put(GeneralParameterEnum.CANTIDAD.getName(),
                            saldo);
            parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametros.put(GeneralParameterEnum.CODIGO.getName(),
                            itemOrigen);
            parametros.put(GeneralParameterEnum.NUMERO.getName(),
                            ordenDeSuministro);
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            Parameter parameter = new Parameter();

            parameter.setFields(parametros);
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubpcontratosControladorUrlEnum.URL51998
                                                            .getValue());
            filas = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(), parameter);

            if (filas > 0) {
                Map<String, Object> parametros2 = new HashMap<>();
                parametros2.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros2.put(GeneralParameterEnum.MODIFIED_BY
                                .getName(),
                                SessionUtil.getUser().getCodigo());
                parametros2.put(GeneralParameterEnum.DATE_MODIFIED
                                .getName(),
                                new Date());
                parametros2.put(GeneralParameterEnum.VALOR.getName(),
                                0);
                parametros2.put(GeneralParameterEnum.ORDEN.getName(),
                                ordenDeSuministro);

                parameter = new Parameter();

                parameter.setFields(parametros2);
                urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubpcontratosControladorUrlEnum.URL39816
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(), parameter);
            }
            else {
                /*
                 * Si no tiene cantidades por entregar puede ser vacio
                 * el detalle, pero verifica si toda la orden de
                 * suministro ha sido entregada
                 */
                Map<String, Object> parametros2 = new HashMap<>();

                parametros2.put(SubpcontratosControladorEnum.PARAM5
                                .getValue(),
                                -1);
                parametros2.put(GeneralParameterEnum.ORDEN.getName(),
                                ordenDeSuministro);
                parametros2.put(GeneralParameterEnum.CANTIDAD.getName(),
                                saldo);
                parametros2.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros2.put(GeneralParameterEnum.CODIGO.getName(),
                                itemOrigen);
                parametros2.put(GeneralParameterEnum.MODIFIED_BY
                                .getName(),
                                SessionUtil.getUser().getCodigo());
                parametros2.put(GeneralParameterEnum.DATE_MODIFIED
                                .getName(),
                                new Date());

                parameter = new Parameter();

                parameter.setFields(parametros2);
                urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubpcontratosControladorUrlEnum.URL19658
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(), parameter);

                Map<String, Object> parametros3 = new HashMap<>();

                parametros3.put(SubpcontratosControladorEnum.PARAM5
                                .getValue(),
                                -1);
                parametros3.put(GeneralParameterEnum.CANTIDAD.getName(),
                                saldo);
                parametros3.put(GeneralParameterEnum.MODIFIED_BY
                                .getName(),
                                SessionUtil.getUser().getCodigo());
                parametros3.put(GeneralParameterEnum.DATE_MODIFIED
                                .getName(),
                                new Date());
                parametros3.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros3.put(GeneralParameterEnum.NUMERO.getName(),
                                ordenDeSuministro);
                parametros3.put(GeneralParameterEnum.CODIGO.getName(),
                                itemOrigen);

                parameter = new Parameter();

                parameter.setFields(parametros3);
                urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubpcontratosControladorUrlEnum.URL46338
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(), parameter);
                actualizarDetalleOrdenSuministro(ordenDeSuministro,
                                saldo,
                                filas);
            }
        }
        catch (SystemException | IllegalAccessException
                        | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    private void actualizarDetalleOrdenSuministro(String ordenDeSuministro,
        int saldo, int filas)
                    throws IllegalAccessException, InstantiationException,
                    ClassNotFoundException, SQLException, NamingException {
        if (filas > 0) {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(),
                            ordenDeSuministro);
            Registro rs1;
            try {
                rs1 = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                SubpcontratosControladorUrlEnum.URL15503
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (rs1 != null) {
                    Map<String, Object> parametros = new HashMap<>();

                    parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    parametros.put(SubpcontratosControladorEnum.PARAM5
                                    .getValue(),
                                    -1);
                    parametros.put(GeneralParameterEnum.CANTIDAD.getName(),
                                    saldo);
                    parametros.put(GeneralParameterEnum.NUMERO.getName(),
                                    registro.getCampos()
                                                    .get(CAMPO_ORDENDESUMINISTRO) == null
                                                        ? " "
                                                        : registro.getCampos()
                                                                        .get(CAMPO_ORDENDESUMINISTRO)
                                                                        .toString());
                    parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());
                    parametros.put(GeneralParameterEnum.DATE_MODIFIED
                                    .getName(),
                                    new Date());

                    Parameter parameter = new Parameter();

                    parameter.setFields(parametros);
                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    SubpcontratosControladorUrlEnum.URL15504
                                                                    .getValue());
                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(), parameter);
                }
            }
            catch (SystemException e) {
                Logger.getLogger(SubpcontratosControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        if (!evaluarValores()) {
            return false;
        }
        // Desea eliminar un item creado por una adicion
        codigo = registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null
                            ? " "
                            : registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
        param.put(SubpcontratosControladorEnum.PARAM3.getValue(), numeroOrden);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        if (!verificar(param)) {
            return false;
        }
        // Desea elimnar un item modificado por una adicion
        try {
            List<Registro> rsList = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubpcontratosControladorUrlEnum.URL15506
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            if (!llenarCadena(rsList)) {
                return false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        double dblCodigoc = Double.parseDouble(codigo);
        double ordencc = getDblVal(CAMPO_ORDENDESUMINISTRO);
        double cantidadlibre = getDblVal(
                        GeneralParameterEnum.CANTIDAD.getName());

        actualizarCantidadTT(dblCodigoc, ordencc, cantidadlibre);
        registro = new Registro();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public boolean verificar(Map<String, Object> param) {
        Registro rs;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubpcontratosControladorUrlEnum.URL15505
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rs != null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2151")
                                .replace("#$tipo#$",
                                                rs.getCampos()
                                                                .get("TIPOO") == null
                                                                    ? " "
                                                                    : rs.getCampos()
                                                                                    .get("TIPOO")
                                                                                    .toString())
                                .replace("#$numero#$",
                                                rs.getCampos()
                                                                .get("NUMEROO") == null
                                                                    ? " "
                                                                    : rs.getCampos()
                                                                                    .get("NUMEROO")
                                                                                    .toString())
                                .replace("#$item#$", rs.getCampos()
                                                .get("ITEMO") == null ? " "
                                                    : rs.getCampos()
                                                                    .get("ITEMO")
                                                                    .toString()));
                return false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    public boolean evaluarValores() {
        if (actualizaPlanDeCompras) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2148"));
            return false;
        }
        double valorCantidad = getDblVal(
                        GeneralParameterEnum.CANTIDAD.getName());
        BigDecimal cantidad = BigDecimal.valueOf(valorCantidad);
        if (cantidad.compareTo(BigDecimal.ZERO) != 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2149"));
            return false;
        }
        return true;
    }

    public boolean llenarCadena(List<Registro> rsList) {
        StringBuilder cadena = new StringBuilder(" ");
        if (!rsList.isEmpty()) {
            for (int i = 0; i < rsList.size(); i++) {
                cadena.append(",")
                                .append(rsList.get(i).getCampos()
                                                .get(GeneralParameterEnum.CLASEORDEN
                                                                .getName()) == null
                                                                    ? " "
                                                                    : rsList.get(i).getCampos()
                                                                                    .get(GeneralParameterEnum.CLASEORDEN
                                                                                                    .getName())
                                                                                    .toString())
                                .append(" - ")
                                .append(rsList.get(i).getCampos()
                                                .get(CAMPO_ORDENDECOMPRA) == null
                                                    ? " "
                                                    : rsList.get(i).getCampos()
                                                                    .get(CAMPO_ORDENDECOMPRA)
                                                                    .toString())
                                .append(" Item - ")
                                .append(rsList.get(i).getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()) == null
                                                                    ? " "
                                                                    : rsList.get(i).getCampos()
                                                                                    .get(GeneralParameterEnum.CODIGO
                                                                                                    .getName())
                                                                                    .toString());
            }
            cadena.append(cadena.toString().substring(2));
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2153")
                            .replace(VARIBALE_CADENA, cadena.toString()));
            return false;
        }
        return true;
    }

    private void actualizarCantidadTT(double dblCodigoc, double ordencc,
        double cantidadlibre) {
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), dblCodigoc);
        param.put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(), ordencc);
        Registro rs;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            SubpcontratosControladorUrlEnum.URL15507
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rs != null) {
                boolean itemAfect = (boolean) registro.getCampos()
                                .get(CAMPO_ITEM_AFECT);
                if (itemAfect) {

                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put(GeneralParameterEnum.CANTIDAD.getName(),
                                    registro.getCampos().get("CANTIDADT")
                                        + "  +  " + cantidadlibre + " ");
                    parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    parametros.put(GeneralParameterEnum.CODIGO.getName(),
                                    dblCodigoc);
                    parametros.put(GeneralParameterEnum.ORDENDESUMINISTRO
                                    .getName(),
                                    ordencc);
                    parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());
                    parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                    new Date());
                    Parameter parameter = new Parameter();

                    parameter.setFields(parametros);
                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    SubpcontratosControladorUrlEnum.URL46540
                                                                    .getValue());
                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(), parameter);

                }
            }
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public String topCampo(int indiceLogico) {
        int top = TOP_BASE;
        if (!visibleAIU && indiceLogico >= IDX_PORC_IVA) {
            indiceLogico -= 2;
        }
        return (top + indiceLogico * SALTO) + "px";
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public List<Registro> getListaNumeroPpto() {
        return listaNumeroPpto;
    }

    public void setListaNumeroPpto(List<Registro> listaNumeroPpto) {
        this.listaNumeroPpto = listaNumeroPpto;
    }

    public List<Registro> getListaTipoPpto() {
        return listaTipoPpto;
    }

    public void setListaTipoPpto(List<Registro> listaTipoPpto) {
        this.listaTipoPpto = listaTipoPpto;
    }

    public List<Registro> getListaTipoRegistro() {
        return listaTipoRegistro;
    }

    public void setListaTipoRegistro(List<Registro> listaTipoRegistro) {
        this.listaTipoRegistro = listaTipoRegistro;
    }

    public boolean isCuatroPorMilVisible() {
        return cuatroPorMilVisible;
    }

    public void setCuatroPorMilVisible(boolean cuatroPorMilVisible) {
        this.cuatroPorMilVisible = cuatroPorMilVisible;
    }

    public boolean isCuatroPorMilLocked() {
        return cuatroPorMilLocked;
    }

    public void setCuatroPorMilLocked(boolean cuatroPorMilLocked) {
        this.cuatroPorMilLocked = cuatroPorMilLocked;
    }

    public RegistroDataModelImpl getListaRegistro() {
        return listaRegistro;
    }

    public void setListaRegistro(RegistroDataModelImpl listaRegistro) {
        this.listaRegistro = listaRegistro;
    }

    public RegistroDataModelImpl getListaElemento() {
        return listaElemento;
    }

    public void setListaElemento(RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    /**
     * @return the listaCodigoUnspsc
     */
    public RegistroDataModelImpl getListaCodigoUnspsc() {
        return listaCodigoUnspsc;
    }

    /**
     * @param listaCodigoUnspsc
     * the listaCodigoUnspsc to set
     */
    public void setListaCodigoUnspsc(RegistroDataModelImpl listaCodigoUnspsc) {
        this.listaCodigoUnspsc = listaCodigoUnspsc;
    }

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public RegistroDataModelImpl getListaRubroPpto() {
        return listaRubroPpto;
    }

    public void setListaRubroPpto(RegistroDataModelImpl listaRubroPpto) {
        this.listaRubroPpto = listaRubroPpto;
    }

    public String getClaseOrden() {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden) {
        this.claseOrden = claseOrden;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public String getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(String valorFinal) {
        this.valorFinal = valorFinal;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public boolean isActualizaPlanDeCompras() {
        return actualizaPlanDeCompras;
    }

    public void setActualizaPlanDeCompras(boolean actualizaPlanDeCompras) {
        this.actualizaPlanDeCompras = actualizaPlanDeCompras;
    }

    public boolean isValoresUnitarios() {
        return valoresUnitarios;
    }

    public void setValoresUnitarios(boolean valoresUnitarios) {
        this.valoresUnitarios = valoresUnitarios;
    }

    public boolean isSubPContratoPermiteAgregar() {
        return subPContratoPermiteAgregar;
    }

    public void setSubPContratoPermiteAgregar(
        boolean subPContratoPermiteAgregar) {
        this.subPContratoPermiteAgregar = subPContratoPermiteAgregar;
    }

    public boolean isSubPContratoPermiteEditar() {
        return subPContratoPermiteEditar;
    }

    public void setSubPContratoPermiteEditar(
        boolean subPContratoPermiteEditar) {
        this.subPContratoPermiteEditar = subPContratoPermiteEditar;
    }

    public boolean isSubPContratoPermiteEliminar() {
        return subPContratoPermiteEliminar;
    }

    public void setSubPContratoPermiteEliminar(
        boolean subPContratoPermiteEliminar) {
        this.subPContratoPermiteEliminar = subPContratoPermiteEliminar;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public boolean isNumeropptoVisible() {
        return numeropptoVisible;
    }

    public void setNumeropptoVisible(boolean numeropptoVisible) {
        this.numeropptoVisible = numeropptoVisible;
    }

    public boolean isRubropptoVisible() {
        return rubropptoVisible;
    }

    public void setRubropptoVisible(boolean rubropptoVisible) {
        this.rubropptoVisible = rubropptoVisible;
    }

    public boolean isValorpptoVisible() {
        return valorpptoVisible;
    }

    public void setValorpptoVisible(boolean valorpptoVisible) {
        this.valorpptoVisible = valorpptoVisible;
    }

    public boolean isRegistroVisible() {
        return registroVisible;
    }

    public void setRegistroVisible(boolean registroVisible) {
        this.registroVisible = registroVisible;
    }

    public boolean isTipoRegistroVisible() {
        return tipoRegistroVisible;
    }

    public void setTipoRegistroVisible(boolean tipoRegistroVisible) {
        this.tipoRegistroVisible = tipoRegistroVisible;
    }

    public boolean isDependenciaVisible() {
        return dependenciaVisible;
    }

    public void setDependenciaVisible(boolean dependenciaVisible) {
        this.dependenciaVisible = dependenciaVisible;
    }

    public boolean isEtqNumeropptoVisible() {
        return etqNumeropptoVisible;
    }

    public void setEtqNumeropptoVisible(boolean etqNumeropptoVisible) {
        this.etqNumeropptoVisible = etqNumeropptoVisible;
    }

    public boolean isEtqRubropptoVisible() {
        return etqRubropptoVisible;
    }

    public void setEtqRubropptoVisible(boolean etqRubropptoVisible) {
        this.etqRubropptoVisible = etqRubropptoVisible;
    }

    public boolean isEtqValorpptoVisible() {
        return etqValorpptoVisible;
    }

    public void setEtqValorpptoVisible(boolean etqValorpptoVisible) {
        this.etqValorpptoVisible = etqValorpptoVisible;
    }

    public boolean isEtqRegistroVisible() {
        return etqRegistroVisible;
    }

    public void setEtqRegistroVisible(boolean etqRegistroVisible) {
        this.etqRegistroVisible = etqRegistroVisible;
    }

    public boolean isEtqTipoRegistroVisible() {
        return etqTipoRegistroVisible;
    }

    public void setEtqTipoRegistroVisible(boolean etqTipoRegistroVisible) {
        this.etqTipoRegistroVisible = etqTipoRegistroVisible;
    }

    public boolean isEtqDependenciaVisible() {
        return etqDependenciaVisible;
    }

    public void setEtqDependenciaVisible(boolean etqDependenciaVisible) {
        this.etqDependenciaVisible = etqDependenciaVisible;
    }

    public boolean isAniopptoVisible() {
        return aniopptoVisible;
    }

    public void setAniopptoVisible(boolean aniopptoVisible) {
        this.aniopptoVisible = aniopptoVisible;
    }

    public boolean isConsecutivopptoVisible() {
        return consecutivopptoVisible;
    }

    public void setConsecutivopptoVisible(boolean consecutivopptoVisible) {
        this.consecutivopptoVisible = consecutivopptoVisible;
    }

    public boolean isEtqTipopptoVisible() {
        return etqTipopptoVisible;
    }

    public void setEtqTipopptoVisible(boolean etqTipopptoVisible) {
        this.etqTipopptoVisible = etqTipopptoVisible;
    }

    public boolean isTipopptoVisible() {
        return tipopptoVisible;
    }

    public void setTipopptoVisible(boolean tipopptoVisible) {
        this.tipopptoVisible = tipopptoVisible;
    }

    public boolean isPorcDescGlobalLocked() {
        return porcDescGlobalLocked;
    }

    public void setPorcDescGlobalLocked(boolean porcDescGlobalLocked) {
        this.porcDescGlobalLocked = porcDescGlobalLocked;
    }

    public boolean isPorcIVAGlobalLocked() {
        return porcIVAGlobalLocked;
    }

    public void setPorcIVAGlobalLocked(boolean porcIVAGlobalLocked) {
        this.porcIVAGlobalLocked = porcIVAGlobalLocked;
    }

    public String getPorcIVAGlobal() {
        return porcIVAGlobal;
    }

    public void setPorcIVAGlobal(String porcIVAGlobal) {
        this.porcIVAGlobal = porcIVAGlobal;
    }

    public String getPorcDescGlobal() {
        return porcDescGlobal;
    }

    public void setPorcDescGlobal(String porcDescGlobal) {
        this.porcDescGlobal = porcDescGlobal;
    }
    
    public String getImpConsumo() {
        return impConsumo;
    }

    public void setImpConsumo(String porcIVAGlobal) {
        this.impConsumo = impConsumo;
    }
    

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getNombreElemento() {
        return nombreElemento;
    }

    public void setNombreElemento(String nombreElemento) {
        this.nombreElemento = nombreElemento;
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public boolean isMuestraAfectar() {
        return muestraAfectar;
    }

    public void setMuestraAfectar(boolean muestraAfectar) {
        this.muestraAfectar = muestraAfectar;
    }

    /**
	 * @return the listaUnidad
	 */
	public RegistroDataModelImpl getListaUnidad() {
		return listaUnidad;
	}

	/**
	 * @param listaUnidad the listaUnidad to set
	 */
	public void setListaUnidad(RegistroDataModelImpl listaUnidad) {
		this.listaUnidad = listaUnidad;
	}

	/**
	 * @return the pideVencimiento
	 */
	public boolean isPideVencimiento() {
		return pideVencimiento;
	}

	/**
	 * @param pideVencimiento the pideVencimiento to set
	 */
	public void setPideVencimiento(boolean pideVencimiento) {
		this.pideVencimiento = pideVencimiento;
	}

	/**
	 * @return the visibleAIU
	 */
	public boolean isVisibleAIU() {
		return visibleAIU;
	}

	/**
	 * @param visibleAIU the visibleAIU to set
	 */
	public void setVisibleAIU(boolean visibleAIU) {
		this.visibleAIU = visibleAIU;
	}

	/**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {

            parametro = ejbSysmanUtil.consultarParametro(
                            compania,
                            nombreParametro,
                            SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            Logger.getLogger(SubpcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Validaciones realizadas antes de cerrar el formulario.
     */
    public void ejecutarrcCerrar() {
        SessionUtil.cleanFlash();
        String ruta = "/pcontrato.sysman";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("vigencia", vigencia);
        parametros.put("claseF", claseOrden);
        parametros.put("titulo", titulo);
        parametros.put("ridPcontrato", ridPContrato);
        parametros.put("convenio", aportantes);

        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametros);
        SessionUtil.redireccionar(direccionador);
    }
}
