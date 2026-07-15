package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.ScierredemesControladorEnum;
import com.sysman.contabilidad.enums.ScierredemesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
 * @author dsuesca modified by jrodriguezr
 * @version 1, 04/03/2016
 * @modified jsforero
 * @version 2. 10/04/2017 Se realizo el refactory.
 * @version 3, 21/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimiento y metodos de la
 * clase Acciones a llamados a EJB.
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped
public class ScierredemesControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private String anio;
    private String tipoComprobante;
    private RegistroDataModelImpl listaCuentaacerrar;
    private RegistroDataModelImpl listaCuentaacerrarE;
    private RegistroDataModelImpl listaContracuenta;
    private RegistroDataModelImpl listaContracuentaE;
    private RegistroDataModelImpl listatipoc;
    private RegistroDataModelImpl listatipocE;
    private String auxiliar;
    private RegistroDataModelImpl listaTerceroImpuestos;
    private RegistroDataModelImpl listaTerceroImpuestosE;
    private List<Registro> listaanio;
    private boolean permiteCierreDeCuentas;
    private Object nombreCuentaCerrar;
    private Object nombreCuentaContra;
    private String codigoCuentaCerrar;
    private String codigoCuentaContra;
    private boolean visibleAnio;
    private String prepararAnio;
    private String tituloMensajes;
    private StreamedContent archivoDescarga;
    private String terceroCuentaContra;
    private String sucursalCuentaContra;
    private String auxiliarCuentaContra;
    private String fuenteCuentaContra;
    private String referenciaCuentaContra;
    private final String cReferencia;
    private final String cFteRecurso;
    private final String cAuxiliar;
    private final String cPlanCntNombre;
    private final String cTercero;
    private final String cSucursal;
    private final String cNombre2;
    private final String cCodigo;
    private final String cNombre;
    private final String cContraCuenta;
    private final String cCtaACerrar;
    private final String cNumero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of ScierredemesControlador
     */
    public ScierredemesControlador() {
        super();
        compania = SessionUtil.getCompania();
        cReferencia = "REFERENCIA";
        cFteRecurso = "FUENTE_RECURSO";
        cAuxiliar = "AUXILIAR";
        cPlanCntNombre = "PLAN_CONTABLE_NOMBRE";
        cTercero = "TERCERO";
        cSucursal = "SUCURSAL";
        cNombre2 = "PLAN_CONTABLE_1_NOMBRE";
        cCodigo = "CODIGO";
        cNombre = "NOMBRE";
        cContraCuenta = "CONTRACUENTA";
        cCtaACerrar = "CUENTAACERRAR";
        cNumero = "NUMERO";
        try {
            numFormulario = GeneralCodigoFormaEnum.SCIERREDEMES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.HEADERCIERRE;
        buscarLlave();
        cargarListaanio();
        cargarListatipoc();

        anio = String.valueOf(SysmanFunciones.ano(
                        new Date())
            - 1);
        String ranio = service.buscarEnLista(anio, cNumero, cNumero,
                        listaanio);
        if (ranio == null) {
            HashMap<String, Object> hanio = new HashMap<>();
            hanio.put("ANO", anio);
            Registro newAnio = new Registro(hanio);
            listaanio.add(newAnio);
        }
        if (!listatipoc.getDatasource().isEmpty()) {
            tipoComprobante = listatipoc.getDatasource().get(1).getCampos()
                            .get(cCodigo).toString();
        }
        reasignarOrigen();
        registro = new Registro();
        registro.getCampos().put("ANO", anio);
        try {
            permiteCierreDeCuentas = "SI"
                            .equals(SysmanFunciones.nvl(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "PERMITE CIERRE DE CUENTAS DE IMPUESTOS",
                                                            SessionUtil.getModulo(),
                                                            new Date(), true),
                                            "").toString());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarListaCuentaacerrar();
        cargarListaContracuenta();
        cargarListaTerceroImpuestos();
        cargarListaContracuentaE();
        cargarListaCuentaacerrarE();
        cargarListaTerceroImpuestosE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.name(), compania);
        parametrosListado.put(GeneralParameterEnum.ANO.name(), anio);
        parametrosListado.put(
                        ScierredemesControladorEnum.TIPOCOMPROBANTE.getValue(),
                        tipoComprobante);

    }

    public void cargarListaCuentaacerrarE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL6972
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(ScierredemesControladorEnum.INICIO.getValue(), 1);
        listaCuentaacerrarE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCuentaacerrar() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL7752
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(ScierredemesControladorEnum.INICIO.getValue(), 0);

        listaCuentaacerrar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaContracuenta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL8533
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        listaContracuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaContracuentaE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL8533
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        listaContracuentaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaTerceroImpuestos() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL11796
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTerceroImpuestos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaanio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        UrlBean urlList = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL9650
                                                        .getValue());
        try {
            listaanio = RegistroConverter.toListRegistro(
                            requestManager.getList(urlList.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListatipoc() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL12713
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        listatipoc = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaTerceroImpuestosE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL13426
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.name(), compania);

        listaTerceroImpuestosE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void oprimirpreparar() {
        // <CODIGO_DESARROLLADO>
        visibleAnio = true;
        tituloMensajes = idioma.getString("TG_ANIO_A_PREPARAR");

        // </CODIGO_DESARROLLADO>
    }

    public void aceptaranio() {
        visibleAnio = false;
        StringBuilder builder = new StringBuilder();
        String inconsistencias = "";
        archivoDescarga = null;
        try {
            if (!validaAnio()) {
                return;
            }
            builder.append(" INCONSISTENCIAS EN EL PROCESO DE CONFIGURAR CUENTAS DE CIERRE.   \n");
            boolean presentaInconsistencias = false;

            List<Registro> rsCuentaACerrar;
            List<Registro> rsContraCuenta;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.name(), compania);
            param.put(GeneralParameterEnum.ANO.name(), anio);
            param.put(ScierredemesControladorEnum.PREPARAANO.getValue(),
                            prepararAnio);

            UrlBean urlList = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ScierredemesControladorUrlEnum.URL13676
                                                            .getValue());

            rsCuentaACerrar = RegistroConverter.toListRegistro(
                            requestManager.getList(urlList.getUrl(), param));

            if (!rsCuentaACerrar.isEmpty()) {
                presentaInconsistencias = true;
                builder.append(" CUENTAS A CERRAR QUE NO EXISTEN EN EL A�O A PREPARAR \n");
                for (Registro registroIn : rsCuentaACerrar) {
                    builder.append(" CUENTA:"
                        + registroIn.getCampos().get(cCtaACerrar)
                        + " NOMBRE:  "
                        + registroIn.getCampos().get(cNombre)
                        + " " + idioma.getString("TG_ANIO") + " " + prepararAnio
                        + " \n");
                }
                inconsistencias = builder.toString();
            }

            UrlBean urlContracuenta = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ScierredemesControladorUrlEnum.URL13629
                                                            .getValue());

            rsContraCuenta = RegistroConverter.toListRegistro(requestManager
                            .getList(urlContracuenta.getUrl(), param));

            if (!rsContraCuenta.isEmpty()) {
                presentaInconsistencias = true;
                builder.append(" CONTRACUENTAS QUE NO EXISTEN EN EL A�O A PREPARAR \n");
                for (Registro registroIn : rsContraCuenta) {
                    builder.append(" CUENTA:"
                        + registroIn.getCampos().get(cContraCuenta)
                        + " NOMBRE:  "
                        + registroIn.getCampos().get(cNombre) + " "
                        + idioma.getString("TG_ANIO") + " "
                        + prepararAnio + " \n");
                }
                inconsistencias = builder.toString();
            }
            if (!presentaInconsistencias) {

                Map<String, Object> parametros = new HashMap<>();
                parametros.put(GeneralParameterEnum.COMPANIA.name(), compania);
                parametros.put(GeneralParameterEnum.ANO.name(), anio);
                parametros.put(ScierredemesControladorEnum.PREPARAANO
                                .getValue(), prepararAnio);
                Parameter parameter = new Parameter();

                parameter.setFields(parametros);
                UrlBean urlInsertSelect = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ScierredemesControladorUrlEnum.URL15755
                                                                .getValue());
                requestManager.save(urlInsertSelect.getUrl(),
                                urlInsertSelect.getMetodo(), parameter);
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB920"));
                cargarListaanio();
                anio = prepararAnio;
                reasignarOrigen();
            }
            else {
                archivoDescarga = JsfUtil.getArchivoDescargaStreamed(
                                JsfUtil.serializarPlano(inconsistencias),
                                "Inconsistencias Conf Cuentas de Cierre.txt");
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB921"));
            }
        }
        catch (JRException | IOException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private boolean validaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(ScierredemesControladorEnum.PREPARAANO.getValue(),
                        prepararAnio);
        UrlBean urlList = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL12734
                                                        .getValue());
        HashMap<String, Object> existe = new HashMap<>();
        HashMap<String, Object> existeHeader = new HashMap<>();
        try {
            existe = (HashMap<String, Object>) requestManager
                            .get(urlList.getUrl(), param).getFields();

            if ("0".equals(existe.get(cNumero).toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB918"));
                return false;
            }
            UrlBean urlListheader = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ScierredemesControladorUrlEnum.URL12735
                                                            .getValue());
            existeHeader = (HashMap<String, Object>) requestManager
                            .get(urlListheader.getUrl(), param).getFields();

            if (!"0".equals(existeHeader.get(cNumero).toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB919"));
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    public void cambiaranio() {
        // <CODIGO_DESARROLLADO>
        reasignarOrigen();
        cargarListaContracuenta();
        cargarListaContracuentaE();
        cargarListaCuentaacerrar();
        cargarListaCuentaacerrarE();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCuentaacerrar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarContracuenta() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    public void cambiarCuentaacerrarC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cPlanCntNombre, nombreCuentaCerrar);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cCtaACerrar, codigoCuentaCerrar);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarContracuentaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombre2, nombreCuentaContra);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cContraCuenta, codigoCuentaContra);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cTercero,
                        terceroCuentaContra);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cSucursal,
                        sucursalCuentaContra);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cAuxiliar,
                        auxiliarCuentaContra);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cFteRecurso,
                        fuenteCuentaContra);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cReferencia,
                        referenciaCuentaContra);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCuentaacerrar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cCtaACerrar,
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cPlanCntNombre,
                        registroAux.getCampos().get(cNombre));

    }

    public void seleccionarFilaCuentaacerrarE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreCuentaCerrar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
        codigoCuentaCerrar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    public void seleccionarFilaContracuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cContraCuenta,
                        SysmanFunciones.nvl(registroAux.getCampos().get("ID"),
                                        "").toString());
        registro.getCampos().put(cNombre2,
                        registroAux.getCampos().get(cNombre));
        registro.getCampos().put(cTercero,
                        SysmanFunciones.validarVariableVacio(
                                        SysmanFunciones.nvl(registroAux
                                                        .getCampos()
                                                        .get(cTercero), "")
                                                        .toString())
                                                            ? SysmanConstantes.CONS_TERCERO
                                                            : SysmanFunciones
                                                                            .nvl(registroAux.getCampos()
                                                                                            .get(cTercero),
                                                                                            "")
                                                                            .toString());
        registro.getCampos().put(cSucursal,
                        SysmanFunciones.validarVariableVacio(
                                        SysmanFunciones.nvl(registroAux
                                                        .getCampos()
                                                        .get(cSucursal), "")
                                                        .toString())
                                                            ? SysmanConstantes.CONS_SUCURSAL
                                                            : SysmanFunciones
                                                                            .nvl(registroAux.getCampos()
                                                                                            .get(cSucursal),
                                                                                            "")
                                                                            .toString());
        registro.getCampos().put(cAuxiliar,
                        SysmanFunciones.validarVariableVacio(
                                        SysmanFunciones.nvl(registroAux
                                                        .getCampos()
                                                        .get(cAuxiliar), "")
                                                        .toString())
                                                            ? SysmanConstantes.CONS_AUXILIAR
                                                            : SysmanFunciones
                                                                            .nvl(registroAux.getCampos()
                                                                                            .get(cAuxiliar),
                                                                                            "")
                                                                            .toString());
        registro.getCampos().put(cFteRecurso,
                        SysmanFunciones.validarVariableVacio(
                                        SysmanFunciones.nvl(registroAux
                                                        .getCampos()
                                                        .get(cFteRecurso), "")
                                                        .toString())
                                                            ? SysmanConstantes.CONS_FUENTE
                                                            : SysmanFunciones
                                                                            .nvl(registroAux.getCampos()
                                                                                            .get(cFteRecurso),
                                                                                            "")
                                                                            .toString());
        registro.getCampos().put(cReferencia,
                        SysmanFunciones.validarVariableVacio(
                                        SysmanFunciones.nvl(registroAux
                                                        .getCampos()
                                                        .get(cReferencia), "")
                                                        .toString())
                                                            ? SysmanConstantes.CONS_REFERENCIA
                                                            : SysmanFunciones
                                                                            .nvl(registroAux.getCampos()
                                                                                            .get(cReferencia),
                                                                                            "")
                                                                            .toString());

    }

    public void seleccionarFilaContracuentaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreCuentaContra = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
        codigoCuentaContra = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        terceroCuentaContra = SysmanFunciones.validarVariableVacio(
                        SysmanFunciones.nvl(
                                        registroAux.getCampos().get(cTercero),
                                        "").toString())
                                            ? SysmanConstantes.CONS_TERCERO
                                            : SysmanFunciones.nvl(
                                                            registroAux.getCampos()
                                                                            .get(cTercero),
                                                            "").toString();
        sucursalCuentaContra = SysmanFunciones.validarVariableVacio(
                        SysmanFunciones.nvl(
                                        registroAux.getCampos().get(cSucursal),
                                        "").toString())
                                            ? SysmanConstantes.CONS_SUCURSAL
                                            : SysmanFunciones.nvl(
                                                            registroAux.getCampos()
                                                                            .get(cSucursal),
                                                            "").toString();
        auxiliarCuentaContra = SysmanFunciones.validarVariableVacio(
                        SysmanFunciones.nvl(
                                        registroAux.getCampos().get(cAuxiliar),
                                        "").toString())
                                            ? SysmanConstantes.CONS_AUXILIAR
                                            : SysmanFunciones.nvl(
                                                            registroAux.getCampos()
                                                                            .get(cAuxiliar),
                                                            "").toString();
        fuenteCuentaContra = SysmanFunciones.validarVariableVacio(
                        SysmanFunciones.nvl(registroAux.getCampos()
                                        .get(cFteRecurso), "").toString())
                                            ? SysmanConstantes.CONS_FUENTE
                                            : SysmanFunciones.nvl(
                                                            registroAux.getCampos()
                                                                            .get(cFteRecurso),
                                                            "")
                                                            .toString();
        referenciaCuentaContra = SysmanFunciones.validarVariableVacio(
                        SysmanFunciones.nvl(registroAux.getCampos()
                                        .get(cReferencia), "").toString())
                                            ? SysmanConstantes.CONS_REFERENCIA
                                            : SysmanFunciones.nvl(
                                                            registroAux.getCampos()
                                                                            .get(cReferencia),
                                                            "")
                                                            .toString();

    }

    public void seleccionarFilatipoc(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoComprobante = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        reasignarOrigen();
    }

    public void seleccionarFilatipocE(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTerceroImpuestos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TERCEROIMPUESTO",
                        registroAux.getCampos().get("NIT"));
    }

    public void seleccionarFilaTerceroImpuestosE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cNombre2);
        registro.getCampos().remove(cPlanCntNombre);

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.name(),
                        compania);
        if (anio != null) {
            registro.getCampos().put(GeneralParameterEnum.ANO.name(), anio);
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB922"));
            return false;
        }
        if (tipoComprobante != null) {
            registro.getCampos().put("TIPOCOMPROBANTE", tipoComprobante);
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB923"));
            return false;
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        HashMap<String, Object> consecutivo = new HashMap<>();
        UrlBean urlListheader = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ScierredemesControladorUrlEnum.URL12737
                                                        .getValue());
        try {
            consecutivo = (HashMap<String, Object>) requestManager
                            .get(urlListheader.getUrl(), param).getFields();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        registro.getCampos().put("CONSECUTIVO",
                        consecutivo.get("NEWCONSEC").toString());
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
        registro.getCampos().remove(cNombre2);
        registro.getCampos().remove(cPlanCntNombre);

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

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.ANO.name());
        registro.getCampos().remove("TIPOCOMPROBANTE");
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.name());

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    public RegistroDataModelImpl getListatipoc() {
        return listatipoc;
    }

    public void setListatipoc(RegistroDataModelImpl listatipoc) {
        this.listatipoc = listatipoc;
    }

    public RegistroDataModelImpl getListatipocE() {
        return listatipocE;
    }

    public void setListatipocE(RegistroDataModelImpl listatipocE) {
        this.listatipocE = listatipocE;
    }

    public boolean isPermiteCierreDeCuentas() {
        return permiteCierreDeCuentas;
    }

    public void setPermiteCierreDeCuentas(boolean permiteCierreDeCuentas) {
        this.permiteCierreDeCuentas = permiteCierreDeCuentas;
    }

    public void setNombreCuentaCerrar(String nombreCuentaCerrar) {
        this.nombreCuentaCerrar = nombreCuentaCerrar;
    }

    public void setNombreCuentaContra(String nombreCuentaContra) {
        this.nombreCuentaContra = nombreCuentaContra;
    }

    public RegistroDataModelImpl getListaCuentaacerrar() {
        return listaCuentaacerrar;
    }

    public void setListaCuentaacerrar(
        RegistroDataModelImpl listaCuentaacerrar) {
        this.listaCuentaacerrar = listaCuentaacerrar;
    }

    public RegistroDataModelImpl getListaCuentaacerrarE() {
        return listaCuentaacerrarE;
    }

    public void setListaCuentaacerrarE(
        RegistroDataModelImpl listaCuentaacerrarE) {
        this.listaCuentaacerrarE = listaCuentaacerrarE;
    }

    public RegistroDataModelImpl getListaContracuenta() {
        return listaContracuenta;
    }

    public void setListaContracuenta(RegistroDataModelImpl listaContracuenta) {
        this.listaContracuenta = listaContracuenta;
    }

    public RegistroDataModelImpl getListaContracuentaE() {
        return listaContracuentaE;
    }

    public void setListaContracuentaE(
        RegistroDataModelImpl listaContracuentaE) {
        this.listaContracuentaE = listaContracuentaE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Object getNombreCuentaCerrar() {
        return nombreCuentaCerrar;
    }

    public void setNombreCuentaCerrar(Object nombreCuentaCerrar) {
        this.nombreCuentaCerrar = nombreCuentaCerrar;
    }

    public Object getNombreCuentaContra() {
        return nombreCuentaContra;
    }

    public void setNombreCuentaContra(Object nombreCuentaContra) {
        this.nombreCuentaContra = nombreCuentaContra;
    }

    public String getCodigoCuentaCerrar() {
        return codigoCuentaCerrar;
    }

    public void setCodigoCuentaCerrar(String codigoCuentaCerrar) {
        this.codigoCuentaCerrar = codigoCuentaCerrar;
    }

    public String getCodigoCuentaContra() {
        return codigoCuentaContra;
    }

    public void setCodigoCuentaContra(String codigoCuentaContra) {
        this.codigoCuentaContra = codigoCuentaContra;
    }

    public RegistroDataModelImpl getListaTerceroImpuestosE() {
        return listaTerceroImpuestosE;
    }

    public void setListaTerceroImpuestosE(
        RegistroDataModelImpl listaTerceroImpuestosE) {
        this.listaTerceroImpuestosE = listaTerceroImpuestosE;
    }

    public boolean isVisibleAnio() {
        return visibleAnio;
    }

    public void setVisibleAnio(boolean visibleAnio) {
        this.visibleAnio = visibleAnio;
    }

    public String getPrepararAnio() {
        return prepararAnio;
    }

    public void setPrepararAnio(String prepararAnio) {
        this.prepararAnio = prepararAnio;
    }

    public String getTituloMensajes() {
        return tituloMensajes;
    }

    public void setTituloMensajes(String tituloMensajes) {
        this.tituloMensajes = tituloMensajes;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public RegistroDataModelImpl getListaTerceroImpuestos() {
        return listaTerceroImpuestos;
    }

    public void setListaTerceroImpuestos(
        RegistroDataModelImpl listaTerceroImpuestos) {
        this.listaTerceroImpuestos = listaTerceroImpuestos;
    }

    public List<Registro> getListaanio() {
        return listaanio;
    }

    public void setListaanio(List<Registro> listaanio) {
        this.listaanio = listaanio;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

}
