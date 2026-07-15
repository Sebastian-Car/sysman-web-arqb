package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialDosRemote;
import com.sysman.predial.enums.ModificaciondepagosControladorEnum;
import com.sysman.predial.enums.ModificaciondepagosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
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
 * @author lcortes
 * @version 1, 26/05/2016
 * @author lcortes
 * @version 1, 24/08/2016 11:46:56 -- Modificado por lcortes
 * 
 * @author eamaya
 * @version 2.0,07/07/2017, Proceso de Refactoring , Manejo de EJBs ,
 * cambio de textos quemados por Texto en Bean y correcciones
 * SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class ModificaciondepagosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante que almacena la compania
     */
    private final String compania;
    /**
     * Constante que almacena la cadena BANCO
     */
    private final String banco;
    /**
     * Constante que almacena la cadena FACTURA
     */
    private final String facturaC;
    /**
     * Constante que almacena la cadena CODIGO
     */
    private final String codigo;
    /**
     * Constante que almacena la cadena NOMBRE
     */
    private final String nombreC;

    /**
     * Constante que almacena la cadena FECHA
     */
    private final String fecha;
    /**
     * Constante que almacena la cadena MODIFICACION
     */
    private final String modificacionC;
    /**
     * Constante que almacena la cadena PAG_BAN
     */
    private final String pagBan;
    /**
     * Constante que almacena la cadena PAQUETE
     */
    private final String paqueteC;
    /**
     * Constante que almacena la cadena PREFEC
     */
    private final String prefec;
    /**
     * Constante que almacena la cadena PREVAL
     */
    private final String preval;
    /**
     * Constante que almacena la cadena TIPO_MODIFICACION
     */
    private final String tipoModificacion;
    /**
     * Constante que almacena la cadena VALOR
     */
    private final String valorC;

    // <DECLARAR_ATRIBUTOS>
    private String anularRecibo;
    private String activarRecibo;
    private String numeroRecibo;
    private String nombre;
    private String cedula;
    private String nBanco;
    private String nFecha;
    private String nPaquete;
    private String nValor;
    private String tipoRecibo;
    private String valor;
    private String campoAnterior;
    private boolean modificarCodigo;
    private boolean modificarFactura;
    private boolean modificarBanco;
    private boolean modificarFecha;
    private boolean modificarPaquete;
    private boolean modificarValor;
    private boolean modificarAnularRecibo;
    private boolean modificarActivarRecibo;
    private boolean modificarNumeroRecibo;
    private Registro registroModificaciones;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listabanco;
    private RegistroDataModelImpl listaANULAR;
    private RegistroDataModelImpl listaACTIVAR;
    private RegistroDataModelImpl listanumeroRecibo;
    private RegistroDataModelImpl listacodigo;
    private RegistroDataModelImpl listafactura;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    @EJB
    private EjbPredialDosRemote ejbPredialDos;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    public ModificaciondepagosControlador() {
        super();
        compania = SessionUtil.getCompania();
        banco = "BANCO";
        facturaC = "FACTURA";
        codigo = "CODIGO";
        nombreC = "NOMBRE";

        fecha = "FECHA";
        modificacionC = "MODIFICACION";
        pagBan = "PAG_BAN";
        paqueteC = "PAQUETE";
        prefec = "PREFEC";
        preval = "PREVAL";
        tipoModificacion = "TIPO_MODIFICACION";
        valorC = "VALOR";
        try {
            numFormulario = GeneralCodigoFormaEnum.MODIFICACIONDEPAGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ModificaciondepagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @Override
    public void iniciarListas() {

        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListacodigo();
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
        enumBase = GenericUrlEnum.IP_MODIFICACIONES_PAGOS;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListabanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificaciondepagosControladorUrlEnum.URL6535
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listabanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGOBANCO");
    }

    public void cargarListaANULAR() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificaciondepagosControladorUrlEnum.URL7117
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(codigo));

        listaANULAR = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.DOCNUM.getName());
    }

    public void cargarListaACTIVAR() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificaciondepagosControladorUrlEnum.URL7932
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(codigo));

        listaACTIVAR = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.DOCNUM.getName());
    }

    public void cargarListanumeroRecibo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificaciondepagosControladorUrlEnum.URL9216
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(codigo));
        param.put(GeneralParameterEnum.FACTURA.getName(),
                        registro.getCampos().get(facturaC));

        listanumeroRecibo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.DOCNUM.getName());

    }

    public void cargarListacodigo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificaciondepagosControladorUrlEnum.URL10052
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    public void cargarListafactura() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ModificaciondepagosControladorUrlEnum.URL10767
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(codigo));

        listafactura = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "PRECOD");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiartipoModificacion() {
        modificarCodigo = false;
        modificarFactura = false;
        modificarBanco = false;
        modificarFecha = false;
        modificarPaquete = false;
        modificarValor = false;
        modificarAnularRecibo = false;
        modificarActivarRecibo = false;
        modificarNumeroRecibo = false;
        cargarListacodigo();
        cargarListafactura();

        if ("01".equals(registro.getCampos().get(tipoModificacion))) // modificar
                                                                     // un
                                                                     // banco
        {
            modificarCodigo = true;
            modificarFactura = true;
            modificarBanco = true;
            anularRecibo = null;
            activarRecibo = null;
            cargarListabanco();
            campoAnterior = "ANT_BANCO";
        }
        if ("02".equals(registro.getCampos().get(tipoModificacion))) // modificar
                                                                     // fecha
                                                                     // de
                                                                     // pago
        {
            modificarCodigo = true;
            modificarFactura = true;
            modificarFecha = true;
            anularRecibo = null;
            activarRecibo = null;
            campoAnterior = "ANT_FECHA";
        }
        if ("03".equals(registro.getCampos().get(tipoModificacion))) // modificar
                                                                     // paquete
        {
            modificarCodigo = true;
            modificarFactura = true;
            modificarPaquete = true;
            anularRecibo = null;
            activarRecibo = null;
            campoAnterior = "ANT_PAQUETE";
        }
        if ("04".equals(registro.getCampos().get(tipoModificacion))) // modificar
                                                                     // valor
                                                                     // de
                                                                     // pago
        {
            modificarCodigo = true;
            modificarFactura = true;
            modificarValor = true;
            campoAnterior = "ANT_VALOR";
        }
        if ("05".equals(registro.getCampos().get(tipoModificacion))) // anular
                                                                     // recibo
        {
            modificarCodigo = true;
            modificarAnularRecibo = true;
            activarRecibo = null;
            cargarListaANULAR();
            if (!"".equals(registro.getCampos().get(codigo))
                || (registro.getCampos().get(codigo) != null)) {
                contarRegLisAnula();
            }
            campoAnterior = "ANULAR_RECIBO";

        }
        evaluarTipo();

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void evaluarTipo() {
        if ("06".equals(registro.getCampos().get(tipoModificacion))) // activar
                                                                     // recibo
        {
            modificarCodigo = true;
            modificarFactura = false;
            modificarActivarRecibo = true;
            anularRecibo = null;
            cargarListaACTIVAR();
            if (!"".equals(registro.getCampos().get(codigo))
                || (registro.getCampos().get(codigo) != null)) {
                contarRegLisAct();
            }
            campoAnterior = "ACTIVAR_RECIBO";
        }
        if ("07".equals(registro.getCampos().get(tipoModificacion))) // nuevo
                                                                     // numero
                                                                     // de
                                                                     // factura
        {
            modificarCodigo = true;
            modificarAnularRecibo = true;
            activarRecibo = null;
            cargarListaANULAR();
            if (!"".equals(registro.getCampos().get(codigo))
                || (registro.getCampos().get(codigo) != null)) {
                contarRegLisAnula();
            }
        }
        if ("08".equals(registro.getCampos().get(tipoModificacion))) // invertir
                                                                     // numero
                                                                     // de
                                                                     // factura
        {
            modificarCodigo = true;
            modificarFactura = true;
            modificarNumeroRecibo = true;
            anularRecibo = null;
            activarRecibo = null;
            cargarListanumeroRecibo();
        }
    }

    public void cambiarfecha() {
        registro.getCampos().put("ANT_FECHA",
                        registro.getCampos().get("FECHAANT"));
    }

    public void cambiarvalor() {
        registro.getCampos().put("ANT_VALOR", registro.getCampos().get(valorC));
        String valorAux = valor.trim();
        if (valorAux.contains("$")) {
            valorAux = valorAux.substring(1, valorAux.length());
            valorAux = valorAux.trim();
        }
        registro.getCampos().put(valorC, valorAux);
    }

    public void cambiarpaquete() {
        registro.getCampos().put("ANT_PAQUETE",
                        registro.getCampos().get("PAQANT"));
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilabanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ANT_BANCO", registro.getCampos().get(banco));
        registro.getCampos().put(banco,
                        registroAux.getCampos().get("CODIGOBANCO"));
    }

    public void seleccionarFilaANULAR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        anularRecibo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.DOCNUM.getName()),
                                        "")
                        .toString();
    }

    public void seleccionarFilaACTIVAR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        activarRecibo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.DOCNUM.getName()),
                                        "")
                        .toString();
    }

    public void seleccionarFilanumeroRecibo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroRecibo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.DOCNUM.getName()),
                                        "")
                        .toString();
    }

    public void seleccionarFilacodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigo, registroAux.getCampos().get(codigo));
        nombre = SysmanFunciones.nvl(registroAux.getCampos().get(nombreC), "")
                        .toString();
        cedula = idioma.getString("TG_CEDULA7") + " "
            + SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), " ")
                            .toString();
        registro.getCampos().put("NUMERO_ORDEN",
                        registroAux.getCampos().get("NUMERO_ORDEN"));
        registro.getCampos().put(facturaC, "");
        nBanco = "";
        nPaquete = "";
        nFecha = "";
        nValor = "";
        if (modificarFactura) {
            cargarListafactura();
            contarRegLisFact();
        }
        if ("05".equals(registro.getCampos().get(tipoModificacion))) {
            cargarListaANULAR();
            contarRegLisAnula();
        }
        else if ("06".equals(registro.getCampos().get(tipoModificacion))) {
            cargarListaACTIVAR();
            contarRegLisAct();
        }
        else if ("08".equals(registro.getCampos().get(tipoModificacion))) {
            cargarListanumeroRecibo();
        }
    }

    public void seleccionarFilafactura(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        String factura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMFACTURA"), "")
                        .toString();
        registro.getCampos().put(facturaC, factura);
        nBanco = idioma.getString("TG_BANCO4") + " "
            + SysmanFunciones.nvl(registroAux.getCampos().get(pagBan), "")
                            .toString();
        nPaquete = idioma.getString("TG_PAQUETE") + " "
            + SysmanFunciones.nvl(registroAux.getCampos().get(paqueteC), "")
                            .toString();
        try {
            String fecha1 = SysmanFunciones.convertirAFechaCadena(
                            (Date) registroAux.getCampos().get(prefec));
            nFecha = idioma.getString("TG_FECHA5") + " " + fecha1;
            BigDecimal valor1 = new BigDecimal(SysmanFunciones
                            .nvl(registroAux.getCampos()
                                            .get(preval), "0")
                            .toString());
            nValor = idioma.getString("TG_VALOR5") + " " + valor1.toString();
            if ("05".equals(registro.getCampos().get(tipoModificacion))) {
                cargarListaANULAR();
            }
            else if ("06".equals(registro.getCampos().get(tipoModificacion))) {
                cargarListaACTIVAR();
            }
            else if ("08".equals(registro.getCampos().get(tipoModificacion))) {
                cargarListanumeroRecibo();
            }

        }
        catch (ParseException e) {
            Logger.getLogger(ModificaciondepagosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirregistrar() {
        String modificacion = registro.getCampos().get(modificacionC) == null
            ? ""
            : registro.getCampos().get(modificacionC).toString();
        if ("i".equals(accion) && "".equals(modificacion)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1231"));
            return;
        }

        if (verificarCampos()) {

            if ("0".equals(registroModificaciones.getCampos().get(modificacionC)
                            .toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1231"));
                return;
            }
            identificarTipoRecibo();
            ejecutarFuncion();

        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarFuncion() {
        String activarAux;
        String anularAux;
        String campoAux;
        try {
            activarAux = (activarRecibo == null) || "".equals(activarRecibo)
                ? "NULL" : activarRecibo;
            anularAux = (anularRecibo == null) || "".equals(anularRecibo)
                ? "NULL" : anularRecibo;
            campoAux = (campoAnterior == null) || "".equals(campoAnterior)
                ? "NULL" : campoAnterior;

            String factura1 = (registro.getCampos().get(facturaC) == null)
                || "".equals(registro.getCampos().get(facturaC)) ? "NULL"
                    : registro.getCampos().get(facturaC).toString();
            String banco1 = registro.getCampos().get(banco) == null ? "NULL"
                : registro.getCampos().get(banco).toString();
            Date fecha1 = (Date) registro.getCampos().get(fecha) == null
                ? null
                : (Date) registro
                                .getCampos().get(fecha);
            String paquete = registro.getCampos().get(paqueteC) == null ? "NULL"
                : registro.getCampos().get(paqueteC).toString();
            String valorn = registro.getCampos().get(valorC) == null ? "NULL"
                : registro.getCampos().get(valorC).toString();

            String res = ejbPredialDos.modificarRecibos(compania,
                            registro.getCampos().get(modificacionC).toString(),
                            tipoRecibo,
                            registro.getCampos().get(tipoModificacion)
                                            .toString(),
                            (Date) registro.getCampos()
                                            .get("FECHA_MODIFICACION"),
                            registro.getCampos().get(codigo).toString(),
                            factura1,
                            fecha1,
                            banco1,
                            paquete,
                            new BigDecimal(valorn),
                            anularAux,
                            activarAux,
                            campoAux, SessionUtil.getUser().getCodigo());

            evaluarFuncion(res);
        }
        catch (SystemException e) {
            Logger.getLogger(ModificaciondepagosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void evaluarFuncion(String res) {
        if (!SysmanFunciones.validarVariableVacio(res)) {
            if ("-1".equals(res)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1188"));
                return;
            }
            else if ("0".equals(res)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1203"));
                return;
            }
            else if ("IP_".equals(res)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1189"));
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB1232") + " " + res);
                accion = "v";
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1189"));
                valor = registro.getCampos().get(valorC).toString();
                accion = "v";
            }
        }
    }

    public void oprimirverModificaciones() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public boolean verificarCampos() {

        if ((registro.getCampos().get(tipoModificacion) == null)
            || "".equals(registro.getCampos().get(tipoModificacion))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB817"));
            return false;

        }
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(ModificaciondepagosControladorEnum.PARAM0.getValue(),
                        registro.getCampos().get("MODIFICACION"));

        param.put(ModificaciondepagosControladorEnum.PARAM1.getValue(),
                        registro.getCampos().get("TIPO_MODIFICACION"));

        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(codigo));

        if (!verificarTipoModificacion(param)) {
            return false;
        }

        return true;

    }

    private boolean verificarTipoModificacion(Map<String, Object> param) {
        if (!modificarUnBanco(param)) {
            return false;
        }

        if (!modificarFechaPago(param)) {
            return false;
        }

        if (!modificarPaquete(param)) {
            return false;
        }

        if (!modificarValorPago(param)) {
            return false;
        }

        if (!modificarAnularRecibo(param)) {
            return false;
        }

        if (!modifcarActivarRecibo(param)) {
            return false;
        }

        return true;
    }

    private boolean modifcarActivarRecibo(Map<String, Object> param) {
        if ("06".equals(registro.getCampos().get(tipoModificacion))
            && ((activarRecibo == null) || ("".equals(activarRecibo))))
        // activar
        // recibo
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB829"));
            return false;
        }
        else {
            try {
                registroModificaciones = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ModificaciondepagosControladorUrlEnum.URL1345
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        return true;
    }

    private boolean modificarAnularRecibo(Map<String, Object> param) {
        if ("05".equals(registro.getCampos().get(tipoModificacion))
            && ((anularRecibo == null) || ("".equals(anularRecibo))))
        // anular
        // recibo
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB828"));
            return false;
        }
        else {
            try {
                registroModificaciones = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ModificaciondepagosControladorUrlEnum.URL1345
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return true;
    }

    private boolean modificarValorPago(Map<String, Object> param) {
        if ("04".equals(registro.getCampos().get(tipoModificacion))) // modificar
        // valor
        // de
        // pago
        {
            if ((valor == null) || "".equals(valor)
                || (registro.getCampos().get(facturaC) == null)
                || "".equals(registro.getCampos().get(facturaC))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB821"));
                return false;
            }
            else {

                param.put(GeneralParameterEnum.VALOR.getName(),
                                registro
                                                .getCampos().get(valorC)
                                                .toString());

                try {
                    registroModificaciones = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    ModificaciondepagosControladorUrlEnum.URL1344
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
        }
        return true;
    }

    private boolean modificarPaquete(Map<String, Object> param) {

        if ("03".equals(registro.getCampos().get(tipoModificacion))) // modificar
                                                                     // paquete
        {
            if ((registro.getCampos().get(paqueteC) == null)
                || ("".equals(registro.getCampos().get(paqueteC)))
                || (registro.getCampos().get(facturaC) == null)
                || "".equals(registro.getCampos().get(facturaC))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB820"));
                return false;
            }
            else {

                param.put(GeneralParameterEnum.PAQUETE.getName(), registro
                                .getCampos().get(paqueteC).toString());

                try {
                    registroModificaciones = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    ModificaciondepagosControladorUrlEnum.URL1343
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
        }
        return true;
    }

    private boolean modificarFechaPago(Map<String, Object> param) {
        if ("02".equals(registro.getCampos().get(tipoModificacion))) // modificar
        // fecha
        // de
        // pago
        {
            if ((registro.getCampos().get(fecha) == null)
                || (registro.getCampos().get(facturaC) == null)
                || "".equals(registro.getCampos().get(facturaC))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB819"));
                return false;
            }
            else {

                param.put(GeneralParameterEnum.FECHA.getName(),
                                registro.getCampos().get(fecha));

                try {
                    registroModificaciones = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    ModificaciondepagosControladorUrlEnum.URL1342
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
        }
        return true;
    }

    private boolean modificarUnBanco(Map<String, Object> param) {
        if ("01".equals(registro.getCampos().get(tipoModificacion))) // modificar
        // un
        // banco
        {
            if ((registro.getCampos().get(banco) == null)
                || "".equals(registro.getCampos().get(banco))
                || (registro.getCampos().get(facturaC) == null)
                || "".equals(registro.getCampos().get(facturaC))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB818"));
                return false;
            }
            else {

                param.put(GeneralParameterEnum.BANCO.getName(),
                                registro.getCampos().get(banco).toString());

                try {
                    registroModificaciones = RegistroConverter.toRegistro(
                                    requestManager.get(
                                                    UrlServiceUtil.getInstance()
                                                                    .getUrlServiceByUrlByEnumID(
                                                                                    ModificaciondepagosControladorUrlEnum.URL1341
                                                                                                    .getValue())
                                                                    .getUrl(),
                                                    param));
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
        }
        return true;
    }

    public void identificarTipoRecibo() {
        tipoRecibo = "";
        String factura1 = (registro.getCampos().get(facturaC) == null)
            || "".equals(registro.getCampos().get(facturaC)) ? "N.A"
                : registro.getCampos().get(facturaC).toString();
        try {
            if (!"N.A".equals(factura1)) {

                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.DOCNUM.getName(), factura1);

                Registro regFactura;

                regFactura = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ModificaciondepagosControladorUrlEnum.URL1350
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                boolean esAbono = (boolean) SysmanFunciones
                                .nvl(regFactura.getCampos()
                                                .get("ESABONO"), false);
                boolean esAcuerdo = (boolean) SysmanFunciones
                                .nvl(regFactura.getCampos()
                                                .get("ESACUERDO"), false);
                boolean esCuota = (boolean) SysmanFunciones
                                .nvl(regFactura.getCampos()
                                                .get("ESCUOTA"), false);

                if (esAbono) {
                    tipoRecibo = "1";
                }
                else if (esAcuerdo) {
                    tipoRecibo = "2";
                }
                else if (esCuota) {
                    tipoRecibo = "3";
                }
                else {
                    tipoRecibo = "4";
                }
            }
            else {
                tipoRecibo = factura1;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    public void contarRegLisFact() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(codigo));

        Registro rAux;
        try {
            rAux = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ModificaciondepagosControladorUrlEnum.URL1351
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            String fact = rAux.getCampos().get("CANT").toString() == null ? ""
                : rAux.getCampos().get("CANT").toString();
            if ("0".equals(fact)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1365"));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void contarRegLisAct() {
        if (!SysmanFunciones.validarVariableVacio(
                        (String) registro.getCampos().get(codigo))) {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(codigo));

            Registro rAux;
            try {
                rAux = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ModificaciondepagosControladorUrlEnum.URL1352
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                String fact = SysmanFunciones
                                .nvl(rAux.getCampos().get("DNUM"), "0")
                                .toString();
                if ("0".equals(fact)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB816"));
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void contarRegLisAnula() {
        if (!SysmanFunciones.validarVariableVacio(
                        (String) registro.getCampos().get(codigo))) {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(codigo));

            Registro rAux;
            try {
                rAux = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ModificaciondepagosControladorUrlEnum.URL1353
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                String fact = SysmanFunciones
                                .nvl(rAux.getCampos().get("DNUM"), "0")
                                .toString();
                if ("0".equals(fact)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB815"));
                }

            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

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
            modificarCodigo = false;
            modificarFactura = false;
            modificarBanco = false;
            modificarFecha = false;
            modificarPaquete = false;
            modificarValor = false;
            modificarAnularRecibo = false;
            modificarActivarRecibo = false;
            modificarNumeroRecibo = false;
            registro.getCampos().put("USUARIO_MODIFICACION",
                            SessionUtil.getUser().getCodigo());
            registro.getCampos().put("FECHA_MODIFICACION", new Date());
            nombre = "";
            cedula = "";
            valor = "";
            nBanco = "";
            nPaquete = "";
            nFecha = "";
            nValor = "";
            anularRecibo = "";
            activarRecibo = "";

        }
        else {
            cambiartipoModificacion();
            try {
                nombre = SysmanFunciones.nvl(
                                registro.getCampos().get(nombreC),
                                "").toString();
                cedula = idioma.getString("TG_CEDULA7")
                    + " "
                    + SysmanFunciones.nvl(registro.getCampos().get("NIT"), "")
                                    .toString();
                valor = SysmanFunciones.nvl(registro.getCampos().get(valorC),
                                "0").toString();

                if (registro.getCampos().get(facturaC) != null) {
                    nBanco = idioma.getString("TG_BANCO4") + " "
                        + SysmanFunciones.nvl(registro.getCampos().get(pagBan),
                                        "").toString();
                    nPaquete = idioma.getString("TG_PAQUETE")
                        + SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        "PAQUETEBANCDET"), "")
                                        .toString();
                    String fecha1 = SysmanFunciones.convertirAFechaCadena(
                                    (Date) registro.getCampos().get(prefec));
                    nFecha = idioma.getString("TG_FECHA5") + " " + fecha1;
                    nValor = idioma.getString("TG_VALOR5") + " "
                        + SysmanFunciones.nvl(registro.getCampos().get(preval),
                                        "").toString();
                }
                else {
                    nBanco = "";
                    nPaquete = "";
                    nFecha = "";
                    nValor = "";
                    cargarListafactura();
                }
                if ((boolean) registro.getCampos().get("ANULAR_PAGO")) {
                    anularRecibo = SysmanFunciones
                                    .nvl(registro.getCampos().get(facturaC), "")
                                    .toString();
                }
                if ((boolean) registro.getCampos().get("ANULAR_RECIBO")) {
                    anularRecibo = SysmanFunciones
                                    .nvl(registro.getCampos().get(facturaC), "")
                                    .toString();
                }
            }
            catch (ParseException e) {
                Logger.getLogger(ModificaciondepagosControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        try {

            registro.getCampos().put("COMPANIA", compania);
            registro.getCampos()
                            .put(modificacionC,
                                            ejbSysmanUtil.generarConsecutivoConValorInicial(
                                                            "IP_MODIFICACIONES_PAGOS",
                                                            " COMPANIA = "
                                                                + compania,
                                                            modificacionC, "1")

            );
        }
        catch (SystemException e) {
            Logger.getLogger(ModificaciondepagosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        if (verificarCampos()) {

            if ("m".equals(accion)) {
                registro.getCampos().remove(
                                GeneralParameterEnum.COMPANIA.getName());
                registro.getCampos().remove("TIPO_MODIFICACIONLB");
                registro.getCampos().remove("PAQANT");
                registro.getCampos().remove("FECHAANT");
            }

            registro.getCampos().remove(preval);
            registro.getCampos().remove("NOMBREBANCO");
            registro.getCampos().remove(nombreC);
            registro.getCampos().remove("NIT");
            registro.getCampos().remove(pagBan);
            registro.getCampos().remove("NUMFACTURA");
            registro.getCampos().remove("PAQUETEBANCDET");
            registro.getCampos().remove(prefec);
            registro.getCampos().remove(preval);
            return true;
        }
        else {
            return false;
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

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
    public String getAnularRecibo() {
        return anularRecibo;
    }

    public void setAnularRecibo(String anularRecibo) {
        this.anularRecibo = anularRecibo;
    }

    public String getActivarRecibo() {
        return activarRecibo;
    }

    public void setActivarRecibo(String activarRecibo) {
        this.activarRecibo = activarRecibo;
    }

    public String getNumeroRecibo() {
        return numeroRecibo;
    }

    public void setNumeroRecibo(String numeroRecibo) {
        this.numeroRecibo = numeroRecibo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getnBanco() {
        return nBanco;
    }

    public void setnBanco(String nBanco) {
        this.nBanco = nBanco;
    }

    public String getnFecha() {
        return nFecha;
    }

    public void setnFecha(String nFecha) {
        this.nFecha = nFecha;
    }

    public String getnPaquete() {
        return nPaquete;
    }

    public void setnPaquete(String nPaquete) {
        this.nPaquete = nPaquete;
    }

    public String getnValor() {
        return nValor;
    }

    public void setnValor(String nValor) {
        this.nValor = nValor;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public boolean isModificarCodigo() {
        return modificarCodigo;
    }

    public void setModificarCodigo(boolean modificarCodigo) {
        this.modificarCodigo = modificarCodigo;
    }

    public boolean isModificarFactura() {
        return modificarFactura;
    }

    public void setModificarFactura(boolean modificarFactura) {
        this.modificarFactura = modificarFactura;
    }

    public boolean isModificarBanco() {
        return modificarBanco;
    }

    public void setModificarBanco(boolean modificarBanco) {
        this.modificarBanco = modificarBanco;
    }

    public boolean isModificarFecha() {
        return modificarFecha;
    }

    public void setModificarFecha(boolean modificarFecha) {
        this.modificarFecha = modificarFecha;
    }

    public boolean isModificarPaquete() {
        return modificarPaquete;
    }

    public void setModificarPaquete(boolean modificarPaquete) {
        this.modificarPaquete = modificarPaquete;
    }

    public boolean isModificarValor() {
        return modificarValor;
    }

    public void setModificarValor(boolean modificarValor) {
        this.modificarValor = modificarValor;
    }

    public boolean isModificarAnularRecibo() {
        return modificarAnularRecibo;
    }

    public void setModificarAnularRecibo(boolean modificarAnularRecibo) {
        this.modificarAnularRecibo = modificarAnularRecibo;
    }

    public boolean isModificarActivarRecibo() {
        return modificarActivarRecibo;
    }

    public void setModificarActivarRecibo(boolean modificarActivarRecibo) {
        this.modificarActivarRecibo = modificarActivarRecibo;
    }

    public boolean isModificarNumeroRecibo() {
        return modificarNumeroRecibo;
    }

    public void setModificarNumeroRecibo(boolean modificarNumeroRecibo) {
        this.modificarNumeroRecibo = modificarNumeroRecibo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListabanco() {
        return listabanco;
    }

    public void setListabanco(RegistroDataModelImpl listabanco) {
        this.listabanco = listabanco;
    }

    public RegistroDataModelImpl getListaANULAR() {
        return listaANULAR;
    }

    public void setListaANULAR(RegistroDataModelImpl listaANULAR) {
        this.listaANULAR = listaANULAR;
    }

    public RegistroDataModelImpl getListaACTIVAR() {
        return listaACTIVAR;
    }

    public void setListaACTIVAR(RegistroDataModelImpl listaACTIVAR) {
        this.listaACTIVAR = listaACTIVAR;
    }

    public RegistroDataModelImpl getListanumeroRecibo() {
        return listanumeroRecibo;
    }

    public void setListanumeroRecibo(RegistroDataModelImpl listanumeroRecibo) {
        this.listanumeroRecibo = listanumeroRecibo;
    }

    public RegistroDataModelImpl getListacodigo() {
        return listacodigo;
    }

    public void setListacodigo(RegistroDataModelImpl listacodigo) {
        this.listacodigo = listacodigo;
    }

    public RegistroDataModelImpl getListafactura() {
        return listafactura;
    }

    public void setListafactura(RegistroDataModelImpl listafactura) {
        this.listafactura = listafactura;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
