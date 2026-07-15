/*-
 * PlantillaswordsControlador.java
 *
 * 3.0
 * 
 * 01/03/2018
 * 
 * Copyright (c) 2018 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.PlantillaswordsControladorEnum;
import com.sysman.general.enums.PlantillaswordsControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
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

import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * La forma plantillaswords no debe ser generada a partir del
 * generadorJSFST, cualquier cambio debe ser realizado sobre la misma.
 *
 * @author jacelas
 * @version 1, 14/10/2015
 *
 * @author lcortes 07/03/2017.
 * @version 1.5 Se modifica el metodo insertarVariables; no se tiene
 * en cuenta validacion de css para el registro de las variables.
 * 
 * @author jreina
 * @version 2, 19/04/2017 se realizaron los cambios de refactoring en
 * cada uno de los combos, en el origen de grilla, de datos y en los
 * subformulario.
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio código formulario y actualización
 * de ConnectorPool
 * 
 * @author asana
 * @version 4.0 02/08/2017 se modifica metodo
 * oprimirDESCARGAR_PLANTILLA(), para descargar correctamente
 * plantilla.
 * 
 * @author ybecerra
 * @version 4.1 03/01/2018 se agrega validacion de menu de hojas de
 * vida
 * 
 * @author jrodrigueza
 * @version 5.0, 01/03/2018 soporte para documentos de MS Excel y
 * modos de creaci&oacute;n para variables de tabla.
 */
@ManagedBean
@ViewScoped
public class PlantillaswordsControlador extends BeanBaseDatosAcmeImpl {

    private final String modulo;
    private final String consPlantilla;
    private final String consNombreTipo;
    private final String consNombre;
    private final String consTbTb562;
    private final String consKeyFecha;
    private final String consFecha;
    private final String consEtiqueta;
    private final String consConsulta;
    private final String consCodigo;
    /**
     * Atributo de referencia para el subformulario VariablesUsuario
     */
    private Registro registroSubVariablesUsuario;
    /**
     * Atributo de referencia para el subformulario VariablesTabla
     */
    private Registro registroSubVariablesTabla;
    private Registro registroSubTipos;
    private String nombreTipo;
    private RegistroDataModelImpl listaVariablesusuario;
    private List<Registro> listaVariablestabla;
    private List<Registro> listaComboTipo;
    private RegistroDataModelImpl listaPlantillaacopiar;
    /**
     * Carga los modos de creaci&oacute;n disponibles para la creación
     * de tablas a partir de una variable.
     */
    private List<Registro> listaModoCreacion;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    private String rutaDocumentos;
    private boolean bloqueadoCargar;
    private Map<String, Object> parametrosEntrada;
    private String formOrigen;
    private String aplicacion;

    @EJB
    private EjbSysmanUtilRemote ejSysmanUtil;
    /**
     * extension de la plantilla
     */
    private String extension;
    
    private String plantillaacopiar;
    private String nuevoNombrePlantilla;
	private String compania;
	private String tipoPlantilla;
	private String nombrePlantilla;
	private Long consecutivo;
	private Registro reg = new Registro();
	private boolean copiando = false;
	private Date fechaActual;
	private String codigoPlantillaActual;

    /**
     * Crea una nueva instancia de PlantillaswordsControlador
     */
    public PlantillaswordsControlador() {
        super();
		compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consPlantilla = "PLANTILLA";
        consNombreTipo = "NOMBRETIPO";
        consNombre = "NOMBRE";
        consTbTb562 = "TB_TB562";
        consKeyFecha = "KEY_FECHA";
        consFecha = "FECHA";
        consEtiqueta = "ETIQUETA";
        consConsulta = "CONSULTA";
        consCodigo = "CODIGO";
        try {
            if (SessionUtil.getFlash() != null) {
                parametrosEntrada = SessionUtil.getFlash();
                formOrigen = (String) parametrosEntrada.get("formOrigen");
                aplicacion = SysmanFunciones.nvlStr(SysmanFunciones.toString(parametrosEntrada.get("aplicacion")),"1");
                
            }
            numFormulario = GeneralCodigoFormaEnum.PLANTILLASWORDS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            registroSubVariablesUsuario = new Registro(
                            new HashMap<String, Object>());
            registroSubVariablesTabla = new Registro(
                            new HashMap<String, Object>());
            registroSubTipos = new Registro(new HashMap<String, Object>());
        }
        catch (Exception ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        if (this.registro == null) {
            this.registro = new Registro(new HashMap<String,Object>());
        }
        if (this.registro.getCampos() == null) {
            this.registro.setCampos(new HashMap<String,Object>());
        }

        if (this.registro.getCampos().get("CONSULTA") == null) {
            this.registro.getCampos().put("CONSULTA", "");
        }
        enumBase = GenericUrlEnum.MODELO_PLANTILLA;
        urlConexionCache = UrlServiceCache.SYSMANIRISST;
        buscarLlave();
        asignarOrigenDatos();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        if ("21".equals(SessionUtil.getModulo())) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PlantillaswordsControladorUrlEnum.URL165
                                                            .getValue());
            if ("21080109".equals(SessionUtil.getMenuActual())) {
                parametrosListado.put(
                                PlantillaswordsControladorEnum.TIPO.getValue(),
                                "53,54");
            }
            else if ("21090105".equals(SessionUtil.getMenuActual())) {
                parametrosListado.put(
                                PlantillaswordsControladorEnum.TIPO.getValue(),
                                "41");
            }
            else if ("21100113".equals(SessionUtil.getMenuActual())) {
                parametrosListado.put(
                                PlantillaswordsControladorEnum.TIPO.getValue(),
                                "51");
            }
            else if ("21070110".equals(SessionUtil.getMenuActual())) {
                parametrosListado.put(
                                PlantillaswordsControladorEnum.TIPO.getValue(),
                                "52");
            }
        } // Modulo de Viaticos
        else if ("6".equals(SessionUtil.getModulo())
            && "70127".equals(SessionUtil.getMenuActual())) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PlantillaswordsControladorUrlEnum.URL165
                                                            .getValue());
            parametrosListado.put(
                            PlantillaswordsControladorEnum.TIPO.getValue(),
                            "40");
        } // Modulo Contabilidad
        else if ("10126".equals(SessionUtil.getMenuActual())) {
            parametrosListado.put(
                            PlantillaswordsControladorEnum.TIPO.getValue(),
                            "59");
        }
		if (parametrosEntrada != null) {
			if (parametrosEntrada.containsKey("menu")) {
				parametrosListado.put("MODULO",
						parametrosEntrada.get("menu").equals("99931") ? aplicacion : SessionUtil.getModulo());
			} else {
				parametrosListado.put("MODULO", SessionUtil.getModulo());
			}
		} else {
			parametrosListado.put("MODULO", SessionUtil.getModulo());
		}
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        cargarListaComboTipo();
        cargarListaModoCreacion();
        cargarListaPlantillaacopiar();
    }

    public void cargarListaPlantillaacopiar() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(PlantillaswordsControladorUrlEnum.URL104084.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);

		listaPlantillaacopiar = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        cargarListaVariablesusuario();
        cargarListaVariablestabla();
        rutaDocumentos = SessionUtil.getRuta(-1) + idioma.getString(consTbTb562)
            + (String) registro.getCampos().get(consNombreTipo)
            + "/" + registro.getCampos().get(consPlantilla);
        File verificar = new File(rutaDocumentos);
        if (!verificar.isFile()) {
            registro.getCampos().put(consPlantilla, "");
        }
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        listaVariablesusuario = null;
        listaVariablestabla = null;
    }

    public void cargarListaComboTipo() {
        String url;
        Map<String, Object> param = new TreeMap<>();
        param.put(PlantillaswordsControladorEnum.MODULO.getValue(),
                        SessionUtil.getModulo());

        if ("21".equals(SessionUtil.getModulo())) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PlantillaswordsControladorUrlEnum.URL165
                                                            .getValue());
            if ("21080109".equals(SessionUtil.getMenuActual())) {
                param.put(PlantillaswordsControladorEnum.TIPO.getValue(),
                                "53,54");
            }
            else if ("21090105".equals(SessionUtil.getMenuActual())) {
                param.put(PlantillaswordsControladorEnum.TIPO.getValue(),
                                "41");
            }
            else if ("21100113".equals(SessionUtil.getMenuActual())) {
                param.put(PlantillaswordsControladorEnum.TIPO.getValue(),
                                "51");
            }
            else if ("21070110".equals(SessionUtil.getMenuActual())) {
                param.put(PlantillaswordsControladorEnum.TIPO.getValue(),
                                "52");
            }
            url = PlantillaswordsControladorUrlEnum.URL232.getValue();
        }
        else {
        	if(parametrosEntrada != null) {
        		if(parametrosEntrada.containsKey("menu") && parametrosEntrada.get("menu").equals("99931")) {
        			param.replace(PlantillaswordsControladorEnum.MODULO.getValue(),
        					aplicacion);
        		}
        	}
        		url = PlantillaswordsControladorUrlEnum.URL4537.getValue();
        }

        try {
            listaComboTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            url)
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaModoCreacion
     */
    public void cargarListaModoCreacion() {
        Map<String, Object> param = new HashMap<>();
        param.put("EXTENSION", extension);
        String urlEnumId = PlantillaswordsControladorUrlEnum.URL32269
                        .getValue();
        try {
            listaModoCreacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            urlEnumId)
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarComboTipo() {
        nombreTipo = service.buscarEnLista(
                        (String) registro.getCampos().get("TIPO"), consCodigo,
                        consNombre,
                        listaComboTipo);

    }

    /**
     * verifica si el documento es de tipo Word o Excel.
     * 
     * @return verdadero si la extension es valida
     */
    public boolean validarExtension() {
        return Arrays.asList("doc", "docx", "xls", "xlsx", "xlsm")
                        .contains(extension);
    }

    public void cargarArchivolector(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        InputStream is;
        extension = FilenameUtils
                        .getExtension(event.getFile().getFileName());
        // segun la extension carga los modos de creacion
        cargarListaModoCreacion();
        generarNombrePlantilla();
        try {
            is = event.getFile().getInputstream();
            if ((is != null)
                && !"".equals(event.getFile().getFileName())
                && validarExtension()) {

                rutaDocumentos = SessionUtil.getRuta(-1)
                    + idioma.getString(consTbTb562) + nombreTipo + "/";

                File verificar2 = new File(SessionUtil.getRuta(-1)
                    + idioma.getString(consTbTb562));
                if (!verificar2.isDirectory()) {
                    verificar2.mkdir();
                }
                File verificar = new File(rutaDocumentos);
                if (!verificar.isDirectory()) {
                    verificar.mkdir();
                }
                JsfUtil.upload(is,
                                (String) registro.getCampos()
                                                .get(consPlantilla),
                                rutaDocumentos);
                agregarRegistroNuevo(false);
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3557"));
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // Redirecciona a la plantilla que tiene el boton que abrio el
    // formulario
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        if (("mresolucion").equals(formOrigen)) {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.MRESOLUCIONS_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        else {
            SessionUtil.redireccionarMenu();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void archivoExiste() {
        rutaDocumentos = SessionUtil.getRuta(-1) + idioma.getString(consTbTb562)
            + nombreTipo + "/";

    }

    public void cargarListaVariablesusuario() {

        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.MODELO_VARIABLES
                                                            .getGridKey());
            Map<String, Object> param = new TreeMap<>();
            param.put(PlantillaswordsControladorEnum.PLANTILLA.getValue(),
                            registro.getCampos().get(consCodigo));


            	param.put(PlantillaswordsControladorEnum.FECHA.getValue(), 
            			SysmanFunciones.convertirAFechaCadena( 
            					(Date) registro.getCampos() 
            					.get(consFecha)));


            listaVariablesusuario = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANIRISST,
                                            GenericUrlEnum.MODELO_VARIABLES
                                                            .getTable()));

        }
        catch (ParseException | SysmanException e) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    public void cargarListaVariablestabla() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(PlantillaswordsControladorEnum.PLANTILLA.getValue(),
                            registro.getCampos().get(consCodigo));

            	param.put(PlantillaswordsControladorEnum.FECHA.getValue(), 
            			SysmanFunciones.convertirAFechaCadena( 
            					(Date) registro.getCampos() 
            					.get(consFecha)));


            listaVariablestabla = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.MODELO_TABLA
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANIRISST,
                                            "MODELO_TABLA"));
        }
        catch (ParseException
                        | SystemException | SysmanException e) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    public boolean verificarEtiqueta() {

        if (registroSubVariablesUsuario.getCampos().get(consEtiqueta).toString()
                        .contains("<#")
            && registroSubVariablesUsuario.getCampos().get(consEtiqueta)
                            .toString().contains("#>")) {
            return true;
        }
        else {
            JsfUtil.agregarMensajeAlertaDialogo(
                            idioma.getString("TB_TB16"));
            return false;

        }
    }

    public boolean verificarEtiquetaActualiza(Registro reg) {

        if (reg.getCampos().get(consEtiqueta).toString().contains("<#")
            && reg.getCampos().get(consEtiqueta).toString().contains("#>")) {
            return true;
        }
        else {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB16"));
            return false;

        }
    }

    public void agregarRegistroSubVariablesusuario() {

        if (verificarEtiqueta()) {

            try {
                registroSubVariablesUsuario.getCampos().put(consPlantilla,
                                registro.getCampos().get(consCodigo));
                registroSubVariablesUsuario.getCampos().put(consFecha,
                                registro.getCampos().get(consFecha));
                registroSubVariablesUsuario.getCampos().put("TIPO", "U");
                registroSubVariablesUsuario.getCampos().put(
                                GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                registroSubVariablesUsuario.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.MODELO_VARIABLES
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSubVariablesUsuario.getCampos());
                listaVariablesusuario.load();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
            catch (SystemException ex) {
                Logger.getLogger(PlantillaswordsControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
            finally {
                registroSubVariablesUsuario = new Registro(
                                new HashMap<String, Object>());
            }

        }

    }

    public void editarRegSubVariablesusuario(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        if (verificarEtiquetaActualiza(reg)) {

            try {

                reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                reg.getCampos().put(
                                GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                Map<String, Object> camposEditar = reg.getCampos();
                camposEditar.remove("TIPO");
                camposEditar.remove("FORMATOLB");
                camposEditar.remove("RNUM");

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.MODELO_VARIABLES
                                                                .getUpdateKey());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                camposEditar,
                                reg.getLlave());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));

            }
            catch (SystemException ex) {
                Logger.getLogger(PlantillaswordsControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    public void eliminarRegSubVariablesusuario(Registro reg) {

        if (consConsulta.equals(reg.getCampos().get("TIPO"))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB17"));
            return;
        }

        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.MODELO_VARIABLES
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaVariablesusuario();
        }
        catch (SystemException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionVariablesusuario() {
        cargarListaVariablesusuario();
        cargarListaVariablestabla();
    }

    public boolean insertaVarTablas(String sql, String tabla) {

        try {

            String sql1 = reemplazarVariablesSql(sql);

            insertarVariablesColumnasTablas(sql1, tabla);
            cargarListaVariablesusuario();
            return true;

        }
        catch (Exception e) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + e.getMessage());
        }

        return false;
    }

    public void agregarRegistroSubVariablestabla() {


            try {

                registroSubVariablesTabla.getCampos().put(consPlantilla,
                                registro.getCampos().get(consCodigo));
                registroSubVariablesTabla.getCampos().put(consFecha,
                                registro.getCampos().get(consFecha));
                registroSubVariablesTabla.getCampos().put(
                                GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                registroSubVariablesTabla.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.MODELO_TABLA
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSubVariablesTabla.getCampos());
                
                
                insertaVarTablas(registroSubVariablesTabla.getCampos()
                        .get(consConsulta).toString(),
                        registroSubVariablesTabla.getCampos().get(consNombre)
                                        .toString());
                
                cargarListaVariablestabla();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));

            }
            catch (SystemException ex) {
                Logger.getLogger(PlantillaswordsControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
            finally {
                registroSubVariablesTabla = new Registro(
                                new HashMap<String, Object>());
            }

        }
    

    public void editarRegSubVariablestabla(RowEditEvent event) {

        Registro reg = (Registro) event.getObject();


            try {
                reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                reg.getCampos().put(
                                GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                reg.getCampos().remove("NOMBRE_MODO_CREACION");
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.MODELO_TABLA
                                                                .getUpdateKey());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                reg.getCampos(),
                                reg.getLlave());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
            catch (SystemException ex) {
                Logger.getLogger(PlantillaswordsControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(
                                                Constantes.MSM_TRANS_INTERRUMPIDA)
                                    + ex.getMessage());
            }
            finally {
                cargarListaVariablestabla();
            }
    }

    public void eliminarRegSubVariablestabla(Registro reg) {

        try {

            reg.getLlave().put(consKeyFecha,
                            SysmanFunciones.convertirAFechaCadena((Date) reg
                                            .getLlave().get(consKeyFecha), "dd/MM/yyyy HH:mm:ss")); 
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.MODELO_TABLA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaVariablesusuario();
            cargarListaVariablestabla();
        }
        catch (SystemException | ParseException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionVariablestabla() {
        cargarListaVariablestabla();

    }

    public void onCancelTipos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // jreina
    public String sqlReporte() {
        return " SELECT " + " MODELO_VARIABLES.ETIQUETA, "
            + " CASE WHEN  MODELO_VARIABLES.TIPO = 'C' THEN 'CONSULTA' WHEN  MODELO_VARIABLES.TIPO = 'U' THEN 'USUARIO' WHEN  MODELO_VARIABLES.TIPO = 'T' THEN 'TABLA' END AS TIPO  "
            + " FROM MODELO_VARIABLES " + " WHERE   PLANTILLA='"
            + registro.getCampos().get(consCodigo) + "'"
            + "   AND FECHA =  " + SysmanFunciones.formatearFecha(
                            (Date) registro.getCampos().get(consFecha))
            + " "
            + "   UNION \n" + "SELECT MODELO_TABLA.NOMBRE,'TABLA' TIPO\n"
            + "FROM MODELO_TABLA\n"
            + "WHERE PLANTILLA='" + registro.getCampos().get(consCodigo) + "'"
            + "AND FECHA =  "
            + SysmanFunciones.formatearFecha(
                            (Date) registro.getCampos().get(consFecha))
            + " " + "ORDER BY TIPO";
    }

    public void oprimirDescargarPlantilla() {
        // <CODIGO_DESARROLLADO>
        File plantilla = new File(rutaDocumentos
            + registro.getCampos().get(consPlantilla));
        try (InputStream fis = new FileInputStream(plantilla)) {

            byte[] vec = new byte[(int) plantilla.length()];
            fis.read(vec, 0, vec.length);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec), plantilla.getName());
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB570"));
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando345() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (actualizarAntes()) {
            genInforme(ReportesBean.FORMATOS.PDF,
                            "000288ListadoEtiquetasConsultaPlantillaWord",
                            sqlReporte());

        }

        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private void genInforme(ReportesBean.FORMATOS formato, String nombreReporte,
        String psql) {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_EQUIPO", "#EQUIPO#");
            parametros.put("PR_STRSQL", psql);
            parametros.put("PR_NOMBREPLANTILLA",
                            registro.getCampos().get(consNombre));

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMANK, formato);
        }
        catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
        }
        catch (SysmanException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioeditarVariables(SelectEvent event) {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        plantillaacopiar = "";
        tipoPlantilla = "";
        nombrePlantilla = "";
        nombreTipo = (String) registro.getCampos().get(consNombreTipo);

        rutaDocumentos = SessionUtil.getRuta(-1) + idioma.getString(consTbTb562)
            + nombreTipo + "/";
        bloqueadoCargar = true;
        if (accion.equals(ACCION_INSERTAR) && copiando == false) {
        	generarConsecutivo();
        	registro.getCampos().put(consCodigo, consecutivo);
            bloqueadoCargar = false;
        }
        // carga los modos de creacion segun la extension
        String filename = SysmanFunciones
                        .toString(registro.getCampos().get(consPlantilla));
        extension = FilenameUtils
                        .getExtension(filename);
        cargarListaModoCreacion();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Busca una variable dentro de la consulta y la reemplaza por un
     * comod&iacute;n
     * 
     * @param sql
     * consulta SQL con variables de reemplazo
     * @return consulta SQL v&aacute;lida
     */
    private String reemplazarVariablesSql(String sql) {
        String consulta = Reporteador.reemplazaSql(sql);
        String regex = "s\\$([a-z]|[A-Z])+\\$s";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(consulta);
        StringBuffer sb = new StringBuffer(consulta.length());
        while (matcher.find()) {
            String cadena = matcher.group();
            String reemplazo = null;
            if ("S$CONDICIONENLACE$S".equalsIgnoreCase(cadena)) {
                reemplazo = " 1 = 2 ";
            }
            else if ("S$CONDICIONUSUARIO$S".equalsIgnoreCase(cadena)) {
                reemplazo = " AND 1=1 ";
            }
            else if (cadena.toUpperCase().contains("FECHA")) {
                reemplazo = "TO_DATE('01/01/2000','DD/MM/YYYY')";
            }
            else if (!"S$CONDICIONENLACE$S".equalsIgnoreCase(cadena)
                && !"S$CONDICIONUSUARIO$S".equalsIgnoreCase(cadena)) {
                reemplazo = "0";
            }
            if (reemplazo != null)
                matcher.appendReplacement(sb, reemplazo);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public boolean sqlCorrecto(String sql) {
        String sqlAux = sql;
        if (SysmanFunciones.validarVariableVacio(sqlAux)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2836"));
            return false;
        }
        sqlAux = reemplazarVariablesSql(sqlAux);
        sqlAux = sqlAux.toUpperCase();
        sqlAux = sqlAux.replace("'", "''");

        try {
            String res = ejSysmanUtil.verificarConsultaPlantilla(sqlAux);

            if (!("OK").equals(res) && res != null) {
                return false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    /**
     * private String obtenerCodPlantillas() { StringBuilder
     * parametros = new StringBuilder(); try { List<Registro> reg =
     * RegistroConverter.toListRegistro(
     * requestManager.getList(UrlServiceUtil.getInstance()
     * .getUrlServiceByUrlByEnumID(
     * PlantillaswordsControladorUrlEnum.URL233 .getValue())
     * .getUrl(), null));
     * 
     * if (reg != null) { for (Registro lista : reg) {
     * parametros.append(lista.getCampos().get("CODIGO_PLANTILLA"))
     * .append(","); } }
     * 
     * } catch (SystemException e) { logger.error(e.getMessage(), e);
     * JsfUtil.agregarMensajeError(e.getMessage()); }
     * 
     * String rta = parametros.toString(); rta = rta.substring(0,
     * rta.length() - 1); return rta;
     * 
     * }
     */

    public boolean insertarVariables(String sql) {
        // vacia las variables si existen
        String sql1 = reemplazarVariablesSql(sql);
        String plantillacodigo = registro.getCampos().get(consCodigo).toString();

        try {
            // -------------------------------------------------------------------------------------------------------
            // Traer columnas de la consulta
            // -------------------------------------------------------------------------------------------------------
            List<String> columnas;

            columnas = service.getCamposListado(ConectorPool.ESQUEMA_SYSMAN,
                            sql1);

            Map<String, Object> campos = new HashMap<>();
            campos.put(PlantillaswordsControladorEnum.PLANTILLA.getValue(),
            		plantillacodigo);
            if (copiando) {
            	campos.put(PlantillaswordsControladorEnum.FECHA.getValue(),fechaActual);
            } else {
            	campos.put(PlantillaswordsControladorEnum.FECHA.getValue(),
            			SysmanFunciones.convertirAFechaCadena(
            					(Date) registro.getCampos()
            					.get(consFecha),
            					"dd/MM/yyyy HH:mm:ss"));
            }
            Parameter parameter = new Parameter();
            parameter.setFields(campos);

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PlantillaswordsControladorUrlEnum.URL12778
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), campos);

            for (int i = 0; i < columnas.size(); i++) {
                Map<String, Object> campos2 = new HashMap<>();
                campos2.put(PlantillaswordsControladorEnum.PLANTILLA.getValue(),
                		plantillacodigo);
                campos2.put(PlantillaswordsControladorEnum.FECHA.getValue(),
                                SysmanFunciones.convertirAFechaCadena(
                                                (Date) registro.getCampos().get(
                                                                consFecha)));
                campos2.put(GeneralParameterEnum.NOMBRE.getName(),
                                columnas.get(i));
                campos2.put(PlantillaswordsControladorEnum.TIPO.getValue(),
                                "C");
                campos2.put(GeneralParameterEnum.FORMATO.getName(), "T");
                campos2.put(PlantillaswordsControladorEnum.ETIQUETA.getValue(),
                                "<#" + columnas.get(i) + "#>");
                campos2.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                campos2.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());
                Parameter parameterIns = new Parameter();
                parameterIns.setFields(campos2);

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                PlantillaswordsControladorUrlEnum.URL9766
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                parameterIns);
            }
            return true;
        }
        catch (SystemException | ParseException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + ""
                                + idioma.getString("TB_TB572"));
            return false;

        }

    }

    public void eliminarCampos(List<String> columnas) {
        StringBuilder sqlEliminar = new StringBuilder();
        if (!columnas.isEmpty()) {
            for (int i = 0; i < columnas.size(); i++) {
                sqlEliminar.append("'<#" + columnas.get(i)
                    + "#>'"
                    + ((i + 1) < columnas.size() ? "," : " "));
            }
        }

        Map<String, Object> campos = new HashMap<>();
        campos.put(PlantillaswordsControladorEnum.PLANTILLA.getValue(),
                        registro.getCampos().get(consCodigo).toString());
        campos.put(GeneralParameterEnum.DESCRIPCION.getName(), tabla);
        try {
            campos.put(PlantillaswordsControladorEnum.FECHA.getValue(),
                            SysmanFunciones.convertirAFechaCadena(
                                            (Date) registro.getCampos()
                                                            .get(consFecha)));
            campos.put(PlantillaswordsControladorEnum.ETIQUETAS.getValue(),
                            sqlEliminar.toString());
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PlantillaswordsControladorUrlEnum.URL17170
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), campos);
        }
        catch (ParseException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean insertarVariablesColumnasTablas(String sql, String tabla) {
        // vacia las variables si existen
        if (css != null) {
            try {
                // -------------------------------------------------------------------------------------------------------
                // Traer columnas de la consulta
                // -------------------------------------------------------------------------------------------------------

                List<String> columnas;

                columnas = service.getCamposListado(ConectorPool.ESQUEMA_SYSMAN,
                                sql);

                eliminarCampos(columnas);

                Map<String, Object> campos = new HashMap<>();
                campos.put(PlantillaswordsControladorEnum.PLANTILLA.getValue(),
                                registro.getCampos().get(consCodigo));
                campos.put(GeneralParameterEnum.DESCRIPCION.getName(), tabla);
                campos.put(PlantillaswordsControladorEnum.FECHA.getValue(),
                                SysmanFunciones.convertirAFechaCadena(
                                                (Date) registro.getCampos().get(
                                                                consFecha)));

                List<Registro> existentes = RegistroConverter
                                .toListRegistro(requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                PlantillaswordsControladorUrlEnum.URL7379
                                                                                                .getValue())
                                                                .getUrl(),
                                                campos));

                for (int k = 0; k < existentes.size(); k++) {
                    for (int i = 0; i < columnas.size(); i++) {

                        if (existentes.get(k).getCampos().get(consEtiqueta)
                                        .toString()
                                        .equals("<#" + columnas.get(i)
                                            + "#>")) {
                            columnas.remove(i);
                        }
                    }
                }

                for (int i = 0; i < columnas.size(); i++) {
                    Map<String, Object> campos2 = new HashMap<>();
                    campos2.put(PlantillaswordsControladorEnum.PLANTILLA
                                    .getValue(),
                                    registro.getCampos().get(consCodigo)
                                                    .toString());
                    campos2.put(PlantillaswordsControladorEnum.FECHA
                                    .getValue(),
                                    SysmanFunciones.convertirAFechaCadena(
                                                    (Date) registro.getCampos()
                                                                    .get(consFecha),"dd/MM/yyyy HH:mm:ss"));
                    campos2.put(GeneralParameterEnum.NOMBRE.getName(),
                                    columnas.get(i));
                    campos2.put(PlantillaswordsControladorEnum.TIPO
                                    .getValue(), "T");
                    campos2.put(GeneralParameterEnum.FORMATO.getName(), "T");
                    campos2.put(PlantillaswordsControladorEnum.ETIQUETA
                                    .getValue(), "<#" + columnas.get(i) + "#>");
                    campos2.put(GeneralParameterEnum.DESCRIPCION.getName(),
                                    tabla);
                    campos2.put(GeneralParameterEnum.CREATED_BY.getName(),
                                    SessionUtil.getUser().getCodigo());
                    campos2.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                    SysmanFunciones.convertirAFechaCadena(
                                                    new Date()));
                    Parameter parameterIns = new Parameter();
                    parameterIns.setFields(campos2);

                    UrlBean urlCreate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    PlantillaswordsControladorUrlEnum.URL9766
                                                                    .getValue());
                    requestManager.save(urlCreate.getUrl(),
                                    urlCreate.getMetodo(), parameterIns);

                }
                return true;
            }
            catch (SystemException | ParseException ex) {
                Logger.getLogger(PlantillaswordsControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(
                                idioma.getString(
                                                Constantes.MSM_TRANS_INTERRUMPIDA)
                                    + ""
                                    + idioma.getString("TB_TB572"));
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
    	String consulta = SysmanFunciones.nvl(SysmanFunciones.toString(registro.getCampos().get("CONSULTA")),"");
    	if(consulta.equals("")) {
    		registro.getCampos().put("CONSULTA","SELECT * FROM DUAL");
    	}
        	return true;
        // </CODIGO_DESARROLLADO>
    }
    
    
    public void oprimirCopiarde() {
    	if (plantillaacopiar != null) {
    		try {
    			copiando = true;
    			
    			
    			codigoPlantillaActual = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

    			Map<String, Object> parametro = new TreeMap<>();
    			parametro.put(GeneralParameterEnum.PLANTILLA.getName(), plantillaacopiar);
    			
    			reg = RegistroConverter
    					.toRegistro(requestManager.get(
    							UrlServiceUtil.getInstance()
    							.getUrlServiceByUrlByEnumID(
    									PlantillaswordsControladorUrlEnum.URL104086.getValue())
    							.getUrl(),
    							parametro));

    			Object fechaPlantilla = reg.getCampos().get("FECHA");
				
						
    			if(reg!=null) {
    				registro.getCampos().put("VERSION", reg.getCampos().get("VERSION"));
    				registro.getCampos().put("TIPO_VARIABLES_CONSULTA", reg.getCampos().get("TIPO_VARIABLES_CONSULTA"));

    				registro.getCampos().put("CONSULTA", reg.getCampos().get("CONSULTA"));
    				String rutaPlantilla = "/opt/sysman/data/reportes/General/plantillasword/" + tipoPlantilla + "/"	+ nombrePlantilla;
    				String rutaDestino = SessionUtil.getRuta(-1) + idioma.getString(consTbTb562) + nombreTipo + "/";
    				copiarPlantilla(rutaPlantilla, rutaDestino);
    				String nombre = SysmanFunciones.toString(registro.getCampos().get("NOMBRE"));
    				int tipo = Integer.parseInt(SysmanFunciones.toString(registro.getCampos().get("TIPO")));
    				ejSysmanUtil.copiarPlantilla(plantillaacopiar, codigoPlantillaActual,  (Date) fechaPlantilla, nombre, tipo, nuevoNombrePlantilla);
    				
    				
    				asignarOrigenDatos();
    				cargarListaVariablesusuario();
    				cargarListaVariablestabla();
    			}
    			registro.getCampos().put("NOMBRETIPO",nombreTipo);
   				cargarRegistro();

    			JsfUtil.agregarMensajeInformativo("Proceso Ejecutado correctamente");
    			accion = "m";

    		} catch (SystemException  e) {
    			logger.error(e.getMessage(), e);
    			JsfUtil.agregarMensajeError(e.getMessage());
    		}finally {
    			copiando = false;
    		}
    		plantillaacopiar = "";
    		tipoPlantilla = "";
    		nombrePlantilla = "";
    	}else {
    		JsfUtil.agregarMensajeInformativo("Debe seleccionar una plantilla a copiar");
    	}
    }
	/**
	 * metodo que llama al seleccionar un registro de un combo grande
	 */
	public void seleccionarFilaPlantillaacopiar(SelectEvent event) {
		
		Registro registroAux = (Registro) event.getObject();
		plantillaacopiar = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
		tipoPlantilla = SysmanFunciones
				.toString(registroAux.getCampos().get(GeneralParameterEnum.TIPO.getName()));
		nombrePlantilla = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.PLANTILLA.getName()));

		JsfUtil.agregarMensajeInformativo("Para finalizar el proceso, de click en el boton Copiar de");

	}
	
	 private void generarConsecutivo() {
	        try {
	        	Date fechaActual = new Date();
	        	int anioActual = SysmanFunciones.ano(fechaActual);

	        	Map<String, Object> param = new TreeMap<>();
	        	
	    		param.put(GeneralParameterEnum.APLICACION.getName(), 
	    			    aplicacion != null && !aplicacion.isEmpty() 
	    			        ? Integer.parseInt(aplicacion)  
	    			        : 0);   

	    			Registro rsConsecutivo = RegistroConverter.toRegistro(
	    					requestManager.get(UrlServiceUtil.getInstance()
	    							.getUrlServiceByUrlByEnumID(
	    									PlantillaswordsControladorUrlEnum.URL104087
	    									.getValue())
	    							.getUrl(), param));

					if (!rsConsecutivo.equals(null)) {
						Object valor = rsConsecutivo.getCampos().get(GeneralParameterEnum.CODIGO.getName());

						if (valor != null) {
							consecutivo = ((Number) valor).longValue();
						} else {
							consecutivo = 1L;
						}
					} else {
						consecutivo = 1L;
					}
	        }
	        catch (SystemException e) {

	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
	    }
	
	 public void copiarPlantilla(String rutaOrigen, String rutaDestino){
	        nuevoNombrePlantilla = "";
	       
	        try {
	        	generarNombrePlantilla();

		        File archivoOrigen = new File(rutaOrigen);

		        if (!archivoOrigen.exists()) {
		            throw new IOException("No se encontró la plantilla en: " + rutaOrigen);
		        }

		        // Crear carpeta destino si no existe
		        File carpetaDestino = new File(rutaDestino);
		        if (!carpetaDestino.isDirectory()) {
		            carpetaDestino.mkdirs();
		        }

		        // InputStream de la plantilla original
		        try (InputStream is = new FileInputStream(archivoOrigen)) {
		            
		            // Ruta completa de la copia con nuevo nombre
		            File archivoDestino = new File(carpetaDestino, nuevoNombrePlantilla);

		            JsfUtil.upload(is, nuevoNombrePlantilla, rutaDestino);
    				registro.getCampos().put("PLANTILLA", nuevoNombrePlantilla);

		            System.out.println("Plantilla duplicada en: " + archivoDestino.getAbsolutePath());
		        }

		    } catch (IOException  e) {
		        e.printStackTrace();
		        JsfUtil.agregarMensajeError("Error al copiar la plantilla: " + e.getMessage());
				registro.getCampos().put("PLANTILLA", "");
				nuevoNombrePlantilla = "";

		    }
	    }

    public void cambiarVersion() {
        generarNombrePlantilla();
    }

    public void cambiarnombre() {
        generarNombrePlantilla();
    }

    public void cambiarcodigo() {
        generarNombrePlantilla();
    }

    public void cambiarfecha() {
        generarNombrePlantilla();
    }

    public void generarNombrePlantilla() {
        String texto = "";
        nuevoNombrePlantilla = "";
        try {
            texto = SysmanFunciones
                            .nvl(registro.getCampos().get(consCodigo), " ")
                            .toString()
                + (registro.getCampos().get(consFecha) == null ? "" : "-")
                + SysmanFunciones.nvlStr(SysmanFunciones.convertirAFechaCadena(
                                (Date) registro.getCampos().get(consFecha),
                                "dd-MM-YYYY"), "");
            nuevoNombrePlantilla = texto.replace(" ", "-") + ".docx";
            
        }
        catch (ParseException ex) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        registro.getCampos().put(consPlantilla,
                        texto.replace(" ", "-") + "." + extension);
    }

    @Override
    public boolean insertarDespues() {
        cargarListaVariablesusuario();
        cargarListaVariablestabla();
        return true;

    }

    @Override
    public boolean actualizarAntes() {
        // CODIGO AUTOCOMPLETADO
        registro.getCampos().remove(consNombreTipo);
        boolean validado;
        validado = sqlCorrecto((String) registro.getCampos().get(consConsulta));
        return validado;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        insertarVariables((String) registro.getCampos().get(consConsulta));
        if (accion.equals(ACCION_MODIFICAR)) {
            cargarRegistro(css, ACCION_MODIFICAR);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            registro.getLlave().put(consKeyFecha, SysmanFunciones
                            .convertirAFechaCadena((Date) registro.getLlave()
                                            .get(consKeyFecha),"dd/MM/yyyy HH:mm:ss"));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public List<Registro> getListaComboTipo() {
        return listaComboTipo;
    }

    public void setListaComboTipo(List<Registro> listaComboTipo) {
        this.listaComboTipo = listaComboTipo;
    }

    public RegistroDataModelImpl getListaVariablesusuario() {
        return listaVariablesusuario;
    }

    public void setListaVariablesusuario(
        RegistroDataModelImpl listaVariablesusuario) {
        this.listaVariablesusuario = listaVariablesusuario;
    }

    public List<Registro> getListaVariablestabla() {
        return listaVariablestabla;
    }

    public void setListaVariablestabla(List<Registro> listaVariablestabla) {
        this.listaVariablestabla = listaVariablestabla;
    }

    /**
     * Retorna la lista listaModoCreacion
     * 
     * @return listaModoCreacion
     */
    public List<Registro> getListaModoCreacion() {
        return listaModoCreacion;
    }

    /**
     * Asigna la lista listaModoCreacion
     * 
     * @param listaModoCreacion
     * Variable a asignar en listaModoCreacion
     */
    public void setListaModoCreacion(List<Registro> listaModoCreacion) {
        this.listaModoCreacion = listaModoCreacion;
    }

    public Registro getRegistroSubVariablesUsuario() {
        return registroSubVariablesUsuario;
    }

    public void setRegistroSubVariablesUsuario(
        Registro registroSubVariablesUsuario) {
        this.registroSubVariablesUsuario = registroSubVariablesUsuario;
    }

    public Registro getRegistroSubVariablesTabla() {
        return registroSubVariablesTabla;
    }

    public void setRegistroSubVariablesTabla(
        Registro registroSubVariablesTabla) {
        this.registroSubVariablesTabla = registroSubVariablesTabla;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public Registro getRegistroSubTipos() {
        return registroSubTipos;
    }

    public void setRegistroSubTipos(Registro registroSubTipos) {
        this.registroSubTipos = registroSubTipos;
    }

    public String getRutaDocumentos() {
        return rutaDocumentos;
    }

    public void setRutaDocumentos(String rutaDocumentos) {
        this.rutaDocumentos = rutaDocumentos;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public boolean isBloqueadoCargar() {
        return bloqueadoCargar;
    }

    public void setBloqueadoCargar(boolean bloqueadoCargar) {
        this.bloqueadoCargar = bloqueadoCargar;
    }

	/**
	 * @return the plantillaacopiar
	 */
	public String getPlantillaacopiar() {
		return plantillaacopiar;
	}

	/**
	 * @param plantillaacopiar the plantillaacopiar to set
	 */
	public void setPlantillaacopiar(String plantillaacopiar) {
		this.plantillaacopiar = plantillaacopiar;
	}

	/**
	 * @return the listaPlantillaacopiar
	 */
	public RegistroDataModelImpl getListaPlantillaacopiar() {
		return listaPlantillaacopiar;
	}

	/**
	 * @param listaPlantillaacopiar the listaPlantillaacopiar to set
	 */
	public void setListaPlantillaacopiar(RegistroDataModelImpl listaPlantillaacopiar) {
		this.listaPlantillaacopiar = listaPlantillaacopiar;
	}
    
	
    

}