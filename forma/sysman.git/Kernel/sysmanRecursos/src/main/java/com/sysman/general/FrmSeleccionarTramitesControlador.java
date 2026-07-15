/*-
 * FrmSeleccionarTramitesControlador.java
 *
 * 1.0
 * 
 * 10/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmSeleccionarTramitesControladorEnum;
import com.sysman.general.enums.FrmSeleccionarTramitesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesWorkflowEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 * Controlador de la forma: <code>frmseleccionartramite</code>.
 * Permite seleccionar un tramite a partir de un tipo de tramite y
 * proceso.
 *
 * @version 1.0, 10/05/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmSeleccionarTramitesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * accedio al formulario.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();

    /**
     * Constante a nivel de clase que aloja el codigo del menu desde
     * el cual se accede al formulario.
     */
    private final String menuActual = SessionUtil.getMenuActual();

    /**
     * Constante que almacena el codigo de la opcion de menu Monitor
     * de Historial de Tramite.
     */
    private static final String M_MONHISTRA = "350203";

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que contiene el codigo del proceso seleccionado en el
     * combo Proceso (CB5927).
     */
    private String proceso;

    /**
     * Variable que almacena el codigo del tipo de tramite
     * seleccionado en el combo Tipo Tramite (CB5928).
     */
    private String tipoTramite;

    /**
     * Variable que almacena el numero del tramite seleccionado en el
     * combo Tramite (CB5929).
     */
    private String tramite;

    /**
     * Variable que contiene el codigo que identifica el detalle del
     * tramite.
     */
    private String itemTramite;

    /**
     * Variable que contiene el codigo del nodo en el que se encuentra
     * el tramite (nodo actual).
     */
    private String nodoActual;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Variable que almacena la llave y parametros enviados desde el
     * formulario de tramites (1763).
     */
    private Map<String, Object> ridWorkflow;

    /**
     * Variable que almacena el contenido del archivo que se va a
     * enviar a workflow.
     */
    private StreamedContent streamedContentFile;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del combo Proceso (CB5927). */
    private List<Registro> listaProceso;

    /**
     * Lista que contiene los detalles del combo Tipo Tramite
     * (CB5928).
     */
    private List<Registro> listaTipoTramite;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los detalles del combo Tramite (CB5929). */
    private RegistroDataModelImpl listaTramite;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de FrmSeleccionarTramitesControlador
     */
    @SuppressWarnings("unchecked")
    public FrmSeleccionarTramitesControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1790
            numFormulario = GeneralCodigoFormaEnum.FRM_SELECCIONAR_TRAMITES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> paramIn = SessionUtil.getFlash();

            if (paramIn != null) {
                streamedContentFile = (StreamedContent) paramIn
                                .get(ConstantesWorkflowEnum.PR_ARCHIVODESCARGA
                                                .getValue());
            }

            /*-Aplica para accesos desde tramites de workflow*/
            ridWorkflow = (Map<String, Object>) SessionUtil
                            .getSessionVarContainer(
                                            ConstantesWorkflowEnum.PR_RID_TRAMITE
                                                            .getValue());
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
        cargarListaProceso();
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
        /*-Cargar valores por omision*/
        if (ridWorkflow != null) {
            proceso = ridWorkflow.get(
                            FrmSeleccionarTramitesControladorEnum.PR_PROCESO
                                            .getValue())
                            .toString();

            cargarListaTipoTramite();

            tipoTramite = ridWorkflow.get(
                            FrmSeleccionarTramitesControladorEnum.PR_TIPO_TRAMITE
                                            .getValue())
                            .toString();

            cargarListaTramite();

            tramite = ridWorkflow.get(
                            FrmSeleccionarTramitesControladorEnum.PR_TRAMITE
                                            .getValue())
                            .toString();

            itemTramite = recuperarValorLista(listaTramite,
                            GeneralParameterEnum.NUMERO.getName(), tramite,
                            FrmSeleccionarTramitesControladorEnum.DETALLE
                                            .getValue());

            nodoActual = recuperarValorLista(listaTramite,
                            GeneralParameterEnum.NUMERO.getName(), tramite,
                            FrmSeleccionarTramitesControladorEnum.NODO_ACTUAL
                                            .getValue());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: <code>listaProceso</code> asociada al combo
     * Proceso (CB5927).
     */
    public void cargarListaProceso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ESTADO.getName(), 4);
        param.put(GeneralParameterEnum.USUARIO.getName(), usuario);

        String urlEnum = M_MONHISTRA.equals(menuActual)
            ? FrmSeleccionarTramitesControladorUrlEnum.URL0002.getValue()
            : FrmSeleccionarTramitesControladorUrlEnum.URL004.getValue();

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(urlEnum)
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaTipoTramite</code> asociada al combo
     * Tipo Tramite (CB5928).
     */
    public void cargarListaTipoTramite() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmSeleccionarTramitesControladorEnum.PROCESO.getValue(),
                        proceso);

        param.put(GeneralParameterEnum.ESTADO.getName(), 4);
        param.put(GeneralParameterEnum.USUARIO.getName(), usuario);

        String urlEnum = M_MONHISTRA.equals(menuActual)
            ? FrmSeleccionarTramitesControladorUrlEnum.URL0001.getValue()
            : FrmSeleccionarTramitesControladorUrlEnum.URL005.getValue();

        try {
            listaTipoTramite = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(urlEnum)
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaTramite</code> asociada al combo
     * Tramite (CB5929). Cuando se accede al formulario desde la
     * opcion de menu: 350203 se listan todos los tramites, en caso
     * contrario solo los tramites con menu asignado.
     */
    public void cargarListaTramite() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmSeleccionarTramitesControladorEnum.PROCESO.getValue(),
                        proceso);

        param.put(GeneralParameterEnum.TIPO_TRAMITE.getName(), tipoTramite);
        param.put("ESTADO", 4); // Activo

        param.put("USUARIO", usuario);

        String urlEnum = M_MONHISTRA.equals(menuActual)
            ? FrmSeleccionarTramitesControladorUrlEnum.URL5085.getValue()
            : FrmSeleccionarTramitesControladorUrlEnum.URL001.getValue();

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);

        listaTramite = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Aceptar (BT3133) en la
     * vista. Redirecciona al Monitor de Historial de Tramites de
     * Workflow.
     */
    public void oprimirBtAceptar() {
        // <CODIGO_DESARROLLADO>
        if (M_MONHISTRA.equals(menuActual)) {
            abrirMonitorHistorial();
            return;
        }

        enviarAWorkflow(streamedContentFile);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar (BT3448) en la
     * vista. Cierra el formulario modal.
     */
    public void oprimirBtCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al seleccionar un item del combo Proceso
     * (CB5927).
     */
    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        tramite = tipoTramite = "";

        cargarListaTipoTramite();
        cargarListaTramite();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al seleccionar un item del combo Tipo Tramite
     * (CB5928).
     */
    public void cambiarTipoTramite() {
        // <CODIGO_DESARROLLADO>
        tramite = "";

        cargarListaTramite();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaTramite</code> asociada al combo Tramite (CB5929).
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTramite(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tramite = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO
                                                        .getName()),
                                        "")
                        .toString();

        itemTramite = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrmSeleccionarTramitesControladorEnum.DETALLE
                                                        .getValue()),
                                        "")
                        .toString();

        nodoActual = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrmSeleccionarTramitesControladorEnum.NODO_ACTUAL
                                                        .getValue()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    /**
     * Metodo por el cual se redirecciona al formulario:
     * FrmMonitorHistorialControlador (1786).
     */
    private void abrirMonitorHistorial() {
        Map<String, Object> param = new TreeMap<>();

        param.put(FrmSeleccionarTramitesControladorEnum.PR_PROCESO.getValue(),
                        proceso);

        param.put(FrmSeleccionarTramitesControladorEnum.PR_TIPO_TRAMITE
                        .getValue(), tipoTramite);

        param.put(FrmSeleccionarTramitesControladorEnum.PR_TRAMITE.getValue(),
                        tramite);

        Direccionador dir = new Direccionador();

        dir.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_MONITOR_HISTORIAL_CONTROLADOR
                                        .getCodigo()));
        dir.setParametros(param);

        RequestContext.getCurrentInstance().closeDialog(dir);
    }

    /**
     * Desencadena el proceso desde el cual se envia el documento
     * generado en el formulario a la etapa de la transaccion de
     * workflow.
     */
    private void enviarAWorkflow(StreamedContent archivoDescarga) {
        String nodoActualRuta = consultarRutaVarNodo(
                        ConstantesWorkflowEnum.S_DOC_ESTANDAR.getValue(),
                        nodoActual);

        Map<String, Object> keyAdjunto = new LinkedHashMap<>();

        keyAdjunto.put(FrmSeleccionarTramitesControladorEnum.KEY_TIPO_TRAMITE
                        .getValue(), tipoTramite);

        keyAdjunto.put(FrmSeleccionarTramitesControladorEnum.KEY_NUMERO_TRAMITE
                        .getValue(), tramite);

        keyAdjunto.put(FrmSeleccionarTramitesControladorEnum.KEY_CONSECUTIVO_TRAMITE
                        .getValue(), itemTramite);

        keyAdjunto.put(FrmSeleccionarTramitesControladorEnum.KEY_CODIGO_NODO_VARIABLE
                        .getValue(),
                        ConstantesWorkflowEnum.S_DOC_ESTANDAR.getValue());

        String[] vContentType = archivoDescarga.getContentType().split("/");

        /*-Recuperar extension archivo descarga.*/
        String extSCF = vContentType[vContentType.length - 1];

        String nombreArchivo = archivoDescarga.getName();

        int pos = nombreArchivo.indexOf(".".concat(extSCF));

        if (pos > 0) {
            nombreArchivo = nombreArchivo.substring(0, pos);
        }

        keyAdjunto.put(FrmSeleccionarTramitesControladorEnum.KEY_NAME_FILE
                        .getValue(), nombreArchivo);

        String fullRoot = JsfUtil.generarNombreArchivo(
                        Integer.toString(SysmanConstantes.MODULO_WORKFLOW),
                        keyAdjunto, nodoActualRuta, extSCF, "");

        JsfUtil.upload(archivoDescarga.getStream(), fullRoot);

        /*- Actualizar valor de la variable S_DOC_ESTANDAR*/
        Map<String, Object> param = new TreeMap<>();

        param.put(FrmSeleccionarTramitesControladorEnum.ADJUNTO.getValue(),
                        nodoActualRuta.concat(FilenameUtils.getName(fullRoot)));

        param.put(GeneralParameterEnum.MODIFIED_BY.getName(), usuario);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmSeleccionarTramitesControladorEnum.PROCESO.getValue(),
                        proceso);

        param.put(GeneralParameterEnum.TIPO_TRAMITE.getName(), tipoTramite);

        param.put(FrmSeleccionarTramitesControladorEnum.TRAMITE.getValue(),
                        tramite);

        param.put(FrmSeleccionarTramitesControladorEnum.D_TRAMITE.getValue(),
                        itemTramite);

        param.put(FrmSeleccionarTramitesControladorEnum.NODO.getValue(),
                        nodoActual);

        param.put(FrmSeleccionarTramitesControladorEnum.VARIABLE.getValue(),
                        ConstantesWorkflowEnum.S_DOC_ESTANDAR.getValue());

        Parameter parameter = new Parameter();
        parameter.setFields(param);

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSeleccionarTramitesControladorUrlEnum.URL002
                                                        .getValue());

        try {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4171"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo utilizado para recuperar el valor de un campo en una
     * lista de registros.
     * 
     * @param list
     * -> Lista que contiene los registros.
     * @param clave
     * -> Nombre de la clave por la que se va a identificar el
     * registro.
     * @param claveValor
     * ->Valor asociado a la clave que identifica el registro en la
     * lista.
     * @param campo
     * -> Nombre del campo del cual se va a recuperar el valor.
     * @return EL valor del campo en el registro.
     */
    private String recuperarValorLista(RegistroDataModelImpl list,
        String clave, Object claveValor, String campo) {
        Map<String, Object> param = new TreeMap<>();
        param.put(clave, claveValor);

        Registro auxReg = null;

        try {
            auxReg = list.getRegistroUnico(param);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return auxReg == null ? ""
            : SysmanFunciones.nvl(auxReg.getCampos().get(campo), "").toString();
    }

    /**
     * Retorna la ruta relativa al nodo donde se ubicara el documento
     * asociado a la variable.
     * 
     * @param var
     * ->Codigo de la variable.
     * @param nodo
     * -> Codigo del nodo.
     * @return -> La ruta relativa al nodo del documento adjunto.
     */
    private String consultarRutaVarNodo(String var, String nodo) {
        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA", compania);
        param.put("PROCESO", proceso);
        param.put("NODO", nodo);
        param.put("VARIABLE", var);

        Registro regAux = null;

        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmSeleccionarTramitesControladorUrlEnum.URL003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return regAux == null ? ""
            : regAux.getCampos().get("ADJUNTO").toString();
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable tipoTramite
     * 
     * @return tipoTramite
     */
    public String getTipoTramite() {
        return tipoTramite;
    }

    /**
     * Asigna la variable tipoTramite
     * 
     * @param tipoTramite
     * Variable a asignar en tipoTramite
     */
    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    /**
     * Retorna la variable tramite
     * 
     * @return tramite
     */
    public String getTramite() {
        return tramite;
    }

    /**
     * Asigna la variable tramite
     * 
     * @param tramite
     * Variable a asignar en tramite
     */
    public void setTramite(String tramite) {
        this.tramite = tramite;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public Map<String, Object> getRidWorkflow() {
        return ridWorkflow;
    }

    public void setRidWorkflow(Map<String, Object> ridWorkflow) {
        this.ridWorkflow = ridWorkflow;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso() {
        return listaProceso;
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

    /**
     * Retorna la lista listaTipoTramite
     * 
     * @return listaTipoTramite
     */
    public List<Registro> getListaTipoTramite() {
        return listaTipoTramite;
    }

    /**
     * Asigna la lista listaTipoTramite
     * 
     * @param listaTipoTramite
     * Variable a asignar en listaTipoTramite
     */
    public void setListaTipoTramite(List<Registro> listaTipoTramite) {
        this.listaTipoTramite = listaTipoTramite;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTramite() {
        return listaTramite;
    }

    public void setListaTramite(RegistroDataModelImpl listaTramite) {
        this.listaTramite = listaTramite;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
