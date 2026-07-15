/*-
 * FrmDocDianControlador.java
 *
 * 1.0
 * 
 * 7/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmDocDianControladorUrlEnum;
import com.sysman.facturaciongeneral.enums.FrmRangoProduccionDianUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbCodigoBarrasRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.ParametrosFormato;
import com.sysman.util.rest.ParametrosLegalizarFactura;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaConsultarReporte;
import com.sysman.util.rest.RespuestaFacturasReporte;
import com.sysman.util.rest.RespuestaFridaLegalizar;
import com.sysman.util.rest.RespuestaFridaLegalizarNotas;
import com.sysman.util.rest.RespuestaNotasReporte;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que envia las facturas a DIAN
 *
 * @version 1.0, 07/01/2021
 * @author eamaya
 * 
 * @version 1.1 26/08/2021
 * @author gfigueredo
 * Se ajusta la función {@link #oprimirEnviarFacturas()}, para
 * reiniciar las variables  pieCodigoBarra y pieTextoBarra en el ciclo for.
 * @see #oprimirEnviarFacturas()
 * 
 * @version 1.2, 06/10/2021
 * @author gfigueredo
 * Se crea la función {@link #crearReportesFridaNotas(String, String)}, para,
 * tomar los valores recibidos al consumir el servicio de legalizarFactura,
 * y extraer los datos de la etiqueta "resultadoProcesoDian"
 * @see #crearReportesFridaNotas(String, String)
 * 
 * @version 1.3, 02/11/2021
 * @author gfigueredo
 * Se ajusta la función {@link #crearReportesFridaNotas(String, String)}, debido
 * a que el resultado proceso dian en el JSON trae la información primero en ARRAY y luego en
 * OBJECT. 
 * @see #crearReportesFridaNotas(String, String)
 * 
 */
@ManagedBean
@ViewScoped
public class FrmDocDianControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String nitCompania;

    private String usuario;

    private String ano;

    private String tipoCobro;

    private String tipoFactRecaudo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbCodigoBarrasRemote ejbCodigoBarras;

    /**
     * Crea una nueva instancia de FrmDocDianControlador
     */
    public FrmDocDianControlador() {
        super();
        compania = SessionUtil.getCompania();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        usuario = SessionUtil.getUser().getCodigo();

        ano = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue())
                        .toString();
        tipoCobro = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue())
                        .toString();

        try {
            // 2229
            numFormulario = GeneralCodigoFormaEnum.FRM_DOC_DIAN_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            tipoFactRecaudo = SysmanFunciones.nvl(SessionUtil
                            .getSessionVarContainer(
                                            ConstantesFacturacionGenEnum.TIPOFACTURA_RECAUDO
                                                            .getValue()),
                            "0").toString();
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
        if (nitCompania.contains("-")) {
            int fin = nitCompania.indexOf("-");
            nitCompania = nitCompania.substring(0, fin);
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarNotas en la vista
     *
     *
     */
    public void oprimirEnviarNotas() {
        archivoDescarga = null;

        String url;
        String tipoformato;
        String codigoReporte;
        String prFormato;
        String log = "";

        log = "|---------------     ENVIO DIAN      ---------------|";

        try {
            url = ejbSysmanUtil.consultarParametro(compania,
                            "URL SERVICIO REST", "69", new Date(), false);

            if (SysmanFunciones.validarVariableVacio(url)) {
                JsfUtil.agregarMensajeAlerta(
                                "Asegurese de configurar el parametro URL SERVICIO REST");
            }
            else {
                prFormato = ejbSysmanUtil.consultarParametro(compania,
                                "SF FORMATO FACTURACION ELECTRONICA",
                                SessionUtil.getModulo(), new Date(), false);

                eliminarTabEstadoNotas();

                String respuesta;
                APIFrida api = new APIFrida();
                
//MROSERO CC 1394 11/04/2025
                respuesta = api.cargarFormatoConsultarReporte(url, nitCompania,
                                "10", "", "181",
                                SysmanFunciones.convertirAFechaCadena(
                                                SysmanFunciones.convertirAFecha(
                                                                "18/05/2020"),
                                                "yyyy/MM/dd"),
                                SysmanFunciones.convertirAFechaCadena(
                                                new Date(),
                                                "yyyy/MM/dd"),
                                "0", "1",
                                "1");

                Gson gson = new Gson();
                RespuestaConsultarReporte respuestaApi = gson.fromJson(
                                respuesta,
                                RespuestaConsultarReporte.class);

                for (RespuestaNotasReporte respuestaNotasReporte : respuestaApi
                                .getCuerpo().getNotas()) {

                    log = log + "\n" + insertarNotas(respuestaNotasReporte);

                }

                List<Registro> listaEstadoNotas = RegistroConverter
                                .toListRegistro(
                                                requestManager.getList(
                                                                UrlServiceUtil
                                                                                .getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmDocDianControladorUrlEnum.URL3562
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                null));

                if (!listaEstadoNotas.isEmpty()) {

                    for (Registro reg : listaEstadoNotas) {

                        tipoformato = "NC".equals(reg.getCampos().get(
                                        GeneralParameterEnum.CLASE.getName()))
                                            ? "02"
                                            : "03";

                        // consumirEalvarez

                        Map<String, Object> param2 = new TreeMap<>();

                        param2.put(GeneralParameterEnum.ANO.getName(),
                                        SysmanFunciones.ano(new Date()));
                        param2.put("PREFIJO", tipoCobro);

                        Registro rs = RegistroConverter
                                        .toRegistro(requestManager.get(
                                                        UrlServiceUtil.getInstance()
                                                                        .getUrlServiceByUrlByEnumID(
                                                                                        FrmDocDianControladorUrlEnum.URL665033
                                                                                                        .getValue())
                                                                        .getUrl(),
                                                        param2));

                        if (rs != null) {

                            codigoReporte = SysmanFunciones
                                            .nvl(rs.getCampos().get("FORMATO"),
                                                            "30002195")
                                            .toString();

                        }
                        else {
                            codigoReporte = prFormato;
                        }

                        Registro rsCertificado = RegistroConverter
                                        .toRegistro(requestManager.get(
                                                        UrlServiceUtil.getInstance()
                                                                        .getUrlServiceByUrlByEnumID(
                                                                                        FrmRangoProduccionDianUrlEnum.URL9457
                                                                                                        .getValue())
                                                                        .getUrl(),
                                                        null));

                        if (rsCertificado != null) {

                            File archivo = new File(
                                            rsCertificado.getCampos().get(
                                                            "RUTA_CERTIFICADO")
                                                            .toString());

                            String nombreCertificado = archivo.getName();

                            byte[] archivoBytes = Files
                                            .readAllBytes(archivo.toPath());

                            String certificado = Base64.getEncoder()
                                            .encodeToString(archivoBytes);

                            String passCertificado = Base64.getEncoder()
                                            .encodeToString(rsCertificado
                                                            .getCampos()
                                                            .get("CONTRA_CERTIFICADO")
                                                            .toString()
                                                            .getBytes());

                            ParametrosLegalizarFactura paramLegalizar = new ParametrosLegalizarFactura();

                            paramLegalizar.setTipoSalida(1);
                            paramLegalizar.setTestSetId(rsCertificado
                                            .getCampos().get("TES_ID")
                                            .toString());
                            paramLegalizar.setCodigoReporte(codigoReporte);

                            ParametrosFormato paramFormato = new ParametrosFormato();

                            paramFormato.setTipoFormato(tipoformato);

                            paramFormato.setNumFormato(reg
                                            .getCampos()
                                            .get("NUMFORMATO")
                                            .toString());

                            paramFormato.setPrefijo(reg.getCampos()
                                            .get(GeneralParameterEnum.CLASE
                                                            .getName())
                                            .toString());

                            paramFormato.setNombreCertificado(
                                            nombreCertificado);

                            paramFormato.setCertificado(certificado);

                            paramFormato.setPassCertificado(passCertificado);

                            paramFormato.setNumDocumentoContribuyente(
                                            nitCompania);

                            paramLegalizar.setParamFormato(paramFormato);

                            APIFrida apiFrida = new APIFrida();
                            String respuestaLegalizar = null;
                            Gson gson2 = new Gson();
                            /**
                             * 14/03/2023 - LJDIAZ - 7728209 - Se corrige captura de la excipon SysmanException, 
                             * ya que esta se crea desde el api de frida, con la respuesta de la dian 
                             * en caso tal de que la nota presente inconveientes con la dian que no 
                             * correspondan a errores
                             */
                            try {
                            	String json = gson2.toJson(paramLegalizar,
                                            ParametrosLegalizarFactura.class);
	                            respuestaLegalizar = apiFrida
	                                            .postFormatoLegalizar(url, json);
                            }catch (SysmanException e) {
                            	logger.error(e.getMessage(), e);
                                JsfUtil.agregarMensajeError(e.getMessage());
                                log = log + "\n"+" Respuesta nota: "+reg
                                        .getCampos()
                                        .get("NUMFORMATO")
                                        .toString()+": "+e.getMessage();
                            }
                            RespuestaApi respuestaApis = gson2.fromJson(
                                            respuestaLegalizar,
                                            RespuestaApi.class);

                            if (respuestaApis != null && respuestaApis.getCodigo() != 0) {
                                log = log + "\n" + respuestaApis.getMensaje()
                                    + " "
                                    + (tipoformato.equals("1") ? " FACTURA:"
                                        : " NOTA:")
                                    + reg
                                                    .getCampos()
                                                    .get("NUMFORMATO")
                                                    .toString()
                                    + ". Proceso terminado ";
                            }
                            else if (respuestaApis != null && respuestaApis.getCodigo() == 0){

                            	log = log + "\n" + crearReportesFridaNotas(respuestaLegalizar, reg
                                                .getCampos()
                                                .get("NUMFORMATO")
                                                .toString());
                            }
                        }

                    }
                }

            }

            ByteArrayInputStream streamTexto = JsfUtil
                            .serializarPlano(log);
            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                            "LogEnviarDian.txt");

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (SystemException | IOException | SysmanException
                        | ParseException | JRException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void eliminarTabEstadoNotas() {

        try {
            UrlBean urlDelete2 = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmDocDianControladorUrlEnum.URL1987
                                                            .getValue());

            requestManager.delete(urlDelete2.getUrl(), null);
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String insertarNotas(RespuestaNotasReporte respuestaNotasReporte) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

        try {

            String urlEnumId2 = FrmDocDianControladorUrlEnum.URL7456
                            .getValue();

            Date fecha = formato.parse(
                            respuestaNotasReporte.getFecha()
                                            .replace("-", "/"));

            Map<String, Object> params2 = new TreeMap<>();

            params2.put(GeneralParameterEnum.CLASE.getName(),
                            respuestaNotasReporte.getClase());
            params2.put("ESTADO", respuestaNotasReporte.getEstado());
            params2.put(GeneralParameterEnum.FECHA.getName(),
                            fecha);
            params2.put("NUMFACTURA", respuestaNotasReporte.getNumFactura());
            params2.put("NUMFORMATO", respuestaNotasReporte.getNumFormato());
            params2.put(GeneralParameterEnum.OBSERVACION.getName(),
                            respuestaNotasReporte.getObservacion());
            params2.put("TIPONOTA", respuestaNotasReporte.getTipoNota());
            params2.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
            params2.put(GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate2 = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(urlEnumId2);

            requestManager.save(urlCreate2.getUrl(), urlCreate2.getMetodo(),
                            params2);
            return "";

        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return idioma.getString("MSG_YA_CREADO") + " " +respuestaNotasReporte.getClase() + " - " +respuestaNotasReporte.getNumFormato();
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarFacturas en la vista
     *
     *
     */
    public void oprimirEnviarFacturas() {
        archivoDescarga = null;
        int tipoformato;
        String prFormato;
        String codigoReporte;
        String url;
        String log = "";
        StringBuilder pieCodigoBarra = new StringBuilder();
        StringBuilder pieTextoBarra = new StringBuilder();
        String codigoBarra;
        log = "|---------------     ENVIO DIAN     ---------------|";

        try {
            url = ejbSysmanUtil.consultarParametro(compania,
                            "URL SERVICIO REST", "69", new Date(), false);
            if (SysmanFunciones.validarVariableVacio(url)) {
                JsfUtil.agregarMensajeAlerta(
                                "Asegurese de configurar el parametro URL SERVICIO REST");
            }
            else {
                prFormato = ejbSysmanUtil.consultarParametro(compania,
                                "SF FORMATO FACTURACION ELECTRONICA",
                                SessionUtil.getModulo(), new Date(), false);

                eliminarTabEstadoFact();

                String respuesta;
                APIFrida api = new APIFrida();

                respuesta = api.cargarFormatoConsultarReporte(url, nitCompania,
                                "10", "", "181",
                                SysmanFunciones.convertirAFechaCadena(
                                                SysmanFunciones.convertirAFecha(
                                                                "18/05/2020"),
                                                "yyyy/MM/dd"),
                                SysmanFunciones.convertirAFechaCadena(
                                                new Date(),
                                                "yyyy/MM/dd"),
                                "1", "0",
                                "0");

                Gson gson = new Gson();
                RespuestaConsultarReporte respuestaApi = gson.fromJson(
                                respuesta,
                                RespuestaConsultarReporte.class);

                for (RespuestaFacturasReporte respuestaFacturasReporte : respuestaApi
                                .getCuerpo().getFacturas()) {
                    insertarFacturas(respuestaFacturasReporte);
                }

                List<Registro> listaEstadoFac = RegistroConverter
                                .toListRegistro(
                                                requestManager.getList(
                                                                UrlServiceUtil
                                                                                .getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmDocDianControladorUrlEnum.URL3561
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                null));

                if (!listaEstadoFac.isEmpty()) {

                    for (Registro reg : listaEstadoFac) {
                    	 pieCodigoBarra = new StringBuilder();
                         pieTextoBarra = new StringBuilder();
                        Map<String, Object> param = new TreeMap<>();

                        param.put(GeneralParameterEnum.NUMERO.getName(), reg
                                        .getCampos()
                                        .get(GeneralParameterEnum.NUMERO
                                                        .getName()));

                        Registro rstipoformato = RegistroConverter
                                        .toRegistro(requestManager.get(
                                                        UrlServiceUtil.getInstance()
                                                                        .getUrlServiceByUrlByEnumID(
                                                                                        FrmDocDianControladorUrlEnum.URL8245
                                                                                                        .getValue())
                                                                        .getUrl(),
                                                        param));

                        if (rstipoformato != null) {

                            tipoformato = Integer.parseInt(rstipoformato
                                            .getCampos().get("INDICADORFAC")
                                            .toString());

                        }
                        else {
                            tipoformato = 1;
                        }

                        // consumirEalvarez

                        Map<String, Object> param2 = new TreeMap<>();

                        param2.put(GeneralParameterEnum.ANO.getName(),
                                        SysmanFunciones.ano(new Date()));
                        param2.put("PREFIJO", reg.getCampos().get("PREFIJO"));

                        Registro rs = RegistroConverter
                                        .toRegistro(requestManager.get(
                                                        UrlServiceUtil.getInstance()
                                                                        .getUrlServiceByUrlByEnumID(
                                                                                        FrmDocDianControladorUrlEnum.URL2735
                                                                                                        .getValue())
                                                                        .getUrl(),
                                                        param2));

                        if (rs != null) {

                            codigoReporte = SysmanFunciones
                                            .nvl(rs.getCampos().get("FORMATO"),
                                                            "30002195")
                                            .toString();

                        }
                        else {
                            codigoReporte = prFormato;
                        }

                        Registro rsCertificado = RegistroConverter
                                        .toRegistro(requestManager.get(
                                                        UrlServiceUtil.getInstance()
                                                                        .getUrlServiceByUrlByEnumID(
                                                                                        FrmRangoProduccionDianUrlEnum.URL9457
                                                                                                        .getValue())
                                                                        .getUrl(),
                                                        null));

                        if (rsCertificado != null) {

                            File archivo = new File(
                                            rsCertificado.getCampos().get(
                                                            "RUTA_CERTIFICADO")
                                                            .toString());

                            String nombreCertificado = archivo.getName();

                            byte[] archivoBytes = Files
                                            .readAllBytes(archivo.toPath());

                            String certificado = Base64.getEncoder()
                                            .encodeToString(archivoBytes);

                            String passCertificado = Base64.getEncoder()
                                            .encodeToString(rsCertificado
                                                            .getCampos()
                                                            .get("CONTRA_CERTIFICADO")
                                                            .toString()
                                                            .getBytes());

                            ParametrosLegalizarFactura paramLegalizar = new ParametrosLegalizarFactura();

                            paramLegalizar.setTipoSalida(1);
                            paramLegalizar.setTestSetId(rsCertificado
                                            .getCampos().get("TES_ID")
                                            .toString());
                            paramLegalizar.setCodigoReporte(codigoReporte);

                            ParametrosFormato paramFormato = new ParametrosFormato();

                            paramFormato.setTipoFormato(
                                            Integer.toString(tipoformato));

                            paramFormato.setNumFormato(reg
                                            .getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName())
                                            .toString());

                            paramFormato.setPrefijo(reg.getCampos()
                                            .get("PREFIJO").toString());

                            paramFormato.setNombreCertificado(
                                            nombreCertificado);

                            paramFormato.setCertificado(certificado);

                            paramFormato.setPassCertificado(passCertificado);

                            paramFormato.setNumDocumentoContribuyente(
                                            nitCompania);

                            // Adicion Codigo Barras

                            boolean permiteCodigoBarra = false;

                            permiteCodigoBarra = ("SI").equals(
                                            SysmanFunciones.nvlStr(ejbSysmanUtil
                                                            .consultarParametro(
                                                                            compania,
                                                                            "SF PERMITE GENERAR CODIGO BARRA FACTURA ELECTRONICA",
                                                                            SessionUtil.getModulo(),
                                                                            new Date(),
                                                                            false),
                                                            "NO"));

                            paramFormato.setMuestraCodigoBarras(
                                            permiteCodigoBarra);
                            if (permiteCodigoBarra) {

                                String codigoEan;

                                Map<String, Object> paramEan = new TreeMap<>();
                                paramEan.put(GeneralParameterEnum.COMPANIA
                                                .getName(),
                                                compania);
                                paramEan.put("ANO", ano);
                                paramEan.put(GeneralParameterEnum.CODIGO
                                                .getName(),
                                                tipoCobro);
                                Registro rsEan = RegistroConverter
                                                .toRegistro(requestManager.get(
                                                                UrlServiceUtil.getInstance()
                                                                                .getUrlServiceByUrlByEnumID(
                                                                                                FrmDocDianControladorUrlEnum.URL665023
                                                                                                                .getValue())
                                                                                .getUrl(),
                                                                paramEan));

                                codigoEan = rsEan.getCampos().get("CODIGOEAN")
                                                .toString();

                                pieCodigoBarra.append((char) 205);
                                pieCodigoBarra.append((char) 102);
                                pieCodigoBarra.append(415);
                                pieCodigoBarra.append(codigoEan);
                                pieCodigoBarra.append("8020");
                                pieCodigoBarra.append(SysmanFunciones.padl(
                                                tipoFactRecaudo,
                                                5, "0"));

                                pieCodigoBarra.append(SysmanFunciones.padl(
                                                reg.getCampos()
                                                                .get(GeneralParameterEnum.NUMERO
                                                                                .getName()

                                                                ).toString(),
                                                19, "0"));
                                pieCodigoBarra.append((char) 102);
                                pieCodigoBarra.append("3900");
                                pieCodigoBarra.append(SysmanFunciones.padl(
                                                reg
                                                                .getCampos()
                                                                .get(GeneralParameterEnum.TOTAL
                                                                                .getName())
                                                                .toString(),
                                                14, "0"));
                                pieCodigoBarra.append((char) 102);
                                pieCodigoBarra.append(96);
                                pieCodigoBarra.append(reg.getCampos()
                                                .get(GeneralParameterEnum.FECHA
                                                                .getName())
                                                .toString());

                                codigoBarra = ejbCodigoBarras
                                                .imprimirCodigoBarras(
                                                                pieCodigoBarra.toString());

                                paramFormato.setCodigoBarras(codigoBarra);

                                pieTextoBarra.append((char) 40);
                                pieTextoBarra.append(415);
                                pieTextoBarra.append((char) 41);
                                pieTextoBarra.append(codigoEan);
                                pieTextoBarra.append((char) 40);
                                pieTextoBarra.append("8020");
                                pieTextoBarra.append((char) 41);
                                pieTextoBarra.append(SysmanFunciones.padl(
                                                tipoFactRecaudo,
                                                5, "0"));
                                pieTextoBarra.append(SysmanFunciones.padl(
                                                reg.getCampos()
                                                                .get(GeneralParameterEnum.NUMERO
                                                                                .getName()

                                                                ).toString(),
                                                19, "0"));
                                pieTextoBarra.append((char) 40);
                                pieTextoBarra.append("3900");
                                pieTextoBarra.append((char) 41);
                                pieTextoBarra.append(SysmanFunciones.padl(
                                                reg
                                                                .getCampos()
                                                                .get(GeneralParameterEnum.TOTAL
                                                                                .getName())
                                                                .toString(),
                                                14, "0"));
                                pieTextoBarra.append((char) 40);
                                pieTextoBarra.append(96);
                                pieTextoBarra.append((char) 41);
                                pieTextoBarra.append(reg.getCampos()
                                                .get(GeneralParameterEnum.FECHA
                                                                .getName())
                                                .toString());

                                paramFormato.setTextoBarras(
                                                pieTextoBarra.toString());
                            }

                            paramLegalizar.setParamFormato(paramFormato);

                            String respuestaLegalizar;
                            APIFrida apiFrida = new APIFrida();

                            Gson gson2 = new Gson();
                            String json = gson2.toJson(paramLegalizar,
                                            ParametrosLegalizarFactura.class);

                            respuestaLegalizar = apiFrida
                                            .postFormatoLegalizar(url, json);

                            RespuestaApi respuestaApis = gson2.fromJson(
                                            respuestaLegalizar,
                                            RespuestaApi.class);

                            if (respuestaApis.getCodigo() != 0) {
                                log = log + "\n" + respuestaApis.getMensaje()
                                    + " "
                                    + (tipoformato == 1 ? " FACTURA:"
                                        : " NOTA:")
                                    + reg
                                                    .getCampos()
                                                    .get(GeneralParameterEnum.NUMERO
                                                                    .getName())
                                                    .toString()
                                    + ". Proceso terminado ";
                            }
                            else {

                                log = log + "\n"
                                    + crearReportesFrida(respuestaLegalizar, reg
                                                    .getCampos()
                                                    .get(GeneralParameterEnum.NUMERO
                                                                    .getName())
                                                    .toString());

                            }
                        }

                    }

                }
            }

            ByteArrayInputStream streamTexto = JsfUtil
                            .serializarPlano(log);
            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                            "LogEnviarDian.txt");

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException | IOException | SysmanException
                        | ParseException | JRException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * @author gfigueredo
     * Función encargada de crear el reporte frida.
     * @param respuestaLegalizar
     * @param factura
     * @return
     */
    private String crearReportesFrida(String respuestaLegalizar,
        String factura) {

        String log = "";

        OutputStream outZip = null;
        OutputStream outPdf = null;

        Gson gson = new Gson();
        //Se deja temporalmente para identificar inconsistencias en documento
        //que impide termiar el proceso correctamente.
        System.out.println("VERIFICAR TOCAN FACT");
        System.out.println(respuestaLegalizar);
        /*
         * 11/01/2022
         * Ticket 7706692 - Se reemplaza RespuestaFridaLegalizar por
         * respuestaFridalegalizarNotas, debido a que esta clase se creó debido a que la respuesta de la Dian,
         * para las notas y las facturas era diferente.
         * El fallo indicado en el ticket permite reconocer que la resupesta de la Dina para las facturas ha cambiado y
         * ahora es igual que el de las notas.
         * No se integra en una sola funcion (crearReportesFrida y
         * crearReportesFridaNotas), con el fin de imprimir en log de servidor mensaje espcifico por cada proceso.
         */
        RespuestaFridaLegalizarNotas legalizar = gson.fromJson(respuestaLegalizar,
                        RespuestaFridaLegalizarNotas.class);

        String zip = legalizar.getCuerpo().getZip();
        String pdf = legalizar.getCuerpo().getReporte();

        Registro rsRutaArchivos;
        try {
            rsRutaArchivos = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmRangoProduccionDianUrlEnum.URL9457
                                                                                            .getValue())
                                                            .getUrl(),
                                            null));

            if (rsRutaArchivos != null) {

                String ruta = SysmanFunciones
                                .nvl(rsRutaArchivos.getCampos()
                                                .get("RUTA_FACTURAS"), "")
                                .toString();

                if (!ruta.isEmpty()) {

                    File verificar = new File(ruta);
                    if (!verificar.isDirectory()) {
                        verificar.mkdirs();
                    }

                    if (zip != null) {

                        byte[] archivoZip = Base64.getDecoder().decode(zip);

                        outZip = new FileOutputStream(ruta + "/" + "FACTURA_"
                            + factura + "_"
                            + SysmanFunciones.convertirAFechaCadena(new Date(),
                                            "YYYYMMDD_HHMMSS")
                            + ".zip");
                        outZip.write(archivoZip);
                        outZip.close();

                    }

                    if (pdf != null) {
                        byte[] archivoPdf = Base64.getDecoder().decode(pdf);

                        outPdf = new FileOutputStream(ruta + "/" + "FACTURA_"
                            + factura + "_"
                            + SysmanFunciones.convertirAFechaCadena(new Date(),
                                            "YYYYMMDD_HHMMSS")
                            + ".pdf");
                        outPdf.write(archivoPdf);
                        outPdf.close();
                    }

                    // --- Lectura del tag de respuesta de la DIAN

                    log = "Estado: " + SysmanFunciones.nvl(legalizar.getCuerpo()
                            .getResultadoProcesoDian()[0]
                            .getStatusMessage(), "").toString() + ". Descripcion: " + SysmanFunciones.nvl(legalizar.getCuerpo()
                            .getResultadoProcesoDian()[0]
                            .getStatusDescription(), "").toString() + ". Error: " + SysmanFunciones.nvl(legalizar.getCuerpo()
                            .getResultadoProcesoDian()[0]
                            .getErrorMessage(), "").toString();

					/*
					 * if (!legalizar.getCuerpo().getResultadoProcesoDian() .isIsValid()) { log =
					 * SysmanFunciones.nvl(legalizar.getCuerpo() .getResultadoProcesoDian()
					 * .getStatusDescription(), "").toString() + " - " +
					 * SysmanFunciones.nvl(legalizar.getCuerpo() .getResultadoProcesoDian()
					 * .getErrorMessage(), "").toString(); }else { log =
					 * SysmanFunciones.nvl(legalizar.getCuerpo() .getResultadoProcesoDian()
					 * .getStatusDescription(), "").toString() + " - " +
					 * SysmanFunciones.nvl(legalizar.getCuerpo() .getResultadoProcesoDian()
					 * .getStatusMessage(), "").toString(); }
					 */

                }
                else {
                    JsfUtil.agregarMensajeAlerta(
                                    "Debe crear la ruta en donde se generaran los reportes");
                }

            }

        }
        catch (SystemException | ParseException | IOException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

        return log;

    }
    
    /**
     * @author gfigueredo
     * 05/10/2021
     * Función creada para generar el zip y pdf de las notas, debido a que 
     * el json de respuesta de la Dian,retorna en la etiqueta "resultadoProcesoDian" un
     * array de objetos que inicia con el caracter [], lo cual se diferencia de la respuesta
     * de las facturas que inicia con el caracter {} y denota un objeto.
     * @param respuestaLegalizar
     * @param factura
     * @return
     */
    private String crearReportesFridaNotas(String respuestaLegalizar,
            String factura) {

            String log = "";

            OutputStream outZip = null;
            OutputStream outPdf = null;

            Gson gson = new Gson();
            //Se deja temporalmente para identificar inconsistencias en documento
            //que impide termiar el proceso correctamente.
            System.out.println("VERIFICAR TOCAN NOTAS");
            System.out.println(respuestaLegalizar);
            RespuestaFridaLegalizarNotas legalizar = gson.fromJson(respuestaLegalizar,
                            RespuestaFridaLegalizarNotas.class);

            String zip = legalizar.getCuerpo().getZip();
            String pdf = legalizar.getCuerpo().getReporte();

            Registro rsRutaArchivos;
            try {
                rsRutaArchivos = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmRangoProduccionDianUrlEnum.URL9457
                                                                                                .getValue())
                                                                .getUrl(),
                                                null));

                if (rsRutaArchivos != null) {

                    String ruta = SysmanFunciones
                                    .nvl(rsRutaArchivos.getCampos()
                                                    .get("RUTA_FACTURAS"), "")
                                    .toString();

                    if (!ruta.isEmpty()) {

                        File verificar = new File(ruta);
                        if (!verificar.isDirectory()) {
                            verificar.mkdirs();
                        }

                        if (zip != null) {

                            byte[] archivoZip = Base64.getDecoder().decode(zip);

                            outZip = new FileOutputStream(ruta + "/" + "FACTURA_"
                                + factura + "_"
                                + SysmanFunciones.convertirAFechaCadena(new Date(),
                                                "YYYYMMDD_HHMMSS")
                                + ".zip");
                            outZip.write(archivoZip);
                            outZip.close();

                        }

                        if (pdf != null) {
                            byte[] archivoPdf = Base64.getDecoder().decode(pdf);

                            outPdf = new FileOutputStream(ruta + "/" + "FACTURA_"
                                + factura + "_"
                                + SysmanFunciones.convertirAFechaCadena(new Date(),
                                                "YYYYMMDD_HHMMSS")
                                + ".pdf");
                            outPdf.write(archivoPdf);
                            outPdf.close();
                        }

                        // --- Lectura del tag de respuesta de la DIAN

                        log = "Estado: " + SysmanFunciones.nvl(legalizar.getCuerpo()
                                .getResultadoProcesoDian()[0]
                                .getStatusMessage(), "").toString() + ". Descripcion: " + SysmanFunciones.nvl(legalizar.getCuerpo()
                                .getResultadoProcesoDian()[0]
                                .getStatusDescription(), "").toString() + ". Error: " + SysmanFunciones.nvl(legalizar.getCuerpo()
                                .getResultadoProcesoDian()[0]
                                .getErrorMessage(), "").toString();
 

                    }
                    else {
                        JsfUtil.agregarMensajeAlerta(
                                        "Debe crear la ruta en donde se generaran los reportes");
                    }

                }

            }
            catch (SystemException | ParseException | IOException e) {

                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());

            }

            return log;

        }

    private void insertarFacturas(
        RespuestaFacturasReporte respuestaFacturasReporte) {

        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

        try {

            String urlEnumId = FrmDocDianControladorUrlEnum.URL9848
                            .getValue();

            Date fecha = formato.parse(
                            respuestaFacturasReporte.getFecha()
                                            .replace("-", "/"));

            Map<String, Object> params = new TreeMap<>();

            params.put(GeneralParameterEnum.ESTADO.getName(),
                            respuestaFacturasReporte.getEstado());
            params.put(GeneralParameterEnum.NUMERO.getName(),
                            respuestaFacturasReporte.getNumFormato());
            params.put(GeneralParameterEnum.TERCERO.getName(),
                            respuestaFacturasReporte.getTercero());
            params.put(GeneralParameterEnum.TOTAL.getName(),
                            new BigDecimal(respuestaFacturasReporte
                                            .getTotal()));
            params.put(GeneralParameterEnum.FECHA.getName(),
                            fecha);
            params.put(GeneralParameterEnum.OBSERVACION.getName(),
                            respuestaFacturasReporte.getObservacion());
            params.put("PREFIJO",
                            respuestaFacturasReporte.getPrefijo());
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

    private void eliminarTabEstadoFact() {

        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmDocDianControladorUrlEnum.URL5474
                                                            .getValue());

            requestManager.delete(urlDelete.getUrl(), null);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
