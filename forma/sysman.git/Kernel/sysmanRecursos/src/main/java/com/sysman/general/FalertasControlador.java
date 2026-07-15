/*-
 * FalertasControlador.java
 *
 * 1.0
 * 
 * 08/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FalertasControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Fomulario continuo que permite la generacion de alertas
 *
 * @version 1.0, 08/05/2018
 * @author jromero
 */
@ManagedBean
@ViewScoped
public class FalertasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private String codigo;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que almacena el modulo desde la vista
     */
    private List<Registro> listaModulo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almacena la dependencia desde la vista
     */
    private RegistroDataModelImpl listaDependencia;
    /**
     * Lista que almacena la dependencia desde la vista
     */
    private RegistroDataModelImpl listaDependenciaE;
    /**
     * Lista que almacena el cargo desde la vista
     */
    private RegistroDataModelImpl listaCargo;
    /**
     * Lista que almacena el cargo desde la vista
     */
    private RegistroDataModelImpl listaCargoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FalertasControlador
     */
    public FalertasControlador() {
        super();
        compania = SessionUtil.getCompania();
        urlConexionCache = UrlServiceCache.SYSMANIRISST;
        try {
            numFormulario = GeneralCodigoFormaEnum.FALERTAS_CONTROLADOR
                            .getCodigo();
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ALERTAS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaModulo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCargo();
        cargarListaCargoE();
        cargarListaDependencia();
        cargarListaDependenciaE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigo);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaModulo
     *
     */
    public void cargarListaModulo() {

        try {
            listaModulo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FalertasControladorUrlEnum.URL8883
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
     * Carga la lista listaCargo
     *
     */
    public void cargarListaCargo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FalertasControladorUrlEnum.URL5505
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCargo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_CARGO");
    }

    /**
     * 
     * Carga la lista listaCargo
     *
     */
    public void cargarListaCargoE() {

        listaCargoE = listaCargo;
    }

    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FalertasControladorUrlEnum.URL5605
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    /**
     * 
     * Carga la listaDependencia
     *
     */
    public void cargarListaDependenciaE() {

        listaDependenciaE = listaDependencia;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registroAux.getCampos()
                                        .get("CODIGO"));

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos()
                                        .get("NOMBRE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = registroAux.getCampos().get("CODIGO").toString();

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos()
                                        .get("NOMBRE").toString());
    }

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

        registro.getCampos().put(GeneralParameterEnum.CARGO.getName(),
                        registroAux.getCampos()
                                        .get("ID_DE_CARGO"));

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos()
                                        .get("NOMBRE_DEL_CARGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCargo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCargoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = registroAux.getCampos().get("ID_DE_CARGO").toString();

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos()
                                        .get("NOMBRE_DEL_CARGO").toString());
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1784-AL_ABRIR Private Sub Form_Open(Cancel As Integer) DoCmd.Maximize End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("NOMBRE");

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.COMPANIA.getName()));

        param.put(GeneralParameterEnum.CODIGO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

        try {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FalertasControladorUrlEnum.URL9893
                                                                            .getValue())
                                            .getUrl(), param));
            if (Integer.parseInt(reg.getCampos().get("PADRE").toString()) > 0) {
                JsfUtil.agregarMensajeAlertaDialogo(
                                idioma.getString("TB_TB4104"));
                return false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     * 
     * 
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
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaModulo
     * 
     * @return listaModulo
     */
    public List<Registro> getListaModulo() {
        return listaModulo;
    }

    /**
     * Asigna la lista listaModulo
     * 
     * @param listaModulo
     * Variable a asignar en listaModulo
     */
    public void setListaModulo(List<Registro> listaModulo) {
        this.listaModulo = listaModulo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCargo
     * 
     * @return listaCargo
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaCargo
     * 
     * @param listaCargo
     * Variable a asignar en listaCargo
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaCargo
     * 
     * @return listaCargo
     */
    public RegistroDataModelImpl getListaDependenciaE() {
        return listaDependenciaE;
    }

    /**
     * Asigna la lista listaCargo
     * 
     * @param listaCargo
     * Variable a asignar en listaCargo
     */
    public void setListaDependenciaE(RegistroDataModelImpl listaDependenciaE) {
        this.listaDependenciaE = listaDependenciaE;
    }

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
     * Retorna la lista listaCargo
     * 
     * @return listaCargo
     */
    public RegistroDataModelImpl getListaCargoE() {
        return listaCargoE;
    }

    /**
     * Asigna la lista listaCargo
     * 
     * @param listaCargo
     * Variable a asignar en listaCargo
     */
    public void setListaCargoE(RegistroDataModelImpl listaCargoE) {
        this.listaCargoE = listaCargoE;
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

    public String getCompania() {
        return compania;
    }

}
