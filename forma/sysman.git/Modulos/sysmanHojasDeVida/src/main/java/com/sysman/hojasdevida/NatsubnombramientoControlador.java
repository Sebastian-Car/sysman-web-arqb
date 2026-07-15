/*-
 * NatsubnombramientosControlador.java
 *
 * 1.0
 *
 * 16/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.NatdatospersonalesControladorEnum;
import com.sysman.hojasdevida.enums.NatsubnombramientoControladorEnum;
import com.sysman.hojasdevida.enums.NatsubnombramientoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 09/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 * version 2.0 09/07/2018, Se agrega método oprimirRetiros(), se mueve
 * boton desde formulario natdatospersonales
 * @asana
 */
@ManagedBean
@ViewScoped
public class NatsubnombramientoControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private String noIdCargo;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * listado de tipos de actos administrativos
     */
    private List<Registro> listaTIPO;
    /**
     * listado de dependencias
     */
    private RegistroDataModelImpl listaDEPEANTE;
    /**
     * listado de dependencias
     */
    private RegistroDataModelImpl listaDEPENUEV;
    /**
     * listado de niveles de escalafon
     */
    private List<Registro> listaEscalafon;
    /**
     * listado de tipo de actos
     */
    private List<Registro> listaTIPODOCUACTO;
    /**
     * listado de anios de funcion
     */
    private List<Registro> listaANOFUNCION;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * listado de cargos
     */
    private RegistroDataModelImpl listaCargo;
    /**
     * listado de categorias
     */
    private RegistroDataModelImpl listaCATEGORIA;
    /**
     * listado de areas
     */
    private RegistroDataModelImpl listaCODAREA;

    /**
     * valor sucursal recibida del formulario principal
     */
    private String codSucursal;

    /**
     *
     * valor del numero de documento recibido por el formulario
     * principal
     */
    private String codNumedocu;

    /**
     * valor del codigo de la persona seleccionada en el formulario
     * principal
     */
    private String codPersona;

    /**
     * formato usado para los marcos en la forma
     */
    private String formato;

    /**
     * formato usado para los marcos en la forma
     */
    private String redireccion;

    private String dependencia;
    /**
     * llave del registro del formulario principal para volver al
     * registro que estaba seleccionado
     */
    private Map<String, Object> ridDatosPersonales;

    private Map<String, Object> ridNombramiento;

    private StreamedContent archivoDescarga;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de NatsubnombramientosControlador
     */
    @SuppressWarnings("unchecked")
    public NatsubnombramientoControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.SUB_NOMBRAMIENTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("ridDatos");
                ridNombramiento = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                redireccion = SysmanFunciones
                                .nvl(parametrosEntrada.get("redireccion"), "0")
                                .toString();
                if ("-1".equals(redireccion)) {
                    rid = (Map<String, Object>) parametrosEntrada.get("rid");
                }

                codSucursal = SysmanFunciones
                                .nvl(parametrosEntrada.get("sucursal"), "")
                                .toString();
                codNumedocu = SysmanFunciones
                                .nvl(parametrosEntrada.get("dp_numedocu"), "")
                                .toString();
                codPersona = SysmanFunciones
                                .nvl(parametrosEntrada.get("codigo"), "")
                                .toString();
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCargo();
        cargarListaCATEGORIA();
        cargarListaCODAREA();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTIPO();
        cargarListaDEPEANTE();
        cargarListaDEPENUEV();
        cargarListaEscalafon();
        cargarListaTIPODOCUACTO();
        cargarListaANOFUNCION();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.NAT_NOMBRAMIENTO;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codNumedocu);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        codSucursal);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaTIPO
     *
     */
    public void cargarListaTIPO() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaTIPO = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatsubnombramientoControladorUrlEnum.URL8587
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaDEPEANTE
     *
     */
    public void cargarListaDEPEANTE() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubnombramientoControladorUrlEnum.URL8585
                                                        .getValue());
        listaDEPEANTE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void seleccionarFilaDEPEANTE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NO_DEPEANTE",
                        registroAux.getCampos().get("CODIGO"));
    }

    public void seleccionarFilaDEPENUEV(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = registro.getCampos().put("NO_DEPENUEV",
                        registroAux.getCampos().get("CODIGO")).toString();
        registro.getCampos().put(
                        NatsubnombramientoControladorEnum.NOMBREDEPENDENCIA
                                        .getValue(),
                        SysmanFunciones.nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "").toString());
    }

    /**
     *
     * Carga la lista listaDEPENUEV
     *
     */
    public void cargarListaDEPENUEV() {
        listaDEPENUEV = listaDEPEANTE;
    }

    /**
     *
     * Carga la lista listaEscalafon
     *
     */
    public void cargarListaEscalafon() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaEscalafon = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatsubnombramientoControladorUrlEnum.URL8586
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaTIPODOCUACTO
     *
     */
    public void cargarListaTIPODOCUACTO() {
        try {
            listaTIPODOCUACTO = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatsubnombramientoControladorUrlEnum.URL8580
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaANOFUNCION
     *
     */
    public void cargarListaANOFUNCION() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), SysmanFunciones
                        .nvl(registro.getCampos().get(
                                        NatsubnombramientoControladorEnum.CODAREA
                                                        .getValue()),
                                        "")
                        .toString());
        try {
            listaANOFUNCION = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            NatsubnombramientoControladorUrlEnum.URL8581
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaCargo
     *
     */
    public void cargarListaCargo() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(NatsubnombramientoControladorEnum.ESCALAFON.getValue(),
                        SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        NatsubnombramientoControladorEnum.NO_ESCALAFON
                                                                        .getValue()),
                                                        ""));
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubnombramientoControladorUrlEnum.URL8583
                                                        .getValue());
        listaCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        NatsubnombramientoControladorEnum.ID_DE_CARGO
                                        .getValue());
    }

    /**
     *
     * Carga la lista listaCATEGORIA
     *
     */
    public void cargarListaCATEGORIA() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(NatsubnombramientoControladorEnum.ESCALAFON.getValue(),
                        SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        NatsubnombramientoControladorEnum.NO_ESCALAFON
                                                                        .getValue()),
                                                        ""));
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano((Date) SysmanFunciones.nvl(
                                        registro.getCampos().get(
                                                        NatsubnombramientoControladorEnum.NO_FECHAEFECTIVIDAD
                                                                        .getValue()),
                                        new Date())));
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubnombramientoControladorUrlEnum.URL8584
                                                        .getValue());
        listaCATEGORIA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        NatsubnombramientoControladorEnum.ID_DE_CATEGORIA
                                        .getValue());
    }

    /**
     *
     * Carga la lista listaCODAREA
     *
     */
    public void cargarListaCODAREA() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatsubnombramientoControladorUrlEnum.URL8582
                                                        .getValue());
        listaCODAREA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        NatsubnombramientoControladorEnum.CODAREA.getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        noIdCargo = (String) registro.getCampos().put(
                        NatsubnombramientoControladorEnum.NO_ID_DE_CARGO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        NatsubnombramientoControladorEnum.ID_DE_CARGO
                                                        .getValue()));

        registro.getCampos().put(
                        NatsubnombramientoControladorEnum.NOMBRECARGO
                                        .getValue(),
                        SysmanFunciones
                                        .nvl(registroAux.getCampos().get(
                                                        NatsubnombramientoControladorEnum.NOMBRE_DEL_CARGO
                                                                        .getValue()),
                                                        "")
                                        .toString());
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCATEGORIA
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCATEGORIA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        NatsubnombramientoControladorEnum.NO_CATEGORIA
                                        .getValue(),
                        registroAux.getCampos().get(
                                        NatsubnombramientoControladorEnum.ID_DE_CATEGORIA
                                                        .getValue()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCODAREA
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCODAREA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        NatsubnombramientoControladorEnum.CODAREA.getValue(),
                        registroAux.getCampos().get(
                                        NatsubnombramientoControladorEnum.CODAREA
                                                        .getValue()));
        cargarListaANOFUNCION();
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);
        parametros.put("numeroDcto", codNumedocu);
        parametros.put("sucursal", codSucursal);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEscalafon() {
        registro.getCampos()
                        .put(NatsubnombramientoControladorEnum.NO_ID_DE_CARGO
                                        .getValue(), null);
        registro.getCampos().put(NatsubnombramientoControladorEnum.NOMBRECARGO
                        .getValue(), null);
        registro.getCampos().put(NatsubnombramientoControladorEnum.NO_CATEGORIA
                        .getValue(), null);
        cargarListaCargo();
        cargarListaCATEGORIA();
    }

    public void cambiarFechaEfectividad() {
        registro.getCampos().put(NatsubnombramientoControladorEnum.NO_CATEGORIA
                        .getValue(), null);
        cargarListaCATEGORIA();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton tipoactoamndtivo en la
     * vista
     *
     *
     */
    public void oprimirtipoactoamndtivo() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametrosEntrada = new HashMap<>();
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.cargarModalDatos(Integer.toString(
                        GeneralCodigoFormaEnum.TIPO_ACTO_ADTIVOS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton funciones en la vista
     *
     *
     */

    public void oprimirbtnFunciones() {
        // <CODIGO_DESARROLLADO>

        if (css != null) {

            if ((registro.getCampos().get("NO_ID_DE_CARGO") == null)
                || (registro.getCampos().get("NO_ID_DE_CARGO").equals(""))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4135"));
            }
            else {
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("rid", css);
                parametros.put("ridDatos", ridDatosPersonales);
                parametros.put("redireccion", "-1");
                parametros.put("nroDocumento", codNumedocu);
                parametros.put("sucursal", registro.getCampos()
                                .get(GeneralParameterEnum.SUCURSAL.getName()));
                parametros.put("dependencia", registro.getCampos()
                                .get("NO_DEPENUEV"));
                parametros.put("codigo", codPersona);

                parametros.put("cargo",
                                registro.getCampos().get("NO_ID_DE_CARGO"));

                Direccionador direccionador = new Direccionador();
                direccionador.setNumForm("1739");
                direccionador.setParametros(parametros);

                SessionUtil.redireccionarForma(direccionador,
                                SessionUtil.getModulo());
            }

        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4133"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirRetiros() {

        if (css != null) {
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("ridDatosPersonales", ridDatosPersonales);
            parametros.put("ridNombramiento", css);
            parametros.put(NatdatospersonalesControladorEnum.NUMEMRODCTO
                            .getValue(),
                            registro.getCampos().get("DP_NUMEDOCU"));
            parametros.put(NatdatospersonalesControladorEnum.SUCURSAL
                            .getValue(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            parametros.put(NatdatospersonalesControladorEnum.CODIGO
                            .getValue(),
                            registro.getCampos()
                                            .get("NB_CODIGOPERSONA"));

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_SUBRENUNCIAS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);

            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4133"));
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
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
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (css == null) {
            registro.getCampos().put(
                            NatsubnombramientoControladorEnum.NO_FECHAEFECTIVIDAD
                                            .getValue(),
                            new Date());
            registro.getCampos().put(
                            NatsubnombramientoControladorEnum.NO_FECHRESODECR
                                            .getValue(),
                            new Date());
            registro.getCampos().put(
                            NatsubnombramientoControladorEnum.NO_FECHACTAPOSE
                                            .getValue(),
                            new Date());

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        codSucursal);
        registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        codNumedocu);
        registro.getCampos()
                        .put(NatsubnombramientoControladorEnum.NB_CODIGOPERSONA
                                        .getValue(), codPersona);
        // </CODIGO_DESARROLLADO>

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        Date fechaActoAdtivo = (Date) registro.getCampos()
                        .get(NatsubnombramientoControladorEnum.NO_FECHRESODECR
                                        .getValue());
        Object fechaPosesion = registro.getCampos()
                        .get(NatsubnombramientoControladorEnum.NO_FECHACTAPOSE
                                        .getValue());
        Object fechaEfectividad = registro.getCampos()
                        .get(NatsubnombramientoControladorEnum.NO_FECHAEFECTIVIDAD
                                        .getValue());

        if (fechaPosesion != null
            && fechaActoAdtivo.after((Date) fechaPosesion)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3990"));
            return false;
        }
        if (fechaPosesion != null && fechaEfectividad != null
            && ((Date) fechaPosesion).after((Date) fechaEfectividad)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3991"));
            return false;
        }
        registro.getCampos()
                        .remove(NatsubnombramientoControladorEnum.NOMBRECARGO
                                        .getValue());
        registro.getCampos().remove(
                        NatsubnombramientoControladorEnum.NOMBREDEPENDENCIA
                                        .getValue());
        if (registro.getCampos().get(
                        NatsubnombramientoControladorEnum.RETIRO_O_NOMBRAMIENTO
                                        .getValue()) == null) {
            registro.getCampos().put(
                            NatsubnombramientoControladorEnum.RETIRO_O_NOMBRAMIENTO
                                            .getValue(),
                            "N");
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        cargarRegistro();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTIPO
     *
     * @return listaTIPO
     */
    public List<Registro> getListaTIPO() {
        return listaTIPO;
    }

    /**
     * Asigna la lista listaTIPO
     *
     * @param listaTIPO
     * Variable a asignar en listaTIPO
     */
    public void setListaTIPO(List<Registro> listaTIPO) {
        this.listaTIPO = listaTIPO;
    }

    /**
     * Retorna la lista listaDEPEANTE
     *
     * @return listaDEPEANTE
     */
    public RegistroDataModelImpl getListaDEPEANTE() {
        return listaDEPEANTE;
    }

    /**
     * Asigna la lista listaDEPEANTE
     *
     * @param listaDEPEANTE
     * Variable a asignar en listaDEPEANTE
     */
    public void setListaDEPEANTE(RegistroDataModelImpl listaDEPEANTE) {
        this.listaDEPEANTE = listaDEPEANTE;
    }

    /**
     * Retorna la lista listaDEPENUEV
     *
     * @return listaDEPENUEV
     */
    public RegistroDataModelImpl getListaDEPENUEV() {
        return listaDEPENUEV;
    }

    /**
     * Asigna la lista listaDEPENUEV
     *
     * @param listaDEPENUEV
     * Variable a asignar en listaDEPENUEV
     */
    public void setListaDEPENUEV(RegistroDataModelImpl listaDEPENUEV) {
        this.listaDEPENUEV = listaDEPENUEV;
    }

    /**
     * Retorna la lista listaEscalafon
     *
     * @return listaEscalafon
     */
    public List<Registro> getListaEscalafon() {
        return listaEscalafon;
    }

    /**
     * Asigna la lista listaEscalafon
     *
     * @param listaEscalafon
     * Variable a asignar en listaEscalafon
     */
    public void setListaEscalafon(List<Registro> listaEscalafon) {
        this.listaEscalafon = listaEscalafon;
    }

    /**
     * Retorna la lista listaTIPODOCUACTO
     *
     * @return listaTIPODOCUACTO
     */
    public List<Registro> getListaTIPODOCUACTO() {
        return listaTIPODOCUACTO;
    }

    /**
     * Asigna la lista listaTIPODOCUACTO
     *
     * @param listaTIPODOCUACTO
     * Variable a asignar en listaTIPODOCUACTO
     */
    public void setListaTIPODOCUACTO(List<Registro> listaTIPODOCUACTO) {
        this.listaTIPODOCUACTO = listaTIPODOCUACTO;
    }

    /**
     * Retorna la lista listaANOFUNCION
     *
     * @return listaANOFUNCION
     */
    public List<Registro> getListaANOFUNCION() {
        return listaANOFUNCION;
    }

    /**
     * Asigna la lista listaANOFUNCION
     *
     * @param listaANOFUNCION
     * Variable a asignar en listaANOFUNCION
     */
    public void setListaANOFUNCION(List<Registro> listaANOFUNCION) {
        this.listaANOFUNCION = listaANOFUNCION;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCargo
     *
     * @return listaCargo
     */
    public RegistroDataModelImpl getListaCargo() {
        return listaCargo;
    }

    /**
     * Asigna la lista listaCargo
     *
     * @param listaCargo
     * Variable a asignar en listaCargo
     */
    public void setListaCargo(RegistroDataModelImpl listaCargo) {
        this.listaCargo = listaCargo;
    }

    /**
     * Retorna la lista listaCATEGORIA
     *
     * @return listaCATEGORIA
     */
    public RegistroDataModelImpl getListaCATEGORIA() {
        return listaCATEGORIA;
    }

    /**
     * Asigna la lista listaCATEGORIA
     *
     * @param listaCATEGORIA
     * Variable a asignar en listaCATEGORIA
     */
    public void setListaCATEGORIA(RegistroDataModelImpl listaCATEGORIA) {
        this.listaCATEGORIA = listaCATEGORIA;
    }

    /**
     * Retorna la lista listaCODAREA
     *
     * @return listaCODAREA
     */
    public RegistroDataModelImpl getListaCODAREA() {
        return listaCODAREA;
    }

    /**
     * Asigna la lista listaCODAREA
     *
     * @param listaCODAREA
     * Variable a asignar en listaCODAREA
     */
    public void setListaCODAREA(RegistroDataModelImpl listaCODAREA) {
        this.listaCODAREA = listaCODAREA;
    }

    public String getCodSucursal() {
        return codSucursal;
    }

    public void setCodSucursal(String codSucursal) {
        this.codSucursal = codSucursal;
    }

    public String getCodNumedocu() {
        return codNumedocu;
    }

    public void setCodNumedocu(String codNumedocu) {
        this.codNumedocu = codNumedocu;

    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getCodPersona() {
        return codPersona;
    }

    public void setCodPersona(String codPersona) {
        this.codPersona = codPersona;
    }

    public String getNoIdCargo() {
        return noIdCargo;
    }

    public void setNoIdCargo(String noIdCargo) {
        this.noIdCargo = noIdCargo;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
