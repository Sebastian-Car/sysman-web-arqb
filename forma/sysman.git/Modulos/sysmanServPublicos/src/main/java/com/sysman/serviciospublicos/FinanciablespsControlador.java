package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSieteRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.FinanciablespsControladorEnum;
import com.sysman.serviciospublicos.enums.FinanciablespsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
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
 * FinanciablespsControlador Controlador que realiza las funciones
 * CRUD del formulario 1107 financiablessp.
 *
 * @author jrodriguezr
 * @version 1, 23/09/2016 08:15:23 -- Modificado por jrodriguezr
 *
 * @version 2, 25/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla.
 *
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.
 */
@ManagedBean
@ViewScoped
public class FinanciablespsControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Constante que almacena el codigo de la compania de ingreso.
     */
    private final String compania;
    private final String codigo;
    private final String periodoC;
    private final String codigoRutaC;
    private final String nroCuotaC;
    private final String conceptoC;
    private final String saldoFinanciableC;
    private final String numeroCuotasC;
    private final String montoFinanciarC;
    private final String valorCuotaC;
    private final String formatoMonedaC;
    private final String anioInicialC;
    private final String periodoInicialC;
    private final String bancoPerProcesoC;
    private final String bloqueadoHastaAnoC;
    private final String createdByC;
    private final String dateCreatedC;
    private final String consNombreConcepto;
    private final String consBloqueado;
    private boolean permiteEliminar;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Valor del atributo correspondiente al codigo de Ruta
     * correspondiente al usuario
     */
    private String codigoRuta;
    /**
     * Valor del atributo correspondiente al nombre del Usuario a
     * financiar.
     */
    private String nombreUsuario;
    /**
     * Valor del atributo ciclo correspondiente al ciclo que se envia
     * por parametro desde el formulario pedirCiclo 1045.
     */
    private String ciclo;
    /**
     * Valor del atributo anio correspondiente al anio que se envia
     * por parametro desde el formulario pedirCiclo 1045.
     */
    private String anio;
    /**
     * Valor del atributo periodo correspondiente al periodo que se
     * envia por parametro desde el formulario pedirCiclo 1045.
     */
    private String periodo;

    private String totalSaldoFin;
    private String totalMonto;
    private String totalCuotas;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de objetos pertenecientes al combo Anios
     */
    private List<Registro> listaAnios;
    /**
     * Lista de objetos pertenecientes al combo Periodos
     */
    private List<Registro> listaPeriodos;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de objetos pertenecientes al combo grande Usuarios
     */
    private RegistroDataModelImpl listaUsuarios;
    /**
     * Lista de objetos pertenecientes al combo grande Usuarios en la
     * lista del registro al editar.
     */
    private RegistroDataModelImpl listaUsuariosE;
    /**
     * Lista de valores pertenecientes al combo grande Conceptos que
     * permite seleccionar un registro al editarlo.
     */
    private RegistroDataModelImpl listaConceptos;
    /**
     * Lista de valores pertenecientes al combo grande
     */
    private RegistroDataModelImpl listaConceptosE;
    /**
     * Valor del atributo correspondiente al auxiliar que se utiliza
     * cuando se selecciona un dato en un combo grande.
     */
    private String auxiliar;
    private String mensajeDialogo;
    private boolean muestraDialogo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private boolean calcula;
    private String etiquetaAceptar;
    private String etiquetaCancelar;
    private String opcionDialogo;
    private Object factura;
    private String bancoPerProceso;
    private String codigoInterno;
    private String perIni;
    private String anioIni;
    private Registro regAux;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCeroRemote;

    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTresRemote;

    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDosRemote;

    @EJB
    private EjbServiciosPublicosSieteRemote ejbServiciosPublicosSieteRemote;
    private String perConcepto32;
    private String gestionaConcFin;
    private String conceptos;

    /**
     * Crea una nueva instancia de FinanciablespsControlador
     */
    public FinanciablespsControlador() {
        super();

        compania = SessionUtil.getCompania();
        codigo = "CODIGO";
        periodoC = "PERIODO";
        codigoRutaC = "CODIGORUTA";
        nroCuotaC = "NROCUOTA";
        conceptoC = "CONCEPTO";
        saldoFinanciableC = "SALDOFINANCIABLE";
        numeroCuotasC = "NUMEROCUOTAS";
        montoFinanciarC = "MONTOFINANCIAR";
        valorCuotaC = "VALORCUOTA";
        formatoMonedaC = "$ #,##0.00";
        anioInicialC = "ANOINICIAL";
        periodoInicialC = "PERIODOINICIAL";
        bancoPerProcesoC = "BANCOPERPROCESO";
        bloqueadoHastaAnoC = "BLOQUEADOHASTAANO";
        createdByC = "CREATED_BY";
        dateCreatedC = "DATE_CREATED";
        consNombreConcepto = "NOMBRECONCEPTO";
        consBloqueado = "BLOQUEADO";

        try {
            // 1107
            numFormulario = GeneralCodigoFormaEnum.FINANCIABLESPS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosHash = SessionUtil.getFlash();
            ciclo = (String) parametrosHash.get("ciclo");
            periodo = (String) parametrosHash.get("periodo");
            anio = (String) parametrosHash.get("anio");
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FinanciablespsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * Metodo que inicializa el formulario listas de combos y asigna
     * el origen de la listaInicial
     */
    @PostConstruct
    public void inicializar() {

        /**
         * Valor del atributo correspondiente a
         */
        enumBase = GenericUrlEnum.SP_FINANCIABLES;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAnios();
        cargarListaPeriodos();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaUsuarios();
        cargarListaUsuariosE();
        cargarListaConceptos();
        cargarListaConceptosE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     *
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRuta == null ? "" : codigoRuta);
        parametrosListado.put("ANIO", anio);
        parametrosListado.put("PERIODOFIN", periodo);
        ejecutaractualizarTotales();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Metodo que realiza la carga de los elementos de la lista Anios
     */
    public void cargarListaAnios() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            listaAnios = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablespsControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que realiza la carga de los elementos de la lista
     * Periodos.
     */
    public void cargarListaPeriodos() {
        try {
            String bloqueaHastaAno = registro.getCampos()
                            .get(bloqueadoHastaAnoC) == null
                                ? "-1"
                                : registro.getCampos()
                                                .get(bloqueadoHastaAnoC)
                                                .toString();

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(FinanciablespsControladorEnum.PARAM0.getValue(),
                            bloqueaHastaAno);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            listaPeriodos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablespsControladorUrlEnum.URL18392
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que realiza la carga de los elementos de la lista
     * Usuarios.
     */
    public void cargarListaUsuarios() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablespsControladorUrlEnum.URL19055
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaUsuarios = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaC);
    }

    /**
     * Metodo que realiza la carga de los elementos de la lista
     * usuarios en el registro continuo Usuarios.
     */
    public void cargarListaUsuariosE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablespsControladorUrlEnum.URL19055
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaUsuariosE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoRutaC);
    }

    /**
     * Metodo que realiza la carga de los elementos de la lista
     * Conceptos.
     */
    public void cargarListaConceptos() {
        UrlBean urlBean;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        if ("SI".equals(perConcepto32)) {
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            FinanciablespsControladorUrlEnum.URL13922
                                            .getValue());
            if ("SI".equals(gestionaConcFin) && (conceptos != null)) {
                param.put(FinanciablespsControladorEnum.PARAM1.getValue(),
                                conceptos);
                urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FinanciablespsControladorUrlEnum.URL13921
                                                                .getValue());
            }
        }
        else {
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            FinanciablespsControladorUrlEnum.URL13924
                                            .getValue());
            if ("SI".equals(gestionaConcFin) && (conceptos != null)) {
                urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FinanciablespsControladorUrlEnum.URL13923
                                                                .getValue());
                param.put(FinanciablespsControladorEnum.PARAM1.getValue(),
                                conceptos);
            }
        }

        listaConceptos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    /**
     * Lista de objetos pertenecientes al combo Conceptos
     */
    public void cargarListaConceptosE() {
        UrlBean urlBean;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        if ("SI".equals(perConcepto32)) {
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            FinanciablespsControladorUrlEnum.URL13922
                                            .getValue());
            if ("SI".equals(gestionaConcFin) && (conceptos != null)) {
                urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FinanciablespsControladorUrlEnum.URL13921
                                                                .getValue());
                param.put(FinanciablespsControladorEnum.PARAM1.getValue(),
                                conceptos);
            }
        }
        else {
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            FinanciablespsControladorUrlEnum.URL13924
                                            .getValue());
            if ("SI".equals(gestionaConcFin) && (conceptos != null)) {
                urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FinanciablespsControladorUrlEnum.URL13923
                                                                .getValue());
                param.put(FinanciablespsControladorEnum.PARAM1.getValue(),
                                conceptos);
            }
        }

        listaConceptosE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cambiarMontoFinanciar() {
        // <CODIGO_DESARROLLADO>
        if ((registro.getCampos().get(montoFinanciarC) != null)
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            numeroCuotasC)
            && !"0".equals(registro.getCampos().get(montoFinanciarC)
                            .toString())) {
            registro.getCampos().put(saldoFinanciableC,
                            registro.getCampos().get(montoFinanciarC));
            registro.getCampos().put(valorCuotaC,
                            Double.parseDouble(registro.getCampos()
                                            .get(saldoFinanciableC).toString())
                                / Double.parseDouble(registro.getCampos()
                                                .get(numeroCuotasC)
                                                .toString()));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNumeroCuotas() {
        // <CODIGO_DESARROLLADO>
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        saldoFinanciableC)
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            numeroCuotasC)) {
            registro.getCampos().put(valorCuotaC,
                            Double.parseDouble(registro.getCampos()
                                            .get(saldoFinanciableC).toString())
                                / Double.parseDouble(registro.getCampos()
                                                .get(numeroCuotasC)
                                                .toString()));

        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarBLOQUEADO() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecutar al presionar en el boton Si o Actual del
     * dialogo
     */
    public void aceptardialogoConfirmar() {
        // <CODIGO_DESARROLLADO>
        regAux.getCampos();
        if ("1".equals(opcionDialogo) && !validarPeriodoSte()) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB1695"));
        }
        else if ("1".equals(opcionDialogo) && validarPeriodoSte()) {
            try {
                validaRegistro(regAux);
                regAux.getCampos().put(createdByC,
                                SessionUtil.getUser().getCodigo());
                regAux.getCampos().put(dateCreatedC, new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.SP_FINANCIABLES
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                regAux.getCampos());

                insertarDespues();
                actualizarDespues();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                Constantes.MSM_REGISTRO_INGRESADO));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else if ("2".equals(opcionDialogo)) {
            calcula = true;
            try {
                regAux.getCampos().put(createdByC,
                                SessionUtil.getUser().getCodigo());
                regAux.getCampos().put(dateCreatedC, new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.SP_FINANCIABLES
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                regAux.getCampos());
                insertarDespues();
                actualizarDespues();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                Constantes.MSM_REGISTRO_INGRESADO));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        muestraDialogo = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al presionar el boton No o Siguiente.
     */
    public void cancelardialogoConfirmar() {
        // <CODIGO_DESARROLLADO>
        if ("2".equals(opcionDialogo)) {
            regAux.getCampos().put(consBloqueado, "-1");
            regAux.getCampos().put("USUARIOBLOQUEO",
                            SessionUtil.getUser().getCodigo());
            regAux.getCampos().put("HORABLOQUEO", new Date());
            regAux.getCampos().put("FECHABLOQUEADO", new Date());
            regAux.getCampos().put("ANO",
                            anioSte(regAux.getCampos().get("ANO")
                                            .toString(),
                                            regAux.getCampos().get(periodoC)
                                                            .toString()));
            regAux.getCampos().put(periodoC,
                            perSte(regAux.getCampos().get("ANO")
                                            .toString(),
                                            regAux.getCampos().get(periodoC)
                                                            .toString()));
            calcula = false;
            try {
                regAux.getCampos().put(createdByC,
                                SessionUtil.getUser().getCodigo());
                regAux.getCampos().put(dateCreatedC, new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.SP_FINANCIABLES
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                regAux.getCampos());

                insertarDespues();
                actualizarDespues();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                Constantes.MSM_REGISTRO_INGRESADO));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        muestraDialogo = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que valida el registro que se va a insertar.
     *
     * @param reg
     * Registro que se va a validar.
     * @return es verdadero cuando el registro es valido.
     */
    private boolean validaRegistro(Registro reg) {
        Registro rs = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablespsControladorUrlEnum.URL5783
                                                                            .getValue())
                                            .getUrl(), param));

            String fimm = rs.getCampos().get("FIMM") == null ? null
                : rs.getCampos().get("FIMM").toString();
            if ("0".equals(rs.getCampos().get("LECTURA").toString())
                && (!SysmanFunciones.validarCampoVacio(rs.getCampos(),
                                bancoPerProcesoC))) {
                if ("F".equals(fimm)) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1696"));
                    return false;
                }
                else if (!"F".equals(fimm)) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1697"));
                    return false;
                }
                return validaCiclo(reg);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return validarResto(reg, rs);
    }

    /**
     * Realiza las demas validaciones luego de que el registro sea
     * valido y abre o no el dialogo para seleccionar si se inserta en
     * el periodo actual o en el siguiente.
     *
     * @param reg
     * Registro que se valida
     * @param rs
     * registro que valida el campo PERIODOSNOCOBROFIN
     * @return verdadero si no se encuentra bloqueado el registro.
     */
    private boolean validarResto(Registro reg, Registro rs) {
        /*
         * '05/01/2011 JP por que el si se activa varias veces se
         * corre el periodo tantas veces como lo activen
         */
        if ((rs.getCampos().get(bancoPerProcesoC) != null)
            && reg.getCampos().get("ANO")
                            .equals(reg.getCampos().get(anioInicialC))
            && !validarPeriodoSte()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1700"));
            return false;
        }
        else if (!"0".equals(
                        rs.getCampos().get("PERIODOSNOCOBROFIN").toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1701")
                            .replace("#PERIODOSNOCOBROFIN#",
                                            rs.getCampos().get(
                                                            "PERIODOSNOCOBROFIN")
                                                            .toString()));
        }
        if (!Boolean.parseBoolean(
                        reg.getCampos().get(consBloqueado).toString())
            && !"0".equals(factura)) {
            etiquetaAceptar = "ACTUAL";
            etiquetaCancelar = "SIGUIENTE";
            opcionDialogo = "2";
            mensajeDialogo = idioma.getString("TB_TB1702");
            muestraDialogo = true;
            return false;
        }
        else {
            calcula = true;
        }
        return true;
    }

    /**
     * Valida si el ciclo existe para el registro a insertar.
     *
     * @param reg
     * Registro a validar.
     * @return es verdadero si el ciclo existe.
     */
    private boolean validaCiclo(Registro reg) {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

            Registro rsCiclo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablespsControladorUrlEnum.URL1749
                                                                            .getValue())
                                            .getUrl(), param));
            if (rsCiclo == null) {
                return false;
            }
            else {
                Date fecha = (Date) reg.getCampos().get(dateCreatedC);
                Date fechaPre = (Date) rsCiclo.getCampos()
                                .get("FECHA_PREPARACION");

                if (fecha.before(fechaPre)) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1698"));
                    return false;
                }
                else if ("0".equals(
                                SysmanFunciones.nvl(
                                                rsCiclo.getCampos()
                                                                .get("INDCALCULADO"),
                                                "0")
                                                .toString())) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1699"));
                    return false;
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo que ocurre cuando se selecciona un valor de la lista
     * Usuarios
     *
     * @param event
     * Evento que activa el metodo.
     */
    public void seleccionarFilaUsuarios(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoRuta = registroAux.getCampos().get(codigoRutaC).toString();
        nombreUsuario = registroAux.getCampos().get("EXPR1").toString();
        periodo = registroAux.getCampos().get(periodoC).toString();
        anio = registroAux.getCampos().get("ANO").toString();
        bancoPerProceso = SysmanFunciones
                        .nvl(registroAux.getCampos().get(bancoPerProcesoC), "")
                        .toString();
        codigoInterno = registroAux.getCampos().get("CODIGOINTERNO").toString();
        reasignarOrigen();
        asignarValoresRegistro();
    }

    /**
     * Metodo que ocurre cuando se selecciona un valor de la lista
     * Usuarios
     *
     * @param event
     * Evento que activa el metodo.
     */
    public void seleccionarFilaUsuariosE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigoRutaC).toString();
    }

    /**
     * Metodo que ocurre cuando se selecciona un valor de la lista
     * Conceptos
     *
     * @param event
     * Evento que activa el metodo.
     */
    public void seleccionarFilaConceptos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        String numConcepto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigo), "")
                        .toString();
        if ("12".equals(numConcepto)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1703"));
            return;
        }
        else {
            registro.getCampos().put(conceptoC,
                            numConcepto);
            registro.getCampos().put(consNombreConcepto,
                            registroAux.getCampos().get("NOMBRE"));
        }

        Registro rs = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
            param.put(GeneralParameterEnum.CONCEPTO.getName(),
                            registro.getCampos().get(conceptoC));

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablespsControladorUrlEnum.URL1069
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rs != null) {
            if (!esFinanciable(
                            registro.getCampos().get(conceptoC).toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1704"));
                return;
            }
            else {
                if (existePeriodo(
                                anioSte(registro.getCampos().get("ANO")
                                                .toString(),
                                                registro.getCampos()
                                                                .get(periodoC)
                                                                .toString()),
                                perSte(registro.getCampos().get("ANO")
                                                .toString(),
                                                registro.getCampos()
                                                                .get(periodoC)
                                                                .toString()))) {
                    perIni = perSte(registro.getCampos().get("ANO").toString(),
                                    registro.getCampos().get(periodoC)
                                                    .toString());
                    anioIni = anioSte(
                                    registro.getCampos().get("ANO").toString(),
                                    registro.getCampos().get(periodoC)
                                                    .toString());
                    registro.getCampos().put("ANO", anioIni);
                    registro.getCampos().put(periodoC, perIni);
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1705")
                                    .replace("#nombrePeriodoSiguiente#",
                                                    nombrePeriodo(
                                                                    registro.getCampos()
                                                                                    .get("ANO")
                                                                                    .toString(),
                                                                    registro.getCampos()
                                                                                    .get(periodoC)
                                                                                    .toString())));
                }
            }
        }
        if ((registro.getCampos().get(saldoFinanciableC) == null)
            || "".equals(registro.getCampos().get(saldoFinanciableC))) {
            registro.getCampos()
                            .put(montoFinanciarC, (int) (Double
                                            .parseDouble(registroAux.getCampos()
                                                            .get("VALOR")
                                                            .toString())
                                + 0.501));
            registro.getCampos()
                            .put(montoFinanciarC, (int) (Double
                                            .parseDouble(registro.getCampos()
                                                            .get(montoFinanciarC)
                                                            .toString())
                                + 0.501));
            registro.getCampos()
                            .put(saldoFinanciableC, (int) ((Double
                                            .parseDouble(registro.getCampos()
                                                            .get(montoFinanciarC)
                                                            .toString())
                                * (1 + (Double.parseDouble(
                                                registroAux.getCampos()
                                                                .get("PORCINTERES")
                                                                .toString())
                                    / 100)))
                                + 0.501));
            /*
             * '23/03/2011 JP Si es una sola cuota no la estaba
             * calculando
             */
            if (Double.parseDouble(registro.getCampos().get(numeroCuotasC)
                            .toString()) > 0) {
                registro.getCampos().put(valorCuotaC,
                                SysmanFunciones.redondear(Double.parseDouble(
                                                registro.getCampos()
                                                                .get(saldoFinanciableC)
                                                                .toString())
                                    / Double.parseDouble(registro.getCampos()
                                                    .get(numeroCuotasC)
                                                    .toString()),
                                                0));
            }
        }
    }

    /**
     * Metodo que trae el nombre del periodo segun el anio y periodo
     * ingresados
     *
     * @param anioActual
     * anio en el que se encuentra.
     * @param periodoActual
     * periodo en el que se encuentra.
     * @return el nombre del periodo
     */
    private String nombrePeriodo(String anioActual,
        String periodoActual) {
        String rs = null;
        try {
            rs = ejbServiciosPublicosCeroRemote.asignarNombrePeriodo(compania,
                            Integer.parseInt(anioActual), periodoActual, null);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return (rs != null) && (registro.getCampos().get("NOMBREPER") != null)
            ? registro.getCampos().get("NOMBREPER").toString() : null;
    }

    private boolean esFinanciable(String concepto) {
        int cont = 0;
        try {
            String concFinanciables = ejbSysmanUtilRemote.consultarParametro(
                            compania, "CONCEPTOS FINANCIABLES",
                            SessionUtil.getModulo(), new Date(), false);
            if (concFinanciables == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1706"));
                return false;
            }
            String[] conceptos = concFinanciables.split(",");
            for (int i = 0; i < conceptos.length; i++) {
                if (conceptos[i].equals(concepto)) {
                    cont++;
                }
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return cont > 1;
    }

    public void seleccionarFilaConceptosE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigo).toString();

    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto actualizarTotales
     * en la vista
     *
     */
    public void ejecutaractualizarTotales() {
        if (listaInicial != null) {
            listaInicial.getFilters();
            Map<String, Object> rsTotales = new HashMap<>();
            Map<String, Object> param = listaInicial.getFilters();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            param.put("ANONOM", param.get("campos['ANO']"));
            param.remove("campos['ANO']");

            param.put("PERIODONOM", param.get("campos['PERIODO']"));
            param.remove("campos['PERIODO']");

            param.put("CONCEPTO", param.get("campos['CONCEPTO']"));
            param.remove("campos['CONCEPTO']");

            param.put(dateCreatedC, param.get("campos['DATE_CREATED']"));
            param.remove("campos['DATE_CREATED']");

            param.put("MONTOFINANCIAR", param.get("campos['MONTOFINANCIAR']"));
            param.remove("campos['MONTOFINANCIAR']");

            param.put("SALDOFINANCIABLE",
                            param.get("campos['SALDOFINANCIABLE']"));
            param.remove("campos['SALDOFINANCIABLE']");

            param.put("NUMEROCUOTAS", param.get("campos['NUMEROCUOTAS']"));
            param.remove("campos['NUMEROCUOTAS']");

            param.put("VALORCUOTA", param.get("campos['VALORCUOTA']"));
            param.remove("campos['VALORCUOTA']");

            param.put(consNombreConcepto,
                            param.get("campos['NOMBRECONCEPTO']"));
            param.remove("campos['NOMBRECONCEPTO']");

            param.put("BLOQUEADOHASTAANO",
                            param.get("campos['BLOQUEADOHASTAANO']"));
            param.remove("campos['BLOQUEADOHASTAANO']");

            param.put("PERUNICO", param.get("campos['PERUNICO']"));
            param.remove("campos['PERUNICO']");

            param.put("BLOQUEADOHASTAPERIODO",
                            param.get("campos['BLOQUEADOHASTAPERIODO']"));
            param.remove("campos['BLOQUEADOHASTAPERIODO']");

            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FinanciablespsControladorUrlEnum.URL8596
                                                            .getValue());
            try {
                rsTotales = requestManager
                                .get(urlReg.getUrl(), param).getFields();
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            totalMonto = new java.text.DecimalFormat(formatoMonedaC)
                            .format(Double.parseDouble(rsTotales
                                            .get("TOTALMONTO").toString()));
            totalSaldoFin = new java.text.DecimalFormat(formatoMonedaC)
                            .format(Double.parseDouble(rsTotales
                                            .get("TOTALSALDO").toString()));
            totalCuotas = new java.text.DecimalFormat(formatoMonedaC)
                            .format(Double.parseDouble(rsTotales
                                            .get("TOTALCUOTA").toString()));
        }

    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Metodo que se ejecuta al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            permiteEliminar = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtilRemote.consultarParametro(
                                            compania,
                                            "PERMITE ELIMINAR FINANCIABLES",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"));
            perConcepto32 = ejbSysmanUtilRemote.consultarParametro(compania,
                            "PERMITE FINANCIAR CONCEPTO 32",
                            SessionUtil.getModulo(), new Date(), false);
            gestionaConcFin = ejbSysmanUtilRemote.consultarParametro(compania,
                            "GESTIONA CONCEPTOS FINANCIABLES EN OPERACIONES",
                            SessionUtil.getModulo(), new Date(), false);
            conceptos = ejbSysmanUtilRemote.consultarParametro(compania,
                            "CONCEPTOS FINANCIABLES REGISTRO DE OPERACIONES",
                            SessionUtil.getModulo(), new Date(), false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cancelar la edicion del formulario.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo que se ejecuta antes de insertar un registro.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("PERUNICO");
        registro.getCampos().remove(consNombreConcepto);
        regAux = new Registro(registro.getCampos());
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablespsControladorUrlEnum.URL5783
                                                                            .getValue())
                                            .getUrl(), param));
            if (rs == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1707"));
                return false;
            }
            if ("P".equals(rs.getCampos().get("FIMM") == null ? ""
                : rs.getCampos().get("FIMM").toString())) {
                /*
                 * '23/03/2011 JP Para que Pregunte si desea agregar
                 * el financiable al siguiente periodo
                 */
                mensajeDialogo = idioma.getString("TB_TB1708");
                etiquetaAceptar = "Si";
                etiquetaCancelar = "No";
                opcionDialogo = "1";
                muestraDialogo = true;
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return validaRegistro(registro);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta despues de insertar un registro.
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        String descripcion = "Monto: " + regAux.getCampos().get(montoFinanciarC)
            + ";Saldo: "
            + regAux.getCampos().get(saldoFinanciableC) + ";Cuotas: "
            + regAux.getCampos().get(numeroCuotasC) + ";Cuota: "
            + regAux.getCampos().get(nroCuotaC) +
            ";Valor Cuota: " + regAux.getCampos().get(valorCuotaC);
        try {
            ejbServiciosPublicosTresRemote.auditoriaGeneral(compania,
                            SessionUtil.getUser().getCodigo(), "FINANCIABLES",
                            "Creación", Integer.parseInt(anio), periodo,
                            codigoInterno, descripcion);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        ejecutaractualizarTotales();
        return true;
    }

    /**
     * Metodo que se ejecuta antes de actualizar un registro.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1107-ANTES_ACTUALIZAR Private Sub
         *
         */
        return true;
    }

    /**
     * Metodo que verifica si existe un periodo en el anio y periodo
     * indicados.
     *
     * @param strAnio
     * anio a verificar
     * @param strPeriodo
     * periodo a verificar
     * @return verdadero si el periodo existe
     */
    private boolean existePeriodo(String strAnio, String strPeriodo) {
        /*
         * '13/01/2011 JP Verifica si el periodo esta creado pues
         * cuando se manejan periodos siguientes no hay certeza de la
         * existencia del periodo
         */
        Registro rs = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), strAnio);
            param.put(GeneralParameterEnum.PERIODO.getName(), strPeriodo);
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablespsControladorUrlEnum.URL7581
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rs == null;
    }

    /**
     * Metodo que permite traer el anio siguiente si el anio y periodo
     * existe.
     *
     * @param anioActual
     * anio actual a verificar
     * @param periodoActual
     * periodo actual a verificar
     * @return si el anio existe trae el valor del anio siguiente.
     */
    private String anioSte(String anioActual, String periodoActual) {
        String rs = null;
        try {
            rs = ejbServiciosPublicosCeroRemote.prepararAnoPeriodoSiguiente(
                            compania, Integer.parseInt(anioActual),
                            periodoActual,
                            "0", "");
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rs;
    }

    /**
     * Metodo que permite traer el periodo siguiente si el anio y
     * periodo existe.
     *
     * @param anioActual
     * anio actual a verificar
     * @param periodoActual
     * periodo actual a verificar
     * @return si el periodo existe trae el valor del periodo
     * siguiente.
     */
    private String perSte(String anioActual, String periodoActual) {

        String rs = null;
        try {
            rs = ejbServiciosPublicosCeroRemote.prepararAnoPeriodoSiguiente(
                            compania, Integer.parseInt(anioActual),
                            periodoActual, "1", "");
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rs;

    }

    /**
     * metodo que verifica si el periodo siguiente existe
     *
     * @return verdadero si el periodo existe.
     */
    private boolean validarPeriodoSte() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO"));
        param.put(GeneralParameterEnum.PERIODO.getName(),
                        registro.getCampos().get(periodoC));
        Registro regExiste = null;
        try {
            regExiste = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablespsControladorUrlEnum.URL9712
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if ((regExiste != null)
            && !"0".equals(regExiste.getCampos().get(periodoC).toString())) {
            String anioSiguiente = anioSte(
                            registro.getCampos().get(anioInicialC).toString(),
                            registro.getCampos()
                                            .get(periodoInicialC).toString());
            String perSiguiente = perSte(
                            registro.getCampos().get(anioInicialC).toString(),
                            registro.getCampos()
                                            .get(periodoInicialC).toString());
            registro.getCampos().put("ANO", anioSiguiente);
            registro.getCampos().put(periodoC, perSiguiente);
            return true;
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1709"));
            return false;
        }
    }

    /**
     * Metodo que se ejecuta despues de actualizar un registro
     */
    @Override
    public boolean actualizarDespues() {
        try {
            String mensaje = ejbServiciosPublicosSieteRemote
                            .calcularFacturacion(compania,
                                            Integer.parseInt(ciclo), codigoRuta,
                                            codigoRuta, false, false,
                                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(mensaje);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    /**
     * Metodo que se ejecuta antes de eliminar un registro.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * '28/12/2010 JP Para que se pueda llamar el formulario desde
         * operaciones
         */
        String resultado = "";
        try {
            resultado = ejbServiciosPublicosDosRemote.eliminarFinanciable(
                            compania, Integer.parseInt(ciclo),
                            Integer.parseInt(anio), periodo, codigoRuta,
                            Integer.parseInt(registro.getCampos().get(conceptoC)
                                            .toString()),
                            SessionUtil.getUser().getCodigo(),
                            new BigDecimal(registro.getCampos()
                                            .get(montoFinanciarC).toString()),
                            new BigDecimal(totalMonto.replace("$", "")
                                            .replace(".", "")
                                            .replace(",", ".").trim()),
                            new BigDecimal(registro.getCampos()
                                            .get(saldoFinanciableC).toString()),
                            new BigDecimal(registro.getCampos()
                                            .get(numeroCuotasC).toString()),
                            registro.getCampos().get(nroCuotaC) == null ? 0
                                : Integer.parseInt(registro.getCampos()
                                                .get(nroCuotaC).toString()),
                            new BigDecimal(registro.getCampos().get(valorCuotaC)
                                            .toString()),
                            bancoPerProceso == null
                                ? "NULL" : bancoPerProceso,
                            codigoInterno);

        }
        catch (NumberFormatException | SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        if (!"".equals(resultado)) {
            String[] resultados = resultado.split(",");
            switch (resultados.length) {
            case 2:
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(resultados[1]));
                return resultados[0].startsWith("false") ? false : true;
            case 3:
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(resultados[1]));
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(resultados[2]));
                return true;
            default:
                break;
            }
        }

        // </CODIGO_DESARROLLADO>
        return true;

    }

    /**
     * metodo que se ejecuta despues de elimninar un registro.
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        if (calcula && !"0"
                        .equals(SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        consBloqueado), "0")
                                        .toString())) {
            String mensaje;
            try {
                mensaje = ejbServiciosPublicosSieteRemote.calcularFacturacion(
                                compania, Integer.parseInt(ciclo), codigoRuta,
                                codigoRuta, false, false,
                                SessionUtil.getUser().getCodigo());
                JsfUtil.agregarMensajeInformativo(mensaje);
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        ejecutaractualizarTotales();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo que elimina los valores de los combos
     */
    @Override
    public void removerCombos() {
        /* No se eliminan los valores de los combos. */
    }

    /**
     * Metodo que asigna valores por defecto a un registro nuevo.
     */
    @Override
    public void asignarValoresRegistro() {
        registro.getCampos().put("CODIGORUTA", codigoRuta);
        registro.getCampos().put("CICLO", ciclo);
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ANO", anio);
        registro.getCampos().put(periodoC, periodo);
        registro.getCampos().put(anioInicialC, anio);
        registro.getCampos().put(periodoInicialC, periodo);
        registro.getCampos().put(dateCreatedC, new Date());
        registro.getCampos().put(montoFinanciarC, 0);
        registro.getCampos().put(saldoFinanciableC, 0);
        registro.getCampos().put(numeroCuotasC, 1);
        registro.getCampos().put(valorCuotaC, 0);
        registro.getCampos().put(bloqueadoHastaAnoC, anio);
        registro.getCampos().put("BLOQUEADOHASTAPERIODO", periodo);

    }

    // <SET_GET_ATRIBUTOS>
    public String getPerIni() {
        return perIni;
    }

    public void setPerIni(String perIni) {
        this.perIni = perIni;
    }

    public String getAnioIni() {
        return anioIni;
    }

    public void setAnioIni(String anioIni) {
        this.anioIni = anioIni;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getMensajeDialogo() {
        return mensajeDialogo;
    }

    public void setMensajeDialogo(String mensajeDialogo) {
        this.mensajeDialogo = mensajeDialogo;
    }

    public boolean isMuestraDialogo() {
        return muestraDialogo;
    }

    public void setMuestraDialogo(boolean muestraDialogo) {
        this.muestraDialogo = muestraDialogo;
    }

    public String getTotalSaldoFin() {
        return totalSaldoFin;
    }

    public void setTotalSaldoFin(String totalSaldoFin) {
        this.totalSaldoFin = totalSaldoFin;
    }

    public String getTotalMonto() {
        return totalMonto;
    }

    public void setTotalMonto(String totalMonto) {
        this.totalMonto = totalMonto;
    }

    public String getTotalCuotas() {
        return totalCuotas;
    }

    public String getEtiquetaAceptar() {
        return etiquetaAceptar;
    }

    public void setEtiquetaAceptar(String etiquetaAceptar) {
        this.etiquetaAceptar = etiquetaAceptar;
    }

    public String getEtiquetaCancelar() {
        return etiquetaCancelar;
    }

    public void setEtiquetaCancelar(String etiquetaCancelar) {
        this.etiquetaCancelar = etiquetaCancelar;
    }

    public void setTotalCuotas(String totalCuotas) {
        this.totalCuotas = totalCuotas;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAnios() {
        return listaAnios;
    }

    public void setListaAnios(List<Registro> listaAnios) {
        this.listaAnios = listaAnios;
    }

    public List<Registro> getListaPeriodos() {
        return listaPeriodos;
    }

    public void setListaPeriodos(List<Registro> listaPeriodos) {
        this.listaPeriodos = listaPeriodos;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(RegistroDataModelImpl listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public RegistroDataModelImpl getListaUsuariosE() {
        return listaUsuariosE;
    }

    public void setListaUsuariosE(RegistroDataModelImpl listaUsuariosE) {
        this.listaUsuariosE = listaUsuariosE;
    }

    public RegistroDataModelImpl getListaConceptos() {
        return listaConceptos;
    }

    public void setListaConceptos(RegistroDataModelImpl listaConceptos) {
        this.listaConceptos = listaConceptos;
    }

    public RegistroDataModelImpl getListaConceptosE() {
        return listaConceptosE;
    }

    public void setListaConceptosE(RegistroDataModelImpl listaConceptosE) {
        this.listaConceptosE = listaConceptosE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public boolean isPermiteEliminar() {
        return permiteEliminar;
    }

    public void setPermiteEliminar(boolean permiteEliminar) {
        this.permiteEliminar = permiteEliminar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
