/*-
 * FrmAsignarTramitesControlador.java
 *
 * 1.0
 * 
 * 05/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.ejb.EjbWorkflowCeroRemote;
import com.sysman.workflow.enums.FrmAsignarTramitesControladorEnum;
import com.sysman.workflow.enums.FrmAsignarTramitesControladorUrlEnum;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import co.com.sysman.colas.procesador.EnumParamProcesador;
import co.com.sysman.comun.excepcion.NegocioExcepcion;
import co.com.sysman.comun.patron.procesador.Procesador;

/**
 * Controlador de la forma: <code>frmasignartramite</code>.
 *
 * @version 1.0, 05/06/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmAsignarTramitesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que contiene el codigo del rol seleccionado en el
     * combo Rol (CB6013).
     */
    private String rol;

    /**
     * Variable que contiene el codigo del usuario seleccionado en el
     * combo Usuario (CB6014).
     */
    private String usuarioEjecutor;

    /**
     * Variable que contiene el usuario interno actual del tramite.
     */
    private String usuarioTramite;

    /** Variable que contiene el codigo del proceso. */
    private String proceso;

    /** Variable que contiene le nombre del proceso. */
    private String procesoNom;

    /** Variable que contiene el codigo del tipo de tramite. */
    private String tipoTramite;

    /** Variable que contiene el nombre del tipo de tramite. */
    private String tipoTramiteNom;

    /** Variable que contiene el numero del tramite. */
    private String tramite;

    /** Variable que contiene el codigo del detalle del tramite. */
    private String dTramite;

    /**
     * Variable que almacena el codigo del nodo en el que se encuentra
     * el tramite.
     */
    private String nodoActual;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del combo Rol (CB6013). */
    private List<Registro> listaCbRol;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los detalles del combo Usuario (CB6014). */
    private RegistroDataModelImpl listaCbUsuario;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_EJBs>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_WORKFLOW</code>.
     */
    @EJB
    private EjbWorkflowCeroRemote ejbWorkflowCero;

    // </DECLARAR_EJBs>

    @Inject
    Procesador<Map<String, Object>, String> procesador;

    /**
     * Crea una nueva instancia de FrmAsignarTramitesControlador
     */
    public FrmAsignarTramitesControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1811
            numFormulario = GeneralCodigoFormaEnum.FRM_ASIGNAR_TRAMITES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> paramIn = SessionUtil.getFlash();

            if (paramIn != null) {
                proceso = recuperarValorCampo(paramIn,
                                FrmAsignarTramitesControladorEnum.PR_PROCESO);

                procesoNom = recuperarValorCampo(paramIn,
                                FrmAsignarTramitesControladorEnum.PR_PROCESO_NOM);

                tipoTramite = recuperarValorCampo(paramIn,
                                FrmAsignarTramitesControladorEnum.PR_TIPO_TRAMITE);

                tipoTramiteNom = recuperarValorCampo(paramIn,
                                FrmAsignarTramitesControladorEnum.PR_TIPO_TRAMITE_NOM);

                tramite = recuperarValorCampo(paramIn,
                                FrmAsignarTramitesControladorEnum.PR_TRAMITE);

                dTramite = recuperarValorCampo(paramIn,
                                FrmAsignarTramitesControladorEnum.PR_D_TRAMITE);

                nodoActual = recuperarValorCampo(paramIn,
                                FrmAsignarTramitesControladorEnum.PR_NODO_ACTUAL);

                usuarioTramite = recuperarValorCampo(paramIn,
                                FrmAsignarTramitesControladorEnum.PR_USUARIO_INT_TRAMITE);
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
        // <CARGAR_LISTA>
        cargarListaCbRol();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
        if (listaCbRol.size() == 1) {
            rol = listaCbRol.get(0).getCampos().get("CODIGO_ROL").toString();

            cargarListaCbUsuario();
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: <code>listaCbRol</code> asociada al combo Rol
     * (CB6013).
     */
    public void cargarListaCbRol() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("PROCESO", proceso);
        param.put("NODO", nodoActual);
        param.put("RACI", 10); // Ejecutor RACI
        param.put(GeneralParameterEnum.ESTADO.getName(), 4); /*-Estado Activo*/

        try {
            listaCbRol = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAsignarTramitesControladorUrlEnum.URL4180
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaCbUsuario</code> asociada al combo
     * Usuario (CB6014).
     */
    public void cargarListaCbUsuario() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ROL", rol);
        param.put("USUARIO_TRAMITE", usuarioTramite);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmAsignarTramitesControladorUrlEnum.URL4563
                                                        .getValue());

        try {
            String[] rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
                            GenericUrlEnum.ROL_USUARIO.getTable());

            listaCbUsuario = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            rowKey);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Aceptar (BT3178) en la
     * vista. Desencadena el proceso para cambiar el ejecutor.
     */
    public void oprimirBtAceptar() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbWorkflowCero.cambiarEjecutor(compania, proceso, tipoTramite,
                            new BigInteger(tramite), new BigInteger(dTramite),
                            usuarioEjecutor, usuario);

            informar("A");
        }
        catch (SystemException | NegocioExcepcion e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        RequestContext.getCurrentInstance().closeDialog(usuarioEjecutor);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /** Metodo ejecutado al cambiar el item del combo Rol (CB6013). */
    public void cambiarCbRol() {
        // <CODIGO_DESARROLLADO>
        usuarioEjecutor = "";

        cargarListaCbUsuario();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaCbUsuario</code> asociada al combo Usuario (CB6014).
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCbUsuario(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        usuarioEjecutor = SysmanFunciones
                        .nvl(registroAux.getCampos().get("USUARIO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    /**
     * Metodo utilizado para recuperar el valor de un campo en una
     * coleccion.
     * 
     * @param col
     * -> Referencia de la coleccion.
     * @param clave
     * -> Enumerado que contiene la clave del campo asociado a la
     * coleccion.
     * @return El valor del campo.
     */
    private String recuperarValorCampo(Map<String, Object> col,
        FrmAsignarTramitesControladorEnum clave) {
        return col.get(clave.getValue()).toString();
    }

    /**
     * Metodo que desencadena el proceso por el cual se informa
     * mediante correo electronico el pase del tramite al ejecutor.
     * 
     * @param raci
     * -> Tipo de usuarios a los que se debe informar.
     * <li>R: Responsable
     * <li>A: Ejecutor
     * <li>C: Consultor
     * <li>I: Informado
     * @throws NegocioExcepcion
     */
    private void informar(String raci) throws NegocioExcepcion {
        String emails = "";
        String asunto = "";
        String cuerpo = "";

        switch (raci) {
        case "A":
            emails = obtenerEmails(usuario);
            asunto = idioma.getString("TB_TB4126");
            cuerpo = idioma.getString("TB_TB4127")
                            .replace("#tipoTramiteNom#", tipoTramiteNom)
                            .replace("#tipoTramite#", tipoTramite)
                            .replace("#procesoNom#", procesoNom)
                            .replace("#proceso#", proceso)
                            .replace("#tramite#", tramite);
            break;
        case "I":
            break;
        default:
            break;
        }

        Map<String, Object> contexto = new HashMap<>();

        contexto.put(EnumParamProcesador.KEY_TIPO_RECEPCION.name(),
                        EnumParamProcesador.KEY_EMAIL.name());

        contexto.put(EnumParamProcesador.KEY_ASUNTO.name(),
                        asunto);

        contexto.put(EnumParamProcesador.KEY_CUERPO_CORREO.name(),
                        cuerpo);

        contexto.put(EnumParamProcesador.KEY_DESTINO.name(),
                        emails);

        procesador.setContexto(contexto);

        procesador.ejecutar();
    }

    /**
     * Metodo utilizado para obtener una secuencia de correos
     * electronicos separados por coma (,) de un listado de usuarios.
     * 
     * @param usuarios
     * -> Listado de usuarios separados por coma (,).
     * @return Secuencia de correos electronicos separados por coma
     * (,).
     */
    private String obtenerEmails(String usuarios) {
        Map<String, Object> param = new TreeMap<>();
        param.put("USUARIO", usuarios);

        StringBuilder emails = new StringBuilder();

        try {
            List<Registro> list = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmAsignarTramitesControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));

            for (int i = 0; i < list.size(); i++) {
                emails.append(list.get(i).getCampos().get("CORREOELECTRONICO")
                                .toString());

                if (i < list.size() - 1) {
                    emails.append(',');
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return emails.toString();
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable rol
     * 
     * @return rol
     */
    public String getRol() {
        return rol;
    }

    /**
     * Asigna la variable rol
     * 
     * @param rol
     * Variable a asignar en rol
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getUsuarioEjecutor() {
        return usuarioEjecutor;
    }

    public void setUsuarioEjecutor(String usuarioEjecutor) {
        this.usuarioEjecutor = usuarioEjecutor;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCbRol
     * 
     * @return listaCbRol
     */
    public List<Registro> getListaCbRol() {
        return listaCbRol;
    }

    /**
     * Asigna la lista listaCbRol
     * 
     * @param listaCbRol
     * Variable a asignar en listaCbRol
     */
    public void setListaCbRol(List<Registro> listaCbRol) {
        this.listaCbRol = listaCbRol;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaCbUsuario() {
        return listaCbUsuario;
    }

    public void setListaCbUsuario(RegistroDataModelImpl listaCbUsuario) {
        this.listaCbUsuario = listaCbUsuario;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
