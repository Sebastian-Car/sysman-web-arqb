package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CompaniasControladorEnum;
import com.sysman.general.enums.CompaniasControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaNueveGeneralRemote;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author lcortes
 * @version 1, 13/11/2015
 * @version 2, 04/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos y en el
 * subformulario.
 * @version 3, 06/04/2017
 * @author jsforero se crea un check para que el usuario pueda decidir
 * si se crea el plan contable o no
 * @author spina - refactorizo conexiones
 * @version 4, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class CompaniasControlador extends BeanBaseDatosAcmeImpl {

    private Registro registroSub;
    private List<Registro> listaPais;
    private List<Registro> listaDepartamento;
    private List<Registro> listaCiudad;
    private List<Registro> listaCodigoTpEntidad;
    private RegistroDataModelImpl listaNITCompania;
    private RegistroDataModelImpl listaNITCompaniaE;
    private RegistroDataModelImpl listaCodigoSchip;
    private List<Registro> listaMoneda;
    private List<Registro> listaSubconsolidada;
    private String companiaBase;
    private int anoPlanContable;
    private boolean crearDatosVisible;
    private String auxiliar;
    private String pais;
    private String departamento;
    private String ciudad;
    private String nitCompania;
    private String nombreCompania;
    private UploadedFile archivoCargaRutaImagen;
    private UploadedFile archivoCargaRutaSticker;
    private String infImagen;
    private String infSticker;
    private String directorioSticker;
    private String directorio;
    private String iconoCompania;
    private String nombreIconoCompania;
    private String stickerCompania;
    private String nombreStickerCompania;
    private InputStream inputStreamImagen;
    private InputStream inputStreamImagenCns;
    private InputStream inputStreamSticker;
    private String iconoCnsc;
    private String infImagenCns;
    private String directorioCns;
    private String nombreIconoCns;
    private String nombreEntidadReciproca;
    private boolean creaPlan;
    private static final String CODIGODEPTO = "CODIGODEPTO";
    private static final String CODCIUDAD = "CODCIUDAD";
    private static final String CODIGO = "CODIGO";
    private static final String CONSOLIDADA = "CONSOLIDADA";
    private static final String NOMBRECOMP = "NOMBRECOMP";
    private static final String COMPANIACON = "COMPANIACON";
    private static final String CODMONEDA = "CODMONEDA";
    private String companiaNomina;
    private int anioNomina;
    private boolean aplicaAlmacen;
    /**
     * Arreglo de bytes que contiene la imagen de la compañia que se
     * carga desde el componente de Primefaces.
     */
    private byte[] imagenBytesCns;
    /**
     * Arreglo de bytes que contiene la imagen de la compaÃ±ia que se
     * carga desde el componente de Primefaces.
     */
    private byte[] imagenBytes;
    /**
     * Arreglo de bytes que contiene el sticker de la compaÃ±ia que se
     * carga desde el componente de Primefaces.
     */
    private byte[] stickerBytes;
    /**
     * lista los regimenes
     */
    private List<Registro> listaRegimen;

    @EJB
    private EjbPrepararAnoRemote ejbPrepararAno;

    /**
     * Ejb para actualizar el indicador de cune en los empleados
     */
    @EJB
    private EjbNominaNueveGeneralRemote ejbNomina;
    
    @EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

    public CompaniasControlador() {
        super();
        try {
            numFormulario = GeneralCodigoFormaEnum.COMPANIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(CompaniasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.COMPANIA;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }

    @Override
    public void iniciarListas() {
        cargarListaPais();
        cargarListaMoneda();
        cargarListaCodigoSchip();
        cargarlistaCodigoTpEntidad();
        cargarListaRegimen();

    }

    @Override
    public void iniciarListasSub() {
        pais = registro.getCampos().get("PAIS") == null ? " "
            : registro.getCampos().get("PAIS").toString();
        departamento = registro.getCampos().get(CODIGODEPTO) == null ? " "
            : registro.getCampos().get(CODIGODEPTO).toString();
        ciudad = registro.getCampos().get(CODCIUDAD) == null ? " "
            : registro.getCampos().get(CODCIUDAD).toString();
        cargarListaDepartamento();
        cargarListaCiudad();
        cargarListaSubconsolidada();
        cargarListaNITCompania();
        cargarListaNITCompaniaE();
        cargarIconoCns();
        cargarIconoCompania();
        cargarStickerCompania();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubconsolidada = null;
    }

    @Override
    public void abrirFormulario() {
        companiaBase = SessionUtil.getCompania();
        anoPlanContable = SysmanFunciones
                        .ano(new Date());
        companiaNomina = SessionUtil.getCompania();
        anioNomina = SysmanFunciones
                        .ano(new Date());
        infImagenCns = "";
        infImagen = "";
        infSticker = "";
        directorio = null;
        directorioCns = null;
        iconoCnsc = null;
        iconoCompania = null;
        inputStreamImagen = null;
        inputStreamImagenCns = null;
        directorioSticker = null;
        stickerCompania = null;
        inputStreamSticker = null;
        try {
        	aplicaAlmacen = SysmanFunciones.nvl(
        			ejbSysmanUtil.consultarParametro(companiaBase, "ALMACEN INTERCOMPANIAS", "-1", new Date(), true),
        			"NO").toString().equals("SI")?true:false;
        } catch (SystemException ex) {
        	logger.error(ex.getMessage(), ex);

        }
    }

    public void cargarListaSubconsolidada() {
        Map<String, Object> param = new TreeMap<>();
        param.put(CompaniasControladorEnum.PARAM2.getValue(),
                        registro.getCampos().get(CODIGO));

        try {
            listaSubconsolidada = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CompaniasControladorUrlEnum.URL6080
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            CONSOLIDADA));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPais() {
        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CompaniasControladorUrlEnum.URL7080
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDepartamento() {
        Map<String, Object> param = new TreeMap<>();
        param.put(CompaniasControladorEnum.PARAM0.getValue(), pais);

        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CompaniasControladorUrlEnum.URL7434
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que carga la lista de regimen
     */
    public void cargarListaRegimen() {
        try {
            listaRegimen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		CompaniasControladorUrlEnum.URL22001
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    public void cargarListaCiudad() {
        Map<String, Object> param = new TreeMap<>();
        param.put(CompaniasControladorEnum.PARAM1.getValue(), departamento);
        param.put(CompaniasControladorEnum.PARAM0.getValue(), pais);

        try {
            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CompaniasControladorUrlEnum.URL8002
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaNITCompania() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CompaniasControladorUrlEnum.URL8520
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CompaniasControladorEnum.PARAM2.getValue(),
                        registro.getCampos().get(CODIGO));

        listaNITCompania = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaNITCompaniaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CompaniasControladorUrlEnum.URL9281
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(CompaniasControladorEnum.PARAM2.getValue(),
                        registro.getCampos().get(CODIGO));

        listaNITCompaniaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaCodigoSchip
     *
     */
    public void cargarListaCodigoSchip() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CompaniasControladorUrlEnum.URL8543
                                                        .getValue());
        listaCodigoSchip = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaMoneda() {
        try {
            listaMoneda = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CompaniasControladorUrlEnum.URL9517
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * 
     */
    public void cargarlistaCodigoTpEntidad() {
    	Map<String, Object> param = new TreeMap<>();
        param.put(CompaniasControladorEnum.PARAM3.getValue(), Integer.parseInt(CompaniasControladorEnum.CATEGORIA.getValue()));
   
    	try {
			listaCodigoTpEntidad = RegistroConverter.toListRegistro(
			        requestManager.getList(UrlServiceUtil.getInstance()
			                .getUrlServiceByUrlByEnumID(
			                                CompaniasControladorUrlEnum.URL1032
			                                                .getValue())
			                .getUrl(), param));
		} catch (SystemException e) {
			 logger.error(e.getMessage(), e);
	         JsfUtil.agregarMensajeError(e.getMessage());
		}
    }

    public void agregarRegistroSubSubconsolidada() {
        try {
            registro.getCampos().get(CODIGO);
            registroSub.getCampos().put("NITCOMPANIA", nitCompania);
            registroSub.getCampos().remove(NOMBRECOMP);
            registroSub.getCampos().remove("NIT");
            registroSub.getCampos().remove("NOMBCOMPANIA");
            registroSub.getCampos().remove("NOMBRE");
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSOLIDADA
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaSubconsolidada();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(CompaniasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubconsolidada(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            registroSub.getCampos().put(COMPANIACON,
                            registro.getCampos().get(CODIGO));
            reg.getCampos().put("NITCOMPANIA", nitCompania);
            reg.getCampos().remove(NOMBRECOMP);
            reg.getCampos().remove(NOMBRECOMP);
            reg.getCampos().remove("NIT");
            reg.getCampos().remove("NOMBCOMPANIA");
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSOLIDADA
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(CompaniasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubconsolidada();
        }
    }

    public void eliminarRegSubSubconsolidada(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSOLIDADA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubconsolidada();
        }
        catch (SystemException ex) {
            Logger.getLogger(CompaniasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubconsolidada() {
        cargarListaSubconsolidada();
    }

    public void oprimirCrear(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
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
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2839"));
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + "<br>"
                                + ex.getMessage());
        }
        return bytes;
    }

    public void cargarArchivoLectorImagenCns(FileUploadEvent event) {

        UploadedFile archivoImagen = event.getFile();
        nombreIconoCns = archivoImagen.getFileName();
        imagenBytesCns = getFileContent(archivoImagen);
        iconoCnsc = JsfUtil.encodeImage(imagenBytesCns);

    }

    public void cargarArchivoLectorImagen(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        UploadedFile archivoImagen = event.getFile();
        nombreIconoCompania = archivoImagen.getFileName();
        imagenBytes = getFileContent(archivoImagen);
        iconoCompania = JsfUtil.encodeImage(imagenBytes);
    }

    public void cargarArchivoLectorSticker(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        UploadedFile archivoImagen = event.getFile();
        nombreStickerCompania = archivoImagen.getFileName();
        stickerBytes = getFileContent(archivoImagen);
        stickerCompania = JsfUtil.encodeImage(stickerBytes);
    }

    public void cambiarPais() {
        // <CODIGO_DESARROLLADO>
        pais = registro.getCampos().get("PAIS") == null ? " "
            : registro.getCampos().get("PAIS").toString();
        registro.getCampos().put(CODIGODEPTO, "");
        registro.getCampos().put(CODCIUDAD, "");
        cargarListaDepartamento();
        listaCiudad = null;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCkManejaCune() {
        try {
            boolean activa = (boolean) registro.getCampos()
                            .get("GENERA_CUNE");
            ejbNomina.actIndCuneEmpleado(
                            registro.getCampos().get(CODIGO).toString(),
                            activa);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarDepartamento() {
        // <CODIGO_DESARROLLADO>
        departamento = registro.getCampos().get(CODIGODEPTO) == null ? " "
            : registro.getCampos().get(CODIGODEPTO).toString();
        cargarListaCiudad();
        // </CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control DgNomina
     * 
     * 
     */
    public void cambiarDgNomina() {
    	//<CODIGO_DESARROLLADO>
    	//</CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar
     * del dialogo DgNomina en la vista
     *
     *
     */
    public void aceptarDgNomina() {
    	//<CODIGO_DESARROLLADO>
    	if (SysmanFunciones.validarVariableVacio(companiaNomina)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB87"));
            return;
        }
        String codigoDestino = registro.getCampos().get(CODIGO) == null ? " "
            : registro.getCampos().get(CODIGO).toString();
        
        try {
			ejbPrepararAno.crearDatosNomina(companiaNomina, anioNomina, codigoDestino);
			
			 JsfUtil.agregarMensajeInformativo(
                     idioma.getString("MSM_PROCESO_EJECUTADO"));
			 
		} catch (SystemException e) {
			 Logger.getLogger(CompaniasControlador.class.getName())
             .log(Level.SEVERE, null, e);
             JsfUtil.agregarMensajeError(e.getMessage());
		}
        
    	//</CODIGO_DESARROLLADO>
    }

    public void aceptarCOMPANIA() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(companiaBase)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB87"));
            return;
        }
        String codigo = registro.getCampos().get(CODIGO) == null ? " "
            : registro.getCampos().get(CODIGO).toString();
        try {

            ejbPrepararAno.crearRegistrosBasicos(codigo, anoPlanContable);

            if (creaPlan) {
                ejbPrepararAno.crearDatosContables(companiaBase,
                                anoPlanContable, codigo);

            }
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            Logger.getLogger(CompaniasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNITCompaniaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        nitCompania = registroSub.getCampos().get("NIT") == null ? " "
            : registroSub.getCampos().get("NIT").toString();
        nombreCompania = registroSub.getCampos().get(NOMBRECOMP) == null ? " "
            : registroSub.getCampos().get(NOMBRECOMP).toString();
        listaSubconsolidada.get(rowNum).getCampos().put("NIT", nitCompania);
        listaSubconsolidada.get(rowNum).getCampos().put(NOMBRECOMP,
                        nombreCompania);
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * @param event
     */

    public void seleccionarFilaNITCompania(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nitCompania = registroAux.getCampos().get("NIT") == null ? " "
            : registroAux.getCampos().get("NIT").toString();
        nombreCompania = registroAux.getCampos().get(NOMBRECOMP) == null ? " "
            : registroAux.getCampos().get(NOMBRECOMP).toString();
        registroSub.getCampos().put(COMPANIACON,
                        registro.getCampos().get(CODIGO));
        registroSub.getCampos().put("NIT", nitCompania);
        registroSub.getCampos().put(NOMBRECOMP, nombreCompania);
    }

    public void seleccionarFilaNITCompaniaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("NIT") == null ? " "
            : registroAux.getCampos().get("NIT").toString();
        nombreCompania = registroAux.getCampos().get(NOMBRECOMP) == null ? " "
            : registroAux.getCampos().get(NOMBRECOMP).toString();
        registroSub.getCampos().put("NIT", auxiliar);
        registroSub.getCampos().put(NOMBRECOMP, nombreCompania);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoSchip
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoSchip(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGOSCHIP",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreEntidadReciproca = registroAux.getCampos().get(
                        GeneralParameterEnum.NOMBRE.getName()).toString();
    }
    /**
     * 
     */
//    public void seleccionarFilaCodigoTpEntidad(SelectEvent event) {
//    	Registro registroAux = (Registro) event.getObject();
//    	
//    	registro.getCampos().put("TIPOENTIDAD",
//                registroAux.getCampos().get(
//                                GeneralParameterEnum.CODIGO.getName()));
//    	registro.getCampos().put("NOMBREENTIDAD",
//                registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
//    }
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (ACCION_INSERTAR.equals(accion)) {
            crearDatosVisible = false;
            listaCiudad = null;
            listaDepartamento = null;
            pais = "";
            cargarListaPais();
            iconoCompania = "";
            iconoCnsc = "";
            stickerCompania = "";
            directorio = "";
            directorioCns = "";
            directorioSticker = "";
            inputStreamImagen = null;
            inputStreamSticker = null;
            inputStreamImagenCns = null;
            infImagen = "";
            infImagenCns = "";
            infSticker = "";

            imagenBytes = null;
            nombreIconoCompania = null;
            stickerBytes = null;
            nombreStickerCompania = null;
            imagenBytesCns = null;
            nombreIconoCns = null;
            nombreEntidadReciproca = null;
        }
        else {
            crearDatosVisible = true;
            nombreEntidadReciproca = SysmanFunciones
                            .nvl(registro.getCampos().get("NOMBRESCHIP"), "")
                            .toString();
        }

    }

    public boolean validarArchivoImagenCns() {
        if (!SysmanFunciones.validarVariableVacio(iconoCnsc)) {
            try {
                File ficheroImagen = new File(directorioCns);
                String subExtImagen = nombreIconoCns.substring(
                                nombreIconoCns.lastIndexOf('.'),
                                nombreIconoCns.length());

                if (SysmanFunciones.validarVariableVacio(directorioCns)) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB2851"));
                    return false;
                }

                if (!tieneExtensionValida(nombreIconoCns)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2852"));
                    return false;
                }

                String msg = idioma.getString("TB_TB2853");
                String prefijo = idioma.getString("TB_TB2854");
                String codigoCompania = registro.getCampos()
                                .get(CODIGO) == null ? " "
                                    : registro.getCampos()
                                                    .get(CODIGO).toString();
                nombreIconoCns = prefijo + codigoCompania + subExtImagen;
                directorio = !directorio.endsWith(File.separator)
                    ? directorio + File.separatorChar
                    : directorio;
                String ruta = directorioCns + nombreIconoCns;
                infImagenCns = msg + " " + ruta;
                registro.getCampos().put("RUTA_CNSC", ruta);

                if (ficheroImagen.exists()) {
                    return true;
                }
                else if (ficheroImagen.isDirectory()) {
                    ficheroImagen.mkdir();
                    return true;
                }
                else {
                    JsfUtil.agregarMensajeAlertaVentana(
                                    idioma.getString("TB_TB2855"));
                    return false;
                }
            }
            catch (NullPointerException ex) {
                Logger.getLogger(CompaniasControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2856"));
                return false;
            }
        }

        return true;
    }

    public boolean validarArchivoImagen() {
        if (!SysmanFunciones.validarVariableVacio(iconoCompania)) {
            try {
                File ficheroImagen = new File(directorio);
                String subExtImagen = nombreIconoCompania.substring(
                                nombreIconoCompania.lastIndexOf('.'),
                                nombreIconoCompania.length());

                if (SysmanFunciones.validarVariableVacio(directorio)) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB96"));
                    return false;
                }

                if (!tieneExtensionValida(nombreIconoCompania)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB97"));
                    return false;
                }

                String msg = idioma.getString("TB_TB98");
                String prefijo = idioma.getString("TB_TB99");
                String codigoCompania = registro.getCampos()
                                .get(CODIGO).toString();
                nombreIconoCompania = prefijo + codigoCompania + subExtImagen;
                directorio = !directorio.endsWith(File.separator)
                    ? directorio + File.separatorChar
                    : directorio;
                String ruta = directorio + nombreIconoCompania;
                infSticker = msg + " " + ruta;
                registro.getCampos().put("RUTA_IMAGEN", ruta);

                if (ficheroImagen.exists()) {
                    return true;
                }
                else if (ficheroImagen.isDirectory()) {
                    ficheroImagen.mkdir();
                    return true;
                }
                else {
                    JsfUtil.agregarMensajeAlertaVentana(
                                    idioma.getString("TB_TB103"));
                    return false;
                }
            }
            catch (NullPointerException ex) {
                Logger.getLogger(CompaniasControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB104"));
                return false;
            }
        }

        return true;
    }

    public boolean validarArchivoSticker() {
        if (!SysmanFunciones.validarVariableVacio(stickerCompania)) {
            try {
                File ficheroSticker = new File(directorioSticker);
                String subExtSticker = nombreStickerCompania.substring(
                                nombreStickerCompania.lastIndexOf('.'),
                                nombreStickerCompania.length());

                if (SysmanFunciones.validarVariableVacio(directorioSticker)) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB107"));
                    return false;
                }

                if (!tieneExtensionValida(nombreStickerCompania)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB109"));
                    return false;
                }
                String msg = idioma.getString("TB_TB110");
                String prefijo = idioma.getString("TB_TB111");
                String codigoCompania = registro.getCampos()
                                .get(CODIGO) == null ? " "
                                    : registro.getCampos()
                                                    .get(CODIGO).toString();
                nombreStickerCompania = prefijo + codigoCompania
                    + subExtSticker;
                directorio = !directorio.endsWith(File.separator)
                    ? directorio + File.separatorChar
                    : directorio;

                String ruta = directorioSticker + nombreStickerCompania;
                infSticker = msg + " " + ruta;
                registro.getCampos().put("RUTA_STICKER", ruta);
                if (ficheroSticker.exists()) {
                    return true;
                }
                else if (ficheroSticker.isDirectory()) {
                    ficheroSticker.mkdir();
                    return true;
                }
                else {
                    JsfUtil.agregarMensajeAlertaVentana(
                                    idioma.getString("TB_TB112"));
                    return false;
                }
            }
            catch (NullPointerException ex) {
                Logger.getLogger(CompaniasControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB121"));
                return false;
            }
        }
        return true;
    }

    private boolean tieneExtensionValida(String nombreArchivo) {
        String regex = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nombreArchivo);
        return matcher.matches();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            if (validarArchivoImagen() && validarArchivoSticker()
                && validarArchivoImagenCns()) {
                ciudad = registro.getCampos().get(CODCIUDAD) == null ? " "
                    : registro.getCampos().get(CODCIUDAD).toString();
                registro.getCampos().put("DEPARTAMENTO", departamento);
                registro.getCampos().put("CIUDAD", ciudad);
                registro.getCampos().put("MONEDA",
                                registro.getCampos().get(CODMONEDA));
                registro.getCampos().remove("NOMBRECIUDAD");
                registro.getCampos().remove("NOMBRESCHIP");
                registro.getCampos().remove(CODIGODEPTO);
                registro.getCampos().remove(CODCIUDAD);
                registro.getCampos().remove(CODMONEDA);
                registro.getCampos().remove("NOMBREENTIDAD");
                return true;
            }
            else {
                return false;
            }
        }
        catch (NullPointerException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        if (imagenBytes != null && directorio != null
            && nombreIconoCompania != null) {
            JsfUtil.upload(imagenBytes, directorio, nombreIconoCompania);
        }
        if (stickerBytes != null && directorioSticker != null
            && nombreStickerCompania != null) {
            JsfUtil.upload(stickerBytes, directorioSticker,
                            nombreStickerCompania);
        }
        if (imagenBytesCns != null && directorioCns != null
            && nombreIconoCns != null) {
            JsfUtil.upload(imagenBytesCns, directorioCns, nombreIconoCns);
        }
        JsfUtil.ejecutarJavaScript("cargarImagen('FR338_nuevo:TS23:IM442')");
        JsfUtil.ejecutarJavaScript("cargarImagen('FR338_nuevo:TS23:IM443')");
        JsfUtil.ejecutarJavaScript("cargarImagen('FR338_nuevo:TS23:IM1371')");
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

    public void oprimirDeclaracion() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        String[] campos = { "companiap" };
        String[] valores = { registro.getCampos().get(CODIGO).toString() };
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.DECLARACIONESTRATEGICAS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton DatosNomina
     * en la vista
     *
     *
     */
    public void oprimirDatosNomina(ActionEvent ac) {
    	//<CODIGO_DESARROLLADO>
    	//</CODIGO_DESARROLLADO>
    }

    private Object[] cargar(String ruta) {
        // 0,nombreIconoCns
        // 1,infImagenCns
        // 2,directorioCns
        // 3,iconoCnsc
        // 4,inputStream
        Object[] aux = new Object[5];
        String rutaImagen = SysmanFunciones.nvl(
                        registro.getCampos().get(ruta), "").toString();
        File file = new File(rutaImagen);
        if (!rutaImagen.isEmpty()) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                aux[0] = rutaImagen.substring(
                                rutaImagen.lastIndexOf(File.separator) + 1,
                                rutaImagen.length());

                aux[1] = aux[0];
                aux[2] = rutaImagen.substring(0,
                                rutaImagen.lastIndexOf(File.separator))
                    + File.separator;
                aux[3] = JsfUtil.encodeImage(rutaImagen);
                aux[4] = inputStream;
                return aux;
            }
            catch (StringIndexOutOfBoundsException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB123"));
            }
            catch (FileNotFoundException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB125") + directorioCns);
            }
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeFatal(idioma.getString("TB_TB2840") + " "
                    + rutaImagen + "<br>" + ex.getMessage());
            }
            finally {
                if (inputStreamImagen != null) {
                    try {
                        inputStreamImagen.close();
                    }
                    catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }
            }
        }
        return aux;
    }

    public void cargarIconoCns() {
        iconoCnsc = null;
        Object[] aux = cargar("RUTA_CNSC");
        nombreIconoCns = (String) aux[0];
        infImagenCns = (String) aux[1];
        directorioCns = (String) aux[2];
        iconoCnsc = (String) aux[3];
        inputStreamImagenCns = (InputStream) aux[4];

    }

    public void cargarIconoCompania() {
        iconoCompania = null;
        Object[] aux = cargar("RUTA_IMAGEN");
        nombreIconoCompania = (String) aux[0];
        infImagen = (String) aux[1];
        directorio = (String) aux[2];
        iconoCompania = (String) aux[3];
        inputStreamImagen = (InputStream) aux[4];

    }

    public void cargarStickerCompania() {
        stickerCompania = null;
        Object[] aux = cargar("RUTA_STICKER");
        nombreStickerCompania = (String) aux[0];
        infSticker = (String) aux[1];
        directorioSticker = (String) aux[2];
        stickerCompania = (String) aux[3];
        inputStreamSticker = (InputStream) aux[4];
    }

    public List<Registro> getListaPais() {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaCiudad() {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad) {
        this.listaCiudad = listaCiudad;
    }

    public RegistroDataModelImpl getListaNITCompania() {
        return listaNITCompania;
    }

    public void setListaNITCompania(RegistroDataModelImpl listaNITCompania) {
        this.listaNITCompania = listaNITCompania;
    }

    public RegistroDataModelImpl getListaNITCompaniaE() {
        return listaNITCompaniaE;
    }

    public void setListaNITCompaniaE(RegistroDataModelImpl listaNITCompaniaE) {
        this.listaNITCompaniaE = listaNITCompaniaE;
    }

    public List<Registro> getListaMoneda() {
        return listaMoneda;
    }

    public void setListaMoneda(List<Registro> listaMoneda) {
        this.listaMoneda = listaMoneda;
    }

    public List<Registro> getListaSubconsolidada() {
        return listaSubconsolidada;
    }

    public void setListaSubconsolidada(List<Registro> listaSubconsolidada) {
        this.listaSubconsolidada = listaSubconsolidada;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getNitCompania() {
        return nitCompania;
    }

    public void setNitCompania(String nitCompania) {
        this.nitCompania = nitCompania;
    }

    public String getNombreCompania() {
        return nombreCompania;
    }

    public void setNombreCompania(String nombreCompania) {
        this.nombreCompania = nombreCompania;
    }

    public UploadedFile getArchivoCargaRutaImagen() {
        return archivoCargaRutaImagen;
    }

    public void setArchivoCargaRutaImagen(UploadedFile archivoCargaRutaImagen) {
        this.archivoCargaRutaImagen = archivoCargaRutaImagen;
    }

    public UploadedFile getArchivoCargaRutaSticker() {
        return archivoCargaRutaSticker;
    }

    public void setArchivoCargaRutaSticker(
        UploadedFile archivoCargaRutaSticker) {
        this.archivoCargaRutaSticker = archivoCargaRutaSticker;
    }

    public String getInfImagen() {
        return infImagen;
    }

    public void setInfImagen(String infImagen) {
        this.infImagen = infImagen;
    }

    public String getInfSticker() {
        return infSticker;
    }

    public void setInfSticker(String infSticker) {
        this.infSticker = infSticker;
    }

    public String getDirectorioSticker() {
        return directorioSticker;
    }

    public void setDirectorioSticker(String directorioSticker) {
        this.directorioSticker = directorioSticker;
    }

    public String getDirectorio() {
        return directorio;
    }

    public void setDirectorio(String directorio) {
        this.directorio = directorio;
    }

    public String getIconoCompania() {
        return iconoCompania;
    }

    public void setIconoCompania(String iconoCompania) {
        this.iconoCompania = iconoCompania;
    }

    public String getStickerCompania() {
        return stickerCompania;
    }

    public void setStickerCompania(String stickerCompania) {
        this.stickerCompania = stickerCompania;
    }

    public InputStream getInputStreamImagen() {
        return inputStreamImagen;
    }

    public void setInputStreamImagen(InputStream inputStreamFoto) {
        this.inputStreamImagen = inputStreamFoto;
    }

    public InputStream getInputStreamSticker() {
        return inputStreamSticker;
    }

    public void setInputStreamSticker(InputStream inputStreamSticker) {
        this.inputStreamSticker = inputStreamSticker;
    }

    public String getCompaniaBase() {
        return companiaBase;
    }

    public void setCompaniaBase(String companiaBase) {
        this.companiaBase = companiaBase;
    }

    public int getAnoPlanContable() {
        return anoPlanContable;
    }

    public void setAnoPlanContable(int anoPlanContable) {
        this.anoPlanContable = anoPlanContable;
    }

    public boolean isCrearDatosVisible() {
        return crearDatosVisible;
    }

    public void setCrearDatosVisible(boolean crearDatosVisible) {
        this.crearDatosVisible = crearDatosVisible;
    }

    public String getIconoCnsc() {
        return iconoCnsc;
    }

    public void setIconoCnsc(String iconoCnsc) {
        this.iconoCnsc = iconoCnsc;
    }

    public String getInfImagenCns() {
        return infImagenCns;
    }

    public void setInfImagenCns(String infImagenCns) {
        this.infImagenCns = infImagenCns;
    }

    public String getDirectorioCns() {
        return directorioCns;
    }

    public void setDirectorioCns(String directorioCns) {
        this.directorioCns = directorioCns;
    }

    public String getNombreIconoCns() {
        return nombreIconoCns;
    }

    public void setNombreIconoCns(String nombreIconoCns) {
        this.nombreIconoCns = nombreIconoCns;
    }

    public InputStream getInputStreamImagenCns() {
        return inputStreamImagenCns;
    }

    public void setInputStreamImagenCns(InputStream inputStreamImagenCns) {
        this.inputStreamImagenCns = inputStreamImagenCns;
    }

    public boolean isCreaPlan() {
        return creaPlan;
    }

    public void setCreaPlan(boolean creaPlan) {
        this.creaPlan = creaPlan;
    }

    public RegistroDataModelImpl getListaCodigoSchip() {
        return listaCodigoSchip;
    }

    public void setListaCodigoSchip(RegistroDataModelImpl listaCodigoSchip) {
        this.listaCodigoSchip = listaCodigoSchip;
    }

    public String getNombreEntidadReciproca() {
        return nombreEntidadReciproca;
    }

    public void setNombreEntidadReciproca(String nombreEntidadReciproca) {
        this.nombreEntidadReciproca = nombreEntidadReciproca;
    }

	public List<Registro> getlistaCodigoTpEntidad() {
		return listaCodigoTpEntidad;
	}

	public void setlistaCodigoTpEntidad(List<Registro> listaCodigoTpEntidad) {
		this.listaCodigoTpEntidad = listaCodigoTpEntidad;
	}

	/**
	 * @return the companiaNomina
	 */
	public String getCompaniaNomina() {
		return companiaNomina;
	}

	/**
	 * @param companiaNomina the companiaNomina to set
	 */
	public void setCompaniaNomina(String companiaNomina) {
		this.companiaNomina = companiaNomina;
	}

	/**
	 * @return the anioNomina
	 */
	public int getAnioNomina() {
		return anioNomina;
	}

	/**
	 * @param anioNomina the anioNomina to set
	 */
	public void setAnioNomina(int anioNomina) {
		this.anioNomina = anioNomina;
	}
	
    public List<Registro> getListaRegimen() {
        return listaRegimen;
    }

    public void setListaRegimen(List<Registro> listaRegimen) {
        this.listaRegimen = listaRegimen;
    }
    
    public boolean isAplicaAlmacen() {
		return aplicaAlmacen;
	}

	public void setAplicaAlmacen(boolean aplicaAlmacen) {
		this.aplicaAlmacen = aplicaAlmacen;
	}
}
