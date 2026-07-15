/*-
 * FrmValidarInventarioFisicoControlador.java
 *
 * 1.0
 * 
 * 13/06/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.FrmValidarInventarioFisicoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 13/06/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmValidarInventarioFisicoControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String dependencia;
    private String responsable;
    private Date fecha;
    private String referencia;
    private String nombreDependencia;
    private String nombreResponsable;
    private String sucursal;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaDependenciaE;
    private RegistroDataModelImpl listaResponsable;
    private RegistroDataModelImpl listaResponsableE;

    @EJB
    private EjbAlmacenCincoRemote ejbAlmacenCincoRemote;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private final String FECHA_LECTURA = "FECHA_LECTURA";

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmValidarInventarioFisicoControlador
     */
    public FrmValidarInventarioFisicoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 2083;
            validarPermisos();
            fecha = new Date();
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
        enumBase = GenericUrlEnum.INVENTARIO_FISICO;
        buscarLlave();
        registro = new Registro();

        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDependencia();
        cargarListaDependenciaE();
        cargarListaResponsableE();
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
        try {
            parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            parametrosListado.put(FECHA_LECTURA,
                            SysmanFunciones.convertirAFechaCadena(fecha));

            parametrosListado.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            dependencia);
            parametrosListado.put(GeneralParameterEnum.RESPONSABLE.getName(),
                            responsable);
            parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmValidarInventarioFisicoControladorUrlEnum.URL1800
                                                            .getValue());
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmValidarInventarioFisicoControladorUrlEnum.URL1795
                                                        .getValue());
        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependenciaE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listaResponsable
     *
     */
    public void cargarListaResponsable() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmValidarInventarioFisicoControladorUrlEnum.URL1796
                                                        .getValue());
        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.RESPONSABLE.getName());

    }

    /**
     * 
     * Carga la lista listaResponsable
     *
     */
    public void cargarListaResponsableE() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Codigo
     * 
     * 
     */

    public void cambiarCodigo() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(fecha.toString())
            || SysmanFunciones.validarVariableVacio(dependencia)
            || SysmanFunciones.validarVariableVacio(responsable)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4294"));
        }
        else if (referencia.length() > 6) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4315"));
        }
        else {
            inventarioFisico();
        }
        referencia = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Fecha
     * 
     * 
     */
    public void cambiarFecha() {
        // <CODIGO_DESARROLLADO>
        dependencia = null;
        nombreDependencia = null;
        responsable = null;
        nombreResponsable = null;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    private void inventarioFisico() {

        try {

            ejbAlmacenCincoRemote.insertInventarioFisico(compania,
                            fecha,
                            dependencia, responsable, sucursal,
                            referencia, SessionUtil.getUser().toString());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            "MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreDependencia = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();

        responsable = null;
        nombreResponsable = null;

        cargarListaResponsable();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        responsable = registroAux.getCampos()
                        .get(GeneralParameterEnum.RESPONSABLE.getName())
                        .toString();

        nombreResponsable = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();

        sucursal = registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName())
                        .toString();

        reasignarOrigen();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CEDULA");
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
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
    /**
     * Retorna la variable dependencia
     * 
     * @return dependencia
     */
    public String getDependencia() {
        return dependencia;
    }

    /**
     * Asigna la variable dependencia
     * 
     * @param dependencia
     * Variable a asignar en dependencia
     */
    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    /**
     * Retorna la variable responsable
     * 
     * @return responsable
     */
    public String getResponsable() {
        return responsable;
    }

    /**
     * Asigna la variable responsable
     * 
     * @param responsable
     * Variable a asignar en responsable
     */
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    /**
     * Retorna la variable fecha
     * 
     * @return fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Asigna la variable fecha
     * 
     * @param fecha
     * Variable a asignar en fecha
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * Retorna la variable referencia
     * 
     * @return referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Asigna la variable referencia
     * 
     * @param referencia
     * Variable a asignar en referencia
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * Retorna la variable nombreDependencia
     * 
     * @return nombreDependencia
     */
    public String getNombreDependencia() {
        return nombreDependencia;
    }

    /**
     * Asigna la variable nombreDependencia
     * 
     * @param nombreDependencia
     * Variable a asignar en nombreDependencia
     */
    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    /**
     * Retorna la variable nombreResponsable
     * 
     * @return nombreResponsable
     */
    public String getNombreResponsable() {
        return nombreResponsable;
    }

    /**
     * Asigna la variable nombreResponsable
     * 
     * @param nombreResponsable
     * Variable a asignar en nombreResponsable
     */
    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependenciaE() {
        return listaDependenciaE;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependenciaE(RegistroDataModelImpl listaDependenciaE) {
        this.listaDependenciaE = listaDependenciaE;
    }

    /**
     * Retorna la lista listaResponsable
     * 
     * @return listaResponsable
     */
    public RegistroDataModelImpl getListaResponsable() {
        return listaResponsable;
    }

    /**
     * Asigna la lista listaResponsable
     * 
     * @param listaResponsable
     * Variable a asignar en listaResponsable
     */
    public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
        this.listaResponsable = listaResponsable;
    }

    /**
     * Retorna la lista listaResponsable
     * 
     * @return listaResponsable
     */
    public RegistroDataModelImpl getListaResponsableE() {
        return listaResponsableE;
    }

    /**
     * Asigna la lista listaResponsable
     * 
     * @param listaResponsable
     * Variable a asignar en listaResponsable
     */
    public void setListaResponsableE(RegistroDataModelImpl listaResponsableE) {
        this.listaResponsableE = listaResponsableE;
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

    /**
     * @return the sucursal
     */
    public String getSucursal() {
        return sucursal;
    }

    /**
     * @param sucursal
     * the sucursal to set
     */
    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
