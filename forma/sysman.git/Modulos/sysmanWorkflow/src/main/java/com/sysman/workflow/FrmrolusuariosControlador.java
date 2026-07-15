/*-
 * FrmrolusuariosControlador.java
 *
 * 1.0
 * 
 * 30/04/2018
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.enums.FrmRolUsuariosControladorEnum;
import com.sysman.workflow.enums.FrmRolUsuariosControladorUrlEnum;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma: frmrolusuario.
 *
 * @version 1.0, 30/04/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped

public class FrmrolusuariosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /** Atributo que contiene el codigo del Rol. */

    private String codigoRol;

    private String codigoUsuario;

    /** Atributo que contiene el Nombre Rol. */

    private final String consNombre;

    /** Atributo que contiene el Nombre Dependencia. */

    private final String consNombreDep;

    private String cCodigo = FrmRolUsuariosControladorEnum.DEPENDENCIA
                    .getValue();

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Combo grande que contiene los detalles del combo
     * listaDependencia (CB5915).
     */
    private RegistroDataModelImpl listaDependencia;
    /**
     * Combo grande que contiene los detalles del combo
     * listaDependencia (CB5915.
     */
    private RegistroDataModelImpl listaDependenciaE;
    /**
     * Combo grande que contiene los detalles del combo listaUsuario
     * (CB5914)
     */
    private RegistroDataModelImpl listaUsuario;
    /**
     * Combo grande que contiene los detalles del combo listaUsuarioE
     * (CB5914)
     */
    private RegistroDataModelImpl listaUsuarioE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    private Object nombreUsu;

    private Object nombreDep;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmrolusuariosControlador
     */
    public FrmrolusuariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consNombre = FrmRolUsuariosControladorEnum.NOMBRE.getValue();
        consNombreDep = FrmRolUsuariosControladorEnum.NOMBRE_DEPENDENCIA
                        .getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ROL_USUARIO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> paramEntrada = SessionUtil.getFlash();
            if (paramEntrada != null) {
                codigoRol = paramEntrada
                                .get(FrmRolUsuariosControladorEnum.PR_CODIGO_ROL
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
        enumBase = GenericUrlEnum.ROL_USUARIO;

        reasignarOrigen();
        buscarLlave();

        registro = new Registro();

        cargarListaDependencia();
        cargarListaDependenciaE();
        cargarListaUsuario();
        cargarListaUsuarioE();

        abrirFormulario();

    }

    private boolean cargarDependencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.USUARIO.getName(),
                        codigoUsuario);

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmRolUsuariosControladorUrlEnum.URL351
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null) {
                cCodigo = rs.getCampos().get(
                                FrmRolUsuariosControladorEnum.DEPENDENCIA
                                                .getValue())
                                .toString();
                return true;

            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4115"));
                return false;

            }

        }
        catch (SystemException e) {
            //
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;

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
                        FrmRolUsuariosControladorEnum.CODIGO_ROL.getValue(),
                        codigoRol);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaDependencia
     *
     * Carga el combo listaDependencia Asociada al combo Dependencia
     */
    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRolUsuariosControladorUrlEnum.URL255
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmRolUsuariosControladorEnum.CODIGO.getValue());

    }

    /**
     * 
     * Carga la lista listaDependencia
     *
     * Carga el combo listaDependencia Asociada al combo Dependencia.
     */
    public void cargarListaDependenciaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRolUsuariosControladorUrlEnum.URL255
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmRolUsuariosControladorEnum.CODIGO.getValue());

    }

    /**
     * 
     * Carga la lista listaUsuario
     *
     * Carga el combo listaDependencia Asociada al combo Usuario.
     */
    public void cargarListaUsuario() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRolUsuariosControladorUrlEnum.URL350
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put("TIPOCUENTA", "U");

        listaUsuario = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmRolUsuariosControladorEnum.CODIGO.getValue());

    }

    /**
     * 
     * Carga la lista listaUsuario
     *
     * Carga el combo listaUsuario Asociada al combo Usuario
     */
    public void cargarListaUsuarioE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRolUsuariosControladorUrlEnum.URL350
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        listaUsuarioE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        FrmRolUsuariosControladorEnum.CODIGO.getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Dependencia en la fila
     * seleccionada dentro de la grilla
     * 
     *
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarDependenciaC(int rowNum) {

        // <CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("DEPENDENCIA", auxiliar);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(consNombreDep, nombreDep);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Usuario en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarUsuarioC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("USUARIO", auxiliar);

        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(consNombre, nombreUsu);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
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
        registro.getCampos().put(
                        FrmRolUsuariosControladorEnum.DEPENDENCIA.getValue(),
                        registroAux.getCampos()
                                        .get(FrmRolUsuariosControladorEnum.CODIGO
                                                        .getValue()));
        registro.getCampos()
                        .put(FrmRolUsuariosControladorEnum.NOMBRE_DEPENDENCIA
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmRolUsuariosControladorEnum.NOMBRE
                                                                        .getValue()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(FrmRolUsuariosControladorEnum.CODIGO.getValue());
        nombreDep = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmRolUsuariosControladorEnum.NOMBRE
                                                        .getValue()),
                                        " ")
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUsuario
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUsuario(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmRolUsuariosControladorEnum.USUARIO.getValue(),
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put(
                        FrmRolUsuariosControladorEnum.NOMBRE.getValue(),
                        registroAux.getCampos()
                                        .get(FrmRolUsuariosControladorEnum.NOMBRE
                                                        .getValue()));

        registro.getCampos().put(
                        GeneralParameterEnum.DEPENDENCIA.getName(),
                        null);

        registro.getCampos().put(
                        FrmRolUsuariosControladorEnum.NOMBRE_DEPENDENCIA
                                        .getValue(),
                        null);

        codigoUsuario = registroAux.getCampos().get("CODIGO").toString();

        if (!cargarDependencia()) {

            return;
        }
        else {

            registro.getCampos().put(
                            GeneralParameterEnum.DEPENDENCIA.getName(),
                            cCodigo);

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            cCodigo);

            try {
                Registro rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmRolUsuariosControladorUrlEnum.URL352
                                                                                .getValue())
                                                .getUrl(), param));

                registro.getCampos().put(
                                FrmRolUsuariosControladorEnum.NOMBRE_DEPENDENCIA
                                                .getValue(),
                                rs.getCampos()
                                                .get(FrmRolUsuariosControladorEnum.NOMBRE
                                                                .getValue()));
            }
            catch (SystemException e) {

                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUsuario
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUsuarioE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");

        nombreUsu = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(FrmRolUsuariosControladorEnum.NOMBRE
                                                        .getValue()),
                                        " ")
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * 
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(
                        FrmRolUsuariosControladorEnum.CODIGO_ROL.getValue(),
                        codigoRol);
        registro.getCampos().remove(
                        FrmRolUsuariosControladorEnum.NOMBRE.getValue());
        registro.getCampos()
                        .remove(FrmRolUsuariosControladorEnum.NOMBRE_DEPENDENCIA
                                        .getValue());

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     * 
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("CODIGO_ROL", codigoRol);
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().remove("NOMBRE_DEPENDENCIA");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
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
        registro.getCampos().remove("CODIGO_ROL");
        registro.getCampos().remove("NOMBRE");
        registro.getCampos().remove("NOMBRE_DEPENDENCIA");
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
    }

    // <SET_GET_ATRIBUTOS>
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
     * Retorna la lista listaUsuario
     * 
     * @return listaUsuario
     */
    public RegistroDataModelImpl getListaUsuario() {
        return listaUsuario;
    }

    /**
     * Asigna la lista listaUsuario
     * 
     * @param listaUsuario
     * Variable a asignar en listaUsuario
     */
    public void setListaUsuario(RegistroDataModelImpl listaUsuario) {
        this.listaUsuario = listaUsuario;
    }

    /**
     * Retorna la lista listaUsuario
     * 
     * @return listaUsuario
     */
    public RegistroDataModelImpl getListaUsuarioE() {
        return listaUsuarioE;
    }

    /**
     * Asigna la lista listaUsuario
     * 
     * @param listaUsuario
     * Variable a asignar en listaUsuario
     */
    public void setListaUsuarioE(RegistroDataModelImpl listaUsuarioE) {
        this.listaUsuarioE = listaUsuarioE;
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
