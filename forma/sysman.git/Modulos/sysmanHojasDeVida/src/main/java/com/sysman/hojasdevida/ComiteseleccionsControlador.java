/*-
 * ComiteseleccionsControlador.java
 *
 * 1.0
 * 
 * 29/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.hojasdevida.enums.ComiteseleccionsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de gestionar (CRUD) los jurados de la convocatoria
 *
 * @version 1.0, 29/01/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class ComiteseleccionsControlador extends BeanBaseContinuoAcmeImpl {
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
     * Lista encargada de almacenar temporalmente lo seleccionado en
     * el combo Nit en la intefaz grafica
     */
    private RegistroDataModelImpl listadocumento;
    /**
     * Lista encargada de almacenar temporalmente lo seleccionado en
     * el combo Nit en la intefaz grafica en la grilla
     */
    private RegistroDataModelImpl listadocumentoE;
    /**
     * Variable encargada de almacenar temporalmente el numero de la
     * convocatoria que se esta gestionando
     */

    private String convocatoria;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Variable encargada de almacenar temporalmente el nombre
     * seleccionado en el compo Nit.
     */
    private String nombres;

    private boolean permiteVer;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ComiteseleccionsControlador
     */
    public ComiteseleccionsControlador() {
        super();
        compania = SessionUtil.getCompania();

        Map<String, Object> parametros = SessionUtil.getFlash();
        convocatoria = (String) parametros.get("convocatoria");
        permiteVer = (boolean) parametros.get("permiteVer");

        try {
            numFormulario = GeneralCodigoFormaEnum.COMITESELECCIONS_CONTROLADOR
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

        enumBase = GenericUrlEnum.NAT_COMITE_SELECCION;

        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListadocumento();
        cargarListadocumentoE();
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
        parametrosListado.put("CONVOCATORIA", convocatoria);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listadocumento
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenarlos en la lista listadocumento
     */
    public void cargarListadocumento() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComiteseleccionsControladorUrlEnum.URL5181
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listadocumento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.DP_NUMEDOCU.getName());

    }

    /**
     * 
     * Carga la lista listadocumento
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenarlos en la lista listadocumento
     */
    public void cargarListadocumentoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComiteseleccionsControladorUrlEnum.URL5181
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listadocumentoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.DP_NUMEDOCU.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control documento en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiardocumentoC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        "KEY_NUMERO_DCTO",
                        auxiliar);
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        GeneralParameterEnum.NOMBRES.getName(),
                        nombres);

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadocumento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladocumento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NUMERO_DCTO",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DP_NUMEDOCU
                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.NOMBRES.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRES
                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadocumento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladocumentoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux,
                        GeneralParameterEnum.DP_NUMEDOCU.getName());

        nombres = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRES.getName());
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
        registro.getCampos().put("NRO_CONVOCATORIA", convocatoria);
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
        registro.getCampos().remove(GeneralParameterEnum.NOMBRES.getName());
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
        //
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
     * Retorna la lista listadocumento
     * 
     * @return listadocumento
     */
    public RegistroDataModelImpl getListadocumento() {
        return listadocumento;
    }

    /**
     * Asigna la lista listadocumento
     * 
     * @param listadocumento
     * Variable a asignar en listadocumento
     */
    public void setListadocumento(RegistroDataModelImpl listadocumento) {
        this.listadocumento = listadocumento;
    }

    /**
     * Retorna la lista listadocumento
     * 
     * @return listadocumento
     */
    public RegistroDataModelImpl getListadocumentoE() {
        return listadocumentoE;
    }

    /**
     * Asigna la lista listadocumento
     * 
     * @param listadocumento
     * Variable a asignar en listadocumento
     */
    public void setListadocumentoE(RegistroDataModelImpl listadocumentoE) {
        this.listadocumentoE = listadocumentoE;
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

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    /**
     * @return the permiteVer
     */
    public boolean isPermiteVer() {
        return permiteVer;
    }

    /**
     * @param permiteVer
     * the permiteVer to set
     */
    public void setPermiteVer(boolean permiteVer) {
        this.permiteVer = permiteVer;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
