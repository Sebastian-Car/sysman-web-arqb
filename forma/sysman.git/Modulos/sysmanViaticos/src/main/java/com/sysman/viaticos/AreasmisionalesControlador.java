/*-
 * AreasmisionalesControlador.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.AreasmisionalesControladorEnum;
import com.sysman.viaticos.enums.AreasmisionalesControladorUrlEnum;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Migracion del formulario access FRM_TIPO_COMPETENCIAS a web
 * controlador FrmtipocompetenciasControlador forma
 * Frmtipocompetencias.xhtml creacion de menu para abrir el formulario
 * continuo, creacion de properties para el formulario continuo.
 *
 * @version 1.0, 18/01/2018
 * @author crodriguez
 */
@ManagedBean
@ViewScoped
public class AreasmisionalesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaFuncionario;
    /**
     * DOCUMENTACION NECESARIA
     */
    private RegistroDataModelImpl listaFuncionarioE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de AreasmisionalesControlador
     */
    public AreasmisionalesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.AREA_MISIONAL_CONTROLADOR
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

        enumBase = GenericUrlEnum.VI_AREAMISIONAL;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFuncionario();
        cargarListaFuncionarioE();
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

    // <ME S_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaFuncionario
     *
     * DOCUMENTACION ADICIONAL
     */
    public void cargarListaFuncionario() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AreasmisionalesControladorUrlEnum.URL543
                                                        .getValue());
        listaFuncionario = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        AreasmisionalesControladorEnum.ID_DE_EMPLEADO
                                        .getValue());
    }

    /**
     * 
     * Carga la lista listaFuncionario
     *
     * DOCUMENTACION ADICIONAL
     */
    public void cargarListaFuncionarioE() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AreasmisionalesControladorUrlEnum.URL543
                                                        .getValue());
        listaFuncionarioE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        AreasmisionalesControladorEnum.ID_DE_EMPLEADO
                                        .getValue());
    }

    // </ME S_CARGAR_LISTA>
    // <ME S_BOTONES>
    // </ME S_BOTONES>
    // <ME S_CAMBIAR>
    // </ME S_CAMBIAR>
    // <ME S_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuncionario
     *
     * DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuncionario(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUNCIONARIO",
                        registroAux.getCampos().get(
                                        AreasmisionalesControladorEnum.ID_DE_EMPLEADO
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuncionario
     *
     * DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuncionarioE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
                        .toString();
    }

    // </ME S_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1612-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name log
         * "Ingresó a Datos Basicos, Áreas Misionales" End Sub
         *//*
           * FR1612-AL_ABRIR Private Sub Form_Load() Me.Caption =
           * NombreEmpresa(0) & ".Área Misional" End Sub
           */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado DOCUMENTACION ADICIONAL
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        long consecutivo = 0;
        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            consecutivo = ejbSysmanUtil
                            .generarSiguienteConsecutivo("VI_AREAMISIONAL",
                                            SysmanFunciones.concatenar(
                                                            "COMPANIA=''",
                                                            compania, "''"),
                                            "CODAMISIONAL");

            registro.getCampos().put("CODAMISIONAL", SysmanFunciones.padl(
                            String.valueOf(consecutivo),
                            2, "0"));
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
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
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
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
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
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
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
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
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
     * DOCUMENTACION ADICIONAL
     * 
     * @return VARIABLE
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
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFuncionario
     * 
     * @return listaFuncionario
     */
    public RegistroDataModelImpl getListaFuncionario() {
        return listaFuncionario;
    }

    /**
     * Asigna la lista listaFuncionario
     * 
     * @param listaFuncionario
     * Variable a asignar en listaFuncionario
     */
    public void setListaFuncionario(RegistroDataModelImpl listaFuncionario) {
        this.listaFuncionario = listaFuncionario;
    }

    /**
     * Retorna la lista listaFuncionario
     * 
     * @return listaFuncionario
     */
    public RegistroDataModelImpl getListaFuncionarioE() {
        return listaFuncionarioE;
    }

    /**
     * Asigna la lista listaFuncionario
     * 
     * @param listaFuncionario
     * Variable a asignar en listaFuncionario
     */
    public void setListaFuncionarioE(RegistroDataModelImpl listaFuncionarioE) {
        this.listaFuncionarioE = listaFuncionarioE;
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
