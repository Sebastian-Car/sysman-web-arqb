/*-
 * FrmRangoProduccionDianControlador.java
 *
 * 1.0
 * 
 * 22/12/2020
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
import com.sysman.facturaciongeneral.enums.FrmRangoProduccionDianUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.RespuestaNumberRangeResponse;
import com.sysman.util.rest.RespuestaRangoFactDian;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 22/12/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmRangoProduccionDianControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que inicia sesion
     */
    private final String usuario;

    private String nitCompania;

    // <DECLARAR_ATRIBUTOS>
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
     * Crea una nueva instancia de FrmRangoProduccionDianControlador
     */
    public FrmRangoProduccionDianControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        try {
            // 2226
            numFormulario = GeneralCodigoFormaEnum.FRM_RANGO_PRODUCCIONDIAN_CONTROLADOR
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

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
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
        // <CODIGO_DESARROLLADO>

        if (nitCompania.contains("-")) {
            int fin = nitCompania.indexOf("-");
            nitCompania = nitCompania.substring(0, fin);
        }

        cargarDatos();

        /*
         * FR2226-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Dim Res As String Dim strJSON As String Dim FACTURA As
         * Object Dim strNameFile As String Dim certInBytes() As Byte
         * Dim lengthArray As Long Dim contador As Integer strNameFile
         * = StrReverse(Mid(StrReverse(getPathFile()), 1, InStr(1,
         * StrReverse(getPathFile()), "\") - 1)) certInBytes =
         * pvToByteArray(readFile(getPathFile())) lengthArray =
         * UBound(certInBytes) + 1 strJSON = "{" & _ Chr(34) &
         * "nombreCertificado" & Chr(34) & ":" & Chr(34) &
         * Nz(strNameFile, "") & Chr(34) & "," & _ Chr(34) &
         * "certificado" & Chr(34) & ":" & Chr(34) &
         * Base64Encode2(certInBytes, lengthArray) & Chr(34) & "," & _
         * Chr(34) & "passCertificado" & Chr(34) & ":" & Chr(34) &
         * EncodeStringToBase64(getPassCert()) & Chr(34) & "," & _
         * Chr(34) & "numContribuyente" & Chr(34) & ":" & Chr(34) &
         * getNitCompany() & Chr(34) & _ "}}" Res =
         * launchRequestPOST(Nz(par("URL SERVICIO RES"), "") &
         * "rangofacturacion/", "dian", Me, strJSON) 'FACTURA = res If
         * (Res = "-1") Then CurrentDb.Execute
         * ("DELETE FROM det_rango_fact") Me!listaRangoDian.Requery
         * For Each FACTURA In x.numberRangeResponse contador =
         * contador + 1 options =
         * "INSERT INTO det_rango_fact(id,prefijo,numero_res,clave_tec,rango_ini,rango_fin, fechadesde, fechahasta,fecharesolucion) values ("
         * & _ contador & ",'" & Nz(JsonParser.GetProperty(FACTURA,
         * "prefix"), " ") & "'," & _
         * Nz(JsonParser.GetProperty(FACTURA, "resolutionNumber"),
         * " ") & ",'" & Nz(JsonParser.GetProperty(FACTURA,
         * "technicalKey"), " ") & "'," &
         * Nz(JsonParser.GetProperty(FACTURA, "fromNumber"), " ") & _
         * "," & Nz(JsonParser.GetProperty(FACTURA, "toNumber"), " ")
         * & ",'" & Nz(JsonParser.GetProperty(FACTURA,
         * "validDateFrom"), " ") & "','" &
         * Nz(JsonParser.GetProperty(FACTURA, "validDateTo"), " ") &
         * "'" & _ ",'" & Nz(JsonParser.GetProperty(FACTURA,
         * "resolutionDate"), " ") & "')" CurrentDb.Execute (options)
         * Next options = options & "$" options = Replace(options,
         * ";$", "") Me!listaRangoDian.Requery Else End If End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    private void cargarDatos() {
        try {
            String url = ejbSysmanUtil.consultarParametro(compania,
                            "URL SERVICIO REST", "69", new Date(), false);

            if (SysmanFunciones.validarVariableVacio(url)) {
                JsfUtil.agregarMensajeAlerta(
                                "Asegurese de configurar el parametro URL SERVICIO REST");
            }
            else {

                Registro rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmRangoProduccionDianUrlEnum.URL9457
                                                                                                .getValue())
                                                                .getUrl(),
                                                null));

                if (rs != null) {

                    File archivo = new File(
                                    rs.getCampos().get("RUTA_CERTIFICADO")
                                                    .toString());

                    String nombreCertificado = archivo.getName();

                    byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

                    String certificado = Base64.getEncoder()
                                    .encodeToString(archivoBytes);

                    String passCertificado = Base64.getEncoder()
                                    .encodeToString(rs.getCampos()
                                                    .get("CONTRA_CERTIFICADO")
                                                    .toString().getBytes());

                    String respuesta;
                    APIFrida api = new APIFrida();

                    respuesta = api.postRangoFacturacionDian(url, nitCompania,
                                    nombreCertificado, certificado,
                                    passCertificado);

                    Gson gson = new Gson();
                    RespuestaRangoFactDian respuestaApi = gson.fromJson(
                                    respuesta,
                                    RespuestaRangoFactDian.class);
                    if ("100".equals(respuestaApi.getCuerpo()
                                    .getOperationCode())) {

                        borrarDatosRangosFacturacion();
                        int contador = 1;

                        for (RespuestaNumberRangeResponse respuestaDatos : respuestaApi
                                        .getCuerpo().getResponseList()
                                        .getNumberRangeResponse()) {

                            insertarRangos(contador, respuestaDatos);

                            contador++;
                        }
                    }
                    else {
                        JsfUtil.agregarMensajeError(respuestaApi.getCuerpo()
                                        .getOperationDescription());
                    }

                }
                else {
                    JsfUtil.agregarMensajeAlerta(
                                    "Asegurese de configurar el certificado en el formulario CONFIGURACION CERTIFICADO DIAN");

                }
            }

        }
        catch (IOException | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void borrarDatosRangosFacturacion() {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmRangoProduccionDianUrlEnum.URL3254
                                                            .getValue());

            requestManager.delete(urlDelete.getUrl(), null);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void insertarRangos(int contador,
        RespuestaNumberRangeResponse respuestaDatos) {

        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

        Map<String, Object> params = new TreeMap<>();

        String urlEnumId = FrmRangoProduccionDianUrlEnum.URL7514.getValue();

        try {

            Date fechaDesde = formato.parse(
                            respuestaDatos.getValidDateFrom()
                                            .replace("-", "/"));

            Date fechaHasta = formato.parse(
                            respuestaDatos.getValidDateTo()
                                            .replace("-", "/"));

            Date fechaResolucion = formato.parse(
                            respuestaDatos.getResolutionDate()
                                            .replace("-", "/"));

            params.put("ID", contador);

            params.put("PREFIJO", respuestaDatos.getPrefix());

            params.put("NUMERO_RES", respuestaDatos.getResolutionNumber());

            params.put("CLAVE_TEC", respuestaDatos.getTechnicalKey());

            params.put("RANGO_INI", respuestaDatos.getFromNumber());

            params.put("RANGO_FIN", respuestaDatos.getToNumber());

            params.put("FECHADESDE", fechaDesde);

            params.put("FECHAHASTA", fechaHasta);

            params.put("FECHARESOLUCION", fechaResolucion);

            params.put("TIPOAMBIENTE", 1);

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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
