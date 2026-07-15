package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.PredialcertpazysalvoconpreControladorEnum;
import com.sysman.predial.enums.PredialcertpazysalvoconpreControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Calendar;
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

import org.primefaces.event.SelectEvent;

/**
 *
 * @author dsuesca
 * @version 1, 15/06/2016
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el codigo: Se pasa el numero del formulario al enumerado, se eliminan conexiones y se ajustan metodos de generacion de reportes.
 *
 * @author spina
 * @version 3, 10/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class PredialcertpazysalvoconpreControlador
                extends BeanBaseDatosAcmeImpl
{
    private final String compania;
    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "TB_TB1028"
     */
    private final String strTb1028;
    /**
     * Constante definida para almacenar la cadena "TB_TB2949"
     */
    private final String strTb2949;
    // <DECLARAR_ATRIBUTOS>
    // <DECLARAR CONSTANTES>
    private final String strTb755;
    private final String strFormatoFecha;
    private final String strFalse;
    private final String strsNumcers;
    // </DECLARAR CONSTANTES>
    private boolean indCopropietarios;
    private boolean indExento;
    private boolean indCertificado;
    private boolean indPazySalvo;
    private boolean indValorizacion;
    private boolean indImprimirCopia;
    private boolean indExe;
    private String codigoPredio;
    private String consecutivo;
    private String destino;
    private String recibo;
    private String expedida;
    private String numCer;
    private Date fechaExpedicion;
    private double valor;
    private String nombrePredio;
    private String observaciones;
    private String direccion;
    private String cedula;
    private String codPredio;
    private String nomPredio;
    private String noRecibo;
    private String observaciones1;
    private String tipoCertificado;
    private String pagoAnio;
    private String mensajeCuadro;
    private String fechaConVigencias;
    private boolean continuarVisible;
    private boolean estaPago;
    private boolean indBorrado;
    private String codigoAnterior;
    private String areaHa;
    private String areaM;
    private String avaluoAnio;
    private String sucursal;
    private String banco;
    private String pagVal;
    private Registro rsDatoPredio;
    private boolean bloqmprimirCopia;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listadestino;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigopredio;
    private RegistroDataModelImpl listaNumConsecutivo;
    private RegistroDataModelImpl listaCmbPropietario;
    private RegistroDataModelImpl listaRecibo;
    private RegistroDataModelImpl listaplantilla;
    private boolean numeroCertificado = false;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private boolean bloqueadoImprimirCopia;
    private boolean bloqueadoCodigoPredio;
    private String orden;
    private int numeroDePazYSalvosExcentos;
    private String txtFactura;
    private String txtFechaPago;
    private boolean generaReciboDePagoParaPazYSalvo;
    private boolean generarPazYSalvoAExcentos;
    private boolean generarCertificadoANoRegistrados;
    private boolean pazYSalvoEspecial;
    private boolean manejaTiposDePazYSalvos;
    private boolean generarPazYSalvoAOtrosPropietarios;

    private int diasMas;
    private boolean visibleNumConsecutivo;
    private boolean bloqueadoDestino;
    private boolean bloqueadoNumConsecutivo;
    private String tipoPlantilla;
    private String plantilla;
    private Date fechaPlantilla;
    private boolean esInformativo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    public PredialcertpazysalvoconpreControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strTb755 = "TB_TB755";
        strFormatoFecha = "ddMMyyyy";
        strFalse = "false";
        strsNumcers = "s$numCer$s";
        strTb1028 = "TB_TB1028";
        strTb2949 = "TB_TB2949";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PREDIALCERTPAZYSALVOCONPRE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(PredialcertpazysalvoconpreControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    public void initAdicional()
    {

        try
        {
            generaReciboDePagoParaPazYSalvo = "SI"
                            .equalsIgnoreCase(SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "GENERA RECIBO DE PAGO PARA PAZ Y SALVO",
                                                            modulo,
                                                            new Date(),
                                                            true),
                                            "NO")
                                            .toString());

            bloqueadoCodigoPredio = false;
            generarPazYSalvoAExcentos = "SI".equalsIgnoreCase(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "GENERAR PAZ Y SALVO A EXCENTOS",
                                            modulo, new Date(), true), "NO")
                            .toString());

            generarCertificadoANoRegistrados = "SI"
                            .equalsIgnoreCase(SysmanFunciones
                                            .nvl(ejbSysmanUtil
                                                            .consultarParametro(
                                                                            compania,
                                                                            "GENERAR CERTIFICADO A NO REGISTRADOS",
                                                                            modulo,
                                                                            new Date(),
                                                                            true),
                                                            "NO")
                                            .toString());
            pazYSalvoEspecial = "SI".equalsIgnoreCase(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "PAZ Y SALVO ESPECIAL",
                                            modulo,
                                            new Date(),
                                            true), "NO")
                            .toString());
            manejaTiposDePazYSalvos = "SI".equalsIgnoreCase(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "MANEJA TIPOS DE PAZ Y SALVOS",
                                            modulo,
                                            new Date(),
                                            true), "NO")
                            .toString());
            generarPazYSalvoAOtrosPropietarios = "SI"
                            .equalsIgnoreCase(SysmanFunciones
                                            .nvl(ejbSysmanUtil
                                                            .consultarParametro(
                                                                            compania,
                                                                            "GENERAR PAZ Y SALVO A OTROS PROPIETARIOS",
                                                                            modulo,
                                                                            new Date(),
                                                                            true),
                                                            "NO")
                                            .toString());
            diasMas = Integer.parseInt(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "DIAS VENCIMIENTO PAZ Y SALVO",
                                            modulo, new Date(), true),
                                            "0")
                            .toString());

            numeroDePazYSalvosExcentos = Integer.parseInt(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "NUMERO DE PAZ Y SALVOS EXCENTOS",
                                            modulo, new Date(), true),
                            "0").toString());

            expedida = SessionUtil.getCompaniaIngreso().getCiudad();
            fechaExpedicion = new Date();
            tipoCertificado = "1";
            tipoPlantilla = "16";
            orden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;

        }
        catch (SystemException e)
        {
            Logger.getLogger(PredialcertpazysalvoconpreControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodigopredio();
        cargarListaRecibo();
        cargarListaplantilla();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListadestino();
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar()
    {
        tabla = "";
        asignarOrigenDatos();
        initAdicional();
        iniciarListas();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListadestino()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listadestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialcertpazysalvoconpreControladorUrlEnum.URL4469
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacodigopredio()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialcertpazysalvoconpreControladorUrlEnum.URL4470
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), orden);
        listacodigopredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaNumConsecutivo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialcertpazysalvoconpreControladorUrlEnum.URL4471
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        listaNumConsecutivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        PredialcertpazysalvoconpreControladorEnum.NUMCER
                                        .getValue());
    }

    public void cargarListaCmbPropietario()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialcertpazysalvoconpreControladorUrlEnum.URL4472
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);
        listaCmbPropietario = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        PredialcertpazysalvoconpreControladorEnum.NIT
                                        .getValue());
    }

    public void cargarListaRecibo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialcertpazysalvoconpreControladorUrlEnum.URL4473
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaRecibo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.REFERENCIA.getName());
    }

    public void cargarListaplantilla()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialcertpazysalvoconpreControladorUrlEnum.URL4474
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(PredialcertpazysalvoconpreControladorEnum.TIPO.getValue(),
                        tipoPlantilla);
        listaplantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiardestino()
    {
        // Define las acciones a realizar cuando se realiza el cambio
        // de valor al campo destino

        if (SysmanFunciones.validarVariableVacio(recibo))
        {
            recibo = "";
        }
    }

    public void cambiarexento()
    {
        valor = valor();

    }

    private double valor()
    {
        double val = 0.0;
        if (indExento)
        {
            val = 0.0;
        }
        else
        {
            try
            {
                if ("1".equals(tipoCertificado))
                {
                    val = Double.parseDouble(SysmanFunciones.nvl(
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "TARIFA CERTIFICADO PREDIO",
                                                    modulo, new Date(), true),
                                    "0").toString());
                }
                else if ("2".equals(tipoCertificado))
                {
                    val = Double.parseDouble(SysmanFunciones.nvl(
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "TARIFA CERTIFICADO DE VALORIZACION",
                                                    modulo, new Date(), true),
                                    "0").toString());
                }
                else
                {
                    val = Double.parseDouble(SysmanFunciones.nvl(
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "TARIFA CERTIFICADO CATASTRAL",
                                                    modulo, new Date(), true),
                                    "0").toString());
                }
            }
            catch (SystemException e)
            {
                Logger.getLogger(PredialcertpazysalvoconpreControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return val;
    }

    public void cambiarindImprimirCopia()
    {
        if (indImprimirCopia)
        {
            visibleNumConsecutivo = true;
            esInformativo = true;
            bloqueadoDestino = true;
        }
        else
        {
            visibleNumConsecutivo = false;
            esInformativo = false;
            numeroCertificado = false;
            consecutivo = codPredio = nomPredio = null;
            bloqueadoDestino = false;
        }
    }

    public void cambiartipoCetificado()
    {
        switch (tipoCertificado)
        {
        case "1":
            tipoPlantilla = "16"; // Formatos paz y salvo
            bloqmprimirCopia = false;
            break;
        case "2":
            tipoPlantilla = "14"; // Certificado valorizacion
            bloqmprimirCopia = true;
            break;
        case "3":
            tipoPlantilla = "12"; // Certificado catastral
            bloqmprimirCopia = true;
            break;
        default:
            break;
        }
        valor = valor();
        plantilla = null;
        cargarListaplantilla();
    }

    public void aceptarcontinuar()
    {
        // <CODIGO_DESARROLLADO>
        if (indImprimirCopia && consecutivo.isEmpty())
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(strTb2949));
            return;
        }
        procesarCertificado();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigopredio(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoPredio = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cedula = registroAux.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.NIT
                                        .getValue())
                        .toString();
        pagoAnio = String.valueOf(registroAux.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.PAGO_ANO
                                        .getValue()));
        indBorrado = (boolean) registroAux.getCampos().get("INDBORRADO");
        codigoAnterior = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOANT1"), "")
                        .toString();
        indExe = (boolean) registroAux.getCampos().get("INDEXE");

        if (validarPredio(registroAux))
        {
            if (generarPazYSalvoAOtrosPropietarios)
            {
                cedula = registroAux.getCampos()
                                .get(PredialcertpazysalvoconpreControladorEnum.NIT
                                                .getValue())
                                .toString();
                cargarListaCmbPropietario();
            }
            asignarCampos(registroAux);
        }
        consecutivo = null;
        codPredio = null;
        nomPredio = null;
        cargarListaNumConsecutivo();

    }

    public void seleccionarFilaNumConsecutivo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        consecutivo = registroAux.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.NUMCER
                                        .getValue())
                        .toString();

        codPredio = registroAux.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.CODIGOPREDIO
                                        .getValue())
                        .toString();
        cedula = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.NIT
                                        .getValue()),
                        "")
                        .toString();
        nomPredio = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        codigoPredio = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(PredialcertpazysalvoconpreControladorEnum.CODIGOPREDIO
                                                        .getValue()),
                                        "")
                        .toString();
        nombrePredio = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        numCer = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.NUMCER
                                        .getValue()),
                        "")
                        .toString();
        direccion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.DIRECCION
                                                        .getName()),
                                        "")
                        .toString();
        Object fechaexp = registroAux.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.FECHA_EXP
                                        .getValue());
        if (fechaexp != null)
        {
            fechaExpedicion = (Date) registroAux.getCampos()
                            .get(PredialcertpazysalvoconpreControladorEnum.FECHA_EXP
                                            .getValue());
        }
        else
        {
            fechaExpedicion = null;
        }
        destino = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DESTINO"), "")
                        .toString();
        bloqueadoDestino = true;
        noRecibo = registroAux.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.REC_VALORIZACION
                                        .getValue()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(PredialcertpazysalvoconpreControladorEnum.REC_VALORIZACION
                                                                            .getValue())
                                                            .toString();

        orden = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_ORDEN.getName()), "")
                        .toString();
        numeroCertificado = true;

    }

    public void seleccionarFilaCmbPropietario(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cedula = registroAux.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.NIT
                                        .getValue())
                        .toString();

        asignarCampos(registroAux);

    }

    public void seleccionarFilaRecibo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        recibo = registroAux.getCampos()
                        .get(GeneralParameterEnum.REFERENCIA.getName())
                        .toString();
        codigoPredio = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO_PREDIO"),
                                        "")
                        .toString();
        bloqueadoCodigoPredio = true;
        if ("".equals(recibo))
        {
            bloqueadoCodigoPredio = false;
            orden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
            cargarListacodigopredio();
            return;
        }

        indImprimirCopia = false;

        orden = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_ORDEN
                                                        .getName()),
                                        "")
                        .toString();

        cargarListacodigopredio();

        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        Registro rs;
        try
        {
            rs = listacodigopredio.getRegistroUnico(params);

            if (validarPredio(rs))
            {
                asignarCampos(rs);
            }

            valor = Double.parseDouble(SysmanFunciones
                            .nvl(registroAux.getCampos().get("VALOR"), "")
                            .toString());
            destino = null;
            bloqueadoDestino = false;
            noRecibo = recibo;
            cedula = SysmanFunciones.nvl(rs.getCampos()
                            .get(PredialcertpazysalvoconpreControladorEnum.NIT
                                            .getValue()),
                            "")
                            .toString();
            nombrePredio = SysmanFunciones
                            .nvl(rs.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()),
                                            "")
                            .toString();
            direccion = SysmanFunciones.nvl(rs.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName()), "")
                            .toString();

            if (generarPazYSalvoAOtrosPropietarios)
            {
                cargarListaCmbPropietario();
            }
            consecutivo = null;
            codPredio = null;
            nomPredio = null;
            cargarListaNumConsecutivo();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaplantilla(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        plantilla = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        try
        {
            fechaPlantilla = SysmanFunciones.convertirAFecha(
                            registroAux.getCampos().get("FECHA").toString());
        }
        catch (ParseException e)
        {
            Logger.getLogger(PredialcertpazysalvoconpreControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>
        if (indImprimirCopia && consecutivo.isEmpty())
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(strTb2949));
            return;
        }

        if (numeroCertificado)
        {
            esInformativo = false;
        }
        else
        {
            esInformativo = true;
        }
        getInformePlantilla();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirGenerar()
    {
        // <CODIGO_DESARROLLADO>

        if (indImprimirCopia && consecutivo.isEmpty())
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString(strTb2949));
            return;
        }
        else
        {

            esInformativo = false;

        }

        getInformePlantilla();

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdNoReg()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("FORMULARIO", "PyS");
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMCERTNOREGISTRADOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    public void consultarConsecutivo(String tipoCertificado)
    {
        try
        {
            numCer = null;
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PredialcertpazysalvoconpreControladorEnum.TIPO.getValue(),
                            tipoCertificado);
            Registro rsConsecutivos = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialcertpazysalvoconpreControladorUrlEnum.URL4476
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsConsecutivos != null)
            {
                numCer = rsConsecutivos.getCampos().get("CONSECUTIVOREAL")
                                .toString();
                if (!SysmanFunciones.validarVariableVacio(numCer))
                {
                    numCer = "" + String.format("%0"
                        + rsConsecutivos.getCampos().get("DIGITOS") + "d",
                                    Integer.parseInt(numCer) + 1)
                        + "";

                    Registro reg = new Registro();
                    reg.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                                    numCer);
                    reg.getCampos().put(
                                    GeneralParameterEnum.MODIFIED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());
                    reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED
                                    .getName(),
                                    new Date());
                    reg.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    reg.getCampos().put(
                                    PredialcertpazysalvoconpreControladorEnum.TIPO
                                                    .getValue(),
                                    tipoCertificado);

                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    PredialcertpazysalvoconpreControladorUrlEnum.URL4477
                                                                    .getValue());
                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(),
                                    reg.getCampos(),
                                    reg.getLlave());

                }
                else
                {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB967"));
                    return;
                }
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB1064"));
                return;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void asignarCampos(Registro registro)
    {
        cedula = SysmanFunciones.nvl(registro.getCampos()
                        .get(PredialcertpazysalvoconpreControladorEnum.NIT
                                        .getValue()),
                        "")
                        .toString();
        nombrePredio = SysmanFunciones
                        .nvl(registro.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();

        direccion = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(GeneralParameterEnum.DIRECCION
                                                        .getName()),
                                        "")
                        .toString();
        txtFactura = SysmanFunciones
                        .nvl(registro.getCampos().get("NUM_COM"), "")
                        .toString();
        txtFechaPago = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(PredialcertpazysalvoconpreControladorEnum.PAG_FEC
                                                        .getValue()),
                                        "")
                        .toString();
        indExe = (boolean) registro.getCampos().get("INDEXE");
        orden = registro.getCampos().get(
                        GeneralParameterEnum.NUMERO_ORDEN.getName()) == null
                            ? SysmanConstantes.NUMERO_ORDEN_PREDIAL
                            : registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO_ORDEN
                                                            .getName())
                                            .toString();
    }

    private boolean validaIndicadores(Registro registro)
    {
        return !registro.getCampos().get("INDBORRADO").toString()
                        .equals(strFalse)
            || !registro.getCampos().get("CODIGO_NO_ACTIVO").toString()
                            .equals(strFalse);
    }

    private boolean validarIndicadoresFinal(Registro rs)
    {
        return (rs != null)
            && (numeroDePazYSalvosExcentos <= Integer
                            .parseInt(rs.getCampos().get("CANT").toString()))
            && SysmanFunciones.validarVariableVacio(recibo);
    }

    public boolean validarPredio(Registro registro)
    {

        cedula = direccion = txtFactura = txtFechaPago = nombrePredio = null;
        indExe = false;

        if (!registro.getCampos().get("BLOQUEADO").toString()
                        .equals(strFalse))
        {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB757"));
            return false;
        }
        if (validaIndicadores(registro))
        {
            Map<String, Object> params = new HashMap<>();
            params.put("CODIGO_PREDIO", codigoPredio);
            try
            {
                Registro verificaRecibo = listaRecibo.getRegistroUnico(params);

                if (verificaRecibo != null)
                {
                    JsfUtil.agregarMensajeAlertaDialogo(
                                    idioma.getString("TB_TB758"));
                    return false;
                }
                else
                {
                    JsfUtil.agregarMensajeAlertaDialogo(
                                    idioma.getString("TB_TB1197"));
                    return false;
                }
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        if (validarCamposRegistro(registro))
        {
            return false;
        }

        return true;
    }

    private boolean validarCamposRegistro(Registro registro)
    {

        if (!String.valueOf(registro.getCampos().get("ACUERDO_PAGO"))
                        .equals(strFalse))
        {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB759"));
            return true;
        }
        if (!String.valueOf(registro.getCampos().get("IND_PROCESOJUD"))
                        .equals(strFalse))
        {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB760"));
            return true;
        }

        if (!String.valueOf(registro.getCampos().get("PROCESO_DE_COBRO"))
                        .equals(strFalse))
        {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB761"));
            return true;
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);

        try
        {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialcertpazysalvoconpreControladorUrlEnum.URL4475
                                                                            .getValue())
                                            .getUrl(), param));
            if (validarIndicadoresFinal(rs))
            {
                JsfUtil.agregarMensajeAlertaDialogo(
                                idioma.getString("TB_TB762"));
                codigoPredio = null;
                return true;

            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    public boolean debeCuotasAcuerdo()
    {
        // No se permite generar paz y salvo cuando el predio tiene un
        // acuerdo de pago activo
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), orden);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        try
        {
            Registro rsAcuerdo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialcertpazysalvoconpreControladorUrlEnum.URL4478
                                                                            .getValue())
                                            .getUrl(), param));
            if (rsAcuerdo != null)
            {
                String datosAcuerdo = " AC. PAGO '"
                    + SysmanFunciones
                                    .nvl(rsAcuerdo.getCampos()
                                                    .get("CODIGOACUERDO"), "")
                                    .toString()
                    + "' ACTIVO / Vigencias "
                    + rsAcuerdo.getCampos().get("PREANOI") + " - "
                    + rsAcuerdo.getCampos().get("PREANO");

                JsfUtil.agregarMensajeInformativoDialogo(
                                idioma.getString("TB_TB1022") + datosAcuerdo);
                return true;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return false;
    }

    public boolean estaPago(String predio, int padre)
    {
        try
        {
            estaPago = true;
            mensajeCuadro = "";

            if (!cargarDatosPredio(predio, padre))
            {
                return false;
            }

            int anioActual = SysmanFunciones.getParteFecha(new Date(),
                            Calendar.YEAR);
            boolean permitePazySalvoVigAnt = "SI"
                            .equalsIgnoreCase(SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            "PERMITE PAZ Y SALVO DE VIGENCIAS ANTERIORES",
                                                            modulo, new Date(),
                                                            true),
                                            "NO").toString());

            if (!permitePazySalvoVigAnt)
            {
                if (!validarAnoPagoYExcedentes(anioActual))
                {
                    return false;
                }
            }
            else
            {
                if (!validarAnoMaxVigencia(anioActual, padre))
                {
                    return false;
                }
            }

        }
        catch (SystemException e)
        {
            Logger.getLogger(PredialcertpazysalvoconpreControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return estaPago;
    }

    private boolean validarAnoMaxVigencia(int anioActual, int padre)
                    throws SystemException
    {

        String intAnoMax = SysmanFunciones.nvl(
                        ejbSysmanUtil.consultarParametro(compania,
                                        "MAXIMA VIGENCIA DE PAZ Y SALVO POR VIGENCIAS ANTERIORES",
                                        modulo, new Date(), true),
                        "2050")
                        .toString();
        if (Integer.parseInt(pagoAnio) == anioActual)
        {
            estaPago = true;
        }
        else if (Integer.parseInt(pagoAnio) < Integer.parseInt(intAnoMax))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(strTb1028));
            estaPago = false;
            return estaPago;
        }
        else
        {
            if (!validaPeriodoYPadre(intAnoMax, padre, anioActual))
            {
                return false;
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);
        params.put(GeneralParameterEnum.ANO.getName(), intAnoMax);
        Registro rsPagosDobles = RegistroConverter.toRegistro(
                        requestManager.get(UrlServiceUtil
                                        .getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        PredialcertpazysalvoconpreControladorUrlEnum.URL4483
                                                                        .getValue())
                                        .getUrl(), params));

        if (Integer.parseInt(
                        rsPagosDobles.getCampos().get("CANT").toString()) > 0)
        {
            estaPago = false;
            mensajeCuadro = idioma.getString("TB_TB3302").replace(
                            "s$cantidad$s",
                            SysmanFunciones.nvl(rsPagosDobles.getCampos()
                                            .get("CANT"), "").toString());
            continuarVisible = true;
        }
        return true;

    }

    private boolean validaPeriodoYPadre(String intAnoMax, int padre,
        int anioActual) throws SystemException
    {
        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        params.put(GeneralParameterEnum.PREDIO.getName(),
                        codigoPredio);
        params.put(GeneralParameterEnum.ANO.getName(),
                        Integer.parseInt(intAnoMax));
        Registro rsFacturados = RegistroConverter.toRegistro(
                        requestManager.get(UrlServiceUtil
                                        .getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        PredialcertpazysalvoconpreControladorUrlEnum.URL4482
                                                                        .getValue())
                                        .getUrl(), params));

        if ("0".equals(rsFacturados.getCampos().get("PAG").toString())
            && (padre == 0))
        {
            estaPago = false;
            mensajeCuadro = idioma.getString("TB_TB1084")
                            .replace("#anio#", String
                                            .valueOf(anioActual));

            continuarVisible = true;
        } // Cuando el predio padre este activo y su
          // ultima vigencia paga sea diferente a la
          // vigencia actual deberia salir un mensaje
          // que alerte al usuario
        else if ((padre == 1)
            && (Integer.parseInt(pagoAnio) != anioActual)
            && (!indBorrado))
        {
            estaPago = false;
            mensajeCuadro = idioma.getString("TB_TB3303")
                            .replace("s$anoActual$s",
                                            String.valueOf(anioActual));
            continuarVisible = true;
        }
        // Cuando el predio padre este inactivo y su
        // ultimo anio pago no corresponde a la vigencia
        // actual, no deberia salir ningun mensaje
        else if ((padre == 1)
            && (Integer.parseInt(pagoAnio) != anioActual)
            && (indBorrado))
        {
            estaPago = true;
        }
        else
        {
            estaPago = false;
            return estaPago;
        }
        return true;
    }

    private boolean validarAnoPagoYExcedentes(int anioActual)
                    throws SystemException
    {
        if (Integer.parseInt(pagoAnio) == anioActual)
        {
            estaPago = true;
        }
        else
        {
            Map<String, Object> param2 = new HashMap<>();
            param2.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param2.put(GeneralParameterEnum.CODIGO.getName(),
                            codigoPredio);
            Registro rsFacturados = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialcertpazysalvoconpreControladorUrlEnum.URL4480
                                                                            .getValue())
                                            .getUrl(), param2));

            if (rsFacturados != null)
            {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(strTb1028));
                estaPago = false;
                return estaPago;
            }
        }

        // Verificar si tiene excedentes por cobrar para
        // vigencias iguales o inferiores a la actual
        Map<String, Object> param3 = new HashMap<>();
        param3.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param3.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);
        Registro rsPagosDobles = RegistroConverter.toRegistro(
                        requestManager.get(UrlServiceUtil
                                        .getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        PredialcertpazysalvoconpreControladorUrlEnum.URL4481
                                                                        .getValue())
                                        .getUrl(), param3));

        if (rsPagosDobles != null)
        {
            JsfUtil.agregarMensajeInformativoDialogo(
                            idioma.getString("TB_TB1029"));
            estaPago = false;
            return estaPago;
        }
        return true;
    }

    private boolean cargarDatosPredio(String predio, int padre)
                    throws SystemException
    {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PREDIO.getName(), predio);
        rsDatoPredio = RegistroConverter.toRegistro(
                        requestManager.get(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        PredialcertpazysalvoconpreControladorUrlEnum.URL4479
                                                                        .getValue())
                                        .getUrl(), param));
        if (rsDatoPredio != null)
        {
            pagoAnio = String.valueOf(
                            SysmanFunciones.nvl(rsDatoPredio.getCampos()
                                            .get(PredialcertpazysalvoconpreControladorEnum.PAGO_ANO
                                                            .getValue()),
                                            "0"));
            areaHa = String.valueOf(rsDatoPredio.getCampos()
                            .get(PredialcertpazysalvoconpreControladorEnum.AREA_HA
                                            .getValue()));
            areaM = String.valueOf(rsDatoPredio.getCampos()
                            .get(PredialcertpazysalvoconpreControladorEnum.AREA_M2
                                            .getValue()));
            avaluoAnio = String.valueOf(
                            rsDatoPredio.getCampos().get("AVALUO_ANO"));
            sucursal = String.valueOf(
                            rsDatoPredio.getCampos().get("SUCURSAL"));
            banco = String.valueOf(SysmanFunciones.nvl(
                            rsDatoPredio.getCampos().get("PAG_BAN"), ""));
            txtFechaPago = SysmanFunciones
                            .nvl(rsDatoPredio.getCampos()
                                            .get(PredialcertpazysalvoconpreControladorEnum.PAG_FEC
                                                            .getValue()),
                                            "")
                            .toString();
            pagVal = String.valueOf(
                            rsDatoPredio.getCampos().get("PAG_VAL"));
            cedula = SysmanFunciones
                            .nvl(rsDatoPredio.getCampos()
                                            .get(PredialcertpazysalvoconpreControladorEnum.NIT
                                                            .getValue()),
                                            "")
                            .toString();
            nombrePredio = SysmanFunciones
                            .nvl(rsDatoPredio.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()),
                                            "")
                            .toString();
            direccion = SysmanFunciones
                            .nvl(rsDatoPredio.getCampos()
                                            .get(GeneralParameterEnum.DIRECCION
                                                            .getName()),
                                            "")
                            .toString();
        }
        else if (padre == 1)
        {
            mensajeCuadro = idioma.getString("TB_TB1107")
                            .replace("s$codigoAnterior$s", codigoAnterior);
            estaPago = false;
            return estaPago;
        }
        else
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString(strTb755));
            estaPago = false;
            return estaPago;
        }
        return true;
    }

    private boolean genInformeCondicional()
    {
        boolean indicador = true;
        if (!numeroCertificado)
        {
            if (estaPago(codigoPredio, 0) && !debeCuotasAcuerdo())
            {
                indicador = validarPagosAldia();
            }
            else
            {
                indicador = false;
            }
        }
        return indicador;
    }

    private boolean validarPagosAldia()
    {
        if (!SysmanFunciones.validarVariableVacio(
                        codigoAnterior)
            && (!estaPago(codigoAnterior, 1)))
        {
            if (rsDatoPredio != null)
            {
                estaPago = false;
                mensajeCuadro = idioma.getString("TB_TB3304")
                                .replace("s$codigoAnterior$s", codigoAnterior);
                continuarVisible = true;
                return false;
            }
            else
            {
                continuarVisible = true;
                return false;
            }

        }
        return true;
    }

    public void getInformePlantilla()
    {
        boolean ind = true;
        if (validarCamposCertificado())
        {
            try
            {
                if ("999999999999999".equalsIgnoreCase(codigoPredio))
                {
                    JsfUtil.agregarMensajeInformativoDialogo(
                                    idioma.getString(
                                                    generarCertificadoANoRegistrados
                                                        ? strTb755
                                                        : "TB_TB756"));
                }
                else
                {
                    ind = genInformeCondicional();
                }
                if (ind)
                {
                    procesarCertificado();
                }
            }
            catch (NullPointerException ex)
            {
                JsfUtil.agregarMensajeError(
                                idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                    + ex.getMessage());
                Logger.getLogger(
                                PredialcertcatastralControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }

    }

    private String mostrarValor()
    {
        String mensaje = "''";
        try
        {
            String mostrarValor = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MOSTRAR VALOR EN PAZ Y SALVO",
                                            modulo, new Date(), true),
                                            "NO");
            if ("SI".equals(mostrarValor))
            {
                mensaje = " INITCAP('Valor') || ' ' || "
                    + (String) SysmanFunciones.nvl(String.valueOf(valor),
                                    "0")
                    + " || ' ' || INITCAP('Pesos Mcte') ";
            }
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return mensaje;
    }

    private void procesarPazYSalvo(HashMap<String, String> variablesConsultaW)
    {

        if (!numeroCertificado)
        {
            try
            {
                if (esInformativo)
                {
                    variablesConsultaW.put(strsNumcers, "'INFORMATIVO'");
                }
                else
                {

                    Date fechaPag = txtFechaPago == "" ? null : SysmanFunciones
                                    .convertirAFecha(txtFechaPago);

                    consultarConsecutivo("P");
                    Registro reg = new Registro();
                    reg.getCampos().put(
                                    PredialcertpazysalvoconpreControladorEnum.REC_VALORIZACION
                                                    .getValue(),
                                    recibo);
                    reg.getCampos().put(
                                    GeneralParameterEnum.DESTINO.getName(),
                                    destino);
                    reg.getCampos().put(
                                    PredialcertpazysalvoconpreControladorEnum.CODIGOPREDIO
                                                    .getValue(),
                                    codigoPredio);
                    reg.getCampos().put(
                                    PredialcertpazysalvoconpreControladorEnum.FECHA_EXP
                                                    .getValue(),
                                    fechaExpedicion);
                    reg.getCampos().put(
                                    GeneralParameterEnum.PAG_BAN.getName(),
                                    banco);
                    reg.getCampos().put(
                                    PredialcertpazysalvoconpreControladorEnum.PAG_FEC
                                                    .getValue(),
                                    fechaPag);
                    reg.getCampos().put("PAG_VAL", pagVal);
                    reg.getCampos().put(
                                    PredialcertpazysalvoconpreControladorEnum.PAGO_ANO
                                                    .getValue(),
                                    pagoAnio);

                    registrarCertificado(reg,
                                    PredialcertpazysalvoconpreControladorUrlEnum.URL4484
                                                    .getValue());

                    variablesConsultaW.put(strsNumcers, numCer);

                }

                variablesConsultaW.put("s$valor$s", "" + mostrarValor() + "");
            }
            catch (ParseException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else
        {
            variablesConsultaW.put(strsNumcers, numCer);
            variablesConsultaW.put("s$valor$s", "" + mostrarValor() + "");
        }

    }

    private void procesarValorizacion(
        HashMap<String, String> variablesConsultaW)
    {
        if (!numeroCertificado)
        {

            if (esInformativo)
            {
                variablesConsultaW.put(strsNumcers, "'INFORMATIVO'");
            }
            else
            {
                consultarConsecutivo("V");
                Registro reg = new Registro();
                reg.getCampos().put(GeneralParameterEnum.PREDIO.getName(),
                                codigoPredio);
                reg.getCampos().put("FECHA_EXPEDICION", fechaExpedicion);
                reg.getCampos().put(
                                GeneralParameterEnum.DESTINO.getName(),
                                destino);

                registrarCertificado(reg,
                                PredialcertpazysalvoconpreControladorUrlEnum.URL4485
                                                .getValue());

                variablesConsultaW.put(strsNumcers, numCer);
            }

        }
        else
        {
            variablesConsultaW.put(strsNumcers, numCer);
        }
    }

    private void procesarCertCatastral(
        HashMap<String, String> variablesConsultaW)
    {
        if (!numeroCertificado)
        {
            cargarListaCmbPropietario();
            variablesConsultaW.put("s$nit$s",
                            "'" + cedula
                                + "'");
            variablesConsultaW.put("s$expedida$s", "'" + expedida + "'");
            if (esInformativo)
            {
                variablesConsultaW.put(strsNumcers, "' '");
                variablesConsultaW.put("s$esInformativo$s",
                                "'" + idioma.getString("TB_TB3305") + "'");
            }
            else
            {
                consultarConsecutivo("C");
                Registro reg = new Registro();
                reg.getCampos().put(
                                PredialcertpazysalvoconpreControladorEnum.CODIGOPREDIO
                                                .getValue(),
                                codigoPredio);
                reg.getCampos().put(
                                PredialcertpazysalvoconpreControladorEnum.FECHA_EXP
                                                .getValue(),
                                fechaExpedicion);

                registrarCertificado(reg,
                                PredialcertpazysalvoconpreControladorUrlEnum.URL4486
                                                .getValue());

                variablesConsultaW.put(strsNumcers, numCer);
                variablesConsultaW.put("s$esInformativo$s", "' '");
            }

        }
        else
        {
            variablesConsultaW.put(strsNumcers, numCer);
        }
    }

    public void registrarCertificado(Registro reg, String urlServicio)
    {
        reg.getCampos().put(
                        GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        reg.getCampos().put(PredialcertpazysalvoconpreControladorEnum.NUMCER
                        .getValue(), numCer);
        reg.getCampos().put(GeneralParameterEnum.DIRECCION
                        .getName(), direccion);
        reg.getCampos().put(GeneralParameterEnum.UBICACION
                        .getName(), "0");
        reg.getCampos().put(PredialcertpazysalvoconpreControladorEnum.AREA_HA
                        .getValue(), areaHa);
        reg.getCampos().put(PredialcertpazysalvoconpreControladorEnum.AREA_M2
                        .getValue(), areaM);
        reg.getCampos().put(
                        GeneralParameterEnum.AVALUO.getName(),
                        avaluoAnio);
        reg.getCampos().put(PredialcertpazysalvoconpreControladorEnum.ANOAVALUO
                        .getValue(), pagoAnio);
        reg.getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        nombrePredio);
        reg.getCampos().put(PredialcertpazysalvoconpreControladorEnum.NIT
                        .getValue(), cedula);
        reg.getCampos().put(GeneralParameterEnum.NUMERO_ORDEN
                        .getName(), orden);
        reg.getCampos().put(
                        GeneralParameterEnum.VALOR.getName(),
                        valor);
        reg.getCampos().put(
                        GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        reg.getCampos().put(
                        GeneralParameterEnum.CREATED_BY
                                        .getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_CREATED
                        .getName(), new Date());
        try
        {
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            urlServicio);
            requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            reg.getCampos());
            cargarListaNumConsecutivo();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void procesarFecha()
    {
        if ("F_PZ_CONVIGENCIAS".equals(fechaConVigencias.substring(0, 17)))
        {
            String fec = fechaConVigencias.substring(0,
                            fechaConVigencias.length());
            fechaConVigencias = fec + "," + pagoAnio + ")";
        }
    }

    public void procesarCertificado()
    {
        try
        {
            fechaConVigencias = ejbSysmanUtil.consultarParametro(compania,
                            "FECHA VENCIMIENTO PAZ Y SALVO", modulo, new Date(),
                            true);

            if (!pagoAnio.isEmpty() && (fechaConVigencias.length() > 10))
            {
                procesarFecha();
            }
            String fechasDias = SysmanFunciones.convertirAFechaCadena(
                            SysmanFunciones.sumarRestarDiasFecha(new Date(),
                                            diasMas));
            String strNombreDocumento = "";
            String[] campos = new String[3];
            String[] valores = new String[3];
            String firma;
            String cargo;

            firma = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE TESORERO", modulo, new Date(),
                            true);
            cargo = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO TESORERO", modulo, new Date(),
                            true);

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$numeroOrden$s", "'" + orden + "'");
            variablesConsultaW.put("s$codigo$s", "'" + codigoPredio + "'");
            variablesConsultaW.put("s$diasVencimiento$s",
                            "'" + fechasDias + "'");
            variablesConsultaW.put("s$elaborado$s", "'" +
                SessionUtil.getUser().getCodigo() + "'");

            variablesConsultaW
                            .put("s$destino$s", "'"
                                + service.buscarEnLista(destino,
                                                GeneralParameterEnum.CODIGO
                                                                .getName(),
                                                "DESCRIPCION", listadestino)
                                + "'");
            variablesConsultaW.put("s$fechaConVigencias$s",
                            "'" + fechaConVigencias + "'");
            variablesConsultaW.put("s$copropietarios$s",
                            indCopropietarios
                                ? "'El predio se encuentra registrado con los siguientes propietarios:'"
                                : "''");
            variablesConsultaW.put("s$companiaProp$s",
                            indCopropietarios ? "'" + compania + "'" : "'-1'");
            variablesConsultaW.put("s$firma$s", "'" + firma + "'");
            variablesConsultaW.put("s$cargo$s", "'" + cargo + "'");

            switch (tipoCertificado)
            {
            case "1":
                procesarPazYSalvo(variablesConsultaW);
                strNombreDocumento = "certificadoPazYSalvo" + SysmanFunciones
                                .convertirAFechaCadena(new Date(),
                                                strFormatoFecha);
                break;
            case "2":
                procesarValorizacion(variablesConsultaW);
                strNombreDocumento = "certificadoValorizacion"
                    + SysmanFunciones.convertirAFechaCadena(new Date(),
                                    strFormatoFecha);
                break;
            case "3":
                procesarCertCatastral(variablesConsultaW);
                strNombreDocumento = "certificadoCatastral"
                    + SysmanFunciones.convertirAFechaCadena(new Date(),
                                    strFormatoFecha);
                break;
            default:
                break;
            }
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = plantilla;
            valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
            valores[2] = strNombreDocumento;

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(),
                            campos, valores);
        }
        catch (NullPointerException | ParseException | SystemException e)
        {
            Logger.getLogger(PredialcertpazysalvoconpreControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1302"));
            return;
        }

    }

    private boolean evaluaNulos()
    {
        boolean validar = false;
        if (SysmanFunciones.validarVariableVacio(expedida)
            || (fechaExpedicion == null))
        {
            validar = true;
        }

        if (SysmanFunciones.validarVariableVacio(destino)
            || SysmanFunciones.validarVariableVacio(String.valueOf(valor)))
        {
            validar = true;
        }
        return validar;
    }

    public boolean validarCamposCertificado()
    {
        listaplantilla.load();
        if (listaplantilla.getDatasource().isEmpty())
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB748"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(plantilla))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB763"));
            return false;
        }

        if (SysmanFunciones.validarVariableVacio(codigoPredio))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB764"));
            return false;
        }
        if (evaluaNulos())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB765"));
            return false;
        }

        cargarListacodigopredio();

        Map<String, Object> params = new HashMap<>();
        params.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
        Registro rs = null;
        try
        {
            rs = listacodigopredio.getRegistroUnico(params);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return validarPredio(rs);

    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        indCopropietarios = true;
        valor = valor();
        bloqmprimirCopia = false;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public boolean getCopropietarios()
    {
        return indCopropietarios;
    }

    public void setCopropietarios(boolean copropietarios)
    {
        this.indCopropietarios = copropietarios;
    }

    public boolean getIndExento()
    {
        return indExento;
    }

    public void setIndExento(boolean indExento)
    {
        this.indExento = indExento;
    }

    public boolean getIndCertificado()
    {
        return indCertificado;
    }

    public void setIndCertificado(boolean indCertificado)
    {
        this.indCertificado = indCertificado;
    }

    public boolean getIndPazySalvo()
    {
        return indPazySalvo;
    }

    public void setIndPazySalvo(boolean indPazySalvo)
    {
        this.indPazySalvo = indPazySalvo;
    }

    public boolean getIndValorizacion()
    {
        return indValorizacion;
    }

    public void setIndValorizacion(boolean indValorizacion)
    {
        this.indValorizacion = indValorizacion;
    }

    public boolean getIndImprimirCopia()
    {
        return indImprimirCopia;
    }

    public void setIndImprimirCopia(boolean indImprimirCopia)
    {
        this.indImprimirCopia = indImprimirCopia;
    }

    public boolean getIndExe()
    {
        return indExe;
    }

    public void setIndExe(boolean indExe)
    {
        this.indExe = indExe;
    }

    public String getCodigoPredio()
    {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio)
    {
        this.codigoPredio = codigoPredio;
    }

    public String getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getDestino()
    {
        return destino;
    }

    public void setDestino(String destino)
    {
        this.destino = destino;
    }

    public String getRecibo()
    {
        return recibo;
    }

    public void setRecibo(String recibo)
    {
        this.recibo = recibo;
    }

    public String getExpedida()
    {
        return expedida;
    }

    public void setExpedida(String expedida)
    {
        this.expedida = expedida;
    }

    public String getNumCer()
    {
        return numCer;
    }

    public void setNumCer(String numCer)
    {
        this.numCer = numCer;
    }

    public Date getFechaExpedicion()
    {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(Date fechaExpedicion)
    {
        this.fechaExpedicion = fechaExpedicion;
    }

    public double getValor()
    {
        return valor;
    }

    public void setValor(double valor)
    {
        this.valor = valor;
    }

    public String getNombrePredio()
    {
        return nombrePredio;
    }

    public void setNombrePredio(String nombrePredio)
    {
        this.nombrePredio = nombrePredio;
    }

    public String getObservaciones()
    {
        return observaciones;
    }

    public void setObservaciones(String observaciones)
    {
        this.observaciones = observaciones;
    }

    public String getDireccion()
    {
        return direccion;
    }

    public void setDireccion(String direccion)
    {
        this.direccion = direccion;
    }

    public String getCedula()
    {
        return cedula;
    }

    public void setCedula(String cedula)
    {
        this.cedula = cedula;
    }

    public String getCodPredio()
    {
        return codPredio;
    }

    public void setCodPredio(String codPredio)
    {
        this.codPredio = codPredio;
    }

    public String getNomPredio()
    {
        return nomPredio;
    }

    public void setNomPredio(String nomPredio)
    {
        this.nomPredio = nomPredio;
    }

    public String getNoRecibo()
    {
        return noRecibo;
    }

    public void setNoRecibo(String noRecibo)
    {
        this.noRecibo = noRecibo;
    }

    public String getObservaciones1()
    {
        return observaciones1;
    }

    public void setObservaciones1(String observaciones1)
    {
        this.observaciones1 = observaciones1;
    }

    public String getTipoCertificado()
    {
        return tipoCertificado;
    }

    public void setTipoCertificado(String tipoCertificado)
    {
        this.tipoCertificado = tipoCertificado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListadestino()
    {
        return listadestino;
    }

    public void setListadestino(List<Registro> listadestino)
    {
        this.listadestino = listadestino;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListacodigopredio()
    {
        return listacodigopredio;
    }

    public void setListacodigopredio(RegistroDataModelImpl listacodigopredio)
    {
        this.listacodigopredio = listacodigopredio;
    }

    public RegistroDataModelImpl getListaNumConsecutivo()
    {
        return listaNumConsecutivo;
    }

    public void setListaNumConsecutivo(
        RegistroDataModelImpl listaNumConsecutivo)
    {
        this.listaNumConsecutivo = listaNumConsecutivo;
    }

    public RegistroDataModelImpl getListaCmbPropietario()
    {
        return listaCmbPropietario;
    }

    public void setListaCmbPropietario(
        RegistroDataModelImpl listaCmbPropietario)
    {
        this.listaCmbPropietario = listaCmbPropietario;
    }

    public RegistroDataModelImpl getListaRecibo()
    {
        return listaRecibo;
    }

    public void setListaRecibo(RegistroDataModelImpl listaRecibo)
    {
        this.listaRecibo = listaRecibo;
    }

    public RegistroDataModelImpl getListaplantilla()
    {
        return listaplantilla;
    }

    public void setListaplantilla(RegistroDataModelImpl listaplantilla)
    {
        this.listaplantilla = listaplantilla;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>

    public boolean isGeneraReciboDePagoParaPazYSalvo()
    {
        return generaReciboDePagoParaPazYSalvo;
    }

    public void setGeneraReciboDePagoParaPazYSalvo(
        boolean generaReciboDePagoParaPazYSalvo)
    {
        this.generaReciboDePagoParaPazYSalvo = generaReciboDePagoParaPazYSalvo;
    }

    public boolean isGenerarPazYSalvoAExcentos()
    {
        return generarPazYSalvoAExcentos;
    }

    public void setGenerarPazYSalvoAExcentos(
        boolean generarPazYSalvoAExcentos)
    {
        this.generarPazYSalvoAExcentos = generarPazYSalvoAExcentos;
    }

    public boolean isGenerarCertificadoANoRegistrados()
    {
        return generarCertificadoANoRegistrados;
    }

    public void setGenerarCertificadoANoRegistrados(
        boolean generarCertificadoANoRegistrados)
    {
        this.generarCertificadoANoRegistrados = generarCertificadoANoRegistrados;
    }

    public boolean isPazYSalvoEspecial()
    {
        return pazYSalvoEspecial;
    }

    public void setPazYSalvoEspecial(boolean pazYSalvoEspecial)
    {
        this.pazYSalvoEspecial = pazYSalvoEspecial;
    }

    public boolean isManejaTiposDePazYSalvos()
    {
        return manejaTiposDePazYSalvos;
    }

    public void setManejaTiposDePazYSalvos(boolean manejaTiposDePazYSalvos)
    {
        this.manejaTiposDePazYSalvos = manejaTiposDePazYSalvos;
    }

    public boolean isGenerarPazYSalvoAOtrosPropietarios()
    {
        return generarPazYSalvoAOtrosPropietarios;
    }

    public void setGenerarPazYSalvoAOtrosPropietarios(
        boolean generarPazYSalvoAOtrosPropietarios)
    {
        this.generarPazYSalvoAOtrosPropietarios = generarPazYSalvoAOtrosPropietarios;
    }

    public int getDiasMas()
    {
        return diasMas;
    }

    public void setDiasMas(int diasMas)
    {
        this.diasMas = diasMas;
    }

    public boolean isVisibleNumConsecutivo()
    {
        return visibleNumConsecutivo;
    }

    public void setVisibleNumConsecutivo(boolean visibleNumConsecutivo)
    {
        this.visibleNumConsecutivo = visibleNumConsecutivo;
    }

    public boolean isBloqueadoDestino()
    {
        return bloqueadoDestino;
    }

    public void setBloqueadoDestino(boolean bloqueadoDestino)
    {
        this.bloqueadoDestino = bloqueadoDestino;
    }

    public boolean isBloqueadoNumConsecutivo()
    {
        return bloqueadoNumConsecutivo;
    }

    public void setBloqueadoNumConsecutivo(boolean bloqueadoNumConsecutivo)
    {
        this.bloqueadoNumConsecutivo = bloqueadoNumConsecutivo;
    }

    public boolean isBloqueadoImprimirCopia()
    {
        return bloqueadoImprimirCopia;
    }

    public void setBloqueadoImprimirCopia(boolean bloqueadoImprimirCopia)
    {
        this.bloqueadoImprimirCopia = bloqueadoImprimirCopia;
    }

    public boolean isBloqueadoCodigoPredio()
    {
        return bloqueadoCodigoPredio;
    }

    public void setBloqueadoCodigoPredio(boolean bloqueadoCodigoPredio)
    {
        this.bloqueadoCodigoPredio = bloqueadoCodigoPredio;
    }

    public String getOrden()
    {
        return orden;
    }

    public void setOrden(String orden)
    {
        this.orden = orden;
    }

    public boolean isIndCopropietarios()
    {
        return indCopropietarios;
    }

    public void setIndCopropietarios(boolean indCopropietarios)
    {
        this.indCopropietarios = indCopropietarios;
    }

    public int getNumeroDePazYSalvosExcentos()
    {
        return numeroDePazYSalvosExcentos;
    }

    public void setNumeroDePazYSalvosExcentos(int numeroDePazYSalvosExcentos)
    {
        this.numeroDePazYSalvosExcentos = numeroDePazYSalvosExcentos;
    }

    public String getTxtFactura()
    {
        return txtFactura;
    }

    public void setTxtFactura(String txtFactura)
    {
        this.txtFactura = txtFactura;
    }

    public String getTxtFechaPago()
    {
        return txtFechaPago;
    }

    public void setTxtFechaPago(String txtFechaPago)
    {
        this.txtFechaPago = txtFechaPago;
    }

    public String getTipoPlantilla()
    {
        return tipoPlantilla;
    }

    public void setTipoPlantilla(String tipoPlantilla)
    {
        this.tipoPlantilla = tipoPlantilla;
    }

    public String getPlantilla()
    {
        return plantilla;
    }

    public void setPlantilla(String plantilla)
    {
        this.plantilla = plantilla;
    }

    public Date getFechaPlantilla()
    {
        return fechaPlantilla;
    }

    public void setFechaPlantilla(Date fechaPlantilla)
    {
        this.fechaPlantilla = fechaPlantilla;
    }

    public String getPagoAnio()
    {
        return pagoAnio;
    }

    public void setPagoAnio(String pagoAnio)
    {
        this.pagoAnio = pagoAnio;
    }

    public String getFechaConVigencias()
    {
        return fechaConVigencias;
    }

    public void setFechaConVigencias(String fechaConVigencias)
    {
        this.fechaConVigencias = fechaConVigencias;
    }

    public boolean isContinuarVisible()
    {
        return continuarVisible;
    }

    public void setContinuarVisible(boolean continuarVisible)
    {
        this.continuarVisible = continuarVisible;
    }

    public boolean isEstaPago()
    {
        return estaPago;
    }

    public void setEstaPago(boolean estaPago)
    {
        this.estaPago = estaPago;
    }

    public boolean isIndBorrado()
    {
        return indBorrado;
    }

    public void setIndBorrado(boolean indBorrado)
    {
        this.indBorrado = indBorrado;
    }

    public String getCodigoAnterior()
    {
        return codigoAnterior;
    }

    public void setCodigoAnterior(String codigoAnterior)
    {
        this.codigoAnterior = codigoAnterior;
    }

    public String getMensajeCuadro()
    {
        return mensajeCuadro;
    }

    public void setMensajeCuadro(String mensajeCuadro)
    {
        this.mensajeCuadro = mensajeCuadro;
    }

    public boolean isEsInformativo()
    {
        return esInformativo;
    }

    public void setEsInformativo(boolean esInformativo)
    {
        this.esInformativo = esInformativo;
    }

    public boolean isBloqmprimirCopia()
    {
        return bloqmprimirCopia;
    }

    public void setBloqmprimirCopia(boolean bloqmprimirCopia)
    {
        this.bloqmprimirCopia = bloqmprimirCopia;
    }

    // </SET_GET_ADICIONALES>
}
