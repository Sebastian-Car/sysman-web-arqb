/*-
 * ClasificardocumentosControlador.java
 *
 * 1.0
 *
 * 16/01/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contratos.enums.ClasificardocumentosControladorUrlEnum;
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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para clasificar documentos
 *
 * @version 1.0, 16/01/2017
 * @author ybecerra
 * 
 * @author eamaya
 * @version 2.0, 03/08/2017, Proceso de Refactoring y cambio de numero
 * del formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class ClasificardocumentosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de Registros de Descripcion
     */
    private List<Registro> listaDescripcion;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de Registros de los Tipos de Contratos
     */
    private RegistroDataModelImpl listatipoContrato;
    /**
     * Lista de Registros de los Tipos de Contratos
     */
    private RegistroDataModelImpl listatipoContratoE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */

    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ClasificardocumentosControlador
     */
    public ClasificardocumentosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CLASIFICARDOCUMENTOS_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        tabla = GenericUrlEnum.CLASIFICA_DOCUMENTOS.getTable();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaDescripcion();
        cargarListatipoContrato();
        cargarListatipoContratoE();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ClasificardocumentosControladorUrlEnum.URL4545
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ClasificardocumentosControladorUrlEnum.URL001
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ClasificardocumentosControladorUrlEnum.URL002
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ClasificardocumentosControladorUrlEnum.URL003
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaDescripcion
     *
     */
    public void cargarListaDescripcion() {

        try {
            listaDescripcion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClasificardocumentosControladorUrlEnum.URL4671
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
     * Carga la lista listatipoContrato
     *
     */
    public void cargarListatipoContrato() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ClasificardocumentosControladorUrlEnum.URL5258
                                                        .getValue());

        listatipoContrato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listatipoContrato
     *
     */
    public void cargarListatipoContratoE() {
        listatipoContratoE = listatipoContrato;

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoContrato
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoContrato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CLASEORDEN",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoContrato
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoContratoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
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
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
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
     *
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBREDOCUMENTO");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
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
     *
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        getListaInicial().load();
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
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
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaDescripcion
     *
     * @return listaDescripcion
     */
    public List<Registro> getListaDescripcion() {
        return listaDescripcion;
    }

    /**
     * Asigna la lista listaDescripcion
     *
     * @param listaDescripcion
     * Variable a asignar en listaDescripcion
     */
    public void setListaDescripcion(List<Registro> listaDescripcion) {
        this.listaDescripcion = listaDescripcion;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listatipoContrato
     * 
     * @return listatipoContrato
     */
    public RegistroDataModelImpl getListatipoContrato() {
        return listatipoContrato;
    }

    /**
     * Asigna la lista listatipoContrato
     * 
     * @param listatipoContrato
     * Variable a asignar en listatipoContrato
     */
    public void setListatipoContrato(RegistroDataModelImpl listatipoContrato) {
        this.listatipoContrato = listatipoContrato;
    }

    /**
     * Retorna la lista listatipoContrato
     * 
     * @return listatipoContrato
     */
    public RegistroDataModelImpl getListatipoContratoE() {
        return listatipoContratoE;
    }

    /**
     * Asigna la lista listatipoContrato
     * 
     * @param listatipoContrato
     * Variable a asignar en listatipoContrato
     */
    public void setListatipoContratoE(
        RegistroDataModelImpl listatipoContratoE) {
        this.listatipoContratoE = listatipoContratoE;
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
