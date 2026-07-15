/*-
 * FrmCargarDatosPredialControlador.java
 *
 * 1.0
 * 
 * 14/05/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.facturaciongeneral.enums.FrmCargarDatosPredialControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanException;
import com.sysman.util.rest.APIDeudaPredio;
import com.sysman.util.rest.RespuestaDeudaDetalles;
import com.sysman.util.rest.RespuestaDeudaPredio;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite hacer la financiacion de los acuerdos de
 * predial
 *
 * @version 1.0, 14/05/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmCargarDatosPredialControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo de la del
     * usuario que inicia sesion
     */
    private final String usuario;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable encargada de almacenar temporalmente el ano de cobro
     * de ingreso al modulo
     */
    private String anio;

    /**
     * Variable encargada de almacenar temporalmente el tipo de cobro
     * de ingreso al modulo
     */
    private String tipoCobro;

    /**
     * /** Variable que almacena si tiene o no descuentola
     * financiacion
     */
    private boolean descuentoLey;
    /**
     * Variable que almacena el codigo del predio
     */
    private String codigoPredio;

    /**
     * Constante que identifica el servicio que busca la URL y tipo de
     * conexión
     */
    private static final String SERVICIO_API = "1710001";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Variable que almacena el tercero que vienen en el objeto JSON
     */
    private String tercero;

    /**
     * Variable que almacena la sucursal del tercero que vienen en el
     * objeto JSON
     */

    private String sucursal;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga la informacion de los predios
     */
    private RegistroDataModelImpl listaDatosPredio;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFacturacionGeneralCero;

    /**
     * Crea una nueva instancia de FrmCargarDatosPredialControlador
     */
    public FrmCargarDatosPredialControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());

        anio = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());

        SessionUtil.cleanFlash();
        try {
            // 2173
            numFormulario = GeneralCodigoFormaEnum.FRM_CARGAR_DATOS_PREDIAL_CONTROLADOR
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDatosPredio();
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
        borrarDatosTabla();
        // </CODIGO_DESARROLLADO>
    }

    private void borrarDatosTabla() {
        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCargarDatosPredialControladorUrlEnum.URL6284
                                                        .getValue());

        try {
            requestManager.delete(urlDelete.getUrl(), params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaDatosPredio
     *
     */
    public void cargarListaDatosPredio() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmCargarDatosPredialControladorUrlEnum.URL4625
                                                        .getValue());

        Map<String, Object> params = new TreeMap<>();

        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put("CODIGO_PREDIO", codigoPredio);

        try {
            listaDatosPredio = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), params,
                            false, CacheUtil.getLlaveServicio(urlConexionCache,
                                            "TEMPORALDEUDAPREDIO"),
                            true);

        }
        catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Financiar en la vista
     *
     *
     */
    public void oprimirFinanciar() {
        // <CODIGO_DESARROLLADO>
        int i = 0;
        if (validarSeleccionados()) {
            String url;
            try {
                url = armarUrl("70");

                int[] aniosSeleccionados = new int[listaDatosPredio
                                .getSeleccionados().size()];
                for (Registro r : listaDatosPredio.getSeleccionados()) {

                    aniosSeleccionados[i] = Integer.parseInt(r.getCampos()
                                    .get(GeneralParameterEnum.ANO.getName())
                                    .toString());

                    i++;
                }

                APIDeudaPredio api = new APIDeudaPredio();

                api.financiar("2", codigoPredio,
                                aniosSeleccionados, descuentoLey ? -1 : 0,
                                usuario,
                                url);

                realizarFacturacion();

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
            catch (SysmanException | IOException e) {

                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        // </CODIGO_DESARROLLADO>
    }

    private void realizarFacturacion() {

        StringBuilder aniosSeleccionados = new StringBuilder();
        String aniosFinanciar;

        for (Registro r : listaDatosPredio.getSeleccionados()) {
            aniosSeleccionados.append(",").append(
                            r.getCampos().get(
                                            GeneralParameterEnum.ANO.getName())
                                            .toString());
        }

        aniosFinanciar = aniosSeleccionados.toString().replaceFirst(",", "");

        try {
            ejbFacturacionGeneralCero.financiarPredial(compania,
                            Integer.parseInt(anio), tipoCobro,
                            codigoPredio, aniosFinanciar, tercero, sucursal,
                            usuario);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarSeleccionados() {

        if (listaDatosPredio.getSeleccionados().isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2848"));
            return false;
        }

        return true;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cargar en la vista
     *
     */

    public void oprimirCargar() {

        try {
            String url = armarUrl("69");
            String respuesta;
            APIDeudaPredio api = new APIDeudaPredio();

            respuesta = api.cargarDatos("2", codigoPredio,
                            descuentoLey ? -1 : 0,
                            usuario, url);

            Gson gson = new Gson();
            RespuestaDeudaPredio respuestaApi = gson.fromJson(
                            respuesta,
                            RespuestaDeudaPredio.class);

            insertarTercero(respuestaApi);

            for (RespuestaDeudaDetalles respuestaDeudaDetalles : respuestaApi
                            .getCuerpo().getDetalles()) {

                insertarConceptos(respuestaDeudaDetalles);

            }

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

            cargarListaDatosPredio();
        }
        catch (SysmanException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void insertarTercero(RespuestaDeudaPredio respuestaApi) {

        Map<String, Object> params = new TreeMap<>();
        try {

            tercero = respuestaApi.getCuerpo().getNit();
            sucursal = respuestaApi.getCuerpo().getSucursal();

            params.put(GeneralParameterEnum.COMPANIA.getName(),
                            respuestaApi.getCuerpo().getCompania());
            params.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
            params.put(GeneralParameterEnum.CIUDAD.getName(),
                            respuestaApi.getCuerpo().getCiudad());
            params.put(GeneralParameterEnum.NIT.getName(), tercero);
            params.put("TIPOID", respuestaApi.getCuerpo().getTipodoc());
            params.put(GeneralParameterEnum.NOMBRE.getName(),
                            respuestaApi.getCuerpo().getNombre());

            params.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
            params.put(GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmCargarDatosPredialControladorUrlEnum.URL8624
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @SuppressWarnings("unchecked")
    private void insertarConceptos(
        RespuestaDeudaDetalles respuestaDeudaDetalles) {

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> result;
        try {
            result = mapper.readValue(
                            respuestaDeudaDetalles.getConceptos()
                                            .toString(),
                            Map.class);

            for (Map.Entry<String, Object> conceptos : result
                            .entrySet()) {

                String urlEnumId = GenericUrlEnum.TEMPORALDEUDAPREDIO
                                .getCreateKey();

                Map<String, Object> params = new TreeMap<>();

                params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                params.put("CODIGO_PREDIO", codigoPredio);
                params.put(GeneralParameterEnum.ANO.getName(),
                                respuestaDeudaDetalles
                                                .getAno());
                params.put(GeneralParameterEnum.CONCEPTO.getName(),
                                conceptos.getKey());
                params.put("TOTAL_DEUDA", respuestaDeudaDetalles
                                .getTotal());
                params.put("VALOR_CONCEPTO", conceptos.getValue());

                params.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
                params.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(urlEnumId);

                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                params);
            }
        }
        catch (SystemException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    private String armarUrl(String servicio) throws SysmanException {

        String url = "";

        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametros.put(GeneralParameterEnum.CODIGO.getName(), servicio);

        Registro rs = new Registro();
        RequestManager requestManager = new RequestManager();
        try {
            rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getUrlBeanById(SERVICIO_API)
                                            .getUrl(),
                            parametros));
        }
        catch (NullPointerException | SystemException e) {
            throw new SysmanException(idioma.getString("TB_TB4230"));

        }
        if (rs == null) {
            throw new SysmanException(idioma.getString("TB_TB4232"));
        }
        else if (rs.getCampos().get(GeneralParameterEnum.URL.getName())
                        .toString() == null) {
            throw new SysmanException(idioma.getString("TB_TB4231"));
        }
        url = rs.getCampos().get(GeneralParameterEnum.URL.getName())
                        .toString();
        return url;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDatosPredio
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDatosPredio(SelectEvent event) {
        // METODO_NO_IMPLEMEnTADO

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoPredio
     * 
     * @return codigoPredio
     */
    public String getCodigoPredio() {
        return codigoPredio;
    }

    /**
     * Asigna la variable codigoPredio
     * 
     * @param codigoPredio
     * Variable a asignar en codigoPredio
     */
    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public boolean isDescuentoLey() {
        return descuentoLey;
    }

    public void setDescuentoLey(boolean descuentoLey) {
        this.descuentoLey = descuentoLey;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDatosPredio
     * 
     * @return listaDatosPredio
     */
    public RegistroDataModelImpl getListaDatosPredio() {
        return listaDatosPredio;
    }

    /**
     * Asigna la lista listaDatosPredio
     * 
     * @param listaDatosPredio
     * Variable a asignar en listaDatosPredio
     */
    public void setListaDatosPredio(RegistroDataModelImpl listaDatosPredio) {
        this.listaDatosPredio = listaDatosPredio;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
