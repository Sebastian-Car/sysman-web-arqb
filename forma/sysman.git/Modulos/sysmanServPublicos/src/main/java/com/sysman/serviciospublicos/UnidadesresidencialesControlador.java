/*-
 * UnidadesresidencialesControlador.java
 *
 * 1.0
 *
 * 15/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.serviciospublicos.enums.UnidadesresidencialesControladorEnum;
import com.sysman.serviciospublicos.enums.UnidadesresidencialesControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Clase encargada de la gesti�n de la interfaz de las unidades
 * adicionales de aseo de un suscriptor.
 *
 * @version 1.0, 15/11/2016
 * @author vmolano
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @author asana
 * @version 3.0 20/06/2017 Se realiza refactoring a controlador
 */
@ManagedBean
@ViewScoped
public class UnidadesresidencialesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    /**
     * Variable que recibe por flash el c�digo del suscriptor actual.
     */
    private String codigoRutaActual;

    /**
     * Variable que almacena el uso del suscriptor actual.
     */
    private String usoActual;

    /**
     * Variable que recibe por flash el ciclo del suscriptor actual.
     */
    private String cicloActual;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de posibles usos para las unidades adicionales.
     */
    private List<Registro> listacmbUso;
    /**
     * Listado de posible estratos para las unidades adicionales.
     */
    private List<Registro> listacmbEstrato;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de UnidadesresidencialesControlador
     */
    public UnidadesresidencialesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.UNIDADESRESIDENCIALES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                cicloActual = parametrosEntrada.get("ciclo").toString();
                codigoRutaActual = parametrosEntrada.get("codigoRuta")
                                .toString();
            }
            else {
                SessionUtil.redireccionarMenu();
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
        enumBase = GenericUrlEnum.SP_UNIDADESRESIDENCIALES;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListacmbUso();
        cargarListacmbEstrato();
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
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        UnidadesresidencialesControladorEnum.PARAM2.getValue(),
                        cicloActual);
        parametrosListado.put(
                        UnidadesresidencialesControladorEnum.PARAM0.getValue(),
                        codigoRutaActual);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listacmbUso
     *
     * En este metodo se carga el listado de posibles usos que pueden
     * tener las unidades adicionales de aseo.
     */
    public void cargarListacmbUso() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {

            listacmbUso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UnidadesresidencialesControladorUrlEnum.URL5527
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
     * Carga la lista listacmbEstrato
     *
     * En este metodo se carga el listado de posibles estratos que
     * pueden tener las unidades adicionales de aseo.
     */
    public void cargarListacmbEstrato() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(UnidadesresidencialesControladorEnum.PARAM1.getValue(),
                        usoActual);

        try {
            listacmbEstrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UnidadesresidencialesControladorUrlEnum.URL6066
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Retorna la variable indice
     *
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control cmbUso
     *
     * Se actualiza el listado de estratos de acuerdo al uso
     * seleccionado.
     *
     */
    public void cambiarcmbUso() {
        // <CODIGO_DESARROLLADO>
        usoActual = registro.getCampos().get("USO").toString();
        cargarListacmbEstrato();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmbUso en la fila
     * seleccionada dentro de la grilla
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarcmbUsoC(int rowNum) {
        usoActual = listaInicial.getDatasource().get(rowNum % 10)
                        .getCampos().get("USO").toString();
        cargarListacmbEstrato();

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

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
     * Metodo ejecutado cuando se cancela la edici�n del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CICLO.getName(),
                        cicloActual);
        registro.getCampos().put(GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRutaActual);

        try {
            StringBuilder condicion = new StringBuilder();
            condicion.append("COMPANIA=");
            condicion.append(compania);
            condicion.append(" AND CICLO=");
            condicion.append(cicloActual);
            condicion.append(" AND CODIGORUTA=");
            condicion.append(codigoRutaActual);
            condicion.append("");

            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            ejbSysmanUtilRemote
                                            .generarConsecutivoConValorInicial(
                                                            UnidadesresidencialesControladorEnum.PARAM3
                                                                            .getValue(),
                                                            condicion.toString(),
                                                            "CONSECUTIVO",
                                                            "1"));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        if (registro.getCampos().get("ESTRATOASEO") == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3239"));
            return false;
        }
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
     * Metodo ejecutado despues de realizar la eliminaci�n del
     * registro
     *
     * @return true
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
        // NO EST� IMPLEMENTADO
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // NO EST� IMPLEMENTADO
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro reg) {
        indice = listaInicial.getRowIndex();
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbUso
     *
     * @return listacmbUso
     */
    public List<Registro> getListacmbUso() {
        return listacmbUso;
    }

    /**
     * Asigna la lista listacmbUso
     *
     * @param listacmbUso
     * Variable a asignar en listacmbUso
     */
    public void setListacmbUso(List<Registro> listacmbUso) {
        this.listacmbUso = listacmbUso;
    }

    /**
     * Retorna la lista listacmbEstrato
     *
     * @return listacmbEstrato
     */
    public List<Registro> getListacmbEstrato() {
        return listacmbEstrato;
    }

    /**
     * Asigna la lista listacmbEstrato
     *
     * @param listacmbEstrato
     * Variable a asignar en listacmbEstrato
     */
    public void setListacmbEstrato(List<Registro> listacmbEstrato) {
        this.listacmbEstrato = listacmbEstrato;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
