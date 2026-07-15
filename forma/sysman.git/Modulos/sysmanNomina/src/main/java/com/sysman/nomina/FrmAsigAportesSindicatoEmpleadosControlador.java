/*-
 * FrmAsigAportesSindicatoEmpleadosControlador.java
 *
 * 1.0
 * 
 * 2 abr. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.FrmAsigAportesSindicatoEmpleadosControladorEnum;
import com.sysman.nomina.enums.FrmAsigAportesSindicatoEmpleadosControladorUrlEnum;
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
 * Formulario que permite configuar los aportes al empelado
 *
 * @version 1.0, 02/04/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmAsigAportesSindicatoEmpleadosControlador
                extends BeanBaseContinuoAcmeImpl {
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
     * Lista que carga los empleados
     */
    private RegistroDataModelImpl listaEmpleado;
    /**
     * Lista que carga los empleados en la grill
     */
    private RegistroDataModelImpl listaEmpleadoE;
    /**
     * Lista que carga los aportes sindicales
     */
    private RegistroDataModelImpl listaCodigoAporte;
    /**
     * Lista que carga los aportes sindicales en la grilla
     */
    private RegistroDataModelImpl listaCodigoAporteE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Variable que almacena el nombre del empleado seleccionado en la
     * grilla
     */
    private String auxiliarNombreEmpleado;

    /**
     * Variable que almacena el sindicato seleccionado en la grilla
     */
    private String auxiliarClaseIdFondo;

    /**
     * Variable que almacena la forma seleccionada en la grilla
     */
    private String auxiliarForma;
    /**
     * Variable que almacena el valor seleccionado en la grilla
     */
    private String auxiliarValor;

    /**
     * Variable que almacena el nombre del sindicato seleccionado en
     * la grilla
     */
    private String auxiliarNombreSindicato;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmAsigAportesSindicatoEmpleadosControlador
     */
    public FrmAsigAportesSindicatoEmpleadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 2056;
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
        enumBase = GenericUrlEnum.PERSONAL_APORTE;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaEmpleado();
        cargarListaEmpleadoE();
        cargarListaCodigoAporte();
        cargarListaCodigoAporteE();
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
     * Carga la lista listaEmpleado
     *
     */
    public void cargarListaEmpleado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAsigAportesSindicatoEmpleadosControladorUrlEnum.URL5243
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }

    /**
     * 
     * Carga la lista listaEmpleadoE
     *
     */
    public void cargarListaEmpleadoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAsigAportesSindicatoEmpleadosControladorUrlEnum.URL5749
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaEmpleadoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoAporte
     *
     */
    public void cargarListaCodigoAporte() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAsigAportesSindicatoEmpleadosControladorUrlEnum.URL6263
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCodigoAporte = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoAporte
     *
     */
    public void cargarListaCodigoAporteE() {
        listaCodigoAporteE = listaCodigoAporte;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Empleado en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEmpleadoC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRECOMPLETO
                                        .getValue(), auxiliarNombreEmpleado);

    }

    /**
     * Metodo ejecutado al cambiar el control CodigoAporte en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigoAporteC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmAsigAportesSindicatoEmpleadosControladorEnum.CLASE_ID_DE_FONDO
                                        .getValue(), auxiliarClaseIdFondo);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmAsigAportesSindicatoEmpleadosControladorEnum.FORMA_DESCUENTO
                                        .getValue(),
                                        auxiliarForma);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.VALOR.getName(),
                                        auxiliarValor);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRE_FONDO
                                        .getValue(),
                                        auxiliarNombreSindicato);
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_EMPLEADO
                                                        .getName()));

        registro.getCampos().put(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRECOMPLETO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRECOMPLETO
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleadoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.ID_DE_EMPLEADO.getName())
                        .toString();

        auxiliarNombreEmpleado = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRECOMPLETO
                                                        .getValue()),
                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoAporte
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoAporte(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_APORTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.CLASE_ID_DE_FONDO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmAsigAportesSindicatoEmpleadosControladorEnum.CLASE_ID_DE_FONDO
                                                        .getValue()));

        registro.getCampos().put(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.FORMA_DESCUENTO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmAsigAportesSindicatoEmpleadosControladorEnum.FORMA_DESCUENTO
                                                        .getValue()));

        registro.getCampos().put(GeneralParameterEnum.VALOR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.VALOR.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoAporteE
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoAporteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();

        auxiliarClaseIdFondo = registroAux.getCampos().get(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.CLASE_ID_DE_FONDO
                                        .getValue())
                        .toString();

        auxiliarForma = registroAux.getCampos().get(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.FORMA_DESCUENTO
                                        .getValue())
                        .toString();

        auxiliarValor = registroAux.getCampos()
                        .get(GeneralParameterEnum.VALOR.getName()).toString();

        auxiliarNombreSindicato = registroAux.getCampos()
                        .get(FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRE_FONDO
                                        .getValue())
                        .toString();

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
        /*
         * FR2056-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 1, Me.Name End Sub
         */
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

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().remove(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRECOMPLETO
                                        .getValue());

        registro.getCampos().remove("NOMBRE_FORMA");

        registro.getCampos().remove(GeneralParameterEnum.VALOR.getName());

        registro.getCampos().remove(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.FORMA_DESCUENTO
                                        .getValue());

        registro.getCampos().remove(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRE_FONDO
                                        .getValue());

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
        /*
         * FR2056-DESPUES_INSERTAR Private Sub Form_AfterInsert()
         * Predecesor_Auxiliar End Sub
         */
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRECOMPLETO
                                        .getValue());

        registro.getCampos().remove("NOMBRE_FORMA");

        registro.getCampos().remove(GeneralParameterEnum.VALOR.getName());

        registro.getCampos().remove(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.FORMA_DESCUENTO
                                        .getValue());

        registro.getCampos().remove(
                        FrmAsigAportesSindicatoEmpleadosControladorEnum.NOMBRE_FONDO
                                        .getValue());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaEmpleado
     * 
     * @return listaEmpleado
     */
    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    /**
     * Asigna la lista listaEmpleado
     * 
     * @param listaEmpleado
     * Variable a asignar en listaEmpleado
     */
    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    /**
     * Retorna la lista listaEmpleado
     * 
     * @return listaEmpleado
     */
    public RegistroDataModelImpl getListaEmpleadoE() {
        return listaEmpleadoE;
    }

    /**
     * Asigna la lista listaEmpleado
     * 
     * @param listaEmpleado
     * Variable a asignar en listaEmpleado
     */
    public void setListaEmpleadoE(RegistroDataModelImpl listaEmpleadoE) {
        this.listaEmpleadoE = listaEmpleadoE;
    }

    /**
     * Retorna la lista listaCodigoAporte
     * 
     * @return listaCodigoAporte
     */
    public RegistroDataModelImpl getListaCodigoAporte() {
        return listaCodigoAporte;
    }

    /**
     * Asigna la lista listaCodigoAporte
     * 
     * @param listaCodigoAporte
     * Variable a asignar en listaCodigoAporte
     */
    public void setListaCodigoAporte(RegistroDataModelImpl listaCodigoAporte) {
        this.listaCodigoAporte = listaCodigoAporte;
    }

    /**
     * Retorna la lista listaCodigoAporte
     * 
     * @return listaCodigoAporte
     */
    public RegistroDataModelImpl getListaCodigoAporteE() {
        return listaCodigoAporteE;
    }

    /**
     * Asigna la lista listaCodigoAporte
     * 
     * @param listaCodigoAporte
     * Variable a asignar en listaCodigoAporte
     */
    public void setListaCodigoAporteE(
        RegistroDataModelImpl listaCodigoAporteE) {
        this.listaCodigoAporteE = listaCodigoAporteE;
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
