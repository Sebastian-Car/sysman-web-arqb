package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialFinRemote;
import com.sysman.predial.enums.CrearAcuerdosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 27/06/2016
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambió el llamado del código del
 * formulario y actualización de ConnectorPool
 * 
 * @author eamaya
 * @version 3.0, 27/06/2017 Proceso de Refactoring, Manejo de EJBs y
 * correcciones SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class CrearAcuerdosControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private String modulo;
    /**
     * Constante definida por el numero de veces que se llama la tabla
     * IP_FACTURADOS
     */
    private final String strIpFacturados;

    /**
     * Constante que almacena el nombre de parámetro TASA INTERES
     * FINANCIACION DE ACUERDO
     */
    private final String parTasaInteresFinanAcuerdo;

    /**
     * Constanque que almacena el nombre del parámetro TASA INTERES
     * RECARGO DE ACUERDO
     */
    private final String parTasaInteresRecarAcuerdo;
    /**
     * Constante que almacena el nombre del parámetro TITULO OPCION
     * DESCUENTOS ESPECIALES
     */

    private final String parTituloOpcDescEpeciales;
    // <DECLARAR_ATRIBUTOS>
    private boolean indAbonoInicial;
    private boolean indDescEsp;
    private String periodicidadAcuerdo;
    private String nroCuotasAcuerdo;
    private String numRecibo;
    private String codigoPredio;
    private String idRespAcuerdo;
    private String nomRespAcuerdo;
    private String dirRespAcuerdo;
    private String telRespAcuerdo;
    private String intFinanciacion;
    private String intRecargo;
    private String nroResAcuerdo;
    private String nomPropietario;
    private String nroAcuerdo;
    private String vlrAbonoInicial;
    private String tituloLeyDesc;
    private String totalDeuda;
    private String totalFinanciar;
    private Double totalDeudaM;
    private Double totalFinanciarM;
    private boolean verDescEsp;
    private boolean perModificar;
    private boolean perModificarVA;
    private boolean verReciboAcuerdo;

    private boolean cuotasAcuerdoSinInteres;
    private boolean manejaTasaVigente;
    private boolean manejaDescuentosEspeciales;
    private String manejaIndicadorDesc;
    private boolean manejaTasaFinanciacion;
    private boolean manejaControlReciboAcuerdo;
    private String manejaTituloDescuentosEspeciales;
    private boolean manejaCuotasVigenciasAdeudadas;
    private boolean preeliminar;
    private boolean bloqueadoCuotas;

    /**
     * Atributo utilizado para validar la visibilidad del cuadro de
     * confirmacion de la funcion
     */
    private boolean visibleDialogo;
    /**
     * Variable que alamacena el texto que tendra el cuadro de dialogo
     */
    private String textoDialogo;

    // variable utilizada en la funcion FC_CREARACUERDO, se espera su
    // desarrollo para asignar su valor
    private boolean acuerdoPasto;
    private String porcMinAbono;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaReciboSoporte;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaCrearacuerdosub;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSub;
    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPredialFinRemote ejbPredialFin;

    // </DECLARAR_ADICIONALES>
    public CrearAcuerdosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strIpFacturados = "IP_FACTURADOS";
        bloqueadoCuotas = false;
        parTasaInteresFinanAcuerdo = "TASA INTERES FINANCIACION DE ACUERDO";
        parTasaInteresRecarAcuerdo = "TASA INTERES RECARGO DE ACUERDO";
        parTituloOpcDescEpeciales = "TITULO OPCION DESCUENTOS ESPECIALES";
        try {
            numFormulario = GeneralCodigoFormaEnum.CREAR_ACUERDOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                codigoPredio = (String) parametrosEntrada.get("codigoPredio");
                nomPropietario = (String) parametrosEntrada
                                .get("nomPropietario");
            }
            else {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(CrearAcuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ejecutarrcAbrir();
        try {

            manDescEsp();

            String perModifTasas = ejbSysmanUtil.consultarParametro(compania,
                            "PERMITE MODIFICAR TASAS DE FINANCIACION", modulo,
                            new Date(), false);

            perModifTasas = perModifTasas == null ? "NO" : perModifTasas;
            if ("SI".equals(perModifTasas)) {
                perModificar = true;
            }
            else {
                perModificar = false;
            }

            // asignar los valores para las tasas configuradas por
            // parï¿½metros del sistema
            intFinanciacion = ejbSysmanUtil.consultarParametro(
                            compania, parTasaInteresFinanAcuerdo,
                            modulo,
                            new Date(), false);

            intFinanciacion = intFinanciacion == null ? "0" : intFinanciacion;
            intRecargo = ejbSysmanUtil.consultarParametro(compania,
                            parTasaInteresRecarAcuerdo, modulo,
                            new Date(), false);

            intRecargo = intRecargo == null ? "0" : intRecargo;

            acuerdoVigAdeudas();

            porcMinAbono = ejbSysmanUtil.consultarParametro(
                            compania,
                            "PORCENTAJE MINIMO DE ABONO EN FINANCIACION",
                            modulo,
                            new Date(), false);

            String parAcuerdoTasaVigente = ejbSysmanUtil.consultarParametro(
                            compania, "ACUERDOS PAGO - USAR TASA VIGENTE",
                            modulo,
                            new Date(), false);

            manejaTasaVigente = ("SI").equals(parAcuerdoTasaVigente);

            String parTasaFin = ejbSysmanUtil.consultarParametro(compania,
                            parTasaInteresFinanAcuerdo, modulo,
                            new Date(), false);

            String parTasaInteres = ejbSysmanUtil.consultarParametro(
                            compania, parTasaInteresRecarAcuerdo, modulo,
                            new Date(), false);

            if (manejaTasaVigente) {
                intFinanciacion = ejbPredialFin.obtenerFechaFinalAcuerdo()
                                .toString();

                intRecargo = intFinanciacion;
            }
            else {
                intFinanciacion = parTasaFin;
                intRecargo = parTasaInteres;
            }

        }
        catch (SystemException e) {
            Logger.getLogger(CrearAcuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }
        cargarParametros();
        validarCampos();
        // </CODIGO_DESARROLLADO>
    }

    private void ejecutarrcAbrir() {

        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
            parametros.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(parametros);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CrearAcuerdosControladorUrlEnum.URL11795
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            Logger.getLogger(CrearAcuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }

        cargarListaCrearacuerdosub();

    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        try {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
            parametros.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(parametros);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CrearAcuerdosControladorUrlEnum.URL11795
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
        }
        catch (SystemException e) {
            Logger.getLogger(CrearAcuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.USUARIOSPREDIALS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void validarCampos() {

        if (manejaDescuentosEspeciales) {
            verDescEsp = true;
            if ("1".equals(manejaIndicadorDesc)) {
                indDescEsp = true;
            }
            else {
                indDescEsp = false;
            }
            tituloLeyDesc = SysmanFunciones
                            .nvlStr(manejaTituloDescuentosEspeciales, ".");
        }
        else {
            verDescEsp = false;
        }

        if (manejaTasaFinanciacion) {
            perModificar = true;
        }
        else {
            perModificar = false;
        }
        if (manejaControlReciboAcuerdo) {
            verReciboAcuerdo = true;
            JsfUtil.agregarMensajeInformativoDialogo(
                            idioma.getString("TB_TB2850"));
        }
        else {
            verReciboAcuerdo = false;
        }
        try {

            Registro rs = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CrearAcuerdosControladorUrlEnum.URL34735
                                                                                            .getValue())
                                                            .getUrl(),
                                                            null));

            if (rs != null) {
                int num = Integer.parseInt(SysmanFunciones.nvlStr(
                                rs.getCampos().get("ULTIMOACUERDO").toString(),
                                "0"))
                    + 1;
                nroAcuerdo = SysmanFunciones.strZero(Integer.toString(num), 10);

            }
            else {
                nroAcuerdo = SysmanFunciones.strZero("1", 10);

            }
            if (manejaCuotasVigenciasAdeudadas) {
                intFinanciacion = "0";
                intRecargo = "0";
                perModificar = false;
                perModificarVA = false;
                nroCuotasAcuerdo = "1";
                bloqueadoCuotas = true;
            }

            if (manejaTasaVigente) {
                intFinanciacion = ejbPredialFin.obtenerFechaFinalAcuerdo()
                                .toString();

                intRecargo = intFinanciacion;
            }
            else {
                intFinanciacion = ejbSysmanUtil.consultarParametro(compania,
                                parTasaInteresFinanAcuerdo, modulo,
                                new Date(), false);

                intRecargo = ejbSysmanUtil.consultarParametro(compania,
                                parTasaInteresRecarAcuerdo,
                                modulo, new Date(), false);

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarParametros() {
        try {
            String parManejaDescuentosEspeciales = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "MANEJA OPCION DESCUENTOS ESPECIALES",
                                            modulo,
                                            new Date(), false);

            manejaDescuentosEspeciales = ("SI")
                            .equals(parManejaDescuentosEspeciales);

            manejaIndicadorDesc = ejbSysmanUtil.consultarParametro(
                            compania,
                            "VALOR PREDETERMINADO INDICADOR OPCION DESC ESP",
                            modulo, new Date(), false);

            manejaTituloDescuentosEspeciales = ejbSysmanUtil.consultarParametro(
                            compania, parTituloOpcDescEpeciales,
                            modulo, new Date(), false);

            String parManejaTasaFinanciacion = ejbSysmanUtil.consultarParametro(
                            compania, "PERMITE MODIFICAR TASAS DE FINANCIACION",
                            modulo, new Date(), false);

            manejaTasaFinanciacion = ("SI").equals(parManejaTasaFinanciacion);

            String parManejaControlReciboAcuerdo = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "MANEJA CONTROL DE RECIBO PARA ACUERDOS DE PAGO",
                                            modulo,
                                            new Date(), false);

            manejaControlReciboAcuerdo = ("SI")
                            .equals(parManejaControlReciboAcuerdo);

            String parManejaCuotasVigenciasAdeudadas = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "TRABAJA CUOTAS ACUERDO SEGUN VIGENCIAS ADEUDADAS",
                                            modulo,
                                            new Date(), false);

            manejaCuotasVigenciasAdeudadas = ("SI")
                            .equals(parManejaCuotasVigenciasAdeudadas);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaReciboSoporte();
        cargarListaCrearacuerdosub();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaCrearacuerdosub();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaCrearacuerdosub = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        asignarOrigenDatos();
        iniciarListas();
        actualizarTotales();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    public void cargarListaCrearacuerdosub() {

        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);

            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            listaCrearacuerdosub = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CrearAcuerdosControladorUrlEnum.URL20280
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            strIpFacturados));
        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(CrearAcuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaReciboSoporte() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CrearAcuerdosControladorUrlEnum.URL21478
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoPredio);

        listaReciboSoporte = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "DOCNUM");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * aceptarconfirmarFuncion en la vista
     */
    public void aceptarconfirmarFuncion() {

        if (!preeliminar) {
            int financiar = crearAcuerdo();

            if (financiar == 1) {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2837"));
                ejecutarrcCerrar();
            }
            else
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2838"));
        }
        else {
            int genReporte = crearAcuerdo();

            if (genReporte == 1) {

                generarReporte(FORMATOS.PDF);
            }
        }
        visibleDialogo = false;
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * cancelarconfirmarFuncion en la vista
     */
    public void cancelarconfirmarFuncion() {
        visibleDialogo = false;
    }

    /**
     * Metodo ejecutado al cambiar el control TxtNCuotas
     * 
     */
    public void cambiarTxtNCuotas() {
        // <CODIGO_DESARROLLADO>
        boolean manejaAcuerdoSinInteres;
        try {
            String parManejaAcuerdoSinInteres = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "MANEJA ACUERDO SIN INTERES",
                                            modulo,
                                            new Date(), false);

            manejaAcuerdoSinInteres = ("SI").equals(parManejaAcuerdoSinInteres);

            String parCuotasAcuerdoSinInteres = ejbSysmanUtil
                            .consultarParametro(compania,
                                            "CUOTAS DE ACUERDO SIN INTERES",
                                            modulo,
                                            new Date(), false);

            String parAcuerdoTasaVigente = ejbSysmanUtil.consultarParametro(
                            compania, "ACUERDOS PAGO - USAR TASA VIGENTE",
                            modulo, new Date(), false);

            manejaTasaVigente = ("SI").equals(parAcuerdoTasaVigente);

            String parTasaVigente = ejbSysmanUtil.consultarParametro(
                            compania, "TASA DE INTERES VIGENTE",
                            modulo, new Date(), false);

            if (manejaAcuerdoSinInteres
                && Integer.parseInt(nroCuotasAcuerdo) <= Integer
                                .parseInt(parCuotasAcuerdoSinInteres)) {
                if ("8912800003".equals(
                                SessionUtil.getCompaniaIngreso().getNit())) {

                    HashMap<String, Object> param = new HashMap<>();

                    param.put(GeneralParameterEnum.CODIGO.getName(),
                                    codigoPredio);

                    Registro rs = RegistroConverter
                                    .toRegistro(requestManager
                                                    .get(UrlServiceUtil
                                                                    .getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    CrearAcuerdosControladorUrlEnum.URL13585
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                                    param));

                    int anio = SysmanFunciones.getParteFecha(new Date(),
                                    Calendar.YEAR);

                    if (rs != null) {
                        int anioMax = Integer.parseInt(rs.getCampos()
                                        .get("PREANOMAX").toString());
                        int vigencias = Integer.parseInt(rs.getCampos()
                                        .get("VIGENCIAS").toString());

                        if (anioMax == anio && vigencias == 1) {
                            intFinanciacion = "0";
                            intRecargo = "0";
                            periodicidadAcuerdo = "3";
                            acuerdoPasto = true;
                        }
                        else {
                            if (manejaTasaVigente) {
                                intFinanciacion = ejbPredialFin
                                                .obtenerFechaFinalAcuerdo()
                                                .toString();

                                intRecargo = intFinanciacion;
                            }
                            else {
                                intFinanciacion = parTasaVigente;
                                intRecargo = parTasaVigente;
                            }
                            periodicidadAcuerdo = "1";
                            acuerdoPasto = false;
                        }
                    }
                    else {
                        JsfUtil.agregarMensajeAlerta(
                                        idioma.getString("TB_TB2848"));
                    }

                }
                else {
                    intFinanciacion = "0";
                    intRecargo = "0";
                }
            }

            if ("891.808.260-0".equals(
                            SessionUtil.getCompaniaIngreso().getNit())) {
                // preeliminar=0
                // PENDIENTE FUNCION Calpred_Usuario
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarAbonoInicial() {
        // Acciones al cambiar el campo de Abono Inicial
    }

    public void cambiarValorAbonoInicial() {
        try {
            if (totalDeudaM > 0) {
                porcMinAbono = porcMinAbono == null ? "0" : porcMinAbono;
                Double porcAbono = Double.parseDouble(porcMinAbono);
                if ((porcAbono < 0) || (porcAbono > 100)) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB27"));
                    vlrAbonoInicial = "0";
                }
                Double vlrMinAbono = SysmanFunciones
                                .redondear(totalDeudaM * (porcAbono / 100), -3);
                if (Double.parseDouble(vlrAbonoInicial) < vlrMinAbono) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB21")
                        + " " + porcAbono + " " + idioma.getString("TB_TB22")
                        + " " + vlrMinAbono + " ."
                        + idioma.getString("TB_TB2869"));
                    vlrAbonoInicial = "0";
                }
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB23"));
                vlrAbonoInicial = "0";
            }
        }

        catch (NumberFormatException ex) {
            Logger.getLogger(CrearAcuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB27"));
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaReciboSoporte(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numRecibo = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get("DOCNUM").toString(), "");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirCmdFinanciar() {
        // <CODIGO_DESARROLLADO>
        preeliminar = false;
        visibleDialogo = true;
        textoDialogo = idioma.getString("TB_TB3086");

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdPrevio() {
        // <CODIGO_DESARROLLADO>
        preeliminar = true;
        visibleDialogo = true;
        textoDialogo = idioma.getString("TB_TB3087");

        // </CODIGO_DESARROLLADO>
    }

    public int crearAcuerdo() {

        int rta = 0;

        if (SysmanFunciones.validarVariableVacio(numRecibo)) {
            numRecibo = "0";
        }
        if (SysmanFunciones.validarVariableVacio(vlrAbonoInicial)) {
            vlrAbonoInicial = "0";
        }

        try {

            rta = ejbPredialFin.crearAcuerdo(compania,
                            SessionUtil.getCompaniaIngreso().getNombre(),
                            codigoPredio,
                            Integer.parseInt(periodicidadAcuerdo),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            idRespAcuerdo,
                            nomRespAcuerdo,
                            dirRespAcuerdo,
                            telRespAcuerdo,
                            Integer.parseInt(nroCuotasAcuerdo),
                            new BigDecimal(intFinanciacion),
                            new BigDecimal(intRecargo),
                            nroResAcuerdo,
                            SessionUtil.getUser().getCodigo(),
                            numRecibo,
                            indDescEsp,
                            preeliminar,
                            indAbonoInicial,
                            vlrAbonoInicial,
                            SessionUtil.getCompaniaIngreso().getNit(),
                            acuerdoPasto);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    public void generarReporte(ReportesBean.FORMATOS formato) {
        // este reporte se usa para el proceso previo y el definitivo
        // con diferentes tablas. Cuando se implemente la lï¿½gica se
        // debe ajustar la generaciï¿½n del reporte

        archivoDescarga = null;
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoAcuerdo", nroAcuerdo);
            parametros.put("PR_NOMBRE_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_FECHA", SysmanFunciones.hoy());
            parametros.put("PR_USUARIOS", SessionUtil.getUser().getApellido1());
            Reporteador.resuelveConsulta("000954TMPDISTRIBUCIONACU",
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000954TMPDISTRIBUCIONACU", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubCrearacuerdosub() {
        // METODO_NO_IMPLEMENTADO
    }

    public void editarRegSubCrearacuerdosub(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            insertarSubAntes(reg);

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            CrearAcuerdosControladorUrlEnum.URL10838
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            actualizarTotales();
            cargarListaCrearacuerdosub();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(CrearAcuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void eliminarRegSubCrearacuerdosub(Registro reg) {
        // METODO_NO_IMPLEMENTADO
    }

    public void cancelarEdicionCrearacuerdosub() {
        cargarListaCrearacuerdosub();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    public void actualizarTotales() {
        try {

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), codigoPredio);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            Registro regAux = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CrearAcuerdosControladorUrlEnum.URL36022
                                                                                            .getValue())
                                                            .getUrl(),
                                                            param));

            totalDeuda = regAux.getCampos().get("TOTALDEUDA").toString();
            totalFinanciar = regAux.getCampos().get("TOTALFINANCIAR")
                            .toString();
            totalDeudaM = Double.parseDouble(
                            regAux.getCampos().get("TOTALDEUDASF").toString());
            totalFinanciarM = Double.parseDouble(
                            regAux.getCampos().get("TOTALDEUDASF").toString());
        }
        catch (Exception ex) {
            totalDeuda = "0";
            totalFinanciar = "0";
            totalDeudaM = 0.0;
            totalFinanciarM = 0.0;
            Logger.getLogger(CrearAcuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    // </METODOS_ADICIONALES>
    private void manDescEsp() {
        String manDescEsp;
        try {
            manDescEsp = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA OPCION DESCUENTOS ESPECIALES",
                            modulo, new Date(), false);

            manDescEsp = manDescEsp == null ? "NO" : manDescEsp;
            if ("SI".equals(manDescEsp)) {
                verDescEsp = true;
                String vlrInd1175 = ejbSysmanUtil.consultarParametro(compania,
                                "VALOR PREDETERMINADO INDICADOR OPCION DESC ESP",
                                modulo, new Date(), false);

                vlrInd1175 = vlrInd1175 == null ? "0" : vlrInd1175;
                indDescEsp = "0".equals(vlrInd1175) ? false : true;
                tituloLeyDesc = ejbSysmanUtil.consultarParametro(compania,
                                parTituloOpcDescEpeciales,
                                modulo, new Date(), false);

                tituloLeyDesc = tituloLeyDesc == null
                    ? parTituloOpcDescEpeciales : tituloLeyDesc;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void acuerdoVigAdeudas() {
        String acuerdoVigAdeudadas;
        try {
            acuerdoVigAdeudadas = ejbSysmanUtil.consultarParametro(
                            compania,
                            "TRABAJA CUOTAS ACUERDO SEGUN VIGENCIAS ADEUDADAS",
                            modulo,
                            new Date(), false);

            acuerdoVigAdeudadas = acuerdoVigAdeudadas == null ? "NO"
                : acuerdoVigAdeudadas;
            if ("SI".equals(acuerdoVigAdeudadas)) {
                perModificarVA = true;
                verReciboAcuerdo = true;
                intRecargo = "0";
                intFinanciacion = "0";
            }
            else {
                perModificarVA = false;
                verReciboAcuerdo = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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

    public void insertarSubAntes(Registro reg) {
        reg.getCampos().remove("PREANO");
        reg.getCampos().remove("IMPUESTO");
        reg.getCampos().remove("INTERESES");
        reg.getCampos().remove("OTROS");
        reg.getCampos().remove("TOTAL");
    }

    // <SET_GET_ATRIBUTOS>
    public boolean getIndAbonoInicial() {
        return indAbonoInicial;
    }

    public void setIndAbonoInicial(boolean indAbonoInicial) {
        this.indAbonoInicial = indAbonoInicial;
    }

    public boolean getIndDescEsp() {
        return indDescEsp;
    }

    public void setIndDescEsp(boolean indDescEsp) {
        this.indDescEsp = indDescEsp;
    }

    public String getPeriodicidadAcuerdo() {
        return periodicidadAcuerdo;
    }

    public void setPeriodicidadAcuerdo(String periodicidadAcuerdo) {
        this.periodicidadAcuerdo = periodicidadAcuerdo;
    }

    public String getNroCuotasAcuerdo() {
        return nroCuotasAcuerdo;
    }

    public void setNroCuotasAcuerdo(String nroCuotasAcuerdo) {
        this.nroCuotasAcuerdo = nroCuotasAcuerdo;
    }

    public String getNumRecibo() {
        return numRecibo;
    }

    public void setNumRecibo(String numRecibo) {
        this.numRecibo = numRecibo;
    }

    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public String getIdRespAcuerdo() {
        return idRespAcuerdo;
    }

    public void setIdRespAcuerdo(String idRespAcuerdo) {
        this.idRespAcuerdo = idRespAcuerdo;
    }

    public String getNomRespAcuerdo() {
        return nomRespAcuerdo;
    }

    public void setNomRespAcuerdo(String nomRespAcuerdo) {
        this.nomRespAcuerdo = nomRespAcuerdo;
    }

    public String getDirRespAcuerdo() {
        return dirRespAcuerdo;
    }

    public void setDirRespAcuerdo(String dirRespAcuerdo) {
        this.dirRespAcuerdo = dirRespAcuerdo;
    }

    public String getTelRespAcuerdo() {
        return telRespAcuerdo;
    }

    public void setTelRespAcuerdo(String telRespAcuerdo) {
        this.telRespAcuerdo = telRespAcuerdo;
    }

    public String getIntFinanciacion() {
        return intFinanciacion;
    }

    public void setIntFinanciacion(String intFinanciacion) {
        this.intFinanciacion = intFinanciacion;
    }

    public String getIntRecargo() {
        return intRecargo;
    }

    public void setIntRecargo(String intRecargo) {
        this.intRecargo = intRecargo;
    }

    public String getNroResAcuerdo() {
        return nroResAcuerdo;
    }

    public void setNroResAcuerdo(String nroResAcuerdo) {
        this.nroResAcuerdo = nroResAcuerdo;
    }

    public String getNomPropietario() {
        return nomPropietario;
    }

    public void setNomPropietario(String nomPropietario) {
        this.nomPropietario = nomPropietario;
    }

    public String getNroAcuerdo() {
        return nroAcuerdo;
    }

    public void setNroAcuerdo(String nroAcuerdo) {
        this.nroAcuerdo = nroAcuerdo;
    }

    public String getVlrAbonoInicial() {
        return vlrAbonoInicial;
    }

    public void setVlrAbonoInicial(String vlrAbonoInicial) {
        this.vlrAbonoInicial = vlrAbonoInicial;
    }

    public String getTituloLeyDesc() {
        return tituloLeyDesc;
    }

    public void setTituloLeyDesc(String tituloLeyDesc) {
        this.tituloLeyDesc = tituloLeyDesc;
    }

    public String getTotalDeuda() {
        return totalDeuda;
    }

    public void setTotalDeuda(String totalDeuda) {
        this.totalDeuda = totalDeuda;
    }

    public String getTotalFinanciar() {
        return totalFinanciar;
    }

    public void setTotalFinanciar(String totalFinanciar) {
        this.totalFinanciar = totalFinanciar;
    }

    public boolean isVerDescEsp() {
        return verDescEsp;
    }

    public void setVerDescEsp(boolean verDescEsp) {
        this.verDescEsp = verDescEsp;
    }

    public boolean isPerModificar() {
        return perModificar;
    }

    public void setPerModificar(boolean perModificar) {
        this.perModificar = perModificar;
    }

    public boolean isPerModificarVA() {
        return perModificarVA;
    }

    public void setPerModificarVA(boolean perModificarVA) {
        this.perModificarVA = perModificarVA;
    }

    public boolean isVerReciboAcuerdo() {
        return verReciboAcuerdo;
    }

    public void setVerReciboAcuerdo(boolean verReciboAcuerdo) {
        this.verReciboAcuerdo = verReciboAcuerdo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isBloqueadoCuotas() {
        return bloqueadoCuotas;
    }

    public void setBloqueadoCuotas(boolean bloqueadoCuotas) {
        this.bloqueadoCuotas = bloqueadoCuotas;
    }

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    public String getTextoDialogo() {
        return textoDialogo;
    }

    public void setTextoDialogo(String textoDialogo) {
        this.textoDialogo = textoDialogo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaReciboSoporte() {
        return listaReciboSoporte;
    }

    public void setListaReciboSoporte(
        RegistroDataModelImpl listaReciboSoporte) {
        this.listaReciboSoporte = listaReciboSoporte;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaCrearacuerdosub() {
        return listaCrearacuerdosub;
    }

    public void setListaCrearacuerdosub(List<Registro> listaCrearacuerdosub) {
        this.listaCrearacuerdosub = listaCrearacuerdosub;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>
}
