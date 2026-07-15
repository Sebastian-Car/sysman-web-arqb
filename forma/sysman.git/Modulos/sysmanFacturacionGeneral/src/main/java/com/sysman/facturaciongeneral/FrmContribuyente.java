/*-
 * FrmContribuyente.java
 *
 * 1.0
 * 
 * 7/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmContribuyenteUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.RespuestaContribuyente;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 * Fromulario que se encarga de configurar los contribuyentes
 *
 * @version 1.0, 09/12/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class FrmContribuyente extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private String nitCompania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el tipo de documento
     */
    private String tipoDocumento;
    /**
     * Variable que almacena el pais
     */
    private String pais;
    /**
     * Variable que almacena el departamento
     */
    private String departamento;
    /**
     * Variable que almacena el departamento
     */
    private String municipio;
    /**
     * Variable que almacena el tipo de organizacion
     */
    private String tipoOrganizacion;
    /**
     * Variable que almacena el tipo de regimen
     */
    private String tipoRegimen;
    /**
     * Variable que almacena el smtp
     */
    private String smtp;
    /**
     * Variable que almacena el numero de documento
     */
    private String numeroDocumento;
    /**
     * Variable que almacena el numero de contribuyente
     */
    private String nombreContribuyente;
    /**
     * Variable que almacena el correo electronico
     */
    private String correoElectronico;
    /**
     * Variable que almancena el numero de telefono
     */
    private String telefono;
    /**
     * Variable que almacena la direccion
     */
    private String direccion;
    /**
     * Variable que almacena el digito de verificacion
     */
    private String digitoVerificacion;
    /**
     * Variable que almacena la direccion fiscal
     */
    private String direccionFiscal;
    /**
     * Variable que almacena el codigo postal
     */
    private String codigoPostal;
    /**
     * Variable que almacena el indicador de software
     */
    private String identificadorSoftware;
    /**
     * Variable que almacena el pin software
     */
    private String pinSoftware;
    /**
     * Variable que almacena el correo entrante
     */
    private String correoEntrante;

    /**
     * Variable que almacena la clave de correo
     */
    private String claveCorreo;
    /**
     * Variable que almacena las responsabilidades fiscales
     */
    private String responibilidadesFiscales;
    /**
     * Variable que almacena la imagen de correo
     */
    private String imagenCorreo;
    /**
     * Variable que almacena el logo
     */
    private String logo;
    /**
     * Variable que almacena las responsabilidadres
     */
    private String responsabilidades;
    /**
     * 
     */
    private String actividad;

    /**
     * Variable que almacena la Url del servicio
     */
    private String url;
    
    /**
     * Variable que almacena el certificado
     */
    private String certificado;
    
    /**
     * Variable que almacena lel password del certificado
     */
    private String passCert;
    
    /**
     * Variable que almacena codigo reporte
     */
    private String codigoReporte; // 30002195 gnral
    
    /**
     * Variable que almacena el testID
     */        
    private String testId;

    private boolean put;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los tipos de identicacion
     */
    private List<Registro> listaTipoIdentificacion;
    /**
     * Lista que carga los paises
     */
    private List<Registro> listaPais;
    /**
     * Lista que carga los departamentos
     */
    private List<Registro> listaDepartamento;
    /**
     * Lista que carga los departamentos
     */
    private List<Registro> listaMunicipio;
    /**
     * Lista que carga los tipos de organizacion
     */
    private List<Registro> listaTipoOrganizacion;
    /**
     * Lista que carga los tipos de regimen
     */
    private List<Registro> listaTipoRegimen;
    /**
     * Lista que carga los Smtp
     */
    private List<Registro> listaSmtp;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmContribuyente
     */
    public FrmContribuyente() {
        super();
        compania = SessionUtil.getCompania();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        put = false;
        try {
            // 2217
            numFormulario = GeneralCodigoFormaEnum.FRM_CONTRIBUYENTE_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>

        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        abrirFormulario();
        cargarListaTipoIdentificacion();
        cargarListaPais();
        cargarListaDepartamento();
        cargarListaMunicipio();
        cargarListaTipoOrganizacion();
        cargarListaTipoRegimen();
        cargarListaSmtp();

    }

    private void cargarInformacionContribuyente() throws SysmanException {
        if (nitCompania.contains("-")) {
            int fin = nitCompania.indexOf("-");
            nitCompania = nitCompania.substring(0, fin);
        }

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

                respuesta = api.cargarDatos(nitCompania, url);
                Gson gson = new Gson();

                RespuestaContribuyente respuestaApi = gson.fromJson(
                                respuesta,
                                RespuestaContribuyente.class);

                tipoDocumento = buscarLista(listaTipoIdentificacion,
                                respuestaApi.getCuerpo()
                                                .getTipoidentificacion());
                digitoVerificacion = respuestaApi.getCuerpo()
                                .getDigitoverificacion();

                nombreContribuyente = respuestaApi.getCuerpo()
                                .getNombrecontribuyente();
                correoElectronico = respuestaApi.getCuerpo()
                                .getCorreoelectronico();
                claveCorreo = respuestaApi.getCuerpo()
                                .getClaveCorreo();

                correoEntrante = respuestaApi.getCuerpo()
                                .getCorreoEntrante();

                smtp = respuestaApi.getCuerpo()
                                .getSmtp();

                telefono = respuestaApi.getCuerpo()
                                .getTelefono();

                direccion = respuestaApi.getCuerpo()
                                .getDireccion();
                direccionFiscal = respuestaApi.getCuerpo()
                                .getDireccionfiscal();

                codigoPostal = respuestaApi.getCuerpo()
                                .getCodigopostal();

                pais = buscarLista(listaPais, respuestaApi.getCuerpo()
                                .getPais());

                departamento = respuestaApi.getCuerpo()
                                .getCodigodepartamento();

                cargarListaMunicipio();

                municipio = respuestaApi.getCuerpo()
                                .getCodigomunicipio();

                tipoRegimen = buscarLista(listaTipoRegimen,
                                respuestaApi.getCuerpo()
                                                .getTiporegimen()
                                                .toUpperCase());

                tipoOrganizacion = buscarLista(listaTipoOrganizacion,
                                respuestaApi.getCuerpo()
                                                .getTipoorganizacion());

                responibilidadesFiscales = respuestaApi.getCuerpo()
                                .getResponsabilidadesfiscales();

                responsabilidades = respuestaApi.getCuerpo()
                                .getTextoResponsabilidades();

                identificadorSoftware = respuestaApi.getCuerpo()
                                .getIdentificadorsoftware();

                pinSoftware = respuestaApi.getCuerpo()
                                .getPinsoftware();

                imagenCorreo = respuestaApi.getCuerpo()
                                .getImgCorreo();

                logo = respuestaApi.getCuerpo()
                                .getLogo();
                
                certificado = respuestaApi.getCuerpo()
                        .getCertificado();

                passCert = respuestaApi.getCuerpo()
                        .getPassCert();
                
				codigoReporte = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
						"SF CODIGO REPORTE FACTURA ELECTRONICA", SessionUtil.getModulo(), new Date(), true), "30002195")
						.toString();
				
				testId = respuestaApi.getCuerpo()
                        .getTestId();
                
                Map<String, Object> param = new TreeMap<>();
                
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.NIT.getName(), nitCompania);

                Registro reg = RegistroConverter.toRegistro(
                                    requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmContribuyenteUrlEnum.URL14204
                                                                    .getValue()).getUrl(), param));
                
                if(reg.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()) != null){
                	actividad = SysmanFunciones.nvlStr(reg.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName()).toString(),"");
                }
            }

        }
        catch (IOException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String buscarLista(List<Registro> lista,
        String busqueda) {

        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getCampos()
                            .containsValue(busqueda)) {

                return lista.get(i).getCampos()
                                .get(GeneralParameterEnum.CODIGO.getName())
                                .toString();

            }

        }
        return null;
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoIdentificacion
     *
     */
    public void cargarListaTipoIdentificacion() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), "69");

        try {
            listaTipoIdentificacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmContribuyenteUrlEnum.URL14950
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaPais
     *
     */
    public void cargarListaPais() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), "61");

        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmContribuyenteUrlEnum.URL14950
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaDepartamento
     *
     */
    public void cargarListaDepartamento() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", "001");
        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmContribuyenteUrlEnum.URL12990
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaMunicipio
     *
     */
    public void cargarListaMunicipio() {
        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", "001");
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), departamento);

        try {
            listaMunicipio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmContribuyenteUrlEnum.URL13818
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaTipoOrganizacion
     *
     */
    public void cargarListaTipoOrganizacion() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), "80");

        try {
            listaTipoOrganizacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmContribuyenteUrlEnum.URL14950
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaTipoRegimen
     *
     */
    public void cargarListaTipoRegimen() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), "66");

        try {
            listaTipoRegimen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmContribuyenteUrlEnum.URL14950
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaSmtp
     *
     */
    public void cargarListaSmtp() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), "31");

        try {
            listaSmtp = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmContribuyenteUrlEnum.URL14580
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control Logo
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoLogo(FileUploadEvent event) {
        byte[] logoBytes;
        UploadedFile archivoImagen = event.getFile();

        logoBytes = getFileContent(archivoImagen);

        logo = Base64.getEncoder().encodeToString(logoBytes);
    }

    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control
     * ImagenCorreo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoImagenCorreo(FileUploadEvent event) {
        byte[] imagenBytes;

        UploadedFile archivoImagen = event.getFile();

        imagenBytes = getFileContent(archivoImagen);

        imagenCorreo = Base64.getEncoder().encodeToString(imagenBytes);
    }
    
    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control
     * CERTIFICADO
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */    
    public void cargarArchivoCertificado(FileUploadEvent event) {
        byte[] certificadoBytes;
        UploadedFile archivoCertificado = event.getFile();

        certificadoBytes = getFileContent(archivoCertificado);

        certificado = Base64.getEncoder().encodeToString(certificadoBytes);
    }          
    
    public void passwordBase64() {
    	byte[] passwordCertificadoBytes = passCert.getBytes(StandardCharsets.UTF_8);
    	passCert = Base64.getEncoder().encodeToString(passwordCertificadoBytes);    
    }      
    
    
    /**
     * Metodo ejecutado al cambiar el control Departamento
     * 
     */
    public void cambiarDepartamento() {
        // <CODIGO_DESARROLLADO>
        cargarListaMunicipio();
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton GuardarContribuyente en la
     * vista
     *
     *
     */
    public void oprimirGuardarContribuyente() {
    	
    	passwordBase64();
    	
        if (nitCompania.contains("-")) {
            int fin = nitCompania.indexOf("-");
            nitCompania = nitCompania.substring(0, fin);
        }

        try {
            url = ejbSysmanUtil.consultarParametro(compania,
                            "URL SERVICIO REST", "69", new Date(), false);
            
			codigoReporte = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"SF CODIGO REPORTE FACTURA ELECTRONICA", SessionUtil.getModulo(), new Date(), true), "30002195")
					.toString(); 

			
            if (SysmanFunciones.validarVariableVacio(url)) {
                JsfUtil.agregarMensajeAlerta(
                                "Asegurese de configurar el parametro URL SERVICIO REST");
            }
            else {

                // modo insertar
                if (!put) {
                    APIFrida api = new APIFrida();
                    api.postContribuyente(url, nitCompania, tipoDocumento,
                                    digitoVerificacion,
                                    nombreContribuyente,
                                    correoElectronico,
                                    claveCorreo,
                                    correoEntrante,
                                    smtp,
                                    telefono,
                                    direccion,
                                    direccionFiscal,
                                    codigoPostal,
                                    pais,
                                    departamento,
                                    municipio,
                                    tipoRegimen,
                                    tipoOrganizacion,
                                    responibilidadesFiscales,
                                    responsabilidades,
                                    identificadorSoftware,
                                    pinSoftware,
                                    imagenCorreo,
                                    logo,
                                    actividad,
                                    certificado, 
                                    passCert,
                                    codigoReporte,testId);

                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_REGISTRO_INGRESADO"));

                }
                // Metodo Actualizar
                else {
                    APIFrida api = new APIFrida();

                    api.putContribuyente(url, nitCompania, tipoDocumento,
                                    digitoVerificacion,
                                    nombreContribuyente,
                                    correoElectronico,
                                    claveCorreo,
                                    correoEntrante,
                                    smtp,
                                    telefono,
                                    direccion,
                                    direccionFiscal,
                                    codigoPostal,
                                    pais,
                                    departamento,
                                    municipio,
                                    tipoRegimen,
                                    tipoOrganizacion,
                                    responibilidadesFiscales,
                                    responsabilidades,
                                    identificadorSoftware,
                                    pinSoftware,
                                    imagenCorreo,
                                    logo,
                                    actividad, 
                                    certificado, 
                                    passCert,codigoReporte,testId);

                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString(
                                                    "MSM_REGISTRO_MODIFICADO"));
                }

            }
        }
        catch (SystemException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnimgCorreo en la vista
     *
     *
     */
    public void oprimirbtnimgCorreo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnLogo en la vista
     *
     *
     */
    public void oprimirbtnLogo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CargarContribuyente en la
     * vista
     *
     */
    public void oprimirCargarContribuyente() {

        archivoDescarga = null;
        // <CODIGO_DESARROLLADO>
        try {
            cargarInformacionContribuyente();
            put = true;
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            JsfUtil.agregarMensajeAlerta(
                            "No existe informacion de contribuyente para el nit de esta compañia");

            put = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     * Recibe el archivo que se cargo en el selector de imagen y lo
     * comnvierte a un arreglo de bytes.
     * 
     * @param file
     * Archivo subido por medio del componente <i>fileUpload</i> de
     * Primefaces.
     * @return Archivo como arreglo de bytes.
     */
    private byte[] getFileContent(UploadedFile file) {
        byte[] bytes = new byte[0];
        try (InputStream stream = file.getInputstream();) {
            bytes = IOUtils.toByteArray(stream);
            JsfUtil.agregarMensajeInformativo("Imagen Cargada");
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + "<br>"
                                + ex.getMessage());
        }
        return bytes;
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        numeroDocumento = nitCompania;

        /*
         * FR2217-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * Dim arg As Integer Dim res As String Dim rs As String Dim
         * strUrl As String Set DB = CurrentDb strUrl =
         * Nz(par("URL SERVICIO RES"), "") Me!txtNumeroDocumento =
         * getNitCompany() '--- Se valida que el parametro este
         * configurado If strUrl = "" Then MsgBox
         * "Asegurese de configurar el parametro URL SERVICIO RES",
         * vbExclamation, "Stefanini Sysman" Exit Sub End If res =
         * launchRequestGET(strUrl, "contribuyente",
         * "numerodocumento:" & getNitCompany, 1) If (res = "-1") Then
         * Me!oldcmb0TipoIdentificacion = JsonParser.GetProperty(x,
         * "tipoidentificacion") Me!oldtxtDigitoVerificacion =
         * JsonParser.GetProperty(x, "digitoverificacion")
         * Me!oldtxtNombreContribuyente = JsonParser.GetProperty(x,
         * "nombrecontribuyente") Me!oldtxtCorreoElectronico =
         * JsonParser.GetProperty(x, "correoelectronico")
         * Me!oldtxtclaveCorreo = JsonParser.GetProperty(x,
         * "claveCorreo") Me!oldtxtCorreoEntrante =
         * JsonParser.GetProperty(x, "correoEntrante") Me!oldcmb0smtp
         * = JsonParser.GetProperty(x, "smtp") Me!oldtxtTelefono =
         * JsonParser.GetProperty(x, "telefono") Me!oldtxtDireccion =
         * JsonParser.GetProperty(x, "direccion")
         * Me!oldtxtDireccionFiscal = JsonParser.GetProperty(x,
         * "direccionfiscal") Me!oldcmb0Pais =
         * JsonParser.GetProperty(x, "pais")
         * Me!oldcmb0codigodepartamento = JsonParser.GetProperty(x,
         * "codigodepartamento") Me!oldtxtCodigoPostal =
         * JsonParser.GetProperty(x, "codigopostal")
         * Me!oldcmb0codigomunicipio = JsonParser.GetProperty(x,
         * "codigomunicipio") Me!oldcmb0TipoRegimen =
         * JsonParser.GetProperty(x, "tiporegimen")
         * Me!oldcmb0TipoOrganizacion = JsonParser.GetProperty(x,
         * "tipoorganizacion") Me!oldtxtIdentificadorSoftware =
         * JsonParser.GetProperty(x, "identificadorsoftware")
         * Me!oldtxtpinsoftware = JsonParser.GetProperty(x,
         * "pinsoftware") Me!oldtxtResponsabilidadesFiscales =
         * JsonParser.GetProperty(x, "responsabilidadesfiscales")
         * Me!oldtxttextoResponsabilidades = JsonParser.GetProperty(x,
         * "textoResponsabilidades") Me!oldtxtimgCorreo =
         * JsonParser.GetProperty(x, "imgCorreo") Me!oldtxtlogo =
         * JsonParser.GetProperty(x, "logo")
         * Me!cmb0TipoIdentificacion.SetFocus
         * Me!cmb0TipoIdentificacion.text = JsonParser.GetProperty(x,
         * "tipoidentificacion") Me!txtDigitoVerificacion =
         * JsonParser.GetProperty(x, "digitoverificacion")
         * Me!txtNombreContribuyente = JsonParser.GetProperty(x,
         * "nombrecontribuyente") Me!txtCorreoElectronico =
         * JsonParser.GetProperty(x, "correoelectronico")
         * Me!txtclaveCorreo = JsonParser.GetProperty(x,
         * "claveCorreo") Me!txtCorreoEntrante =
         * JsonParser.GetProperty(x, "correoEntrante") Me!cmb0smtp =
         * JsonParser.GetProperty(x, "smtp") Me!txtTelefono =
         * JsonParser.GetProperty(x, "telefono") Me!txtDireccion =
         * JsonParser.GetProperty(x, "direccion")
         * Me!txtDireccionFiscal = JsonParser.GetProperty(x,
         * "direccionfiscal") Me!cmb0Pais.SetFocus Me!cmb0Pais.text =
         * JsonParser.GetProperty(x, "pais") Me!cmb0codigodepartamento
         * = JsonParser.GetProperty(x, "codigodepartamento")
         * Me!cmb0codigomunicipio = JsonParser.GetProperty(x,
         * "codigomunicipio") Me!txtCodigoPostal =
         * JsonParser.GetProperty(x, "codigopostal")
         * Me!cmb0TipoRegimen.SetFocus If (JsonParser.GetProperty(x,
         * "tiporegimen") = "SIMPLE") Or (JsonParser.GetProperty(x,
         * "tiporegimen") = "ORDINARIO") Or (JsonParser.GetProperty(x,
         * "tiporegimen") = "NO OBLIGADO A REGISTRARSE EN EL RUT PN")
         * Then Me!cmb0TipoRegimen.text = "" Else
         * Me!cmb0TipoRegimen.text = JsonParser.GetProperty(x,
         * "tiporegimen") End If Me!cmb0TipoOrganizacion.SetFocus
         * Me!cmb0TipoOrganizacion.text = JsonParser.GetProperty(x,
         * "tipoorganizacion") Me!txtIdentificadorSoftware =
         * JsonParser.GetProperty(x, "identificadorsoftware")
         * Me!txtpinsoftware = JsonParser.GetProperty(x,
         * "pinsoftware") Me!txtResponsabilidadesFiscales =
         * JsonParser.GetProperty(x, "responsabilidadesfiscales")
         * Me!txttextoResponsabilidades = JsonParser.GetProperty(x,
         * "textoResponsabilidades") Me!imgCorreo =
         * JsonParser.GetProperty(x, "imgCorreo") Me!logo =
         * JsonParser.GetProperty(x, "logo")
         * Me!cmb0TipoIdentificacion.SetFocus End If End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
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

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoDocumento
     * 
     * @return tipoDocumento
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Asigna la variable tipoDocumento
     * 
     * @param tipoDocumento
     * Variable a asignar en tipoDocumento
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * Retorna la variable pais
     * 
     * @return pais
     */
    public String getPais() {
        return pais;
    }

    /**
     * Asigna la variable pais
     * 
     * @param pais
     * Variable a asignar en pais
     */
    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * Retorna la variable departamento
     * 
     * @return departamento
     */
    public String getDepartamento() {
        return departamento;
    }

    /**
     * Asigna la variable departamento
     * 
     * @param departamento
     * Variable a asignar en departamento
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * Retorna la variable municipio
     * 
     * @return municipio
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * Asigna la variable municipio
     * 
     * @param municipio
     * Variable a asignar en municipio
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * Retorna la variable tipoOrganizacion
     * 
     * @return tipoOrganizacion
     */
    public String getTipoOrganizacion() {
        return tipoOrganizacion;
    }

    /**
     * Asigna la variable tipoOrganizacion
     * 
     * @param tipoOrganizacion
     * Variable a asignar en tipoOrganizacion
     */
    public void setTipoOrganizacion(String tipoOrganizacion) {
        this.tipoOrganizacion = tipoOrganizacion;
    }

    /**
     * Retorna la variable tipoRegimen
     * 
     * @return tipoRegimen
     */
    public String getTipoRegimen() {
        return tipoRegimen;
    }

    /**
     * Asigna la variable tipoRegimen
     * 
     * @param tipoRegimen
     * Variable a asignar en tipoRegimen
     */
    public void setTipoRegimen(String tipoRegimen) {
        this.tipoRegimen = tipoRegimen;
    }

    /**
     * Retorna la variable smtp
     * 
     * @return smtp
     */
    public String getSmtp() {
        return smtp;
    }

    /**
     * Asigna la variable smtp
     * 
     * @param smtp
     * Variable a asignar en smtp
     */
    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }

    /**
     * Retorna la variable numeroDocumento
     * 
     * @return numeroDocumento
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * Asigna la variable numeroDocumento
     * 
     * @param numeroDocumento
     * Variable a asignar en numeroDocumento
     */
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    /**
     * Retorna la variable nombreContribuyente
     * 
     * @return nombreContribuyente
     */
    public String getNombreContribuyente() {
        return nombreContribuyente;
    }

    /**
     * Asigna la variable nombreContribuyente
     * 
     * @param nombreContribuyente
     * Variable a asignar en nombreContribuyente
     */
    public void setNombreContribuyente(String nombreContribuyente) {
        this.nombreContribuyente = nombreContribuyente;
    }

    /**
     * Retorna la variable correoElectronico
     * 
     * @return correoElectronico
     */
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    /**
     * Asigna la variable correoElectronico
     * 
     * @param correoElectronico
     * Variable a asignar en correoElectronico
     */
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    /**
     * Retorna la variable telefono
     * 
     * @return telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Asigna la variable telefono
     * 
     * @param telefono
     * Variable a asignar en telefono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Retorna la variable direccion
     * 
     * @return direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Asigna la variable direccion
     * 
     * @param direccion
     * Variable a asignar en direccion
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Retorna la variable digitoVerificacion
     * 
     * @return digitoVerificacion
     */
    public String getDigitoVerificacion() {
        return digitoVerificacion;
    }

    /**
     * Asigna la variable digitoVerificacion
     * 
     * @param digitoVerificacion
     * Variable a asignar en digitoVerificacion
     */
    public void setDigitoVerificacion(String digitoVerificacion) {
        this.digitoVerificacion = digitoVerificacion;
    }

    /**
     * Retorna la variable direccionFiscal
     * 
     * @return direccionFiscal
     */
    public String getDireccionFiscal() {
        return direccionFiscal;
    }

    /**
     * Asigna la variable direccionFiscal
     * 
     * @param direccionFiscal
     * Variable a asignar en direccionFiscal
     */
    public void setDireccionFiscal(String direccionFiscal) {
        this.direccionFiscal = direccionFiscal;
    }

    /**
     * Retorna la variable codigoPostal
     * 
     * @return codigoPostal
     */
    public String getCodigoPostal() {
        return codigoPostal;
    }

    /**
     * Asigna la variable codigoPostal
     * 
     * @param codigoPostal
     * Variable a asignar en codigoPostal
     */
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    /**
     * Retorna la variable identificadorSoftware
     * 
     * @return identificadorSoftware
     */
    public String getIdentificadorSoftware() {
        return identificadorSoftware;
    }

    /**
     * Asigna la variable identificadorSoftware
     * 
     * @param identificadorSoftware
     * Variable a asignar en identificadorSoftware
     */
    public void setIdentificadorSoftware(String identificadorSoftware) {
        this.identificadorSoftware = identificadorSoftware;
    }

    /**
     * Retorna la variable pinSoftware
     * 
     * @return pinSoftware
     */
    public String getPinSoftware() {
        return pinSoftware;
    }

    /**
     * Asigna la variable pinSoftware
     * 
     * @param pinSoftware
     * Variable a asignar en pinSoftware
     */
    public void setPinSoftware(String pinSoftware) {
        this.pinSoftware = pinSoftware;
    }

    /**
     * Retorna la variable correoEntrante
     * 
     * @return correoEntrante
     */
    public String getCorreoEntrante() {
        return correoEntrante;
    }

    /**
     * Asigna la variable correoEntrante
     * 
     * @param correoEntrante
     * Variable a asignar en correoEntrante
     */
    public void setCorreoEntrante(String correoEntrante) {
        this.correoEntrante = correoEntrante;
    }

    /**
     * Retorna la variable claveCorreo
     * 
     * @return claveCorreo
     */
    public String getClaveCorreo() {
        return claveCorreo;
    }

    /**
     * Asigna la variable claveCorreo
     * 
     * @param claveCorreo
     * Variable a asignar en claveCorreo
     */
    public void setClaveCorreo(String claveCorreo) {
        this.claveCorreo = claveCorreo;
    }

    /**
     * Retorna la variable responibilidadesFiscales
     * 
     * @return responibilidadesFiscales
     */
    public String getResponibilidadesFiscales() {
        return responibilidadesFiscales;
    }

    /**
     * Asigna la variable responibilidadesFiscales
     * 
     * @param responibilidadesFiscales
     * Variable a asignar en responibilidadesFiscales
     */
    public void setResponibilidadesFiscales(String responibilidadesFiscales) {
        this.responibilidadesFiscales = responibilidadesFiscales;
    }

    /**
     * Retorna la variable imagenCorreo
     * 
     * @return imagenCorreo
     */
    public String getImagenCorreo() {
        return imagenCorreo;
    }

    /**
     * Asigna la variable imagenCorreo
     * 
     * @param imagenCorreo
     * Variable a asignar en imagenCorreo
     */
    public void setImagenCorreo(String imagenCorreo) {
        this.imagenCorreo = imagenCorreo;
    }

    /**
     * Retorna la variable logo
     * 
     * @return logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     * Asigna la variable logo
     * 
     * @param logo
     * Variable a asignar en logo
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * Retorna la variable responsabilidades
     * 
     * @return responsabilidades
     */
    public String getResponsabilidades() {
        return responsabilidades;
    }

    /**
     * Asigna la variable responsabilidades
     * 
     * @param responsabilidades
     * Variable a asignar en responsabilidades
     */
    public void setResponsabilidades(String responsabilidades) {
        this.responsabilidades = responsabilidades;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipoIdentificacion
     * 
     * @return listaTipoIdentificacion
     */
    public List<Registro> getListaTipoIdentificacion() {
        return listaTipoIdentificacion;
    }

    /**
     * Asigna la lista listaTipoIdentificacion
     * 
     * @param listaTipoIdentificacion
     * Variable a asignar en listaTipoIdentificacion
     */
    public void setListaTipoIdentificacion(
        List<Registro> listaTipoIdentificacion) {
        this.listaTipoIdentificacion = listaTipoIdentificacion;
    }

    /**
     * Retorna la lista listaPais
     * 
     * @return listaPais
     */
    public List<Registro> getListaPais() {
        return listaPais;
    }

    /**
     * Asigna la lista listaPais
     * 
     * @param listaPais
     * Variable a asignar en listaPais
     */
    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    /**
     * Retorna la lista listaDepartamento
     * 
     * @return listaDepartamento
     */
    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    /**
     * Asigna la lista listaDepartamento
     * 
     * @param listaDepartamento
     * Variable a asignar en listaDepartamento
     */
    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    /**
     * Retorna la lista listaMunicipio
     * 
     * @return listaMunicipio
     */
    public List<Registro> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Asigna la lista listaMunicipio
     * 
     * @param listaMunicipio
     * Variable a asignar en listaMunicipio
     */
    public void setListaMunicipio(List<Registro> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    /**
     * Retorna la lista listaTipoOrganizacion
     * 
     * @return listaTipoOrganizacion
     */
    public List<Registro> getListaTipoOrganizacion() {
        return listaTipoOrganizacion;
    }

    /**
     * Asigna la lista listaTipoOrganizacion
     * 
     * @param listaTipoOrganizacion
     * Variable a asignar en listaTipoOrganizacion
     */
    public void setListaTipoOrganizacion(List<Registro> listaTipoOrganizacion) {
        this.listaTipoOrganizacion = listaTipoOrganizacion;
    }

    /**
     * Retorna la lista listaTipoRegimen
     * 
     * @return listaTipoRegimen
     */
    public List<Registro> getListaTipoRegimen() {
        return listaTipoRegimen;
    }

    /**
     * Asigna la lista listaTipoRegimen
     * 
     * @param listaTipoRegimen
     * Variable a asignar en listaTipoRegimen
     */
    public void setListaTipoRegimen(List<Registro> listaTipoRegimen) {
        this.listaTipoRegimen = listaTipoRegimen;
    }

    /**
     * Retorna la lista listaSmtp
     * 
     * @return listaSmtp
     */
    public List<Registro> getListaSmtp() {
        return listaSmtp;
    }

    /**
     * Asigna la lista listaSmtp
     * 
     * @param listaSmtp
     * Variable a asignar en listaSmtp
     */
    public void setListaSmtp(List<Registro> listaSmtp) {
        this.listaSmtp = listaSmtp;
    }

    /**
     * Retorna la variable Certificado
     * 
     * @return certificado
     */    
    
	public String getCertificado() {
		return certificado;
	}

    /**
     * Asigna la variable Certificado
     * 
     * @param certificado
     * Variable a asignar en certificado
     */
	
	public void setCertificado(String certificado) {
		this.certificado = certificado;
	}
	
    /**
     * Retorna la variable passwordCertificado
     * 
     * @return passwordCertificado
     */
	public String getPassCert() {
		return passCert;
	}

	public void setPassCert(String passCert) {
		this.passCert = passCert;
	}

	public String getCodigoReporte() {
		return codigoReporte;
	}

	public void setCodigoReporte(String codigoReporte) {
		this.codigoReporte = codigoReporte;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}


	

	
    
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}