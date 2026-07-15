package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.predial.enums.FrmconfiguraciontarifasControladorEnum;
import com.sysman.predial.enums.FrmconfiguraciontarifasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
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
 * @version 1, 21/05/2016
 * @version 2, 29/06/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 */
@ManagedBean
@ViewScoped
public class FrmconfiguraciontarifasControlador
                extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    private boolean estrato;
    private boolean clasePredio;
    private boolean tipoEstrato;
    private List<Registro> listaClaseInicial;
    private List<Registro> listaClaseFinal;
    private RegistroDataModelImpl listaPredioInicial;
    private RegistroDataModelImpl listaPredioInicialE;
    private RegistroDataModelImpl listaPredioFinal;
    private RegistroDataModelImpl listaPredioFinalE;
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoInicialE;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaTipoFinalE;
    private RegistroDataModelImpl listaCODTARIFA;
    private RegistroDataModelImpl listaCODTARIFAE;
    private Object auxOrdenI;
    private Object auxTrpran;
    private Object auxTrpano;
    private Object auxOrdenF;
    private String predioInicial;
    private String tipoPredioInicial;
    private String tipoPredioInicialE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;

    /**
     * Creates a new instance of FrmconfiguraciontarifasControlador
     */
    public FrmconfiguraciontarifasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCONFIGURACIONTARIFAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmconfiguraciontarifasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.IP_CONFIGURACIONTARIFAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaClaseInicial();
        cargarListaPredioInicial();
        cargarListaPredioInicialE();
        cargarListaTipoInicial();
        cargarListaTipoInicialE();
        cargarListaCODTARIFA();
        cargarListaCODTARIFAE();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaClaseInicial() {
        try {
            listaClaseInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmconfiguraciontarifasControladorUrlEnum.URL4814
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaClaseFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmconfiguraciontarifasControladorEnum.CLASEINICIAL
                        .getValue(),
                        registro.getCampos()
                                        .get(FrmconfiguraciontarifasControladorEnum.CLASE_INICIAL
                                                        .getValue()));
        try {
            listaClaseFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmconfiguraciontarifasControladorUrlEnum.URL5207
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPredioInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL5672
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaPredioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaPredioInicialE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL5672
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listaPredioInicialE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaPredioFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL8239
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmconfiguraciontarifasControladorEnum.CODIGOINICIAL
                        .getValue(),
                        predioInicial);
        listaPredioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaPredioFinalE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL8239
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmconfiguraciontarifasControladorEnum.CODIGOINICIAL
                        .getValue(),
                        predioInicial);
        listaPredioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL11071
                                                        .getValue());
        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoInicialE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL11071
                                                        .getValue());
        listaTipoInicialE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL12011
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmconfiguraciontarifasControladorEnum.TIPOINICIAL.getValue(),
                        tipoPredioInicial);
        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaTipoFinalE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL12011
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmconfiguraciontarifasControladorEnum.TIPOINICIAL.getValue(),
                        tipoPredioInicialE);
        listaTipoFinalE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCODTARIFA() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL13271
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCODTARIFA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmconfiguraciontarifasControladorEnum.TRPCOD
                                        .getValue());
    }

    public void cargarListaCODTARIFAE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconfiguraciontarifasControladorUrlEnum.URL13271
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCODTARIFAE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmconfiguraciontarifasControladorEnum.TRPCOD
                                        .getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAplicarTodas() {
        // <CODIGO_DESARROLLADO>
        try {
            if (ejbPredialOcho.aplicarTodasCfgTarifas(compania,
                            SessionUtil.getUser().getCodigo())) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB944"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * @param reg
     * @param indice
     * parametro que viene de la forma
     */
    public void oprimirCmdAplicar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO> indice
        try {
            if (ejbPredialOcho.aplicarConfigTarifa(compania,
                            new BigInteger(reg.getCampos().get("ID")
                                            .toString()),
                            SessionUtil.getUser().getCodigo())) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB944"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarClaseInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaClaseFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAream2Inicial() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEstrato() {
        // <CODIGO_DESARROLLADO>
        estrato = (boolean) registro.getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.IND_ESTRATO
                                        .getValue());
        if (!estrato) {
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.ESTRATO_INICIAL
                                            .getValue(), "");
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.ESTRATO_FINAL
                                            .getValue(), "");
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarclasePredio() {
        // <CODIGO_DESARROLLADO>
        clasePredio = (boolean) registro.getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.IND_CLASEPREDIO
                                        .getValue());
        if (!clasePredio) {
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.CLASE_INICIAL
                                            .getValue(), "");
            registro.getCampos().put("CLASE_FINAL", "");
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartipoEstrato() {
        // <CODIGO_DESARROLLADO>
        tipoEstrato = (boolean) registro.getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.IND_TIPOESTRATO
                                        .getValue());
        if (!tipoEstrato) {
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.TIPO_INICIAL
                                            .getValue(), "");
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.TIPO_FINAL
                                            .getValue(), "");
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPredioInicialC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("PREDIO_INICIAL", auxiliar)
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("NUMERO_ORDENI", auxOrdenI);
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ")
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPredioFinalC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("PREDIO_FINAL", auxiliar)
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.NUMERO_ORDENF
                                        .getValue(), auxOrdenF);
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ")
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarClaseInicialC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("FECHALARGA", "hola ")
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ")
        // <CODIGO_DESARROLLADO>
        registro.getCampos().putAll(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos());
        cargarListaClaseFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCODTARIFAC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.TRPRAN
                                        .getValue(),
                                        auxTrpran);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.TRPANO
                                        .getValue(),
                                        auxTrpano);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("CODTARIFA", auxiliar);
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ")
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAream2InicialC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("FECHALARGA", "hola ")
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ")
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEstratoC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("FECHALARGA", "hola ")
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ")
        estrato = (boolean) listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.IND_ESTRATO
                                        .getValue());
        if (!estrato) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.ESTRATO_INICIAL
                                            .getValue(), "");
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.ESTRATO_FINAL
                                            .getValue(), "");
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarclasePredioC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("FECHALARGA", "hola ")
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ")
        clasePredio = (boolean) listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.IND_CLASEPREDIO
                                        .getValue());
        if (!clasePredio) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.CLASE_INICIAL
                                            .getValue(), "");
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put("CLASE_FINAL", "");
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartipoEstratoC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("FECHALARGA", "hola ")
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ")
        // <CODIGO_DESARROLLADO>
        tipoEstrato = (boolean) listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.IND_TIPOESTRATO
                                        .getValue());
        if (!tipoEstrato) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.TIPO_INICIAL
                                            .getValue(), "");
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.TIPO_FINAL
                                            .getValue(), "");
        }
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaPredioInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PREDIO_INICIAL",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        predioInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        registro.getCampos().put("NUMERO_ORDENI",
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        cargarListaPredioFinal();
    }

    public void seleccionarFilaPredioInicialE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        auxOrdenI = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        cargarListaPredioFinalE();
    }

    public void seleccionarFilaPredioFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PREDIO_FINAL",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.NUMERO_ORDENF
                                        .getValue(),
                                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
    }

    public void seleccionarFilaPredioFinalE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        auxOrdenF = registro.getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.TRPRAN
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmconfiguraciontarifasControladorEnum.NUMERO_ORDENF
                                                                        .getValue()));

    }

    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.TIPO_INICIAL
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.CODIGO
                                                                        .getName()));
        tipoPredioInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        cargarListaTipoFinal();
    }

    public void seleccionarFilaTipoInicialE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoPredioInicialE = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        cargarListaTipoFinalE();
    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.TIPO_FINAL
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.CODIGO
                                                                        .getName()));
    }

    public void seleccionarFilaTipoFinalE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    public void seleccionarFilaCODTARIFA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODTARIFA",
                        registroAux.getCampos()
                                        .get(FrmconfiguraciontarifasControladorEnum.TRPCOD
                                                        .getValue()));
        registro.getCampos().put(FrmconfiguraciontarifasControladorEnum.TRPRAN
                        .getValue(),
                        registroAux.getCampos()
                                        .get(FrmconfiguraciontarifasControladorEnum.TRPRAN
                                                        .getValue()));
        registro.getCampos().put(FrmconfiguraciontarifasControladorEnum.TRPANO
                        .getValue(),
                        registroAux.getCampos()
                                        .get(FrmconfiguraciontarifasControladorEnum.TRPANO
                                                        .getValue()));
    }

    public void seleccionarFilaCODTARIFAE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.TRPCOD
                                        .getValue()),
                        "").toString();
        auxTrpano = registroAux.getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.TRPANO
                                        .getValue());
        auxTrpran = registroAux.getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.TRPRAN
                                        .getValue());
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        asignarValoresRegistro();
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

        boolean rta = false;
        validaRegistro(registro);
        // </CODIGO_DESARROLLADO>
        try {
            rta = ejbPredialOcho.validarConfigTarifas(
                            Boolean.parseBoolean(registro.getCampos()
                                            .get(FrmconfiguraciontarifasControladorEnum.IND_TIPOESTRATO
                                                            .getValue())
                                            .toString()),
                            registro.getCampos()
                                            .get(FrmconfiguraciontarifasControladorEnum.TIPO_INICIAL
                                                            .getValue())
                                            .toString(),
                            registro.getCampos()
                                            .get(FrmconfiguraciontarifasControladorEnum.TIPO_FINAL
                                                            .getValue())
                                            .toString(),
                            Boolean.parseBoolean(registro.getCampos()
                                            .get(FrmconfiguraciontarifasControladorEnum.IND_ESTRATO
                                                            .getValue())
                                            .toString()),
                            registro.getCampos()
                                            .get(FrmconfiguraciontarifasControladorEnum.ESTRATO_INICIAL
                                                            .getValue())
                                            .toString(),
                            registro.getCampos()
                                            .get(FrmconfiguraciontarifasControladorEnum.ESTRATO_FINAL
                                                            .getValue())
                                            .toString());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    private void validaRegistro(Registro registro) {
        if ((registro.getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.IND_CLASEPREDIO
                                        .getValue()) == null)
            || !Boolean.parseBoolean(registro.getCampos()
                            .get(FrmconfiguraciontarifasControladorEnum.IND_CLASEPREDIO
                                            .getValue())
                            .toString())) {
            registro.getCampos().put(
                            FrmconfiguraciontarifasControladorEnum.IND_CLASEPREDIO
                                            .getValue(),
                            false);
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.CLASE_INICIAL
                                            .getValue(), "");
            registro.getCampos().put("CLASE_FINAL", "");
        }
        if ((registro.getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.IND_ESTRATO
                                        .getValue()) == null)
            || !Boolean.parseBoolean(registro.getCampos()
                            .get(FrmconfiguraciontarifasControladorEnum.IND_ESTRATO
                                            .getValue())
                            .toString())) {
            registro.getCampos().put(
                            FrmconfiguraciontarifasControladorEnum.IND_ESTRATO
                                            .getValue(),
                            false);
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.ESTRATO_INICIAL
                                            .getValue(), "");
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.ESTRATO_FINAL
                                            .getValue(), "");
        }
        if ((registro.getCampos()
                        .get(FrmconfiguraciontarifasControladorEnum.IND_TIPOESTRATO
                                        .getValue()) == null)
            || !Boolean.parseBoolean(registro.getCampos()
                            .get(FrmconfiguraciontarifasControladorEnum.IND_TIPOESTRATO
                                            .getValue())
                            .toString())) {
            registro.getCampos().put(
                            FrmconfiguraciontarifasControladorEnum.IND_TIPOESTRATO
                                            .getValue(),
                            false);
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.TIPO_INICIAL
                                            .getValue(), "");
            registro.getCampos()
                            .put(FrmconfiguraciontarifasControladorEnum.TIPO_FINAL
                                            .getValue(), "");
        }

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
    }

    @Override
    public void asignarValoresRegistro() {
        registro.getCampos().put("AVALUO_INICIAL", "0");
        registro.getCampos().put("AVALUO_FINAL",
                        FrmconfiguraciontarifasControladorEnum.NUEVES
                                        .getValue());
        registro.getCampos().put("AREAM2_INICIAL", "0");
        registro.getCampos().put("AREAM2_FINAL",
                        FrmconfiguraciontarifasControladorEnum.NUEVES
                                        .getValue());
        registro.getCampos().put("AREACONST_INICIAL", "0");
        registro.getCampos().put("AREACONST_FINAL",
                        FrmconfiguraciontarifasControladorEnum.NUEVES
                                        .getValue());
        registro.getCampos().put("AREAHA_INICIAL", "0");
        registro.getCampos().put("AREAHA_FINAL",
                        FrmconfiguraciontarifasControladorEnum.NUEVES
                                        .getValue());
        registro.getCampos().put("PORCINICIAL", "0");
        registro.getCampos().put("PORCFINAL", "100");
        registro.getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.IND_TIPOESTRATO
                                        .getValue(), false);
        registro.getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.IND_CLASEPREDIO
                                        .getValue(), false);
        registro.getCampos()
                        .put(FrmconfiguraciontarifasControladorEnum.IND_ESTRATO
                                        .getValue(), false);
    }

    // <SET_GET_ATRIBUTOS>
    public boolean isEstrato() {
        return estrato;
    }

    public void setEstrato(boolean estrato) {
        this.estrato = estrato;
    }

    public boolean isClaseEstrato() {
        return clasePredio;
    }

    public void setClaseEstrato(boolean claseEstrato) {
        this.clasePredio = claseEstrato;
    }

    public boolean isTipoEstrato() {
        return tipoEstrato;
    }

    public void setTipoEstrato(boolean tipoEstrato) {
        this.tipoEstrato = tipoEstrato;
    }

    public void setListaCODTARIFA(RegistroDataModelImpl listaCODTARIFA) {
        this.listaCODTARIFA = listaCODTARIFA;
    }

    public List<Registro> getListaClaseInicial() {
        return listaClaseInicial;
    }

    public void setListaClaseInicial(List<Registro> listaClaseInicial) {
        this.listaClaseInicial = listaClaseInicial;
    }

    public List<Registro> getListaClaseFinal() {
        return listaClaseFinal;
    }

    public void setListaClaseFinal(List<Registro> listaClaseFinal) {
        this.listaClaseFinal = listaClaseFinal;
    }

    public RegistroDataModelImpl getListaPredioInicial() {
        return listaPredioInicial;
    }

    public void setListaPredioInicial(
        RegistroDataModelImpl listaPredioInicial) {
        this.listaPredioInicial = listaPredioInicial;
    }

    public RegistroDataModelImpl getListaPredioInicialE() {
        return listaPredioInicialE;
    }

    public void setListaPredioInicialE(
        RegistroDataModelImpl listaPredioInicialE) {
        this.listaPredioInicialE = listaPredioInicialE;
    }

    public RegistroDataModelImpl getListaPredioFinal() {
        return listaPredioFinal;
    }

    public void setListaPredioFinal(RegistroDataModelImpl listaPredioFinal) {
        this.listaPredioFinal = listaPredioFinal;
    }

    public RegistroDataModelImpl getListaPredioFinalE() {
        return listaPredioFinalE;
    }

    public void setListaPredioFinalE(RegistroDataModelImpl listaPredioFinalE) {
        this.listaPredioFinalE = listaPredioFinalE;
    }

    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoInicialE() {
        return listaTipoInicialE;
    }

    public void setListaTipoInicialE(RegistroDataModelImpl listaTipoInicialE) {
        this.listaTipoInicialE = listaTipoInicialE;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    public RegistroDataModelImpl getListaTipoFinalE() {
        return listaTipoFinalE;
    }

    public void setListaTipoFinalE(RegistroDataModelImpl listaTipoFinalE) {
        this.listaTipoFinalE = listaTipoFinalE;
    }

    public RegistroDataModelImpl getListaCODTARIFA() {
        return listaCODTARIFA;
    }

    public RegistroDataModelImpl getListaCODTARIFAE() {
        return listaCODTARIFAE;
    }

    public void setListaCODTARIFAE(RegistroDataModelImpl listaCODTARIFAE) {
        this.listaCODTARIFAE = listaCODTARIFAE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
