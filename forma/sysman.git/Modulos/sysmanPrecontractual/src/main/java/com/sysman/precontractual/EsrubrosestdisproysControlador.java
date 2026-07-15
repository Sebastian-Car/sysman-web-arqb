package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.EsrubrosestdisproysControladorEnum;
import com.sysman.precontractual.enums.EsrubrosestdisproysControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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

/**
 *
 * @author apineda
 * @version 1, 30/03/2016
 * 
 * @version 2, 24/08/2017, <strong>pespitia</strong>:<br>
 * Se reemplazó el número del formulario por enumerado.<br>
 * Refactoring de sentencias SQL.<br>
 * Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class EsrubrosestdisproysControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;

    /** Constante a nivel de clase que aloja la cadena: AUXILIAR */
    private final String cAuxiliar;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ANO</code>
     */
    private final String cAno;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ANIO</code>
     */
    private final String cAnio;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CENTRO_COSTO</code>
     */
    private final String cCentroCosto;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CLASES</code>
     */
    private final String cClases;

    /**
     * Constante a nivel de clase que aloja la cadena: CODIGO_CUENTA
     */
    private final String cCodigoCuenta;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COD_ESTUDIO</code>
     */
    private final String cCodEstudio;

    /** Constante a nivel de clase que aloja la cadena: COMPROBANTE */
    private final String cComprobante;

    /** Constante a nivel de clase que aloja la cadena: CONSECUTIVO */
    private final String cConsecutivo;

    /** Constante a nivel de clase que aloja la cadena: CREDITO */
    private final String cCredito;

    /** Constante a nivel de clase que aloja la cadena: DEBITO */
    private final String cDebito;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>DESTINO</code>
     */
    private final String cDestino;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>FECHA_DIS</code>
     */
    private final String cFechaDis;

    /** Constante a nivel de clase que aloja la cadena: NOMBREAUX */
    private final String cNombreAux;

    /** Constante a nivel de clase que aloja la cadena: NUMERO */
    private final String cNumero;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPO</code>
     */
    private final String cTipo;

    /** Constante a nivel de clase que aloja la cadena: TIPO_CPTE */
    private final String cTipoCpte;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    private int indice;
    private RegistroDataModelImpl listaConsecutivo;
    private RegistroDataModelImpl listaConsecutivoE;
    private RegistroDataModelImpl listaComprobante;
    private RegistroDataModelImpl listaComprobanteE;
    private String auxiliar;
    private List<Registro> listaTipoCpte;
    private List<Registro> listaAnio;
    private String anio;
    private String tipoCpte;
    private String comprobante;
    private String codEstudio;
    private String tipoDia;
    private String accion;
    private String consecutivo;

    String cuenta;
    String fuente;
    String debito;
    String credito;
    String nombreaux;

    private final Map<String, Object> miParametros;
    private HashMap<String, Object> ridEstudio;
    private String vigenciaPeriodo;

    /**
     * Atributo que gestiona la visibilidad de los botones de crear,
     * editar y eliminar
     */
    private boolean esCreador;

    /**
     * Atributo que gestiona las funciones y procedimientos del
     * paquete <code>PCK_SYSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of EsrubrosestdisproysControlador
     */
    @SuppressWarnings("unchecked")
    public EsrubrosestdisproysControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        miParametros = SessionUtil.getFlash();

        cAuxiliar = GeneralParameterEnum.AUXILIAR.getName();
        cAno = GeneralParameterEnum.ANO.getName();
        cAnio = EsrubrosestdisproysControladorEnum.ANIO.getValue();
        cCentroCosto = GeneralParameterEnum.CENTRO_COSTO.getName();
        cCodEstudio = GeneralParameterEnum.COD_ESTUDIO.getName();
        cClases = EsrubrosestdisproysControladorEnum.CLASES.getValue();
        cComprobante = GeneralParameterEnum.COMPROBANTE.getName();
        cConsecutivo = GeneralParameterEnum.CONSECUTIVO.getName();
        cCredito = EsrubrosestdisproysControladorEnum.CREDITO.getValue();
        cDebito = EsrubrosestdisproysControladorEnum.DEBITO.getValue();
        cDestino = GeneralParameterEnum.DESTINO.getName();
        cFechaDis = EsrubrosestdisproysControladorEnum.FECHA_DIS.getValue();
        cNombreAux = EsrubrosestdisproysControladorEnum.NOMBREAUX.getValue();
        cNumero = GeneralParameterEnum.NUMERO.getName();
        cTipo = EsrubrosestdisproysControladorEnum.TIPO.getValue();
        cTipoCpte = GeneralParameterEnum.TIPO_CPTE.getName();
        cCompania = GeneralParameterEnum.COMPANIA.getName();

        cCodigoCuenta = EsrubrosestdisproysControladorEnum.CODIGO_CUENTA
                        .getValue();

        try {
            // 590
            numFormulario = GeneralCodigoFormaEnum.ESRUBROSESTDISPROYS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            if (parametros != null) {
                ridEstudio = (HashMap<String, Object>) miParametros
                                .get("ridEstudio");

                codEstudio = (String) miParametros.get("codEstudio");
                tipoDia = (String) miParametros.get("tipoDia");
                setVigenciaPeriodo(
                                (String) miParametros.get("vigenciaPeriodo"));

                esCreador = Boolean.parseBoolean(
                                miParametros.get("esCreador").toString());

                miParametros.put("rid", ridEstudio);
                miParametros.remove("ridEstudio");
            }

            SessionUtil.cleanFlash();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ES_RUBROS_EST_DIS;
        registro = new Registro();

        buscarLlave();
        reasignarOrigen();
        cargarListaTipoCpte();
        cargarListaAnio();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put("CODIGO", codEstudio);
    }

    public void cargarListaTipoCpte() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cClases, "DIS,ADD,DMR");

        try {
            listaTipoCpte = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EsrubrosestdisproysControladorUrlEnum.URL6771
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EsrubrosestdisproysControladorUrlEnum.URL7214
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaComprobante() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EsrubrosestdisproysControladorUrlEnum.URL7608
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cAnio, anio);
        param.put(cTipo, tipoCpte);

        listaComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumero);
    }

    public void cargarListaComprobanteE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EsrubrosestdisproysControladorUrlEnum.URL7608
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cAnio, anio);
        param.put(cTipo, tipoCpte);

        listaComprobanteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumero);
    }

    public void cargarListaConsecutivo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EsrubrosestdisproysControladorUrlEnum.URL9053
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cAno, anio);
        param.put("TIPO_CPTE", tipoCpte);
        param.put("COMPROBANTE", comprobante);

        listaConsecutivo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cConsecutivo);
    }

    public void cargarListaConsecutivoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EsrubrosestdisproysControladorUrlEnum.URL9053
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cAno, anio);
        param.put("TIPO_CPTE", tipoCpte);
        param.put("COMPROBANTE", comprobante);

        listaConsecutivoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cConsecutivo);
    }

    public void cambiarTipoCpte() {
        // <CODIGO_DESARROLLADO>
        tipoCpte = (String) registro.getCampos().get(cTipoCpte);
        registro.getCampos().put(cComprobante, "");

        blanquear();
        cargarListaComprobante();
        cargarListaConsecutivo();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        anio = (String) registro.getCampos().get(cAno);

        registro.getCampos().put(cComprobante, "");

        blanquear();
        cargarListaComprobante();
        cargarListaConsecutivo();
        // </CODIGO_DESARROLLADO>

    }

    public void cambiarConsecutivoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        blanquearSeleccionado(rowNum);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cConsecutivo, consecutivo);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cCodigoCuenta, cuenta);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cAuxiliar, fuente);

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cDebito,
                        debito);

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cCredito,
                        credito);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreAux, nombreaux);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTipoCpteC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        tipoCpte = (String) listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(cTipoCpte);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cComprobante, "");

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cConsecutivo, "");

        blanquearSeleccionado(rowNum);
        cargarListaComprobanteE();
        cargarListaConsecutivoE();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnioC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        anio = (String) listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get(cAno);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cComprobante, "");

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cConsecutivo, "");

        blanquearSeleccionado(rowNum);
        cargarListaComprobanteE();
        cargarListaConsecutivoE();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarComprobanteC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        blanquearSeleccionado(rowNum);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cConsecutivo, "");

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cComprobante, comprobante);

        // </CODIGO_DESARROLLADO>
    }

    public void blanquearSeleccionado(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cCodigoCuenta, "");

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cAuxiliar, "");

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cDebito,
                        "");

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cCredito,
                        "");

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(cNombreAux, "");
    }

    public void seleccionarFilaConsecutivo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cConsecutivo,
                        registroAux.getCampos().get(cConsecutivo));

        registro.getCampos().put(cCodigoCuenta,
                        registroAux.getCampos().get("CUENTA"));

        registro.getCampos().put(cAuxiliar,
                        registroAux.getCampos().get(cAuxiliar));

        registro.getCampos().put(cDebito,
                        registroAux.getCampos().get("VALOR_DEBITO"));

        registro.getCampos().put(cCredito,
                        registroAux.getCampos().get("VALOR_CREDITO"));

        registro.getCampos().put(cNombreAux,
                        registroAux.getCampos().get(cNombreAux));

        registro.getCampos().put(cCentroCosto,
                        registroAux.getCampos().get(cCentroCosto));

        registro.getCampos().put(cFechaDis,
                        registroAux.getCampos().get(cFechaDis));

    }

    public void seleccionarFilaConsecutivoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        if (!SysmanFunciones.nvl(registroAux.getCampos().get(cConsecutivo), "")
                        .toString().trim().isEmpty()) {
            auxiliar = registroAux.getCampos().get(cConsecutivo).toString();
            consecutivo = auxiliar;
            cuenta = registroAux.getCampos().get("CUENTA").toString();
            fuente = registroAux.getCampos().get(cAuxiliar).toString();
            debito = registroAux.getCampos().get("VALOR_DEBITO").toString();
            credito = registroAux.getCampos().get("VALOR_CREDITO").toString();
            nombreaux = registroAux.getCampos().get(cNombreAux).toString();
        }
        else {
            auxiliar = null;
            consecutivo = null;
            cuenta = null;
            fuente = null;
            debito = null;
            credito = null;
            nombreaux = null;
        }
    }

    public void seleccionarFilaComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(cComprobante,
                        registroAux.getCampos().get(cNumero));

        comprobante = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cNumero), "")
                        .toString();

        blanquear();
        cargarListaConsecutivo();
    }

    public void seleccionarFilaComprobanteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        if (!registroAux.getCampos().get(cNumero).toString().trim()
                        .isEmpty()) {
            auxiliar = registroAux.getCampos().get(cNumero).toString();
            comprobante = auxiliar;
            cargarListaConsecutivoE();
        }
        else {
            auxiliar = null;
            comprobante = null;
        }
    }

    public void blanquear() {
        registro.getCampos().put(cConsecutivo, "");
        registro.getCampos().put(cCodigoCuenta, "");
        registro.getCampos().put(cAuxiliar, "");
        registro.getCampos().put(cDebito, "");
        registro.getCampos().put(cCredito, "");
        registro.getCampos().put(cNombreAux, "");
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
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cCodEstudio, codEstudio);
        registro.getCampos().put(cDestino, tipoDia);

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public String validarCampos() {
        StringBuilder camposModificados = new StringBuilder();

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), cAno)) {
            camposModificados.append(" -> Año");
        }

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cTipoCpte)) {
            camposModificados.append(" -> Tipo Comprobante");
        }

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cComprobante)) {
            camposModificados.append(" -> Comprobante");
        }

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cConsecutivo)) {
            camposModificados.append(" -> Id Rubro");
        }

        return camposModificados.toString();
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        /* Si el anio esta cerrado */
        if (validarEstadoAnio()) {
            return false;
        }

        registro.getCampos().remove(cNombreAux);

        String camposVacios = validarCampos();

        if (camposVacios.length() > 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3490")
                            .replace("#CAMPOS#", camposVacios));
            return false;
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Verifica el estado del anio y retorna true si esta cerrado.
     * 
     * @return <code>true</code>: El anio esta cerrado.
     */
    private boolean validarEstadoAnio() {
        try {
            String estado = ejbSysmanUtil.verificarEstadoPeriodoAnual(compania,
                            Integer.parseInt(anio), Integer.parseInt(modulo),
                            1);

            /* Si el anio esta cerrado */
            if (!"A".equals(estado)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3491"));
                return true;
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return false;
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
        return !validarEstadoAnio();
        // </CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cCodEstudio);
        registro.getCampos().remove(cCodigoCuenta);
        registro.getCampos().remove(cAuxiliar);
        registro.getCampos().remove(cDebito);
        registro.getCampos().remove(cCredito);
        registro.getCampos().remove(cCentroCosto);
        registro.getCampos().remove(cDestino);
        registro.getCampos().remove(cFechaDis);
        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        tipoCpte = registro.getCampos().get(cTipoCpte).toString();
        anio = registro.getCampos().get(cAno).toString();
        comprobante = registro.getCampos().get(cComprobante).toString();

        cargarListaComprobanteE();
        cargarListaConsecutivoE();
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(miParametros);

        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /** Metodo ejecutado al cambiar el control AUXILIAR */
    public void cambiarAUXILIAR() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AUXILIAR en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarAUXILIARC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getDebito() {
        return debito;
    }

    public void setDebito(String debito) {
        this.debito = debito;
    }

    public String getCredito() {
        return credito;
    }

    public void setCredito(String credito) {
        this.credito = credito;
    }

    public String getNombreaux() {
        return nombreaux;
    }

    public void setNombreaux(String nombreaux) {
        this.nombreaux = nombreaux;
    }

    public List<Registro> getListaTipoCpte() {
        return listaTipoCpte;
    }

    public void setListaTipoCpte(List<Registro> listaTipoCpte) {
        this.listaTipoCpte = listaTipoCpte;
    }

    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    public RegistroDataModelImpl getListaConsecutivo() {
        return listaConsecutivo;
    }

    public void setListaConsecutivo(RegistroDataModelImpl listaConsecutivo) {
        this.listaConsecutivo = listaConsecutivo;
    }

    public RegistroDataModelImpl getListaConsecutivoE() {
        return listaConsecutivoE;
    }

    public void setListaConsecutivoE(RegistroDataModelImpl listaConsecutivoE) {
        this.listaConsecutivoE = listaConsecutivoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getTipoCpte() {
        return tipoCpte;
    }

    public void setTipoCpte(String tipoCpte) {
        this.tipoCpte = tipoCpte;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getCodEstudio() {
        return codEstudio;
    }

    public void setCodEstudio(String codEstudio) {
        this.codEstudio = codEstudio;
    }

    public String getTipoDia() {
        return tipoDia;
    }

    public void setTipoDia(String tipoDia) {
        this.tipoDia = tipoDia;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public RegistroDataModelImpl getListaComprobante() {
        return listaComprobante;
    }

    public void setListaComprobante(RegistroDataModelImpl listaComprobante) {
        this.listaComprobante = listaComprobante;
    }

    public RegistroDataModelImpl getListaComprobanteE() {
        return listaComprobanteE;
    }

    public void setListaComprobanteE(RegistroDataModelImpl listaComprobanteE) {
        this.listaComprobanteE = listaComprobanteE;
    }

    public Map<String, Object> getRidEstudio() {
        return ridEstudio;
    }

    public void setRidEstudio(Map<String, Object> ridEstudio) {
        this.ridEstudio = (HashMap<String, Object>) ridEstudio;
    }

    public boolean isEsCreador() {
        return esCreador;
    }

    public void setEsCreador(boolean esCreador) {
        this.esCreador = esCreador;
    }

    public String getVigenciaPeriodo() {
        return vigenciaPeriodo;
    }

    public void setVigenciaPeriodo(String vigenciaPeriodo) {
        this.vigenciaPeriodo = vigenciaPeriodo;
    }
}
