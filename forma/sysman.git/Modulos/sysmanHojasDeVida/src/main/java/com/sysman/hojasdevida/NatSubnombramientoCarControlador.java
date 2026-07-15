/*-
 * NatSubnombramientoCarControlador.java
 *
 * 1.0
 * 
 * 5/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.NatSubnombramientoCarControladorEnum;
import com.sysman.hojasdevida.enums.NatSubnombramientoCarControladorUrlEnum;
import com.sysman.hojasdevida.enums.NatsubnombramientoControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Formulario que administra el nombramiento en periodo de prueba
 *
 * @version 1.0, 05/02/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class NatSubnombramientoCarControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el numero de modulo al
     * cual el usuario ingreso
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Numero de documento del empleado que vienen por parametro
     */
    private String numeroDcto;

    /**
     * Numero de sucursal del empleado que vienen por parametro
     */
    private String sucursal;
    /**
     * Atributo que identifica desde que boton se accedio al
     * formulario
     */

    private String opcionBoton;

    /**
     * Titulo que se asigna al formulario dependiendo de que boton se
     * ingreso
     */
    private String tituloFormulario;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los tipos de nombramiento
     */
    private RegistroDataModelImpl listaTipoNombramiento;
    /**
     * Lista que carga las actas adminsitrativas
     */
    private RegistroDataModelImpl listaNoTipodocuacto;
    /**
     * Lista que carga los cargos de la compania
     */
    private RegistroDataModelImpl listaCargoAnterior;
    /**
     * Lista que carga los cargos de la compania
     */
    private RegistroDataModelImpl listaCargoNuevo;
    /**
     * Lista que carga las dependencias de la compania
     */
    private RegistroDataModelImpl listaDependenciaAnterior;
    /**
     * Lista que carga las dependencias de la compania
     */
    private RegistroDataModelImpl listaDependenciaNueva;

    private Map<String, Object> parametrosEntrada;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de NatSubnombramientoCarControlador
     */
    public NatSubnombramientoCarControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.NAT_SUBNOMBRAMIENTO_CAR_CONTROLADOR
                            .getCodigo();
            parametrosEntrada = SessionUtil.getFlash();
            registro = new Registro(new HashMap<String, Object>());
            if (parametrosEntrada != null) {

                numeroDcto = parametrosEntrada.get("numeroDcto")
                                .toString();

                sucursal = parametrosEntrada.get("sucursal")
                                .toString();

                opcionBoton = parametrosEntrada.get("opcionBoton")
                                .toString();

                if ("1".equals(opcionBoton)) {
                    tituloFormulario = idioma.getString("TB_TB3974");
                }
                else {
                    tituloFormulario = idioma.getString("TB_TB3975");
                }

            }
            validarPermisos();
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
        cargarListaTipoNombramiento();
        cargarListaNoTipodocuacto();
        cargarListaCargoAnterior();
        cargarListaCargoNuevo();
        cargarListaDependenciaAnterior();
        cargarListaDependenciaNueva();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
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
        tabla = "NAT_NOMBRAMIENTO";
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
        iniciarListas();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put("KEY_COMPANIA",
                        compania);

        parametrosListado.put("KEY_DP_NUMEDOCU",
                        numeroDcto);

        parametrosListado.put("KEY_SUCURSAL",
                        sucursal);

        if ("1".equals(opcionBoton)) {

            urlLectura = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            NatSubnombramientoCarControladorUrlEnum.URL9999
                                                            .getValue());
        }
        else {
            urlLectura = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            NatSubnombramientoCarControladorUrlEnum.URL8888
                                                            .getValue());

        }

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubnombramientoCarControladorUrlEnum.URL2525
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubnombramientoCarControladorUrlEnum.URL4242
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubnombramientoCarControladorUrlEnum.URL6464
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoNombramiento
     *
     */
    public void cargarListaTipoNombramiento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubnombramientoCarControladorUrlEnum.URL9454
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(NatSubnombramientoCarControladorEnum.TIPO.getValue(), "I");

        listaTipoNombramiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "IDFORMA");
    }

    /**
     * 
     * Carga la lista listaNoTipodocuacto
     *
     */
    public void cargarListaNoTipodocuacto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubnombramientoCarControladorUrlEnum.URL10414
                                                        .getValue());

        listaNoTipodocuacto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCargoAnterior
     *
     */
    public void cargarListaCargoAnterior() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubnombramientoCarControladorUrlEnum.URL10891
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCargoAnterior = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NatSubnombramientoCarControladorEnum.ID_DE_CARGO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaCargoNuevo
     *
     */
    public void cargarListaCargoNuevo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubnombramientoCarControladorUrlEnum.URL11432
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCargoNuevo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NatSubnombramientoCarControladorEnum.ID_DE_CARGO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaDependenciaAnterior
     *
     */
    public void cargarListaDependenciaAnterior() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubnombramientoCarControladorUrlEnum.URL11988
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependenciaAnterior = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaDependenciaNueva
     *
     */
    public void cargarListaDependenciaNueva() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NatSubnombramientoCarControladorUrlEnum.URL12527
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependenciaNueva = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoNombramiento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoNombramiento(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        NatsubnombramientoControladorEnum.NO_TIPO.getValue(),
                        registroAux.getCampos().get("NOMBREFORMA"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNoTipodocuacto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNoTipodocuacto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NO_TIPODOCUACTO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCargoAnterior
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargoAnterior(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NO_CARGOA",
                        registroAux.getCampos()
                                        .get(NatSubnombramientoCarControladorEnum.ID_DE_CARGO
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCargoNuevo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargoNuevo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NO_CARGON",
                        registroAux.getCampos()
                                        .get(NatSubnombramientoCarControladorEnum.ID_DE_CARGO
                                                        .getValue()));

        registro.getCampos().put("NO_CODIGOCARGO",
                        registroAux.getCampos()
                                        .get("GRADO"));

        registro.getCampos().put("NO_GRADOCARGO",
                        registroAux.getCampos()
                                        .get("CODIGOCARGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependenciaAnterior
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaAnterior(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NO_DEPEANTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependenciaNueva
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaNueva(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NO_DEPENUEV",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
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
        if (existeRegistro()) {
            cargarRegistro(parametrosListado, ACCION_MODIFICAR);
        }

    }

    private boolean existeRegistro() {
        boolean rta = true;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.NUMERO_DCTO.getName(), numeroDcto);

        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);

        param.put("CONDICION", opcionBoton);

        Registro reg;
        try {

            if ("1".equals(opcionBoton)) {

                reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                NatSubnombramientoCarControladorUrlEnum.URL5757
                                                                                .getValue())
                                                .getUrl(), param));
            }
            else {
                reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                NatSubnombramientoCarControladorUrlEnum.URL5858
                                                                                .getValue())
                                                .getUrl(), param));
            }

            if (reg == null) {

                rta = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return rta;

    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
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
                        sucursal);
        registro.getCampos().put("DP_NUMEDOCU",
                        numeroDcto);

        registro.getCampos().put("NO_CAUSA", "INSCRIPCION");
        if ("1".equals(opcionBoton)) {
            registro.getCampos().put(NatsubnombramientoControladorEnum.NO_TIPO
                            .getValue(), "PP");
        }

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
     * 
     */
    @Override
    public boolean actualizarAntes() {

        if (!SysmanFunciones.validarVariableVacio(accion)
            && accion.equals(ACCION_MODIFICAR)) {

            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());

            registro.getCampos()
                            .remove("CONDICION");

            registro.getCampos()
                            .remove(GeneralParameterEnum.NUMERO_DCTO.getName());

            registro.getLlave().put("KEY_NO_NUME",
                            registro.getCampos().get("NO_NUME"));
            registro.getLlave().put("KEY_NO_FECHRESODECR",
                            registro.getCampos().get("NO_FECHRESODECR"));

            if ("1".equals(opcionBoton)) {
                registro.getCampos()
                                .put(NatsubnombramientoControladorEnum.NO_TIPO
                                                .getValue(), "PP");
            }
            else {
                registro.getCampos()
                                .put(NatsubnombramientoControladorEnum.NO_TIPO
                                                .getValue(), null);
            }

        }

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        if (css != null) {
            cargarRegistro(registro.getLlave(), accion, registro.getIndice());
        }
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

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void ejecutarrcCerrar() {

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_INSCRIPCIONES_CARRERAS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoNombramiento
     * 
     * @return listaTipoNombramiento
     */
    public RegistroDataModelImpl getListaTipoNombramiento() {
        return listaTipoNombramiento;
    }

    /**
     * Asigna la lista listaTipoNombramiento
     * 
     * @param listaTipoNombramiento
     * Variable a asignar en listaTipoNombramiento
     */
    public void setListaTipoNombramiento(
        RegistroDataModelImpl listaTipoNombramiento) {
        this.listaTipoNombramiento = listaTipoNombramiento;
    }

    /**
     * Retorna la lista listaNoTipodocuacto
     * 
     * @return listaNoTipodocuacto
     */
    public RegistroDataModelImpl getListaNoTipodocuacto() {
        return listaNoTipodocuacto;
    }

    /**
     * Asigna la lista listaNoTipodocuacto
     * 
     * @param listaNoTipodocuacto
     * Variable a asignar en listaNoTipodocuacto
     */
    public void setListaNoTipodocuacto(
        RegistroDataModelImpl listaNoTipodocuacto) {
        this.listaNoTipodocuacto = listaNoTipodocuacto;
    }

    /**
     * Retorna la lista listaCargoAnterior
     * 
     * @return listaCargoAnterior
     */
    public RegistroDataModelImpl getListaCargoAnterior() {
        return listaCargoAnterior;
    }

    /**
     * Asigna la lista listaCargoAnterior
     * 
     * @param listaCargoAnterior
     * Variable a asignar en listaCargoAnterior
     */
    public void setListaCargoAnterior(
        RegistroDataModelImpl listaCargoAnterior) {
        this.listaCargoAnterior = listaCargoAnterior;
    }

    /**
     * Retorna la lista listaCargoNuevo
     * 
     * @return listaCargoNuevo
     */
    public RegistroDataModelImpl getListaCargoNuevo() {
        return listaCargoNuevo;
    }

    /**
     * Asigna la lista listaCargoNuevo
     * 
     * @param listaCargoNuevo
     * Variable a asignar en listaCargoNuevo
     */
    public void setListaCargoNuevo(RegistroDataModelImpl listaCargoNuevo) {
        this.listaCargoNuevo = listaCargoNuevo;
    }

    /**
     * Retorna la lista listaDependenciaAnterior
     * 
     * @return listaDependenciaAnterior
     */
    public RegistroDataModelImpl getListaDependenciaAnterior() {
        return listaDependenciaAnterior;
    }

    /**
     * Asigna la lista listaDependenciaAnterior
     * 
     * @param listaDependenciaAnterior
     * Variable a asignar en listaDependenciaAnterior
     */
    public void setListaDependenciaAnterior(
        RegistroDataModelImpl listaDependenciaAnterior) {
        this.listaDependenciaAnterior = listaDependenciaAnterior;
    }

    /**
     * Retorna la lista listaDependenciaNueva
     * 
     * @return listaDependenciaNueva
     */
    public RegistroDataModelImpl getListaDependenciaNueva() {
        return listaDependenciaNueva;
    }

    /**
     * Asigna la lista listaDependenciaNueva
     * 
     * @param listaDependenciaNueva
     * Variable a asignar en listaDependenciaNueva
     */
    public void setListaDependenciaNueva(
        RegistroDataModelImpl listaDependenciaNueva) {
        this.listaDependenciaNueva = listaDependenciaNueva;
    }

    public String getTituloFormulario() {
        return tituloFormulario;
    }

    public void setTituloFormulario(String tituloFormulario) {
        this.tituloFormulario = tituloFormulario;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
