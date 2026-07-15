/*-
 * FinaneducacionsControlador.java
 *
 * 1.0
 *
 * 07/02/2018
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
import com.sysman.hojasdevida.enums.FinaneducacionsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que sirve para visualizar los niveles de educación del aspirante de hojas de vida sección capacitación.
 *
 * @version 1.0, 07/02/2018
 * @author spina
 * 
 * @version 2.0, 02/05/2018
 * @author jromero
 */
@ManagedBean
@ViewScoped

public class FinaneducacionsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
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
     * Lista que almacena el tipo de modalidad desde la vista
     */
    private RegistroDataModelImpl listaModalidad;
    /**
     * Lista que almacena el tipo de modalidad desde la vista
     */
    private RegistroDataModelImpl listaModalidadE;
    /**
     * Lista que almacena el titulo obtenido desde la vista
     */
    private RegistroDataModelImpl listaTituloObtenido1;
    /**
     * Lista que almacena el titulo obtenido desde la vista
     */
    private RegistroDataModelImpl listaTituloObtenido1E;
    /**
     * /** Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;
    private Map<String, Object> rid;
    private String idempleado;
    private String numeroDcto;
    private String sucursal;
    private String numero;
    private Object modalidad;
    private Object titulobtenido;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FinaneducacionsControlador
     */
    public FinaneducacionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada
                                .get("rid");

                idempleado = (String) parametrosEntrada
                                .get("idempleado");
                numeroDcto = (String) parametrosEntrada.get("numeroDcto");
                sucursal = (String) parametrosEntrada.get("sucursal");
                numero = (String) parametrosEntrada.get("numero");
            }

            numFormulario = GeneralCodigoFormaEnum.FINANEDUCACIONS_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.NAT_FORMACION_ACADEMICA.getTable();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaModalidad();
        cargarListaModalidadE();
        cargarListaTituloObtenido1();
        cargarListaTituloObtenido1E();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        idempleado);
        parametrosListado.put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        numeroDcto);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        parametrosListado.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        numero);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinaneducacionsControladorUrlEnum.URL3535
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinaneducacionsControladorUrlEnum.URL9591
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinaneducacionsControladorUrlEnum.URL6565
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinaneducacionsControladorUrlEnum.URL5651
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaModalidad
     *
     */
    public void cargarListaModalidad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinaneducacionsControladorUrlEnum.URL4120
                                                        .getValue());

        listaModalidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, "MO_CODIGO");
    }

    /**
     * 
     * Carga la lista listaModalidad
     *
     */

    public void cargarListaModalidadE() {

        listaModalidadE = listaModalidad;

    }

    /**
     * 
     * Carga la lista listaTituloObtenido1
     *
     */
    public void cargarListaTituloObtenido1() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinaneducacionsControladorUrlEnum.URL7878
                                                        .getValue());

        listaTituloObtenido1 = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, "CODIGOPROF");

    }

    /**
     * 
     * Carga la lista listaTituloObtenido1
     *
     */
    public void cargarListaTituloObtenido1E() {

        listaTituloObtenido1E = listaTituloObtenido1;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaModalidad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModalidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("MODALIDAD",
                        registroAux.getCampos().get("MO_NOMBRE"));

        modalidad = registroAux.getCampos().get("MO_CODIGO").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaModalidad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModalidadE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("MO_NOMBRE");
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTituloObtenido1
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTituloObtenido1(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TITULOOBTENIDO",
                        registroAux.getCampos().get("CODIGOPROF"));

        titulobtenido = registroAux.getCampos().get("NOMBRE_PROFESION")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTituloObtenido1
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTituloObtenido1E(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGOPROF");
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
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

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        numeroDcto);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put("NFA_CODIGOPERSONA", idempleado);

        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                        numero);
        registro.getCampos().put("MODALIDAD", modalidad);

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
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
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
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del registro
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
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO_DCTO.getName());
        registro.getCampos().remove("NOMBRETITULO");
        registro.getCampos().remove("NFA_CODIGOPERSONA");
        registro.getCampos().remove("NUMERO");

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
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
     * Retorna la lista listaModalidad
     * 
     * @return listaModalidad
     */
    public RegistroDataModelImpl getListaModalidad() {
        return listaModalidad;
    }

    /**
     * Asigna la lista listaModalidad
     * 
     * @param listaModalidad
     * Variable a asignar en listaModalidad
     */
    public void setListaModalidad(RegistroDataModelImpl listaModalidad) {
        this.listaModalidad = listaModalidad;
    }

    /**
     * Retorna la lista listaModalidad
     * 
     * @return listaModalidad
     */
    public RegistroDataModelImpl getListaModalidadE() {
        return listaModalidadE;
    }

    /**
     * Asigna la lista listaModalidad
     * 
     * @param listaModalidad
     * Variable a asignar en listaModalidad
     */
    public void setListaModalidadE(RegistroDataModelImpl listaModalidadE) {
        this.listaModalidadE = listaModalidadE;
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

    public String getCompania() {
        return compania;
    }

    public RegistroDataModelImpl getListaTituloObtenido1() {
        return listaTituloObtenido1;
    }

    public void setListaTituloObtenido1(
        RegistroDataModelImpl listaTituloObtenido1) {
        this.listaTituloObtenido1 = listaTituloObtenido1;
    }

    public RegistroDataModelImpl getListaTituloObtenido1E() {
        return listaTituloObtenido1E;
    }

    public void setListaTituloObtenido1E(
        RegistroDataModelImpl listaTituloObtenido1E) {
        this.listaTituloObtenido1E = listaTituloObtenido1E;
    }
}
