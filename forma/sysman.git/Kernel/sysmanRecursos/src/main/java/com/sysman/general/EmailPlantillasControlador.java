/*-
 * EmailPlantillasControlador.java
 *
 * 1.0
 * 
 * 8/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.EmailPlantillasControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que gestiona las plantillas de email y sus destinatarios
 *
 * @version 1.0, 08/06/2018
 * @author eamaya
 * 
 * @version 2.0 21/06/2018 , La forma de este formulario no se debe
 * migrar
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped

public class EmailPlantillasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String usuario;

    private boolean verEditor;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los procesos
     */
    private List<Registro> listaProceso;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista que carga los email de destino
     */
    private RegistroDataModelImpl listaEmaildestino;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de EmailPlantillasControlador
     */
    public EmailPlantillasControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        urlConexionCache = UrlServiceCache.SYSMANIRISST;
        try {
            numFormulario = GeneralCodigoFormaEnum.EMAIL_PLANTILLAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
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
        cargarListaProceso();
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
        cargarListaEmaildestino();
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
        listaEmaildestino = null;
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
        enumBase = GenericUrlEnum.EMAIL_PLANTILLA;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    /**
     * 
     * Carga la lista listaEmaildestino
     *
     */
    public void cargarListaEmaildestino() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.EMAIL_DESTINO
                                                        .getGridKey());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put("PLANTILLA", registro.getCampos()
                        .get(GeneralParameterEnum.CONSECUTIVO.getName()));

        try {
            listaEmaildestino = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANIRISST,
                                            GenericUrlEnum.EMAIL_DESTINO
                                                            .getTable()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaProceso
     *
     */
    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();

        param.put("CATEGORIA", "10");

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EmailPlantillasControladorUrlEnum.URL7903
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Origen
     * 
     * 
     */
    public void cambiarOrigen() {
        if (!SysmanFunciones.validarEmail(registro.getCampos()
                        .get("ORIGEN")
                        .toString())) {

            JsfUtil.agregarMensajeAlerta("No es un email valido");

            registro.getCampos().put("ORIGEN", null);

        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProceso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProceso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PROCESO", registroAux.getCampos().get("ID"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Emaildestino
     * 
     */
    public void agregarRegistroSubEmaildestino() {
        try {

            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            registroSub.getCampos().put("PLANTILLA",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CONSECUTIVO
                                                            .getName()));

            registroSub.getCampos().put("CODIGO_DESTINATARIO",
                            generarConsecutivoDestinataro());

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            usuario);

            if (SysmanFunciones.validarEmail(registroSub.getCampos()
                            .get("CORREO_DESTINATARIO").toString())) {

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.EMAIL_DESTINO
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSub.getCampos());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));

            }
            else {
                JsfUtil.agregarMensajeAlerta("No es un email valido");
            }

            cargarListaEmaildestino();

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    private Object generarConsecutivoDestinataro() {
        long consecutivo = 0;

        try {
            consecutivo = (long) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMANK,
                            "PCK_SYSMAN_UTL.FC_GENCONSECUTIVO",
                            "UN_TABLA             =>'EMAIL_DESTINO', "
                                + "UN_CRITERIO          =>' COMPANIA =''"
                                + compania + "'' AND PLANTILLA = ''" +
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CONSECUTIVO
                                                                .getName())
                                + "''', "
                                + "UN_CAMPO  =>'CODIGO_DESTINATARIO'",

                            Types.BIGINT);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
    }

    /**
     * Metodo de edicion del formulario Emaildestino
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubEmaildestino(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            usuario);

            if (SysmanFunciones.validarEmail(reg.getCampos()
                            .get("CORREO_DESTINATARIO").toString())) {
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.EMAIL_DESTINO
                                                                .getUpdateKey());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                reg.getCampos(), reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
            else {
                JsfUtil.agregarMensajeAlerta("No es un email valido");
            }

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaEmaildestino();
        }
    }

    /**
     * Metodo de eliminacion del formulario Emaildestino
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubEmaildestino(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.EMAIL_DESTINO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaEmaildestino();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Emaildestino
     *
     */
    public void cancelarEdicionEmaildestino() {
        cargarListaEmaildestino();
    }

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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        actualizarHtml();
        // </CODIGO_DESARROLLADO>
    }

    public void activarEditor() {
        verEditor = !verEditor;
    }

    public void actualizarHtml() {
        JsfUtil.ejecutarJavaScript("$(\"#panelEditor\").html(`"
            + SysmanFunciones.nvl(registro.getCampos().get("DESCRIPCION"), "")
            + "`)");
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);

        registro.getCampos().put("CONSECUTIVO", generarConsecutivo());

        // </CODIGO_DESARROLLADO>
        return true;
    }

    private long generarConsecutivo() {
        long consecutivo = 0;

        try {
            consecutivo = (long) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMANK,
                            "PCK_SYSMAN_UTL.FC_GENCONSECUTIVO",
                            "UN_TABLA             =>'EMAIL_PLANTILLA', "
                                + "UN_CRITERIO          =>' COMPANIA =''" + compania + "''', "
                                + "UN_CAMPO  =>'CONSECUTIVO'",

                            Types.BIGINT);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
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
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }

        actualizarHtml();
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
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public boolean isVerEditor() {
        return verEditor;
    }

    public void setVerEditor(boolean verEditor) {
        this.verEditor = verEditor;
    }

    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    public RegistroDataModelImpl getListaEmaildestino() {
        return listaEmaildestino;
    }

    public void setListaEmaildestino(RegistroDataModelImpl listaEmaildestino) {
        this.listaEmaildestino = listaEmaildestino;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>
}
