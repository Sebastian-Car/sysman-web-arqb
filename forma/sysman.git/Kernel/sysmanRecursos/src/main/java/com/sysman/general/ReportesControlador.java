/*-
 * ReportesControlador.java
 *
 * 1.0
 * 
 * 16/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ReportesControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
 * Formulario que administra los reportes del generador..
 *
 * @version 1.0, 16/05/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class ReportesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String modulo;

    private final String consCodigoCon;
    private final String consCodigoFil;
    private final String consCodigoRep;
    private final String consAplicacion;
    private final String consRuta;
    private final String consTb562;
    private final String consCampo;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos Plantilla y funciona como contenedor del archivo que
     * se debe guardar
     */
    private ContenedorArchivo contArchivoPlantilla;
    private int indiceParametros;
    private String reg;
    private String rutaDocumentos;
    private String extension;
    private String nombreModulo;
    private int visible;

    private boolean bloqueado;

    /**
     * Atributo que cambia el nombre del boton
     */
    private String tituloBoton;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaValorFiltro;
    private RegistroDataModelImpl listaValorFiltroE;

    private RegistroDataModelImpl listaFiltro;
    private RegistroDataModelImpl listaFiltroE;

    private RegistroDataModelImpl listaConsultasRp;
    private RegistroDataModelImpl listaConsultaDetalle;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private RegistroDataModelImpl listaConsultas;
    private RegistroDataModelImpl listaParametros;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario Parametros
     */
    private Registro registroSubParametros;
    /**
     * Atributo de referencia para el subformulario Consultas
     */
    private Registro registroSubConsultas;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ReportesControlador
     */
    public ReportesControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCodigoCon = "CODIGO_CONSULTA";
        consCodigoFil = "CODIGO_FILTRO";
        consCodigoRep = "CODIGO_REPORTE";
        consAplicacion = "aplicacion";
        consRuta = "RUTA_REPORTE";
        consTb562 = "TB_TB562";
        consCampo = "CAMPO";
        SessionUtil.getSessionVar(consAplicacion);
        modulo = SessionUtil.getModulo();
        tituloBoton = idioma.getString("TB_TB4102");
        try {
            numFormulario = GeneralCodigoFormaEnum.REPORTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubParametros = new Registro(
                            new HashMap<String, Object>());
            registroSubConsultas = new Registro(
                            new HashMap<String, Object>());
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
        cargarListaConsultasRp();
        cargarListaFiltro();
        cargarListaFiltroE();
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
        cargarListaConsultas();
        cargarListaParametros();

        cargarListaConsultaDetalle();
        // rutaDocumentos = SessionUtil.getRuta(-1)
        // + idioma.getString("TB_TB562") + "/"+ nombreModulo + "/" +
        // registro.getCampos().get("RUTA_REPORTE").toString()
        // File verificar = new File(rutaDocumentos)
        // if verificar
        // isFile())
        // registro.getCampos().put("RUTA_REPORTE", "")
        //
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
        listaConsultas = null;
        listaParametros = null;
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
        enumBase = GenericUrlEnum.REPORTES;
        urlConexionCache = UrlServiceCache.SYSMANIRISST;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("APLICAUSUARIO", -1);

        parametrosListado.put("MODULO",
                        SessionUtil.getSessionVar(consAplicacion));
    }

    /**
     * 
     * Carga la lista listaD_consultas
     *
     */
    public void cargarListaConsultas() {

        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ReportesControladorUrlEnum.URL13756
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(consCodigoCon,
                            registro.getCampos().get(consCodigoCon));
            param.put(consCodigoRep,
                            registro.getCampos().get(consCodigoRep));

            listaConsultas = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANIRISST,
                                            GenericUrlEnum.D_CONSULTAS
                                                            .getTable()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaD_q_parametros
     *
     */
    public void cargarListaParametros() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportesControladorUrlEnum.URL15144
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(consCodigoRep,
                        registro.getCampos().get(consCodigoRep));
        String[] rowKey;
        try {
            rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
                            GenericUrlEnum.D_PARAMETROS.getTable());
            listaParametros = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            rowKey);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaFiltro
     */
    public void cargarListaValorFiltro() {
        String aux;

        switch (reg) {
        case "001":
            aux = ReportesControladorUrlEnum.URL10305
                            .getValue();
            break;
        case "002":
            aux = ReportesControladorUrlEnum.URL11554
                            .getValue();
            break;
        default:
            aux = "";
            break;
        }
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(aux);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaValorFiltro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaFiltro
     */
    public void cargarListaValorFiltroE() {
        String aux;

        switch (reg) {
        case "001":
            aux = ReportesControladorUrlEnum.URL10305
                            .getValue();
            break;
        case "002":
            aux = ReportesControladorUrlEnum.URL11554
                            .getValue();
            break;
        default:
            aux = "";
            break;
        }
        listaValorFiltroE = null;
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(aux);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaValorFiltroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Carga la lista listaConsultaDetalle
     *
     */
    public void cargarListaConsultaDetalle() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportesControladorUrlEnum.URL8547
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(consCodigoCon,
                        registro.getCampos().get(consCodigoCon));

        listaConsultaDetalle = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, false,
                        consCampo, true);

    }

    /**
     * 
     * Carga la lista listaConsultasRp
     *
     */
    public void cargarListaConsultasRp() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put("MODULO",
                        SessionUtil.getSessionVar(consAplicacion));

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportesControladorUrlEnum.URL8177
                                                        .getValue());

        listaConsultasRp = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarArchivoPlantilla(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        InputStream is;
        extension = FilenameUtils
                        .getExtension(event.getFile().getFileName());
        registro.getCampos().put(consRuta, event.getFile().getFileName());

        // segun la extension carga los modos de creacion
        try {
            is = event.getFile().getInputstream();
            if ((is != null)
                && !"".equals(event.getFile().getFileName())
                && validarExtension()) {

                rutaDocumentos = SessionUtil.getRuta(-1)
                    + idioma.getString(consTb562) + "/" + nombreModulo + "/";

                File verificar2 = new File(SessionUtil.getRuta(-1)
                    + idioma.getString(consTb562));
                if (!verificar2.isDirectory()) {
                    verificar2.mkdir();
                }
                File verificar = new File(rutaDocumentos);
                if (!verificar.isDirectory()) {
                    verificar.mkdir();
                }
                JsfUtil.upload(is,
                                (String) registro.getCampos()
                                                .get(consRuta),
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

    public boolean validarExtension() {
        return Arrays.asList("doc", "docx")
                        .contains(extension);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Filtro en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFiltroC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        reg = listaParametros.getDatasource().get(rowNum % 10).getCampos()
                        .get(consCodigoFil).toString();
        cargarListaValorFiltro();
        cargarListaValorFiltroE();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPorDefecto() {
        // <CODIGO_DESARROLLADO>

        if ((boolean) registroSubParametros
                        .getCampos().get("PORDEFECTO")) {
            bloqueado = true;
        }
        else {
            bloqueado = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PorDefecto en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPorDefectoC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        if ((boolean) listaParametros.getDatasource().get(rowNum % 10)
                        .getCampos().get("PORDEFECTO")) {
            bloqueado = true;
            listaParametros.getDatasource().get(rowNum % 10)
                            .getCampos().put("CODIGO_FILTRO", null);
        }
        else {
            bloqueado = false;
            listaParametros.getDatasource().get(rowNum % 10)
                            .getCampos().put("VALOR_DEFECTO", null);
        }
        // </CODIGO_DESARROLLADO>
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFiltro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaValorFiltro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubParametros.getCampos().put("VALOR_FILTRO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFiltro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaValorFiltroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConsultaDetalle
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConsultaDetalle(SelectEvent event) {
        // METODO NO IMPLEMENTADO
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFiltro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFiltro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubParametros.getCampos().put(consCodigoFil,
                        registroAux.getCampos().get(consCodigoFil));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFiltro
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFiltroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(consCodigoFil).toString();
    }

    /**
     * Carga la lista listaFiltro
     *
     */
    public void cargarListaFiltro() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportesControladorUrlEnum.URL13311
                                                        .getValue());

        listaFiltro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigoFil);
    }

    /**
     * Carga la lista listaFiltro
     *
     */
    public void cargarListaFiltroE() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReportesControladorUrlEnum.URL13311
                                                        .getValue());

        listaFiltroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigoFil);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConsultasRp
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConsultasRp(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(consCodigoCon,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put("NOMBRE_CONSULTA", registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
    }

    private void insertarCondicion(String sql) {
        try {

            String regex = ":([a-z]|[A-Z])+";
            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(sql.toUpperCase());

            Map<String, Object> campos = new HashMap<>();
            campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            campos.put(consCodigoRep,
                            registro.getCampos().get(consCodigoRep));

            Parameter parameter = new Parameter();
            parameter.setFields(campos);

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ReportesControladorUrlEnum.URL6861
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), campos);

            while (matcher.find()) {

                String cadena = matcher.group();

                Map<String, Object> campos2 = new HashMap<>();

                campos2.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                campos2.put(consCodigoRep,
                                registro.getCampos().get(consCodigoRep));

                campos2.put("CODIGO_PARAMETRO",
                                consecutivoDetalleParametro());

                campos2.put("NOMBRE_PARAMETRO", cadena.toLowerCase());

                campos2.put("ETIQUETA_PARAMETRO",
                                cadena.substring(1, cadena.length()));

                campos2.put("TIPO_PARAMETRO", "S");

                campos2.put(GeneralParameterEnum.CREATED_BY
                                .getName(), SessionUtil.getUser().getCodigo());

                campos2.put(GeneralParameterEnum.DATE_CREATED
                                .getName(), new Date());

                Parameter parameterIns = new Parameter();
                parameterIns.setFields(campos2);

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ReportesControladorUrlEnum.URL8351
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(),
                                urlCreate.getMetodo(),
                                parameterIns);

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private Object consecutivoDetalleParametro() {
        long consecutivoGenerado = 0;
        String idReporte = SysmanFunciones
                        .toString(registro.getCampos().get(consCodigoRep));
        String condicion = SysmanFunciones.concatenar(" COMPANIA = ''",
                        compania, "'' AND CODIGO_REPORTE =''", idReporte,
                        "''");
        try {
            consecutivoGenerado = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "D_PARAMETROS", condicion,
                            "CODIGO_PARAMETRO", ConectorPool.ESQUEMA_SYSMANK);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivoGenerado;
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    /**
     * Metodo ejecutado al oprimir el boton EnviarCorreo en la vista
     */
    public void oprimirEnviarCorreo() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo ejecutado al oprimir el boton EnviarCorreo en la vista
     */
    public void oprimirPdf() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo ejecutado al oprimir el boton EnviarCorreo en la vista
     */
    public void oprimirWord() {
        // METODO NO IMPLEMENTADO
    }

    public void oprimirSeleccionarCampos() {

        String[] campos = { "codigo" };
        Object[] valores = { registro.getCampos().get(consCodigoCon) };

        SessionUtil.cargarModalDatosFlashCerrar("1801",
                        modulo, campos,
                        valores);

    }

    /**
     * Metodo ejecutado al oprimir el boton EnviarCorreo en la vista
     */
    public void oprimirSeleccionar() {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put("VISIBLE", visible);
            parametros.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(consCodigoCon));
            parametros.put(consCodigoRep,
                            registro.getCampos().get(consCodigoRep));
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            Parameter parameter = new Parameter();
            parameter.setFields(parametros);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ReportesControladorUrlEnum.URL9045
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
            visible = visible == -1 ? 0 : -1;
            cambiarTextoBoton();
            cargarListaConsultas();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cambiarTextoBoton() {
        if (visible == 0) {

            tituloBoton = idioma.getString("TB_TB4103");
        }
        else {
            tituloBoton = idioma.getString("TB_TB4102");
        }

    }

    public void oprimirDescargarPlantilla() {
        File plantilla = new File(rutaDocumentos
            + registro.getCampos().get(consRuta));
        try (InputStream fis = new FileInputStream(plantilla)) {

            byte[] vec = new byte[(int) plantilla.length()];
            fis.read(vec, 0, vec.length);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec), plantilla.getName());
        }
        catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB570"));
        }
        catch (JRException | IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton EnviarCorreo en la vista
     */
    public void oprimirExcel() {
        // METODO NO IMPLEMENTADO
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario D_consultas
     * 
     */
    public void agregarRegistroSubConsultas() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo de edicion del formulario D_consultas
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubConsultas(RowEditEvent event) {
        // METODO NO IMPLEMENTADO
        Registro regAux = (Registro) event.getObject();

        regAux.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());

        regAux.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());

        regAux.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

        regAux.getCampos().remove("NOMBRE_TIPO");

        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_CONSULTAS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            regAux.getCampos(), regAux.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaConsultas();
        }
    }

    /**
     * Metodo de eliminacion del formulario D_consultas
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubConsultas(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_CONSULTAS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarListaConsultas();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario D_consultas
     *
     */
    public void cancelarEdicionConsultas() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo de insercion del formulario D_q_parametros
     * 
     */
    public void agregarRegistroSubParametros() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo de edicion del formulario D_q_parametros
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubParametros(RowEditEvent event) {
        Registro regPar = (Registro) event.getObject();
        try {
            regPar.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            regPar.getCampos().remove("NOMBRE_TIPO");
            regPar.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            regPar.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            regPar.getCampos().put("APLICA_USUARIO", -1);
            regPar.getCampos().remove("NOMBRE_FILTRO");

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_PARAMETROS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            regPar.getCampos(), regPar.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaParametros();
        }

    }

    /**
     * Metodo de eliminacion del formulario D_q_parametros
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubParametros(Registro reg) {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario D_q_parametros
     *
     */
    public void cancelarEdicionParametros() {
        // METODO NO IMPLEMENTADO
        listaParametros.load();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        try {
            List<Registro> listaApliaciones = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReportesControladorUrlEnum.URL45227
                                                                            .getValue())
                                            .getUrl(), null));
            nombreModulo = service.buscarEnLista(
                            SessionUtil.getSessionVar(consAplicacion)
                                            .toString(),
                            "APLICACION",
                            "NOMBRE",
                            listaApliaciones).toUpperCase();
            rutaDocumentos = SessionUtil.getRuta(-1)
                + idioma.getString(consTb562) + "/" + nombreModulo + "/";
            visible = 0;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicionParametros(Registro r) {
        Registro regis = r;
        if ((boolean) regis.getCampos().get("PORDEFECTO")) {
            bloqueado = true;
        }
        else {
            bloqueado = false;
        }

    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        cargarListaConsultaDetalle();
        rutaDocumentos = SessionUtil.getRuta(-1)
            + idioma.getString(consTb562) + "/" + nombreModulo + "/";
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRE_CONSULTA");

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
        insertarCondicion(registro.getCampos().get("CONDICION").toString());
        insertarDetalles();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private Object consecutivoDetalleConsulta() {
        long consecutivoGenerado = 0;
        String idConsulta = SysmanFunciones
                        .toString(registro.getCampos().get(consCodigoCon));
        String idReporte = SysmanFunciones
                        .toString(registro.getCampos().get(consCodigoRep));
        String condicion = SysmanFunciones.concatenar(" COMPANIA = ''",
                        compania, "'' AND CODIGO =''", idConsulta,
                        "'' AND CODIGO_REPORTE= ''", idReporte, "''");
        try {
            consecutivoGenerado = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "D_CONSULTAS", condicion,
                            "CODIGO_DETALLE", ConectorPool.ESQUEMA_SYSMANK);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivoGenerado;
    }

    public void insertarDetalles() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(consCodigoCon));

            List<Registro> columnas = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReportesControladorUrlEnum.URL12000
                                                                            .getValue())
                                            .getUrl(),
                                            param));

            Map<String, Object> campos = new HashMap<>();
            campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            campos.put(consCodigoCon,
                            registro.getCampos().get(consCodigoCon));
            campos.put(consCodigoRep,
                            registro.getCampos().get(consCodigoRep));

            Parameter parameter = new Parameter();
            parameter.setFields(campos);

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ReportesControladorUrlEnum.URL11400
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), campos);

            for (int i = 0; i < columnas.size(); i++) {
                Map<String, Object> campos2 = new HashMap<>();
                campos2.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                campos2.put("CODIGO_DETALLE", consecutivoDetalleConsulta());
                campos2.put(consCodigoRep,
                                registro.getCampos().get(consCodigoRep));
                campos2.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get(consCodigoCon));
                campos2.put(consCampo,
                                columnas.get(i).getCampos().get(consCampo));
                campos2.put("TIPO", columnas.get(i).getCampos().get("TIPO"));
                campos2.put("ETIQUETA",
                                columnas.get(i).getCampos().get("ETIQUETA"));
                campos2.put("VISIBLE", -1);
                campos2.put("ORDEN", i + 1);
                campos2.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                campos2.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());
                Parameter parameterIns = new Parameter();
                parameterIns.setFields(campos2);

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ReportesControladorUrlEnum.URL14935
                                                                .getValue());

                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                parameterIns);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaConsultas
     * 
     * @return listaConsultas
     */
    public RegistroDataModelImpl getListaConsultas() {
        return listaConsultas;
    }

    /**
     * Asigna la lista listaConsultas
     * 
     * @param listaConsultas
     * Variable a asignar en listaConsultas
     */
    public void setListaConsultas(RegistroDataModelImpl listaConsultas) {
        this.listaConsultas = listaConsultas;
    }

    /**
     * Retorna la lista listaParametros
     * 
     * @return listaParametros
     */
    public RegistroDataModelImpl getListaParametros() {
        return listaParametros;
    }

    /**
     * Asigna la lista listaParametros
     * 
     * @param listaParametros
     * Variable a asignar en listaParametros
     */
    public void setListaParametros(RegistroDataModelImpl listaParametros) {
        this.listaParametros = listaParametros;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubConsultas
     * 
     * @return registroSubConsultas
     */
    public Registro getRegistroSubConsultas() {
        return registroSubConsultas;
    }

    /**
     * Asigna el objeto registroSubConsultas
     * 
     * @param registroSubConsultas
     * Variable a asignar en registroSubConsultas
     */
    public void setRegistroSubConsultas(Registro registroSubConsultas) {
        this.registroSubConsultas = registroSubConsultas;
    }

    /**
     * Retorna el objeto registroSubParametros
     * 
     * @return registroSubParametros
     */
    public Registro getRegistroSubParametros() {
        return registroSubParametros;
    }

    /**
     * Asigna el objeto registroSubParametros
     * 
     * @param registroSubParametros
     * Variable a asignar en registroSubParametros
     */
    public void setRegistroSubParametros(Registro registroSubParametros) {
        this.registroSubParametros = registroSubParametros;
    }

    public ContenedorArchivo getContArchivoPlantilla() {
        return contArchivoPlantilla;
    }

    public void setContArchivoPlantilla(
        ContenedorArchivo contArchivoPlantilla) {
        this.contArchivoPlantilla = contArchivoPlantilla;
    }

    public RegistroDataModelImpl getListaValorFiltro() {
        return listaValorFiltro;
    }

    public void setListaValorFiltro(RegistroDataModelImpl listaValorFiltro) {
        this.listaValorFiltro = listaValorFiltro;
    }

    public RegistroDataModelImpl getListaValorFiltroE() {
        return listaValorFiltroE;
    }

    public void setListaValorFiltroE(RegistroDataModelImpl listaValorFiltroE) {
        this.listaValorFiltroE = listaValorFiltroE;
    }

    public RegistroDataModelImpl getListaFiltro() {
        return listaFiltro;
    }

    public void setListaFiltro(RegistroDataModelImpl listaFiltro) {
        this.listaFiltro = listaFiltro;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * Retorna la lista listaConsultasRp
     * 
     * @return listaConsultasRp
     */
    public RegistroDataModelImpl getListaConsultasRp() {
        return listaConsultasRp;
    }

    /**
     * Asigna la lista listaConsultasRp
     * 
     * @param listaConsultasRp
     * Variable a asignar en listaConsultasRp
     */
    public void setListaConsultasRp(RegistroDataModelImpl listaConsultasRp) {
        this.listaConsultasRp = listaConsultasRp;
    }

    public int getIndiceParametros() {
        return indiceParametros;
    }

    public void setIndiceParametros(int indiceParametros) {
        this.indiceParametros = indiceParametros;
    }

    public RegistroDataModelImpl getListaFiltroE() {
        return listaFiltroE;
    }

    public void setListaFiltroE(RegistroDataModelImpl listaFiltroE) {
        this.listaFiltroE = listaFiltroE;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public RegistroDataModelImpl getListaConsultaDetalle() {
        return listaConsultaDetalle;
    }

    public void setListaConsultaDetalle(
        RegistroDataModelImpl listaConsultaDetalle) {
        this.listaConsultaDetalle = listaConsultaDetalle;
    }

    public String getTituloBoton() {
        return tituloBoton;
    }

    public void setTituloBoton(String tituloBoton) {
        this.tituloBoton = tituloBoton;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    // </SET_GET_ADICIONALES>
}
