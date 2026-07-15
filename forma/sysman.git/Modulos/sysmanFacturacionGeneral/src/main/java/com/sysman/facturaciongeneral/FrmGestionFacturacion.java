/*-
 * FrmGestionFacturacion.java
 *
 * 1.0
 * 
 * 17/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmGestionFacturacionUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.RespuestaCuerpoRangoFacturacion;
import com.sysman.util.rest.RespuestaRangoFacturacion;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que permite administracr los rangos de facturacion
 *
 * @version 1.0, 17/12/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmGestionFacturacion extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String usuario;

    private String nitCompania;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable que almacena la url del servicio de FRIDA
     */
    private String url;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmGestionFacturacion
     */
    public FrmGestionFacturacion() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        try {
            // 2222
            numFormulario = GeneralCodigoFormaEnum.FRM_GESTION_FACTURACION_CONTROLADOR
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
        enumBase = GenericUrlEnum.DET_RANGO_FACT;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Nuevo en la vista
     *
     *
     */
    public void oprimirNuevo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Actualizar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirActualizar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
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

        if (nitCompania.contains("-")) {
            int fin = nitCompania.indexOf("-");
            nitCompania = nitCompania.substring(0, fin);
        }

        cargarDatosServicio();

        // </CODIGO_DESARROLLADO>
    }

    private void borrarDatosTabla() {
        Map<String, Object> params = new TreeMap<>();

        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmGestionFacturacionUrlEnum.URL8521
                                                        .getValue());

        try {
            requestManager.delete(urlDelete.getUrl(), params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cargarDatosServicio() {
        try {
            borrarDatosTabla();

            url = ejbSysmanUtil.consultarParametro(compania,
                            "URL SERVICIO REST", "69", new Date(), false);

            if (SysmanFunciones.validarVariableVacio(url)) {
                JsfUtil.agregarMensajeAlerta(
                                "Asegurese de configurar el parametro URL SERVICIO REST");
            }
            else {
                String respuesta;
                APIFrida api = new APIFrida();

                respuesta = api.cargarRangoFacturacion(nitCompania, url);

                Gson gson = new Gson();
                RespuestaRangoFacturacion respuestaApi = gson.fromJson(
                                respuesta,
                                RespuestaRangoFacturacion.class);

                for (RespuestaCuerpoRangoFacturacion respuestaCuerpoRangoFacturacion : respuestaApi
                                .getCuerpo()) {
                    insertarRangos(respuestaCuerpoRangoFacturacion);

                }

                reasignarOrigen();
            }
        }
        catch (SystemException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private void insertarRangos(
        RespuestaCuerpoRangoFacturacion respuestaCuerpoRangoFacturacion) {

        String urlEnumId = GenericUrlEnum.DET_RANGO_FACT
                        .getCreateKey();

        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

        Map<String, Object> params = new TreeMap<>();
        try {

            Date fechaDesde = formato.parse(
                            respuestaCuerpoRangoFacturacion
                                            .getFechadesde()
                                            .replace("-", "/"));

            Date fechaHasta = formato.parse(
                            respuestaCuerpoRangoFacturacion
                                            .getFechahasta()
                                            .replace("-", "/"));

            formato.applyPattern(SysmanFunciones.FORMATO_FECHA_ESTANDAR);

            params.put("ID", respuestaCuerpoRangoFacturacion.getId());

            params.put("PREFIJO", respuestaCuerpoRangoFacturacion.getPrefijo());

            params.put("NUMERO_RES",
                            respuestaCuerpoRangoFacturacion
                                            .getNumeroresolucion());
            params.put("CLAVE_TEC",
                            respuestaCuerpoRangoFacturacion.getClavetecnica());
            params.put("RANGO_INI",
                            respuestaCuerpoRangoFacturacion.getRangoinicial());
            params.put("RANGO_FIN",
                            respuestaCuerpoRangoFacturacion.getRangofinal());

            params.put("FECHADESDE",
                            formato.format(fechaDesde));

            params.put("FECHAHASTA",
                            formato.format(fechaHasta));

            params.put("TIPOAMBIENTE",
                            respuestaCuerpoRangoFacturacion
                                            .getIdFeTipoAmbiente());

            params.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
            params.put(GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            params);
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        reasignarOrigen();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

    }

    @Override
    public boolean insertarAntes() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date fechaDesde = (Date) registro.getCampos().get("FECHADESDE");

            Date fechaHasta = (Date) registro.getCampos().get("FECHAHASTA");
            APIFrida api = new APIFrida();
            api.postRangoFacturacion(url, nitCompania,
                            registro.getCampos().get("PREFIJO").toString(),
                            registro.getCampos().get("NUMERO_RES").toString(),
                            registro.getCampos().get("CLAVE_TEC").toString(),
                            registro.getCampos().get("RANGO_INI").toString(),
                            registro.getCampos().get("RANGO_FIN").toString(),
                            simpleDateFormat.format(fechaDesde),
                            simpleDateFormat.format(fechaHasta),
                            registro.getCampos().get("TIPOAMBIENTE")
                                            .toString());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cargarDatosServicio();
        return false;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean actualizarAntes() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {

            Date fechaDesde = (Date) registro.getCampos().get("FECHADESDE");

            Date fechaHasta = (Date) registro.getCampos().get("FECHAHASTA");
            APIFrida api = new APIFrida();
            api.putRangoFacturacion(url, nitCompania,
                            registro.getCampos().get("ID").toString(),
                            registro.getCampos().get("PREFIJO").toString(),
                            registro.getCampos().get("NUMERO_RES").toString(),
                            registro.getCampos().get("CLAVE_TEC").toString(),
                            registro.getCampos().get("RANGO_INI").toString(),
                            registro.getCampos().get("RANGO_FIN").toString(),
                            simpleDateFormat.format(fechaDesde),
                            simpleDateFormat.format(fechaHasta),
                            registro.getCampos().get("TIPOAMBIENTE")
                                            .toString());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            "MSM_REGISTRO_MODIFICADO"));

            reasignarOrigen();
        }
        catch (IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarDatosServicio();
        return false;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }
}
