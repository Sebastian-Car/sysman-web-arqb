/*-
 * CCostoTransaccionModelosControlador.java
 *
 * 1.0
 * 
 * 04/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.transautomaticas;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.transautomaticas.enums.CCostoTransaccionModelosControladorUrlEnum;
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
 * Permite configurar los centros de costo de la transaccion
 *
 * @version 1.0, 04/10/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class CCostoTransaccionModelosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    private final String compania;
    private int ano;
    private String tipo;
    private String numero;
    private Map<String, Object> ridParametroCostos;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * carga lista de los centros de costos a asignar a transaccion
     */
    private RegistroDataModelImpl listacentroCosto;
    /**
     * carga lista de los centros de costos a asignar a transaccion
     */
    private RegistroDataModelImpl listacentroCostoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CCostoTransaccionModelosControlador
     */
    public CCostoTransaccionModelosControlador() {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        try {
            // 1952
            numFormulario = GeneralCodigoFormaEnum.FRM_CCTRANSACCIONMODELO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            if (parametrosEntrada != null) {
                ridParametroCostos = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                ano = (int) parametrosEntrada.get("ano");
                tipo = (String) parametrosEntrada.get("tipo");
                numero = (String) parametrosEntrada.get("numero");
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.TRANSACCIONMODELO_CC;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacentroCosto();
        cargarListacentroCostoE();
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
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipo);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numero);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacentroCosto
     */
    public void cargarListacentroCosto() {

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CCostoTransaccionModelosControladorUrlEnum.URL1812
                                                        .getValue());

        listacentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacentroCosto
     */
    public void cargarListacentroCostoE() {

        UrlBean urlBean;

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CCostoTransaccionModelosControladorUrlEnum.URL1812
                                                        .getValue());

        listacentroCostoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarcentroCostoC(int rowNun) {

        // <CODIGO_DESARROLLADO>

        /*
         * listaInicial.getDatasource().get(rowNun %
         * 10).getCampos().put("CODIGO",
         * registro.getCampos().get(GeneralParameterEnum.CODIGO.
         * getName()));
         */
        listaInicial.getDatasource().get(rowNun % 10).getCampos().put("NOMBRE",
                        registro.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroCosto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacentroCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacentroCostoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

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
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(GeneralParameterEnum.TIPO.getName(), tipo);
        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), numero);
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacentroCosto
     * 
     * @return listacentroCosto
     */
    public RegistroDataModelImpl getListacentroCosto() {
        return listacentroCosto;
    }

    /**
     * Asigna la lista listacentroCosto
     * 
     * @param listacentroCosto
     * Variable a asignar en listacentroCosto
     */
    public void setListacentroCosto(RegistroDataModelImpl listacentroCosto) {
        this.listacentroCosto = listacentroCosto;
    }

    /**
     * Retorna la lista listacentroCosto
     * 
     * @return listacentroCosto
     */
    public RegistroDataModelImpl getListacentroCostoE() {
        return listacentroCostoE;
    }

    /**
     * Asigna la lista listacentroCosto
     * 
     * @param listacentroCosto
     * Variable a asignar en listacentroCosto
     */
    public void setListacentroCostoE(RegistroDataModelImpl listacentroCostoE) {
        this.listacentroCostoE = listacentroCostoE;
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
