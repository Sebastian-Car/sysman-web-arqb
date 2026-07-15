package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
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
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.ImpresionfacturasControladorEnum;
import com.sysman.serviciospublicos.enums.ImpresionfacturasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 31/08/2016
 * @version 2, 31/05/2017 jrodriguezr Se refactoriza el c�digo SQL
 * de las listas para utilizar DSS. Tambi�n los llamados a
 * funciones, procedimientos y m�todos de la clase Acciones a
 * llamados a EJB. Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class ImpresionfacturasControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String cicloTxt;
    private final String codigoFinalTxt;
    private final String codigoInicialTxt;
    private final String codigoRutaTxt;
    private final String fechaLimite1;
    private final String fechaLimite2;
    private final String manejaProceso;
    private final String nombreInicial;
    private final String nombreFinal;
    private final String periodoTxt;

    /**
     * Constante definida para almacenar la cadena "COMPANIA"
     */
    private final String cCompania;
    /**
     * Constante definida para almacenar la cadena "NUMERO"
     */
    private final String cNumero;
    /**
     * Constante definida para almacenar la cadena "NORECIBOINICIAL"
     */
    private final String cNoReciboInicial;
    /**
     * Constante definida para almacenar la cadena "ciclo"
     */
    private final String cCicloLower;
    // <DECLARAR_ATRIBUTOS>
    private String modificado;
    private boolean respeta;
    private String nombrePeriodo;
    private String totalFacPerAct = "";
    private String txtFimm = "";
    private String bancoperproceso = "";
    private String codigoInterno = "";
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmbCiclo;
    private RegistroDataModelImpl listaMiCodigoInicial;
    private RegistroDataModelImpl listaMiCodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    private String ciclo;
    private String ano;
    private String periodo;
    private String codigoInicial;
    private String codigoFinal;
    private String marca;
    private String marcaIni;
    private String noReciboInicial;
    private String nombreCodInicial;
    private String nombreCodFinal;

    private boolean cuadroFechasVisible;
    /**
     * Atributo que toma el valor del codigo del combo codigo Inicial
     */
    private String codInicial;
    /**
     * Atributo que toma el valor del codigo del combo codigo Final
     */
    private String codFinal;
    /**
     * Atributo que toma el valor del nombre del combo codigo Inicial
     */
    private String nomInicial;
    /**
     * Atributo que toma el valor del nombre del combo codigo Final
     */
    private String nomFinal;
    /**
     * Atributo que valida si los combos de Ciclo , Codigo Inicial y
     * Codigo Final son editables.
     */
    private boolean bloqueadoCodigos;
    /**
     * Almacena el rid del usuario cuando ha sido redireccionado desde
     * FacturaControlador
     */
    private Map<String, Object> ridSuscriptor;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private boolean fecSuspensionVisible;
    private boolean blImprimirBarras;
    private String strCodEAN;
    private boolean fecBarra;
    private boolean unCodTer;
    private boolean publiTer;
    private boolean respetaVisible;
    private boolean facturaInicialBloq;
    private boolean desdeSuscriptor;
    private boolean sus;
    private boolean activarComponentes;
    private boolean soloDatos;
    private boolean soloDatosVisible;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicos;

    @EJB
    private EjbServiciosPublicosOchoRemote ejbServiciosPublicosOcho;
    private String controlaFechas;
    private String rutaFactura;

    // </DECLARAR_ADICIONALES>
    @SuppressWarnings("unchecked")
    public ImpresionfacturasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cicloTxt = GeneralParameterEnum.CICLO.getName();
        codigoFinalTxt = "CODIGOFINAL";
        codigoInicialTxt = "CODIGOINICIAL";
        codigoRutaTxt = "CODIGORUTA";
        fechaLimite1 = "FECHALIMITE1";
        fechaLimite2 = "FECHALIMITE2";
        manejaProceso = "MANEJA PROCESO TERCERIZADO";
        nombreInicial = "NOMBREINICIAL";
        nombreFinal = "NOMBREFINAL";
        periodoTxt = GeneralParameterEnum.PERIODO.getName();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cNumero = GeneralParameterEnum.NUMERO.getName();
        cNoReciboInicial = "NORECIBOINICIAL";
        cCicloLower = cicloTxt.toLowerCase();
        desdeSuscriptor = false;
        registro = new Registro();
        try {
            // 1076
            numFormulario = GeneralCodigoFormaEnum.IMPRESIONFACTURAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            if ("740501".equals(SessionUtil.getMenuActual())) { // DETALLE
                marca = "1";/* Normal */
                marcaIni = "1";
                noReciboInicial = "0";
                activarComponentes = true;
            }
            else {
                activarComponentes = false;
                Map<String, Object> parametros = SessionUtil.getFlash();
                if (parametros != null) {
                    ciclo = parametros.get(cCicloLower).toString();
                    ano = parametros.get("ano").toString();
                    periodo = parametros.get(periodoTxt.toLowerCase())
                                    .toString();
                    codigoInicial = parametros.get("codigoInicial").toString();
                    codigoFinal = parametros.get("codigoFinal").toString();
                    marca = parametros.get("marca").toString();
                    marcaIni = parametros.get("marcaIni").toString();
                    noReciboInicial = SysmanFunciones
                                    .nvl(parametros.get("noReciboInicial"), "0")
                                    .toString();
                    rid = new HashMap<String, Object>();
                    rid.put("KEY_COMPANIA", compania);
                    rid.put("KEY_CICLO", ciclo);
                    rid.put("KEY_ANO", ano);
                    rid.put("KEY_PERIODO", periodo);
                    codInicial = codigoInicial;
                    codFinal = codigoFinal;
                    nomInicial = SysmanFunciones
                                    .nvl(parametros.get("nombreInicial"), "")
                                    .toString();
                    nomFinal = SysmanFunciones
                                    .nvl(parametros.get("nombreFinal"), "")
                                    .toString();
                    desdeSuscriptor = (boolean) SysmanFunciones.nvl(
                                    parametros.get("desdeSuscriptor"), false);
                    ridSuscriptor = (Map<String, Object>) parametros.get("rid");

                    facturaInicialBloq = true;
                }
            }
            // </INI_ADICIONAL>
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SP_PARAMETROFACTURACION;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCmbCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionfacturasControladorUrlEnum.URL12595
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCmbCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumero);
    }

    public void cargarListaMiCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionfacturasControladorUrlEnum.URL13432
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        registro.getCampos().get(cicloTxt));
        listaMiCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaTxt);
    }

    public void cargarListaMiCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ImpresionfacturasControladorUrlEnum.URL14360
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CICLO.getName(),
                        registro.getCampos().get(cicloTxt));
        param.put(ImpresionfacturasControladorEnum.CODIGOINICIAL.getValue(),
                        codInicial);
        listaMiCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaTxt);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarFechaLimiteInicial() {
        // <CODIGO_DESARROLLADO>

        if ("SI".equals(controlaFechas)
            && SysmanFunciones.comparaFechas(
                            (Date) registro.getCampos().get(fechaLimite1),
                            SysmanFunciones.truncarFecha(new Date()))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1443"));
            registro.getCampos().put(fechaLimite1, new Date());
        }
    }

    public void cambiarFechaLimiteFinal() {
        // <CODIGO_DESARROLLADO>
        if ("SI".equals(controlaFechas)
            && ((Date) registro.getCampos().get(fechaLimite2))
                            .before(new Date())) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1444"));
            registro.getCampos().put(fechaLimite2, new Date());
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmbCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (!creaRegistro(registroAux.getCampos().get(cNumero).toString(),
                        registroAux.getCampos().get(codigoInicialTxt)
                                        .toString(),
                        registroAux.getCampos().get(codigoFinalTxt).toString(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ANO.getName())
                                        .toString(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.PERIODO.getName())
                                        .toString(),
                        registroAux.getCampos().get("FECHAPAGO1"),
                        registroAux.getCampos().get("FECHAPAGO2"))) {
            return;
        }
    }

    private boolean creaRegistro(String parCiclo, String codigoInicial,
        String codigoFinal, String anio, String parPeriodo, Object fechaPago1,
        Object fechaPago2) {
        try {
            if (!validarCodigos(
                            parCiclo,
                            SysmanFunciones.nvl(codigoInicial, "")
                                            .toString())
                || !validarCodigos(
                                parCiclo,
                                SysmanFunciones.nvl(codigoFinal, "")
                                                .toString())) {
                return false;
            }
            Map<String, Object> key = new HashMap<>();
            key.put("KEY_COMPANIA", compania);
            key.put("KEY_CICLO", parCiclo);
            key.put("KEY_ANO", anio);
            key.put("KEY_PERIODO", parPeriodo);
            registro = RegistroConverter.toRegistro(
                            requestManager.get(urlLectura.getUrl(), key));
            if ((registro == null) || registro.getCampos().isEmpty()) {
                registro = new Registro();
                registro.getCampos().put(cCompania, compania);
                registro.getCampos().put(cNoReciboInicial, noReciboInicial);
                registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                anio);
                registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                                parPeriodo);
                registro.getCampos().put(codigoInicialTxt,
                                codigoInicial);
                registro.getCampos().put(codigoFinalTxt,
                                codigoFinal);
                registro.getCampos().put(fechaLimite1,
                                fechaPago1);
                registro.getCampos().put(fechaLimite2,
                                fechaPago2);
                registro.getCampos().put(GeneralParameterEnum.CICLO.getName(),
                                parCiclo);

                agregarRegistroNuevo(true);
                cargarRegistro(key, ACCION_MODIFICAR);
            }
            else {
                css = key;
                accion = ACCION_MODIFICAR;
                registro.getCampos().put(codigoInicialTxt,
                                codigoInicial);
                registro.getCampos().put(codigoFinalTxt,
                                codigoFinal);
            }
            nombrePeriodo = cargarNombrePeriodo(registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()).toString(),
                            registro.getCampos()
                                            .get(periodoTxt).toString(),
                            null);
            codInicial = registro.getCampos().get(codigoInicialTxt)
                            .toString();
            nomInicial = registro.getCampos().get(nombreInicial)
                            .toString();
            codFinal = registro.getCampos().get(codigoFinalTxt).toString();
            nomFinal = registro.getCampos().get(nombreFinal)
                            .toString();
            cargarListaMiCodigoInicial();
            cargarListaMiCodigoFinal();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    public void seleccionarFilaMiCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoRutaTxt), "")
                        .toString();
        if (!SysmanFunciones.validarVariableVacio(codInicial)) {
            registro.getCampos().put(codigoInicialTxt, codInicial);
            nomInicial = SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()),
                                            "")
                            .toString();
        }
        cargarListaMiCodigoFinal();
    }

    public void seleccionarFilaMiCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codFinal = registroAux.getCampos().get(codigoRutaTxt).toString();
        registro.getCampos().put(codigoFinalTxt, codFinal);
        nomFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        accion = ACCION_MODIFICAR;
        agregarRegistroNuevo(false);
        try {
            if (("1").equals(marca) && ("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "PERMITE ACTUALIZACION FECHAS DE LECTURA",
                                            modulo, new Date(), true),
                            "NO"))) {
                cuadroFechasVisible = true;
                return;
            }
            else {
                creaParametroFacturacion();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private void creaParametroFacturacion()
                    throws SystemException {

        String formato = ejbServiciosPublicosOcho
                        .crearParametroFacturacion(compania,
                                        Integer.parseInt(registro
                                                        .getCampos()
                                                        .get(GeneralParameterEnum.CICLO
                                                                        .getName())
                                                        .toString()),
                                        Integer.parseInt(registro
                                                        .getCampos()
                                                        .get(GeneralParameterEnum.ANO
                                                                        .getName())
                                                        .toString()),
                                        registro.getCampos()
                                                        .get(GeneralParameterEnum.PERIODO
                                                                        .getName())
                                                        .toString(),
                                        Integer.parseInt(marca),
                                        SessionUtil.getUser()
                                                        .getCodigo());
        if (formato != null) {
            genInforme(FORMATOS.PDF, formato);
        }

    }

    private boolean validarCodigos(String ciclo, String codigo) {
        boolean esValido = false;
        try {
            esValido = ejbServiciosPublicosOcho.validarCodigoRuta(compania,
                            Integer.parseInt(ciclo),
                            codigo);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return esValido;
    }

    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.CODIGO.getName(), compania);
            Registro registroRuta = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImpresionfacturasControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
            rutaFactura = registroRuta.getCampos().get("FIRMAFACTURA")
                            .toString();

            bloqueadoCodigos = false;
            if (!"1".equals(marca) || desdeSuscriptor) {
                bloqueadoCodigos = true;
                registro = RegistroConverter.toRegistro(
                                requestManager.get(urlLectura.getUrl(), rid));
                if ((registro == null) || registro.getCampos().isEmpty()) {
                    registro = new Registro();
                    registro.getCampos().put(cCompania, compania);
                    registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                    ano);
                    registro.getCampos().put(
                                    GeneralParameterEnum.PERIODO.getName(),
                                    periodo);
                    registro.getCampos().put(codigoInicialTxt,
                                    codigoInicial);
                    registro.getCampos().put(codigoFinalTxt,
                                    codigoFinal);
                    registro.getCampos().put(
                                    GeneralParameterEnum.CICLO.getName(),
                                    ciclo);
                    registro.getCampos().put(cNoReciboInicial,
                                    noReciboInicial);
                    agregarRegistroNuevo(true);
                }
                else {
                    cargarRegistro(rid, ACCION_MODIFICAR);
                    cargarListaCmbCiclo();
                    param.clear();
                    param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                    registro.getCampos().put(fechaLimite1,
                                    listaCmbCiclo.getRegistroUnico(param)
                                                    .getCampos()
                                                    .get("FECHAPAGO1"));
                    registro.getCampos().put(fechaLimite2,
                                    listaCmbCiclo.getRegistroUnico(param)
                                                    .getCampos()
                                                    .get("FECHAPAGO2"));
                    registro.getCampos().put(codigoInicialTxt,
                                    codigoInicial);
                    registro.getCampos().put(codigoFinalTxt,
                                    codigoFinal);
                    registro.getCampos().put(cNoReciboInicial,
                                    noReciboInicial);
                }

            }

            facturaInicialBloq = !"0".equals(noReciboInicial) ? true : false;

            controlaFechas = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CONTROLAR FECHA LIMITE",
                            modulo, new Date(), true);
            String facturaSinTroquelar = ejbSysmanUtil.consultarParametro(
                            compania,
                            "PERMITE GENERAR FACTURA SIN TROQUELAR",
                            modulo, new Date(), true);
            soloDatosVisible = "SI".equals(facturaSinTroquelar);
            fecSuspensionVisible = ("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "MOSTRAR FECHA SUSPENSION PARAMETROFACTURACION",
                                            modulo, new Date(), true),
                            "NO"));

            blImprimirBarras = ("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "RECAUDO CON CODIGO DE BARRAS",
                                            modulo, new Date(), true),
                            "NO"));

            strCodEAN = SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania, "CODIGO DE SERVICIO EAN",
                                            modulo, new Date(), true),
                            "9999999999999").toString();

            fecBarra = ("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "INCLUYE FECHA EN BARRAS EAN",
                                            modulo, new Date(), true),
                            "NO"));

            publiTer = ("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            manejaProceso,
                                            modulo, new Date(), true),
                            "NO"))
                && ("SI").equals(SysmanFunciones.nvl(
                                ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "MANEJA PUBLICIDAD EN PROCESO TERCERIZADO",
                                                modulo, new Date(), true),
                                "NO"));

            unCodTer = ("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            manejaProceso,
                                            modulo, new Date(), true),
                            "NO"))
                && ("SI").equals(SysmanFunciones.nvl(
                                ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "PROCESO TERCERIZADO CON UN SOLO C�DIGO DE BARRAS",
                                                modulo, new Date(), true),
                                "NO"));

            respetaVisible = ("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania, "PERMITIR COPIAS EN LOTE",
                                            modulo, new Date(), true),
                            "NO"))
                && (("1").equals(marca));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarListaCmbCiclo();

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        modificado = "2";
        nombreCodInicial = null;
        nombreCodFinal = null;
        respeta = false;
        try {
            if (("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "CONTROLA MODIFICADORES PARAMETROS DE IMPRESION",
                                            modulo, new Date(), true),
                            "NO"))
                && (ACCION_MODIFICAR).equals(accion)) {
                accion = ACCION_VER;
            }
            if (css != null) {
                cargarListaMiCodigoInicial();
                cargarListaMiCodigoFinal();
                nombreCodInicial = registro.getCampos().get(nombreInicial)
                                .toString();
                nombreCodFinal = registro.getCampos().get(nombreFinal)
                                .toString();

                nombrePeriodo = cargarNombrePeriodo(
                                registro.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName())
                                                .toString(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.PERIODO
                                                                .getName())
                                                .toString(),
                                null);
            }
            else {
                nombrePeriodo = null;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private String cargarNombrePeriodo(String anio, String periodo,
        String frecuencia)
                        throws SystemException {
        return ejbServiciosPublicos.asignarNombrePeriodo(compania,
                        Integer.parseInt(anio), periodo, frecuencia);
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     *
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        if (desdeSuscriptor) {
            Map<String, Object> parametros = new HashMap<>();

            parametros.put(cCicloLower, ciclo);
            parametros.put("ano", ano);
            parametros.put("periodo", periodo);
            parametros.put("rid", ridSuscriptor);
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FACTURA_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {
            SessionUtil.redireccionarMenu();
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(nombreInicial);
        registro.getCampos().remove(nombreFinal);
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
            registro.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public String getModificado() {
        return modificado;
    }

    public void setModificado(String modificado) {
        this.modificado = modificado;
    }

    public boolean getRespeta() {
        return respeta;
    }

    public void setRespeta(boolean respeta) {
        this.respeta = respeta;
    }

    public String getNombrePeriodo() {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable codInicial
     *
     * @return codInicial
     */
    public String getCodInicial() {
        return codInicial;
    }

    /**
     * Asigna la variable codInicial
     *
     * @param codInicial
     * Variable a asignar en codInicial
     */
    public void setCodInicial(String codInicial) {
        this.codInicial = codInicial;
    }

    /**
     * Retorna la variable codFinal
     *
     * @return codFinal
     */
    public String getCodFinal() {
        return codFinal;
    }

    /**
     * Asigna la variable codFinal
     *
     * @param codFinal
     * Variable a asignar en codFinal
     */
    public void setCodFinal(String codFinal) {
        this.codFinal = codFinal;
    }

    /**
     * Retorna la variable nomInicial
     *
     * @return nomInicial
     */
    public String getNomInicial() {
        return nomInicial;
    }

    /**
     * Asigna la variable nomInicial
     *
     * @param nomInicial
     * Variable a asignar en nomInicial
     */
    public void setNomInicial(String nomInicial) {
        this.nomInicial = nomInicial;
    }

    /**
     * Retorna la variable nomFinal
     *
     * @return nomFinal
     */
    public String getNomFinal() {
        return nomFinal;
    }

    /**
     * Asigna la variable nomFinal
     *
     * @param nomFinal
     * Variable a asignar en nomFinal
     */
    public void setNomFinal(String nomFinal) {
        this.nomFinal = nomFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCmbCiclo() {
        return listaCmbCiclo;
    }

    public void setListaCmbCiclo(RegistroDataModelImpl listaCmbCiclo) {
        this.listaCmbCiclo = listaCmbCiclo;
    }

    public RegistroDataModelImpl getListaMiCodigoInicial() {
        return listaMiCodigoInicial;
    }

    public void setListaMiCodigoInicial(
        RegistroDataModelImpl listaMiCodigoInicial) {
        this.listaMiCodigoInicial = listaMiCodigoInicial;
    }

    public RegistroDataModelImpl getListaMiCodigoFinal() {
        return listaMiCodigoFinal;
    }

    public void setListaMiCodigoFinal(
        RegistroDataModelImpl listaMiCodigoFinal) {
        this.listaMiCodigoFinal = listaMiCodigoFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getMarcaIni() {
        return marcaIni;
    }

    public void setMarcaIni(String marcaIni) {
        this.marcaIni = marcaIni;
    }

    public String getNoReciboInicial() {
        return noReciboInicial;
    }

    public void setNoReciboInicial(String noReciboInicial) {
        this.noReciboInicial = noReciboInicial;
    }
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    public boolean isFecSuspensionVisible() {
        return fecSuspensionVisible;
    }

    public boolean isBloqueadoCodigos() {
        return bloqueadoCodigos;
    }

    public void setBloqueadoCodigos(boolean bloqueadoCodigos) {
        this.bloqueadoCodigos = bloqueadoCodigos;
    }

    public void setFecSuspensionVisible(boolean fecSuspensionVisible) {
        this.fecSuspensionVisible = fecSuspensionVisible;
    }

    public boolean isBlImprimirBarras() {
        return blImprimirBarras;
    }

    public void setBlImprimirBarras(boolean blImprimirBarras) {
        this.blImprimirBarras = blImprimirBarras;
    }

    public String getStrCodEAN() {
        return strCodEAN;
    }

    public void setStrCodEAN(String strCodEAN) {
        this.strCodEAN = strCodEAN;
    }

    public boolean isFecBarra() {
        return fecBarra;
    }

    public void setFecBarra(boolean fecBarra) {
        this.fecBarra = fecBarra;
    }

    public boolean isUnCodTer() {
        return unCodTer;
    }

    public void setUnCodTer(boolean unCodTer) {
        this.unCodTer = unCodTer;
    }

    public boolean isPubliTer() {
        return publiTer;
    }

    public void setPubliTer(boolean publiTer) {
        this.publiTer = publiTer;
    }

    public boolean isRespetaVisible() {
        return respetaVisible;
    }

    public void setRespetaVisible(boolean respetaVisible) {
        this.respetaVisible = respetaVisible;
    }

    public boolean isFacturaInicialBloq() {
        return facturaInicialBloq;
    }

    public void setFacturaInicialBloq(boolean facturaInicialBloq) {
        this.facturaInicialBloq = facturaInicialBloq;
    }

    public boolean isCuadroFechasVisible() {
        return cuadroFechasVisible;
    }

    public void setCuadroFechasVisible(boolean cuadroFechasVisible) {
        this.cuadroFechasVisible = cuadroFechasVisible;
    }

    public boolean isDesdeSuscriptor() {
        return desdeSuscriptor;
    }

    public void setDesdeSuscriptor(boolean desdeSuscriptor) {
        this.desdeSuscriptor = desdeSuscriptor;
    }

    public String getNombreCodInicial() {
        return nombreCodInicial;
    }

    public void setNombreCodInicial(String nombreCodInicial) {
        this.nombreCodInicial = nombreCodInicial;
    }

    public String getNombreCodFinal() {
        return nombreCodFinal;
    }

    public void setNombreCodFinal(String nombreCodFinal) {
        this.nombreCodFinal = nombreCodFinal;
    }

    public String getTotalFacPerAct() {
        return totalFacPerAct;
    }

    public void setTotalFacPerAct(String totalFacPerAct) {
        this.totalFacPerAct = totalFacPerAct;
    }

    public String getTxtFimm() {
        return txtFimm;
    }

    public void setTxtFimm(String txtFimm) {
        this.txtFimm = txtFimm;
    }

    public String getBancoperproceso() {
        return bancoperproceso;
    }

    public void setBancoperproceso(String bancoperproceso) {
        this.bancoperproceso = bancoperproceso;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public boolean isSus() {
        return sus;
    }

    public void setSus(boolean sus) {
        this.sus = sus;
    }

    // </SET_GET_ADICIONALES>

    public void aceptarCuadroFechas() {
        // <CODIGO_DESARROLLADO>
        cuadroFechasVisible = false;
        try {
            creaParametroFacturacion();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cancelarCuadroFechas() {
        // <CODIGO_DESARROLLADO>
        cuadroFechasVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public boolean isActivarComponentes() {
        return activarComponentes;
    }

    public void setActivarComponentes(boolean activarComponentes) {
        this.activarComponentes = activarComponentes;
    }

    private void genInforme(ReportesBean.FORMATOS formato, String reporte) {
        archivoDescarga = null;
        try {
            /* Reemplazos de la consulta */
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(cCicloLower, registro.getCampos()
                            .get(GeneralParameterEnum.CICLO.getName()));
            reemplazar.put("marca", marca);
            reemplazar.put("codigoInicial",
                            registro.getCampos().get(codigoInicialTxt));
            reemplazar.put("codigoFinal",
                            registro.getCampos().get(codigoFinalTxt));
            reemplazar.put("deudaMayorA", ("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "PERMITE EMITIR FACTURAS EN CEROS",
                                            modulo, new Date(), true),
                            "NO")) ? "-1" : "0");
            /* Parametros de la factura */
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBREGERENTE", ejbSysmanUtil.consultarParametro(
                            compania, "NOMBRE DEL GERENTE",
                            SessionUtil.getModulo(),
                            new Date(), true));
            parametros.put("PR_SOLO_DATOS", soloDatos);
            parametros.put("PR_SOLOUNCODDEBARRAS", unCodTer);
            parametros.put("PR_RUTAFIRMAGERENTE", rutaFactura);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException
                        | IOException | SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean isSoloDatos() {
        return soloDatos;
    }

    public void setSoloDatos(boolean soloDatos) {
        this.soloDatos = soloDatos;
    }

    public boolean isSoloDatosVisible() {
        return soloDatosVisible;
    }

    public void setSoloDatosVisible(boolean soloDatosVisible) {
        this.soloDatosVisible = soloDatosVisible;
    }
}
