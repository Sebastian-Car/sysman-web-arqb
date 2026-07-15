package com.sysman.almacen;

import com.sysman.almacen.enums.FrmprediosControladorEnum;
import com.sysman.almacen.enums.FrmprediosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 18/02/2016
 * @version 2, 28/04/2017 modificado por jcrodriguez
 * descripcion:--depuracion del controlador --creacion de dss
 * --creacion de metodos privados
 * 
 * @version 3.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar el getLlave por el getLlaveServicio del CacheUtil.
 */
@ManagedBean
@ViewScoped
public class FrmprediosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * variable que almacena la compañia
     */
    private final String compania;
    /**
     * variable que almacen el modulo
     */
    private final String modulo;
    /**
     * variable que almacena los registros sub
     */
    private Registro registroSubSubPredioUsos;
    /**
     * variable que almacena los registros sub
     */
    private Registro registroSubSubPredioServicios;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaUBICACION;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaNOTARIA;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaDptoEscritura;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaCiudadEscritura;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaMODALIDAD;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaTipoPredio;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaRESPONSABLE;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaPAIS;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaUso;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaServicio;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaSubprediousos;
    /**
     * variables que almacenan datos en una lista
     */
    private List<Registro> listaSubpredioservicios;
    /**
     * variables que almacenan datos en una lista
     */
    private RegistroDataModelImpl listaCSeriePlaca;
    /**
     * variable que almacena una cadena
     */
    private String stringFoto;
    /**
     * variable que almacena la ruta de una foto
     */
    private String rutaFoto;
    /**
     * variable que almacena el nombre de la foto
     */
    private String nombreFoto;
    /**
     * variable que almacena el input stream foto
     */
    private InputStream inputStreamFoto;
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que almacena el archivo plano
     */
    private String stringPlano;
    /**
     * variable
     */
    private InputStream inputStreamPlano;
    /**
     * variable que almacena la ruta del archivo plano
     */
    private String rutaPlano;
    /**
     * variable que almacena el nombre del archivo plano
     */
    private String nombrePlano;
    /**
     * variable que almacena el nombre clasificacion
     */
    private String nombreClasificacion;
    /**
     * variable que almacena el estado
     */
    private boolean bloqSeriePlaca;
    /**
     * variable que almacena una cadena
     */
    private String visibleAnulada;
    /**
     * variable que almacena una cadena
     */
    private String anulada;
    /**
     * varible ejb para los servicios
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * varaible que contiene los bytes de foto
     */
    private byte[] imagenBytesFoto;
    /**
     * varaible que contiene los bytes de plano
     */
    private byte[] imagenBytesPlano;

    public FrmprediosControlador() {
        super();
        // 523
        numFormulario = GeneralCodigoFormaEnum.FRMPREDIOS_CONTROLADOR
                        .getCodigo();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            registro = new Registro(new HashMap<String, Object>());
            registroSubSubPredioUsos = new Registro(
                            new HashMap<String, Object>());
            registroSubSubPredioServicios = new Registro(
                            new HashMap<String, Object>());
            validarPermisos();
        }
        catch (SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * metodo que se llama al inicializar el formulario
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PREDIOS;
        buscarLlave();
        asignarOrigenDatos();
        anulada = FrmprediosControladorEnum.ANULADA.getValue();
    }

    /**
     * metodo que se llama para asignar el origende datos
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * metodo que se llama para inicializar las listas
     */
    @Override
    public void iniciarListas() {
        cargarListaCSeriePlaca();
        cargarListaUBICACION();
        cargarListaNOTARIA();
        cargarListaMODALIDAD();
        cargarListaTipoPredio();
        cargarListaRESPONSABLE();
        cargarListaPAIS();
        cargarListaUso();
        cargarListaServicio();
    }

    /**
     * metodo que se llama para inicializar las listas
     */
    @Override
    public void iniciarListasSub() {
        cargarListaSubprediousos();
        cargarListaSubpredioservicios();
        cargarListaPAIS();
        cargarListaDptoEscritura();
        cargarListaCiudadEscritura();
        cargarFoto();
        cargarPlano();
    }

    /**
     * metodo que se llama para inicializar las listas en null
     */
    @Override
    public void iniciarListasSubNulo() {
        listaSubprediousos = null;
        listaSubpredioservicios = null;
        listaDptoEscritura = null;
        listaCiudadEscritura = null;
    }

    /**
     * metodo que se llama al cargar los datos del sub formulario de
     * predios usos
     */
    public void cargarListaSubprediousos() {
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        GenericUrlEnum.USOS_PREDIO.getGridKey());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosControladorEnum.IDPREDIO.getValue(), registro
                        .getCampos()
                        .get(FrmprediosControladorEnum.ID_PREDIO.getValue()));

        try {
            listaSubprediousos = RegistroConverter.toListRegistro(
                            requestManager.getList(url.getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmprediosControladorEnum.USOSPREDIO
                                                            .getValue()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama para cargar los datos de la grilla del sub
     * formulario predios servicios
     */
    public void cargarListaSubpredioservicios() {
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        GenericUrlEnum.SERVICIOS_PREDIO.getGridKey());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosControladorEnum.IDPREDIO.getValue(), registro
                        .getCampos()
                        .get(FrmprediosControladorEnum.ID_PREDIO.getValue()));

        try {
            listaSubpredioservicios = RegistroConverter.toListRegistro(
                            requestManager.getList(url.getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmprediosControladorEnum.SERVICIOS_PREDIO
                                                            .getValue()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que es llamado para cargar una lista de ubicacion
     */
    public void cargarListaUBICACION() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaUBICACION = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL12999
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que es llamado para cargar una lista de notarias
     */
    public void cargarListaNOTARIA() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaNOTARIA = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL13489
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que es llamado para cargar una lista de escrituras
     */
    public void cargarListaDptoEscritura() {

        Map<String, Object> param = new TreeMap<>();
        param.put(FrmprediosControladorEnum.PAIS.getValue(), registro
                        .getCampos()
                        .get(FrmprediosControladorEnum.PAIS.getValue()));

        try {
            listaDptoEscritura = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL13922
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que es llamado para cargar una lista ciudades escrituras
     */
    public void cargarListaCiudadEscritura() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmprediosControladorEnum.PAIS.getValue(), registro
                        .getCampos()
                        .get(FrmprediosControladorEnum.PAIS.getValue()));
        param.put(FrmprediosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos()
                                        .get(FrmprediosControladorEnum.DPTO_ESCRITURA
                                                        .getValue()));

        try {
            listaCiudadEscritura = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL14446
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que es llamado para cargar una lista de modalidades
     */
    public void cargarListaMODALIDAD() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaMODALIDAD = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL15059
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que es llamado para cargar una lista de tipo predios
     */
    public void cargarListaTipoPredio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoPredio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL15529
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que es llamado para cargar una lista de responsables
     */
    public void cargarListaRESPONSABLE() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaRESPONSABLE = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL16003
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que es llamado para cargar una lista de paises
     */
    public void cargarListaPAIS() {
        try {
            listaPAIS = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL17036
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que es llamado para cargar una lista de usos
     */
    public void cargarListaUso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaUso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL17335
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que es llamado para cargar una lista de servicios
     */
    public void cargarListaServicio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaServicio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprediosControladorUrlEnum.URL17668
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que es llamado para cargar una lista grande de series de
     * placas
     */
    public void cargarListaCSeriePlaca() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprediosControladorUrlEnum.URL18082
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaCSeriePlaca = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.SERIE.getName());
    }

    private String getParametro(String parametro, boolean isMayuMin) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, parametro, modulo,
                            new Date(), isMayuMin);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

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
            JsfUtil.agregarMensajeInformativo(idioma.getString(
                            FrmprediosControladorEnum.TB_TB2839.getValue()));
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            FrmprediosControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue())
                                + "<br>"
                                + ex.getMessage());
        }
        return bytes;
    }

    /**
     * metodo que se llama cuando se presiona el boton para subir el
     * archivo imagen
     * 
     * @param event
     */
    public void cargarArchivoLectorFoto(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        UploadedFile archivoImagen = event.getFile();
        imagenBytesFoto = getFileContent(archivoImagen);
        stringFoto = JsfUtil.encodeImage(imagenBytesFoto);
        rutaFoto = getParametro(
                        FrmprediosControladorEnum.RUTA_IMAGENES.getValue(),
                        false);
        String id = !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmprediosControladorEnum.ID_PREDIO.getValue())
                            ? registro.getCampos()
                                            .get(FrmprediosControladorEnum.ID_PREDIO
                                                            .getValue())
                                            .toString()
                            : addIdimage();
        nombreFoto = "FotoPredio"
            + id
            + event.getFile().getFileName().substring(
                            event.getFile().getFileName().lastIndexOf('.'));
        registro.getCampos().put(
                        FrmprediosControladorEnum.RUTAFOTO.getValue(),
                        rutaFoto.endsWith(File.separator) ? rutaFoto
                            : rutaFoto + File.separator);
        registro.getCampos().put(
                        FrmprediosControladorEnum.CODFOTO.getValue(),
                        nombreFoto);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que adiciona el id para el nombre de la imagen
     * 
     * @return
     */
    private String addIdimage() {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmprediosControladorEnum.ID_PREDIO.getValue())
            && ACCION_INSERTAR.equals(accion)) {
            Long dblconsecutivo;
            try {
                dblconsecutivo = ejbSysmanUtil
                                .generarConsecutivoConValorInicial(
                                                FrmprediosControladorEnum.PREDIOS
                                                                .getValue(),
                                                "COMPANIA =''" + compania
                                                    + "''",
                                                FrmprediosControladorEnum.ID_PREDIO
                                                                .getValue(),
                                                null);
                return SysmanFunciones.padl(Long.toString(dblconsecutivo), 10,
                                "0");
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return null;
    }

    /**
     * metodo que se llama cuando se presiona el boton para subir el
     * archivo imagen
     * 
     * @param event
     */
    public void cargarArchivoLectorPlano(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        UploadedFile archivoImagen = event.getFile();
        imagenBytesPlano = getFileContent(archivoImagen);
        stringPlano = JsfUtil.encodeImage(imagenBytesPlano);
        rutaPlano = getParametro(
                        FrmprediosControladorEnum.RUTA_IMAGENES.getValue(),
                        false);
        String id = !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmprediosControladorEnum.ID_PREDIO.getValue())
                            ? registro.getCampos()
                                            .get(FrmprediosControladorEnum.ID_PREDIO
                                                            .getValue())
                                            .toString()
                            : addIdimage();
        nombrePlano = "PlanoPredio"
            + id
            + event.getFile().getFileName().substring(
                            event.getFile().getFileName().lastIndexOf('.'));
        registro.getCampos().put(
                        FrmprediosControladorEnum.RUTAPLANO.getValue(),
                        rutaPlano.endsWith(File.separator) ? rutaPlano
                            : rutaPlano + File.separator);
        registro.getCampos().put(
                        FrmprediosControladorEnum.CODPLANO.getValue(),
                        nombrePlano);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama para cuando se registra un predios uso del
     * sub formulario
     */
    public void agregarRegistroSubSubprediousos() {
        try {
            registroSubSubPredioUsos.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubSubPredioUsos.getCampos().put(
                            FrmprediosControladorEnum.ID_PREDIO.getValue(),
                            registro.getCampos()
                                            .get(FrmprediosControladorEnum.ID_PREDIO
                                                            .getValue()));
            registroSubSubPredioUsos.getCampos()
                            .remove(FrmprediosControladorEnum.RID.getValue());
            registroSubSubPredioUsos.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSubPredioUsos.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubSubPredioUsos.getCampos()
                            .remove(GeneralParameterEnum.DESCRIPCION.getName());
            registroSubSubPredioUsos.getCampos()
                            .remove(GeneralParameterEnum.MODIFIED_BY.getName());
            registroSubSubPredioUsos.getCampos().remove(
                            GeneralParameterEnum.DATE_MODIFIED.getName());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.USOS_PREDIO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubPredioUsos.getCampos());
            cargarListaSubprediousos();
            JsfUtil.agregarMensajeInformativo(idioma.getString(
                            FrmprediosControladorEnum.TB_TB1945.getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubPredioUsos = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * metodo que se llama al editar un registro del sub formulario
     * usos
     * 
     * @param event
     */
    public void editarRegSubSubprediousos(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        reg.getCampos().remove(FrmprediosControladorEnum.ID_PREDIO.getValue());
        reg.getCampos().remove(FrmprediosControladorEnum.RID.getValue());
        reg.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.USOS_PREDIO
                                                            .getUpdateKey());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            FrmprediosControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubprediousos();
        }
    }

    /**
     * metodo que se llama al eliminar un registro del sub formulario
     * usos
     * 
     * @param reg
     */
    public void eliminarRegSubSubprediousos(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.USOS_PREDIO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            FrmprediosControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));
            cargarListaSubprediousos();
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * metodo que se llama al cancelar la edicion de un registro del
     * sub formulario usos
     */
    public void cancelarEdicionSubprediousos() {
        cargarListaSubprediousos();
        cargarListaSubpredioservicios();
    }

    /**
     * metodo que se utiliza para registrar nuevos datos en el sub
     * formuilario servicios
     */
    public void agregarRegistroSubSubpredioservicios() {
        try {

            registroSubSubPredioServicios.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubSubPredioServicios.getCampos().put(
                            FrmprediosControladorEnum.ID_PREDIO.getValue(),
                            registro.getCampos()
                                            .get(FrmprediosControladorEnum.ID_PREDIO
                                                            .getValue()));
            registroSubSubPredioServicios.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSubPredioServicios.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubSubPredioServicios.getCampos()
                            .remove(GeneralParameterEnum.NOMBRE.getName());
            registroSubSubPredioServicios.getCampos()
                            .remove(FrmprediosControladorEnum.RID.getValue());
            registroSubSubPredioServicios.getCampos().remove(
                            GeneralParameterEnum.DATE_MODIFIED.getName());
            registroSubSubPredioServicios.getCampos()
                            .remove(GeneralParameterEnum.MODIFIED_BY.getName());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SERVICIOS_PREDIO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubPredioServicios.getCampos());

            cargarListaSubpredioservicios();
            JsfUtil.agregarMensajeInformativo(idioma.getString(
                            FrmprediosControladorEnum.TB_TB1945.getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubPredioServicios = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * metodo que se llama al editar un registro de la grilla del sub
     * formulario servicios
     * 
     * @param event
     */
    public void editarRegSubSubpredioservicios(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());
        reg.getCampos().remove(FrmprediosControladorEnum.RID.getValue());
        reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        reg.getCampos().remove(FrmprediosControladorEnum.ID_PREDIO.getValue());
        reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
        reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());

        try {
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SERVICIOS_PREDIO
                                                            .getUpdateKey());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            FrmprediosControladorEnum.MSM_REGISTRO_MODIFICADO
                                                            .getValue()));
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubpredioservicios();
        }
    }

    /**
     * metodo que se llama al eliminar un registro del sub formulario
     * servicios
     * 
     * @param reg
     */
    public void eliminarRegSubSubpredioservicios(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SERVICIOS_PREDIO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(
                                            FrmprediosControladorEnum.MSM_REGISTRO_ELIMINADO
                                                            .getValue()));
            cargarListaSubpredioservicios();
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * metodo quese llama para cancelar la edicion de un registro del
     * sub formulario servicios
     */
    public void cancelarEdicionSubpredioservicios() {
        cargarListaSubpredioservicios();
    }

    /**
     * metodo que se llama al oprimir el boton edicion de direccion
     */
    public void oprimirEditorDir() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "1" };
        String[] valores = { "1" };
        SessionUtil.cargarModalDatosFlashCerrar("529", modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama al oprimir el boton de adiciones
     */
    public void oprimirBtAdiciones() {
        agregarRegistroNuevo(false);
        // <CODIGO_DESARROLLADO>

        String[] campos = { GeneralParameterEnum.PREDIO.getName()
                        .toLowerCase() };
        String[] valores = { registro.getCampos()
                        .get(FrmprediosControladorEnum.ID_PREDIO.getValue())
                        .toString() };
        SessionUtil.cargarModalDatosFlash("531", modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama al oprimir el boton de avaluos
     */
    public void oprimirBtHistAvaluos() {
        // <CODIGO_DESARROLLADO>
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmprediosControladorUrlEnum.URL16045.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosControladorEnum.ID_PREDIO.getValue(), registro
                        .getCampos()
                        .get(FrmprediosControladorEnum.ID_PREDIO.getValue()));
        Registro reg = null;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        String ano;
        if (reg == null) {
            ano = String.valueOf(SysmanFunciones.ano(new Date()));
        }
        else {
            ano = reg.getCampos().get(
                            FrmprediosControladorEnum.ULTIMOAVALUO.getValue())
                            .toString();
        }
        agregarRegistroNuevo(false);
        String[] campos = { GeneralParameterEnum.PREDIO.getName().toLowerCase(),
                            FrmprediosControladorEnum.ULTIMOAVALUO_A.getValue(),
                            GeneralParameterEnum.PLACA.getName()
                                            .toLowerCase() };
        String[] valores = { registro.getCampos()
                        .get(FrmprediosControladorEnum.ID_PREDIO.getValue())
                        .toString(),
                             ano,
                             registro.getCampos()
                                             .get(FrmprediosControladorEnum.SERIEPLACA
                                                             .getValue())
                                             .toString() };
        SessionUtil.cargarModalDatosFlash("532", modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama al oprimir el boton de descarga la foto
     */
    public void oprimirDescargaFoto() {
        archivoDescarga = null;
        if ("i".equalsIgnoreCase(accion)) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            FrmprediosControladorEnum.TB_TB1656.getValue()));
            return;
        }
        try {
            if (inputStreamFoto == null) {
                JsfUtil.agregarMensajeError(idioma
                                .getString(FrmprediosControladorEnum.TB_TB1952
                                                .getValue()));
                return;
            }
            if ((registroIni.get(FrmprediosControladorEnum.RUTAFOTO
                            .getValue()) != registro.getCampos()
                                            .get(FrmprediosControladorEnum.RUTAFOTO
                                                            .getValue()))
                || (registroIni.get(FrmprediosControladorEnum.CODFOTO
                                .getValue()) != registro.getCampos()
                                                .get(FrmprediosControladorEnum.CODFOTO
                                                                .getValue()))) {
                JsfUtil.agregarMensajeError(idioma
                                .getString(FrmprediosControladorEnum.TB_TB1957
                                                .getValue()));
                JsfUtil.ejecutarJavaScript(
                                "cargarImagen('FR523_nuevo:TS34:IM436')");
                return;
            }
            archivoDescarga = JsfUtil.getArchivoDescarga(inputStreamFoto,
                            nombreFoto);
            cargarFoto();
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(idioma.getString(
                            FrmprediosControladorEnum.TB_TB1959.getValue()));
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * metodo que se llama al oprimir el boton de descarga el plano
     */
    public void oprimirDescargaPlano() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if ("i".equalsIgnoreCase(accion)) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            FrmprediosControladorEnum.TB_TB1960.getValue()));
            return;
        }
        try {
            if (inputStreamPlano == null) {
                JsfUtil.agregarMensajeError(idioma
                                .getString(FrmprediosControladorEnum.TB_TB1962
                                                .getValue()));
                return;
            }
            if ((registroIni.get(FrmprediosControladorEnum.RUTAFOTO
                            .getValue()) == registro.getCampos()
                                            .get(FrmprediosControladorEnum.RUTAFOTO
                                                            .getValue()))
                && (registroIni.get(FrmprediosControladorEnum.CODPLANO
                                .getValue()) == registro.getCampos()
                                                .get(FrmprediosControladorEnum.CODPLANO
                                                                .getValue()))) {
                JsfUtil.agregarMensajeError(idioma
                                .getString(FrmprediosControladorEnum.TB_TB1957
                                                .getValue()));
                JsfUtil.ejecutarJavaScript(
                                "cargarImagen('FR523_nuevo:TS34:IM440')");
                return;
            }
            archivoDescarga = JsfUtil.getArchivoDescarga(inputStreamPlano,
                            nombrePlano);
            cargarPlano();
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(idioma.getString(
                            FrmprediosControladorEnum.TB_TB1959.getValue()));
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama para retornar al modal edicion de direccion
     */
    public void retornarFormularioEditorDir() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null
            && isParametroDireccion(parametrosEntrada)) {
            registro.getCampos().put(GeneralParameterEnum.DIRECCION.getName(),
                            parametrosEntrada.get(GeneralParameterEnum.DIRECCION
                                            .getName().toLowerCase()));
        }
        SessionUtil.cleanFlash();

    }

    /**
     * metodo que se llama para verificar el estado del registro
     * direccion
     * 
     * @param parametrosEntrada
     * @return
     */
    public boolean isParametroDireccion(
        Map<String, Object> parametrosEntrada) {
        return !parametrosEntrada.get(
                        GeneralParameterEnum.DIRECCION.getName().toLowerCase())
                        .toString().trim()
                        .isEmpty();
    }

    /**
     * metodo que se llama para cambiar el uso
     * 
     * @param rowNum
     */
    public void cambiarUsoC(int rowNum) {
        // metodo heredado del bean base
    }

    /**
     * metodo que se llama para cambiar el servicio
     * 
     * @param rowNum
     */
    public void cambiarServicioC(int rowNum) {
        // metodo heredado del bean base
    }

    /**
     * metodo que se llama al cambiar el departamento de donde se
     * registro la escritura
     */
    public void cambiarDptoEscritura() {
        cargarListaCiudadEscritura();
        registro.getCampos().put(
                        FrmprediosControladorEnum.CIUDAD_ESCRITURA.getValue(),
                        "");
    }

    /**
     * metodo que se llama al cambiar el pais
     */
    public void cambiarPAIS() {
        cargarListaDptoEscritura();
        cargarListaCiudadEscritura();
        registro.getCampos().put(
                        FrmprediosControladorEnum.DPTO_ESCRITURA.getValue(),
                        "");
        registro.getCampos().put(
                        FrmprediosControladorEnum.CIUDAD_ESCRITURA.getValue(),
                        "");
    }

    /**
     * metodo que se llama al cambiar el estado de un combo sencillo
     */
    public void cambiarAccAdoquin() {
        if (!registro.getCampos()
                        .get(FrmprediosControladorEnum.ACCADOQUIN.getValue())
                        .equals(false)) {
            registro.getCampos().put(
                            FrmprediosControladorEnum.ACCASFALTO.getValue(),
                            false);
            registro.getCampos().put(
                            FrmprediosControladorEnum.ACCRIGIDO.getValue(),
                            false);

        }
    }

    /**
     * metodo que se llama al cambiar el estado de un combo sencillo
     */
    public void cambiarAccAsfalto() {
        if (!registro.getCampos()
                        .get(FrmprediosControladorEnum.ACCASFALTO.getValue())
                        .equals(false)) {
            registro.getCampos().put(
                            FrmprediosControladorEnum.ACCADOQUIN.getValue(),
                            false);
            registro.getCampos().put(
                            FrmprediosControladorEnum.ACCRIGIDO.getValue(),
                            false);
        }
    }

    /**
     * metodo que se llama al cambiar el estado de un combo sencillo
     */
    public void cambiarAccPrincipal() {
        if (!registro.getCampos()
                        .get(FrmprediosControladorEnum.ACC_PRINCIPAL.getValue())
                        .equals(false)) {
            registro.getCampos().put(
                            FrmprediosControladorEnum.ACC_SECUNDARIA.getValue(),
                            false);
        }
    }

    /**
     * metodo que se llama al cambiar el estado de un combo sencillo
     */
    public void cambiarAccRigido() {
        if (!registro.getCampos()
                        .get(FrmprediosControladorEnum.ACCRIGIDO.getValue())
                        .equals(false)) {
            registro.getCampos().put(
                            FrmprediosControladorEnum.ACCADOQUIN.getValue(),
                            false);
            registro.getCampos().put(
                            FrmprediosControladorEnum.ACCASFALTO.getValue(),
                            false);
        }
    }

    /**
     * metodo que se llama al cambiar el estado de un combo sencillo
     */
    public void cambiarAccSecundaria() {
        if (!registro.getCampos().get(
                        FrmprediosControladorEnum.ACC_SECUNDARIA.getValue())
                        .equals(false)) {
            registro.getCampos().put(
                            FrmprediosControladorEnum.ACC_PRINCIPAL.getValue(),
                            false);
        }
    }

    /**
     * metodo que se llama al cambiar el estado de un combo sencillo
     */
    public void cambiarPredioRegular() {
        if (!registro.getCampos().get(
                        FrmprediosControladorEnum.PREDIO_REGULAR.getValue())
                        .equals(false)) {
            registro.getCampos().put(FrmprediosControladorEnum.PREDIO_IRREGULAR
                            .getValue(), false);
        }
    }

    /**
     * metodo que se llama al cambiar el estado de un combo sencillo
     */
    public void cambiarPredioIrregular() {
        if (!registro.getCampos().get(
                        FrmprediosControladorEnum.PREDIO_IRREGULAR.getValue())
                        .equals(false)) {
            registro.getCampos().put(
                            FrmprediosControladorEnum.PREDIO_REGULAR.getValue(),
                            false);
        }
    }

    /**
     * metodo que se llama al cambiar el check inclinado
     */
    public void cambiarPredioInclinado() {
        if (!registro.getCampos().get(
                        FrmprediosControladorEnum.PREDIO_INCLINADO.getValue())
                        .equals(false)) {
            registro.getCampos().put(
                            FrmprediosControladorEnum.PREDIO_PLANO.getValue(),
                            false);
        }
    }

    /**
     * metodo que se llama al cambiar el check predio plano
     */
    public void cambiarPredioPlano() {
        if (!registro.getCampos()
                        .get(FrmprediosControladorEnum.PREDIO_PLANO.getValue())
                        .equals(false)) {
            registro.getCampos().put(FrmprediosControladorEnum.PREDIO_INCLINADO
                            .getValue(), false);
        }

    }

    /**
     * metodo que se llama al seleccionar un registro de un combo
     * grande
     * 
     * @param event
     */
    public void seleccionarFilaCSeriePlaca(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (registro == null) {
            registro = new Registro(new HashMap<String, Object>());
        }
        registro.getCampos()
                        .put(FrmprediosControladorEnum.SERIEPLACA
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.SERIE
                                                                        .getName()));
        if (verificarVia()) {
            return;
        }
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmprediosControladorUrlEnum.URL16046.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosControladorEnum.SERIEPLACA.getValue(), registro
                        .getCampos()
                        .get(FrmprediosControladorEnum.SERIEPLACA.getValue()));

        Registro rs = null;

        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            FrmprediosControladorEnum.PREDIOS
                                                            .getValue()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rs != null && ACCION_INSERTAR.equals(accion)) {
            cargarRegistro(rs.getLlave(), ACCION_MODIFICAR);
            return;
        }

        registraPredio(registroAux);
    }

    /**
     * metodo que se llama para resetear las variables de origen de
     * control de los campos cuando existe una placa con determinado
     * predio
     */
    public void undoPlaca() {
        registro.getCampos().put(
                        FrmprediosControladorEnum.SERIEPLACA.getValue(), "");
        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), "");
        nombreClasificacion = "";
        registro.getCampos().put(
                        FrmprediosControladorEnum.CODINVENTARIO.getValue(), "");
    }

    /**
     * VERIFICO QUE NO EXISTA NINGUNA VIA CON EL MOVIMIENTO
     * CORRESPONDIENTE
     * 
     * @return
     */
    public boolean verificarVia() {
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmprediosControladorUrlEnum.URL16047.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosControladorEnum.SERIE_PLACA.getValue(),
                        registro.getCampos()
                                        .get(FrmprediosControladorEnum.SERIEPLACA
                                                        .getValue()));
        Registro rs = null;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rs != null) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            FrmprediosControladorEnum.TB_TB1964.getValue()));
            undoPlaca();
            return true;
        }

        return false;
    }

    /**
     * Determina si la placa solicitada ya tiene informacion asociada.
     * 
     * @param strCodigo
     */
    public boolean buscarSeriePlaca() {
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmprediosControladorUrlEnum.URL16046.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosControladorEnum.SERIE_PLACA.getValue(),
                        SysmanFunciones
                                        .nvl(registro.getCampos()
                                                        .get(FrmprediosControladorEnum.SERIEPLACA
                                                                        .getValue()),
                                                        "")
                                        .toString());
        Registro rs = null;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rs != null) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            FrmprediosControladorEnum.TB_TB1965.getValue()));
            undoPlaca();
            return true;
        }
        return false;
    }

    /**
     * Verifica la existencia de la placa.
     * 
     * @param strCodigo
     */
    public void verificarPlaca(String strCodigo) {
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmprediosControladorUrlEnum.URL16050.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosControladorEnum.SERIE_PLACA.getValue(), strCodigo);

        List<Registro> rs = null;
        try {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(url.getUrl(), param));
            if (rs.isEmpty()) {
                JsfUtil.agregarMensajeError(idioma
                                .getString(FrmprediosControladorEnum.TB_TB1966
                                                .getValue()));
                undoPlaca();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que asigna datos al registro
     * 
     * @param codigo
     * @param registroAux
     * @param strCodigo
     */
    public void asignar(String codigo, Registro registroAux, String strCodigo) {
        if (!strCodigo.isEmpty()) {
            registro.getCampos().put(
                            FrmprediosControladorEnum.SERIEPLACA.getValue(),
                            codigo);
            registro.getCampos().put(
                            FrmprediosControladorEnum.CODINVENTARIO.getValue(),
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.ELEMENTO
                                                            .getName()));
            nombreClasificacion = registroAux.getCampos().get(
                            FrmprediosControladorEnum.NOMBRELARGO.getValue())
                            .toString();
            registro.getCampos().put(
                            FrmprediosControladorEnum.RESPONSABLE.getValue(),
                            registroAux.getCampos()
                                            .get(FrmprediosControladorEnum.RESPONSABLE
                                                            .getValue()));
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL_RESPONSABLE
                                                            .getName()));
        }
    }

    /**
     * metodo de verificacion de existencia de predios que estan
     * relacionados con las placas
     * 
     * @param registroAux
     */
    public void registrarPredioElse(Registro registroAux) {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmprediosControladorEnum.ID_PREDIO.getValue())) {

            if (verificarVia()) {
                return;
            }

            String strCodigo = SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(FrmprediosControladorEnum.SERIEPLACA
                                                            .getValue()),
                                            "")
                            .toString();

            String codigo = registro.getCampos().get(
                            FrmprediosControladorEnum.SERIEPLACA.getValue())
                            .toString();
            undoPlaca();

            if (buscarSeriePlaca()) {
                return;
            }

            verificarPlaca(strCodigo);
            asignar(codigo, registroAux, strCodigo);

        }
        else if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmprediosControladorEnum.ID_PREDIO.getValue())
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            FrmprediosControladorEnum.CODINVENTARIO
                                            .getValue())) {

            if (verificarVia()) {
                return;
            }

            registro.getCampos().put(
                            FrmprediosControladorEnum.CODINVENTARIO.getValue(),
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.ELEMENTO
                                                            .getName()));
            nombreClasificacion = registroAux.getCampos().get(
                            FrmprediosControladorEnum.NOMBRELARGO.getValue())
                            .toString();
            registro.getCampos().put(
                            FrmprediosControladorEnum.RESPONSABLE.getValue(),
                            registroAux.getCampos()
                                            .get(FrmprediosControladorEnum.RESPONSABLE
                                                            .getValue()));
            registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL_RESPONSABLE
                                                            .getName()));

        }
        else if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmprediosControladorEnum.ID_PREDIO.getValue())
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            FrmprediosControladorEnum.COD_CLASIFICACION
                                            .getValue())) {
            accion = "i";
        }
    }

    /**
     * metodo que se llama para registrar un predio
     * 
     * @param registroAux
     */
    public void registraPredio(Registro registroAux) {

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmprediosControladorEnum.SERIEPLACA.getValue())) {
            undoPlaca();
        }
        else {
            registrarPredioElse(registroAux);
        }

        //// modificacion version 2007.09.01
        //// traer de almacen la vida util de la placa seleccionada..
        modificarVersion2007(registroAux);

    }

    /**
     * metodo que trae la vida util de la placa seleccionada
     * 
     * @param registroAux
     */
    private void modificarVersion2007(Registro registroAux) {
        UrlBean url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmprediosControladorUrlEnum.URL16048.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmprediosControladorEnum.SERIE_PLACA.getValue(),
                        SysmanFunciones.nvl(
                                        SysmanFunciones.validarCampoVacio(
                                                        registro.getCampos(),
                                                        FrmprediosControladorEnum.SERIEPLACA
                                                                        .getValue())
                                                                            ? null
                                                                            : registro.getCampos()
                                                                                            .get(FrmprediosControladorEnum.SERIEPLACA
                                                                                                            .getValue()),
                                        -1));
        Registro rs = null;
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        String acc = accion;

        if (rs != null) {
            accion = "v";
            visibleAnulada = FrmprediosControladorEnum.BLOCK.getValue();
            bloqSeriePlaca = false;
        }
        else {
            accion = acc;
            visibleAnulada = FrmprediosControladorEnum.NONE.getValue();
        }

        registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
    }

    /**
     * metodo que se invoca cuando se abre el formulario
     */
    @Override
    public void abrirFormulario() {
        // Codigo
    }

    /**
     * metodo que se llama al cargar el registro
     */
    @Override
    public void cargarRegistro() {
        precargarRegistro();
        if (!"i".equals(accion)) {
            UrlBean url = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmprediosControladorUrlEnum.URL16049
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmprediosControladorEnum.CODINVENTARIO.getValue(),
                            registro.getCampos()
                                            .get(FrmprediosControladorEnum.CODINVENTARIO
                                                            .getValue()));

            Registro rs = null;
            try {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(url.getUrl(), param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            if (rs != null) {
                nombreClasificacion = SysmanFunciones
                                .nvl(rs.getCampos()
                                                .get(FrmprediosControladorEnum.NOMBRELARGO
                                                                .getValue()),
                                                "")
                                .toString();
            }
            else {
                nombreClasificacion = "";
            }
        }
        else {
            registro = new Registro(new HashMap<String, Object>());
            nombreClasificacion = null;
            stringFoto = null;
            inputStreamFoto = null;
            stringPlano = null;
            inputStreamPlano = null;
        }
    }

    /**
     * metodo que adiciona el separador a la ruta donde va a quedar
     * guardadas las imagenes
     * 
     * @param var
     */
    private void addBarraRuta(String var) {

        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(), var)) {
            String ruta = registro.getCampos().get(var).toString();
            if (!ruta.endsWith(File.separator)) {
                ruta = ruta + File.separator;
                registro.getCampos().put(var, ruta);
            }
        }
        else {
            registro.getCampos().put(var, "");
        }
    }

    /**
     * metodo que se invoca para agregar informacion a los registros
     * null o dato del mismo registro
     * 
     * @param var
     */
    private void addRegistroVacios(String var) {
        registro.getCampos().put(var,
                        SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                        var) ? null
                                            : registro.getCampos().get(var));
    }

    /**
     * metodo que asegura que esten todos los registros para el
     * servicio insert y update
     */
    private void addAllRegistros() {
        addRegistroVacios(FrmprediosControladorEnum.EXT_LINDEROS_FRENTE
                        .getValue());
        addRegistroVacios(FrmprediosControladorEnum.ACC_ADOQUIN.getValue());
        addRegistroVacios(GeneralParameterEnum.SUCURSAL.getName());
        addRegistroVacios(
                        FrmprediosControladorEnum.PREDIO_IRREGULAR.getValue());
        addRegistroVacios(FrmprediosControladorEnum.NOTARIA.getValue());
        addRegistroVacios(FrmprediosControladorEnum.ACC_PRINCIPAL.getValue());
        addRegistroVacios(FrmprediosControladorEnum.RUTA_FOTO.getValue());
        addRegistroVacios(
                        FrmprediosControladorEnum.EXT_LINDEROS_IZQ.getValue());
        addRegistroVacios(FrmprediosControladorEnum.NECESITA_INVENTARIO
                        .getValue());
        addRegistroVacios(FrmprediosControladorEnum.PREDIO_PLANO.getValue());
        addRegistroVacios(
                        FrmprediosControladorEnum.DESC_LINDEROS_IZQ.getValue());
        addRegistroVacios(FrmprediosControladorEnum.FECHA_ESCRITURA.getValue());
        addRegistroVacios(FrmprediosControladorEnum.DPTO_ESCRITURA.getValue());
        addRegistroVacios(FrmprediosControladorEnum.ESCRITURA_NO.getValue());
        addRegistroVacios(FrmprediosControladorEnum.MODALIDAD.getValue());
        addRegistroVacios(FrmprediosControladorEnum.DESC_LINDEROS_FONDO
                        .getValue());
        addRegistroVacios(FrmprediosControladorEnum.COD_PREDIAL.getValue());
        addRegistroVacios(
                        FrmprediosControladorEnum.CIUDAD_ESCRITURA.getValue());
        addRegistroVacios(FrmprediosControladorEnum.PREDIO_REGULAR.getValue());
        addRegistroVacios(
                        FrmprediosControladorEnum.PREDIO_INCLINADO.getValue());
        addRegistroVacios(FrmprediosControladorEnum.COD_FOTO.getValue());
        addRegistroVacios(FrmprediosControladorEnum.ACC_SECUNDARIA.getValue());
        addRegistroVacios(FrmprediosControladorEnum.NOMBRE.getValue());
        addRegistroVacios(FrmprediosControladorEnum.ID_PREDIO.getValue());
        addRegistroVacios(FrmprediosControladorEnum.COD_INVENTARIO.getValue());
        addRegistroVacios(
                        FrmprediosControladorEnum.ANO_CONSTRUCCION.getValue());
        addRegistroVacios(GeneralParameterEnum.DIRECCION.getName());
        addRegistroVacios(FrmprediosControladorEnum.DESTINACION.getValue());
        addRegistroVacios(FrmprediosControladorEnum.DESC_LINDEROS_FRENTE
                        .getValue());
        addRegistroVacios(
                        FrmprediosControladorEnum.DESC_LINDEROS_DER.getValue());
        addRegistroVacios(
                        FrmprediosControladorEnum.MATR_INMOVILIARIA.getValue());
        addRegistroVacios(FrmprediosControladorEnum.RESPONSABLE.getValue());
        addRegistroVacios(GeneralParameterEnum.COMPANIA.getName());
        addRegistroVacios(FrmprediosControladorEnum.PAIS.getValue());
        addRegistroVacios(FrmprediosControladorEnum.TIPO_PREDIO.getValue());
        addRegistroVacios(FrmprediosControladorEnum.EXT_LINDEROS_FONDO
                        .getValue());
        addRegistroVacios(FrmprediosControladorEnum.COD_PLANO.getValue());
        addRegistroVacios(FrmprediosControladorEnum.OBSERVACIONES.getValue());
        addRegistroVacios(FrmprediosControladorEnum.UBICACION.getValue());
        addRegistroVacios(FrmprediosControladorEnum.ACC_RIGIDO.getValue());
        addRegistroVacios(FrmprediosControladorEnum.ACC_ASFALTO.getValue());
        addRegistroVacios(FrmprediosControladorEnum.SERIE_PLACA.getValue());
        addRegistroVacios(
                        FrmprediosControladorEnum.EXT_LINDEROS_DER.getValue());
        addRegistroVacios(FrmprediosControladorEnum.AREA_ESCRITURA.getValue());
        addRegistroVacios(FrmprediosControladorEnum.RUTA_PLANO.getValue());
        addRegistroVacios(FrmprediosControladorEnum.ENCUESTA_VALOR.getValue());
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        String strconsecutivo;
        long dblconsecutivo;
        try {
            dblconsecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            FrmprediosControladorEnum.PREDIOS.getValue(),
                            "COMPANIA =''" + compania + "''",
                            FrmprediosControladorEnum.ID_PREDIO.getValue(),
                            "1");

            String aux = Long.toString(dblconsecutivo);
            strconsecutivo = SysmanFunciones.padl(aux, 10, "0");
            registro.getCampos().put(
                            FrmprediosControladorEnum.ID_PREDIO.getValue(),
                            strconsecutivo);
            addBarraRuta(FrmprediosControladorEnum.RUTAFOTO.getValue());
            addBarraRuta(FrmprediosControladorEnum.RUTAPLANO.getValue());
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmprediosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        addAllRegistros();
        return true;
    }

    @Override
    public boolean insertarDespues() {
        accion = null;
        if (imagenBytesFoto != null && nombreFoto != null && rutaFoto != null) {
            rutaFoto = rutaFoto.endsWith(File.separator) ? rutaFoto
                : rutaFoto + File.separator;
            JsfUtil.upload(imagenBytesFoto, rutaFoto, nombreFoto);
        }
        if (imagenBytesPlano != null && nombrePlano != null
            && rutaPlano != null) {
            rutaPlano = rutaPlano.endsWith(File.separator) ? rutaPlano
                : rutaPlano + File.separator;
            JsfUtil.upload(imagenBytesPlano, rutaPlano, nombrePlano);
        }
        cargarFoto();
        cargarPlano();
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarAntes() {
        if ("m".equals(accion)) {
            registro.getCampos().remove(
                            GeneralParameterEnum.DATE_CREATED.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CREATED_BY.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(
                            FrmprediosControladorEnum.ID_PREDIO.getValue());

        }
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean actualizarDespues() {
        nombreFoto = nombreFoto.replace(File.separator, "");
        nombrePlano = nombrePlano.replace(File.separator, "");

        if (imagenBytesFoto != null && nombreFoto != null && rutaFoto != null) {
            rutaFoto = rutaFoto.endsWith(File.separator) ? rutaFoto
                : rutaFoto + File.separator;

            JsfUtil.upload(imagenBytesFoto, rutaFoto, nombreFoto);
        }
        if (imagenBytesPlano != null && nombrePlano != null
            && rutaPlano != null) {
            rutaPlano = rutaPlano.endsWith(File.separator) ? rutaPlano
                : rutaPlano + File.separator;

            JsfUtil.upload(imagenBytesPlano, rutaPlano, nombrePlano);
        }
        cargarFoto();
        cargarPlano();
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    /**
     * metodo heredado del bean padre
     */
    public void cargarFoto() {
        String ruta = (registro.getCampos().get(
                        FrmprediosControladorEnum.RUTAFOTO.getValue()) == null
                            ? ""
                            : (registro.getCampos()
                                            .get(FrmprediosControladorEnum.RUTAFOTO
                                                            .getValue())
                                            .toString()
                                            .endsWith(File.separator)
                                                ? registro.getCampos()
                                                                .get(FrmprediosControladorEnum.RUTAFOTO
                                                                                .getValue())
                                                : registro.getCampos()
                                                                .get(FrmprediosControladorEnum.RUTAFOTO
                                                                                .getValue())
                                                    + File.separator))
            + "" + (registro.getCampos().get(FrmprediosControladorEnum.CODFOTO
                            .getValue()) == null ? ""
                                : registro.getCampos()
                                                .get(FrmprediosControladorEnum.CODFOTO
                                                                .getValue()));
        nombreFoto = registro.getCampos().get(
                        FrmprediosControladorEnum.CODFOTO.getValue()) == null
                            ? ""
                            : registro.getCampos()
                                            .get(FrmprediosControladorEnum.CODFOTO
                                                            .getValue())
                                            .toString();
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmprediosControladorEnum.CODFOTO.getValue())) {
            if (!SysmanFunciones.validarVariableVacio(ruta)
                && !SysmanFunciones.validarVariableVacio(nombreFoto)) {
                try {
                    stringFoto = JsfUtil.encodeImage(ruta);
                    inputStreamFoto = new FileInputStream(new File(ruta));

                }
                catch (FileNotFoundException ex) {
                    Logger.getLogger(FrmprediosControlador.class
                                    .getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.agregarMensajeError(idioma
                                    .getString(FrmprediosControladorEnum.TB_TB1969
                                                    .getValue()));
                    stringFoto = null;
                    inputStreamFoto = null;
                }
            }
            else {
                stringFoto = null;
                inputStreamFoto = null;
            }
        }
        else {
            stringFoto = null;
            inputStreamFoto = null;
            JsfUtil.agregarMensajeInformativo(idioma.getString(
                            FrmprediosControladorEnum.TB_TB3129.getValue())
                            .replace(
                                            FrmprediosControladorEnum.REEMPLAZO
                                                            .getValue(),
                                            FrmprediosControladorEnum.FOTO
                                                            .getValue()));
        }
    }

    /**
     * metodo heredado del bean padre
     */
    public void cargarPlano() {
        String ruta = (registro.getCampos().get(
                        FrmprediosControladorEnum.RUTAFOTO.getValue()) == null
                            ? ""
                            : registro.getCampos()
                                            .get(FrmprediosControladorEnum.RUTAFOTO
                                                            .getValue()))
            + ""
            + (registro.getCampos().get(FrmprediosControladorEnum.CODPLANO
                            .getValue()) == null ? ""
                                : registro.getCampos()
                                                .get(FrmprediosControladorEnum.CODPLANO
                                                                .getValue()));
        nombrePlano = registro.getCampos().get(
                        FrmprediosControladorEnum.CODPLANO.getValue()) == null
                            ? ""
                            : registro.getCampos()
                                            .get(FrmprediosControladorEnum.CODPLANO
                                                            .getValue())
                                            .toString();
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        FrmprediosControladorEnum.CODPLANO.getValue())) {
            if (!SysmanFunciones.validarVariableVacio(ruta)
                && !SysmanFunciones.validarVariableVacio(nombrePlano)) {
                try {
                    stringPlano = JsfUtil.encodeImage(ruta);
                    inputStreamPlano = new FileInputStream(new File(ruta));

                }
                catch (FileNotFoundException ex) {
                    Logger.getLogger(FrmprediosControlador.class
                                    .getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.agregarMensajeError(idioma
                                    .getString(FrmprediosControladorEnum.TB_TB1971
                                                    .getValue()));
                    stringPlano = null;
                    inputStreamPlano = null;
                }
            }
            else {
                stringPlano = null;
                inputStreamPlano = null;
            }
        }
        else {
            stringPlano = null;
            inputStreamPlano = null;
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(FrmprediosControladorEnum.TB_TB3129
                                            .getValue())
                            .replace(FrmprediosControladorEnum.REEMPLAZO
                                            .getValue(),
                                            FrmprediosControladorEnum.PLANO
                                                            .getValue()));
        }
    }

    /**
     * metodos get y set
     * 
     * @return
     */
    public String getStringFoto() {
        return stringFoto;
    }

    public void setStringFoto(String stringFoto) {
        this.stringFoto = stringFoto;
    }

    public List<Registro> getListaUBICACION() {
        return listaUBICACION;
    }

    public void setListaUBICACION(List<Registro> listaUBICACION) {
        this.listaUBICACION = listaUBICACION;
    }

    public List<Registro> getListaNOTARIA() {
        return listaNOTARIA;
    }

    public void setListaNOTARIA(List<Registro> listaNOTARIA) {
        this.listaNOTARIA = listaNOTARIA;
    }

    public List<Registro> getListaDptoEscritura() {
        return listaDptoEscritura;
    }

    public void setListaDptoEscritura(List<Registro> listaDptoEscritura) {
        this.listaDptoEscritura = listaDptoEscritura;
    }

    public List<Registro> getListaCiudadEscritura() {
        return listaCiudadEscritura;
    }

    public void setListaCiudadEscritura(List<Registro> listaCiudadEscritura) {
        this.listaCiudadEscritura = listaCiudadEscritura;
    }

    public List<Registro> getListaMODALIDAD() {
        return listaMODALIDAD;
    }

    public void setListaMODALIDAD(List<Registro> listaMODALIDAD) {
        this.listaMODALIDAD = listaMODALIDAD;
    }

    public List<Registro> getListaTipoPredio() {
        return listaTipoPredio;
    }

    public void setListaTipoPredio(List<Registro> listaTipoPredio) {
        this.listaTipoPredio = listaTipoPredio;
    }

    public List<Registro> getListaRESPONSABLE() {
        return listaRESPONSABLE;
    }

    public void setListaRESPONSABLE(List<Registro> listaRESPONSABLE) {
        this.listaRESPONSABLE = listaRESPONSABLE;
    }

    public List<Registro> getListaPAIS() {
        return listaPAIS;
    }

    public void setListaPAIS(List<Registro> listaPAIS) {
        this.listaPAIS = listaPAIS;
    }

    public List<Registro> getListaUso() {
        return listaUso;
    }

    public void setListaUso(List<Registro> listaUso) {
        this.listaUso = listaUso;
    }

    public List<Registro> getListaServicio() {
        return listaServicio;
    }

    public void setListaServicio(List<Registro> listaServicio) {
        this.listaServicio = listaServicio;
    }

    public List<Registro> getListaSubprediousos() {
        return listaSubprediousos;
    }

    public void setListaSubprediousos(List<Registro> listaSubprediousos) {
        this.listaSubprediousos = listaSubprediousos;
    }

    public List<Registro> getListaSubpredioservicios() {
        return listaSubpredioservicios;
    }

    public void setListaSubpredioservicios(
        List<Registro> listaSubpredioservicios) {
        this.listaSubpredioservicios = listaSubpredioservicios;
    }

    public RegistroDataModelImpl getListaCSeriePlaca() {
        return listaCSeriePlaca;
    }

    public void setListaCSeriePlaca(RegistroDataModelImpl listaCSeriePlaca) {
        this.listaCSeriePlaca = listaCSeriePlaca;
    }

    public Registro getRegistroSubSubPredioUsos() {
        return registroSubSubPredioUsos;
    }

    public void setRegistroSubSubPredioUsos(Registro registroSubSubPredioUsos) {
        this.registroSubSubPredioUsos = registroSubSubPredioUsos;
    }

    public Registro getRegistroSubSubPredioServicios() {
        return registroSubSubPredioServicios;
    }

    public void setRegistroSubSubPredioServicios(
        Registro registroSubSubPredioServicios) {
        this.registroSubSubPredioServicios = registroSubSubPredioServicios;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getStringPlano() {
        return stringPlano;
    }

    public void setStringPlano(String stringPlano) {
        this.stringPlano = stringPlano;
    }

    public String getNombreClasificacion() {
        return nombreClasificacion;
    }

    public void setNombreClasificacion(String nombreClasificacion) {
        this.nombreClasificacion = nombreClasificacion;
    }

    public boolean isBloqSeriePlaca() {
        return bloqSeriePlaca;
    }

    public void setBloqSeriePlaca(boolean bloqSeriePlaca) {
        this.bloqSeriePlaca = bloqSeriePlaca;
    }

    public String getVisibleAnulada() {
        return visibleAnulada;
    }

    public void setVisibleAnulada(String visibleAnulada) {
        this.visibleAnulada = visibleAnulada;
    }

    public String getAnulada() {
        return anulada;
    }

    public void setAnulada(String anulada) {
        this.anulada = anulada;
    }

}
