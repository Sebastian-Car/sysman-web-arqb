/*-
 * FrmtiporecursosControlador.java
 *
 * 1.0
 * 
 * 12/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmtiporecursosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de gestionar todos los tipos de recursos en un
 * proyecto
 *
 * @version 1.0, 12/03/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class FrmtiporecursosControlador extends BeanBaseContinuoAcmeImpl {
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
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable encargada de almacenar los datos de los tipoSireciInf
     */
    private RegistroDataModelImpl listatipoSireciInf;
    /**
     * Variable encargada de almacenar los datos de los tipoSireciInf
     */
    private RegistroDataModelImpl listatipoSireciInfE;
    /**
     * Variable encargada de almacenar los datos de los
     * tipoSireciInfApsb
     */
    private RegistroDataModelImpl listatipoSireciApsb;
    /**
     * Variable encargada de almacenar los datos de los
     * tipoSireciInfApsb
     */
    private RegistroDataModelImpl listatipoSireciApsbE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * variable encargada de almacenar temporlamente el nombre del
     * tipoInf
     */
    private String nombreTipoInf;
    /**
     * variable encargada de almacenar temporlamente el nombre del
     * tipoInf
     */
    private String nombreTipoApsb;
    /**
     * variable encargada de almacenar temporlamente el nombre del
     * tipoAPSB
     */
    private final String nomtipoInfCons;
    /**
     * variable encargada de almacenar temporlamente el nombre del
     * tipoAPSB
     */
    private final String nomApsbCons;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmtiporecursosControlador
     */
    public FrmtiporecursosControlador() {
        super();
        compania = SessionUtil.getCompania();

        nomtipoInfCons = "NOM_TIPO_INF";
        nomApsbCons = "NOM_APSB";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMTIPORECURSOS_CONTROLADOR
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

        enumBase = GenericUrlEnum.BP_TIPORECURSOS;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListatipoSireciInf();
        cargarListatipoSireciInfE();
        cargarListatipoSireciApsb();
        cargarListatipoSireciApsbE();
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
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listatipoSireciInf
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar los datos de respuesta en la lista listatipoSireciInf
     */
    public void cargarListatipoSireciInf() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtiporecursosControladorUrlEnum.URL3222
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoSireciInf = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listatipoSireciInf Metodo encargado de hacer el
     * llamado a la base de datos y almacenar los datos de respuesta
     * en la lista listatipoSireciInfE
     */
    public void cargarListatipoSireciInfE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtiporecursosControladorUrlEnum.URL3222
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoSireciInfE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listatipoSireciApsb
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar los datos de respuesta en la lista
     * listatipoSireciApsb
     */
    public void cargarListatipoSireciApsb() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtiporecursosControladorUrlEnum.URL17434
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoSireciApsb = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listatipoSireciApsb
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar los datos de respuesta en la lista
     * listatipoSireciApsbE
     */
    public void cargarListatipoSireciApsbE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmtiporecursosControladorUrlEnum.URL17434
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoSireciApsbE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiartipoSireciInfC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("TIPO_SIRECI_INF", auxiliar);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(nomtipoInfCons, nombreTipoInf);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control tipoSireciApsb en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartipoSireciApsbC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("TIPO_SIRECI_APSB", auxiliar);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(nomApsbCons, nombreTipoApsb);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoSireciInf
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoSireciInf(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_SIRECI_INF",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(nomtipoInfCons, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoSireciInf
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoSireciInfE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        nombreTipoInf = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoSireciApsb
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoSireciApsb(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_SIRECI_APSB",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(nomApsbCons, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoSireciApsb
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoSireciApsbE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());

        nombreTipoApsb = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
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
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().remove(nomtipoInfCons);
        registro.getCampos().remove(nomApsbCons);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(nomtipoInfCons);
        registro.getCampos().remove(nomApsbCons);

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listatipoSireciInf
     * 
     * @return listatipoSireciInf
     */
    public RegistroDataModelImpl getListatipoSireciInf() {
        return listatipoSireciInf;
    }

    /**
     * Asigna la lista listatipoSireciInf
     * 
     * @param listatipoSireciInf
     * Variable a asignar en listatipoSireciInf
     */
    public void setListatipoSireciInf(
        RegistroDataModelImpl listatipoSireciInf) {
        this.listatipoSireciInf = listatipoSireciInf;
    }

    /**
     * Retorna la lista listatipoSireciInf
     * 
     * @return listatipoSireciInf
     */
    public RegistroDataModelImpl getListatipoSireciInfE() {
        return listatipoSireciInfE;
    }

    /**
     * Asigna la lista listatipoSireciInf
     * 
     * @param listatipoSireciInf
     * Variable a asignar en listatipoSireciInf
     */
    public void setListatipoSireciInfE(
        RegistroDataModelImpl listatipoSireciInfE) {
        this.listatipoSireciInfE = listatipoSireciInfE;
    }

    /**
     * Retorna la lista listatipoSireciApsb
     * 
     * @return listatipoSireciApsb
     */
    public RegistroDataModelImpl getListatipoSireciApsb() {
        return listatipoSireciApsb;
    }

    /**
     * Asigna la lista listatipoSireciApsb
     * 
     * @param listatipoSireciApsb
     * Variable a asignar en listatipoSireciApsb
     */
    public void setListatipoSireciApsb(
        RegistroDataModelImpl listatipoSireciApsb) {
        this.listatipoSireciApsb = listatipoSireciApsb;
    }

    /**
     * Retorna la lista listatipoSireciApsb
     * 
     * @return listatipoSireciApsb
     */
    public RegistroDataModelImpl getListatipoSireciApsbE() {
        return listatipoSireciApsbE;
    }

    /**
     * Asigna la lista listatipoSireciApsb
     * 
     * @param listatipoSireciApsb
     * Variable a asignar en listatipoSireciApsb
     */
    public void setListatipoSireciApsbE(
        RegistroDataModelImpl listatipoSireciApsbE) {
        this.listatipoSireciApsbE = listatipoSireciApsbE;
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

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

}
