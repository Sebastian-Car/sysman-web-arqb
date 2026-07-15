/*-
 * PrepararplantillaControlador.java
 *
 * 1.0
 * 
 * 17/02/2020
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIPdf;
import com.sysman.workflow.enums.DTramiteVariablesControladorEnum;
import com.sysman.workflow.enums.FrmDTramitesControladorEnum;
import com.sysman.workflow.enums.PrepararplantillaControladorUrlEnum;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 17/02/2020
 * @author jalfonso
 */
@ManagedBean
@ViewScoped
public class PrepararplantillaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la
     * cual inicio sesion el usuario, el valor de esta constante es asignado en
     * el constructor a la variable de sesion correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     */
    private String codigoPlantilla;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>f
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     */
    private RegistroDataModelImpl listaprepararplantilla;
    private String proceso;
    private String tipoTramite;
    private String tramite;
    private String detalleTramite;
    private String nodo;
    private int codFormRedireccion;
    private Map<String, Object> ridTramite;
    private String modulo;
    private String nombrePlantilla;
    private Date fechaPlantilla;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PrepararplantillaControlador
     */
    public PrepararplantillaControlador() {

        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1763
            numFormulario = GeneralCodigoFormaEnum.PREPARARPLANTILLA
                            .getCodigo();

            validarPermisos();

            Map<String, Object> paramIn = SessionUtil.getFlash();

            if (paramIn != null) {
                proceso = paramIn
                                .get(DTramiteVariablesControladorEnum.PR_PROCESO
                                                .getValue())
                                .toString();

                tipoTramite = paramIn
                                .get(FrmDTramitesControladorEnum.PR_TIPOTRAMITE
                                                .getValue())
                                .toString();

                tramite = paramIn
                                .get(DTramiteVariablesControladorEnum.PR_TRAMITE
                                                .getValue())
                                .toString();

                nodo = paramIn.get(FrmDTramitesControladorEnum.PR_NODO_ORIGEN
                                .getValue()).toString();

                // codFormRedireccion = (int) paramIn
                // .get(DTramiteVariablesControladorEnum.PR_COD_FORM
                // .getValue());

                // ridTramite = (Map<String, Object>) paramIn
                // .get(FrmTramitesControladorEnum.PR_ROWKEY
                // .getValue());
            }

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del
     * Bean ha sido creado, en este se realizan las asignaciones iniciales
     * necesarias para la visualizacion del formulario, como son tablas,
     * origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaprepararplantilla();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
     * tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaprepararplantilla
     *
     * TODO DOCUMENTACION ADICIONAL
     */
    public void cargarListaprepararplantilla() {
        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA", compania);
        param.put("TIPO", "55");
        param.put("NODO", nodo);
        param.put("PROCESO", proceso);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrepararplantillaControladorUrlEnum.URL1035006
                                                        .getValue());
        listaprepararplantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT1203 en la vista
     *
     *
     */
    public void oprimirPreparar() {

        Map<String, Object> param = new HashMap<>();
        param.put("s$compania$s", compania);
        param.put("s$proceso$s", proceso);
        param.put("s$tipoTramite$s", tipoTramite);
        param.put("s$tramiteInicial$s", tramite);
        param.put("s$tramiteFinal$s", tramite);
        param.put("s$etapa$s", nodo);
        param.put("s$imagen$s",
                        urlFirmaImagenIdipron());
        param.put("s$usuario$s", SessionUtil.getUser().getCodigo());

        /* Reemplazos de la consulta asociada a la plantilla */
        SessionUtil.setSessionVar("variablesConsultaWord", param);

        String[] claves = new String[3];
        claves[0] = "codigoPlantilla";
        claves[1] = "fechaPlantilla";
        claves[2] = "nombreDocDescarga";

        String[] valores = new String[3];
        valores[0] = codigoPlantilla;

        valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);

        valores[2] = nombrePlantilla;

        // RequestContext.getCurrentInstance().closeDialog(null);

        SessionUtil.cargarModalDatosFlashCerrar(Integer
                        .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), claves, valores);

        // RequestContext.getCurrentInstance().closeDialog(null);

    }

    /**
     * M&eacute;todo que consume el servicio de idipron para obtener la url de
     * la firma
     * 
     * @return Retorna la url de la imagen dentor del servicio de idipron
     */

    private String urlFirmaImagenIdipron() {

        String rta = "";

        try {
            // Solo aplica para Idipron
            if (SessionUtil.getCompaniaIngreso().getNit().contains("899999333")
                && "SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "GENERA PLANTILLA PDF",
                                                modulo,
                                                new Date(),
                                                false), "NO"))) {
                // MZANGUNA(18/07) Dejar la Url desde una tabla o parametro no
                // quemada

                String url = "";

                url = ejbSysmanUtil.consultarParametro(
                                compania,
                                "URL FIRMAS IDIPRON",
                                modulo,
                                new Date(),
                                false);

                // String url =
                // "http://10.80.11.17:8080/CertificadosDigitales/ws/certificado/#cedula#";

                url = url.replace("#cedula#",
                                SessionUtil.getUser().getCedula());

                String respuesta = "";
                APIPdf api = new APIPdf();
                try {
                    respuesta = api.cargarItem(url);
                }
                catch (IOException | SysmanException e) {
                    logger.error("MZANGUNA. No hay conexion en el servicio "
                        + url);
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(respuesta);
                    JSONArray preguntas;
                    if (jsonObject.getString("codigo").equals("200")) {
                        preguntas = jsonObject.getJSONArray("data");
                        JSONObject object1 = preguntas.getJSONObject(0);
                        rta = object1
                                        .getString("ruta_imagen");
                    }
                }
                catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }

        }
        catch (SystemException e1) {

            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        return rta;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BT1202 en la vista
     *
     *
     */
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaprepararplantilla
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaprepararplantilla(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        codigoPlantilla = registroAux.getCampos().get("CODIGO").toString();
        nombrePlantilla = registroAux.getCampos().get("NOMBRE").toString();
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoPlantilla
     * 
     * @return codigoPlantilla
     */
    public String getCodigoPlantilla() {
        return codigoPlantilla;
    }

    /**
     * Asigna la variable codigoPlantilla
     * 
     * @param codigoPlantilla
     * Variable a asignar en codigoPlantilla
     */
    public void setCodigoPlantilla(String codigoPlantilla) {
        this.codigoPlantilla = codigoPlantilla;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaprepararplantilla
     * 
     * @return listaprepararplantilla
     */
    public RegistroDataModelImpl getListaprepararplantilla() {
        return listaprepararplantilla;
    }

    /**
     * Asigna la lista listaprepararplantilla
     * 
     * @param listaprepararplantilla
     * Variable a asignar en listaprepararplantilla
     */
    public void setListaprepararplantilla(
        RegistroDataModelImpl listaprepararplantilla) {
        this.listaprepararplantilla = listaprepararplantilla;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
