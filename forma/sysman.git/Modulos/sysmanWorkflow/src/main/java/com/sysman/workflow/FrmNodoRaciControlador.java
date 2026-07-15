/*-
 * FrmNodoRaciControlador.java
 *
 * 1.0
 * 
 * 20/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.workflow.enums.FrmNodoRaciControladorEnum;
import com.sysman.workflow.enums.FrmNodoRaciControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Migración del formulario en access FRM_NODO_RACI a web con el
 * controlador FrmNodoRaciControlador
 *
 * @version 1.0, 20/04/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped

public class FrmNodoRaciControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /** Atributo que contiene el codigo del proceso del nodo. */
    private String codigoProceso;

    /** Atributo que contiene el codigo del nodo. */
    private String codigoNodo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los detalles del combo CODIGO_ROL (CB5899).
     */

    private List<Registro> listaCodigoRol;
    /**
     * Lista que contiene los detalles del combo CODIGO_RACI (CB5900)
     */
    private List<Registro> listaCodigoRaci;
    /**
     * lista que contiene los detalles del combo Estado (CB5902)
     */
    private List<Registro> listaEstado;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmNodoRaciControlador
     */
    public FrmNodoRaciControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_NODOS_RACI_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> paramEntrada = SessionUtil.getFlash();
            if (paramEntrada != null) {
                codigoProceso = paramEntrada
                                .get(FrmNodoRaciControladorEnum.PR_CODIGO_PROCESO
                                                .getValue())
                                .toString();

                codigoNodo = paramEntrada
                                .get(FrmNodoRaciControladorEnum.PR_CODIGO_NODO
                                                .getValue())
                                .toString();
            }

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

        enumBase = GenericUrlEnum.NODO_RACI;
        reasignarOrigen();
        buscarLlave();

        registro = new Registro();

        cargarListaCodigoRol();
        cargarListaCodigoRaci();
        cargarListaEstado();

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
        parametrosListado.put("COMPANIA", compania);
        parametrosListado.put("CODIGO_PROCESO", codigoProceso);
        parametrosListado.put("CODIGO_NODO", codigoNodo);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     *
     * Carga la lista ListaCODIGO_ROL Asociada al combo CODIGO_ROL
     */
    public void cargarListaCodigoRol() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaCodigoRol = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodoRaciControladorUrlEnum.URL3348
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
     * Carga la lista ListaCODIGO_RACI Asociada al combo CODIGO_RACI
     *
     * 
     */
    public void cargarListaCodigoRaci() {
        Map<String, Object> param = new TreeMap<>();

        param.put(FrmNodoRaciControladorEnum.CATEGORIA.getValue(),
                        8);

        try {
            listaCodigoRaci = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodoRaciControladorUrlEnum.URL3349
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
     * Carga la lista listaEstado Asociada al combo Estado
     */
    public void cargarListaEstado() {
        Map<String, Object> param = new TreeMap<>();

        param.put(FrmNodoRaciControladorEnum.CATEGORIA.getValue(), 4);

        try {
            listaEstado = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmNodoRaciControladorUrlEnum.URL3349
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        registro.getCampos().put("ESTADO", 4);
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
     * @return true;
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("CODIGO_PROCESO", codigoProceso);
        registro.getCampos().put("CODIGO_NODO", codigoNodo);
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
        registro.getCampos().remove("ESTADO_NOM");
        registro.getCampos().remove("CODIGO_RACI_NOM");
        registro.getCampos().remove("CODIGO_ROL_NOM");
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
        listaInicial.load();
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
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("CODIGO_PROCESO");
        registro.getCampos().remove("CODIGO_NODO");

    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
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
        // Auto-generated method stub
        registro.getCampos().put("ESTADO", 4);

    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCODIGO_ROL
     * 
     * @return listaCODIGO_ROL
     */
    public List<Registro> getListaCodigoRol() {
        return listaCodigoRol;
    }

    /**
     * Asigna la lista listaCODIGO_ROL
     * 
     * @param listaCODIGO_ROL
     * Variable a asignar en listaCODIGO_ROL
     */
    public void setListaCodigoRol(List<Registro> listaCodigoRol) {
        this.listaCodigoRol = listaCodigoRol;
    }

    /**
     * Retorna la lista listaCODIGO_RACI
     * 
     * @return listaCODIGO_RACI
     */
    public List<Registro> getListaCodigoRaci() {
        return listaCodigoRaci;
    }

    /**
     * Asigna la lista listaCODIGO_RACI
     * 
     * @param listaCODIGO_RACI
     * Variable a asignar en listaCODIGO_RACI
     */
    public void setListaCodigoRaci(List<Registro> listaCodigoRaci) {
        this.listaCodigoRaci = listaCodigoRaci;
    }

    /**
     * Retorna la lista listaESTADO
     * 
     * @return listaESTADO
     */
    public List<Registro> getListaEstado() {
        return listaEstado;
    }

    /**
     * Asigna la lista listaESTADO
     * 
     * @param listaESTADO
     * Variable a asignar en listaESTADO
     */
    public void setListaEstado(List<Registro> listaESTADO) {
        this.listaEstado = listaESTADO;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
