package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.ejb.EjbPredialUnoRemote;
import com.sysman.predial.enums.PredialregispagbansControladorEnum;
import com.sysman.predial.enums.PredialregispagbansControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 07/06/2016
 *
 * @author asana version 2, 27/07/2017, 08,11,12/09/2017 se realiza proceso de refactoring
 */
@ManagedBean
@ViewScoped
public class PredialregispagbansControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String nOrden;
    private final String modulo;
    private final String acumu;
    private final String barras;
    private final String com;
    private final String docNumC;
    private final String esAcuerdo;
    private final String nomBanco;
    private final String numCupones;
    private final String numCuponesAcu;
    private final String numFactura;
    private final String vlrRepor;
    private final String pagBan;
    private final String paquete;
    private final String preAno;
    private final String preCod;
    private final String preFecVar;
    private final String preVal;
    private final String msj1;
    private final String msj2;
    private final String total;
    private boolean asignarProyectosVisible;
    private boolean cuadroFechaVisible;
    private boolean camopofechaactivo;
    private boolean campoBancoActivo;
    private boolean campoPaqueteActivo;
    private Date preFecLim;
    private Date preFec;
    private List<Registro> listaanofin;
    private RegistroDataModelImpl listaPreCod;
    private RegistroDataModelImpl listaPreCodE;
    private String auxiliar;
    private RegistroDataModelImpl listacodigobanco;
    private List<Registro> listaDpagobancosdet;
    private Registro registroSub;

    @EJB
    private EjbPredialUnoRemote ejbPredialUnoRemote;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;
    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    public PredialregispagbansControlador() {
        super();
        compania = SessionUtil.getCompania();
        nOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        modulo = SessionUtil.getModulo();
        acumu = "ACUMULADO";
        barras = "BARRAS";
        com = "COMPANIA";
        docNumC = "DOCNUM";
        esAcuerdo = "ESACUERDO";
        nomBanco = "NOMBREBANCO";
        numCupones = "NROCUPONES";
        numCuponesAcu = "NROCUPONESACU";
        numFactura = "NUMFACTURA";
        vlrRepor = "VLRREPORTADO";
        pagBan = "PAG_BAN";
        paquete = "PAQUETE";
        preAno = "PREANO";
        preCod = "PRECOD";
        preFecVar = "PREFEC";
        preVal = "PREVAL";
        msj1 = "TB_TB1359";
        msj2 = "TB_TB1387";
        total = "TOTAL";
        camopofechaactivo = false;
        campoBancoActivo = false;
        campoPaqueteActivo = false;

        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALREGISPAGBANS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
            }
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaPreCod();
        cargarListaPreCodE();
        cargarListacodigobanco();
        cargarListaanofin();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaDpagobancosdet();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaDpagobancosdet = null;
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.IP_PAGO_BANCOSCAB;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {

        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    public void cargarListaDpagobancosdet() {

        Map<String, Object> param = new TreeMap<>();

        try {
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PredialregispagbansControladorEnum.PARAM0.getValue(),
                            registro.getCampos().get(preFecVar));
            param.put(PredialregispagbansControladorEnum.PARAM1.getValue(),
                            registro.getCampos().get(paquete));
            param.put(PredialregispagbansControladorEnum.PARAM2.getValue(),
                            registro.getCampos().get(pagBan));

            listaDpagobancosdet = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialregispagbansControladorUrlEnum.URL26049
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            PredialregispagbansControladorEnum.PARAM3
                                                            .getValue()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaanofin() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaanofin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialregispagbansControladorUrlEnum.URL56094
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPreCod() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialregispagbansControladorUrlEnum.URL62135
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        nOrden);

        listaPreCod = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, docNumC);
    }

    public void cargarListaPreCodE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialregispagbansControladorUrlEnum.URL62135
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        nOrden);

        listaPreCodE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, docNumC);
    }

    public void cargarListacodigobanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialregispagbansControladorUrlEnum.URL66778
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacodigobanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOBANCO");
    }

    // </METODOS_CARGAR_LISTA>

    // <METODOS_CAMBIAR>

    public void cambiarcuponesrepor() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(
                        (String) registro.getCampos().get(numCupones))
                        && SysmanFunciones.validarVariableVacio(
                                        (String) registro.getCampos().get(numCuponesAcu))
                        && (Double.parseDouble(registro.getCampos().get(numCuponesAcu)
                                        .toString()) > Double
                                                        .parseDouble(registro.getCampos()
                                                                        .get(numCupones)
                                                                        .toString()))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1328"));
            if (css != null) {
                registro.getCampos().put(numCupones,
                                registroIni.get(numCupones));
            }
            else {
                registro.getCampos().put(numCupones, null);
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarvlrrepor() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(
                        (String) registro.getCampos().get(vlrRepor))
                        && SysmanFunciones.validarVariableVacio(
                                        (String) registro.getCampos().get(acumu))
                        && (Double.parseDouble(registro.getCampos().get(acumu)
                                        .toString()) > Double
                                                        .parseDouble(registro.getCampos()
                                                                        .get(vlrRepor)
                                                                        .toString()))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1327"));
            if (css != null) {
                registro.getCampos().put(vlrRepor,
                                registroIni.get(vlrRepor));
            }
            else {
                registro.getCampos().put(vlrRepor, null);
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcuponesacum() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarvlracum() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarBarras() {
        // <CODIGO_DESARROLLADO>

        try {
            if (SysmanFunciones.validarVariableVacio(
                            registroSub.getCampos().get("BARRAS").toString())) {
                registroSub.getCampos().put(numFactura, null);
                return;
            }
            String dateAux = SysmanFunciones.nvl(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "FECHA DE BLOQUEO DE REGISTRO DE PAGOS",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                            "NO").toString();
            if (!("").equals(dateAux)) {
                Date dtFecha = SysmanFunciones.convertirAFecha(dateAux);

                if (((Date) registro.getCampos().get(preFecVar))
                                .before(dtFecha)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1137"));
                    registroSub.getCampos().put(barras, null);
                }
            }

            if (!evaluarCodigoBarras()) {
                return;
            }
        }
        catch (ParseException | SystemException e) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public boolean evaluarCodigoBarras() {
        try {
            if (("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "MANEJA CODIGO DE BARRAS MULTIIMPUESTO",
                                            modulo, new Date(), true),
                            "NO"))) {
                if ((registroSub.getCampos().get(barras).toString()
                                .length() != 24)
                                || (registroSub.getCampos().get(barras).toString()
                                                .length() != 72)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString(msj1));
                    registroSub.getCampos().put(barras, null);
                    return false;
                }
                evaluarBarras();
            }
            else {
                if (!registrarBarras()) {
                    return false;
                }
            }
        }
        catch (SystemException e) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    public void evaluarBarras() {
        String cod;
        if (registroSub.getCampos().get(barras).toString()
                        .length() == 24) {
            registrarFactura();
        }
        else if (registroSub.getCampos().get(barras).toString()
                        .length() == 72) {
            if (!("70000").equals(mid(registroSub.getCampos()
                            .get(barras).toString(),
                            21, 5))) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(msj1));
                registroSub.getCampos().put(barras, null);
                return;
            }
            registroSub.getCampos().put(numFactura,
                            mid(registroSub.getCampos().get(barras)
                                            .toString(), 36, 9));
            cod = getPreCod();

            if (("").equals(cod)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(msj2));
                registroSub.getCampos().put(barras, null);
                registroSub.getCampos().put(numFactura, null);
                return;
            }
            else {
                registroSub.getCampos().put(numFactura,
                                registroSub.getCampos().get(barras));
                registroSub.getCampos().put(preCod, cod);
                validacionReciboPago(registroSub.getCampos()
                                .get(numFactura).toString());
            }
        }
    }

    public void registrarFactura() {
        String cod;
        if (!("60000").equals(mid(registroSub.getCampos()
                        .get(barras).toString(),
                        1, 5))) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1360"));
            registroSub.getCampos().put(barras, null);
            return;
        }
        registroSub.getCampos().put(numFactura,
                        mid(registroSub.getCampos().get(barras)
                                        .toString(), 15, 9));
        cod = getPreCod();

        if (("").equals(cod)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(msj2));
            registroSub.getCampos().put(barras, null);
            registroSub.getCampos().put(numFactura, null);
            return;
        }
        else {
            registroSub.getCampos().put(numFactura,
                            registroSub.getCampos().get(barras));
            registroSub.getCampos().put(preCod, cod);
            validacionReciboPago(registroSub.getCampos()
                            .get(numFactura).toString());
        }
    }

    public boolean registrarBarras() {
        String cod;
        if (registroSub.getCampos().get(barras).toString()
                        .length() == 15) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1362"));
            registroSub.getCampos().put(barras, null);
            return false;
        }
        else if (registroSub.getCampos().get(barras).toString()
                        .length() == 9) {
            registroSub.getCampos().put(numFactura,
                            registroSub.getCampos().get(barras));
            cod = getPreCod();

            if (("").equals(cod)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(msj2));
                registroSub.getCampos().put(barras, null);
                registroSub.getCampos().put(numFactura, null);
                return false;
            }
            else {
                registroSub.getCampos().put(numFactura,
                                registroSub.getCampos().get(barras));
                registroSub.getCampos().put(preCod, cod);
                validacionReciboPago(registroSub.getCampos()
                                .get(numFactura).toString());
            }

        }
        else if (registroSub.getCampos().get(barras).toString()
                        .length() == 72) {
            registroSub.getCampos()
                            .put(preCod, mid(registroSub.getCampos()
                                            .get(barras).toString(),
                                            21, 15));
            registroSub.getCampos().put(numFactura,
                            mid(registroSub.getCampos().get(barras)
                                            .toString(), 36, 9));
            validacionReciboPago(registroSub.getCampos()
                            .get(numFactura).toString());

        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString(msj1));
            registroSub.getCampos().put(barras, null);
            return false;
        }
        return true;
    }

    public void seleccionarFilaPreCod(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(preCod,
                        registroAux.getCampos().get("CODIGO"));

        if ((registroSub.getCampos().get(barras) != null) && (registroSub
                        .getCampos().get(barras).toString().length() == 72)) {
            registroSub.getCampos().put(preVal, mid(
                            registroSub.getCampos().get(barras).toString(),
                            49, 14));
        }
        else {
            registroSub.getCampos().put(preVal,
                            registroAux.getCampos().get(total));
        }

        validacionReciboPago(registroAux.getCampos().get(docNumC).toString());
    }

    public void seleccionarFilaPreCodE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
    }

    public void seleccionarFilacodigobanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(pagBan,
                        registroAux.getCampos().get("CODIGOBANCO"));
        registro.getCampos().put(nomBanco,
                        registroAux.getCampos().get(nomBanco));
    }

    public void oprimirComando40() {
        agregarRegistroNuevo(false);
        Map<String, Object> param = new TreeMap<>();
        param.put("rid", css);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FACTURASCORRIGE_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public void oprimirregistrarPagoDoble() {
        // <CODIGO_DESARROLLADO>
        String nombrebanco = SysmanFunciones
                        .validarCampoVacio(registro.getCampos(), nomBanco) ? ""
                                        : registro.getCampos().get(nomBanco)
                                                        .toString();
        agregarRegistroNuevo(false);
        String fechaAux;
        try {
            fechaAux = SysmanFunciones.convertirAFechaCadena(
                            (Date) registro.getCampos().get(preFecVar));
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("banco",
                            registro.getCampos().get(pagBan).toString());
            parametros.put("nombrebanco", nombrebanco);
            parametros.put("paquete",
                            registro.getCampos().get(paquete).toString());
            parametros.put("fecha", fechaAux);

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.PAGOSDOBLESDOS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        catch (ParseException e) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdProyecto() {
        registro.getCampos();
        agregarRegistroNuevo(false);
        String[] campos = { "rid" };
        Object[] valores = { css };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.FRMASIGNARPROYVALORES_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubDpagobancosdet() {
        try {

            registroSub.getCampos().put(com, compania);
            registroSub.getCampos()
                            .put(PredialregispagbansControladorEnum.PARAM7
                                            .getValue(), nOrden);
            registroSub.getCampos().put(paquete,
                            registro.getCampos().get(paquete));
            registroSub.getCampos().put(pagBan,
                            registro.getCampos().get(pagBan));
            registroSub.getCampos().put(preFecVar,
                            registro.getCampos().get(preFecVar));
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            if (antesEditarSub(registroSub)) {

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                PredialregispagbansControladorUrlEnum.URL24820
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSub.getCampos());

                cargarListaDpagobancosdet();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
                realizarPago(registroSub);
                registroSub = new Registro(new HashMap<String, Object>());
                cargarRegistro(css, "m");
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubDpagobancosdet(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            if (antesEditarSub(reg)) {
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                PredialregispagbansControladorUrlEnum.URL24821
                                                                .getValue());
                requestManager.save(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                registroSub.getCampos());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaDpagobancosdet();
        }
    }

    public void eliminarRegSubDpagobancosdet() {
        JsfUtil.agregarMensajeError(idioma.getString("TB_TB1329"));
        return;
    }

    public boolean antesEditarSub(Registro regE) {

        boolean resultado = false;

        try {

            resultado = ejbPredialOcho.validaPago(compania,
                            nOrden,
                            regE.getCampos().get("PAQUETE").toString(),
                            regE.getCampos().get("PAG_BAN").toString(),
                            (Date) regE.getCampos().get("PREFEC"),
                            regE.getCampos().get("PRECOD").toString(),
                            regE.getCampos().get(numFactura).toString(),
                            SessionUtil.getUser().getCodigo());

        }
        catch (NumberFormatException | SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        try {
            if (!resultado) {
                return false;
            }

            if (!evaluarPreFec()) {
                return false;
            }

        }
        catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    public boolean evaluarPreFec() {
        Date fechaLimiteS = new Date();
        Date fechaRecaudo = new Date();
        Date fechaPreparacion = new Date();
        try {
            fechaLimiteS = SysmanFunciones.convertirAFecha(SysmanFunciones.convertirAFechaCadena(preFecLim));
            fechaRecaudo = SysmanFunciones
                            .convertirAFecha(SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get(preFecVar)));
            fechaPreparacion = SysmanFunciones.convertirAFecha(SysmanFunciones.convertirAFechaCadena(preFec));

            if ((preFecLim != null) && (fechaLimiteS.before(
                            fechaRecaudo))) {
                cuadroFechaVisible = true;
                return false;
            }
            else if ((preFec != null)
                            && (fechaRecaudo.before(fechaPreparacion))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1388"));
                return false;
            }
        }
        catch (ParseException e) {
            Logger.getLogger(PredialregispagbansControlador.class.getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        return true;
    }

    public boolean evaluarAbonoCuenta(Registro regE, Registro rs) {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), nOrden);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        regE.getCampos().get(preCod));

        Registro rsFacAb = null;
        try {
            rsFacAb = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialregispagbansControladorUrlEnum.URL24815
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rsFacAb != null) {
            if (("1").equals(rsFacAb.getCampos().get("CUENTA"))) {
                if (!rs.getCampos().get(preVal).equals(
                                rsFacAb.getCampos().get("TOTALABONO"))
                                || !rs.getCampos().get(preFecVar).equals(rsFacAb
                                                .getCampos()
                                                .get("FECHAFACTURADO"))) {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB1370"));
                    return false;
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB1371"));
                return false;
            }
        }
        return true;
    }

    public void cancelarEdicionDpagobancosdet() {
        cargarListaDpagobancosdet();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            if (("SI").equals(SysmanFunciones.nvl(
                            ejbSysmanUtilRemote.consultarParametro(compania,
                                            "MANEJA APORTE VOLUNTARIO", modulo,
                                            new Date(), true),
                            "NO"))) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB691"));
                asignarProyectosVisible = true;
            }
            else {
                asignarProyectosVisible = false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (css == null) {
            registro.getCampos().put(preFecVar, new Date());
        }

        if (accion.equals(ACCION_MODIFICAR)) {
            campoPaqueteActivo = true;
            camopofechaactivo = true;
            campoBancoActivo = true;
        }
        else {
            campoPaqueteActivo = false;
            camopofechaactivo = false;
            campoBancoActivo = false;
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(com, compania);
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

        registro.getCampos().remove(nomBanco);

        if ("m".equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }
        try {
            registro.getCampos().put(preFecVar, SysmanFunciones
                            .convertirAFecha(
                                            SysmanFunciones.convertirAFechaCadena(
                                                            (Date) registro.getCampos()
                                                                            .get(preFecVar))));
        }
        catch (ParseException e) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PredialregispagbansControladorEnum.PARAM6.getValue(),
                        registro.getCampos().get(pagBan));

        Registro aux = null;
        try {
            aux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialregispagbansControladorUrlEnum.URL24816
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(nomBanco,
                        aux == null ? " " : aux.getCampos().get(nomBanco));
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

    public boolean isAsignarProyectosVisible() {
        return asignarProyectosVisible;
    }

    public void setAsignarProyectosVisible(boolean asignarProyectosVisible) {
        this.asignarProyectosVisible = asignarProyectosVisible;
    }

    public boolean isCuadroFechaVisible() {
        return cuadroFechaVisible;
    }

    public void setCuadroFechaVisible(boolean cuadroFechaVisible) {
        this.cuadroFechaVisible = cuadroFechaVisible;
    }

    public Date getPreFecLim() {
        return preFecLim;
    }

    public void setPreFecLim(Date preFecLim) {
        this.preFecLim = preFecLim;
    }

    public Date getPreFec() {
        return preFec;
    }

    public void setPreFec(Date preFec) {
        this.preFec = preFec;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaanofin() {
        return listaanofin;
    }

    public void setListaanofin(List<Registro> listaanofin) {
        this.listaanofin = listaanofin;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaPreCod() {
        return listaPreCod;
    }

    public void setListaPreCod(RegistroDataModelImpl listaPreCod) {
        this.listaPreCod = listaPreCod;
    }

    public RegistroDataModelImpl getListaPreCodE() {
        return listaPreCodE;
    }

    public void setListaPreCodE(RegistroDataModelImpl listaPreCodE) {
        this.listaPreCodE = listaPreCodE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListacodigobanco() {
        return listacodigobanco;
    }

    public void setListacodigobanco(RegistroDataModelImpl listacodigobanco) {
        this.listacodigobanco = listacodigobanco;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaDpagobancosdet() {
        return listaDpagobancosdet;
    }

    public void setListaDpagobancosdet(List<Registro> listaPpagobancosdet) {
        this.listaDpagobancosdet = listaPpagobancosdet;
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

    public void realizarPago(Registro regE) {
        try {
            String user = SessionUtil.getUser().getCodigo();
            String bancoa = regE.getCampos().get(pagBan).toString();
            String codPredio = regE.getCampos().get(preCod).toString();
            String fechaRecaudo = SysmanFunciones.convertirAFechaCadena(
                            (Date) regE.getCampos().get(preFecVar));
            String reciboPago = regE.getCampos().get(numFactura).toString();
            String totalRecibo = regE.getCampos().get(preVal).toString();
            String paqueteA = regE.getCampos().get(paquete).toString();

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(PredialregispagbansControladorEnum.PARAM4.getValue(),
                            reciboPago);
            param.put(PredialregispagbansControladorEnum.PARAM5.getValue(),
                            codPredio);

            Registro rsRecibo;

            rsRecibo = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PredialregispagbansControladorUrlEnum.URL24817
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rsRecibo != null) {
                if ((boolean) rsRecibo.getCampos().get("ESABONO")) {
                    ejbPredialUnoRemote.registrarRecaudoAbonos(compania,
                                    reciboPago, codPredio, bancoa, fechaRecaudo,
                                    nOrden, Integer.parseInt(modulo), user,
                                    new BigDecimal(totalRecibo));
                }
                else if ((boolean) rsRecibo.getCampos().get("ESCUOTA")) {
                    ejbPredialUnoRemote.registrarRecaudoCuotas(compania,
                                    reciboPago, codPredio, bancoa, fechaRecaudo,
                                    nOrden, Integer.parseInt(modulo), user,
                                    new BigDecimal(totalRecibo),
                                    new BigDecimal(rsRecibo.getCampos()
                                                    .get("NCUOTA")
                                                    .toString()),
                                    Integer.parseInt(rsRecibo.getCampos()
                                                    .get(preAno)
                                                    .toString()));
                }
                else if ((boolean) rsRecibo.getCampos().get(esAcuerdo)
                                && ("0").equals(SysmanFunciones
                                                .nvl(rsRecibo.getCampos().get(
                                                                "TIPOABONOAACUERDO"),
                                                                "0"))) {
                    ejbPredialUnoRemote.registrarRecaudoAcuerdos(compania,
                                    reciboPago, codPredio, bancoa, fechaRecaudo,
                                    nOrden, Integer.parseInt(modulo), user,
                                    new BigDecimal(totalRecibo));
                }
                else if ((boolean) rsRecibo.getCampos().get(esAcuerdo)
                                && !("0").equals(SysmanFunciones
                                                .nvl(rsRecibo.getCampos().get(
                                                                "TIPOABONOAACUERDO"),
                                                                "0"))) {
                    ejbPredialUnoRemote.registrarReciboAbonoAAcuerdo(compania,
                                    reciboPago, codPredio, bancoa, fechaRecaudo,
                                    nOrden, Integer.parseInt(modulo), user,
                                    new BigDecimal(totalRecibo), paqueteA);
                }
                else if ((boolean) rsRecibo.getCampos().get("UNICO_ANO")) {
                    ejbPredialUnoRemote.registrarRecaudoUnicoVigencia(compania,
                                    reciboPago, codPredio, bancoa, fechaRecaudo,
                                    nOrden, Integer.parseInt(modulo),
                                    new BigDecimal(totalRecibo),
                                    Integer.parseInt(rsRecibo.getCampos()
                                                    .get(preAno).toString()),
                                    paqueteA, user);
                }
                else {
                    ejbPredialUnoRemote.registrarRecaudo(compania, reciboPago,
                                    codPredio, bancoa, fechaRecaudo, nOrden,
                                    new BigDecimal(totalRecibo),
                                    Integer.parseInt(rsRecibo.getCampos()
                                                    .get(preAno).toString()),
                                    paqueteA, user);
                }
            }

        }
        catch (ParseException | SystemException e) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String mid(String valor, int ini, int len) {
        return valor.substring(ini - 1, (ini - 1) + len);
    }

    public void validacionReciboPago(String docNum) {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(preCod, registroSub.getCampos().get(preCod));
        param.put(docNumC, docNum);
        Registro regAux;
        try {
            regAux = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PredialregispagbansControladorUrlEnum.URL24819
                                                                                            .getValue())
                                                            .getUrl(), param));

            if (regAux == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1355"));
                registroSub.getCampos().put(preCod, null);
                registroSub.getCampos().put(preVal, null);
                registroSub.getCampos().put(barras, null);
                registroSub.getCampos().put(numFactura, null);
                registroSub.getCampos().put("ANO", null);
                preFecLim = null;
            }
            else {
                registroSub.getCampos().put("ANO",
                                regAux.getCampos().get(preAno));
                registroSub.getCampos().put(numFactura,
                                regAux.getCampos().get(docNumC));
                registroSub.getCampos().put(preVal,
                                regAux.getCampos().get(preVal));

                if (regAux.getCampos().get("PREFECLIM") != null) {
                    preFecLim = (Date) regAux.getCampos().get("PREFECLIM");
                }
                else {
                    preFecLim = null;
                }

                if (regAux.getCampos().get(preFecVar) != null) {
                    preFec = (Date) regAux.getCampos().get(preFecVar);
                }
                else {
                    preFec = null;
                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String getPreCod() {
        String valor = null;
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(numFactura, registroSub.getCampos().get(numFactura));
        Registro reg;

        try {
            reg = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PredialregispagbansControladorUrlEnum.URL24818
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            if (reg != null) {
                valor = reg.getCampos().get(preCod).toString();

            }
            else {
                valor = "";
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return valor;
    }

    public void aceptarCuadroFecha() {
        // <CODIGO_DESARROLLADO>
        try {
            registroSub.getCampos().put(com, compania);
            registroSub.getCampos()
                            .put(PredialregispagbansControladorEnum.PARAM7
                                            .getValue(), nOrden);
            registroSub.getCampos().put(paquete,
                            registro.getCampos().get(paquete));
            registroSub.getCampos().put(pagBan,
                            registro.getCampos().get(pagBan));
            registroSub.getCampos().put(preFecVar,
                            registro.getCampos().get(preFecVar));

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PredialregispagbansControladorUrlEnum.URL24820
                                                            .getValue());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaDpagobancosdet();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
            realizarPago(registroSub);
            cargarRegistro(css, "m");
        }
        catch (SystemException ex) {
            Logger.getLogger(PredialregispagbansControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
        cuadroFechaVisible = false;
    }

    public void cancelarCuadroFecha() {
        cuadroFechaVisible = false;
    }

    public boolean iscamopofechaactivo() {
        return camopofechaactivo;
    }

    public void setcamopofechaactivo(boolean camopofechaactivo) {
        this.camopofechaactivo = camopofechaactivo;
    }

    public boolean isCampoBancoActivo() {
        return campoBancoActivo;
    }

    public void setCampoBancoActivo(boolean campoBancoActivo) {
        this.campoBancoActivo = campoBancoActivo;
    }

    public boolean isCampoPaqueteActivo() {
        return campoPaqueteActivo;
    }

    public void setCampoPaqueteActivo(boolean campoPaqueteActivo) {
        this.campoPaqueteActivo = campoPaqueteActivo;
    }

}
