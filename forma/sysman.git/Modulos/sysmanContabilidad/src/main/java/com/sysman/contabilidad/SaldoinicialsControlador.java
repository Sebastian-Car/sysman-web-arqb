package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.enums.SaldoinicialsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
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
 * @author sdaza
 * @version 1, 04/03/2016
 * @modifier amonroy
 * @version 2, 11/04/2017 Proceso de Refactoring y Revision de buenas
 * practicas sugeridas por la herramienta SonarLint
 */
@ManagedBean
@ViewScoped
public class SaldoinicialsControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "AUXILIAR" en el formulario, almacena el
     * texto AUXILIAR
     */
    private final String cAuxiliar;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "CENTRO_COSTO" en el formulario, almacena
     * el texto CENTRO_COSTO
     */
    private final String cCentroCosto;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "CODIGO" en el formulario, almacena el
     * texto CODIGO
     */
    private final String cCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "CODIGO_NOMBRE" en el formulario,
     * almacena el texto CODIGO_NOMBRE
     */
    private final String cCodigoNombre;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "FUENTE_RECURSO" en el formulario,
     * almacena el texto FUENTE_RECURSO
     */
    private final String cFuenteRecurso;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "MAN_AUX_FUE" en el formulario, almacena
     * el texto MAN_AUX_FUE
     */
    private final String cManFuente;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "MAN_AUX_GEN" en el formulario, almacena
     * el texto MAN_AUX_GEN
     */
    private final String cManGen;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "MAN_AUX_REF" en el formulario, almacena
     * el texto MAN_AUX_REF
     */
    private final String cManReferencia;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "MAN_AUX_TER" en el formulario, almacena
     * el texto MAN_AUX_TER
     */
    private final String cManTercero;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "MAN_CEN_CTO" en el formulario, almacena
     * el texto MAN_CEN_CTO
     */
    private final String cManCentro;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "NATURALEZA" en el formulario, almacena
     * el texto NATURALEZA
     */
    private final String cNaturaleza;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "NOMBRE" en el formulario, almacena el
     * texto NOMBRE
     */
    private final String cNombre;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "NOMBRE_AUXILIAR" en el formulario,
     * almacena el texto NOMBRE_AUXILIAR
     */
    private final String cNombreAuxiliar;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "NOMBRE_CENTRO" en el formulario,
     * almacena el texto NOMBRE_CENTRO
     */
    private final String cNombreCentro;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra ""NOMBRE_FUENTE"" en el formulario,
     * almacena el texto NOMBRE_FUENTE
     */
    private final String cNombreFuente;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "NOMBRE_REFERENCIA" en el formulario,
     * almacena el texto NOMBRE_REFERENCIA
     */
    private final String cNombreReferencia;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "NOMBRE_TERCERO" en el formulario,
     * almacena el texto NOMBRE_TERCERO
     */
    private final String cNombreTercero;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "REFERENCIA" en el formulario, almacena
     * el texto REFERENCIA
     */
    private final String cReferencia;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "SALDOINICIAL" en el formulario, almacena
     * el texto SALDOINICIAL
     */
    private final String cSaldoInicial;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "SUCURSAL" en el formulario, almacena el
     * texto SUCURSAL
     */
    private final String cSucursal;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "TB_TB600" en el formulario, almacena el
     * texto TB_TB600
     */
    private final String cTb600;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "TB_TB601" en el formulario, almacena el
     * texto TB_TB601
     */
    private final String cTb601;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "TB_TB602" en el formulario, almacena el
     * texto TB_TB602
     */
    private final String cTb602;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "TB_TB603" en el formulario, almacena el
     * texto TB_TB603
     */
    private final String cTb603;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "TB_TB604" en el formulario, almacena el
     * texto TB_TB604
     */
    private final String cTb604;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "TERCERO" en el formulario, almacena el
     * texto TERCERO
     */
    private final String cTercero;

    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaTerceroE;
    private RegistroDataModelImpl listaCentroCosto;
    private RegistroDataModelImpl listaCentroCostoE;
    private RegistroDataModelImpl listaAuxiliar;
    private RegistroDataModelImpl listaAuxiliarE;
    private RegistroDataModelImpl listaCodigo;
    private RegistroDataModelImpl listaCodigoE;
    private RegistroDataModelImpl listaReferencia;
    private RegistroDataModelImpl listaReferenciaE;
    private RegistroDataModelImpl listaFuenteRecurso;
    private RegistroDataModelImpl listaFuenteRecursoE;
    private String auxiliar;
    private String ano;
    private boolean manTercero = true;
    private boolean manAuxiliar = true;
    private boolean manCCto = true;
    private boolean manReferencia = true;
    private boolean manFuente = true;
    private String naturaleza;
    private int indice;
    private String nombreCodigo;

    @EJB
    private EjbSysmanUtilRemote sysmanUtil;

    /**
     * Creates a new instance of SaldoinicialsControlador
     */
    public SaldoinicialsControlador() {
        super();
        compania = SessionUtil.getCompania();
        cAuxiliar = "AUXILIAR";
        cCentroCosto = "CENTRO_COSTO";
        cCodigo = "CODIGO";
        cCodigoNombre = "CODIGO_NOMBRE";
        cFuenteRecurso = "FUENTE_RECURSO";
        cManFuente = "MAN_AUX_FUE";
        cManGen = "MAN_AUX_GEN";
        cManReferencia = "MAN_AUX_REF";
        cManTercero = "MAN_AUX_TER";
        cManCentro = "MAN_CEN_CTO";
        cNaturaleza = "NATURALEZA";
        cNombre = "NOMBRE";
        cNombreAuxiliar = "NOMBRE_AUXILIAR";
        cNombreCentro = "NOMBRE_CENTRO";
        cNombreFuente = "NOMBRE_FUENTE";
        cNombreReferencia = "NOMBRE_REFERENCIA";
        cNombreTercero = "NOMBRE_TERCERO";
        cReferencia = "REFERENCIA";
        cSaldoInicial = "SALDOINICIAL";
        cSucursal = "SUCURSAL";
        cTb600 = "TB_TB600";
        cTb601 = "TB_TB601";
        cTb602 = "TB_TB602";
        cTb603 = "TB_TB603";
        cTb604 = "TB_TB604";
        cTercero = "TERCERO";
        indicadorClonarPermisos = true;

        try {
            numFormulario = GeneralCodigoFormaEnum.SALDOINICIALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null && !SysmanFunciones
                            .validarCampoVacio(parametrosEntrada, "ano")) {
                ano = parametrosEntrada.get("ano").toString();
            }
        }
        catch (Exception ex) {
            Logger.getLogger(SaldoinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SALDOSINICIALES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaCodigo();
        abrirFormulario();
        String rta = "";
        try {
            rta = sysmanUtil.verificarEstadoPeriodoAnual(compania,
                            Integer.parseInt(ano), 1, 1);
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(SaldoinicialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

        if (!"A".equals(rta)) {
            permisos[0] = false;
            permisos[1] = false;
            permisos[2] = false;
            permisos[3] = true;
            permisos[4] = true;
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            "TB_TB3166"));
        }
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
    }

    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL6922
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL7635
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaCentroCosto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL8352
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCentroCostoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL9247
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCentroCostoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaAuxiliar() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL10139
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaAuxiliarE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL11032
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaAuxiliarE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCodigo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL11923
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCodigoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL13773
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaReferencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL15627
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaReferenciaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL16448
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaReferenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaFuenteRecurso() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL17272
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaFuenteRecursoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SaldoinicialsControladorUrlEnum.URL18164
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaFuenteRecursoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cambiarTercero() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCentroCosto() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAuxiliar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodigo() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cTercero, registroAux.getCampos().get("NIT"));
        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));
        registro.getCampos().put(cNombreTercero,
                        registroAux.getCampos().get(cNombre));
    }

    public void seleccionarFilaTerceroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "")
                        .toString();
        registro.getCampos().put(cSucursal,
                        registroAux.getCampos().get(cSucursal));
        registro.getCampos().put(cNombreTercero,
                        registroAux.getCampos().get(cNombre));
    }

    public void seleccionarFilaCentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (registroAux.getCampos().size() != 0) {
            if ((boolean) registroAux.getCampos().get("MOVIMIENTO")) {
                registro.getCampos().put(cCentroCosto,
                                registroAux.getCampos().get(cCodigo));
                registro.getCampos().put(cNombreCentro,
                                registroAux.getCampos().get(cNombre));
            }
            else {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB598"));
            }
        }
        else {
            registro.getCampos().put(cCentroCosto, "");
            registro.getCampos().put(cNombreCentro, "");
        }
    }

    public void seleccionarFilaCentroCostoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        registro.getCampos().put(cNombreCentro,
                        registroAux.getCampos().get(cNombre));
    }

    public void seleccionarFilaAuxiliar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (registroAux.getCampos().size() != 0) {
            if ((boolean) registroAux.getCampos().get("MOVIMIENTO")) {
                registro.getCampos().put(cAuxiliar,
                                registroAux.getCampos().get(cCodigo));
                registro.getCampos().put(cNombreAuxiliar,
                                registroAux.getCampos().get(cNombre));
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3297"));
            }
        }
        else {
            registro.getCampos().put(cAuxiliar, "");
            registro.getCampos().put(cNombreAuxiliar, "");
        }
    }

    public void seleccionarFilaAuxiliarE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        registro.getCampos().put(cNombreAuxiliar,
                        registroAux.getCampos().get(cNombre));

    }

    public void seleccionarFilaCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cCodigo,
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cCodigoNombre,
                        registroAux.getCampos().get(cNombre));
        nombreCodigo = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNombre), "")
                        .toString();
        naturaleza = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNaturaleza), "")
                        .toString();
        cargarListaTercero();
        cargarListaCentroCosto();
        cargarListaAuxiliar();

        cargarListaReferencia();
        cargarListaFuenteRecurso();

        Map<String, Object> paramsRegUnico;
        try {
            if ((boolean) registroAux.getCampos().get(cManTercero)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(cTb600));
                manTercero = false;
            }
            else {
                paramsRegUnico = new HashMap<>();
                registro.getCampos().put(cTercero,
                                SysmanConstantes.CONS_TERCERO);
                registro.getCampos().put(cSucursal,
                                SysmanConstantes.CONS_SUCURSAL);

                paramsRegUnico.put("NIT", SysmanConstantes.CONS_TERCERO);

                registro.getCampos().put(cNombreTercero,
                                listaTercero.getRegistroUnico(paramsRegUnico)
                                                .getCampos().get(cNombre));

                manTercero = true;
            }
            if ((boolean) registroAux.getCampos().get(cManGen)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(cTb601));
                manAuxiliar = false;
            }
            else {
                paramsRegUnico = new HashMap<>();
                registro.getCampos().put(cAuxiliar,
                                SysmanConstantes.CONS_AUXILIAR);

                paramsRegUnico.put(cCodigo, SysmanConstantes.CONS_AUXILIAR);
                registro.getCampos().put(cNombreAuxiliar,
                                listaAuxiliar.getRegistroUnico(paramsRegUnico)
                                                .getCampos().get(cNombre));
                manAuxiliar = true;
            }
            if ((boolean) registroAux.getCampos().get(cManCentro)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(cTb602));
                manCCto = false;
            }
            else {
                paramsRegUnico = new HashMap<>();
                registro.getCampos().put(cCentroCosto,
                                SysmanConstantes.CONS_CENTRO);

                paramsRegUnico.put(cCodigo, SysmanConstantes.CONS_CENTRO);
                registro.getCampos().put(cNombreCentro,
                                listaCentroCosto.getRegistroUnico(
                                                paramsRegUnico)
                                                .getCampos()
                                                .get(cNombre));

                manCCto = true;
            }

            if ((boolean) registroAux.getCampos().get(cManReferencia)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(cTb603));
                manReferencia = false;
            }
            else {
                paramsRegUnico = new HashMap<>();
                cargarListaReferencia();
                registro.getCampos().put(cReferencia,
                                SysmanConstantes.CONS_REFERENCIA);
                paramsRegUnico.put(cCodigo, SysmanConstantes.CONS_REFERENCIA);
                registro.getCampos().put(cNombreReferencia,
                                listaReferencia.getRegistroUnico(paramsRegUnico)
                                                .getCampos()
                                                .get(cNombre));

                manReferencia = true;
            }

            if ((boolean) registroAux.getCampos().get(cManFuente)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(cTb604));
                manFuente = false;
            }
            else {
                paramsRegUnico = new HashMap<>();
                cargarListaFuenteRecurso();
                registro.getCampos().put(cFuenteRecurso,
                                SysmanConstantes.CONS_FUENTE);
                paramsRegUnico.put(cCodigo, SysmanConstantes.CONS_FUENTE);
                registro.getCampos().put(cNombreFuente,
                                listaFuenteRecurso.getRegistroUnico(
                                                paramsRegUnico)
                                                .getCampos()
                                                .get(cNombre));

                manFuente = true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaCodigoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        registro.getCampos().put(cCodigo,
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cCodigoNombre,
                        registroAux.getCampos().get(cNombre));
        naturaleza = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNaturaleza), "")
                        .toString();

        cargarListaTerceroE();
        cargarListaCentroCostoE();
        cargarListaAuxiliarE();
        cargarListaCodigoE();
        if ((boolean) registroAux.getCampos().get(cManTercero)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(cTb600));
            manTercero = false;
        }
        else {
            registro.getCampos().put(cTercero, SysmanConstantes.CONS_TERCERO);
            registro.getCampos().put(cSucursal,
                            SysmanConstantes.CONS_SUCURSAL);
            manTercero = true;
        }
        if ((boolean) registroAux.getCampos().get(cManGen)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(cTb601));
            manAuxiliar = false;
        }
        else {
            registro.getCampos().put(cAuxiliar,
                            SysmanConstantes.CONS_AUXILIAR);
            manAuxiliar = true;
        }
        if ((boolean) registroAux.getCampos().get(cManCentro)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(cTb602));
            manCCto = false;
        }
        else {
            registro.getCampos().put(cCentroCosto,
                            SysmanConstantes.CONS_CENTRO);
            manCCto = true;
        }

        if ((boolean) registroAux.getCampos().get(cManReferencia)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(cTb603));
            manReferencia = false;
        }
        else {
            registro.getCampos().put(cReferencia,
                            SysmanConstantes.CONS_REFERENCIA);
            manReferencia = true;
        }

        if ((boolean) registroAux.getCampos().get(cManFuente)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(cTb604));
            manFuente = false;
        }
        else {
            registro.getCampos().put(cFuenteRecurso,
                            SysmanConstantes.CONS_FUENTE);
            manFuente = true;
        }

    }

    public void cambiarCodigoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cCodigoNombre, nombreCodigo);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cTercero,
                        registro.getCampos().get(cTercero));
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cSucursal, registro.getCampos().get(cSucursal));
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreTercero, null);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cAuxiliar, registro.getCampos().get(cAuxiliar));
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreAuxiliar, null);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cCentroCosto,
                        registro.getCampos().get(cCentroCosto));
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreCentro, null);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cReferencia, registro.getCampos().get(cReferencia));
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreReferencia, null);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cFuenteRecurso,
                        registro.getCampos().get(cFuenteRecurso));
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreFuente, null);
    }

    public void seleccionarFilaReferencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cReferencia,
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cNombreReferencia,
                        registroAux.getCampos().get(cNombre));

    }

    public void seleccionarFilaReferenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        registro.getCampos().put(cNombreReferencia,
                        registroAux.getCampos().get(cNombre));
    }

    public void seleccionarFilaFuenteRecurso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cFuenteRecurso,
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cNombreFuente,
                        registroAux.getCampos().get(cNombre));

    }

    public void seleccionarFilaFuenteRecursoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        registro.getCampos().put(cNombreFuente,
                        registroAux.getCampos().get(cNombre));
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

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().remove(cCodigoNombre);
        registro.getCampos().remove(cNombreTercero);
        registro.getCampos().remove(cNombreCentro);
        registro.getCampos().remove(cNombreAuxiliar);
        registro.getCampos().remove(cNombreReferencia);
        registro.getCampos().remove(cNombreFuente);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Valida que se haya selecionado un valor para los campos
     * "Tercero" ycSucursal" Si la cuenta tiene indicador de tercero
     * 
     * @return verdadero si los campos obligatorios no tienen valor
     */
    private boolean validarTercero() {
        return manTercero && ((registro.getCampos().get(cTercero) == null)
            || (registro.getCampos().get(cSucursal) == null));
    }

    /**
     * Valida que se haya selecionado un valor para el campo
     * "Auxiliar" si la cuenta tiene indicador de auxiliar
     * 
     * @return
     */
    private boolean validarAuxiliar() {
        return manAuxiliar && (registro.getCampos().get(cAuxiliar) == null);
    }

    /**
     * Evalua si la cuenta seleccionada tiene algun movimiento o
     * auxiliar y verifica que se completen los campos obligatorios
     * segun sea el caso
     * 
     * @return verdadero si los campos obligatorios han sido
     * completados
     */
    private boolean validarCampos() {
        boolean respuesta = true;
        if (validarTercero()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(cTb600));
            respuesta = false;
        }
        if (validarAuxiliar()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(cTb601));
            respuesta = false;
        }

        if (manCCto && (registro.getCampos().get(cCentroCosto) == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(cTb602));
            respuesta = false;
        }

        if (manReferencia && (registro.getCampos().get(cReferencia) == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(cTb603));
            respuesta = false;
        }

        if (manFuente && (registro.getCampos().get(cFuenteRecurso) == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(cTb604));
            respuesta = false;
        }

        if (registro.getCampos().get(cSaldoInicial) == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB930"));
            respuesta = false;
        }
        return respuesta;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (!validarCampos()) {
            return false;
        }
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(cCodigoNombre);
        registro.getCampos().remove(cNombreTercero);
        registro.getCampos().remove(cNombreAuxiliar);
        registro.getCampos().remove(cNombreCentro);
        registro.getCampos().remove(cNombreReferencia);
        registro.getCampos().remove(cNombreFuente);
        registro.getCampos().remove("CONTABILIZADO");
    }

    @Override
    public void asignarValoresRegistro() {
        manTercero = true;
        manCCto = true;
        manAuxiliar = true;
        manReferencia = true;
        manFuente = true;
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        String codigo = SysmanFunciones
                        .nvl(registro.getCampos().get(cCodigo), "").toString();
        Map<String, Object> paramsRegUnico = new HashMap<>();
        paramsRegUnico.put("CODIGO", codigo);
        Registro auxCuenta = null;
        try {
            auxCuenta = listaCodigo
                            .getRegistroUnico(paramsRegUnico);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (auxCuenta != null) {
            manTercero = (boolean) auxCuenta.getCampos().get(cManTercero);
            manCCto = (boolean) auxCuenta.getCampos().get(cManCentro);
            manAuxiliar = (boolean) auxCuenta.getCampos().get(cManGen);
            manReferencia = (boolean) auxCuenta.getCampos().get(cManReferencia);
            manFuente = (boolean) auxCuenta.getCampos().get(cManFuente);
        }

    }

    public void ejecutarrcCerrar() {
        SessionUtil.cleanFlash();
        SessionUtil.redireccionar("/menu.sysman");
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    public RegistroDataModelImpl getListaTerceroE() {
        return listaTerceroE;
    }

    public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
        this.listaTerceroE = listaTerceroE;
    }

    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    public RegistroDataModelImpl getListaCentroCostoE() {
        return listaCentroCostoE;
    }

    public void setListaCentroCostoE(RegistroDataModelImpl listaCentroCostoE) {
        this.listaCentroCostoE = listaCentroCostoE;
    }

    public RegistroDataModelImpl getListaAuxiliar() {
        return listaAuxiliar;
    }

    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }

    public RegistroDataModelImpl getListaAuxiliarE() {
        return listaAuxiliarE;
    }

    public void setListaAuxiliarE(RegistroDataModelImpl listaAuxiliarE) {
        this.listaAuxiliarE = listaAuxiliarE;
    }

    public RegistroDataModelImpl getListaCodigo() {
        return listaCodigo;
    }

    public void setListaCodigo(RegistroDataModelImpl listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    public RegistroDataModelImpl getListaCodigoE() {
        return listaCodigoE;
    }

    public void setListaCodigoE(RegistroDataModelImpl listaCodigoE) {
        this.listaCodigoE = listaCodigoE;
    }

    public RegistroDataModelImpl getListaReferencia() {
        return listaReferencia;
    }

    public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
        this.listaReferencia = listaReferencia;
    }

    public RegistroDataModelImpl getListaReferenciaE() {
        return listaReferenciaE;
    }

    public void setListaReferenciaE(RegistroDataModelImpl listaReferenciaE) {
        this.listaReferenciaE = listaReferenciaE;
    }

    public RegistroDataModelImpl getListaFuenteRecurso() {
        return listaFuenteRecurso;
    }

    public void setListaFuenteRecurso(
        RegistroDataModelImpl listaFuenteRecurso) {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }

    public RegistroDataModelImpl getListaFuenteRecursoE() {
        return listaFuenteRecursoE;
    }

    public void setListaFuenteRecursoE(
        RegistroDataModelImpl listaFuenteRecursoE) {
        this.listaFuenteRecursoE = listaFuenteRecursoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public boolean isManTercero() {
        return manTercero;
    }

    public boolean isManAuxiliar() {
        return manAuxiliar;
    }

    public boolean isManCCto() {
        return manCCto;
    }

    public boolean isManReferencia() {
        return manReferencia;
    }

    public boolean isManFuente() {
        return manFuente;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

}
