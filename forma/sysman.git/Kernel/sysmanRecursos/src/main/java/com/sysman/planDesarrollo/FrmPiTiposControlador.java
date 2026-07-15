/*-
 * FrmPiTiposControlador.java
 *
 * 1.0
 * 
 * 05/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.planDesarrollo;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.planDesarrollo.enums.FrmPiTiposControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para redireccionar de este formulario al formulario
 * plan de accion
 *
 * @version 1.0, 05/03/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class FrmPiTiposControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el tipo seleccionado
     */
    private String tipo;
    /**
     * Atributo que almacena la vigencia seleccionada
     */
    private String vigencia;
    /**
     * Atributo que almacena el nombre del tipo seleccionado
     */
    private String nombreTipo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de las vigencias
     */
    private List<Registro> listaVigencia;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los tipos
     */
    private RegistroDataModelImpl listaTipo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmPiTiposControlador
     */
    public FrmPiTiposControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            // 1734
            numFormulario = GeneralCodigoFormaEnum.PI_TIPOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
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
        // <CARGAR_LISTA>
        cargarListaVigencia();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

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

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigencia
     */
    public void cargarListaVigencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmPiTiposControladorUrlEnum.URL145
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaTipo
     */
    public void cargarListaTipo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPiTiposControladorUrlEnum.URL168
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        /*
         * param.put(GeneralParameterEnum.CLASE.getName(), "PRO");
         */

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> param = new TreeMap<>();

        param.put("tipo", tipo);
        param.put(GeneralParameterEnum.VIGENCIA.getName().toLowerCase(),
                        vigencia);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.TRANSPLANDESARROLLO_CONTROLADOR
                                        .getCodigo()));

        RequestContext.getCurrentInstance().closeDialog(direccionador);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTipo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreTipo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipo
     * 
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna la variable tipo
     * 
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna la variable vigencia
     * 
     * @return vigencia
     */
    public String getVigencia() {
        return vigencia;
    }

    /**
     * Asigna la variable vigencia
     * 
     * @param vigencia
     * Variable a asignar en vigencia
     */
    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    /**
     * Retorna la variable nombreTipo
     * 
     * @return nombreTipo
     */
    public String getNombreTipo() {
        return nombreTipo;
    }

    /**
     * Asigna la variable nombreTipo
     * 
     * @param nombreTipo
     * Variable a asignar en nombreTipo
     */
    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipo
     * 
     * @return listaTipo
     */
    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    /**
     * Asigna la lista listaTipo
     * 
     * @param listaTipo
     * Variable a asignar en listaTipo
     */
    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
