/*-
 * FrmObjetivosDesSostControlador.java
 *
 * 1.0
 * 
 * 16/12/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.enums.FrmObjetivosDesSostControladorEnum;
import com.sysman.plandesarrollo.enums.FrmObjetivosDesSostControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador para formulario que permite el ingreso de un
 * c&oacute;digo y una descripci&oacute;n de un Objetivo de Desarrollo
 * Sostenible para una vigencia determinada. Adem&aacute;s, permite
 * registrar las metas respectivas por cada ODS, las cuales pueden ser
 * mas de una y tambien se registrar con un c&oacute;digo y un nombre.
 *
 * @version 1.0, 16/12/2019
 * @author jrodrigueza
 */
@ManagedBean
@ViewScoped
public class FrmObjetivosDesSostControlador extends BeanBaseDatosAcmeImpl {

    public static final String STR_COMPANIA = GeneralParameterEnum.COMPANIA
                    .getName();

    public static final String VIGENCIA_ODS = FrmObjetivosDesSostControladorEnum.VIGENCIA_ODS
                    .getValue();

    public static final String CODIGO_ODS = FrmObjetivosDesSostControladorEnum.CODIGO_ODS
                    .getValue();

    public static final String ID = FrmObjetivosDesSostControladorEnum.ID
                    .getValue();

    public static final String DESCRIPCION_META = FrmObjetivosDesSostControladorEnum.DESCRIPCION_META
                    .getValue();

    public static final String PORCENTAJE_PARTICIPACION = FrmObjetivosDesSostControladorEnum.PORCENTAJE_PARTICIPACION
                    .getValue();

    public static final String STR_VIGENCIA = GeneralParameterEnum.VIGENCIA
                    .getName();

    public static final String STR_CODIGO = GeneralParameterEnum.CODIGO
                    .getName();

    private static final String DESCRIPCION = GeneralParameterEnum.DESCRIPCION
                    .getName();

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /** N&uacute;mero de a&ntilde;o asociado al ODS. */
    private Integer vigencia;

    /** C&oacute;digo que identifica al ODS. */
    private String codigo;
    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_LISTAS>
    /**
     * Lista de a&ntilde;os para el campo vigencia.
     */
    private List<Registro> listavigenciaOds;
    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de metas provenientes del plan indicativo.
     */
    private RegistroDataModelImpl listacodigoMeta;
    /**
     * Lista de metas provenientes del plan indicativo. Externo.
     */
    private RegistroDataModelImpl listacodigoMetaE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Subformulario de metas O.D.S para realizar operaciones CRUD.
     */
    private RegistroDataModelImpl listaSubmetasods;
    // </DECLARAR_LISTAS_SUBFORM>

    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>

    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    // </DECLARAR_ADICIONALES>

    /**
     * Crea una nueva instancia de FrmObjetivosDesSostControlador
     */
    public FrmObjetivosDesSostControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_OBJETIVOS_DES_SOST
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                vigencia = (Integer) parametros.get(STR_VIGENCIA);
                codigo = (String) parametros.get(STR_CODIGO);
            }
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
        if (vigencia != null) {
            cargarListacodigoMeta();
            cargarListacodigoMetaE();
        }
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListavigenciaOds();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubmetasods();
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
        listaSubmetasods = null;
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
        enumBase = GenericUrlEnum.BP_ODS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario.
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(STR_COMPANIA, compania);
        parametrosListado.put(STR_VIGENCIA, vigencia);
        parametrosListado.put(STR_CODIGO, codigo);
    }

    /** Carga la lista para sub-formulario Metas ODS. */
    public void cargarListaSubmetasods() {
        Map<String, Object> campos = registro.getCampos();

        Map<String, Object> params = new HashMap<>();
        params.put(STR_COMPANIA, compania);
        params.put(VIGENCIA_ODS, campos.get(STR_VIGENCIA));
        params.put(CODIGO_ODS, campos.get(STR_CODIGO));

        UrlBean urlSub = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.BP_ODS_META
                                        .getGridKey());

        String[] clave = null;
        try {
            clave = CacheUtil.getLlaveServicio(urlConexionCache,
                            GenericUrlEnum.BP_ODS_META.getTable());
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            listaSubmetasods = new RegistroDataModelImpl(urlSub.getUrl(),
                            urlSub.getUrlConteo().getUrl(), params, clave);
        }

    }

    // <METODOS_CARGAR_LISTA>
    /** Carga la listado de a&ntilde;os en orden descendente. */
    public void cargarListavigenciaOds() {
        Map<String, Object> params = new HashMap<>();
        params.put(STR_COMPANIA, compania);
        String urlEnumId = FrmObjetivosDesSostControladorUrlEnum.URL4002
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        try {
            List<Parameter> parameters = requestManager
                            .getList(urlBean.getUrl(), params);
            listavigenciaOds = RegistroConverter.toListRegistro(parameters);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Carga la listado de metas, provenientes del plan indicativo
     * para el a&ntilde;o especificado.
     */
    public void cargarListacodigoMeta() {
        Map<String, Object> params = new HashMap<>();
        params.put(STR_VIGENCIA, vigencia);

        String urlEnumId = FrmObjetivosDesSostControladorUrlEnum.URL552052
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        listacodigoMeta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), params, true, ID);
    }

    /** Carga la lista listacodigoMeta */
    public void cargarListacodigoMetaE() {
        Map<String, Object> params = new HashMap<>();
        params.put(STR_VIGENCIA, vigencia);

        String urlEnumId = FrmObjetivosDesSostControladorUrlEnum.URL552052
                        .getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);

        listacodigoMetaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), params, true, ID);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoMeta
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoMeta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(STR_CODIGO,
                        registroAux.getCampos().get(ID));
        registroSub.getCampos().put(DESCRIPCION,
                        registroAux.getCampos().get(DESCRIPCION));
        registroSub.getCampos().put(PORCENTAJE_PARTICIPACION,
                        registroAux.getCampos().get(PORCENTAJE_PARTICIPACION));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigoMeta.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoMetaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.toString(registroAux.getCampos().get(ID));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del sub-formulario Metas ODS.
     */
    public void agregarRegistroSubSubmetasods() {
        Map<String, Object> campos = registro.getCampos();
        registroSub.getCampos().put(STR_COMPANIA, compania);
        registroSub.getCampos().put(VIGENCIA_ODS, campos.get(STR_VIGENCIA));
        registroSub.getCampos().put(CODIGO_ODS, campos.get(STR_CODIGO));
        registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());

        UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.BP_ODS_META
                                        .getCreateKey());

        Parameter params = new Parameter();
        params.setFields(registroSub.getCampos());

        try {
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarListaSubmetasods();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
            registroSub = new Registro(new HashMap<String, Object>());
        }

    }

    /**
     * Metodo de edicion del sub-formulario Metas ODS.
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubmetasods(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        // elimina campos que hacen parte de la llave
        reg.getCampos().remove(STR_COMPANIA);
        reg.getCampos().remove(VIGENCIA_ODS);
        reg.getCampos().remove(CODIGO_ODS);

        // datos de auditoria para setencia que modifica
        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());

        // adiciona los filtros de llave primaria
        reg.getCampos().putAll(reg.getLlave());

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.BP_ODS_META
                                        .getUpdateKey());

        Parameter params = new Parameter();
        params.setFields(reg.getCampos());

        try {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            params);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
            cargarListaSubmetasods();
        }
    }

    /**
     * Metodo de eliminacion del sub-formulario Metas ODS.
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubmetasods(Registro reg) {
        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.BP_ODS_META
                                        .getDeleteKey());

        try {
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubmetasods();
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Metas ODS.
     */
    public void cancelarEdicionSubmetasods() {
        cargarListaSubmetasods();
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro.
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        Map<String, Object> campos = registro.getCampos();
        vigencia = (Integer) campos.get(STR_VIGENCIA);
        codigo = (String) campos.get(STR_CODIGO);
        cargarListacodigoMeta();
        cargarListacodigoMetaE();
        iniciarListasSub();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * 
     * @return verdadero si puede proceder con la accion
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(STR_COMPANIA, compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro.
     * 
     * @return verdadero si puede proceder con la accion
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro.
     * 
     * @return verdadero si puede proceder con la accion
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos().remove(STR_COMPANIA);
            registro.getCampos().remove(STR_CODIGO);
            registro.getCampos().remove(STR_VIGENCIA);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro.
     * 
     * @return verdadero si puede proceder con la accion
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro.
     * 
     * @return verdadero si puede proceder con la accion
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro.
     * 
     * @return verdadero si puede proceder con la accion
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
     * Retorna la lista listavigenciaODS
     * 
     * @return listavigenciaODS
     */
    public List<Registro> getListavigenciaOds() {
        return listavigenciaOds;
    }

    /**
     * Asigna la lista listavigenciaODS
     * 
     * @param listavigenciaODS
     * Variable a asignar en listavigenciaODS
     */
    public void setListavigenciaOds(List<Registro> listavigenciaOds) {
        this.listavigenciaOds = listavigenciaOds;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacodigoMeta
     * 
     * @return listacodigoMeta
     */
    public RegistroDataModelImpl getListacodigoMeta() {
        return listacodigoMeta;
    }

    /**
     * Asigna la lista listacodigoMeta
     * 
     * @param listacodigoMeta
     * Variable a asignar en listacodigoMeta
     */
    public void setListacodigoMeta(RegistroDataModelImpl listacodigoMeta) {
        this.listacodigoMeta = listacodigoMeta;
    }

    /**
     * Retorna la lista listacodigoMeta
     * 
     * @return listacodigoMeta
     */
    public RegistroDataModelImpl getListacodigoMetaE() {
        return listacodigoMetaE;
    }

    /**
     * Asigna la lista listacodigoMeta
     * 
     * @param listacodigoMeta
     * Variable a asignar en listacodigoMeta
     */
    public void setListacodigoMetaE(RegistroDataModelImpl listacodigoMetaE) {
        this.listacodigoMetaE = listacodigoMetaE;
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
     * Retorna la lista listaMetasods
     * 
     * @return listaMetasods
     */
    public RegistroDataModelImpl getListaSubmetasods() {
        return listaSubmetasods;
    }

    /**
     * Asigna la lista listaMetasods
     * 
     * @param listaMetasods
     * Variable a asignar en listaMetasods
     */
    public void setListaSubmetasods(RegistroDataModelImpl listaSubmetasods) {
        this.listaSubmetasods = listaSubmetasods;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }
    // </SET_GET_ADICIONALES>
}