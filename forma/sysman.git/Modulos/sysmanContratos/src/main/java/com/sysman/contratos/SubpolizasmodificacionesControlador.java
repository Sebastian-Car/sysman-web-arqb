package com.sysman.contratos;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.enums.SubpolizasmodificacionesControladorEnum;
import com.sysman.contratos.enums.SubpolizasmodificacionesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
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
 * @version 1, 09/03/2016
 * 
 * @modifier amonroy
 * @version 2,14/08/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones y procedimientos que son
 * llamadas en el controlador
 * 
 * @author ybecerra
 * @version 3, 13/12/2017 - cambios en el combo de codigo
 * 
 */
@ManagedBean
@ViewScoped
public class SubpolizasmodificacionesControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Almaena el codigo del modulo en el que se esta trabajando en
     * este caso Contratos
     */
    private final String modulo;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "CODIGO" dentro del controlador
     */
    private final String cCodigo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "VALORASEGURADO" dentro del controlador
     */
    private final String cValorAsegurado;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "VLRANTERIOR" dentro del controlador
     */
    private final String cVlrAnterior;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "TIPO" dentro del controlador
     */
    private final String cTipo;

    private String tipoContrato;
    private String tipoContratoAfec;
    private String numeroContrato;
    private boolean manejaHistoricoEnPolizas;
    private String numeroAfectado;
    private boolean bloqueadoValorAsegurado;
    private String codigo;
    private String consecutivo;
    private String tipo;
    private String valorAsegurado;
    private String valorAnterior;
    private String aseguradora;
    private String observacion;
    private Date fechaExpedicion;
    private Date vigenciaDesde;
    private Date vigenciaHasta;
    private String valor;
    private String estado;
    private String valorActual;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTipo;
    private List<Registro> listaaseguradora;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigo;
    private RegistroDataModelImpl listaCodigoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Implementacion del EJB de SysmanUtil para hacer el llamado a la
     * funcion FC_GENCONSECUTIVO
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de SubpolizasmodificacionesControlador
     */
    public SubpolizasmodificacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cValorAsegurado = SubpolizasmodificacionesControladorEnum.VALORASEGURADO
                        .getValue();
        cVlrAnterior = SubpolizasmodificacionesControladorEnum.VLRANTERIOR
                        .getValue();
        cTipo = SubpolizasmodificacionesControladorEnum.TIPO.getValue();

        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {

                tipoContrato = parametrosEntrada.get("tipoContrato").toString();
                tipoContratoAfec = parametrosEntrada.get("tipoAfectado")
                                .toString();
                numeroContrato = parametrosEntrada.get("numeroContrato")
                                .toString();
                numeroAfectado = parametrosEntrada.get("numeroAfectado")
                                .toString();
            }

            numFormulario = GeneralCodigoFormaEnum.SUBPOLIZASMODIFICACIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(
                            SubpolizasmodificacionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        tabla = SubpolizasmodificacionesControladorEnum.POLIZAS.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        consultarParametro();
        cargarListaCodigo();
        cargarListaCodigoE();
        cargarListaTipo();
        cargarListaaseguradora();
        abrirFormulario();

        if (manejaHistoricoEnPolizas) {
            bloqueadoValorAsegurado = true;
        }

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        parametrosListado.put(GeneralParameterEnum.ORDEN.getName(),
                        numeroContrato);

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpolizasmodificacionesControladorUrlEnum.URL001
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpolizasmodificacionesControladorUrlEnum.URL002
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpolizasmodificacionesControladorUrlEnum.URL003
                                                        .getValue());
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubpolizasmodificacionesControladorUrlEnum.URL004
                                                        .getValue());

    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * Asigna la variable indice
     * 
     * @param indice
     * Variable a asignar en indice
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipo
     */
    public void cargarListaTipo() {
        try {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpolizasmodificacionesControladorUrlEnum.URL7842
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaaseguradora
     */
    public void cargarListaaseguradora() {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaaseguradora = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpolizasmodificacionesControladorUrlEnum.URL8145
                                                                            .getValue())
                                            .getUrl(), params));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCodigo
     */
    public void cargarListaCodigo() {
        UrlBean urlBean;

        if (manejaHistoricoEnPolizas) {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubpolizasmodificacionesControladorUrlEnum.URL254
                                                            .getValue());
        }
        else {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubpolizasmodificacionesControladorUrlEnum.URL244
                                                            .getValue());
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroAfectado);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        tipoContratoAfec);

        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    /**
     * 
     * Carga la lista listaCodigo
     * 
     */
    public void cargarListaCodigoE() {

        UrlBean urlBean;

        if (manejaHistoricoEnPolizas) {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubpolizasmodificacionesControladorUrlEnum.URL254
                                                            .getValue());
        }
        else {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SubpolizasmodificacionesControladorUrlEnum.URL244
                                                            .getValue());
        }

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroAfectado);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        tipoContratoAfec);

        listaCodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Tipo
     * 
     */
    public void cambiarTipo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control VALORACTUAL
     * 
     */
    public void cambiarVALORACTUAL() {
        // <CODIGO_DESARROLLADO>

        Double valAnterior = Double.parseDouble(
                        nvl(registro.getCampos().get(cVlrAnterior), "0.0")
                                        .toString());
        Double valActual = Double.parseDouble(
                        nvl(registro.getCampos().get("VLRACTUAL"), "0.0")
                                        .toString());
        registro.getCampos().put(cValorAsegurado,
                        valActual + valAnterior);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Tipo en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se
        // realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .remove(cCodigo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Codigo en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        cCodigo,
                        codigo);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivo);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        SubpolizasmodificacionesControladorEnum.TIPO
                                        .getValue(),
                        tipo);

        if (manejaHistoricoEnPolizas) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(SubpolizasmodificacionesControladorEnum.VLRANTERIOR
                                            .getValue(),
                                            valorAnterior);
            SysmanFunciones.nvl(listaInicial.getDatasource().get(rowNum % 10)
                            .getCampos()
                            .put(SubpolizasmodificacionesControladorEnum.VALORASEGURADO
                                            .getValue(),
                                            valorAnterior),
                            valorAnterior);
        }
        else {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(SubpolizasmodificacionesControladorEnum.VALORASEGURADO
                                            .getValue(),
                                            valorAsegurado);
        }
        listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.VLRACTUAL
                                        .getValue(),
                                        valorActual);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.ASEGURADORA
                                        .getValue(),
                                        aseguradora);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.OBSERVACIONES
                                        .getValue(),
                                        observacion);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.FECHAEXPEDICION
                                        .getValue(), fechaExpedicion);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.VIGENCIADESDE
                                        .getValue(),
                                        vigenciaDesde);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.VIGENCIAHASTA
                                        .getValue(),
                                        vigenciaHasta);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.VALOR.getName(),
                        valor);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.ESTADO.getName(),
                        estado);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        SubpolizasmodificacionesControladorEnum.FECHAAPROBACIONCUMPLIMIENTO
                                        .getValue(),
                        null);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        SubpolizasmodificacionesControladorEnum.FECHAAPROBACIONRESPONSABILIDAD
                                        .getValue(),
                        null);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control VALORACTUAL en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarVALORACTUALC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        Double valAnterior = Double.parseDouble(
                        nvl(listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get(cVlrAnterior), "0.0")
                                                        .toString());
        Double valActual = Double.parseDouble(
                        nvl(listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("VLRACTUAL"), "0.0")
                                                        .toString());
        listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().put(cValorAsegurado,
                                        valActual + valAnterior);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cCodigo,
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CONSECUTIVO
                                                        .getName()));
        registro.getCampos().put(cTipo,
                        registroAux.getCampos()
                                        .get(cTipo));

        if (manejaHistoricoEnPolizas) {
            registro.getCampos()
                            .put(SubpolizasmodificacionesControladorEnum.VLRANTERIOR
                                            .getValue(),
                                            registroAux.getCampos()
                                                            .get(SubpolizasmodificacionesControladorEnum.VLRANTERIOR
                                                                            .getValue()));
            registro.getCampos()
                            .put(SubpolizasmodificacionesControladorEnum.VALORASEGURADO
                                            .getValue(),
                                            registroAux.getCampos()
                                                            .get(SubpolizasmodificacionesControladorEnum.VLRANTERIOR
                                                                            .getValue()));
        }
        else {
            registro.getCampos()
                            .put(SubpolizasmodificacionesControladorEnum.VALORASEGURADO
                                            .getValue(),
                                            registroAux.getCampos()
                                                            .get(SubpolizasmodificacionesControladorEnum.VALORASEGURADO
                                                                            .getValue()));
        }

        registro.getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.ASEGURADORA
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SubpolizasmodificacionesControladorEnum.ASEGURADORA
                                                                        .getValue()));
        registro.getCampos()
                        .put(GeneralParameterEnum.OBSERVACIONES.getName(),
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.OBSERVACIONES
                                                                        .getName()));
        registro.getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.FECHAEXPEDICION
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SubpolizasmodificacionesControladorEnum.FECHAEXPEDICION
                                                                        .getValue()));
        registro.getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.VIGENCIADESDE
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SubpolizasmodificacionesControladorEnum.VIGENCIADESDE
                                                                        .getValue()));
        registro.getCampos()
                        .put(SubpolizasmodificacionesControladorEnum.VIGENCIAHASTA
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SubpolizasmodificacionesControladorEnum.VIGENCIAHASTA
                                                                        .getValue()));
        registro.getCampos()
                        .put(GeneralParameterEnum.VALOR.getName(),
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.VALOR
                                                                        .getName()));
        registro.getCampos()
                        .put(GeneralParameterEnum.ESTADO.getName(),
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.ESTADO
                                                                        .getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        codigo = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
        consecutivo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName()), "")
                        .toString();
        tipo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(SubpolizasmodificacionesControladorEnum.TIPO
                                        .getValue()),
                        "")
                        .toString();
        if (manejaHistoricoEnPolizas) {
            valorAnterior = SysmanFunciones.nvl(registroAux.getCampos()
                            .get(SubpolizasmodificacionesControladorEnum.VLRANTERIOR
                                            .getValue()),
                            "")
                            .toString();
        }
        else {
            valorAsegurado = SysmanFunciones.nvl(registroAux.getCampos()
                            .get(SubpolizasmodificacionesControladorEnum.VALORASEGURADO
                                            .getValue()),
                            "")
                            .toString();
        }

        aseguradora = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(SubpolizasmodificacionesControladorEnum.ASEGURADORA
                                        .getValue()),
                        "")
                        .toString();
        observacion = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(SubpolizasmodificacionesControladorEnum.OBSERVACIONES
                                        .getValue()),
                        "")
                        .toString();
        fechaExpedicion = (Date) SysmanFunciones.nvl(registroAux.getCampos()
                        .get(SubpolizasmodificacionesControladorEnum.FECHAEXPEDICION
                                        .getValue()),
                        null);

        vigenciaDesde = (Date) SysmanFunciones.nvl(registroAux.getCampos()
                        .get(SubpolizasmodificacionesControladorEnum.VIGENCIADESDE
                                        .getValue()),
                        null);
        vigenciaHasta = (Date) SysmanFunciones.nvl(registroAux.getCampos()
                        .get(SubpolizasmodificacionesControladorEnum.VIGENCIAHASTA
                                        .getValue()),
                        null);
        valor = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.VALOR.getName()),
                        "")
                        .toString();
        estado = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.ESTADO.getName()),
                        "")
                        .toString();
        valorActual = registroAux.getCampos()
                        .get(SubpolizasmodificacionesControladorEnum.VLRACTUAL
                                        .getValue())
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Realiza la consulta del valor asignado al parametro MANEJA
     * HISTORICO EN POLIZAS en la base de datos
     */
    private void consultarParametro() {
        try {
            manejaHistoricoEnPolizas = "SI"
                            .equals(SysmanFunciones.nvlStr(
                                            ejbSysmanUtil.consultarParametro(
                                                            compania,
                                                            "MANEJA HISTORICO EN POLIZAS",
                                                            modulo, new Date(),
                                                            true),
                                            "NO"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * @param rs
     */
    private void actualizarInformacionRegistro(Registro rs) {
        bloqueadoValorAsegurado = true;
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.NUMERO.getName(), numeroAfectado);
            params.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(cCodigo));
            params.put(cTipo, registro.getCampos().get(cTipo));

            Registro rsModPoliza = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubpolizasmodificacionesControladorUrlEnum.URL006
                                                                            .getValue())
                                            .getUrl(), params));

            if (rsModPoliza != null && rsModPoliza.getCampos()
                            .get(cValorAsegurado) != null) {
                registro.getCampos().put(cVlrAnterior, rsModPoliza
                                .getCampos().get(cValorAsegurado));
            }
            else {
                registro.getCampos().put(cVlrAnterior,
                                rs.getCampos().get(cValorAsegurado));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     * 
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cValorAsegurado)
            && !manejaHistoricoEnPolizas) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2125"));
            return false;
        }

        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(),
                            tipoContrato);
            registro.getCampos()
                            .put(SubpolizasmodificacionesControladorEnum.ORDENDECOMPRA
                                            .getValue(), numeroContrato);

            if (manejaHistoricoEnPolizas) {
                Map<String, Object> params = new TreeMap<>();
                params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                params.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get(cCodigo));
                params.put(GeneralParameterEnum.NUMERO.getName(),
                                numeroAfectado);
                params.put(SubpolizasmodificacionesControladorEnum.TIPO
                                .getValue(), registro.getCampos().get(cTipo));

                Registro rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SubpolizasmodificacionesControladorUrlEnum.URL005
                                                                                .getValue())
                                                .getUrl(), params));

                if (rs != null) {
                    actualizarInformacionRegistro(rs);
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2126"));
                    return false;
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        String sucursal = service.buscarEnLista(
                        registro.getCampos().get("ASEGURADORA")
                                        .toString(),
                        "NITASEGURADORA",
                        "SUCURSAL",
                        listaaseguradora);

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CLASEORDEN.getName());
        registro.getCampos()
                        .remove(SubpolizasmodificacionesControladorEnum.ORDENDECOMPRA
                                        .getValue());
        registro.getCampos()
                        .remove(SubpolizasmodificacionesControladorEnum.TIPODESC
                                        .getValue());
        registro.getCampos()
                        .remove(SubpolizasmodificacionesControladorEnum.NOMBREASEGURADORA
                                        .getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {

        Map<String, Object> parametros = SessionUtil.getFlash();
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.ADICIONESPCONTRATOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoContrato
     * 
     * @return tipoContrato
     */
    public String getTipoContrato() {
        return tipoContrato;
    }

    /**
     * Asigna la variable tipoContrato
     * 
     * @param tipoContrato
     * Variable a asignar en tipoContrato
     */
    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    /**
     * Retorna la variable numeroContrato
     * 
     * @return numeroContrato
     */
    public String getNumeroContrato() {
        return numeroContrato;
    }

    /**
     * Asigna la variable numeroContrato
     * 
     * @param numeroContrato
     * Variable a asignar en numeroContrato
     */
    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    /**
     * Retorna la variable manejaHistoricoEnPolizas
     * 
     * @return manejaHistoricoEnPolizas
     */
    public boolean isManejaHistoricoEnPolizas() {
        return manejaHistoricoEnPolizas;
    }

    /**
     * Asigna la variable manejaHistoricoEnPolizas
     * 
     * @param manejaHistoricoEnPolizas
     * Variable a asignar en manejaHistoricoEnPolizas
     */
    public void setManejaHistoricoEnPolizas(boolean manejaHistoricoEnPolizas) {
        this.manejaHistoricoEnPolizas = manejaHistoricoEnPolizas;
    }

    /**
     * Retorna la variable numeroAfectado
     * 
     * @return numeroAfectado
     */
    public String getNumeroAfectado() {
        return numeroAfectado;
    }

    /**
     * Asigna la variable numeroAfectado
     * 
     * @param numeroAfectado
     * Variable a asignar en numeroAfectado
     */
    public void setNumeroAfectado(String numeroAfectado) {
        this.numeroAfectado = numeroAfectado;
    }

    /**
     * Retorna la variable bloqueadoValorAsegurado
     * 
     * @return bloqueadoValorAsegurado
     */
    public boolean isBloqueadoValorAsegurado() {
        return bloqueadoValorAsegurado;
    }

    /**
     * Asigna la variable bloqueadoValorAsegurado
     * 
     * @param bloqueadoValorAsegurado
     * Variable a asignar en bloqueadoValorAsegurado
     */
    public void setBloqueadoValorAsegurado(boolean bloqueadoValorAsegurado) {
        this.bloqueadoValorAsegurado = bloqueadoValorAsegurado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipo
     * 
     * @return listaTipo
     */
    public List<Registro> getListaTipo() {
        return listaTipo;
    }

    /**
     * Asigna la lista listaTipo
     * 
     * @param listaTipo
     * Variable a asignar en listaTipo
     */
    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }

    /**
     * Retorna la lista listaaseguradora
     * 
     * @return listaaseguradora
     */
    public List<Registro> getListaaseguradora() {
        return listaaseguradora;
    }

    /**
     * Asigna la lista listaaseguradora
     * 
     * @param listaaseguradora
     * Variable a asignar en listaaseguradora
     */
    public void setListaaseguradora(List<Registro> listaaseguradora) {
        this.listaaseguradora = listaaseguradora;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigo
     * 
     * @return listaCodigo
     */
    public RegistroDataModelImpl getListaCodigo() {
        return listaCodigo;
    }

    /**
     * Asigna la lista listaCodigo
     * 
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigo(RegistroDataModelImpl listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    /**
     * Retorna la lista listaCodigo
     * 
     * @return listaCodigo
     */
    public RegistroDataModelImpl getListaCodigoE() {
        return listaCodigoE;
    }

    /**
     * Asigna la lista listaCodigo
     * 
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigoE(RegistroDataModelImpl listaCodigoE) {
        this.listaCodigoE = listaCodigoE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>

}
