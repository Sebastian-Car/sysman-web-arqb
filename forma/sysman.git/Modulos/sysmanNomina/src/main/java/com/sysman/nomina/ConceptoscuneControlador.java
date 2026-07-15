/*-
 * ConceptoscuneControlador.java
 *
 * 1.0
 * 
 * 13/09/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ConceptosCuneControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador de conceptos Cune
 *
 * @version 1.0, 13/09/2021
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class ConceptoscuneControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private RegistroDataModelImpl listaCbCodCune;
    /**
     */
    private RegistroDataModelImpl listaCbCodCuneE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConceptoscuneControlador
     */
    public ConceptoscuneControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONCEPTOS_CUNE.getCodigo();
            validarPermisos();
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

        tabla = GenericUrlEnum.CONCEPTOS.getTable();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        cargarListaCbCodCune();
        cargarListaCbCodCuneE();
        abrirFormulario();

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

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosCuneControladorUrlEnum.URL0001
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosCuneControladorUrlEnum.URL0002
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCbCodCune
     *
     */
    public void cargarListaCbCodCune() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosCuneControladorUrlEnum.URL0003
                                                        .getValue());

        listaCbCodCune = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCbCodCune
     *
     */
    public void cargarListaCbCodCuneE() {
        // int clase =
        // Integer.parseInt(registro.getCampos().get("CLASECONCEPTO").toString());
        listaCbCodCuneE = listaCbCodCune;
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
     * listaCbCodCune
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCodCune(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_CUNE",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCbCodCune
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbCodCuneE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("CLASECONCEPTO");
        registro.getCampos().remove("ID_DE_CONCEPTO");
        registro.getCampos().remove("NOMBRE_CONCEPTO");
        registro.getCampos().remove("CLASE");
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // N.A
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // n.A
    }

    /**
     * Retorna la lista listaCbCodCune
     * 
     * @return listaCbCodCune
     */
    public RegistroDataModelImpl getListaCbCodCune() {
        return listaCbCodCune;
    }

    /**
     * Asigna la lista listaCbCodCune
     * 
     * @param listaCbCodCune
     * Variable a asignar en listaCbCodCune
     */
    public void setListaCbCodCune(RegistroDataModelImpl listaCbCodCune) {
        this.listaCbCodCune = listaCbCodCune;
    }

    /**
     * Retorna la lista listaCbCodCune
     * 
     * @return listaCbCodCune
     */
    public RegistroDataModelImpl getListaCbCodCuneE() {
        return listaCbCodCuneE;
    }

    /**
     * Asigna la lista listaCbCodCune
     * 
     * @param listaCbCodCune
     * Variable a asignar en listaCbCodCune
     */
    public void setListaCbCodCuneE(RegistroDataModelImpl listaCbCodCuneE) {
        this.listaCbCodCuneE = listaCbCodCuneE;
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
