package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.ejb.EjbPredialSeisRemote;
import com.sysman.predial.enums.RegistropagobancospazsControladorEnum;
import com.sysman.predial.enums.RegistropagobancospazsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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
 * @author dsuesca
 * @version 1, 26/05/2016
 * @version 0.2, 02/03/2017, pespitia <br>
 * Buenas practicas SonarLint. Ajustes en el proceso de insertar y
 * eliminar del subformulario dPagoBancosDet.
 * 
 * @author eamaya
 * @version 3.0 15/07/2017 Proceso de Refactoring DSS y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class RegistropagobancospazsControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    private final String modulo;
    private final String vlAcumuladoCons;
    private final String valorCons;
    private final String codigoPredioCons;
    private final String barrasCons;
    private final String anuladoCons;
    private final String strCons;
    private final String prefecCons;
    private final String paqueteCons;
    private final String pagbanCons;
    private final String numOrdenCons;
    private final String numCuponesAcucCons;
    private final String msgRecaudarReciboCons;
    private final String msgCodigoIncorrectoCons;
    private final String msgExisteBloqRegCons;

    /** Constante a nivel de clase que aloja el valor FECHA_ANULADO */
    private final String cFechaAnulado;

    /**
     * Constante a nivel de clase que aloja el valor FECHA_LIMITE_PAG
     */
    private final String cFechaLimite;

    /** Constante a nivel de clase que aloja el valor TB_TB1142 */
    private final String tb1142;

    /** Constante a nivel de clase que aloja el valor TB_TB1159 */
    private final String tb1159;

    private final String usuario;

    // <DECLARAR_ATRIBUTOS>
    private String nombreBanco;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigobanco;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaDpagobancosdet;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    /**
     * Constante a nivel de clase que aloja el valor FECHA DE BLOQUEO
     * DE REGISTRO DE PAGOS
     */
    private final String parFechaBloqueo;

    /**
     * Constante a nivel de clase que aloja el valor MANEJA CODIGO DE
     * BARRAS MULTIIMPUESTO
     */
    private final String parManejaCodigo;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSub;
    private boolean muestraDialogo;

    /** Control que indica si la factura se encuentra pagada */
    private boolean indFactura;

    /**
     * Variable que controla el bloqueo de los componentes cuando se
     * va a realizar una actualizacion
     */
    private boolean blockUpdate;

    private Registro regAux;
    private String tituloDialogo;
    private boolean muestraDialogoEliminar;
    private String tituloDialogoEliminar;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbPredialCeroRemote ejbPRedialCero;

    @EJB
    private EjbPredialSeisRemote ejbPredialSeis;

    // </DECLARAR_ADICIONALES>
    public RegistropagobancospazsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();

        vlAcumuladoCons = "VLACUMULADO";
        valorCons = "VALOR";
        codigoPredioCons = "CODIGO_PREDIO";
        barrasCons = "BARRAS";
        anuladoCons = "ANULADO";
        strCons = "60999";
        prefecCons = "PREFEC";
        paqueteCons = "PAQUETE";
        pagbanCons = "PAG_BAN";
        numOrdenCons = "NUMERO_ORDEN";
        numCuponesAcucCons = "NROCUPONESACU";
        msgRecaudarReciboCons = "TB_TB1139";
        msgCodigoIncorrectoCons = "TB_TB1138";
        msgExisteBloqRegCons = "TB_TB1137";
        cFechaAnulado = "FECHA_ANULADO";
        cFechaLimite = "FECHA_LIMITE_PAG";

        tb1142 = "TB_TB1142";
        tb1159 = "TB_TB1159";

        parFechaBloqueo = "FECHA DE BLOQUEO DE REGISTRO DE PAGOS";
        parManejaCodigo = "MANEJA CODIGO DE BARRAS MULTIIMPUESTO";

        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTROPAGOBANCOSPAZS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RegistropagobancospazsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacodigobanco();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaDpagobancosdet();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaDpagobancosdet = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.IP_PAGOS_BANCOSCAB_PAZ;
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
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(RegistropagobancospazsControladorEnum.PREFEC.getValue(),
                            registro.getCampos()
                                            .get(prefecCons));
            param.put(GeneralParameterEnum.PAQUETE.getName(),
                            registro.getCampos()
                                            .get(paqueteCons));
            param.put(GeneralParameterEnum.PAG_BAN.getName(),
                            registro.getCampos()
                                            .get(pagbanCons));

            listaDpagobancosdet = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistropagobancospazsControladorUrlEnum.URL7123
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            "IP_PAGOS_BANCOSDET_PAZ"));
        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacodigobanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistropagobancospazsControladorUrlEnum.URL8509
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacodigobanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOBANCO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiardialogoSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void aceptardialogoSub() {
        // <CODIGO_DESARROLLADO>
        muestraDialogo = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelardialogoSub() {
        // <CODIGO_DESARROLLADO>
        muestraDialogo = false;
        registroSub = new Registro(new HashMap<String, Object>());
        // </CODIGO_DESARROLLADO>
    }

    public void aceptardialogoEliminar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> map = regAux.getCampos();

        double valorAcum = Double.parseDouble(
                        registro.getCampos().get(vlAcumuladoCons).toString());

        double numCupAcum = Double.parseDouble(registro.getCampos()
                        .get(numCuponesAcucCons).toString());

        try {

            ejbPredialSeis.reversarPagoPazYSalvo(compania,
                            map.get("REFERENCIA").toString(),
                            (Date) map.get("PREFEC"),
                            map.get(paqueteCons).toString(),
                            map.get(pagbanCons).toString(),
                            Double.toString(numCupAcum),
                            Double.toString(valorAcum), usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarListaDpagobancosdet();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            muestraDialogoEliminar = false;
            cargarRegistro(css, ACCION_MODIFICAR);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void obtenerFechaBloqueoRegistro(StringBuilder strFecha) {
        try {
            strFecha.append(ejbSysmanUtl.consultarParametro(compania,
                            parFechaBloqueo, modulo, new Date(), false));

        }
        catch (SystemException e) {
            Logger.getLogger(RegistropagobancospazsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (strFecha.toString() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(tb1159));
            return;
        }
    }

    public Date convertirCadenaFecha(StringBuilder strFecha) {
        Date dtFecha = null;
        try {
            dtFecha = SysmanFunciones.convertirAFecha(strFecha.toString());
        }
        catch (ParseException e) {
            Logger.getLogger(RegistropagobancospazsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return dtFecha;
    }

    public void manejarMultimpuesto(StringBuilder manejaMultimpuesto) {
        try {
            manejaMultimpuesto.append(ejbSysmanUtl.consultarParametro(compania,
                            parManejaCodigo, modulo, new Date(), false));

        }
        catch (SystemException e) {
            Logger.getLogger(RegistropagobancospazsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void compararTamanoRegistroMenor() {
        if (registroSub.getCampos().get(barrasCons).toString().length() < 24) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgExisteBloqRegCons));
            return;
        }
        if ((registroSub.getCampos().get(barrasCons).toString().length() > 24)
            && (registroSub.getCampos().get(barrasCons).toString()
                            .length() < 72)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgCodigoIncorrectoCons));
            return;
        }

    }

    public void compararTamanoRegistroIgual() {
        if (registroSub.getCampos().get(barrasCons).toString().length() == 24) {
            if (!(strCons).equals(registroSub.getCampos().get(barrasCons)
                            .toString().substring(0, 5))) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgCodigoIncorrectoCons));
                return;
            }
            registroSub.getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            registroSub.getCampos()
                                            .get(barrasCons).toString()
                                            .substring(
                                                            registroSub.getCampos()
                                                                            .get(barrasCons)
                                                                            .toString()
                                                                            .length()
                                                                - 9,
                                                            registroSub.getCampos()
                                                                            .get(barrasCons)
                                                                            .toString()
                                                                            .length()));
        }
        if (registroSub.getCampos().get(barrasCons).toString().length() == 72) {
            if (!(strCons).equals(registroSub.getCampos().get(barrasCons)
                            .toString().substring(20, 25))) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgCodigoIncorrectoCons));
                return;
            }
            registroSub.getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            registroSub.getCampos()
                                            .get(barrasCons).toString()
                                            .substring(35, 44));
        }
    }

    public void compararTamanoRegistro9() {
        if (registroSub.getCampos().get(barrasCons).toString().length() < 9) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgCodigoIncorrectoCons));
            return;
        }
        if (registroSub.getCampos().get(barrasCons).toString().length() == 9) {
            registroSub.getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            registroSub.getCampos().get(barrasCons));
        }
    }

    public void compararTamanoRegistroEntre() {
        if ((registroSub.getCampos().get(barrasCons).toString().length() > 9)
            && (registroSub.getCampos().get(barrasCons).toString()
                            .length() < 72)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgCodigoIncorrectoCons));
            return;
        }

        if (registroSub.getCampos().get(barrasCons).toString().length() == 72) {
            registroSub.getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            registroSub.getCampos()
                                            .get(barrasCons).toString()
                                            .substring(35, 44));
        }
    }

    public boolean validarAnuladoPago(Registro rsRecibo) {
        Date dtFechaPrefec;
        if (!Boolean.parseBoolean(
                        rsRecibo.getCampos().get(anuladoCons).toString())
            && !Boolean.parseBoolean(
                            rsRecibo.getCampos().get("PAGO").toString())) {
            registroSub.getCampos().put(codigoPredioCons,
                            rsRecibo.getCampos().get(codigoPredioCons));
            registroSub.getCampos().put(valorCons,
                            rsRecibo.getCampos().get(valorCons));
            registroSub.getCampos().put("TIPO",
                            rsRecibo.getCampos().get("TIPO"));
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(numOrdenCons,
                            rsRecibo.getCampos().get(numOrdenCons));
            dtFechaPrefec = (Date) registro.getCampos().get(prefecCons);
            Date dtFechaLimite = (Date) rsRecibo.getCampos()
                            .get(cFechaLimite);

            if (dtFechaPrefec.after(dtFechaLimite)) {
                tituloDialogo = idioma.getString("TB_TB1140");
                muestraDialogo = true;
                regAux = new Registro();
                regAux.getCampos().putAll(registroSub.getCampos());

                return false;
            }
        }

        return true;
    }

    public boolean compararFechaExpedicionRegistro(Registro rsRecibo,
        Date dtFechaExp, Date dtFechaRegistro) {
        if (dtFechaExp.compareTo(dtFechaRegistro) < 0) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(msgRecaudarReciboCons));

            return false;
        }
        if (Boolean.parseBoolean(
                        rsRecibo.getCampos().get(anuladoCons).toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1141")
                + "  " + convertirFecha((Date) rsRecibo.getCampos()
                                .get(cFechaAnulado)));
            return false;
        }
        if (Boolean.parseBoolean(rsRecibo.getCampos().get("PAGO").toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(tb1142));
            indFactura = false;
            return false;
        }

        indFactura = true;
        return true;
    }

    /**
     * Asigna la mascara DD/MM/YYYY a la fecha ingresada por el
     * parametro {@code fecha}
     * 
     * @param fecha
     * @return El valor de la fecha como cadena.
     */
    public String convertirFecha(Date fecha) {
        String miFecha = "";

        try {
            miFecha = SysmanFunciones.convertirAFechaCadena(fecha);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return miFecha;
    }

    /**
     * Recupera el valor asignado a un parametro en el ESQUEMA_SYSMAN
     * 
     * @param par
     * Nombre del parametro.
     * @return El valor del parametro.<br>
     * Nulo si el parametro no esta configurado.
     */
    private String recuperarParametro(String par) {
        String valor = null;
        try {
            valor = ejbSysmanUtl.consultarParametro(compania, par, modulo,
                            new Date(), false);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return valor;
    }

    private boolean validarProcesoFecha(Date dtFechaPrefec) {
        String strFecha = recuperarParametro(parFechaBloqueo);
        Date dtFecha = null;

        if (SysmanFunciones.validarVariableVacio(strFecha)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(tb1159));
            return true;
        }

        try {
            dtFecha = SysmanFunciones.convertirAFecha(strFecha);
        }
        catch (ParseException e) {
            Logger.getLogger(RegistropagobancospazsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (dtFechaPrefec.before(dtFecha)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgExisteBloqRegCons));
            return true;
        }

        return false;
    }

    /**
     * Evalua la condicion ingresada por parametro, si se cumple
     * muestra un mensaje informativo.
     * 
     * @param key
     * Condicion ingresada.
     * @param mensaje
     * Descripcion del mensaje informativo.
     * @return {@code true} si se cumple la condicion. <br>
     * {@code false} : si no se cumple.
     */
    private boolean evaluarCondicion(boolean key, String mensaje) {
        if (key) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(mensaje));
            return true;
        }

        return false;
    }

    private void asignarValorCampo(Map<String, Object> campos, String clave,
        Object valor, boolean key) {
        if (key) {
            campos.put(clave, valor);
        }
    }

    private boolean validarCambiarBarrasInicial() {
        registroSub.getCampos().put(codigoPredioCons, "");
        registroSub.getCampos().put(valorCons, "");

        Date dtFechaPrefec = (Date) registro.getCampos().get(prefecCons);

        if (validarProcesoFecha(dtFechaPrefec)) {
            return true;
        }

        if ((registroSub.getCampos().get(barrasCons) == null)
            && (registroSub.getCampos().get("PREDIO") == null)) {
            return true;
        }

        return false;
    }

    private boolean procesarParteA() {
        boolean cond1 = evaluarCondicion(
                        registroSub.getCampos().get(barrasCons).toString()
                                        .length() < 24,
                        msgExisteBloqRegCons);

        boolean cond2 = evaluarCondicion(
                        (registroSub.getCampos().get(barrasCons).toString()
                                        .length() > 24)
                            && (registroSub.getCampos().get(barrasCons)
                                            .toString()
                                            .length() < 72),
                        msgCodigoIncorrectoCons);

        if (cond1 || cond2) {
            return true;
        }

        if (procesarBarrasConMultiImpuesto()) {
            return true;
        }

        return false;
    }

    private boolean procesarParteB() {
        String manejaMultimpuesto = recuperarParametro(parManejaCodigo);

        if ((manejaMultimpuesto != null) && "SI".equals(manejaMultimpuesto)) {
            if (procesarParteA()) {
                return true;
            }
        }
        else {
            if (procesarBarrasSinMultiImpuesto()) {
                return true;
            }
        }

        return false;
    }

    public void cambiarBarras() {
        // <CODIGO_DESARROLLADO>

        if (validarCambiarBarrasInicial()) {
            return;
        }

        if (procesarParteB()) {
            return;
        }

        if (actualizarRecibo()) {
            return;
        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean actualizarRecibo() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.REFERENCIA.getName(),
                        registroSub.getCampos()
                                        .get(GeneralParameterEnum.REFERENCIA
                                                        .getName()));

        Registro rsRecibo;
        try {
            rsRecibo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistropagobancospazsControladorUrlEnum.URL9087
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsRecibo != null) {
                Date dtFechaExp = (Date) rsRecibo.getCampos()
                                .get("FECHA_EXPEDICION");

                Date dtFechaRegistro = (Date) registro.getCampos()
                                .get(prefecCons);

                /*- si la fecha del recibo es mayor no permita insertar*/
                if (dtFechaExp.compareTo(dtFechaRegistro) > 0) {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString(msgRecaudarReciboCons));
                    return true;
                }

                if (actualizarDatosRecibo(rsRecibo)) {
                    return true;
                }
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1143"));
                return true;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return false;
    }

    private boolean procesarBarrasConMultiImpuesto() {
        if (registroSub.getCampos().get(barrasCons).toString()
                        .length() == 24) {

            if (!strCons.equals(registroSub.getCampos().get(barrasCons)
                            .toString().substring(0, 5))) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgCodigoIncorrectoCons));
                return true;
            }

            registroSub.getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            registroSub
                                            .getCampos().get(barrasCons)
                                            .toString()
                                            .substring(
                                                            registroSub.getCampos()
                                                                            .get(barrasCons)
                                                                            .toString()
                                                                            .length()
                                                                - 9,
                                                            registroSub.getCampos()
                                                                            .get(barrasCons)
                                                                            .toString()
                                                                            .length()));
        }

        if (registroSub.getCampos().get(barrasCons).toString()
                        .length() == 72) {
            if (!strCons.equals(registroSub.getCampos().get(barrasCons)
                            .toString().substring(20, 25))) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgCodigoIncorrectoCons));
                return true;
            }

            registroSub.getCampos()
                            .put(GeneralParameterEnum.REFERENCIA.getName(),
                                            registroSub.getCampos().get(
                                                            barrasCons)
                                                            .toString()
                                                            .substring(35, 44));
        }

        return false;
    }

    private boolean procesarBarrasSinMultiImpuesto() {
        String cantD = recuperarCantDigitos();
        String barras = registroSub.getCampos().get(barrasCons).toString();

        boolean cond3 = evaluarCondicion(cantD.isEmpty(),
                        idioma.getString("TB_TB2874"));

        if (cond3) {
            return true;
        }

        if (barras.length() < Integer.parseInt(cantD)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2875")
                            .replace("#BARRAS#", barras)
                            .replace("#LONGITUD#", cantD));
            return true;
        }

        asignarValorCampo(registroSub.getCampos(),
                        GeneralParameterEnum.REFERENCIA.getName(),
                        registroSub.getCampos().get(barrasCons),
                        barras.length() == 9);

        if ((barras.length() > 9) && (barras.length() < 72)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1147").replace("#BARRAS#",
                                            barras));
            return true;
        }
        return false;
    }

    private boolean actualizarDatosRecibo(Registro rsRecibo) {
        if (Boolean.parseBoolean(
                        rsRecibo.getCampos().get(anuladoCons).toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1141")
                + "  " + SysmanFunciones.nvlStr(
                                convertirFecha((Date) rsRecibo.getCampos()
                                                .get(cFechaAnulado)),
                                ""));
            return true;
        }

        if (Boolean.parseBoolean(
                        rsRecibo.getCampos().get("PAGO").toString())) {
            indFactura = false;
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(tb1142));
            return true;
        }

        if (!Boolean.parseBoolean(
                        rsRecibo.getCampos().get(anuladoCons).toString())
            && !Boolean.parseBoolean(
                            rsRecibo.getCampos().get("PAGO").toString())) {
            registroSub.getCampos().put(codigoPredioCons,
                            rsRecibo.getCampos().get(codigoPredioCons));

            registroSub.getCampos().put(valorCons,
                            rsRecibo.getCampos().get(valorCons));

            registroSub.getCampos().put("TIPO",
                            rsRecibo.getCampos().get("TIPO"));

            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            registroSub.getCampos().put(numOrdenCons,
                            rsRecibo.getCampos().get(numOrdenCons));

            Date dtFechaPrefec = (Date) registro.getCampos().get(prefecCons);

            Date dtFechaLimite = (Date) rsRecibo.getCampos()
                            .get(cFechaLimite);

            if (dtFechaPrefec.after(dtFechaLimite)) {
                indFactura = true;
                tituloDialogo = idioma.getString("TB_TB1140");
                muestraDialogo = true;
                regAux = new Registro();
                regAux.getCampos().putAll(registroSub.getCampos());
                return true;
            }
        }

        indFactura = true;

        return false;
    }

    /**
     * Consulta la cantidad de digitos que debe tener el codigo de paz
     * y salvos.
     * 
     * @return vacio, en caso de que no exista el codigo 'P' de paz y
     * salvos.
     */
    private String recuperarCantDigitos() {

        String retorno = "";
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.CODIGO.getName(), "P");

        try {

            Registro auxReg;
            auxReg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistropagobancospazsControladorUrlEnum.URL6070
                                                                            .getValue())
                                            .getUrl(), param));

            if (SysmanFunciones.validarCampoVacio(auxReg.getCampos(),
                            "DIGITOS")) {
                retorno = "";
                return retorno;
            }
            retorno = SysmanFunciones.nvl(auxReg.getCampos().get("DIGITOS"), "")
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

        return retorno;

    }

    public void compararFechas(StringBuilder strFecha) {
        Date dtFecha = null;
        Date dtFechaPrefec = null;
        try {
            dtFecha = SysmanFunciones.convertirAFecha(strFecha.toString());
            dtFechaPrefec = SysmanFunciones.convertirAFecha(
                            registro.getCampos().get(prefecCons).toString(),
                            "yyyy-MM-dd");

            if (dtFechaPrefec.before(dtFecha)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgExisteBloqRegCons));
                return;
            }
        }
        catch (ParseException e) {
            Logger.getLogger(RegistropagobancospazsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void compararTamanoRegistroMenorC(int rowNum) {
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() < 24) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgCodigoIncorrectoCons));
            return;
        }
        if ((listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() > 24)
            && (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .get(barrasCons).toString().length() < 72)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgCodigoIncorrectoCons));
            return;
        }
    }

    public void compararTamanoRegistroIgualC(int rowNum) {
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() == 24) {
            if (!(strCons).equals(listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos().get(barrasCons).toString()
                            .substring(0, 5))) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgCodigoIncorrectoCons));
                return;
            }
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            registroSub.getCampos().get(barrasCons).toString()
                                            .substring(
                                                            listaInicial.getDatasource()
                                                                            .get(rowNum
                                                                                % 10)
                                                                            .getCampos()
                                                                            .get(barrasCons)
                                                                            .toString()
                                                                            .length()
                                                                - 9,
                                                            listaInicial.getDatasource()
                                                                            .get(rowNum
                                                                                % 10)
                                                                            .getCampos()
                                                                            .get(barrasCons)
                                                                            .toString()
                                                                            .length()));
        }
        if (registroSub.getCampos().get(barrasCons).toString().length() == 72) {
            if (!(strCons).equals(registroSub.getCampos().get(barrasCons)
                            .toString().substring(20, 25))) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgCodigoIncorrectoCons));
                return;
            }
            registroSub.getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos().get(barrasCons)
                                            .toString().substring(35, 44));
        }
    }

    public void compararTamanoRegistro9C(int rowNum) {
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() < 9) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgCodigoIncorrectoCons));
            return;
        }
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() == 9) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos().get(barrasCons));
        }
    }

    public void compararTamanoRegistroEntreC(int rowNum) {
        if ((listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() > 9)
            && (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .get(barrasCons).toString().length() < 72)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgCodigoIncorrectoCons));
            return;
        }

        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() == 72) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            registroSub.getCampos().get(barrasCons).toString()
                                            .substring(35, 44));
        }
    }

    public void validarAnuladoPagoC(Registro rsRecibo, int rowNum) {
        Date dtFechaPrefec;
        if (!Boolean.parseBoolean(
                        rsRecibo.getCampos().get(anuladoCons).toString())
            && !Boolean.parseBoolean(
                            rsRecibo.getCampos().get("PAGO").toString())) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            codigoPredioCons,
                            rsRecibo.getCampos().get(codigoPredioCons));
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            valorCons, rsRecibo.getCampos().get(valorCons));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put("TIPO", rsRecibo.getCampos().get("TIPO"));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(GeneralParameterEnum.COMPANIA.getName(),
                                            compania);
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            numOrdenCons,
                            rsRecibo.getCampos().get(numOrdenCons));
            dtFechaPrefec = (Date) listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos().get(prefecCons);
            Date dtFechaLimite = (Date) rsRecibo.getCampos()
                            .get(cFechaLimite);

            if (dtFechaPrefec.after(dtFechaLimite)) {
                tituloDialogo = idioma.getString(msgRecaudarReciboCons);
                muestraDialogo = true;

                regAux.getCampos().putAll(listaInicial.getDatasource()
                                .get(rowNum % 10).getCampos());
                return;
            }
        }
    }

    public void compararFechaExpedicionRegistroC(Registro rsRecibo,
        Date dtFechaExp, Date dtFechaRegistro) {
        if (dtFechaExp.compareTo(dtFechaRegistro) < 0) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(msgRecaudarReciboCons));
            return;
        }
        if (Boolean.parseBoolean(
                        rsRecibo.getCampos().get(anuladoCons).toString())) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgRecaudarReciboCons) + "  "
                                + rsRecibo.getCampos().get(cFechaAnulado));
            return;
        }
        if (Boolean.parseBoolean(rsRecibo.getCampos().get("PAGO").toString())) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgRecaudarReciboCons));
            return;
        }
    }

    private boolean procesarBarrasC(int rowNum) {
        String manejaMultimpuesto = SysmanFunciones
                        .nvlStr(recuperarParametro(parManejaCodigo), "NO");

        if ("SI".equals(manejaMultimpuesto)
            && procesarBarrasConMultiImpuestoC(rowNum)) {
            return true;
        }
        else {
            if (procesarBarrasSinMultiImpuestoC(rowNum)) {
                return true;
            }
        }

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.REFERENCIA.getName(),
                        registroSub.getCampos()
                                        .get(GeneralParameterEnum.REFERENCIA
                                                        .getName()));

        Registro rsRecibo;
        try {
            rsRecibo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistropagobancospazsControladorUrlEnum.URL9087
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsRecibo != null) {
                if (actualizarDatosReciboC(rsRecibo, rowNum)) {
                    return true;
                }
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1143"));
                return true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    public void cambiarBarrasC(int rowNum) {
        Date dtFechaPrefec = null;

        try {
            dtFechaPrefec = SysmanFunciones.convertirAFecha(
                            registro.getCampos().get(prefecCons).toString(),
                            "yyyy-MM-dd");

        }
        catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        if (validarProcesoFecha(dtFechaPrefec)) {
            return;
        }

        if ((listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons) == null)
            && (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .get("PREDIO") == null)) {
            return;
        }

        if (procesarBarrasC(rowNum)) {
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean procesarBarrasSinMultiImpuestoC(int rowNum) {
        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() < 9) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgCodigoIncorrectoCons));
            return true;
        }

        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() == 9) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos().get(barrasCons));
        }

        if ((listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() > 9)
            && (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .get(barrasCons).toString().length() < 72)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(msgCodigoIncorrectoCons));
            return true;
        }

        if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length() == 72) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            registroSub.getCampos().get(barrasCons)
                                            .toString()
                                            .substring(35, 44));
        }

        return false;
    }

    /**
     * Evalua el valor de vardad de dos condiciones mediante el
     * operador OR.
     * 
     * @param cond1
     * @param cond2
     * @return
     */
    private boolean validarCondicion(boolean cond1, boolean cond2) {
        return cond1 || cond2;
    }

    private boolean procesarBarrasConMultiImpuestoC(int rowNum) {
        int longitud = listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .get(barrasCons).toString().length();

        boolean cond1 = evaluarCondicion(longitud < 24,
                        idioma.getString(msgCodigoIncorrectoCons));

        boolean cond2 = evaluarCondicion(longitud > 24 && longitud < 72,
                        idioma.getString(msgCodigoIncorrectoCons));

        if (validarCondicion(cond1, cond2)) {
            return true;
        }

        if (longitud == 24) {
            if (!strCons.equals(listaInicial.getDatasource()
                            .get(rowNum % 10).getCampos()
                            .get(barrasCons).toString().substring(0, 5))) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgCodigoIncorrectoCons));
                return true;
            }

            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            registroSub.getCampos().get(barrasCons)
                                            .toString()
                                            .substring(
                                                            listaInicial.getDatasource()
                                                                            .get(rowNum
                                                                                % 10)
                                                                            .getCampos()
                                                                            .get(barrasCons)
                                                                            .toString()
                                                                            .length()
                                                                - 9,
                                                            listaInicial.getDatasource()
                                                                            .get(rowNum
                                                                                % 10)
                                                                            .getCampos()
                                                                            .get(barrasCons)
                                                                            .toString()
                                                                            .length()));
        }

        if (registroSub.getCampos().get(barrasCons).toString()
                        .length() == 72) {
            if (!strCons.equals(registroSub.getCampos().get(barrasCons)
                            .toString().substring(20, 25))) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(msgCodigoIncorrectoCons));
                return true;
            }

            registroSub.getCampos().put(
                            GeneralParameterEnum.REFERENCIA.getName(),
                            listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos().get(barrasCons)
                                            .toString().substring(35, 44));
        }

        return false;
    }

    private boolean actualizarDatosReciboC(Registro rsRecibo, int rowNum) {
        Date dtFechaExp = (Date) rsRecibo.getCampos()
                        .get("FECHA_EXPEDICION");

        Date dtFechaRegistro = (Date) registro.getCampos().get(prefecCons);

        if (dtFechaExp.compareTo(dtFechaRegistro) < 0) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(msgRecaudarReciboCons));
            return true;
        }

        if (!Boolean.parseBoolean(
                        rsRecibo.getCampos().get(anuladoCons).toString())
            && !Boolean.parseBoolean(
                            rsRecibo.getCampos().get("PAGO").toString())) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            codigoPredioCons,
                            rsRecibo.getCampos().get(codigoPredioCons));
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            valorCons, rsRecibo.getCampos().get(valorCons));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put("TIPO", rsRecibo.getCampos().get("TIPO"));
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(GeneralParameterEnum.COMPANIA.getName(),
                                            compania);
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            numOrdenCons,
                            rsRecibo.getCampos().get(numOrdenCons));
            Date dtFechaPrefec = (Date) listaInicial.getDatasource()
                            .get(rowNum % 10).getCampos().get(prefecCons);
            Date dtFechaLimite = (Date) rsRecibo.getCampos()
                            .get(cFechaLimite);

            if (dtFechaPrefec.after(dtFechaLimite)) {
                tituloDialogo = idioma.getString(msgRecaudarReciboCons);
                muestraDialogo = true;

                regAux.getCampos().putAll(listaInicial.getDatasource()
                                .get(rowNum % 10).getCampos());
                return true;
            }
        }

        boolean cond1 = evaluarCondicion(Boolean.parseBoolean(
                        rsRecibo.getCampos().get(anuladoCons).toString()),
                        idioma.getString(msgRecaudarReciboCons)
                            + "  "
                            + rsRecibo.getCampos().get(cFechaAnulado));

        boolean cond2 = evaluarCondicion(Boolean.parseBoolean(
                        rsRecibo.getCampos().get("PAGO").toString()),
                        idioma.getString(msgRecaudarReciboCons));

        if (cond1 || cond2) {
            return true;
        }

        return false;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigobanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(pagbanCons,
                        registroAux.getCampos().get("CODIGOBANCO"));

        registro.getCampos().put("NROCUPONES", 0);
        registro.getCampos().put("VLREPORTADO", 0);
        registro.getCampos().put(numCuponesAcucCons, 0);
        registro.getCampos().put(vlAcumuladoCons, 0);
        nombreBanco = registroAux.getCampos().get("NOMBREBANCO").toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubDpagobancosdet() {
        try {

            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(prefecCons,
                            registro.getCampos().get(prefecCons));

            registroSub.getCampos().put(paqueteCons,
                            registro.getCampos().get(paqueteCons));

            registroSub.getCampos().put(pagbanCons,
                            registro.getCampos().get(pagbanCons));

            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            if (indFactura) {

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                RegistropagobancospazsControladorUrlEnum.URL53221
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(),
                                urlCreate.getMetodo(),
                                registroSub.getCampos());

                ejbPRedialCero.realizarPagoPazySalvo(compania,
                                registroSub.getCampos()
                                                .get(GeneralParameterEnum.REFERENCIA
                                                                .getName())
                                                .toString(),
                                (Date) registroSub.getCampos()
                                                .get(prefecCons),
                                registroSub.getCampos().get(pagbanCons)
                                                .toString(),
                                registroSub.getCampos().get(paqueteCons)
                                                .toString(),
                                SessionUtil.getUser().getCodigo(),
                                Integer.toString(Integer
                                                .parseInt(registro.getCampos()
                                                                .get(numCuponesAcucCons)
                                                                .toString())
                                    + 1),
                                registro.getCampos().get(vlAcumuladoCons)
                                                .toString());

                cargarListaDpagobancosdet();

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString(tb1142));
            }
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(RegistropagobancospazsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
            cargarRegistro(css, "m");
        }
    }

    public void editarRegSubDpagobancosdet(RowEditEvent event) {
        // METODO_NO_IMPLEMENTADO
    }

    public void eliminarRegSubDpagobancosdet(Registro reg) {
        regAux = new Registro(reg.getCampos());
        regAux.getLlave().putAll(reg.getLlave());

        tituloDialogoEliminar = idioma.getString("TB_TB1146").replace(
                        "#referencia#",
                        reg.getCampos().get(GeneralParameterEnum.REFERENCIA
                                        .getName()).toString());
        muestraDialogoEliminar = true;
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
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (css == null) {
            registro.getCampos().put(prefecCons, new Date());
            nombreBanco = null;
            blockUpdate = false;
        }
        else {

            Map<String, Object> param = new TreeMap<>();

            param.put(RegistropagobancospazsControladorEnum.CODIGOBANCO
                            .getValue(), registro.getCampos().get(pagbanCons));
            try {
                nombreBanco = listacodigobanco
                                .getRegistroUnico(param).getCampos()
                                .get("NOMBREBANCO").toString();

                registroSub.getCampos().put(prefecCons,
                                registro.getCampos().get(prefecCons));

                registroSub.getCampos().put(pagbanCons,
                                registro.getCampos().get(pagbanCons));

                registroSub.getCampos().put(paqueteCons,
                                registro.getCampos().get(paqueteCons));

                blockUpdate = true;

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
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
    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public boolean isMuestraDialogo() {
        return muestraDialogo;
    }

    public void setMuestraDialogo(boolean muestraDialogo) {
        this.muestraDialogo = muestraDialogo;
    }

    public Registro getRegAux() {
        return regAux;
    }

    public void setRegAux(Registro regAux) {
        this.regAux = regAux;
    }

    public String getTituloDialogo() {
        return tituloDialogo;
    }

    public void setTituloDialogo(String tituloDialogo) {
        this.tituloDialogo = tituloDialogo;
    }

    public boolean isMuestraDialogoEliminar() {
        return muestraDialogoEliminar;
    }

    public void setMuestraDialogoEliminar(boolean muestraDialogoEliminar) {
        this.muestraDialogoEliminar = muestraDialogoEliminar;
    }

    public String getTituloDialogoEliminar() {
        return tituloDialogoEliminar;
    }

    public void setTituloDialogoEliminar(String tituloDialogoEliminar) {
        this.tituloDialogoEliminar = tituloDialogoEliminar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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

    public void setListaDpagobancosdet(List<Registro> listaDpagobancosdet) {
        this.listaDpagobancosdet = listaDpagobancosdet;
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

    public boolean isBlockUpdate() {
        return blockUpdate;
    }

    public void setBlockUpdate(boolean blockUpdate) {
        this.blockUpdate = blockUpdate;
    }

    // </SET_GET_ADICIONALES>
}
