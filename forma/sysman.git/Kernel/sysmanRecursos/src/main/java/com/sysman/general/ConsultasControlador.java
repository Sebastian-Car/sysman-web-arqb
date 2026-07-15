/*-
 * ConsultasControlador.java
 *
 * 1.0
 * 
 * 15/05/2018
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
import com.sysman.general.enums.ConsultasControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que administra las consultas de los reportes del
 * reporteador
 *
 * @version 1.0, 15/05/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class ConsultasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String consCodigoCon;

    private final String consCodigoFiltro;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que inicio sesion, el valor de esta constante es asignado en el
     * constructor a la variable de sesion correspondiente
     */
    private final String usuario;

    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que carga los modulos de la aplicacion
     */
    private List<Registro> listaModulo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que carga los filtros
     */
    private RegistroDataModelImpl listaFiltro;
    /**
     * Lista que carga los filtros en la grilla
     */
    private RegistroDataModelImpl listaFiltroE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista que carga los detallas de las consultas
     */
    private RegistroDataModelImpl listaDetalleconsultas;
    /**
     * Lista que carga los detalles de los parametros
     */
    private RegistroDataModelImpl listaDetalleparametros;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario DetalleConsultas
     */
    private Registro registroSubDetalleConsultas;
    /**
     * Atributo de referencia para el subformulario DetalleParametros
     */
    private Registro registroSubDetalleParametros;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de ConsultasControlador
     */
    public ConsultasControlador() {
        super();
        compania = SessionUtil.getCompania();
        consCodigoCon = "CODIGO_CONSULTA";
        consCodigoFiltro = "CODIGO_FILTRO";
        usuario = SessionUtil.getUser().getCodigo();
        urlConexionCache = UrlServiceCache.SYSMANIRISST;
        modulo = SessionUtil.getSessionVar("aplicacion").toString();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONSULTASRP_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubDetalleConsultas = new Registro(
                            new HashMap<String, Object>());
            registroSubDetalleParametros = new Registro(
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
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaFiltro();
        cargarListaFiltroE();
        cargarListaModulo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaDetalleconsultas();
        cargarListaDetalleparametros();
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
        listaDetalleconsultas = null;
        listaDetalleparametros = null;
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
        enumBase = GenericUrlEnum.CONSULTAS_RP;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put("APLICAUSUARIO", 0);

        parametrosListado.put("MODULO", modulo);

    }

    /**
     * 
     * Carga la lista listaDetalleconsultas
     *
     */
    public void cargarListaDetalleconsultas() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.D_CONSULTAS
                                                        .getGridKey());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(consCodigoCon,
                        registro.getCampos().get(consCodigoCon));
        param.put("CODIGO_REPORTE", "0");

        try {
            listaDetalleconsultas = new RegistroDataModelImpl(urlBean.getUrl(),
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
     * Carga la lista listaDetalleparametros
     *
     */
    public void cargarListaDetalleparametros() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.D_PARAMETROSCONSULTAS
                                                        .getGridKey());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(consCodigoCon,
                        registro.getCampos().get(consCodigoCon));

        try {
            listaDetalleparametros = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANIRISST,
                                            GenericUrlEnum.D_PARAMETROSCONSULTAS
                                                            .getTable()));
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaModulo
     *
     */
    public void cargarListaModulo() {
        try {
            listaModulo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConsultasControladorUrlEnum.URL7600
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
     * Carga la lista listaFiltro
     *
     */
    public void cargarListaFiltro() {

        Map<String, Object> param = new TreeMap<>();

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConsultasControladorUrlEnum.URL7334
                                                        .getValue());

        listaFiltro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigoFiltro);
    }

    /**
     * 
     * Carga la lista listaFiltro
     *
     */
    public void cargarListaFiltroE() {
        listaFiltroE = listaFiltro;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFiltro
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFiltro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubDetalleParametros.getCampos().put(consCodigoFiltro,
                        registroAux.getCampos().get(consCodigoFiltro));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFiltro
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFiltroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(consCodigoFiltro);
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Detalleconsultas
     * 
     */
    public void agregarRegistroSubDetalleconsultas() {
        try {

            registroSubDetalleConsultas.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);

            registroSubDetalleConsultas.getCampos()
                            .put(GeneralParameterEnum.CODIGO
                                            .getName(),
                                            registro.getCampos().get(
                                                            consCodigoCon));

            registroSubDetalleConsultas.getCampos()
                            .put("CODIGO_DETALLE",
                                            consecutivoDetalleConsulta());

            registroSubDetalleConsultas.getCampos()
                            .put(GeneralParameterEnum.CREATED_BY
                                            .getName(), usuario);

            registroSubDetalleConsultas.getCampos()
                            .put(GeneralParameterEnum.DATE_CREATED
                                            .getName(), new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_CONSULTAS
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubDetalleConsultas.getCampos());
            cargarListaDetalleconsultas();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubDetalleConsultas = new Registro(
                            new HashMap<String, Object>());
        }
    }

    private Object consecutivoDetalleConsulta() {
        long consecutivoGenerado = 0;
        String idConsulta = SysmanFunciones
                        .toString(registro.getCampos().get(consCodigoCon));
        String condicion = SysmanFunciones.concatenar("COMPANIA = ''",
                        compania, "'' AND CODIGO = ''", idConsulta,
                        "'' AND CODIGO_REPORTE= ''0''");
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

    /**
     * Metodo de edicion del formulario Detalleconsultas
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubDetalleconsultas(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());

        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        usuario);

        reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

        reg.getCampos().remove("NOMBRE_TIPO");

        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_CONSULTAS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaDetalleconsultas();
        }
    }

    /**
     * Metodo de eliminacion del formulario Detalleconsultas
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubDetalleconsultas(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_CONSULTAS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarListaDetalleconsultas();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Detalleconsultas
     *
     */
    public void cancelarEdicionDetalleconsultas() {
        listaDetalleconsultas.load();

    }

    /**
     * Metodo de insercion del formulario Detalleparametros
     * 
     */
    public void agregarRegistroSubDetalleparametros() {
        try {

            registroSubDetalleParametros.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);

            registroSubDetalleParametros.getCampos()
                            .put(GeneralParameterEnum.CODIGO
                                            .getName(),
                                            registro.getCampos().get(
                                                            consCodigoCon));

            registroSubDetalleParametros.getCampos()
                            .put("CODIGO_PARAMETRO",
                                            consecutivoDetalleParametro());

            registroSubDetalleParametros.getCampos()
                            .put(GeneralParameterEnum.CREATED_BY
                                            .getName(), usuario);

            registroSubDetalleParametros.getCampos()
                            .put(GeneralParameterEnum.DATE_CREATED
                                            .getName(), new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_PARAMETROSCONSULTAS
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubDetalleParametros.getCampos());
            cargarListaDetalleparametros();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubDetalleConsultas = new Registro(
                            new HashMap<String, Object>());
        }

    }

    private Object consecutivoDetalleParametro() {
        long consecutivoGenerado = 0;
        String idConsulta = SysmanFunciones
                        .toString(registro.getCampos().get(consCodigoCon));
        String condicion = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "'' AND CODIGO_CONSULTA = ''", idConsulta + "''");
        try {
            consecutivoGenerado = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "D_PARAMETROSCONSULTAS", condicion,
                            "CODIGO_PARAMETRO", ConectorPool.ESQUEMA_SYSMANK);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivoGenerado;
    }

    /**
     * Metodo de edicion del formulario Detalleparametros
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubDetalleparametros(RowEditEvent event) {

        Registro reg = (Registro) event.getObject();

        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());

        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        usuario);

        reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

        reg.getCampos().remove("NOMBRE_TIPO");

        reg.getCampos().remove("NOMBRE_FILTRO");

        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_PARAMETROSCONSULTAS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaDetalleparametros();
        }

    }

    /**
     * Metodo de eliminacion del formulario Detalleparametros
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubDetalleparametros(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_PARAMETROSCONSULTAS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarListaDetalleparametros();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Detalleparametros
     *
     */
    public void cancelarEdicionDetalleparametros() {
        listaDetalleparametros.load();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

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
            String res = ejbSysmanUtil.verificarConsultaPlantilla(sqlAux);

            if (res.isEmpty() && !("OK").equals(res)) {
                return false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(PlantillaswordsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean insertarVariables(String sql) {

        insertarFiltros(sql);

        // vacia las variables si existen

        String sql1 = reemplazarVariablesSql(sql);

        try {
            // -------------
            // Traer nombre de los campos o alias de las columnas de
            // la consulta
            // -------------

            sql1 = sql1.replace("'", "''");
            String datos = ejbSysmanUtil.detectarCampos(sql1);

            String[] registros = datos.split(SysmanConstantes.SEPARADOR_REG);

            //
            // Eliminar campos de la consulta en la tabla de detalle
            //

            Map<String, Object> campos = new HashMap<>();
            campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            campos.put(consCodigoCon,
                            registro.getCampos().get(consCodigoCon));
            campos.put("CODIGO_REPORTE", "0");

            Parameter parameter = new Parameter();
            parameter.setFields(campos);

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConsultasControladorUrlEnum.URL11400
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(), campos);

            for (int i = 0; i < registros.length; i++) {
                String[] columnas = registros[i]
                                .split(SysmanConstantes.SEPARADOR_COL);
                Map<String, Object> campos2 = new HashMap<>();
                campos2.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                campos2.put("CODIGO_DETALLE", consecutivoDetalleConsulta());
                campos2.put("CODIGO_REPORTE", "0");
                campos2.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get(consCodigoCon));
                campos2.put("CAMPO", columnas[0]);
                campos2.put("TIPO", columnas[1]);
                campos2.put("ETIQUETA", columnas[0]);
                campos2.put("VISIBLE", -1);
                campos2.put("ORDEN", i + 1);
                campos2.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                campos2.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                SysmanFunciones.convertirAFechaCadena(
                                                new Date()));
                Parameter parameterIns = new Parameter();
                parameterIns.setFields(campos2);

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ConsultasControladorUrlEnum.URL14935
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

    private void insertarFiltros(String sql) {
        try {

            String regex = ":([a-z]|[A-Z])+";
            Pattern pattern = Pattern.compile(regex);

            if (sql.toUpperCase().contains("WHERE")) {

                String[] consulta = sql.toUpperCase().split("WHERE");
                Matcher matcher = pattern.matcher(consulta[1]);

                Map<String, Object> campos = new HashMap<>();
                campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                campos.put(consCodigoCon,
                                registro.getCampos().get(consCodigoCon));

                Parameter parameter = new Parameter();
                parameter.setFields(campos);

                UrlBean urlDelete = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ConsultasControladorUrlEnum.URL6861
                                                                .getValue());
                requestManager.delete(urlDelete.getUrl(), campos);

                while (matcher.find()) {

                    String cadena = matcher.group();

                    Map<String, Object> campos2 = new HashMap<>();

                    campos2.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);

                    campos2.put(consCodigoCon,
                                    registro.getCampos().get(
                                                    consCodigoCon));

                    campos2.put("CODIGO_PARAMETRO",
                                    consecutivoDetalleParametro());

                    campos2.put("NOMBRE_PARAMETRO", cadena.toLowerCase());

                    campos2.put("ETIQUETA_PARAMETRO",
                                    cadena.substring(1, cadena.length()));

                    campos2.put("TIPO_PARAMETRO", "S");

                    if ("COMPANIA".equalsIgnoreCase(
                                    cadena.substring(1, cadena.length()))) {
                        campos2.put("VALOR_FILTRO", compania);
                    }

                    campos2.put(GeneralParameterEnum.CREATED_BY
                                    .getName(), usuario);

                    campos2.put(GeneralParameterEnum.DATE_CREATED
                                    .getName(), new Date());

                    Parameter parameterIns = new Parameter();
                    parameterIns.setFields(campos2);

                    UrlBean urlCreate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    ConsultasControladorUrlEnum.URL8351
                                                                    .getValue());
                    requestManager.save(urlCreate.getUrl(),
                                    urlCreate.getMetodo(),
                                    parameterIns);

                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Busca una variable dentro de la consulta y la reemplaza por un
     * valor
     * 
     * @param sql
     * consulta SQL con variables de reemplazo
     * @return consulta SQL con valores
     */
    private String reemplazarVariablesSql(String sql) {
        String consulta = Reporteador.reemplazaSql(sql);
        // String regex = "s\\$([a-z]|[A-Z])+\\$s";
        String regex = ":([a-z]|[A-Z])+";
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

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

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
        if (accion.equals(ACCION_INSERTAR)) {
            registro.getCampos().put("MODULO", modulo);
        }
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
     */
    @Override
    public boolean actualizarAntes() {
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }

        registro.getCampos()
                        .remove("NOMBRE_ESTADO");

        boolean validado;
        validado = sqlCorrecto((String) registro.getCampos().get("SQL"));
        return validado;

    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        insertarVariables(registro.getCampos().get("SQL").toString());
        return true;
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

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaModulo
     * 
     * @return listaModulo
     */
    public List<Registro> getListaModulo() {
        return listaModulo;
    }

    /**
     * Asigna la lista listaModulo
     * 
     * @param listaModulo
     * Variable a asignar en listaModulo
     */
    public void setListaModulo(List<Registro> listaModulo) {
        this.listaModulo = listaModulo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFiltro
     * 
     * @return listaFiltro
     */
    public RegistroDataModelImpl getListaFiltro() {
        return listaFiltro;
    }

    /**
     * Asigna la lista listaFiltro
     * 
     * @param listaFiltro
     * Variable a asignar en listaFiltro
     */
    public void setListaFiltro(RegistroDataModelImpl listaFiltro) {
        this.listaFiltro = listaFiltro;
    }

    /**
     * Retorna la lista listaFiltro
     * 
     * @return listaFiltro
     */
    public RegistroDataModelImpl getListaFiltroE() {
        return listaFiltroE;
    }

    /**
     * Asigna la lista listaFiltro
     * 
     * @param listaFiltro
     * Variable a asignar en listaFiltro
     */
    public void setListaFiltroE(RegistroDataModelImpl listaFiltroE) {
        this.listaFiltroE = listaFiltroE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaDetalleconsultas
     * 
     * @return listaDetalleconsultas
     */
    public RegistroDataModelImpl getListaDetalleconsultas() {
        return listaDetalleconsultas;
    }

    /**
     * Asigna la lista listaDetalleconsultas
     * 
     * @param listaDetalleconsultas
     * Variable a asignar en listaDetalleconsultas
     */
    public void setListaDetalleconsultas(
        RegistroDataModelImpl listaDetalleconsultas) {
        this.listaDetalleconsultas = listaDetalleconsultas;
    }

    /**
     * Retorna la lista listaDetalleparametros
     * 
     * @return listaDetalleparametros
     */
    public RegistroDataModelImpl getListaDetalleparametros() {
        return listaDetalleparametros;
    }

    /**
     * Asigna la lista listaDetalleparametros
     * 
     * @param listaDetalleparametros
     * Variable a asignar en listaDetalleparametros
     */
    public void setListaDetalleparametros(
        RegistroDataModelImpl listaDetalleparametros) {
        this.listaDetalleparametros = listaDetalleparametros;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubDetalleConsultas
     * 
     * @return registroSubDetalleConsultas
     */
    public Registro getRegistroSubDetalleConsultas() {
        return registroSubDetalleConsultas;
    }

    /**
     * Asigna el objeto registroSubDetalleConsultas
     * 
     * @param registroSubDetalleConsultas
     * Variable a asignar en registroSubDetalleConsultas
     */
    public void setRegistroSubDetalleConsultas(
        Registro registroSubDetalleConsultas) {
        this.registroSubDetalleConsultas = registroSubDetalleConsultas;
    }

    /**
     * Retorna el objeto registroSubDetalleParametros
     * 
     * @return registroSubDetalleParametros
     */
    public Registro getRegistroSubDetalleParametros() {
        return registroSubDetalleParametros;
    }

    /**
     * Asigna el objeto registroSubDetalleParametros
     * 
     * @param registroSubDetalleParametros
     * Variable a asignar en registroSubDetalleParametros
     */
    public void setRegistroSubDetalleParametros(
        Registro registroSubDetalleParametros) {
        this.registroSubDetalleParametros = registroSubDetalleParametros;
    }
    // </SET_GET_ADICIONALES>
}
