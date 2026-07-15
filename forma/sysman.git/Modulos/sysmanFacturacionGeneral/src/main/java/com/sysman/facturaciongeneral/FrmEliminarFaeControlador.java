/*-
 * FrmEliminarFaeControlador.java
 *
 * 1.0
 * 
 * 19/03/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.facturaciongeneral.enums.FacturacionconceptosControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmEliminarFaeUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.ParametroDeleteEnvioFactura;
import com.sysman.util.rest.RespuestaEnvioFactura;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que permite eliminar los documentos diligenciados en
 * frida
 *
 * @version 1.0, 19/03/2021
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmEliminarFaeControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private String nitCompania;
    private final String usuario;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Variable que almacena la url del servicio de FRIDA
     */
    private String url;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    
    @EJB
    private EjbFacturacionGeneralCuatroRemote ejbfacturacionCuatro;

    /**
     * Crea una nueva instancia de FrmEliminarFaeControlador
     */
    public FrmEliminarFaeControlador() {
        super();
        compania = SessionUtil.getCompania();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        usuario = SessionUtil.getUser().getCodigo();
        try {

            // 2252
            numFormulario = GeneralCodigoFormaEnum.FRM_ELIMINAR_FAE_CONTROLADOR
                            .getCodigo();
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.TEMP_BORRADO_FACTURAS;

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
     * Metodo ejecutado al oprimir el boton Eliminar
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirEliminar(Registro reg, int indice) {

        String tipoFormato = "";

        tipoFormato = "NC".equals(reg.getCampos().get("PREFIJO").toString())
            ? "02"
            : "ND".equals(reg.getCampos().get("PREFIJO").toString()) ? "03"
                : "01";

        try {

            Registro rs;

            rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FacturacionconceptosControladorUrlEnum.URL9457
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));

            if (rs != null) {

                File archivo = new File(
                                rs.getCampos().get(
                                                "RUTA_CERTIFICADO")
                                                .toString());

                String nombreCertificado = archivo
                                .getName();

                byte[] archivoBytes = Files
                                .readAllBytes(archivo
                                                .toPath());

                String certificado = Base64.getEncoder()
                                .encodeToString(archivoBytes);

                String passCertificado = Base64.getEncoder()
                                .encodeToString(rs
                                                .getCampos()
                                                .get("CONTRA_CERTIFICADO")
                                                .toString()
                                                .getBytes());

                ParametroDeleteEnvioFactura paramDelete = new ParametroDeleteEnvioFactura();

                paramDelete.setTipoFormato(tipoFormato);
                paramDelete.setNumFormato(
                                reg.getCampos().get("NUM_FACTURA").toString());
                paramDelete.setPrefijo(
                                reg.getCampos().get("PREFIJO").toString());
                paramDelete.setCertificado(certificado);
                paramDelete.setNombreCertificado(
                                nombreCertificado);
                paramDelete.setPassCertificado(
                                passCertificado);
                paramDelete.setNumDocumentoContribuyente(
                                nitCompania);

                Gson gson2 = new Gson();
                String json = gson2.toJson(paramDelete,
                                ParametroDeleteEnvioFactura.class);

                APIFrida apiFrida = new APIFrida();

                apiFrida.deleteEnvioFactura(url, json);

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));

                insertarDatos();

            }

        }
        catch (SystemException | IOException | SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

        insertarDatos();
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private void insertarDatos() {
        borrarDocumentos();

        try {
            url = ejbSysmanUtil.consultarParametro(compania,
                            "URL SERVICIO REST", "69", new Date(), false);

            if (SysmanFunciones.validarVariableVacio(url)) {
                JsfUtil.agregarMensajeAlerta(
                                "Asegurese de configurar el parametro URL SERVICIO REST");
            }
            else {

                String respuesta;
                APIFrida api = new APIFrida();

                respuesta = api.cargarEnvioFacatura(nitCompania, url);

                Gson gson = new Gson();
                RespuestaEnvioFactura respuestaApi = gson.fromJson(
                                respuesta,
                                RespuestaEnvioFactura.class);
                
              // INI MOD  JM CC 3425 le paso el json directo a un paquete para que no se tarde tanto 
                final DecimalFormat df = new DecimalFormat("#.####################################");
                df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));

                Gson gson2 = new GsonBuilder()
                    .registerTypeAdapter(Double.class, new TypeAdapter<Double>() {
                        @Override
                        public void write(JsonWriter out, Double value) throws IOException {
                            if (value == null) {
                                out.nullValue();
                            } else {
                                out.value(df.format(value));
                            }
                        }

                        @Override
                        public Double read(JsonReader in) throws IOException {
                            return in.nextDouble();
                        }
                    })
                    .create();
                

                String jsonString = gson2.toJson(respuestaApi.getCuerpo());
                
                String respuestapck = ejbfacturacionCuatro.actualizarTablaFacturas(jsonString.toString());
                
                // FIN JM CC 3425

                reasignarOrigen();
            }

        }
        catch (SystemException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void borrarDocumentos() {
        Map<String, Object> params = new TreeMap<>();

        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEliminarFaeUrlEnum.URL2564
                                                        .getValue());

        try {
            requestManager.delete(urlDelete.getUrl(), params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void insertarDocumentos(List<Object> datos) {
        Map<String, Object> params = new TreeMap<>();

        String urlEnumId = FrmEliminarFaeUrlEnum.URL4589.getValue();

        String descEstado = "";

        switch (datos.get(3).toString()) {
        case "000":
            descEstado = "PERSISTA";
            break;
        case "7200001":
            descEstado = "RECIBIDA";
            break;
        case "7200003":
            descEstado = "EN PROCESO DE VALIDACI�N";
            break;
        case "7200004":
            descEstado = "FALLIDA (Documento no cumple 1 o m�s validaciones de la DIAN)";
            break;
        default:
            descEstado = "";
            break;
        }

        try {

            params.put("NUM_FACTURA", new DecimalFormat(
                            "#.####################################")
                                            .format(datos.get(0)));
            params.put("PREFIJO", datos.get(1));
            params.put("ID_FACTURA", new DecimalFormat(
                            "#.####################################")
                                            .format(datos.get(2)));
            params.put("COD_ESTADO", datos.get(3));
            params.put("DESCRIPCION_ESTADO", descEstado);

            params.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
            params.put(GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId);

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            params);
        }
        catch (SystemException e) {
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

}
