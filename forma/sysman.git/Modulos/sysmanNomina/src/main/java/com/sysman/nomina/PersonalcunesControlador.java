/*-
 * PersonalcunesControlador.java
 *
 * 1.0
 * 
 * 02/09/2021
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.PersonalcunesControladorEnum;
import com.sysman.nomina.enums.PersonalcunesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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
 * Controlador para la configuraci&oacute;n de personal en
 * n&oacute;mina electronica
 *
 * @version 1.0, 02/09/2021
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class PersonalcunesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private List<Registro> listaCbTipoVinculacion;
    /**
     * Combo medio de pagos
     */
    private RegistroDataModelImpl listaCbMedioPago;

    private RegistroDataModelImpl listaCbMedioPagoE;

    private String auxiliar;
    /**
     * Combo bancos
     */
    private RegistroDataModelImpl listaCbBanco;
    private RegistroDataModelImpl listaCbBancoE;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PersonalcunesControlador
     */
    public PersonalcunesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PERSONAL_CUNE.getCodigo();
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

        tabla = GenericUrlEnum.PERSONAL.getTable();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaCbTipoVinculacion();
        cargarListaCbMedioPago();
        cargarListaCbMedioPagoE();
        cargarListaCbBanco();
        cargarListaCbBancoE();
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalcunesControladorUrlEnum.URL151
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalcunesControladorUrlEnum.URL210PUT
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCbTipoVinculacion
     *
     */
    public void cargarListaCbTipoVinculacion() {
        Map<String, Object> param = new TreeMap<>();
        try {
            listaCbTipoVinculacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PersonalcunesControladorUrlEnum.URL634
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCbMedioPago
     *
     */
    public void cargarListaCbMedioPago() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalcunesControladorUrlEnum.URL1744
                                                        .getValue());
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCbMedioPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCbMedioPago
     *
     */
    public void cargarListaCbMedioPagoE() {
        listaCbMedioPagoE = listaCbMedioPago;
    }

    /**
     * 
     * Carga la lista listaCbBanco
     *
     */
    public void cargarListaCbBanco() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalcunesControladorUrlEnum.URL459
                                                        .getValue());
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCbBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.BANCO.getName());
    }

    /**
     * 
     * Carga la lista listaCbBanco
     *
     */
    public void cargarListaCbBancoE() {
        listaCbBancoE = listaCbBanco;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(
                        PersonalcunesControladorEnum.IDEMPLEADO.getValue());
        registro.getCampos().remove(
                        PersonalcunesControladorEnum.TIPOVINCULACIONNIE
                                        .getValue());

        registro.getCampos().remove(
                        PersonalcunesControladorEnum.TCONTRATO_NIE061
                                        .getValue());
        
        registro.getCampos().remove(
        		PersonalcunesControladorEnum.STIPO_NIE042
        			.getValue());

        registro.getCampos().remove(
                        PersonalcunesControladorEnum.BANCO.getValue());

        registro.getCampos().remove(
                        PersonalcunesControladorEnum.CUENTA.getValue());

        registro.getCampos().remove(
                        PersonalcunesControladorEnum.TIPOCUENTA.getValue());

        registro.getCampos().remove(
                        PersonalcunesControladorEnum.MEDIOPAGONIE.getValue());

        return true;
    }

    public void seleccionarFilaCbMedioPago(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("MEDIODEPAGO_NIE065",
                        registroAux.getCampos().get("CODIGO"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCbMedioPago
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbMedioPagoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
    }

    public void seleccionarFilaCbBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(PersonalcunesControladorEnum.BANCO.getValue(),
                        registroAux.getCampos()
                                        .get(PersonalcunesControladorEnum.BANCO
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCbBanco
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbBancoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(PersonalcunesControladorEnum.BANCO.getValue())
                        .toString();
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return reg actualizado
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
     * 
     * @return reg eliminado
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
        // N.A
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCbTipoVinculacion
     * 
     * @return listaCbTipoVinculacion
     */
    public List<Registro> getListaCbTipoVinculacion() {
        return listaCbTipoVinculacion;
    }

    /**
     * Asigna la lista listaCbTipoVinculacion
     * 
     * @param listaCbTipoVinculacion
     * Variable a asignar en listaCbTipoVinculacion
     */
    public void setListaCbTipoVinculacion(
        List<Registro> listaCbTipoVinculacion) {
        this.listaCbTipoVinculacion = listaCbTipoVinculacion;
    }

    /**
     * Retorna la lista listaCbMedioPago
     * 
     * @return listaCbMedioPago
     */
    public RegistroDataModelImpl getListaCbMedioPago() {
        return listaCbMedioPago;
    }

    /**
     * Asigna la lista listaCbMedioPago
     * 
     * @param listaCbMedioPago
     * Variable a asignar en listaCbMedioPago
     */
    public void setListaCbMedioPago(RegistroDataModelImpl listaCbMedioPago) {
        this.listaCbMedioPago = listaCbMedioPago;
    }

    /**
     * Retorna la lista listaCbBanco
     * 
     * @return listaCbBanco
     */
    public RegistroDataModelImpl getListaCbBanco() {
        return listaCbBanco;
    }

    public RegistroDataModelImpl getListaCbBancoE() {
        return listaCbBancoE;
    }

    public void setListaCbBancoE(RegistroDataModelImpl listaCbBancoE) {
        this.listaCbBancoE = listaCbBancoE;
    }

    public RegistroDataModelImpl getListaCbMedioPagoE() {
        return listaCbMedioPagoE;
    }

    public void setListaCbMedioPagoE(RegistroDataModelImpl listaCbMedioPagoE) {
        this.listaCbMedioPagoE = listaCbMedioPagoE;
    }

    /**
     * Asigna la lista listaCbBanco
     * 
     * @param listaCbBanco
     * Variable a asignar en listaCbBanco
     */
    public void setListaCbBanco(RegistroDataModelImpl listaCbBanco) {
        this.listaCbBanco = listaCbBanco;
    }

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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
